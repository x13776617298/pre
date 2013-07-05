package com.babytree.apps.pregnancy.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.model.Discuz;
import com.babytree.apps.comm.ui.UserinfoNewActivity;
import com.babytree.apps.comm.ui.page.AbstractDataLoaderHandler;
import com.babytree.apps.comm.ui.page.AbstractPageableAdapter;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.view.EmojiTextView;
import com.babytree.apps.pregnancy.R;

public class PostReplyDiscuzListAdapter extends AbstractPageableAdapter<Base> {
	private BabytreeBitmapCache bitmapCache;// 缓存对象
	private Context mContext;

	public PostReplyDiscuzListAdapter(PullToRefreshListView listView, Context context, int loadingViewResourceId,
			int reloadViewResourceId, AbstractDataLoaderHandler<Base> handler) {
		super(listView, context, loadingViewResourceId, reloadViewResourceId, handler);
		this.mContext = context;
		// ---------------------缓存模块start--------------------------
		bitmapCache = BabytreeBitmapCache.create(context);
		// ---------------------缓存模块end----------------------------
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewCache viewCache;
		final Discuz bean = (Discuz) getItem(position);
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.forum_item, null);
			viewCache = new ViewCache(convertView);
			convertView.setTag(viewCache);
		} else {
			viewCache = (ViewCache) convertView.getTag();
		}
		ImageView is_jing = viewCache.getJingImg();
		if (bean.is_elite == 1) {
			is_jing.setVisibility(View.VISIBLE);
		} else {
			is_jing.setVisibility(View.GONE);
		}

		ImageView headImg = viewCache.getHeadImg();
		headImg.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, UserinfoNewActivity.class);
				Bundle bl = new Bundle();
				bl.putString("user_encode_id", bean.author_id);
				intent.putExtras(bl);
				mContext.startActivity(intent);
			}
		});
		String headUrl = bean.author_avatar;
		// @pengxh 关闭MD5
		ImageView imageView = viewCache.getHeadImg();
		imageView.setVisibility(View.VISIBLE);

		// ---------------------缓存模块start--------------------------
		bitmapCache.display(imageView, headUrl);
		// ---------------------缓存模块end----------------------------

		// Set Title Begin
		EmojiTextView title = viewCache.getTitle();
		String titleMessage = bean.title;

		if (bean.is_fav == 1) {
			titleMessage += "<img src=\"ic_picture\">";
		}
		if (bean.is_top == 1) {
			titleMessage += "<img src=\"ic_ding\">";
		}

		title.setEmojiText(titleMessage);
		// Set Title End
		TextView responseCount = viewCache.getResponseCount();
		responseCount.setText(String.valueOf(bean.response_count));

		TextView pvCount = viewCache.getPvCount();
		pvCount.setText(String.valueOf(bean.pv_count));

		TextView authorName = viewCache.getAuthorName();
		authorName.setText(bean.author_name);
		TextView lastResponseTs = viewCache.getLastResponseTs();
		lastResponseTs.setText(BabytreeUtil.formatTimestamp(bean.last_response_ts));
		convertView.setBackgroundResource(R.drawable.list_selector_background);
		return convertView;
	}

	static class ViewCache {
		private View baseView;

		private EmojiTextView title;

		private TextView response_count;

		private TextView pv_count;

		private TextView author_name;

		private ImageView img_head;

		private TextView last_response_ts;

		private LinearLayout title_bar;

		private LinearLayout layout_item;

		private TextView txt_message;

		private ImageView jing_img;

		public ViewCache(View view) {
			baseView = view;
		}

		public EmojiTextView getTitle() {
			if (title == null) {
				title = (EmojiTextView) baseView.findViewById(R.id.txt_title);
			}
			return title;
		}

		public LinearLayout getTitleBar() {
			if (title_bar == null) {
				title_bar = (LinearLayout) baseView.findViewById(R.id.title_bar);
			}
			return title_bar;
		}

		public LinearLayout getLayoutItem() {
			if (layout_item == null) {
				layout_item = (LinearLayout) baseView.findViewById(R.id.layout_item);
			}
			return layout_item;
		}

		public TextView getTxtMessage() {
			if (txt_message == null) {
				txt_message = (TextView) baseView.findViewById(R.id.txt_message);
			}
			return txt_message;
		}

		public TextView getResponseCount() {
			if (response_count == null) {
				response_count = (TextView) baseView.findViewById(R.id.txt_response_count);
			}
			return response_count;
		}

		public TextView getPvCount() {
			if (pv_count == null) {
				pv_count = (TextView) baseView.findViewById(R.id.txt_pv_count);
			}
			return pv_count;
		}

		public TextView getAuthorName() {
			if (author_name == null) {
				author_name = (TextView) baseView.findViewById(R.id.txt_author_name);
			}
			return author_name;
		}

		public TextView getLastResponseTs() {
			if (last_response_ts == null) {
				last_response_ts = (TextView) baseView.findViewById(R.id.txt_last_response_ts);
			}
			return last_response_ts;
		}

		public ImageView getJingImg() {
			if (jing_img == null) {
				jing_img = (ImageView) baseView.findViewById(R.id.jing_img);
			}
			return jing_img;
		}

		public ImageView getHeadImg() {
			if (img_head == null) {
				img_head = (ImageView) baseView.findViewById(R.id.iv_head);
			}
			return img_head;
		}

	}
}
