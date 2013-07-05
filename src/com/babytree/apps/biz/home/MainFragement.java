package com.babytree.apps.biz.home;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Gallery;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.biz.father.FatherIntrActivity;
import com.babytree.apps.biz.father.ctr.FatherController;
import com.babytree.apps.biz.father.model.BindStatus;
import com.babytree.apps.biz.father.ui.FatherTitleBar;
import com.babytree.apps.biz.father.ui.PregnancyTipView;
import com.babytree.apps.biz.father.util.Utils;
import com.babytree.apps.biz.home.ad.PagerControl;
import com.babytree.apps.biz.home.ad.ScrollPager;
import com.babytree.apps.biz.home.ad.ScrollPager.OnCreateChildView;
import com.babytree.apps.biz.home.ad.ScrollPager.OnScrollListener;
import com.babytree.apps.biz.home.adapter.HomeGridAdapter;
import com.babytree.apps.biz.home.adapter.HomeListAdapter;
import com.babytree.apps.biz.home.ctr.HomeController;
import com.babytree.apps.biz.home.model.Banner;
import com.babytree.apps.biz.home.model.Commend;
import com.babytree.apps.biz.home.ui.MommyMenuFragment;
import com.babytree.apps.biz.knowledge.InformationActivity;
import com.babytree.apps.biz.knowledge.InformationDetailActivity;
import com.babytree.apps.biz.knowledge.KitchenActivity;
import com.babytree.apps.biz.knowledge.RemindActivity;
import com.babytree.apps.biz.knowledge.Y_KitchenActivity;
import com.babytree.apps.biz.knowledge.Y_KnowledgeActivity;
import com.babytree.apps.biz.knowledge.Y_RemindActivity;
import com.babytree.apps.biz.knowledge.Y_RemindDetailActivity;
import com.babytree.apps.biz.knowledge.ctr.Y_CalendarDbController;
import com.babytree.apps.biz.knowledge.model.Y_Knowledge;
import com.babytree.apps.biz.topic.TopicNewActivity;
import com.babytree.apps.biz.user.LoginActivity;
import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.config.UrlConstrants;
import com.babytree.apps.comm.ctr.BabytreeController;
import com.babytree.apps.comm.ctr.BaseController;
import com.babytree.apps.comm.ctr.MessageController;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.ctr.SignInController;
import com.babytree.apps.comm.model.Discuz;
import com.babytree.apps.comm.model.InOutBox;
import com.babytree.apps.comm.model.UserAddInfo;
import com.babytree.apps.comm.net.BabytreeAsyncTask;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.ui.ForecastActivity;
import com.babytree.apps.comm.ui.ForumTabActivity;
import com.babytree.apps.comm.ui.ForumTabHospitalActivity;
import com.babytree.apps.comm.ui.HospitalsInfoListActivity;
import com.babytree.apps.comm.ui.LocationList3Activity;
import com.babytree.apps.comm.ui.MikaActivity;
import com.babytree.apps.comm.ui.SignInActivity;
import com.babytree.apps.comm.ui.activity.BabyTreeWebviewActivity;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.comm.view.BabyTreeFragment;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * 孕期-育儿公共的主页主页-Fragement
 * 
 * @author pengxh
 * 
 */
@SuppressLint("ValidFragment")
public class MainFragement extends BabyTreeFragment implements OnClickListener {
	private static final String TAG = MainFragement.class.getSimpleName();

	private BabytreeBitmapCache bitmapCache;// 缓存对象

	private static final int KNOWLEDGE_TOTAL_DAYS = 364;

	private Y_CalendarDbController mDbController;

	private int hasDaysNum;

	private long birthdayTimestamp;

	private String imagePath = "";

	private String loginStr = "";

	/**
	 * 小孩生日(格式:201201)
	 */
	private String mBirthday;

	private Y_Knowledge bean;

	private ProgressDialog mDialog;

	// private TextView birthdayTxt;

	private boolean isChoiceHospital;
	private String hospitalName, positionName, hospitalId, groupId;
	private boolean isHospitalNotice = false;
	private PregnancyTipView mPregnancyTipView;
	/**
	 * 邀请准爸爸
	 */
	private Button invite;

	private View layoutAd;

	/**
	 * 广告条页
	 */
	private ScrollPager<Banner> mPager;

	/**
	 * 广告数据
	 */
	private ArrayList<Banner> dataList;
	/**
	 * 广告底部的点点点... 控制器
	 */
	private PagerControl mControl;

	/**
	 * bar 条
	 */
	private FatherTitleBar mTitleBar;

	private ListView mRecommendList;

	private ArrayList<Commend> mCommends = new ArrayList<Commend>();

	/**
	 * 加载失败文本提示
	 */
	private TextView mFailedTextView;

	/**
	 * 签到
	 */
	private TextView tvSignIn;

	/**
	 * 签到标记
	 */
	private String mIsSign = "0";

	private ScrollView scrollView;
	/**
	 * 记录ScrollView滚动的位置 - Y轴
	 */
	private int scrollY = 0;

	public MainFragement(FatherTitleBar titleBar) {
		this.mTitleBar = titleBar;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		BabytreeLog.d(TAG + " onCreate ------");
		// // 关闭之前多余界面
		// closeOtherActivity();
		// // 加入关闭通知监听(需要在onCreate之前调用)
		// babytreecloselistener = this;

		super.onCreate(savedInstanceState);

		// ---------------------缓存模块start--------------------------
		bitmapCache = BabytreeBitmapCache.create(mContext);
		// ---------------------缓存模块end----------------------------

		loginStr = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOGIN_STRING);

		mApplication = (PregnancyApplication) mContext.getApplication();
		mApplication.y_getCalendarDbAdapter();
		mDbController = mApplication.y_getCalendarDbController();
		bean = (Y_Knowledge) mContext.getIntent().getSerializableExtra("remind");
		if (null != bean) {
			startActivity(new Intent(mContext, Y_RemindDetailActivity.class).putExtra("_id", bean._id)
					.putExtra("status", bean.status).putExtra("is_important", bean.is_important)
					.putExtra("days_number", bean.days_number));
		}
		String rpsCount = mContext.getIntent().getStringExtra("response_count");
		String disId = mContext.getIntent().getStringExtra("discuz_id");
		String page = mContext.getIntent().getStringExtra("page");
		if (rpsCount != null && !"".equals(rpsCount) && disId != null && !"".equals(disId) && page != null
				&& !"".equals(page)) {
			TopicNewActivity.launch(mContext, Integer.parseInt(disId), Integer.parseInt(page));
		}

		String iPromo = mContext.getIntent().getStringExtra("discuz_id");
		if (null != iPromo && !"".equals(iPromo)) {
			TopicNewActivity.launch(mContext, Integer.parseInt(iPromo), 0);
		}

		loginStr = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOGIN_STRING);
		boolean isLoginStr = SharedPreferencesUtil.getBooleanValue(mContext, ShareKeys.ISLOGINSTR);
		if (loginStr != null && !loginStr.equals("") && isLoginStr == true) {
			initCookie(loginStr);
			BabytreeLog.d("初始化cookie initCookie - logString = " + loginStr);
		}
		// 广告栏相关
		// 获取宝宝生日
		birthdayTimestamp = SharedPreferencesUtil.getLongValue(mContext, ShareKeys.BIRTHDAY_TIMESTAMP);

		// 获取大图
		getPicture();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		BabytreeLog.d(TAG + " onCreateView ------");
		View view = inflater.inflate(R.layout.homepage_fragment, null);
		scrollView = (ScrollView) view;
		mPregnancyTipView = (PregnancyTipView) view.findViewById(R.id.pregnancy_tip);
		long birthday = SharedPreferencesUtil.getLongValue(mContext, ShareKeys.BIRTHDAY_TIMESTAMP);
		if (BabytreeUtil.isPregnancy(mContext)) {
			// 宝宝年龄大小
			mPregnancyTipView.showBabyBirthday(birthday);
		} else {
			// 预产期
			mPregnancyTipView.showDateMommy(birthday);
		}
		mPregnancyTipView.setPregnancyClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, InformationDetailActivity.class);
				intent.putExtra("id", 10000);
				intent.putExtra("title", "准妈妈孕周的计算");
				BabytreeUtil.launch(mContext, intent, false, 0);
			}
		});

		// 签到
		tvSignIn = (TextView) view.findViewById(R.id.home_sign_in);
		tvSignIn.setBackgroundResource(R.drawable.sign_in_un);
		tvSignIn.setOnClickListener(this);
		int leftw = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int lefth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		View v = mTitleBar.getRightButton();
		v.measure(leftw, lefth);
		tvSignIn.measure(leftw, lefth);
		int width = Utils.px2dip(mContext, v.getMeasuredWidth());
		BabytreeLog.d("tvSignIn.getMeasuredWidth() = " + tvSignIn.getMeasuredWidth() + " - v.getMeasuredWidth() "
				+ v.getMeasuredWidth());
		int signInWidth = Utils.px2dip(mContext, tvSignIn.getMeasuredWidth());
		BabytreeLog.d("tvSignIn.() = " + signInWidth + " - v.() " + width);
		FrameLayout.LayoutParams wrapParams = (FrameLayout.LayoutParams) tvSignIn.getLayoutParams();
		wrapParams.rightMargin = ((width - signInWidth) / 2);
		tvSignIn.setLayoutParams(wrapParams);
		tvSignIn.setPadding(10, 6, 10, 40);

		// 邀请准爸爸
		invite = (Button) view.findViewById(R.id.main_activity_invite);
		invite.setOnClickListener(this);
		invite.setVisibility(View.GONE);

		// 九宫格
		GridView gridView = (GridView) view.findViewById(R.id.home_grid);
		gridView.setAdapter(new HomeGridAdapter(mContext));
		gridView.setOnItemClickListener(onItemClickListener);

		// 广告
		List<Banner> data = new ArrayList<Banner>();
		layoutAd = view.findViewById(R.id.layout_ad);
		mControl = (PagerControl) view.findViewById(R.id.pager_indicator);
		mControl.lazyInit(PagerControl.INDICATOR_DOT_PAGER);

		mPager = (ScrollPager<Banner>) view.findViewById(R.id.pager);
		mPager.addOnScrollListener(mScrollListener);
		mPager.addOnCreateChildView(mCreateChildView);

		// 推荐列表
		mRecommendList = (ListView) view.findViewById(R.id.home_list);
		mRecommendList.setOnItemClickListener(onItemClickListener);
		mFailedTextView = (TextView) view.findViewById(R.id.notify_fail);
		mFailedTextView.setOnClickListener(this);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		BabytreeLog.d(TAG + " onActivityCreated ------");
		// 获取推荐列表
		getCommendTopics();

		scrollView.smoothScrollTo(0, 20);
	}

	/**
	 * 初始化广告数据
	 * 
	 * @param data
	 */
	public void initAdData(List<Banner> data) {
		mPager.setData(data);
		mControl.setPagesIndicatorType(mPager.getChildCount());
	}

	/**
	 * 初始化推荐列表
	 */
	private void initRecommendTopics() {
		mRecommendList.setAdapter(new HomeListAdapter(mContext, mCommends));
	}

	private Handler myCookieHandler = new Handler() {
		public void handleMessage(Message msg) {
			DataResult ret = (DataResult) msg.obj;
			switch (ret.status) {
			case BabytreeController.SUCCESS_CODE:
				SharedPreferencesUtil.setValue(mContext, ShareKeys.COOKIE, (String) ret.data);
				SharedPreferencesUtil.setValue(mContext, ShareKeys.ISLOGINSTR, true);
				break;
			default:
				// Load error
				SharedPreferencesUtil.setValue(mContext, ShareKeys.ISLOGINSTR, false);
				ExceptionUtil.catchException(ret.error, mContext);
				break;
			}
		}
	};

	// 获取cookie
	private void initCookie(final String loginStr) {
		// Loading

		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(mContext)) {
						ret = P_BabytreeController.checkLoginCookie(mContext, loginStr);
					} else {
						ret = new DataResult();
						ret.message = BabytreeController.NetworkExceptionMessage;
						ret.status = BabytreeController.NetworkExceptionCode;
					}
				} catch (Exception e) {
					ret = new DataResult();
					ret.message = BabytreeController.SystemExceptionMessage;
					ret.status = BabytreeController.SystemExceptionCode;
				}
				message.obj = ret;
				myCookieHandler.sendMessage(message);
			}

		}.start();

	}

	// ==========UMENG Begin===========
	@Override
	public void onResume() {
		super.onResume();
		BabytreeLog.d(TAG + " onResume ------");
		loginStr = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOGIN_STRING);

		mBirthday = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.BIRTHDAY); // 201205
		boolean isLoginStr = SharedPreferencesUtil.getBooleanValue(mContext, ShareKeys.ISLOGINSTR);
		if (loginStr != null && !loginStr.equals("") && isLoginStr == false) {
			initCookie(loginStr);
		}
		birthdayTimestamp = SharedPreferencesUtil.getLongValue(mContext, ShareKeys.BIRTHDAY_TIMESTAMP);
		try {
			hasDaysNum = BabytreeUtil.getBetweenDays(birthdayTimestamp, Calendar.getInstance(Locale.CHINA)
					.getTimeInMillis());
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (hasDaysNum > 0 && hasDaysNum < KNOWLEDGE_TOTAL_DAYS) {
			SharedPreferencesUtil.setValue(mContext, ShareKeys.Y_HASDAYS, hasDaysNum);
			try {
				// 知识 提醒 食谱标题内容
				List<Y_Knowledge> list5 = mDbController.getKnowledgeListByDays(hasDaysNum + 1, CommConstants.TYPE_KNOW);
				for (Y_Knowledge know : list5) {
					if (know.category_id != 19 && know.view_type == 4) {
						if (!know.title.equals("")) {
						} else {
						}
					} else {
					}

				}
				List<Y_Knowledge> list0 = mDbController.getKnowledgeListByDays(hasDaysNum + 1, CommConstants.TYPE_KNOW);
				for (Y_Knowledge know : list0) {
					if (know.category_id != 19 && know.view_type == 4) {
						if (!know.title.equals("")) {
						} else {
						}
					} else {
					}

				}
				Random random = new Random();

				int weekDays = (hasDaysNum / 7) + 1;

				int startDays = weekDays == 1 ? 1 : (weekDays - 1) * 7;
				int endDays = weekDays * 7;
				List<Y_Knowledge> remindLIst = mDbController.getKnowledgeListByDays(startDays, endDays,
						CommConstants.TYPE_REMIND);
				if (remindLIst.size() > 1) {
					int index = random.nextInt(remindLIst.size() - 1);
				} else if (remindLIst.size() > 0) {
				}
			} catch (Exception ex) {

			}
		} else if (hasDaysNum >= KNOWLEDGE_TOTAL_DAYS) {
			SharedPreferencesUtil.setValue(mContext, ShareKeys.Y_HASDAYS, KNOWLEDGE_TOTAL_DAYS);
		} else {
			SharedPreferencesUtil.setValue(mContext, ShareKeys.Y_HASDAYS, 0);
		}

		if (loginStr != null && !loginStr.equals("")) {
		} else {
		}

		isChoiceHospital = SharedPreferencesUtil.getBooleanValue(mContext, ShareKeys.ISCHOICEHOSPITAL);
		hospitalName = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.HOSPITAL_NAME);
		hospitalId = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.HOSPITAL_ID);
		groupId = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.GROUP_ID);
		positionName = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOCATION_FOR_HOSPITAL);
		int lengthForHospital = com.babytree.apps.comm.util.BabytreeUtil.getStringLength(hospitalName);
		int lengthForPosition = com.babytree.apps.comm.util.BabytreeUtil.getStringLength(positionName);
		if (!isChoiceHospital) {
			isHospitalNotice = true;
		} else {
			if (null != hospitalName && !"".equals(hospitalName)) {
				if (hospitalId == null || groupId == null || "0".equals(groupId)) {
				} else {
					if (lengthForHospital > 12) {
					} else {
					}
				}

			} else if (null == hospitalName || "".equals(hospitalName)) {
				if (null != positionName && !"".equals(positionName)) {
					if (lengthForPosition > 8) {
					} else {
					}
				}
			}

		}
		// 怀孕超过12周没有设置医院提醒
		if (null != loginStr && !"".equals(loginStr) && isHospitalNotice) {
			if (((280 - hasDaysNum) / 7) > 12) {
				isHospitalNotice = false;
				Toast.makeText(mContext, "请选择您预订的医院！", Toast.LENGTH_SHORT).show();
			}
		}

		// 设置宝宝年龄

		// birthdayTxt.setText(com.babytree.apps.comm.util.BabytreeUtil.getBabyBirthday(birthdayTimestamp));

		// 是否同步预产期/宝宝生日
		boolean isNeedPre = SharedPreferencesUtil.getBooleanValue(mContext, ShareKeys.IS_NEED_PRE, true);
		if (isNeedPre && loginStr != null && !loginStr.equals("")) {
			BabytreeLog.d("Sync baby birthday begin.");
			SimpleDateFormat mDateFormatForApi = new SimpleDateFormat("yyyy-M-d", Locale.CHINA);
			syncPre(mDateFormatForApi.format(new Date(birthdayTimestamp)));
		}

		// 邀请准爸爸
		if (BabytreeUtil.isPregnancy(mContext)) { // 育儿
			invite.setVisibility(View.GONE);
		} else {// 孕期
			if (TextUtils.isEmpty(getLoginString())) {
				invite.setVisibility(View.VISIBLE);
			} else {
				// 获取绑定状态
				getBindStatus();
			}
		}

		if (dataList == null || dataList.size() == 0) {
			new MAsyncTask().execute();
		} else {
			BabytreeLog.d("已经加载广告,无需重新加载广告");
		}

		if (BabytreeUtil.isLogin(mContext)) {
			// 获取签到状态
			process();
		}
		mRecommendList.setFocusable(false);
		scroll2Position(scrollView, scrollY);
		mRecommendList.setFocusable(true);

		// 获取相关消息数目
		initReceiverTxt();
	}

	@Override
	public void onPause() {
		super.onPause();
		BabytreeLog.d(TAG + "onPause");
		// 停止广告循环
		// adBannerView.stopAd();
		scrollY = scrollView.getScrollY();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.main_activity_invite:
			// 邀请准爸爸
			if (loginStr != null && !loginStr.equals("")) {
				startActivity(new Intent(mContext, FatherIntrActivity.class));
			} else {
				Intent intent = new Intent(mContext, LoginActivity.class);
				startActivityForResult(intent, 111);
			}
			break;

		case R.id.home_sign_in:
			// 签到
			BabytreeLog.d(TAG + " 签到");
			goSignIn();
			break;

		case R.id.notify_fail:
			//
			BabytreeLog.d(TAG + " 重新加载推荐列表");
			getCommendTopics();
			break;
		default:
			break;
		}
	}

	/**
	 * 滚动到某位置
	 * 
	 * @param scrollView
	 */
	private void scroll2Position(final ScrollView scrollView, final int scrollY) {
		scrollView.post(new Runnable() {

			@Override
			public void run() {
				BabytreeLog.d("滚动 ScrollView... " + scrollY);
				scrollView.smoothScrollTo(0, scrollY);
			}
		});

	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		BabytreeLog.d("onActivityResult YQ Home page to invite Login...req = " + requestCode + " ret = " + resultCode);

		if (requestCode == 0 && resultCode == 888) {
			loginStr = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOGIN_STRING);
		}

		if (requestCode == 111 && resultCode == 888) {
			loginStr = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOGIN_STRING);
			Intent intent = new Intent(mContext, FatherIntrActivity.class);
			BabytreeUtil.launch(mContext, intent, false, 0);
		}
	};

	/**
	 * 是否展示titleBar上的信息提示
	 * 
	 * @param isTitleLeftButton
	 *            指定TitleButton的左边还是右边的铵钮。
	 * @param isShowNum
	 *            是否在消息圈内展示数值，数量数值在100以内。
	 * @param count
	 *            消息的数量
	 */
	public void titleBarTipMsg(boolean isTitleLeftButton, boolean isShowNum, int count, int resId) {
		boolean isShow = ((count <= 0) ? false : true);
		if (isTitleLeftButton) { // 左侧
			BabytreeLog.d("设置消息提示 Left " + isShow);
			mTitleBar.setLeftButtonTagNum(isShow, resId);
			if (isShow) {
				BabytreeLog.d("发送广播 - 刷新左侧菜单消息列表 新消息数 = " + count);
				LocalBroadcastManager.getInstance(mApplication).sendBroadcast(
						new Intent(MommyMenuFragment.ACTION_NOTICE_REFRESH));
			}
		} else {// 右侧
			BabytreeLog.d("设置消息提示 Right " + isShow);
			mTitleBar.setRightButtonTagNum(isShow, resId);
			if (isShow) {
				BabytreeLog.d("刷新右侧菜单消息列表 新消息数 = " + count);
			}
		}
	}

	private int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength),
				Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	public void showDialog(String title, String content, String okText, String cancleText, boolean cancelable,
			OnCancelListener btnCancle, OnClickListener btnOk) {
		mDialog = new ProgressDialog(mContext);
		mDialog.setTitle(title);
		mDialog.setMessage(content);
		mDialog.setCancelable(cancelable);
	}

	/**
	 * 同步预产期(每次启动应用时判断是否同步过,没有同步则调用此方法同步一次)
	 * 
	 * @author wangshuaibo
	 */
	private void syncPre(final String tempPre) {
		final Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				DataResult ret = (DataResult) msg.obj;
				if (ret.status == BabytreeController.SUCCESS_CODE) {
					BabytreeLog.d("Sync baby birthday success.");
					String tmpBirthday = (String) ret.data;
					SharedPreferencesUtil.setValue(mContext, ShareKeys.IS_NEED_PRE, false);
					SharedPreferencesUtil.setValue(mContext, ShareKeys.BABY_BIRTHDAY_TS, tmpBirthday);
				} else {
					BabytreeLog.d("Sync baby birthday faild.");
				}
			}
		};
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();

				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(mContext)) {
						ret = HomeController.savePersonalInfo(loginStr, null, null, tempPre);
					} else {
						ret = new DataResult();
						ret.message = BabytreeController.NetworkExceptionMessage;
						ret.status = BabytreeController.NetworkExceptionCode;
					}
				} catch (Exception e) {
					ret = new DataResult();
					ret.message = BabytreeController.SystemExceptionMessage;
					ret.status = BabytreeController.SystemExceptionCode;
				}
				message.obj = ret;
				mHandler.sendMessage(message);
			}
		}.start();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 跳转到知识页
	 */
	public void goKnowledge() {
		Intent intent;
		if (BabytreeUtil.isPregnancy(mContext)) {// 育儿
			intent = new Intent(mContext, Y_KnowledgeActivity.class);
			BabytreeUtil.launch(mContext, intent, false, 0);
		} else {// 孕期
			MobclickAgent.onEvent(mContext, EventContants.index, EventContants.index_knowledge);
			intent = new Intent(mContext, InformationActivity.class);
			BabytreeUtil.launch(mContext, intent, false, 0);
		}
	}

	/**
	 * 跳转到签到页
	 */
	public void goSignIn() {
		Intent intent;
		// 去签到界面
		if (BabytreeUtil.isLogin(mContext)) {
			intent = new Intent(mContext, SignInActivity.class);
			BabytreeUtil.launch(mContext, intent, false, 0);
		} else {
			intent = new Intent(mContext, LoginActivity.class);
			intent.putExtra("fromY", true);
			BabytreeUtil.launch(mContext, intent, false, 0);
		}
		// MobclickAgent.onEvent(mContext, EventContants.index,
		// EventContants.index_box);
		// intent = new Intent(mContext, BabyTreeWebviewActivity.class);
		// intent.putExtra("url", UrlConstrants.CHECKIN);
		// intent.putExtra("title",
		// mContext.getResources().getString(R.string.s_home_sign_in));
		// BabytreeUtil.launch(mContext, intent, false, 0);
	}

	/**
	 * 跳转到美食厨房
	 */
	public void goKitchen() {
		Intent intent;
		if (BabytreeUtil.isPregnancy(mContext)) { // 育儿
			intent = new Intent(mContext, Y_KitchenActivity.class);
			BabytreeUtil.launch(mContext, intent, false, 0);
		} else {// 孕期
			MobclickAgent.onEvent(mContext, EventContants.index, EventContants.index_kitchen);
			intent = new Intent(mContext, KitchenActivity.class);
			BabytreeUtil.launch(mContext, intent, false, 0);
		}
	}

	/**
	 * 跳转到关爱提醒
	 */
	public void goTip() {
		Intent intent;
		if (BabytreeUtil.isPregnancy(mContext)) { // 育儿
			intent = new Intent(mContext, Y_RemindActivity.class);
			BabytreeUtil.launch(mContext, intent, false, 0);
		} else {// 孕期
			MobclickAgent.onEvent(mContext, EventContants.index, EventContants.index_remind);
			intent = new Intent(mContext, RemindActivity.class);
			BabytreeUtil.launch(mContext, intent, false, 0);
		}
	}

	/**
	 * 跳转到同龄圈子
	 */
	public void goSameAgeCircle() {
		Intent intent;
		MobclickAgent.onEvent(mContext, EventContants.index, EventContants.index_communication);
		intent = new Intent(mContext, ForumTabActivity.class);
		BabytreeUtil.launch(mContext, intent, false, 0);
	}

	/**
	 * 跳转到医院交流圈子
	 */
	public void goHospitalCircle() {
		MobclickAgent.onEvent(mContext, EventContants.index, EventContants.yiyuanquanzi);
		if (!isChoiceHospital) {
			LocationList3Activity.launch(mContext, false);
		} else {
			if (null != hospitalName && !"".equals(hospitalName)) {
				if (groupId == null || "0".equals(groupId)) {
					LocationList3Activity.launch(mContext, false);
				} else {
					startActivity(new Intent(mContext, ForumTabHospitalActivity.class));
				}
			} else if (null == hospitalName || "".equals(hospitalName)) {
				if (null != positionName && !"".equals(positionName)) {
					String location = SharedPreferencesUtil.getStringValue(mApplication,
							ShareKeys.LOCATION_FOR_HOSPITAL);
					if (location != null) {
						HospitalsInfoListActivity.lauch(mContext, location, null, true);
					} else {
						LocationList3Activity.launch(mContext, false);
					}
				}
			}
		}
	}

	/**
	 * 跳转到专家在线
	 */
	public void goExpert() {
		// TODO 专家在线埋点
		// MobclickAgent.onEvent(mContext, EventContants.index,
		// EventContants.index_box);

		BabyTreeWebviewActivity.launch(mContext, UrlConstrants.EXPERT_ON_LINE, "专家在线");
	}

	/**
	 * 跳转到babytreeBox申请页
	 */
	public void goBabytreeBoxApply() {
		MobclickAgent.onEvent(mContext, EventContants.index, EventContants.index_box);

		BabyTreeWebviewActivity.launch(mContext, UrlConstrants.BABYBOX_URL, "申请BabyBox");
	}

	/**
	 * 跳转到精彩活动
	 */
	public void goActive() {

		MobclickAgent.onEvent(mContext, EventContants.index, EventContants.jingcaihuodong);

		BabyTreeWebviewActivity.launch(mContext, UrlConstrants.ACTIVITY_URL, "精彩活动");
	}

	/**
	 * 跳转到小工具
	 */
	public void goSmallTools() {
		Intent intent;
		MobclickAgent.onEvent(mContext, EventContants.index, EventContants.index_tool);
		intent = new Intent(mContext, ForecastActivity.class);
		BabytreeUtil.launch(mContext, intent, false, 0);
	}

	/**
	 * 跳转到早教光盘-申请米卡界面
	 */
	public void goCD() {
		Intent intent;
		MobclickAgent.onEvent(mContext, EventContants.index, EventContants.index_tool);
		intent = new Intent(mContext, MikaActivity.class);
		BabytreeUtil.launch(mContext, intent, false, 0);
	}

	/**
	 * 跳转到推荐帖子详情页
	 */
	public void goRecommendTopicDetail(int position) {
		// Intent intent;
		// intent = new Intent(mContext, TopicNewActivity.class);
		Commend commend = mCommends.get(position);
		Discuz discuz = new Discuz();
		discuz.discuz_id = Integer.parseInt(commend.getId());
		TopicNewActivity.launch(mContext, discuz.discuz_id, 1);
	}

	/**
	 * adapterView监听器
	 */
	AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> adapterView, View view, int postion, long id) {

			switch (adapterView.getId()) {
			case R.id.home_grid: // 九宫格
				BabytreeLog.d(TAG + "九宫格item click -- pos = " + postion);
				switch (postion) {
				case 0:// 每日知识
					goKnowledge();
					break;

				case 1:// 关爱提醒
					goTip();
					break;

				case 2:// 美食厨房
					goKitchen();
					break;

				case 3:// 同龄交流圈
					goSameAgeCircle();
					break;

				case 4:// 医院交流圈
					goHospitalCircle();
					break;

				case 5:// 专家在线
					goExpert();
					break;

				case 6:// babyBox
					goBabytreeBoxApply();
					break;

				case 7:// 精彩活动
					goActive();
					break;

				case 8:// 小工具/早教光盘
					if (BabytreeUtil.isPregnancy(mContext)) { // 育儿
						goCD();
					} else {// 孕期
						goSmallTools();
					}
					break;

				default:
					break;
				}
				break;

			case R.id.home_list:
				BabytreeLog.d(TAG + "推荐列表 item click -- pos = " + postion);
				goRecommendTopicDetail(postion);
				break;
			default:
				break;
			}
		}
	};

	OnScrollListener mScrollListener = new OnScrollListener() {
		@Override
		public void onScroll(int scrollX) {

		}

		@Override
		public void onViewScrollFinished(int currentPage) {
			mControl.setCurrentPager(currentPage);
		}
	};

	OnCreateChildView<Banner> mCreateChildView = new OnCreateChildView<Banner>() {
		@Override
		public View createChildView(final Banner banner) {

			LinearLayout layout = new LinearLayout(mContext);
			int padding = 6;
			layout.setPadding(padding, padding, padding, padding);
			layout.setBackgroundResource(R.drawable.ads_bg);
			ImageView imageView = new ImageView(mContext);
			imageView.setLayoutParams(new Gallery.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
			imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			imageView.setBackgroundResource(R.drawable.ads_empty_bg);
			String curr_URL = banner.imgUrl;
			// ---------------------缓存模块start--------------------------
			bitmapCache.display(imageView, curr_URL);
			// ---------------------缓存模块end----------------------------

			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					MobclickAgent.onEvent(mContext,
							EventContants.banner.replace("$num", (mPager.getCurrentPage() + 1) + ""));
					BabytreeLog.d("clicked - "
							+ EventContants.banner.replace("$num", (mPager.getCurrentPage() + 1) + "") + banner.title);
					// select_type: "1":跳帖子页
					// select_type: "2":跳url
					if (banner.selectType == 2) {

						BabyTreeWebviewActivity.launch(mContext, banner.url, "详情");

					} else if (banner.selectType == 1) {

						TopicNewActivity.launch(mContext, banner.topicId, 1);
					}
				}
			});
			layout.addView(imageView);
			return layout;
		}
	};

	/**
	 * 获取广告任务
	 * 
	 * @author pengxh
	 * 
	 */
	private class MAsyncTask extends AsyncTask<Void, Integer, DataResult> {

		@Override
		protected DataResult doInBackground(Void... params) {
			BabytreeLog.d("开始请求广告");
			DataResult ret = null;
			try {
				ret = HomeController.getBannerList(mBirthday);
			} catch (Exception e) {
				BabytreeLog.e("Load ad faild.", e);
				ret = new DataResult();
				ret.error = ExceptionUtil.printException(e).toString();
			}
			return ret;
		}

		@Override
		protected void onPreExecute() {
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void onPostExecute(DataResult result) {
			if (result.status == BaseController.SUCCESS_CODE) {
				dataList = (ArrayList<Banner>) result.data;
				if (dataList.size() > 0) {
					BabytreeLog.d("广告下载成功 -- " + dataList.size() + "条, onPostExecute " + dataList);
					layoutAd.setVisibility(View.VISIBLE);
					// 初始化数据
					initAdData(dataList);
				}
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
		}
	}

	/**
	 * 获取绑定状态
	 */
	private void getBindStatus() {
		new BindStatusTask(mContext).execute(getLoginString(), getGender());
	}

	/**
	 * 邀请码
	 */
	private class BindStatusTask extends BabytreeAsyncTask {

		public BindStatusTask(Context context) {
			super(context);
		}

		@Override
		protected DataResult toNet(String[] params) {
			return FatherController.getBindStatus(params[0], params[1]);
		}

		@Override
		protected void success(DataResult result) {
			try {
				BindStatus bindStatus = (BindStatus) result.data;
				BabytreeLog.d("首页获取-绑定状态" + bindStatus.getBindStatus());
				if ("1".equalsIgnoreCase(bindStatus.getBindStatus())) {// 没有绑定
					invite.setVisibility(View.GONE);// 隐藏邀请准爸爸入口
				} else {
					invite.setVisibility(View.VISIBLE);// 显示邀请准爸爸入口
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void failure(DataResult result) {
		}

		@Override
		protected String getDialogMessage() {
			return "";
		}
	}

	/**
	 * 获取绑定状态
	 */
	private void getPicture() {
		//
		String key = "0";
		if (BabytreeUtil.isPregnancy(mContext)) {
			key = CommConstants.HOME_PIC_MOMMY_KEY_YUER;
		} else {
			key = CommConstants.HOME_PIC_MOMMY_KEY_PREGNANCY;
		}
		new PictureTask(mContext).execute(getLoginString(), key);
	}

	/**
	 * 获取图片
	 */
	private class PictureTask extends BabytreeAsyncTask {

		public PictureTask(Context context) {
			super(context);
		}

		@Override
		protected DataResult toNet(String[] params) {
			return HomeController.getPicture(params[0], params[1]);
		}

		@Override
		protected void success(DataResult result) {
			try {
				String url = (String) result.data;
				if (url != null) {
					BabytreeLog.d("首页大图获取成功-大图URL= " + url);
					mPregnancyTipView.updateTipViewPicture(url);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void failure(DataResult result) {
			BabytreeLog.d("首页大图获取失败 - " + result.message);
		}

		@Override
		protected String getDialogMessage() {
			return "";
		}
	}

	/**
	 * 获取推荐列表
	 */
	private void getCommendTopics() {
		// TODO 参数更正
		new CommendTopicTask(mContext).execute();
	}

	/**
	 * 获取推荐列表
	 */
	private class CommendTopicTask extends BabytreeAsyncTask {

		public CommendTopicTask(Context context) {
			super(context);
		}

		@Override
		protected DataResult toNet(String[] params) {
			return HomeController.getRecommendTopics();
		}

		@Override
		protected void onPreExecute() {
			mFailedTextView.setText(mContext.getResources().getString(R.string.s_home_loading));
			mFailedTextView.setVisibility(View.VISIBLE);
			mRecommendList.setVisibility(View.GONE);
		}

		@Override
		protected void success(DataResult result) {
			try {
				ArrayList<Commend> commends = (ArrayList<Commend>) result.data;
				mCommends = commends;
				initRecommendTopics();
				mFailedTextView.setVisibility(View.GONE);
				mRecommendList.setVisibility(View.VISIBLE);
				BabytreeLog.d(TAG + "推荐列表获取成功 - 条数 - " + mCommends.size());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void failure(DataResult result) {
			mRecommendList.setVisibility(View.GONE);
			mFailedTextView.setText(mContext.getResources().getString(R.string.s_home_load_fail));
			mFailedTextView.setVisibility(View.VISIBLE);
			BabytreeLog.d(TAG + "推荐列表获取失败 - " + result.message);
		}

		@Override
		protected String getDialogMessage() {
			return "";
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (mDialog != null)
				mDialog.dismiss();
			DataResult ret = (DataResult) msg.obj;
			switch (ret.status) {
			case BabytreeController.SUCCESS_CODE:
				UserAddInfo bean = (UserAddInfo) ret.data;
				mIsSign = bean.is_sign;
				if (mIsSign.equals("0")) {
					tvSignIn.setBackgroundResource(R.drawable.sign_in_un);
				} else {
					tvSignIn.setBackgroundResource(R.drawable.sign_in);
				}
				tvSignIn.setPadding(10, 6, 10, 40);
				tvSignIn.setEms(1);
				tvSignIn.setMaxEms(1);
				break;
			default:
				Toast.makeText(mContext, "亲，你的网络不给力", Toast.LENGTH_SHORT).show();
				ExceptionUtil.catchException(ret.error, mContext);
				break;
			}
		}
	};

	/**
	 * 获取签到状态
	 */
	private void process() {
		new Thread() {

			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(mContext)) {
						ret = SignInController.getUserAddInfo(loginStr);
					} else {
						ret = new DataResult();
						ret.message = BabytreeController.NetworkExceptionMessage;
						ret.status = BabytreeController.NetworkExceptionCode;
					}
				} catch (Exception e) {
					ret = new DataResult();
					ret.message = BabytreeController.SystemExceptionMessage;
					ret.status = BabytreeController.SystemExceptionCode;
					ret.error = ExceptionUtil.printException(e).toString();
				}
				message.obj = ret;
				handler.sendMessage(message);
			}

		}.start();
	}

	/**
	 * 获取相关消息数
	 */
	private void initReceiverTxt() {
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(mContext)) {
						ret = MessageController.getUnreadMessageCount(loginStr);
					} else {
						ret = new DataResult();
						ret.message = P_BabytreeController.NetworkExceptionMessage;
						ret.status = P_BabytreeController.NetworkExceptionCode;
					}

				} catch (Exception e) {
					ret = new DataResult();
					ret.message = P_BabytreeController.SystemExceptionMessage;
					ret.status = P_BabytreeController.SystemExceptionCode;
				}
				message.obj = ret;
				receiveHandler.sendMessage(message);
			}
		}.start();
	}

	Handler receiveHandler = new Handler() {
		public void handleMessage(Message msg) {
			DataResult ret = (DataResult) msg.obj;
			InOutBox io = (InOutBox) ret.data;
			if (ret.status == P_BabytreeController.SUCCESS_CODE) {
				if (io != null) {
					try {
						BabytreeLog.d(TAG + "获取未读消息数成功");
						shouLeft(Integer.parseInt(io.user_comment_reply_unread_count));
						shouRight(Integer.parseInt(io.unreadCount));
					} catch (Exception ex) {
						shouLeft(0);
						shouRight(0);
					}
				}
			} else {
				shouLeft(0);
				shouRight(0);
				BabytreeLog.d(TAG + "获取未读消息数 发生异常");
				ExceptionUtil.catchException(ret.error, mContext);
			}
		}
	};

	/**
	 * 评论回复列表 - 未读消息数
	 * 
	 * @param user_comment_reply_unread_count
	 */
	private void shouLeft(int user_comment_reply_unread_count) {
		BabytreeLog.d(TAG + "评论回复 - 未读消息数 " + user_comment_reply_unread_count);
		titleBarTipMsg(true, false, user_comment_reply_unread_count, R.drawable.point);
	}

	/**
	 * 收件箱 - 未读消息数
	 * 
	 * @param unreadCount
	 */
	private void shouRight(int unreadCount) {
		BabytreeLog.d(TAG + "收件箱 - 未读消息数 " + unreadCount);
		titleBarTipMsg(false, false, unreadCount, R.drawable.point);
	}
}
