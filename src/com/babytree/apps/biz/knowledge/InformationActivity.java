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
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
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
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.ui.SearchActivity;
import com.babytree.apps.comm.ui.TagTopicListActivity;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.ui.adapter.PinnedHeaderListViewAdapter;
import com.babytree.apps.comm.ui.widget.PinnedHeaderListView;
import com.babytree.apps.comm.util.BabytreeTitleUtil;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.ImageCacheLoaderForBitmap;
import com.babytree.apps.comm.util.ImageCacheLoaderForBitmap.ImageCallbackForBitmap;
import com.babytree.apps.comm.util.Md5Util;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * 孕期--每日知识列表页
 */
public class InformationActivity extends BabytreeActivity implements OnItemClickListener, OnClickListener,
		OnTouchListener {

	private PinnedHeaderListView mListView;

	private PregnancyApplication mApplication;

	private CalendarDbAdapter mDbAdapter;

	private CalendarDbController mController;

	private MAdapter mAdapter;

	private int topPadding = 0;

	private ImageCacheLoaderForBitmap mImageCacheLoaderForBitmap;

	boolean autoNextPageLoading = false;

	private List<Pair<String, ArrayList<Knowledge>>> weekList;

	private int hasDaysNum, todaySelected;

	private PopupWindow mMenu;

	private MyAdatper myAdapter;

	private ListView searchListView;
	// "今天",
	/*
	 * private final String[] mString = { "备孕周", "孕1周", "孕2周", "孕3周", "孕4周",
	 * "孕5周", "孕6周", "孕7周", "孕8周", "孕9周", "孕10周", "孕11周", "孕12周", "孕13周",
	 * "孕14周", "孕15周", "孕16周", "孕17周", "孕18周", "孕19周", "孕20周", "孕21周", "孕22周",
	 * "孕23周", "孕24周", "孕25周", "孕26周", "孕27周", "孕28周", "孕29周", "孕30周", "孕31周",
	 * "孕32周", "孕33周", "孕34周", "孕35周", "孕36周", "孕37周", "孕38周", "孕39周", "孕40周" };
	 */

	private String[] mString;
	private AlertDialog mDialog;
	private ImageView mImgIcon;

	private ImageView mImageOut;

	private ImageView mImageIn;

	private ArrayList<String> popList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.knowledge_information_activity);

		// 注册广播监听
		IntentFilter filter = new IntentFilter();
		filter.addAction("DataUpdate");
		LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);

		mImgIcon = (ImageView) findViewById(R.id.img_icon);
		mImgIcon.setOnClickListener(this);
		mImageOut = (ImageView) findViewById(R.id.iv_item_bg_out);
		mImageIn = (ImageView) findViewById(R.id.iv_item_bg_in);

		mListView = (PinnedHeaderListView) findViewById(R.id.information_activity_list);

		mApplication = (PregnancyApplication) getApplication();
		mDbAdapter = mApplication.getCalendarDbAdapter();
		mController = new CalendarDbController(mDbAdapter);

		mImageCacheLoaderForBitmap = new ImageCacheLoaderForBitmap();

		mListView.setOnItemClickListener(this);
		findViewById(R.id.btn_left).setOnClickListener(this);
		findViewById(R.id.btn_right_left).setOnClickListener(this);
		findViewById(R.id.txt_center).setOnClickListener(this);
		// 妈妈怀孕天数
		hasDaysNum = BabytreeUtil.getBetweenDays(Calendar.getInstance(Locale.CHINA).getTimeInMillis(),
				SharedPreferencesUtil.getLongValue(this, ShareKeys.BIRTHDAY_TIMESTAMP));
		// 首次进去计算今天位置
		todaySelected = 280 - hasDaysNum - 3 * 7 + 1;

		View view = View.inflate(this, R.layout.knowledge_information_search_menu, null);
		searchListView = (ListView) view.findViewById(R.id.layout_list_view);

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

			if (msg.what == 0) {// dismiss
				if (mDialog != null && !InformationActivity.this.isFinishing()) {
					mDialog.dismiss();
				}
				mString = new String[popList.size()];
				popList.toArray(mString);
				myAdapter = new MyAdatper();
				searchListView.setAdapter(myAdapter);
				searchListView.setOnItemClickListener(InformationActivity.this);

				mListView.setPinnedHeaderView(LayoutInflater.from(InformationActivity.this).inflate(
						R.layout.knowledge_information_header, mListView, false));
				mAdapter = new MAdapter();
				mListView.setAdapter(mAdapter);
				mAdapter.setSelectedPosition(todaySelected);
				mAdapter.notifyDataSetInvalidated();
				mListView.setSelection(todaySelected);
			}

		};
	};

	private void initData() {
		if (mDialog == null)
			mDialog = new ProgressDialog(InformationActivity.this);
		mDialog.setCancelable(true);
		mDialog.setMessage("加载中···");
		mDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				weekList = mController.getWeekListForKnowledge();
				popList = mController.getType();
				Message msg = handler.obtainMessage();
				handler.sendMessage(msg);
			}
		}).start();

	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// mAdapter.mList = mController.getWeekList();
			int id = 0;
			int position = 0;
			int status = 0;
			try {
				position = intent.getIntExtra("position", 0);
				if (position != 0) {
					position--;
				}
				status = intent.getIntExtra("status", 0);
				id = intent.getIntExtra("_id", 0);

				List<Knowledge> list = new ArrayList<Knowledge>();
				list = mAdapter.mList.get(position / 7).second.get(position % 7).list;
				for (int i = 0; i < list.size(); i++) {
					if (list.get(i).type_id == CommConstants.TYPE_REMIND && list.get(i).is_important == 1) {
						list.get(i).status = status;
						BabytreeLog.d("知识更新 CommConstants.TYPE_REMIND");
						break;
					}
				}
			} catch (Exception e) {
			}
			Knowledge knowledge = mAdapter.updateKnowledgeListById(id);
			knowledge.status = status;
			mAdapter.notifyDataSetChanged();
			BabytreeLog.d("知识更新 = notifyDataSetChanged");
		}
	};

	@Override
	public void onStart() {
		super.onStart();

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

		public Knowledge updateKnowledgeListById(int knowledgeId) {

			// final Knowledge bean = (Knowledge) getItem(0);
			ArrayList<Knowledge> knowledgeList = null;
			// title
			int c = mList.size();
			for (int i = 0; i < c; i++) {
				knowledgeList = mList.get(i).second;
				int count = knowledgeList.size();
				for (int j = 0; j < count; j++) {
					final Knowledge knowledge = knowledgeList.get(j);
					ArrayList<Knowledge> knowledges = knowledge.list;
					int c2 = knowledges.size();
					for (int k = 0; k < c2; k++) {
						final Knowledge know = knowledges.get(k);
						if (know._id == knowledgeId) {
							BabytreeLog.d("更新知识 DO = id = " + knowledgeId);
							return know;
						}
					}
				}
			}
			return null;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewCache viewCache;
			if (convertView == null) {
				convertView = View.inflate(InformationActivity.this, R.layout.knowledge_information_item, null);
				viewCache = new ViewCache(convertView);
				convertView.setTag(viewCache);
			} else {
				viewCache = (ViewCache) convertView.getTag();
			}

			final Knowledge bean = (Knowledge) getItem(position);
			LinearLayout linearLayout = viewCache.getLinearLayout();
			LinearLayout linearPressed = viewCache.getLayoutPressed();
			TextView today = viewCache.getToday();
			TextView yuchanqi = viewCache.getYuchanqi();
			TextView week = viewCache.getWeek();
			TextView date = viewCache.getDate();
			TextView everyDayTitle = viewCache.getEveryDayTitle();
			TextView subTitle = viewCache.getSubTitle();
			TextView content = viewCache.getContent();
			ImageView image = viewCache.getContentImage();
			CheckBox checkBox0 = viewCache.getCheckBox0();
			TextView remind0 = viewCache.getRemind0();
			CheckBox checkBox1 = viewCache.getCheckBox1();
			TextView remind1 = viewCache.getRemind1();
			CheckBox checkBox2 = viewCache.getCheckBox2();
			TextView remind2 = viewCache.getRemind2();
			ImageView line0 = viewCache.getLine0();
			ImageView line1 = viewCache.getLine1();
			ImageView line2 = viewCache.getLine2();
			LinearLayout ll0 = viewCache.getLl0();
			LinearLayout ll1 = viewCache.getLl1();
			LinearLayout ll2 = viewCache.getLl2();
			LinearLayout layout_know = viewCache.getLayoutKnow();
			LinearLayout topicLL0 = viewCache.getTopicLL0();
			LinearLayout topicLL1 = viewCache.getTopicLL1();
			LinearLayout topicLL2 = viewCache.getTopicLL2();
			TextView topicTV0 = viewCache.getTopicTV0();
			TextView topicTV1 = viewCache.getTopicTV1();
			TextView topicTV2 = viewCache.getTopicTV2();
			ImageView topicLine0 = viewCache.getTopicLine0();
			ImageView topicLine1 = viewCache.getTopicLine1();
			ImageView topicLine2 = viewCache.getTopicLine2();
			topicLine0.setVisibility(View.GONE);
			topicLine1.setVisibility(View.GONE);
			topicLine2.setVisibility(View.GONE);
			topicLL0.setVisibility(View.GONE);
			topicLL1.setVisibility(View.GONE);
			topicLL2.setVisibility(View.GONE);
			today.setVisibility(View.GONE);
			yuchanqi.setVisibility(View.GONE);
			image.setVisibility(View.GONE);
			line0.setVisibility(View.GONE);
			line1.setVisibility(View.GONE);
			line2.setVisibility(View.GONE);
			ll0.setVisibility(View.GONE);
			ll1.setVisibility(View.GONE);
			ll2.setVisibility(View.GONE);
			everyDayTitle.setVisibility(View.GONE);
			subTitle.setVisibility(View.GONE);
			// week
			final int has = hasDaysNum;
			if (position != 0) {
				week.setText(getWeek(getBeforeAfterDate(getPregnancyDay(has).toString(), (bean.days_number - 2))));
			} else {
				week.setVisibility(View.GONE);
			}
			// date
			Calendar cal = new GregorianCalendar();
			cal.setTime(getBeforeAfterDate(getPregnancyDay(has).toString(), (bean.days_number - 2)));
			if (position != 0) {
				date.setText((cal.get(Calendar.MONTH) + 1) + "月" + cal.get(Calendar.DAY_OF_MONTH) + "日");
			} else {
				date.setVisibility(View.GONE);
			}
			// title
			ArrayList<Knowledge> list = bean.list;

			for (int i = 0; i < list.size(); i++) {
				if (list.get(i).category_id == 5) {
					continue;
				}
				final Knowledge knowledge = list.get(i);

				if (knowledge.type_id == CommConstants.TYPE_KNOW) {
					linearPressed.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// 知识
							// Umeng Evert
							MobclickAgent.onEvent(getBaseContext(), EventContants.know, EventContants.know_item_click);
							Intent intent = null;
							if (position == 0) {
								intent = new Intent(InformationActivity.this, InformationForTitleActivity.class)
										.putExtra("type_name", "备孕").putExtra("mStrings", mString);
							} else {
								intent = new Intent(getApplicationContext(), InformationDetailActivity.class);
								intent.putExtra(InformationDetailActivity.BUNDLE_ID, knowledge._id);
								intent.putExtra(InformationDetailActivity.BUNDLE_CATEGORY_ID, knowledge.category_id);
								intent.putExtra(InformationDetailActivity.BUNDLE_DAYS_NUMBER, knowledge.days_number);
							}
							startActivity(intent);
						}
					});
					layout_know.setVisibility(View.VISIBLE);
					if (knowledge.category_id != 5) {
						if (knowledge.title != null && !knowledge.title.equals("")) {
							subTitle.setVisibility(View.VISIBLE);
							subTitle.setText(knowledge.title);
						} else {
							everyDayTitle.setVisibility(View.GONE);
						}
						if (knowledge.category_id != 0 && position != 0) {
							everyDayTitle.setVisibility(View.VISIBLE);
							everyDayTitle.setText(BabytreeTitleUtil.switchTitle(knowledge.category_id,
									BabytreeTitleUtil.TYPE_TITLE));
						} else {
							everyDayTitle.setVisibility(View.GONE);
						}
						content.setText(knowledge.summary_content);
						// 图片
						if (knowledge.summary_image != null && !knowledge.summary_image.equals("")) {
							image.setVisibility(View.VISIBLE);
							image.setTag(Md5Util.md5(knowledge.summary_image));
							Bitmap mBitmap = mImageCacheLoaderForBitmap.loadDrawableForBitmap("htmls/",
									knowledge.summary_image, InformationActivity.this, new ImageCallbackForBitmap() {
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
												// catch block
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
					}

				} else if (knowledge.type_id == CommConstants.TYPE_REMIND) {// remind
					if (ll0.getVisibility() == View.GONE && knowledge.is_important == 1) {
						ll0.setVisibility(View.VISIBLE);
						line0.setVisibility(View.VISIBLE);
						remind0.setText(knowledge.title);
						ll0.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								// Umeng Evert
								MobclickAgent.onEvent(getBaseContext(), EventContants.know, EventContants.know_remind);
								Intent intent = new Intent(getApplicationContext(), RemindDetailActivity.class);
								intent.putExtra("_id", knowledge._id);
								intent.putExtra("title", knowledge.title);
								intent.putExtra("status", knowledge.status);
								intent.putExtra("days_number", knowledge.days_number);
								intent.putExtra("position", position);
								// intent.putExtra("identify", 0);
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
								Intent intent = new Intent(getApplicationContext(), RemindDetailActivity.class);
								intent.putExtra("_id", knowledge._id);
								intent.putExtra("title", knowledge.title);
								intent.putExtra("status", knowledge.status);
								intent.putExtra("identify", 0);
								intent.putExtra("days_number", knowledge.days_number);
								intent.putExtra("position", position);
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
								Intent intent = new Intent(getApplicationContext(), RemindDetailActivity.class);
								intent.putExtra("_id", knowledge._id);
								intent.putExtra("title", knowledge.title);
								intent.putExtra("status", knowledge.status);
								intent.putExtra("identify", 0);
								intent.putExtra("days_number", knowledge.days_number);
								intent.putExtra("position", position);
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
				} else if (knowledge.type_id == CommConstants.TYPE_TOPIC) {// 讨论
					if (topicLL0.getVisibility() == View.GONE) {
						topicLine0.setVisibility(View.VISIBLE);
						topicLL0.setVisibility(View.VISIBLE);
						topicTV0.setText(knowledge.title);
						topicLL0.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								startActivity(new Intent(InformationActivity.this, TagTopicListActivity.class)
										.putExtra("tag", knowledge.title));
							}
						});

					} else if (topicLL1.getVisibility() == View.GONE) {
						topicLine1.setVisibility(View.VISIBLE);
						topicLL1.setVisibility(View.VISIBLE);
						topicTV1.setText(knowledge.topics);
						topicLL1.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								startActivity(new Intent(InformationActivity.this, TagTopicListActivity.class)
										.putExtra("tag", knowledge.title));
							}
						});

					} else if (topicLL2.getVisibility() == View.GONE) {
						topicLine2.setVisibility(View.VISIBLE);
						topicLL2.setVisibility(View.VISIBLE);
						topicTV2.setText(knowledge.topics);
						topicLL2.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								startActivity(new Intent(InformationActivity.this, TagTopicListActivity.class)
										.putExtra("tag", knowledge.title));
							}
						});

					}

				}
				yuchanqi.setText("离预产期还有" + hasDaysNum + "天");
			}

			int section = getSectionForPosition(position);
			boolean displaySectionHeaders = (getPositionForSection(section) == position);
			bindSectionHeader(convertView, position, displaySectionHeaders);
			if (selectedPosition == position && hasDaysNum >= 0) {
				linearLayout.setBackgroundResource(R.color.pink);
				today.setVisibility(View.VISIBLE);
				yuchanqi.setVisibility(View.VISIBLE);
				// selectedPosition = -1;
				if (!displaySectionHeaders) {
					LinearLayout layoutOther = viewCache.getLayoutOther();
					layoutOther.setPadding(0, 30, 0, 0);
				}
			} else {
				linearLayout.setBackgroundResource(R.color.background);
				today.setVisibility(View.GONE);
				yuchanqi.setVisibility(View.GONE);

			}
			return convertView;
		}

		public void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
			TextView tv = (TextView) view.findViewById(R.id.information_header);
			LinearLayout layoutOther = (LinearLayout) view.findViewById(R.id.information_layout_other);
			if (topPadding == 0) {
				topPadding = layoutOther.getPaddingTop();
			}
			LinearLayout layoutTitle = (LinearLayout) view.findViewById(R.id.information_layout_title);
			if (displaySectionHeader) {
				layoutTitle.setVisibility(View.VISIBLE);
				tv.setText(getSections()[getSectionForPosition(position)]);
				layoutOther.setPadding(0, topPadding, 0, 0);
			} else {
				layoutTitle.setVisibility(View.GONE);
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
		if (mDialog != null) {
			mDialog.dismiss();
		}
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
			startActivity(new Intent(this, InformationForTitleActivity.class).putExtra("type_name", mString[position])
					.putExtra("mStrings", mString));
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
		private TextView today, yuchanqi, week, date, subTitle, content, remind0, remind1, remind2, everyDayTitle;

		private View baseView;

		private ImageView contentImage, line0, line1, line2;

		private CheckBox checkBox0, checkBox1, checkBox2;

		private LinearLayout ll0, ll1, ll2, linearLayout, layoutOther, layoutPressed;

		private LinearLayout topicLL0, topicLL1, topicLL2;

		private TextView topicTV0, topicTV1, topicTV2;

		private ImageView topicLine0, topicLine1, topicLine2;

		public ViewCache(View view) {
			baseView = view;
		}

		public ImageView getTopicLine0() {
			if (topicLine0 == null) {
				topicLine0 = (ImageView) baseView.findViewById(R.id.topic_line0);
			}
			return topicLine0;
		}

		public ImageView getTopicLine1() {
			if (topicLine1 == null) {
				topicLine1 = (ImageView) baseView.findViewById(R.id.topic_line1);
			}
			return topicLine1;
		}

		public ImageView getTopicLine2() {
			if (topicLine2 == null) {
				topicLine2 = (ImageView) baseView.findViewById(R.id.topic_line2);
			}
			return topicLine2;
		}

		public TextView getTopicTV0() {
			if (topicTV0 == null) {
				topicTV0 = (TextView) baseView.findViewById(R.id.layout_topic_textview0);
			}
			return topicTV0;
		}

		public TextView getTopicTV1() {
			if (topicTV1 == null) {
				topicTV1 = (TextView) baseView.findViewById(R.id.layout_topic_textview1);
			}
			return topicTV1;
		}

		public TextView getTopicTV2() {
			if (topicTV2 == null) {
				topicTV2 = (TextView) baseView.findViewById(R.id.layout_topic_textview2);
			}
			return topicTV2;
		}

		public LinearLayout getTopicLL0() {
			if (topicLL0 == null) {
				topicLL0 = (LinearLayout) baseView.findViewById(R.id.topic0);
			}
			return topicLL0;
		}

		public LinearLayout getTopicLL1() {
			if (topicLL1 == null) {
				topicLL1 = (LinearLayout) baseView.findViewById(R.id.topic1);
			}
			return topicLL1;
		}

		public LinearLayout getTopicLL2() {
			if (topicLL2 == null) {
				topicLL2 = (LinearLayout) baseView.findViewById(R.id.topic2);
			}
			return topicLL2;
		}

		public LinearLayout getLayoutPressed() {
			if (layoutPressed == null) {
				layoutPressed = (LinearLayout) baseView.findViewById(R.id.layout_pressed);
			}
			return layoutPressed;
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

		public LinearLayout getLl0() {
			if (ll0 == null) {
				ll0 = (LinearLayout) baseView.findViewById(R.id.ll0);
			}
			return ll0;
		}

		public LinearLayout getLl1() {
			if (ll1 == null) {
				ll1 = (LinearLayout) baseView.findViewById(R.id.ll1);
			}
			return ll1;
		}

		public LinearLayout getLl2() {
			if (ll2 == null) {
				ll2 = (LinearLayout) baseView.findViewById(R.id.ll2);
			}
			return ll2;
		}

		public LinearLayout getLinearLayout() {
			if (linearLayout == null) {
				linearLayout = (LinearLayout) baseView.findViewById(R.id.information_layout_other);
			}
			return linearLayout;
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
				convertView = View.inflate(InformationActivity.this, R.layout.knowledge_information_search_item, null);
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
