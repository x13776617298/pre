package com.babytree.apps.comm.ui;

import java.util.ArrayList;

import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.ui.AboutMikaActivity;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.ui.adapter.CommentAdapter;
import com.babytree.apps.comm.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.handler.CommentHandler;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MiKaCommentListActivity extends BabytreeTitleAcitivty implements
		OnRefreshListener, OnItemClickListener, OnClickListener {
	private PullToRefreshListView mListView;

	private CommentHandler mHandler;

	private CommentAdapter mAdapter;

	private ArrayList<Base> values;
	
	private ImageView mImgService;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.iv_mika_service).setOnClickListener(this);
		mImgService = (ImageView) findViewById(R.id.iv_mika_service);
		mImgService.setVisibility(View.VISIBLE);
		View view = View.inflate(this, R.layout.mika_comment_list_head, null);
		view.findViewById(R.id.iv_more_info_mika).setOnClickListener(this);
		mListView = (PullToRefreshListView) findViewById(R.id.list);
		mListView.getRefreshableView().addHeaderView(view);
		mHandler = new CommentHandler(this, null);
		values = mHandler.getValues();
		mAdapter = new CommentAdapter(mListView, this, R.layout.loading,
				R.layout.reloading, mHandler,values);
		mListView.getRefreshableView().setAdapter(mAdapter);
		mListView.setOnScrollListener(mAdapter);
		mListView.setOnRefreshListener(this);
		mListView.getRefreshableView().setOnItemClickListener(this);
	}
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRefresh() {
	    mHandler.refershTop(System.currentTimeMillis());
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.iv_more_info_mika:
			startActivity(new Intent(this, AboutMikaActivity.class).putExtra("url", "http://www.mika123.com")
					.putExtra("title", "米卡官网"));
			break;
		case R.id.iv_mika_service:
			 new AlertDialog
             .Builder(this)
             .setTitle("确认拨出电话吗？")
             .setPositiveButton("是", new DialogInterface.OnClickListener(){
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
         			Intent intent = new Intent();
         			// 系统默认的action，用来打开默认的电话界面
         			intent.setAction(Intent.ACTION_CALL);
         			intent.setData(Uri.parse("tel:" + AboutMikaActivity.SERVICE_TELEPHONE));
         			startActivity(intent);
                 }
             })
             .setNegativeButton("否", new DialogInterface.OnClickListener(){
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                 }
             })
             .show();
			break;
		}

	}

	@Override
	public void setLeftButton(Button button) {
	}

	@Override
	public void setRightButton(Button button) {
	}

	@Override
	public String getTitleString() {
		// TODO Auto-generated method stub
		return "用户点评";
	}

	@Override
	public int getBodyView() {
		// TODO Auto-generated method stub
		return R.layout.mika_comment_list_activity;
	}
}
