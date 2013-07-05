package com.babytree.apps.comm.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface.OnCancelListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.ctr.BabytreeController;
import com.babytree.apps.comm.ctr.HospitalController;
import com.babytree.apps.comm.model.Hospital;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.HospitalMapActivity;
import com.umeng.analytics.MobclickAgent;

public class HospitalIntroductionActivity extends BabytreeActivity implements OnClickListener {

	private TextView mTvHospitalName, mTvHospitalTelNum, mTvHospitalAddress, mTvHospitalIntroduction;
	private ImageView mImgLocation;
	private ProgressDialog mDialog;
	private String mHospitalId;
	private double mX = 0, mY = 0;
	private String mName = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hospital_introduction_activity);
		mImgLocation = (ImageView) findViewById(R.id.img_location);
		mImgLocation.setOnClickListener(this);
		mTvHospitalAddress = (TextView) findViewById(R.id.tv_address_hospital);
		mTvHospitalName = (TextView) findViewById(R.id.tv_name_hospital);
		mTvHospitalTelNum = (TextView) findViewById(R.id.tv_phone_num_hospital);
		mTvHospitalIntroduction = (TextView) findViewById(R.id.tv_introduction_hospital);
		mHospitalId = getIntent().getStringExtra("hospital_id");
		if (mHospitalId == null || mHospitalId.equals("")) {
			mHospitalId = SharedPreferencesUtil.getStringValue(this, "hospital_id");
		}
		if (mHospitalId != null && !mHospitalId.equals("")) {
			process();
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

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mDialog != null && !HospitalIntroductionActivity.this.isFinishing()) {
				mDialog.dismiss();
			}
			DataResult ret = (DataResult) msg.obj;
			switch (ret.status) {
			case BabytreeController.SUCCESS_CODE:
				Hospital hospital = (Hospital) ret.data;
				mName = hospital.name;
				mX = Double.parseDouble(hospital.x);
				mY = Double.parseDouble(hospital.y);
				mTvHospitalName.setText(mName);
				mTvHospitalTelNum.setText(hospital.tel);
				mTvHospitalAddress.setText(hospital.address);
				mTvHospitalIntroduction.setText(hospital.description);
				break;
			default:
				ExceptionUtil.catchException(ret.error, HospitalIntroductionActivity.this);
				break;
			}
		}
	};

	private void process() {
		showDialog(null, "加载中...", null, null, true, null, null);
		new Thread() {

			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(HospitalIntroductionActivity.this)) {
						ret = HospitalController.getInfo(mHospitalId);
					} else {
						ret = new DataResult();
						ret.message = BabytreeController.NetworkExceptionMessage;
						ret.status = BabytreeController.NetworkExceptionCode;
					}
				} catch (Exception e) {
					ret = new DataResult();
					ret.message = BabytreeController.SystemExceptionMessage;
					ret.status = BabytreeController.SystemExceptionCode;
					ret.error = ExceptionUtil.printException(e).toString();
				}
				message.obj = ret;
				handler.sendMessage(message);
			}

		}.start();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.img_location:
			MobclickAgent.onEvent(this, EventContants.clickmap);
			if (mName != null && !mName.equals("") && mX != 0 && mY != 0) {
				HospitalMapActivity.launch(this, mX, mY, mName);
			}
			break;
		}
	}

}
