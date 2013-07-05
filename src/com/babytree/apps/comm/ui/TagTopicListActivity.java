package com.babytree.apps.comm.ui;

import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.comm.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.pregnancy.ui.adapter.TagTopicListAdapter;
import com.babytree.apps.pregnancy.ui.handler.TagTopicListHandler;
import com.umeng.analytics.MobclickAgent;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Tag列表
 */
public class TagTopicListActivity extends BabytreeTitleAcitivty implements OnRefreshListener, OnItemClickListener,
		OnClickListener {

	private PullToRefreshListView mListView;

	private TagTopicListHandler mHandler;

	private TagTopicListAdapter mAdapter;

	private String mTag;

	private EditText mEditText;
	private Button btnSure;
	private int num = 0;

	private String loginStr;

	private ArrayList<Base> values;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loginStr = SharedPreferencesUtil.getStringValue(this,ShareKeys.LOGIN_STRING);
		mListView = (PullToRefreshListView) findViewById(R.id.list);

		mTag = getIntent().getStringExtra("tag");
		initData(mTag);

		setTitleString(mTag);
		mEditText = (EditText) findViewById(R.id.main_edit_txt);
		btnSure = (Button) findViewById(R.id.img_search);
		btnSure.setOnClickListener(this);
	}

	private void initData(String mTag) {
		mHandler = new TagTopicListHandler(this, mTag, loginStr);
		values = mHandler.getValues();
		mAdapter = new TagTopicListAdapter(mListView, this, R.layout.loading, R.layout.reloading, mHandler, values);
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
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {

		if (v.getId() == R.id.img_search) {
			String keyWord = mEditText.getText().toString().trim();
			if (!"".equals(keyWord)) {
				// UMENG Event
				MobclickAgent.onEvent(this, EventContants.event_search);
				// setTag(keyWord);
				InputMethodManager imm = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
				imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				if (num >= 0 && num < 10) {
					List<String> sList = new ArrayList<String>();
					for (int i = 0; i < 10; i++) {
						String s = SharedPreferencesUtil.getStringValue(this, "keyword_" + String.valueOf(i));
						if (null != s) {
							sList.add(s);
						}
					}
					if (!sList.contains(keyWord)) {
						SharedPreferencesUtil.setValue(this, "keyword_" + String.valueOf(num), keyWord);
						SharedPreferencesUtil.setValue(this, "search_click_num", num += 1);
					}

				}
				mTag = keyWord;
				setTitleString(mTag);
				initData(mTag);
			} else {
				Toast.makeText(this, R.string.question_can_not_null, Toast.LENGTH_SHORT).show();
				return;
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		num = SharedPreferencesUtil.getIntValue(this, "search_click_num");
		if (num <= -1 || num >= 10) {
			num = 0;
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
		return null;
	}

	@Override
	public int getBodyView() {
		return R.layout.tag_topic_list_activity;
	}
}
