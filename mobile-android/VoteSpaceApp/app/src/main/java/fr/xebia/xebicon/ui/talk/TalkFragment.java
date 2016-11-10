package fr.xebia.xebicon.ui.talk;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.commonsware.cwac.anddown.AndDown;

import java.util.LinkedHashSet;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.bus.MemoSavedEvent;
import fr.xebia.xebicon.core.misc.Preferences;
import fr.xebia.xebicon.model.Speaker;
import fr.xebia.xebicon.model.Talk;
import fr.xebia.xebicon.model.Vote;
import fr.xebia.xebicon.ui.note.MemoActivity;
import fr.xebia.xebicon.ui.rating.RatingActivity;
import fr.xebia.xebicon.ui.speaker.SpeakerDetailsActivity;
import fr.xebia.xebicon.ui.widget.CheckableFrameLayout;
import fr.xebia.xebicon.ui.widget.ObservableScrollView;
import fr.xebia.xebicon.ui.widget.UIUtils;
import fr.xebia.xebicon.ui.widget.UnderlinedTextView;
import icepick.Icepick;
import icepick.Icicle;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import se.emilsjolander.sprinkles.Model;
import se.emilsjolander.sprinkles.OneQuery;
import se.emilsjolander.sprinkles.Query;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static fr.xebia.xebicon.core.XebiConApplication.BUS;
import static fr.xebia.xebicon.service.NotificationSchedulerIntentService.buildScheduleNotificationIntentFromTalk;

public class TalkFragment extends Fragment implements OneQuery.ResultHandler<Talk>, ManyQuery.ResultHandler<Speaker>,
        ObservableScrollView.ScrollViewListener, Toolbar.OnMenuItemClickListener {

    private static final String EXTRA_TALK_ID = "fr.xebia.xebicon.EXTRA_TALK_ID";
    private static final String EXTRA_TALK_TITLE = "fr.xebia.xebicon.EXTRA_TALK_TITLE";
    private static final String EXTRA_TALK_COLOR = "fr.xebia.xebicon.EXTRA_TALK_COLOR";

    private static final float PHOTO_ASPECT_RATIO = 1.8f;
    private static final float GAP_FILL_DISTANCE_MULTIPLIER = 1.5f;

    @InjectView(R.id.toolbar) Toolbar toolbar;

    @InjectView(R.id.scroll_view) ObservableScrollView mScrollView;

    @InjectView(R.id.talk_photo_container) ViewGroup mTalkPhotoContainer;
    @InjectView(R.id.talk_photo) ImageView mTalkPhoto;

    @InjectView(R.id.talk_details_container) ViewGroup mTalkDetailsContainer;
    @InjectView(R.id.track) UnderlinedTextView mTrack;
    @InjectView(R.id.track_content) TextView mTrackContent;
    @InjectView(R.id.informations) TextView mInformations;
    @InjectView(R.id.summary) UnderlinedTextView mSummary;
    @InjectView(R.id.summary_content) TextView mSummaryContent;
    @InjectView(R.id.track_memo_title) UnderlinedTextView mMemo;
    @InjectView(R.id.track_memo_value) TextView mMemoContent;
    @InjectView(R.id.speakers) UnderlinedTextView mSpeakers;
    @InjectView(R.id.speakers_container) ViewGroup mSpeakersContainer;
    @InjectView(R.id.talk_rating) UnderlinedTextView mTalkRating;
    @InjectView(R.id.talk_vote) Button mTalkVote;
    @InjectView(R.id.talk_vote_warning) TextView mTalkVoteWarning;

    @InjectView(R.id.talk_header) ViewGroup mTalkHeader;
    @InjectView(R.id.talk_header_contents) ViewGroup mTalkHeaderContents;
    @InjectView(R.id.title) TextView mTitle;
    @InjectView(R.id.add_schedule_button) CheckableFrameLayout mAddScheduleBtn;
    @InjectView(R.id.add_schedule_icon) ImageView mAddScheduleIcon;

    @Icicle String mExtraTalkId;
    @Icicle String mExtraTalkTitle;
    @Icicle int mExtraTalkColor;

    private Talk mTalk;
    private Vote mVote;

    private int mConferenceId;
    private int mAddScheduleBtnHeightPixels;

    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mAddScheduleBtnHeightPixels = mAddScheduleBtn.getHeight();
            recomputePhotoAndScrollingMetrics();
        }
    };
    private int mPhotoHeightPixels;
    private int mHeaderHeightPixels;
    private int mToolbarHeightPixels;

    public static Fragment newInstance(String talkId, String talkTitle, int color) {
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_TALK_ID, talkId);
        arguments.putString(EXTRA_TALK_TITLE, talkTitle);
        arguments.putInt(EXTRA_TALK_COLOR, color);
        Fragment fragment = new TalkFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setHasOptionsMenu(true);
        mConferenceId = Preferences.getSelectedConference(getActivity());
    }

    @Override
    public void onStart() {
        super.onStart();
        BUS.register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshRatingBarState();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.talk_fragment, container, false);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        if (mExtraTalkId == null) {
            mExtraTalkId = getArguments().getString(EXTRA_TALK_ID);
            mExtraTalkTitle = getArguments().getString(EXTRA_TALK_TITLE);
            mExtraTalkColor = getArguments().getInt(EXTRA_TALK_COLOR);
        }


        configureHeaders();
        setupCustomScrolling();

        mTitle.setText(mExtraTalkTitle);
        toolbar.setBackgroundColor(mExtraTalkColor);

        toolbar.setNavigationIcon(R.drawable.ic_up);
        toolbar.setNavigationOnClickListener(view1 -> NavUtils.navigateUpFromSameTask(getActivity()));
        getActivity().getMenuInflater().inflate(R.menu.talk, toolbar.getMenu());

        toolbar.setOnMenuItemClickListener(this);

        mTalkHeaderContents.setBackgroundColor(mExtraTalkColor);

        mTalkVote.setOnClickListener(v -> startActivity(new Intent(getActivity(), RatingActivity.class)
                .putExtra(EXTRA_TALK_ID, mTalk.getId())
                .putExtra(EXTRA_TALK_TITLE, mTalk.getTitle())
                .putExtra(EXTRA_TALK_COLOR, mTalk.getColor())));

        getTalk();

    }

    private void getTalk() {
        Query.one(Talk.class, "SELECT * FROM Talks WHERE _id=? AND conferenceId=?", mExtraTalkId, mConferenceId)
                .getAsync(getLoaderManager(), this, null);
    }

    private void configureHeaders() {
        int dividerColor = getResources().getColor(android.R.color.darker_gray);
        int underlineHeight = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mSummary.setUnderlineColor(dividerColor);
        mSummary.setUnderlineHeight(underlineHeight);
        mSummary.setTextColor(mExtraTalkColor);

        mSpeakers.setUnderlineColor(dividerColor);
        mSpeakers.setUnderlineHeight(underlineHeight);
        mSpeakers.setTextColor(mExtraTalkColor);

        mTrack.setUnderlineColor(dividerColor);
        mTrack.setUnderlineHeight(underlineHeight);
        mTrack.setTextColor(mExtraTalkColor);

        mMemo.setUnderlineColor(dividerColor);
        mMemo.setUnderlineHeight(underlineHeight);
        mMemo.setTextColor(mExtraTalkColor);

        mTalkRating.setUnderlineColor(dividerColor);
        mTalkRating.setUnderlineHeight(underlineHeight);
        mTalkRating.setTextColor(mExtraTalkColor);
    }

    private void setupCustomScrolling() {
        mScrollView.setScrollViewListener(this);
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }
    }

    private void recomputePhotoAndScrollingMetrics() {
        mToolbarHeightPixels = toolbar.getHeight();
        mHeaderHeightPixels = mTalkHeaderContents.getHeight();

        mPhotoHeightPixels = (int) (mTalkPhoto.getWidth() / PHOTO_ASPECT_RATIO);
        mPhotoHeightPixels = Math.min(mPhotoHeightPixels, getView().getHeight() * 2 / 3);

        ViewGroup.LayoutParams lp;
        lp = mTalkPhotoContainer.getLayoutParams();
        if (lp.height != mPhotoHeightPixels) {
            lp.height = mPhotoHeightPixels;
            mTalkPhotoContainer.setLayoutParams(lp);
        }

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)
                mTalkDetailsContainer.getLayoutParams();
        if (mlp.topMargin != mHeaderHeightPixels + mPhotoHeightPixels + mToolbarHeightPixels) {
            mlp.topMargin = mHeaderHeightPixels + mPhotoHeightPixels + mToolbarHeightPixels;
            mTalkDetailsContainer.setLayoutParams(mlp);
        }

        onScrollChanged(mScrollView, 0, 0, 0, 0);
    }

    @Override
    public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy) {
        final Activity activity = getActivity();
        if (activity == null) {
            return;
        }

        // Reposition the header bar -- it's normally anchored to the top of the content,
        // but locks to the top of the screen on scroll
        int scrollY = mScrollView.getScrollY();

        float newTop = Math.max(mPhotoHeightPixels, scrollY);
        mTalkHeader.setTranslationY(newTop);
        mAddScheduleBtn.setTranslationY(newTop + mHeaderHeightPixels + mToolbarHeightPixels - mAddScheduleBtnHeightPixels / 2);

        // Move background photo (parallax effect)
        mTalkPhotoContainer.setTranslationY(scrollY * 0.6f);
    }

    @OnClick(R.id.add_schedule_button)
    public void onAddScheduleBtnClicked() {
        if (mTalk != null) {
            mTalk.setFavorite(!mTalk.isFavorite());
            mTalk.saveAsync(new Model.OnSavedCallback() {
                @Override
                public void onSaved() {
                    boolean favorite = mTalk.isFavorite();
                    mAddScheduleBtn.setChecked(favorite, true);
                    UIUtils.setOrAnimatePlusCheckIcon(getActivity(), mAddScheduleIcon, favorite, true);
                    if (favorite && getActivity() != null) {
                        getActivity().startService(buildScheduleNotificationIntentFromTalk(getActivity(), mTalk));
                    }
                }
            });
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onStop() {
        BUS.unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        mScrollView.setScrollViewListener(null);
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.removeGlobalOnLayoutListener(mGlobalLayoutListener);
        }
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mTalk = null;
        super.onDestroy();
    }

    @Override
    public boolean handleResult(Talk talk) {
        mTalk = talk;
        if (talk == null) {
            getActivity().finish();
            return false;
        }

        if (getView() == null) {
            return true;
        }
        mTitle.setText(talk.getTitle());
        mInformations.setText(String.format("%s | %s | %s", talk.getDay(), talk.getPeriod(), talk.getRoom()));

        boolean favorite = mTalk.isFavorite();
        mAddScheduleBtn.setChecked(favorite, false);
        mAddScheduleBtn.setVisibility(talk.isKeynote() ? INVISIBLE : VISIBLE);
        UIUtils.setOrAnimatePlusCheckIcon(getActivity(), mAddScheduleIcon, favorite, true);

        if (!TextUtils.isEmpty(talk.getSummary())) {
            try {
                mSummaryContent.setText(Html.fromHtml(new AndDown().markdownToHtml(talk.getSummary())));
            } catch (Exception e) {
                mSummaryContent.setText(talk.getSummary());
            }
        } else {
            mSummaryContent.setText(R.string.no_talk_details);
        }


        mTrackContent.setText(talk.getTrack().replace(",", ", "));

        mTalkPhoto.setImageResource(getTalkBackgroundResource(talk));

        bindMemo();

        refreshRatingBarState();

        Query.many(Speaker.class, "SELECT * FROM Speakers AS S JOIN Speaker_Talk ST ON S._id=ST.speakerId WHERE ST.talkId=? AND S" +
                        ".conferenceId=?",
                talk.getId(), mConferenceId).getAsync(getLoaderManager(), this);

        if (getView().getAlpha() == 0) {
            getView().animate().alpha(1).start();
        }

        return true;
    }

    private int getTalkBackgroundResource(Talk talk) {
        return getResources().getIdentifier("devoxx_talk_template_" + talk.getPosition() % 14, "drawable", getActivity().getPackageName());
    }

    public void refreshRatingBarState() {
        if (mTalk == null) {
            return;
        }
        mTalkRating.setVisibility(VISIBLE);

        if (System.currentTimeMillis() > mTalk.getFromUtcTime()) {
            mTalkVote.setVisibility(VISIBLE);
            mTalkVoteWarning.setVisibility(GONE);
        } else {
            mTalkVote.setVisibility(GONE);
            mTalkVoteWarning.setVisibility(VISIBLE);
        }
    }

    @Override
    public boolean handleResult(CursorList<Speaker> speakers) {
        if (getView() == null) {
            return false;
        }

        mSpeakersContainer.removeAllViews();
        if (speakers != null && speakers.size() > 0) {
            mSpeakers.setVisibility(VISIBLE);
            mSpeakersContainer.setVisibility(VISIBLE);
            LayoutInflater inflater = LayoutInflater.from(getActivity());
            for (final Speaker speaker : speakers.asList()) {
                TalkSpeakerItemView speakerView = (TalkSpeakerItemView) inflater.inflate(R.layout.talk_speaker_item_view,
                        mSpeakersContainer, false);
                speakerView.bind(speaker);
                speakerView.setBackgroundResource(R.drawable.text_view_selector);
                speakerView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), SpeakerDetailsActivity.class);
                        intent.putExtra(SpeakerDetailsActivity.EXTRA_SPEAKER_ID, speaker.getId());
                        intent.putExtra(SpeakerDetailsActivity.EXTRA_COLOR, mTalk.getColor());
                        startActivity(intent);
                    }
                });
                mSpeakersContainer.addView(speakerView);
            }
            mTalk.setSpeakers(new LinkedHashSet<>(speakers.asList()));
        } else {
            mSpeakers.setVisibility(GONE);
            mSpeakersContainer.setVisibility(GONE);
        }
        return false;
    }


    private void bindMemo() {
        if (mTalk.getMemo() != null && mTalk.getMemo().trim().length() > 0) {
            try {
                mMemoContent.setText(Html.fromHtml(new AndDown().markdownToHtml(mTalk.getMemo())));
            } catch (Exception e) {
                mMemoContent.setText(mTalk.getMemo());
            }
            mMemo.setVisibility(VISIBLE);
            mMemoContent.setVisibility(VISIBLE);
        } else {
            mMemo.setVisibility(GONE);
            mMemoContent.setVisibility(GONE);
        }
    }

    public void onEventMainThread(MemoSavedEvent event) {
        getTalk();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_note:
                if (mTalk != null) {
                    Intent intent = new Intent(getActivity(), MemoActivity.class);
                    intent.putExtra(EXTRA_TALK_ID, mTalk.getId());
                    intent.putExtra(EXTRA_TALK_TITLE, mTalk.getTitle());
                    intent.putExtra(EXTRA_TALK_COLOR, mTalk.getColor());
                    startActivity(intent);
                }
                return true;
            case R.id.action_send:
                if (mTalk != null) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("message/rfc822");
                    intent.putExtra(Intent.EXTRA_SUBJECT, mTalk.getUncotedTitle());
                    intent.putExtra(Intent.EXTRA_TEXT, mTalk.getBody(getActivity()));
                    try {
                        startActivity(Intent.createChooser(intent, getResources().getString(R.string.send_memo_via)));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), R.string.cannot_send_email, Toast.LENGTH_SHORT).show();
                    }
                }
            default:
                return false;
        }
    }
}
