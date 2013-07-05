package com.babytree.apps.biz.knowledge;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.TextView;

import com.babytree.apps.biz.knowledge.ctr.CalendarDbController;
import com.babytree.apps.biz.knowledge.model.Knowledge;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.db.CalendarDbAdapter;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.ui.ForumActivity;
import com.babytree.apps.comm.ui.TagTopicListActivity;
import com.babytree.apps.comm.ui.WeekTopicListActivity;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;
import com.umeng.analytics.MobclickAgent;

/**
 * 孕期--提醒详情
 */
public class RemindDetailActivity extends BabytreeActivity implements OnClickListener {

	private WebView mWebView;

	private CalendarDbAdapter mDbAdapter;

	private CalendarDbController mCalendarDbController;

	private CheckBox cb;
	private TextView text;
	private int _id, is_important, days;
	private int status;
	int important;
	private Context ctx;
	private int position;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.knowledge_remind_detail_activity);
		ctx = this;
		findViewById(R.id.btn_left).setOnClickListener(this);
		mWebView = (WebView) findViewById(R.id.web_remind);
		text = (TextView) findViewById(R.id.remind_detail_important);
		String title = getIntent().getStringExtra("title");

		_id = getIntent().getIntExtra("_id", 0);// 读出数据
		status = getIntent().getIntExtra("status", 0);// 读出数据
		is_important = getIntent().getIntExtra("is_important", 0);
		days = getIntent().getIntExtra("days_number", 0);
		position = getIntent().getIntExtra("position", 0);
		cb = (CheckBox) findViewById(R.id.cb_remind);
		cb.setText(title);
		if (status == 1) {
			cb.setChecked(true);
		} else {
			cb.setChecked(false);
		}

		cb.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				CheckBox cb = (CheckBox) v;
				if (cb.isChecked()) {
					mCalendarDbController.updateKnowledge(_id, 1);
					sendBroadCast(1);
					sendBroadCastForRemind(1);
				} else {
					mCalendarDbController.updateKnowledge(_id, 0);
					sendBroadCast(0);
					sendBroadCastForRemind(0);
				}

			}

		});

		String url = String.valueOf(_id) + ".html";

		url = this.getNavigateUrl(url);

		findViewById(R.id.btn_left).setOnClickListener(this);

		mDbAdapter = mApplication.getCalendarDbAdapter();
		mCalendarDbController = new CalendarDbController(mDbAdapter);

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		webSettings.setDefaultTextEncodingName("utf-8");

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				mWebView.loadUrl("javascript:" + "function nativeDiscuzReply(discuz_id,refer_id,position){"
						+ " window.android.nativeDiscuzReply(discuz_id,refer_id,position);" + " }"
						+ "function nativeDiscuzByTag(tag){ " + "window.android.nativeDiscuzByTag(tag); " + "}"
						+ "function nativeNewBirthclubByWeek(week){ "
						+ "window.android.nativeNewBirthclubByWeek(week);" + "}"
						+ "function nativeAllBirthclubByWeek(week){ "
						+ "window.android.nativeAllBirthclubByWeek(week);" + "}" + "function nativeNavigate(url){ "
						+ "window.android.nativeNavigate(url);" + "}");
				// 判断是否为爸爸版(如果是屏蔽超链接)
				if (mIsFather) {
					String js = " (function () {" + "var slice = Array.prototype.slice;"
							+ "var list = document.querySelectorAll(\".other.link > :not([id])\");"
							+ "slice.call(list).forEach(function(v){" + "v.style.display=\"none\";" + "});" + "}());";
					mWebView.loadUrl("javascript:" + js);
				}
				processDate();
			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return true;
			}

		});
		mWebView.addJavascriptInterface(new JavaScriptInterface(), "android");
		mWebView.loadUrl(url);
		// 重要度
		Random random = new Random();
		if (is_important == 1) {
			important = 70 + random.nextInt(20);
		} else if (is_important == 0) {
			important = 50 + random.nextInt(40);
		}
		text.setText("本阶段" + String.valueOf(important) + "%的准妈妈做了这件事");

		// 判断是否为爸爸版,更改UI样式
		if (mIsFather) {
			findViewById(R.id.layout_title).setBackgroundDrawable(getResources().getDrawable(R.drawable.title_back));
			findViewById(R.id.btn_left).setBackgroundDrawable(getResources().getDrawable(R.drawable.f_btn_back));
		}

	}

	private void sendBroadCast(int status) {
		Intent intent = new Intent();
		intent.setAction("DataUpdate");
		intent.putExtra("position", position);
		intent.putExtra("status", status);
		intent.putExtra("_id", _id);
		intent.setPackage(getPackageName());
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	private void sendBroadCastForRemind(int status) {
		Intent intent = new Intent();
		intent.setAction("DataUpdateForRemind");
		intent.putExtra("status", status);
		intent.putExtra("_id", _id);
		intent.putExtra("position", position);
		intent.setPackage(getPackageName());
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
	}

	public final class JavaScriptInterface {

		JavaScriptInterface() {
		}

		/**
		 * This is not called on the UI thread. Post a runnable to invoke
		 * loadUrl on the UI thread.
		 */
		public void clickOnAndroid() {
			mWebView.goBack();
			/*
			 * mHandler.post(new Runnable() { public void run() {
			 * mWebView.loadUrl("javascript:wave()"); } });
			 */

		}

		public void message(String s) {
		}

		public void nativeDiscuzReply(String discuzId, String referId, String position) {
		}

		public void nativeDiscuzByTag(String tag) {
			// Umeng Evert
			MobclickAgent.onEvent(getBaseContext(), EventContants.know, EventContants.know_detail_communication);
			Intent intent = new Intent(ctx, TagTopicListActivity.class);
			intent.putExtra("tag", tag);
			startActivity(intent);
		}

		public void nativeNavigate(String url) {
			// Umeng Evert
			MobclickAgent.onEvent(getBaseContext(), EventContants.know, EventContants.know_detail_correlation);
			// mWebView.loadUrl(getNavigateUrl(url));

			String urlId = getNavigateUrl(url);
			BabytreeLog.d("孕期知识下一条newUrl = " + urlId + " -- newUrl id = " + urlId.substring(0, urlId.indexOf(".")));
			urlId = urlId.substring(urlId.lastIndexOf("/") + 1, urlId.length());
			urlId = urlId.substring(0, urlId.indexOf("."));
			BabytreeLog.d("孕期知识下一条newUrl = " + urlId);
			CalendarDbController calendarDbController = mApplication.getCalendarDbController();
			Knowledge bean = calendarDbController.getKnowledgeByID(Integer.parseInt(urlId));

			Intent intent = new Intent(RemindDetailActivity.this, RemindDetailActivity.class);
			intent.putExtra("_id", bean._id);
			intent.putExtra("title", bean.title);
			intent.putExtra("status", bean.status);
			intent.putExtra("is_important", bean.is_important);
			intent.putExtra("identify", 1);
			// intent.putExtra("days_number", bean.days_number);
			intent.putExtra("position", position);
			finish();
			startActivity(intent);
		}

		/**
		 * 访问孕周在当周的同龄贴
		 * 
		 * @param week
		 *            为查看的当前孕周周数
		 */
		public void nativeNewBirthclubByWeek(String week) {
			// 计算方法为 （40-week）*7 + 今天日期 换算成同龄圈 进入该同龄圈
			SimpleDateFormat format = new SimpleDateFormat("yyyyMM", Locale.getDefault());
			long time = (40l - Long.parseLong(week)) * 7l * 24l * 60l * 60l * 1000l + System.currentTimeMillis();
			String birthday = format.format(new Date(time));
			Intent intent = new Intent(RemindDetailActivity.this, ForumActivity.class);
			intent.putExtra("birthday", birthday);
			intent.putExtra("type", 1);
			startActivity(intent);

		}

		/**
		 * 访问所有该孕月的同龄贴
		 * 
		 * @param week
		 *            为查看的当前孕周周数
		 */
		public void nativeAllBirthclubByWeek(String week) {
			Intent intent = new Intent(RemindDetailActivity.this, WeekTopicListActivity.class);
			int month = ((Integer.parseInt(week) - 1) / 4) + 1;
			intent.putExtra("week", month);
			startActivity(intent);
		}
	}

	private void processDate() {
		Message msg = new Message();
		hander.sendMessage(msg);
	}

	Handler hander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			long l2 = System.currentTimeMillis() + (long) 280l * 24l * 60l * 60l * 1000l - (long) days * 24l * 60l
					* 60l * 1000l;
			SimpleDateFormat s = new SimpleDateFormat("yyyy年MM月", Locale.getDefault());
			mWebView.loadUrl("javascript:replace_txt('" + s.format(new Date(l2)) + "')");

		}

	};

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_left) {
			finish();
		}
	}

	private String getNavigateUrl(String url) {
		String navigateUrl;
		String head = url.substring(0, 4);
		if (head.equals("http")) {
			navigateUrl = url;
		} else {
			navigateUrl = "file:///android_asset/htmls/" + url;
		}
		return navigateUrl;
	}
}
