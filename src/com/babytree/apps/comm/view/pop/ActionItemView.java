package com.babytree.apps.comm.view.pop;

import android.content.Context;
import android.widget.Button;
import android.widget.LinearLayout;

import com.babytree.apps.pregnancy.R;

public class ActionItemView extends Button {

	/**
	 * item数据
	 */
	public ActionItem actionItem;

	public ActionItemView(Context context, ActionItem actionItem) {
		super(context);
		this.actionItem = actionItem;
		init(context, actionItem);
	}

	/**
	 * 初始化默认属性
	 * 
	 * @param context
	 * @param actionItem
	 */
	private void init(Context context, ActionItem actionItem) {
		setId(actionItem.mPosition);
		setFocusable(true);
		setTextSize(18f);
		setTextColor(context.getResources().getColor(android.R.color.white));
		setPadding(12, 8, 12, 8);
		setText(actionItem.mTitle);
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.layout_selector_background));

		actionItem.mDrawable.setBounds(0, 0, actionItem.mDrawable.getMinimumWidth(),
				actionItem.mDrawable.getMinimumHeight());
		setCompoundDrawables(actionItem.mDrawable, null, null, null);

		// item button布局参数
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		int marginH = 8;
		int marginV = 3;
		params.leftMargin = marginH;
		params.topMargin = marginV;
		params.rightMargin = marginH;
		params.bottomMargin = marginV;
		setLayoutParams(params);

	}

	/**
	 * 设置布局参数
	 * 
	 * @param params
	 */
	public void updateLayoutParams(LinearLayout.LayoutParams params) {
		setLayoutParams(params);
	}

	/**
	 * 刷新Item的左侧图片Drawable
	 * 
	 * @param actionItem
	 */
	public void refresh(ActionItem actionItem) {
		actionItem.mDrawable.setBounds(0, 0, actionItem.mDrawable.getMinimumWidth(),
				actionItem.mDrawable.getMinimumHeight());
		setCompoundDrawables(actionItem.mDrawable, null, null, null);
		setText(actionItem.mTitle);
	}

}
