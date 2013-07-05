package com.babytree.apps.pregnancy.ui.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.model.SessionMessageListBean;
import com.babytree.apps.comm.ui.page.AbstractDataLoaderHandler;
import com.babytree.apps.comm.ui.page.AbstractPageableAdapter;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.comm.util.Md5Util;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

/**
 * 消息页面适配器
 * 
 * @author Administrator
 * 
 */
public class AllMessageListAdapter extends AbstractPageableAdapter<Base> {
	private Context mContext;

	public static interface IMsgViewType {
		int IMVT_COM_MSG = 0;
		int IMVT_TO_MSG = 1;
	}

	public Object getItem(int position) {
		return list.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public int getItemViewType(int position) {
		// TODO Auto-generated method stub
		SessionMessageListBean entity = (SessionMessageListBean) list.get(position);

		if (otherUserId.equalsIgnoreCase(entity.user_encode_id)&&!otherUserId.equalsIgnoreCase(userId)) {
			return IMsgViewType.IMVT_COM_MSG;
		} else {
			return IMsgViewType.IMVT_TO_MSG;
		}

	}

	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	private List<Base> list;
	/**
	 * 对方的ID
	 */
	private String otherUserId;
	private SesseonMessageListener listener;
	
	/**
	 * 自己的ID
	 */
	private String userId;
	public AllMessageListAdapter(PullToRefreshListView listView, Context context, int loadingViewResourceId,
			int reloadViewResourceId, AbstractDataLoaderHandler<Base> handler, List<Base> list, String otherUserId) {
		super(listView, context, loadingViewResourceId, reloadViewResourceId, handler);
		this.mContext = context;
		this.list = list;
		this.otherUserId = otherUserId;
		userId=SharedPreferencesUtil.getStringValue(context, ShareKeys.USER_ENCODE_ID);
	}

	/**
	 * 自己的图片地址
	 */
	private String mainUrl = "";
	/**
	 * 自己的ID
	 */
	private String mainUserName = "";

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final SessionMessageListBean entity = (SessionMessageListBean) list.get(position);
		boolean isComMsg = true;
		if (otherUserId.equalsIgnoreCase(entity.user_encode_id)&&!otherUserId.equalsIgnoreCase(userId)) {
			
			// 居左
			isComMsg = true;
		} else {
			// 居右
			isComMsg = false;
			// 如果是自己
			if (entity.user_avatar.equalsIgnoreCase("")) {
				entity.user_avatar = mainUrl;
			} else {
				mainUrl = entity.user_avatar;
			}
			if (entity.user_encode_id.equalsIgnoreCase("")) {
				entity.user_encode_id = mainUserName;
			} else {
				mainUserName = entity.user_encode_id;
			}
		}

		ViewHolder viewHolder = null;
		if (convertView == null) {
			if (isComMsg) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.chatting_item_msg_text_left, null);
			} else {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.chatting_item_msg_text_right, null);
			}

			viewHolder = new ViewHolder();
			viewHolder.tvSendTime = (TextView) convertView.findViewById(R.id.tv_sendtime);
			viewHolder.tvUserName = (TextView) convertView.findViewById(R.id.tv_username);
			viewHolder.tvContent = (TextView) convertView.findViewById(R.id.tv_chatcontent);
			viewHolder.iv = (ImageView) convertView.findViewById(R.id.iv_userhead);
			viewHolder.isComMsg = isComMsg;

			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}

		viewHolder.tvSendTime.setText(entity.last_ts);
		viewHolder.tvUserName.setText(entity.nickname);
		viewHolder.tvUserName.setVisibility(View.INVISIBLE);
		viewHolder.tvContent.setText(entity.content);
		viewHolder.tvContent.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {

				if (listener != null) {
					listener.onClickTextView(entity);
				}

				return true;
			}
		});

		viewHolder.iv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (listener != null) {
					listener.onClickImageView(entity.user_encode_id);
				}
			}
		});

		try {
			setImage(viewHolder.iv, entity.user_avatar, mContext);
		} catch (Exception e) {
			// TODO: handle exception
		}

		return convertView;
	}

	static class ViewHolder {
		public ImageView iv;
		public TextView tvSendTime;
		public TextView tvUserName;
		public TextView tvContent;
		public boolean isComMsg = true;
	}
	
	public static void setImage(final ImageView iv, String url, Context context) {
		
		// ---------------------缓存模块start--------------------------
		BabytreeBitmapCache bitmapCache = BabytreeBitmapCache.create(context);
		// ---------------------缓存模块end----------------------------
		// @pengxh 关闭MD5
		// viewCache.getHeadImg().setTag(Md5Util.md5(headUrl));
		iv.setTag(Md5Util.md5(url));
		iv.setVisibility(View.VISIBLE);
		// ---------------------缓存模块start--------------------------
		bitmapCache
				.display(iv, url);
		// ---------------------缓存模块end----------------------------
	}

	public void setListener(SesseonMessageListener listener) {
		this.listener = listener;
	}

}
