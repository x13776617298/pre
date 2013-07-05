package com.babytree.apps.comm.ui;

import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.comm.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.adapter.WeekTopicListAdapter;
import com.babytree.apps.pregnancy.ui.handler.WeekTopicListHandler;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;

/**
 * 所有该孕月的同龄贴
 */
public class WeekTopicListActivity extends BabytreeTitleAcitivty implements
		OnRefreshListener, OnItemClickListener, OnClickListener {

	private PullToRefreshListView mListView;

	private WeekTopicListHandler mHandler;

	private WeekTopicListAdapter mAdapter;

	private int pregMonth;

	private ArrayList<Base> values;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mListView = (PullToRefreshListView) findViewById(R.id.list);

		pregMonth = getIntent().getIntExtra("week", 0);
		setTitleString("孕" + pregMonth + "月");
		if(pregMonth>10){
			pregMonth = 10 - pregMonth;
		}
		mHandler = new WeekTopicListHandler(this, pregMonth);
		values = mHandler.getValues();
		mAdapter = new WeekTopicListAdapter(mListView, this, R.layout.loading,
				R.layout.reloading, mHandler, values);
		mListView.getRefreshableView().setAdapter(mAdapter);
		mListView.setOnScrollListener(mAdapter);
		mListView.setOnRefreshListener(this);
		mListView.getRefreshableView().setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	public void onRefresh() {
		mHandler.refershTop(System.currentTimeMillis());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}


	@Override
	public void setLeftButton(Button button) {
	}

	@Override
	public void setRightButton(Button button) {
	}

	@Override
	public String getTitleString() {
		return null;
	}

	@Override
	public int getBodyView() {
		return R.layout.week_topic_list_activity;
	}
}
