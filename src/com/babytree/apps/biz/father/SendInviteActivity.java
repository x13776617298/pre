package com.babytree.apps.biz.father;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.biz.father.ctr.FatherController;
import com.babytree.apps.biz.father.model.BindStatus;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.ctr.BaseController;
import com.babytree.apps.comm.net.BabytreeAsyncTask;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 发送邀请页面
 */
public class SendInviteActivity extends BabytreeTitleAcitivty implements OnClickListener {

	/**
	 * 发送邀请
	 */
	private Button btnInvite;

	/**
	 * 取消邀请
	 */
	private Button btnInviteCancel;

	/**
	 * 重新获取
	 */
	private Button btnInviteRetry;

	/**
	 * 再次邀请
	 */
	private Button btnInviteAgain;
	/**
	 * 邀请码
	 */
	private TextView tvCode;

	/**
	 * 邀请提示信息
	 */
	private TextView tvInfo;

	/**
	 * 成功
	 */
	private LinearLayout layoutInviteYes;

	/**
	 * 失败
	 */
	private LinearLayout layoutInviteNo;

	/**
	 * 重试
	 */
	private LinearLayout layoutInviteRetry;

	private Context mContext;

	/**
	 * 邀请短信信息
	 */
	private String SMS_MESSAGE = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mContext = this;
		btnInvite = (Button) findViewById(R.id.btn_send_invite);
		btnInvite.setOnClickListener(this);

		btnInviteCancel = (Button) findViewById(R.id.btn_send_invite_cancel);
		btnInviteCancel.setOnClickListener(this);

		btnInviteRetry = (Button) findViewById(R.id.btn_send_invite_retry);
		btnInviteRetry.setOnClickListener(this);

		btnInviteAgain = (Button) findViewById(R.id.btn_again_invite);
		btnInviteAgain.setOnClickListener(this);

		tvCode = (TextView) findViewById(R.id.tv_send_invite_code);
		tvInfo = (TextView) findViewById(R.id.tv_send_invite_info);

		layoutInviteYes = (LinearLayout) findViewById(R.id.layout_invite_yes);
		layoutInviteNo = (LinearLayout) findViewById(R.id.layout_invite_no);
		layoutInviteRetry = (LinearLayout) findViewById(R.id.layout_invite_retry);

		// 获取绑定状态
		getBindStatus();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_left) {
			finish();
		} else if (v.getId() == R.id.btn_send_invite_cancel) {
			unBind();// 解除绑定
		} else if (v.getId() == R.id.btn_send_invite) {
			sendInviteSMS();
		} else if (v.getId() == R.id.btn_send_invite_retry) {
			sendInviteSMS();
		} else if (v.getId() == R.id.btn_again_invite) {
			tvCode.setText(mContext.getResources().getString(R.string.s_invite_code_loading));
			// 获取绑定状态
			getBindStatus();
		}
	}

	/**
	 * 初始化邀请码
	 */
	private void initInviteCode(BindStatus bindStatus) {
		if ("0".equalsIgnoreCase(bindStatus.getBindStatus())) {// 没有绑定
			bindNo();
		} else {// 绑定成功
			tvInfo.setText(mContext.getResources().getString(R.string.s_invite_tip_already));
			bindYes();
		}
		tvCode.setText(bindStatus.getCode());// 设置邀请码
	}

	/**
	 * 初始化解绑邀请码
	 */
	private void initUnBindInviteCode(BindStatus bindStatus) {
		if ("1".equalsIgnoreCase(bindStatus.getBindStatus())) {// 没有绑定
			tvInfo.setText(mContext.getResources().getString(R.string.s_invite_tip));
			bindNo();
		} else {// 绑定成功
			bindYes();
		}
		tvCode.setText(bindStatus.getCode());// 设置邀请码
	}

	/**
	 * 已经绑定
	 */
	public void bindYes() {
		layoutInviteYes.setVisibility(View.VISIBLE);
		layoutInviteNo.setVisibility(View.GONE);
		layoutInviteRetry.setVisibility(View.GONE);
	}

	/**
	 * 没有绑定
	 */
	public void bindNo() {
		layoutInviteYes.setVisibility(View.GONE);
		layoutInviteRetry.setVisibility(View.GONE);
		layoutInviteNo.setVisibility(View.VISIBLE);
	}

	/**
	 * 重新邀请
	 */
	public void bindRetry() {
		layoutInviteYes.setVisibility(View.GONE);
		layoutInviteNo.setVisibility(View.GONE);
		layoutInviteRetry.setVisibility(View.VISIBLE);
	}

	/**
	 * 获取绑定状态
	 */
	private void getBindStatus() {
		new BindStatusTask(this).execute(getLoginString(), getGender());
	}

	/**
	 * 解除绑定
	 */
	private void unBind() {
		new UnBindTask(this).execute(getLoginString(), getGender());
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
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
		return getResources().getString(R.string.s_invite_father);
	}

	@Override
	public int getBodyView() {
		return R.layout.send_invite_activity;
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
				initInviteCode(bindStatus);// 初始化邀请码
				initInviteMsg(bindStatus);// 初始化邀请信息
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(mContext, BaseController.JSONExceptionMessage, Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected void failure(DataResult result) {
			bindRetry();
			tvCode.setText(mContext.getResources().getString(R.string.s_invite_code_retry));
			Toast.makeText(mContext, mContext.getResources().getString(R.string.s_fail_get_invite_code),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 解除绑定
	 */
	private class UnBindTask extends BabytreeAsyncTask {

		public UnBindTask(Context context) {
			super(context);
		}

		@Override
		protected DataResult toNet(String[] params) {
			return FatherController.unbind(params[0], params[1]);
		}

		@Override
		protected void success(DataResult result) {
			try {
				BindStatus bindStatus = (BindStatus) result.data;
				bindStatus.setBindStatus("1");// 解除成功手动赋值为"1"
				if ("1".equalsIgnoreCase(bindStatus.getBindStatus())) {// 解除绑定成功
					// 设置新邀请码
					initUnBindInviteCode(bindStatus);
					// 更新UI
					initInviteMsg(bindStatus);// 初始化邀请信息
				} else {// 解除绑定失败

				}
			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(mContext, BaseController.JSONExceptionMessage, Toast.LENGTH_SHORT).show();
			}

		}

		@Override
		protected void failure(DataResult result) {
			Toast.makeText(mContext, mContext.getResources().getString(R.string.s_fail_unbind), Toast.LENGTH_SHORT)
					.show();
		}

	}

	/**
	 * 初始化邀请信息
	 */
	private void initInviteMsg(BindStatus bindStatus) {
		// 生成邀请信息
		SMS_MESSAGE = bindStatus.getCode();
	}

	/**
	 * 发送邀请短信
	 */
	private void sendInviteSMS() {
		if (!TextUtils.isEmpty(SMS_MESSAGE)) {
			Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"));
			intent.putExtra("sms_body", String.format(getResources().getString(R.string.s_send_msg),
					CommConstants.APK_DADDY_LOAD_URL, SMS_MESSAGE));
			if (BabytreeUtil.hasIntentActivities(this, intent)) {
				startActivity(intent);
			} else {
				Toast.makeText(mContext, "没有找到相应的应用程序", Toast.LENGTH_SHORT).show();
			}
		} else {
			// 显示重新获取控件
			bindRetry();
			tvCode.setText(mContext.getResources().getString(R.string.s_invite_code_retry));
			Toast.makeText(mContext, mContext.getResources().getString(R.string.s_fail_get_invite_code),
					Toast.LENGTH_SHORT).show();
		}
	}
}