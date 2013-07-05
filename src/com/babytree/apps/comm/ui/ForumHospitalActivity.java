package com.babytree.apps.comm.ui;

import com.babytree.apps.biz.topicpost.TopicPostNewActivity;
import com.babytree.apps.biz.user.LoginActivity;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.ctr.HospitalController;
import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.model.UserCountInfo;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.comm.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.adapter.ForumHospitalAdapter;
import com.babytree.apps.pregnancy.ui.handler.ForumHospitalHandler;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * 医院圈子页
 */
public class ForumHospitalActivity extends BabytreeActivity implements OnRefreshListener, OnItemClickListener,
		OnClickListener, OnTouchListener {

	private PullToRefreshListView mListView;

	private ForumHospitalHandler mHandler;

	private ForumHospitalAdapter mAdapter;

	private int mGroupId;

	private String mBirthday;

	private String mName;

	private int mType = 0;

	TextView date_baby;// 孕几月

	private ArrayList<Base> values;

	// 医院圈子
	private String mhospitalId;
	private String mGroupIdNew;
	private String mIsElite;

	private RelativeLayout bottomLayout;// 最底下跳转孕妈的信息栏
	private LinearLayout close;
	private TextView countTxt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forum_hospital_activity_new);
		mListView = (PullToRefreshListView) findViewById(R.id.list);

		mIsElite = getIntent().getStringExtra("is_elite");
		mName = getIntent().getStringExtra("hospital_name");
		if (mName == null || mName.equals("")) {
			mName = SharedPreferencesUtil.getStringValue(this, "hospital_name");
		}
		mGroupIdNew = getIntent().getStringExtra("group_id");
		if (mGroupIdNew == null || mGroupIdNew.equals("")) {
			mGroupIdNew = SharedPreferencesUtil.getStringValue(this, ShareKeys.GROUP_ID);
		}
		mhospitalId = getIntent().getStringExtra("hospital_id");
		if (mhospitalId == null || mhospitalId.equals("")) {
			mhospitalId = SharedPreferencesUtil.getStringValue(this, "hospital_id");
		}
		mHandler = new ForumHospitalHandler(this, mGroupIdNew, mIsElite);
		values = mHandler.getValues();
		mAdapter = new ForumHospitalAdapter(mListView, this, R.layout.loading, R.layout.reloading, mHandler, values);
		mListView.getRefreshableView().setAdapter(mAdapter);
		mListView.getRefreshableView().setOnScrollListener(mAdapter);
		mListView.getRefreshableView().setOnTouchListener(this);
		mListView.setOnRefreshListener(this);
		mListView.getRefreshableView().setOnItemClickListener(this);

		bottomLayout = (RelativeLayout) findViewById(R.id.rl_skip);
		close = (LinearLayout) findViewById(R.id.ll_close);
		countTxt = (TextView) findViewById(R.id.tv_countInfo);
		if (mIsElite.equals("0")) {
			// mEmptyLayout.setBackgroundResource(R.drawable.ic_no_hospital_topic);
			countTxt.setOnClickListener(this);
			close.setOnClickListener(this);
			if (mhospitalId != null)
				initUserCount(mhospitalId);
		}

	}

	private void initUserCount(final String hospitalId) {
		final Handler myHandler = new Handler() {

			public void handleMessage(android.os.Message msg) {

				if (msg.what == 1) {
					DataResult result = (DataResult) msg.obj;
					if (result.data != null) {
						bottomLayout.setVisibility(View.VISIBLE);
						UserCountInfo countInfo = (UserCountInfo) result.data;
						if (countInfo.hospital_prenant_count.equals("0")) {
							countTxt.setText("抢坐第一个孕妈宝座邀请好友得大奖");
						} else {
							countTxt.setText("有" + countInfo.hospital_prenant_count + "个孕妈在这里哦，快看看都有谁吧！");
						}
					} else {
						countTxt.setText("抢坐第一个孕妈宝座邀请好友得大奖");
					}
				}
			};
		};
		new Thread() {
			@Override
			public void run() {
				super.run();
				DataResult result = new DataResult();
				if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(ForumHospitalActivity.this)) {
					String loginString = SharedPreferencesUtil.getStringValue(getApplicationContext(),
							ShareKeys.LOGIN_STRING);
					result = HospitalController.getUserCount(loginString, hospitalId);
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	}

	public static void setListViewFullScreen(Activity activity, PullToRefreshListView pullListView,
			ArrayList<Base> values, int size) {
		FrameLayout.LayoutParams linearParams = (FrameLayout.LayoutParams) pullListView.getRefreshableView()
				.getLayoutParams();
		if (values.size() > size) {
			linearParams.height = FrameLayout.LayoutParams.WRAP_CONTENT;
		} else {
			linearParams.height = com.babytree.apps.comm.util.BabytreeUtil.getScreenHeight(activity);
		}
		pullListView.getRefreshableView().setLayoutParams(linearParams);
	}

	@Override
	public void onRefresh() {
		mHandler.refershTop(System.currentTimeMillis());
	}

	@Override
	protected void onResume() {
		super.onResume();
		setListViewFullScreen(this, mListView, values, 4);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_left) {
			finish();
		} else if (v.getId() == R.id.btn_right) {
			// 是否登录
			if (!isLogin()) {
				Intent intent = new Intent(this, LoginActivity.class);
				intent.putExtra(LoginActivity.BUNDLE_RETURN, TopicPostNewActivity.class);
				intent.putExtra(TopicPostNewActivity.BUNDLE_GROUP_ID, mGroupId);
				intent.putExtra(TopicPostNewActivity.BUNDLE_NAME, mName);
				intent.putExtra(TopicPostNewActivity.BUNDLE_BIRTHDAY, mBirthday);
				startActivity(intent);
			} else {
				
				// 跳转到发新帖页面
				TopicPostNewActivity.launch(mContext, mGroupId, mBirthday, mName, null, null, null, null, false, 0);
			}

		} else if (v == countTxt) {
			MobclickAgent.onEvent(getBaseContext(), EventContants.clickbanner);
			Intent mIntent = new Intent();
			mIntent.setAction("change_tab_to_four");
			mIntent.setPackage(getPackageName());
			LocalBroadcastManager.getInstance(this).sendBroadcast(mIntent);
		} else if (v == close) {
			bottomLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return super.onTouchEvent(event);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
		}
	}

	/**
	 * 加上之间隔多少 天
	 * 
	 * @param l1
	 * @param l2
	 * @return
	 * @throws ParseException
	 */
	public int getBetweenMonth(long l1, long l2) throws ParseException {
		int betweenDays = 0;
		Calendar c1 = Calendar.getInstance(Locale.CHINA);
		Calendar c2 = Calendar.getInstance(Locale.CHINA);
		c1.setTimeInMillis(l1);
		c2.setTimeInMillis(l2);
		// 保证第二个时间一定大于第一个时间
		if (c1.after(c2)) {
			c1.setTimeInMillis(l2);
			c2.setTimeInMillis(l1);
		} else {
		}
		int betweenYears = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
		betweenDays = c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR);
		for (int i = 0; i < betweenYears; i++) {
			c1.set(Calendar.YEAR, (c1.get(Calendar.YEAR) + 1));
			betweenDays += c1.getMaximum(Calendar.DAY_OF_YEAR);
		}
		return betweenDays;
	}
}
