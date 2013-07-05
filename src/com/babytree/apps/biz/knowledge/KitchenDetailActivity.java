package com.babytree.apps.biz.knowledge;

import android.os.Bundle;
import android.view.View.OnClickListener;

/**
 * 每日厨房详情页
 */
public class KitchenDetailActivity extends InformationDetailActivity implements OnClickListener {

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
	}

	@Override
	public String getTitleString() {
		return "营养食谱";
	}

	@Override
	protected void changeTitle(String url) {
		// Nothing todo.
	}
}
