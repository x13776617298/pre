package com.babytree.apps.biz.home.adapter;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

/**
 * 主页菜单的专用Adapter，该Adapter对外不提供修改内部菜单数据的操作。
 * 
 * @author pengxh
 */
public class MommyMenuAdapter extends BaseAdapter {
	private static final String TAG = "MenuAdapter";
	private static int DIGITAL_ZERO = 0;// 表示无数据。
	private static int DEFAULT_INIT_VALUE = -1;

	private String[] mMenuTexts;
	private TypedArray mImgs;
	private Context mContext;
	private LayoutInflater mInflater;
	private int mCount = DEFAULT_INIT_VALUE;// 为-1，防止取余时被0除，而报错误。
	private Map<Integer, View> arrayList;

	private BabytreeBitmapCache mBitmapCache;

	/**
	 * 默认用户头像
	 */
	private Bitmap mAvatorBitmap;
	private Resources res;

	public MommyMenuAdapter(Context context) {
		initMenuParams(context);
	}

	/**
	 * 初始化菜单信息
	 * 
	 * @param context
	 */
	public void initMenuParams(Context context) {
		this.mContext = context;
		res = mContext.getResources();
		if (BabytreeUtil.isPregnancy(mContext)) {// 育儿页菜单数据
			mMenuTexts = res.getStringArray(R.array.home_slide_menu_mommy_titles_yuer);// title
			mImgs = res.obtainTypedArray(R.array.home_slide_menu_mommy_icons_yuer);// icon

		} else {// 孕期页菜单数据
			int pregnancyWeeks = BabytreeUtil.getPregnancyWeeks(mContext);
			if (pregnancyWeeks >= 36) { // 显示育儿菜单
				BabytreeLog.d("显示切换育儿菜单 - 怀孕周数 - " + pregnancyWeeks);
				mMenuTexts = res.getStringArray(R.array.home_slide_menu_mommy_titles);// title
				mImgs = res.obtainTypedArray(R.array.home_slide_menu_mommy_icons);// icon
			} else {
				mMenuTexts = res.getStringArray(R.array.home_slide_menu_mommy_titles_yuer);// title
				mImgs = res.obtainTypedArray(R.array.home_slide_menu_mommy_icons_yuer);// icon
			}
		}
		mInflater = LayoutInflater.from(mContext);
		mBitmapCache = BabytreeBitmapCache.create(mContext);
		mAvatorBitmap = BitmapFactory.decodeResource(mContext.getResources(),
				mImgs.getResourceId(1, DEFAULT_INIT_VALUE));
		arrayList = new HashMap<Integer, View>(mMenuTexts.length);
		BabytreeLog.d(TAG + " init size " + arrayList.size());

		if (this.selectItemPosition > 1) {
			setSelectItemPositionAfterRefresh();
		}
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
			convertView = mInflater.inflate(R.layout.slide_menu_mommy_row, parent, false);
			holder = new ViewHolder();
			holder.menuBg = (RelativeLayout) convertView.findViewById(R.id.row_layout);
			holder.menuText = (TextView) convertView.findViewById(R.id.row_title);
			holder.menuLeftImg = (ImageView) convertView.findViewById(R.id.row_icon);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (isEmpty()) {// 防止取余时被0除
			return null;
		}

		int mode = position % mCount;
		holder.menuText.setText(mMenuTexts[mode]);
		holder.menuText.setTextColor(getColor(R.color.menu_text_color));
		holder.menuText.setShadowLayer(5f, 1f, 0f, getColor(R.color.menu_text_shadow_color));
		int id = mImgs.getResourceId(mode, DEFAULT_INIT_VALUE);
		// if (id != DEFAULT_INIT_VALUE) {
		// holder.menuLeftImg.setVisibility(View.VISIBLE);
		holder.menuLeftImg.setImageResource(id);
		// } else {
		// holder.menuLeftImg.setVisibility(View.GONE);
		// }
		arrayList.put(position, convertView);
		updateUser(position);
		// BabytreeLog.d(TAG + " getView size " + arrayList.size());

		if (position == selectItemPosition) {
			convertView.setBackgroundColor(res.getColor(R.color.c_home_menu_background_selected));
		} else {
			convertView.setBackgroundResource(R.drawable.list_slidemenu_bj);
		}

		return convertView;
	}

	private void setv(View view, int visible) {
		view.setVisibility(visible);
	}

	public void updateUser(int position) {
		if (position == 1) {// 更新用户项
			ViewHolder holder = (ViewHolder) arrayList.get(position).getTag();
			// BabytreeLog.d(TAG + " 更新用户Item信息 position = " + position);
			String userAvatorUrl = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.HEAD);
			String userNickName = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.NICKNAME);
			String userName = (userNickName == null || userNickName.equalsIgnoreCase("")) ? mMenuTexts[position]
					: userNickName;
			if (BabytreeUtil.isLogin(mContext)) { // 用户项(已登录)
				holder.menuText.setText(userName);
				mBitmapCache.display(holder.menuLeftImg, userAvatorUrl, mAvatorBitmap, mAvatorBitmap);
			} else { // 未登录
				if (arrayList != null && arrayList.size() == mMenuTexts.length) {
					// int position = 1;
					// View view = arrayList.get(position);
					// ViewHolder holder = (ViewHolder) view.getTag();
					// String userNickName =
					// SharedPreferencesUtil.getStringValue(mContext,
					// ShareKeys.NICKNAME);
					// String userName = (userNickName == null ||
					// userNickName.equalsIgnoreCase("")) ? mMenuTexts[position]
					// : userNickName;
					holder.menuText.setText(userName);
					holder.menuLeftImg.setImageResource(mImgs.getResourceId(1, DEFAULT_INIT_VALUE));
				}
			}
			notifyDataSetChanged();
		}
	}

	/**
	 * 更新菜单项
	 * 
	 * @param position
	 *            菜单项位置position
	 */
	public synchronized void updateMenuNickNameItem() {
		if (arrayList != null && arrayList.size() == mMenuTexts.length) {
			int position = 1;
			View view = arrayList.get(position);
			ViewHolder holder = (ViewHolder) view.getTag();
			String userNickName = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.NICKNAME);
			String userName = (userNickName == null || userNickName.equalsIgnoreCase("")) ? mMenuTexts[position]
					: userNickName;
			holder.menuText.setText(userName);
			holder.menuLeftImg.setImageResource(mImgs.getResourceId(1, DEFAULT_INIT_VALUE));
			BabytreeLog.d(TAG + " 更新导航菜单用户信息");
			notifyDataSetChanged();
		}
	}

	/**
	 * 更新菜单项
	 * 
	 * @param position
	 */
	// public synchronized void updateMenuUIItem(int position) {
	// View view = arrayList.get(oldPosition);
	// ViewHolder holder = (ViewHolder) view.getTag();
	//
	// view.setBackgroundResource(TRANSPARENT);
	// holder.menuText.setText(mMenuTexts[oldPosition]);
	//
	// view = arrayList.get(position);
	// holder = (ViewHolder) view.getTag();
	//
	// view.setBackgroundResource(R.drawable.menu_list_item_bg);
	// holder.menuText.setText(mMenuTexts[position]);
	//
	// oldPosition = position;
	// notifyDataSetChanged();
	// }

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
		RelativeLayout menuBg;
		TextView menuText;
		ImageView menuLeftImg;
	}

	/**
	 * 刷新菜单
	 * 
	 * @param context
	 */
	public synchronized void refreshAllMenuItem(Context context) {
		BabytreeLog.d("刷新所有菜单项");
		initMenuParams(context);
	}

	/**
	 * 设置当前选择的item position
	 * 
	 * @param selectItem
	 */
	public void setSelectItemPosition(int selectItem) {
		this.selectItemPosition = selectItem;
		this.selectItemText = mMenuTexts[selectItem];
	}

	/**
	 * 设置菜单刷新之后的item position, 用来设置选中的listView的item背景
	 * <p>
	 * 注意：必须在initMenuParams(context)之后调用
	 * 
	 * @param selectItem
	 */
	public void setSelectItemPositionAfterRefresh() {
		int length = mMenuTexts.length;
		for (int i = 0; i < length; i++) {
			if (selectItemText.equalsIgnoreCase(mMenuTexts[i])) {
				this.selectItemPosition = i;
				break;
			}
		}
	}

	/**
	 * 当前选中的行position
	 */
	private int selectItemPosition = 0;

	/**
	 * 当前选中的行position
	 */
	private String selectItemText = "首页";

}
