package com.babytree.apps.biz.push.service;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.babytree.apps.biz.knowledge.RemindDetailActivity;
import com.babytree.apps.biz.knowledge.Y_RemindDetailActivity;
import com.babytree.apps.biz.knowledge.ctr.CalendarDbController;
import com.babytree.apps.biz.knowledge.ctr.Y_CalendarDbController;
import com.babytree.apps.biz.knowledge.model.Knowledge;
import com.babytree.apps.biz.knowledge.model.Y_Knowledge;
import com.babytree.apps.biz.welcome.Y_WelcomeActivity;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;

/**
 * 本地重要提醒服务
 * 
 */
public class LocalService extends Service {
	private NotificationManager mNotiManager;

	@Override
	public void onCreate() {
		mNotiManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	}

	@Override
	public void onStart(Intent intent, int id) {
		BabytreeLog.i("BabytreeService", "LocalService onStart.");
		String role = SharedPreferencesUtil.getStringValue(getApplicationContext(), ShareKeys.APP_TYPE_KEY,
				CommConstants.APP_TYPE_UNKNOW);
		BabytreeLog.i("User role is " + role);
		if (role.equalsIgnoreCase(CommConstants.APP_TYPE_MOMMY)) {

			boolean notifyAuto = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_AUTO, true);
			if (notifyAuto) {
				try {
					// 晚8点到9点
					long minTime = 0;
					long maxTime = System.currentTimeMillis() + 1000;
					long currentTime = System.currentTimeMillis();
					Calendar calendarTemp = Calendar.getInstance(Locale.CHINA);
					calendarTemp.set(Calendar.HOUR_OF_DAY, 20);
					minTime = calendarTemp.getTimeInMillis();
					calendarTemp = Calendar.getInstance(Locale.CHINA);
					calendarTemp.set(Calendar.HOUR_OF_DAY, 21);
					maxTime = calendarTemp.getTimeInMillis();
					if (currentTime > minTime && currentTime <= maxTime) {

						// 孕期
						Knowledge bean = new Knowledge();
						Calendar calendar = Calendar.getInstance(Locale.CHINA);
						calendar.setTimeInMillis(System.currentTimeMillis());
						long dateData = SharedPreferencesUtil.getLongValue(this, ShareKeys.BIRTHDAY_TIMESTAMP);
						int hasDaysNum = getBetweenDays(dateData);
						int weekDays = ((280 - hasDaysNum) / 7) + 1;// 周
						int endDays = 280 - hasDaysNum;
						int startDays = 280 - hasDaysNum - 1;
						CalendarDbController mDbController = (CalendarDbController) ((PregnancyApplication) getApplication())
								.getCalendarDbController();
						List<Knowledge> remindLIst = mDbController.getKnowledgeListByDays(startDays, endDays,
								CommConstants.TYPE_REMIND);
						if (remindLIst.size() > 0) {
							for (int i = 0; i < remindLIst.size(); i++) {
								bean = remindLIst.get(i);
								if (bean.is_important == 1) {
									bean._id = remindLIst.get(i)._id;
									bean.status = remindLIst.get(i).status;
									bean.is_important = remindLIst.get(i).is_important;
									bean.days_number = remindLIst.get(i).days_number;
									int daysTmp = SharedPreferencesUtil.getIntValue(this, ShareKeys.PREGNANCY_MAX_DAYS);
									if (bean.days_number > daysTmp) {
										showNotification(bean, "关爱提醒", R.drawable.icon_notify);
										SharedPreferencesUtil.setValue(this, ShareKeys.PREGNANCY_MAX_DAYS,
												bean.days_number);
									}
									break;
								}
							}

						}
						boolean isNotify = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.IS_NOTIFY, false);
						if (!isNotify) {
							if (weekDays > 40 && dateData != -1) {
								showNotificationForY("温馨提示", "您的宝宝已经诞生了吧 ，快来看看有为您准备的贴心育儿内容");
								SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.IS_NOTIFY, true);
							}
						}
						// 育儿
						Y_Knowledge y_bean = new Y_Knowledge();
						Calendar y_calendar = Calendar.getInstance(Locale.CHINA);
						y_calendar.setTimeInMillis(System.currentTimeMillis());
						long y_dateData = SharedPreferencesUtil.getLongValue(this, ShareKeys.BIRTHDAY_TIMESTAMP);
						int y_hasDaysNum = 0;
						y_hasDaysNum = BabytreeUtil.getBetweenDays(y_dateData, System.currentTimeMillis());
						// int y_weekDays = y_hasDaysNum / 7 + 1;
						int y_startDays = y_hasDaysNum;
						int y_endDays = y_hasDaysNum;
						Y_CalendarDbController y_mDbController = (Y_CalendarDbController) ((PregnancyApplication) getApplication())
								.y_getCalendarDbController();
						List<Y_Knowledge> y_remindLIst = y_mDbController.getKnowledgeListByDays(y_startDays, y_endDays,
								CommConstants.TYPE_REMIND);
						if (y_remindLIst.size() > 0) {
							for (int i = 0; i < y_remindLIst.size(); i++) {
								y_bean = y_remindLIst.get(i);
								if (y_bean.is_important == 1) {
									y_bean._id = y_remindLIst.get(i)._id;
									y_bean.title = y_remindLIst.get(i).title;
									y_bean.status = y_remindLIst.get(i).status;
									y_bean.is_important = y_remindLIst.get(i).is_important;
									y_bean.days_number = y_remindLIst.get(i).days_number;
									int daysTmp = SharedPreferencesUtil.getIntValue(this, ShareKeys.PARENTING_MAX_DAYS);
									if (bean.days_number > daysTmp) {
										showNotificationP(y_bean, "关爱提醒", 1, R.drawable.icon_notify);
										SharedPreferencesUtil.setValue(this, ShareKeys.PARENTING_MAX_DAYS,
												y_bean.days_number);
									}
									break;
								}
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	private void showNotificationForY(String contentTitle, String title) {
		boolean notifySound = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_SOUND, true);
		boolean notifyViarate = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_VIARATE, true);
		Notification notification = new Notification(R.drawable.icon_notify, title, System.currentTimeMillis());
		Intent intent = new Intent(this, Y_WelcomeActivity.class);

		if (notifySound) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		}
		if (notifyViarate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}
		intent.putExtra("umeng_event", EventContants.android_promo_local);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, contentTitle, title, pendingIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotiManager.notify(1, notification);

	}

	private void showNotification(Knowledge bean, String contentTitle, int resId) {
		boolean notifySound = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_SOUND, true);
		boolean notifyViarate = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_VIARATE, true);
		Notification notification = new Notification(resId, bean.title, System.currentTimeMillis());
		Intent intent = new Intent(this, RemindDetailActivity.class);
		intent.putExtra("umeng_event", EventContants.android_promo_local);
		intent.putExtra("_id", bean._id);
		intent.putExtra("title", bean.title);
		intent.putExtra("status", bean.status);
		intent.putExtra("is_important", bean.is_important);
		intent.putExtra("identify", 1);
		if (notifySound) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		}
		if (notifyViarate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}

		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, contentTitle, bean.title, pendingIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotiManager.notify(2, notification);

	}

	private void showNotificationP(Y_Knowledge bean, String contentTitle, int id, int resId) {
		boolean notifySound = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_SOUND, true);
		boolean notifyViarate = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_VIARATE, true);
		Notification notification = new Notification(resId, bean.title, System.currentTimeMillis());
		Intent intent = new Intent(this, Y_RemindDetailActivity.class);
		intent.putExtra("umeng_event", EventContants.android_promo_local);
		intent.putExtra("_id", bean._id);
		intent.putExtra("title", bean.title);
		intent.putExtra("status", bean.status);
		intent.putExtra("is_important", bean.is_important);
		intent.putExtra("identify", 1);
		if (notifySound) {
			notification.defaults |= Notification.DEFAULT_SOUND;
		}
		if (notifyViarate) {
			notification.defaults |= Notification.DEFAULT_VIBRATE;
		}

		intent.setAction(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
		PendingIntent pIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		notification.setLatestEventInfo(this, contentTitle, bean.title, pIntent);
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		mNotiManager.notify(id, notification);
	}

	public int getBetweenDays(long l) {
		Calendar nowCal = Calendar.getInstance();
		long nowTime = nowCal.getTimeInMillis();
		long betweenTime = l - nowTime;
		if (betweenTime > 0 && betweenTime <= 280l * 24l * 60l * 60l * 1000l) {
			return (int) (betweenTime / (24l * 60l * 60l * 1000l)) + 1;
		} else {
			return 0;
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
