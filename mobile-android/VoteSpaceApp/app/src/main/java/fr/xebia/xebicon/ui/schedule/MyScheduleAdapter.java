/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package fr.xebia.xebicon.ui.schedule;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.xebia.xebicon.R;
import fr.xebia.xebicon.core.utils.Compatibility;
import fr.xebia.xebicon.core.utils.TimeUtils;
import fr.xebia.xebicon.model.MyScheduleItem;
import fr.xebia.xebicon.model.Talk;
import fr.xebia.xebicon.ui.browse.BrowseTalksActivity;
import fr.xebia.xebicon.ui.talk.TalkActivity;
import fr.xebia.xebicon.ui.widget.UIUtils;
import timber.log.Timber;

/**
 * Adapter that produces views to render (one day of) the "My Schedule" screen.
 */
public class MyScheduleAdapter implements ListAdapter, AbsListView.RecyclerListener {

    private static final int TAG_ID_FOR_VIEW_TYPE = R.id.myschedule_viewtype_tagkey;
    private static final int TAG_ID_FOR_ITEM = R.id.myschedule_item_tagkey;
    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_NOW = 1;
    private static final int VIEW_TYPE_PAST_DURING_CONFERENCE = 2;

    private final Context mContext;
    private final int mItemTouchOverlayResId;

    // list of items served by this adapter
    ArrayList<MyScheduleItem> mItems = new ArrayList<>();

    // observers to notify about changes in the data
    ArrayList<DataSetObserver> mObservers = new ArrayList<>();

    int mThemePrimaryColor;
    int mDefaultStartTimeColor;
    int mDefaultEndTimeColor;

    public MyScheduleAdapter(Context context) {
        mContext = context;

        TypedValue a = new TypedValue();
        mContext.getTheme().resolveAttribute(R.attr.colorPrimary, a, true);
        mThemePrimaryColor = a.data;

        mContext.getTheme().resolveAttribute(R.attr.myScheduleItemTouchOverlay, a, true);
        mItemTouchOverlayResId = a.resourceId;

        mDefaultStartTimeColor = mContext.getResources().getColor(R.color.body_text_2);
        mDefaultEndTimeColor = mContext.getResources().getColor(R.color.body_text_3);

    }

    @Override
    public boolean areAllItemsEnabled() {
        return true;
    }

    @Override
    public boolean isEnabled(int position) {
        return true;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        if (!mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (mObservers.contains(observer)) {
            mObservers.remove(observer);
        }
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public MyScheduleItem getItem(int position) {
        return position >= 0 && position < mItems.size() ? mItems.get(position) : null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    private static final String MY_VIEW_TAG = "MyScheduleAdapter_MY_VIEW_TAG";

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Resources res = mContext.getResources();

        TextView startTimeView;
        TextView endTimeView;

        int itemViewType = getItemViewType(position);
        boolean isNowPlaying = false;
        boolean isPastDuringConference = false;
        int layoutResId = R.layout.my_schedule_item;
        if (itemViewType == VIEW_TYPE_NOW) {
            isNowPlaying = true;
            layoutResId = R.layout.my_schedule_item_now;
        } else if (itemViewType == VIEW_TYPE_PAST_DURING_CONFERENCE) {
            isPastDuringConference = true;
            layoutResId = R.layout.my_schedule_item_past;
        }

        // If the view to recycle is null or is for the wrong view type or data
        // generation, ignore it and create a new one.
        if (view == null || !MY_VIEW_TAG.equals(view.getTag())
                || view.getTag(TAG_ID_FOR_VIEW_TYPE) == null
                || !view.getTag(TAG_ID_FOR_VIEW_TYPE).equals(itemViewType)) {
            view = ((LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
                    .inflate(layoutResId, parent, false);
            // save this view's type, so we only recycle when the view's type is the same:
            view.setTag(TAG_ID_FOR_VIEW_TYPE, itemViewType);
            // Use one listener per view, so when the view is recycled, the listener is reused as
            // well. Use the View tag as a container for the destination Uri.
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyScheduleItem myScheduleItem = (MyScheduleItem) v.getTag(TAG_ID_FOR_ITEM);
                    Intent intent = null;
                    switch (myScheduleItem.type) {
                        case MyScheduleItem.FREE:
                            intent = new Intent(mContext, BrowseTalksActivity.class);
                            intent.putExtra(BrowseTalksActivity.EXTRA_TITLE, TimeUtils.formatDayTime(new Date(myScheduleItem.startTime)));
                            intent.putExtra(BrowseTalksActivity.EXTRA_AVAILABLE_TALKS, myScheduleItem.getAvailableTalksIds());
                            break;
                        case MyScheduleItem.SESSION:
                            Talk selectedTalk = myScheduleItem.selectedTalk;
                            intent = new Intent(mContext, TalkActivity.class);
                            intent.putExtra(TalkActivity.EXTRA_TALK_ID, selectedTalk.getId());
                            intent.putExtra(TalkActivity.EXTRA_TALK_TITLE, selectedTalk.getTitle());
                            intent.putExtra(TalkActivity.EXTRA_TALK_COLOR, selectedTalk.getColor());
                            break;
                    }
                    if (intent != null) {
                        mContext.startActivity(intent);
                    }
                }
            });
        }

        if (position < 0 || position >= mItems.size()) {
            Timber.e("Invalid view position passed to MyScheduleAdapter: " + position);
            return view;
        }
        final MyScheduleItem item = mItems.get(position);

        view.setTag(MY_VIEW_TAG);
        startTimeView = (TextView) view.findViewById(R.id.start_time);
        endTimeView = (TextView) view.findViewById(R.id.end_time);
        ImageView bgImageView = (ImageView) view.findViewById(R.id.background_image);
        final ImageView sessionImageView = (ImageView) view.findViewById(R.id.session_image);
        FrameLayout boxView = (FrameLayout) view.findViewById(R.id.box);
        TextView slotTitleView = (TextView) view.findViewById(R.id.slot_title);
        TextView slotSubtitleView = (TextView) view.findViewById(R.id.slot_subtitle);
        ImageButton giveFeedbackButton = (ImageButton) view.findViewById(R.id.give_feedback_button);
        int heightNormal = res.getDimensionPixelSize(R.dimen.my_schedule_item_height);
        int heightBreak = ViewGroup.LayoutParams.WRAP_CONTENT;
        int heightPast = res.getDimensionPixelSize(R.dimen.my_schedule_item_height_past);

        long now = System.currentTimeMillis();
        boolean showEndTime = false;
        boolean isBlockNow = false;
        if (item.endTime <= now) {
            // session has ended
            startTimeView.setText(R.string.session_finished);
        } else if (item.startTime <= now) {
            // session is happening now!
            isBlockNow = true;
            startTimeView.setText(R.string.session_now);
            showEndTime = item.type == MyScheduleItem.BREAK || item.hasTalkSelected();
        } else {
            // session in the future
            startTimeView.setText(TimeUtils.formatShortTime(new Date(item.startTime)));
            // do we need and end time view?
            showEndTime = item.type == MyScheduleItem.BREAK || item.hasTalkSelected();
        }

        if (endTimeView != null) {
            if (showEndTime) {
                endTimeView.setVisibility(View.VISIBLE);
                endTimeView.setText(res.getString(R.string.my_schedule_end_time,
                        TimeUtils.formatShortTime(new Date(item.endTime))));
            } else {
                // no need to show end time
                endTimeView.setVisibility(View.GONE);
            }
        }

        View conflictWarning = view.findViewById(R.id.conflict_warning);
        if (conflictWarning != null) {
            conflictWarning.setVisibility(View.GONE);
        }

        // Set default colors to time indicators, in case they were overridden by conflict warning:
        if (!isNowPlaying) {
            if (startTimeView != null) {
                startTimeView.setTextColor(mDefaultStartTimeColor);
            }
            if (endTimeView != null) {
                endTimeView.setTextColor(mDefaultEndTimeColor);
            }
        }

        view.setTag(TAG_ID_FOR_ITEM, item);
        if (item.type == MyScheduleItem.FREE) {
            view.getLayoutParams().height = isPastDuringConference ? heightPast : heightNormal;
            boxView.setBackgroundResource(R.drawable.my_schedule_item_free);
            boxView.setForeground(mContext.getResources().getDrawable(mItemTouchOverlayResId));
            bgImageView.setVisibility(View.GONE);
            sessionImageView.setVisibility(View.GONE);
            if (giveFeedbackButton != null) {
                giveFeedbackButton.setVisibility(View.GONE);
            }
            slotTitleView.setText(R.string.browse_sessions);
            slotTitleView.setTextColor(mThemePrimaryColor);
            slotTitleView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
            if (slotSubtitleView != null) {
                List<Talk> availableTalks = item.availableTalks;
                slotSubtitleView.setText(res.getQuantityString(R.plurals.available_talks, availableTalks.size(), availableTalks.size()));
                slotSubtitleView.setTextColor(res.getColor(R.color.body_text_2));
            }
        } else if (item.type == MyScheduleItem.BREAK) {
            view.getLayoutParams().height = isPastDuringConference ? heightPast : heightBreak;
            boxView.setBackgroundResource(R.drawable.my_schedule_item_break);
            boxView.setForeground(null);
            bgImageView.setVisibility(View.GONE);
            sessionImageView.setVisibility(View.GONE);
            if (giveFeedbackButton != null) {
                giveFeedbackButton.setVisibility(View.GONE);
            }
            slotTitleView.setText(Compatibility.getLocalizedTitle(mContext, item.title));
            slotTitleView.setTextColor(res.getColor(R.color.body_text_1));
            slotTitleView.setTypeface(Typeface.SANS_SERIF, Typeface.NORMAL);
            if (slotSubtitleView != null) {
                slotSubtitleView.setText(item.subtitle);
                slotSubtitleView.setTextColor(res.getColor(R.color.body_text_2));
            }

        } else if (item.type == MyScheduleItem.SESSION) {
            view.getLayoutParams().height = isPastDuringConference ? heightPast : heightNormal;
            boxView.setBackgroundResource(R.drawable.my_schedule_item_session);
            boxView.setForeground(mContext.getResources().getDrawable(mItemTouchOverlayResId));
            bgImageView.setVisibility(View.VISIBLE);
            sessionImageView.setVisibility(View.VISIBLE);
            if (giveFeedbackButton != null) {
                boolean showFeedbackButton = !item.hasGivenFeedback;
                // Can't use isPastDuringConference because we want to show feedback after the
                // conference too.
                if (showFeedbackButton) {
                    if (item.endTime > now) {
                        // Session hasn't finished yet, don't show button.
                        showFeedbackButton = false;
                    }
                }
                giveFeedbackButton.setVisibility(showFeedbackButton ? View.VISIBLE : View.GONE);
                giveFeedbackButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Talk selectedTalk = item.selectedTalk;
                        Intent intent = new Intent(mContext, TalkActivity.class);
                        intent.putExtra(TalkActivity.EXTRA_TALK_ID, selectedTalk.getId());
                        intent.putExtra(TalkActivity.EXTRA_TALK_TITLE, selectedTalk.getTitle());
                        intent.putExtra(TalkActivity.EXTRA_TALK_COLOR, selectedTalk.getColor());
                        mContext.startActivity(intent);
                    }
                });
            }
            int color = UIUtils.scaleSessionColorToDefaultBG(item.backgroundColor == 0 ? mThemePrimaryColor : item.backgroundColor);

            final ColorDrawable colorDrawable = new ColorDrawable(color);
            bgImageView.setImageDrawable(colorDrawable);
            bgImageView.setColorFilter(UIUtils.setColorAlpha(color, UIUtils.SESSION_PHOTO_SCRIM_ALPHA));

            sessionImageView.setColorFilter(UIUtils.setColorAlpha(color, UIUtils.SESSION_PHOTO_SCRIM_ALPHA));
            view.post(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(mContext).load(getTalkBackgroundResource(item.selectedTalk)).into(sessionImageView);
                }
            });

            slotTitleView.setText(item.title);
            slotTitleView.setTextColor(isBlockNow ? Color.WHITE : res.getColor(R.color.body_text_1_inverse));
            slotTitleView.setTypeface(Typeface.SANS_SERIF, Typeface.BOLD);
            if (slotSubtitleView != null) {
                slotSubtitleView.setText(item.subtitle);
                slotSubtitleView.setTextColor(res.getColor(R.color.body_text_2_inverse));
            }

        } else {
            Timber.e("Invalid item type in MyScheduleAdapter: " + item.type);
        }

        // show or hide the "conflict" warning
        if (!isPastDuringConference) {
            final boolean showConflict = item.conflicting;
            conflictWarning.setVisibility(showConflict ? View.VISIBLE : View.GONE);
            if (showConflict && !isNowPlaying) {
                int conflictColor = res.getColor(R.color.my_schedule_conflict);
                startTimeView.setTextColor(conflictColor);
                if (endTimeView != null) {
                    endTimeView.setTextColor(conflictColor);
                }
            }
        }

        return view;
    }

    private int getTalkBackgroundResource(Talk talk) {
        return mContext.getResources().getIdentifier("devoxx_talk_template_" + talk.getPosition() % 14, "drawable", mContext.getPackageName());
    }

    @Override
    public int getItemViewType(int position) {
        if (position < 0 || position >= mItems.size()) {
            Timber.e("Invalid view position passed to MyScheduleAdapter: " + position);
            return VIEW_TYPE_NORMAL;
        }
        MyScheduleItem item = mItems.get(position);
        long now = System.currentTimeMillis();
        if (item.startTime <= now && now <= item.endTime && item.type == MyScheduleItem.SESSION) {
            return VIEW_TYPE_NOW;
        } else if (item.endTime <= now) {
            return VIEW_TYPE_PAST_DURING_CONFERENCE;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public boolean isEmpty() {
        return mItems.isEmpty();
    }

    public void clear() {
        updateItems(null);
    }

    private void notifyObservers() {
        for (DataSetObserver observer : mObservers) {
            observer.onChanged();
        }
    }

    public void forceUpdate() {
        notifyObservers();
    }

    public void updateItems(List<MyScheduleItem> items) {
        mItems.clear();
        if (items != null) {
            mItems.addAll(items);
        }
        notifyObservers();
    }

    @Override
    public void onMovedToScrapHeap(View view) {
        if (view != null) {
            ImageView sessionImageView = (ImageView) view.findViewById(R.id.session_image);
            if (sessionImageView != null) {
                sessionImageView.clearAnimation();
            }
        }
    }
}
