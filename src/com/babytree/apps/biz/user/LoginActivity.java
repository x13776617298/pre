package com.babytree.apps.biz.user;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.biz.home.ctr.HomeController;
import com.babytree.apps.biz.home.ui.MommyMenuFragment;
import com.babytree.apps.biz.user.model.User;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.BabytreeController;
import com.babytree.apps.comm.ctr.BaseController;
import com.babytree.apps.comm.ctr.HospitalController;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.ctr.ThirdController;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.ui.activity.BabyTreeWebviewActivity;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.tencent.tauth.TencentOpenAPI;
import com.tencent.tauth.TencentOpenHost;
import com.tencent.tauth.bean.OpenId;
import com.tencent.tauth.http.Callback;
import com.tencent.tauth.http.TDebug;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.controller.UMInfoAgent;
import com.umeng.socialize.controller.listener.SocializeListeners.OauthCallbackListener;
import com.umeng.socialize.controller.listener.SocializeListeners.PlatformInfoListener;
import com.umeng.socialize.exception.SocializeException;

/**
 * 登录
 */
public class LoginActivity extends BabytreeTitleAcitivty implements OnClickListener {

	public static final String BUNDLE_RETURN = "return";

	private AuthReceiver receiver;

	private ProgressDialog mDialog;

	private EditText mTxtEmail;

	private EditText mTxtPassword;

	private TextView mFindPassword;

	/**
	 * 登录成功之后要跳转的页面
	 */
	private Class<?> mReturn;

	String mail, psw;

	// //用于区别平台，获取名字
	private static final int TENC = 111;

	private static final int SINA = 222;

	private static final int RENREN = 333;

	private String uid;
	private String token;
	private String openId;
	private String type;

	@Override
	public void onCreate(Bundle savedInstanceState) {

		mReturn = (Class<?>) getIntent().getSerializableExtra(BUNDLE_RETURN);

		super.onCreate(savedInstanceState);

		mTxtEmail = (EditText) findViewById(R.id.txt_email);
		mTxtPassword = (EditText) findViewById(R.id.txt_password);
		mFindPassword = (TextView) findViewById(R.id.tv_find_password);
		CharSequence findPassword = getResources().getString(R.string.find_password);
		String htmlLinkText = "<a href=\"http://www.babytree.com/reg/forgotpwd.php\">" + findPassword + "</a>";
		mFindPassword.setText(Html.fromHtml(htmlLinkText));
		mFindPassword.setMovementMethod(LinkMovementMethod.getInstance());
		findPassword = mFindPassword.getText();
		if (findPassword instanceof Spannable) {
			int end = findPassword.length();
			Spannable sp = (Spannable) mFindPassword.getText();
			URLSpan[] urls = sp.getSpans(0, end, URLSpan.class);
			SpannableStringBuilder style = new SpannableStringBuilder(findPassword);
			style.clearSpans();// should clear old spans
			for (URLSpan url : urls) {
				MyURLSpan myURLSpan = new MyURLSpan(url.getURL());
				style.setSpan(myURLSpan, sp.getSpanStart(url), sp.getSpanEnd(url), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
			}
			mFindPassword.setText(style);
		}
		Button mBtnLogin = (Button) findViewById(R.id.btn_login);
		mBtnLogin.setOnClickListener(this);
		Button mBtnRegister = (Button) findViewById(R.id.btn_register);
		mBtnRegister.setOnClickListener(this);

		findViewById(R.id.btn_login_tenc).setOnClickListener(this);
		findViewById(R.id.btn_login_sina).setOnClickListener(this);

		if (mIsPregnancy) {
			// FrameLayout flTitle = (FrameLayout)
			// viewBody.findViewById(R.id.fl_title);
			// flTitle.setBackgroundDrawable(getResources().getDrawable(R.drawable.y_title_bg));
			// leftBtn.setBackgroundDrawable(getResources().getDrawable(R.drawable.y_btn_back));
		}
		WebView.enablePlatformNotifications();
		registerIntentReceivers();

	}

	private class MyURLSpan extends ClickableSpan {

		private String mUrl;

		MyURLSpan(String url) {
			mUrl = url;
		}

		@Override
		public void onClick(View widget) {

			// 跳转到找回密码
			BabyTreeWebviewActivity.launch(mContext, mUrl, "找回密码");

		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_left) {
			finish();

		} else if (v.getId() == R.id.btn_register) {
			unregisterIntentReceivers();
			Intent intent = new Intent(this, RegisterActivity.class);
			intent.putExtras(getIntent());
			startActivityForResult(intent, 0);

		} else if (v.getId() == R.id.btn_login) {
			String email = mTxtEmail.getText().toString().trim();
			String password = mTxtPassword.getText().toString().trim();
			if (email.equals("")) {
				Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
				return;
			}
			if (password.equals("")) {
				Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
				return;
			}
			process(email, password);
			SharedPreferencesUtil.setValue(this, ShareKeys.EMAIL, email);
		} else if (v.getId() == R.id.btn_login_tenc) {
			// 腾讯登录
			// switchLogin(SHARE_MEDIA.TENCENT);
			auth();
		} else if (v.getId() == R.id.btn_login_sina) {
			// 新浪登录
			if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(LoginActivity.this)) {
				switchLogin(SHARE_MEDIA.SINA);
			} else {
				Toast.makeText(getApplicationContext(), BaseController.NetworkExceptionMessage, Toast.LENGTH_SHORT)
						.show();
			}
		}
	}

	private void switchLogin(final SHARE_MEDIA media) {
		UMInfoAgent.removeOauth(this, media);
		mApplication.getUmSocialService().doOauthVerify(this, media, new OauthCallbackListener() {

			@Override
			public void onError(SocializeException arg0, SHARE_MEDIA arg1) {
				Toast.makeText(LoginActivity.this, "授权失败", Toast.LENGTH_SHORT).show();

			}

			@Override
			public void onComplete(Bundle bundle, SHARE_MEDIA arg1) {
				mApplication.getUmSocialService().getPlatformInfo(LoginActivity.this, media,
						new PlatformInfoListener() {

							@Override
							public void onStart() {

							}

							@Override
							public void onComplete(int arg0, Map<String, Object> map) {
								if (map != null && map.size() > 0) {
									uid = String.valueOf((Object) map.get("uid"));
									token = String.valueOf((Object) map.get("access_token"));
									openId = String.valueOf((Object) map.get("openid"));
									if (openId.equals("null")) {
										openId = uid;
									}
									if (media == SHARE_MEDIA.TENCENT) {
										type = "2";
									} else if (media == SHARE_MEDIA.SINA) {
										type = "1";
									}
									showDialog(null, "提交中...", null, null, true, null, null);
									thirdLogin();
								}
							}

						});
			}
		});
	}

	private void thirdLogin() {
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				try {

					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(LoginActivity.this)) {
						ret = ThirdController.thirdPartLogin(uid, openId, token, type);
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
				Message message = new Message();
				message.obj = ret;
				message.what = 4;
				handler.sendMessage(message);
			}
		}.start();
	}

	private void setHospital(final String hospitalId) {
		new Thread() {
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(LoginActivity.this)) {
						String loginString = SharedPreferencesUtil.getStringValue(getApplicationContext(),
								ShareKeys.LOGIN_STRING);
						ret = HospitalController.setHospital(loginString, hospitalId, null, null);
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
				message.what = 111;
				handler.sendMessage(message);
			};
		}.start();
	}

	public void process(final String email, final String password) {
		showDialog(null, "提交中...", null, null, true, null, null);
		new Thread() {

			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(LoginActivity.this)) {
						ret = P_BabytreeController.login(email, password);
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
				handler.sendMessage(message);
			}

		}.start();
	}

	// 存入LoginString
	private void loginSuccess(User user) {
		// Umeng Evert
		MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.com_login);
		SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.LOGIN_STRING, user.login_string);
		SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.USER_ENCODE_ID, user.enc_user_id);
		SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.NICKNAME, user.nickname);
		SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.CAN_MODIFY_NICKNAME, user.can_modify_nickname);
		SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.HEAD, user.avatar_url);
		// 判断用户第一次登录选择医院以后是否需要同步到他的个人信息里
		boolean isNessarySyn = SharedPreferencesUtil.getBooleanValue(getApplicationContext(), ShareKeys.ISNESSARYSYN);
		if (isNessarySyn) {
			String hospitalId = SharedPreferencesUtil.getStringValue(getApplicationContext(), ShareKeys.HOSPITAL_ID);
			setHospital(hospitalId);
		} else {
			SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.HOSPITAL_ID, user.hospital_id);
			if (user.hospital_id != null) {
				SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.ISCHOICEHOSPITAL, true);
			}
			SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.HOSPITAL_NAME, user.hospital_name);
			SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.BABY_BIRTHDAY_TS, user.baby_birthday_ts);
			SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.GROUP_ID, user.group_id);
		}

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		if (user.reg_ts != null && !user.reg_ts.equals("")) {
			String reg_ts = dateFormat.format(new Date(Long.parseLong(user.reg_ts) * 1000));
			SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.REG_TS, reg_ts);
		}
		SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.GENDER, user.gender);
		if (user.location.equals("") || user.location.equals("0")) {
			SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.LOCATION, "1101");
		} else {
			SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.LOCATION, user.location);
		}
		SharedPreferencesUtil.setValue(this, ShareKeys.EMAIL, user.email);
		Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
		
		// 登录成功发送一个广播，刷新数据
		LocalBroadcastManager.getInstance(mContext.getApplicationContext()).sendBroadcast(
				new Intent(MommyMenuFragment.ACTION_LOGIN));
		BabytreeLog.d("登录成功 and 发送登录成功广播");
		
		// 同步预产期
		SimpleDateFormat mDateFormatForApi = new SimpleDateFormat("yyyy-M-d", Locale.CHINA);
		long pregnancyMills = SharedPreferencesUtil.getLongValue(this, ShareKeys.BIRTHDAY_TIMESTAMP);
		String preTmp = mDateFormatForApi.format(new Date(pregnancyMills));
		syncPre(preTmp, user.login_string);
		if (null != mReturn) {
			Intent intent = new Intent(LoginActivity.this, mReturn);
			intent.putExtras(getIntent());
			startActivity(intent);
		}
		setResult(888);
		finish();
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if (mDialog != null && !LoginActivity.this.isFinishing())
				mDialog.dismiss();

			if (msg.what == 111) {
				DataResult result = (DataResult) msg.obj;
				if (result.status == P_BabytreeController.SUCCESS_CODE) {
					Toast.makeText(LoginActivity.this, "同步医院成功！", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(LoginActivity.this, "同步医院失败，请重新设置医院！", Toast.LENGTH_SHORT).show();
					ExceptionUtil.catchException(result.error, getBaseContext());
				}
			} else if (msg.what == 4) {
				DataResult result = (DataResult) msg.obj;
				if (result.status == P_BabytreeController.SUCCESS_CODE) {
					// Umeng Evert
					MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.com_reg);
					User user = (User) result.data;
					Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();

					MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.com_login);
					SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.LOGIN_STRING, user.login_string);
					SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.USER_ENCODE_ID, user.enc_user_id);
					SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.NICKNAME, user.nickname);
					SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.CAN_MODIFY_NICKNAME,
							user.can_modify_nickname);
					SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.HEAD, user.avatar_url);
					// 判断用户第一次登录选择医院以后是否需要同步到他的个人信息里
					boolean isNessarySyn = SharedPreferencesUtil.getBooleanValue(getApplicationContext(),
							ShareKeys.ISNESSARYSYN);
					if (isNessarySyn) {
						String hospitalId = SharedPreferencesUtil.getStringValue(getApplicationContext(),
								ShareKeys.HOSPITAL_ID);
						setHospital(hospitalId);
					} else {
						SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.HOSPITAL_ID, user.hospital_id);
						if (user.hospital_id != null) {
							SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.ISCHOICEHOSPITAL, true);
						}
						SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.HOSPITAL_NAME, user.hospital_name);
						SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.BABY_BIRTHDAY_TS,
								user.baby_birthday_ts);
						SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.GROUP_ID, user.group_id);
					}

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
					if (user.reg_ts != null && !user.reg_ts.equals("")) {
						String reg_ts = dateFormat.format(new Date(Long.parseLong(user.reg_ts) * 1000));
						SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.REG_TS, reg_ts);
					}
					SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.GENDER, user.gender);
					if (user.location.equals("") || user.location.equals("0")) {
						SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.LOCATION, "1101");
					} else {
						SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.LOCATION, user.location);
					}
					SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.EMAIL, user.email);
					if ("true".equalsIgnoreCase(user.can_write_invitation_code)) {
						Intent intent = new Intent(LoginActivity.this, RegisterSureActivity.class);
						intent.putExtra("can_invidate", true);
						startActivity(intent);
						finish();
					} else {
						Intent intent = new Intent(LoginActivity.this, ChoiceShareActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						intent.putExtra("type", type);
						intent.putExtra("access_token", token);
						intent.putExtra("openid", openId);
						// setResult(RESULT_OK, intent);
						startActivity(intent);
						setResult(888);

						finish();
					}

				} else if (result.message.equals("没有绑定用户")) {
					Intent intent = new Intent(LoginActivity.this, RegisterSureActivity.class);
					intent.putExtra("token", token);
					intent.putExtra("open_id", openId);
					intent.putExtra("type", type);
					startActivity(intent);
					finish();
				} else {
					ExceptionUtil.catchException(result.error, LoginActivity.this);
					Toast.makeText(LoginActivity.this, result.message, Toast.LENGTH_SHORT).show();
				}
			} else if (msg.what == 999) {
				Toast.makeText(LoginActivity.this, "授权失败,请重试!", Toast.LENGTH_SHORT).show();
			} else {
				DataResult ret = (DataResult) msg.obj;
				switch (ret.status) {
				case P_BabytreeController.SUCCESS_CODE:
					User user = (User) ret.data;
					if (msg.arg1 != 0) {
						judgeNickName(user, msg.arg1);
					} else {
						SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.WHICH_THIRD, "");
					}
					loginSuccess(user);
					break;
				default:
					ExceptionUtil.catchException(ret.error, LoginActivity.this);
					Toast.makeText(LoginActivity.this, ret.message, Toast.LENGTH_SHORT).show();
					break;
				}
			}

		}

		/**
		 * 根据登录平台不同，获取对应的名字
		 * 
		 * @param user
		 * @param arg1
		 */
		private void judgeNickName(User user, int arg1) {
			try {
				switch (arg1) {
				case TENC:
					SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.WHICH_THIRD, "tenc");
					// user.nickname=UMSnsService.getUserNickname(LoginActivity.this,
					// UMSnsService.SHARE_TO.TENC);
					break;
				case SINA:
					SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.WHICH_THIRD, "sina");
					// user.nickname=UMSnsService.getUserNickname(LoginActivity.this,
					// UMSnsService.SHARE_TO.SINA);
					break;
				case RENREN:
					// user.nickname=UMSnsService.getUserNickname(LoginActivity.this,
					// UMSnsService.SHARE_TO.RENR);
					break;
				case 0:
					break;
				default:
					break;
				}
			} catch (Exception e) {
				ExceptionUtil.catchException(e.toString(), LoginActivity.this);
			}
		}

	};

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		registerIntentReceivers();
		if (resultCode == RESULT_OK) {

			finish();
		}
	}

	public void showDialog(String title, String content, String okText, String cancleText, boolean cancelable,
			OnCancelListener btnCancle, OnClickListener btnOk) {
		mDialog = new ProgressDialog(this);
		mDialog.setTitle(title);
		mDialog.setMessage(content);
		mDialog.setCancelable(cancelable);
		mDialog.show();
	}

	// 注册腾讯qq授权监听
	private void registerIntentReceivers() {
		receiver = new AuthReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(TencentOpenHost.AUTH_BROADCAST);
		registerReceiver(receiver, filter);
	}

	private void unregisterIntentReceivers() {
		unregisterReceiver(receiver);
	}

	// 腾讯qq授权监听
	public class AuthReceiver extends BroadcastReceiver {
		private static final String TAG = CommConstants.COMM_TAG;

		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle exts = intent.getExtras();
			String raw = exts.getString("raw");
			String access_token = exts.getString(TencentOpenHost.ACCESS_TOKEN);
			String expires_in = exts.getString(TencentOpenHost.EXPIRES_IN);
			String error_ret = exts.getString(TencentOpenHost.ERROR_RET);
			String error_des = exts.getString(TencentOpenHost.ERROR_DES);
			Log.i(TAG, String.format("raw: %s, access_token:%s, expires_in:%s", raw, access_token, expires_in));

			if (access_token != null) {
				showDialog(null, "提交中...", null, null, true, null, null);
				// 获取到access token
				token = access_token;
				// 用access token 来获取open id

				TencentOpenAPI.openid(access_token, new Callback() {

					@Override
					public void onSuccess(Object obj) {
						openId = ((OpenId) obj).getOpenId();
						type = "2";
						Looper.prepare();
						String username = ThirdController
								.getTencentUsername(token, CommConstants.TENCENT_APPID, openId);
						if (username != null) {
							uid = username;
							thirdLogin();
						} else {
							Toast.makeText(getApplicationContext(), "授权失败,请重试!", Toast.LENGTH_SHORT).show();
							handler.sendEmptyMessage(999);
						}
						//

					}

					@Override
					public void onFail(int ret, String msg) {
						Looper.prepare();
						handler.sendEmptyMessage(999);
						TDebug.msg(msg, getApplicationContext());

					}

					@Override
					public void onCancel(int arg0) {

					}
				});
			}
			if (error_ret != null) {
				Toast.makeText(context, "授权失败,请重试!\n" + error_des, Toast.LENGTH_SHORT).show();
			}
		}
	}

	private void auth() {
		Intent intent = new Intent(this, com.tencent.tauth.TAuthView.class);

		intent.putExtra(TencentOpenHost.CLIENT_ID, CommConstants.TENCENT_APPID);
		intent.putExtra(TencentOpenHost.SCOPE, CommConstants.TENCENT_SCOPE);
		intent.putExtra(TencentOpenHost.TARGET, "_slef");
		intent.putExtra(TencentOpenHost.CALLBACK, CommConstants.TENCENT_CALLBACK);

		startActivity(intent);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (receiver != null) {
			unregisterIntentReceivers();
		}
	}

	@Override
	public void setLeftButton(Button button) {
		button.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_back));
	}

	@Override
	public void setRightButton(Button button) {

	}

	@Override
	public String getTitleString() {
		return "登录";
	}

	@Override
	public int getBodyView() {
		return R.layout.login_activity;
	}

	/**
	 * 同步预产期
	 * 
	 * @author wangshuaibo
	 */
	private void syncPre(final String tempPre, final String loginString) {
		BabytreeLog.d("同步预产期 - saveAndSyncPregnacy(pregnancyEnd) - " + tempPre);
		final Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				DataResult ret = (DataResult) msg.obj;
				if (ret.status == BabytreeController.SUCCESS_CODE) {
					BabytreeLog.d("Sync baby birthday success.");
					String tmpBirthday = (String) ret.data;
					SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.IS_NEED_PRE, false);
					SharedPreferencesUtil.setValue(LoginActivity.this, ShareKeys.BABY_BIRTHDAY_TS, tmpBirthday);
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
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(LoginActivity.this)) {
						ret = HomeController.savePersonalInfo(loginString, null, null, tempPre);
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
}
