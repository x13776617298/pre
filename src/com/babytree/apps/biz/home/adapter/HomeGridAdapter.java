package com.babytree.apps.biz.home.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.pregnancy.R;

/**
 * 首页Grid 适配器
 * 
 * @author pengxh
 */
public class HomeGridAdapter extends BaseAdapter {
	private static final String TAG = "MenuAdapter";
	private static int DIGITAL_ZERO = 0;// 表示无数据。
	private static int DEFAULT_INIT_VALUE = -1;

	/**
	 * 首页gird title
	 */
	private String[] mItemTitles;

	/**
	 * 首页grid 背景颜色id
	 */
	private int[] mItemColorIds;

	/**
	 * 首页grid icon
	 */
	private TypedArray mItemIconIds;

	private Context mContext;

	private StateListDrawable listDrawable;

	public HomeGridAdapter(Context context) {
		this.mContext = context;
		Resources res = mContext.getResources();

		// 判断应用是孕期还是育儿 true:育儿 ，否则孕期 默认为孕期

		if (BabytreeUtil.isPregnancy(mContext)) {// 育儿页九宫格数据
			mItemTitles = res.getStringArray(R.array.home_grid_titles_yuer);// title
			mItemIconIds = res.obtainTypedArray(R.array.home_grid_drawable_ids_yuer);// icon

		} else {// 孕期页九宫格数据
			mItemTitles = res.getStringArray(R.array.home_grid_titles);// title
			mItemIconIds = res.obtainTypedArray(R.array.home_grid_drawable_ids);// icon
		}
		mItemColorIds = res.getIntArray(R.array.home_grid_color_ids);// backgroundColor

		BabytreeLog.d("colos " + mItemColorIds.length);
		int focusId = R.color.blue;
		listDrawable = BabytreeUtil.newSelector(mContext, R.drawable.trans, focusId, -1, -1);
	}

	@Override
	public int getCount() {
		// int result = DIGITAL_ZERO;
		// if (mItemTitles != null && mItemTitles.length != DIGITAL_ZERO)
		// result = mItemTitles.length;
		// mCount = (result == DIGITAL_ZERO ? DEFAULT_INIT_VALUE : result);
		// return result;
		return mItemTitles.length;
	}

	@Override
	public String getItem(int position) {
		if (isEmpty()) {// 防止取余时被0除
			return null;
		}
		// return mItemTitles[position % mCount];
		return mItemTitles[position];
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.homepage_grid_item, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.home_item_icon);
			holder.title = (TextView) convertView.findViewById(R.id.home_item_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		convertView.setBackgroundColor(mItemColorIds[position]);
//		 convertView.setBackgroundDrawable(listDrawable);
		holder.icon.setImageResource(mItemIconIds.getResourceId(position, -1));
		holder.title.setText(mItemTitles[position]);
		return convertView;
	}

	/**
	 * 更新九宫格
	 */
	private void updateGrideDate() {

	}

	/**
	 * 获取R.Color中的颜色值
	 * 
	 * @param color
	 * @return
	 */
	public int getColor(int color) {
		return mContext.getResources().getColor(color);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView title;
		ImageView icon;
	}
}
