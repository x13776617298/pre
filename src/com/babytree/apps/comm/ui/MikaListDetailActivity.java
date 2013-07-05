package com.babytree.apps.comm.ui;

import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;

public class MikaListDetailActivity extends BabytreeTitleAcitivty implements OnClickListener{
	
	private WebView mWebView;
	
	private String BASE_URL = "http://www.babytree.com/promo/mika_api/get_comment_detail.php?";

	private String mContentId;
	
	private String mContentType;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.mika_list_detail_activity);
		
		mContentId = getIntent().getStringExtra("content_id");
		mContentType = getIntent().getStringExtra("content_type");
		mWebView = (WebView)findViewById(R.id.webview);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		webSettings.setDefaultTextEncodingName("utf-8");
		mWebView.loadUrl(createUrl(mContentId,mContentType));
	}

	private String createUrl(String id, String tyep) {
		String url = BASE_URL + "&content_id=" +id+ "&content_type="
				+ tyep;
		return url;
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_left:
            finish();
			break;
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
		return "点评详情";
	}

	@Override
	public int getBodyView() {
		// TODO Auto-generated method stub
		return R.layout.mika_list_detail_activity;
	}
}
