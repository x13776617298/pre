package com.babytree.apps.comm.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.babytree.apps.comm.ui.activity.listener.BabyTreeForumTitleListener;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.view.BabyTreeForumTitleView;
import com.babytree.apps.pregnancy.R;

/**
 * 带舌头的Title Activity
 * 
 * @author luozheng
 * 
 */
public abstract class BabyTreeForumTitleActivity extends BabytreeTitleAcitivty implements BabyTreeForumTitleListener {
	/**
	 * 带舌头的按钮
	 */
	protected BabyTreeForumTitleView babytreeForumView;

	private BabyTreeForumTitleListener babytreeforumtitlelistener;

	private ImageView imgIn;

	private ImageView imgOut;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void setTitleView(LinearLayout linearlayout) {
		babytreeforumtitlelistener = this;
		babytreeForumView = new BabyTreeForumTitleView(BabyTreeForumTitleActivity.this);
		LayoutParams params = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		linearlayout.addView(babytreeForumView, params);

		TextView textView = babytreeForumView.getmTvCenter();

		ImageView imgDown = babytreeForumView.getmIvItemDown();
		imgIn = babytreeForumView.getmIvItenIn();
		imgOut = babytreeForumView.getmIvItemOut();
		if (BabytreeUtil.isPregnancy(this)) {
			imgOut.setBackgroundResource(R.drawable.y_ic_item_menu_bg_out);
		}
		String text = "";
		super.setTitleString(text);
		if (babytreeforumtitlelistener != null) {
			text = babytreeforumtitlelistener.getTitleString();
		}
		textView.setText(text);

		imgDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				onTitleClick();
			}
		});
		textView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onTitleClick();
			}
		});
	}

	private void onTitleClick() {

		if (babytreeforumtitlelistener != null) {
			babytreeforumtitlelistener.titleOnclick();
		}

	}

	/**
	 * 动态设置标题名字
	 * 
	 * @param str
	 * @author luozheng
	 */
	public void setTitleString(String str) {
		babytreeForumView.getmTvCenter().setText(str);
	}

	/**
	 * 获取舌头图片
	 * 
	 * @return
	 * @author luozheng
	 */
	public ImageView getImgIn() {
		return imgIn;
	}

	/**
	 * 获取舌头外框图片
	 * 
	 * @return
	 * @author luozheng
	 */
	public ImageView getImgOut() {
		return imgOut;
	}
}
