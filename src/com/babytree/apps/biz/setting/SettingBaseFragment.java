package com.babytree.apps.biz.setting;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.babytree.apps.biz.home.ui.MommyMenuFragment;
import com.babytree.apps.biz.home.ui.MommyMenuFragment.LogoutBroadcast;
import com.babytree.apps.biz.user.LoginActivity;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.config.UrlConstrants;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.ui.UnionActivity;
import com.babytree.apps.comm.ui.activity.BabyTreeWebviewActivity;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.comm.view.BabyTreeFragment;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.UMFeedbackService;

/**
 * 设置页面 <br>
 * 育儿孕期设置页面公用部分,修改的时候注意,要保证育儿,孕期都包含<br>
 * 目前共用一个布局文件 setting_activity.xml
 */
public abstract class SettingBaseFragment extends BabyTreeFragment implements OnClickListener {

	/**
	 * 预产期/宝宝生日修改文本框
	 */
	private TextView txtPregnancy;
	/**
	 * 用户昵称文本框
	 */
	private TextView txtNickname;
	/**
	 * 版本号文本框
	 */
	private TextView txtVersion;
	/**
	 * 登录信息文本框
	 */
	private TextView txtLoginMsg;
	/**
	 * 用户昵称
	 */
	private String mNickname;
	/**
	 * 是否开启通知文本框
	 */
	private TextView topicSet;

	/**
	 * 预产期修改/宝宝生日修改文本框
	 */
	protected TextView txtPreOrBirthEdit;

	/**
	 * 切换App版本文本框(切换到孕期/切换到育儿)
	 */
	protected TextView txtChangeApp;

	/**
	 * 是否打开通知标识
	 */
	private boolean topicIsOpen;

	/**
	 * 用户登录Token
	 */
	protected String mLoginString;

	private SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日", Locale.CHINA);

	/**
	 * 屏幕亮度值 最大值225 最小值0(不要小于30)
	 */
	private int brightness = 0;
	/**
	 * 是否开启自动调节亮度
	 */
	private boolean isAutoBrightness = false;

	/**
	 * 获取屏幕的亮度  
	 */
	public static int getScreenBrightness(Activity activity) {
		int value = 0;
		ContentResolver cr = activity.getContentResolver();
		try {
			value = Settings.System.getInt(cr, Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {

		}
		return value;
	}

	/**
	 *  * 设置亮度  
	 */
	public static void setScreenBrightness(Activity activity, int brightness) {
		WindowManager.LayoutParams params = activity.getWindow().getAttributes();
		if (brightness > 255 || brightness < 0) {
			return;
		}
		if (brightness < 30) {
			return;
		}
		params.screenBrightness = brightness / 255f;
		activity.getWindow().setAttributes(params);

	}

	/**
	 * 开启亮度自动调节
	 * 
	 */
	public static void startAutoBrightness(Activity activity) {
		Settings.System.putInt(activity.getContentResolver(), "screen_brightness_mode", 1);
	}

	/**
	 * 停止自动亮度调节
	 */
	public static void stopAutoBrightness(Activity activity) {
		Settings.System.putInt(activity.getContentResolver(), "screen_brightness_mode", 0);
	}

	/**
	 * 保存亮度设置状态
	 */
	public static void saveBrightness(ContentResolver resolver, int brightness) {
		Uri uri = android.provider.Settings.System.getUriFor("screen_brightness");
		android.provider.Settings.System.putInt(resolver, "screen_brightness", brightness);
		// resolver.registerContentObserver(uri, true, myContentObserver);
		resolver.notifyChange(uri, null);
	}

	/**
	 * 判断是否开启了自动亮度调节
	 */
	public static boolean isAutoBrightness(ContentResolver aContentResolver) {
		boolean automicBrightness = false;
		try {
			automicBrightness = Settings.System.getInt(aContentResolver, "screen_brightness_mode") == 1;
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		return automicBrightness;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View viewBody = inflater.inflate(R.layout.setting_fragment, null);
		viewBody.findViewById(R.id.layout_edit).setOnClickListener(this);
		viewBody.findViewById(R.id.layout_login).setOnClickListener(this);
		viewBody.findViewById(R.id.layout_about).setOnClickListener(this);
		viewBody.findViewById(R.id.layout_feedback).setOnClickListener(this);
		viewBody.findViewById(R.id.layout_share).setOnClickListener(this);
		viewBody.findViewById(R.id.layout_topic_set).setOnClickListener(this);
		viewBody.findViewById(R.id.layout_notification).setOnClickListener(this);
		viewBody.findViewById(R.id.layout_visitbabytree).setOnClickListener(this);
		viewBody.findViewById(R.id.layout_change_app).setOnClickListener(this);
		viewBody.findViewById(R.id.layout_more).setOnClickListener(this);
		viewBody.findViewById(R.id.layout_check_update).setOnClickListener(this);
		viewBody.findViewById(R.id.layout_eyes).setOnClickListener(this);

		txtPregnancy = (TextView) viewBody.findViewById(R.id.txt_pregnancy);
		txtNickname = (TextView) viewBody.findViewById(R.id.txt_nickname);
		txtVersion = (TextView) viewBody.findViewById(R.id.txt_version);
		txtLoginMsg = (TextView) viewBody.findViewById(R.id.txt_login_msg);
		topicSet = (TextView) viewBody.findViewById(R.id.setting_topic_txt);
		txtPreOrBirthEdit = (TextView) viewBody.findViewById(R.id.txt_pre_or_birth_edit);
		txtChangeApp = (TextView) viewBody.findViewById(R.id.txt_change_app);

		txtVersion.setText(com.babytree.apps.comm.util.BabytreeUtil.getAppVersionName(mContext));
		return viewBody;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();

		long birthdayTimestamp = SharedPreferencesUtil.getLongValue(mContext, ShareKeys.BIRTHDAY_TIMESTAMP,
				System.currentTimeMillis());
		String birth = df.format(new Date(birthdayTimestamp));
		txtPregnancy.setText(birth);
		mNickname = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.NICKNAME);
		if (TextUtils.isEmpty(mNickname)) {
			txtLoginMsg.setText("登录/注册");
			txtNickname.setText("未登录");
		} else {
			txtLoginMsg.setText("注销登录");
			txtNickname.setText(mNickname);
		}
		topicIsOpen = SharedPreferencesUtil.getBooleanValue(mContext, ShareKeys.NOTIFY_AUTO, true);

		if (topicIsOpen) {
			topicSet.setText("点击关闭");
			getView().findViewById(R.id.layout_notification).setVisibility(View.VISIBLE);
			getView().findViewById(R.id.layout_notification_line).setVisibility(View.VISIBLE);
		} else {
			topicSet.setText("点击开启");
			getView().findViewById(R.id.layout_notification).setVisibility(View.GONE);
			getView().findViewById(R.id.layout_notification_line).setVisibility(View.GONE);
		}
		topicIsOpen = !topicIsOpen;
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);

		if (v.getId() == R.id.layout_eyes) {
			// 设置亮度
			showBrightnessDlg();
		} else if (v.getId() == R.id.layout_check_update) {
			// 检查更新
			BabytreeUtil.checkVersionUpdate(mContext, true);
		} else if (v.getId() == R.id.layout_login) {
			// 登录/注销

			if (mNickname == null) {
				// 跳转到登录页面 TODO
				MobclickAgent.onEvent(mContext, EventContants.setup, EventContants.setup_login);
				BabytreeUtil.launch(mContext, new Intent(mContext, LoginActivity.class), false, 0);
			} else {
				// 注销登录
				MobclickAgent.onEvent(mContext, EventContants.setup, EventContants.setup_logout);

				Resources r = this.getResources();
				String title = r.getString(R.string.s_exit_sure);
				String textLeft = r.getString(R.string.sure);
				DialogInterface.OnClickListener leftListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						txtLoginMsg.setText("登录/注册");
						txtNickname.setText("未登录");
						mNickname = null;
						// 清理信息
						logout();
					}
				};
				String textRight = r.getString(R.string.dialog_cancle);
				DialogInterface.OnClickListener rightListener = new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				};
				// 显示普通对话框
				showAlertDialog(title, "", null, textLeft, leftListener, textRight, rightListener);
			}

		} else if (v.getId() == R.id.layout_about) {
			// 关于我们
			MobclickAgent.onEvent(mContext, EventContants.setup, EventContants.setup_about);

			BabyTreeWebviewActivity.launch(mContext, UrlConstrants.ABOUT_US, "关于");

		} else if (v.getId() == R.id.layout_feedback) {
			// 打开反馈
			MobclickAgent.onEvent(mContext, EventContants.setup, EventContants.setup_feedback);
			UMFeedbackService.openUmengFeedbackSDK(mContext);
		} else if (v.getId() == R.id.layout_share) {
			// 分享应用
			MobclickAgent.onEvent(mContext, EventContants.setup, EventContants.setup_share);
			com.babytree.apps.comm.util.BabytreeUtil.shareApp(mContext);
		} else if (v.getId() == R.id.layout_more) {
			// 更多应用
			MobclickAgent.onEvent(mContext, EventContants.setup, EventContants.setup_more_app);
			Intent intent = new Intent(mContext, UnionActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.layout_notification) {
			// 提醒设置
			MobclickAgent.onEvent(mContext, EventContants.carer, EventContants.setup_noticeSetting);
			Intent i = new Intent();
			i.setClass(mContext, NotifySettingActivity.class);
			startActivity(i);
		} else if (v.getId() == R.id.layout_topic_set) {
			// 是否开始通知
			if (!topicIsOpen) {
				SharedPreferencesUtil.setValue(mContext, ShareKeys.NOTIFY_AUTO, false);
				topicIsOpen = true;
				topicSet.setText("点击开启");
				mApplication.getBabytreePushService().stopMessageService();
				mApplication.getBabytreePushService().stopLocalService();
				getView().findViewById(R.id.layout_notification).setVisibility(View.GONE);
				getView().findViewById(R.id.layout_notification_line).setVisibility(View.GONE);
			} else {
				SharedPreferencesUtil.setValue(mContext, ShareKeys.NOTIFY_AUTO, true);
				topicIsOpen = false;
				topicSet.setText("点击关闭");
				// 间隔
				int alerm = SharedPreferencesUtil.getIntValue(mContext, ShareKeys.NOTIFY_ALERM);
				mApplication.getBabytreePushService().restartLocalService(alerm);
				mApplication.getBabytreePushService().restartMessageService(alerm);
				getView().findViewById(R.id.layout_notification).setVisibility(View.VISIBLE);
				getView().findViewById(R.id.layout_notification_line).setVisibility(View.VISIBLE);
			}
		} else if (v.getId() == R.id.layout_visitbabytree) {
			// 访问宝宝树
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(Uri.parse("http://www.babytree.com"));// 设置一个URI地址
			startActivity(intent);// 用startActivity打开这个指定的网页。
		}
	}

	/**
	 * 注销操作 清理存储的信息以及cookies
	 */
	protected void logout() {
		// 退出需要清空的信息
		SharedPreferencesUtil.removeKeyArray(mContext, new String[] { ShareKeys.LOGIN_STRING, ShareKeys.NICKNAME,
				ShareKeys.HEAD, ShareKeys.HOSPITAL_ID, ShareKeys.ISCHOICEHOSPITAL, ShareKeys.HOSPITAL_NAME,
				ShareKeys.BABY_BIRTHDAY_TS, ShareKeys.GROUP_ID, ShareKeys.COOKIE, ShareKeys.USER_ENCODE_ID,
				ShareKeys.ISLOGINSTR, ShareKeys.ISNESSARYSYN });
		// 清空cookies
		com.babytree.apps.comm.util.BabytreeUtil.clearCookies(mContext);

		LocalBroadcastManager.getInstance(mContext.getApplicationContext()).sendBroadcast(
				new Intent(MommyMenuFragment.ACTION_LOGOUT));

	}

	/**
	 * 显示设置亮度对话框
	 */
	private void showBrightnessDlg() {
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		View view = View.inflate(mContext, R.layout.brightness_dlg, null);
		builder.setTitle("亮度");
		builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				if (android.os.Build.VERSION.SDK_INT > 7) {
					if (isAutoBrightness) {
						startAutoBrightness(mContext);
					} else {
						stopAutoBrightness(mContext);
						saveBrightness(mContext.getContentResolver(), brightness);
					}
				} else {
					saveBrightness(mContext.getContentResolver(), brightness);
				}
			}
		});
		builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				int max = getScreenBrightness(mContext);
				setScreenBrightness(mContext, max);

			}
		});
		builder.setOnKeyListener(new DialogInterface.OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
				return true;
			}
		});
		builder.setView(view);

		Dialog d = builder.create();
		d.setCanceledOnTouchOutside(false);
		d.show();

		CheckBox checkbox = (CheckBox) view.findViewById(R.id.checkBox1);
		final SeekBar seekbar = (SeekBar) view.findViewById(R.id.seekBar1);
		brightness = getScreenBrightness(mContext);
		seekbar.setMax(255);

		if (android.os.Build.VERSION.SDK_INT > 7) {
			checkbox.setVisibility(View.VISIBLE);
		} else {
			checkbox.setVisibility(View.GONE);
		}
		seekbar.setProgress(brightness);

		isAutoBrightness = isAutoBrightness(mContext.getContentResolver());
		if (android.os.Build.VERSION.SDK_INT > 7) {
			if (isAutoBrightness) {
				checkbox.setChecked(true);
				seekbar.setEnabled(false);
			} else {
				checkbox.setChecked(false);
				seekbar.setEnabled(true);
			}
		}

		checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				seekbar.setEnabled(!isChecked);
				isAutoBrightness = isChecked;
			}
		});

		seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				setScreenBrightness(mContext, progress);
				brightness = progress;
			}
		});
	}
}
