package com.babytree.apps.biz.user;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.biz.user.model.User;
import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.HospitalController;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.ctr.SignInController;
import com.babytree.apps.comm.ctr.ThirdController;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.Md5Util;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 第三方登录授权后的注册页面
 * 
 * @author ybq
 * 
 */

public class RegisterSureActivity extends BabytreeActivity implements OnClickListener, OnCheckedChangeListener,
		OnFocusChangeListener {
	private BabytreeBitmapCache bitmapCache;// 缓存对象
	private Context mContext;
	
	private LinearLayout mainLayou;
	private LayoutInflater mInflater;
	private View view;
	// 页面title控件生命
	private Button mBtnLeft;
	private Button mBtnRight;
	private RadioGroup mRadioGroup;
	// 新用户注册切换按钮
	private RadioButton mRadioButtonLeft;
	// 已绑定用户注册按钮
	private RadioButton mRadioButtonRight;

	private EditText nickNameEdit;
	private EditText emailEdit;
	private EditText yqmEdit;
	private EditText passwordEdit;
	private TextView checkNickname;
	private TextView checkEmail;
	private TextView tvLoad;
	private LinearLayout myGallery;

	private String token;
	private String type;
	private String openId;
	private List<ImageView> mList;
	private ProgressDialog mDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register_sure_activity);
		mContext = this;

		// ---------------------缓存模块start--------------------------
		bitmapCache = BabytreeBitmapCache.create(mContext);
		// ---------------------缓存模块end----------------------------
		mainLayou = (LinearLayout) findViewById(R.id.regist_sure_layout);
		mInflater = getLayoutInflater();
		token = getIntent().getStringExtra("token");
		type = getIntent().getStringExtra("type");
		openId = getIntent().getStringExtra("open_id");
		boolean can_invidate = getIntent().getBooleanExtra("can_invidate", false);
		initTitleWidget();
		// initThirdWidget();
		if (can_invidate) {
			initThirdWidget();
		} else {
			initFirstWidget();
		}

	}

	/**
	 * 初始化页面头部的控件信息
	 */
	private void initTitleWidget() {
		mBtnLeft = (Button) findViewById(R.id.btn_left);
		mBtnRight = (Button) findViewById(R.id.btn_right);
		mBtnLeft.setOnClickListener(this);
		mBtnRight.setOnClickListener(this);
		mRadioGroup = (RadioGroup) findViewById(R.id.rg_choice);
		mRadioButtonLeft = (RadioButton) findViewById(R.id.rb_left);
		mRadioButtonRight = (RadioButton) findViewById(R.id.rb_right);
		mRadioButtonLeft.setOnCheckedChangeListener(this);
		mRadioButtonRight.setOnCheckedChangeListener(this);

		if (mIsPregnancy) {
			FrameLayout flTitle = (FrameLayout) findViewById(R.id.fl_title);
			flTitle.setBackgroundDrawable(getResources().getDrawable(R.drawable.y_title_bg));
			mBtnLeft.setBackgroundDrawable(getResources().getDrawable(R.drawable.y_btn_back));
			mBtnRight.setBackgroundDrawable(getResources().getDrawable(R.drawable.y_btn_main_change));
			mRadioButtonLeft.setBackgroundDrawable(getResources().getDrawable(R.drawable.y_btn_choice_left));
			mRadioButtonRight.setBackgroundDrawable(getResources().getDrawable(R.drawable.y_btn_choice_right));

		}
	}

	/**
	 * 初始化授权后所要显示的页面的控件信息
	 */
	private void initFirstWidget() {
		mBtnRight.setVisibility(View.GONE);
		mRadioGroup.setVisibility(View.VISIBLE);
		view = mInflater.inflate(R.layout.regist_sure_first_item, null, false);
		mainLayou.addView(view);
		nickNameEdit = (EditText) view.findViewById(R.id.txt_nickname);
		emailEdit = (EditText) view.findViewById(R.id.txt_email);
		// yqmEdit = (EditText) view.findViewById(R.id.txt_yqm);
		checkEmail = (TextView) view.findViewById(R.id.tv_check_email);
		checkNickname = (TextView) view.findViewById(R.id.tv_check_nickname);
		Button sureButton = (Button) view.findViewById(R.id.btn_sure);
		nickNameEdit.setOnFocusChangeListener(this);
		emailEdit.setOnFocusChangeListener(this);
		// yqmEdit.setOnClickListener(this);
		sureButton.setOnClickListener(this);
	}

	/**
	 * 初始化授权后所要显示的已注册账户页面的控件信息
	 */
	private void initSecondWidget() {
		mBtnRight.setVisibility(View.GONE);
		mRadioGroup.setVisibility(View.VISIBLE);
		view = mInflater.inflate(R.layout.regist_sure_second_item, null, false);
		mainLayou.addView(view);
		emailEdit = (EditText) view.findViewById(R.id.txt_second_email);
		passwordEdit = (EditText) view.findViewById(R.id.txt_second_password);
		Button sureButton = (Button) view.findViewById(R.id.btn_second_sure);
		// emailEdit.setOnClickListener(this);
		// passwordEdit.setOnClickListener(this);
		sureButton.setOnClickListener(this);
	}

	/**
	 * 初始化授权后所要显示的没注册过账户需要输入邀请码页面的控件信息
	 */
	private void initThirdWidget() {
		mBtnRight.setVisibility(View.VISIBLE);
		mBtnLeft.setVisibility(View.GONE);
		mRadioGroup.setVisibility(View.GONE);
		view = mInflater.inflate(R.layout.regist_sure_third_item, null, false);
		mainLayou.addView(view);
		yqmEdit = (EditText) view.findViewById(R.id.txt_third_yqm);
		myGallery = (LinearLayout) view.findViewById(R.id.mygallery);
		tvLoad = (TextView) view.findViewById(R.id.tv_load);
		TextView txt = (TextView) view.findViewById(R.id.tv_current_num);
		txt.setText(Html.fromHtml(txt.getText().toString() + "<font color=\"#ff0000\">" + "0" + "</font>"));

		Button sureButton = (Button) view.findViewById(R.id.btn_third_sure);
		// yqmEdit.setOnClickListener(this);
		sureButton.setOnClickListener(this);
		getProductList();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		if (isChecked) {
			switch (buttonView.getId()) {
			case R.id.rb_left:
				clearView();
				initFirstWidget();
				break;
			case R.id.rb_right:
				clearView();
				initSecondWidget();
				break;
			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
			finish();
			break;
		case R.id.btn_right:
			if (type != null) {
				Intent intent = new Intent(RegisterSureActivity.this, ChoiceShareActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intent.putExtra("type", type);
				intent.putExtra("access_token", token);
				intent.putExtra("openid", openId);
				startActivity(intent);
				finish();
			} else {
				finish();
			}
			break;
		case R.id.btn_sure:
			String nickName = nickNameEdit.getText().toString();
			String email = emailEdit.getText().toString();
			if (nickName != null && (!"".equals(nickName)) && email != null && !"".equals(email)) {
				String babyBirthday = SharedPreferencesUtil.getStringValue(getApplicationContext(),
						ShareKeys.BABY_BIRTHDAY_TS);
				showDialog(null, "提交中...", null, null, true, null, null);
				newUserThirdBD(nickName, email, babyBirthday);
			} else {
				Toast.makeText(RegisterSureActivity.this, "昵称或密码不能为空！", Toast.LENGTH_SHORT).show();
			}

			break;
		case R.id.btn_second_sure:
			String emails = emailEdit.getText().toString();
			String password = passwordEdit.getText().toString();
			if (emails != null && (!"".equals(emails)) && password != null && !"".equals(password)) {
				showDialog(null, "绑定中...", null, null, true, null, null);
				oldUserThirdBD(password, emails);
			} else {
				Toast.makeText(RegisterSureActivity.this, "账号或密码不能为空！", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btn_third_sure:
			String yqm = yqmEdit.getText().toString();
			if (yqm != null && !"".equals(yqm)) {
				showDialog(null, "提交中...", null, null, true, null, null);
				submitYQM(yqm);
			} else {
				Toast.makeText(RegisterSureActivity.this, "请输入邀请码！", Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			finish();
			return true;
		} else {
			return false;
		}
	}

	private void clearView() {
		mainLayou.removeView(view);
	}

	private View insertImage() {
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int width = metrics.widthPixels / 4;

		LinearLayout layout = new LinearLayout(getApplicationContext());
		layout.setLayoutParams(new LayoutParams(width, width));
		layout.setGravity(Gravity.CENTER);

		ImageView imageView = new ImageView(getApplicationContext());
		imageView.setLayoutParams(new LayoutParams(width - 10, width - 10));
		imageView.setImageResource(R.drawable.avatar_big);
		mList.add(imageView);
		layout.addView(imageView);
		return layout;

	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		switch (v.getId()) {
		case R.id.txt_nickname:
			final String nickname = nickNameEdit.getText().toString().trim();
			if (!hasFocus && !nickname.equals("")) {
				userRegisterCheckNickname(nickname);
			}
			break;
		case R.id.txt_email:
			final String email = emailEdit.getText().toString().trim();
			if (!hasFocus && !email.equals("")) {
				userRegisterCheckEmail(email);
			}
			break;
		default:
			break;
		}
	}

	private void newUserThirdBD(final String nickName, final String email, final String babyBirthday) {
		new Thread() {
			@Override
			public void run() {

				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(RegisterSureActivity.this)) {
						ret = ThirdController.newUserThirdBD(openId, token, type, nickName, email, babyBirthday);
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
				message.what = 4;
				handler.sendMessage(message);
			}

		}.start();
	}

	private void oldUserThirdBD(final String password, final String email) {

		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(RegisterSureActivity.this)) {
						ret = ThirdController.oldUserThirdBD(openId, token, type, password, email);
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
				message.what = 5;
				handler.sendMessage(message);
			}

		}.start();
	}

	private void userRegisterCheckNickname(final String nickname) {
		checkNickname.setVisibility(View.VISIBLE);
		checkNickname.setText("昵称检测中...");
		new Thread() {

			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(RegisterSureActivity.this)) {
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
		checkEmail.setVisibility(View.VISIBLE);
		checkEmail.setText("邮箱检测中...");
		new Thread() {

			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(RegisterSureActivity.this)) {
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

	private void getProductList() {
		tvLoad.setVisibility(View.VISIBLE);
		new Thread() {

			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(RegisterSureActivity.this)) {
						ret = SignInController.getProductList();
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
				message.what = 6;
				handler.sendMessage(message);
			}

		}.start();
	}

	private void submitYQM(final String invateCode) {
		new Thread() {

			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(RegisterSureActivity.this)) {
						String loginStr = SharedPreferencesUtil.getStringValue(getApplicationContext(),
								ShareKeys.LOGIN_STRING);
						ret = SignInController.invite(loginStr, invateCode);
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
				message.what = 7;
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
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(RegisterSureActivity.this)) {
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
			if (mDialog != null && !RegisterSureActivity.this.isFinishing()) {
				mDialog.dismiss();
			}
			DataResult ret = (DataResult) msg.obj;
			switch (msg.what) {
			// 检测昵称
			case 2:
				switch (ret.status) {
				case P_BabytreeController.SUCCESS_CODE:
					checkNickname.setText("检测通过");
					break;
				default:
					ExceptionUtil.catchException(ret.error, RegisterSureActivity.this);
					checkNickname.setText(ret.message);
					break;
				}
				break;
			// 检测邮箱
			case 3:
				switch (ret.status) {
				case P_BabytreeController.SUCCESS_CODE:
					checkEmail.setText("检测通过");
					break;
				default:
					ExceptionUtil.catchException(ret.error, RegisterSureActivity.this);
					checkEmail.setText(ret.message);
					break;
				}
				break;
			// 新用户第三方授权登录
			case 4:
				if (ret.status == P_BabytreeController.SUCCESS_CODE) {
					// Umeng Evert
					MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.com_reg);
					User user = (User) ret.data;
					Toast.makeText(RegisterSureActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
					SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.LOGIN_STRING,
							user.login_string);
					SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.USER_ENCODE_ID,
							user.enc_user_id);
					SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.NICKNAME, user.nickname);
					SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.CAN_MODIFY_NICKNAME,
							user.can_modify_nickname);
					SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.HEAD, user.avatar_url);
					// 判断用户第一次登录选择医院以后是否需要同步到他的个人信息里
					boolean isNessarySyn = SharedPreferencesUtil.getBooleanValue(getApplicationContext(),
							ShareKeys.ISNESSARYSYN);
					if (isNessarySyn) {
						String hospitalId = SharedPreferencesUtil.getStringValue(getApplicationContext(),
								ShareKeys.HOSPITAL_ID);
						setHospital(hospitalId);
					} else {
						SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.HOSPITAL_ID,
								user.hospital_id);
						if (user.hospital_id != null) {
							SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.ISCHOICEHOSPITAL,
									true);
						}
						SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.HOSPITAL_NAME,
								user.hospital_name);
						SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.BABY_BIRTHDAY_TS,
								user.baby_birthday_ts);
						SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.GROUP_ID, user.group_id);
					}

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
					if (user.reg_ts != null && !user.reg_ts.equals("")) {
						String reg_ts = dateFormat.format(new Date(Long.parseLong(user.reg_ts) * 1000));
						SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.REG_TS, reg_ts);
					}
					SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.GENDER, user.gender);
					if (user.location.equals("") || user.location.equals("0")) {
						SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.LOCATION, "1101");
					} else {
						SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.LOCATION, user.location);
					}
					SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.EMAIL, user.email);
					// if("true".equalsIgnoreCase(user.can_write_invitation_code)){
					clearView();
					initThirdWidget();
					// }
					// else{
					// Intent intent = new Intent(RegisterSureActivity.this,
					// ChoiceShareActivity.class);
					// // setResult(RESULT_OK, intent);
					// intent.putExtra("type", type);
					// startActivity(intent);
					// finish();
					// }
				} else {
					Toast.makeText(RegisterSureActivity.this, ret.message, Toast.LENGTH_SHORT).show();
				}
				break;
			// 已注册用户第三方授权绑定
			case 5:
				if (ret.status == P_BabytreeController.SUCCESS_CODE) {
					// Umeng Evert
					MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.com_reg);
					User user = (User) ret.data;
					Toast.makeText(RegisterSureActivity.this, "绑定成功", Toast.LENGTH_SHORT).show();
					SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.LOGIN_STRING,
							user.login_string);
					SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.USER_ENCODE_ID,
							user.enc_user_id);
					SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.NICKNAME, user.nickname);
					SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.CAN_MODIFY_NICKNAME,
							user.can_modify_nickname);
					SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.HEAD, user.avatar_url);
					// 判断用户第一次登录选择医院以后是否需要同步到他的个人信息里
					boolean isNessarySyn = SharedPreferencesUtil.getBooleanValue(getApplicationContext(),
							ShareKeys.ISNESSARYSYN);
					if (isNessarySyn) {
						String hospitalId = SharedPreferencesUtil.getStringValue(getApplicationContext(),
								ShareKeys.HOSPITAL_ID);
						setHospital(hospitalId);
					} else {
						SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.HOSPITAL_ID,
								user.hospital_id);
						if (user.hospital_id != null) {
							SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.ISCHOICEHOSPITAL,
									true);
						}
						SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.HOSPITAL_NAME,
								user.hospital_name);
						SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.BABY_BIRTHDAY_TS,
								user.baby_birthday_ts);
						SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.GROUP_ID, user.group_id);
					}

					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
					if (user.reg_ts != null && !user.reg_ts.equals("")) {
						String reg_ts = dateFormat.format(new Date(Long.parseLong(user.reg_ts) * 1000));
						SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.REG_TS, reg_ts);
					}
					SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.GENDER, user.gender);
					if (user.location.equals("") || user.location.equals("0")) {
						SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.LOCATION, "1101");
					} else {
						SharedPreferencesUtil.setValue(RegisterSureActivity.this, ShareKeys.LOCATION, user.location);
					}
					SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.EMAIL, user.email);
					if ("true".equalsIgnoreCase(user.can_write_invitation_code)) {
						Toast.makeText(RegisterSureActivity.this, "已成功绑定！", Toast.LENGTH_SHORT).show();
						clearView();
						initThirdWidget();
					} else {
						Intent intent = new Intent(RegisterSureActivity.this, ChoiceShareActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						intent.putExtra("type", type);
						intent.putExtra("access_token", token);
						intent.putExtra("openid", openId);
						startActivity(intent);
						finish();
					}
				} else {
					Toast.makeText(RegisterSureActivity.this, ret.message, Toast.LENGTH_SHORT).show();
				}
				break;
			case 6:
				tvLoad.setVisibility(View.GONE);
				switch (ret.status) {
				case P_BabytreeController.SUCCESS_CODE:
					ArrayList<String> urlList = new ArrayList<String>();
					urlList = (ArrayList<String>) ret.data;
					mList = new ArrayList<ImageView>(urlList.size());
					for (int i = 0; i < urlList.size(); i++) {
						myGallery.addView(insertImage());
					}
					for (int j = 0; j < mList.size(); j++) {
						final ImageView image = mList.get(j);
						image.setTag(Md5Util.md5(urlList.get(j)));

						// ---------------------缓存模块start--------------------------
						bitmapCache.display(image, urlList.get(j));
						// ---------------------缓存模块end----------------------------
					}
					break;
				default:
					ExceptionUtil.catchException(ret.error, RegisterSureActivity.this);
					Toast.makeText(RegisterSureActivity.this, ret.message, Toast.LENGTH_SHORT).show();
					break;
				}
				break;
			case 7:
				if (mDialog != null && !RegisterSureActivity.this.isFinishing()) {
					mDialog.dismiss();
				}
				if (ret.status == P_BabytreeController.SUCCESS_CODE) {
					if (type != null) {
						Intent intent = new Intent(RegisterSureActivity.this, ChoiceShareActivity.class);
						intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
						intent.putExtra("type", type);
						intent.putExtra("access_token", token);
						intent.putExtra("openid", openId);
						startActivity(intent);
						finish();
					} else {
						finish();
					}

				} else {
					dialog();
				}
				break;
			case 111: {
				if (ret.status == P_BabytreeController.SUCCESS_CODE) {
					Toast.makeText(RegisterSureActivity.this, "同步医院成功！", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(RegisterSureActivity.this, "同步医院失败，请重新设置医院！", Toast.LENGTH_SHORT).show();
					ExceptionUtil.catchException(ret.error, getBaseContext());
				}
			}
				break;
			default:
				break;
			}
		}

	};

	private void dialog() {
		try {
			AlertDialog.Builder exitSystemDialog = new AlertDialog.Builder(this);
			exitSystemDialog.setTitle("提示");
			exitSystemDialog.setMessage("如果您输入的邀请码没有错，就是这个手机已经被邀请过咯~不要作弊哦");
			exitSystemDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					arg0.dismiss();
				}
			});
			exitSystemDialog.create().show();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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

}
