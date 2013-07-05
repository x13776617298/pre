package com.babytree.apps.pregnancy.ui;

import com.babytree.apps.biz.knowledge.ctr.CalendarDbController;
import com.babytree.apps.biz.knowledge.ctr.Y_CalendarDbController;
import com.babytree.apps.biz.push.BabytreePushService;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.BabytreeDbController;
import com.babytree.apps.comm.ctr.LocationDbController;
import com.babytree.apps.comm.db.CalendarDbAdapter;
import com.babytree.apps.comm.db.DbAdapter;
import com.babytree.apps.comm.db.DbAdapterForOld;
import com.babytree.apps.comm.db.LocationDbAdapter;
import com.babytree.apps.comm.db.Y_CalendarDbAdapter;
import com.babytree.apps.comm.net.BabytreeHttp;
import com.babytree.apps.comm.service.BabytreeApplication;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.mapapi.BMapManager;

public class PregnancyApplication extends BabytreeApplication {

	// 百度MapAPI的管理类
	private BMapManager mBMapMan = null;

	// 数据库
	private static DbAdapter dbAdapter;
	private static DbAdapterForOld dbAdapterForOld;
	private static CalendarDbAdapter calendarDbAdapter;
	private static Y_CalendarDbAdapter y_calendarDbAdapter;
	private static LocationDbAdapter locationDbAdapter;

	// DB Controller
	private static CalendarDbController calendarDbController;
	private static Y_CalendarDbController y_calendarDbController;
	private static LocationDbController locationDbController;
	private static BabytreeDbController dbController;

	/**
	 * 推送服务
	 */
	private BabytreePushService mPushService;

	/**
	 * 获取推送服务
	 * 
	 * @author wangshuaibo
	 * @return
	 */
	public BabytreePushService getBabytreePushService() {
		return mPushService;
	}

	public DbAdapter getDbAdapter() {
		return dbAdapter;
	}

	public DbAdapterForOld getDbAdapterForOld() {
		return dbAdapterForOld;
	}

	public CalendarDbAdapter getCalendarDbAdapter() {
		return calendarDbAdapter;
	}

	public Y_CalendarDbAdapter y_getCalendarDbAdapter() {
		return y_calendarDbAdapter;
	}

	public LocationDbAdapter getLocationDbAdapter() {
		return locationDbAdapter;
	}

	public BabytreeDbController getDbController() {
		if (dbController == null) {
			dbController = new BabytreeDbController(getDbAdapter());
		}
		return dbController;
	}

	public CalendarDbController getCalendarDbController() {
		if (calendarDbController == null) {
			calendarDbController = new CalendarDbController(getCalendarDbAdapter());
		}
		return calendarDbController;
	}

	public Y_CalendarDbController y_getCalendarDbController() {
		if (y_calendarDbController == null) {
			y_calendarDbController = new Y_CalendarDbController(y_getCalendarDbAdapter());
		}
		return y_calendarDbController;
	}

	public LocationDbController getLocationDbController() {
		if (locationDbController == null) {
			locationDbController = new LocationDbController(getLocationDbAdapter());
		}
		return locationDbController;
	}

	public BMapManager getBMapManager() {
		return mBMapMan;
	}

	@Override
	public void onCreate() {

		BabytreeLog.enableLog(true);

		setAppId("pregnancy");

		setBDLocationListener(new MLocationListenner());

		super.onCreate();

		// 初始化数据库
		try {
			dbAdapter = new DbAdapter(this);
			dbAdapterForOld = new DbAdapterForOld(this);
			calendarDbAdapter = new CalendarDbAdapter(this);
			y_calendarDbAdapter = new Y_CalendarDbAdapter(this);
			locationDbAdapter = new LocationDbAdapter(this);

			// 百度地图
			mBMapMan = new BMapManager(this);
			mBMapMan.init(com.babytree.apps.comm.config.CommConstants.BMAP_KEY, null);
			// 服务
			mPushService = new BabytreePushService(this, false);
			// 是否开启服务(2,4)
			// 类型4、收到短消息，需要进入用户的短消息列表
			// 类型2、帖子收到回复，需要进入帖子详情页
			boolean notifyAuto = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.NOTIFY_AUTO, true);
			if (notifyAuto) {
				// 间隔
				int alerm = SharedPreferencesUtil.getIntValue(this, ShareKeys.NOTIFY_ALERM);
				// 消息服务
				mPushService.startMessageService(alerm);
				// 本地服务
				mPushService.startLocalService(alerm);
			}
			// 活动服务
			mPushService.startActivityService(CommConstants.ACTIVITY_ALERM_INTERVAL);
		} finally {
		}

	}

	private class MLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			if (location == null)
				return;
			BabytreeLog.d("Request location success.");
			BabytreeLog.d("The location info latitude " + location.getLatitude() + " longitude "
					+ location.getLongitude() + ".");
			BabytreeHttp.setLocation(location.getLatitude(), location.getLongitude());
			// 设置定位城市
			SharedPreferencesUtil
					.setValue(getApplicationContext(), ShareKeys.LOCATION_FOR_HOSPITAL, location.getCity());
			if (mLocationClient != null && mLocationClient.isStarted()) {
				mLocationClient.stop();
				mLocationClient = null;
			}

		}

		@Override
		public void onReceivePoi(BDLocation arg0) {

		}
	}

	public void closeDB() {
		try {
			dbAdapter.close();
			dbAdapterForOld.close();
			calendarDbAdapter.close();
			y_calendarDbAdapter.close();
			locationDbAdapter.close();
		} finally {

		}
	}

	@Override
	public void onTerminate() {
		BabytreeLog.i("Application terminate");
		super.onTerminate();

		closeDB();

	}

}
