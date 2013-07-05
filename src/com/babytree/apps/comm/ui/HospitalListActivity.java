package com.babytree.apps.comm.ui;

import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.babytree.apps.biz.home.HomePageActivity;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.HospitalController;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.model.Hospital;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

/**
 * 选择医院
 * 
 * @author wangbingqi
 * 
 */
public class HospitalListActivity extends BabytreeActivity implements OnClickListener, OnItemClickListener {

	private Button buttonBack;
	private ListView hospitalListView;
	private HospitalAdapter myAdapter;
	private LinearLayout loadLayout;
	private List<Hospital> hospitalList;
	private LayoutInflater mInflater;
	private ProgressDialog mDialog;
	private String hospitalId;
	private String hospitalName;
	private String groupId;

	public static void lauch(Context context, String key) {
		Intent intent = new Intent(context, HospitalListActivity.class);
		intent.putExtra("key", key);
		context.startActivity(intent);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hospital_list_activity);
		loadLayout = (LinearLayout) findViewById(R.id.load);
		mInflater = getLayoutInflater();
		buttonBack = (Button) findViewById(R.id.btn_left);
		buttonBack.setOnClickListener(this);
		hospitalListView = (ListView) findViewById(R.id.list);
		hospitalListView.setOnItemClickListener(this);
		hospitalListView.setVisibility(View.GONE);
		loadLayout.setVisibility(View.VISIBLE);
		String key = getIntent().getStringExtra("key");
		boolean isFromLocation = getIntent().getBooleanExtra("from_location", false);
		if (isFromLocation) {
			initFromLocation(key);
		} else {
			initFromNotLocation(key);
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v == buttonBack) {
			finish();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Hospital hospital = hospitalList.get(position);
		hospitalId = hospital.id;
		hospitalName = hospital.name;
		groupId = hospital.group_id;
		SharedPreferencesUtil.setValue(HospitalListActivity.this, ShareKeys.ISCHOICEHOSPITAL, true);
		SharedPreferencesUtil.setValue(HospitalListActivity.this, ShareKeys.HOSPITAL_ID, hospitalId);
		SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.HOSPITAL_NAME, hospitalName);
		SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.GROUP_ID, groupId);
		// 判断用户第一次登录选择医院以后是否需要同步到他的个人信息里
		SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.ISNESSARYSYN, true);
		setResult(RESULT_OK);
		Intent intent = new Intent(HospitalListActivity.this, HomePageActivity.class);
		startActivity(intent);
		finish();
	}
	private void initFromNotLocation(final String key) {
		new Thread() {
			@Override
			public void run() {
				DataResult result = new DataResult();
				if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(HospitalListActivity.this)) {
					result = HospitalController.search(key, null, null);
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

	private void initFromLocation(final String key) {
		new Thread() {
			@Override
			public void run() {
				DataResult result = new DataResult();
				if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(HospitalListActivity.this)) {
					result = HospitalController.getListByRegion(null, null, key);
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
					loadLayout.setVisibility(View.GONE);
					hospitalList = (List<Hospital>) result.data;
					myAdapter = new HospitalAdapter();
					hospitalListView.setAdapter(myAdapter);
					hospitalListView.setVisibility(View.VISIBLE);
				}
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
		public View getView(int position, View view, ViewGroup viewGroup) {
			ViewHolder holder = null;
			if (view == null) {
				holder = new ViewHolder();
				view = mInflater.inflate(R.layout.hospital_list_item2, null);
				holder.hospitalName = (TextView) view.findViewById(R.id.title);
				view.setTag(holder);
			} else {
				holder = (ViewHolder) view.getTag();
			}
			Hospital hospitalItem = hospitalList.get(position);
			holder.hospitalName.setText(hospitalItem.name);
			return view;
		}

	}

	private static class ViewHolder {
		// 医院名称
		private TextView hospitalName;
	}

	private void cancelDialog() {
		if (mDialog != null && !isFinishing()) {
			mDialog.dismiss();
			mDialog = null;
		}
	}
}
