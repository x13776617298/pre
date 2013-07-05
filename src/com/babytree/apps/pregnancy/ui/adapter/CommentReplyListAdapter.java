package com.babytree.apps.pregnancy.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.babytree.apps.biz.topic.TopicNewActivity;
import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.model.TopicComment;
import com.babytree.apps.comm.model.TopicReply;
import com.babytree.apps.comm.ui.category.PinnedHeaderListViewBean;
import com.babytree.apps.comm.ui.page.AbstractDataLoaderHandler;
import com.babytree.apps.comm.ui.page.AbstractPageableAdapter;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.pregnancy.R;

public class CommentReplyListAdapter extends AbstractPageableAdapter<Base> {
	private Context mContext;
	public List<Base> list = new ArrayList<Base>();

	public CommentReplyListAdapter(PullToRefreshListView listView, Context context, int loadingViewResourceId,
			int reloadViewResourceId, AbstractDataLoaderHandler<Base> handler) {
		super(listView, context, loadingViewResourceId, reloadViewResourceId, handler);
		this.mContext = context;
	}

	public CommentReplyListAdapter(PullToRefreshListView listView, Context context, int loadingViewResourceId,
			int reloadViewResourceId, AbstractDataLoaderHandler<Base> handler, ArrayList<Base> values) {
		super(listView, context, loadingViewResourceId, reloadViewResourceId, handler);
		this.mContext = context;
		this.list = values;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewCache viewCache;
		PinnedHeaderListViewBean bean = (PinnedHeaderListViewBean) getItem(position);
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.comment_reply_list_item, null);
			viewCache = new ViewCache(convertView);
			convertView.setTag(viewCache);
		} else {
			viewCache = (ViewCache) convertView.getTag();
		}

		LinearLayout mLayoutComment = viewCache.getLayoutComment();
		LinearLayout mLayoutReply = viewCache.getLayoutReply();
		if (bean.title.equals("2")) {
			mLayoutComment.setVisibility(View.VISIBLE);
			mLayoutReply.setVisibility(View.GONE);
			final TopicComment topicComment = (TopicComment) bean.getObject();
			TextView nickName = viewCache.getNickName();
			nickName.setText(topicComment.reply_user_nickname);
			TextView title = viewCache.getTitle();
			String titleStr = topicComment.reply_user_nickname + "   在话题  " + "<font color=\"#67c9fb\">"
					+ topicComment.topic_title + "</font>" + "中回复了你 ";
			title.setText(Html.fromHtml(titleStr));
			TextView content = viewCache.getContent();
			content.setText(topicComment.reply_user_content);
			TextView myContent = viewCache.getMyReply();
			myContent.setText(topicComment.my_reply_content);
			TextView time = viewCache.getReplyTime();
			time.setText(com.babytree.apps.comm.util.BabytreeUtil.formatTimestampForNotice(Long
					.parseLong(topicComment.reply_user_ts)));
			LinearLayout mLinearLayout = viewCache.getLayout();
			mLinearLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, TopicNewActivity.class);
					
					Bundle bundle = new Bundle();
					bundle.putInt(TopicNewActivity.BUNDLE_DISCUZ_ID, Integer.parseInt(topicComment.topic_id));
					bundle.putInt(TopicNewActivity.BUNDLE_PAGE, Integer.parseInt(topicComment.topic_reply_page));
					intent.putExtras(bundle);
					
					mContext.startActivity(intent);
				}
			});

		} else if (bean.title.equals("1")) {
			mLayoutComment.setVisibility(View.GONE);
			mLayoutReply.setVisibility(View.VISIBLE);
			final TopicReply topicReply = (TopicReply) bean.getObject();
			TextView topicTitle = viewCache.getTopicTitle();
			topicTitle.setText(topicReply.topic_title);
			TextView replyCount = viewCache.getTopicReplyCount();
			replyCount.setText("  有" + topicReply.topic_reply_unread_count + "条新回帖");

			TextView time = viewCache.getReplyTime();
			time.setText(com.babytree.apps.comm.util.BabytreeUtil.formatTimestampForNotice(Long
					.parseLong(topicReply.topic_last_reply_ts)));

			LinearLayout mLinearLayout = viewCache.getLayout();
			mLinearLayout.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(mContext, TopicNewActivity.class);
					
					Bundle bundle = new Bundle();
					bundle.putInt(TopicNewActivity.BUNDLE_DISCUZ_ID, Integer.parseInt(topicReply.topic_id));
					bundle.putInt(TopicNewActivity.BUNDLE_PAGE, Integer.parseInt(topicReply.topic_reply_page));
					intent.putExtras(bundle);
					
					mContext.startActivity(intent);
				}
			});
		}

		return convertView;
	}

	static class ViewCache {
		private View baseView;

		private TextView replyTime;

		private LinearLayout layoutComment, layoutReply, layout;

		private TextView nickName;

		private TextView title;

		private TextView content;

		private TextView myReply;

		private TextView topicTitle;

		private TextView topicReplyCount;

		public ViewCache(View view) {
			baseView = view;
		}

		public TextView getReplyTime() {
			if (replyTime == null) {
				replyTime = (TextView) baseView.findViewById(R.id.tv_reply_time);
			}
			return replyTime;
		}

		public TextView getTitle() {
			if (title == null) {
				title = (TextView) baseView.findViewById(R.id.tv_title);
			}
			return title;
		}

		public TextView getNickName() {
			if (nickName == null) {
				nickName = (TextView) baseView.findViewById(R.id.tv_name);
			}
			return nickName;
		}

		public TextView getContent() {
			if (content == null) {
				content = (TextView) baseView.findViewById(R.id.tv_content);
			}
			return content;
		}

		public TextView getMyReply() {
			if (myReply == null) {
				myReply = (TextView) baseView.findViewById(R.id.tv_my_reply);
			}
			return myReply;
		}

		public TextView getTopicTitle() {
			if (topicTitle == null) {
				topicTitle = (TextView) baseView.findViewById(R.id.tv_topic_title);
			}
			return topicTitle;
		}

		public TextView getTopicReplyCount() {
			if (topicReplyCount == null) {
				topicReplyCount = (TextView) baseView.findViewById(R.id.tv_topic_reply_count);
			}
			return topicReplyCount;
		}

		public LinearLayout getLayoutComment() {
			if (layoutComment == null) {
				layoutComment = (LinearLayout) baseView.findViewById(R.id.layout_comment);
			}
			return layoutComment;
		}

		public LinearLayout getLayoutReply() {
			if (layoutReply == null) {
				layoutReply = (LinearLayout) baseView.findViewById(R.id.layout_reply);
			}
			return layoutReply;
		}

		public LinearLayout getLayout() {
			if (layout == null) {
				layout = (LinearLayout) baseView.findViewById(R.id.layout);
			}
			return layout;
		}
	}
}
