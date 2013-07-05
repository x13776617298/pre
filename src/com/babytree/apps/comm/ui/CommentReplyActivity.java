package com.babytree.apps.comm.ui;

import java.util.ArrayList;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.adapter.CommentReplyListAdapter;
import com.babytree.apps.pregnancy.ui.handler.CommentReplyListHandler;

/**
 * 评论回复
 * 
 * @author wangbingqi
 * 
 */
public class CommentReplyActivity extends BabytreeTitleAcitivty implements OnRefreshListener, OnClickListener,
		OnItemClickListener {
	private PullToRefreshListView mListView;

	private CommentReplyListHandler mHandler;

	private CommentReplyListAdapter mAdapter;
	private ArrayList<Base> values;
	private String loginStr;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loginStr = SharedPreferencesUtil.getStringValue(this, ShareKeys.LOGIN_STRING);

		mListView = (PullToRefreshListView) findViewById(R.id.list);
		mHandler = new CommentReplyListHandler(this, loginStr);
		values = mHandler.getValues();
		mAdapter = new CommentReplyListAdapter(mListView, this, R.layout.loading, R.layout.reloading, mHandler, values);
		mListView.getRefreshableView().setAdapter(mAdapter);
		mListView.setOnScrollListener(mAdapter);
		mListView.setOnRefreshListener(this);
		mListView.getRefreshableView().setOnItemClickListener(this);
		mListView.getRefreshableView().setSelector(R.drawable.no_item_click);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	@Override
	public void onRefresh() {
		if(mHandler != null){
			mHandler.refershTop(System.currentTimeMillis());
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
		return "评论回复";
	}

	@Override
	public int getBodyView() {
		return R.layout.comment_reply_activity;
	}

}
