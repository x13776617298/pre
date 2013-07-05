package com.babytree.apps.comm.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.adapter.HotGroupListAdapter;
import com.babytree.apps.pregnancy.ui.handler.HotGroupListHandler;

public class HotGroupActivity extends BabytreeTitleAcitivty implements OnRefreshListener, OnClickListener,
		OnItemClickListener {

	private PullToRefreshListView mListView;

	private HotGroupListHandler mHandler;

	private HotGroupListAdapter mAdapter;
	private ArrayList<Base> values;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mListView = (PullToRefreshListView) findViewById(R.id.list);
		mHandler = new HotGroupListHandler(this);
		values = mHandler.getValues();
		mAdapter = new HotGroupListAdapter(mListView, this, R.layout.loading, R.layout.reloading, mHandler, values);
		mListView.getRefreshableView().setAdapter(mAdapter);
		mListView.setOnScrollListener(mAdapter);
		mListView.setOnRefreshListener(this);
		mListView.getRefreshableView().setOnItemClickListener(this);

	}

	@Override
	public void onRefresh() {
		mHandler.refershTop(System.currentTimeMillis());
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	public void setLeftButton(Button button) {
	}

	@Override
	public void setRightButton(Button button) {
	}

	@Override
	public String getTitleString() {
		return "推荐圈子";
	}

	@Override
	public int getBodyView() {
		return R.layout.hot_group_activity;
	}

}
