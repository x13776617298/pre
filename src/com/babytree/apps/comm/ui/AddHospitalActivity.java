package com.babytree.apps.comm.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.biz.home.HomePageActivity;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.HospitalController;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.model.AddHospitalInfo;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

public class AddHospitalActivity extends BabytreeTitleAcitivty implements OnClickListener {

	private EditText mEdit;
	private ImageView mClean;
	private TextView mChoiceCity;
	private ProgressDialog mDialog;
	/**
	 * 医院名字
	 */
	private String hospitalName = "";
	private String cityName = "";
	private String cityCode = "";
	private String provinceName = "";

	public static void launch(Context context) {
		Intent intent = new Intent(context, AddHospitalActivity.class);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mEdit = (EditText) findViewById(R.id.et_input_hospital);
		mClean = (ImageView) findViewById(R.id.iv_clean);
		mClean.setOnClickListener(this);
		mChoiceCity = (TextView) findViewById(R.id.tv_input_location);
		mChoiceCity.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		if (v == mClean) {
			mEdit.setText("");
		} else if (v == mChoiceCity) {
			Intent intent = new Intent(AddHospitalActivity.this, LocationList3Activity.class);
			intent.putExtra("isFromAddHospital", true);
			startActivity(intent);
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			DataResult ret = (DataResult) msg.obj;
			cancelDialog();
			switch (ret.status) {
			case P_BabytreeController.SUCCESS_CODE:
				AddHospitalInfo info = (AddHospitalInfo) ret.data;
				if (info != null) {
					if (info.hospital_name != null)
						SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.HOSPITAL_NAME,
								info.hospital_name);
					if (info.hospital_id != null) {
						SharedPreferencesUtil
								.setValue(getApplicationContext(), ShareKeys.HOSPITAL_ID, info.hospital_id);
						SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.ISCHOICEHOSPITAL, true);
					}
					SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.GROUP_ID, info.group_id);
					Intent intent = new Intent(AddHospitalActivity.this, HomePageActivity.class);
					startActivity(intent);
					finish();
				} else {
					SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.HOSPITAL_NAME, hospitalName);
					SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.HOSPITAL_ID, null);
					SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.ISCHOICEHOSPITAL, false);
					SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.GROUP_ID, null);
					showAlertDialog();
				}
				SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.ADD_HOSPITAL_PROVINCE, null);
				SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.ADD_HOSPITAL_CITY_CODE, null);
				SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.ADD_HOSPITAL_CITY, null);
				break;
			default:
				ExceptionUtil.catchException(ret.error, AddHospitalActivity.this);
				Toast.makeText(AddHospitalActivity.this, "服务器接口异常", Toast.LENGTH_SHORT).show();
				break;
			}
		};
	};

	private void submitHospital(final String hospitalName) {
		showDialog(null, "提交中，请稍后...", null, null, true, null, null);
		new Thread() {
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(AddHospitalActivity.this)) {
						String loginString = SharedPreferencesUtil.getStringValue(getApplicationContext(),
								ShareKeys.LOGIN_STRING);
						ret = HospitalController.setHospital(loginString, null, hospitalName, cityCode);
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
				handler.sendMessage(message);
			};
		}.start();
	}

	@Override
	protected void onResume() {
		super.onResume();
		provinceName = SharedPreferencesUtil.getStringValue(getApplicationContext(), ShareKeys.ADD_HOSPITAL_PROVINCE);
		cityCode = SharedPreferencesUtil.getStringValue(getApplicationContext(), ShareKeys.ADD_HOSPITAL_CITY_CODE);
		cityName = SharedPreferencesUtil.getStringValue(getApplicationContext(), ShareKeys.ADD_HOSPITAL_CITY);
		if (provinceName != null && cityName != null)
			mChoiceCity.setText(provinceName + " " + cityName);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelDialog();
	}

	private void showAlertDialog() {
		new AlertDialog.Builder(this).setTitle(getResources().getString(R.string.save_success))
				.setMessage(getResources().getString(R.string.set_hospital_failed))
				.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						startActivity(new Intent(AddHospitalActivity.this, HomePageActivity.class));
						finish();
						arg0.dismiss();
					}
				}).show();
	}

	private void showDialog(String title, String content, String okText, String cancleText, boolean cancelable,
			OnCancelListener btnCancle, OnClickListener btnOk) {
		mDialog = new ProgressDialog(this);
		mDialog.setTitle(title);
		mDialog.setMessage(content);
		mDialog.setCancelable(cancelable);
		mDialog.show();
	}

	private void cancelDialog() {
		if (mDialog != null && !this.isFinishing()) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	@Override
	public void setLeftButton(Button button) {
	}

	@Override
	public void setRightButton(Button button) {
		button.setText("保存");
		button.setVisibility(View.VISIBLE);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendHospital();
			}
		});
	}

	/**
	 * 提交医院
	 * 
	 * @author luozheng
	 */
	private void sendHospital() {
		hospitalName = mEdit.getText().toString();
		if ("".equalsIgnoreCase(hospitalName) || cityCode == null) {
			Toast.makeText(AddHospitalActivity.this, R.string.question_hospital_can_not_null, Toast.LENGTH_SHORT)
					.show();
			return;
		} else {
			submitHospital(hospitalName);
		}
	}

	@Override
	public String getTitleString() {
		return "设置您的医院";
	}

	@Override
	public int getBodyView() {
		return R.layout.add_hospital_activity;
	}

	@Override
	public void setTitleView(LinearLayout linearlayout) {

	}

}
