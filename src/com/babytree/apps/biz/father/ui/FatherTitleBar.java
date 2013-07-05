package com.babytree.apps.biz.father.ui;

import com.babytree.apps.biz.father.util.Utils;
import com.babytree.apps.pregnancy.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 爸爸版的TitleBar
 * 
 * @author gaierlin
 */
public class FatherTitleBar extends LinearLayout {
	private Context mContext;
	private View mTitleBarBg;
	private View mTitleBarTopLine;
	private View mTitleBarShadow;

	private FatherTitleButton mLeftButton;
	private FatherTitleButton mRightButton;

	private TextView mTitleName;

	private TitleBarOnClick mOnClick;

	public FatherTitleBar(Context context) {
		super(context);
		mContext = context;
	}

	public FatherTitleBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init(mContext);
	}

	private void init(Context context) {

		mTitleBarBg = findViewById(R.id.title_bar_bg);
		mTitleBarTopLine = findViewById(R.id.title_bar_top_line);

		mLeftButton = (FatherTitleButton) findViewById(R.id.title_button_left);
		mLeftButton.setLeftDividerLineVisibility(View.GONE);
		mLeftButton.setButtonImage(R.drawable.side_bar);
		mLeftButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnClick != null)
					mOnClick.leftButtonOnClick();
			}
		});

		mRightButton = (FatherTitleButton) findViewById(R.id.title_button_right);
		mRightButton.setRightDividerLineVisibility(View.GONE);
		mRightButton.setButtonImage(R.drawable.dialogue);
		mRightButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mOnClick != null)
					mOnClick.rightButtonOnClick();
			}
		});

		mTitleName = (TextView) findViewById(R.id.title_name);

		mTitleBarShadow = findViewById(R.id.title_bar_shadow);
	}

	public void setLeftButtonVisibility(int visibility) {
		if (Utils.checkVisibility(visibility)) {
			mLeftButton.setVisibility(visibility);
		}
	}

	public void setRightButtonVisibility(int visibility) {
		if (Utils.checkVisibility(visibility)) {
			mRightButton.setVisibility(visibility);
		}
	}

	public void setShadowVisibility(int visibility) {
		if (Utils.checkVisibility(visibility)) {
			mTitleBarShadow.setVisibility(visibility);
		}
	}

	/**
	 * 显示右上角数字提示 - 使用自定义图片
	 * 
	 * @param num
	 * @param resId
	 *            图片资源Id
	 */
	public void setLeftButtonTagNum(int num, int resId) {
		if (Utils.checkResId(resId)) {
			mLeftButton.setButtonNumUpIcon(num, mContext, resId);
		} else {
		}
	}

	public void setLeftButtonTagNum(int num) {
		mLeftButton.setButtonNumUpIcon(num, mContext);
	}

	/**
	 * 设置右上角数字提示 - 使用自定义图片
	 * 
	 * @param num
	 * @param resId
	 *            图片资源Id
	 */
	public void setRightButtonTagNum(int num, int resId) {
		if (Utils.checkResId(resId)) {
			mRightButton.setButtonNumUpIcon(num, mContext, resId);
		} else {
		}
	}

	public void setRightButtonTagNum(int num) {
		mRightButton.setButtonNumUpIcon(num, mContext);
	}

	/**
	 * 显示右上角数字提示 - 使用默认图片
	 * 
	 * @param isShow
	 * @param resId
	 *            图片资源Id
	 */
	public void setLeftButtonTagNum(boolean isShow, int resId) {
		if (Utils.checkResId(resId)) {
			mLeftButton.setButtonNumUpicon(isShow, resId);
		} else {
		}
	}

	public void setLeftButtonTagNum(boolean isShow) {
		mLeftButton.setButtonNumUpicon(isShow);
	}

	/**
	 * 显示右上角数字提示 - 使用自定义图片
	 * 
	 * @param isShow
	 * @param resId
	 *            图片资源Id
	 */
	public void setRightButtonTagNum(boolean isShow, int resId) {
		if (Utils.checkResId(resId)) {
			mRightButton.setButtonNumUpicon(isShow, resId);
		} else {
		}
	}

	public void setRightButtonTagNum(boolean isShow) {
		mRightButton.setButtonNumUpicon(isShow);
	}

	public void setTitleBarName(String name) {
		mTitleName.setText(name);
	}

	public void setTitleTextSize(int size) {
		mTitleName.setTextSize(size);
	}

	public void setTitleTextGravity(int gravity) {
		mTitleName.setGravity(gravity);
	}

	public void setTitleTextColor(int color) {
		mTitleName.setTextColor(color);
	}

	public void setTitleBar(String barName, int titleSize, int titleGravity, int color) {
		mTitleName.setText(barName);
		mTitleName.setTextSize(titleSize);
		mTitleName.setGravity(titleGravity);
		mTitleName.setTextColor(color);
	}

	public void setTitleBarOnClick(TitleBarOnClick onClick) {
		this.mOnClick = onClick;
	}

	/**
	 * 将bar条填充
	 */
	public void setTitleBarFill() {
		setPadding(0, 0, 0, 0);
	}

	/**
	 * 设置bar条背景
	 * 
	 * @param resId
	 */
	public void setTitleBarBackground(int resId) {
		if (Utils.checkResId(resId)) {
			mTitleBarBg.setBackgroundResource(resId);
		}
	}

	public void setTitleBarTopLine(int resId) {
		if (Utils.checkResId(resId)) {
			mTitleBarTopLine.setBackgroundResource(resId);
		} else {
		}
	}

	public void setTitleBarTopLineVisibile(int visible) {
		mTitleBarTopLine.setVisibility(visible);
	}

	public void setRightEnabled(boolean enabled) {
		mRightButton.setEnabled(enabled);
	}

	public void setLeftEnabled(boolean enabled) {
		mLeftButton.setEnabled(enabled);
	}

	public void setLeftButtonImage(int resId) {
		mLeftButton.setButtonImage(resId);
	}

	public void setRightButtonImage(int resId) {
		mRightButton.setButtonImage(resId);
	}

	/**
	 * 获取Bar 条左侧按钮
	 * 
	 * @return
	 */
	public FatherTitleButton getLeftButton() {
		return mLeftButton;

	}

	/**
	 * 获取Bar 条右侧按钮
	 * 
	 * @return
	 */
	public FatherTitleButton getRightButton() {
		return mRightButton;

	}

	public interface TitleBarOnClick {
		void leftButtonOnClick();

		void rightButtonOnClick();
	}
}
