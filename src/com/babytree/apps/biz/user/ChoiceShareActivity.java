package com.babytree.apps.biz.user;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.ctr.ThirdController;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.pregnancy.R;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.bean.SocializeEntity;
import com.umeng.socialize.bean.UMShareMsg;
import com.umeng.socialize.controller.listener.SocializeListeners;

public class ChoiceShareActivity extends BabytreeActivity implements OnClickListener {

	private String access_token;
	private String openid;

	private CheckBox mIsChoiceShare;
	private Button mSureButton;
	private String type;
	private ImageView imgChoiceShare;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		type = getIntent().getStringExtra("type");
		access_token = getIntent().getStringExtra("access_token");
		openid = getIntent().getStringExtra("openid");

		setContentView(R.layout.choice_share_activity);

		mIsChoiceShare = (CheckBox) findViewById(R.id.cb_choice);
		imgChoiceShare = (ImageView) findViewById(R.id.img_choice_share);
		if (mIsPregnancy) {
			// 育儿
			imgChoiceShare.setImageResource(R.drawable.ic_choice_share_yuer);
		} else {
			// 孕期
			imgChoiceShare.setImageResource(R.drawable.ic_choice_share_yunqi);
		}
		mSureButton = (Button) findViewById(R.id.btn_start);
		mSureButton.setOnClickListener(this);

	}

	@Override
	public void onClick(View v) {
		if (v == mSureButton) {
			ShareWB();
			finish();
		}
	}

	private void ShareWB() {
		if (mIsChoiceShare.isChecked()) {
			// 判断是否是新浪发微博
			if (type != null) {
				if (type.equals("1")) {
					UMShareMsg message = new UMShareMsg();
					message.text = getResources().getString(R.string.share_content);
					mApplication.getUmSocialService().postShare(ChoiceShareActivity.this, SHARE_MEDIA.SINA, message,
							new SocializeListeners.SnsPostListener() {

								@Override
								public void onComplete(SHARE_MEDIA platform, int eCode, SocializeEntity entity) {
									if (eCode == 200) {
										Toast.makeText(ChoiceShareActivity.this, "分享成功.", Toast.LENGTH_SHORT).show();
									} else {
										String eMsg = "";
										if (eCode == -101)
											eMsg = "没有授权";

										Toast.makeText(ChoiceShareActivity.this, "分享失败[" + eCode + "] " + eMsg,
												Toast.LENGTH_SHORT).show();
									}
								}

								@Override
								public void onStart() {
									Toast.makeText(ChoiceShareActivity.this, "开始分享.", Toast.LENGTH_SHORT).show();
								}

							});
				} else if (type.equals("2")) {
					new Thread() {
						public void run() {
							if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(ChoiceShareActivity.this)) {
								ThirdController.sharToTencent(access_token, CommConstants.TENCENT_APPID, openid,
										getResources().getString(R.string.share_content));
							}
						};
					}.start();

				}
			}
		}
	}

}
