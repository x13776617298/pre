package com.babytree.apps.comm.ui;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.ui.widget.wheelview.NumericWheelAdapter;
import com.babytree.apps.comm.ui.widget.wheelview.OnWheelChangedListener;
import com.babytree.apps.comm.ui.widget.wheelview.WheelView;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 设置宝宝生日
 * 
 * @author wangbingqi
 * 
 */
public class BirthdayActivity extends BabytreeTitleAcitivty implements OnClickListener {
	private static final int START_YEAR = 1990, END_YEAR = 2100;
	private static final long NOW_MILLISEC = System.currentTimeMillis();
	private long pregnancyStart, pregnancyEnd;
	private boolean isFirst = false;
	private Button sureBtn;
	private TextView birthdayTxt;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy年M月d日");
	private long lastBirthday;
	/**
	 * 左按钮
	 */
	private Button mbTLeft;

	public void onCreate(Bundle savedInstanceState) {
		// 加入关闭通知监听(需要在onCreate之前调用)
		babytreecloselistener = this;
		super.onCreate(savedInstanceState);

		isFirst = getIntent().getBooleanExtra("first", false);
		sureBtn = (Button) findViewById(R.id.btn_sure);
		birthdayTxt = (TextView) findViewById(R.id.txt_birthday);
		sureBtn.setOnClickListener(this);
		birthdayTxt.setOnClickListener(this);
		if (isFirst) {
			mbTLeft.setVisibility(View.GONE);
			sureBtn.setText("下一步");
		} else {
			mbTLeft.setVisibility(View.VISIBLE);
			sureBtn.setVisibility(View.GONE);
			sureBtn.setText("确定");
		}
		lastBirthday = SharedPreferencesUtil.getLongValue(this, ShareKeys.BIRTHDAY_TIMESTAMP);
		if (lastBirthday == -1L) {
			birthdayTxt.setText(mDateFormat.format(new java.util.Date(NOW_MILLISEC)));
		} else {
			birthdayTxt.setText(mDateFormat.format(new java.util.Date(lastBirthday)));
		}
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btn_sure:
			startActivity(new Intent(this, Y_DateSelectResultActivity.class));
			break;
		case R.id.txt_birthday:
			showPopWindowForYue(arg0);
			break;
		}

	}

	private void setValueForView(int type, long pregnancyStart3, long pregnancyEnd3) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTimeInMillis(pregnancyEnd3);
		int month = calendar.get(Calendar.MONTH) + 1;
		String birthday = calendar.get(Calendar.YEAR) + "" + month;
		if (month < 10) {
			birthday = calendar.get(Calendar.YEAR) + "0" + month;
		}
		SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.BIRTHDAY, birthday); // 同龄圈
		SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.BIRTHDAY_TIMESTAMP, pregnancyEnd3);
		birthdayTxt.setText(mDateFormat.format(new java.util.Date(pregnancyEnd3)));
		// 设置为需要同步预产期/宝宝生日
		SharedPreferencesUtil.setValue(this, ShareKeys.IS_NEED_PRE, true);
	}

	private void showPopWindowForYue(View v) {
		final View view = this.getLayoutInflater().inflate(R.layout.pregnancy_calculator_pop_for_yue, null);
		final PopupWindow pop = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
		final Button okBtn = (Button) view.findViewById(R.id.btn_ok);

		final WheelView yearWV = (WheelView) view.findViewById(R.id.wv_year);
		final WheelView monthWV = (WheelView) view.findViewById(R.id.wv_month);
		final WheelView dayWV = (WheelView) view.findViewById(R.id.wv_day);
		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				pop.dismiss();
				MobclickAgent.onEvent(getBaseContext(), EventContants.carer, EventContants.personal_setDuedate);
				calculateBirthday(yearWV, monthWV, dayWV);
				setValueForView(0, pregnancyStart, pregnancyEnd);
				if (!isFirst) {
					setResult(0);
					finish();
				}
			}
		});
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		lastBirthday = SharedPreferencesUtil.getLongValue(this, ShareKeys.BIRTHDAY_TIMESTAMP);
		if (lastBirthday != -1L) {
			calendar.setTimeInMillis(lastBirthday);
		} else {
			calendar.setTimeInMillis(NOW_MILLISEC);
		}
		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DATE);

		// 添加大小月月份并将其转换为list,方便之后的判断
		String[] months_big = { "1", "3", "5", "7", "8", "10", "12" };
		String[] months_little = { "4", "6", "9", "11" };

		final List<String> list_big = Arrays.asList(months_big);
		final List<String> list_little = Arrays.asList(months_little);
		yearWV.setAdapter(new NumericWheelAdapter(START_YEAR, END_YEAR));// 设置"年"的显示数据
		yearWV.setCyclic(true);// 可循环滚动
		yearWV.setLabel("年");// 添加文字
		yearWV.setCurrentItem(year - START_YEAR);// 初始化时显示的数据
		// 月
		monthWV.setAdapter(new NumericWheelAdapter(1, 12));
		monthWV.setCyclic(true);
		monthWV.setLabel("月");
		monthWV.setCurrentItem(month);

		// 日
		dayWV.setCyclic(true);
		// 判断大小月及是否闰年,用来确定"日"的数据
		if (list_big.contains(String.valueOf(month + 1))) {
			dayWV.setAdapter(new NumericWheelAdapter(1, 31));
		} else if (list_little.contains(String.valueOf(month + 1))) {
			dayWV.setAdapter(new NumericWheelAdapter(1, 30));
		} else {
			// 闰年
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0)
				dayWV.setAdapter(new NumericWheelAdapter(1, 29));
			else
				dayWV.setAdapter(new NumericWheelAdapter(1, 28));
		}
		dayWV.setLabel("日");
		dayWV.setCurrentItem(day - 1);

		// 添加"年"监听
		OnWheelChangedListener wheelListener_year = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int year_num = newValue + START_YEAR;
				// 判断大小月及是否闰年,用来确定"日"的数据
				// 大月日范围：0-30
				if (list_big.contains(String.valueOf(monthWV.getCurrentItem() + 1))) {
					dayWV.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(monthWV.getCurrentItem() + 1))) {// 小月日范围：0-29
					if (dayWV.getCurrentItem() == 30) {
						dayWV.setCurrentItem(29);
					}
					dayWV.setAdapter(new NumericWheelAdapter(1, 30));
				} else {
					if ((year_num % 4 == 0 && year_num % 100 != 0) || year_num % 400 == 0) {
						// 闰年二月日范围：0-28
						if (dayWV.getCurrentItem() == 30 || dayWV.getCurrentItem() == 29) {
							dayWV.setCurrentItem(28);
						}
						dayWV.setAdapter(new NumericWheelAdapter(1, 29));
					} else {
						// 非闰年二月日范围：0-27
						if (dayWV.getCurrentItem() == 30 || dayWV.getCurrentItem() == 29
								|| dayWV.getCurrentItem() == 28) {
							dayWV.setCurrentItem(27);
						}
						dayWV.setAdapter(new NumericWheelAdapter(1, 28));
					}
				}
				// mWvDay.setCurrentItem(0);
				// Calculate
				calculateBirthday(yearWV, monthWV, dayWV);
			}
		};
		// 添加"月"监听
		OnWheelChangedListener wheelListener_month = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				int month_num = newValue + 1;
				// 判断大小月及是否闰年,用来确定"日"的数据
				if (list_big.contains(String.valueOf(month_num))) {
					dayWV.setAdapter(new NumericWheelAdapter(1, 31));
				} else if (list_little.contains(String.valueOf(month_num))) {
					if (dayWV.getCurrentItem() == 30) {
						dayWV.setCurrentItem(29);
					}
					dayWV.setAdapter(new NumericWheelAdapter(1, 30));

				} else {
					if (((yearWV.getCurrentItem() + START_YEAR) % 4 == 0 && (yearWV.getCurrentItem() + START_YEAR) % 100 != 0)
							|| (yearWV.getCurrentItem() + START_YEAR) % 400 == 0) {
						if (dayWV.getCurrentItem() == 30 || dayWV.getCurrentItem() == 29) {
							dayWV.setCurrentItem(28);
						}
						dayWV.setAdapter(new NumericWheelAdapter(1, 29));
					} else {
						if (dayWV.getCurrentItem() == 30 || dayWV.getCurrentItem() == 29
								|| dayWV.getCurrentItem() == 28) {
							dayWV.setCurrentItem(27);
						}
						dayWV.setAdapter(new NumericWheelAdapter(1, 28));
					}
				}
				// mWvDay.setCurrentItem(0);
				// Calculate
				calculateBirthday(yearWV, monthWV, dayWV);
			}
		};
		// 添加"日"监听
		OnWheelChangedListener wheelListener_day = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// Calculate
				calculateBirthday(yearWV, monthWV, dayWV);
			}
		};
		yearWV.addChangingListener(wheelListener_year);
		monthWV.addChangingListener(wheelListener_month);
		dayWV.addChangingListener(wheelListener_day);

		// 根据屏幕密度来指定选择器字体的大小
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		int textSize = (int) (dm.density * 20);

		dayWV.TEXT_SIZE = textSize;
		monthWV.TEXT_SIZE = textSize;
		yearWV.TEXT_SIZE = textSize;
		pop.setFocusable(true);
		pop.update();
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);
	}

	private void calculateBirthday(WheelView mWvYear, WheelView mWvMonth, WheelView mWvDay) {
		String year = mWvYear.getAdapter().getItem(mWvYear.getCurrentItem());
		String month = mWvMonth.getAdapter().getItem(mWvMonth.getCurrentItem());
		String day = mWvDay.getAdapter().getItem(mWvDay.getCurrentItem());
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.set(Calendar.YEAR, Integer.parseInt(year));
		calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
		pregnancyEnd = calendar.getTimeInMillis();
	}

	public void onResume() {
		super.onResume();
	}

	@Override
	public void setLeftButton(Button button) {
		mbTLeft = button;
	}

	@Override
	public void setRightButton(Button button) {

	}

	@Override
	public String getTitleString() {
		return "输入宝宝出生日期";
	}

	@Override
	public int getBodyView() {
		return R.layout.y_birthday_activity;
	}
}
