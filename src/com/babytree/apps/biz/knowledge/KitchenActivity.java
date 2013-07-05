package com.babytree.apps.biz.knowledge;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.babytree.apps.biz.knowledge.ctr.CalendarDbController;
import com.babytree.apps.biz.knowledge.model.Knowledge;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.db.CalendarDbAdapter;
import com.babytree.apps.comm.ui.SearchActivity;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.ui.adapter.PinnedHeaderListViewAdapter;
import com.babytree.apps.comm.ui.widget.PinnedHeaderListView;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * 孕期--每日厨房列表页
 * 
 * @author luozheng
 * 
 */
public class KitchenActivity extends BabytreeActivity implements OnItemClickListener, OnClickListener, OnTouchListener {
	private PinnedHeaderListView mListView;

	private PregnancyApplication mApplication;

	private CalendarDbAdapter mDbAdapter;

	private CalendarDbController mController;

	private MAdapter mAdapter;

	private int topPadding = 0;

	boolean autoNextPageLoading = false;

	private List<Pair<String, ArrayList<Knowledge>>> weekList;

	private int hasDaysNum, todaySelected;

	private PopupWindow mMenu;

	private MyAdatper myAdapter;

	private ListView searchListView;

	private final String[] mString = { "今天", "孕3周", "孕4周", "孕5周", "孕6周", "孕7周", "孕8周", "孕9周", "孕10周", "孕11周", "孕12周",
			"孕13周", "孕14周", "孕15周", "孕16周", "孕17周", "孕18周", "孕19周", "孕20周", "孕21周", "孕22周", "孕23周", "孕24周", "孕25周",
			"孕26周", "孕27周", "孕28周", "孕29周", "孕30周", "孕31周", "孕32周", "孕33周", "孕34周", "孕35周", "孕36周", "孕37周", "孕38周",
			"孕39周" };

	private AlertDialog mDialog;
	private ImageView mImgIcon;
	private ImageView mImageOut;
	private ImageView mImageIn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.knowledge_kitchen_activity);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("加载中···");
		mImgIcon = (ImageView) findViewById(R.id.img_icon);
		mImgIcon.setOnClickListener(this);
		mImageOut = (ImageView) findViewById(R.id.iv_item_bg_out);
		mImageIn = (ImageView) findViewById(R.id.iv_item_bg_in);
		mListView = (PinnedHeaderListView) findViewById(R.id.kitchen_activity_list);

		mApplication = (PregnancyApplication) getApplication();
		mDbAdapter = mApplication.getCalendarDbAdapter();
		mController = new CalendarDbController(mDbAdapter);

		mListView.setOnItemClickListener(this);
		findViewById(R.id.btn_left).setOnClickListener(this);
		findViewById(R.id.btn_right_left).setOnClickListener(this);
		findViewById(R.id.txt_center).setOnClickListener(this);
		// 妈妈怀孕天数
		hasDaysNum = BabytreeUtil.getBetweenDays(Calendar.getInstance(Locale.CHINA).getTimeInMillis(),
				SharedPreferencesUtil.getLongValue(this, ShareKeys.BIRTHDAY_TIMESTAMP));
		todaySelected = 280 - hasDaysNum - 3 * 7;

		View view = View.inflate(this, R.layout.knowledge_information_search_menu, null);
		myAdapter = new MyAdatper();
		searchListView = (ListView) view.findViewById(R.id.layout_list_view);
		searchListView.setAdapter(myAdapter);
		searchListView.setOnItemClickListener(this);
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int height = 0;
		if (metrics.heightPixels == 480) {
			height = (metrics.heightPixels) * 1 / 2 + 12;
		} else if (metrics.heightPixels == 854) {
			height = (metrics.heightPixels) * 1 / 2 - 35;
		} else {
			height = (metrics.heightPixels) * 1 / 2 - 5;
		}
		int width = (metrics.widthPixels) * 1 / 2 - 65;
		mMenu = new PopupWindow(view, width, height);
		mMenu.setBackgroundDrawable(getResources().getDrawable(R.drawable.sort_bkg_new));
		mMenu.setOutsideTouchable(true);
		mMenu.update();
		mMenu.setTouchable(true);
		mMenu.setFocusable(true);
		initData();

		// 判断是否为爸爸版,更改UI样式
		if (mIsFather) {
			findViewById(R.id.layout_title).setBackgroundDrawable(getResources().getDrawable(R.drawable.title_back));
			findViewById(R.id.btn_left).setBackgroundDrawable(getResources().getDrawable(R.drawable.f_btn_back));
			findViewById(R.id.btn_right_left).setVisibility(View.GONE);
		}

	}

	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (mDialog != null && !KitchenActivity.this.isFinishing()) {
				mDialog.dismiss();
			}
			if (weekList.size() > 40) {
				weekList.remove(40);
			}
			mListView.setPinnedHeaderView(LayoutInflater.from(KitchenActivity.this).inflate(
					R.layout.knowledge_information_header, mListView, false));
			mAdapter = new MAdapter();
			mListView.setAdapter(mAdapter);
			mAdapter.setSelectedPosition(todaySelected);
			mAdapter.notifyDataSetInvalidated();
			mListView.setSelection(todaySelected);
		};
	};

	private void initData() {
		mDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				weekList = mController.getWeekList();
				Message message = handler.obtainMessage();
				handler.sendMessage(message);
			}
		}).start();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_left) {
			if (mMenu.isShowing()) {
				mMenu.dismiss();
				mImageIn.setVisibility(View.VISIBLE);
			}
			finish();
		} else if (v.getId() == R.id.btn_right_left) {
			MobclickAgent.onEvent(getBaseContext(), EventContants.setup, EventContants.setup_share);
			startActivity(new Intent(this, SearchActivity.class));
		} else if (v.getId() == R.id.txt_center || v.getId() == R.id.img_icon) {
			MobclickAgent.onEvent(getBaseContext(), EventContants.know, EventContants.know_search);
			if (mMenu.isShowing()) {
				mMenu.dismiss();
				mImageIn.setVisibility(View.VISIBLE);
			} else {
				mMenu.showAsDropDown(mImageOut, 2, -10);
				mImageIn.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent == searchListView) {
			if (position > 0) {
				mListView.setSelection((position - 1) * 7);
			} else if (position == 0) {
				mListView.setSelection(todaySelected);
				mAdapter.setSelectedPosition(todaySelected);
			}
			mAdapter.notifyDataSetInvalidated();
			if (mMenu.isShowing()) {
				mMenu.dismiss();
				mImageIn.setVisibility(View.VISIBLE);
			}
		}
	}

	private class MAdapter extends PinnedHeaderListViewAdapter {
		private int selectedPosition = -1;// 选中的位置
		private List<Pair<String, ArrayList<Knowledge>>> mList = weekList;

		@Override
		public int getCount() {
			int count = 0;
			for (int i = 0; i < mList.size(); i++) {
				count += mList.get(i).second.size();
			}
			return count;
		}

		@Override
		public Object getItem(int position) {
			int count = 0;
			for (int i = 0; i < mList.size(); i++) {
				if (position >= count && position < count + mList.get(i).second.size()) {

					return mList.get(i).second.get(position - count);

				}
				count += mList.get(i).second.size();

			}

			return null;
		}

		public void setSelectedPosition(int position) {
			selectedPosition = position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewCache viewCache;
			if (convertView == null) {
				convertView = View.inflate(KitchenActivity.this, R.layout.knowledge_kitchen_activity_item, null);
				viewCache = new ViewCache(convertView);
				convertView.setTag(viewCache);
			} else {
				viewCache = (ViewCache) convertView.getTag();
			}
			final Knowledge bean = (Knowledge) getItem(position);
			LinearLayout llOther = viewCache.getLayoutOther();
			TextView today = viewCache.getToday();
			TextView yuchanqi = viewCache.getYuchanqi();
			TextView week = viewCache.getWeek();
			TextView date = viewCache.getDate();
			TextView title = viewCache.getTitle();
			TextView content = viewCache.getContent();
			LinearLayout clickLayout = viewCache.getLinearLayout();
			today.setVisibility(View.GONE);
			yuchanqi.setVisibility(View.GONE);
			// week
			final int has = hasDaysNum;
			week.setText(getWeek(getBeforeAfterDate(getPregnancyDay(has).toString(), (bean.days_number - 2))));
			// date
			Calendar cal = new GregorianCalendar();
			cal.setTime(getBeforeAfterDate(getPregnancyDay(has).toString(), (bean.days_number - 2)));
			date.setText((cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日");

			// title
			ArrayList<Knowledge> list = bean.list;
			for (final Knowledge knowledge : list) {
				if (knowledge.type_id == CommConstants.TYPE_KNOW
						&& knowledge.category_id == CommConstants.TYPE_KITCHEN5) {
					// yuchanqi
					yuchanqi.setText("离预产期还有" + hasDaysNum + "天");
					// title
					if (knowledge.title != null && !("").equals(knowledge.title)) {
						// title.setVisibility(View.VISIBLE);
						title.setText(knowledge.title);
					}
					// content
					content.setText(knowledge.summary_content);

					clickLayout.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {

							Intent intent = new Intent(mContext, KitchenDetailActivity.class);
							intent.putExtra(KitchenDetailActivity.BUNDLE_ID, knowledge._id);
							intent.putExtra(KitchenDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
							intent.putExtra(KitchenDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);

							BabytreeUtil.launch(mContext, intent, false, 0);

						}

					});

				}
			}
			int section = getSectionForPosition(position);
			boolean displaySectionHeaders = (getPositionForSection(section) == position);
			bindSectionHeader(convertView, position, displaySectionHeaders);
			if (selectedPosition == position && hasDaysNum >= 0) {
				llOther.setBackgroundResource(R.color.pink);
				today.setVisibility(View.VISIBLE);
				yuchanqi.setVisibility(View.VISIBLE);
				if (!displaySectionHeaders) {
					LinearLayout layoutOther = viewCache.getLayoutOther();
					layoutOther.setPadding(0, 30, 0, 0);
				}
			} else {
				llOther.setBackgroundResource(R.color.background);
				today.setVisibility(View.GONE);
				yuchanqi.setVisibility(View.GONE);

			}
			return convertView;
		}

		@Override
		public void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
			TextView tv = (TextView) view.findViewById(R.id.kitchen_header);
			LinearLayout layoutOther = (LinearLayout) view.findViewById(R.id.kitchen_layout_other);
			if (topPadding == 0) {
				topPadding = layoutOther.getPaddingTop();
			}
			LinearLayout layoutTitle = (LinearLayout) view.findViewById(R.id.kitchen_layout_title);
			if (displaySectionHeader) {
				layoutTitle.setVisibility(View.VISIBLE);
				tv.setText(getSections()[getSectionForPosition(position)]);
				layoutOther.setPadding(0, topPadding, 0, 0);
			} else {
				layoutTitle.setVisibility(View.GONE);
				layoutOther.setPadding(0, 0, 0, 0);
			}
		}

		@Override
		public void configurePinnedHeader(View header, int position, int alpha) {
			((TextView) header.findViewById(R.id.information_header))
					.setText(getSections()[getSectionForPosition(position)]);
		}

		@Override
		public int getPositionForSection(int section) {
			if (section < 0)
				section = 0;
			if (section >= mList.size())
				section = mList.size() - 1;
			int c = 0;
			for (int i = 0; i < mList.size(); i++) {
				if (section == i) {
					return c;
				}
				c += mList.get(i).second.size();
			}
			return 0;
		}

		@Override
		public int getSectionForPosition(int position) {
			int count = 0;
			for (int i = 0; i < mList.size(); i++) {
				if (position >= count && position < count + mList.get(i).second.size()) {
					return i;
				}
				count += mList.get(i).second.size();
			}
			return -1;
		}

		@Override
		public String[] getSections() {
			String[] res = new String[mList.size()];
			for (int i = 0; i < mList.size(); i++) {
				res[i] = mList.get(i).first;
			}
			return res;
		}
	}

	/**
	 * @param datestr
	 *            日期字符串
	 * @param day
	 *            相对天数，为正数表示之后，为负数表示之前
	 * @return 指定日期字符串n天之前或者之后的日期
	 */
	public java.sql.Date getPregnancyDay(int day) {

		Calendar cal = new GregorianCalendar();
		int Year = cal.get(Calendar.YEAR);
		int Month = cal.get(Calendar.MONTH);
		int Day = cal.get(Calendar.DAY_OF_MONTH);

		int NewDay = Day - (280 - day - 1);

		cal.set(Calendar.YEAR, Year);
		cal.set(Calendar.MONTH, Month);
		cal.set(Calendar.DAY_OF_MONTH, NewDay);
		return new java.sql.Date(cal.getTimeInMillis());
	}

	public java.sql.Date getBeforeAfterDate(String datestr, int day) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
		java.sql.Date olddate = null;
		try {
			df.setLenient(false);
			olddate = new java.sql.Date(df.parse(datestr).getTime());
		} catch (ParseException e) {
			throw new RuntimeException("日期转换错误");
		}
		Calendar cal = new GregorianCalendar();
		cal.setTime(olddate);

		int Year = cal.get(Calendar.YEAR);
		int Month = cal.get(Calendar.MONTH);
		int Day = cal.get(Calendar.DAY_OF_MONTH);

		int NewDay = Day + day;

		cal.set(Calendar.YEAR, Year);
		cal.set(Calendar.MONTH, Month);
		cal.set(Calendar.DAY_OF_MONTH, NewDay);
		return new java.sql.Date(cal.getTimeInMillis());
	}

	public String getWeek(java.util.Date date) {
		HashMap<Integer, String> hm = new HashMap<Integer, String>();
		hm.put(Calendar.SUNDAY, "周日");
		hm.put(Calendar.MONDAY, "周一");
		hm.put(Calendar.TUESDAY, "周二");
		hm.put(Calendar.WEDNESDAY, "周三");
		hm.put(Calendar.THURSDAY, "周四");
		hm.put(Calendar.FRIDAY, "周五");
		hm.put(Calendar.SATURDAY, "周六");
		Calendar cal = new GregorianCalendar();
		cal.setTime(date);
		return hm.get(cal.get(Calendar.DAY_OF_WEEK)).toString();
	}

	private static class ViewCache {
		private View baseView;
		private LinearLayout llOther;
		private TextView today, yuchanqi, week, date, title, content;
		private LinearLayout layout;

		public ViewCache(View view) {
			this.baseView = view;
		}

		public LinearLayout getLayoutOther() {
			if (llOther == null) {
				llOther = (LinearLayout) baseView.findViewById(R.id.kitchen_layout_other);
			}
			return llOther;
		}

		public TextView getToday() {
			if (today == null) {
				today = (TextView) baseView.findViewById(R.id.text_today);
			}
			return today;
		}

		public TextView getYuchanqi() {
			if (yuchanqi == null) {
				yuchanqi = (TextView) baseView.findViewById(R.id.text_yuchanqi);
			}
			return yuchanqi;
		}

		public TextView getWeek() {
			if (week == null) {
				week = (TextView) baseView.findViewById(R.id.text_pregnancy_week);
			}
			return week;
		}

		public TextView getDate() {
			if (date == null) {
				date = (TextView) baseView.findViewById(R.id.text_pregnancy_date);
			}
			return date;
		}

		public TextView getTitle() {
			if (title == null) {
				title = (TextView) baseView.findViewById(R.id.txt_title);
			}
			return title;
		}

		public TextView getContent() {
			if (content == null) {
				content = (TextView) baseView.findViewById(R.id.txt_content);
			}
			return content;
		}

		// public ImageView getContentImage(){
		// if(cImageView == null){
		// cImageView = (ImageView)baseView.findViewById(R.id.content_image);
		// }
		// return cImageView;
		// }
		public LinearLayout getLinearLayout() {
			if (layout == null) {
				layout = (LinearLayout) baseView.findViewById(R.id.click_layout);
			}
			return layout;
		}
	}

	private class MyAdatper extends BaseAdapter {

		@Override
		public int getCount() {
			return mString.length;
		}

		@Override
		public Object getItem(int position) {
			return mString[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			TextView view = null;
			if (convertView == null) {
				convertView = View.inflate(KitchenActivity.this, R.layout.knowledge_information_search_item, null);
				view = (TextView) convertView.findViewById(R.id.menu_text);
				convertView.setTag(view);
			} else {
				view = (TextView) convertView.getTag();
			}
			view.setText(mString[position].toString());
			return convertView;
		}

	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (mMenu != null && mMenu.isShowing()) {
			mMenu.dismiss();
			mImageIn.setVisibility(View.VISIBLE);
		}
		return false;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mMenu != null && mMenu.isShowing()) {
			mMenu.dismiss();
			mImageIn.setVisibility(View.VISIBLE);
		}
		return super.onTouchEvent(event);

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mMenu.isShowing()) {
			mMenu.dismiss();
			mImageIn.setVisibility(View.VISIBLE);
		}
	}
}
