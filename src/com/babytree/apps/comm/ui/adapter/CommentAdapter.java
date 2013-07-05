package com.babytree.apps.comm.ui.adapter;

import java.util.ArrayList;

import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.model.MikaComment;
import com.babytree.apps.comm.ui.MikaListDetailActivity;
import com.babytree.apps.comm.ui.page.AbstractDataLoaderHandler;
import com.babytree.apps.comm.ui.page.AbstractPageableAdapter;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.pregnancy.R;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentAdapter extends AbstractPageableAdapter<Base> {
	private Context mContext;
	private ArrayList<Base> values;

	private int[] images = { R.drawable.mika_list_item1,
			R.drawable.mika_list_item2, R.drawable.mika_list_item3,
			R.drawable.mika_list_item4, R.drawable.mika_list_item5 };

	public CommentAdapter(PullToRefreshListView listView, Context context,
			int loadingViewResourceId, int reloadViewResourceId,
			AbstractDataLoaderHandler<Base> handler) {
		super(listView, context, loadingViewResourceId, reloadViewResourceId,
				handler);
		this.mContext = context;
	}

	public CommentAdapter(PullToRefreshListView listView, Context context,
			int loadingViewResourceId, int reloadViewResourceId,
			AbstractDataLoaderHandler<Base> handler, ArrayList<Base> values) {
		super(listView, context, loadingViewResourceId, reloadViewResourceId,
				handler);
		this.mContext = context;
		this.values = values;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewCache viewCache;

		MikaComment bean = (MikaComment) getItem(position);
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.mika_list_item, null);
			viewCache = new ViewCache(convertView);
			convertView.setTag(viewCache);
		} else {
			viewCache = (ViewCache) convertView.getTag();
		}

		TextView head = viewCache.getHead();
		head.setText(bean.title);

		LinearLayout headlayout = viewCache.getHeadLayout();
		headlayout.setBackgroundDrawable(mContext.getResources().getDrawable(images[position % 5]));

		TextView age = viewCache.getAge();
		age.setText(bean.babyage);

		TextView authorName = viewCache.getAuthorName();
		authorName.setText(bean.nickname);

		TextView content = viewCache.getContent();
		content.setText(bean.comment);

		LinearLayout mLinearLayout = viewCache.getLayout();
		mLinearLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MikaComment comment = (MikaComment) values.get(position);
				Intent intent = new Intent(mContext,
						MikaListDetailActivity.class);
				intent.putExtra("content_id", comment.content_id);
				intent.putExtra("content_type", comment.content_type);
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

	static class ViewCache {
		private View baseView;

		private TextView head;

		private TextView babyage;

		private TextView content;

		private TextView author_name;

		private LinearLayout layout;

		private LinearLayout head_layout;

		public ViewCache(View view) {
			baseView = view;
		}

		public TextView getHead() {
			if (head == null) {
				head = (TextView) baseView.findViewById(R.id.tv_item_head);
			}
			return head;
		}

		public TextView getContent() {
			if (content == null) {
				content = (TextView) baseView
						.findViewById(R.id.tv_comment_content);
			}
			return content;
		}

		public TextView getAge() {
			if (babyage == null) {
				babyage = (TextView) baseView.findViewById(R.id.tv_babyage);
			}
			return babyage;
		}

		public TextView getAuthorName() {
			if (author_name == null) {
				author_name = (TextView) baseView.findViewById(R.id.tv_name);
			}
			return author_name;
		}

		public LinearLayout getLayout() {
			if (layout == null) {
				layout = (LinearLayout) baseView.findViewById(R.id.layout);
			}
			return layout;
		}

		public LinearLayout getHeadLayout() {
			if (head_layout == null) {
				head_layout = (LinearLayout) baseView
						.findViewById(R.id.head_layout);
			}
			return head_layout;
		}
	}
}
