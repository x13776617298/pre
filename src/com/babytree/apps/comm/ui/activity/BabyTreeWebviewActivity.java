package com.babytree.apps.comm.ui.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.DownloadListener;
import android.webkit.HttpAuthHandler;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import com.babytree.apps.biz.topic.TopicNewActivity;
import com.babytree.apps.biz.topicpost.TopicPostNewActivity;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.model.Discuz;
import com.babytree.apps.comm.net.BabytreeHttp;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

/**
 * 自定义Webview<br>
 * 注:<br>
 * 混淆加-keep class (包名).BabyTreeWebviewActivity$JavaScriptInterface
 */
@SuppressLint("SetJavaScriptEnabled")
public class BabyTreeWebviewActivity extends BabytreeTitleAcitivty {

	public static final String BUNDLE_URL = "url";
	public static final String BUNDLE_TITLE = "title";

	/**
	 * BabyBox发表新帖JS<br>
	 * navTitle 字符类型 发帖页的导航栏显示文字，默认为“发表新帖”（不超过10汉字）<br>
	 * groupId 数字类型 目标圈子ID<br>
	 * groupName 字符类型 目标圈子名称（不超过10汉字）<br>
	 * topicTitle 字符类型 帖子标题（不超过10汉字）<br>
	 * tip 字符类型 显示在内容区的提示文字（不超过20个汉字）<br>
	 */
	private static final String CUSTOM_CREATE_TOPIC_JS = "javascript:"
			+ "function customCreateTopic(navTitle,groupId,groupName,topicTitle,tip){ "
			+ "window.android.customCreateTopic(navTitle,groupId,groupName,topicTitle,tip); " + "}";

	/**
	 * 网页URL
	 */
	private String mUrl;
	/**
	 * 标题
	 */
	private String mTtitle;
	private WebView mWebView;

	/**
	 * 用户cookie
	 */
	private String mCookie;

	public static void launch(Activity context, String url, String title) {
		Intent intent = new Intent(context, BabyTreeWebviewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString(BUNDLE_URL, url);
		bundle.putString(BUNDLE_TITLE, title);
		intent.putExtras(bundle);
		BabytreeUtil.launch(context, intent, false, 0);
	}

	public void onCreate(Bundle savedInstanceState) {

		mUrl = getIntent().getStringExtra(BUNDLE_URL);
		mTtitle = getIntent().getStringExtra(BUNDLE_TITLE);
		
		if (TextUtils.isEmpty(mTtitle)) {
			mTtitle = "详情";
		}

		super.onCreate(savedInstanceState);

		/**
		 * 获取用户Cookie
		 */
		mCookie = SharedPreferencesUtil.getStringValue(this, ShareKeys.COOKIE);
		mWebView = (WebView) findViewById(R.id.webview);

		WebSettings webSettings = mWebView.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(true);
		webSettings.setBuiltInZoomControls(true);
		webSettings.setDefaultTextEncodingName("UTF-8");
		mWebView.setWebViewClient(new MyWebViewClient());
		mWebView.setDownloadListener(new MyWebViewDownLoadListener());
		mWebView.setWebChromeClient(new MyWebChromeClient());
		// 设置Cookie
		BabytreeUtil.setCookies(this, mUrl, mCookie);
		mWebView.loadUrl(mUrl);
		mWebView.addJavascriptInterface(new JavaScriptInterface(), "android");
	}

	private class MyWebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype,
				long contentLength) {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			startActivity(intent);
		}

	}

	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
		}

		@Override
		public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
			new AlertDialog.Builder(BabyTreeWebviewActivity.this).setTitle("提示").setMessage(message)
					.setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							result.confirm();
						}
					}).setCancelable(false).create().show();

			return true;

		}
	}

	private class MyWebViewClient extends WebViewClient {
		@Override
		public void onReceivedHttpAuthRequest(WebView view, HttpAuthHandler handler, String host, String realm) {
			// 网页认证
			handler.proceed(BabytreeHttp.USER_NAME, BabytreeHttp.PASSWORD);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// 设置Cookie
			BabytreeUtil.setCookies(getApplication(), url, mCookie);
			if (url.contains("/community/topic_mobile.php")) {
				Intent intent = new Intent(getBaseContext(), TopicNewActivity.class);
				Discuz discuz = getPareams(url);
				intent.putExtra(TopicNewActivity.BUNDLE_DISCUZ_ID, discuz._id);
				startActivity(intent);
				finish();
			} else {
				return super.shouldOverrideUrlLoading(view, url);
			}
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			dismissLoadingDialog();
			// 加载js
			mWebView.loadUrl(CUSTOM_CREATE_TOPIC_JS);
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			showLoadingDialog();
		}
	}

	@Override
	public void setLeftButton(Button button) {
		// Nothing todo.
	}

	@Override
	public void setRightButton(Button button) {
		// Nothing todo.
	}

	@Override
	public String getTitleString() {
		return mTtitle;
	}

	@Override
	public int getBodyView() {
		return R.layout.babytree_webview_activity;
	}

	public static Discuz getPareams(String url) {
		Discuz discuz = new Discuz();
		String tail = url.substring(url.indexOf("?") + 1);
		String[] params = tail.split("&");
		for (int i = 0; i < params.length; i++) {
			if (params[i].contains("id=")) {
				try {
					discuz._id = Integer.parseInt(params[i].substring(params[i].indexOf("=") + 1));
					discuz.discuz_id = discuz._id;
				} catch (NumberFormatException e) {
					e.printStackTrace();
					discuz._id = 0;
				}

			} else if (params[i].contains("author_response_count=")) {
				try {
					discuz.author_response_count = Integer.parseInt(params[i].substring(params[i].indexOf("=") + 1));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					discuz.author_response_count = 0;
				}

			} else if (params[i].contains("respond_count=")) {
				try {
					discuz.response_count = Integer.parseInt(params[i].substring(params[i].indexOf("=") + 1));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					discuz.response_count = 0;
				}

			} else if (params[i].contains("is_fav=")) {
				try {
					discuz.is_fav = Integer.parseInt(params[i].substring(params[i].indexOf("=") + 1));
				} catch (NumberFormatException e) {
					e.printStackTrace();
					discuz.is_fav = 0;
				}

			}
		}

		return discuz;
	}

	public final class JavaScriptInterface {

		JavaScriptInterface() {
		}

		/**
		 * BabyBox发帖
		 * 
		 * @author wangshuaibo
		 * @param navTitle
		 *            字符类型 发帖页的导航栏显示文字，默认为“发表新帖”（不超过10汉字）
		 * @param groupId
		 *            数字类型 目标圈子ID
		 * @param groupName
		 *            字符类型 目标圈子名称（不超过10汉字）
		 * @param topicTitle
		 *            字符类型 帖子标题（不超过10汉字）
		 * @param tip
		 *            字符类型 显示在内容区的提示文字（不超过20个汉字）
		 */
		public void customCreateTopic(String navTitle, String groupId, String groupName, String topicTitle, String tip) {

			int bundleGroupId = 0;
			if (TextUtils.isDigitsOnly(groupId)) {
				bundleGroupId = Integer.parseInt(groupId);
			}

			TopicPostNewActivity.launch(mContext, bundleGroupId, null, groupName, null, navTitle, topicTitle, tip,
					false, 0);

		}
	}

}
