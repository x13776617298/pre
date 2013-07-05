package com.babytree.apps.biz.setting;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

import com.babytree.apps.biz.father.FatherIntrActivity;
import com.babytree.apps.biz.father.RoleSelectActivity;
import com.babytree.apps.biz.father.SendInviteActivity;
import com.babytree.apps.biz.father.ctr.FatherController;
import com.babytree.apps.biz.father.model.BindStatus;
import com.babytree.apps.biz.home.HomePageActivity;
import com.babytree.apps.biz.home.ui.MommyMenuFragment;
import com.babytree.apps.biz.user.LoginActivity;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.BaseController;
import com.babytree.apps.comm.net.BabytreeAsyncTask;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.ui.CalculatorActivity;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 孕期 设置页面
 */
public class SettingFragment extends SettingBaseFragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View viewBody = super.onCreateView(inflater, container, savedInstanceState);

		viewBody.findViewById(R.id.layout_invite).setOnClickListener(this);
		viewBody.findViewById(R.id.layout_change_2_daddy).setOnClickListener(this);
		return viewBody;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	@Override
	public void onClick(View v) {

		super.onClick(v);

		if (v.getId() == R.id.layout_change_2_daddy) {
			// 切换到爸爸版
			if (TextUtils.isEmpty(getLoginString())) {
				// TODO
				// closeOtherActivity();
				// finish();
				Intent intent = new Intent(mContext, RoleSelectActivity.class);
				startActivity(intent);
			} else {
				getBindStatus();
			}
		} else if (v.getId() == R.id.layout_invite) {
			// 邀请准爸爸
			mLoginString = getLoginString();
			if (mLoginString != null && !mLoginString.equals("")) {
				// Umeng Evert
				MobclickAgent.onEvent(mContext, EventContants.setup, EventContants.setup_duedate);
				Intent intent = new Intent(mContext, FatherIntrActivity.class);
				startActivity(intent);
			} else {
				Intent intent = new Intent(mContext, LoginActivity.class);
				startActivityForResult(intent, 111);
			}
		} else if (v.getId() == R.id.layout_edit) {
			// 跳转到修改预产期
			MobclickAgent.onEvent(mContext, EventContants.setup, EventContants.setup_duedate);
			Intent intent = new Intent(mContext, CalculatorActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.layout_change_app) {
			Resources r = this.getResources();
			String message = r.getString(R.string.s_switch_2_yuer);
			String textLeft = r.getString(R.string.sure);
			DialogInterface.OnClickListener leftListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferencesUtil.setValue(mContext, ShareKeys.IS_PREGNANCY, true);
					startActivity(new Intent(mContext, com.babytree.apps.biz.welcome.Y_WelcomeActivity.class).putExtra(
							"is_from_change_btn", "yes"));
					// TODO
					mContext.setResult(Activity.RESULT_OK);
					// 关闭其它界面 TODO
					// closeOtherActivity();
					SettingFragment.this.getActivity().finish();
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
			showAlertDialog("", message, null, textLeft, leftListener, textRight, rightListener);
		}
	}

	/**
	 * 切换到角色选择页面
	 */
	public void change2RoleSelect() {
		Resources r = this.getResources();
		String title = r.getString(R.string.s_switch_2_daddy);
		String message = r.getString(R.string.s_mommy_info_clear);
		String textLeft = r.getString(R.string.sure);
		DialogInterface.OnClickListener leftListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 清理配置信息
				logout();
				// 清理身份标识讯息
				clearRoleInfo();
				Intent intent = new Intent(mContext, RoleSelectActivity.class);
				startActivity(intent);
				// 关闭其它界面 TODO
				// closeOtherActivity();
				// finish();
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
		showAlertDialog(title, message, null, textLeft, leftListener, textRight, rightListener);
	}

	/**
	 * 清除身份标识信息
	 */
	private void clearRoleInfo() {
		SharedPreferencesUtil.removeKey(mContext, ShareKeys.APP_TYPE_KEY);
	}

	/**
	 * 获取绑定状态
	 */
	private void getBindStatus() {
		new BindStatusTask(mContext).execute(getLoginString(), getGender());
	}

	/**
	 * 绑定状态
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
				if ("0".equalsIgnoreCase(bindStatus.getBindStatus())) {// 没有绑定
					change2RoleSelect();// 跳转到身份选择页面
				} else {// 已经绑定
					Toast.makeText(mContext, mContext.getResources().getString(R.string.s_invite_unbind_first),
							Toast.LENGTH_LONG).show();
				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(mContext, BaseController.JSONExceptionMessage, Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected void failure(DataResult result) {
			Toast.makeText(mContext, result.message, Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 111 && resultCode == 888) {
			BabytreeLog.d("YQ Setting page to invite Login...");
			startActivity(new Intent(mContext, FatherIntrActivity.class));
		}
	}
}
