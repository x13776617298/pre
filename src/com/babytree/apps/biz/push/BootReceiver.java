package com.babytree.apps.biz.push;

import com.babytree.apps.biz.push.service.ActivityService;
import com.babytree.apps.biz.push.service.MessageService;
import com.babytree.apps.comm.tools.BabytreeLog;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Handler;

/**
 * 网络变化监听 监听到网络变化,如果有活动的网络请求推送接口.
 * 
 * @author wangshuaibo
 * 
 */
public class BootReceiver extends BroadcastReceiver {

	private static final String ACIONT_NETWORK = "android.net.conn.CONNECTIVITY_CHANGE";
	private static final long delayMillis = 1 * 60 * 1000; // 1分钟后启动服务

	@Override
	public void onReceive(final Context context, Intent intent) {
		try {
			if (intent != null) {
				if (intent.getAction().equals(ACIONT_NETWORK)) {
					BabytreeLog.d("Network changed.");
					// true 代表网络断开 false 代表网络没有断开
					boolean isBreak = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
					if (!isBreak) {
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								// 活动服务
								Intent activityService = new Intent(context, ActivityService.class);
								context.startService(activityService);
								BabytreeLog.d("ActivityService start.");

							}
						}, delayMillis);
						new Handler().postDelayed(new Runnable() {

							@Override
							public void run() {
								// 个人消息服务
								Intent messageService = new Intent(context, MessageService.class);
								context.startService(messageService);
								BabytreeLog.d("MessageService start.");

							}
						}, delayMillis);

					}
				}
			}
		} catch (Exception ex) {
			BabytreeLog.e(ex.toString(), ex);
		}
	}
}
