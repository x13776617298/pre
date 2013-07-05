package com.babytree.apps.comm.ui;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.BabytreeController;
import com.babytree.apps.comm.ctr.HospitalController;
import com.babytree.apps.comm.model.Doctor;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

public class HospitalDoctorListActivity extends BabytreeActivity implements OnClickListener {

	private ListView mlistView;
	private TextView mTvEmpty;
	private ProgressDialog mDialog;
	private String mHospitalId;
	private String mGroupId;

	private ArrayList<Doctor> mList;
	private MAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hospital_doctor_list_activity);
		mlistView = (ListView) findViewById(R.id.list);
		mTvEmpty = (TextView) findViewById(R.id.tv_empty);
		mHospitalId = getIntent().getStringExtra("hospital_id");
		if (mHospitalId == null || mHospitalId.equals("")) {
			mHospitalId = SharedPreferencesUtil.getStringValue(this, ShareKeys.HOSPITAL_ID);
		}
		mGroupId = getIntent().getStringExtra("group_id");
		if (mGroupId == null || mGroupId.equals("")) {
			mGroupId = SharedPreferencesUtil.getStringValue(this, ShareKeys.GROUP_ID);
		}
		if (mHospitalId != null && !mHospitalId.equals("")) {
			process();
		}
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	public void showDialog(String title, String content, String okText, String cancleText, boolean cancelable,
			OnCancelListener btnCancle, OnClickListener btnOk) {
		mDialog = new ProgressDialog(this);
		mDialog.setTitle(title);
		mDialog.setMessage(content);
		mDialog.setCancelable(cancelable);
		mDialog.show();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mDialog != null && !HospitalDoctorListActivity.this.isFinishing())
				mDialog.dismiss();
			DataResult ret = (DataResult) msg.obj;
			switch (ret.status) {
			case BabytreeController.SUCCESS_CODE:
				if (ret.totalSize > 0) {
					mTvEmpty.setVisibility(View.GONE);
					mlistView.setVisibility(View.VISIBLE);
					mList = (ArrayList<Doctor>) ret.data;
					mAdapter = new MAdapter(HospitalDoctorListActivity.this, mList);
					mlistView.setAdapter(mAdapter);
				} else {
					mTvEmpty.setVisibility(View.VISIBLE);
					mlistView.setVisibility(View.GONE);
				}
				break;
			default:
				ExceptionUtil.catchException(ret.error, HospitalDoctorListActivity.this);
				break;
			}
		}
	};

	private void process() {
		showDialog(null, "加载中...", null, null, true, null, null);
		new Thread() {

			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(HospitalDoctorListActivity.this)) {
						ret = HospitalController.getDoctorList(mHospitalId);
					} else {
						ret = new DataResult();
						ret.message = BabytreeController.NetworkExceptionMessage;
						ret.status = BabytreeController.NetworkExceptionCode;
					}
				} catch (Exception e) {
					ret = new DataResult();
					ret.message = BabytreeController.SystemExceptionMessage;
					ret.status = BabytreeController.SystemExceptionCode;
					ret.error = ExceptionUtil.printException(e).toString();
				}
				message.obj = ret;
				handler.sendMessage(message);
			}

		}.start();
	}

	private class MAdapter extends BaseAdapter {
		private Context mContext;
		private ArrayList list;

		public MAdapter(Context context, ArrayList list) {
			this.mContext = context;
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewCache viewCache;
			final Doctor bean = (Doctor) list.get(position);
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.doctor_list_activity_item, null);
				viewCache = new ViewCache(convertView);
				convertView.setTag(viewCache);
			} else {
				viewCache = (ViewCache) convertView.getTag();
				// count = 0;
			}
			TextView mTvName = viewCache.getName();
			mTvName.setText(bean.name);

			TextView mTvTitle = viewCache.getTitle();
			mTvTitle.setText(bean.title);

			TextView mTvTopicCount = viewCache.getTopicCount();
			mTvTopicCount.setText(bean.topic_count + "个");

			LinearLayout layout = viewCache.getLayout();
			if (position == 0) {
				layout.setBackgroundResource(R.drawable.doctor_list_selector_top_background);
			} else if (position == list.size() - 1) {
				layout.setBackgroundResource(R.drawable.doctor_list_selector_bottom_background);
			} else {
				layout.setBackgroundResource(R.drawable.doctor_list_selector_background);
			}
			layout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mContext.startActivity(new Intent(HospitalDoctorListActivity.this, DoctorTopicListActivity.class)
							.putExtra("doctor_name", bean.name).putExtra("group_id", mGroupId)
							.putExtra("position", position));
				}
			});
			return convertView;
		}
	}

	static class ViewCache {
		private View baseView;
		private TextView name;
		private TextView title;
		private TextView topicCount;
		private LinearLayout layout;

		public ViewCache(View view) {
			baseView = view;
		}

		public TextView getName() {
			if (name == null) {
				name = (TextView) baseView.findViewById(R.id.tv_name_doctor);
			}
			return name;
		}

		public TextView getTitle() {
			if (title == null) {
				title = (TextView) baseView.findViewById(R.id.tv_title_doctor);
			}
			return title;
		}

		public TextView getTopicCount() {
			if (topicCount == null) {
				topicCount = (TextView) baseView.findViewById(R.id.tv_doctor_discuss_count);
			}
			return topicCount;
		}

		public LinearLayout getLayout() {
			if (layout == null) {
				layout = (LinearLayout) baseView.findViewById(R.id.layout);
			}
			return layout;
		}
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

	}

}
