package com.babytree.apps.comm.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.babytree.apps.biz.topicpost.TopicPostNewActivity;
import com.babytree.apps.biz.user.LoginActivity;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.adapter.ForumAdapter;
import com.babytree.apps.pregnancy.ui.handler.ForumHandler;

/**
 * 圈子页
 */
public class ForumActivity extends BabytreeActivity implements OnRefreshListener, OnItemClickListener, OnClickListener,
		OnTouchListener {

	private PullToRefreshListView mListView;

	private ForumHandler mHandler;

	private ForumAdapter mAdapter;

	private int mGroupId;

	private PopupWindow mMenu;

	private TextView mTxtTitle;

	private TextView mTxtMessage;

	private String mBirthday;

	private String mName;

	private ImageView mImgIcon;

	private ImageView mImageOut;

	private ImageView mImageIn;

	private int mType = 0;

	private boolean flag = false;// 判断是孕期还是出生

	TextView date_baby;// 孕几月
	private long dateData;
	private int hasDaysNum;
	private int month;// 孕多少月了
	private int preg_month;// +10~~~~-12，正：未出生，负:出生，传递的参数
	private String title_pregmonth = "精华帖";

	// private long mCurrentTime = -1;
	private String loginStr;

	private ArrayList<Base> values;

	private Button btn_back;
	private FrameLayout fl_title;
	private Button btn_right;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forum_activity);
		loginStr = SharedPreferencesUtil.getStringValue(this, ShareKeys.LOGIN_STRING);
		mListView = (PullToRefreshListView) findViewById(R.id.list);
		mImageOut = (ImageView) findViewById(R.id.iv_item_bg_out);
		mImageIn = (ImageView) findViewById(R.id.iv_item_bg_in);
		btn_back = (Button) findViewById(R.id.btn_left);
		btn_back.setOnClickListener(this);
		btn_right = (Button) findViewById(R.id.btn_right);
		btn_right.setOnClickListener(this);
		fl_title = (FrameLayout) findViewById(R.id.title);
		if (com.babytree.apps.comm.util.BabytreeUtil.isPregnancy(this)) {
			fl_title.setBackgroundResource(R.drawable.y_title_bg);
			btn_back.setBackgroundResource(R.drawable.y_btn_back);
			btn_right.setBackgroundResource(R.drawable.y_btn_post);
			mImageOut.setBackgroundResource(R.drawable.y_ic_item_menu_bg_out);
		}
		mTxtTitle = (TextView) findViewById(R.id.txt_center);
		mTxtMessage = (TextView) findViewById(R.id.txt_message);
		mImgIcon = (ImageView) findViewById(R.id.img_icon);
		mTxtTitle.setOnClickListener(this);
		mImgIcon.setOnClickListener(this);
		View view = View.inflate(this, R.layout.forum_menu, null);
		mMenu = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mBirthday = getIntent().getStringExtra("birthday");
		mType = getIntent().getIntExtra("type", 0);
		if (mType == 0) {
			if (mBirthday == null) {
				mGroupId = getIntent().getIntExtra("group_id", 0);
				mName = getIntent().getStringExtra("name");
				mBirthday = SharedPreferencesUtil.getStringValue(this, "birthday"); // 201205
			}
		} else {
			mBirthday = getIntent().getStringExtra("birthday");
		}
		if (mGroupId != 0) {
			mBirthday = null;
		} else {
			try {
				String tempName = mBirthday.substring(0, 4) + "年" + mBirthday.substring(4, mBirthday.length()) + "月";
				mName = tempName + "同龄圈";
			} catch (Exception e) {
				mName = "我的同龄圈";
			}
		}
		mHandler = new ForumHandler(this, mGroupId, mTxtMessage, "last_response_ts", false, mBirthday, mName, loginStr);
		values = mHandler.getValues();
		mAdapter = new ForumAdapter(mListView, this, R.layout.loading, R.layout.reloading, mHandler, values);
		mListView.getRefreshableView().setAdapter(mAdapter);
		mListView.setOnScrollListener(mAdapter);
		mListView.setOnTouchListener(this);
		mListView.setOnRefreshListener(this);
		mListView.getRefreshableView().setOnScrollListener(mAdapter);
		mListView.getRefreshableView().setOnItemClickListener(this);

		view.findViewById(R.id.layout_sort_response).setOnClickListener(this);
		view.findViewById(R.id.layout_sort_create).setOnClickListener(this);
		view.findViewById(R.id.layout_sort_elite).setOnClickListener(this);
		view.findViewById(R.id.layout_sort_datebaby_elite).setOnClickListener(this);
		view.findViewById(R.id.layout_sort_location).setOnClickListener(this);
		date_baby = (TextView) view.findViewById(R.id.date_baby);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onRefresh() {
		mHandler.refershTop(System.currentTimeMillis());
	}

	@Override
	protected void onResume() {
		super.onResume();

		dateData = SharedPreferencesUtil.getLongValue(this, "birthday_timestamp");
		try {
			hasDaysNum = getBetweenMonth(Calendar.getInstance(Locale.CHINA).getTimeInMillis(), dateData);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		if (!flag) {
			month = ((int) ((280 - hasDaysNum) / 7 + 1)) / 4;
			if (month <= 0) {
				month = 1;
			}
			if (month > 10) {
				month = 10;
			}
			preg_month = month;
			date_baby.setVisibility(View.VISIBLE);
			title_pregmonth = "孕" + month + "月精华帖";
			date_baby.setText("孕" + month);
		} else {
			date_baby.setVisibility(View.VISIBLE);
			month = hasDaysNum / 30;
			if (month > 12) {
				month = 12;
			}
			preg_month = -month;
			title_pregmonth = month + "月精华帖";
			date_baby.setText(month + "");
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mMenu.isShowing()) {
			mMenu.dismiss();
			mImageIn.setVisibility(View.VISIBLE);
		}
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
		} else if (v.getId() == R.id.txt_center || v.getId() == R.id.img_icon) {
			if (mMenu.isShowing()) {
				mMenu.dismiss();
				mImageIn.setVisibility(View.VISIBLE);
			} else {
				mMenu.showAsDropDown(mImageOut, 2, -8);
				mImageIn.setVisibility(View.INVISIBLE);
			}
		} else if (v.getId() == R.id.layout_sort_response) {
			// 最后回复
			mHandler.refersh("last_response_ts", false, 0, 0, mBirthday);
			mTxtTitle.setText("最后回复");
			mMenu.dismiss();
			mImageIn.setVisibility(View.VISIBLE);
		} else if (v.getId() == R.id.layout_sort_create) {
			// 最新发表
			mHandler.refersh("create_ts", false, 0, 0, mBirthday);
			mTxtTitle.setText("最新发布");
			mMenu.dismiss();
			mImageIn.setVisibility(View.VISIBLE);
		} else if (v.getId() == R.id.layout_sort_elite) {
			// 精华帖
			mHandler.refersh(null, true, 0, 0, mBirthday);
			mTxtTitle.setText("精华帖");
			mMenu.dismiss();
			mImageIn.setVisibility(View.VISIBLE);
		} else if (v.getId() == R.id.layout_sort_datebaby_elite) {
			// 孕几月精华贴
			mHandler.refersh(null, true, 0, 0, mBirthday, String.valueOf(preg_month));
			mTxtTitle.setText(title_pregmonth);
			mMenu.dismiss();
			mImageIn.setVisibility(View.VISIBLE);
		} else if (v.getId() == R.id.layout_sort_location) {
			mMenu.dismiss();
			mImageIn.setVisibility(View.VISIBLE);
			Intent intent = new Intent(this, LocationActivity.class);
			startActivityForResult(intent, 0);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			int _id = data.getIntExtra("_id", 0);
			String type = data.getStringExtra("type");
			String name = data.getStringExtra("name");
			if (type.equals("city")) {
				mHandler.refersh(null, false, 0, _id, mBirthday);
			} else if (type.equals("province")) {
				mHandler.refersh(null, false, _id, 0, mBirthday);
			}
			mTxtTitle.setText(name);
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
			flag = true;
			c1.setTimeInMillis(l2);
			c2.setTimeInMillis(l1);
		} else {
			flag = false;
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
