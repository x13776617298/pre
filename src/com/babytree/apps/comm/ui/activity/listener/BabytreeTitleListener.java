package com.babytree.apps.comm.ui.activity.listener;

import android.widget.Button;
import android.widget.LinearLayout;

/**
 * 自定义带title的activity 接口
 * 
 * @author wangbingqi
 * 
 */
public interface BabytreeTitleListener {
	/**
	 * 左上角button
	 * 
	 * @param button
	 *            左上角button对象
	 * @author wangbingqi
	 */
	void setLeftButton(Button button);

	/**
	 * 右上角button
	 * 
	 * @param button
	 *            右上角button对象
	 * @author wangbingqi
	 */
	void setRightButton(Button button);

	/**
	 * 设置title中间的布局
	 * 
	 * @param linearlayout
	 * @author wangbingqi
	 */
	void setTitleView(LinearLayout linearlayout);

	/**
	 * 获得title文字 不设置默认隐藏
	 * 
	 * @return
	 * @author wangbingqi
	 */
	String getTitleString();

	/**
	 * 获得身体部分
	 * 
	 * @return 自定义的view ID
	 * @author wangbingqi
	 */
	int getBodyView();
}