package com.babytree.apps.comm.ui;

import java.util.List;

import android.app.ProgressDialog;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.biz.home.HomePageActivity;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.HospitalController;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.model.Hospital;
import com.babytree.apps.comm.model.SortedHospital;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.ui.widget.PinnedHeaderListView;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.adapter.ChoiceHospitalAdapter;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.umeng.analytics.MobclickAgent;

/**
 * 选择城市
 * 
 * @author luozheng
 * 
 */
public class ChoiceHospitalActivity extends BabytreeTitleAcitivty implements OnClickListener, OnItemClickListener {

	private PinnedHeaderListView mListView;
	private Button refreshLocationImg;
	private LinearLayout loadLayout;
	private List<SortedHospital> hospitalList;
	private EditText mEdit;
	private Button mSearchButton;
	private Button mChangeLocation;
	private TextView locationText;
	private ChoiceHospitalAdapter mAdapter;
	private ProgressDialog mDialog;
	private ImageView mengceng;
	private String hospitalId;
	private String hospitalName;
	private String groupId;

	private LocationClient mLocationClient = null;
	private MyLocationListenner myListener = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		super.onCreate(savedInstanceState);
		loadLayout = (LinearLayout) findViewById(R.id.load);
		loadLayout.setVisibility(View.GONE);
		mEdit = (EditText) findViewById(R.id.et_search_edit);
		mSearchButton = (Button) findViewById(R.id.btn_search);
		mSearchButton.setOnClickListener(this);
		mChangeLocation = (Button) findViewById(R.id.btn_change_location);
		mChangeLocation.setOnClickListener(this);
		locationText = (TextView) findViewById(R.id.tv_refresh_info);
		mListView = (PinnedHeaderListView) this.findViewById(R.id.list);
		mListView.setOnItemClickListener(this);
		refreshLocationImg = (Button) findViewById(R.id.iv_refresh);
		refreshLocationImg.setOnClickListener(this);
		String location = SharedPreferencesUtil
				.getStringValue(getApplicationContext(), ShareKeys.LOCATION_FOR_HOSPITAL);
		if (location == null) {
			locationText.setText("请重新定位!");
		} else {
			locationText.setText(location);
			init(location);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mLocationClient != null) {
			mLocationClient.unRegisterLocationListener(myListener);
			mLocationClient = null;
			myListener = null;
		}
	}

	@Override
	protected int setPopWindowImage() {
		boolean b = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.CHOICEHOSPITALMC);
		if (b) {
			return 0;
		}
		SharedPreferencesUtil.setValue(this, ShareKeys.CHOICEHOSPITALMC, true);
		return R.drawable.mengceng;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		SortedHospital sortHospital = hospitalList.get(position);
		Hospital hospital = sortHospital.getHospital();
		hospitalId = hospital.id;
		hospitalName = hospital.name;
		groupId = hospital.group_id;
		SharedPreferencesUtil.setValue(ChoiceHospitalActivity.this, ShareKeys.ISCHOICEHOSPITAL, true);
		SharedPreferencesUtil.setValue(ChoiceHospitalActivity.this, ShareKeys.HOSPITAL_ID, hospitalId);
		SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.HOSPITAL_NAME, hospitalName);
		SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.GROUP_ID, groupId);
		SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.ISNESSARYSYN, true);
		Intent intent = new Intent(ChoiceHospitalActivity.this, HomePageActivity.class);
		startActivity(intent);
		finish();

	}

	@Override
	public void onClick(View v) {
		if (v == refreshLocationImg) {
			showDialog(null, "正在定位，请稍后...", null, null, true, null, null);
			getLocation();

		} else if (v == mSearchButton) {
			String keyword = mEdit.getText().toString().trim();
			if ("".equals(keyword)) {
				Toast.makeText(ChoiceHospitalActivity.this, R.string.question_can_not_null, Toast.LENGTH_SHORT).show();
				return;
			} else {
				Intent intent = new Intent(this, HospitalListActivity.class);
				intent.putExtra("key", keyword);
				startActivityForResult(intent, 10);
			}
		} else if (v == mChangeLocation) {
			Intent intent = new Intent(this, LocationList3Activity.class);
			intent.putExtra("isFromInit", true);
			startActivityForResult(intent, 10);
		} else if (v == mengceng) {
			mengceng.setVisibility(View.GONE);
		}
	}

	private void initData(DataResult result) {
		loadLayout.setVisibility(View.GONE);
		hospitalList = (List<SortedHospital>) result.data;
		if (hospitalList != null && hospitalList.size() > 0) {
			mListView.setPinnedHeaderView(LayoutInflater.from(ChoiceHospitalActivity.this).inflate(
					R.layout.hospital_information_header, mListView, false));
			if (mAdapter == null) {
				mAdapter = new ChoiceHospitalAdapter(this, hospitalList);
				mListView.setAdapter(mAdapter);
			} else {
				mAdapter.notifyDataSetChanged();
			}
			mListView.setVisibility(View.VISIBLE);
		}
	}

	private void init(final String city) {
		loadLayout.setVisibility(View.VISIBLE);
		new Thread() {
			@Override
			public void run() {
				DataResult result = new DataResult();
				if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(ChoiceHospitalActivity.this)) {
					result = HospitalController.getSortedListByRegion(city);
				} else {
					result.message = P_BabytreeController.NetworkExceptionMessage;
					result.status = P_BabytreeController.NetworkExceptionCode;
				}
				Message message = new Message();
				message.what = 1;
				message.obj = result;
				myHandler.sendMessage(message);
			}
		}.start();

	}

	private Handler myHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			cancelDialog();
			if (msg.what == 1) {
				DataResult result = (DataResult) msg.obj;
				if (result.data != null) {
					initData(result);
				}
			}
		};
	};

	private void getLocation() {
		mLocationClient = new LocationClient(this);
		LocationClientOption option = new LocationClientOption();
		option.setOpenGps(true); // 打开gps
		option.setCoorType("bd09ll"); // 设置坐标类型
		option.disableCache(false);
		option.setProdName("pregnancy");
		option.setPriority(LocationClientOption.NetWorkFirst);
		option.setScanSpan(5000); // 设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
		mLocationClient.setLocOption(option);
		myListener = new MyLocationListenner();
		mLocationClient.registerLocationListener(myListener);
		mLocationClient.start();
	}

	/**
	 * 监听函数，又新位置的时候，格式化成字符串，输出到屏幕中
	 */
	public class MyLocationListenner implements BDLocationListener {
		@Override
		public void onReceiveLocation(BDLocation location) {
			cancelDialog();
			if (location == null)
				return;
			String city = location.getCity();
			if (city != null && !"".equals(city)) {
				String oldCity = SharedPreferencesUtil.getStringValue(getApplicationContext(),
						ShareKeys.LOCATION_FOR_HOSPITAL);
				if (city != null && !city.equals(oldCity)) {
					SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.LOCATION_FOR_HOSPITAL, city);
					init(city);
				}
			}
			mLocationClient.stop();
			mLocationClient.unRegisterLocationListener(myListener);
		}

		@Override
		public void onReceivePoi(BDLocation arg0) {

		}

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 10:
			if (resultCode == RESULT_OK) {
				finish();
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	private void showDialog(String title, String content, String okText, String cancleText, boolean cancelable,
			OnCancelListener btnCancle, OnClickListener btnOk) {
		mDialog = new ProgressDialog(this);
		mDialog.setTitle(title);
		mDialog.setMessage(content);
		mDialog.setCancelable(cancelable);
		mDialog.show();
	}

	private void cancelDialog() {
		if (mDialog != null && !isFinishing()) {
			mDialog.dismiss();
			mDialog = null;
		}
	}

	@Override
	public void setLeftButton(Button button) {
	}

	@Override
	public void setRightButton(Button button) {
		button.setVisibility(View.VISIBLE);
		button.setText("跳过");
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MobclickAgent.onEvent(ChoiceHospitalActivity.this, EventContants.tiaoguo);
				SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.ISCHOICEHOSPITAL, false);
				Intent intent = new Intent(ChoiceHospitalActivity.this, HomePageActivity.class);
				startActivity(intent);
				finish();
			}
		});

	}

	@Override
	public String getTitleString() {
		return "选择城市";
	}

	@Override
	public int getBodyView() {
		return R.layout.choice_hospital_activity;
	}

}
