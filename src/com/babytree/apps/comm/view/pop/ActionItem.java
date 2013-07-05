package com.babytree.apps.comm.view.pop;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

/**
 * popMenu Item对象
 * 
 * @author pengxh
 * 
 */
public class ActionItem {
	
	/**
	 * ActionItem 在父控件中index -- 从0开始计数
	 */
	public int mIndex;

	/**
	 * ActionItem 标识position -- 从0开始计数
	 */
	public int mPosition;
	/**
	 * ActionItem 图标Icon
	 */
	public Drawable mDrawable;

	/**
	 * ActionItem 文本Title
	 */
	public CharSequence mTitle;

	public ActionItem(int position, Drawable d, CharSequence title) {
		mPosition = position;
		mDrawable = d;
		mTitle = title;
	}

	public ActionItem(int position, Context ctx, int drawableId, CharSequence title) {
		mPosition = position;
		mDrawable = ctx.getResources().getDrawable(drawableId);
		mTitle = title;
	}

	public ActionItem(int position, Context ctx, Drawable d, int titleId) {
		mPosition = position;
		mDrawable = d;
		mTitle = ctx.getResources().getString(titleId);
	}

	public ActionItem(int position, Context ctx, int drawableId, int titleId) {
		mPosition = position;
		Resources r = ctx.getResources();
		mDrawable = r.getDrawable(drawableId);
		mTitle = r.getString(titleId);
	}
	
	public void setIndex(int index) {
		this.mIndex = index;
	}

}
