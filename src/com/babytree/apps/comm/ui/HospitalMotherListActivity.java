package com.babytree.apps.comm.ui;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.babytree.apps.biz.topicpost.TopicPostNewActivity;
import com.babytree.apps.biz.user.LoginActivity;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.comm.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.adapter.HospitalMotherListAdapter;
import com.babytree.apps.pregnancy.ui.handler.HospitalMotherListHandler;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.AdapterView.OnItemClickListener;

public class HospitalMotherListActivity extends BabytreeActivity implements OnRefreshListener, OnItemClickListener,
		OnClickListener, OnTouchListener {
	private PullToRefreshListView mListView;

	private HospitalMotherListHandler mHandler;

	private HospitalMotherListAdapter mAdapter;

	private int mGroupId;

	private String mBirthday;

	private int mType = 0;

	private ArrayList<Base> values;

	// 医院圈子
	private String mHospitalId;
	private String mHospitalName;
	private ImageView mImgSinaShare, mImgTencShare, mImgQQzoneShare, mImgSmsShare;
	private Button mbtnSinaShare, mbtnTencShare, mbtnQQzoneShare, mbtnSmsShare;
	private StringBuffer stringBuffer = new StringBuffer();
	private String content = "";

	// 我在@快乐孕期 加入了#海淀妇幼保健院#圈子 想了解@海淀妇幼保健院滴准妈妈一起来吧.http://t.cn/zl3WF0e
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.forum_hospital_for_mother_activity_new);
		mHospitalName = getIntent().getStringExtra("hospital_name");
		if (mHospitalName == null || mHospitalName.equals("")) {
			mHospitalName = SharedPreferencesUtil.getStringValue(this, "hospital_name");
		}
		stringBuffer.append("我在宝宝树 @快乐孕期 加入了#");
		stringBuffer.append(mHospitalName);
		stringBuffer.append("#圈子 想了解@");
		stringBuffer.append(mHospitalName);
		stringBuffer.append(" 滴准妈妈一起来吧.http://t.cn/zl3WF0e");
		content = stringBuffer.toString();
		mImgSinaShare = (ImageView) findViewById(R.id.img_sina_share);
		mImgSinaShare.setOnClickListener(this);
		mImgTencShare = (ImageView) findViewById(R.id.img_tenc_share);
		mImgTencShare.setOnClickListener(this);
		mImgQQzoneShare = (ImageView) findViewById(R.id.img_qqzone_share);
		mImgQQzoneShare.setOnClickListener(this);
		mImgSmsShare = (ImageView) findViewById(R.id.img_sms_share);
		mImgSmsShare.setOnClickListener(this);

		// 孕妈圈子分享
		mbtnSinaShare = (Button) findViewById(R.id.img_sina_share1);
		mbtnSinaShare.setOnClickListener(this);
		mbtnTencShare = (Button) findViewById(R.id.img_tenc_share1);
		mbtnTencShare.setOnClickListener(this);
		mbtnQQzoneShare = (Button) findViewById(R.id.img_qqzone_share1);
		mbtnQQzoneShare.setOnClickListener(this);
		mbtnSmsShare = (Button) findViewById(R.id.img_sms_share1);
		mbtnSmsShare.setOnClickListener(this);

		mListView = (PullToRefreshListView) findViewById(R.id.list);

		mHospitalId = getIntent().getStringExtra("hospital_id");
		if (mHospitalId == null || mHospitalId.equals("")) {
			mHospitalId = SharedPreferencesUtil.getStringValue(this, "hospital_id");
		}
		mHandler = new HospitalMotherListHandler(this, mHospitalId);
		values = mHandler.getValues();
		mAdapter = new HospitalMotherListAdapter(mListView, this, R.layout.loading, R.layout.reloading, mHandler,
				values);
		mListView.getRefreshableView().setAdapter(mAdapter);
		mListView.getRefreshableView().setOnScrollListener(mAdapter);
		mListView.getRefreshableView().setOnTouchListener(this);
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

	// ==========UMENG Begin===========
	@Override
	protected void onResume() {
		super.onResume();
		ForumHospitalActivity.setListViewFullScreen(this, mListView, values, 3);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.btn_left) {
			finish();
		} else if (v.getId() == R.id.btn_right) {
			// 是否登录
			if (!isLogin()) {
				Intent intent = new Intent(this, LoginActivity.class);
				intent.putExtra(LoginActivity.BUNDLE_RETURN, TopicPostNewActivity.class);
				intent.putExtra(TopicPostNewActivity.BUNDLE_GROUP_ID, mGroupId);
				intent.putExtra(TopicPostNewActivity.BUNDLE_BIRTHDAY, mBirthday);
				startActivity(intent);
			} else {
				
				// 跳转到发新帖页面
				TopicPostNewActivity.launch(mContext, mGroupId, mBirthday, null, null, null, null, null, false, 0);
			}
		} else if (v.getId() == R.id.img_sina_share || v.getId() == R.id.img_sina_share1) {
			MobclickAgent.onEvent(getBaseContext(), EventContants.sina);
			mApplication.getUmSocialService().setShareImage(null);
			mApplication.getUmSocialService().setShareContent(content);
			mApplication.getUmSocialService().directShare(this, SHARE_MEDIA.SINA, null);
		} else if (v.getId() == R.id.img_tenc_share || v.getId() == R.id.img_tenc_share1) {
			MobclickAgent.onEvent(getBaseContext(), EventContants.tencent);
			mApplication.getUmSocialService().setShareImage(null);
			mApplication.getUmSocialService().setShareContent(content);
			mApplication.getUmSocialService().directShare(this, SHARE_MEDIA.TENCENT, null);
		} else if (v.getId() == R.id.img_qqzone_share || v.getId() == R.id.img_qqzone_share1) {
			MobclickAgent.onEvent(getBaseContext(), EventContants.Qzone);
			mApplication.getUmSocialService().setShareImage(null);
			mApplication.getUmSocialService().setShareContent(content);
			mApplication.getUmSocialService().directShare(this, SHARE_MEDIA.RENREN, null);
		} else if (v.getId() == R.id.img_sms_share || v.getId() == R.id.img_sms_share1) {
			MobclickAgent.onEvent(getBaseContext(), EventContants.duanxin);
			mApplication.getUmSocialService().shareSms(this, content);
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
			// flag = true;
			c1.setTimeInMillis(l2);
			c2.setTimeInMillis(l1);
		} else {
			// flag = false;
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
