package com.babytree.apps.comm.ui;

import com.babytree.apps.comm.ui.AboutMikaActivity;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MikaActivity extends BabytreeTitleAcitivty implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		findViewById(R.id.mika_img).setOnClickListener(this);
		findViewById(R.id.btn_mikafree).setOnClickListener(this);
		findViewById(R.id.btn_reviews).setOnClickListener(this);
		findViewById(R.id.iv_mika_service).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mika_img:
			startActivity(new Intent(this, AboutMikaActivity.class).putExtra("url",
					"file:///android_asset/others/aboutMika.shtml"));
			break;
		case R.id.btn_mikafree:
			startActivity(new Intent(this, ApplyFreeMikaActivity.class));
			break;
		case R.id.btn_reviews:
			startActivity(new Intent(this, MiKaCommentListActivity.class));
			break;
		case R.id.iv_mika_service:
			new AlertDialog.Builder(this).setTitle("确认拨出电话吗？")
					.setPositiveButton("是", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent();
							// 系统默认的action，用来打开默认的电话界面
							intent.setAction(Intent.ACTION_DIAL);
							intent.setData(Uri.parse("tel:" + AboutMikaActivity.SERVICE_TELEPHONE));
							if (BabytreeUtil.hasIntentActivities(mContext, intent)) {
								startActivity(intent);
							} else {
								Toast.makeText(mContext, "对不起，该设备不能拨打电话", Toast.LENGTH_LONG).show();
							}
						}
					}).setNegativeButton("否", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
						}
					}).show();
			break;
		}

	}

	@Override
	public void setLeftButton(Button button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRightButton(Button button) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTitleString() {
		// TODO Auto-generated method stub
		return "免费申请早教光盘";
	}

	@Override
	public int getBodyView() {
		// TODO Auto-generated method stub
		return R.layout.mika_activity;
	}

}
