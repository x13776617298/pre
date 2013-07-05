
package com.babytree.apps.comm.ui;

import com.babytree.apps.comm.db.LocationDbAdapter;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;
import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
 * 选择医院所在地 省份
 * @author wangbingqi
 *
 */
public class LocationList3Activity extends BabytreeTitleAcitivty implements OnClickListener {

    private ListView mListView;

    private PregnancyApplication mApplication;

    private LocationDbAdapter mDbAdapter;

    private MAdapter mAdapter;

    private Cursor mCursor;
    
    private boolean isFromInit; 
    
    private boolean isFromAddHospital;
    
//	private Button btn_back;
	
//	private FrameLayout fl_title;
    
    public static void launch(Context context,boolean isFromInit){
		Intent intent = new Intent(context,LocationList3Activity.class);
		intent.putExtra("isFromInit", isFromInit);
		context.startActivity(intent);
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        btn_back = (Button) findViewById(R.id.btn_left);
//        btn_back.setOnClickListener(this);
//        fl_title = (FrameLayout)findViewById(R.id.title);
//        if(BabytreeUtil.isPregnancy(this)){
//			fl_title.setBackgroundResource(R.drawable.y_title_bg);
//			btn_back.setBackgroundResource(R.drawable.y_btn_back);
//		}
        
        isFromInit = getIntent().getBooleanExtra("isFromInit", false);
        isFromAddHospital = getIntent().getBooleanExtra("isFromAddHospital", false);
        mApplication = (PregnancyApplication)getApplication();
        mDbAdapter = mApplication.getLocationDbAdapter();
        mListView = (ListView)findViewById(R.id.list);
        
        String sql = "select * from china_location_utf8 where type='province'";
        mCursor = mDbAdapter.rawQuery(sql, null);


        mAdapter = new MAdapter(this, R.layout.hospital_list_item2, mCursor, new String[] {
            "longname"
        }, new int[] {
            R.id.title
        });

        mListView.setAdapter(mAdapter);
        mListView.setVisibility(View.VISIBLE);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            	mCursor.moveToPosition(position);
            	String name = mCursor.getString(mCursor.getColumnIndex("longname"));
            	Intent intent = new Intent(getApplicationContext(), LocationDetail3Activity.class);
                intent.putExtra("_id", id);
                intent.putExtra("name", name);
                intent.putExtra("isFromInit", isFromInit);
                intent.putExtra("isFromAddHospital", isFromAddHospital);
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
    public void onClick(View v) {
        if (v.getId() == R.id.btn_left) {
            finish();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
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
		return "选择医院所在地区";
	}

	@Override
	public int getBodyView() {
		// TODO Auto-generated method stub
		return R.layout.choice_location_activity;
	}

}
