package com.babytree.apps.pregnancy.ui;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKLocationManager;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapController;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.RouteOverlay;
import com.umeng.analytics.MobclickAgent;

public class HospitalMapActivity extends MapActivity implements OnClickListener {

	private BMapManager mBMapManager = null;
	private PregnancyApplication mBabytreeApplication = null;

	private MapView mMapView;
	private GeoPoint mPoint;

	private double mX;
	private double mY;
	
	private Button btn_back;
	private FrameLayout fl_title;
	private Button btn_right;

	/**
	 * 
	 * @param context
	 * @param x
	 *            经度
	 * @param y
	 *            纬度
	 * @param name
	 *            医院名称
	 */
	public static void launch(Context context, double x, double y, String name) {
		Intent intent = new Intent(context, HospitalMapActivity.class);
		intent.putExtra("x", x);
		intent.putExtra("y", y);
		intent.putExtra("name", name);
		context.startActivity(intent);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hospital_map_activity);

		mX = getIntent().getDoubleExtra("x", 0);
		mY = getIntent().getDoubleExtra("y", 0);

		btn_back = (Button) findViewById(R.id.btn_left);
		btn_back.setOnClickListener(this);
		btn_right = (Button) findViewById(R.id.btn_right);
		btn_right.setOnClickListener(this);
		fl_title = (FrameLayout)findViewById(R.id.title);
		if(BabytreeUtil.isPregnancy(this)){
			fl_title.setBackgroundResource(R.drawable.y_title_bg);
			btn_back.setBackgroundResource(R.drawable.y_btn_back);
			btn_right.setBackgroundResource(R.drawable.y_btn_main_change);
		}
		mBabytreeApplication = (PregnancyApplication) getApplication();
		mBMapManager = mBabytreeApplication.getBMapManager();
		initMapActivity(mBMapManager);
		mMapView = (MapView) findViewById(R.id.bmapsView);
		mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件

		MapController mMapController = mMapView.getController(); // 得到mMapView的控制权,可以用它控制和驱动平移和缩放
		// 用给定的经纬度构造一个GeoPoint，单位是微度 (度1E6)
		mPoint = new GeoPoint((int) (mY * 1E6), (int) (mX * 1E6));
		mMapController.setCenter(mPoint); // 设置地图中心点
		mMapController.setZoom(16); // 设置地图zoom级别

		// 显示OverlayItem
		Drawable marker = getResources().getDrawable(R.drawable.iconmarka);
		mMapView.getOverlays().add(new OverItemT(marker, mPoint, this));
	}

	private void getLocationLine() {
		MKSearch mMKSearch = new MKSearch();
		mMKSearch.init(mBMapManager, new MySearchListener());// 注意，MKSearchListener只支持一个，以最后一次设置为准
		// 初始化Location模块
		MKLocationManager mLocationManager = mBMapManager.getLocationManager();
		// 通过enableProvider和disableProvider方法，选择定位的Provider
		mLocationManager.enableProvider(MKLocationManager.MK_NETWORK_PROVIDER);
		// mLocationManager.disableProvider(MKLocationManager.MK_GPS_PROVIDER);
		// 添加定位图层
		MyLocationOverlay mylocTest = new MyLocationOverlay(this, mMapView);
		mylocTest.enableMyLocation(); // 启用定位
		mylocTest.enableCompass(); // 启用指南针
		mMapView.getOverlays().add(mylocTest);

		Location locationInfo = mLocationManager.getLocationInfo();
		if (locationInfo != null) {

			MKPlanNode start = new MKPlanNode();
			MKPlanNode end = new MKPlanNode();
			end.pt = mPoint;
			GeoPoint endPoint = new GeoPoint(
					(int) (locationInfo.getLatitude() * 1E6),
					(int) (locationInfo.getLongitude() * 1E6));

			start.pt = endPoint;
			// 设置驾车路线搜索策略，时间优先、费用最少或距离最短
			mMKSearch.setDrivingPolicy(MKSearch.ECAR_TIME_FIRST);
			mMKSearch.drivingSearch(null, start, null, end);
		} else {
			Toast.makeText(this, "获取路线失败,请重试!", Toast.LENGTH_SHORT).show();
		}
	}

	class OverItemT extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> GeoList = new ArrayList<OverlayItem>();

		public OverItemT(Drawable marker, GeoPoint geoPoint, Context context) {
			super(boundCenterBottom(marker));

			GeoList.add(new OverlayItem(geoPoint, null, null));
			populate(); // createItem(int)方法构造item。一旦有了数据，在调用其它方法前，首先调用这个方法
		}

		@Override
		protected OverlayItem createItem(int i) {
			return GeoList.get(i);
		}

		@Override
		public int size() {
			return GeoList.size();
		}

		@Override
		// 处理当点击事件
		protected boolean onTap(int i) {
			return true;
		}
	}

	public class MySearchListener implements MKSearchListener {
		@Override
		public void onGetAddrResult(MKAddrInfo res, int iError) {

		}

		@Override
		public void onGetDrivingRouteResult(MKDrivingRouteResult result,
				int iError) {
			if (result == null) {
				return;
			}
			RouteOverlay routeOverlay = new RouteOverlay(
					HospitalMapActivity.this, mMapView);
			routeOverlay.setData(result.getPlan(0).getRoute(0));
			mMapView.getOverlays().add(routeOverlay);
		}

		@Override
		public void onGetPoiResult(MKPoiResult result, int type, int iError) {
		}

		@Override
		public void onGetTransitRouteResult(MKTransitRouteResult result,
				int iError) {
		}

		@Override
		public void onGetWalkingRouteResult(MKWalkingRouteResult result,
				int iError) {
		}

		@Override
		public void onGetBusDetailResult(MKBusLineResult result, int iError) {

		}

		@Override
		public void onGetSuggestionResult(MKSuggestionResult result, int iError) {

		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_left) {
			finish();
		} else if (v.getId() == R.id.btn_right) {
			getLocationLine();
		}
	}

	// ==========UMENG Begin===========
	@Override
	protected void onResume() {
		if (mBMapManager != null) {
			mBMapManager.start();
		}
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		if (mBMapManager != null) {
			mBMapManager.stop();
		}
		super.onPause();
		MobclickAgent.onPause(this);
	}

	// ==========UMENG End===========

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}
}
