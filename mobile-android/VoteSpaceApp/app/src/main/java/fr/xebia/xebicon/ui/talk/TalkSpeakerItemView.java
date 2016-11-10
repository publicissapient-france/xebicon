package fr.xebia.xebicon.ui.talk;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.squareup.picasso.Picasso;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.transform.CircleTransform;
import fr.xebia.xebicon.model.Speaker;

import static android.text.Html.fromHtml;

public class TalkSpeakerItemView extends LinearLayout {

    @InjectView(R.id.speaker_image) ImageView mSpeakerImage;
    @InjectView(R.id.speaker_name) TextView mSpeakerName;

    public TalkSpeakerItemView(Context context) {
        super(context);
    }

    public TalkSpeakerItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TalkSpeakerItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this, this);
    }

    public void bind(Speaker speaker) {
        String company = speaker.getCompany();
        if (company == null) {
            mSpeakerName.setText(fromHtml(getResources().getString(R.string.speaker_format_without_company, speaker.getFirstName(),
                    speaker.getLastName())));
        } else {
            mSpeakerName.setText(fromHtml(getResources().getString(R.string.speaker_format, speaker.getFirstName(),
                    speaker.getLastName(), company)));
        }

        if (!TextUtils.isEmpty(speaker.getImageURL())) {
            Picasso.with(getContext()).load(speaker.getImageURL())
                    .placeholder(R.drawable.speaker_placeholder_round)
                    .transform(new CircleTransform())
                    .noFade()
                    .fit()
                    .centerCrop()
                    .into(mSpeakerImage);
        }
    }
}
