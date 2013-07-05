package com.babytree.apps.biz.father.adapter;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 主页菜单的专用Adapter，该Adapter对外不提供修改内部菜单数据的操作。
 * 
 * @author gaierlin
 */
public class MenuAdapter extends BaseAdapter {
	private static final String TAG = "MenuAdapter";
	private static int TRANSPARENT = android.R.color.transparent;
	private static int DIGITAL_ZERO = 0;// 表示无数据。
	private static int DEFAULT_INIT_VALUE = -1;

	private String[] mMenuTexts;
	private TypedArray mImgs;
	private Context mContext;
	private LayoutInflater mInflater;
	private int mCount = DEFAULT_INIT_VALUE;// 为-1，防止取余时被0除，而报错误。
	private int oldPosition = DIGITAL_ZERO;
	private boolean isBinding = false;
	private ArrayList<View> arrayList;

	public MenuAdapter(Context context) {
		this.mContext = context;
		mMenuTexts = mContext.getResources().getStringArray(R.array.menu_names);
		mImgs = mContext.getResources().obtainTypedArray(R.array.menu_left_img);
		mInflater = LayoutInflater.from(mContext);

		arrayList = new ArrayList<View>(mMenuTexts.length);
		String inviteCode = SharedPreferencesUtil.getStringValue(mContext,
				ShareKeys.INVITE_CODE_KEY, null);
		isBinding = inviteCode != null;// 邀请不为是被绑定
	}

	@Override
	public int getCount() {
		int result = DIGITAL_ZERO;
		if (mMenuTexts != null && mMenuTexts.length != DIGITAL_ZERO)
			result = mMenuTexts.length;
		mCount = (result == DIGITAL_ZERO ? DEFAULT_INIT_VALUE : result);
		return result;
	}

	@Override
	public String getItem(int position) {
		if (isEmpty()) {// 防止取余时被0除
			return null;
		}
		return mMenuTexts[position % mCount];
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.row, parent, false);
			holder = new ViewHolder();
			holder.menuBg = (LinearLayout) convertView
					.findViewById(R.id.row_layout);
			holder.menuText = (TextView) convertView
					.findViewById(R.id.row_title);
			holder.menuLeftImg = (ImageView) convertView
					.findViewById(R.id.row_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (isEmpty()) {// 防止取余时被0除
			return null;
		}

		int mode = position % mCount;
		holder.menuText.setText(bindMenuName(mMenuTexts[mode]));
		holder.menuText.setTextColor(getColor(R.color.menu_text_color));
		holder.menuText.setShadowLayer(5f, 1f, 0f,
				getColor(R.color.menu_text_shadow_color));
		int id = mImgs.getResourceId(mode, DEFAULT_INIT_VALUE);
		if (id != DEFAULT_INIT_VALUE) {
			holder.menuLeftImg.setVisibility(View.VISIBLE);
			holder.menuLeftImg.setImageResource(id);
		} else {
			holder.menuLeftImg.setVisibility(View.GONE);
		}
		arrayList.add(mode, convertView);
		return convertView;
	}

	/**
	 * 更新菜单项
	 * 
	 * @param position
	 */
	public synchronized void updateMenuUIItem(int position) {
		Log.d(TAG, "position = " + position + " , oldPosition = " + oldPosition);
		if (oldPosition == position)
			return;
		View view = arrayList.get(oldPosition);
		ViewHolder holder = (ViewHolder) view.getTag();

		view.setBackgroundResource(TRANSPARENT);
		holder.menuText.setText(bindMenuName(mMenuTexts[oldPosition]));

		view = arrayList.get(position);
		holder = (ViewHolder) view.getTag();
		
		view.setBackgroundResource(R.drawable.menu_list_item_bg);
		holder.menuText.setText(bindMenuName(mMenuTexts[position]));
		
		oldPosition = position;
		// notifyDataSetChanged();
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
		LinearLayout menuBg;
		TextView menuText;
		ImageView menuLeftImg;
	}

	/**
	 * 菜单项的名字，根据绑定状态，决定菜单名称。
	 * 
	 * @param menuName
	 */
	private String bindMenuName(String menuName) {
		int pos = menuName.indexOf("|");
		if (pos == -1) {
			return menuName;
		}
		if (isBinding) {
			return menuName.substring(pos + 1);
		}
		return menuName.substring(0, pos);
	}
	
	public boolean isBinding(){
		return isBinding;
	}
	
	public void setBinding(boolean bindStatus){
		this.isBinding = bindStatus;
	}
	
	public synchronized void updateAllMenuItem(){
		View view = null;
		ViewHolder holder = null;
		for(int i =0;i<mCount;i++){
			view = arrayList.get(oldPosition);
			holder = (ViewHolder) view.getTag();
			holder.menuText.setText(bindMenuName(mMenuTexts[oldPosition]));
			Log.d(TAG,"updateAllMenuItem");
		}
	}
}
