package com.babytree.apps.comm.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.babytree.apps.biz.topicpost.TopicPostNewActivity;
import com.babytree.apps.biz.user.LoginActivity;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.listener.BabytreeOnClickListenner;
import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.adapter.DoctorTopicListAdapter;
import com.babytree.apps.pregnancy.ui.handler.DoctorTopicListHandler;
import com.umeng.analytics.MobclickAgent;

/**
 * 医生话题圈子列表
 */
public class DoctorTopicListActivity extends BabytreeTitleAcitivty implements OnRefreshListener, OnItemClickListener,
		OnClickListener {

	private PullToRefreshListView mListView;

	private DoctorTopicListHandler mHandler;

	private DoctorTopicListAdapter mAdapter;

	private TextView mTxtMessage;

	private ArrayList<Base> values;
	private String mDoctorName = "";
	private String mGroupId;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mDoctorName = getIntent().getStringExtra("doctor_name");
		mGroupId = getIntent().getStringExtra("group_id");
		if (mGroupId == null || mGroupId.equals("")) {
			mGroupId = SharedPreferencesUtil.getStringValue(this, ShareKeys.GROUP_ID);
		}
		mListView = (PullToRefreshListView) findViewById(R.id.list);
		mTxtMessage = (TextView) findViewById(R.id.txt_message);
		StringBuffer sb = new StringBuffer();
		sb.append("有关");
		sb.append(mDoctorName);
		sb.append("医生的讨论");

		setTitleString(sb.toString());

		mHandler = new DoctorTopicListHandler(this, mDoctorName, mGroupId, mTxtMessage, sb.toString());
		values = mHandler.getValues();
		mAdapter = new DoctorTopicListAdapter(mListView, this, R.layout.loading, R.layout.reloading, mHandler, values);
		mListView.getRefreshableView().setAdapter(mAdapter);
		mListView.setOnScrollListener(mAdapter);
		mListView.setOnRefreshListener(this);
		mListView.getRefreshableView().setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	}

	@Override
	public void onRefresh() {
		mHandler.refershTop(System.currentTimeMillis());
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
	}

	@Override
	public void setLeftButton(Button button) {

	}

	@Override
	public void setRightButton(Button button) {
		if (mIsPregnancy) {
			button.setBackgroundResource(R.drawable.y_btn_post);
		} else {
			button.setBackgroundResource(R.drawable.btn_post);
		}
		button.setVisibility(View.VISIBLE);
		button.setOnClickListener(new BabytreeOnClickListenner() {
			@Override
			public void onClick(View v) {

				super.onClick(v);

				// 是否登录
				if (!isLogin()) {

					// 未登录用户跳转到登录页面
					MobclickAgent.onEvent(getBaseContext(), EventContants.com,
							EventContants.communicate_createTopicToLogin);

					Intent intent = new Intent(DoctorTopicListActivity.this, LoginActivity.class);
					intent.putExtra(LoginActivity.BUNDLE_RETURN, TopicPostNewActivity.class);
					intent.putExtra(TopicPostNewActivity.BUNDLE_GROUP_ID, Integer.parseInt(mGroupId));
					intent.putExtra(TopicPostNewActivity.BUNDLE_NAME, mDoctorName);
					intent.putExtra(TopicPostNewActivity.BUNDLE_DOCTOR_NAME, mDoctorName);

					BabytreeUtil.launch(mContext, intent, false, 0);

				} else {

					MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.communicate_createTopic);

					// 登录用户跳转到发帖页面
					TopicPostNewActivity.launch(mContext, Integer.parseInt(mGroupId), null, mDoctorName, mDoctorName,
							null, null, null, false, 0);
				}
			}
		});
	}

	@Override
	public String getTitleString() {
		return null;
	}

	@Override
	public int getBodyView() {
		return R.layout.doctor_topic_list_activity;
	}
}
