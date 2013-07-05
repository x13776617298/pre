package com.babytree.apps.biz.push;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.babytree.apps.biz.push.service.ActivityService;
import com.babytree.apps.biz.push.service.LocalService;
import com.babytree.apps.biz.push.service.MessageService;

/**
 * 推送服务类 目前采用轮询的方式获取推送信息,推送类型分3类 <br>
 * 1.活动类(如:孕期app活动,育儿app活动)<br>
 * 2.消息类(如:个人短消息,回帖消息,爸爸完成任务给妈妈加孕气值)<br>
 * 3.本地关爱提醒通知<br>
 * 
 * @author wangshuaibo
 * 
 */
public class BabytreePushService {
	/**
	 * 消息服务首次启动时间
	 */
	private static final long MESSAGE_FIRST_ALERM_INTERVAL = 1L * 60L * 1000L; // 默认1分钟
	/**
	 * 活动服务首次启动时间
	 */
	private static final long ACTIVITY_FIRST_ALERM_INTERVAL = 10L * 60L * 1000L; // 10分钟

	/**
	 * debug轮询时间
	 */
	private static final long DEBUG_ALERM_INTERVAL = 30L * 1000L; // 默认30秒

	/**
	 * AppContext
	 */
	private Context mAppContext;

	/**
	 * 是否DEBUG模式(debug模式每一分钟轮询一次)
	 */
	private boolean mDebug;

	/**
	 * 闹铃管理
	 */
	private AlarmManager mAlarmManager;
	/**
	 * 消息Intent
	 */
	private PendingIntent mMessageSender;
	/**
	 * 活动Intent
	 */
	private PendingIntent mActivitySender;

	/**
	 * 本地Intent
	 */
	private PendingIntent mLocalSender;

	public BabytreePushService(Context appContext, boolean debug) {
		mAppContext = appContext;
		mDebug = debug;

		// 初始化闹铃管理
		mAlarmManager = (AlarmManager) mAppContext.getSystemService(Context.ALARM_SERVICE);

	}

	/**
	 * 启动
	 * 
	 * @author wangshuaibo
	 */
	public void start() {

	}

	/**
	 * 停止
	 * 
	 * @author wangshuaibo
	 */
	public void stop() {

	}

	/**
	 * 启动消息服务
	 * 
	 * @param interval
	 *            启动间隔(分钟)
	 * @author wangshuaibo
	 */
	public void startMessageService(long interval) {
		Intent intent = new Intent(mAppContext, MessageService.class);
		mMessageSender = PendingIntent.getService(mAppContext, 1, intent, 0);
		mAlarmManager.cancel(mMessageSender);
		long firstTime = SystemClock.elapsedRealtime() + MESSAGE_FIRST_ALERM_INTERVAL;
		long intervalTime = interval * 60L * 1000L;
		if (mDebug) {
			firstTime = 0;
			intervalTime = DEBUG_ALERM_INTERVAL;
		}
		mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, intervalTime, mMessageSender);
	}

	/**
	 * 停止消息服务
	 * 
	 * @author wangshuaibo
	 */
	public void stopMessageService() {
		mAlarmManager.cancel(mMessageSender);
	}

	/**
	 * 重新启动消息服务
	 * 
	 * @param interval
	 *            启动间隔(分钟)
	 * @author wangshuaibo
	 */
	public void restartMessageService(long interval) {
		startMessageService(interval);
	}

	/**
	 * 启动本地服务
	 * 
	 * @param interval
	 *            启动间隔(分钟)
	 * @author wangshuaibo
	 */
	public void startLocalService(long interval) {
		Intent intent = new Intent(mAppContext, LocalService.class);
		mLocalSender = PendingIntent.getService(mAppContext, 2, intent, 0);
		mAlarmManager.cancel(mLocalSender);
		long firstTime = SystemClock.elapsedRealtime() + interval * 60L * 1000L;
		long intervalTime = interval * 60L * 1000L;
		if (mDebug) {
			firstTime = 0;
			intervalTime = DEBUG_ALERM_INTERVAL;
		}
		mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, intervalTime, mLocalSender);
	}

	/**
	 * 停止本地服务
	 * 
	 * @author wangshuaibo
	 */
	public void stopLocalService() {
		mAlarmManager.cancel(mLocalSender);
	}

	/**
	 * 重新启动本地服务
	 * 
	 * @param interval
	 *            启动间隔(分钟)
	 * @author wangshuaibo
	 */
	public void restartLocalService(long interval) {
		startLocalService(interval);
	}

	/**
	 * 启动活动服务
	 * 
	 * @param interval
	 *            启动间隔(分钟)
	 * @author wangshuaibo
	 */
	public void startActivityService(long interval) {
		Intent intent = new Intent(mAppContext, ActivityService.class);
		mActivitySender = PendingIntent.getService(mAppContext, 3, intent, 0);
		mAlarmManager.cancel(mActivitySender);
		long firstTime = SystemClock.elapsedRealtime() + ACTIVITY_FIRST_ALERM_INTERVAL;
		long intervalTime = interval * 60L * 1000L;
		if (mDebug) {
			firstTime = 0;
			intervalTime = DEBUG_ALERM_INTERVAL;
		}
		mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, firstTime, intervalTime, mActivitySender);
	}

	/**
	 * 停止本地服务
	 * 
	 * @author wangshuaibo
	 */
	public void stopActivityService() {
		mAlarmManager.cancel(mActivitySender);
	}

	/**
	 * 重新启动本地服务
	 * 
	 * @param interval
	 *            启动间隔(分钟)
	 * @author wangshuaibo
	 */
	public void restartActivityService(long interval) {
		startActivityService(interval);
	}
}
