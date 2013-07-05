
package com.babytree.apps.comm.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.babytree.apps.comm.ctr.BabytreeDbController;
import com.babytree.apps.comm.ctr.LocationDbController;
import com.babytree.apps.comm.db.DbAdapter;
import com.babytree.apps.comm.db.LocationDbAdapter;
import com.babytree.apps.comm.model.Location;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.ui.category.PinnedHeaderListView;
import com.babytree.apps.comm.ui.category.PinnedHeaderListViewAdapter;
import com.babytree.apps.comm.ui.category.PinnedHeaderListViewArrayAdapter;
import com.babytree.apps.comm.ui.category.PinnedHeaderListViewBean;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.PregnancyApplication;

/**
 * 地区选择
 */
public class LocationActivity extends BabytreeActivity implements OnClickListener, OnItemClickListener {

    // 北京 上海 广州 深圳 天津 重庆 青岛 成都 南京 杭州
    private ArrayList<PinnedHeaderListViewBean> hotLocations = new ArrayList<PinnedHeaderListViewBean>();

    private PinnedHeaderListView mListView;

    private PinnedHeaderListViewArrayAdapter arrayAdapter;

    private MAdapter mAdapter;

    private PregnancyApplication mApplication;

    private LocationDbAdapter mLocationDbAdapter;

    private DbAdapter mDbAdapter;

    private LocationDbController mLocationDbController;

    private BabytreeDbController mDbController;

    private class MAdapter extends PinnedHeaderListViewAdapter {

        private PinnedHeaderListViewArrayAdapter mArrayAdapter;

        public MAdapter(PinnedHeaderListViewArrayAdapter arrayAdapter) {
            super(arrayAdapter);
            mArrayAdapter = arrayAdapter;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                view = View.inflate(getApplicationContext(), R.layout.location_item, null);
            }
            PinnedHeaderListViewBean currentItem = mArrayAdapter.items.get(position);
            if (currentItem != null) {
                final TextView header = (TextView)view.findViewById(R.id.txt_title);
                final TextView textView = (TextView)view.findViewById(R.id.txt_location);
                if (textView != null) {
                    Location bean = (Location)currentItem.item;
                    textView.setText(bean.name);
                }
                if (header != null) {
                    header.setText(currentItem.title);
                }
                int section = getSectionForPosition(position);
                if (getPositionForSection(section) == position) {
                    // 显示标题
                    view.findViewById(R.id.layout_title).setVisibility(View.VISIBLE);
                    header.setVisibility(View.VISIBLE);
                } else {
                    // 隐藏标题
                    view.findViewById(R.id.layout_title).setVisibility(View.GONE);
                    header.setVisibility(View.GONE);
                }

            }
            return view;
        }

        @Override
        public void configurePinnedHeader(View header, int position, int alpha) {
            int section = getSectionForPosition(position);
            String title = (String)getIndexer().getSections()[section];
            ((TextView)header.findViewById(R.id.txt_title)).setText(title);

        }
    }

    private Button btnLeft;
    private FrameLayout layoutTitle;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.location_activity);
        btnLeft=(Button) findViewById(R.id.btn_left);
        btnLeft.setOnClickListener(this);
        layoutTitle=(FrameLayout)findViewById(R.id.title);
        if(com.babytree.apps.comm.util.BabytreeUtil.isPregnancy(this)){
        	layoutTitle.setBackgroundResource(R.drawable.y_title_bg);
			btnLeft.setBackgroundResource(R.drawable.y_btn_back);
		}
        mListView = (PinnedHeaderListView)findViewById(R.id.list);

        mApplication = (PregnancyApplication)getApplication();
        mLocationDbAdapter = mApplication.getLocationDbAdapter();
        mLocationDbController = new LocationDbController(mLocationDbAdapter);
        mDbAdapter = mApplication.getDbAdapter();
        mDbController = new BabytreeDbController(mDbAdapter);

        initData();

        arrayAdapter = new PinnedHeaderListViewArrayAdapter(this, R.id.txt_location, hotLocations);

        mAdapter = new MAdapter(arrayAdapter);

        mListView.setAdapter(mAdapter);
        mListView.setOnScrollListener(mAdapter);

        mListView.setPinnedHeaderView(getLayoutInflater().inflate(R.layout.location_header,
                mListView, false));

        mListView.setOnItemClickListener(this);

    }

    private static final String TITLE_ALAWY = "您经常使用的";

    private static final String TITLE_HOT = "热门地区";

    private static final String TYPE_PROVINCE = "province";

    private static final String TYPE_CITY = "city";

    private void initData() {
        ArrayList<Location> locationList = mDbController.getLocationList();
        for (Location location : locationList) {
            hotLocations.add(new PinnedHeaderListViewBean(new Location(location._id, location.name,
                    location.type), TITLE_ALAWY));
        }

        // HOT
        hotLocations.add(new PinnedHeaderListViewBean(new Location(1100, "北京", TYPE_PROVINCE),
                TITLE_HOT));
        hotLocations.add(new PinnedHeaderListViewBean(new Location(3100, "上海", TYPE_PROVINCE),
                TITLE_HOT));
        hotLocations.add(new PinnedHeaderListViewBean(new Location(4401, "广州", TYPE_CITY),
                TITLE_HOT));
        hotLocations.add(new PinnedHeaderListViewBean(new Location(4403, "深圳", TYPE_CITY),
                TITLE_HOT));
        hotLocations.add(new PinnedHeaderListViewBean(new Location(1200, "天津", TYPE_PROVINCE),
                TITLE_HOT));
        hotLocations.add(new PinnedHeaderListViewBean(new Location(5000, "重庆", TYPE_PROVINCE),
                TITLE_HOT));
        hotLocations.add(new PinnedHeaderListViewBean(new Location(3702, "青岛", TYPE_CITY),
                TITLE_HOT));
        hotLocations.add(new PinnedHeaderListViewBean(new Location(5101, "成都", TYPE_CITY),
                TITLE_HOT));
        hotLocations.add(new PinnedHeaderListViewBean(new Location(3201, "南京", TYPE_CITY),
                TITLE_HOT));
        hotLocations.add(new PinnedHeaderListViewBean(new Location(3301, "杭州", TYPE_CITY),
                TITLE_HOT));
        hotLocations.add(new PinnedHeaderListViewBean(new Location(0, "更多地区", TYPE_PROVINCE),
                TITLE_HOT));
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_left) {
            finish();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
    	super.onItemClick(parent, view, position, id);

        Location item = (Location)hotLocations.get(position).item;

        if (item._id == 0) {
            // All
            Intent intent = new Intent(getApplicationContext(), LocationListActivity.class);
            startActivityForResult(intent, 0);
        } else {
            // 增加访问记录
            mDbController.addLocation(item);
            Intent intent = new Intent(getApplicationContext(), ForumActivity.class);
            intent.putExtra("_id", item._id);
            intent.putExtra("type", item.type);
            intent.putExtra("name", item.name);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            // 增加访问记录
            int _id = data.getIntExtra("_id", 0);
            Location location = mLocationDbController.getLocationById(_id);
            mDbController.addLocation(location);
            setResult(resultCode, data);
            finish();
        }
    }

}