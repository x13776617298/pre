package com.babytree.apps.comm.ui.activity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.babytree.apps.comm.ui.activity.listener.BabytreeTitleListener;
import com.babytree.apps.comm.view.BabyTreeTitleView;
import com.babytree.apps.pregnancy.R;

/**
 * 带标题栏的activity
 * 
 * @author wangbingqi
 * 
 */
public abstract class BabytreeTitleAcitivty extends BabytreeActivity implements BabytreeTitleListener {
	/**
	 * 自定义标题栏View
	 */
	private BabyTreeTitleView view;
	/**
	 * body
	 */
	private View viewBody;
	/**
	 * 身体的布局
	 */
	private RelativeLayout relativelayout;

	private BabytreeTitleListener listener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		listener = this;
		view = new BabyTreeTitleView(this);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		setContentView(view);

		if (mIsPregnancy) {
			// 育儿版(更换为育儿的title样式)
			view.getFramelayout().setBackgroundDrawable(getResources().getDrawable(R.drawable.y_title_bg));
			view.getButton_left().setBackgroundDrawable(getResources().getDrawable(R.drawable.y_btn_back));
			view.getButton_right().setBackgroundDrawable(getResources().getDrawable(R.drawable.y_btn_main_change));
		} else if (mIsFather) {
			// 爸爸版(更换为爸爸版的title样式)
			view.getFramelayout().setBackgroundDrawable(getResources().getDrawable(R.drawable.title_back));
			view.getButton_left().setBackgroundDrawable(getResources().getDrawable(R.drawable.f_btn_back));
			view.getButton_right().setBackgroundDrawable(getResources().getDrawable(R.drawable.f_btn_main_change));
		}

		if (listener == null) {
			return;
		}

		Button buttonLeft = view.getButton_left();

		buttonLeft.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		Button buttonRight = view.getButton_right();
		if (!TextUtils.isEmpty(buttonRight.getText())) {
			buttonRight.setVisibility(View.VISIBLE);
		}
		TextView textView = view.getTextView();
		String titleString = listener.getTitleString();
		if (titleString == null) {
			titleString = "";
		}
		textView.setText(titleString);

		LinearLayout linearlayout = view.getLinearlayout();
		listener.setTitleView(linearlayout);
		listener.setLeftButton(buttonLeft);
		listener.setRightButton(buttonRight);

		relativelayout = view.getRelativeLayout();
		if (listener.getBodyView() == 0) {
			return;
		}
		viewBody = LayoutInflater.from(this).inflate(listener.getBodyView(), null);
		viewBody.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		if (viewBody != null) {
			relativelayout.addView(viewBody);
		}
	}

	/**
	 * 获取右边Button
	 * 
	 * @author wangshuaibo
	 * @return
	 */
	public Button getRightButton() {
		return view.getButton_right();
	}

	/**
	 * 获取左边Button
	 * 
	 * @author wangshuaibo
	 * @return
	 */
	public Button getLeftButton() {
		return view.getButton_left();
	}

	/**
	 * 替换viewBody
	 * 
	 * @param view
	 */
	public void cleanViewBody(View view) {
		if (viewBody != null) {
			relativelayout.removeAllViews();
			viewBody = null;
		}
		viewBody = view;
		relativelayout.addView(viewBody);
	}

	/**
	 * 替换bady布局
	 * 
	 * @param view
	 * @author wangbingqi
	 */
	public void cleanBadyView(View view) {
		relativelayout.removeAllViews();
		relativelayout.addView(view);
	}

	/**
	 * 替换bady布局
	 * 
	 * @param id
	 * @author wangbingqi
	 */
	public void cleanBadyView(int id) {
		relativelayout.removeAllViews();
		View view = LayoutInflater.from(this).inflate(id, null);
		relativelayout.addView(view);
	}

	/**
	 * 动态设置标题名字
	 * 
	 * @param str
	 * @author luozheng
	 */
	public void setTitleString(String str) {
		view.getTextView().setText(str);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		delDialog();
		closePopWindows();
	}

	@Override
	public void setTitleView(LinearLayout linearlayout) {

	}

	/**
	 * 加载中对话框
	 */
	private ProgressDialog mLoadingDialog;

	/**
	 * 普通对话框
	 */
	private AlertDialog.Builder builder;

	/**
	 * 显示普通对话框
	 * 
	 * @param title
	 *            标题
	 * @param message
	 *            内容
	 * @param view
	 *            自定义展示view 如果view!=null 则不显示message
	 * @param textLeft
	 *            左边按钮的文字
	 * @param leftListener
	 *            左边按钮监听
	 * @param textRight
	 *            右边按钮的文字
	 * @param rightListener
	 *            右边按钮的监听
	 */
	protected void showAlertDialog(String title, String message, View view, String textLeft,
			DialogInterface.OnClickListener leftListener, String textRight,
			DialogInterface.OnClickListener rightListener) {
		builder = null;
		builder = new AlertDialog.Builder(this);

		builder.setTitle(title);
		if (view != null) {
			builder.setView(view);
		} else {
			if (message != null && !message.equalsIgnoreCase(""))
				builder.setMessage(message);
		}
		builder.setPositiveButton(textLeft, leftListener);
		builder.setNegativeButton(textRight, rightListener);
		builder.setCancelable(true);
		builder.show();
	}

	protected void showAlertItemDialog(String title, String[] items, DialogInterface.OnClickListener listner) {
		builder = null;
		builder = new AlertDialog.Builder(this);
		builder.setTitle(title);
		builder.setItems(items, listner);
		builder.setCancelable(true);
		builder.show();
	}

	/**
	 * 显示加载中的对话框
	 * 
	 * @param message
	 *            提示信息
	 */
	protected void showLoadingDialog(String message) {
		if (mLoadingDialog == null) {
			mLoadingDialog = new ProgressDialog(this);
		}
		mLoadingDialog.setMessage(message);
		mLoadingDialog.setCancelable(true);
		if (!mLoadingDialog.isShowing()) {
			mLoadingDialog.show();
		}
	}

	/**
	 * 显示加载中对话框
	 */
	protected void showLoadingDialog() {
		showLoadingDialog("加载中...");
	}

	/**
	 * 关闭加载中的对话框
	 */
	@Deprecated
	protected void closeDialog() {
		dismissLoadingDialog();
	}

	/**
	 * 关闭加载中的对话框
	 */
	protected void dismissLoadingDialog() {
		if (mLoadingDialog != null && !isFinishing()) {
			mLoadingDialog.dismiss();
		}
	}

	/**
	 * 删除全部对话框
	 */
	private void delDialog() {
		dismissLoadingDialog();
		if (mLoadingDialog != null) {
			mLoadingDialog = null;
		}
		if (builder != null) {
			builder = null;
		}
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		if (hasFocus) {
			showPopWindow(setPopWindowImage());
		}
	}

	/**
	 * 设置popwindow图片
	 * 
	 * @return
	 * @author luozheng
	 */
	protected int setPopWindowImage() {
		return 0;
	}

	private PopupWindow pop = null;

	protected void showPopWindow(int drawableId) {
		if (drawableId == 0) {
			return;
		}
		if (pop == null) {
			ImageView imageView = null;
			imageView = new ImageView(this);
			imageView.setBackgroundResource(drawableId);
			pop = new PopupWindow(imageView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					closePopWindows();
				}
			});
		}
		if (!pop.isShowing()) {

			pop.showAtLocation(view, Gravity.BOTTOM, 0, 0);

		}
	}

	private void closePopWindows() {
		if (pop != null) {
			pop.dismiss();
			pop = null;
		}
	}
}
