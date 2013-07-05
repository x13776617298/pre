package com.babytree.apps.biz.user;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.biz.home.ctr.HomeController;
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
 * 注册
 * 
 * @author wangbingqi
 * 
 */
public class RegisterActivity extends BabytreeTitleAcitivty implements OnClickListener, OnFocusChangeListener {
	private AuthReceiver receiver;

	private ProgressDialog mDialog;

	private EditText mTxtEmail;

	private EditText mTxtPassword;

	private EditText mTxtNickname;

	private TextView mTxtNicknameMessage;

	private TextView mTxtEmailMessage;

	private String password;
	private String uid;
	private String token;
	private String openId;
	private String type;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mTxtEmail = (EditText) findViewById(R.id.txt_email);
		mTxtPassword = (EditText) findViewById(R.id.txt_password);
		mTxtNickname = (EditText) findViewById(R.id.txt_nickname);
		mTxtNicknameMessage = (TextView) findViewById(R.id.txt_nickname_message);
		mTxtEmailMessage = (TextView) findViewById(R.id.txt_email_message);

		TextView title1 = (TextView) findViewById(R.id.tv_reg_title1);
		TextView title2 = (TextView) findViewById(R.id.tv_reg_title2);
		boolean isFromY = getIntent().getBooleanExtra("fromY", false);
		if (isFromY) {
			title1.setVisibility(View.VISIBLE);
			title2.setVisibility(View.VISIBLE);
		} else {
			title1.setVisibility(View.GONE);
			title2.setVisibility(View.GONE);
		}
		mTxtNickname.setOnFocusChangeListener(this);
		mTxtEmail.setOnFocusChangeListener(this);

		findViewById(R.id.btn_right).setOnClickListener(this);
		findViewById(R.id.btn_login_tenc).setOnClickListener(this);
		findViewById(R.id.btn_login_sina).setOnClickListener(this);

		WebView.enablePlatformNotifications();
		registerIntentReceivers();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_right) {
			String email = mTxtEmail.getText().toString().trim();
			password = mTxtPassword.getText().toString().trim();
			String nickname = mTxtNickname.getText().toString().trim();
			if (email.equals("")) {
				Toast.makeText(this, "请输入邮箱", Toast.LENGTH_SHORT).show();
				return;
			}
			if (password.equals("")) {
				Toast.makeText(this, "请输入密码", Toast.LENGTH_SHORT).show();
				return;
			}
			if (nickname.equals("")) {
				Toast.makeText(this, "请输入昵称", Toast.LENGTH_SHORT).show();
				return;
			}
			process(email, password, nickname);
		} else if (v.getId() == R.id.btn_login_tenc) {
			// 腾讯登录
			if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(RegisterActivity.this)) {
				auth();
			} else {
				Toast.makeText(getApplicationContext(), BaseController.NetworkExceptionMessage, Toast.LENGTH_SHORT)
						.show();
			}
		} else if (v.getId() == R.id.btn_login_sina) {
			// 新浪登录
			if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(RegisterActivity.this)) {
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
				Toast.makeText(RegisterActivity.this, "授权失败", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onComplete(Bundle bundle, SHARE_MEDIA arg1) {
				mApplication.getUmSocialService().getPlatformInfo(RegisterActivity.this, media,
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

	private void process(final String email, final String password, final String nickname) {
		showDialog(null, "提交中...", null, null, true, null, null);
		new Thread() {

			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(RegisterActivity.this)) {
						ret = P_BabytreeController.register(email, password, nickname);
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
				message.what = 1;
				handler.sendMessage(message);
			}

		}.start();
	}

	private void thirdLogin() {
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				try {

					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(RegisterActivity.this)) {
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

	private void userRegisterCheckNickname(final String nickname) {
		mTxtNicknameMessage.setText("昵称检测中...");
		new Thread() {

			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(RegisterActivity.this)) {
						ret = P_BabytreeController.userRegisterCheckNickname(nickname);
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
				message.what = 2;
				handler.sendMessage(message);
			}

		}.start();
	}

	private void userRegisterCheckEmail(final String email) {
		mTxtEmailMessage.setText("邮箱检测中...");
		new Thread() {

			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(RegisterActivity.this)) {
						ret = P_BabytreeController.userRegisterCheckEmail(email);
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
				message.what = 3;
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
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(RegisterActivity.this)) {
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

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (mDialog != null && !RegisterActivity.this.isFinishing()) {
				mDialog.dismiss();
			}
			if (msg.what == 1) {

				DataResult ret = (DataResult) msg.obj;
				switch (ret.status) {
				case P_BabytreeController.SUCCESS_CODE:
					// Umeng Evert
					MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.com_reg);
					User user = (User) ret.data;
					Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
					SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.LOGIN_STRING, user.login_string);
					SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.USER_ENCODE_ID, user.enc_user_id);
					SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.NICKNAME, user.nickname);
					SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.CAN_MODIFY_NICKNAME,
							user.can_modify_nickname);
					SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.HEAD, user.avatar_url);
					// 判断用户第一次登录选择医院以后是否需要同步到他的个人信息里
					boolean isNessarySyn = SharedPreferencesUtil.getBooleanValue(getApplicationContext(),
							ShareKeys.ISNESSARYSYN);
					if (isNessarySyn) {
						String hospitalId = SharedPreferencesUtil.getStringValue(getApplicationContext(),
								ShareKeys.HOSPITAL_ID);
						setHospital(hospitalId);
					} else {
						SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.HOSPITAL_ID, user.hospital_id);
						if (user.hospital_id != null) {
							SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.ISCHOICEHOSPITAL, true);
						}
						SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.HOSPITAL_NAME,
								user.hospital_name);
						SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.BABY_BIRTHDAY_TS,
								user.baby_birthday_ts);
						SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.GROUP_ID, user.group_id);
					}

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
					if (user.reg_ts != null && !user.reg_ts.equals("")) {
						String reg_ts = dateFormat.format(new Date(Long.parseLong(user.reg_ts) * 1000));
						SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.REG_TS, reg_ts);
					}
					SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.GENDER, user.gender);
					if (user.location.equals("") || user.location.equals("0")) {
						SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.LOCATION, "1101");
					} else {
						SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.LOCATION, user.location);
					}
					SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.EMAIL, user.email);

					// 同步预产期
					SimpleDateFormat mDateFormatForApi = new SimpleDateFormat("yyyy-M-d", Locale.CHINA);
					long pregnancyMills = SharedPreferencesUtil.getLongValue(RegisterActivity.this,
							ShareKeys.BIRTHDAY_TIMESTAMP);
					String preTmp = mDateFormatForApi.format(new Date(pregnancyMills));
					syncPre(preTmp, user.login_string);

					Intent intent = new Intent(RegisterActivity.this, RegisterSureActivity.class);
					intent.putExtra("can_invidate", true);
					setResult(RESULT_OK);
					startActivity(intent);
					// intent.putExtra("user", user);
					// intent.putExtra("email", user.email);
					// intent.putExtra("password", password);
					// setResult(RESULT_OK, intent);
					// startActivity(intent);
					finish();
					break;
				default:
					ExceptionUtil.catchException(ret.error, RegisterActivity.this);
					Toast.makeText(RegisterActivity.this, ret.message, Toast.LENGTH_SHORT).show();
					break;
				}
			} else if (msg.what == 2) { // 检测昵称
				DataResult ret = (DataResult) msg.obj;
				switch (ret.status) {
				case P_BabytreeController.SUCCESS_CODE:
					mTxtNicknameMessage.setText("检测通过");
					break;
				default:
					ExceptionUtil.catchException(ret.error, RegisterActivity.this);
					Toast.makeText(RegisterActivity.this, ret.message, Toast.LENGTH_SHORT).show();
					mTxtNicknameMessage.setText(ret.message);
					break;
				}
			} else if (msg.what == 3) { // 检测邮箱
				DataResult ret = (DataResult) msg.obj;
				switch (ret.status) {
				case P_BabytreeController.SUCCESS_CODE:
					mTxtEmailMessage.setText("检测通过");
					break;
				default:
					ExceptionUtil.catchException(ret.error, RegisterActivity.this);
					Toast.makeText(RegisterActivity.this, ret.message, Toast.LENGTH_SHORT).show();
					mTxtEmailMessage.setText(ret.message);
					break;
				}
			} else if (msg.what == 4) {
				DataResult result = (DataResult) msg.obj;
				if (result.status == P_BabytreeController.SUCCESS_CODE) {
					// Umeng Evert
					MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.com_reg);
					User user = (User) result.data;
					Toast.makeText(RegisterActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
					SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.LOGIN_STRING, user.login_string);
					SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.USER_ENCODE_ID, user.enc_user_id);
					SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.NICKNAME, user.nickname);
					SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.CAN_MODIFY_NICKNAME,
							user.can_modify_nickname);
					SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.HEAD, user.avatar_url);
					// 判断用户第一次登录选择医院以后是否需要同步到他的个人信息里
					boolean isNessarySyn = SharedPreferencesUtil.getBooleanValue(getApplicationContext(),
							ShareKeys.ISNESSARYSYN);
					if (isNessarySyn) {
						String hospitalId = SharedPreferencesUtil.getStringValue(getApplicationContext(),
								ShareKeys.HOSPITAL_ID);
						setHospital(hospitalId);
					} else {
						SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.HOSPITAL_ID, user.hospital_id);
						if (user.hospital_id != null) {
							SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.ISCHOICEHOSPITAL, true);
						}
						SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.HOSPITAL_NAME,
								user.hospital_name);
						SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.BABY_BIRTHDAY_TS,
								user.baby_birthday_ts);
						SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.GROUP_ID, user.group_id);
					}

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
					if (user.reg_ts != null && !user.reg_ts.equals("")) {
						String reg_ts = dateFormat.format(new Date(Long.parseLong(user.reg_ts) * 1000));
						SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.REG_TS, reg_ts);
					}
					SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.GENDER, user.gender);
					if (user.location.equals("") || user.location.equals("0")) {
						SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.LOCATION, "1101");
					} else {
						SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.LOCATION, user.location);
					}
					SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.EMAIL, user.email);
					if ("true".equalsIgnoreCase(user.can_write_invitation_code)) {
						Intent intent = new Intent(RegisterActivity.this, RegisterSureActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						intent.putExtra("can_invidate", true);
						intent.putExtra("type", type);
						intent.putExtra("token", token);
						intent.putExtra("open_id", openId);
						startActivity(intent);
						setResult(RESULT_OK);
						finish();
					} else {
						Intent intent = new Intent(RegisterActivity.this, ChoiceShareActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						intent.putExtra("type", type);
						intent.putExtra("access_token", token);
						intent.putExtra("openid", openId);
						setResult(RESULT_OK);
						startActivity(intent);
						finish();
					}

				} else if (result.message.equals("没有绑定用户")) {
					Intent intent = new Intent(RegisterActivity.this, RegisterSureActivity.class);
					intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
					intent.putExtra("token", token);
					intent.putExtra("open_id", openId);
					intent.putExtra("type", type);
					startActivity(intent);
					finish();
				} else {
					ExceptionUtil.catchException(result.error, RegisterActivity.this);
					Toast.makeText(RegisterActivity.this, result.message, Toast.LENGTH_SHORT).show();
				}
			} else if (msg.what == 999) {
				Toast.makeText(RegisterActivity.this, "授权失败,请重试!", Toast.LENGTH_SHORT).show();
			} else if (msg.what == 111) {
				DataResult result = (DataResult) msg.obj;
				if (result.status == P_BabytreeController.SUCCESS_CODE) {
					Toast.makeText(RegisterActivity.this, "同步医院成功！", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(RegisterActivity.this, "同步医院失败，请重新设置医院！", Toast.LENGTH_SHORT).show();
					ExceptionUtil.catchException(result.error, getBaseContext());
				}
			}
		}

	};

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
							handler.sendEmptyMessage(999);
						}

					}

					@Override
					public void onFail(int ret, String msg) {
						Looper.prepare();
						TDebug.msg(msg, getApplicationContext());
						handler.sendEmptyMessage(999);
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
	public void onFocusChange(View v, boolean hasFocus) {

		if (v == mTxtEmail) {
			final String email = mTxtEmail.getText().toString().trim();
			if (!hasFocus && !email.equals("")) {
				userRegisterCheckEmail(email);
			}
		} else if (v == mTxtNickname) {
			final String nickname = mTxtNickname.getText().toString().trim();
			if (!hasFocus && !nickname.equals("")) {
				userRegisterCheckNickname(nickname);
			}
		}
	}

	@Override
	public void setLeftButton(Button button) {
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
	}

	@Override
	public void setRightButton(Button button) {

	}

	@Override
	public String getTitleString() {
		return "注册";
	}

	@Override
	public int getBodyView() {
		return R.layout.register_activity;
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
					SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.IS_NEED_PRE, false);
					SharedPreferencesUtil.setValue(RegisterActivity.this, ShareKeys.BABY_BIRTHDAY_TS, tmpBirthday);
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
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(RegisterActivity.this)) {
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
