package com.babytree.apps.biz.push.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.babytree.apps.biz.home.HomePageActivity;
import com.babytree.apps.biz.home.MainFragement;
import com.babytree.apps.biz.notice.NoticeActivity;
import com.babytree.apps.biz.notify.AnimationShowWindow;
import com.babytree.apps.biz.push.ctr.PushController;
import com.babytree.apps.biz.push.model.PushMessage;
import com.babytree.apps.biz.topic.TopicNewActivity;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.Md5Util;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

/**
 * 个人通知服务
 * 
 */
public class MessageService extends Service {
	// $md5=md5($app_id.$uid.'babytree2305')
	// 获取个人回帖消息:$md5_2
	// 获取个人短消息:$md5_4
	// 获取孕气推送值:$md5_5

	private NotificationManager mNotiManager;
	private String mUserEncodeId;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		mNotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	private Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				DataResult ret = (DataResult) msg.obj;
				if (ret.status == P_BabytreeController.SUCCESS_CODE) {
					int serial_number = SharedPreferencesUtil.getIntValue(MessageService.this, "message_2");

					ArrayList<PushMessage> data = (ArrayList<PushMessage>) ret.data;
					for (PushMessage pushMessage : data) {
						if (serial_number == -1 || serial_number < pushMessage.serial_number) {
							showNotification2(String.valueOf(pushMessage.tr), String.valueOf(pushMessage.id),
									String.valueOf(pushMessage.ar), getResources().getString(R.string.app_name),
									pushMessage.alert, pushMessage.id);
						}
					}
					SharedPreferencesUtil.setValue(getApplicationContext(), "message_2", ret.totalSize);
				}
			} else if (msg.what == 2) {
				DataResult ret = (DataResult) msg.obj;
				if (ret.status == P_BabytreeController.SUCCESS_CODE) {
					int serial_number = SharedPreferencesUtil.getIntValue(MessageService.this, "message_4");

					ArrayList<PushMessage> data = (ArrayList<PushMessage>) ret.data;
					for (PushMessage pushMessage : data) {
						if (serial_number == -1 || serial_number < pushMessage.serial_number) {

							showNotification4(getResources().getString(R.string.app_name), pushMessage.alert,
									pushMessage.id);
						}
					}
					SharedPreferencesUtil.setValue(getApplicationContext(), "message_4", ret.totalSize);
				}
			} else if (msg.what == 3) {
				DataResult ret = (DataResult) msg.obj;
				if (ret.status == P_BabytreeController.SUCCESS_CODE) {

					int serial_number = SharedPreferencesUtil.getIntValue(MessageService.this, "message_5");

					ArrayList<PushMessage> data = (ArrayList<PushMessage>) ret.data;
					for (PushMessage pushMessage : data) {
						if (serial_number == -1 || serial_number < pushMessage.serial_number) {

							boolean isRun = mainActivityIsRuning();
							BabytreeLog.d("The application run is " + isRun);
							if (isRun) {
								AnimationShowWindow animationShowWindow = new AnimationShowWindow(MessageService.this,
										pushMessage.yunqi);
								animationShowWindow.show();

							} else {
								showNotification5(getResources().getString(R.string.app_name), pushMessage.alert,
										pushMessage.id, pushMessage.yunqi);
							}

							// 发送加孕期广播
							// Intent intent = new Intent();
							// intent.setAction(MainBaseActivity.ACTION_NOTIFY);
							// intent.putExtra("yunqi", pushMessage.yunqi);
							// intent.setPackage(getPackageName());
							// LocalBroadcastManager.getInstance(MessageService.this).sendBroadcast(intent);

						}
					}
					SharedPreferencesUtil.setValue(getApplicationContext(), "message_5", ret.totalSize);
				}
			}
		}

	};

	/**
	 * 首页是否在运行
	 * 
	 * @author wangshuaibo
	 * @return
	 */
	private boolean mainActivityIsRuning() {
		try {
			ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			RunningTaskInfo runningTaskInfo = manager.getRunningTasks(1).get(0);
			if (runningTaskInfo.topActivity.getPackageName().equals(getPackageName())) {
				return true;

			}
			return false;
		} catch (Exception ex) {
			return false;
		}
	}

	@Override
	public void onStart(Intent intent, int startId) {
		BabytreeLog.i("BabytreeService", "MessageService onStart.");
		String role = SharedPreferencesUtil.getStringValue(getApplicationContext(), ShareKeys.APP_TYPE_KEY,
				CommConstants.APP_TYPE_UNKNOW);
		BabytreeLog.i("User role is " + role);
		if (role.equalsIgnoreCase(CommConstants.APP_TYPE_MOMMY)) {
			boolean notifyAuto = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_AUTO, true);
			if (notifyAuto) {

				mUserEncodeId = SharedPreferencesUtil.getStringValue(this, ShareKeys.USER_ENCODE_ID);
				if (mUserEncodeId != null && !mUserEncodeId.trim().equals("")) {
					int time = SharedPreferencesUtil.getIntValue(this, ShareKeys.NOTIFY_TIME);
					long minTime = 0;
					long maxTime = System.currentTimeMillis() + 1000;
					long currentTime = System.currentTimeMillis();
					// 1:全天
					// 2:早9点到晚10点
					// 3:早8点到晚10点
					// 4:早7点到晚10点
					if (time == 2) {
						Calendar calendar = Calendar.getInstance(Locale.CHINA);
						calendar.set(Calendar.HOUR_OF_DAY, 9);
						minTime = calendar.getTimeInMillis();
						calendar = Calendar.getInstance(Locale.CHINA);
						calendar.set(Calendar.HOUR_OF_DAY, 22);
						maxTime = calendar.getTimeInMillis();
					} else if (time == 3) {
						Calendar calendar = Calendar.getInstance(Locale.CHINA);
						calendar.set(Calendar.HOUR_OF_DAY, 8);
						minTime = calendar.getTimeInMillis();
						calendar = Calendar.getInstance(Locale.CHINA);
						calendar.set(Calendar.HOUR_OF_DAY, 22);
						maxTime = calendar.getTimeInMillis();
					} else if (time == 4) {
						Calendar calendar = Calendar.getInstance(Locale.CHINA);
						calendar.set(Calendar.HOUR_OF_DAY, 7);
						minTime = calendar.getTimeInMillis();
						calendar = Calendar.getInstance(Locale.CHINA);
						calendar.set(Calendar.HOUR_OF_DAY, 22);
						maxTime = calendar.getTimeInMillis();
					}
					if (currentTime > minTime && currentTime <= maxTime) {
						// 获取个人回帖消息:$md5_2
						final String mkey_2 = Md5Util.md5(mUserEncodeId + "babytree2305") + "_2";
						new Thread() {
							public void run() {

								BabytreeLog.i("Start topic push thread");

								DataResult ret = null;
								Message message = new Message();
								try {
									if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(MessageService.this)) {
										ret = PushController.getMessage(mkey_2);
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
								message.what = 1;
								mHandler.sendMessage(message);
							};
						}.start();
						// 获取个人短消息:$md5_4
						final String mkey_4 = Md5Util.md5(mUserEncodeId + "babytree2305") + "_4";
						new Thread() {
							public void run() {

								BabytreeLog.i("Start message push thread");

								DataResult ret = null;
								Message message = new Message();
								try {
									if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(MessageService.this)) {
										ret = PushController.getMessage(mkey_4);
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
								message.what = 2;
								mHandler.sendMessage(message);
							};
						}.start();
						// 获取孕期值推送:$md5_5
						final String mkey_5 = Md5Util.md5(mUserEncodeId + "babytree2305") + "_5";
						new Thread() {
							public void run() {

								BabytreeLog.i("Start yunqi push thread");

								DataResult ret = null;
								Message message = new Message();
								try {
									if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(MessageService.this)) {
										ret = PushController.getMessage(mkey_5);
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
								message.what = 3;
								mHandler.sendMessage(message);
							};
						}.start();
					}
				}
			}
		}
	}

	private void showNotification2(String response_count, String discuz_id, String page, String contentTitle,
			String content, int notifyId) {
		boolean notifySound = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_SOUND, true);
		boolean notifyViarate = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_VIARATE, true);
		Notification notification = new Notification(R.drawable.icon_notify, content, System.currentTimeMillis());
		Intent intent = new Intent(this, TopicNewActivity.class);
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 增加友盟事件
		intent.putExtra("umeng_event", EventContants.android_promo_topic);
		Bundle bundle = new Bundle();
		bundle.putInt(TopicNewActivity.BUNDLE_DISCUZ_ID, Integer.parseInt(discuz_id));
		bundle.putInt(TopicNewActivity.BUNDLE_PAGE, Integer.parseInt(page));
		intent.putExtras(bundle);
		if (notifySound) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		}
		if (notifyViarate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, contentTitle, content, pendingIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotiManager.notify(notifyId, notification);

	}

	private void showNotification4(String contentTitle, String content, int notifyId) {
		boolean notifySound = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_SOUND, true);
		boolean notifyViarate = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_VIARATE, true);
		Notification notification = new Notification(R.drawable.icon_notify, content, System.currentTimeMillis());
		Intent intent = new Intent(this, NoticeActivity.class);
		if (notifySound) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		}
		if (notifyViarate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 增加友盟事件
		intent.putExtra("umeng_event", EventContants.android_promo_message);
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, contentTitle, content, pendingIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotiManager.notify(notifyId, notification);

	}

	private void showNotification5(String contentTitle, String content, int notifyId, int yunqi) {
		boolean notifySound = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_SOUND, true);
		boolean notifyViarate = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_VIARATE, true);
		Intent intent = null;
		if (SharedPreferencesUtil.getBooleanValue(this, ShareKeys.IS_PREGNANCY)) {// 进入育儿
			intent = new Intent(this, MainFragement.class);
		} else {
			intent = new Intent(this, HomePageActivity.class);
		}
		intent.putExtra("yunqi", yunqi);
		Notification notification = new Notification(R.drawable.icon_notify, content, System.currentTimeMillis());

		if (notifySound) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		}
		if (notifyViarate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// 增加友盟事件
		intent.putExtra("umeng_event", EventContants.android_promo_yunqi);
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, contentTitle, content, pendingIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotiManager.notify(notifyId, notification);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
