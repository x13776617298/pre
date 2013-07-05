package com.babytree.apps.biz.home.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.babytree.apps.biz.father.util.Utils;
import com.babytree.apps.pregnancy.R;

/**
 * 首页ItemView
 * 
 * @author pengxh
 * 
 */
public class HomeGridItem extends LinearLayout {

	private Context mContext;

	/**
	 * item 图片
	 */
	private ImageView mItemImage;

	/**
	 * item title
	 */
	private TextView mItemTitle;

	public HomeGridItem(Context context) {
		super(context);
		mContext = context;
	}

	public HomeGridItem(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init(mContext);
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	private void init(Context context) {
		mItemImage = (ImageView) findViewById(R.id.home_item_icon);
		mItemTitle = (TextView) findViewById(R.id.home_item_title);
	}

	/**
	 * 获取title
	 */
	public String getTitle() {
		return mItemTitle.getText().toString();
	}

	/**
	 * 设置item icon
	 * 
	 * @param title
	 */
	public void setIcon(int resid) {
		if (Utils.checkResId(resid)) {
			mItemImage.setBackgroundResource(resid);
		}
	}

	/**
	 * 设置item title
	 * 
	 * @param title
	 */
	public void setTitle(String title) {
		mItemTitle.setText(title);
	}

	/**
	 * 设置Item 背景
	 * 
	 * @param resId
	 */
	public void setItemBackground(int resId) {
		if (Utils.checkResId(resId)) {
			setBackgroundResource(resId);
		}
	}

	/**
	 * 设置Item 颜色背景
	 * 
	 * @param resId
	 */
	public void setItemBackgroundColor(int color) {
		if (Utils.checkResId(color)) {
			setBackgroundColor(color);
		}
	}

	// /**
	// * 添加Item监听
	// *
	// * @param itemListener
	// */
	// public void setHomeGridItemListener(HomeGridItemListener itemListener) {
	// this.mGridItemListener = itemListener;
	// }
	//
	// /**
	// * 首页Grid Item监听器
	// *
	// * @author Administrator
	// *
	// */
	// public interface HomeGridItemListener {
	// /**
	// * item被点击
	// */
	// void itemOnClick();
	//
	// /**
	// * item被长按
	// */
	// void itemOnLongClick();
	// }
}
