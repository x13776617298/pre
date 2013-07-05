
package com.babytree.apps.comm.ui;

import com.babytree.apps.pregnancy.R;
import com.babytree.apps.comm.config.EventContants;
import com.umeng.analytics.MobclickAgent;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;

/**
 * 交流首页
 */
public class ForumTabActivity extends TabActivity implements OnTabChangeListener {

    private TabHost mTabHost;

    private TabWidget mTabWidget;

    private LayoutInflater mInflater;

    private static final int TAB_1 = 0;

    private static final int TAB_2 = 1;

    private static final int TAB_3 = 2;

    private static final int TAB_4 = 3;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forum_tab_activity);

        mTabHost = getTabHost();
        mTabHost.setup(getLocalActivityManager());
        mTabWidget = mTabHost.getTabWidget();

        mInflater = LayoutInflater.from(this);

        setTab1();
        setTab2();
        setTab3();
        setTab4();
        mTabHost.setOnTabChangedListener(this);
        setTab(TAB_1, true);
    }

    private void setTab1() {
        View view = mInflater.inflate(R.layout.forum_tab_indicator, null);
        ((ImageView)view.findViewById(R.id.tab_icon)).setBackgroundResource(R.drawable.icon_wodequanzi);
        ((TextView)view.findViewById(R.id.tab_label)).setText(getResources().getString(
                R.string.tab_forum_1));
        Intent newsList = new Intent(this, ForumActivity.class);
        TabSpec mTabSpec1 = mTabHost.newTabSpec(getResources().getString(R.string.tab_forum_1));
        mTabSpec1.setIndicator(view);
        mTabSpec1.setContent(newsList);
        mTabHost.addTab(mTabSpec1);
    }

    private void setTab2() {
        View view = mInflater.inflate(R.layout.forum_tab_indicator, null);
        ((ImageView)view.findViewById(R.id.tab_icon)).setBackgroundResource(R.drawable.icon_allquanzi);
        ((TextView)view.findViewById(R.id.tab_label)).setText(getResources().getString(
                R.string.tab_forum_2));
        Intent newsList = new Intent(this, GroupActivity.class);
        TabSpec mTabSpec2 = mTabHost.newTabSpec(getResources().getString(R.string.tab_forum_2));
        mTabSpec2.setIndicator(view);
        mTabSpec2.setContent(newsList);
        mTabHost.addTab(mTabSpec2);
    }

    private void setTab3() {
    	   View view = mInflater.inflate(R.layout.forum_tab_indicator, null);
           ((ImageView)view.findViewById(R.id.tab_icon)).setBackgroundResource(R.drawable.ic_tuijiantab_normal);
           ((TextView)view.findViewById(R.id.tab_label)).setText(getResources().getString(
                   R.string.tab_forum_4));
           Intent newsList = new Intent(this, HotGroupActivity.class);
           TabSpec mTabSpec3 = mTabHost.newTabSpec(getResources().getString(R.string.tab_forum_4));
           mTabSpec3.setIndicator(view);
           mTabSpec3.setContent(newsList);
           mTabHost.addTab(mTabSpec3);
    }
    
    private void setTab4() {
        View view = mInflater.inflate(R.layout.forum_tab_indicator, null);
        ((ImageView)view.findViewById(R.id.tab_icon)).setBackgroundResource(R.drawable.icon_quzisearch_normal);
        ((TextView)view.findViewById(R.id.tab_label)).setText(getResources().getString(
                R.string.tab_forum_3));
        Intent newsList = new Intent(this, SearchActivity.class);
        TabSpec mTabSpec4 = mTabHost.newTabSpec(getResources().getString(R.string.tab_forum_3));
        mTabSpec4.setIndicator(view);
        mTabSpec4.setContent(newsList);
        mTabHost.addTab(mTabSpec4);
    }

    private void setTab(int id, boolean flag) {
        switch (id) {
            case TAB_1:
                mTabWidget.getChildAt(TAB_1).setBackgroundResource(R.drawable.ic_alltab_click);
                mTabWidget.getChildAt(TAB_2).setBackgroundResource(R.drawable.ic_qiansange_normal);
                mTabWidget.getChildAt(TAB_3).setBackgroundResource(R.drawable.ic_qiansange_normal);
                mTabWidget.getChildAt(TAB_4).setBackgroundResource(R.drawable.ic_forthtab_normal);
                ((ImageView) mTabWidget.getChildAt(TAB_1).findViewById(
						R.id.tab_icon)).setBackgroundResource(R.drawable.icon_wodequanzi_press);
                ((ImageView) mTabWidget.getChildAt(TAB_2).findViewById(
						R.id.tab_icon)).setBackgroundResource(R.drawable.icon_allquanzi);
                ((ImageView) mTabWidget.getChildAt(TAB_3).findViewById(
						R.id.tab_icon)).setBackgroundResource(R.drawable.ic_tuijiantab_normal);
                ((ImageView) mTabWidget.getChildAt(TAB_4).findViewById(
						R.id.tab_icon)).setBackgroundResource(R.drawable.icon_quzisearch_normal);
                break;
            case TAB_2:
                mTabWidget.getChildAt(TAB_1).setBackgroundResource(R.drawable.ic_qiansange_normal);
                mTabWidget.getChildAt(TAB_2).setBackgroundResource(R.drawable.ic_alltab_click);
                mTabWidget.getChildAt(TAB_3).setBackgroundResource(R.drawable.ic_qiansange_normal);
                mTabWidget.getChildAt(TAB_4).setBackgroundResource(R.drawable.ic_forthtab_normal);
                ((ImageView) mTabWidget.getChildAt(TAB_1).findViewById(
						R.id.tab_icon)).setBackgroundResource(R.drawable.icon_wodequanzi);
                ((ImageView) mTabWidget.getChildAt(TAB_2).findViewById(
						R.id.tab_icon)).setBackgroundResource(R.drawable.icon_allquanzi_press);
                ((ImageView) mTabWidget.getChildAt(TAB_3).findViewById(
						R.id.tab_icon)).setBackgroundResource(R.drawable.ic_tuijiantab_normal);
                ((ImageView) mTabWidget.getChildAt(TAB_4).findViewById(
						R.id.tab_icon)).setBackgroundResource(R.drawable.icon_quzisearch_normal);
                break;
            case TAB_3:
                mTabWidget.getChildAt(TAB_1).setBackgroundResource(R.drawable.ic_qiansange_normal);
                mTabWidget.getChildAt(TAB_2).setBackgroundResource(R.drawable.ic_qiansange_normal);
                mTabWidget.getChildAt(TAB_3).setBackgroundResource(R.drawable.ic_alltab_click);
                mTabWidget.getChildAt(TAB_4).setBackgroundResource(R.drawable.ic_forthtab_normal);
                ((ImageView) mTabWidget.getChildAt(TAB_1).findViewById(
						R.id.tab_icon)).setBackgroundResource(R.drawable.icon_wodequanzi);
                ((ImageView) mTabWidget.getChildAt(TAB_2).findViewById(
						R.id.tab_icon)).setBackgroundResource(R.drawable.icon_allquanzi);
                ((ImageView) mTabWidget.getChildAt(TAB_3).findViewById(
						R.id.tab_icon)).setBackgroundResource(R.drawable.icon_tuijiantab_click);
                ((ImageView) mTabWidget.getChildAt(TAB_4).findViewById(
						R.id.tab_icon)).setBackgroundResource(R.drawable.icon_quzisearch_normal);
                 break;
            case TAB_4:
                mTabWidget.getChildAt(TAB_1).setBackgroundResource(R.drawable.ic_qiansange_normal);
                mTabWidget.getChildAt(TAB_2).setBackgroundResource(R.drawable.ic_qiansange_normal);
                mTabWidget.getChildAt(TAB_3).setBackgroundResource(R.drawable.ic_qiansange_normal);
                mTabWidget.getChildAt(TAB_4).setBackgroundResource(R.drawable.ic_alltab_click);
                ((ImageView) mTabWidget.getChildAt(TAB_1).findViewById(
						R.id.tab_icon)).setBackgroundResource(R.drawable.icon_wodequanzi);
                ((ImageView) mTabWidget.getChildAt(TAB_2).findViewById(
						R.id.tab_icon)).setBackgroundResource(R.drawable.icon_allquanzi);
                ((ImageView) mTabWidget.getChildAt(TAB_3).findViewById(
						R.id.tab_icon)).setBackgroundResource(R.drawable.ic_tuijiantab_normal);
                ((ImageView) mTabWidget.getChildAt(TAB_4).findViewById(
						R.id.tab_icon)).setBackgroundResource(R.drawable.icon_quzisearch_press);
                break;
            default:
                break;
        }
    }

    @Override
    public void onTabChanged(String tabId) {
        if (tabId.equalsIgnoreCase(getResources().getString(R.string.tab_forum_1))) {
            // Umeng Evert
            MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.com_my_group);
            setTab(TAB_1, true);
        } else if (tabId.equalsIgnoreCase(getResources().getString(R.string.tab_forum_2))) {
            // Umeng Evert
            MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.com_all);
            setTab(TAB_2, true);
        } else if (tabId.equalsIgnoreCase(getResources().getString(R.string.tab_forum_3))) {
            // Umeng Evert
            setTab(TAB_4, true);
        } else if (tabId.equalsIgnoreCase(getResources().getString(R.string.tab_forum_4))) {
            // Umeng Evert
            setTab(TAB_3, true);
        } 
    }
}
