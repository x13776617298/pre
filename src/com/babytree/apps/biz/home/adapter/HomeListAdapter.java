package com.babytree.apps.biz.home.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.content.res.TypedArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.babytree.apps.biz.home.model.Commend;
import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.pregnancy.R;

/**
 * 首页Grid 适配器
 * 
 * @author pengxh
 */
public class HomeListAdapter extends BaseAdapter {
	private static final String TAG = "MenuAdapter";
	private static int DIGITAL_ZERO = 0;// 表示无数据。
	private static int DEFAULT_INIT_VALUE = -1;

	private TypedArray mImgs;
	private Context mContext;
	private LayoutInflater mInflater;

	private BabytreeBitmapCache bitmapCache;

	/**
	 * 推荐列表数据
	 */
	private ArrayList<Commend> mCommends;

	public HomeListAdapter(Context context, ArrayList<Commend> commends) {
		this.mContext = context;
		bitmapCache = BabytreeBitmapCache.create(mContext);
		mCommends = commends;
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		// int result = DIGITAL_ZERO;
		// if (mItemTitles != null && mItemTitles.length != DIGITAL_ZERO)
		// result = mItemTitles.length;
		// mCount = (result == DIGITAL_ZERO ? DEFAULT_INIT_VALUE : result);
		// return result;
		return mCommends.size();
	}

	@Override
	public Object getItem(int position) {
		return mCommends.get(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.homepage_list_item, null);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.home_list_icon);
			holder.title = (TextView) convertView.findViewById(R.id.home_list_title);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Commend commend = mCommends.get(position);
		bitmapCache.display(holder.icon, commend.getImg());
		holder.title.setText(commend.getTitle());
		return convertView;
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
