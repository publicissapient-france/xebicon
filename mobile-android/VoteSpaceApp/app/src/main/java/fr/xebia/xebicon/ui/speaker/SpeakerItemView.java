package fr.xebia.xebicon.ui.speaker;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.adapter.BaseItemView;
import fr.xebia.xebicon.model.Speaker;
import fr.xebia.xebicon.ui.widget.ExtendedLinearLayout;

public class SpeakerItemView extends ExtendedLinearLayout implements BaseItemView<Speaker>, View.OnClickListener {

    @InjectView(R.id.speaker_image) ImageView mSpeakerImage;
    @InjectView(R.id.speaker_name) TextView mSpeakerName;
    private ViewTreeObserver.OnGlobalLayoutListener globalLayoutListener;
    private Speaker speaker;

    public SpeakerItemView(Context context) {
        super(context);
    }

    public SpeakerItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpeakerItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this, this);

        setOnClickListener(this);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        globalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mSpeakerImage.getLayoutParams().height = getWidth() - mSpeakerName.getHeight();
                mSpeakerImage.invalidate();
            }
        };
        mSpeakerImage.getViewTreeObserver().addOnGlobalLayoutListener(globalLayoutListener);
    }

    @Override
    public void bindView(Speaker speaker) {
        this.speaker = speaker;

        mSpeakerName.setText(String.format("%s %s", speaker.getFirstName(), speaker.getLastName()));
        post(new Runnable() {
            @Override
            public void run() {
                int measuredWidth = getMeasuredWidth();
                int measuredHeight = measuredWidth - mSpeakerName.getMeasuredHeight();
                mSpeakerImage.getLayoutParams().height = measuredHeight;
                mSpeakerImage.invalidate();

                if (!TextUtils.isEmpty(speaker.getImageURL())) {
                    Picasso.with(getContext()).load(speaker.getImageURL())
                            .placeholder(R.drawable.speaker_placeholder)
                            .resize(measuredWidth, measuredHeight)
                            .centerCrop()
                            .into(mSpeakerImage);
                }
            }

        });

    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mSpeakerImage.getViewTreeObserver().removeGlobalOnLayoutListener(globalLayoutListener);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getContext(), SpeakerDetailsActivity.class);
        intent.putExtra(SpeakerDetailsActivity.EXTRA_SPEAKER_ID, speaker.getId());
        getContext().startActivity(intent);
    }
}
