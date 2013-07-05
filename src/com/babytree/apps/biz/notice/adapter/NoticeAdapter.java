package com.babytree.apps.biz.notice.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.babytree.apps.biz.notice.model.UserMessageListBean;
import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.handmark.pulltorefresh.library.internal.BabyTreeBaseAdapter;

public class NoticeAdapter extends BabyTreeBaseAdapter<UserMessageListBean> {

	/**
	 * 缓存对象
	 */
	private BabytreeBitmapCache bitmapCache;

	private String mNickname;

	public NoticeAdapter(Context context) {
		super(context);

		bitmapCache = BabytreeBitmapCache.create(mContext);

		mNickname = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.NICKNAME, "");
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewholder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.notice_list, null);
			viewholder = new ViewHolder();
			viewholder.iv = (ImageView) convertView.findViewById(R.id.notice_imageview);
			viewholder.tv_num = (TextView) convertView.findViewById(R.id.notice_textview1);
			viewholder.tv_title = (TextView) convertView.findViewById(R.id.notice_textview2);
			viewholder.tv_msg = (TextView) convertView.findViewById(R.id.notice_textview3);
			convertView.setTag(viewholder);
		} else {
			viewholder = (ViewHolder) convertView.getTag();
		}

		UserMessageListBean bean = getItem(position);

		bitmapCache.display(viewholder.iv, bean.user_avatar);

		viewholder.tv_msg.setText(bean.content);
		if (bean.unread_count == 0) {
			viewholder.tv_num.setVisibility(View.INVISIBLE);
		} else if (bean.unread_count >= 99) {
			viewholder.tv_num.setText("new");
			viewholder.tv_num.setVisibility(View.VISIBLE);
		} else {
			viewholder.tv_num.setText(String.valueOf(bean.unread_count));
			viewholder.tv_num.setVisibility(View.VISIBLE);
		}
		String nickname = bean.nickname;
		// 判断是否是老公的昵称
		if (nickname.equals(mNickname + "88")) {
			nickname = "老公";
		}
		viewholder.tv_title.setText(nickname);
		return convertView;
	}

	static class ViewHolder {
		public ImageView iv;
		public TextView tv_num;
		public TextView tv_title;
		public TextView tv_msg;
	}

}
