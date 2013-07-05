package com.babytree.apps.comm.ui;

import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * 设置完预产期，下一步
 * 
 * @author luozheng
 * 
 */
public class DateSelectResultActivity extends BabytreeTitleAcitivty implements
		OnClickListener {
	private Button tv3;

	private TextView tv0, tv1, tv2;

	private long dateData;

	private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy年M月d日");

	private int method;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// 加入关闭通知监听(需要在onCreate之前调用)
		babytreecloselistener = this;
		super.onCreate(savedInstanceState);
		tv3 = (Button) findViewById(R.id.date_selector_result_startuse);
		tv0 = (TextView) findViewById(R.id.date_selector_result_tv);
		tv1 = (TextView) findViewById(R.id.date_selector_result_date);
		tv2 = (TextView) findViewById(R.id.date_selector_result_daysnumber);
		tv3.setOnClickListener(this);
		dateData = SharedPreferencesUtil.getLongValue(this,
				"birthday_timestamp");
		tv1.setText(mDateFormat.format(new java.util.Date(dateData)));
		tv2.setText("今天离预产期还有" + String.valueOf(getBetweenDays(dateData)) + "天");
		method = SharedPreferencesUtil.getIntValue(this, "method");
		if (method == 1) {
			tv0.setText("您的预产期是");
		}
	}

	public int getBetweenDays(long l) {
		Calendar nowCal = Calendar.getInstance(Locale.CHINA);
		long nowTime = nowCal.getTimeInMillis();
		long betweenTime = l - nowTime;
		if (betweenTime > 0 && betweenTime <= 280l * 24l * 60l * 60l * 1000l) {
			return (int) (betweenTime / (24l * 60l * 60l * 1000l)) + 1;
		} else {
			return 0;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.date_selector_result_startuse:
			startActivity(new Intent(this, ChoiceHospitalActivity.class));
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
		return R.layout.date_selector_result;
	}

}
