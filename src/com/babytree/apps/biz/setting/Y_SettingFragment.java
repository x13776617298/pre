package com.babytree.apps.biz.setting;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ui.BirthdayActivity;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 育儿设置页面
 * 
 */
public class Y_SettingFragment extends SettingBaseFragment implements OnClickListener {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View viewBody = super.onCreateView(inflater, container, savedInstanceState);
		// 隐藏/修改关于孕期的内容
		txtChangeApp.setText(getResources().getString(R.string.s_setting_switfh_2_yunqi));
		txtPreOrBirthEdit.setText(getResources().getString(R.string.s_setting_parenting_edit));
		viewBody.findViewById(R.id.layout_invite_side).setVisibility(View.GONE);
		return viewBody;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onClick(View v) {

		super.onClick(v);

		if (v.getId() == R.id.layout_edit) {
			// Umeng Evert
			MobclickAgent.onEvent(mContext, EventContants.setup, EventContants.setup_duedate);
			// 跳转到修改宝宝生日
			Intent intent = new Intent(mContext, BirthdayActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.layout_change_app) {
			AlertDialog.Builder builder = new Builder(mContext);
			builder.setMessage("确认切换到快乐孕期版本吗？");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferencesUtil.setValue(mContext, ShareKeys.IS_PREGNANCY, false);
					startActivity(new Intent(mContext, com.babytree.apps.biz.welcome.WelcomeActivity.class));
					// TODO
					// setResult(RESULT_OK);
					// // 关闭其它界面
					// closeOtherActivity();
					// finish();
					Y_SettingFragment.this.getActivity().finish();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
			AlertDialog dialog = builder.create();
			dialog.show();
		}
	}

}
