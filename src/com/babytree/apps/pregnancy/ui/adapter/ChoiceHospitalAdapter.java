package com.babytree.apps.pregnancy.ui.adapter;

import java.util.List;

import com.babytree.apps.comm.model.Hospital;
import com.babytree.apps.comm.model.SortedHospital;
import com.babytree.apps.comm.ui.adapter.PinnedHeaderListViewAdapter;
import com.babytree.apps.pregnancy.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChoiceHospitalAdapter extends PinnedHeaderListViewAdapter {

	private List<SortedHospital> list = null;
	private Context mContext;
	private int topPadding = 0;

	public ChoiceHospitalAdapter(Context mContext, List<SortedHospital> list) {
		this.mContext = mContext;
		this.list = list;
	}

	public int getCount() {
		return this.list.size();
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View view, ViewGroup arg2) {
		ViewHolder viewHolder = null;
		if (view == null) {
			viewHolder = new ViewHolder();
			view = LayoutInflater.from(mContext).inflate(
					R.layout.hospital_list_item, null);
			viewHolder.hospitalName = (TextView) view.findViewById(R.id.title);
			viewHolder.hospitalArea = (TextView) view
					.findViewById(R.id.hospital_information_header);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}
		SortedHospital sortHospital = list.get(position);
		Hospital mContent = sortHospital.getHospital();
		if (position == 0) {
			viewHolder.hospitalArea.setVisibility(View.VISIBLE);
			viewHolder.hospitalArea.setText(sortHospital.getSortName());
		} else {
			String lastCatalog = list.get(position - 1).getSortName();
			if (sortHospital.getSortName().equals(lastCatalog)) {
				viewHolder.hospitalArea.setVisibility(View.GONE);
			} else {
				viewHolder.hospitalArea.setVisibility(View.VISIBLE);
				viewHolder.hospitalArea.setText(sortHospital.getSortName());
			}
		}

		viewHolder.hospitalName.setText(mContent.name);

		return view;

	}

	static class ViewHolder {
		TextView hospitalName;
		TextView hospitalArea;
	}

	public Object[] getSections() {
		// TODO Auto-generated method stub
		SortedHospital[] res = new SortedHospital[list.size()];
		for (int i = 0; i < res.length; i++) {
			res[i] = list.get(i);
		}
		return res;
	}

	public int getSectionForPosition(int position) {

		return position;
	}

	public int getPositionForSection(int section) {
		SortedHospital mContent;
		String l;
		if (section == '!') {
			return 0;
		} else {
			for (int i = 0; i < getCount(); i++) {
				mContent = (SortedHospital) list.get(i);
				l = mContent.getSortName();
				if (l != "") {
					char firstChar = l.toUpperCase().charAt(0);
					if (firstChar == section) {
						return i;
					}
				}

			}
		}
		mContent = null;
		l = null;
		return -1;
	}

	@Override
	public void bindSectionHeader(View view, int position,
			boolean displaySectionHeader) {
		TextView tv = (TextView) view
				.findViewById(R.id.hospital_information_header);
		RelativeLayout layoutOther = (RelativeLayout) view
				.findViewById(R.id.hospital_information_layout_other);
		if (topPadding == 0) {
			topPadding = layoutOther.getPaddingTop();
		}
		LinearLayout layoutTitle = (LinearLayout) view
				.findViewById(R.id.hospital_area_title);
		if (displaySectionHeader) {
			layoutTitle.setVisibility(View.VISIBLE);
			tv.setText(((SortedHospital) getSections()[getSectionForPosition(position)])
					.getSortName());
			layoutOther.setPadding(0, topPadding, 0, 0);
		} else {
			layoutTitle.setVisibility(View.GONE);
			layoutOther.setPadding(0, 0, 0, 0);
		}
	}

	@Override
	public void configurePinnedHeader(View header, int position, int alpha) {
		// TODO Auto-generated method stub
		((TextView) header.findViewById(R.id.hospital_information_header))
				.setText(((SortedHospital) getSections()[getSectionForPosition(position)])
						.getSortName());
	}
}