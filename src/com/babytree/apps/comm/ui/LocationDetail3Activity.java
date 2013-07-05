
package com.babytree.apps.comm.ui;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.db.LocationDbAdapter;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;

/**
 * 选择医院所在地 地区
 * @author wangbingqi
 *
 */
public class LocationDetail3Activity extends BabytreeTitleAcitivty implements OnClickListener {

    private ListView mListView;

    private PregnancyApplication mApplication;

    private LocationDbAdapter mDbAdapter;

    private MAdapter mAdapter;

    private Cursor mCursor;
    
    private long mId;
    private String name;
    private boolean isFromInit;
    private boolean isFromAddHospital;
    
//	private Button btn_back;
	
//	private FrameLayout fl_title;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.choice_location_activity);
//        btn_back = (Button) findViewById(R.id.btn_left);
//        btn_back.setOnClickListener(this);
//        fl_title = (FrameLayout)findViewById(R.id.title);
//        if(BabytreeUtil.isPregnancy(this)){
//			fl_title.setBackgroundResource(R.drawable.y_title_bg);
//			btn_back.setBackgroundResource(R.drawable.y_btn_back);
//		}
//        TextView title = (TextView) findViewById(R.id.txt_center);
        
        mId = getIntent().getLongExtra("_id", 0);
        name = getIntent().getStringExtra("name");
//        title.setText(name);
        setTitleString(name);
        isFromInit = getIntent().getBooleanExtra("isFromInit", false);
        isFromAddHospital = getIntent().getBooleanExtra("isFromAddHospital", false);
        mApplication = (PregnancyApplication)getApplication();
        mDbAdapter = mApplication.getLocationDbAdapter();
        mListView = (ListView)findViewById(R.id.list);
        
        String sql = "select * from china_location_utf8 where province="+mId+"";
        mCursor = mDbAdapter.rawQuery(sql, null);

        mAdapter = new MAdapter(this, R.layout.hospital_list_item2, mCursor, new String[] {
            "name"
        }, new int[] {
            R.id.title
        });

        mListView.setAdapter(mAdapter);

        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mCursor.moveToPosition(position);
                String name11 = mCursor.getString(mCursor.getColumnIndex("name"));
                String cityId = mCursor.getString(mCursor.getColumnIndex("_id"));
                if(isFromInit){
        			Intent intent = new Intent(LocationDetail3Activity.this,HospitalListActivity.class);
        			intent.putExtra("key", name11);
        			intent.putExtra("from_location", true);
        			startActivityForResult(intent, 10);
        			setResult(RESULT_OK);
        		}else if(isFromAddHospital){
        			SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.ADD_HOSPITAL_PROVINCE, name);
        			SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.ADD_HOSPITAL_CITY_CODE, cityId);
        			SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.ADD_HOSPITAL_CITY, name11);
        			setResult(RESULT_OK);
        			finish();
        		}else{
        			Intent intent = new Intent(LocationDetail3Activity.this,HospitalsInfoListActivity.class);
        			intent.putExtra("key", name11);
//        			intent.putExtra("fromLocationACT", "true");
        			intent.putExtra("province_id", mId);
        			intent.putExtra("city_id", cityId);
        			startActivityForResult(intent, 11);
        			setResult(RESULT_OK);
        		}
                
            }
        });

    }

    private class MAdapter extends SimpleCursorAdapter {

        public MAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
            super(context, layout, c, from, to);
        }

    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 11:
			if (resultCode == RESULT_OK) {
				finish();
			}
			break;
		case 10:
			if (resultCode == RESULT_OK) {
				finish();
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
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
		return null;
	}
	@Override
	public int getBodyView() {
		// TODO Auto-generated method stub
		return R.layout.choice_location_activity;
	}
    

}
