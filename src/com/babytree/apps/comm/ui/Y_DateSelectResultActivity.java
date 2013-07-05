package com.babytree.apps.comm.ui;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.babytree.apps.biz.home.HomePageActivity;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

/**
 * 育儿 设置生日 下一步
 * 
 * @author wangbingqi
 * 
 */
public class Y_DateSelectResultActivity extends BabytreeTitleAcitivty implements OnClickListener {
	private static final long NOW_MILLISEC = System.currentTimeMillis();
	private Button tv3;

	private TextView tv1, tv2;

	private long dateData;

	private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy年M月d日");

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		tv3 = (Button) findViewById(R.id.date_selector_result_startuse);
		tv1 = (TextView) findViewById(R.id.date_selector_result_date);
		tv2 = (TextView) findViewById(R.id.date_selector_result_daysnumber);
		tv3.setOnClickListener(this);
		dateData = SharedPreferencesUtil.getLongValue(this, ShareKeys.BIRTHDAY_TIMESTAMP);
		tv1.setText(mDateFormat.format(new java.util.Date(dateData)));
		tv2.setText(com.babytree.apps.comm.util.BabytreeUtil.getBabyBirthday(dateData));
	}

	public int getBetweenDays(long l) {
		Calendar nowCal = Calendar.getInstance(Locale.CHINA);
		long nowTime = nowCal.getTimeInMillis();
		long betweenTime = l - nowTime;
		if (betweenTime > 0 && betweenTime <= 364l * 24l * 60l * 60l * 1000l) {
			return (int) (betweenTime / (24l * 60l * 60l * 1000l)) + 1;
		} else {
			return 0;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.date_selector_result_startuse:
//			startActivity(new Intent(this, Y_HomePageActivity.class));
			startActivity(new Intent(this, HomePageActivity.class));
			finish();
			break;
		default:
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
		return "设置";
	}

	@Override
	public int getBodyView() {
		// TODO Auto-generated method stub
		return R.layout.y_date_selector_result;
	}

}
