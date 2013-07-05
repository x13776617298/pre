package com.babytree.apps.biz.topic.adpter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.babytree.apps.biz.father.util.Utils;
import com.babytree.apps.biz.topic.TopicNewActivity;
import com.babytree.apps.biz.topic.model.ANode;
import com.babytree.apps.biz.topic.model.ImgNode;
import com.babytree.apps.biz.topic.model.Node;
import com.babytree.apps.biz.topic.model.TextNode;
import com.babytree.apps.biz.topic.model.n.ReplayFooterNode;
import com.babytree.apps.biz.topic.model.n.ReplayHeaderNode;
import com.babytree.apps.biz.topic.view.ViewUtil;
import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.bitmap.core.BitmapDisplayConfig;
import com.babytree.apps.comm.bitmap.core.BitmapDisplayConfig.AnimationType;
import com.babytree.apps.comm.ui.activity.BabyTreeWebviewActivity;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.view.EmojiTextView;
import com.babytree.apps.pregnancy.R;
import com.handmark.pulltorefresh.library.internal.BabyTreeBaseAdapter;

public class TopicBaseAdapter<T> extends BabyTreeBaseAdapter<T> {

	private Context mContext;
	private BabytreeBitmapCache bitmapCache;
	private BitmapDisplayConfig bitmapDisplayConfig;

	public TopicBaseAdapter(Context context) {
		super(context);
		this.mContext = context;

		bitmapCache = BabytreeBitmapCache.create(mContext);
		bitmapDisplayConfig = new BitmapDisplayConfig();
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.topic_img_empty);
		bitmapDisplayConfig.setLoadingBitmap(bitmap);
		bitmapDisplayConfig.setLoadfailBitmap(bitmap);

		int px = Utils.dip2px(mContext, ViewUtil.IMAGE_DIP);
		bitmapDisplayConfig.setBitmapHeight(px);
		bitmapDisplayConfig.setBitmapWidth(px);

		bitmapDisplayConfig.setAnimationType(AnimationType.fadeIn);
	}

	public BitmapDisplayConfig getBitmapDisplayConfig() {
		return bitmapDisplayConfig;
	}

	@Override
	public int getItemViewType(int position) {
		Node node = (Node) getItem(position);
		if (node.tag.equalsIgnoreCase("text")) {
			return 1;
		} else if (node.tag.equalsIgnoreCase("img")) {
			return 2;
		} else if (node.tag.equalsIgnoreCase("a")) {
			return 3;
		} else if (node.tag.equalsIgnoreCase("reply_header")) {
			return 4;
		} else if (node.tag.equalsIgnoreCase("reply_footer")) {
			return 5;
		} else {
			return 6;
		}
	};

	public int getViewTypeCount() {
		return 6;
	};

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		int type = getItemViewType(position);
		// 取数据
		if (convertView == null) {
			viewHolder = new ViewHolder();
			switch (type) {
			case 1:
				// 文本
				convertView = LayoutInflater.from(mContext).inflate(R.layout.topic_model_text_item, null);
				viewHolder.textView = (EmojiTextView) convertView.findViewById(R.id.topic_text_content);
				break;
			case 2:
				// 图片
				convertView = LayoutInflater.from(mContext).inflate(R.layout.topic_model_img_item, null);
				viewHolder.imageView = (ImageView) convertView.findViewById(R.id.topic_img_content);
				break;
			case 3:
				// 超链
				convertView = LayoutInflater.from(mContext).inflate(R.layout.topic_model_a_item, null);
				viewHolder.textView_a = (EmojiTextView) convertView.findViewById(R.id.topic_text_content);
				viewHolder.imageView_a = (ImageView) convertView.findViewById(R.id.topic_img_content);
				break;
			case 4:
				// 头部
				convertView = LayoutInflater.from(mContext).inflate(R.layout.topic_model_header_item, null);
				viewHolder.avator = (ImageView) convertView.findViewById(R.id.topic_user_avator);
				viewHolder.levelIcon = (ImageView) convertView.findViewById(R.id.ic_level);
				viewHolder.level = (TextView) convertView.findViewById(R.id.topic_user_level);
				viewHolder.userName = (TextView) convertView.findViewById(R.id.topic_user_name);
				viewHolder.floor = (TextView) convertView.findViewById(R.id.topic_user_floor);
				viewHolder.position = (TextView) convertView.findViewById(R.id.topic_lou_textView1);
				break;
			case 5:
				// 底部
				convertView = LayoutInflater.from(mContext).inflate(R.layout.topic_model_footer_item, null);
				viewHolder.address = (TextView) convertView.findViewById(R.id.topic_user_address);
				viewHolder.time = (TextView) convertView.findViewById(R.id.topic_user_time);
				viewHolder.reply = (Button) convertView.findViewById(R.id.topic_user_reply);
				break;
			default:
				break;
			}
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		Node nodefinal = (Node) getItem(position);
		String str = nodefinal.tag;
		// 初始化回复信息
		if (str.equalsIgnoreCase("text")) {
			TextNode node = (TextNode) (nodefinal);
			viewHolder.textView.setEmojiText(node.text);
		} else if (str.equalsIgnoreCase("img")) {
			ImgNode node = ((ImgNode) (nodefinal));

			imageLoadListener.setBigImage(node.small_src, node.big_src, bitmapDisplayConfig, viewHolder.imageView);

		} else if (str.equalsIgnoreCase("a")) {
			ANode aNode = (ANode) nodefinal;
			a(aNode, viewHolder);// 处理a类型数据
		} else if (str.equalsIgnoreCase("reply_header")) {
			final ReplayHeaderNode node = ((ReplayHeaderNode) (nodefinal));
			// 头像
			viewHolder.avator.setVisibility(View.VISIBLE);
			ImageView ivAvator = viewHolder.avator;// 头像
			bitmapCache.display(ivAvator, node.user_info.author_avatar);
			ivAvator.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// 跳转到用户信息页
					replyOnClickListener.onUserClick(node.user_info.author_enc_user_id);
				}
			});

			// 楼层
			viewHolder.floor.setText(node.floor + "楼");// 设置楼主
			// 名字
			viewHolder.userName.setText(node.user_info.author_name);
			// 是否是楼主
			if (node.is_author) {
				Drawable drawable = mContext.getResources().getDrawable(R.drawable.ic_topic_author);
				drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
				viewHolder.userName.setCompoundDrawables(null, null, drawable, null);
			} else {
				viewHolder.userName.setCompoundDrawables(null, null, null, null);

			}

			// 等级icon
			if (!node.user_info.user_level_img.equalsIgnoreCase("")) {
				viewHolder.levelIcon.setImageBitmap(null);
				bitmapCache.display(viewHolder.levelIcon, node.user_info.user_level_img);// 等级
				viewHolder.levelIcon.setVisibility(View.VISIBLE);
			} else {
				viewHolder.levelIcon.setVisibility(View.INVISIBLE);
			}
			// 等级
			if (!node.user_info.user_level.equalsIgnoreCase("")) {
				viewHolder.level.setText("LV" + node.user_info.user_level);// 等级
				viewHolder.level.setVisibility(View.VISIBLE);
			} else {
				viewHolder.level.setVisibility(View.INVISIBLE);
			}
			viewHolder.position.setVisibility(View.GONE);
			if (!node.position.equalsIgnoreCase("0") && !node.position.equalsIgnoreCase("-1")) {
				viewHolder.position.setVisibility(View.VISIBLE);
				String tmp = "回复" + node.position + "楼 " + node.position_user.nickname + "的内容";
				viewHolder.position.setText(tmp);
			} else {
				viewHolder.position.setVisibility(View.GONE);
			}

		} else if (str.equalsIgnoreCase("reply_footer")) {
			final ReplayFooterNode node = ((ReplayFooterNode) (nodefinal));
			// 地点
			viewHolder.address.setText(node.city_name);
			// 时间
			viewHolder.time.setText(BabytreeUtil.formatTimestamp(Long.parseLong(node.create_ts)));

			viewHolder.reply.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if (replyOnClickListener != null) {
						replyOnClickListener.onClick(node);
					}
				}
			});
		}

		return convertView;
	}

	/**
	 * 回复按钮监听
	 */
	private ReplyOnClickListener replyOnClickListener;

	private ImageLoadListener imageLoadListener;

	/**
	 * 设置加载图片监听
	 * 
	 * @param listener
	 */
	public void setImageLoadListener(ImageLoadListener listener) {
		this.imageLoadListener = listener;
	}

	/**
	 * 设置回复楼层按钮坚挺
	 * 
	 * @param listener
	 */
	public void setReplyOnClickListener(ReplyOnClickListener listener) {
		this.replyOnClickListener = listener;
	}

	/**
	 * 加载图片监听
	 * 
	 * @author wangshuaibo
	 * 
	 */
	public interface ImageLoadListener {
		/**
		 * 设置查看大图图片
		 * 
		 * @author wangshuaibo
		 * @param smallUrl
		 * @param bigUrl
		 * @param config
		 * @param imageView
		 */
		void setBigImage(String smallUrl, String bigUrl, BitmapDisplayConfig config, ImageView imageView);

		/**
		 * 设置超链接图片
		 * 
		 * @author wangshuaibo
		 * @param smallUrl
		 * @param href
		 * @param config
		 * @param imageView
		 */
		void setHrefImage(String smallUrl, String href, BitmapDisplayConfig config, ImageView imageView);

		/**
		 * 设置帖子链接图片
		 * 
		 * @author wangshuaibo
		 * @param smallUrl
		 * @param topicId
		 * @param config
		 * @param imageView
		 */
		void setTopicImage(String smallUrl, int topicId, BitmapDisplayConfig config, ImageView imageView);

	}

	/**
	 * 回复按钮回调
	 * 
	 * @author wangbingqi
	 * 
	 */
	public interface ReplyOnClickListener {
		/**
		 * 回复按钮
		 * 
		 * @param ReplayFooterNode
		 *            node
		 */
		void onClick(ReplayFooterNode node);
		/**
		 * 点击用户头像
		 * @author wangshuaibo
		 * @param userId
		 */
		void onUserClick(String userId);
	}

	void a(ANode node, ViewHolder viewHolder) {
		final ANode aNode = node;
		if ("url".equalsIgnoreCase(aNode.type)) {// 站外链接
			final Node subNode = aNode.node;
			if (subNode.tag.equalsIgnoreCase("text")) {
				EmojiTextView view = viewHolder.textView_a;
				viewHolder.textView_a.setVisibility(View.VISIBLE);
				viewHolder.imageView_a.setVisibility(View.GONE);
				view.setEmojiText(((TextNode) subNode).text);
				view.setTextColor(Color.BLUE);
				view.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
				viewHolder.textView_a.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 跳转到Webview
						Intent intent = new Intent();
						intent.setClass(mContext, BabyTreeWebviewActivity.class);
						intent.putExtra(BabyTreeWebviewActivity.BUNDLE_URL, aNode.href);
						mContext.startActivity(intent);

					}
				});
			} else if (subNode.tag.equalsIgnoreCase("img")) {
				ImageView view = viewHolder.imageView_a;
				viewHolder.textView_a.setVisibility(View.GONE);
				viewHolder.imageView_a.setVisibility(View.VISIBLE);
				String smallUrl = ((ImgNode) subNode).small_src;

				imageLoadListener.setHrefImage(smallUrl, aNode.href, bitmapDisplayConfig, view);
			}
		} else {// 站内
			if (aNode.node.tag.equalsIgnoreCase("text")) {// 站内贴/文字格式
				EmojiTextView view = viewHolder.textView_a;
				viewHolder.textView_a.setVisibility(View.VISIBLE);
				viewHolder.imageView_a.setVisibility(View.GONE);
				view.setEmojiText(((TextNode) aNode.node).text);
				view.setTextColor(Color.BLUE);
				view.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
				view.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						// 跳转到帖子页
						Intent intent = new Intent(mContext, TopicNewActivity.class);

						int topicId = 0;
						try {
							topicId = Integer.parseInt(aNode.topic_id);
						} catch (Exception e) {
							e.printStackTrace();
						}
						Bundle bundle = new Bundle();
						bundle.putInt(TopicNewActivity.BUNDLE_DISCUZ_ID, topicId);
						intent.putExtras(bundle);
						mContext.startActivity(intent);
					}
				});
			} else {// 站内贴/图片格式
				ImageView view = viewHolder.imageView_a;
				viewHolder.textView_a.setVisibility(View.GONE);
				viewHolder.imageView_a.setVisibility(View.VISIBLE);
				String smallUrl = ((ImgNode) aNode.node).small_src;
				int topicId = 0;
				try {
					topicId = Integer.parseInt(aNode.topic_id);
				} catch (Exception e) {
					e.printStackTrace();
				}
				imageLoadListener.setTopicImage(smallUrl, topicId, bitmapDisplayConfig, view);
			}
		}
	}

	private static class ViewHolder {
		/**
		 * 头像
		 */
		public ImageView avator;
		/**
		 * 级别ICON
		 */
		public ImageView levelIcon;
		/**
		 * 级别文字
		 */
		public TextView level;
		/**
		 * 用户名
		 */
		public TextView userName;
		/**
		 * 楼层
		 */
		public TextView floor;
		/**
		 * 位置 回复楼层
		 */
		public TextView position;
		/**
		 * 城市
		 */
		public TextView address;
		/**
		 * 时间
		 */
		public TextView time;
		/**
		 * 回复按钮文字
		 */
		public Button reply;

		/**
		 * 正文
		 */
		public EmojiTextView textView;
		/**
		 * 正文图片
		 */
		public ImageView imageView;
		/**
		 * 正文-a类型
		 */
		public EmojiTextView textView_a;
		/**
		 * 正文图片-a类型
		 */
		public ImageView imageView_a;
	}

}
