package com.babytree.apps.comm.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.BabytreeDbController;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.model.TopicGroup;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.ui.category.PinnedHeaderListView;
import com.babytree.apps.comm.ui.category.PinnedHeaderListViewAdapter;
import com.babytree.apps.comm.ui.category.PinnedHeaderListViewArrayAdapter;
import com.babytree.apps.comm.ui.category.PinnedHeaderListViewBean;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 全部圈子
 */
public class GroupActivity extends BabytreeTitleAcitivty implements OnItemClickListener, OnClickListener {

	private PinnedHeaderListView mListView;

	private MAdapter mAdapter;

	private BabytreeDbController mDbController;

	private String mBirthday;

	private ArrayList<PinnedHeaderListViewBean> mList;

	private PinnedHeaderListViewArrayAdapter arrayAdapter;

	private String loginStr;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loginStr = SharedPreferencesUtil.getStringValue(this, ShareKeys.LOGIN_STRING);
		mBirthday = SharedPreferencesUtil.getStringValue(this, ShareKeys.BIRTHDAY);

		mDbController = mApplication.getDbController();

		mListView = (PinnedHeaderListView) findViewById(R.id.listGroup);

		initData(loginStr);

	}

	private void initData(final String loginStr) {
		// Loading
		showLoading();
		new Thread() {

			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(GroupActivity.this)) {
						ret = P_BabytreeController.getCommunityGroupList(loginStr, mBirthday);
					} else {
						ret = new DataResult();
						ret.message = P_BabytreeController.NetworkExceptionMessage;
						ret.status = P_BabytreeController.NetworkExceptionCode;
					}
				} catch (Exception e) {
					ret = new DataResult();
					ret.message = P_BabytreeController.SystemExceptionMessage;
					ret.status = P_BabytreeController.SystemExceptionCode;
				}
				message.obj = ret;
				handler.sendMessage(message);
			}

		}.start();

	}

	private void showLoading() {
		findViewById(R.id.load).setVisibility(View.VISIBLE);
		findViewById(R.id.listGroup).setVisibility(View.GONE);
		findViewById(R.id.layout_empty).setVisibility(View.GONE);
	}

	private void hideLoading() {
		findViewById(R.id.load).setVisibility(View.GONE);
		findViewById(R.id.listGroup).setVisibility(View.VISIBLE);
		findViewById(R.id.layout_empty).setVisibility(View.GONE);
	}

	private void loadError() {
		findViewById(R.id.load).setVisibility(View.GONE);
		findViewById(R.id.listGroup).setVisibility(View.GONE);
		findViewById(R.id.layout_empty).setVisibility(View.GONE);
		if (!this.isFinishing()) {
			showReloadDialogForFirst();
		}
	}

	private void dataEmpty() {
		findViewById(R.id.load).setVisibility(View.GONE);
		findViewById(R.id.listGroup).setVisibility(View.GONE);
		findViewById(R.id.layout_empty).setVisibility(View.VISIBLE);
		if (!this.isFinishing()) {
			showReloadDialogForFirst();
		}

	}

	private void showReloadDialogForFirst() {
		AlertDialog.Builder dialog = new AlertDialog.Builder(this);
		dialog.setMessage(this.getResources().getString(R.string.load_failure));
		dialog.setPositiveButton(this.getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int arg1) {
				dialog.dismiss();
			}
		});
		dialog.setNegativeButton(this.getResources().getString(R.string.reload), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
				dialog.dismiss();
				initData(loginStr);
			}
		});
		dialog.create().show();
	}

	private ArrayList<PinnedHeaderListViewBean> removeSameItem(ArrayList<PinnedHeaderListViewBean> topList,
			ArrayList<PinnedHeaderListViewBean> dataList) {
		for (PinnedHeaderListViewBean bean : topList) {
			int index = indexOf(bean, dataList);
			if (index != -1) {
				dataList.remove(index);
			}
		}
		dataList.addAll(0, topList);
		return dataList;
	}

	public int indexOf(PinnedHeaderListViewBean bean, ArrayList<PinnedHeaderListViewBean> dataList) {
		for (int i = 0; i < dataList.size(); i++) {
			TopicGroup group = (TopicGroup) bean.item;
			TopicGroup group2 = (TopicGroup) dataList.get(i).item;
			if (group.group_id == group2.group_id) {
				return i;
			}
		}
		return -1;
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {

		@Override
		@SuppressWarnings("unchecked")
		public void handleMessage(Message msg) {
			DataResult ret = (DataResult) msg.obj;
			switch (ret.status) {
			case P_BabytreeController.SUCCESS_CODE:

				// 数据库中查询我喜欢的
				ArrayList<PinnedHeaderListViewBean> topList = mDbController.getTopicGroupList();
				mList = removeSameItem(topList, (ArrayList<PinnedHeaderListViewBean>) ret.data);
				if (mList.size() > 0) {
					// Hide loading
					hideLoading();
					// Success
					arrayAdapter = new PinnedHeaderListViewArrayAdapter(getApplicationContext(), R.id.txt_name, mList);
					mAdapter = new MAdapter(arrayAdapter);
					mListView.setAdapter(mAdapter);
					mListView.setOnScrollListener(mAdapter);
					mListView.setOnItemClickListener(GroupActivity.this);
					mListView.setPinnedHeaderView(getLayoutInflater().inflate(R.layout.group_header, mListView, false));

				} else {
					// Data empty
					dataEmpty();
				}
				break;
			default:
				// Load error
				loadError();
				ExceptionUtil.catchException(ret.error, GroupActivity.this);
				Toast.makeText(GroupActivity.this, ret.message, Toast.LENGTH_SHORT).show();
				break;
			}
		}

	};

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		TopicGroup group = (TopicGroup) mList.get(position).item;
		Intent intent = new Intent(this, ForumActivity.class);
		intent.putExtra("group_id", group.group_id);
		intent.putExtra("name", group.name);
		startActivity(intent);
	}

	private class MAdapter extends PinnedHeaderListViewAdapter {

		private PinnedHeaderListViewArrayAdapter mArrayAdapter;

		private int PADDING_TOP = 0;

		public MAdapter(PinnedHeaderListViewArrayAdapter arrayAdapter) {
			super(arrayAdapter);
			mArrayAdapter = arrayAdapter;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewCache viewCache;
			if (convertView == null) {
				convertView = View.inflate(getApplicationContext(), R.layout.group_item, null);
				viewCache = new ViewCache(convertView);
				convertView.setTag(viewCache);
			} else {
				viewCache = (ViewCache) convertView.getTag();
			}
			PinnedHeaderListViewBean currentItem = mArrayAdapter.items.get(position);
			if (currentItem != null) {
				final TopicGroup bean = (TopicGroup) currentItem.item;
				TextView tv = (TextView) convertView.findViewById(R.id.txt_name);
				tv.setText(bean.name);
				int section = getSectionForPosition(position);
				// 显示标题
				LinearLayout layoutOther = viewCache.getLayoutOther();
				if (getPositionForSection(section) == position) {

					if (PADDING_TOP == 0) {
						PADDING_TOP = layoutOther.getPaddingTop();
					}

					layoutOther.setPadding(0, PADDING_TOP, 0, 0);

					viewCache.getLayoutTitle().setVisibility(View.VISIBLE);
					String title = bean.title;
					if ("birth".equals(bean.title)) {
						title = "我喜欢的圈子";
					} else if ("other_birth".equals(bean.title)) {
						title = "同龄圈";
					} else if ("other".equals(bean.title)) {
						title = "其它圈子";
					}
					viewCache.getTitle().setText(title);

				} else {
					// 隐藏标题
					viewCache.getLayoutTitle().setVisibility(View.GONE);
					viewCache.getTitle().setText("");
					layoutOther.setPadding(0, 0, 0, 0);

				}
				layoutOther.setBackgroundResource(R.drawable.list_selector_background);
				layoutOther.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						Intent intent = new Intent(GroupActivity.this, ForumActivity.class);
						intent.putExtra("group_id", bean.group_id);
						intent.putExtra("name", bean.name);
						startActivity(intent);
					}
				});

				CheckBox favorite = viewCache.getFavorite();
				if (bean.status == 1) {
					favorite.setChecked(true);
				} else {
					favorite.setChecked(false);
				}
				favorite.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// Umeng Evert
						MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.com_all_like);
						CheckBox cb = (CheckBox) v;
						if (cb.isChecked()) {
							bean.status = 1;
							mDbController.addTopicGroup(bean);
						} else {
							bean.status = 0;
							mDbController.deleteTopicGroup(bean._id);
						}
						mAdapter.notifyDataSetChanged();
					}
				});

				viewCache.getName().setText(bean.name);
				viewCache.getDiscussionCount().setText(bean.discussion_count + "贴");
				viewCache.getDescription().setText(bean.description);
			}

			return convertView;

		}

		@Override
		public void configurePinnedHeader(View header, int position, int alpha) {
			int section = getSectionForPosition(position);
			String title = (String) getIndexer().getSections()[section];
			if ("birth".equals(title)) {
				title = "我喜欢的圈子";
			} else if ("other_birth".equals(title)) {
				title = "同龄圈";
			} else if ("other".equals(title)) {
				title = "其它圈子";
			}
			((TextView) header.findViewById(R.id.txt_title)).setText(title);
		}

	}

	private static class ViewCache {
		private View baseView;

		private TextView name;

		private TextView discussion_count;

		private TextView description;

		private TextView title;

		private CheckBox favorite;

		private LinearLayout layoutTitle;

		private LinearLayout layoutOther;

		public ViewCache(View view) {
			baseView = view;
		}

		public TextView getName() {
			if (name == null) {
				name = (TextView) baseView.findViewById(R.id.txt_name);
			}
			return name;
		}

		public CheckBox getFavorite() {
			if (favorite == null) {
				favorite = (CheckBox) baseView.findViewById(R.id.cb_favorite);
			}
			return favorite;
		}

		public TextView getDiscussionCount() {
			if (discussion_count == null) {
				discussion_count = (TextView) baseView.findViewById(R.id.txt_discussion_count);
			}
			return discussion_count;
		}

		public TextView getDescription() {
			if (description == null) {
				description = (TextView) baseView.findViewById(R.id.txt_description);
			}
			return description;
		}

		public TextView getTitle() {
			if (title == null) {
				title = (TextView) baseView.findViewById(R.id.txt_title);
			}
			return title;
		}

		public LinearLayout getLayoutTitle() {
			if (layoutTitle == null) {
				layoutTitle = (LinearLayout) baseView.findViewById(R.id.layout_title);
			}
			return layoutTitle;
		}

		public LinearLayout getLayoutOther() {
			if (layoutOther == null) {
				layoutOther = (LinearLayout) baseView.findViewById(R.id.layout_other);
			}
			return layoutOther;
		}

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		if (v.getId() == R.id.btn_left) {
			finish();
		}
	}

	@Override
	public void setLeftButton(Button button) {
		
	}

	@Override
	public void setRightButton(Button button) {
		button.setVisibility(View.GONE);
		
	}

	@Override
	public String getTitleString() {
		return "全部圈子";
	}

	@Override
	public int getBodyView() {
		return R.layout.group_activity;
	}
}