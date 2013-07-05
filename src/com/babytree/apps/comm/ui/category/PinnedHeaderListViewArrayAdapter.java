
package com.babytree.apps.comm.ui.category;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

public class PinnedHeaderListViewArrayAdapter extends ArrayAdapter<PinnedHeaderListViewBean> {

    public ArrayList<PinnedHeaderListViewBean> items;

    public PinnedHeaderListViewArrayAdapter(final Context context, final int textViewResourceId,
            ArrayList<PinnedHeaderListViewBean> items) {
        super(context, textViewResourceId, items);
        this.items = items;
    }
}
