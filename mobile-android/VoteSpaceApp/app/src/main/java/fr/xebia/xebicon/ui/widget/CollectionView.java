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

package fr.xebia.xebicon.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import fr.xebia.xebicon.R;
import timber.log.Timber;


public class CollectionView extends ListView {
    private static final String TAG = "CollectionView";

    private static final int BUILTIN_VIEWTYPE_HEADER = 0;
    private static final int BUILTIN_VIEWTYPE_COUNT = 1;
    RowComputeResult mRowComputeResult = new RowComputeResult();
    private Inventory mInventory = new Inventory();
    private CollectionViewCallbacks mCallbacks = null;
    private int mContentTopClearance = 0;
    private int mInternalPadding;
    private MultiScrollListener mMultiScrollListener;

    public CollectionView(Context context) {
        this(context, null);
    }

    public CollectionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollectionView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setAdapter(new MyListAdapter());
        setDivider(null);
        setDividerHeight(0);
        setItemsCanFocus(false);
        setChoiceMode(ListView.CHOICE_MODE_NONE);
        setSelector(android.R.color.transparent);

        if (attrs != null) {
            final TypedArray xmlArgs = context.obtainStyledAttributes(attrs, R.styleable.CollectionView, defStyle, 0);
            mInternalPadding = xmlArgs.getDimensionPixelSize(R.styleable.CollectionView_internalPadding, 0);
            mContentTopClearance = xmlArgs.getDimensionPixelSize(R.styleable.CollectionView_contentTopClearance, 0);
        }
    }

    public void updateInventory(final Inventory inv) {
        updateInventory(inv, true);
    }

    public void updateInventory(final Inventory inv, boolean animate) {
        if (animate) {
            Timber.d("CollectionView updating inventory with animation.");
            setAlpha(0);
            updateInventoryImmediate(inv, animate);
            doFadeInAnimation();
        } else {
            Timber.d("CollectionView updating inventory without animation.");
            updateInventoryImmediate(inv, animate);
        }
    }

    private void updateInventoryImmediate(Inventory inv, boolean animate) {
        mInventory = new Inventory(inv);
        notifyAdapterDataSetChanged();
        if (animate) {
            startLayoutAnimation();
        }
    }

    private void doFadeInAnimation() {
        setAlpha(0);
        animate().setDuration(350).alpha(1);
    }

    public void setCollectionAdapter(CollectionViewCallbacks adapter) {
        mCallbacks = adapter;
    }

    private void notifyAdapterDataSetChanged() {
        // We have to set up a new adapter (as opposed to just calling notifyDataSetChanged()
        // because we might need MORE view types than before, and ListView isn't prepared to
        // handle the case where its existing adapter suddenly needs to increase the number of
        // view types it needs.
        setAdapter(new MyListAdapter());
    }

    public void setContentTopClearance(int clearance) {
        if (mContentTopClearance != clearance) {
            mContentTopClearance = clearance;
            setPadding(getPaddingLeft(), mContentTopClearance,
                    getPaddingRight(), getPaddingBottom());
            notifyAdapterDataSetChanged();
        }
    }

    private boolean computeRowContent(int row, RowComputeResult result) {
        int curRow = 0;
        int posInGroup = 0;
        for (InventoryGroup group : mInventory.mGroups) {
            if (group.mShowHeader) {
                if (curRow == row) {
                    // row is a group header!
                    result.row = row;
                    result.isHeader = true;
                    result.groupId = group.mGroupId;
                    result.group = group;
                    result.groupOffset = -1;
                    return true;
                }
                curRow++;
            }
            posInGroup = 0;
            while (posInGroup < group.mItemCount) {
                if (curRow == row) {
                    // this is the row we are looking for
                    result.row = row;
                    result.isHeader = false;
                    result.groupId = group.mGroupId;
                    result.group = group;
                    result.groupOffset = posInGroup;
                    return true;
                }
                // advance to next row
                posInGroup += group.mDisplayCols;
                curRow++;
            }
        }
        return false;
    }

    private View getRowView(int row, View convertView, ViewGroup parent) {
        if (computeRowContent(row, mRowComputeResult)) {
            return makeRow(convertView, mRowComputeResult, parent);
        } else {
            Log.e(TAG, "Invalid row passed to getView: " + row);
            return convertView != null ? convertView : new View(getContext());
        }
    }

    private int getRowViewType(int row) {
        if (computeRowContent(row, mRowComputeResult)) {
            int type;
            if (mRowComputeResult.isHeader) {
                type = BUILTIN_VIEWTYPE_HEADER;
            } else {
                type = BUILTIN_VIEWTYPE_COUNT + mInventory.getGroupIndex(mRowComputeResult.groupId);
            }
            return type;
        } else {
            Log.e(TAG, "Invalid row passed to getItemViewType: " + row);
            return 0;
        }
    }

    @Override
    public void setOnScrollListener(OnScrollListener listener) {
        if (mMultiScrollListener == null) {
            mMultiScrollListener = new MultiScrollListener();
            super.setOnScrollListener(mMultiScrollListener);
        }
        mMultiScrollListener.addOnScrollListener(listener);
    }

    private View makeRow(View view, RowComputeResult rowInfo, ViewGroup parent) {
        if (mCallbacks == null) {
            Log.e(TAG, "Call to makeRow without an adapter installed");
            return view != null ? view : new View(getContext());
        }

        // Notice that view types are tied to a specific instance of mInventory by hashcode,
        // so when mInventory is updated, we don't attempt to reuse views that were used for
        // a previous incarnation of mInventory (the views may be incompatible).
        String desiredViewType = mInventory.hashCode() + "." + getRowViewType(rowInfo.row);
        String actualViewType = (view != null && view.getTag() != null) ?
                view.getTag().toString() : "";
        if (!desiredViewType.equals(actualViewType)) {
            // We can't recycle this view. We have to make a new one.
            view = null;
        }

        if (rowInfo.isHeader) {
            if (view == null) {
                view = mCallbacks.newCollectionHeaderView(getContext(), parent);
            }
            mCallbacks.bindCollectionHeaderView(getContext(), view, rowInfo.groupId, rowInfo.group.mHeaderLabel);
        } else {
            view = makeItemRow(view, rowInfo);
        }

        view.setTag(desiredViewType);
        return view;
    }

    private View makeItemRow(View convertView, RowComputeResult rowInfo) {
        return (convertView == null) ? makeNewItemRow(rowInfo) : recycleItemRow(convertView, rowInfo);
    }

    private View getItemView(RowComputeResult rowInfo, int column, View view, ViewGroup parent) {
        int indexInGroup = rowInfo.groupOffset + column;
        if (indexInGroup >= rowInfo.group.mItemCount) {
            // out of bounds, so use an empty view
            if (view != null && view instanceof EmptyView) {
                return view;
            }
            view = new EmptyView(getContext());
            view.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            return view;
        }

        if (view == null || view instanceof EmptyView) {
            view = mCallbacks.newCollectionItemView(getContext(), rowInfo.groupId, parent);
        }

        mCallbacks.bindCollectionItemView(getContext(), view, rowInfo.group.getOffset() + indexInGroup);
        return view;
    }

    private LinearLayout.LayoutParams setupLayoutParams(View view) {
        LinearLayout.LayoutParams viewLayoutParams;
        if (view.getLayoutParams() instanceof LinearLayout.LayoutParams) {
            viewLayoutParams = (LinearLayout.LayoutParams) view.getLayoutParams();
        } else {
            // This shouldn't happen... but if it does, let's work around it as well as we can.
            Timber.w("Unexpected class for collection view item's layout params: " + view.getLayoutParams().getClass().getName());
            viewLayoutParams = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        }
        viewLayoutParams.leftMargin = mInternalPadding / 2;
        viewLayoutParams.rightMargin = mInternalPadding / 2;
        viewLayoutParams.bottomMargin = mInternalPadding;
        viewLayoutParams.width = LayoutParams.MATCH_PARENT;
        viewLayoutParams.weight = 1.0f;
        view.setLayoutParams(viewLayoutParams);
        return viewLayoutParams;
    }

    private View makeNewItemRow(RowComputeResult rowInfo) {
        LinearLayout ll = new LinearLayout(getContext());
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setDescendantFocusability(FOCUS_AFTER_DESCENDANTS);
        ll.setLayoutParams(params);

        int i;
        for (i = 0; i < rowInfo.group.mDisplayCols; i++) {
            View view = getItemView(rowInfo, i, null, ll);
            LinearLayout.LayoutParams viewLayoutParams = setupLayoutParams(view);
            ll.addView(view, viewLayoutParams);
        }

        return ll;
    }

    private View recycleItemRow(View convertView, RowComputeResult rowInfo) {
        int i;
        LinearLayout ll = (LinearLayout) convertView;
        for (i = 0; i < rowInfo.group.mDisplayCols; i++) {
            View view = ll.getChildAt(i);
            View newView = getItemView(rowInfo, i, view, ll);
            if (view != newView) {
                LinearLayout.LayoutParams thisViewParams = setupLayoutParams(newView);
                ll.removeViewAt(i);
                ll.addView(newView, i, thisViewParams);
            }
        }
        return ll;
    }

    private static class EmptyView extends View {
        private EmptyView(Context ctx) {
            super(ctx);
        }
    }

    public static class Inventory {
        private ArrayList<InventoryGroup> mGroups = new ArrayList<>();

        public Inventory() {
        }

        public Inventory(Inventory copyFrom) {
            for (InventoryGroup group : copyFrom.mGroups) {
                mGroups.add(group);
            }
        }

        public void addGroup(InventoryGroup group) {
            if (group.mItemCount > 0) {
                mGroups.add(new InventoryGroup(group));
            }
        }

        public int getTotalItemCount() {
            int total = 0;
            for (InventoryGroup group : mGroups) {
                total += group.mItemCount;
            }
            return total;
        }

        public int getGroupCount() {
            return mGroups.size();
        }

        public int getGroupIndex(int groupId) {
            for (int i = 0; i < mGroups.size(); i++) {
                if (mGroups.get(i).mGroupId == groupId) {
                    return i;
                }
            }
            return -1;
        }
    }

    private static class MultiScrollListener implements OnScrollListener {
        private final Set<OnScrollListener> children = new HashSet<>();


        public void addOnScrollListener(OnScrollListener listener) {
            children.add(listener);
        }


        @Override
        public void onScrollStateChanged(AbsListView absListView, int i) {
            for (OnScrollListener listener : children) {
                listener.onScrollStateChanged(absListView, i);
            }
        }

        @Override
        public void onScroll(AbsListView absListView, int i, int i2, int i3) {
            for (OnScrollListener listener : children) {
                listener.onScroll(absListView, i, i2, i3);
            }
        }
    }

    public static class InventoryGroup implements Cloneable {
        private int mGroupId = 0;
        private boolean mShowHeader = false;
        private String mHeaderLabel = "";
        private int mDataIndexStart = 0;
        private int mDisplayCols = 1;
        private int mItemCount = 0;
        private int mOffset;

        public InventoryGroup(int groupId) {
            mGroupId = groupId;
        }

        public InventoryGroup(InventoryGroup copyFrom) {
            mGroupId = copyFrom.mGroupId;
            mShowHeader = copyFrom.mShowHeader;
            mDataIndexStart = copyFrom.mDataIndexStart;
            mDisplayCols = copyFrom.mDisplayCols;
            mItemCount = copyFrom.mItemCount;
            mHeaderLabel = copyFrom.mHeaderLabel;
            mOffset = copyFrom.mOffset;
        }

        public InventoryGroup setShowHeader(boolean showHeader) {
            mShowHeader = showHeader;
            return this;
        }

        public String getHeaderLabel() {
            return mHeaderLabel;
        }

        public InventoryGroup setHeaderLabel(String label) {
            mHeaderLabel = label;
            return this;
        }

        public InventoryGroup setDataIndexStart(int dataIndexStart) {
            mDataIndexStart = dataIndexStart;
            return this;
        }

        public InventoryGroup setDisplayCols(int cols) {
            mDisplayCols = cols > 1 ? cols : 1;
            return this;
        }

        public InventoryGroup setItemCount(int count) {
            mItemCount = count;
            return this;
        }

        public InventoryGroup incrementItemCount() {
            mItemCount++;
            return this;
        }

        public int getRowCount() {
            return (mShowHeader ? 1 : 0) + (mItemCount / mDisplayCols) + ((mItemCount % mDisplayCols > 0) ? 1 : 0);
        }

        public int getOffset() {
            return mOffset;
        }

        public InventoryGroup setOffset(int offset) {
            mOffset = offset;
            return this;
        }
    }

    private class RowComputeResult {
        int row;
        boolean isHeader;
        int groupId;
        InventoryGroup group;
        int groupOffset;
    }

    protected class MyListAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            int rowCount = 0;
            for (InventoryGroup group : mInventory.mGroups) {
                int thisGroupRowCount = group.getRowCount();
                rowCount += thisGroupRowCount;
            }
            return rowCount;
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int row, View convertView, ViewGroup parent) {
            return getRowView(row, convertView, parent);
        }

        @Override
        public int getItemViewType(int row) {
            return getRowViewType(row);
        }

        @Override
        public int getViewTypeCount() {
            return BUILTIN_VIEWTYPE_COUNT + mInventory.mGroups.size();
        }
    }

}
