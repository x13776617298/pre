package com.babytree.apps.biz.home.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.pregnancy.R;

/**
 * 妈妈版左侧菜单-通知列表header
 * 
 * @author pengxh
 */
public class NotifyHeaderView extends RelativeLayout {
	private static final String TAG = NotifyHeaderView.class.getSimpleName();
	private Context mContext;
	/**
	 * 加载提示
	 */
	private ProgressBar mProgressBar;

	private TextView mTitle;

	public NotifyHeaderView(Context context) {
		super(context);
		mContext = context;
	}

	public NotifyHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		BabytreeLog.d(TAG + " - onFinishInflate - init");
		init(mContext);
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 */
	private void init(Context context) {
		setBackgroundColor(mContext.getResources().getColor(R.color.c_home_menu_background_selected));
		mProgressBar = (ProgressBar) findViewById(R.id.header_loading);
		mTitle = (TextView) findViewById(R.id.header_title);
		mTitle.setShadowLayer(1f, 0f, 1f, android.R.color.black);
	}

	/**
	 * 设置title投影
	 * 
	 * @param radius
	 * @param dx
	 * @param dy
	 * @param color
	 */
	public void setTitleShadowLayer(float radius, float dx, float dy, int color) {
		mTitle.setShadowLayer(radius, dx, dy, color);
	}

	/**
	 * 设置title
	 * 
	 * @param text
	 */
	public void setTitleText(String text) {
		mTitle.setText(text);
	}

	/**
	 * 设置title字体大小
	 * 
	 * @param size
	 */
	public void setTitleTextSize(float size) {
		mTitle.setTextSize(size);
	}

	/**
	 * 设置title内容对其方式
	 * 
	 * @param gravity
	 */
	public void setTitleTextGravity(int gravity) {
		mTitle.setGravity(gravity);
	}

	/**
	 * 设置title字体颜色
	 * 
	 * @param color
	 */
	public void setTitleTextColor(int color) {
		mTitle.setTextColor(color);
	}

	/**
	 * 设置headerView属性
	 * 
	 * @param barName
	 * @param titleSize
	 * @param titleGravity
	 * @param color
	 */
	public void setTitleBar(String barName, int titleSize, int titleGravity, int color) {
		mTitle.setText(barName);
		mTitle.setTextSize(titleSize);
		mTitle.setGravity(titleGravity);
		mTitle.setTextColor(color);
	}

	/**
	 * 设置进度可见性
	 * 
	 * @param visible
	 *            View.VISIBLE / View.GONE
	 */
	public void setProgressVisiable(int visible) {
		mProgressBar.setVisibility(visible);
	}
}
