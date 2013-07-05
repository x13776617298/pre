package com.babytree.apps.comm.ui;

import java.util.ArrayList;
import java.util.List;

import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.comm.config.EventContants;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SearchActivity extends BabytreeTitleAcitivty implements OnItemClickListener, OnClickListener {
	private EditText mEditText;
	private Button imgSearch;
	private String mTag;

	private int num = 0;
	private List<String> list;
	private ListView mListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mListView = (ListView) findViewById(R.id.keyword_listview);
		mListView.setOnItemClickListener(this);
		mEditText = (EditText) findViewById(R.id.main_edit_txt);
		imgSearch = (Button) findViewById(R.id.img_search);
		imgSearch.setOnClickListener(this);
	}

	private List<String> initList() {
		List<String> listStr = new ArrayList<String>();
		for (int i = 0; i < 10; i++) {
			String keyWord = SharedPreferencesUtil.getStringValue(this, "keyword_" + String.valueOf(i));
			if (null != keyWord && !"".equals(keyWord)) {
				listStr.add(keyWord);
			}

		}
		return listStr;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_search:
			String keyWord = mEditText.getText().toString().trim();
			if (!"".equals(keyWord)) {
				// UMENG Event
				MobclickAgent.onEvent(this, EventContants.event_search);
				InputMethodManager imm = ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE));
				imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

				if (num >= 0 && num < 10) {
					List<String> sList = new ArrayList<String>();
					for (int i = 0; i < 10; i++) {
						String s = SharedPreferencesUtil.getStringValue(this, "keyword_" + String.valueOf(i));
						if (null != s) {
							sList.add(s);
						}
					}
					if (!sList.contains(keyWord)) {
						SharedPreferencesUtil.setValue(this, "keyword_" + String.valueOf(num), keyWord);
						SharedPreferencesUtil.setValue(this, "search_click_num", num += 1);
					}

				}
				mTag = keyWord;
				setTitleString(mTag);
				startActivity(new Intent(this, TagTopicListActivity.class).putExtra("tag", keyWord));
				mEditText.setText("");
			} else {
				Toast.makeText(this, R.string.question_can_not_null, Toast.LENGTH_SHORT).show();
				return;
			}
			break;
		default:
			break;
		}

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		String keyWord = list.get(position);
		startActivity(new Intent(this, TagTopicListActivity.class).putExtra("tag", keyWord));
	}

	// ==========UMENG Begin===========
	@Override
	protected void onResume() {
		super.onResume();
		num = SharedPreferencesUtil.getIntValue(this, "search_click_num");
		if (num <= -1 || num >= 10) {
			num = 0;
		}
		list = new ArrayList<String>();
		list = initList();
		if (list.size() > 0) {
			MAdapter adapter = new MAdapter(SearchActivity.this, list);
			mListView.setAdapter(adapter);
		}

	}

	private class MAdapter extends BaseAdapter {
		private List<String> list = new ArrayList<String>();
		private Context context;

		public MAdapter(Context context, List<String> list) {
			this.list = list;
			this.context = context;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.search_activity_item, parent, false);
			}
			TextView keyWord = (TextView) convertView.findViewById(R.id.search_activity_item_keyword);
			ImageView delImg = (ImageView) convertView.findViewById(R.id.search_activity_item_delete_img);
			keyWord.setText(list.get(position));
			delImg.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					String key = "";
					for (int i = 0; i < 10; i++) {
						String s = SharedPreferencesUtil.getStringValue(context, "keyword_" + String.valueOf(i));
						if (getItem(position).equals(s)) {
							key = "keyword_" + String.valueOf(i);
							break;
						}
					}
					if (!"".equals(key)) {
						SharedPreferencesUtil.removeKey(context, key);
						list.remove(position);
					}

					notifyDataSetChanged();
				}

			});
			return convertView;
		}

	}

	@Override
	public void setLeftButton(Button button) {
	}

	@Override
	public void setRightButton(Button button) {
	}

	@Override
	public String getTitleString() {
		return "搜索";
	}

	@Override
	public int getBodyView() {
		return R.layout.search_activity_n;
	}
}
