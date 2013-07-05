package com.babytree.apps.biz.setting;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;

/**
 * 提醒设置页面
 */
public class NotifySettingActivity extends BabytreeTitleAcitivty implements OnClickListener {
	private CheckBox checkRingTone;
	private CheckBox checkVibrate;
	private RelativeLayout layoutNotify;
	private RelativeLayout layoutAlerm;
	private TextView txtNotifySumm;
	private TextView txtNotifyAlerm;
	private PregnancyApplication mApplication;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mApplication = (PregnancyApplication) getApplication();

		checkRingTone = (CheckBox) findViewById(R.id.check_two);
		checkVibrate = (CheckBox) findViewById(R.id.check_three);
		layoutNotify = (RelativeLayout) findViewById(R.id.layout_notify);
		layoutAlerm = (RelativeLayout) findViewById(R.id.layout_alerm);
		txtNotifySumm = (TextView) findViewById(R.id.txt_notify_summ);
		txtNotifyAlerm = (TextView) findViewById(R.id.txt_notify_alerm);

		checkRingTone.setOnClickListener(this);
		layoutNotify.setOnClickListener(this);
		layoutAlerm.setOnClickListener(this);
		checkVibrate.setOnClickListener(this);
		init();
	}

	public void init() {
		if (SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_SOUND, true)) {
			checkRingTone.setChecked(true);
		} else {
			checkRingTone.setChecked(false);
		}
		if (SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_VIARATE, true)) {
			checkVibrate.setChecked(true);
		} else {
			checkVibrate.setChecked(false);
		}
		int alermIndex = getValueIndex(SharedPreferencesUtil.getIntValue(this, ShareKeys.NOTIFY_ALERM, 10),
				R.array.notify_alerm_values);// 默认10分钟
		String alermItem = getResources().getStringArray(R.array.notify_alerm_items)[alermIndex];
		txtNotifySumm.setText("每" + alermItem + "提醒一次");

		int timeIndex = getValueIndex(SharedPreferencesUtil.getIntValue(this, ShareKeys.NOTIFY_TIME, 1),
				R.array.notify_time_values);// 默认1
		String timeItem = getResources().getStringArray(R.array.notify_time_items)[timeIndex];
		txtNotifyAlerm.setText(timeItem);
	}

	private void setTime(int checkId) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("操作");
		builder.setSingleChoiceItems(R.array.notify_time_items, checkId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int value = Integer.parseInt(getResources().getStringArray(R.array.notify_time_values)[which]);
				SharedPreferencesUtil.setValue(NotifySettingActivity.this, ShareKeys.NOTIFY_TIME, value);
				init();
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	private void setAlerm(int checkId) {
		Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("操作");
		builder.setSingleChoiceItems(R.array.notify_alerm_items, checkId, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				int value = Integer.parseInt(getResources().getStringArray(R.array.notify_alerm_values)[which]);
				SharedPreferencesUtil.setValue(NotifySettingActivity.this, ShareKeys.NOTIFY_ALERM, value);
				init();
				mApplication.getBabytreePushService().restartLocalService(value);
				mApplication.getBabytreePushService().restartMessageService(value);
				dialog.dismiss();
			}
		});
		builder.create().show();
	}

	@Override
	public void onClick(View v) {
		if (v == checkRingTone) {
			if (checkRingTone.isChecked()) {
				SharedPreferencesUtil.setValue(this, ShareKeys.NOTIFY_SOUND, true);
			} else {
				SharedPreferencesUtil.setValue(this, ShareKeys.NOTIFY_SOUND, false);
			}
		} else if (v == checkVibrate) {
			if (checkVibrate.isChecked()) {
				SharedPreferencesUtil.setValue(this, ShareKeys.NOTIFY_VIARATE, true);
			} else {
				SharedPreferencesUtil.setValue(this, ShareKeys.NOTIFY_VIARATE, false);
			}
		} else if (v == layoutNotify) {
			setAlerm(getValueIndex(SharedPreferencesUtil.getIntValue(this, ShareKeys.NOTIFY_ALERM),
					R.array.notify_alerm_values));
		} else if (v == layoutAlerm) {
			setTime(getValueIndex(SharedPreferencesUtil.getIntValue(this, ShareKeys.NOTIFY_TIME),
					R.array.notify_time_values));
		}
	}

	private int getValueIndex(int value, int resId) {
		int ret = 0;
		String[] array = getResources().getStringArray(resId);
		for (int i = 0; i < array.length; i++) {
			int item = Integer.parseInt(array[i]);
			if (item == value) {
				ret = i;
				break;
			}
		}
		return ret;
	}

	@Override
	public void setLeftButton(Button button) {

	}

	@Override
	public void setRightButton(Button button) {

	}

	@Override
	public String getTitleString() {
		return "提醒设置";
	}

	@Override
	public int getBodyView() {
		return R.layout.notify_setting_activity;
	}
}
