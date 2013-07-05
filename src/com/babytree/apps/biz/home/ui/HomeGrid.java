package com.babytree.apps.biz.home.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

public class HomeGrid extends GridView {
	public HomeGrid(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public HomeGrid(Context context) {
		super(context);
	}

	public HomeGrid(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);
	}

}
