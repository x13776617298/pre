package com.babytree.apps.biz.home.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.babytree.apps.biz.home.model.Notify;
import com.babytree.apps.comm.model.TopicComment;
import com.babytree.apps.comm.model.TopicReply;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.pregnancy.R;

/**
 * 首页菜单-通知列表适配器
 * 
 * @author pengxh
 */
public class HomeNotifyListAdapter extends BaseAdapter {
	private static final String TAG = "MenuAdapter";
	private static int DIGITAL_ZERO = 0;// 表示无数据。
	private static int DEFAULT_INIT_VALUE = -1;

	/**
	 * 标题颜色
	 */
	private String mTitleColor = "#ffffff";

	private Context mContext;

	/**
	 * 推荐列表数据
	 */
	private ArrayList<Notify> mNotifyList;

	public HomeNotifyListAdapter(Context context, ArrayList<Notify> notifys) {
		this.mContext = context;
		this.mNotifyList = notifys;
	}

	@Override
	public int getCount() {
		// int result = DIGITAL_ZERO;
		// if (mItemTitles != null && mItemTitles.length != DIGITAL_ZERO)
		// result = mItemTitles.length;
		// mCount = (result == DIGITAL_ZERO ? DEFAULT_INIT_VALUE : result);
		// return result;
		return mNotifyList.size();
	}

	@Override
	public Object getItem(int position) {
		return mNotifyList.get(position);
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.homepage_notify_list_item, null);
			holder = new ViewHolder();
			holder.title = (TextView) convertView.findViewById(R.id.home_list_title);
			holder.time = (TextView) convertView.findViewById(R.id.home_list_time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Notify notify = mNotifyList.get(position);
		String title = "";
		switch (notify.getType()) {
		case 1:// XX在XX回复了你
			title = mContext.getResources().getString(R.string.s_home_notify_title_type1);
			TopicReply reply = (TopicReply) notify.getData();
//			reply.user_unread_coun
			String p1 = BabytreeUtil.setHtmlColor(reply.topic_title, mTitleColor);
			int unReadMsg = Integer.parseInt(reply.topic_reply_unread_count);
			title = String.format(title, p1, unReadMsg);
			holder.title.setText(Html.fromHtml(title));
			holder.time.setText(BabytreeUtil.formatTimestamp(Long.parseLong(reply.topic_last_reply_ts)));
			break;
		case 2:// 话题XX有n条新回帖
			title = mContext.getResources().getString(R.string.s_home_notify_title_type2);
			TopicComment comment = (TopicComment) notify.getData();
//			comment.user_unread_coun
			String commentUser = BabytreeUtil.setHtmlColor(comment.reply_user_nickname, mTitleColor);
			String topic = BabytreeUtil.setHtmlColor(comment.topic_title, mTitleColor);
			title = String.format(title, commentUser, topic);
			holder.title.setText(Html.fromHtml(title));
			holder.time.setText(BabytreeUtil.formatTimestamp(Long.parseLong(comment.reply_user_ts)));
			break;

		default:
			break;
		}

		return convertView;
	}

	/**
	 * 获取R.Color中的颜色值
	 * 
	 * @param color
	 * @return
	 */
	public int getColor(int color) {
		return mContext.getResources().getColor(color);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	static class ViewHolder {
		TextView title;
		TextView time;
	}

}
