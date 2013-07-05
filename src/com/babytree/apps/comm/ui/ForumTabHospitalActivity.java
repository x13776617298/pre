package com.babytree.apps.comm.ui;

import android.app.TabActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.TabWidget;
import android.widget.TextView;

import com.babytree.apps.biz.topicpost.TopicPostNewActivity;
import com.babytree.apps.biz.user.LoginActivity;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 交流首页
 */
public class ForumTabHospitalActivity extends TabActivity implements OnTabChangeListener, OnClickListener,
		OnTouchListener {

	private TabHost mTabHost;

	private TabWidget mTabWidget;

	private LayoutInflater mInflater;

	private TextView textView2;
	private TextView textView3;
	private TextView textView4;
	private TextView textView5;

	private static final int TAB_1 = 0;

	private static final int TAB_2 = 1;

	private static final int TAB_3 = 2;

	private static final int TAB_4 = 3;

	private static final int TAB_5 = 4;

	private MReceiver mReceiver;

	private LinearLayout mMenuLinearLayout;
	protected TextView mTxtTitle;
	protected ImageView mImgIcon;
	protected PopupWindow mMenu;

	private String mBirthday;

	// 二级选择控制title不变的标志
	private IntentFilter filter;

	private String mHospitalName = "";
	private String mHospitalGroupId;
	private String mHospitalId;

	private Button btn_back;
	private FrameLayout fl_title;
	private Button btn_right;
	private ImageView mImageOut;
	private ImageView mImageIn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forum_tab_hospital_activity);
		mImageOut = (ImageView) findViewById(R.id.iv_item_bg_out);
		mImageIn = (ImageView) findViewById(R.id.iv_item_bg_in);
		fl_title = (FrameLayout) findViewById(R.id.title);
		btn_back = (Button) findViewById(R.id.btn_left);
		btn_back.setOnClickListener(this);
		btn_right = (Button) findViewById(R.id.btn_right);
		btn_right.setOnClickListener(this);
		if (BabytreeUtil.isPregnancy(this)) {
			fl_title.setBackgroundResource(R.drawable.y_title_bg);
			btn_back.setBackgroundResource(R.drawable.y_btn_back);
			btn_right.setBackgroundResource(R.drawable.y_btn_post);
			mImageOut.setBackgroundResource(R.drawable.y_ic_item_menu_bg_out);
		}
		mBirthday = getIntent().getStringExtra("birthday");
		findViewById(R.id.btn_left).setOnClickListener(this);
		findViewById(R.id.btn_right).setOnClickListener(this);
		mHospitalName = getIntent().getStringExtra("hospital_name");
		if (mHospitalName == null || mHospitalName.equals("")) {
			mHospitalName = SharedPreferencesUtil.getStringValue(this, ShareKeys.HOSPITAL_NAME);
		}
		mHospitalGroupId = getIntent().getStringExtra("group_id");
		if (mHospitalGroupId == null || mHospitalGroupId.equals("")) {
			mHospitalGroupId = SharedPreferencesUtil.getStringValue(this, ShareKeys.GROUP_ID);
		}
		mHospitalId = getIntent().getStringExtra("hospital_id");
		if (mHospitalId == null || mHospitalId.equals("")) {
			mHospitalId = SharedPreferencesUtil.getStringValue(this, ShareKeys.HOSPITAL_ID);
		}
		mImgIcon = (ImageView) findViewById(R.id.img_icon);
		mImgIcon.setOnClickListener(this);

		mTxtTitle = (TextView) findViewById(R.id.txt_center);
		if (mHospitalName.length() > 7) {
			mHospitalName = mHospitalName.substring(0, 7) + "...";
		}
		mTxtTitle.setText(mHospitalName);

		mTxtTitle.setOnClickListener(this);

		if (mBirthday == null) {
			mBirthday = SharedPreferencesUtil.getStringValue(this, "birthday"); // 201205
		}

		View view = View.inflate(this, R.layout.forum_hospital_menu, null);

		mMenu = new PopupWindow(view, LayoutParams.WRAP_CONTENT, 80);
		mMenuLinearLayout = (LinearLayout) view.findViewById(R.id.layout_city_hospital);
		mMenuLinearLayout.setOnClickListener(this);

		mTabHost = getTabHost();
		mTabHost.setup(getLocalActivityManager());
		mTabWidget = mTabHost.getTabWidget();

		mInflater = LayoutInflater.from(this);
		// 注册监听
		mReceiver = new MReceiver();
		filter = new IntentFilter();
		filter.addAction("change_tab_to_four");
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);

		setTab1();
		setTab2();
		setTab3();
		setTab4();
		setTab5();
		int count = mTabWidget.getChildCount();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenWidth = dm.widthPixels;
		// Log.i("test", "screenWidth=" + screenWidth);
		if (count > 3) {
			for (int i = 0; i < count; i++) {
				mTabWidget.getChildTabViewAt(i).setMinimumWidth((screenWidth) / 5);
			}
		}
		setTab(TAB_2, true);
		mTabHost.setOnTabChangedListener(this);
		mTabHost.setCurrentTab(TAB_2);

	}

	private void setTab1() {
		View view = mInflater.inflate(R.layout.forum_tab_indicator_hospital, null);
		((TextView) view.findViewById(R.id.tab_label)).setText(getResources().getString(R.string.tab_forum_hospital_1));
		Intent newsList = new Intent(this, ForumHospitalForEliteActivity.class);
		newsList.putExtra("is_elite", "1");
		newsList.putExtra("hospital_id", mHospitalId);
		newsList.putExtra("hospital_name", mHospitalName);
		newsList.putExtra("group_id", mHospitalGroupId);
		TabSpec mTabSpec1 = mTabHost.newTabSpec(getResources().getString(R.string.tab_forum_hospital_1));
		mTabSpec1.setIndicator(view);
		mTabSpec1.setContent(newsList);
		mTabHost.addTab(mTabSpec1);
	}

	private void setTab2() {
		View view = mInflater.inflate(R.layout.forum_tab_indicator_hospital, null);
		textView2 = ((TextView) view.findViewById(R.id.tab_label));
		textView2.setText(getResources().getString(R.string.tab_forum_hospital_2));
		Intent newsList = new Intent(this, ForumHospitalActivity.class);
		newsList.putExtra("is_elite", "0");
		newsList.putExtra("hospital_id", mHospitalId);
		newsList.putExtra("hospital_name", mHospitalName);
		newsList.putExtra("group_id", mHospitalGroupId);
		TabSpec mTabSpec2 = mTabHost.newTabSpec(getResources().getString(R.string.tab_forum_hospital_2));
		mTabSpec2.setIndicator(view);
		mTabSpec2.setContent(newsList);
		mTabHost.addTab(mTabSpec2);
	}

	private void setTab3() {
		View view = mInflater.inflate(R.layout.forum_tab_indicator_hospital, null);
		textView3 = ((TextView) view.findViewById(R.id.tab_label));
		textView3.setText(getResources().getString(R.string.tab_forum_hospital_3));
		Intent newsList = new Intent(this, HospitalDoctorListActivity.class);
		newsList.putExtra("is_elite", "0");
		newsList.putExtra("hospital_id", mHospitalId);
		newsList.putExtra("hospital_name", mHospitalName);
		newsList.putExtra("group_id", mHospitalGroupId);
		TabSpec mTabSpec2 = mTabHost.newTabSpec(getResources().getString(R.string.tab_forum_hospital_3));
		mTabSpec2.setIndicator(view);
		mTabSpec2.setContent(newsList);
		mTabHost.addTab(mTabSpec2);
	}

	private void setTab4() {
		View view = mInflater.inflate(R.layout.forum_tab_indicator_hospital, null);
		textView4 = ((TextView) view.findViewById(R.id.tab_label));
		textView4.setText(getResources().getString(R.string.tab_forum_hospital_4));
		Intent newsList = new Intent(this, HospitalMotherListActivity.class);
		newsList.putExtra("is_elite", "0");
		newsList.putExtra("hospital_id", mHospitalId);
		newsList.putExtra("hospital_name", mHospitalName);
		TabSpec mTabSpec2 = mTabHost.newTabSpec(getResources().getString(R.string.tab_forum_hospital_4));
		mTabSpec2.setIndicator(view);
		mTabSpec2.setContent(newsList);
		mTabHost.addTab(mTabSpec2);
	}

	private void setTab5() {
		View view = mInflater.inflate(R.layout.forum_tab_indicator_hospital, null);
		textView5 = ((TextView) view.findViewById(R.id.tab_label));
		textView5.setText(getResources().getString(R.string.tab_forum_hospital_5));
		Intent newsList = new Intent(this, HospitalIntroductionActivity.class);
		newsList.putExtra("is_elite", "0");
		newsList.putExtra("hospital_id", mHospitalId);
		newsList.putExtra("hospital_name", mHospitalName);
		newsList.putExtra("group_id", mHospitalGroupId);
		TabSpec mTabSpec2 = mTabHost.newTabSpec(getResources().getString(R.string.tab_forum_hospital_5));
		mTabSpec2.setIndicator(view);
		mTabSpec2.setContent(newsList);
		mTabHost.addTab(mTabSpec2);
	}

	private void setTab(int id, boolean flag) {
		switch (id) {
		case TAB_1:
			mTabWidget.getChildAt(TAB_1).setBackgroundResource(R.drawable.ic_hospital_forum_selected);
			mTabWidget.getChildAt(TAB_2).setBackgroundResource(R.drawable.ic_hospital_forum_unselected);
			mTabWidget.getChildAt(TAB_3).setBackgroundResource(R.drawable.ic_hospital_forum_unselected);
			mTabWidget.getChildAt(TAB_4).setBackgroundResource(R.drawable.ic_hospital_forum_unselected);
			mTabWidget.getChildAt(TAB_5).setBackgroundResource(R.drawable.ic_hospital_forum_unselected);
			break;
		case TAB_2:
			mTabWidget.getChildAt(TAB_1).setBackgroundResource(R.drawable.ic_hospital_forum_unselected2);
			mTabWidget.getChildAt(TAB_2).setBackgroundResource(R.drawable.ic_hospital_forum_selected2);
			mTabWidget.getChildAt(TAB_3).setBackgroundResource(R.drawable.ic_hospital_forum_unselected);
			mTabWidget.getChildAt(TAB_4).setBackgroundResource(R.drawable.ic_hospital_forum_unselected);
			mTabWidget.getChildAt(TAB_5).setBackgroundResource(R.drawable.ic_hospital_forum_unselected);
			break;
		case TAB_3:
			mTabWidget.getChildAt(TAB_1).setBackgroundResource(R.drawable.ic_hospital_forum_unselected2);
			mTabWidget.getChildAt(TAB_3).setBackgroundResource(R.drawable.ic_hospital_forum_selected2);
			mTabWidget.getChildAt(TAB_2).setBackgroundResource(R.drawable.ic_hospital_forum_unselected);
			mTabWidget.getChildAt(TAB_4).setBackgroundResource(R.drawable.ic_hospital_forum_unselected);
			mTabWidget.getChildAt(TAB_5).setBackgroundResource(R.drawable.ic_hospital_forum_unselected);
			break;
		case TAB_4:
			mTabWidget.getChildAt(TAB_1).setBackgroundResource(R.drawable.ic_hospital_forum_unselected2);
			mTabWidget.getChildAt(TAB_4).setBackgroundResource(R.drawable.ic_hospital_forum_selected2);
			mTabWidget.getChildAt(TAB_2).setBackgroundResource(R.drawable.ic_hospital_forum_unselected);
			mTabWidget.getChildAt(TAB_3).setBackgroundResource(R.drawable.ic_hospital_forum_unselected);
			mTabWidget.getChildAt(TAB_5).setBackgroundResource(R.drawable.ic_hospital_forum_unselected);
			break;
		case TAB_5:
			mTabWidget.getChildAt(TAB_1).setBackgroundResource(R.drawable.ic_hospital_forum_unselected2);
			mTabWidget.getChildAt(TAB_5).setBackgroundResource(R.drawable.ic_hospital_forum_selected2);
			mTabWidget.getChildAt(TAB_2).setBackgroundResource(R.drawable.ic_hospital_forum_unselected);
			mTabWidget.getChildAt(TAB_4).setBackgroundResource(R.drawable.ic_hospital_forum_unselected);
			mTabWidget.getChildAt(TAB_3).setBackgroundResource(R.drawable.ic_hospital_forum_unselected);
			break;

		default:
			break;
		}
	}

	@Override
	public void onTabChanged(String tabId) {
		if (tabId.equalsIgnoreCase(getResources().getString(R.string.tab_forum_hospital_1))) {
			// Umeng Evert
			MobclickAgent.onEvent(getBaseContext(), EventContants.quanzijingxuan);
			setTab(TAB_1, true);
		} else if (tabId.equalsIgnoreCase(getResources().getString(R.string.tab_forum_hospital_2))) {
			// Umeng Evert
			MobclickAgent.onEvent(getBaseContext(), EventContants.quanzijiaoliu);
			setTab(TAB_2, true);
		} else if (tabId.equalsIgnoreCase(getResources().getString(R.string.tab_forum_hospital_3))) {
			// Umeng Evert
			MobclickAgent.onEvent(getBaseContext(), EventContants.yishengtaolun);
			setTab(TAB_3, true);
		} else if (tabId.equalsIgnoreCase(getResources().getString(R.string.tab_forum_hospital_4))) {
			// Umeng Evert
			MobclickAgent.onEvent(getBaseContext(), EventContants.tongyuanyunma);
			setTab(TAB_4, true);
		} else if (tabId.equalsIgnoreCase(getResources().getString(R.string.tab_forum_hospital_5))) {
			// Umeng Evert
			MobclickAgent.onEvent(getBaseContext(), EventContants.yiyuanjieshao);
			setTab(TAB_5, true);
		}
	}

	public class MReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals("change_tab_to_four")) {
				setTab(TAB_4, true);
				mTabHost.setCurrentTab(TAB_4);
			}
		}

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (mMenu != null && mMenu.isShowing()) {
			mMenu.dismiss();
			mImageIn.setVisibility(View.VISIBLE);
		}

		return super.onTouchEvent(event);

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		if (mMenu != null && mMenu.isShowing()) {
			mMenu.dismiss();
			mImageIn.setVisibility(View.VISIBLE);
		}

		return false;
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);

		if (mMenu.isShowing()) {
			mMenu.dismiss();
			mImageIn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	}

	/**
	 * 
	 * @param context
	 * @param hospitalId
	 *            医院id
	 * @param hospitalName
	 *            医院名字
	 * @param groupId
	 *            圈子id
	 */
	public static void launch(Context context, String hospitalId, String hospitalName, String groupId) {
		Intent intent = new Intent(context, ForumTabHospitalActivity.class);
		intent.putExtra("hospital_id", hospitalId);
		intent.putExtra("hospital_name", hospitalName);
		intent.putExtra("group_id", groupId);
		context.startActivity(intent);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			// 是否登录
			if (!BabytreeUtil.isLogin(this)) {
				MobclickAgent
						.onEvent(getBaseContext(), EventContants.com, EventContants.communicate_createTopicToLogin);
				Intent intent = new Intent(this, LoginActivity.class);
				intent.putExtra(LoginActivity.BUNDLE_RETURN, TopicPostNewActivity.class);
				intent.putExtra(TopicPostNewActivity.BUNDLE_GROUP_ID, Integer.parseInt(mHospitalGroupId));
				intent.putExtra(TopicPostNewActivity.BUNDLE_NAME, mHospitalName);
				startActivity(intent);
			} else {

				MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.communicate_createTopic);

				// 跳转到发新帖页面
				TopicPostNewActivity.launch(this, Integer.parseInt(mHospitalGroupId), null, mHospitalName, null, null,
						null, null, false, 0);

			}
			break;
		case R.id.img_icon:
		case R.id.txt_center:
			if (mMenu.isShowing()) {
				mMenu.dismiss();
				mImageIn.setVisibility(View.VISIBLE);
			} else {
				mMenu.showAsDropDown(mImageOut, 2, -8);
				mImageIn.setVisibility(View.INVISIBLE);
			}
			break;
		case R.id.layout_city_hospital:
			mImageIn.setVisibility(View.VISIBLE);
			Intent intent = new Intent(this, HospitalsInfoListActivity.class);
			intent.putExtra("key", "");
			intent.putExtra("hospital_id", mHospitalId);
			intent.putExtra("fromUserCenter", true);
			startActivity(intent);
			break;

		}
	}
}
