package com.babytree.apps.biz.father;

import android.app.ProgressDialog;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.pregnancy.R;

/**
 * 邀请准爸爸介绍页面
 */
public class FatherIntrActivity extends BabytreeTitleAcitivty implements OnClickListener {

	private ProgressDialog mDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 启用广播关闭机制
		babytreecloselistener = this;
		super.onCreate(savedInstanceState);

		Button mBtnLogin = (Button) findViewById(R.id.btn_invite_next);
		mBtnLogin.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_left) {
			finish();

		} else if (v.getId() == R.id.btn_invite_next) {
			BabytreeUtil.launch(mContext, new Intent(mContext, SendInviteActivity.class), false, 0);
		}
	}

	public void showDialog(String title, String content, String okText, String cancleText, boolean cancelable,
			OnCancelListener btnCancle, OnClickListener btnOk) {
		mDialog = new ProgressDialog(this);
		mDialog.setTitle(title);
		mDialog.setMessage(content);
		mDialog.setCancelable(cancelable);
		mDialog.show();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void setLeftButton(Button button) {
		button.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_back));
	}

	@Override
	public void setRightButton(Button button) {

	}

	@Override
	public String getTitleString() {
		return "爸爸版介绍";
	}

	@Override
	public int getBodyView() {
		return R.layout.father_intr_activity;
	}
}