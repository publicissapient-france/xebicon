package fr.xebia.xebicon.ui.schedule;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.adapter.BaseItemView;
import fr.xebia.xebicon.model.Talk;
import fr.xebia.xebicon.ui.talk.TalkActivity;
import fr.xebia.xebicon.ui.widget.ExtendedRelativeLayout;
import fr.xebia.xebicon.ui.widget.UIUtils;

public class ScheduleItemView extends ExtendedRelativeLayout implements Callback, BaseItemView<Talk>, View.OnClickListener {

    @InjectView(R.id.schedule_bg_img) ImageView mScheduleBgImg;
    @InjectView(R.id.schedule_title) TextView mScheduleTitle;
    @InjectView(R.id.schedule_speakers) TextView mScheduleSpeakers;
    @InjectView(R.id.schedule_room) TextView mScheduleRoom;
    @InjectView(R.id.indicator_in_schedule) View mInSchedule;
    private boolean wideMode;
    private boolean landscape;
    private Talk talk;

    public ScheduleItemView(Context context) {
        super(context);
    }

    public ScheduleItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        ButterKnife.inject(this);

        setOnClickListener(this);

        wideMode = getContext().getResources().getBoolean(R.bool.wide_mode);
        landscape = getContext().getResources().getBoolean(R.bool.landscape);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        super.onMeasure(widthMeasureSpec, wideMode && landscape ?
                MeasureSpec.makeMeasureSpec((int) (widthSize / 1.7f), widthMode) : widthMeasureSpec);
    }

    @Override
    public void bindView(Talk el) {
        this.talk = el;

        setBackgroundColor(UIUtils.setColorAlpha(talk.getColor(), 0.65f));
        mScheduleBgImg.setColorFilter(UIUtils.setColorAlpha(talk.getColor(), 0.65f));
        mScheduleBgImg.setBackgroundColor(talk.getColor());
        Picasso.with(getContext())
                .load(getItemBackgroundResource(talk))
                .fit()
                .centerCrop()
                .config(Bitmap.Config.RGB_565)
                .into(mScheduleBgImg);

        mScheduleTitle.setText(talk.getTitle());

        if (System.currentTimeMillis() > talk.getToUtcTime()) {
            mScheduleRoom.setText(getResources().getString(R.string.ended));
        } else {
            mScheduleRoom.setText(String.format("%s | %s\n%s", talk.getDay(), talk.getPeriod(), talk.getRoom()));
        }

        mScheduleSpeakers.setText(talk.getPrettySpeakers());
        mScheduleSpeakers.setVisibility(TextUtils.isEmpty(talk.getPrettySpeakers()) ? INVISIBLE : VISIBLE);

        mInSchedule.setVisibility(talk.isFavorite() ? VISIBLE : GONE);
    }

    private int getItemBackgroundResource(Talk talk) {
        return getResources().getIdentifier("devoxx_template_" + talk.getPosition() % 14, "drawable", getContext().getPackageName());
    }

    @Override
    public void onSuccess() {
        setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    @Override
    public void onError() {

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
