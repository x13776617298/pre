package com.babytree.apps.comm.ui;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.biz.home.ctr.HomeController;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.LocationDbController;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.model.Location;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * 个人资料编辑
 * 
 * @author wangbingqi
 * 
 */
public class PersonalInfoEditActvity extends BabytreeTitleAcitivty implements OnClickListener {
	private final String MALE = "男";
	private final String FAMALE = "女";
	private TextView emailTxt, nickNameTxt, sexTxt, positionTxt, registerTimeTxt;
	private ImageView icNicknameImg;
	private String emailStr, sexStr, positionStr = "1101";
	private String loginStr;
	private ProgressDialog mDialog;

	private LocationDbController mDbController;
	private PregnancyApplication mApplication;

	private StringBuffer allName;

	private Location location1;
	private Location location2;

	private int _id;

	private String content = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		allName = new StringBuffer();
		mApplication = (PregnancyApplication) getApplication();
		mDbController = new LocationDbController(mApplication.getLocationDbAdapter());

		icNicknameImg = (ImageView) findViewById(R.id.ic_arrow_name);
		findViewById(R.id.layout_position).setOnClickListener(this);
		findViewById(R.id.layout_sex).setOnClickListener(this);
		findViewById(R.id.layout_nickname).setOnClickListener(this);
		Button buttonSave = (Button) findViewById(R.id.btn_save);

		buttonSave.setOnClickListener(this);
		if (com.babytree.apps.comm.util.BabytreeUtil.isPregnancy(this)) {
			buttonSave.setBackgroundResource(R.drawable.btn_pressed1);
		}

		emailTxt = (TextView) findViewById(R.id.email);
		String whichThird = SharedPreferencesUtil.getStringValue(this, ShareKeys.WHICH_THIRD);
		if (whichThird != null && !whichThird.equals("")) {
			if (whichThird.equals("tenc")) {
				emailTxt.setText("腾讯微博帐号登录");
			} else if (whichThird.equals("sina")) {
				emailTxt.setText("新浪微博帐号登录");
			}
		} else {
			emailStr = SharedPreferencesUtil.getStringValue(this, "email");
			if (emailStr != null && !"".equals(emailStr)) {
				emailTxt.setText(emailStr);
			}
		}
		nickNameTxt = (TextView) findViewById(R.id.personal_nickname);
		String nickname = SharedPreferencesUtil.getStringValue(this, "nickname");
		if (nickname != null && !"".equals(nickname)) {
			nickNameTxt.setText(nickname);
		}
		registerTimeTxt = (TextView) findViewById(R.id.register_time);
		String registerTime = SharedPreferencesUtil.getStringValue(this, "reg_ts");
		if (registerTime != null && !"".equals(registerTime)) {
			registerTimeTxt.setText(registerTime);
		}
		sexTxt = (TextView) findViewById(R.id.personal_sex);
		sexStr = SharedPreferencesUtil.getStringValue(this, "gender");
		if (sexStr != null && !sexStr.equals("")) {
			String sexTmp = "";
			if (sexStr.equals("male")) {
				sexTmp = "男";
			} else {
				sexTmp = "女";
			}
			sexTxt.setText(sexTmp);
		}
		sexTxt.setOnClickListener(this);
		positionTxt = (TextView) findViewById(R.id.personal_position_position);

		positionStr = SharedPreferencesUtil.getStringValue(this, ShareKeys.LOCATION);
		if (positionStr == null || positionStr.equals("") || positionStr.equalsIgnoreCase("null")) {
			positionStr = "1101"; // 默认东城
		}
		if (positionStr != null && !positionStr.equals("")) {
			location2 = mDbController.getLocationById(Integer.parseInt(positionStr));
			if (location2 != null) {
				if (location2.province != null && !location2.province.equals("")) {
					location1 = mDbController.getLocationById(Integer.parseInt(location2.province));
				}
			}
			if (location1 != null) {
				allName.append(location1.name + "  " + location2.name);
				positionTxt.setText(allName);
			}
		}
		positionTxt.setOnClickListener(this);
		loginStr = SharedPreferencesUtil.getStringValue(this, ShareKeys.LOGIN_STRING);
	}

	@Override
	protected void onResume() {
		super.onResume();
		loginStr = SharedPreferencesUtil.getStringValue(this, ShareKeys.LOGIN_STRING);
		String mCanModify = SharedPreferencesUtil.getStringValue(PersonalInfoEditActvity.this, "can_modify_nickname");
		if (mCanModify != null && !mCanModify.equals("")) {
			if (mCanModify.equals("false")) {
				nickNameTxt.setTextColor(getResources().getColor(R.color.gray));
				icNicknameImg.setVisibility(View.GONE);
			} else if (mCanModify.equals("true")) {
				nickNameTxt.setTextColor(getResources().getColor(R.color.blue));
				icNicknameImg.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.layout_position:
			// 选择省份
			startActivityForResult(new Intent(this, LocationListActivity.class), 0);
			break;
		case R.id.layout_sex:
			sexStr = sexTxt.getText().toString().trim();
			if (null != sexStr && !"".equals(sexStr)) {
				if (MALE.equals(sexStr)) {
					sexStr = "female";
					sexTxt.setText(FAMALE);
				} else {
					sexStr = "male";
					sexTxt.setText(MALE);
				}
			}
			break;
		case R.id.layout_nickname:
			String mCanModify = SharedPreferencesUtil.getStringValue(PersonalInfoEditActvity.this,
					"can_modify_nickname");
			if (mCanModify != null && !mCanModify.equals("")) {
				if (mCanModify.equals("true")) {
					renameNickName();
				}
			}
			break;
		case R.id.btn_save:
			MobclickAgent.onEvent(getBaseContext(), EventContants.carer, EventContants.carer_savePersonInfo);
			if (null != loginStr && !"".equals(loginStr)) {
				savePersonalInfo(loginStr, sexStr, String.valueOf(_id), null);
			}

		default:
			break;
		}

	}

	private void savePersonalInfo(final String loginStr, final String sexStr, final String positionStr,
			final String mBirthday) {
		showDialog(null, "提交中...", null, null, true, null, null);
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();

				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(PersonalInfoEditActvity.this)) {
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
			if (mDialog != null && !PersonalInfoEditActvity.this.isFinishing()) {
				mDialog.dismiss();
			}
			DataResult ret = (DataResult) msg.obj;
			if (ret.status == P_BabytreeController.SUCCESS_CODE) {
				Toast.makeText(PersonalInfoEditActvity.this, "信息保存成功!", Toast.LENGTH_SHORT).show();
				SharedPreferencesUtil.setValue(PersonalInfoEditActvity.this, "gender", sexStr);
				SharedPreferencesUtil.setValue(PersonalInfoEditActvity.this, ShareKeys.LOCATION, String.valueOf(_id));

			} else {
				Toast.makeText(PersonalInfoEditActvity.this, ret.message, Toast.LENGTH_SHORT).show();
			}
		}
	};

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// if (resultCode == RESULT_OK) {
		try {
			String mpositionStr = data.getStringExtra("name");
			positionTxt.setText(mpositionStr);
			_id = data.getIntExtra("_id", 0);
		} catch (Exception e) {
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

	Handler reNameHandler = new Handler() {
		public void handleMessage(Message msg) {
			DataResult ret = (DataResult) msg.obj;
			if (ret.status == P_BabytreeController.SUCCESS_CODE) {
				MobclickAgent.onEvent(getBaseContext(), EventContants.index, EventContants.index_modifyNickname);
				Toast.makeText(PersonalInfoEditActvity.this, "昵称修改成功!", Toast.LENGTH_SHORT).show();
				SharedPreferencesUtil.setValue(PersonalInfoEditActvity.this, "can_modify_nickname", "false");
				nickNameTxt.setTextColor(getResources().getColor(R.color.gray));
				nickNameTxt.setText(content);
				SharedPreferencesUtil.setValue(PersonalInfoEditActvity.this, "nickname", content);
				icNicknameImg.setVisibility(View.GONE);
			} else {
				Toast.makeText(PersonalInfoEditActvity.this, ret.message, Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void renameNickName() {
		LayoutInflater factory = LayoutInflater.from(this);
		View view = factory.inflate(R.layout.mine_message_layout, null);
		final TextView txtUsername = (TextView) view.findViewById(R.id.txt_username);
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("修改昵称");
		builder.setView(view);
		builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Umeng Event

				content = txtUsername.getText().toString().trim();
				if (content.equals("")) {
					Toast.makeText(getApplicationContext(), "请输入新昵称", Toast.LENGTH_SHORT).show();
				} else {
					new Thread() {

						@Override
						public void run() {
							DataResult ret = null;
							Message message = new Message();
							try {
								if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(PersonalInfoEditActvity.this)) {
									ret = P_BabytreeController.reNameNickName(loginStr, content);
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
							reNameHandler.sendMessage(message);
						}

					}.start();

					dialog.dismiss();

				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
				dialog.dismiss();
			}
		});
		AlertDialog dialog = builder.create();
		// 显示之前用反射重新设置handler,阻止点击按钮关闭dialog
		try {
			Field field = dialog.getClass().getDeclaredField("mAlert");
			field.setAccessible(true);
			Object obj = field.get(dialog);
			field = obj.getClass().getDeclaredField("mHandler");
			field.setAccessible(true);
			field.set(obj, new ButtonHandler(dialog));

		} catch (Exception e) {
			e.printStackTrace();
		}

		dialog.show();
	}

	class ButtonHandler extends Handler {

		private WeakReference<DialogInterface> mDialog;

		public ButtonHandler(DialogInterface dialog) {
			mDialog = new WeakReference<DialogInterface>(dialog);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case DialogInterface.BUTTON_POSITIVE:
			case DialogInterface.BUTTON_NEGATIVE:
			case DialogInterface.BUTTON_NEUTRAL:
				((DialogInterface.OnClickListener) msg.obj).onClick(mDialog.get(), msg.what);
				break;
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
		return "个人资料编辑";
	}

	@Override
	public int getBodyView() {
		return R.layout.personal_info_edit;
	}

}
