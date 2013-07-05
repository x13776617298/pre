package com.babytree.apps.biz.welcome;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.babytree.apps.biz.home.HomePageActivity;
import com.babytree.apps.comm.ui.BirthdayActivity;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.pregnancy.R;

/**
 * 育儿欢迎页面
 * 
 */
public class Y_WelcomeActivity extends BabytreeActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.y_welcome_activity);
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1000);
					handler.sendEmptyMessage(1);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}.start();

	}

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			String isFromChangeBtn = getIntent().getStringExtra("is_from_change_btn");
			if (isFromChangeBtn == null || isFromChangeBtn.equals("")) {
				startActivity(new Intent(getApplicationContext(), HomePageActivity.class).putExtra("first", true));
			} else {
				startActivity(new Intent(getApplicationContext(), BirthdayActivity.class).putExtra("first", true));
			}
			finish();
		}

	};
}
