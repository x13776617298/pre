package com.babytree.apps.biz.knowledge;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
import com.babytree.apps.comm.util.BabytreeTitleUtil;
import com.babytree.apps.comm.util.ImageCacheLoaderForBitmap;
import com.babytree.apps.comm.util.ImageCacheLoaderForBitmap.ImageCallbackForBitmap;
import com.babytree.apps.comm.util.Md5Util;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * 育儿--每日知识列表页
 * 
 * @author luozheng
 * 
 */
public class Y_KnowledgeActivity extends BabytreeActivity implements OnItemClickListener, OnClickListener {
	private PinnedHeaderListView mListView;

	private PregnancyApplication mApplication;

	private Y_CalendarDbAdapter mDbAdapter;

	private Y_CalendarDbController mController;

	private MAdapter mAdapter;

	private int topPadding = 0;

	private ImageCacheLoaderForBitmap mImageCacheLoader;

	boolean autoNextPageLoading = false;

	private List<Pair<String, ArrayList<Y_Knowledge>>> weekList;

	private int hasDaysNum, todaySelected;

	private PopupWindow mMenu;

	private MyAdatper myAdapter;

	private ListView searchListView;

	private String[] mString;

	private AlertDialog mDialog;
	private ImageView mImgIcon;
	private ImageView mImageOut;
	private ImageView mImageIn;
	private DisplayMetrics metrics;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.knowledge_y_knowledge_activity);

		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction("DataUpdate");
		LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);

		mImageOut = (ImageView) findViewById(R.id.iv_item_bg_out);
		mImageIn = (ImageView) findViewById(R.id.iv_item_bg_in);
		metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		findViewById(R.id.txt_center).setOnClickListener(this);
		mImgIcon = (ImageView) findViewById(R.id.img_icon);
		mImgIcon.setOnClickListener(this);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("加载中···");

		mListView = (PinnedHeaderListView) findViewById(R.id.information_activity_list);

		mApplication = (PregnancyApplication) getApplication();
		mDbAdapter = mApplication.y_getCalendarDbAdapter();
		mController = new Y_CalendarDbController(mDbAdapter);

		mImageCacheLoader = new ImageCacheLoaderForBitmap();

		mListView.setOnItemClickListener(this);
		findViewById(R.id.btn_left).setOnClickListener(this);
		findViewById(R.id.btn_right_left).setOnClickListener(this);
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
			searchListView.setSelection(searchSelectPosition - 3);
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
			if (mDialog != null && !Y_KnowledgeActivity.this.isFinishing()) {
				mDialog.dismiss();
			}
			mListView.setPinnedHeaderView(LayoutInflater.from(Y_KnowledgeActivity.this).inflate(
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
				Message msg = handler.obtainMessage();
				handler.sendMessage(msg);
			}
		}).start();

	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// mAdapter.mList = mController.getWeekList();
			int position = 0;
			int status = 0;
			int remindId = 0;
			position = intent.getIntExtra("position", 0);
			status = intent.getIntExtra("status", 0);
			remindId = intent.getIntExtra("remind_id", 0);
			List<Y_Knowledge> list = new ArrayList<Y_Knowledge>();
			List<Y_Knowledge> rList = new ArrayList<Y_Knowledge>();
			// list = mAdapter.mList.get(position/7).second;
			list = mAdapter.mList.get(position / 7).second.get(position % 7).list;
			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).type_id == CommConstants.TYPE_REMIND) {
					// list.get(i).status = status;
					rList.add(list.get(i));
				}
			}
			rList.get(remindId).status = status;

			mAdapter.notifyDataSetChanged();
		}
	};

	@Override
	public void onStart() {
		super.onStart();

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
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewCache viewCache;
			if (convertView == null) {
				convertView = View
						.inflate(Y_KnowledgeActivity.this, R.layout.knowledge_y_knowledge_activity_item, null);
				viewCache = new ViewCache(convertView);
				convertView.setTag(viewCache);
			} else {
				viewCache = (ViewCache) convertView.getTag();
			}

			final Y_Knowledge bean = (Y_Knowledge) getItem(position);
			// 头部txt
			// 其他
			LinearLayout otherLL = viewCache.getLayoutOther();
			// 今天
			TextView today = viewCache.getToday();
			TextView week = viewCache.getWeek();
			TextView date = viewCache.getDate();
			// TextView month = viewCache.getMonth();
			today.setVisibility(View.GONE);

			// 第一条知识
			RelativeLayout rl1 = viewCache.getRL1();
			TextView everyDayTitle1 = viewCache.getEveryDayTitle1();
			TextView subTitle1 = viewCache.getSubTitle1();
			TextView content1 = viewCache.getContent1();
			ImageView img1 = viewCache.getContentImage1();
			ImageView knowledgeImg1 = viewCache.getKnowledgeImg1();
			rl1.setVisibility(View.GONE);
			everyDayTitle1.setVisibility(View.GONE);
			subTitle1.setVisibility(View.GONE);
			content1.setVisibility(View.GONE);
			img1.setVisibility(View.GONE);
			knowledgeImg1.setVisibility(View.GONE);

			// 第二条知识
			RelativeLayout rl2 = viewCache.getRL2();
			TextView everyDayTitle2 = viewCache.getEveryDayTitle2();
			TextView knowledgeTxt2 = viewCache.getKnowledgeTxt2();
			ImageView knowledgeImg2 = viewCache.getKnowledgeImg2();
			rl2.setVisibility(View.GONE);
			everyDayTitle2.setVisibility(View.GONE);
			knowledgeTxt2.setVisibility(View.GONE);
			knowledgeImg2.setVisibility(View.GONE);

			// 第三条知识
			RelativeLayout rl3 = viewCache.getRL3();
			TextView everyDayTitle3 = viewCache.getEveryDayTitle3();
			TextView subTitle3 = viewCache.getSubTitle3();
			TextView content3 = viewCache.getContent3();
			ImageView img3 = viewCache.getContentImage3();
			ImageView knowledgeImg3 = viewCache.getKnowledgeImg3();
			rl3.setVisibility(View.GONE);
			everyDayTitle3.setVisibility(View.GONE);
			subTitle3.setVisibility(View.GONE);
			content3.setVisibility(View.GONE);
			img3.setVisibility(View.GONE);
			knowledgeImg3.setVisibility(View.GONE);

			// 第四条知识
			RelativeLayout rl4 = viewCache.getRL4();
			TextView everyDayTitle4 = viewCache.getEveryDayTitle4();
			TextView knowledgeTxt4 = viewCache.getKnowledgeTxt4();
			// ImageView knowledgeImg2 = viewCache.getKnowledgeImg2();
			rl4.setVisibility(View.GONE);
			everyDayTitle4.setVisibility(View.GONE);
			knowledgeTxt4.setVisibility(View.GONE);

			// 提醒
			CheckBox checkBox0 = viewCache.getCheckBox0();
			TextView remind0 = viewCache.getRemind0();
			CheckBox checkBox1 = viewCache.getCheckBox1();
			TextView remind1 = viewCache.getRemind1();
			CheckBox checkBox2 = viewCache.getCheckBox2();
			TextView remind2 = viewCache.getRemind2();
			ImageView line0 = viewCache.getLine0();
			ImageView line1 = viewCache.getLine1();
			ImageView line2 = viewCache.getLine2();
			RelativeLayout ll0 = viewCache.getLl0();
			RelativeLayout ll1 = viewCache.getLl1();
			RelativeLayout ll2 = viewCache.getLl2();
			line0.setVisibility(View.GONE);
			line1.setVisibility(View.GONE);
			line2.setVisibility(View.GONE);
			ll0.setVisibility(View.GONE);
			ll1.setVisibility(View.GONE);
			ll2.setVisibility(View.GONE);
			// week
			final int has = hasDaysNum;

			week.setText(getWeek(getBeforeAfterDate(getPregnancyDay(has).toString(), (bean.days_number - 1))));
			// date
			Calendar cal = new GregorianCalendar();
			cal.setTime(getBeforeAfterDate(getPregnancyDay(has).toString(), (bean.days_number - 1)));
			date.setText((cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日");
			// title
			ArrayList<Y_Knowledge> list = bean.list;
			for (int i = 0; i < list.size(); i++) {
				final Y_Knowledge knowledge = list.get(i);
				if (knowledge.type_id == CommConstants.TYPE_KNOW && knowledge.category_id != 19) {
					if (position < 182) {
						if (knowledge.category_id == 1 || knowledge.category_id == 2) {
							if (knowledge.view_type == 1) {
								rl1.setVisibility(View.VISIBLE);
								rl1.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										// 知识
										// Umeng Evert
										MobclickAgent.onEvent(getBaseContext(), EventContants.know,
												EventContants.know_item_click);
										Intent intent = new Intent(getApplicationContext(),
												InformationDetailActivity.class);
										intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
										intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
										intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
										startActivity(intent);
									}
								});
								content1.setVisibility(View.VISIBLE);

								if (knowledge.title != null && !knowledge.title.equals("")) {
									subTitle1.setVisibility(View.VISIBLE);
									subTitle1.setText(knowledge.title);
									if (metrics.heightPixels >= 1280)
										content1.setPadding(4, 100, 0, 0);
									else
										content1.setPadding(4, 60, 0, 0);

								} else {
									// everyDayTitle.setVisibility(View.GONE);
									subTitle1.setVisibility(View.GONE);
									content1.setPadding(4, 25, 0, 0);
								}
								if (knowledge.category_id != 0) {
									everyDayTitle1.setVisibility(View.VISIBLE);
									everyDayTitle1.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
											BabytreeTitleUtil.Y_TYPE_TITLE));
								} else {
									everyDayTitle1.setVisibility(View.GONE);
								}
								content1.setText(ToDBC(knowledge.summary_content));
								// 图片
								if (knowledge.summary_image != null && !knowledge.summary_image.equals("")) {

									img1.setVisibility(View.VISIBLE);
									img1.setTag(Md5Util.md5(knowledge.summary_image));
									Bitmap cacheDrawable = mImageCacheLoader.loadDrawableForBitmap("y/",
											knowledge.summary_image, Y_KnowledgeActivity.this,
											new ImageCallbackForBitmap() {

												@Override
												public void imageLoadedForBitmap(Bitmap bp, String imageUrl) {
													ImageView tagImage = (ImageView) mListView
															.findViewWithTag(imageUrl);
													if (tagImage != null) {
														if (bp != null) {
															tagImage.setImageBitmap(bp);
														}
													}

												}
											});
									if (cacheDrawable != null) {
										img1.setImageBitmap(cacheDrawable);
									}
								} else {
									img1.setVisibility(View.GONE);
								}
							} else if (knowledge.view_type == 2) {
								rl2.setVisibility(View.VISIBLE);
								rl2.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										MobclickAgent.onEvent(getBaseContext(), EventContants.know,
												EventContants.know_item_click);
										Intent intent = new Intent(getApplicationContext(),
												InformationDetailActivity.class);
										intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
										intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
										intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
										startActivity(intent);
									}

								});
								knowledgeImg1.setVisibility(View.VISIBLE);

								knowledgeTxt2.setVisibility(View.VISIBLE);
								if (position % 7 == 0) {
									everyDayTitle2.setVisibility(View.VISIBLE);
									everyDayTitle2.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
											BabytreeTitleUtil.Y_TYPE_TITLE));
								} else {
									everyDayTitle2.setVisibility(View.GONE);
								}
								if (null != knowledge.title && !"".equals(knowledge.title)) {
									knowledgeTxt2.setText(knowledge.title);
								} else {
									knowledgeTxt2.setText(knowledge.summary_content);
								}
							}
						} else if (knowledge.category_id == 8) {
							if (knowledge.view_type == 4) {
								rl1.setVisibility(View.VISIBLE);
								rl1.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										// 知识
										// Umeng Evert
										MobclickAgent.onEvent(getBaseContext(), EventContants.know,
												EventContants.know_item_click);
										Intent intent = new Intent(getApplicationContext(),
												InformationDetailActivity.class);
										intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
										intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
										intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
										startActivity(intent);
									}
								});
								content1.setVisibility(View.VISIBLE);

								if (knowledge.title != null && !knowledge.title.equals("")) {
									subTitle1.setVisibility(View.VISIBLE);
									subTitle1.setText(knowledge.title);
									if (metrics.heightPixels >= 1280)
										content1.setPadding(4, 100, 0, 0);
									else
										content1.setPadding(4, 60, 0, 0);
								} else {
									// everyDayTitle.setVisibility(View.GONE);
									subTitle1.setVisibility(View.GONE);
									content1.setPadding(4, 25, 0, 0);
								}
								if (knowledge.category_id != 0) {
									everyDayTitle1.setVisibility(View.VISIBLE);
									everyDayTitle1.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
											BabytreeTitleUtil.Y_TYPE_TITLE));
								} else {
									everyDayTitle1.setVisibility(View.GONE);
								}
								content1.setText(knowledge.summary_content);
								// 图片
								if (knowledge.summary_image != null && !knowledge.summary_image.equals("")) {

									img1.setVisibility(View.VISIBLE);
									img1.setTag(Md5Util.md5(knowledge.summary_image));
									Bitmap cacheDrawable = mImageCacheLoader.loadDrawableForBitmap("y/",
											knowledge.summary_image, Y_KnowledgeActivity.this,
											new ImageCallbackForBitmap() {

												@Override
												public void imageLoadedForBitmap(Bitmap bp, String imageUrl) {
													ImageView tagImage = (ImageView) mListView
															.findViewWithTag(imageUrl);
													if (tagImage != null) {
														if (bp != null) {
															tagImage.setImageBitmap(bp);
														}
													}

												}
											});
									if (cacheDrawable != null) {
										img1.setImageBitmap(cacheDrawable);
									}
								} else {
									img1.setVisibility(View.GONE);
								}
							} else if (knowledge.view_type == 3) {
								rl2.setVisibility(View.VISIBLE);
								rl2.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										MobclickAgent.onEvent(getBaseContext(), EventContants.know,
												EventContants.know_item_click);
										Intent intent = new Intent(getApplicationContext(),
												InformationDetailActivity.class);
										intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
										intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
										intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
										startActivity(intent);
									}

								});
								knowledgeImg1.setVisibility(View.VISIBLE);

								knowledgeTxt2.setVisibility(View.VISIBLE);
								if (position % 7 == 0) {
									everyDayTitle2.setVisibility(View.VISIBLE);
									everyDayTitle2.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
											BabytreeTitleUtil.Y_TYPE_TITLE));
								} else {
									everyDayTitle2.setVisibility(View.GONE);
								}
								if (null != knowledge.title && !"".equals(knowledge.title)) {
									knowledgeTxt2.setText(knowledge.title);
								} else {
									knowledgeTxt2.setText(knowledge.summary_content);
								}
							}
						} else if (knowledge.category_id == 9) {
							if (knowledge.view_type == 4) {
								rl3.setVisibility(View.VISIBLE);
								rl3.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										MobclickAgent.onEvent(getBaseContext(), EventContants.know,
												EventContants.know_item_click);
										Intent intent = new Intent(getApplicationContext(),
												InformationDetailActivity.class);
										intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
										intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
										intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
										startActivity(intent);
									}

								});
								content3.setVisibility(View.VISIBLE);
								knowledgeImg2.setVisibility(View.VISIBLE);

								if (knowledge.title != null && !knowledge.title.equals("")) {
									subTitle3.setVisibility(View.VISIBLE);
									subTitle3.setText(knowledge.title);
									if (metrics.heightPixels >= 1280)
										content3.setPadding(4, 80, 0, 0);
									else
										content3.setPadding(4, 60, 0, 0);

								} else {
									// everyDayTitle.setVisibility(View.GONE);
									subTitle3.setVisibility(View.GONE);
									content3.setPadding(4, 25, 0, 0);
								}
								if (knowledge.category_id != 0) {
									everyDayTitle3.setVisibility(View.VISIBLE);
									everyDayTitle3.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
											BabytreeTitleUtil.Y_TYPE_TITLE));
								} else {
									everyDayTitle3.setVisibility(View.GONE);
								}
								content3.setText(knowledge.summary_content);
								// 图片
								if (knowledge.summary_image != null && !knowledge.summary_image.equals("")) {

									img3.setVisibility(View.VISIBLE);
									img3.setTag(Md5Util.md5(knowledge.summary_image));
									Bitmap cacheDrawable = mImageCacheLoader.loadDrawableForBitmap("y/",
											knowledge.summary_image, Y_KnowledgeActivity.this,
											new ImageCallbackForBitmap() {

												@Override
												public void imageLoadedForBitmap(Bitmap bp, String imageUrl) {
													ImageView tagImage = (ImageView) mListView
															.findViewWithTag(imageUrl);
													if (tagImage != null) {
														if (bp != null) {
															tagImage.setImageBitmap(bp);
														}
													}

												}
											});
									if (cacheDrawable != null) {
										img3.setImageBitmap(cacheDrawable);
									}
								} else {
									img3.setVisibility(View.GONE);
								}
							} else if (knowledge.view_type == 3) {
								rl4.setVisibility(View.VISIBLE);
								rl4.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										MobclickAgent.onEvent(getBaseContext(), EventContants.know,
												EventContants.know_item_click);
										Intent intent = new Intent(getApplicationContext(),
												InformationDetailActivity.class);
										intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
										intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
										intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
										startActivity(intent);
									}

								});
								// knowledgeImg4.setVisibility(View.VISIBLE);
								knowledgeImg3.setVisibility(View.VISIBLE);
								knowledgeTxt4.setVisibility(View.VISIBLE);
								if (position % 7 == 0) {
									everyDayTitle4.setVisibility(View.VISIBLE);
									everyDayTitle4.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
											BabytreeTitleUtil.Y_TYPE_TITLE));
								} else {
									everyDayTitle4.setVisibility(View.GONE);
								}
								if (null != knowledge.title && !"".equals(knowledge.title)) {
									knowledgeTxt4.setText(knowledge.title);
								} else {
									knowledgeTxt4.setText(knowledge.summary_content);
								}
							}

						} else {
							if (knowledge.view_type == 4) {
								rl1.setVisibility(View.VISIBLE);
								rl1.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										// 知识
										// Umeng Evert
										MobclickAgent.onEvent(getBaseContext(), EventContants.know,
												EventContants.know_item_click);
										Intent intent = new Intent(getApplicationContext(),
												InformationDetailActivity.class);
										intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
										intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
										intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
										startActivity(intent);
									}
								});
								content1.setVisibility(View.VISIBLE);

								if (knowledge.title != null && !knowledge.title.equals("")) {
									subTitle1.setVisibility(View.VISIBLE);
									subTitle1.setText(knowledge.title);
									if (metrics.heightPixels >= 1280)
										content1.setPadding(4, 100, 0, 0);
									else
										content1.setPadding(4, 60, 0, 0);
								} else {
									// everyDayTitle.setVisibility(View.GONE);
									subTitle1.setVisibility(View.GONE);
									content1.setPadding(4, 25, 0, 0);
								}
								if (knowledge.category_id != 0) {
									everyDayTitle1.setVisibility(View.VISIBLE);
									everyDayTitle1.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
											BabytreeTitleUtil.Y_TYPE_TITLE));
								} else {
									everyDayTitle1.setVisibility(View.GONE);
								}
								content1.setText(knowledge.summary_content);
								// 图片
								if (knowledge.summary_image != null && !knowledge.summary_image.equals("")) {

									img1.setVisibility(View.VISIBLE);
									img1.setTag(Md5Util.md5(knowledge.summary_image));
									Bitmap cacheDrawable = mImageCacheLoader.loadDrawableForBitmap("y/",
											knowledge.summary_image, Y_KnowledgeActivity.this,
											new ImageCallbackForBitmap() {

												@Override
												public void imageLoadedForBitmap(Bitmap bp, String imageUrl) {
													ImageView tagImage = (ImageView) mListView
															.findViewWithTag(imageUrl);
													if (tagImage != null) {
														if (bp != null) {
															tagImage.setImageBitmap(bp);
														}
													}

												}
											});
									if (cacheDrawable != null) {
										img1.setImageBitmap(cacheDrawable);
									}
								} else {
									img1.setVisibility(View.GONE);
								}
							} else if (knowledge.view_type == 3) {
								rl2.setVisibility(View.VISIBLE);
								rl2.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										MobclickAgent.onEvent(getBaseContext(), EventContants.know,
												EventContants.know_item_click);
										Intent intent = new Intent(getApplicationContext(),
												InformationDetailActivity.class);
										intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
										intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
										intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
										startActivity(intent);
									}

								});
								knowledgeImg1.setVisibility(View.VISIBLE);

								knowledgeTxt2.setVisibility(View.VISIBLE);
								if (position % 7 == 0) {
									everyDayTitle2.setVisibility(View.VISIBLE);
									everyDayTitle2.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
											BabytreeTitleUtil.Y_TYPE_TITLE));
								} else {
									everyDayTitle2.setVisibility(View.GONE);
								}
								if (null != knowledge.title && !"".equals(knowledge.title)) {
									knowledgeTxt2.setText(knowledge.title);
								} else {
									knowledgeTxt2.setText(knowledge.summary_content);
								}
							}
						}
					} else if (position >= 182) {
						if (knowledge.category_id == 1 || knowledge.category_id == 2) {
							if (knowledge.view_type == 1) {
								rl1.setVisibility(View.VISIBLE);
								rl1.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										// 知识
										// Umeng Evert
										MobclickAgent.onEvent(getBaseContext(), EventContants.know,
												EventContants.know_item_click);
										Intent intent = new Intent(getApplicationContext(),
												InformationDetailActivity.class);
										intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
										intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
										intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
										startActivity(intent);
									}
								});
								content1.setVisibility(View.VISIBLE);

								if (knowledge.title != null && !knowledge.title.equals("")) {
									subTitle1.setVisibility(View.VISIBLE);
									subTitle1.setText(knowledge.title);
									if (metrics.heightPixels >= 1280)
										content1.setPadding(4, 100, 0, 0);
									else
										content1.setPadding(4, 60, 0, 0);

								} else {
									// everyDayTitle.setVisibility(View.GONE);
									subTitle1.setVisibility(View.GONE);
									content1.setPadding(4, 25, 0, 0);
								}
								if (knowledge.category_id != 0) {
									everyDayTitle1.setVisibility(View.VISIBLE);
									everyDayTitle1.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
											BabytreeTitleUtil.Y_TYPE_TITLE));
								} else {
									everyDayTitle1.setVisibility(View.GONE);
								}
								content1.setText(knowledge.summary_content);
								// 图片
								if (knowledge.summary_image != null && !knowledge.summary_image.equals("")) {

									img1.setVisibility(View.VISIBLE);
									img1.setTag(Md5Util.md5(knowledge.summary_image));
									Bitmap cacheDrawable = mImageCacheLoader.loadDrawableForBitmap("y/",
											knowledge.summary_image, Y_KnowledgeActivity.this,
											new ImageCallbackForBitmap() {

												@Override
												public void imageLoadedForBitmap(Bitmap bp, String imageUrl) {
													ImageView tagImage = (ImageView) mListView
															.findViewWithTag(imageUrl);
													if (tagImage != null) {
														if (bp != null) {
															tagImage.setImageBitmap(bp);
														}
													}

												}
											});
									if (cacheDrawable != null) {
										img1.setImageBitmap(cacheDrawable);
									}
								} else {
									img1.setVisibility(View.GONE);
								}
							} else if (knowledge.view_type == 2) {
								rl2.setVisibility(View.VISIBLE);
								rl2.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										MobclickAgent.onEvent(getBaseContext(), EventContants.know,
												EventContants.know_item_click);
										Intent intent = new Intent(getApplicationContext(),
												InformationDetailActivity.class);
										intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
										intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
										intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
										startActivity(intent);
									}

								});
								knowledgeImg1.setVisibility(View.VISIBLE);

								knowledgeTxt2.setVisibility(View.VISIBLE);
								if (position % 7 == 0) {
									everyDayTitle2.setVisibility(View.VISIBLE);
									everyDayTitle2.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
											BabytreeTitleUtil.Y_TYPE_TITLE));
								} else {
									everyDayTitle2.setVisibility(View.GONE);
								}
								if (null != knowledge.title && !"".equals(knowledge.title)) {
									knowledgeTxt2.setText(knowledge.title);
								} else {
									knowledgeTxt2.setText(knowledge.summary_content);
								}
							}
						} else if (knowledge.category_id == 9) {
							if (knowledge.view_type == 4) {
								rl1.setVisibility(View.VISIBLE);
								rl1.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										// 知识
										// Umeng Evert
										MobclickAgent.onEvent(getBaseContext(), EventContants.know,
												EventContants.know_item_click);
										Intent intent = new Intent(getApplicationContext(),
												InformationDetailActivity.class);
										intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
										intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
										intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
										startActivity(intent);
									}
								});
								content1.setVisibility(View.VISIBLE);

								if (knowledge.title != null && !knowledge.title.equals("")) {
									subTitle1.setVisibility(View.VISIBLE);
									subTitle1.setText(knowledge.title);
									if (metrics.heightPixels >= 1280)
										content1.setPadding(4, 100, 0, 0);
									else
										content1.setPadding(4, 60, 0, 0);

								} else {
									// everyDayTitle.setVisibility(View.GONE);
									subTitle1.setVisibility(View.GONE);
									content1.setPadding(4, 25, 0, 0);
								}
								if (knowledge.category_id != 0) {
									everyDayTitle1.setVisibility(View.VISIBLE);
									everyDayTitle1.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
											BabytreeTitleUtil.Y_TYPE_TITLE));
								} else {
									everyDayTitle1.setVisibility(View.GONE);
								}
								content1.setText(knowledge.summary_content);
								// 图片
								if (knowledge.summary_image != null && !knowledge.summary_image.equals("")) {

									img1.setVisibility(View.VISIBLE);
									img1.setTag(Md5Util.md5(knowledge.summary_image));
									Bitmap cacheDrawable = mImageCacheLoader.loadDrawableForBitmap("y/",
											knowledge.summary_image, Y_KnowledgeActivity.this,
											new ImageCallbackForBitmap() {

												@Override
												public void imageLoadedForBitmap(Bitmap bp, String imageUrl) {
													ImageView tagImage = (ImageView) mListView
															.findViewWithTag(imageUrl);
													if (tagImage != null) {
														if (bp != null) {
															tagImage.setImageBitmap(bp);
														}
													}

												}
											});
									if (cacheDrawable != null) {
										img1.setImageBitmap(cacheDrawable);
									}
								} else {
									img1.setVisibility(View.GONE);
								}
							} else if (knowledge.view_type == 3) {
								rl2.setVisibility(View.VISIBLE);
								rl2.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										MobclickAgent.onEvent(getBaseContext(), EventContants.know,
												EventContants.know_item_click);
										Intent intent = new Intent(getApplicationContext(),
												InformationDetailActivity.class);
										intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
										intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
										intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
										startActivity(intent);
									}

								});
								knowledgeImg1.setVisibility(View.VISIBLE);

								knowledgeTxt2.setVisibility(View.VISIBLE);
								if (position % 7 == 0) {
									everyDayTitle2.setVisibility(View.VISIBLE);
									everyDayTitle2.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
											BabytreeTitleUtil.Y_TYPE_TITLE));
								} else {
									everyDayTitle2.setVisibility(View.GONE);
								}
								if (null != knowledge.title && !"".equals(knowledge.title)) {
									knowledgeTxt2.setText(knowledge.title);
								} else {
									knowledgeTxt2.setText(knowledge.summary_content);
								}
							}
						} else if (knowledge.category_id == 18) {
							if (knowledge.view_type == 4) {
								rl3.setVisibility(View.VISIBLE);
								rl3.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										MobclickAgent.onEvent(getBaseContext(), EventContants.know,
												EventContants.know_item_click);
										Intent intent = new Intent(getApplicationContext(),
												InformationDetailActivity.class);
										intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
										intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
										intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
										startActivity(intent);
									}

								});
								content3.setVisibility(View.VISIBLE);
								knowledgeImg2.setVisibility(View.VISIBLE);

								if (knowledge.title != null && !knowledge.title.equals("")) {
									subTitle3.setVisibility(View.VISIBLE);
									subTitle3.setText(knowledge.title);
									if (metrics.heightPixels >= 1280)
										content3.setPadding(4, 80, 0, 0);
									else
										content3.setPadding(4, 60, 0, 0);
								} else {
									// everyDayTitle.setVisibility(View.GONE);
									subTitle3.setVisibility(View.GONE);
									content3.setPadding(4, 25, 0, 0);
								}
								if (knowledge.category_id != 0) {
									everyDayTitle3.setVisibility(View.VISIBLE);
									everyDayTitle3.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
											BabytreeTitleUtil.Y_TYPE_TITLE));
								} else {
									everyDayTitle3.setVisibility(View.GONE);
								}
								content3.setText(knowledge.summary_content);
								// 图片
								if (knowledge.summary_image != null && !knowledge.summary_image.equals("")) {

									img3.setVisibility(View.VISIBLE);
									img3.setTag(Md5Util.md5(knowledge.summary_image));
									Bitmap cacheDrawable = mImageCacheLoader.loadDrawableForBitmap("y/",
											knowledge.summary_image, Y_KnowledgeActivity.this,
											new ImageCallbackForBitmap() {

												@Override
												public void imageLoadedForBitmap(Bitmap bp, String imageUrl) {
													ImageView tagImage = (ImageView) mListView
															.findViewWithTag(imageUrl);
													if (tagImage != null) {
														if (bp != null) {
															tagImage.setImageBitmap(bp);
														}
													}

												}
											});
									if (cacheDrawable != null) {
										img3.setImageBitmap(cacheDrawable);
									}
								} else {
									img3.setVisibility(View.GONE);
								}
							} else if (knowledge.view_type == 3) {
								rl4.setVisibility(View.VISIBLE);
								rl4.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										MobclickAgent.onEvent(getBaseContext(), EventContants.know,
												EventContants.know_item_click);
										Intent intent = new Intent(getApplicationContext(),
												InformationDetailActivity.class);
										intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
										intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
										intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
										startActivity(intent);
									}

								});
								// knowledgeImg4.setVisibility(View.VISIBLE);
								knowledgeImg3.setVisibility(View.VISIBLE);
								knowledgeTxt4.setVisibility(View.VISIBLE);
								if (position % 7 == 0) {
									everyDayTitle4.setVisibility(View.VISIBLE);
									everyDayTitle4.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
											BabytreeTitleUtil.Y_TYPE_TITLE));
								} else {
									everyDayTitle4.setVisibility(View.GONE);
								}
								if (null != knowledge.title && !"".equals(knowledge.title)) {
									knowledgeTxt4.setText(knowledge.title);
								} else {
									knowledgeTxt4.setText(knowledge.summary_content);
								}
							}

						} else {
							if (knowledge.view_type == 4) {
								rl1.setVisibility(View.VISIBLE);
								rl1.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										// 知识
										// Umeng Evert
										MobclickAgent.onEvent(getBaseContext(), EventContants.know,
												EventContants.know_item_click);
										Intent intent = new Intent(getApplicationContext(),
												InformationDetailActivity.class);
										intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
										intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
										intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
										startActivity(intent);
									}
								});
								content1.setVisibility(View.VISIBLE);

								if (knowledge.title != null && !knowledge.title.equals("")) {
									subTitle1.setVisibility(View.VISIBLE);
									subTitle1.setText(knowledge.title);
									if (metrics.heightPixels >= 1280)
										content1.setPadding(4, 100, 0, 0);
									else
										content1.setPadding(4, 60, 0, 0);

								} else {
									// everyDayTitle.setVisibility(View.GONE);
									subTitle1.setVisibility(View.GONE);
									content1.setPadding(4, 25, 0, 0);
								}
								if (knowledge.category_id != 0) {
									everyDayTitle1.setVisibility(View.VISIBLE);
									everyDayTitle1.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
											BabytreeTitleUtil.Y_TYPE_TITLE));
								} else {
									everyDayTitle1.setVisibility(View.GONE);
								}
								content1.setText(knowledge.summary_content);
								// 图片
								if (knowledge.summary_image != null && !knowledge.summary_image.equals("")) {

									img1.setVisibility(View.VISIBLE);
									img1.setTag(Md5Util.md5(knowledge.summary_image));
									Bitmap cacheDrawable = mImageCacheLoader.loadDrawableForBitmap("y/",
											knowledge.summary_image, Y_KnowledgeActivity.this,
											new ImageCallbackForBitmap() {

												@Override
												public void imageLoadedForBitmap(Bitmap bp, String imageUrl) {
													ImageView tagImage = (ImageView) mListView
															.findViewWithTag(imageUrl);
													if (tagImage != null) {
														if (bp != null) {
															tagImage.setImageBitmap(bp);
														}
													}

												}
											});
									if (cacheDrawable != null) {
										img1.setImageBitmap(cacheDrawable);
									}
								} else {
									img1.setVisibility(View.GONE);
								}
							} else if (knowledge.view_type == 3) {
								rl2.setVisibility(View.VISIBLE);
								rl2.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										MobclickAgent.onEvent(getBaseContext(), EventContants.know,
												EventContants.know_item_click);
										Intent intent = new Intent(getApplicationContext(),
												InformationDetailActivity.class);
										intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
										intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
										intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
										startActivity(intent);
									}

								});
								knowledgeImg1.setVisibility(View.VISIBLE);

								knowledgeTxt2.setVisibility(View.VISIBLE);
								if (position % 7 == 0) {
									everyDayTitle2.setVisibility(View.VISIBLE);
									everyDayTitle2.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
											BabytreeTitleUtil.Y_TYPE_TITLE));
								} else {
									everyDayTitle2.setVisibility(View.GONE);
								}
								if (null != knowledge.title && !"".equals(knowledge.title)) {
									knowledgeTxt2.setText(knowledge.title);
								} else {
									knowledgeTxt2.setText(knowledge.summary_content);
								}
							}
						}
					}

					// arrowImg0.setPadding(350, rl0.getLayoutParams().height,
					// 0, 0);
				} else if (knowledge.type_id == CommConstants.TYPE_REMIND && knowledge.category_id != 19) {// remind
					if (ll0.getVisibility() == View.GONE && knowledge.is_important == 1) {
						ll0.setVisibility(View.VISIBLE);
						line0.setVisibility(View.VISIBLE);
						remind0.setText(knowledge.title);
						ll0.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// Umeng Evert
								MobclickAgent.onEvent(getBaseContext(), EventContants.know, EventContants.know_remind);
								Intent intent = new Intent(getApplicationContext(), Y_RemindDetailActivity.class);
								intent.putExtra("_id", knowledge._id);
								intent.putExtra("title", knowledge.title);
								intent.putExtra("status", knowledge.status);
								intent.putExtra("days_number", knowledge.days_number);
								intent.putExtra("position", position);
								intent.putExtra("remind_id", 0);
								startActivity(intent);
							}
						});
						if (knowledge.status == 1) {
							checkBox0.setChecked(true);
						} else {
							checkBox0.setChecked(false);
						}
						checkBox0.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								CheckBox cb = (CheckBox) v;
								if (cb.isChecked()) {
									mController.updateKnowledge(knowledge._id, 1);
									knowledge.status = 1;
								} else {
									mController.updateKnowledge(knowledge._id, 0);
									knowledge.status = 0;
								}
							}
						});
					} else if (ll1.getVisibility() == View.GONE && knowledge.is_important == 1) {
						ll1.setVisibility(View.VISIBLE);
						line1.setVisibility(View.VISIBLE);
						remind1.setText(knowledge.title);

						ll1.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// Umeng Evert
								MobclickAgent.onEvent(getBaseContext(), EventContants.know, EventContants.know_remind);
								Intent intent = new Intent(getApplicationContext(), Y_RemindDetailActivity.class);
								intent.putExtra("_id", knowledge._id);
								intent.putExtra("title", knowledge.title);
								intent.putExtra("status", knowledge.status);
								intent.putExtra("identify", 0);
								intent.putExtra("days_number", knowledge.days_number);
								intent.putExtra("position", position);
								intent.putExtra("remind_id", 1);
								startActivity(intent);
							}
						});

						if (knowledge.status == 1) {
							checkBox1.setChecked(true);
						} else {
							checkBox1.setChecked(false);
						}
						checkBox1.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {

								CheckBox cb = (CheckBox) v;
								if (cb.isChecked()) {
									mController.updateKnowledge(knowledge._id, 1);
									knowledge.status = 1;
								} else {
									mController.updateKnowledge(knowledge._id, 0);
									knowledge.status = 0;
								}
							}
						});
					} else if (ll2.getVisibility() == View.GONE && knowledge.is_important == 1) {
						ll2.setVisibility(View.VISIBLE);
						line2.setVisibility(View.VISIBLE);
						remind2.setText(knowledge.title);

						ll2.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// Umeng Evert
								MobclickAgent.onEvent(getBaseContext(), EventContants.know, EventContants.know_remind);
								Intent intent = new Intent(getApplicationContext(), Y_RemindDetailActivity.class);
								intent.putExtra("_id", knowledge._id);
								intent.putExtra("title", knowledge.title);
								intent.putExtra("status", knowledge.status);
								intent.putExtra("identify", 0);
								intent.putExtra("days_number", knowledge.days_number);
								intent.putExtra("position", position);
								intent.putExtra("remind_id", 2);
								startActivity(intent);
							}
						});

						if (knowledge.status == 1) {
							checkBox2.setChecked(true);
						} else {
							checkBox2.setChecked(false);
						}
						checkBox2.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								CheckBox cb = (CheckBox) v;
								if (cb.isChecked()) {
									mController.updateKnowledge(knowledge._id, 1);
									knowledge.status = 1;
								} else {
									mController.updateKnowledge(knowledge._id, 0);
									knowledge.status = 0;
								}
							}
						});
					}
				}
				// yuchanqi.setText("离预产期还有" + (280 - knowledge.days_number) +
				// "天");
			}

			int section = getSectionForPosition(position);
			boolean displaySectionHeaders = (getPositionForSection(section) == position);
			bindSectionHeader(convertView, position, displaySectionHeaders);
			if (position == has) {
				otherLL.setBackgroundResource(R.color.light_green);
				today.setVisibility(View.VISIBLE);
				if (!displaySectionHeaders) {
					LinearLayout layoutOther = viewCache.getLayoutOther();
					layoutOther.setPadding(0, 30, 0, 0);
				}
			} else {
				otherLL.setBackgroundResource(R.color.background);
				today.setVisibility(View.GONE);
			}
			// if (selectedPosition == position && hasDaysNum >= 0) {
			// otherLL.setBackgroundResource(R.color.light_green);
			// today.setVisibility(View.VISIBLE);
			// // yuchanqi.setVisibility(View.VISIBLE);
			// selectedPosition = -1;
			// if (!displaySectionHeaders) {
			// LinearLayout layoutOther = viewCache.getLayoutOther();
			// layoutOther.setPadding(0, 30, 0, 0);
			// }
			// } else {
			// otherLL.setBackgroundResource(R.color.background);
			// today.setVisibility(View.GONE);
			// // yuchanqi.setVisibility(View.GONE);
			//
			// }
			return convertView;
		}

		public void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
			TextView tv = (TextView) view.findViewById(R.id.information_header);
			LinearLayout layoutOther = (LinearLayout) view.findViewById(R.id.information_layout_other);
			if (topPadding == 0) {
				topPadding = layoutOther.getPaddingTop();
			}
			// LinearLayout layoutTitle = (LinearLayout) view
			// .findViewById(R.id.information_layout_title);
			if (displaySectionHeader) {
				tv.setVisibility(View.VISIBLE);
				tv.setText(getSections()[getSectionForPosition(position)]);
				layoutOther.setPadding(0, topPadding, 0, 0);
			} else {
				tv.setVisibility(View.GONE);
				layoutOther.setPadding(0, 0, 0, 0);
			}
		}

		public void configurePinnedHeader(View header, int position, int alpha) {
			((TextView) header.findViewById(R.id.information_header))
					.setText(getSections()[getSectionForPosition(position)]);
		}

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

		public String[] getSections() {
			String[] res = new String[mList.size()];
			for (int i = 0; i < mList.size(); i++) {
				res[i] = mList.get(i).first;
			}
			return res;
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
		} else if (v.getId() == R.id.btn_right_left) {
			// Umeng Evert
			MobclickAgent.onEvent(getBaseContext(), EventContants.setup, EventContants.setup_share);
			startActivity(new Intent(this, SearchActivity.class));

		} else if (v.getId() == R.id.txt_center || v.getId() == R.id.img_icon) {
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
		MobclickAgent.onEvent(getBaseContext(), EventContants.carer, EventContants.knowledage_today);
		if (parent == searchListView) {
			mListView.setSelection(position * 7);
			mAdapter.notifyDataSetInvalidated();
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
		private TextView today, week, date, remind0, remind1, remind2;
		private TextView everyDayTitle1, subTitle1, content1;
		private TextView everyDayTitle2;
		private TextView everyDayTitle3, subTitle3, content3;
		private TextView everyDayTitle4;
		private ImageView contentImg1, contentImg3;
		private View baseView;

		private ImageView line0, line1, line2;

		private CheckBox checkBox0, checkBox1, checkBox2;

		private LinearLayout layoutOther;
		private RelativeLayout ll0, ll1, ll2;
		private RelativeLayout rl4, rl1, rl2, rl3;

		private ImageView knowledgeImg1, knowledgeImg2, knowledgeImg3;
		private TextView knowledgeTxt2, knowledgeTxt4;

		public ViewCache(View view) {
			baseView = view;
		}

		public LinearLayout getLayoutOther() {
			if (layoutOther == null) {
				layoutOther = (LinearLayout) baseView.findViewById(R.id.information_layout_other);
			}
			return layoutOther;
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

		// public TextView getMonth() {
		// if (month == null) {
		// month = (TextView) baseView
		// .findViewById(R.id.text_pregnancy_month);
		// }
		// return month;
		// }
		public RelativeLayout getRL1() {
			if (rl1 == null) {
				rl1 = (RelativeLayout) baseView.findViewById(R.id.rl_1);
			}
			return rl1;
		}

		public RelativeLayout getRL2() {
			if (rl2 == null) {
				rl2 = (RelativeLayout) baseView.findViewById(R.id.rl_2);
			}
			return rl2;
		}

		public RelativeLayout getRL3() {
			if (rl3 == null) {
				rl3 = (RelativeLayout) baseView.findViewById(R.id.rl_3);
			}
			return rl3;
		}

		public RelativeLayout getRL4() {
			if (rl4 == null) {
				rl4 = (RelativeLayout) baseView.findViewById(R.id.rl_4);
			}
			return rl4;
		}

		public TextView getEveryDayTitle1() {
			if (everyDayTitle1 == null) {
				everyDayTitle1 = (TextView) baseView.findViewById(R.id.txt_title_1);
			}
			return everyDayTitle1;
		}

		public TextView getEveryDayTitle2() {
			if (everyDayTitle2 == null) {
				everyDayTitle2 = (TextView) baseView.findViewById(R.id.txt_title_2);
			}
			return everyDayTitle2;
		}

		public TextView getEveryDayTitle3() {
			if (everyDayTitle3 == null) {
				everyDayTitle3 = (TextView) baseView.findViewById(R.id.txt_title_3);
			}
			return everyDayTitle3;
		}

		public TextView getEveryDayTitle4() {
			if (everyDayTitle4 == null) {
				everyDayTitle4 = (TextView) baseView.findViewById(R.id.txt_title_4);
			}
			return everyDayTitle4;
		}

		public TextView getSubTitle1() {
			if (subTitle1 == null) {
				subTitle1 = (TextView) baseView.findViewById(R.id.txt_subtitle_1);
			}
			return subTitle1;
		}

		// public TextView getSubTitle2() {
		// if (subTitle2 == null) {
		// subTitle2 = (TextView) baseView.findViewById(R.id.txt_subtitle_2);
		// }
		// return subTitle2;
		// }
		public TextView getSubTitle3() {
			if (subTitle3 == null) {
				subTitle3 = (TextView) baseView.findViewById(R.id.txt_subtitle_3);
			}
			return subTitle3;
		}

		// public TextView getSubTitle4() {
		// if (subTitle4 == null) {
		// subTitle4 = (TextView) baseView.findViewById(R.id.txt_subtitle_4);
		// }
		// return subTitle4;
		// }

		public TextView getContent1() {
			if (content1 == null) {
				content1 = (TextView) baseView.findViewById(R.id.txt_content_1);
			}
			return content1;
		}

		public TextView getContent3() {
			if (content3 == null) {
				content3 = (TextView) baseView.findViewById(R.id.txt_content_3);
			}
			return content3;
		}

		public ImageView getContentImage1() {
			if (contentImg1 == null) {
				contentImg1 = (ImageView) baseView.findViewById(R.id.content_image_1);
			}
			return contentImg1;
		}

		public ImageView getContentImage3() {
			if (contentImg3 == null) {
				contentImg3 = (ImageView) baseView.findViewById(R.id.content_image_3);
			}
			return contentImg3;
		}

		public CheckBox getCheckBox0() {
			if (checkBox0 == null) {
				checkBox0 = (CheckBox) baseView.findViewById(R.id.layout_remind_checkbox0);
			}
			return checkBox0;
		}

		public TextView getRemind0() {
			if (remind0 == null) {
				remind0 = (TextView) baseView.findViewById(R.id.layout_remind_textview0);
			}
			return remind0;
		}

		public CheckBox getCheckBox1() {
			if (checkBox1 == null) {
				checkBox1 = (CheckBox) baseView.findViewById(R.id.layout_remind_checkbox1);
			}
			return checkBox1;
		}

		public TextView getRemind1() {
			if (remind1 == null) {
				remind1 = (TextView) baseView.findViewById(R.id.layout_remind_textview1);
			}
			return remind1;
		}

		public CheckBox getCheckBox2() {
			if (checkBox2 == null) {
				checkBox2 = (CheckBox) baseView.findViewById(R.id.layout_remind_checkbox2);
			}
			return checkBox2;
		}

		public TextView getRemind2() {
			if (remind2 == null) {
				remind2 = (TextView) baseView.findViewById(R.id.layout_remind_textview2);
			}
			return remind2;
		}

		public ImageView getLine0() {
			if (line0 == null) {
				line0 = (ImageView) baseView.findViewById(R.id.line0);
			}
			return line0;
		}

		public ImageView getLine1() {
			if (line1 == null) {
				line1 = (ImageView) baseView.findViewById(R.id.line1);
			}
			return line1;
		}

		public ImageView getLine2() {
			if (line2 == null) {
				line2 = (ImageView) baseView.findViewById(R.id.line2);
			}
			return line2;
		}

		public RelativeLayout getLl0() {
			if (ll0 == null) {
				ll0 = (RelativeLayout) baseView.findViewById(R.id.ll0);
			}
			return ll0;
		}

		public RelativeLayout getLl1() {
			if (ll1 == null) {
				ll1 = (RelativeLayout) baseView.findViewById(R.id.ll1);
			}
			return ll1;
		}

		public RelativeLayout getLl2() {
			if (ll2 == null) {
				ll2 = (RelativeLayout) baseView.findViewById(R.id.ll2);
			}
			return ll2;
		}

		public ImageView getKnowledgeImg1() {
			if (knowledgeImg1 == null) {
				knowledgeImg1 = (ImageView) baseView.findViewById(R.id.img_knowledge_line1);
			}
			return knowledgeImg1;
		}

		public ImageView getKnowledgeImg2() {
			if (knowledgeImg2 == null) {
				knowledgeImg2 = (ImageView) baseView.findViewById(R.id.img_knowledge_line2);
			}
			return knowledgeImg2;
		}

		public ImageView getKnowledgeImg3() {
			if (knowledgeImg3 == null) {
				knowledgeImg3 = (ImageView) baseView.findViewById(R.id.img_knowledge_line3);
			}
			return knowledgeImg3;
		}

		public TextView getKnowledgeTxt2() {
			if (knowledgeTxt2 == null) {
				knowledgeTxt2 = (TextView) baseView.findViewById(R.id.tv_knowledge_2);
			}
			return knowledgeTxt2;
		}

		public TextView getKnowledgeTxt4() {
			if (knowledgeTxt4 == null) {
				knowledgeTxt4 = (TextView) baseView.findViewById(R.id.tv_knowledge_4);
			}
			return knowledgeTxt4;
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
				convertView = View
						.inflate(Y_KnowledgeActivity.this, R.layout.knowledge_y_information_search_item, null);
				view = (TextView) convertView.findViewById(R.id.menu_text);
				convertView.setTag(view);
			} else {
				view = (TextView) convertView.getTag();
			}
			view.setText(mString[position].toString());
			return convertView;
		}

	}

	// 半角转成全角
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i < c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i] > 65280 && c[i] < 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
}
