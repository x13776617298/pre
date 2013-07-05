package com.babytree.apps.comm.ui;

import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.xp.controller.ExchangeDataService;
import com.umeng.xp.view.ExchangeViewManager;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

/**
 * 广告管理
 *
 */
public class UnionActivity extends BabytreeTitleAcitivty implements OnClickListener , OnCheckedChangeListener{

	public static final String TAB_BABYTREE = "babytree";
	public static final String TAB_HOT = "hot";

	private ListView lvBabytree;
	private ListView lvHot;

	private ViewGroup vgBabytree;
	private ViewGroup vgHot;
	
	private RadioGroup rgTab;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.union_activity);

		lvBabytree = (ListView) findViewById(R.id.list1);
		lvHot = (ListView) findViewById(R.id.list2);

		vgBabytree = (ViewGroup) findViewById(R.id.father1);
		vgHot = (ViewGroup) findViewById(R.id.father2);
		
		rgTab = (RadioGroup) findViewById(R.id.rg_tab);
		rgTab.setOnCheckedChangeListener(this);

		ExchangeDataService exchangeDataService1 = new ExchangeDataService();
		exchangeDataService1.setKeywords(TAB_BABYTREE);// 设置分组的关键词。
		exchangeDataService1.autofill = 0; // 自主广告数量小的情况下，不要自动填充来自交往网络的广告。
		new ExchangeViewManager(this, exchangeDataService1).addView(vgBabytree,
				lvBabytree);
		ExchangeDataService exchangeDataService2 = new ExchangeDataService();
		// exchangeDataService2.setKeywords(TAB_HOT);// 设置分组的关键词。
		exchangeDataService2.autofill = 0; // 自主广告数量小的情况下，不要自动填充来自交往网络的广告。
		new ExchangeViewManager(this, exchangeDataService2).addView(vgHot,
				lvHot);
	}


	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		
		if(checkedId == R.id.rb_tab1){
			vgBabytree.setVisibility(View.VISIBLE);
			vgHot.setVisibility(View.GONE);
		}else if(checkedId == R.id.rb_tab2){
			vgBabytree.setVisibility(View.GONE);
			vgHot.setVisibility(View.VISIBLE);
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
		return "应用推荐";
	}

	@Override
	public int getBodyView() {
		return R.layout.union_activity;
	}
}
