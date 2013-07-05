package com.babytree.apps.biz.center;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.biz.home.ctr.HomeController;
import com.babytree.apps.biz.home.model.Notify;
import com.babytree.apps.biz.topic.TopicNewActivity;
import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.BabyTreeFruitController;
import com.babytree.apps.comm.ctr.LocationDbController;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.model.Discuz;
import com.babytree.apps.comm.model.Location;
import com.babytree.apps.comm.model.TopicComment;
import com.babytree.apps.comm.model.TopicReply;
import com.babytree.apps.comm.model.Total;
import com.babytree.apps.comm.net.BabytreeAsyncTask;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.ui.BirthdayActivity;
import com.babytree.apps.comm.ui.CalculatorActivity;
import com.babytree.apps.comm.ui.HospitalsInfoListActivity;
import com.babytree.apps.comm.ui.LocationList3Activity;
import com.babytree.apps.comm.ui.LocationListActivity;
import com.babytree.apps.comm.ui.PersonalInfoEditActvity;
import com.babytree.apps.comm.ui.UserinfoNewActivity;
import com.babytree.apps.comm.ui.activity.BabyTreeWebviewActivity;
import com.babytree.apps.comm.ui.activity.BabytreePhotographFragement;
import com.babytree.apps.comm.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.comm.view.EmojiTextView;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;
import com.babytree.apps.pregnancy.ui.adapter.CommentReplyListAdapter;
import com.babytree.apps.pregnancy.ui.handler.CommentReplyListHandler;
import com.umeng.analytics.MobclickAgent;

/**
 * 个人中心页
 * 
 * @author wangbingqi
 * 
 */
public class MainCenterFragement extends BabytreePhotographFragement implements OnItemClickListener, OnScrollListener,
		OnRefreshListener, OnClickListener {

	private static final String TAG = MainCenterFragement.class.getSimpleName();
	private BabytreeBitmapCache bitmapCache;// 缓存对象
	private String loginStr = "";

	private ProgressDialog mDialog;;

	/**
	 * 位置按钮
	 */
	private FrameLayout frameLayout_location;
	/**
	 * 名字按钮
	 */
	private FrameLayout perCenterNickName;
	/**
	 * 医院
	 */
	private FrameLayout perCenterHospital;
	/**
	 * 预产期
	 */
	private FrameLayout changePregnancyImg;
	/**
	 * 名字文本
	 */
	private TextView mTxtNickname;
	/**
	 * 预产期文本
	 */
	private TextView pregnancyTxt;
	/**
	 * 医院文本
	 */
	private TextView centerHospitalTxt;
	/**
	 * 位置文本
	 */
	private TextView tv_location;

	/**
	 * 名字string
	 */
	private String nickname;
	/**
	 * 预产期
	 */
	private Long dateData;
	/**
	 * 医院名字
	 */
	private String hospitalName;
	/**
	 * 地理位置
	 */
	private String locationStr = "";
	private LocationDbController mDbController;
	/**
	 * 性别
	 */
	private String sexStr = "";
	/**
	 * 地理位置代表ID
	 */
	private int _id = 1101;

	private String positionStr;
	private Location location1;
	private Location location2;

	private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);

	private String groupId;
	private String positionName;
	private String hospitalId;

	// ----------------------
	private static final int BUTTON_POST = 10;
	private static final int BUTTON_REPLY = 11;
	private static final int BUTTON_COLLECT = 12;
	private static final int BUTTON_RL = 13;

	private int buttonType = BUTTON_POST;
	private int pageNo = 1;

	private boolean postChangable = true;
	private boolean replyChangable;
	private boolean collectChangable;
	private boolean rlChangable;

	private boolean postAddAble = true;
	private boolean replyAddAble;
	private boolean collectAddAble;
	private boolean rlAddAble;

	private long mCurrentTime = -1;
	private int visibleLastIndex = 0; // 最后的可视项索引

	private Button loadMoreBtn;

	private List<Discuz> postList = new ArrayList<Discuz>();
	private List<Discuz> replyList = new ArrayList<Discuz>();
	private List<Discuz> collectList = new ArrayList<Discuz>();
	private List<Base> rlList = new ArrayList<Base>();

	private RLAdapter rlAdapter;

	private PerAdapter pAdapter;

	private ListView mListView;

	/**
	 * 消息数
	 */
	private TextView unreadNoticeTv;

	/**
	 * 发表的帖子
	 */
	private Button postBtn;
	/**
	 * 回复的帖子
	 */
	private Button replyBtn;
	/**
	 * 收藏的帖子
	 */
	private Button collectBtn;

	/**
	 * 消息按钮
	 */
	private Button rlBtn;
	/**
	 * 头像
	 */
	private ImageView mHeadImg;

	private String imagePath = "";

	/**
	 * 水果数
	 */
	private String fruit_total = "";
	/**
	 * 水果数
	 */
	private TextView mTvFruit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 加入关闭通知监听
		// babytreecloselistener = this;
		super.onCreate(savedInstanceState);

		BabytreeLog.d(TAG + " onCreate ---------------");

		// ---------------------缓存模块start--------------------------
		bitmapCache = BabytreeBitmapCache.create(mContext);
		// ---------------------缓存模块end----------------------------

		pAdapter = new PerAdapter(new ArrayList<Discuz>());
		rlAdapter = new RLAdapter(new ArrayList<Base>());

		// init();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		BabytreeLog.d(TAG + " onCreateView ---------------");
		View view = inflater.inflate(R.layout.main_activity_slidingdrawer_header_list, null);
		init(view);
		// msgListinit(view);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		BabytreeLog.d(TAG + " onResume ---------------");
		// initReceiverTxt();
		try {
			String positionStr = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOCATION);
			if (positionStr == null || positionStr.equalsIgnoreCase("0") || positionStr.equalsIgnoreCase("null")
					|| positionStr.equalsIgnoreCase("")) {
				positionStr = "1101";
			}
			if (positionStr != null && !positionStr.equals("")) {
				location2 = mDbController.getLocationById(Integer.parseInt(positionStr));
				if (location2 != null) {
					if (location2.province != null && !location2.province.equals("")) {
						location1 = mDbController.getLocationById(Integer.parseInt(location2.province));
					}
				}
				if (location1 != null) {
					locationStr = location1.name + "  " + location2.name;
				}
			} else {
				locationStr = "";
			}
			tv_location.setText(locationStr);
		} finally {
		}

		groupId = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.GROUP_ID);
		positionName = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOCATION_FOR_HOSPITAL);
		hospitalId = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.HOSPITAL_ID);
		nickname = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.NICKNAME);
		dateData = SharedPreferencesUtil.getLongValue(mContext, ShareKeys.BIRTHDAY_TIMESTAMP);
		hospitalName = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.HOSPITAL_NAME);
		mApplication = (PregnancyApplication) mApplication;
		mDbController = new LocationDbController(mApplication.getLocationDbAdapter());
		if (pregnancyTxt != null) {
			pregnancyTxt.setText(mDateFormat.format(new java.util.Date(dateData)));
		}
		if (hospitalName != null && centerHospitalTxt != null) {
			if (hospitalName.equalsIgnoreCase("")) {
				centerHospitalTxt.setText("请选择医院");
			} else {
				centerHospitalTxt.setText(hospitalName);
			}
		}

		// 头像
		try {
			loginStr = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOGIN_STRING);
			imagePath = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.IMAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		setImageViewOnResume();
		if (loginStr != null) {
			doNet(loginStr);
		}
		if (mListView.getAdapter() instanceof PerAdapter) {
			postBtn.performClick();
			BabytreeLog.d("发表的帖子 postBtn.performClick()");
		}
	}

	/**
	 * 重新加载该页面设置头像
	 */
	private void setImageViewOnResume() {
		String head = "";
		try {
			loginStr = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOGIN_STRING);
			head = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.HEAD);
			imagePath = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.IMAGE);
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 加载头像
		if (loginStr != null && head != null) {
			initiaAvatar(mHeadImg, head);
		} else if (imagePath != null && !imagePath.equals("")) {
			setImage(imagePath);
		}

	}

	/**
	 * 设置图片
	 * 
	 * @param url
	 */
	private void setImage(String url) {
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(url, opts);
		// dada add 先导入缩放并导入内存
		opts.inSampleSize = computeSampleSize(opts, -1, 150 * 150);
		opts.inJustDecodeBounds = false;
		Bitmap bmp = BitmapFactory.decodeFile(url, opts);
		if (bmp != null) {
			mHeadImg.setImageBitmap(bmp);
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		BabytreeLog.d("MainCenterActivity requestCode: " + requestCode + " resultCode:" + resultCode);
		loginStr = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOGIN_STRING);
		if (loginStr == null || loginStr.equalsIgnoreCase("")) {
			// dada add 如果进入这个页面 未登录 则 直接跳转到登录页面
			// startActivity(new Intent(getApplicationContext(),
			// MainUnLoginActivity.class));
			// mContext.finish();
			return;
		}
		if (resultCode == 10) {
			_id = data.getIntExtra("_id", 0);
			if (null != loginStr && !"".equals(loginStr)) {
				savePersonalInfo(loginStr, sexStr, String.valueOf(_id), null);
			}

		}

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		switch (v.getId()) {
		case R.id.fruit_textView1:
		// 水果数
		{
			String url = "http://www.babytree.com/community/topic_mobile.php?id=5282387";
			String title = "水果介绍";
			BabyTreeWebviewActivity.launch(mContext, url, title);
		}
			break;
		case R.id.head_img:
			// 头像
			// TODO
			showPhotoMenu(150, 150);
			break;

		case R.id.main_post_btn:
			// 发表的帖子
			switchAdapter(pAdapter);
			buttonType = BUTTON_POST;
			pageNo = 1;
			postChangable = true;
			replyChangable = false;
			collectChangable = false;
			rlChangable = false;
			MobclickAgent.onEvent(mContext, EventContants.index, EventContants.index_post);
			postBtn.setBackgroundResource(R.drawable.personal_info_pressed);
			replyBtn.setBackgroundResource(R.drawable.personal_info_normal);
			collectBtn.setBackgroundResource(R.drawable.personal_info_normal);
			rlBtn.setBackgroundResource(R.drawable.personal_info_normal);
			if (null != loginStr && !"".equals(loginStr)) {
				getPostList(null, loginStr, "post");
			}
			break;
		case R.id.main_reply_btn:
			// 回复的帖子
			switchAdapter(pAdapter);
			buttonType = BUTTON_REPLY;
			pageNo = 1;
			postChangable = false;
			replyChangable = true;
			collectChangable = false;
			rlChangable = false;
			MobclickAgent.onEvent(mContext, EventContants.index, EventContants.index_reply);
			postBtn.setBackgroundResource(R.drawable.personal_info_normal);
			replyBtn.setBackgroundResource(R.drawable.personal_info_pressed);
			collectBtn.setBackgroundResource(R.drawable.personal_info_normal);
			rlBtn.setBackgroundResource(R.drawable.personal_info_normal);
			showDialog(null, "加载中...", null, null, true, null, null);
			if (null != loginStr && !"".equals(loginStr)) {
				getReplyList(null, loginStr, "reply");
			}
			break;
		case R.id.main_collect_btn:
			// 收藏的帖子
			switchAdapter(pAdapter);
			buttonType = BUTTON_COLLECT;
			pageNo = 1;
			postChangable = false;
			replyChangable = false;
			collectChangable = true;
			rlChangable = false;
			MobclickAgent.onEvent(mContext, EventContants.index, EventContants.index_collect);
			postBtn.setBackgroundResource(R.drawable.personal_info_normal);
			replyBtn.setBackgroundResource(R.drawable.personal_info_normal);
			rlBtn.setBackgroundResource(R.drawable.personal_info_normal);
			collectBtn.setBackgroundResource(R.drawable.personal_info_pressed);
			showDialog(null, "加载中...", null, null, true, null, null);
			if (null != loginStr && !"".equals(loginStr)) {
				getFavorList(loginStr);
			}
			break;
		case R.id.main_notice_rl:
			// 评论回复
			switchAdapter(rlAdapter);
			buttonType = BUTTON_RL;
			pageNo = 1;
			postChangable = false;
			replyChangable = false;
			collectChangable = false;
			rlChangable = true;
			MobclickAgent.onEvent(mContext, EventContants.index, EventContants.index_reply);
			postBtn.setBackgroundResource(R.drawable.personal_info_normal);
			replyBtn.setBackgroundResource(R.drawable.personal_info_normal);
			rlBtn.setBackgroundResource(R.drawable.personal_info_pressed);
			collectBtn.setBackgroundResource(R.drawable.personal_info_normal);
			showDialog(null, "加载中...", null, null, true, null, null);
			if (null != loginStr && !"".equals(loginStr)) {
				getRLList(loginStr);
			}
			break;
		case R.id.main_pregnancy_img_location:
			// 位置
			startActivityForResult(new Intent(mContext, LocationListActivity.class), 0);
			break;
		case R.id.main_personal_center_nickname:
			// 名字
			MobclickAgent.onEvent(mContext, EventContants.index, EventContants.index_change_personalInfo);
			startActivity(new Intent(mContext, PersonalInfoEditActvity.class));
			break;
		case R.id.main_pregnancy_img:
			// 预产期
			if (com.babytree.apps.comm.util.BabytreeUtil.isPregnancy(mContext)) {
				MobclickAgent.onEvent(mContext, EventContants.index, EventContants.index_change_pregnancy);
				startActivityForResult(new Intent(mContext, BirthdayActivity.class), 0);
			} else {

				MobclickAgent.onEvent(mContext, EventContants.index, EventContants.index_change_pregnancy);
				startActivityForResult(new Intent(mContext, CalculatorActivity.class), 0);
			}
			break;
		case R.id.main_personal_center_hospital:

			// 医院
			if (groupId != null && !"0".equals(groupId)) {
				Intent intent = new Intent(mContext, HospitalsInfoListActivity.class);
				intent.putExtra("key", positionName);
				intent.putExtra("hospital_id", hospitalId);
				intent.putExtra("fromUserCenter", true);
				startActivityForResult(intent, 0);
			} else {
				startActivityForResult(new Intent(mContext, LocationList3Activity.class), 0);
			}
			break;

		default:
			break;
		}
	}

	/**
	 * 更换数据适配器
	 * 
	 * @param adapter
	 */
	private void switchAdapter(BaseAdapter adapter) {
		mListView.setAdapter(adapter);
	}

	private PullToRefreshListView mPullToRefreshListView;

	private CommentReplyListHandler mCommentReplyHandler;

	private CommentReplyListAdapter mAdapter;
	private ArrayList<Base> values;

	@Override
	public void onRefresh() {
		if (mHandler != null) {
			mCommentReplyHandler.refershTop(System.currentTimeMillis());
		}
	}

	private void msgListinit(View viewBody) {
		// TODO Auto-generated method stub
		loginStr = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOGIN_STRING);

		mPullToRefreshListView = (PullToRefreshListView) viewBody.findViewById(R.id.list);
		mCommentReplyHandler = new CommentReplyListHandler(mContext, loginStr);
		values = mCommentReplyHandler.getValues();
		mAdapter = new CommentReplyListAdapter(mPullToRefreshListView, mContext, R.layout.loading, R.layout.reloading,
				mCommentReplyHandler, values);
		mPullToRefreshListView.getRefreshableView().setAdapter(mAdapter);
		mPullToRefreshListView.setOnScrollListener(mAdapter);
		mPullToRefreshListView.setOnRefreshListener(this);
		mPullToRefreshListView.getRefreshableView().setOnItemClickListener(this);
		mPullToRefreshListView.getRefreshableView().setSelector(R.drawable.no_item_click);
	}

	/**
	 * 登录后 初始化页面
	 * 
	 * @author wangbingqi
	 */
	private void init(View viewBody) {
		loginStr = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOGIN_STRING);
		groupId = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.GROUP_ID);
		positionName = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOCATION_FOR_HOSPITAL);
		hospitalId = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.HOSPITAL_ID);
		nickname = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.NICKNAME);
		dateData = SharedPreferencesUtil.getLongValue(mContext, ShareKeys.BIRTHDAY_TIMESTAMP);
		hospitalName = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.HOSPITAL_NAME);
		mApplication = (PregnancyApplication) mApplication;
		mDbController = new LocationDbController(mApplication.getLocationDbAdapter());
		sexStr = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.GENDER);

		// listview初始化
		loadMoreBtn = (Button) LayoutInflater.from(mContext).inflate(R.layout.load_more, null)
				.findViewById(R.id.btn_load_more);
		loadMoreBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				loadMoreBtn.setText("加载中...");
				loadMoreBtn.setEnabled(false);
				if (null != loginStr && !"".equals(loginStr)) {
					if (buttonType == BUTTON_POST) {
						postAddAble = true;
						pageNo++;
						getPostList(null, loginStr, "post");
					}
					if (buttonType == BUTTON_REPLY) {
						replyAddAble = true;
						pageNo++;
						getReplyList(null, loginStr, "reply");
					}
					if (buttonType == BUTTON_COLLECT) {
						collectAddAble = true;
						pageNo++;
						getFavorList(loginStr);
					}
					if (buttonType == BUTTON_RL) {
						rlAddAble = true;
						pageNo++;
						getRLList(loginStr);
					}
				}
			}

		});
		mListView = (ListView) viewBody.findViewById(R.id.main_listview);
		mListView.setOnItemClickListener(this);
		View v = View.inflate(mContext, R.layout.main_activity_slidingdrawer_header, null);
		mListView.addHeaderView(v);
		pAdapter = new PerAdapter(replyList);
		mListView.setAdapter(pAdapter);
		// switchAdapter(pAdapter);
		if (null != loginStr && !"".equals(loginStr)) {
			getPostList(null, loginStr, "post");
		}
		mListView.addFooterView(loadMoreBtn);
		mListView.setOnScrollListener(this);

		postBtn = (Button) v.findViewById(R.id.main_post_btn);
		replyBtn = (Button) v.findViewById(R.id.main_reply_btn);
		collectBtn = (Button) v.findViewById(R.id.main_collect_btn);
		postBtn.setOnClickListener(this);
		replyBtn.setOnClickListener(this);
		collectBtn.setOnClickListener(this);

		frameLayout_location = (FrameLayout) v.findViewById(R.id.main_pregnancy_img_location);
		frameLayout_location.setOnClickListener(this);

		perCenterNickName = (FrameLayout) v.findViewById(R.id.main_personal_center_nickname);
		perCenterNickName.setOnClickListener(this);

		perCenterHospital = (FrameLayout) v.findViewById(R.id.main_personal_center_hospital);
		perCenterHospital.setOnClickListener(this);

		changePregnancyImg = (FrameLayout) v.findViewById(R.id.main_pregnancy_img);
		changePregnancyImg.setOnClickListener(this);

		mTxtNickname = (TextView) v.findViewById(R.id.txt_nickname);
		if (nickname != null && !"".equals(nickname)) {
			mTxtNickname.setText(nickname);
		}

		pregnancyTxt = (TextView) v.findViewById(R.id.pregnancy_date_tv);
		pregnancyTxt.setText(mDateFormat.format(new java.util.Date(dateData)));

		centerHospitalTxt = (TextView) v.findViewById(R.id.txt_hospital);
		if (hospitalName == null || hospitalName.equalsIgnoreCase("")) {
			centerHospitalTxt.setText("请选择医院");
		} else {
			centerHospitalTxt.setText(hospitalName);
		}

		tv_location = (TextView) v.findViewById(R.id.main_pregnancy_text_location);

		unreadNoticeTv = (TextView) v.findViewById(R.id.tv_unread_notice);

		rlBtn = (Button) v.findViewById(R.id.main_notice_rl);
		rlBtn.setOnClickListener(this);

		mHeadImg = (ImageView) v.findViewById(R.id.head_img);
		mHeadImg.setOnClickListener(this);
		mHeadImg.setOnCreateContextMenuListener(this);

		if (com.babytree.apps.comm.util.BabytreeUtil.isPregnancy(mContext)) {
			// 如果是育儿
			TextView text1 = (TextView) v.findViewById(R.id.main_activity_slidingdrawer_header_textview1);
			text1.setText("宝宝生日:");
		}

		mTvFruit = (TextView) v.findViewById(R.id.fruit_textView1);
		mTvFruit.setOnClickListener(this);

	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
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

	private void getPostList(final String object, final String loginStr2, final String string) {
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(mContext)) {
						ret = P_BabytreeController.getUserDiscuzList(object, loginStr2, string, pageNo);
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
				postHandler.sendMessage(message);
			}
		}.start();

	}

	Handler postHandler = new Handler() {
		public void handleMessage(Message msg) {
			DataResult ret = (DataResult) msg.obj;
			loadMoreBtn.setEnabled(true);
			if (ret.status == P_BabytreeController.SUCCESS_CODE) {
				if (null != ret.data) {
					if (postChangable) {
						postAddAble = false;
						postChangable = false;
						postList = (List<Discuz>) ret.data;
						if (postList.size() >= 20) {
							loadMoreBtn.setVisibility(View.VISIBLE);
						} else {
							loadMoreBtn.setVisibility(View.GONE);
						}
						pAdapter.list.clear();
						pAdapter.list = postList;
						pAdapter.notifyDataSetChanged();
					}
					if (postAddAble) {
						postList = (List<Discuz>) ret.data;
						if (postList.size() >= 20) {
							loadMoreBtn.setVisibility(View.VISIBLE);
						} else {
							loadMoreBtn.setVisibility(View.GONE);
						}
						pAdapter.list.addAll(postList);
						pAdapter.notifyDataSetChanged();
					}
				} else {
					pAdapter.list.clear();
					pAdapter.notifyDataSetChanged();
				}
				loadMoreBtn.setText("查看下20条");
			} else {
				ExceptionUtil.catchException(ret.error, mContext);
			}
		}
	};

	private void getReplyList(final String object, final String loginStr2, final String string) {
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(mContext)) {
						ret = P_BabytreeController.getUserDiscuzList(object, loginStr2, string, pageNo);
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
				replyHandler.sendMessage(message);
			}
		}.start();

	}

	Handler replyHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mDialog != null && !mContext.isFinishing()) {
				mDialog.dismiss();
			}
			DataResult ret = (DataResult) msg.obj;
			loadMoreBtn.setEnabled(true);
			if (ret.status == P_BabytreeController.SUCCESS_CODE) {
				if (null != ret.data) {
					if (replyChangable) {
						replyAddAble = false;
						replyChangable = false;
						replyList = (List<Discuz>) ret.data;
						if (replyList.size() >= 20) {
							loadMoreBtn.setVisibility(View.VISIBLE);
						} else {
							loadMoreBtn.setVisibility(View.GONE);
						}
						pAdapter.list.clear();
						pAdapter.list = replyList;
						pAdapter.notifyDataSetChanged();
					}
					if (replyAddAble) {
						replyList = (List<Discuz>) ret.data;
						if (replyList.size() >= 20) {
							loadMoreBtn.setVisibility(View.VISIBLE);
						} else {
							loadMoreBtn.setVisibility(View.GONE);
						}
						pAdapter.list.addAll(replyList);
						pAdapter.notifyDataSetChanged();
					}
				} else {
					pAdapter.list.clear();
					pAdapter.notifyDataSetChanged();
				}
				loadMoreBtn.setText("查看下20条");
			} else {
				ExceptionUtil.catchException(ret.error, mContext);
			}
		}
	};

	private void getRLList(final String loginStr) {
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(mContext)) {
						BabytreeLog.d("用户中心-加载评论回复 参数logString getFavDiscuzList = " + loginStr);
						// ret = P_BabytreeController.getFavDiscuzList(loginStr,
						// "group_discussion", pageNo);
						ret = HomeController.getMessageListForCommentReply(loginStr, pageNo);
						// ret =
						// MessageController.getMessageListForCommentReply(loginStr,
						// pageNo);
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
				rlHandler.sendMessage(message);
			}
		}.start();

	}

	Handler rlHandler = new Handler() {
		public void handleMessage(Message msg) {

			if (mDialog != null && !mContext.isFinishing()) {
				mDialog.dismiss();
			}
			DataResult ret = (DataResult) msg.obj;
			loadMoreBtn.setEnabled(true);
			if (ret.status == P_BabytreeController.SUCCESS_CODE) {
				BabytreeLog.d("用户中心-加载评论回复 参数logString handleMessage SUCCESS_CODE = " + loginStr + " -- " + msg.obj);
				if (null != ret.data) {
					if (rlChangable) {
						rlAddAble = false;
						rlChangable = false;
						rlList = (List<Base>) ret.data;
						if (rlList.size() >= 20) {
							loadMoreBtn.setVisibility(View.VISIBLE);
						} else {
							loadMoreBtn.setVisibility(View.GONE);
						}
						rlAdapter.list.clear();
						rlAdapter.list = rlList;
						rlAdapter.notifyDataSetChanged();
					}
					if (rlAddAble) {
						rlList = (List<Base>) ret.data;
						if (rlList.size() >= 20) {
							loadMoreBtn.setVisibility(View.VISIBLE);
						} else {
							loadMoreBtn.setVisibility(View.GONE);
						}
						rlAdapter.list.addAll(rlList);
						rlAdapter.notifyDataSetChanged();
					}
				} else {
					rlAdapter.list.clear();
					rlAdapter.notifyDataSetChanged();
				}
				loadMoreBtn.setText("查看下20条");
			} else {
				rlAdapter.list.clear();
				rlAdapter.notifyDataSetChanged();
				ExceptionUtil.catchException(ret.error, mContext);
			}
		}
	};

	private void getFavorList(final String loginStr) {
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(mContext)) {
						ret = P_BabytreeController.getFavDiscuzList(loginStr, "group_discussion", pageNo);
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
				collectHandler.sendMessage(message);
			}
		}.start();

	}

	Handler collectHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mDialog != null && !mContext.isFinishing()) {
				mDialog.dismiss();
			}
			DataResult ret = (DataResult) msg.obj;
			loadMoreBtn.setEnabled(true);
			if (ret.status == P_BabytreeController.SUCCESS_CODE) {
				if (null != ret.data) {
					if (collectChangable) {
						collectAddAble = false;
						collectChangable = false;
						collectList = (List<Discuz>) ret.data;
						if (collectList.size() >= 20) {
							loadMoreBtn.setVisibility(View.VISIBLE);
						} else {
							loadMoreBtn.setVisibility(View.GONE);
						}
						pAdapter.list.clear();
						pAdapter.list = collectList;
						pAdapter.notifyDataSetChanged();
					}
					if (collectAddAble) {
						collectList = (List<Discuz>) ret.data;
						if (collectList.size() >= 20) {
							loadMoreBtn.setVisibility(View.VISIBLE);
						} else {
							loadMoreBtn.setVisibility(View.GONE);
						}
						pAdapter.list.addAll(collectList);
						pAdapter.notifyDataSetChanged();
					}
				} else {
					pAdapter.list.clear();
					pAdapter.notifyDataSetChanged();
				}
				loadMoreBtn.setText("查看下20条");
			} else {
				pAdapter.list.clear();
				pAdapter.notifyDataSetChanged();
				ExceptionUtil.catchException(ret.error, mContext);
			}
		}
	};

	public void showDialog(String title, String content, String okText, String cancleText, boolean cancelable,
			OnCancelListener btnCancle, OnClickListener btnOk) {
		mDialog = new ProgressDialog(mContext);
		mDialog.setTitle(title);
		mDialog.setMessage(content);
		mDialog.setCancelable(cancelable);
	}

	/**
	 * 列表数据适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class RLAdapter extends BaseAdapter {
		public List<Base> list = new ArrayList<Base>();

		public RLAdapter(ArrayList<Base> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			RLViewCache viewCache;
			// PinnedHeaderListViewBean bean = (PinnedHeaderListViewBean)
			// getItem(position);
			Notify bean = (Notify) getItem(position);
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.comment_reply_list_item, null);
				viewCache = new RLViewCache(convertView);
				convertView.setTag(viewCache);
			} else {
				viewCache = (RLViewCache) convertView.getTag();
			}

			LinearLayout mLayoutComment = viewCache.getLayoutComment();
			LinearLayout mLayoutReply = viewCache.getLayoutReply();
			// if (bean.title.equals("2")) {
			if ("2".equalsIgnoreCase(bean.getType() + "")) {
				mLayoutComment.setVisibility(View.VISIBLE);
				mLayoutReply.setVisibility(View.GONE);
				// final TopicComment topicComment = (TopicComment)
				// bean.getObject();
				final TopicComment topicComment = (TopicComment) bean.getData();
				TextView nickName = viewCache.getNickName();
				nickName.setText(topicComment.reply_user_nickname);
				TextView title = viewCache.getTitle();
				String titleStr = topicComment.reply_user_nickname + "   在话题  " + "<font color=\"#67c9fb\">"
						+ topicComment.topic_title + "</font>" + "中回复了你 ";
				title.setText(Html.fromHtml(titleStr));
				TextView content = viewCache.getContent();
				content.setText(topicComment.reply_user_content);
				TextView myContent = viewCache.getMyReply();
				myContent.setText(topicComment.my_reply_content);
				TextView time = viewCache.getReplyTime();
				time.setText(com.babytree.apps.comm.util.BabytreeUtil.formatTimestampForNotice(Long
						.parseLong(topicComment.reply_user_ts)));
				LinearLayout mLinearLayout = viewCache.getLayout();
				mLinearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						TopicNewActivity.launch(mContext, Integer.parseInt(topicComment.topic_id),
								Integer.parseInt(topicComment.topic_reply_page));
					}
				});

			} else if ("1".equalsIgnoreCase(bean.getType() + "")) {
				// if (bean.title.equals("1")) {
				mLayoutComment.setVisibility(View.GONE);
				mLayoutReply.setVisibility(View.VISIBLE);
				// final TopicReply topicReply = (TopicReply) bean.getObject();
				final TopicReply topicReply = (TopicReply) bean.getData();
				TextView topicTitle = viewCache.getTopicTitle();
				topicTitle.setText(topicReply.topic_title);
				TextView replyCount = viewCache.getTopicReplyCount();
				replyCount.setText("  有" + topicReply.topic_reply_unread_count + "条新回帖");

				TextView time = viewCache.getReplyTime();
				time.setText(com.babytree.apps.comm.util.BabytreeUtil.formatTimestampForNotice(Long
						.parseLong(topicReply.topic_last_reply_ts)));

				LinearLayout mLinearLayout = viewCache.getLayout();
				mLinearLayout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						TopicNewActivity.launch(mContext, Integer.parseInt(topicReply.topic_id),
								Integer.parseInt(topicReply.topic_reply_page));
					}
				});
			}

			return convertView;
		}

		class RLViewCache {
			private View baseView;

			private TextView replyTime;

			private LinearLayout layoutComment, layoutReply, layout;

			private TextView nickName;

			private TextView title;

			private TextView content;

			private TextView myReply;

			private TextView topicTitle;

			private TextView topicReplyCount;

			public RLViewCache(View view) {
				baseView = view;
			}

			public TextView getReplyTime() {
				if (replyTime == null) {
					replyTime = (TextView) baseView.findViewById(R.id.tv_reply_time);
				}
				return replyTime;
			}

			public TextView getTitle() {
				if (title == null) {
					title = (TextView) baseView.findViewById(R.id.tv_title);
				}
				return title;
			}

			public TextView getNickName() {
				if (nickName == null) {
					nickName = (TextView) baseView.findViewById(R.id.tv_name);
				}
				return nickName;
			}

			public TextView getContent() {
				if (content == null) {
					content = (TextView) baseView.findViewById(R.id.tv_content);
				}
				return content;
			}

			public TextView getMyReply() {
				if (myReply == null) {
					myReply = (TextView) baseView.findViewById(R.id.tv_my_reply);
				}
				return myReply;
			}

			public TextView getTopicTitle() {
				if (topicTitle == null) {
					topicTitle = (TextView) baseView.findViewById(R.id.tv_topic_title);
				}
				return topicTitle;
			}

			public TextView getTopicReplyCount() {
				if (topicReplyCount == null) {
					topicReplyCount = (TextView) baseView.findViewById(R.id.tv_topic_reply_count);
				}
				return topicReplyCount;
			}

			public LinearLayout getLayoutComment() {
				if (layoutComment == null) {
					layoutComment = (LinearLayout) baseView.findViewById(R.id.layout_comment);
				}
				return layoutComment;
			}

			public LinearLayout getLayoutReply() {
				if (layoutReply == null) {
					layoutReply = (LinearLayout) baseView.findViewById(R.id.layout_reply);
				}
				return layoutReply;
			}

			public LinearLayout getLayout() {
				if (layout == null) {
					layout = (LinearLayout) baseView.findViewById(R.id.layout);
				}
				return layout;
			}
		}

	}

	/**
	 * 列表数据适配器
	 * 
	 * @author Administrator
	 * 
	 */
	private class PerAdapter extends BaseAdapter {
		public List<Discuz> list = new ArrayList<Discuz>();

		public PerAdapter(List<Discuz> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			ViewCache viewCache;
			final Discuz bean = (Discuz) getItem(position);
			if (convertView == null) {
				convertView = View.inflate(mContext, R.layout.forum_item, null);
				viewCache = new ViewCache(convertView);
				convertView.setTag(viewCache);
			} else {
				viewCache = (ViewCache) convertView.getTag();
			}
			ImageView is_jing = viewCache.getJingImg();
			if (bean.is_elite == 1) {
				is_jing.setVisibility(View.VISIBLE);
			} else {
				is_jing.setVisibility(View.GONE);
			}

			ImageView headImg = viewCache.getHeadImg();
			headImg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, UserinfoNewActivity.class);
					Bundle bl = new Bundle();
					bl.putString("user_encode_id", bean.author_id);
					intent.putExtras(bl);
					startActivity(intent);
				}
			});

			String headUrl = bean.author_avatar;
			ImageView imageView = viewCache.getHeadImg();
			imageView.setVisibility(View.VISIBLE);
			// ---------------------缓存模块start--------------------------
			bitmapCache.display(imageView, headUrl);
			// ---------------------缓存模块end----------------------------

			// Set Title Begin
			EmojiTextView title = viewCache.getTitle();
			String titleMessage = bean.title;

			if (bean.is_fav == 1) {
				titleMessage += "<img src=\"ic_picture\">";
			}
			if (bean.is_top == 1) {
				titleMessage += "<img src=\"ic_ding\">";
			}

			title.setEmojiText(titleMessage);
			// Set Title End
			TextView responseCount = viewCache.getResponseCount();
			responseCount.setText(String.valueOf(bean.response_count));

			TextView pvCount = viewCache.getPvCount();
			pvCount.setText(String.valueOf(bean.pv_count));

			TextView authorName = viewCache.getAuthorName();
			authorName.setText(bean.author_name);
			TextView lastResponseTs = viewCache.getLastResponseTs();
			lastResponseTs.setText(BabytreeUtil.formatTimestamp(bean.last_response_ts));

			LinearLayout mLayoutItem = viewCache.getLayoutItem();

			mLayoutItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Discuz discuz = (Discuz) pAdapter.list.get(position);
					TopicNewActivity.launch(mContext, discuz.discuz_id, 0);
				}

			});
			return convertView;
		}

	}

	class ViewCache {
		private View baseView;

		private EmojiTextView title;

		private TextView response_count;

		private TextView pv_count;

		private TextView author_name;

		private ImageView img_head;

		private TextView last_response_ts;

		private LinearLayout title_bar;

		private LinearLayout layout_item;

		private TextView txt_message;

		private ImageView jing_img;

		public ViewCache(View view) {
			baseView = view;
		}

		public EmojiTextView getTitle() {
			if (title == null) {
				title = (EmojiTextView) baseView.findViewById(R.id.txt_title);
			}
			return title;
		}

		public LinearLayout getTitleBar() {
			if (title_bar == null) {
				title_bar = (LinearLayout) baseView.findViewById(R.id.title_bar);
			}
			return title_bar;
		}

		public LinearLayout getLayoutItem() {
			if (layout_item == null) {
				layout_item = (LinearLayout) baseView.findViewById(R.id.layout_item);
			}
			return layout_item;
		}

		public TextView getTxtMessage() {
			if (txt_message == null) {
				txt_message = (TextView) baseView.findViewById(R.id.txt_message);
			}
			return txt_message;
		}

		public TextView getResponseCount() {
			if (response_count == null) {
				response_count = (TextView) baseView.findViewById(R.id.txt_response_count);
			}
			return response_count;
		}

		public TextView getPvCount() {
			if (pv_count == null) {
				pv_count = (TextView) baseView.findViewById(R.id.txt_pv_count);
			}
			return pv_count;
		}

		public TextView getAuthorName() {
			if (author_name == null) {
				author_name = (TextView) baseView.findViewById(R.id.txt_author_name);
			}
			return author_name;
		}

		public TextView getLastResponseTs() {
			if (last_response_ts == null) {
				last_response_ts = (TextView) baseView.findViewById(R.id.txt_last_response_ts);
			}
			return last_response_ts;
		}

		public ImageView getJingImg() {
			if (jing_img == null) {
				jing_img = (ImageView) baseView.findViewById(R.id.jing_img);
			}
			return jing_img;
		}

		public ImageView getHeadImg() {
			if (img_head == null) {
				img_head = (ImageView) baseView.findViewById(R.id.iv_head);
			}
			return img_head;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Discuz discuz = (Discuz) pAdapter.list.get(position - 1);

		TopicNewActivity.launch(mContext, discuz.discuz_id, 0);

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int itemsLastIndex = pAdapter.getCount() - 1; // 数据集最后一项的索引
		int lastIndex = itemsLastIndex + 1; // 加上底部的loadMoreView项
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
			// 如果是自动加载,可以在这里放置异步加载数据的代码
			if (null != loginStr && !"".equals(loginStr)) {
				if (buttonType == BUTTON_POST) {
					postAddAble = true;
					pageNo++;
					getPostList(null, loginStr, "post");
				}
				if (buttonType == BUTTON_REPLY) {
					replyAddAble = true;
					pageNo++;
					getReplyList(null, loginStr, "reply");
				}
				if (buttonType == BUTTON_COLLECT) {
					collectAddAble = true;
					pageNo++;
					getFavorList(loginStr);
				}
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
	}

	private void savePersonalInfo(final String loginStr, final String sexStr, final String positionStr,
			final String mBirthday) {
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();

				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(mContext)) {
						ret = HomeController.savePersonalInfo(loginStr, sexStr, positionStr, mBirthday);
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
				mHandler.sendMessage(message);
			}
		}.start();
	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mDialog != null && !mContext.isFinishing()) {
				mDialog.dismiss();
			}
			DataResult ret = (DataResult) msg.obj;
			if (ret.status == P_BabytreeController.SUCCESS_CODE) {
				Toast.makeText(mContext, "信息保存成功!", Toast.LENGTH_SHORT).show();
				SharedPreferencesUtil.setValue(mContext, ShareKeys.GENDER, sexStr);
				SharedPreferencesUtil.setValue(mContext, ShareKeys.LOCATION, String.valueOf(_id));
				positionStr = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOCATION);
				if (positionStr != null && !positionStr.equals("")) {
					location2 = mDbController.getLocationById(Integer.parseInt(positionStr));
					if (location2 != null) {
						if (location2.province != null && !location2.province.equals("")) {
							location1 = mDbController.getLocationById(Integer.parseInt(location2.province));
						}
					}
					if (location1 != null) {
						locationStr = location1.name + "  " + location2.name;
					}
				} else {
					locationStr = "";
				}
				tv_location.setText(locationStr);

			} else {
				Toast.makeText(mContext, ret.message, Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void doNet(final String loginStr) {

		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(mContext)) {
						ret = BabyTreeFruitController.getFruit(loginStr);
						if (ret.status == P_BabytreeController.SUCCESS_CODE) {
							/*
							 * 临时在这里直接返回水果数 以后重构抛弃这个方式 只返回水果树
							 */
							String num = "";
							num = ((Total) (ret.data)).fruit_total;
							message.obj = num;
							fruitHander.sendMessage(message);
						} else {

						}
					} else {
						// ret = new DataResult();
						// ret.message =
						// BabytreeController.NetworkExceptionMessage;
						// ret.status = BabytreeController.NetworkExceptionCode;
					}
				} catch (Exception e) {
					// ret = new DataResult();
					// ret.message = BabytreeController.SystemExceptionMessage;
					// ret.status = BabytreeController.SystemExceptionCode;
				}
			}

		}.start();
	}

	public Handler fruitHander = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			fruit_total = msg.obj.toString() + "水果";
			mTvFruit.setText(fruit_total);
		}
	};

	@Override
	protected void getBitmap(Bitmap bitmap) {
		SharedPreferencesUtil.setValue(mContext, ShareKeys.IMAGE, mBitmapPath);
		if (loginStr != null && !loginStr.equals("")) {
			processPhoto(loginStr, mBitmapPath);
		} else {
			mHeadImg.setImageBitmap(bitmap);
		}
	}

	private void processPhoto(final String loginString, final String imageAbsPath) {
		new UpLoadHead(mContext).execute(loginString, imageAbsPath);
	}

	/**
	 * 上传头像
	 * 
	 * @author wangbingqi
	 * 
	 */
	private class UpLoadHead extends BabytreeAsyncTask {

		public UpLoadHead(Context context) {
			super(context);
		}

		@Override
		protected DataResult toNet(String[] params) {
			return HomeController.postPhotoForMain(params[0], params[1]);
		}

		@Override
		protected void success(DataResult result) {
			Toast.makeText(mContext, "上传头像成功", Toast.LENGTH_SHORT).show();
			String head = result.data.toString();
			SharedPreferencesUtil.setValue(mContext, ShareKeys.HEAD, head);
			if (loginStr != null && head != null) {
				initiaAvatar(mHeadImg, head);
			}
		}

		@Override
		protected void failure(DataResult result) {
			Toast.makeText(mContext, "上传头像失败", Toast.LENGTH_SHORT).show();
			ExceptionUtil.catchException(result.error, mContext);
		}

		@Override
		protected String getDialogMessage() {
			return "头像正在上传中，请稍后...";
		}
	}

	private void initiaAvatar(final ImageView mImageView2, String head) {
		bitmapCache.display(mImageView2, head);
	}
}
