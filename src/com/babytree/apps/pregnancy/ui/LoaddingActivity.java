package com.babytree.apps.pregnancy.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.babytree.apps.biz.father.RoleSelectActivity;
import com.babytree.apps.biz.home.HomePageActivity;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.ui.CalculatorActivity;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.ui.widget.MyScrollLayout;
import com.babytree.apps.comm.ui.widget.OnViewChangeListener;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 新功能介绍
 * 
 */
public class LoaddingActivity extends BabytreeActivity implements OnViewChangeListener {

	private MyScrollLayout mScrollLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.welcome);
		initView();
	}

	private void initView() {
		mScrollLayout = (MyScrollLayout) findViewById(R.id.ScrollLayout);
		findViewById(R.id.layout_go).setOnClickListener(onClick);
		mScrollLayout.SetOnViewChangeListener(this);
	}

	private View.OnClickListener onClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.layout_go:
				String role = SharedPreferencesUtil.getStringValue(getApplicationContext(), ShareKeys.APP_TYPE_KEY,
						CommConstants.APP_TYPE_UNKNOW);
				if (role.equalsIgnoreCase(CommConstants.APP_TYPE_MOMMY)) {// 妈妈版
					BabytreeLog.d("Default entry In Mommy Version...");
					entranceMommyApp();
				} else if (role.equalsIgnoreCase(CommConstants.APP_TYPE_DADDY)) {
					BabytreeLog.d("Default entry In Daddy Version...");
				} else {// 未知：则进入爸爸妈妈版选择页面
					BabytreeLog.d("Default entry In UnKnow Version...");
					String preTime = SharedPreferencesUtil.getStringValue(getApplicationContext(),
							ShareKeys.BIRTHDAY_TIMESTAMP);
					if (preTime != null && !preTime.equalsIgnoreCase("")) { // 本地有预产期，则为妈妈版老用户
						// 为老用户添加妈妈班标记
						// 存储妈妈版的标记
						SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.APP_TYPE_KEY,
								CommConstants.APP_TYPE_MOMMY);
						entranceMommyApp();
					} else {
						startActivity(new Intent(getApplicationContext(), RoleSelectActivity.class));
					}
				}
				break;
			default:
				break;
			}
			SharedPreferencesUtil.setValue(LoaddingActivity.this,
					"isAlreadyInit" + BabytreeUtil.getAppVersionCode(LoaddingActivity.this), true);
			finish();
		}

	};

	/**
	 * 进入妈妈版
	 */
	public void entranceMommyApp() {
		String mBirthday = SharedPreferencesUtil.getStringValue(getApplicationContext(), ShareKeys.BIRTHDAY_TIMESTAMP);
		if (mBirthday == null) {
			Intent intent = new Intent(LoaddingActivity.this, CalculatorActivity.class).putExtra("first", true);
			startActivity(intent);
		} else {
			startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
		}
	}

	@Override
	public void OnViewChange(int position) {
	}
}
