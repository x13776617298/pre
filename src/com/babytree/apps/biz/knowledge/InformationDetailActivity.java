package com.babytree.apps.biz.knowledge;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import com.babytree.apps.biz.knowledge.ctr.CalendarDbController;
import com.babytree.apps.biz.knowledge.ctr.Y_CalendarDbController;
import com.babytree.apps.biz.knowledge.model.Knowledge;
import com.babytree.apps.biz.knowledge.model.Y_Knowledge;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.ctr.BabytreeController;
import com.babytree.apps.comm.listener.BabytreeOnClickListenner;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.ui.CalculatorActivity;
import com.babytree.apps.comm.ui.ForumActivity;
import com.babytree.apps.comm.ui.TagTopicListActivity;
import com.babytree.apps.comm.ui.WeekTopicListActivity;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.BabytreeTitleUtil;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.pregnancy.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnDownUpRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnPullEventListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase.State;
import com.handmark.pulltorefresh.library.PullToRefreshWebView;
import com.umeng.analytics.MobclickAgent;
import com.umeng.socialize.bean.ShareImage;

/**
 * 孕期-每日知识详情页
 * 
 */
@SuppressLint("SetJavaScriptEnabled")
public class InformationDetailActivity extends BabytreeTitleAcitivty {

	public static final String BUNDLE_ID = "id";
	public static final String BUNDLE_CATEGORY_ID = "category_id";
	public static final String BUNDLE_DAYS_NUMBER = "days_number";

	private static final int WHAT_LOAD_URL = 1;

	/**
	 * NativeJs
	 */
	private static final String JS = "javascript:" + "function nativeDiscuzByTag(tag){ "
			+ "window.android.nativeDiscuzByTag(tag); " + "}" + "function nativeNewBirthclubByWeek(week){ "
			+ "window.android.nativeNewBirthclubByWeek(week);" + "}" + "function nativeAllBirthclubByWeek(week){ "
			+ "window.android.nativeAllBirthclubByWeek(week);" + "}" + "function nativeNavigate(url){ "
			+ "window.android.nativeNavigate(url);" + "}" + "function nativeSetTitle(title){ "
			+ "window.android.nativeSetTitle(title);" + "}" + "function nativeSetPNTitle(pTip, nTip){ "
			+ "window.android.nativeSetPNTitle(pTip, nTip);" + "}" + "function nativeModfiyDuedate(){ "
			+ "window.android.nativeModfiyDuedate();" + "}";

	/**
	 * 屏蔽超链接JS
	 */
	private static final String HIDE_URL_JS = "javascript:" + " (function () {" + "var slice = Array.prototype.slice;"
			+ "var list = document.querySelectorAll(\".other.link > :not([id])\");"
			+ "slice.call(list).forEach(function(v){" + "v.style.display=\"none\";" + "});" + "}());";

	/**
	 * 隐藏上一篇,下一篇JS
	 */
	private static final String HIDE_UP_DOWN_JS = "javascript:if (typeof hidepn != 'undefined' && hidepn instanceof Function) {  hidepn();}";

	/**
	 * 下拉加载上一页JS
	 */
	private static final String PJS = "javascript:if(document.getElementById(\"p_day\")){"
			+ "document.getElementById(\"p_day\").childNodes[0].onclick();" + "}else{window.location.reload();}";

	/**
	 * 上拉加载下一页
	 */
	private static final String NJS = "javascript:if(document.getElementById(\"n_day\")){"
			+ "document.getElementById(\"n_day\").childNodes[0].onclick();" + "}else{window.location.reload();}";

	/**
	 * 设置上一页,上一页Title JS
	 */
	private static final String SETTITLE_JS = "javascript:var pTip='';var nTip='';if(document.getElementById(\"p_day\")){" +
			"pTip=document.getElementById(\"p_day\").childNodes[0].innerHTML;}" +
			"if(document.getElementById(\"n_day\")){" +
			"nTip=document.getElementById(\"n_day\").childNodes[0].innerHTML;}"
			+ "nativeSetPNTitle(pTip,nTip);";

	/**
	 * 下拉上拉WebView
	 */
	private PullToRefreshWebView mPullRefreshWebView;
	private WebView mWebView;
	protected CalendarDbController mCalendarDbController;
	protected Y_CalendarDbController mYCalendarDbController;

	/**
	 * WebView需要加载的URL
	 */
	private String publicUlr;
	/**
	 * 怀孕天数/宝宝出生天数
	 */
	private int daysNumber;
	/**
	 * 分享时的标题
	 */
	private String title = "";
	/**
	 * 上一条标题
	 */
	private String pTitle = "";
	/**
	 * 下一条标题
	 */
	private String nTitle = "";
	/**
	 * HTML对应的ID
	 */
	protected int id;

	/**
	 * 知识分类标识
	 */
	private int categroyId;

	@Override
	public void onCreate(Bundle icicle) {

		id = getIntent().getIntExtra(BUNDLE_ID, 0);
		daysNumber = getIntent().getIntExtra(BUNDLE_DAYS_NUMBER, 0);
		categroyId = getIntent().getIntExtra(BUNDLE_CATEGORY_ID, 0);

		super.onCreate(icicle);

		mCalendarDbController = mApplication.getCalendarDbController();
		mYCalendarDbController = mApplication.y_getCalendarDbController();

		publicUlr = id + ".html";
		publicUlr = getNavigateUrl(publicUlr);

		mPullRefreshWebView = (PullToRefreshWebView) findViewById(R.id.webview);
		mWebView = mPullRefreshWebView.getRefreshableView();

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		mWebView.setDrawingCacheEnabled(true);
		webSettings.setDefaultTextEncodingName("UTF-8");
		mWebView.setVerticalScrollBarEnabled(false);

		mWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageFinished(WebView view, String url) {
				super.onPageFinished(view, url);
				
				// 清空上一页,下一页提示
				pTitle = "";
				nTitle = "";
				
				mWebView.loadUrl(JS);
				mWebView.loadUrl(HIDE_UP_DOWN_JS);
				// 设置上一页,上一页Title
				mWebView.loadUrl(SETTITLE_JS);
				replaceTxt();
				// 判断是否为爸爸版(如果是屏蔽超链接)
				if (mIsFather) {
					mWebView.loadUrl(HIDE_URL_JS);
				}

				// 更新Title标题
				changeTitle(publicUlr);

			}

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				return true;
			}

		});
		mWebView.loadUrl(publicUlr);
		mWebView.addJavascriptInterface(new JavaScriptInterface(), "android");

		// 下拉,上拉
		// 设置没有加载图标
		mPullRefreshWebView.getLoadingLayoutProxy().setLoadingDrawable(null);
		mPullRefreshWebView.setOnRefreshListener(new OnDownUpRefreshListener<WebView>() {

			@Override
			public void onDownToRefresh(PullToRefreshBase<WebView> refreshView) {
				// 下拉加载上一页
				mWebView.loadUrl(PJS);
				
			}

			@Override
			public void onUpToRefresh(PullToRefreshBase<WebView> refreshView) {
				// 上拉加载下一页
				mWebView.loadUrl(NJS);

			}
		});
		// 上拉,下拉文案
		mPullRefreshWebView.setOnPullEventListener(new OnPullEventListener<WebView>() {

			@Override
			public void onPullEvent(PullToRefreshBase<WebView> refreshView, State state, Mode direction) {
				if (direction == Mode.PULL_FROM_START) {
					// 下拉
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(pTitle);
				} else if (direction == Mode.PULL_FROM_END) {
					// 上拉
					refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(nTitle);
				}
			}
		});

	}

	protected void changeTitle(String url) {
		String htmlId = "";
		try {

			htmlId = url.substring(url.lastIndexOf("/") + 1, url.length());
			htmlId = htmlId.substring(0, htmlId.indexOf("."));
		} finally {
		}

		if (!TextUtils.isDigitsOnly(htmlId)) {
			return;
		}

		String title = "";

		if (mIsPregnancy) {
			Y_Knowledge knowledge = mYCalendarDbController.getKnowledgeByID(categroyId);
			if (knowledge != null) {
				title = getTitle(knowledge.category_id, knowledge.days_number, mIsPregnancy);
			}
		} else {
			Knowledge knowledge = mCalendarDbController.getKnowledgeByID(Integer.parseInt(htmlId));
			if (knowledge != null) {
				title = getTitle(knowledge.category_id, knowledge.days_number, mIsPregnancy);
			}
		}
		if(TextUtils.isEmpty(title)){
			title = "知识详情";
		}
		// 设置标题
		setTitleString(title);

	}

	/**
	 * 设置Title
	 * 
	 * @author wangshuaibo
	 * @param categroy
	 *            分类
	 * @param daysNumber
	 *            怀孕天数/宝宝出生天数
	 */
	private String getTitle(int categroy, int daysNumber, boolean isPregnancy) {

		String title = "";
		String categroyName = "";

		if (isPregnancy) {
			// 育儿
			categroyName = BabytreeTitleUtil.switchTitle(categroy, BabytreeTitleUtil.Y_TYPE_TITLE);

		} else {
			// 孕期
			categroyName = BabytreeTitleUtil.switchTitle(categroy, BabytreeTitleUtil.TYPE_TITLE);
		}
		String dayName = getDayName(daysNumber, isPregnancy);
		title = categroyName + "(" + dayName + ")";

		return title;

	}

	/**
	 * 返回孕几周/婴儿期几周
	 * 
	 * @author wangshuaibo
	 * @param daysNumber
	 * @param isPregnancy
	 * @return
	 */
	private String getDayName(int daysNumber, boolean isPregnancy) {
		String dayName = "";
		
		if (isPregnancy) {
			int week = daysNumber / 7;
			// 育儿
			if (week <= 0) {
				week = 1;
			}
			dayName = "婴儿期" + week + "周";
		} else {
			// 孕期
			int week = (daysNumber-1) / 7;
			if (week < 3) { // <3备孕周
				dayName = "备孕周";
			} else {
				dayName = "孕" + week + "周";
			}

		}

		return dayName;
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {

			if (msg.what == WHAT_LOAD_URL) {
				String url = (String) msg.obj;
				mWebView.loadUrl(url);
			}

		};
	};

	public final class JavaScriptInterface {

		JavaScriptInterface() {
		}

		public void nativeDiscuzByTag(String tag) {
			MobclickAgent.onEvent(getBaseContext(), EventContants.know, EventContants.know_detail_communication);
			Intent intent = new Intent(mContext, TagTopicListActivity.class);
			intent.putExtra("tag", tag);

			BabytreeUtil.launch(mContext, intent, false, 0);
		}

		/**
		 * 点击相关知识链接/上一页/下一页
		 * 
		 * @author wangshuaibo
		 * @param url
		 */
		public void nativeNavigate(String url) {
			BabytreeLog.i("NativeNavigate Url : " + url);

			MobclickAgent.onEvent(getBaseContext(), EventContants.know, EventContants.know_detail_correlation);
			publicUlr = getNavigateUrl(url);
			Message message = new Message();
			message.what = WHAT_LOAD_URL;
			message.obj = publicUlr;
			mHandler.sendMessage(message);
		}

		/**
		 * 访问孕周在当周的同龄贴
		 * 
		 * @param week
		 *            为查看的当前孕周周数
		 */
		public void nativeNewBirthclubByWeek(String week) {
			// 计算方法为 （40-week）*7 + 今天日期 换算成同龄圈 进入该同龄圈
			SimpleDateFormat format = new SimpleDateFormat("yyyyMM", Locale.CHINA);
			long time = 0L;
			if (mIsPregnancy) {
				// 育儿
				time = ((52l - Long.parseLong(week)) * 7L - 365L) * 24l * 60l * 60l * 1000l
						+ System.currentTimeMillis();
			} else {
				// 孕期
				time = (40l - Long.parseLong(week)) * 7l * 24l * 60l * 60l * 1000l + System.currentTimeMillis();
			}
			String birthday = format.format(new Date(time));
			Intent intent = new Intent(InformationDetailActivity.this, ForumActivity.class);
			intent.putExtra("birthday", birthday);
			intent.putExtra("type", 1);
			BabytreeUtil.launch(mContext, intent, false, 0);

		}

		/**
		 * 访问所有该孕月的同龄贴
		 * 
		 * @param week
		 *            为查看的当前孕周周数
		 */
		public void nativeAllBirthclubByWeek(String week) {
			Intent intent = new Intent(InformationDetailActivity.this, WeekTopicListActivity.class);
			int month = ((Integer.parseInt(week) - 1) / 4) + 1;
			intent.putExtra("week", month);
			BabytreeUtil.launch(mContext, intent, false, 0);
		}

		/**
		 * 获取分享的Title，每个页面内容不同
		 */
		public void nativeSetTitle(String content) {
			title = content;
		}

		/**
		 * 设置上拉,下拉提示的内容
		 */
		public void nativeSetPNTitle(String pTip, String nTip) {
			BabytreeLog.d("pTip:" + pTip);
			BabytreeLog.d("nTip:" + nTip);
			if (TextUtils.isEmpty(pTip)) {
				pTip = "没有上一篇了";
			}
			if (TextUtils.isEmpty(nTip)) {
				nTip = "没有下一篇了";
			}
			pTitle = pTip;
			nTitle = nTip;
		}

		/**
		 * 跳转到计算预产期页面
		 * 
		 * @author wangshuaibo
		 */
		public void nativeModfiyDuedate() {
			Intent intent = new Intent(mContext, CalculatorActivity.class);
			BabytreeUtil.launch(mContext, intent, false, 0);
		}
	}

	protected String getNavigateUrl(String url) {
		String navigateUrl;
		String head = url.substring(0, 4);
		if (head.equals("http")) {
			navigateUrl = url;
		} else {
			if (id == 10000) {
				navigateUrl = "file:///android_asset/others/" + url;
			} else {
				if (mIsPregnancy) {
					// 育儿
					if (url.contains("y_")) {
						navigateUrl = "file:///android_asset/y/" + url;
					} else {
						navigateUrl = "file:///android_asset/y/y_" + url;
					}
				} else {
					// 孕期
					navigateUrl = "file:///android_asset/htmls/" + url;
				}
			}
		}
		return navigateUrl;
	}

	final class MyWebChromeClient extends WebChromeClient {
		@Override
		public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
			result.confirm();
			return true;
		}

		public void onReceivedTouchIconUrl(WebView view, String url, boolean precomposed) {
		}
	}

	/**
	 * 替换查看yyyy年MM月妈妈交流圈
	 * 
	 * @author wangshuaibo
	 */
	private void replaceTxt() {
		SimpleDateFormat s = new SimpleDateFormat("yyyy年MM月", Locale.CHINA);
		long birthTs = 0L;
		if (mIsPregnancy) {
			// 育儿
			birthTs = System.currentTimeMillis() - daysNumber * 24 * 60 * 60 * 1000L;
		} else {
			// 孕期
			birthTs = System.currentTimeMillis() + (long) 280 * 24 * 60 * 60 * 1000L - (long) daysNumber * 24 * 60l
					* 60 * 1000L;
		}
		mWebView.loadUrl("javascript:replace_txt('" + s.format(new Date(birthTs)) + "')");
	}

	@Override
	public void setLeftButton(Button button) {

	}

	@Override
	public void setRightButton(Button button) {
		button.setVisibility(View.VISIBLE);
		button.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_share));

		button.setOnClickListener(new BabytreeOnClickListenner() {
			@Override
			public void onClick(View v) {
				super.onClick(v);
				if (!BabytreeUtil.hasNetwork(mContext)) {
					Toast.makeText(mContext, BabytreeController.ConnectExceptionMessage, Toast.LENGTH_SHORT).show();
					return;
				}
				MobclickAgent.onEvent(getBaseContext(), EventContants.know, EventContants.know_share);
				title = title.equalsIgnoreCase("") ? mContext.getResources().getString(R.string.share_content) : title
						+ " @" + getResources().getString(R.string.app_name);
				float i = mWebView.getScale();
				int h = (int) (mWebView.getContentHeight() * i);
				// 分享信息
				if (h != 0) {
					Bitmap bitmap = Bitmap.createBitmap(mWebView.getWidth(), h, Bitmap.Config.ARGB_4444);
					final Canvas c = new Canvas(bitmap);
					mWebView.draw(c);
					mApplication.getUmSocialService().setShareImage(null);
					ShareImage shareImage = new ShareImage(mContext, bitmap);
					mApplication.getUmSocialService().setShareContent(title);
					mApplication.getUmSocialService().setShareImage(shareImage);
					mApplication.getUmSocialService().openShare(mContext);
					bitmap.recycle();
					bitmap = null;
				}
			}
		});

	}

	@Override
	public String getTitleString() {
		return getTitle(categroyId, daysNumber, mIsPregnancy);
	}

	@Override
	public int getBodyView() {
		return R.layout.knowledge_information_detail_activity;
	}

}
