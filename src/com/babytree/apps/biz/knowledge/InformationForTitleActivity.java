package com.babytree.apps.biz.knowledge;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
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
import com.babytree.apps.comm.util.BabytreeTitleUtil;
import com.babytree.apps.comm.util.ImageCacheLoaderForBitmap;
import com.babytree.apps.comm.util.ImageCacheLoaderForBitmap.ImageCallbackForBitmap;
import com.babytree.apps.comm.util.Md5Util;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * 孕期每日知识(备孕期知识列表页)
 */
public class InformationForTitleActivity extends BabytreeActivity implements OnItemClickListener, OnClickListener,
		OnTouchListener {

	private ListView mListView;

	private PregnancyApplication mApplication;

	private CalendarDbAdapter mDbAdapter;

	private CalendarDbController mController;

	private MAdapter mAdapter;

	private ImageCacheLoaderForBitmap mImageCacheLoaderForBitmap;

	boolean autoNextPageLoading = false;

	private ArrayList<Knowledge> typeList;

	private PopupWindow mMenu;

	private MyAdatper myAdapter;

	private ListView searchListView;

	private String[] mString;
	private AlertDialog mDialog;
	private ImageView mImgIcon;

	private ImageView mImageOut;

	private ImageView mImageIn;

	private String typeName;

	private TextView layoutTitleTxt;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.knowledge_information_for_title_activity);

		layoutTitleTxt = (TextView) findViewById(R.id.information_header);
		mString = getIntent().getStringArrayExtra("mStrings");
		typeName = getIntent().getStringExtra("type_name");
		layoutTitleTxt.setText(typeName);

		mImgIcon = (ImageView) findViewById(R.id.img_icon);
		mImgIcon.setOnClickListener(this);
		mImageOut = (ImageView) findViewById(R.id.iv_item_bg_out);
		mImageIn = (ImageView) findViewById(R.id.iv_item_bg_in);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("加载中···");

		mListView = (ListView) findViewById(R.id.information_activity_list);
		mApplication = (PregnancyApplication) getApplication();
		mDbAdapter = mApplication.getCalendarDbAdapter();
		mController = new CalendarDbController(mDbAdapter);

		mImageCacheLoaderForBitmap = new ImageCacheLoaderForBitmap();

		mListView.setOnItemClickListener(this);
		findViewById(R.id.btn_left).setOnClickListener(this);
		// findViewById(R.id.btn_right).setOnClickListener(this);
		findViewById(R.id.btn_right_left).setOnClickListener(this);
		findViewById(R.id.txt_center).setOnClickListener(this);

		View view = View.inflate(this, R.layout.knowledge_information_search_menu, null);

		searchListView = (ListView) view.findViewById(R.id.layout_list_view);
		if (mString != null) {
			myAdapter = new MyAdatper();
			searchListView.setAdapter(myAdapter);
			searchListView.setOnItemClickListener(InformationForTitleActivity.this);
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

		// 判断是否为爸爸版,更改UI样式
		if (mIsFather) {
			findViewById(R.id.layout_title).setBackgroundDrawable(getResources().getDrawable(R.drawable.title_back));
			findViewById(R.id.btn_left).setBackgroundDrawable(getResources().getDrawable(R.drawable.f_btn_back));
			findViewById(R.id.btn_right_left).setVisibility(View.GONE);
		}
	}

	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			if (mDialog != null && !InformationForTitleActivity.this.isFinishing()) {
				mDialog.dismiss();
			}

			if (typeList.size() > 0) {
				mAdapter = new MAdapter();
				mListView.setAdapter(mAdapter);
			}
		};
	};

	private void initData() {
		mDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				typeList = mController.getKnowledgeListForType(typeName);
				Message msg = handler.obtainMessage();
				handler.sendMessage(msg);
			}
		}).start();

	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// mAdapter.mList = mController.getWeekList();
			/*
			 * int position = 0; int status = 0; position =
			 * intent.getIntExtra("position", 0); status =
			 * intent.getIntExtra("status", 0); List<Knowledge> list = new
			 * ArrayList<Knowledge>(); // list =
			 * mAdapter.mList.get(position/7).second; list =
			 * mAdapter.mList.get(position / 7).second.get(position % 7).list;
			 * for (int i = 0; i < list.size(); i++) { if (list.get(i).type_id
			 * == BabytreeConstants.TYPE_REMIND && list.get(i).is_important ==
			 * 1) { list.get(i).status = status; break; } }
			 * mAdapter.notifyDataSetChanged();
			 */
		}
	};

	@Override
	public void onStart() {
		super.onStart();

	}

	private class MAdapter extends BaseAdapter {
		private ArrayList<Knowledge> mList = typeList;

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewCache viewCache;
			if (convertView == null) {
				convertView = View.inflate(InformationForTitleActivity.this,
						R.layout.knowledge_information_for_title_item, null);
				viewCache = new ViewCache(convertView);
				convertView.setTag(viewCache);
			} else {
				viewCache = (ViewCache) convertView.getTag();
				/*
				 * if(position==0){ viewCache.getLayoutOther().setPadding(0, 30,
				 * 0, 0); }
				 */
			}

			final Knowledge knowledge = mList.get(position);

			LinearLayout linearPressed = viewCache.getLayoutPressed();//
			TextView everyDayTitle = viewCache.getEveryDayTitle();//
			TextView subTitle = viewCache.getSubTitle();//
			TextView content = viewCache.getContent();//
			ImageView image = viewCache.getContentImage();//

			LinearLayout layout_know = viewCache.getLayoutKnow();//

			linearPressed.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 知识
					Intent intent = new Intent(getApplicationContext(), InformationDetailActivity.class);
					intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
					intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
					intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
					startActivity(intent);
				}
			});

			layout_know.setVisibility(View.VISIBLE);

			if (knowledge.title != null && !knowledge.title.equals("")) {
				subTitle.setVisibility(View.VISIBLE);
				subTitle.setText(knowledge.title);
			} else {
				subTitle.setVisibility(View.GONE);
			}
			if (knowledge.category_id != 0) {
				everyDayTitle.setVisibility(View.VISIBLE);
				everyDayTitle.setText(BabytreeTitleUtil
						.switchTitle(knowledge.category_id, BabytreeTitleUtil.TYPE_TITLE));
			} else {
				everyDayTitle.setVisibility(View.GONE);
			}
			content.setText(knowledge.summary_content);
			// 图片
			if (knowledge.summary_image != null && !knowledge.summary_image.equals("")) {
				image.setVisibility(View.VISIBLE);
				image.setTag(Md5Util.md5(knowledge.summary_image));
				Bitmap mBitmap = mImageCacheLoaderForBitmap.loadDrawableForBitmap("htmls/", knowledge.summary_image,
						InformationForTitleActivity.this, new ImageCallbackForBitmap() {
							public void imageLoadedForBitmap(Bitmap bp, String imageUrl) {
								try {
									ImageView tagImage = (ImageView) mListView.findViewWithTag(imageUrl);
									if (tagImage != null) {
										if (bp != null) {
											// tagImage.setImageDrawable(imageDrawable);
											tagImage.setImageBitmap(bp);
										}
									}
								} catch (OutOfMemoryError e) {
									e.printStackTrace();
									bp.recycle();
								}
							}
						});
				if (mBitmap != null) {
					image.setImageBitmap(mBitmap);
				}

			} else {
				image.setVisibility(View.GONE);
			}

			/*
			 * int section = getSectionForPosition(position); boolean
			 * displaySectionHeaders = (getPositionForSection(section) ==
			 * position); bindSectionHeader(convertView, position,
			 * displaySectionHeaders); if (selectedPosition == position &&
			 * hasDaysNum >= 0) {
			 * linearLayout.setBackgroundResource(R.color.pink);
			 * today.setVisibility(View.GONE);
			 * yuchanqi.setVisibility(View.GONE); selectedPosition = -1; if
			 * (!displaySectionHeaders) { LinearLayout layoutOther =
			 * viewCache.getLayoutOther(); layoutOther.setPadding(0, 30, 0, 0);
			 * } } else {
			 * linearLayout.setBackgroundResource(R.color.background);
			 * today.setVisibility(View.GONE);
			 * yuchanqi.setVisibility(View.GONE);
			 * 
			 * }
			 */return convertView;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (mMenu.isShowing()) {
			mMenu.dismiss();
			mImageIn.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_left) {
			if (mMenu.isShowing()) {
				mMenu.dismiss();
				mImageIn.setVisibility(View.VISIBLE);
			}
			finish();
		}
		// else if (v.getId() == R.id.btn_right) {
		// // Umeng Evert
		// MobclickAgent.onEvent(getBaseContext(), EventContants.know,
		// EventContants.know_today);
		// mListView.setSelection(todaySelected);
		// mAdapter.setSelectedPosition(todaySelected);
		// mAdapter.notifyDataSetInvalidated();
		// }
		else if (v.getId() == R.id.btn_right_left) {
			// Umeng Evert

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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		MobclickAgent.onEvent(getBaseContext(), EventContants.carer, EventContants.knowledage_today);
		if (parent == searchListView) {
			typeName = mString[position];
			layoutTitleTxt.setText(typeName);
			initData();

			if (mMenu.isShowing()) {
				mMenu.dismiss();
				mImageIn.setVisibility(View.VISIBLE);
			}
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
		private TextView subTitle, content, everyDayTitle;

		private View baseView;

		private ImageView contentImage;

		private LinearLayout layoutPressed;

		public ViewCache(View view) {
			baseView = view;
		}

		public LinearLayout getLayoutPressed() {
			if (layoutPressed == null) {
				layoutPressed = (LinearLayout) baseView.findViewById(R.id.layout_pressed);
			}
			return layoutPressed;
		}

		public TextView getEveryDayTitle() {
			if (everyDayTitle == null) {
				everyDayTitle = (TextView) baseView.findViewById(R.id.txt_title);
			}
			return everyDayTitle;
		}

		public TextView getSubTitle() {
			if (subTitle == null) {
				subTitle = (TextView) baseView.findViewById(R.id.txt_subtitle);
			}
			return subTitle;
		}

		public TextView getContent() {
			if (content == null) {
				content = (TextView) baseView.findViewById(R.id.txt_content);
			}
			return content;
		}

		public ImageView getContentImage() {
			if (contentImage == null) {
				contentImage = (ImageView) baseView.findViewById(R.id.content_image);
			}
			return contentImage;
		}

		private LinearLayout layout_know;

		public LinearLayout getLayoutKnow() {
			if (layout_know == null) {
				layout_know = (LinearLayout) baseView.findViewById(R.id.layout_know);
			}
			return layout_know;
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
				convertView = View.inflate(InformationForTitleActivity.this,
						R.layout.knowledge_information_search_item, null);
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
