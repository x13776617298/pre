
package com.babytree.apps.comm.ui;

import com.babytree.apps.comm.db.LocationDbAdapter;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Canvas;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * 地区选择  选择市
 * @author wangbingqi
 *
 */
public class LocationDetailActivity extends BabytreeTitleAcitivty implements OnClickListener {

    private ListView mListView;

    private PregnancyApplication mApplication;

    private LocationDbAdapter mDbAdapter;

    private MAdapter mAdapter;

    private Cursor mCursor;
    
    private long mId;
    private String name;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mId = getIntent().getLongExtra("_id", 0);
        name = getIntent().getStringExtra("name");
        mApplication = (PregnancyApplication)getApplication();
        mDbAdapter = mApplication.getLocationDbAdapter();
        mListView = (ListView)findViewById(R.id.list);
        
        String sql = "select * from china_location_utf8 where province="+mId+" order by dropdownorder asc";
        mCursor = mDbAdapter.rawQuery(sql, null);

        mAdapter = new MAdapter(this, R.layout.location_detail_item, mCursor, new String[] {
            "name"
        }, new int[] {
            R.id.txt_name
        });

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), LocationListActivity.class);
                mCursor.moveToPosition(position);
                String type = mCursor.getString(mCursor.getColumnIndex("type"));
                int _id = mCursor.getInt(mCursor.getColumnIndex("_id"));
                String name11 = mCursor.getString(mCursor.getColumnIndex("name"));
                intent.putExtra("_id", _id);
                intent.putExtra("type", type);
                intent.putExtra("name", name + " " + name11);
                intent.putExtra("province", name);
                intent.putExtra("city", name11);
                setResult(10, intent);
                finish();
            }
        });
        
    }

    private class MAdapter extends SimpleCursorAdapter {

        public MAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
        }

    }
	@Override
	public void setLeftButton(Button button) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setRightButton(Button button) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getTitleString() {
		// TODO Auto-generated method stub
		return "地区选择";
	}
	@Override
	public int getBodyView() {
		// TODO Auto-generated method stub
		return R.layout.location_detail_activity;
	}

}
