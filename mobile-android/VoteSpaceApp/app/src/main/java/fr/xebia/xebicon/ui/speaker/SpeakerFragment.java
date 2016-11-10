package fr.xebia.xebicon.ui.speaker;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.activity.BaseActivity;
import fr.xebia.xebicon.core.adapter.BaseRecyclerAdapter;
import fr.xebia.xebicon.core.misc.Preferences;
import fr.xebia.xebicon.model.Speaker;
import fr.xebia.xebicon.ui.widget.HeaderGridView;
import fr.xebia.xebicon.ui.widget.UIUtils;
import icepick.Icepick;
import icepick.Icicle;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import se.emilsjolander.sprinkles.Query;

import java.util.List;

public class SpeakerFragment extends Fragment implements ManyQuery.ResultHandler<Speaker> {

    @InjectView(R.id.container) FrameLayout mContainer;
    @InjectView(R.id.empty_id) TextView mEmptyText;
    @InjectView(R.id.speakers_grid) RecyclerView mSpeakersGrid;

    private BaseRecyclerAdapter<Speaker, SpeakerItemView> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setRetainInstance(true);

        adapter = new BaseRecyclerAdapter<>(getActivity(), R.layout.speaker_short_item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.speaker_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        boolean isLandscape = getResources().getBoolean(R.bool.landscape);
        mSpeakersGrid.setLayoutManager(new GridLayoutManager(getActivity(), isLandscape ? 4 : 3));

        /*
        mSpeakersGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getActivity(), SpeakerDetailsActivity.class);
                intent.putExtra(SpeakerDetailsActivity.EXTRA_SPEAKER_ID, ((Speaker) mSpeakersGrid.getAdapter().getItem(position)).getId());
                startActivity(intent);
            }
        });
        */

        int conferenceId = Preferences.getSelectedConference(getActivity());
        Query.many(Speaker.class, "SELECT * FROM Speakers WHERE conferenceId=? ORDER BY firstName ASC, lastName ASC",
                conferenceId).getAsync(getLoaderManager(), this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }


    @Override
    public void onDestroyView() {
        mSpeakersGrid.setOnScrollListener(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public boolean handleResult(CursorList<Speaker> speakersCursor) {
        List<Speaker> speakers = speakersCursor.asList();
        if (getView() == null) {
            return false;
        }

        if (speakers == null || speakers.isEmpty()) {
            mEmptyText.setText(getString(R.string.no_data));
            mEmptyText.setVisibility(View.VISIBLE);
            mSpeakersGrid.setVisibility(View.GONE);
        } else {
            mEmptyText.setText("");
            mEmptyText.setVisibility(View.GONE);
            mSpeakersGrid.setVisibility(View.VISIBLE);

            adapter.setDatas(speakers);
            mSpeakersGrid.setAdapter(adapter);
        }

        return false;
    }

}
