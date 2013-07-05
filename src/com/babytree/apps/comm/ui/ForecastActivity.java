package com.babytree.apps.comm.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.model.ForecastInfo;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 生男生女的预测
 * 
 * @author luozheng
 * 
 */
public class ForecastActivity extends BabytreeTitleAcitivty implements OnClickListener {
	private ListView mListView;
	private List<ForecastInfo> fList;
	private ForecastAdapter mAdapter;
	private int boy, girl;
	private Map<Integer, Boolean> firSelect, secSelect, thiSelect;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		boy = 0;
		girl = 0;
		mListView = (ListView) findViewById(R.id.forecast_activity_list);
		fList = initData();
		firSelect = new HashMap<Integer, Boolean>();
		secSelect = new HashMap<Integer, Boolean>();
		thiSelect = new HashMap<Integer, Boolean>();
		for (int i = 0; i < fList.size(); i++) {
			firSelect.put(i, false);
			secSelect.put(i, false);
			thiSelect.put(i, false);
		}
		mAdapter = new ForecastAdapter();
		mListView.setAdapter(mAdapter);
	}

	private class ForecastAdapter extends BaseAdapter {
		private List<ForecastInfo> list = fList;

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			if (convertView == null) {
				convertView = View.inflate(ForecastActivity.this, R.layout.forecast_activity_item, null);
			}
			TextView question = (TextView) convertView.findViewById(R.id.forecast_item_question);
			final CheckBox ansCbx0 = (CheckBox) convertView.findViewById(R.id.forecast_item_answer_cbx0);
			TextView ansTxt0 = (TextView) convertView.findViewById(R.id.forecast_item_answer_txt0);
			final CheckBox ansCbx1 = (CheckBox) convertView.findViewById(R.id.forecast_item_answer_cbx1);
			TextView ansTxt1 = (TextView) convertView.findViewById(R.id.forecast_item_answer_txt1);
			final CheckBox ansCbx2 = (CheckBox) convertView.findViewById(R.id.forecast_item_answer_cbx2);
			TextView ansTxt2 = (TextView) convertView.findViewById(R.id.forecast_item_answer_txt2);
			ansCbx0.setVisibility(View.VISIBLE);
			ansCbx0.setClickable(true);
			ansCbx1.setClickable(true);
			ansCbx2.setClickable(true);
			ansCbx0.setChecked(firSelect.get(position));
			ansCbx1.setChecked(secSelect.get(position));
			ansCbx2.setChecked(thiSelect.get(position));
			ansCbx0.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ansCbx1.setChecked(false);
					ansCbx2.setChecked(false);
					ansCbx0.setChecked(true);
					boy += 4;
					firSelect.put(position, true);
				}

			});
			ansCbx1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ansCbx0.setChecked(false);
					ansCbx2.setChecked(false);
					ansCbx1.setChecked(true);
					girl += 4;
					secSelect.put(position, true);
				}

			});
			ansCbx2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					ansCbx0.setChecked(false);
					ansCbx1.setChecked(false);
					ansCbx2.setChecked(true);
					thiSelect.put(position, true);
				}

			});
			final ForecastInfo item = (ForecastInfo) getItem(position);
			// final ForecastInfo item = list.get(position);
			question.setText(item.question);
			ansTxt0.setText(item.ansFir);
			ansTxt1.setText(item.ansSec);
			ansTxt2.setText(item.ansThi);
			return convertView;
		}

	}

	public void clearInfo() {
		for (int i = 0; i < mAdapter.getCount(); i++) {
			try {
				View view = (View) mListView.getChildAt(i);
				final CheckBox ansCbx0;
				final CheckBox ansCbx1;
				final CheckBox ansCbx2;
				ansCbx0 = (CheckBox) view.findViewById(R.id.forecast_item_answer_cbx0);
				ansCbx1 = (CheckBox) view.findViewById(R.id.forecast_item_answer_cbx1);
				ansCbx2 = (CheckBox) view.findViewById(R.id.forecast_item_answer_cbx2);
				ansCbx0.setChecked(false);
				ansCbx1.setChecked(false);
				ansCbx2.setChecked(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void dialog(String sex) {
		AlertDialog.Builder sureDialog = new AlertDialog.Builder(this);
		sureDialog.setTitle("预测结果");
		sureDialog.setMessage(sex);
		sureDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
			}
		});
		sureDialog.create().show();
	}

	private List<ForecastInfo> initData() {
		List<ForecastInfo> list = new ArrayList<ForecastInfo>();
		ForecastInfo info0 = new ForecastInfo();
		info0.question = getResources().getString(R.string.s1_forecast);
		info0.ansFir = getResources().getString(R.string.forecast_select_yuan);
		info0.ansSec = getResources().getString(R.string.forecast_select_jian);
		info0.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info0);
		ForecastInfo info1 = new ForecastInfo();
		info1.question = getResources().getString(R.string.s2_forecast);
		info1.ansFir = getResources().getString(R.string.forecast_select_xiao);
		info1.ansSec = getResources().getString(R.string.forecast_select_da);
		info1.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info1);
		ForecastInfo info2 = new ForecastInfo();
		info2.question = getResources().getString(R.string.s3_forecast);
		info2.ansFir = getResources().getString(R.string.forecast_select_xia);
		info2.ansSec = getResources().getString(R.string.forecast_select_shang);
		info2.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info2);
		ForecastInfo info3 = new ForecastInfo();
		info3.question = getResources().getString(R.string.s4_forecast);
		info3.ansFir = getResources().getString(R.string.forecast_select_buqiang);
		info3.ansSec = getResources().getString(R.string.forecast_select_qianglie);
		info3.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info3);
		ForecastInfo info4 = new ForecastInfo();
		info4.question = getResources().getString(R.string.s5_forecast);
		info4.ansFir = getResources().getString(R.string.forecast_select_suan);
		info4.ansSec = getResources().getString(R.string.forecast_select_la);
		info4.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info4);
		ForecastInfo info5 = new ForecastInfo();
		info5.question = getResources().getString(R.string.s6_forecast);
		info5.ansFir = getResources().getString(R.string.forecast_select_chou);
		info5.ansSec = getResources().getString(R.string.forecast_select_piaoliang);
		info5.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info5);
		ForecastInfo info6 = new ForecastInfo();
		info6.question = getResources().getString(R.string.s7_forecast);
		info6.ansFir = getResources().getString(R.string.forecast_select_hei);
		info6.ansSec = getResources().getString(R.string.forecast_select_meibian);
		info6.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info6);
		ForecastInfo info7 = new ForecastInfo();
		info7.question = getResources().getString(R.string.s8_forecast);
		info7.ansFir = getResources().getString(R.string.forecast_select_da);
		info7.ansSec = getResources().getString(R.string.forecast_select_meibian);
		info7.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info7);
		ForecastInfo info8 = new ForecastInfo();
		info8.question = getResources().getString(R.string.s9_forecast);
		info8.ansFir = getResources().getString(R.string.forecast_select_buda);
		info8.ansSec = getResources().getString(R.string.forecast_select_zengda);
		info8.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info8);
		ForecastInfo info9 = new ForecastInfo();
		info9.question = getResources().getString(R.string.s10_forecast);
		info9.ansFir = getResources().getString(R.string.forecast_select_tuchu);
		info9.ansSec = getResources().getString(R.string.forecast_select_butuchu);
		info9.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info9);
		ForecastInfo info10 = new ForecastInfo();
		info10.question = getResources().getString(R.string.s11_forecast);
		info10.ansFir = getResources().getString(R.string.forecast_select_meizhongzhang);
		info10.ansSec = getResources().getString(R.string.forecast_select_zhongzhang);
		info10.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info10);
		ForecastInfo info11 = new ForecastInfo();
		info11.question = getResources().getString(R.string.s12_forecast);
		info11.ansFir = getResources().getString(R.string.forecast_select_xizhi);
		info11.ansSec = getResources().getString(R.string.forecast_select_buzhi);
		info11.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info11);
		ForecastInfo info12 = new ForecastInfo();
		info12.question = getResources().getString(R.string.s13_forecast);
		info12.ansFir = getResources().getString(R.string.forecast_select_zuo);
		info12.ansSec = getResources().getString(R.string.forecast_select_you);
		info12.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info12);
		ForecastInfo info13 = new ForecastInfo();
		info13.question = getResources().getString(R.string.s14_forecast);
		info13.ansFir = getResources().getString(R.string.forecast_select_meigaibian);
		info13.ansSec = getResources().getString(R.string.forecast_select_pang);
		info13.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info13);
		ForecastInfo info14 = new ForecastInfo();
		info14.question = getResources().getString(R.string.s15_forecast);
		info14.ansFir = getResources().getString(R.string.forecast_select_fubu);
		info14.ansSec = getResources().getString(R.string.forecast_select_pigu);
		info14.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info14);
		ForecastInfo info15 = new ForecastInfo();
		info15.question = getResources().getString(R.string.s16_forecast);
		info15.ansFir = getResources().getString(R.string.forecast_select_chongpei);
		info15.ansSec = getResources().getString(R.string.forecast_select_wuli);
		info15.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info15);
		ForecastInfo info16 = new ForecastInfo();
		info16.question = getResources().getString(R.string.s17_forecast);
		info16.ansFir = getResources().getString(R.string.forecast_select_pianjian);
		info16.ansSec = getResources().getString(R.string.forecast_select_piansuan);
		info16.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info16);
		ForecastInfo info17 = new ForecastInfo();
		info17.question = getResources().getString(R.string.s18_forecast);
		info17.ansFir = getResources().getString(R.string.forecast_select_qing);
		info17.ansSec = getResources().getString(R.string.forecast_select_buqing);
		info17.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info17);
		ForecastInfo info18 = new ForecastInfo();
		info18.question = getResources().getString(R.string.s19_forecast);
		info18.ansFir = getResources().getString(R.string.forecast_select_qianhoubai);
		info18.ansSec = getResources().getString(R.string.forecast_select_zuoyoubai);
		info18.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info18);
		ForecastInfo info19 = new ForecastInfo();
		info19.question = getResources().getString(R.string.s20_forecast);
		info19.ansFir = getResources().getString(R.string.forecast_select_16);
		info19.ansSec = getResources().getString(R.string.forecast_select_20);
		info19.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info19);
		ForecastInfo info20 = new ForecastInfo();
		info20.question = getResources().getString(R.string.s21_forecast);
		info20.ansFir = getResources().getString(R.string.forecast_select_zuo);
		info20.ansSec = getResources().getString(R.string.forecast_select_you);
		info20.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info20);
		ForecastInfo info21 = new ForecastInfo();
		info21.question = getResources().getString(R.string.s22_forecast);
		info21.ansFir = getResources().getString(R.string.forecast_select_quan);
		info21.ansSec = getResources().getString(R.string.forecast_select_zhengge);
		info21.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info21);
		ForecastInfo info22 = new ForecastInfo();
		info22.question = getResources().getString(R.string.s23_forecast);
		info22.ansFir = getResources().getString(R.string.forecast_select_qianbi);
		info22.ansSec = getResources().getString(R.string.forecast_select_houbi);
		info22.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info22);
		ForecastInfo info23 = new ForecastInfo();
		info23.question = getResources().getString(R.string.s24_forecast);
		info23.ansFir = getResources().getString(R.string.forecast_select_140);
		info23.ansSec = getResources().getString(R.string.forecast_select_150);
		info23.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info23);
		ForecastInfo info24 = new ForecastInfo();
		info24.question = getResources().getString(R.string.s25_forecast);
		info24.ansFir = getResources().getString(R.string.forecast_select_lianwai);
		info24.ansSec = getResources().getString(R.string.forecast_select_xiangma);
		info24.ansThi = getResources().getString(R.string.forecast_select_buzhidao);
		list.add(info24);
		return list;
	}

	@Override
	public void setLeftButton(Button button) {
	}

	@Override
	public void setRightButton(Button button) {
		button.setBackgroundResource(R.drawable.btn_comm1);
		button.setVisibility(View.VISIBLE);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MobclickAgent.onEvent(getBaseContext(), EventContants.carer, EventContants.gender_result);
				if (boy > girl && boy != 0) {
					boy = 0;
					girl = 0;
					clearInfo();
					dialog("您宝宝的性别可能为男宝宝");
					// Toast.makeText(this, "boy", Toast.LENGTH_SHORT).show();
				} else if (boy < girl) {
					boy = 0;
					girl = 0;
					clearInfo();
					dialog("您宝宝的性别可能为女宝宝");
				} else if (boy == 0 && girl == 0 || boy == girl) {
					dialog("对不起，根据您的条件无法预测生男生女！");
				}
			}
		});
	}

	@Override
	public String getTitleString() {
		return "生男生女预测";
	}

	@Override
	public int getBodyView() {
		return R.layout.forecast_activity;
	}
}
