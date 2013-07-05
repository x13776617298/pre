package com.babytree.apps.comm.ui;

import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.ButtomClickUtil;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.TextView;
public class AboutMikaActivity extends BabytreeTitleAcitivty implements OnClickListener{
	
	private WebView mWebView;
	
	private String url ;
	
	private String mTitle;
	
	private TextView mtxtCenter;
	
	public static final String SERVICE_TELEPHONE = "4000445288";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		findViewById(R.id.img_mika_service).setOnClickListener(this);
		mTitle = getIntent().getStringExtra("title");
		if(mTitle != null && !mTitle.equals("")){
			setTitleString(mTitle);
		}
		url = getIntent().getStringExtra("url");
		mWebView = (WebView)findViewById(R.id.webview);
		WebSettings webSettings = mWebView.getSettings();
		webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);
		webSettings.setSavePassword(false);
		webSettings.setSaveFormData(false);
		webSettings.setJavaScriptEnabled(true);
		webSettings.setSupportZoom(false);
		webSettings.setDefaultTextEncodingName("utf-8");
		mWebView.loadUrl(url);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.img_mika_service:
			 new AlertDialog
             .Builder(this)
             .setTitle("确认拨出电话吗？")
             .setPositiveButton("是", new DialogInterface.OnClickListener(){
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
         			Intent intent = new Intent();
         			// 系统默认的action，用来打开默认的电话界面
         			intent.setAction(Intent.ACTION_CALL);
         			intent.setData(Uri.parse("tel:" + SERVICE_TELEPHONE));
         			startActivity(intent);
                 }
             })
             .setNegativeButton("否", new DialogInterface.OnClickListener(){
                 @Override
                 public void onClick(DialogInterface dialog, int which) {
                 }
             })
             .show();
			break;
		}
		
	}

	@Override
	public void setLeftButton(Button button) {
	}

	@Override
	public void setRightButton(Button button) {
		button.setBackgroundResource(R.drawable.y_btn_share);
		button.setVisibility(View.VISIBLE);
		button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(ButtomClickUtil.isFastDoubleClick()){
					return;
				}
				mApplication.getUmSocialService().setShareImage(null);
	            mApplication.getUmSocialService().setShareContent("");
				mApplication.getUmSocialService().openShare(AboutMikaActivity.this);
			}
		});
	}

	@Override
	public String getTitleString() {
		// TODO Auto-generated method stub
		return "米卡成长天地";
	}

	@Override
	public int getBodyView() {
		return R.layout.about_mika_activity;
	}

}
