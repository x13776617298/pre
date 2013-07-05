
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
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * 选择省份
 * @author wangbingqi
 *
 */
public class LocationListActivity extends BabytreeTitleAcitivty implements OnClickListener {

    private ListView mListView;

    private PregnancyApplication mApplication;

    private LocationDbAdapter mDbAdapter;

    private MAdapter mAdapter;

    private Cursor mCursor;
    private String province;
//    private Button btnLeft;
//    private FrameLayout layoutTitle;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApplication = (PregnancyApplication)getApplication();
        mDbAdapter = mApplication.getLocationDbAdapter();
        mListView = (ListView)findViewById(R.id.list);
        
        String sql = "select * from china_location_utf8 where type='province' order by cast([order] as int) asc";
        mCursor = mDbAdapter.rawQuery(sql, null);

        
        mAdapter = new MAdapter(this, R.layout.location_list_item, mCursor, new String[] {
            "name"
        }, new int[] {
            R.id.txt_name
        });

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	mCursor.moveToPosition(position);
            	String name = mCursor.getString(mCursor.getColumnIndex("name"));
            	
            	Intent intent = new Intent(getApplicationContext(), LocationDetailActivity.class);
                intent.putExtra("_id", id);
                intent.putExtra("name", name);
                startActivityForResult(intent, 0);
            }
        });

    }

    private class MAdapter extends SimpleCursorAdapter {

        public MAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
        }

    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 10) {
            setResult(resultCode, data);
            finish();
        }
    }
 // ==========UMENG Begin===========
    @Override
	protected void onDestroy() {
		if(mCursor!=null){
			mCursor.close();
		}
		super.onDestroy();
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
		return R.layout.location_list_activity;
	}

}
