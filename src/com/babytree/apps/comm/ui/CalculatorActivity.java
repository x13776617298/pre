package com.babytree.apps.comm.ui;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.babytree.apps.biz.home.ctr.HomeController;
import com.babytree.apps.biz.home.ui.MommyMenuFragment;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.BabytreeController;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.ui.widget.wheelview.NumericWheelAdapter;
import com.babytree.apps.comm.ui.widget.wheelview.OnWheelChangedListener;
import com.babytree.apps.comm.ui.widget.wheelview.WheelView;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 输入孕期的类
 * 
 * @author luozheng
 * 
 */
public class CalculatorActivity extends BabytreeTitleAcitivty implements OnClickListener {
	private static final int START_YEAR = 1990, END_YEAR = 2100;
	private static final int PREGNANCY_INTERVAL_DAY = 280;
	private static final int TYPE_YUE = 1;
	private static final int TYPE_INPUT = 2;
	private static final long NOW_MILLISEC = System.currentTimeMillis();
	private static final long ONE_DAY_MILLISEC = 24L * 60L * 60L * 1000L;
	private Button mOkBtn, cBtn, iBtn, sureBtn;
	private TextView yTxt, cycleTxt, pTxt;
	private LinearLayout cLL, pLL;
	private int mType = TYPE_YUE;
	private SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy年M月d日");
	private int cycleDay = 28;
	private boolean isFirst = false;
	private TextView resultTxt;
	private int preCycleDay = 28;
	private long pregnancyStart, pregnancyEnd;
	private long lastYueValue, lastInputValue;
	private int lastCycleDay;
	private WheelView yearWV;
	private WheelView monthWV;
	private WheelView dayWV;

	/**
	 * 左按钮
	 */
	private Button mBtLeft;

	public void onCreate(Bundle savedInstanceState) {
		// 加入关闭通知监听(需要在onCreate之前调用)
		babytreecloselistener = this;
		super.onCreate(savedInstanceState);

		isFirst = getIntent().getBooleanExtra("first", false);
		cBtn = (Button) findViewById(R.id.pregnancy_calculater);
		iBtn = (Button) findViewById(R.id.pregnancy_in);
		mOkBtn = (Button) findViewById(R.id.ok_btn);
		sureBtn = (Button) findViewById(R.id.sure_btn);
		yTxt = (TextView) findViewById(R.id.pregnancy_yue_txt);
		cycleTxt = (TextView) findViewById(R.id.pregnancy_cycle_txt);
		pTxt = (TextView) findViewById(R.id.pregnancy_pregnancy_txt);
		cLL = (LinearLayout) findViewById(R.id.pregnancy_yue_ll);
		pLL = (LinearLayout) findViewById(R.id.pregnancy_pregnancy_ll);
		resultTxt = (TextView) findViewById(R.id.pregnancy_result);
		mOkBtn.setOnClickListener(this);
		sureBtn.setOnClickListener(this);
		yTxt.setOnClickListener(this);
		cycleTxt.setOnClickListener(this);
		pTxt.setOnClickListener(this);
		cBtn.setOnClickListener(this);
		iBtn.setOnClickListener(this);
		lastYueValue = SharedPreferencesUtil.getLongValue(this, ShareKeys.LAST_YUE_TXT_VALUE);
		lastInputValue = SharedPreferencesUtil.getLongValue(this, ShareKeys.LAST_INPUT_TXT_VALUE);
		lastCycleDay = SharedPreferencesUtil.getIntValue(this, ShareKeys.LAST_CYCLE_TXT_VALUE);
		if (lastYueValue == -1L) {
			yTxt.setText(mDateFormat.format(new java.util.Date(NOW_MILLISEC)));
		} else {
			yTxt.setText(mDateFormat.format(new java.util.Date(lastYueValue)));
		}
		if (lastCycleDay == -1) {
			cycleTxt.setText(String.valueOf(28));
		} else {
			cycleTxt.setText(String.valueOf(lastCycleDay));
		}
		if (lastInputValue == -1L) {
			pTxt.setText(mDateFormat.format(new java.util.Date(NOW_MILLISEC + 280L * ONE_DAY_MILLISEC)));
		} else {
			pTxt.setText(mDateFormat.format(new java.util.Date(lastInputValue)));
		}

		if (isFirst) {

			mBtLeft.setVisibility(View.GONE);
			mOkBtn.setVisibility(View.VISIBLE);
			sureBtn.setVisibility(View.GONE);
		} else {
			mBtLeft.setVisibility(View.VISIBLE);
			mOkBtn.setVisibility(View.GONE);
			sureBtn.setVisibility(View.GONE);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.ok_btn:
			if (pregnancyStart <= 0L && pregnancyEnd <= 0L) {
				if (mType == TYPE_INPUT) {
					pregnancyStart = NOW_MILLISEC;
					pregnancyEnd = NOW_MILLISEC + PREGNANCY_INTERVAL_DAY * ONE_DAY_MILLISEC;
					setValueForView(0, pregnancyStart, pregnancyEnd);
					pregnancyStart = 0L;
					pregnancyEnd = 0L;
				} else {
					pregnancyStart = NOW_MILLISEC;
					pregnancyEnd = NOW_MILLISEC + PREGNANCY_INTERVAL_DAY * ONE_DAY_MILLISEC;
					setValueForView(0, pregnancyStart, pregnancyEnd);
					pregnancyStart = 0L;
					pregnancyEnd = 0L;
				}
			}
			startActivity(new Intent(this, DateSelectResultActivity.class));
			break;
		case R.id.pregnancy_calculater:
			lastYueValue = SharedPreferencesUtil.getLongValue(this, ShareKeys.LAST_YUE_TXT_VALUE);
			lastInputValue = SharedPreferencesUtil.getLongValue(this, ShareKeys.LAST_INPUT_TXT_VALUE);
			lastCycleDay = SharedPreferencesUtil.getIntValue(this, ShareKeys.LAST_CYCLE_TXT_VALUE);
			resultTxt.setText("");
			mType = TYPE_YUE;
			cLL.setVisibility(View.VISIBLE);
			pLL.setVisibility(View.GONE);
			cBtn.setBackgroundResource(R.drawable.pregnancy_left_selected);
			iBtn.setBackgroundResource(R.drawable.pregnancy_right_normal);
			cBtn.setTextColor(Color.WHITE);
			iBtn.setTextColor(Color.BLACK);
			showPopWindowForYue(v, TYPE_YUE);
			break;
		case R.id.pregnancy_in:// 输入预产期
			lastYueValue = SharedPreferencesUtil.getLongValue(this, ShareKeys.LAST_YUE_TXT_VALUE);
			lastInputValue = SharedPreferencesUtil.getLongValue(this, ShareKeys.LAST_INPUT_TXT_VALUE);
			lastCycleDay = SharedPreferencesUtil.getIntValue(this, ShareKeys.LAST_CYCLE_TXT_VALUE);
			resultTxt.setText("");
			mType = TYPE_INPUT;
			cLL.setVisibility(View.GONE);
			pLL.setVisibility(View.VISIBLE);
			cBtn.setBackgroundResource(R.drawable.pregnancy_left_normal);
			iBtn.setBackgroundResource(R.drawable.pregnancy_right_selected);
			cBtn.setTextColor(Color.BLACK);
			iBtn.setTextColor(Color.WHITE);
			showPopWindowForYue(v, TYPE_INPUT);
			break;
		case R.id.pregnancy_yue_txt:
			showPopWindowForYue(v, TYPE_YUE);// 末次月经第一天
			break;
		case R.id.pregnancy_cycle_txt:
			showPopWindowForCycle(v);// 月经周期
			break;
		case R.id.pregnancy_pregnancy_txt:
			showPopWindowForYue(v, TYPE_INPUT);// 输入预产期
			break;
		case R.id.sure_btn:
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 保存预产期
	 * 
	 * @param pregnancyMills
	 */
	private void saveAndSyncPregnacy(long pregnancyMills) {
		SimpleDateFormat mDateFormatForApi = new SimpleDateFormat("yyyy-M-d");
		String loginString = getLoginString();
		if (!TextUtils.isEmpty(loginString)) {
			syncPre(mDateFormatForApi.format(new Date(pregnancyMills)), loginString);
		}

		// 预产期修改 - 发送预产期变动的广播 - 通知左侧菜单页面刷新UI
		LocalBroadcastManager.getInstance(mContext.getApplicationContext()).sendBroadcast(
				new Intent(MommyMenuFragment.ACTION_PRENANCY_CHANGED));
		BabytreeLog.d("同步预产期 and 发送预产期变更广播");

	}

	/**
	 * 同步预产期
	 * 
	 * @author wangshuaibo
	 */
	private void syncPre(final String tempPre, final String loginString) {
		BabytreeLog.d("同步预产期 - saveAndSyncPregnacy(pregnancyEnd) - " + tempPre);
		final Handler mHandler = new Handler() {
			public void handleMessage(Message msg) {
				DataResult ret = (DataResult) msg.obj;
				if (ret.status == BabytreeController.SUCCESS_CODE) {
					BabytreeLog.d("Sync baby birthday success.");
					String tmpBirthday = (String) ret.data;
					SharedPreferencesUtil.setValue(CalculatorActivity.this, ShareKeys.IS_NEED_PRE, false);
					SharedPreferencesUtil.setValue(CalculatorActivity.this, ShareKeys.BABY_BIRTHDAY_TS, tmpBirthday);
				} else {
					BabytreeLog.d("Sync baby birthday faild.");
				}
			}
		};
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(CalculatorActivity.this)) {
						ret = HomeController.savePersonalInfo(loginString, null, null, tempPre);
					} else {
						ret = new DataResult();
						ret.message = BabytreeController.NetworkExceptionMessage;
						ret.status = BabytreeController.NetworkExceptionCode;
					}
				} catch (Exception e) {
					ret = new DataResult();
					ret.message = BabytreeController.SystemExceptionMessage;
					ret.status = BabytreeController.SystemExceptionCode;
				}
				message.obj = ret;
				mHandler.sendMessage(message);
			}
		}.start();
	}

	private void setValueForView(int type, long pregnancyStart3, long pregnancyEnd3) {
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTimeInMillis(pregnancyEnd3);
		int month = calendar.get(Calendar.MONTH) + 1;
		String birthday = calendar.get(Calendar.YEAR) + "" + month;
		if (month < 10) {
			birthday = calendar.get(Calendar.YEAR) + "0" + month;
		}
		SharedPreferencesUtil.setValue(this, ShareKeys.BIRTHDAY, birthday); // 同龄圈
		SharedPreferencesUtil.setValue(this, ShareKeys.BIRTHDAY_TIMESTAMP, pregnancyEnd3);// 保存预产期时间milliseconds
		// 设置view value
		resultTxt.setText("您的预产期为：" + mDateFormat.format(new java.util.Date(pregnancyEnd3)));
		if (type == TYPE_YUE) {
			yTxt.setText(mDateFormat.format(new java.util.Date(pregnancyStart3)));
			cycleTxt.setText(String.valueOf(cycleDay));
			SharedPreferencesUtil.setValue(this, ShareKeys.LAST_YUE_TXT_VALUE, pregnancyStart3);
			SharedPreferencesUtil.setValue(this, ShareKeys.LAST_CYCLE_TXT_VALUE, cycleDay);
		} else if (type == TYPE_INPUT) {
			pTxt.setText(mDateFormat.format(new java.util.Date(pregnancyEnd3)));
			SharedPreferencesUtil.setValue(this, ShareKeys.LAST_INPUT_TXT_VALUE, pregnancyEnd3);
		}
		// 设置为需要同步预产期/宝宝生日
		SharedPreferencesUtil.setValue(this, ShareKeys.IS_NEED_PRE, true);
	}

	private void showPopWindowForCycle(View v) {
		final View view = this.getLayoutInflater().inflate(R.layout.pregnancy_calculator_pop_for_cycle, null);
		final PopupWindow pop = new PopupWindow(view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);
		final WheelView cycleWV = (WheelView) view.findViewById(R.id.start_activity_cycle_whv);
		cycleWV.setCyclic(true);
		cycleWV.setLabel("天");
		cycleWV.setAdapter(new NumericWheelAdapter(20, 45));
		// final int currentDay = SharedPreferencesUtil.getIntValue(this,
		// "cycle_day");
		if (lastCycleDay != -1) {
			cycleWV.setCurrentItem(lastCycleDay - 20);
		} else {
			cycleWV.setCurrentItem(8);
		}
		// 根据屏幕密度来指定选择器字体的大小
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		int textSize = (int) (dm.density * 20);
		cycleWV.TEXT_SIZE = textSize;
		if (lastYueValue == -1L) {
			pregnancyStart = NOW_MILLISEC;
			pregnancyEnd = NOW_MILLISEC + PREGNANCY_INTERVAL_DAY * ONE_DAY_MILLISEC;
		} else {
			pregnancyStart = lastYueValue;
			pregnancyEnd = lastYueValue + PREGNANCY_INTERVAL_DAY * ONE_DAY_MILLISEC;
		}
		OnWheelChangedListener wheelListener = new OnWheelChangedListener() {

			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				cycleDay = cycleWV.getCurrentItem() + 20;
				calculateBirthday(TYPE_YUE, yearWV, monthWV, dayWV);
				setValueForView(TYPE_YUE, pregnancyStart, pregnancyEnd);
			}

		};
		cycleWV.addChangingListener(wheelListener);
		view.findViewById(R.id.btn_ok).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				pop.dismiss();
				MobclickAgent.onEvent(getBaseContext(), EventContants.carer, EventContants.personal_setDuedate);
				cycleDay = cycleWV.getCurrentItem() + 20;
				pregnancyEnd += (cycleDay - preCycleDay) * ONE_DAY_MILLISEC;
				setValueForView(TYPE_YUE, pregnancyStart, pregnancyEnd);

				// TODO pengxh add new 同步预产期
				saveAndSyncPregnacy(pregnancyEnd);
			}

		});
		pop.setFocusable(true);
		pop.update();
		pop.setBackgroundDrawable(new BitmapDrawable());
		pop.showAtLocation(v, Gravity.BOTTOM, 0, 0);
	}

	private void showPopWindowForYue(View v, final int type) {
		final View view = this.getLayoutInflater().inflate(R.layout.pregnancy_calculator_pop_for_yue, null);
		final PopupWindow pop = new PopupWindow(view, LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT, true);
		final Button okBtn = (Button) view.findViewById(R.id.btn_ok);

		yearWV = (WheelView) view.findViewById(R.id.wv_year);
		monthWV = (WheelView) view.findViewById(R.id.wv_month);
		dayWV = (WheelView) view.findViewById(R.id.wv_day);
		okBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				pop.dismiss();
				if (mType == TYPE_YUE) {
					MobclickAgent.onEvent(getBaseContext(), EventContants.carer, EventContants.personal_computeDuedate);
					calculateBirthday(TYPE_YUE, yearWV, monthWV, dayWV);
					setValueForView(TYPE_YUE, pregnancyStart, pregnancyEnd);
					// TODO pengxh add new 同步预产期
					saveAndSyncPregnacy(pregnancyEnd);
				} else if (mType == TYPE_INPUT) {
					MobclickAgent.onEvent(getBaseContext(), EventContants.carer, EventContants.personal_setDuedate);
					calculateBirthday(TYPE_INPUT, yearWV, monthWV, dayWV);
					setValueForView(TYPE_INPUT, pregnancyStart, pregnancyEnd);
					// TODO pengxh add new 同步预产期
					saveAndSyncPregnacy(pregnancyEnd);
				}
				if (!isFirst) {
					setResult(0);
					finish();
				}
			}
		});
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		if (mType == TYPE_YUE) {
			if (lastYueValue == -1L) {
				calendar.setTimeInMillis(NOW_MILLISEC);
			} else {
				calendar.setTimeInMillis(lastYueValue);
			}

		} else if (mType == TYPE_INPUT) {
			if (lastInputValue == -1L) {
				calendar.setTimeInMillis(NOW_MILLISEC + 280L * ONE_DAY_MILLISEC);
			} else {
				calendar.setTimeInMillis(lastInputValue);
			}

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
				calculateBirthday(type, yearWV, monthWV, dayWV);
				setValueForView(TYPE_INPUT, pregnancyStart, pregnancyEnd);
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
				// Calculate
				calculateBirthday(type, yearWV, monthWV, dayWV);
				setValueForView(TYPE_INPUT, pregnancyStart, pregnancyEnd);
			}
		};
		// 添加"日"监听
		OnWheelChangedListener wheelListener_day = new OnWheelChangedListener() {
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				// Calculate
				calculateBirthday(type, yearWV, monthWV, dayWV);
				setValueForView(TYPE_INPUT, pregnancyStart, pregnancyEnd);
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

	private void calculateBirthday(int type, WheelView mWvYear, WheelView mWvMonth, WheelView mWvDay) {
		if (mWvYear != null) {
			String year = mWvYear.getAdapter().getItem(mWvYear.getCurrentItem());
			String month = mWvMonth.getAdapter().getItem(mWvMonth.getCurrentItem());
			String day = mWvDay.getAdapter().getItem(mWvDay.getCurrentItem());
			Calendar calendar = Calendar.getInstance(Locale.CHINA);
			calendar.set(Calendar.YEAR, Integer.parseInt(year));
			calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
			calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
			if (type == TYPE_YUE) {
				pregnancyStart = calendar.getTimeInMillis();
				pregnancyEnd = pregnancyStart + (cycleDay - 28 + PREGNANCY_INTERVAL_DAY) * ONE_DAY_MILLISEC;
			} else if (type == TYPE_INPUT) {
				calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
				pregnancyEnd = calendar.getTimeInMillis();
			}
		}
	}

	@Override
	public void setLeftButton(Button button) {
		mBtLeft = button;
	}

	@Override
	public void setRightButton(Button button) {
	}

	@Override
	public String getTitleString() {
		return "预产期计算";
	}

	@Override
	public int getBodyView() {
		return R.layout.pregnancy_calculator_activity;
	}
}
