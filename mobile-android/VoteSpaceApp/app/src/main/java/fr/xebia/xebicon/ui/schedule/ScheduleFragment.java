package fr.xebia.xebicon.ui.schedule;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.activity.BaseActivity;
import fr.xebia.xebicon.core.adapter.BaseRecyclerAdapter;
import fr.xebia.xebicon.core.misc.Preferences;
import fr.xebia.xebicon.model.Schedule;
import fr.xebia.xebicon.model.TagMetadata;
import fr.xebia.xebicon.model.Tags;
import fr.xebia.xebicon.model.Talk;
import icepick.Icepick;
import icepick.Icicle;
import se.emilsjolander.sprinkles.CursorList;
import se.emilsjolander.sprinkles.ManyQuery;
import se.emilsjolander.sprinkles.Query;
import timber.log.Timber;

public class ScheduleFragment extends Fragment implements ManyQuery.ResultHandler<Talk>, SwipeRefreshLayout.OnRefreshListener {

    public static final String TAG = "ScheduleFragment";
    public static final int DEFAULT_GROUP_ID = 0;
    private static final int PAST_GROUP_ID = 1;

    @InjectView(R.id.container) LinearLayout mContainer;
    @InjectView(R.id.empty_id) TextView mEmptyText;
    @InjectView(R.id.schedule_grid) RecyclerView mScheduleGrid;
    @InjectView(R.id.filters_box) ViewGroup mFiltersBox;
    @InjectView(R.id.secondary_filter_spinner_1) Spinner mSecondaryFilterSpinner1;
    @InjectView(R.id.swipe_refresh) SwipeRefreshLayout swipeRefreshLayout;


    // filter tags that are currently selected
    @Icicle String[] mFilterTags = {"", ""};
    // filter tags that we have to restore (as a result of Activity recreation)
    @Icicle String[] mFilterTagsToRestore = {null, null};

    private Schedule mSchedule;

    private Spinner mSpinner;
    private FilterScheduleSpinnerAdapter mFilterScheduleSpinnerAdapter;

    private TagMetadata mTagMetadata;

    private boolean mLandscapeMode;
    private boolean mWideMode;
    private boolean mFirstLoad;

    private BaseRecyclerAdapter<Talk, ScheduleItemView> adapter;
    private GridLayoutManager layoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);
        setRetainInstance(true);

        adapter = new BaseRecyclerAdapter<>(getActivity(), R.layout.schedule_item_view);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.schedule_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mFirstLoad = true;

        ButterKnife.inject(this, view);

        mLandscapeMode = getResources().getBoolean(R.bool.landscape);
        mWideMode = getResources().getBoolean(R.bool.wide_mode);

        layoutManager = new GridLayoutManager(getActivity(), getNumColumns(false));
        mScheduleGrid.setLayoutManager(layoutManager);
        mScheduleGrid.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this);

        configureActionBarSpinner();

        retrievedTalks();
    }

    private void retrievedTalks() {
        int conferenceId = Preferences.getSelectedConference(getActivity());
        Query.many(Talk.class, "SELECT * FROM Talks WHERE conferenceId=? ORDER BY fromTime ASC, toTime ASC, _id ASC", conferenceId)
                .getAsync(getLoaderManager(), this);

        computeActionBarSpinnerAdapter();
    }

    private void configureActionBarSpinner() {
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        View spinnerContainer = LayoutInflater.from(actionBar.getThemedContext()).inflate(R.layout.actionbar_spinner, null);
        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        actionBar.setCustomView(spinnerContainer, lp);
        actionBar.setDisplayShowTitleEnabled(false);
        mFilterScheduleSpinnerAdapter = new FilterScheduleSpinnerAdapter(getActivity(), true);

        mSpinner = (Spinner) spinnerContainer.findViewById(R.id.actionbar_spinner);
        mSpinner.setAdapter(mFilterScheduleSpinnerAdapter);
        mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> spinner, View view, int position, long itemId) {
                onActionBarFilterSelected(mFilterScheduleSpinnerAdapter.getTag(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        computeActionBarSpinnerAdapter();
    }

    private void onActionBarFilterSelected(String tag) {
        if (tag.equals(mFilterTags[0])) {
            // nothing to do
            return;
        }

        mFilterTags[0] = tag;

        // Reset secondary filters
        for (int i = 1; i < mFilterTags.length; i++) {
            mFilterTags[i] = "";
        }
        populateScheduleGrid(true);
    }

    private void populateScheduleGrid(boolean reset) {
        populateScheduleGrid(reset, true);
    }

    private void populateScheduleGrid(boolean reset, boolean computeActionBar) {
        if (computeActionBar) {
            computeActionBarSpinnerAdapter();
        }

        boolean filtering = !"".equals(mFilterTags[0]);
        int itemLayout = filtering ? R.layout.talk_item_view : R.layout.schedule_item_view;
        layoutManager.setSpanCount(getNumColumns(filtering));
        adapter.setViewResId(itemLayout);

        if (mTagMetadata == null) {
            return;
        }

        List<Talk> filteredTalks = mSchedule.getFilteredTalks(
                mTagMetadata.getTag(mFilterTags[0]),
                mTagMetadata.getTag(mFilterTags[1]));

        if (filteredTalks.isEmpty()) {
            mEmptyText.setVisibility(View.VISIBLE);
            mScheduleGrid.setVisibility(View.GONE);
        } else {
            mScheduleGrid.setVisibility(View.VISIBLE);
            mEmptyText.setVisibility(View.GONE);

            if (!reset && !computeActionBar) {
                return;
            }

            adapter.setDatas(filteredTalks);
        }

        mScheduleGrid.setAdapter(adapter);
    }

    private int getNumColumns(boolean filtering) {
        int numColumns;
        if (mLandscapeMode) {
            numColumns = filtering ? 2 : 3;
            if (mWideMode && filtering) {
                numColumns++;
            }
        } else {
            numColumns = filtering ? 1 : 2;
            if (mWideMode) {
                numColumns++;
            }
        }
        return numColumns;
    }

    private void showSecondaryFilters() {
        if (mTagMetadata == null) {
            showFilterBox(false);
            return;
        }
        // repopulate secondary filter spinners
        boolean showFilter = !TextUtils.isEmpty(mFilterTags[0]);
        if (showFilter) {
            TagMetadata.Tag topTag = mTagMetadata.getTag(mFilterTags[0]);
            String topCategory = topTag.getCategory();
            if (topCategory.equals(Tags.FILTER_CATEGORIES[0])) {
                populateSecondLevelFilterSpinner(1);
            } else if (topCategory.equals(Tags.FILTER_CATEGORIES[1])) {
                populateSecondLevelFilterSpinner(0);
            }

            showFilterBox(true);
        } else {
            showFilterBox(false);
        }
    }

    private void populateSecondLevelFilterSpinner(int catIndex) {
        String tagToRestore = mFilterTagsToRestore[1];
        Spinner spinner = mSecondaryFilterSpinner1;
        Parcelable spinnerState = spinner.onSaveInstanceState();
        final int filterIndex = 1;
        String tagCategory = Tags.FILTER_CATEGORIES[catIndex];
        boolean isTopicCategory = Tags.CATEGORY_TOPIC.equals(tagCategory);

        final FilterScheduleSpinnerAdapter adapter;
        if (spinner.getAdapter() == null) {
            adapter = new FilterScheduleSpinnerAdapter(getActivity(), false);
            spinner.setAdapter(adapter);
        } else {
            adapter = (FilterScheduleSpinnerAdapter) spinner.getAdapter();
            adapter.clear();
        }
        adapter.addItem("", getString(Tags.EXPLORE_CATEGORY_ALL_STRING[catIndex]), false, 0);

        List<TagMetadata.Tag> tags = mTagMetadata.getTagsInCategory(tagCategory);
        int itemToSelect = spinner.getSelectedItemPosition();
        if (tags != null) {
            for (TagMetadata.Tag tag : tags) {
                adapter.addItem(tag.getId(), tag.getName(), false,
                        isTopicCategory ? tag.getColor() : 0);
                if (!TextUtils.isEmpty(tagToRestore) && tag.getId().equals(tagToRestore)) {
                    itemToSelect = adapter.getCount() - 1;
                    mFilterTagsToRestore[1] = null;
                }
            }
        } else {
            Timber.e("Can't populate spinner. Category has no tags: " + tagCategory);
        }
        mFilterTagsToRestore[1] = null;

        adapter.notifyDataSetChanged();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectTag(adapter.getTag(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectTag("");
            }

            private void selectTag(String tag) {
                if (!mFilterTags[filterIndex].equals(tag)) {
                    mFilterTags[filterIndex] = tag;
                    populateScheduleGrid(true);
                }
            }
        });

        if (itemToSelect != spinner.getSelectedItemPosition()) {
            spinner.setSelection(itemToSelect, false);
            spinner.onRestoreInstanceState(spinnerState);
        }
    }

    private void showFilterBox(boolean show) {
        if (mFiltersBox != null) {
            mFiltersBox.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        if (!show) {
            mSecondaryFilterSpinner1.setSelection(0);
        }
    }

    private void computeActionBarSpinnerAdapter() {
        mFilterScheduleSpinnerAdapter.clear();

        mFilterScheduleSpinnerAdapter.addItem("", getString(R.string.all_talks), false, 0);

        if (mSchedule != null) {
            int itemToSelect = -1;
            mTagMetadata = TagMetadata.fromSchedule(mSchedule);
            for (int i = 0; i < Tags.FILTER_CATEGORIES.length; i++) {
                String category = Tags.FILTER_CATEGORIES[i];
                String categoryTitle = getString(Tags.FILTER_CATEGORY_TITLE[i]);

                List<TagMetadata.Tag> tags = mTagMetadata.getTagsInCategory(category);
                if (tags != null) {
                    mFilterScheduleSpinnerAdapter.addHeader(categoryTitle);
                    for (TagMetadata.Tag tag : tags) {
                        mFilterScheduleSpinnerAdapter.addItem(tag.getId(), tag.getName(), true, Tags.CATEGORY_TOPIC.equals(category) ? tag
                                .getColor() : 0);
                        if (!TextUtils.isEmpty(mFilterTagsToRestore[0]) && tag.getId().equals(mFilterTagsToRestore[0])) {
                            mFilterTagsToRestore[0] = null;
                            itemToSelect = mFilterScheduleSpinnerAdapter.getCount() - 1;
                        }
                    }
                } else {
                    Timber.w("Ignoring filter category with no tags: " + category);
                }
            }
            mFilterTagsToRestore[0] = null;

            if (itemToSelect >= 0) {
                Timber.d("Restoring item selection to primary spinner: " + itemToSelect);
                mSpinner.setSelection(itemToSelect);
            }
        }

        mFilterScheduleSpinnerAdapter.notifyDataSetChanged();
        showSecondaryFilters();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mFilterTagsToRestore[0] = mFilterTags[0];
        mFilterTagsToRestore[1] = mFilterTags[1];
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public boolean handleResult(CursorList<Talk> cursorList) {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(false);
        }
        mSchedule = new Schedule(cursorList == null ? new ArrayList<Talk>() : cursorList.asList(), true);
        mTagMetadata = TagMetadata.fromSchedule(mSchedule);

        if (getView() == null) {
            return true;
        }

        populateScheduleGrid(true, mFirstLoad);

        mFirstLoad = false;

        return true;
    }

    @Override
    public void onDestroyView() {
        mSpinner = null;
        BaseActivity activity = (BaseActivity) getActivity();
        mScheduleGrid.setOnScrollListener(null);
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(true);
        retrievedTalks();
    }
}
