package com.babytree.apps.comm.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;

import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.util.ButtomClickUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;

/**
 * 共用Fragment<br>
 * 目前包含:<br>
 * 1.公用Activity<br>
 * 2.公用Applicaton<br>
 * 
 * @author wangshuaibo
 * 
 */
public class BabyTreeFragment extends Fragment implements OnClickListener {

	protected Activity mContext;
	protected PregnancyApplication mApplication;

	/**
	 * 普通对话框
	 */
	private AlertDialog.Builder builder;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = getActivity();
		mApplication = (PregnancyApplication) getActivity().getApplication();
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public void onClick(View v) {
		if (ButtomClickUtil.isFastDoubleClick()) {
			return;
		}

	}

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
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);

		builder.setTitle(title);
		if (view != null) {
			builder.setView(view);
		} else {
			if (!TextUtils.isEmpty(message))
				builder.setMessage(message);
		}
		builder.setPositiveButton(textLeft, leftListener);
		builder.setNegativeButton(textRight, rightListener);
		builder.setCancelable(true);
		builder.show();
	}

	protected void showAlertItemDialog(String title, String[] items, DialogInterface.OnClickListener listner) {
		builder = null;
		builder = new AlertDialog.Builder(mContext);
		builder.setTitle(title);
		builder.setItems(items, listner);
		builder.setCancelable(true);
		builder.show();
	}

	/**
	 * 获取用户登录Token
	 * 
	 * @author wangshuaibo
	 * @return
	 */
	protected String getLoginString() {
		return SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOGIN_STRING, "");
	}

	/**
	 * 获取gender(0-妈妈 1-爸爸)
	 * 
	 * @return
	 */
	protected final String getGender() {
		String role = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.APP_TYPE_KEY,
				CommConstants.APP_TYPE_UNKNOW);
		return role;
	}

	protected void closeDialog() {
	}
}
