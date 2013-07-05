package com.babytree.apps.biz.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.babytree.apps.biz.father.RoleSelectActivity;
import com.babytree.apps.biz.father.WelcomeFatherActivity;
import com.babytree.apps.biz.home.HomePageActivity;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.ui.CalculatorActivity;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.LoaddingActivity;

public class WelcomeActivity extends BabytreeActivity {
	private String mBirthday;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 判断是否清空用户登录信息
		boolean isNeedLogin = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.IS_NEED_LOGING, true);
		if (isNeedLogin) {
			// Toast.makeText(this, "系统升级,需要您重新登录帐号!", Toast.LENGTH_SHORT).show();
			SharedPreferencesUtil.removeKeyArray(this, new String[] { ShareKeys.LOGIN_STRING, ShareKeys.USER_ENCODE_ID,
					ShareKeys.NICKNAME });
			SharedPreferencesUtil.setValue(this, ShareKeys.IS_NEED_LOGING, false);
		}
		if (!SharedPreferencesUtil.getBooleanValue(WelcomeActivity.this, ShareKeys.IS_PREGNANCY)) {// 进入孕期
			setContentView(R.layout.welcome_activity);

			new Thread() {
				public void run() {
					try {
						Thread.sleep(1000);
						mBirthday = SharedPreferencesUtil.getStringValue(getApplicationContext(),
								ShareKeys.BIRTHDAY_TIMESTAMP);
						handler.sendEmptyMessage(1);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}.start();
		} else {// 进入育儿
			startActivity(new Intent(getApplicationContext(), com.babytree.apps.biz.welcome.Y_WelcomeActivity.class));
			finish();
		}

	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// 判断是不是新版
			boolean isAlreadyInit = SharedPreferencesUtil.getBooleanValue(getApplicationContext(), "isAlreadyInit"
					+ BabytreeUtil.getAppVersionCode(WelcomeActivity.this));
			if (!isAlreadyInit) {// 是新版(没有进行初始化)
				startActivity(new Intent(getApplicationContext(), LoaddingActivity.class));
				finish();
			} else {// 爸爸新版
				// 存储爸爸版的标记
				String role = SharedPreferencesUtil.getStringValue(getApplicationContext(), ShareKeys.APP_TYPE_KEY,
						CommConstants.APP_TYPE_UNKNOW);
				if (role.equalsIgnoreCase(CommConstants.APP_TYPE_MOMMY)) {// 妈妈版
					BabytreeLog.d("Default entry In Mommy Version...");
					if (mBirthday == null) {
						Intent intent = new Intent(WelcomeActivity.this, CalculatorActivity.class).putExtra("first",
								true);
						startActivity(intent);
					} else {
						startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
					}
				} else if (role.equalsIgnoreCase(CommConstants.APP_TYPE_DADDY)) {
					BabytreeLog.d("Default entry In Daddy Version...");
					startActivity(new Intent(getApplicationContext(), WelcomeFatherActivity.class));
				} else {// 未知：则进入爸爸妈妈版选择页面
					startActivity(new Intent(getApplicationContext(), RoleSelectActivity.class));
				}
			}
			finish();
		}

	};
}
