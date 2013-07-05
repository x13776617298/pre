package com.babytree.apps.biz.push.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.annotation.SuppressLint;
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

import com.babytree.apps.biz.push.ctr.PushController;
import com.babytree.apps.biz.push.model.PushMessage;
import com.babytree.apps.biz.topic.TopicNewActivity;
import com.babytree.apps.biz.welcome.WelcomeActivity;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.ui.activity.BabyTreeWebviewActivity;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

/**
 * 活动服务
 * 
 */
public class ActivityService extends Service {
	// android_promo_pregnancy_1 // 快乐孕期活动 只打开应用
	// android_promo_pregnancy_2 // 快乐孕期活动 打开帖子
	// android_promo_pregnancy_3 // 快乐孕期活动 打开一个url页面

	private NotificationManager mNotiManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		mNotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {

		@SuppressWarnings("unchecked")
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == 1) {
				DataResult ret = (DataResult) msg.obj;
				if (ret.status == P_BabytreeController.SUCCESS_CODE) {
					ArrayList<PushMessage> data = (ArrayList<PushMessage>) ret.data;
					int serial_number = SharedPreferencesUtil.getIntValue(ActivityService.this,
							CommConstants.android_promo_pregnancy_1);
					for (PushMessage pushMessage : data) {

						if (serial_number == -1 || serial_number < pushMessage.serial_number) {

							showNotification1(getResources().getString(R.string.app_name), pushMessage.alert, 3);
						}
					}
					SharedPreferencesUtil.setValue(getApplicationContext(), CommConstants.android_promo_pregnancy_1,
							ret.totalSize);
				}
			} else if (msg.what == 2) {
				DataResult ret = (DataResult) msg.obj;
				if (ret.status == P_BabytreeController.SUCCESS_CODE) {
					int serial_number = SharedPreferencesUtil.getIntValue(ActivityService.this,
							CommConstants.android_promo_pregnancy_2);
					ArrayList<PushMessage> data = (ArrayList<PushMessage>) ret.data;
					for (PushMessage pushMessage : data) {
						if (serial_number == -1 || serial_number < pushMessage.serial_number) {
							showNotification2(String.valueOf(pushMessage.tr), String.valueOf(pushMessage.id),
									String.valueOf(pushMessage.p), getResources().getString(R.string.app_name),
									pushMessage.alert, pushMessage.id);
						}
					}
					SharedPreferencesUtil.setValue(getApplicationContext(), CommConstants.android_promo_pregnancy_2,
							ret.totalSize);
				}
			} else if (msg.what == 3) {
				DataResult ret = (DataResult) msg.obj;
				if (ret.status == P_BabytreeController.SUCCESS_CODE) {
					ArrayList<PushMessage> data = (ArrayList<PushMessage>) ret.data;
					int serial_number = SharedPreferencesUtil.getIntValue(ActivityService.this,
							CommConstants.android_promo_pregnancy_3);
					for (PushMessage pushMessage : data) {
						// 判断是否在推送范围内
						if (isShowNotify(pushMessage)) {

							if (serial_number == -1 || serial_number > ret.totalSize) {

								showNotification3(getResources().getString(R.string.app_name), pushMessage.alert, 4,
										pushMessage.u);
							}
						}
					}
					SharedPreferencesUtil.setValue(getApplicationContext(), CommConstants.android_promo_pregnancy_3,
							ret.totalSize);
				}
			}
		}

	};

	private boolean isShowNotify(PushMessage pushMessage) {
		String loginString = SharedPreferencesUtil.getStringValue(ActivityService.this, "login_string");
		int loc_id = pushMessage.loc_id;
		int prov_id = pushMessage.prov_id;
		int week_type = pushMessage.week_type;
		int min_week = pushMessage.min_week;
		int max_week = pushMessage.max_week;
		long dateData = SharedPreferencesUtil.getLongValue(ActivityService.this, ShareKeys.BIRTHDAY_TIMESTAMP);
		int weekDays = 0;
		if (week_type == 1) {// 孕期
			int hasDaysNum = getBetweenDays(dateData);
			weekDays = ((280 - hasDaysNum) / 7) + 1;// 周
		} else {
			int hasDaysNum = 0;
			try {
				hasDaysNum = BabytreeUtil.getBetweenDays(dateData, System.currentTimeMillis());
			} catch (Exception e) {
				e.printStackTrace();
			}
			weekDays = hasDaysNum / 7 + 1;
		}
		if (loginString != null && !loginString.trim().equals("")) {
			// 登录
			String locationId = SharedPreferencesUtil.getStringValue(this, ShareKeys.LOCATION);
			int userLocationId = 0;
			int userProvId = 0;
			try {
				userLocationId = Integer.parseInt(locationId);
				userProvId = Integer.parseInt(locationId.substring(0, 2));
			} catch (Exception e) {
			}
			if (loc_id != 0 && prov_id != 0) {
				// 限制了地区
				if (userLocationId == loc_id && userProvId == prov_id && weekDays >= min_week && weekDays <= max_week) {
					return true;
				}
				return false;
			} else {
				if (min_week != 0 && max_week != 0) {
					// 限制了时间
					if (weekDays >= min_week && weekDays <= max_week) {
						return true;
					}
				}
				return true;
			}
		} else {
			// 未登录
			if (min_week != 0 && max_week != 0) {
				// 限制了时间
				if (weekDays >= min_week && weekDays <= max_week) {
					return true;
				}
			}
			return true;
		}
	}

	private int getBetweenDays(long l) {
		Calendar nowCal = Calendar.getInstance(Locale.CHINA);
		long nowTime = nowCal.getTimeInMillis();
		long betweenTime = l - nowTime;
		if (betweenTime > 0 && betweenTime <= 280l * 24l * 60l * 60l * 1000l) {
			return (int) (betweenTime / (24l * 60l * 60l * 1000l)) + 1;
		} else {
			return 0;
		}
	}

	private void showNotification1(String contentTitle, String content, int notifyId) {
		boolean notifySound = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_SOUND, true);
		boolean notifyViarate = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_VIARATE, true);
		Notification notification = new Notification(R.drawable.icon_notify, content, System.currentTimeMillis());
		Intent intent = new Intent();
		intent.setClass(this, WelcomeActivity.class);
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
		mNotiManager.notify(notifyId + 100, notification);

	}

	private void showNotification2(String response_count, String discuz_id, String page, String contentTitle,
			String content, int notifyId) {
		boolean notifySound = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_SOUND, true);
		boolean notifyViarate = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_VIARATE, true);
		Notification notification = new Notification(R.drawable.icon_notify, content, System.currentTimeMillis());
		Intent intent = new Intent(this, TopicNewActivity.class);

		intent.putExtra("umeng_event", EventContants.android_promo_pregnancy_2);
		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);

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
		mNotiManager.notify(notifyId + 100, notification);

	}

	private void showNotification3(String contentTitle, String content, int notifyId, String url) {
		boolean notifySound = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_SOUND, true);
		boolean notifyViarate = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_VIARATE, true);
		Notification notification = new Notification(R.drawable.icon_notify, content, System.currentTimeMillis());
		Intent intent = new Intent();
		intent.setClass(this, BabyTreeWebviewActivity.class);
		intent.putExtra(BabyTreeWebviewActivity.BUNDLE_URL, url);
		intent.putExtra("umeng_event", EventContants.android_promo_pregnancy_3);
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
		mNotiManager.notify(notifyId + 100, notification);

	}

	@Override
	public void onStart(Intent intent, int startId) {
		BabytreeLog.i("BabytreeService", "ActivityService onStart.");
		String role = SharedPreferencesUtil.getStringValue(getApplicationContext(), ShareKeys.APP_TYPE_KEY,
				CommConstants.APP_TYPE_UNKNOW);
		BabytreeLog.i("User role is " + role);
		if (role.equalsIgnoreCase(CommConstants.APP_TYPE_MOMMY)) {
			// 妈妈版

			// 早9点到晚10点
			long minTime = 0;
			long maxTime = System.currentTimeMillis() + 1000;
			long currentTime = System.currentTimeMillis();
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			calendar.set(Calendar.HOUR_OF_DAY, 9);
			minTime = calendar.getTimeInMillis();
			calendar = Calendar.getInstance(Locale.CHINA);
			calendar.set(Calendar.HOUR_OF_DAY, 22);
			maxTime = calendar.getTimeInMillis();
			if (currentTime > minTime && currentTime <= maxTime) {
				// android_promo_pregnancy_1 // 快乐孕期活动 只打开应用
				new Thread() {
					public void run() {

						BabytreeLog.i("Start promo1 push thread");

						DataResult ret = null;
						Message message = new Message();
						try {
							if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(ActivityService.this)) {
								ret = PushController.getMessage(CommConstants.android_promo_pregnancy_1);
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
				// android_promo_pregnancy_2 // 快乐孕期活动 打开帖子
				new Thread() {
					public void run() {

						BabytreeLog.i("Start promo2 push thread");

						DataResult ret = null;
						Message message = new Message();
						try {
							if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(ActivityService.this)) {
								ret = PushController.getMessage(CommConstants.android_promo_pregnancy_2);
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
				// android_promo_pregnancy_3 // 快乐孕期活动 打开一个url页面
				new Thread() {
					public void run() {
						DataResult ret = null;
						Message message = new Message();
						try {
							if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(ActivityService.this)) {
								ret = PushController.getMessage(CommConstants.android_promo_pregnancy_3);
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

	@Override
	public void onDestroy() {
	}

}
