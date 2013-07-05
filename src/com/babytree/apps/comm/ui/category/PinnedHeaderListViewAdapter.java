
package com.babytree.apps.comm.ui.category;

import com.babytree.apps.comm.ui.category.PinnedHeaderListView.PinnedHeaderAdapter;

import android.database.DataSetObserver;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SectionIndexer;

import java.util.HashMap;
import java.util.Map;

public abstract class PinnedHeaderListViewAdapter implements ListAdapter, PinnedHeaderAdapter,
        OnScrollListener, SectionIndexer, OnItemClickListener {

    private PinnedHeaderListViewSectionIndexer mIndexer;

    private String[] mTitles;// 所有分组的名字

    private int[] mCounts;// 所有分组的个数

    private int mTitleCounts = 0;

    private int viewTypeCount;

    private OnItemClickListener clickListenner;

    private PinnedHeaderListViewArrayAdapter arrayAdapter;

    private Map<String, View> currentViewSections = new HashMap<String, View>();

    public PinnedHeaderListViewAdapter(PinnedHeaderListViewArrayAdapter arrayAdapter) {
        this.arrayAdapter = arrayAdapter;
        arrayAdapter.registerDataSetObserver(dataSetObserver);
        updateTotalCount();
        mIndexer = new PinnedHeaderListViewSectionIndexer(mTitles, mCounts);
    }

    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            updateTotalCount();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            updateTotalCount();
        };
    };

    private boolean isTheSame(final String previousSection, final String newSection) {
        if (previousSection == null) {
            return newSection == null;
        } else {
            return previousSection.equals(newSection);
        }
    }

    private void fillSections() {
        mTitles = new String[mTitleCounts];
        mCounts = new int[mTitleCounts];
        final int count = arrayAdapter.getCount();
        String currentSection = null;
        int newSectionIndex = 0;
        int newSectionCounts = 0;
        String previousSection = null;
        for (int i = 0; i < count; i++) {
            newSectionCounts++;
            currentSection = arrayAdapter.items.get(i).title;
            if (!isTheSame(previousSection, currentSection)) {
                mTitles[newSectionIndex] = currentSection;
                previousSection = currentSection;
                if (newSectionIndex == 1) {// 如果是首次开始，则减1(因为第一次进入循环时，前一个为空，相当于indexCount多加了一次)
                    mCounts[0] = newSectionCounts - 1;
                } else if (newSectionIndex != 0) {
                    mCounts[newSectionIndex - 1] = newSectionCounts;
                }
                if (i != 0) {// 首次进入，计数不置0，其他情况，重新计数
                    newSectionCounts = 0;
                }
                newSectionIndex++;
            } else if (i == count - 1) {// 如果是最后一个,因为进入的时候把newSectionCounts置为0，下次不会计数，少加了一次
                mCounts[newSectionIndex - 1] = newSectionCounts + 1;
            }

        }
    }

    private void updateTotalCount() {
        String currentSection = null;
        viewTypeCount = arrayAdapter.getViewTypeCount() + 1;
        final int count = arrayAdapter.getCount();
        for (int i = 0; i < count; i++) {
            final PinnedHeaderListViewBean item = (PinnedHeaderListViewBean)arrayAdapter.getItem(i);
            if (!isTheSame(currentSection, item.title)) {
                mTitleCounts++;
                currentSection = item.title;
            }
        }
        fillSections();
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {
        arrayAdapter.registerDataSetObserver(observer);

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        arrayAdapter.unregisterDataSetObserver(observer);

    }
    
    public void notifyDataSetChanged(){
        arrayAdapter.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return arrayAdapter.getCount();
    }

    protected Integer getLinkedPosition(final int position) {
        return position;
    }

    protected PinnedHeaderListViewSectionIndexer getIndexer() {
        return mIndexer;
    }

    @Override
    public Object getItem(int position) {
        final int linkedItemPosition = getLinkedPosition(position);
        return arrayAdapter.getItem(linkedItemPosition);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return arrayAdapter.hasStableIds();
    }

    @Override
    public int getItemViewType(int position) {
        return arrayAdapter.getItemViewType(getLinkedPosition(position));
    }

    @Override
    public int getViewTypeCount() {
        return viewTypeCount;
    }

    @Override
    public boolean isEmpty() {
        return arrayAdapter.isEmpty();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
            int totalItemCount) {
        if (view instanceof PinnedHeaderListView) {
            ((PinnedHeaderListView)view).configureHeaderView(firstVisibleItem);
        }

    }

    @Override
    public int getPinnedHeaderState(int position) {
        int realPosition = position;
        if (mIndexer == null) {
            return PINNED_HEADER_GONE;
        }
        if (realPosition < 0) {
            return PINNED_HEADER_GONE;
        }
        int section = getSectionForPosition(realPosition);
        int nextSectionPosition = getPositionForSection(section + 1);
        if (nextSectionPosition != -1 && realPosition == nextSectionPosition - 1) {
            return PINNED_HEADER_PUSHED_UP;
        }
        return PINNED_HEADER_VISIBLE;
    }

    @Override
    public boolean areAllItemsEnabled() {
        return arrayAdapter.areAllItemsEnabled();
    }

    @Override
    public boolean isEnabled(int position) {
        return arrayAdapter.isEnabled(getLinkedPosition(position));
    }

    @Override
    public Object[] getSections() {
        if (mIndexer == null) {
            return new String[] {
                ""
            };
        } else {
            return mIndexer.getSections();
        }
    }

    @Override
    public int getPositionForSection(int section) {
        if (mIndexer == null) {
            return -1;
        }
        return mIndexer.getPositionForSection(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        if (mIndexer == null) {
            return -1;
        }
        return mIndexer.getSectionForPosition(position);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (clickListenner != null) {
            clickListenner.onItemClick(parent, view, getLinkedPosition(position), id);
        }
    }

    public void setOnItemClickListener(final OnItemClickListener linkedListener) {
        this.clickListenner = linkedListener;
    }

    protected synchronized void replaceSectionViewsInMaps(final String section, final View theView) {
        if (currentViewSections.containsKey(theView)) {
            currentViewSections.remove(theView);
        }
        currentViewSections.put(section, theView);
    }

}