package com.babytree.apps.comm.ui.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ui.activity.listener.BabytreeCloseListener;
import com.babytree.apps.comm.util.ButtomClickUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * 公共Activity<br>
 * 包括:<br>
 * 1.公用application<br>
 * 2.公用context<br>
 * 3.公用是否为育儿标识<br>
 * 4.结束activty广播<br>
 * 5.友盟onResume,onPause<br>
 * 6.onClick,重写时需要调用super.onClick<br>
 * 7.onItemClick,重写时需要调用super.onItemClick<br>
 * 8.传umeng_event,会设置友盟onEvent<br>
 * 9.获取loginString<br>
 * 10. 获取gender(0-妈妈 1-爸爸)<br>
 * 11.公用是否为爸爸版标识<br>
 * 
 * @author wangshuaibo
 * 
 */
public class BabytreeActivity extends Activity implements OnClickListener, OnItemClickListener, BabytreeCloseListener {

	public static final String ACTION_EXITAPP = "com.babytree.apps.exit";

	protected PregnancyApplication mApplication;

	protected Activity mContext;

	protected BabytreeCloseListener babytreecloselistener;
	/**
	 * true为育儿false为孕期
	 */
	protected boolean mIsPregnancy;

	/**
	 * true为爸爸版
	 */
	protected boolean mIsFather;

	/**
	 * 结束activty广播
	 */
	protected BroadcastReceiver broadcastReceiverForPregnancy = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			finish();
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 设置友盟事件
		setUmengEvent(getIntent().getStringExtra("umeng_event"));
		mApplication = (PregnancyApplication) getApplication();
		mContext = this;
		// 孕期/育儿
		mIsPregnancy = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.IS_PREGNANCY, false);

		// 爸爸版/妈妈版
		String role = SharedPreferencesUtil.getStringValue(getApplicationContext(), ShareKeys.APP_TYPE_KEY,
				CommConstants.APP_TYPE_UNKNOW);
		if (role == CommConstants.APP_TYPE_DADDY) {
			mIsFather = true;
		}

		if (babytreecloselistener != null) {
			babytreecloselistener.onCloseCreate();
		}
	}

	/**
	 * 设置友盟事件
	 * 
	 * @author wangshuaibo
	 * @param event
	 */
	private void setUmengEvent(String event) {
		if (event != null) {
			MobclickAgent.onEvent(this, event);
		}
	}

	/**
	 * 获取loginString
	 * 
	 * @return
	 */
	protected final String getLoginString() {
		String loginStr = SharedPreferencesUtil.getStringValue(this, ShareKeys.LOGIN_STRING);
		return loginStr;
	}

	/**
	 * 是否登录
	 * 
	 * @author wangshuaibo
	 * @return
	 */
	protected final boolean isLogin() {
		String loginStr = SharedPreferencesUtil.getStringValue(this, ShareKeys.LOGIN_STRING);
		if (TextUtils.isEmpty(loginStr)) {
			return false;
		}
		return true;
	}

	/**
	 * 获取gender(0-妈妈 1-爸爸)
	 * 
	 * @return
	 */
	protected final String getGender() {
		String role = SharedPreferencesUtil.getStringValue(getApplicationContext(), ShareKeys.APP_TYPE_KEY,
				CommConstants.APP_TYPE_UNKNOW);
		return role;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (babytreecloselistener != null) {
			babytreecloselistener.onCloseDestroy();
		}
	}

	@Override
	public void onClick(View v) {
		if (ButtomClickUtil.isFastDoubleClick()) {
			return;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (ButtomClickUtil.isFastDoubleClick()) {
			return;
		}
	}

	@Override
	public void onCloseCreate() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_EXITAPP);
		LocalBroadcastManager.getInstance(this).registerReceiver(this.broadcastReceiverForPregnancy, filter);
	}

	@Override
	public void onCloseDestroy() {
		LocalBroadcastManager.getInstance(this).unregisterReceiver(this.broadcastReceiverForPregnancy);

	}

	/**
	 * 关闭所有实现broadcastReceiverForPregnancy的界面
	 * 
	 * @author wangshuaibo
	 */
	protected void closeOtherActivity() {
		Intent intent = new Intent();
		intent.setAction(ACTION_EXITAPP);
		intent.setPackage(getPackageName());
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
}
