
package com.babytree.apps.pregnancy.ui;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;

import com.babytree.apps.biz.topic.TopicNewActivity;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.model.Discuz;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.adapter.PostReplyDiscuzListAdapter;
import com.babytree.apps.pregnancy.ui.handler.PostReplyDiscuzListHandler;

/**
 * 我发表的帖子和我回复的帖子
 */
public class PostReplyDiscuzListActivity extends BabytreeActivity implements OnRefreshListener,
        OnItemClickListener, OnClickListener {

    private PullToRefreshListView mListView;

    private PostReplyDiscuzListHandler mHandler;

    private PostReplyDiscuzListAdapter mAdapter;

    private String mLoginString;

    private String mUserEncodeId;

    private String mFlag;

    private String mType;

    private TextView mTxtTitle;
    
    private long mCurrentTime = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_reply_discuz_list_activity);

        mListView = (PullToRefreshListView)findViewById(R.id.list);

        mType = getIntent().getStringExtra("type");
        mFlag = getIntent().getStringExtra("flag");
        mUserEncodeId = getIntent().getStringExtra("user_encode_id");
        mLoginString = SharedPreferencesUtil.getStringValue(this, ShareKeys.LOGIN_STRING);
        mTxtTitle = (TextView)findViewById(R.id.txt_center);
        if ("mine".equals(mFlag)) {
            if ("post".equals(mType)) {
                mTxtTitle.setText("我的发表");
            } else if ("reply".equals(mType)) {
                mTxtTitle.setText("我的回复");
            }
            mHandler = new PostReplyDiscuzListHandler(this, null, mLoginString, mType);
        } else if ("userinfo".equals(mFlag)) {
            if ("post".equals(mType)) {
                mTxtTitle.setText("发表列表");
            } else if ("reply".equals(mType)) {
                mTxtTitle.setText("回复列表");
            }
            mHandler = new PostReplyDiscuzListHandler(this, mUserEncodeId, null, mType);
        }

        mAdapter = new PostReplyDiscuzListAdapter(mListView, this, R.layout.loading,
                R.layout.reloading, mHandler);
        mListView.getRefreshableView().setAdapter(mAdapter);
        mListView.setOnScrollListener(mAdapter);
        mListView.setOnRefreshListener(this);
        mListView.getRefreshableView().setOnItemClickListener(this);

        findViewById(R.id.btn_left).setOnClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        ArrayList<Base> values = mHandler.getValues();
        Discuz discuz = (Discuz)values.get(position - 1);
		Intent intent = new Intent(getBaseContext(), TopicNewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(TopicNewActivity.BUNDLE_DISCUZ_ID, discuz.discuz_id);
		intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onRefresh() {
        mHandler.refershTop(System.currentTimeMillis());
    }

    @Override
    public void onClick(View v) {
    	
    	super.onClick(v);

        if (v.getId() == R.id.btn_left) {
            finish();
        }
    }
}
