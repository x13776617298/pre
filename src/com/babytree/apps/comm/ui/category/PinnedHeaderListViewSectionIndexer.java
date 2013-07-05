
package com.babytree.apps.comm.ui.category;

import java.util.Arrays;

import android.widget.SectionIndexer;

public class PinnedHeaderListViewSectionIndexer implements SectionIndexer {
    private final String[] mTitles;

    private final int[] mPositions;

    private final int mCount;

    public PinnedHeaderListViewSectionIndexer(String[] titles, int[] counts) {
        if (titles == null || counts == null) {
            throw new NullPointerException();
        }
        if (titles.length != counts.length) {
            throw new IllegalArgumentException(
                    "The sections and counts arrays must have the same length");
        }
        this.mTitles = titles;
        mPositions = new int[counts.length];
        int position = 0;
        for (int i = 0; i < counts.length; i++) {
            if (mTitles[i] == null) {
                mTitles[i] = "";
            } else {
                mTitles[i] = mTitles[i].trim();
            }

            mPositions[i] = position;
            position += counts[i];
        }
        mCount = position;
    }

    @Override
    public Object[] getSections() {
        return mTitles;
    }

    @Override
    public int getPositionForSection(int section) {
        if (section < 0 || section > mTitles.length) {
            return -1;
        }
        if (section == mPositions.length) {
            return mPositions[mPositions.length - 1];
        }
        return mPositions[section];
    }

    @Override
    public int getSectionForPosition(int position) {
        if (position < 0 || position >= mCount) {
            return -1;
        }
        int index = Arrays.binarySearch(mPositions, position);
        return index >= 0 ? index : -index - 2;
    }

}
