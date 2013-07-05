package com.babytree.apps.comm.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.pregnancy.R;

public class ApplyFreeMikaActivity extends BabytreeTitleAcitivty implements OnClickListener {

	private EditText mEditParentsName, mEditPhoneNum, mEditEmailAddress, mEditPostCode;
	private Button mBtnProvinces, mBtnApplyMika;
	private ProgressDialog mDialog;
	private String mProvinceStr;
	private String mCityStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mEditParentsName = (EditText) findViewById(R.id.et_parents_name);
		mEditPhoneNum = (EditText) findViewById(R.id.et_phones_number);
		mEditEmailAddress = (EditText) findViewById(R.id.et_email_address);
		mEditPostCode = (EditText) findViewById(R.id.et_postcode);

		mBtnProvinces = (Button) findViewById(R.id.btn_provinces);
		mBtnApplyMika = (Button) findViewById(R.id.btn_mika_apply);
		mBtnProvinces.setOnClickListener(this);
		mBtnApplyMika.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_provinces:
			startActivityForResult(new Intent(this, LocationListActivity.class), 10);
			break;
		case R.id.btn_mika_apply:
			String name = mEditParentsName.getText().toString();
			String mobile = mEditPhoneNum.getText().toString();
			String address = mEditEmailAddress.getText().toString();
			String postcode = mEditPostCode.getText().toString();
			String provinces = mBtnProvinces.getText().toString();

			if (judgeNotEmpty(name) && judgeNotEmpty(mobile) && judgeNotEmpty(address) && judgeNotEmpty(postcode)
					&& judgeNotEmpty(provinces)) {
				ApplyMiKa(name, mobile, mProvinceStr, mCityStr, address, postcode);
			} else {
				showAlertDialog("请将信息填写完整。");
			}
			break;
		}
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mDialog != null && ApplyFreeMikaActivity.this.isFinishing()) {
				mDialog.dismiss();
			}
			DataResult ret = (DataResult) msg.obj;
			if (ret.status == P_BabytreeController.SUCCESS_CODE) {
				Toast.makeText(ApplyFreeMikaActivity.this, "提交信息成功!", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(ApplyFreeMikaActivity.this, ret.message, Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void ApplyMiKa(final String name, final String mobile, final String province, final String city,
			final String address, final String zipcode) {
		showDialog(null, "提交中...", null, null, true, null, null);
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();

				try {
					if (BabytreeUtil.hasNetwork(ApplyFreeMikaActivity.this)) {
						// ret = BabytreeController.getAdsList(birthday);
						ret = P_BabytreeController.applyMika(name, mobile, province, city, address, zipcode);
					} else {
						ret = new DataResult();
						ret.message = P_BabytreeController.NetworkExceptionMessage;
						ret.status = P_BabytreeController.NetworkExceptionCode;
					}
				} catch (Exception e) {
					ret = new DataResult();
					ret.message = P_BabytreeController.SystemExceptionMessage;
					ret.status = P_BabytreeController.SystemExceptionCode;
				}
				message.obj = ret;
				mHandler.sendMessage(message);
			}
		}.start();

	}

	public void showDialog(String title, String content, String okText, String cancleText, boolean cancelable,
			OnCancelListener btnCancle, OnClickListener btnOk) {
		mDialog = new ProgressDialog(this);
		mDialog.setTitle(title);
		mDialog.setMessage(content);
		mDialog.setCancelable(cancelable);
		mDialog.show();
	}

	private void showAlertDialog(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}

	private boolean judgeNotEmpty(String tmp) {
		if (tmp == null || tmp.equals("")) {
			return false;
		} else {
			return true;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		String mpositionStr = data.getStringExtra("name");
		mProvinceStr = data.getStringExtra("province");
		mCityStr = data.getStringExtra("city");
		mBtnProvinces.setText(mpositionStr);
	}

	@Override
	public void setLeftButton(Button button) {

	}

	@Override
	public void setRightButton(Button button) {

	}

	@Override
	public String getTitleString() {
		return "免费体验光盘";
	}

	@Override
	public int getBodyView() {
		return R.layout.apply_freemika_activity;
	}

}
