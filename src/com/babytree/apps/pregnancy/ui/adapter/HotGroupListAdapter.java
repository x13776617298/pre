
package com.babytree.apps.pregnancy.ui.adapter;

import java.util.ArrayList;

import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.model.Group;
import com.babytree.apps.comm.ui.ForumActivity;
import com.babytree.apps.comm.ui.page.AbstractDataLoaderHandler;
import com.babytree.apps.comm.ui.page.AbstractPageableAdapter;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

public class HotGroupListAdapter extends AbstractPageableAdapter<Base> {
    private Context mContext;
    private ArrayList<Base> values;

	public HotGroupListAdapter(PullToRefreshListView listView, Context context,
            int loadingViewResourceId, int reloadViewResourceId,
            AbstractDataLoaderHandler<Base> handler) {
        super(listView, context, loadingViewResourceId, reloadViewResourceId, handler);
        this.mContext = context;
    }
    public HotGroupListAdapter(PullToRefreshListView listView, Context context,
            int loadingViewResourceId, int reloadViewResourceId,
            AbstractDataLoaderHandler<Base> handler,ArrayList<Base> values) {
        super(listView, context, loadingViewResourceId, reloadViewResourceId, handler);
        this.mContext = context;
        this.values = values;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewCache viewCache;
        Group bean = (Group)getItem(position);
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.hot_group_list_item, null);
            viewCache = new ViewCache(convertView);
            convertView.setTag(viewCache);
        } else {
            viewCache = (ViewCache)convertView.getTag();
        }
        TextView title = viewCache.getTitle();
        title.setText(bean.name);
        TextView content = viewCache.getContent();
        content.setText(bean.description);
        TextView topicCount = viewCache.getTopicCount();
        topicCount.setText(bean.discussion_count+"");
        LinearLayout mLinearLayout = viewCache.getLayout();
		mLinearLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		    if(position < 6){
	        MobclickAgent.onEvent(mContext, EventContants.tuijianquanzi+"_"+String.valueOf(position+1));
				}
		    Group item = (Group) values.get(position);
			Intent intent = new Intent(mContext, ForumActivity.class);
			intent.putExtra("group_id", item.group_id);
			intent.putExtra("name", item.name);
			mContext.startActivity(intent);
			}
		});
        return convertView;
    }

    static class ViewCache {
        private View baseView;

        private TextView title;

        private TextView content;
        
        private TextView topicCount;
        
        private LinearLayout layout;

        public ViewCache(View view) {
            baseView = view;
        }

        public TextView getTitle() {
            if (title == null) {
                title = (TextView)baseView.findViewById(R.id.tv_title);
            }
            return title;
        }

        public TextView getContent() {
            if (content == null) {
                content = (TextView)baseView.findViewById(R.id.tv_content);
            }
            return content;
        }
        
        public TextView getTopicCount() {
            if (topicCount == null) {
            	topicCount = (TextView)baseView.findViewById(R.id.tv_topic_count);
            }
            return topicCount;
        }
        
        public LinearLayout getLayout() {
			if (layout == null) {
				layout = (LinearLayout) baseView.findViewById(R.id.layout);
			}
			return layout;
		}
    }
}
