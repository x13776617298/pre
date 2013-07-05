package com.babytree.apps.biz.knowledge;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.babytree.apps.biz.knowledge.ctr.Y_CalendarDbController;
import com.babytree.apps.biz.knowledge.model.Y_Knowledge;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.db.Y_CalendarDbAdapter;
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
 * 育儿--每日厨房知识列表页
 * 
 * @author luozheng
 * 
 */
public class Y_KitchenActivity extends BabytreeActivity implements OnItemClickListener, OnClickListener {
	private PinnedHeaderListView mListView;

	private PregnancyApplication mApplication;

	private Y_CalendarDbAdapter mDbAdapter;

	private Y_CalendarDbController mController;

	private MAdapter mAdapter;

	private int topPadding = 0;

	// private ImageCacheLoaderForBitmap mImageCacheLoader;

	boolean autoNextPageLoading = false;

	private List<Pair<String, ArrayList<Y_Knowledge>>> weekList;

	private int hasDaysNum, todaySelected;

	private PopupWindow mMenu;

	private MyAdatper myAdapter;

	private ListView searchListView;

	private String[] mString;
	//
	// private boolean areButtonsShowing;
	// private RelativeLayout composerButtonsWrapper;
	// private ImageView composerButtonsShowHideButtonIcon;
	//
	// private RelativeLayout composerButtonsShowHideButton;

	private AlertDialog mDialog;
	private ImageView mImgIcon;
	private ImageView mImageOut;
	private ImageView mImageIn;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.knowledge_y_kitchen_activity);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("加载中···");
		mImgIcon = (ImageView) findViewById(R.id.img_icon);
		mImgIcon.setOnClickListener(this);
		mImageOut = (ImageView) findViewById(R.id.iv_item_bg_out);
		mImageIn = (ImageView) findViewById(R.id.iv_item_bg_in);
		mListView = (PinnedHeaderListView) findViewById(R.id.kitchen_activity_list);

		mApplication = (PregnancyApplication) getApplication();
		mDbAdapter = mApplication.y_getCalendarDbAdapter();
		mController = new Y_CalendarDbController(mDbAdapter);

		// mImageCacheLoader = new ImageCacheLoaderForBitmap();

		mListView.setOnItemClickListener(this);
		findViewById(R.id.btn_left).setOnClickListener(this);
		// findViewById(R.id.btn_right).setOnClickListener(this);
		findViewById(R.id.btn_right_left).setOnClickListener(this);
		findViewById(R.id.txt_center).setOnClickListener(this);
		hasDaysNum = SharedPreferencesUtil.getIntValue(this, ShareKeys.Y_HASDAYS);
		todaySelected = hasDaysNum;
		mString = initStrArray(52);

		View view = View.inflate(this, R.layout.knowledge_y_information_search_menu, null);
		myAdapter = new MyAdatper();
		searchListView = (ListView) view.findViewById(R.id.layout_list_view);
		searchListView.setAdapter(myAdapter);
		searchListView.setOnItemClickListener(this);
		int searchSelectPosition = todaySelected / 7;
		if (searchSelectPosition >= 0 && searchSelectPosition < 5) {
			searchListView.setSelection(0);
		} else {
			searchListView.setSelection(searchSelectPosition - 2);
		}
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

	}

	private String[] initStrArray(int num) {
		String[] array = new String[num];
		for (int i = 0; i < num; i++) {
			array[i] = "婴儿期第" + (i + 1) + "周";
		}

		return array;
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (mDialog != null && !Y_KitchenActivity.this.isFinishing()) {
				mDialog.dismiss();
			}
			if (weekList.size() > 40) {
				weekList.remove(40);
			}
			mListView.setPinnedHeaderView(LayoutInflater.from(Y_KitchenActivity.this).inflate(
					R.layout.knowledge_y_information_header, mListView, false));
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
		} else if (v.getId() == R.id.txt_center) {
			MobclickAgent.onEvent(getBaseContext(), EventContants.know, EventContants.know_search);
			if (mMenu.isShowing()) {
				mMenu.dismiss();
				mImageIn.setVisibility(View.VISIBLE);
			} else {
				mMenu.showAsDropDown(mImageOut, 2, -12);
				mImageIn.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (parent == searchListView) {
			mListView.setSelection(position * 7);
			mAdapter.notifyDataSetInvalidated();
			if (mMenu.isShowing()) {
				mMenu.dismiss();
				mImageIn.setVisibility(View.VISIBLE);
			}
		}
	}

	private class MAdapter extends PinnedHeaderListViewAdapter {
		private int selectedPosition = -1;// 选中的位置
		private List<Pair<String, ArrayList<Y_Knowledge>>> mList = weekList;

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
				convertView = View.inflate(Y_KitchenActivity.this, R.layout.knowledge_y_kitchen_activity_item, null);
				viewCache = new ViewCache(convertView);
				convertView.setTag(viewCache);
			} else {
				viewCache = (ViewCache) convertView.getTag();
			}
			final Y_Knowledge bean = (Y_Knowledge) getItem(position);
			LinearLayout llOther = viewCache.getLayoutOther();
			TextView today = viewCache.getToday();
			TextView week = viewCache.getWeek();
			TextView date = viewCache.getDate();
			TextView title = viewCache.getTitle();
			TextView content = viewCache.getContent();
			RelativeLayout kRL = viewCache.getKitchenRL();
			// ImageView cImageView = viewCache.getContentImage();
			today.setVisibility(View.GONE);
			// week
			final int has = hasDaysNum;

			week.setText(getWeek(getBeforeAfterDate(getPregnancyDay(has).toString(), (bean.days_number - 1))));
			// date
			Calendar cal = new GregorianCalendar();
			cal.setTime(getBeforeAfterDate(getPregnancyDay(has).toString(), (bean.days_number - 1)));
			date.setText((cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日");

			// title
			ArrayList<Y_Knowledge> list = bean.list;
			for (final Y_Knowledge knowledge : list) {
				if (knowledge.type_id == CommConstants.TYPE_KNOW
						&& knowledge.category_id == CommConstants.TYPE_KITCHEN19) {
					// yuchanqi
					// yuchanqi.setText("离预产期还有" + (280 - knowledge.days_number)
					// + "天");
					// title
					if (knowledge.title != null && !("").equals(knowledge.title)) {
						// title.setVisibility(View.VISIBLE);
						title.setText(knowledge.title);
					}
					// content
					content.setText(knowledge.summary_content);

					kRL.setOnClickListener(new OnClickListener() {

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
			if (position == has) {
				llOther.setBackgroundResource(R.color.light_green);
				today.setVisibility(View.VISIBLE);
				if (!displaySectionHeaders) {
					LinearLayout layoutOther = viewCache.getLayoutOther();
					layoutOther.setPadding(0, 30, 0, 0);
				}
			} else {
				llOther.setBackgroundResource(R.color.background);
				today.setVisibility(View.GONE);
			}
			// if (selectedPosition == position && hasDaysNum >= 0) {
			// llOther.setBackgroundResource(R.color.light_green);
			// today.setVisibility(View.VISIBLE);
			// // yuchanqi.setVisibility(View.VISIBLE);
			// selectedPosition = -1;
			// if (!displaySectionHeaders) {
			// LinearLayout layoutOther = viewCache.getLayoutOther();
			// layoutOther.setPadding(0, 30, 0, 0);
			// }
			// } else {
			// llOther.setBackgroundResource(R.color.background);
			// today.setVisibility(View.GONE);
			// // yuchanqi.setVisibility(View.GONE);
			//
			// }
			return convertView;
		}

		@Override
		public void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
			TextView tv = (TextView) view.findViewById(R.id.kitchen_header);
			LinearLayout layoutOther = (LinearLayout) view.findViewById(R.id.kitchen_layout_other);
			if (topPadding == 0) {
				topPadding = layoutOther.getPaddingTop();
			}
			if (displaySectionHeader) {
				tv.setVisibility(View.VISIBLE);
				tv.setText(getSections()[getSectionForPosition(position)]);
				layoutOther.setPadding(0, topPadding, 0, 0);
			} else {
				tv.setVisibility(View.GONE);
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

		int NewDay = Day - day;

		cal.set(Calendar.YEAR, Year);
		cal.set(Calendar.MONTH, Month);
		cal.set(Calendar.DAY_OF_MONTH, NewDay);
		return new java.sql.Date(cal.getTimeInMillis());
	}

	public java.sql.Date getBeforeAfterDate(String datestr, int day) {
		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
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
		private RelativeLayout kitchenRL;
		private TextView today, week, date, title, content;

		public ViewCache(View view) {
			this.baseView = view;
		}

		public RelativeLayout getKitchenRL() {
			if (kitchenRL == null) {
				kitchenRL = (RelativeLayout) baseView.findViewById(R.id.kitchen_rl);
			}
			return kitchenRL;
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
		// cImageView = (ImageView)baseView.findViewById(R.id.img_content);
		// }
		// return cImageView;
		// }
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
				convertView = View.inflate(Y_KitchenActivity.this, R.layout.knowledge_y_information_search_item, null);
				view = (TextView) convertView.findViewById(R.id.menu_text);
				convertView.setTag(view);
			} else {
				view = (TextView) convertView.getTag();
			}
			view.setText(mString[position].toString());
			return convertView;
		}

	}
}
