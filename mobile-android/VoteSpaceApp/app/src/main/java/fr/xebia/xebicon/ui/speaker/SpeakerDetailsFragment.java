package fr.xebia.xebicon.ui.speaker;

import android.app.Activity;
import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.misc.Preferences;
import fr.xebia.xebicon.core.transform.CircleTransform;
import fr.xebia.xebicon.model.Speaker;
import fr.xebia.xebicon.model.Talk;
import fr.xebia.xebicon.ui.talk.TalkActivity;
import fr.xebia.xebicon.ui.widget.UnderlinedTextView;
import icepick.Icepick;
import icepick.Icicle;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import se.emilsjolander.sprinkles.OneQuery;
import se.emilsjolander.sprinkles.Query;

public class SpeakerDetailsFragment extends Fragment implements OneQuery.ResultHandler<Speaker>, ManyQuery.ResultHandler<Talk> {

    public static final String EXTRA_SPEAKER_ID = "fr.xebia.xebicon.EXTRA_SPEAKER_ID";
    private static final String EXTRA_COLOR = "fr.xebia.xebicon.EXTRA_COLOR";

    @InjectView(R.id.speaker_image)
    ImageView mSpeakerImage;
    @InjectView(R.id.speaker_name)
    TextView mSpeakerName;
    @InjectView(R.id.speaker_bio)
    UnderlinedTextView mSpeakerBio;
    @InjectView(R.id.speaker_bio_content)
    TextView mSpeakerBioContent;
    @InjectView(R.id.speaker_presentations)
    UnderlinedTextView mSpeakerPresentations;
    @InjectView(R.id.speaker_presentations_content)
    ViewGroup mSpeakerPresentationsContent;

    @Icicle
    String mSpeakerId;

    private int mThemePrimaryColor;
    private int mContextColor;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.speaker_details_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        if (mSpeakerId == null) {
            mSpeakerId = getArguments().getString(EXTRA_SPEAKER_ID);
        }
        mContextColor = getArguments().getInt(EXTRA_COLOR);
        int conferenceId = Preferences.getSelectedConference(getActivity());
        Query.one(Speaker.class, "SELECT * FROM Speakers WHERE _id=? AND conferenceId=?", mSpeakerId, conferenceId)
                .getAsync(getLoaderManager(), this);
        Query.many(Talk.class, "SELECT * FROM Talks AS T JOIN Speaker_Talk AS ST ON T._id=ST.talkId WHERE ST.speakerId=? AND T" +
                ".conferenceId=? ORDER BY T" +
                ".fromTime ASC", mSpeakerId, conferenceId)
                .getAsync(getLoaderManager(), this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        TypedValue a = new TypedValue();
        activity.getTheme().resolveAttribute(R.attr.colorPrimary, a, true);
        mThemePrimaryColor = a.data;
        configureHeaders();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    private void configureHeaders() {
        int underlineColor = getResources().getColor(R.color.list_dropdown_divider_color);
        int underlineHeight = Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1, getResources().getDisplayMetrics()));
        mSpeakerBio.setTextColor(mContextColor != 0 ? mContextColor : mThemePrimaryColor);
        mSpeakerBio.setUnderlineColor(underlineColor);
        mSpeakerBio.setUnderlineHeight(underlineHeight);

        mSpeakerPresentations.setTextColor(mContextColor != 0 ? mContextColor : mThemePrimaryColor);
        mSpeakerPresentations.setUnderlineColor(underlineColor);
        mSpeakerPresentations.setUnderlineHeight(underlineHeight);
    }

    public static Fragment newInstance(String speakerId, int contextColor) {
        Fragment fragment = new SpeakerDetailsFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_SPEAKER_ID, speakerId);
        arguments.putInt(EXTRA_COLOR, contextColor);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public boolean handleResult(Speaker speaker) {
        if (getView() == null) {
            return false;
        }

        if (!TextUtils.isEmpty(speaker.getImageURL())) {
            Picasso.with(getActivity())
                    .load(speaker.getImageURL())
                    .placeholder(R.drawable.speaker_placeholder_round)
                    .fit()
                    .transform(new CircleTransform())
                    .centerCrop()
                    .into(mSpeakerImage);
        }
        mSpeakerBioContent.setText(Html.fromHtml(speaker.getBio()));

        String company = speaker.getCompany();
        final String tweetHandle = speaker.getTweetHandle();
        if (company == null) {
            if (TextUtils.isEmpty(tweetHandle)) {
                mSpeakerName.setText(Html.fromHtml(getString(R.string.speaker_format_without_company, speaker.getFirstName(),
                        speaker.getLastName())));
            } else {
                mSpeakerName.setText(Html.fromHtml(getString(R.string.speaker_details_format_with_twitter_without_company,
                        speaker.getFirstName(), speaker.getLastName(), tweetHandle)));
            }
        } else {
            if (TextUtils.isEmpty(tweetHandle)) {
                mSpeakerName.setText(Html.fromHtml(getString(R.string.speaker_details_format, speaker.getFirstName(), speaker.getLastName(),
                        company)));
            } else {
                mSpeakerName.setText(Html.fromHtml(getString(R.string.speaker_details_format_with_twitter, speaker.getFirstName(),
                        speaker.getLastName(),
                        company, tweetHandle)));
            }
        }

        if (!TextUtils.isEmpty(tweetHandle)) {
            mSpeakerName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("http://www.twitter.com/%s", tweetHandle)));
                    try {
                        startActivity(intent);
                    } catch (ActivityNotFoundException e) {

                    }
                }
            });
        }

        return false;
    }

    @Override
    public boolean handleResult(CursorList<Talk> talks) {
        if (getView() == null) {
            return false;
        }

        mSpeakerPresentationsContent.removeAllViews();
        if (talks != null) {
            final LayoutInflater inflater = LayoutInflater.from(getActivity());
            for (final Talk talk : talks.asList()) {
                SpeakerScheduleItemView scheduleItemView = (SpeakerScheduleItemView) inflater.inflate(R.layout.speaker_schedule_item_view,
                        mSpeakerPresentationsContent, false);
                scheduleItemView.bind(talk);
                scheduleItemView.setBackgroundResource(R.drawable.text_view_selector);
                scheduleItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), TalkActivity.class);
                        intent.putExtra(TalkActivity.EXTRA_TALK_ID, talk.getId());
                        intent.putExtra(TalkActivity.EXTRA_TALK_TITLE, talk.getTitle());
                        intent.putExtra(TalkActivity.EXTRA_TALK_COLOR, talk.getColor());
                        startActivity(intent);
                    }
                });
                mSpeakerPresentationsContent.addView(scheduleItemView);
            }
        }
        return false;
    }

}
