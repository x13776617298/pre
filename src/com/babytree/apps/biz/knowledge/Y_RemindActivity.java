package com.babytree.apps.biz.knowledge;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.TextView;

import com.babytree.apps.biz.knowledge.ctr.Y_CalendarDbController;
import com.babytree.apps.biz.knowledge.model.Y_Knowledge;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.db.Y_CalendarDbAdapter;
import com.babytree.apps.comm.ui.SearchActivity;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.ui.adapter.PinnedHeaderListViewAdapter;
import com.babytree.apps.comm.ui.widget.PinnedHeaderListView;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * 育儿--提醒列表页
 */
public class Y_RemindActivity extends BabytreeActivity implements OnItemClickListener, OnClickListener {

	private PinnedHeaderListView mListView;

	private PregnancyApplication mApplication;

	private Y_CalendarDbAdapter mDbAdapter;

	private Y_CalendarDbController mController;

	private MAdapter mAdapter;

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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.knowledge_y_remind_activity);
		// 注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction("DataUpdateForRemind");
		LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver, filter);

		mImgIcon = (ImageView) findViewById(R.id.img_icon);
		mImgIcon.setOnClickListener(this);
		mImageOut = (ImageView) findViewById(R.id.iv_item_bg_out);
		mImageIn = (ImageView) findViewById(R.id.iv_item_bg_in);
		mDialog = new ProgressDialog(this);
		mDialog.setMessage("加载中···");

		mListView = (PinnedHeaderListView) findViewById(R.id.remind_activity_list);

		mApplication = (PregnancyApplication) getApplication();
		mDbAdapter = mApplication.y_getCalendarDbAdapter();
		mController = new Y_CalendarDbController(mDbAdapter);

		hasDaysNum = SharedPreferencesUtil.getIntValue(this, ShareKeys.Y_HASDAYS);
		todaySelected = hasDaysNum / 7;
		mString = initStrArray(52);
		// mAdapter = new MAdapter();
		// mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);

		findViewById(R.id.btn_left).setOnClickListener(this);
		findViewById(R.id.btn_right).setOnClickListener(this);
		findViewById(R.id.txt_center).setOnClickListener(this);
		View view = View.inflate(this, R.layout.knowledge_y_information_search_menu, null);
		myAdapter = new MyAdatper();
		searchListView = (ListView) view.findViewById(R.id.layout_list_view);
		searchListView.setAdapter(myAdapter);
		searchListView.setOnItemClickListener(this);
		if (todaySelected >= 0 && todaySelected < 5) {
			searchListView.setSelection(0);
		} else {
			searchListView.setSelection(todaySelected - 2);
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
			if (mDialog != null && !Y_RemindActivity.this.isFinishing()) {
				mDialog.dismiss();
			}
			mListView.setPinnedHeaderView(LayoutInflater.from(Y_RemindActivity.this).inflate(
					R.layout.knowledge_y_remind_header, mListView, false));
			mAdapter = new MAdapter();
			mListView.setAdapter(mAdapter);
			mListView.setSelection(mAdapter.getPositionForSection(todaySelected));
			mAdapter.setSelectedSection(todaySelected);
			mAdapter.notifyDataSetInvalidated();
		};
	};

	private void initData() {
		mDialog.show();
		new Thread(new Runnable() {
			@Override
			public void run() {
				weekList = mController.getWeekListForRemind();
				Message message = handler.obtainMessage();
				handler.sendMessage(message);
			}
		}).start();

	}

	private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int status = 0;
			int id = 0;
			status = intent.getIntExtra("status", 0);
			id = intent.getIntExtra("_id", 0);
			Y_Knowledge know = (Y_Knowledge) mAdapter.getY_KnowledgeById(id);
			know.status = status;
			mAdapter.notifyDataSetChanged();
		}
	};
	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver);
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_left) {
			finish();
		} else if (v.getId() == R.id.btn_right) {
			// Umeng Evert
			MobclickAgent.onEvent(getBaseContext(), EventContants.setup, EventContants.setup_share);
			startActivity(new Intent(this, SearchActivity.class));
			/*
			 * MobclickAgent.onEvent(getBaseContext(), EventContants.know,
			 * EventContants.know_search); if (mMenu.isShowing()) {
			 * mMenu.dismiss(); } else { mMenu.showAsDropDown(v, -50, 0); //
			 * mMenu.showAtLocation(v, Gravity.RIGHT | Gravity.BOTTOM, 0, // 0);
			 * }
			 */
			// mListView.setSelection(todaySelected);
			// mAdapter.setSelectedPosition(todaySelected);
			// mAdapter.notifyDataSetInvalidated();
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
		// Intent intent = new Intent(this, RemindDetailActivity.class);
		//
		// // Umeng Evert
		// MobclickAgent.onEvent(getBaseContext(), EventContants.remind,
		// EventContants.remind_item_click);
		// Knowledge bean = (Knowledge)mAdapter.getItem(position);
		//
		// intent.putExtra("_id", bean._id);
		// intent.putExtra("title", bean.title);
		// intent.putExtra("status", bean.status);
		// intent.putExtra("is_important", bean.is_important);
		// intent.putExtra("identify", 1);
		// startActivity(intent);
		// finish();
		if (parent == searchListView) {
			// if (position > 0) {
			// mListView.setSelection(mAdapter
			// .getPositionForSection((position - 1)));
			// } else if (position == 0) {
			// mListView.setSelection(mAdapter
			// .getPositionForSection(todaySelected));
			// mAdapter.setSelectedSection(todaySelected);
			// }
			mListView.setSelection(mAdapter.getPositionForSection(position));
			mAdapter.notifyDataSetInvalidated();
			if (mMenu.isShowing()) {
				mMenu.dismiss();
				mImageIn.setVisibility(View.VISIBLE);
			}
		}
	}

	private class MAdapter extends PinnedHeaderListViewAdapter {

		private int selectedSection = -1;// 选中的位置

		private List<Pair<String, ArrayList<Y_Knowledge>>> mList = weekList;

		private int topPadding = 0;

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
					ArrayList<Y_Knowledge> list = mList.get(i).second;
					ArrayList<Y_Knowledge> listIs = new ArrayList<Y_Knowledge>();
					ArrayList<Y_Knowledge> listNot = new ArrayList<Y_Knowledge>();
					for (Y_Knowledge knowledge : list) {
						if (knowledge.is_important == 1) {
							listIs.add(knowledge);
						} else if (knowledge.is_important == 0) {
							listNot.add(knowledge);
						}
					}
					listIs.addAll(listNot);
					return listIs.get(position - count);
					// return mList.get(i).second.get(position - count);
				}
				count += mList.get(i).second.size();
			}
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		public Y_Knowledge getY_KnowledgeById(int knowledgeId) {
			int count = getCount();
			for (int i = 0; i < count; i++) {
				ArrayList<Y_Knowledge> knowledges = mList.get(i).second;

				for (Y_Knowledge y_Knowledge : knowledges) {
					if (y_Knowledge._id == knowledgeId) {
						return y_Knowledge;
					}
				}
			}
			return null;
		}

		public void setSelectedSection(int section) {
			selectedSection = section;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewCache viewCache;
			if (convertView == null) {
				convertView = View.inflate(Y_RemindActivity.this, R.layout.knowledge_y_remind_item, null);
				viewCache = new ViewCache(convertView);
				convertView.setTag(viewCache);
			} else {
				viewCache = (ViewCache) convertView.getTag();
			}

			final Y_Knowledge bean = (Y_Knowledge) getItem(position);
			TextView name = viewCache.getName();
			LinearLayout linearLayout = viewCache.getLinearLayout();
			linearLayout.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(Y_RemindActivity.this, Y_RemindDetailActivity.class);
					// Umeng Evert
					MobclickAgent.onEvent(getBaseContext(), EventContants.remind, EventContants.remind_item_click);
					intent.putExtra("_id", bean._id);
					intent.putExtra("title", bean.title);
					intent.putExtra("status", bean.status);
					intent.putExtra("is_important", bean.is_important);
					intent.putExtra("identify", 1);
					intent.putExtra("position", position);
					startActivity(intent);
				}

			});
			TextView is_important = viewCache.getIsImportant();
			CheckBox cb_action = viewCache.getAction();
			if (bean.status == 1) {
				cb_action.setChecked(true);
			} else {
				cb_action.setChecked(false);
			}

			cb_action.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// Umeng Evert
					MobclickAgent.onEvent(getBaseContext(), EventContants.remind, EventContants.remind_check);
					CheckBox cb = (CheckBox) v;
					if (cb.isChecked()) {
						mController.updateKnowledge(bean._id, 1);
						bean.status = 1;
					} else {
						mController.updateKnowledge(bean._id, 0);
						bean.status = 0;
					}
				}
			});
			name.setText(bean.title);
			if (bean.is_important == 1) {
				is_important.setVisibility(View.VISIBLE);
				is_important.setText("本周重点");
			} else {
				is_important.setVisibility(View.GONE);
			}
			final int section = getSectionForPosition(position);
			boolean displaySectionHeaders = (getPositionForSection(section) == position);
			bindSectionHeader(convertView, position, displaySectionHeaders);
			if (selectedSection == section) {
				linearLayout.setBackgroundResource(R.drawable.y_remind_list_selector_background);
			} else {
				linearLayout.setBackgroundResource(R.drawable.y_list_selector_background);
			}
			return convertView;
		}

		@Override
		public void bindSectionHeader(View view, int position, boolean displaySectionHeader) {
			TextView tv = (TextView) view.findViewById(R.id.remind_header);
			LinearLayout layoutOther = (LinearLayout) view.findViewById(R.id.remind_layout_other);
			LinearLayout layoutTitle = (LinearLayout) view.findViewById(R.id.remind_layout_title);
			if (topPadding == 0) {
				topPadding = layoutOther.getPaddingTop();
			}
			if (displaySectionHeader) {
				layoutTitle.setVisibility(View.VISIBLE);
				tv.setVisibility(View.VISIBLE);
				tv.setText(getSections()[getSectionForPosition(position)]);
				layoutOther.setPadding(0, topPadding, 0, 0);
			} else {
				layoutTitle.setVisibility(View.GONE);
				layoutOther.setPadding(0, 0, 0, 0);
			}
		}

		@Override
		public void configurePinnedHeader(View header, int position, int alpha) {
			((TextView) header.findViewById(R.id.remind_header))
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

	private static class ViewCache {
		private View baseView;

		private TextView name;

		private TextView is_important;

		private CheckBox cb_action;

		private LinearLayout linearLayout;

		public ViewCache(View view) {
			baseView = view;
		}

		public TextView getName() {
			if (name == null) {
				name = (TextView) baseView.findViewById(R.id.txt_name);
			}
			return name;
		}

		public TextView getIsImportant() {
			if (is_important == null) {
				is_important = (TextView) baseView.findViewById(R.id.txt_is_important);
			}
			return is_important;
		}

		public CheckBox getAction() {
			if (cb_action == null) {
				cb_action = (CheckBox) baseView.findViewById(R.id.cb_action);
			}
			return cb_action;
		}

		public LinearLayout getLinearLayout() {
			if (linearLayout == null) {
				linearLayout = (LinearLayout) baseView.findViewById(R.id.remind_layout_other);
			}
			return linearLayout;
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
				convertView = View.inflate(Y_RemindActivity.this, R.layout.knowledge_y_information_search_item, null);
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
