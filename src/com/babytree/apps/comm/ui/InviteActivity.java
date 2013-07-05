package com.babytree.apps.comm.ui;

import java.util.ArrayList;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.Photo;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.ctr.SignInController;
import com.babytree.apps.comm.model.Prize;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.Md5Util;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.umeng.socialize.bean.SHARE_MEDIA;

/**
 * 添加好友
 * 
 */
public class InviteActivity extends BabytreeTitleAcitivty implements OnClickListener {

	private BabytreeBitmapCache bitmapCache;// 缓存对象

	public Context mContext;

	/**
	 * 抽奖按钮
	 */
	protected Button btn_ok;

	/**
	 * 电话本列表
	 */
	protected Button btnInviteFriend;

	/**
	 * 通讯录按钮
	 */
	protected RadioButton rb_contact;

	/**
	 * 腾讯微博按钮
	 */
	protected RadioButton rb_ten;

	/**
	 * 新浪微博按钮
	 */
	protected RadioButton rb_sin;

	/**
	 * 提交抽奖信息
	 */
	protected Button bt_sendmsg;

	/**
	 * 申请规则
	 */
	protected TextView tv_help;

	/**
	 * 金蛋
	 */
	protected ImageView iv_pic;

	/**
	 * 添加好友只界面
	 */
	protected RelativeLayout rl_relativelayout1;
	/**
	 * 添加好友抽奖提交页面
	 */
	protected RelativeLayout rl_relativelayout2;
	/**
	 * 查询规则 webView页面
	 */
	protected RelativeLayout rl_relativelayout3;
	/**
	 * 查询规则view
	 */
	protected WebView wv_view;

	/**
	 * 获取库Phon表字段
	 */
	private static final String[] PHONES_PROJECTION = new String[] { Phone.DISPLAY_NAME, Phone.NUMBER, Photo.PHOTO_ID,
			Phone.CONTACT_ID };

	/** 联系人显示名称 **/
	private static final int PHONES_DISPLAY_NAME_INDEX = 0;

	/** 电话号码 **/
	private static final int PHONES_NUMBER_INDEX = 1;

	/** 联系人名称 **/
	private ArrayList<String> mContactsName = new ArrayList<String>();

	/** 联系人头像 **/
	private ArrayList<String> mContactsNumber = new ArrayList<String>();

	// /**
	// * 是否可以抽奖
	// */
	// private boolean islottery=false;

	/**
	 * 当前页状态
	 */
	private PAGE page = PAGE.FIRST;

	/**
	 * 短信内容
	 */
	private String SMS_MESSAGE = "可能帮你增加孕气的人“的人名打开短信分享内容分享内容文案” 我在“快乐孕期”为我的宝宝收集孕气值，还差一点就能为我宝宝兑换礼品了，快帮忙下载“快乐孕期” 填入邀请码 0403 ，帮我加孕期值吧：下载地址http://";

	/**
	 * 对话框
	 */
	private ProgressDialog mDialog;

	/**
	 * 邀请码
	 */
	private String mInviteCode = "12345";

	/**
	 * 抽奖次数
	 */
	private String mLotteryCount = "0";
	/**
	 * 邀请好友数
	 */
	private String mInviteFriendCount = "";

	/**
	 * 抽奖响应数据
	 */
	private Prize prize;
	/**
	 * 奖品图片
	 */
	private ImageView iv_view5;
	/**
	 * 奖品名称
	 */
	private TextView tv_name;
	/**
	 * 奖品图片地址
	 */
	private String pic_url = "";
	/**
	 * 抽奖次数提示
	 */
	private TextView tv_count;
	/**
	 * 邀请好友数
	 */
	private TextView tv_num;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.mContext = this;

		// ---------------------缓存模块start--------------------------
		bitmapCache = BabytreeBitmapCache.create(mContext);
		// ---------------------缓存模块end----------------------------

		mInviteCode = getIntent().getStringExtra("invite_code");
		mLotteryCount = getIntent().getStringExtra("lottery_count");
		mInviteFriendCount = getIntent().getStringExtra("invite_friend_count");

		SMS_MESSAGE = "我正在使用一款非常实用的客户端“快乐孕期”，分享给同样是准父母的你，这里有孕期同步提醒及知识、最新母婴咨询、同龄同地上百万父母互动讨论、各种有奖活动。下载地址：http://r.babytree.com/wztb5x"
				+ " 邀请码" + mInviteCode + " （输入编码给我助力吧）";

		btn_ok = (Button) findViewById(R.id.button1);
		rb_contact = (RadioButton) findViewById(R.id.radioButton1);
		rb_ten = (RadioButton) findViewById(R.id.radioButton2);
		rb_sin = (RadioButton) findViewById(R.id.radioButton3);
		tv_help = (TextView) findViewById(R.id.textView2);
		btnInviteFriend = (Button) findViewById(R.id.btn_send_invite);
		btnInviteFriend.setOnClickListener(this);
		iv_pic = (ImageView) findViewById(R.id.invite_imageView1);
		iv_view5 = (ImageView) findViewById(R.id.invite_imageView5);
		iv_view5.setVisibility(View.GONE);

		tv_name = (TextView) findViewById(R.id.textView4);

		tv_count = (TextView) findViewById(R.id.invite_textView0);
		tv_count.setText(Html.fromHtml("剩余抽奖次数<font color=\"#ff0000\">" + mLotteryCount + "</font>"));
		tv_num = (TextView) findViewById(R.id.invite_textView4);
		tv_num.setText(Html.fromHtml("每邀请一位好友可进行一次抽奖,目前邀请了<font color=\"#ff0000\">" + mInviteFriendCount + "</font>"
				+ "位好友"));

		rl_relativelayout1 = (RelativeLayout) findViewById(R.id.invite_relativelayout1);
		rl_relativelayout2 = (RelativeLayout) findViewById(R.id.invite_relativelayout2);
		rl_relativelayout3 = (RelativeLayout) findViewById(R.id.invite_relativelayout3);
		wv_view = (WebView) findViewById(R.id.invite_webView1);

		bt_sendmsg = (Button) findViewById(R.id.button2);
		bt_sendmsg.setOnClickListener(this);

		rb_contact.setOnClickListener(this);
		rb_ten.setOnClickListener(this);
		rb_sin.setOnClickListener(this);
		tv_help.setOnClickListener(this);

		lottery(mLotteryCount.equalsIgnoreCase("0") ? false : true);
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		rl_relativelayout1.setVisibility(View.VISIBLE);
		rl_relativelayout2.setVisibility(View.GONE);
		rl_relativelayout3.setVisibility(View.GONE);
		page = PAGE.FIRST;
	}

	/**
	 * 是否润徐抽奖
	 */
	private void lottery(boolean isb) {
		if (isb) {
			btn_ok.setOnClickListener(this);
			btn_ok.setBackgroundDrawable(getResources().getDrawable(R.drawable.dianjichoujiang));
			btn_ok.setText(getResources().getString(R.string.invite_str11));
			iv_pic.setImageDrawable(getResources().getDrawable(R.drawable.invite_egg2));

		} else {
			btn_ok.setOnClickListener(null);
			btn_ok.setBackgroundDrawable(getResources().getDrawable(R.drawable.bukechoujiang));
			btn_ok.setText(getResources().getString(R.string.invite_str2));
			iv_pic.setImageDrawable(getResources().getDrawable(R.drawable.invite_egg1));
		}

	}

	/**
	 * 点击抽奖
	 */
	private void onClickLottery() {

		showDialog(null, "加载中...", null, null, true, null, null);
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(InviteActivity.this)) {
						String mLoginString = SharedPreferencesUtil.getStringValue(InviteActivity.this,
								ShareKeys.LOGIN_STRING);
						// String mLoginString =
						// "u8537139711_21179cd974730bc40caca48230121177_1354271156";
						ret = SignInController.toLottery(mLoginString);
					} else {
						ret = new DataResult();
						ret.message = P_BabytreeController.NetworkExceptionMessage;
						ret.status = P_BabytreeController.NetworkExceptionCode;
					}
				} catch (Exception e) {
					ret = new DataResult();
					ret.message = P_BabytreeController.SystemExceptionMessage;
					ret.status = P_BabytreeController.SystemExceptionCode;
					ret.error = ExceptionUtil.printException(e).toString();
				}
				message.obj = ret;
				message.what = 10;
				hander.sendMessage(message);
			}

		}.start();
	}

	/**
	 * 推荐按钮
	 * 
	 * @param num
	 *            号码
	 * @param name
	 *            名字
	 */
	private void onClickContacts(String num, String name) {
		Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:" + num));
		intent.putExtra("sms_body", SMS_MESSAGE);
		startActivity(intent);
	}

	/**
	 * 新浪微博分享
	 */
	private void onClickSin() {
		mApplication.getUmSocialService().setShareImage(null);
		mApplication.getUmSocialService().setShareContent(SMS_MESSAGE);
		mApplication.getUmSocialService().directShare(this, SHARE_MEDIA.SINA, null);
	}

	/**
	 * 腾讯微博分享
	 */
	private void onClickTen() {
		mApplication.getUmSocialService().setShareImage(null);
		mApplication.getUmSocialService().setShareContent(SMS_MESSAGE);
		mApplication.getUmSocialService().directShare(this, SHARE_MEDIA.TENCENT, null);
	}

	/**
	 * 左上角返回键
	 */
	protected void onBack() {
		switch (page) {
		case FIRST:
			setResult(RESULT_OK, new Intent().putExtra("mLotteryCount", mLotteryCount));
			finish();

			break;
		case LOTTERY:
		case WEBVIEW:
			rl_relativelayout1.setVisibility(View.VISIBLE);
			rl_relativelayout2.setVisibility(View.GONE);
			rl_relativelayout3.setVisibility(View.GONE);
			page = PAGE.FIRST;
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBack();
		}
		return true;

	}

	public void showDialog(String title, String content, String okText, String cancleText, boolean cancelable,
			OnCancelListener btnCancle, OnClickListener btnOk) {
		mDialog = new ProgressDialog(this);
		mDialog.setTitle(title);
		mDialog.setMessage(content);
		mDialog.setCancelable(cancelable);
		mDialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			// 抽奖
			onClickLottery();
			break;
		case R.id.textView2:
			// 申请规则
			rl_relativelayout1.setVisibility(View.GONE);
			rl_relativelayout2.setVisibility(View.GONE);
			rl_relativelayout3.setVisibility(View.VISIBLE);
			page = PAGE.WEBVIEW;
			wv_view.loadUrl("http://m.babytree.com/pregnancy/rules.php");
			break;
		case R.id.radioButton1:
			// 通讯录

			break;
		case R.id.radioButton2:
			// 新浪微博
			onClickSin();
			break;
		case R.id.radioButton3:

			// 腾讯微博
			onClickTen();
			break;
		case R.id.button2:
			// 填写地址
			startActivity(new Intent(InviteActivity.this, ReceiveActivity.class).putExtra("invite_pic", pic_url));
			break;

		case R.id.btn_send_invite:
			Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
			intent.putExtra("sms_body", SMS_MESSAGE);
			if (BabytreeUtil.hasIntentActivities(this, intent)) {
				startActivity(intent);
			} else {
				Toast.makeText(mContext, "没有找到相应的应用程序", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}

	}

	private Handler hander = new Handler(Looper.myLooper()) {
		@Override
		public void handleMessage(Message msg) {

			if (mDialog != null && !InviteActivity.this.isFinishing())
				mDialog.dismiss();

			switch (msg.what) {
			case 0:

				break;
			case 10:

				DataResult ret = (DataResult) msg.obj;
				switch (ret.status) {
				case 0:

					rl_relativelayout1.setVisibility(View.GONE);
					rl_relativelayout2.setVisibility(View.VISIBLE);
					rl_relativelayout3.setVisibility(View.GONE);

					page = PAGE.LOTTERY;

					prize = new Prize();
					prize = (Prize) ret.data;
					pic_url = prize.prizeimage;

					tv_name.setText("恭喜你砸中了" + prize.prizename);

					if (prize.prizetype.equalsIgnoreCase("0")) {
						bt_sendmsg.setVisibility(View.INVISIBLE);
					} else {
						bt_sendmsg.setVisibility(View.VISIBLE);
					}

					mLotteryCount = prize.prizecount;
					tv_count.setText(Html.fromHtml("剩余抽奖次数<font color=\"#ff0000\">" + mLotteryCount + "</font>"));
					iv_view5.setTag(Md5Util.md5(prize.prizeimage));
					iv_view5.setVisibility(View.VISIBLE);

					lottery(mLotteryCount.equalsIgnoreCase("0") ? false : true);
					// lottery(false);

					// ---------------------缓存模块start--------------------------
					bitmapCache.display(iv_view5, prize.prizeimage);
					// ---------------------缓存模块end----------------------------

					break;
				default:
					Toast.makeText(InviteActivity.this, ret.message, Toast.LENGTH_SHORT).show();
					ExceptionUtil.catchException(ret.error, InviteActivity.this);
					break;
				}

				break;
			default:
				break;
			}
		}
	};

	/**
	 * 页 状态
	 * 
	 * @author Administrator
	 * 
	 */
	enum PAGE {
		FIRST, LOTTERY, WEBVIEW
	}

	@Override
	public void setLeftButton(Button button) {
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBack();
			}
		});

	}

	@Override
	public void setRightButton(Button button) {

	}

	@Override
	public String getTitleString() {
		return "邀请得好礼";
	}

	@Override
	public int getBodyView() {
		return R.layout.invite_activity;
	}
}
