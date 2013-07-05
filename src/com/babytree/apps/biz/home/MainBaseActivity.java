package com.babytree.apps.biz.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import com.babytree.apps.biz.notify.AnimationShowWindow;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.ui.activity.BabytreePhotographActivity;

/**
 * 主页基类<br>
 * 包括:<br>
 * 加孕气广播
 * 
 * @author wangshuaibo
 * 
 */
public abstract class MainBaseActivity extends BabytreePhotographActivity {
	/**
	 * 加孕气广播ACTION
	 */
	public static final String ACTION_NOTIFY = "com.babytree.apps.notify";

	/**
	 * 加孕气广播
	 */
	protected NotifyBroadcast notifyBroadcast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		notifyBroadcast = new NotifyBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_NOTIFY);

		LocalBroadcastManager.getInstance(this).registerReceiver(notifyBroadcast, filter);

	}

	@Override
	protected void onStart() {
		super.onStart();

		int yunqi = getIntent().getIntExtra("yunqi", 0);
		showNotify(yunqi);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		LocalBroadcastManager.getInstance(this).unregisterReceiver(notifyBroadcast);

		mApplication.closeDB();

	}

	/**
	 * 加孕气广播
	 * 
	 * @author wangshuaibo
	 * 
	 */
	private class NotifyBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			int yunqi = intent.getIntExtra("yunqi", 0);
			showNotify(yunqi);
		}

	}

	/**
	 * 显示加孕气动画
	 * 
	 * @author wangshuaibo
	 * @param yunqi
	 */
	private void showNotify(int yunqi) {
		if (yunqi != 0) {
			BabytreeLog.d("Yunqi is " + yunqi);
			AnimationShowWindow animationShowWindow = new AnimationShowWindow(MainBaseActivity.this, yunqi);
			animationShowWindow.show();
		}
	}

}
