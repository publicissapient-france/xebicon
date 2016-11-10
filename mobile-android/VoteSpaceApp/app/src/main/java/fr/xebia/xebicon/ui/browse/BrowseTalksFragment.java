package fr.xebia.xebicon.ui.browse;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.activity.BaseActivity;
import fr.xebia.xebicon.core.adapter.BaseRecyclerAdapter;
import fr.xebia.xebicon.core.misc.Preferences;
import fr.xebia.xebicon.core.utils.SqlUtils;
import fr.xebia.xebicon.model.Talk;
import fr.xebia.xebicon.ui.widget.CollectionView;
import fr.xebia.xebicon.ui.widget.UIUtils;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import se.emilsjolander.sprinkles.Query;

import static com.fluent.android.bundle.FluentBundle.newFluentBundle;
import static com.fluent.android.bundle.FragmentArgsSetter.setFragmentArguments;

public class BrowseTalksFragment extends Fragment implements ManyQuery.ResultHandler<Talk> {

    public static final String TAG = "BrowseTalksFragment";
    private static final int DEFAULT_GROUP_ID = 0;
    private static final int PAST_GROUP_ID = 1;

    public static String ARG_AVAILABLE_TALKS = "fr.xebia.xebicon.ARG_AVAILABLE_TALKS";

    @InjectView(R.id.empty_id) TextView mEmptyText;
    @InjectView(R.id.talks_grid) RecyclerView mTalksGrid;

    private BaseRecyclerAdapter<Talk, TalkItemView> adapter;

    private List<Talk> mTalks;
    private boolean mLandscapeMode;
    private boolean mWideMode;
    private boolean mResumed;
    private boolean mPopulated;

    public static BrowseTalksFragment newInstance(ArrayList<String> availableTalks) {
        return setFragmentArguments(new BrowseTalksFragment(), newFluentBundle().put(ARG_AVAILABLE_TALKS, availableTalks));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        int conferenceId = Preferences.getSelectedConference(getActivity());
        mLandscapeMode = getResources().getBoolean(R.bool.landscape);
        mWideMode = getResources().getBoolean(R.bool.wide_mode);

        adapter = new BaseRecyclerAdapter<>(getActivity(), R.layout.talk_item_view);

        List<String> availableTalksIds = getArguments().getStringArrayList(ARG_AVAILABLE_TALKS);
        Query.many(Talk.class, "SELECT * FROM Talks WHERE conferenceId=? AND _id IN (" +
                SqlUtils.toSqlArray(availableTalksIds) + ") ORDER BY fromTime ASC, toTime ASC, _id ASC", conferenceId).getAsync(getLoaderManager(), this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.browse_talks_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);

        mTalksGrid.setLayoutManager(new GridLayoutManager(getActivity(), getNumColumns()));
    }

    @Override
    public void onResume() {
        super.onResume();
        mResumed = true;
        if (mTalks != null && !mPopulated) {
            populateTalksGrid();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mResumed = false;
    }

    @Override
    public void onDestroyView() {
        mTalksGrid.setOnScrollListener(null);
        super.onDestroyView();
    }

    @Override
    public boolean handleResult(CursorList<Talk> cursorList) {
        mTalks = cursorList.asList();

        if (getView() != null) {
            populateTalksGrid();
        }

        return true;
    }

    private void populateTalksGrid() {
        if (mTalks.isEmpty()) {
            mEmptyText.setVisibility(View.VISIBLE);
            mTalksGrid.setVisibility(View.GONE);
        } else {
            mTalksGrid.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);

            adapter.setDatas(mTalks);
            mTalksGrid.setAdapter(adapter);

        }
        mPopulated = true;
    }

    private int getNumColumns() {
        int numColumns = mLandscapeMode ? 2 : 1;
        if (mWideMode) {
            numColumns++;
        }
        return numColumns;
    }
}
