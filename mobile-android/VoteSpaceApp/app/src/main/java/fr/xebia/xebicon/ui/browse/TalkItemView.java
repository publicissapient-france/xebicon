package fr.xebia.xebicon.ui.browse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.adapter.BaseItemView;
import fr.xebia.xebicon.model.Talk;
import fr.xebia.xebicon.ui.talk.TalkActivity;

public class TalkItemView extends FrameLayout implements BaseItemView<Talk>, View.OnClickListener {

    @InjectView(R.id.talk_photo) ImageView mTalkPhoto;
    @InjectView(R.id.talk_category) TextView mTalkCategory;
    @InjectView(R.id.talk_title) TextView mTalkTitle;
    @InjectView(R.id.talk_subtitle) TextView mTalkSubTitle;
    @InjectView(R.id.talk_snippet) TextView mTalkSnippet;
    @InjectView(R.id.indicator_in_schedule) ImageView mInSchedule;
    @InjectView(R.id.info_box) ViewGroup mInfoBox;
    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener;
    private Talk talk;

    public TalkItemView(Context context) {
        super(context);
    }

    public TalkItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public TalkItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);

        setOnClickListener(this);
        mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeGlobalOnLayoutListener(this);
                if (mTalkSnippet.getLineCount() > 3) {
                    int lineEndIndex = mTalkSnippet.getLayout().getLineEnd(2);
                    String text = mTalkSnippet.getText().subSequence(0, lineEndIndex - 3) + "...";
                    mTalkSnippet.setText(text);
                }

            }
        };
    }

    @Override
    public void bindView(Talk el) {
        this.talk = el;

        setTag(talk);
        setBackgroundColor(talk.getColor());
        Picasso.with(getContext()).load(getItemBackgroundResource(talk))
                .fit()
                .config(Bitmap.Config.RGB_565)
                .centerCrop()
                .into(mTalkPhoto);
        mTalkCategory.setText(talk.getType());
        mTalkTitle.setText(talk.getTitle());

        if (System.currentTimeMillis() > talk.getToUtcTime()) {
            mTalkSubTitle.setText(getResources().getString(R.string.ended));
        } else {
            mTalkSubTitle.setText(String.format("%s | %s | %s", talk.getDay(), talk.getPeriod(), talk.getRoom()));
        }

        mTalkSnippet.setText(Html.fromHtml(talk.getSummary()));
        mInSchedule.setVisibility(talk.isFavorite() ? VISIBLE : GONE);
        mInfoBox.setBackgroundColor(talk.getColor());

        getViewTreeObserver().addOnGlobalLayoutListener(mGlobalLayoutListener);
    }

    private int getItemBackgroundResource(Talk talk) {
        return getResources().getIdentifier("devoxx_talk_template_" + talk.getPosition() % 14, "drawable", getContext().getPackageName());
    }

    @Override
    public void onClick(View v) {
        if (!talk.isBreak()) {
            Intent intent = new Intent(getContext(), TalkActivity.class);
            intent.putExtra(TalkActivity.EXTRA_TALK_ID, talk.getId());
            intent.putExtra(TalkActivity.EXTRA_TALK_TITLE, talk.getTitle());
            intent.putExtra(TalkActivity.EXTRA_TALK_COLOR, talk.getColor());
            getContext().startActivity(intent);
        }
    }
}
