package com.babytree.apps.comm.ui;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.biz.father.FatherIntrActivity;
import com.babytree.apps.biz.father.ctr.FatherController;
import com.babytree.apps.biz.father.model.BindStatus;
import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.BabytreeController;
import com.babytree.apps.comm.ctr.BaseController;
import com.babytree.apps.comm.ctr.SignInController;
import com.babytree.apps.comm.model.UserAddInfo;
import com.babytree.apps.comm.net.BabytreeAsyncTask;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.umeng.xp.Promoter;
import com.umeng.xp.controller.ExchangeDataService;
import com.umeng.xp.controller.XpListenersCenter.ExchangeDataRequestListener;

/**
 * 
 * 签到加孕气页
 * 
 */
public class SignInActivity extends BabytreeTitleAcitivty implements OnClickListener {

	private BabytreeBitmapCache bitmapCache;// 缓存对象
	private Context mContext;

	private TextView mTvgetPregLuckyCount, mTvSignDay, mTvFriendCount, mTvInviteCode;
	private TextView mTvMyLuckyCount;
	/**
	 * 爸爸添加的孕气
	 */
	private TextView mTvYQDaddy;
	private ImageView mImgTree, mImgKeduValue;
	private ProgressDialog mDialog;
	private Button mBtnSignAddLucky;
	/**
	 * 邀请爸爸
	 */
	private Button mBtnInviteFather;

	private String mInviteCode = "0", mInviteFriendCount = "0", mLotteryCount = "0";

	private String loginStr;

	private String mIsSign = "0";

	private TextView mTvGiftTxt1, mTvGiftTxt2, mTvGiftTxt3, mTvGiftTxt4;

	private ImageView mImgGif1, mImgGif2, mImgGif3, mImgGif4;

	private TextView mTvLevel;

	private LinearLayout myGallery;

	private HorizontalScrollView horizontalScrollView;

	private static String TAB_SIGNIN = "signin";

	private List<ImageView> mList;
	private List<TextView> mListTv;

	private Handler mHsvHandler = new Handler();

	private UserAddInfo mUserAddInfo;

	private TextView mTvAppRec;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;

		// ---------------------缓存模块start--------------------------
		bitmapCache = BabytreeBitmapCache.create(mContext);
		// ---------------------缓存模块end----------------------------

		loginStr = SharedPreferencesUtil.getStringValue(this, ShareKeys.LOGIN_STRING);
		mTvAppRec = (TextView) findViewById(R.id.tv_app_rec);
		myGallery = (LinearLayout) findViewById(R.id.mygallery);
		horizontalScrollView = (HorizontalScrollView) findViewById(R.id.hsv_scrollview);
		mBtnSignAddLucky = (Button) findViewById(R.id.btn_sign_add_lucky);
		mBtnSignAddLucky.setOnClickListener(this);
		mBtnInviteFather = (Button) findViewById(R.id.btn_invite_father);
		mBtnInviteFather.setOnClickListener(this);
		findViewById(R.id.btn_invite_friend).setOnClickListener(this);
		findViewById(R.id.btn_apply_lucky1).setOnClickListener(this);
		mTvMyLuckyCount = (TextView) findViewById(R.id.tv_my_lucky_count);
		mTvYQDaddy = (TextView) findViewById(R.id.yunqi_add_by_daddy);
		mTvgetPregLuckyCount = (TextView) findViewById(R.id.tv_sign_lucky_count);
		mTvSignDay = (TextView) findViewById(R.id.tv_day_sign);
		mTvFriendCount = (TextView) findViewById(R.id.tv_invite_friend_count);
		mTvInviteCode = (TextView) findViewById(R.id.tv_invite_code);
		mImgTree = (ImageView) findViewById(R.id.img_tree);
		mImgKeduValue = (ImageView) findViewById(R.id.img_kedu_value);

		mTvGiftTxt1 = (TextView) findViewById(R.id.tv_gift_txt1);
		mTvGiftTxt2 = (TextView) findViewById(R.id.tv_gift_txt2);
		mTvGiftTxt3 = (TextView) findViewById(R.id.tv_gift_txt3);
		mTvGiftTxt4 = (TextView) findViewById(R.id.tv_gift_txt4);

		mTvLevel = (TextView) findViewById(R.id.tv_level);

		mImgGif1 = (ImageView) findViewById(R.id.img_gift1);
		mImgGif2 = (ImageView) findViewById(R.id.img_gift2);
		mImgGif3 = (ImageView) findViewById(R.id.img_gift3);
		mImgGif4 = (ImageView) findViewById(R.id.img_gift4);
		initAds();
	}

	@Override
	protected void onResume() {
		process();
		super.onResume();
	}

	private View insertImage(List<ImageView> mList, List<TextView> mListTv) {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels / 5;

		LinearLayout layout = new LinearLayout(getApplicationContext());
		layout.setLayoutParams(new LayoutParams(width - 10, LayoutParams.WRAP_CONTENT));
		layout.setGravity(Gravity.CENTER);
		layout.setOrientation(LinearLayout.VERTICAL);

		ImageView imageView = new ImageView(getApplicationContext());
		imageView.setLayoutParams(new LayoutParams(width - 20, width - 10));
		imageView.setImageResource(R.drawable.avatar_big);

		TextView textView = new TextView(getApplicationContext());
		textView.setLayoutParams(new LayoutParams(width - 10, LayoutParams.WRAP_CONTENT));
		textView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 10);
		mList.add(imageView);
		mListTv.add(textView);
		layout.addView(imageView);
		layout.addView(textView);
		return layout;

	}

	private Runnable mSVRunnable = new Runnable() {
		@Override
		public void run() {
			horizontalScrollView.scrollTo(200, 0);// 改变滚动条的位置
		}
	};

	private void initAds() {
		final ExchangeDataService exchangeDataService = new ExchangeDataService();
		exchangeDataService.setKeywords(TAB_SIGNIN);// 设置分组的关键词。
		exchangeDataService.autofill = 0; // 自主广告数量小的情况下，不要自动填充来自交往网络的广告。
		exchangeDataService.requestDataAsyn(this, new ExchangeDataRequestListener() {

			@Override
			public void dataReceived(int status, List<Promoter> list) {
				if (status == 1 && list != null && list.size() > 0) {
					mTvAppRec.setVisibility(View.VISIBLE);
					mList = new ArrayList<ImageView>(list.size());
					mListTv = new ArrayList<TextView>(list.size());
					for (int i = 0; i < list.size(); i++) {
						myGallery.addView(insertImage(mList, mListTv));
						// com.umeng.xp.view
					}
					mHsvHandler.post(mSVRunnable);
					for (int j = 0; j < list.size(); j++) {
						final ImageView imageView = mList.get(j);
						TextView textView = mListTv.get(j);
						String title = list.get(j).title;
						textView.setText(title);
						final Promoter promoter = list.get(j);
						imageView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								exchangeDataService.clickOnPromoter(promoter);
							}
						});

						// Drawable cacheDrawable =
						// mAsyncImageLoader.loadDrawable(list.get(j).icon,
						// getApplicationContext(), true, new ImageCallback() {
						//
						// @Override
						// public void imageLoaded(Drawable imageDrawable,
						// String imageUrl) {
						// ImageView tagImage = (ImageView)
						// imageView.findViewWithTag(imageUrl);
						// if (tagImage != null) {
						// if (imageDrawable != null) {
						// tagImage.setImageDrawable(imageDrawable);
						// }
						// }
						// }
						// });
						// if (cacheDrawable != null) {
						// imageView.setImageDrawable(cacheDrawable);
						// }

						// ---------------------缓存模块start--------------------------
						bitmapCache.display(imageView, list.get(j).icon);
						// ---------------------缓存模块end----------------------------
					}
				}
			}
		});

	}

	public void showDialog(String title, String content, String okText, String cancleText, boolean cancelable,
			OnCancelListener btnCancle, OnClickListener btnOk) {
		try {
			if (mDialog != null && !SignInActivity.this.isFinishing()) {
				mDialog.dismiss();
				mDialog = null;
			}
			mDialog = new ProgressDialog(this);
			mDialog.setTitle(title);
			mDialog.setMessage(content);
			mDialog.setCancelable(cancelable);
			if (!mDialog.isShowing()) {
				mDialog.show();
			}
		} catch (Exception e) {
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
				mUserAddInfo = bean;
				mLotteryCount = bean.lottery_value;
				mInviteCode = bean.invitation_code;
				mInviteFriendCount = bean.invite_value;
				mIsSign = bean.is_sign;
				String treeImgUrl = bean.tree_image;
				mTvMyLuckyCount.setText(bean.pre_value);
				mTvInviteCode.setText(bean.invitation_code);
				mTvFriendCount.setText(bean.invite_value);
				mTvgetPregLuckyCount.setText(bean.sign_value);
				mTvSignDay.setText(bean.sign_days);
				if (mIsSign.equals("0")) {
//					mBtnSignAddLucky.setBackgroundResource(R.drawable.btn_lucky_normal);
					mBtnSignAddLucky.setText("签到领孕气");
				} else {
					mBtnSignAddLucky.setBackgroundResource(R.drawable.img_signed);
					mBtnSignAddLucky.setText("已签到");
					mBtnSignAddLucky.setClickable(false);
				}
				if (treeImgUrl != null && !treeImgUrl.equals("")) {

					// ---------------------缓存模块start--------------------------
					bitmapCache.display(mImgTree, treeImgUrl);
					// ---------------------缓存模块end----------------------------
				}
				CalPregnancyLuckyValue(bean.pre_value);

				// 获取绑定状态
				getBindStatus();
				break;
			default:
				Toast.makeText(SignInActivity.this, "亲，你的网络不给力", Toast.LENGTH_SHORT).show();
				ExceptionUtil.catchException(ret.error, SignInActivity.this);
				break;
			}
		}
	};

	private void process() {
		showDialog(null, "加载中...", null, null, true, null, null);
		new Thread() {

			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(SignInActivity.this)) {
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

	protected void CalPregnancyLuckyValue(String pre_value) {
		Float luckyValue = Float.parseFloat(pre_value);
		float per = 0;
		if (luckyValue < 50) {
			per = luckyValue / 50;
			mTvLevel.setText("L0");
		} else if (luckyValue >= 50 && luckyValue < 200) {
			per = (luckyValue - 50) / (200 - 50);
			mTvGiftTxt1.setText("已获得");
			mImgGif1.setBackgroundResource(R.drawable.b1);
			mTvLevel.setText("L1");
		} else if (luckyValue >= 200 && luckyValue < 1000) {
			per = (luckyValue - 200) / (1000 - 200);
			mTvGiftTxt1.setText("已获得");
			mTvGiftTxt2.setText("已获得");
			mImgGif1.setBackgroundResource(R.drawable.b1);
			mImgGif2.setBackgroundResource(R.drawable.b2);
			mTvLevel.setText("L2");
		} else if (luckyValue >= 1000 && luckyValue < 3000) {
			per = (luckyValue - 1000) / (3000 - 1000);
			mTvGiftTxt1.setText("已获得");
			mTvGiftTxt2.setText("已获得");
			mTvGiftTxt3.setText("已获得");
			mImgGif1.setBackgroundResource(R.drawable.b1);
			mImgGif2.setBackgroundResource(R.drawable.b2);
			mImgGif3.setBackgroundResource(R.drawable.b3);
			mTvLevel.setText("L3");
		} else if (luckyValue >= 3000) {//
			per = 1f;
			mTvGiftTxt1.setText("已获得");
			mTvGiftTxt2.setText("已获得");
			mTvGiftTxt3.setText("已获得");
			mTvGiftTxt4.setText("已获得");
			mImgGif1.setBackgroundResource(R.drawable.b1);
			mImgGif2.setBackgroundResource(R.drawable.b2);
			mImgGif3.setBackgroundResource(R.drawable.b3);
			mImgGif4.setBackgroundResource(R.drawable.b4);
			mTvLevel.setText("L4");
		}
		LayoutParams para;
		para = mImgKeduValue.getLayoutParams();
		para.height = (int) (per * 158);
		para.width = LayoutParams.WRAP_CONTENT;
		mImgKeduValue.setLayoutParams(para);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_sign_add_lucky:
			signAndAddLucky();
			break;
		case R.id.btn_invite_father:
			startActivity(new Intent(this, FatherIntrActivity.class));
			break;
		case R.id.btn_invite_friend:
			if (mInviteCode == null || mInviteCode.equals("")) {
				mInviteCode = "0";
			}
			if (mLotteryCount == null || mLotteryCount.equals("")) {
				mLotteryCount = "0";
			}
			if (mInviteFriendCount == null || mInviteFriendCount.equals("")) {
				mInviteFriendCount = "0";
			}
			if (mUserAddInfo != null) {
				startActivityForResult(new Intent(this, InviteActivity.class).putExtra("invite_code", mInviteCode)
						.putExtra("lottery_count", mLotteryCount).putExtra("invite_friend_count", mInviteFriendCount),
						111);
			} else {
				Toast.makeText(SignInActivity.this, "未获取到相关信息", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btn_apply_lucky1:
			startActivity(new Intent(this, ReceiveActivity.class));
			break;
		}
	}

	private Handler signHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (mDialog != null)
				mDialog.dismiss();
			DataResult ret = (DataResult) msg.obj;
			switch (ret.status) {
			case BabytreeController.SUCCESS_CODE:
				mBtnSignAddLucky.setBackgroundResource(R.drawable.img_signed);
				mBtnSignAddLucky.setText("您已签到");
				mBtnSignAddLucky.setClickable(false);
				process();
				break;
			default:
				Toast.makeText(SignInActivity.this, "亲，你的网络不给力", Toast.LENGTH_SHORT).show();
				ExceptionUtil.catchException(ret.error, SignInActivity.this);
				break;
			}
		}
	};

	private void signAndAddLucky() {
		showDialog(null, "提交中...", null, null, true, null, null);
		new Thread() {

			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(SignInActivity.this)) {
						ret = SignInController.signInAndAddLucky(loginStr);
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
				signHandler.sendMessage(message);
			}

		}.start();

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == 111) {
				process();
			}
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
		return "签到加孕气";
	}

	@Override
	public int getBodyView() {
		return R.layout.sign_in_activity;
	}

	/**
	 * 显示爸爸添加的孕气
	 * 
	 * @param bindStatus
	 *            绑定状态
	 */
	private void showYQDaddy(BindStatus bindStatus) {
		if (bindStatus != null) {
			if ("1".equalsIgnoreCase(bindStatus.getBindStatus())) {// 已绑定
				String style = "<font color=\"#ef6484\">$T</font>";
				style = style.replace("$T", mUserAddInfo.baba_yunqi + "");
				String yqInfo = getResources().getString(R.string.s_yunqi_add_by_daddy);
				yqInfo = String.format(yqInfo, style);
				mTvYQDaddy.setText(Html.fromHtml(yqInfo));
				mTvYQDaddy.setVisibility(View.VISIBLE);
				// 隐藏邀请爸爸加孕气按钮
				mBtnInviteFather.setVisibility(View.GONE);
			} else {
				mTvYQDaddy.setVisibility(View.GONE);
				// 显示邀请爸爸加孕气按钮
				mBtnInviteFather.setVisibility(View.VISIBLE);
			}
		}
	}

	/**
	 * 获取绑定状态
	 */
	private void getBindStatus() {
		new BindStatusTask(this).execute(getLoginString(), getGender());
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
				showYQDaddy(bindStatus);
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(mContext, BaseController.JSONExceptionMessage, Toast.LENGTH_SHORT).show();
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
}
