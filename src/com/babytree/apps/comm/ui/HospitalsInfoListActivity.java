package com.babytree.apps.comm.ui;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.biz.topic.TopicNewActivity;
import com.babytree.apps.biz.user.LoginActivity;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.HospitalController;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.model.Discuz;
import com.babytree.apps.comm.model.Hospital;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

/**
 * %s医院
 * 
 * @author wangbingqi
 * 
 */
public class HospitalsInfoListActivity extends BabytreeTitleAcitivty implements OnClickListener {
	private ListView hospitalListView;
	private HospitalAdapter myAdapter;
	private LinearLayout loadLayout;
	private List<Hospital> hospitalList;
	private View footerView;
	private LinearLayout addHospitalLayout;
	private String loginStr;
	private ProgressDialog mDialog;
	private String hospitalName;
	private String hospitalId;
	private String groupId;
	private EditText mEdit;
	private Button searchButton;
	private String keys;
	private boolean isFromUserCenter;
	private String provinceId;
	private String cityId;

	public static void lauch(Context context, String key, String hospitalId, boolean isFromUserCenter) {
		Intent intent = new Intent(context, HospitalsInfoListActivity.class);
		intent.putExtra("key", key);
		intent.putExtra("hospital_id", hospitalId);
		intent.putExtra("fromUserCenter", isFromUserCenter);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loadLayout = (LinearLayout) findViewById(R.id.load);
		mEdit = (EditText) findViewById(R.id.et_search_edit);
		searchButton = (Button) findViewById(R.id.btn_search);
		searchButton.setOnClickListener(this);

		hospitalListView = (ListView) findViewById(R.id.list);
		hospitalListView.setVisibility(View.GONE);
		footerView = getLayoutInflater().inflate(getResources().getLayout(R.layout.hospitals_info_list_footer), null);
		addHospitalLayout = (LinearLayout) footerView.findViewById(R.id.btn_add_hospital);
		addHospitalLayout.setOnClickListener(this);
		loadLayout.setVisibility(View.VISIBLE);
		loginStr = SharedPreferencesUtil.getStringValue(getApplicationContext(), ShareKeys.LOGIN_STRING);
		String key = getIntent().getStringExtra("key");
		String hospitalId = getIntent().getStringExtra("hospital_id");
		isFromUserCenter = getIntent().getBooleanExtra("fromUserCenter", false);
		provinceId = getIntent().getStringExtra("province_id");
		cityId = getIntent().getStringExtra("city_id");


		if (key != null) {
			setTitleString(String.format(getResources().getString(R.string.location_hospital_info_title),
					new Object[] { key }));
		}
		if (isFromUserCenter) {
			setTitleString(String.format(getResources().getString(R.string.location_hospital_info_title),
					new Object[] { "同城" }));
		}
		init(key, hospitalId);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (v == addHospitalLayout) {
			loginStr = SharedPreferencesUtil.getStringValue(getApplicationContext(), ShareKeys.LOGIN_STRING);
			if (loginStr != null) {
				AddHospitalActivity.launch(this);
			} else {
				Intent intent = new Intent(this, LoginActivity.class);
				startActivity(intent);
			}

		} else if (v == searchButton) {
			String keyword = mEdit.getText().toString().trim();
			if ("".equals(keyword)) {
				Toast.makeText(HospitalsInfoListActivity.this, R.string.question_can_not_null, Toast.LENGTH_SHORT)
						.show();
				return;
			} else {
				if (keys == null) {
					keys = keyword;
					reInit(keyword);
				} else {
					if (!keys.equals(keyword)) {
						keys = keyword;
						reInit(keyword);
					} else {
						Toast.makeText(mContext, "您已经搜索过该医院啦", Toast.LENGTH_LONG).show();
					}
				}
			}
		}
	}


	private void init(final String key, final String hospitalId) {
		new Thread() {
			@Override
			public void run() {
				DataResult result = new DataResult();
				if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(HospitalsInfoListActivity.this)) {
					boolean isProvince = com.babytree.apps.comm.util.BabytreeUtil.ProvinceORcity(key);
					if (isProvince && hospitalId == null) {
						result = HospitalController.getListByRegion(null, key, null);
					} else if (hospitalId != null) {
						result = HospitalController.getListByRegion(hospitalId, null, null);
					} else {
						result = HospitalController.getListByRegion(null, null, key);
					}
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

	private void reInit(final String key) {
		showDialog(null, "正在搜索...", null, null, true, null, null);
		String title = String.format(getResources().getString(R.string.location_hospital_info_title),
				new Object[] { key });
		if (title != null && title.length() > 5) {
			setTitleString(title.substring(0, 6));
		} else {
			setTitleString(title);
		}
		new Thread() {
			@Override
			public void run() {
				DataResult result = new DataResult();
				if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(HospitalsInfoListActivity.this)) {
					if (provinceId != null) {
						if (provinceId.equals("1100") || provinceId.equals("1200") || provinceId.equals("3100")
								|| provinceId.equals("5000")) {
							result = HospitalController.search(key, provinceId, null);
						}
					} else if (cityId != null) {
						result = HospitalController.search(key, null, cityId);
					} else {
						result = HospitalController.search(key, null, null);
					}

				} else {
					result.message = P_BabytreeController.NetworkExceptionMessage;
					result.status = P_BabytreeController.NetworkExceptionCode;
				}
				Message message = new Message();
				message.what = 3;
				message.obj = result;
				myHandler.sendMessage(message);
			}
		}.start();

	}

	private void setHospital(String hospital_name, String hospital_id) {
		final String hospitalName = hospital_name;
		final String hospitalId = hospital_id;
		new Thread() {
			@Override
			public void run() {
				DataResult result = new DataResult();
				if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(HospitalsInfoListActivity.this)) {
					result = HospitalController.setHospital(loginStr, hospitalId, hospitalName, null);
				} else {
					result.message = P_BabytreeController.NetworkExceptionMessage;
					result.status = P_BabytreeController.NetworkExceptionCode;
				}
				Message message = new Message();
				message.what = 2;
				message.obj = result;
				myHandler.sendMessage(message);
			}
		}.start();

	}

	private Handler myHandler = new Handler() {

		public void handleMessage(android.os.Message msg) {
			DataResult result = (DataResult) msg.obj;
			switch (msg.what) {
			case 1:
				loadLayout.setVisibility(View.GONE);
				if (result.data != null) {
					hospitalList = (List<Hospital>) result.data;
					myAdapter = new HospitalAdapter();
					hospitalListView.addFooterView(footerView);
					footerView.setVisibility(View.GONE);
					hospitalListView.setAdapter(myAdapter);
					hospitalListView.setVisibility(View.VISIBLE);
				}
				break;
			case 2:
				cancelDialog();
				SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.ISCHOICEHOSPITAL, true);
				SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.HOSPITAL_ID, hospitalId);
				SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.HOSPITAL_NAME, hospitalName);
				SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.GROUP_ID, groupId);
				Intent intent = new Intent(HospitalsInfoListActivity.this, ForumTabHospitalActivity.class);
				intent.putExtra("hospital_id", hospitalId);
				intent.putExtra("hospital_name", hospitalName);
				intent.putExtra("group_id", groupId);
				startActivity(intent);
				setResult(RESULT_OK);
				finish();
				break;
			case 3:
				cancelDialog();
				if (result.data != null) {
					if (keys.length() > 7) {
						setTitleString(String.format(
								getResources().getString(R.string.location_hospital_info_title),
								new Object[] { keys.substring(0, 7) }));
					}
					setTitleString(String.format(getResources()
							.getString(R.string.location_hospital_info_title), new Object[] { keys }));
					hospitalList = (List<Hospital>) result.data;
					if (myAdapter == null) {
						myAdapter = new HospitalAdapter();
						hospitalListView.addFooterView(footerView);
						footerView.setVisibility(View.VISIBLE);
						hospitalListView.setAdapter(myAdapter);
						hospitalListView.setVisibility(View.VISIBLE);
					} else {
						myAdapter.notifyDataSetInvalidated();
						footerView.setVisibility(View.VISIBLE);
					}
					if (hospitalList != null && hospitalList.size() == 0) {
						Toast.makeText(mContext, "无搜索结果", Toast.LENGTH_LONG).show();
					}
				}
				break;
			default:
				break;
			}
		};
	};

	private class HospitalAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return hospitalList.size();
		}

		@Override
		public Object getItem(int id) {
			return hospitalList.get(id);
		}

		@Override
		public long getItemId(int id) {
			return id;
		}

		@Override
		public View getView(final int position, View view, ViewGroup viewGroup) {
			ViewHolder holder = null;
			if (view == null) {
				holder = new ViewHolder();
				view = View.inflate(HospitalsInfoListActivity.this, R.layout.hospitals_info_item, null);
				holder.hospitalName = (TextView) view.findViewById(R.id.tv_name_hospital);
				holder.tzCount = (TextView) view.findViewById(R.id.tv_tz);
				holder.ymCount = (TextView) view.findViewById(R.id.tv_ym);
				holder.setMyHospitalButton = (Button) view.findViewById(R.id.btn_set_my_hospital);
				holder.jdTxt = (TextView) view.findViewById(R.id.tv_jd_hospital);
				holder.layout = (LinearLayout) view.findViewById(R.id.layout);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			Hospital hospitalItem = hospitalList.get(position);
			holder.hospitalName.setText(hospitalItem.name);
			holder.tzCount.setText(hospitalItem.topic_count);
			holder.ymCount.setText(hospitalItem.user_count);
			holder.layout.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Hospital hospital = hospitalList.get(position);
					Log.e("hospital_id", hospital.id);
					Log.e("hospital_name", hospital.name);
					Log.e("group_id", hospital.group_id);
					Intent intent = new Intent(HospitalsInfoListActivity.this, ForumTabHospitalActivity.class);
					intent.putExtra("hospital_id", hospital.id);
					intent.putExtra("hospital_name", hospital.name);
					intent.putExtra("group_id", hospital.group_id);
					startActivity(intent);
					finish();
				}
			});
			holder.setMyHospitalButton.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// 点击设为我的医院
					MobclickAgent.onEvent(getBaseContext(), EventContants.wodeyiyuan);
					loginStr = SharedPreferencesUtil.getStringValue(getApplicationContext(), ShareKeys.LOGIN_STRING);
					if (loginStr != null) {
						Hospital hospitalItem = hospitalList.get(position);
						showDialog(null, "设置中，请稍后...", null, null, true, null, null);
						hospitalId = hospitalItem.id;
						hospitalName = hospitalItem.name;
						groupId = hospitalItem.group_id;
						setHospital(hospitalItem.name, hospitalItem.id);
					} else {
						Intent intent = new Intent(HospitalsInfoListActivity.this, LoginActivity.class);
						startActivity(intent);
					}
				}
			});
			if (hospitalItem.discus == null || hospitalItem.discus.discuz_id == null
					|| "0".equals(hospitalItem.discus.discuz_id)) {
				holder.jdTxt.setVisibility(View.GONE);
			} else {
				holder.jdTxt.setVisibility(View.VISIBLE);
				holder.jdTxt.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Hospital hospitalItem = hospitalList.get(position);
						TopicNewActivity.launch(mContext, Integer.parseInt(hospitalItem.discus.discuz_id), 0);
					}
				});
			}
			return view;
		}
	}

	private static class ViewHolder {
		// 医院名称
		private TextView hospitalName;
		// 帖子数
		private TextView tzCount;
		// 孕妈数
		private TextView ymCount;
		// 设置我的医院
		private Button setMyHospitalButton;
		// 建档攻略
		private TextView jdTxt;
		// 整个item框
		private LinearLayout layout;
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
		button.setText("选择地区");
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isFromUserCenter) {
					Intent intent = new Intent(HospitalsInfoListActivity.this, LocationList3Activity.class);
					startActivity(intent);
					finish();
				} else {
					finish();
				}

			}
		});
	}

	@Override
	public String getTitleString() {
		return "%s医院";
	}

	@Override
	public int getBodyView() {
		return R.layout.hospitals_info_list_activity;
	}

}
