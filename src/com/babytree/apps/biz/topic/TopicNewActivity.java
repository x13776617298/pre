package com.babytree.apps.biz.topic;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.babytree.apps.biz.photo.PhotoActivity;
import com.babytree.apps.biz.topic.adpter.TopicBaseAdapter;
import com.babytree.apps.biz.topic.adpter.TopicBaseAdapter.ImageLoadListener;
import com.babytree.apps.biz.topic.adpter.TopicBaseAdapter.ReplyOnClickListener;
import com.babytree.apps.biz.topic.ctr.TopicDetailsController;
import com.babytree.apps.biz.topic.model.ANode;
import com.babytree.apps.biz.topic.model.Discussion;
import com.babytree.apps.biz.topic.model.DiscussionContent;
import com.babytree.apps.biz.topic.model.ImgNode;
import com.babytree.apps.biz.topic.model.Node;
import com.babytree.apps.biz.topic.model.TextNode;
import com.babytree.apps.biz.topic.model.n.ReplayFooterNode;
import com.babytree.apps.biz.topic.model.n.TopicNewBean;
import com.babytree.apps.biz.topic.model.replay.TopicReplayBean;
import com.babytree.apps.biz.topic.view.KeyboardListenRelativeLayout;
import com.babytree.apps.biz.topic.view.KeyboardListenRelativeLayout.IOnKeyboardStateChangedListener;
import com.babytree.apps.biz.topic.view.ViewUtil;
import com.babytree.apps.biz.user.LoginActivity;
import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.bitmap.core.BitmapDisplayConfig;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.config.UmKeys;
import com.babytree.apps.comm.config.UrlConstrants;
import com.babytree.apps.comm.ctr.BaseController;
import com.babytree.apps.comm.net.BabytreeAsyncTask;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.ui.UserinfoNewActivity;
import com.babytree.apps.comm.ui.activity.BabyTreeWebviewActivity;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.FolderOper;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.comm.view.EmojiTextView;
import com.babytree.apps.comm.view.pop.ActionConfig;
import com.babytree.apps.comm.view.pop.ActionItem;
import com.babytree.apps.comm.view.pop.ActionItemView;
import com.babytree.apps.comm.view.pop.ActionMenu;
import com.babytree.apps.comm.view.pop.ActionMenu.OnPopMenuItemListener;
import com.babytree.apps.pregnancy.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.internal.BabyTreeBaseAdapter;
import com.umeng.analytics.MobclickAgent;

public class TopicNewActivity extends TopicGestureActivity<Node> implements OnClickListener, ReplyOnClickListener,
		ImageLoadListener {

	private final String TAG = TopicNewActivity.class.getSimpleName();

	/**
	 * int
	 */
	public static final String BUNDLE_DISCUZ_ID = "discuz_id";
	/**
	 * int
	 */
	public static final String BUNDLE_PAGE = "page";

	private TopicBaseAdapter<Node> mAdapter;
	/**
	 * 缓存处理对象
	 */
	private BabytreeBitmapCache bitmapCache;

	/**
	 * 总页数
	 */
	private int totalPageCount = 1;
	/**
	 * 当前页
	 */
	private int currPage = 1;

	/**
	 * 拍照按钮
	 */
	private ImageView btnTakePhoto;
	/**
	 * 发表评论
	 */
	private Button btnSendComment;

	/**
	 * 输入框 FOR 回复
	 */
	private EditText etAddComment;

	/**
	 * 头view
	 */
	private View headView;

	private LinearLayout linearLayout;

	/**
	 * 帖子ID
	 */
	private int discuz_id;

	/**
	 * 输入框布局
	 */
	private LinearLayout layoutComment;
	/**
	 * 假的输入框布局
	 */
	private RelativeLayout layoutComment2;
	/**
	 * 假输入框显示文字
	 */
	private TextView mTv_show;

	/**
	 * 分享显示的字符串
	 */
	private StringBuffer msharedStrBuffer = new StringBuffer();
	private static final String BASE_URL = UrlConstrants.HOST_URL + "/community/topic_mobile.php?id=";

	/**
	 * 头信息
	 */
	public Discussion discussion;
	/**
	 * 删除按钮
	 */
	private Button btnDelete;

	private ActionMenu mActionMenu;

	private ActionItemView mActionItemView;
	/**
	 * 是否是收藏
	 */
	private boolean mIsFav = false;
	/**
	 * 是否是只看楼主
	 */
	private boolean mOnlyAnthor = false;
	/**
	 * 删帖异步对象
	 */
	private AsyncTask<String, Integer, DataResult> deleteTz;
	/**
	 * 收藏帖子异步对象
	 */
	private AsyncTask<String, Integer, DataResult> favorTopic;

	/**
	 * 回复帖子异步线程
	 */
	private AsyncTask<String, Integer, DataResult> topicReply;
	/**
	 * 回复楼主数据缓存
	 */
	private TopicReplayBean topicReplayBean = new TopicReplayBean();
	/**
	 * 回复楼层数据缓存
	 */
	private TopicReplayBean topicReplayFloorBean = new TopicReplayBean();
	/**
	 * 是否回复楼主 true回复楼主 false 回复楼层
	 */
	private boolean isReplayOriginal = true;

	/**
	 * 回复当前楼层的数据
	 */
	private ReplayFooterNode replayfooternode;

	/**
	 * 文件头路径
	 */
	private String filePath = BabytreeBitmapCache.getAppCacheDirectory(mContext).getAbsolutePath() + "/";
	/**
	 * 临时文件存储位置
	 */
	private String tmp_parth = "";

	/**
	 * 是否是刷新第一页
	 */
	private boolean isFirstRefresh = true;
	/**
	 * 是否是刷新最后一页
	 */
	private boolean isLastRefresh = false;

	/**
	 * 是否初始化右边按钮
	 */
	private boolean isInit = false;

	public static void launch(Activity context, int discuz_id, int page) {
		Intent intent = new Intent(context, TopicNewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(TopicNewActivity.BUNDLE_DISCUZ_ID, discuz_id);

		if (page <= 0) {
			page = 1;
		}

		bundle.putInt(TopicNewActivity.BUNDLE_PAGE, page);
		intent.putExtras(bundle);
		BabytreeUtil.launch(context, intent, false, 0);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		discuz_id = getIntent().getIntExtra(BUNDLE_DISCUZ_ID, 0);
		currPage = getIntent().getIntExtra(BUNDLE_PAGE, 1); // 默认第一页

		super.onCreate(savedInstanceState);

		bitmapCache = BabytreeBitmapCache.create(mContext);
		btnTakePhoto = (ImageView) findViewById(R.id.btn_take_photo);
		btnTakePhoto.setOnClickListener(this);
		btnSendComment = (Button) findViewById(R.id.btn_send_comment);
		btnSendComment.setOnClickListener(this);
		etAddComment = (EditText) findViewById(R.id.et_add_comment);
		etAddComment.addTextChangedListener(textWatcher);
		layoutComment = (LinearLayout) findViewById(R.id.layout_add_comment);
		layoutComment.setVisibility(View.GONE);
		layoutComment.setOnClickListener(this);
		layoutComment2 = (RelativeLayout) findViewById(R.id.layout_add_comment2);
		layoutComment2.setVisibility(View.GONE);
		mTv_show = (TextView) findViewById(R.id.et_add_comment2);
		mTv_show.setOnClickListener(this);
		KeyboardListenRelativeLayout relativeLayout = (KeyboardListenRelativeLayout) findViewById(R.id.keyboardRelativeLayout);
		relativeLayout.setOnKeyboardStateChangedListener(keyboardStateChangedListener);
	}

	/**
	 * 监听文本变化
	 */
	TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (isReplayOriginal) {
				// 回复楼主 保存数据
				topicReplayBean.content = etAddComment.getText().toString();
			} else {
				// 回复楼层 保存数据
				topicReplayFloorBean.content = etAddComment.getText().toString();
				topicReplayFloorBean.position = replayfooternode.floor;
			}
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};

	/**
	 * 设置分享帖子的文本描述
	 */
	private void setTitleMessage() {
		String shareUrl = BASE_URL + discuz_id;
		String mTitle = discussion.discussion_title;
		msharedStrBuffer.append("Hi,我在宝宝树和孕妈妈们一起交流，话题是").append(" " + mTitle).append(",").append(" " + shareUrl + " ")
				.append("------来自宝宝树《@快乐孕期 》andriod客户端。");
	}

	/**
	 * 初始化头部信息
	 * 
	 * @param headView
	 * @param disduz
	 */
	private void initHeadView(View headView, final Discussion disduz) {
		// 首页Head

		// 帖子标题信息
		((EmojiTextView) headView.findViewById(R.id.topic_list_header_title_item))
				.setEmojiText(disduz.discussion_title);
		// 设置楼主
		((TextView) headView.findViewById(R.id.topic_user_floor)).setText("楼主");
		// 昵称
		((TextView) headView.findViewById(R.id.topic_user_name)).setText(disduz.user_info.author_name);
		// 预览数
		((TextView) headView.findViewById(R.id.topic_user_view_count)).setText(disduz.view_count);
		// 评论数
		((TextView) headView.findViewById(R.id.topic_user_reply_count)).setText(disduz.reply_count);
		// 圈子标题
		((TextView) headView.findViewById(R.id.txt_topic_group)).setText("来自:" + disduz.group_data.title);
		// 删除按钮
		btnDelete = (Button) headView.findViewById(R.id.topic_user_del);
		btnDelete.setOnClickListener(this);
		btnDelete.setVisibility(View.INVISIBLE);

		// 发表时间
		((TextView) headView.findViewById(R.id.topic_user_time)).setText(BabytreeUtil.formatTimestamp(Long
				.parseLong(disduz.create_ts)));
		// 用户地址
		((TextView) headView.findViewById(R.id.topic_user_city)).setText(disduz.city_name);
		// 等级
		TextView tvLevel = (TextView) headView.findViewById(R.id.topic_user_level);

		if (!disduz.user_info.user_level.equalsIgnoreCase("")) {
			tvLevel.setVisibility(View.VISIBLE);
			tvLevel.setText("LV" + disduz.user_info.user_level);// 等级
		} else {
			tvLevel.setVisibility(View.INVISIBLE);
		}
		// 头像
		ImageView image = (ImageView) headView.findViewById(R.id.topic_user_avator);
		image.setOnClickListener(this);
		bitmapCache.display(image, disduz.user_info.author_avatar);
		image.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				nativeUserBrowse(mContext, disduz.user_info.author_enc_user_id + "");
			}
		});

		// 加载等级
		ImageView ivLevelIcon = (ImageView) headView.findViewById(R.id.ic_level);
		if (!disduz.user_info.user_level_img.equalsIgnoreCase("")) {
			ivLevelIcon.setVisibility(View.VISIBLE);
			bitmapCache.display(ivLevelIcon, disduz.user_info.user_level_img);
		} else {
			ivLevelIcon.setVisibility(View.INVISIBLE);
		}

		// 帖子内容
		LinearLayout layoutContent = (LinearLayout) headView.findViewById(R.id.topic_content_layout);
		// 先清空容器中的内容
		layoutContent.removeAllViews();
		DiscussionContent content = disduz.discussion_content;
		List<Node> nodes = content.list;
		for (final Node node : nodes) {
			if (node.tag.equalsIgnoreCase("text")) {
				TextNode tmpnode = ((TextNode) node);
				layoutContent.addView(ViewUtil.createContentTextView(mContext, tmpnode.text));
			} else if (node.tag.equalsIgnoreCase("img")) {
				ImageView view = (ImageView) ViewUtil.createContentImageView(mContext);
				String smallUrl = ((ImgNode) node).small_src;
				String bigUrl = ((ImgNode) node).big_src;

				BitmapDisplayConfig bitmapDisplayConfig = mAdapter.getBitmapDisplayConfig();
				setBigImage(smallUrl, bigUrl, bitmapDisplayConfig, view);

				layoutContent.addView(view);
			} else if (node.tag.equalsIgnoreCase("a")) {
				final ANode aNode = (ANode) node;
				if ("url".equalsIgnoreCase(aNode.type)) {// 站外链接
					final Node subNode = aNode.node;
					if (subNode.tag.equalsIgnoreCase("text")) {
						TextView textView = ViewUtil.createContentTextView(mContext, ((TextNode) subNode).text);
						textView.setTextColor(Color.BLUE);
						textView.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划
						layoutContent.addView(textView);
						textView.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {

								BabyTreeWebviewActivity.launch(mContext, aNode.href, null);

							}
						});
					} else if (subNode.tag.equalsIgnoreCase("img")) {
						ImageView view = (ImageView) ViewUtil.createContentImageView(mContext);

						String smallUrl = ((ImgNode) subNode).small_src;
						String href = aNode.href;

						BitmapDisplayConfig bitmapDisplayConfig = mAdapter.getBitmapDisplayConfig();
						setHrefImage(smallUrl, href, bitmapDisplayConfig, view);

						layoutContent.addView(view);
					}
				} else {// 站内
					final int topicId = Integer.parseInt(aNode.topic_id);
					if (aNode.node.tag.equalsIgnoreCase("text")) {// 站内贴/文字格式
						TextNode tnode = (TextNode) (aNode.node);
						TextView view = ViewUtil.createContentTextView(mContext, tnode.text);
						view.setTextColor(Color.BLUE);
						view.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);// 下划线
						view.setOnClickListener(new View.OnClickListener() {

							@Override
							public void onClick(View v) {
								TopicNewActivity.launch(mContext, topicId, 0);
							}
						});
						layoutContent.addView(view);
					} else {// 站内贴/图片格式
						ImageView view = (ImageView) ViewUtil.createContentImageView(mContext);

						BitmapDisplayConfig bitmapDisplayConfig = mAdapter.getBitmapDisplayConfig();
						String smallUrl = ((ImgNode) aNode.node).small_src;
						setTopicImage(smallUrl, topicId, bitmapDisplayConfig, view);

						layoutContent.addView(view);
					}
				}
			} else {

			}
		}
		String userId = SharedPreferencesUtil.getStringValue(TopicNewActivity.this, ShareKeys.USER_ENCODE_ID);
		if (disduz.user_info.author_enc_user_id.equalsIgnoreCase(userId)) {
			btnDelete.setVisibility(View.VISIBLE);
		} else {
			btnDelete.setVisibility(View.INVISIBLE);
		}
		BabytreeLog.d(TAG + " - initHeadView");
	}

	private void initOthersView() {
		layoutComment2.setVisibility(View.VISIBLE);
		getRightButton().setEnabled(true);
	}

	/**
	 * 显示换页选项
	 */
	private void showPageDialog() {
		final String[] items = new String[totalPageCount];
		for (int i = 0; i < totalPageCount; i++) {
			items[i] = (i + 1) + "";
		}
		showAlertItemDialog("", items, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 换页
				toPage(which + 1);
			}
		});
	}

	/**
	 * 指定前往 num帖子页
	 * 
	 * @param num
	 */
	private void toPage(int num) {
		currPage = num;
		setFirsLoading(true);
		// Clear data.
		clearData();
		notifyDataSetChanged();
		onNetStart();
	}

	@Override
	protected void getBitmap(Bitmap bitmap) {
		btnTakePhoto.setImageBitmap(null);
		if (isReplayOriginal) {
			topicReplayBean.photoPaht = tmp_parth;
			if (topicReplayBean.bitmap != null) {
				topicReplayBean.bitmap.recycle();
				topicReplayBean.bitmap = null;
			}
			topicReplayBean.bitmap = bitmap;
			btnTakePhoto.setImageBitmap(bitmap);
			showEditText(1);

		} else {
			topicReplayFloorBean.photoPaht = tmp_parth;
			if (topicReplayFloorBean.bitmap != null) {
				topicReplayFloorBean.bitmap.recycle();
				topicReplayFloorBean.bitmap = null;
			}
			topicReplayFloorBean.content = etAddComment.getText().toString();
			topicReplayFloorBean.position = replayfooternode.floor;
			topicReplayFloorBean.bitmap = bitmap;
			btnTakePhoto.setImageBitmap(bitmap);
			showEditText(2);
		}
	}

	@Override
	public void onClick(View v) {

		super.onClick(v);

		switch (v.getId()) {
		case R.id.topic_user_avator:
			// 查看用户信息
			nativeUserBrowse(TopicNewActivity.this, discussion.user_info.author_enc_user_id);
			break;
		case R.id.btn_take_photo:
			// 拍照
			if (isLogin()) {
				if (FolderOper.isExistSdcard(this)) {
					String filename = getPhotoFilename(new Date());
					tmp_parth = filePath + filename;
					if (isReplayOriginal) {
						topicReplayBean.content = etAddComment.getText().toString();
						if (topicReplayBean.bitmap != null) {
							showPhotoMenu(1024, 768, "删除", tmp_parth);
						} else {
							showPhotoMenuSave(1024, 768, tmp_parth);
						}
					} else {
						topicReplayFloorBean.content = etAddComment.getText().toString();
						if (topicReplayFloorBean.bitmap != null) {
							showPhotoMenu(1024, 768, "删除", tmp_parth);
						} else {
							showPhotoMenuSave(1024, 768, tmp_parth);
						}
					}

				}
			} else {
				toLoginActivity();
			}

			break;
		case R.id.btn_send_comment:
			// 发送
			if (isLogin()) {
				if (etAddComment.getText().toString().length() < 5) {
					Toast.makeText(TopicNewActivity.this, "请输入至少五个字！", Toast.LENGTH_SHORT).show();
					return;
				} else {

					if (isReplayOriginal) {
						String content = etAddComment.getText().toString();
						String photoPaht = topicReplayBean.photoPaht;
						String discuzId = String.valueOf(discuz_id);
						String referId = "";
						String position = "";
						process(getLoginString(), discuzId, referId, position, content, photoPaht);
					} else {
						String content = etAddComment.getText().toString();
						String photoPaht = topicReplayFloorBean.photoPaht;
						String discuzId = String.valueOf(discuz_id);
						String referId = replayfooternode.reply_id;
						String position = replayfooternode.floor;
						process(getLoginString(), discuzId, referId, position, content, photoPaht);
					}
				}
			} else {
				toLoginActivity();
			}
			break;
		case R.id.topic_user_del:
			// 删除帖子
			showAlertDialog(discuz_id);
			break;
		case R.id.layout_add_comment:
		case R.id.et_add_comment2:
			// 显示输入框
			showEditText(1);
			break;
		default:
			break;
		}
	};

	/**
	 * 读取图片文件名
	 * 
	 * @param date
	 * @return
	 */
	private String getPhotoFilename(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddKms", Locale.CHINA);
		return dateFormat.format(date) + ".jpg";
	}

	IOnKeyboardStateChangedListener keyboardStateChangedListener = new IOnKeyboardStateChangedListener() {

		public void onKeyboardStateChanged(int state) {
			switch (state) {
			case KeyboardListenRelativeLayout.KEYBOARD_STATE_HIDE:// 软键盘隐藏
				Message msg = new Message();
				msg.what = 0;
				mhander.sendMessage(msg);
				break;
			case KeyboardListenRelativeLayout.KEYBOARD_STATE_SHOW:// 软键盘显示
				break;
			default:
				break;
			}
		}
	};

	/**
	 * 显示输入框的hander
	 */
	@SuppressLint("HandlerLeak")
	private Handler mhander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case 0:
				// 隐藏
				layoutComment2.setVisibility(View.VISIBLE);
				layoutComment.setVisibility(View.GONE);
				btnTakePhoto.setImageBitmap(null);
				break;
			case 1:
				// 显示楼主回复
				layoutComment.setVisibility(View.VISIBLE);
				layoutComment2.setVisibility(View.GONE);
				isReplayOriginal = true;
				if (topicReplayBean.content != null) {
					// 回复的内容不为空
					etAddComment.setText(topicReplayBean.content);
				}
				if (topicReplayBean.bitmap != null) {
					btnTakePhoto.setImageBitmap(topicReplayBean.bitmap);
				}
				// android:hint
				etAddComment.setHint("回复楼主");
				etAddComment.setSelection(etAddComment.getText().length());
				break;
			case 2:
				// 显示回复楼层
				layoutComment.setVisibility(View.VISIBLE);
				layoutComment2.setVisibility(View.GONE);
				isReplayOriginal = false;
				if (replayfooternode != null && replayfooternode.floor.equalsIgnoreCase(topicReplayFloorBean.position)) {
					// 如果回复楼层 是 保存的楼层
					// 则直接填充内容
					etAddComment.setText(topicReplayFloorBean.content);
					if (topicReplayFloorBean.bitmap != null) {
						btnTakePhoto.setImageBitmap(topicReplayFloorBean.bitmap);
					}
				} else {
					if (topicReplayFloorBean.bitmap != null) {
						btnTakePhoto.setImageBitmap(null);
						topicReplayFloorBean.bitmap.recycle();
						topicReplayFloorBean.bitmap = null;
					}
				}
				etAddComment.setHint("回复" + replayfooternode.floor + "楼:");
				etAddComment.setSelection(etAddComment.getText().length());
				break;
			default:
				break;
			}

		}
	};

	@Override
	public void otherdoing() {
		btnTakePhoto.setImageBitmap(null);
		if (isReplayOriginal) {
			if (topicReplayBean.bitmap != null)
				topicReplayBean.bitmap.recycle();
			topicReplayBean.bitmap = null;
			topicReplayBean.photoPaht = "";
		} else {
			if (topicReplayFloorBean.bitmap != null)
				topicReplayFloorBean.bitmap.recycle();
			topicReplayFloorBean.bitmap = null;
			topicReplayFloorBean.photoPaht = "";
		}
	};

	@Override
	public void setLeftButton(Button button) {

	}

	@Override
	public void setRightButton(Button button) {
		button.setVisibility(View.VISIBLE);
		button.setEnabled(false);
		button.setText("更多");
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mActionMenu.show(v, 0, 5);
			}
		});
	}

	/**
	 * 显示输入框
	 * 
	 * @param id
	 *            1回复楼主 2回复楼层
	 */
	private void showEditText(int id) {
		etAddComment.setFocusable(true);
		etAddComment.setFocusableInTouchMode(true);
		etAddComment.requestFocus();
		InputMethodManager m = (InputMethodManager) etAddComment.getContext().getSystemService(
				Context.INPUT_METHOD_SERVICE);
		m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);// 调用输入法弹出、收缩方法，设置的两个值为显示时的flag和隐藏时的flag
		Message msg = new Message();
		msg.what = id;
		mhander.sendMessage(msg);
	}

	/**
	 * 隐藏键盘
	 */
	private void concealEditText() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(etAddComment.getWindowToken(), 0);
		Message msg = new Message();
		msg.what = 0;
		mhander.sendMessage(msg);
	}

	/**
	 * 初始化 右上角 菜单
	 */
	private void initpopMenuAction() {

		int index = 0;
		ArrayList<ActionItem> actionItems = new ArrayList<ActionItem>();
		actionItems.add(new ActionItem(index++, this, R.drawable.btn_icon_fenxiang, "分享"));
		if (discussion == null || discussion.is_fav.equalsIgnoreCase("0")) {
			// 未收藏
			mIsFav = false;
			actionItems.add(new ActionItem(index++, this, R.drawable.btn_icon_shoucang_no, "收藏"));
		} else {
			mIsFav = true;
			actionItems.add(new ActionItem(index++, this, R.drawable.btn_icon_shoucang, "已收藏"));
		}

		actionItems.add(new ActionItem(index++, this, R.drawable.btn_icon_fanye, " 跳页"));
		actionItems.add(new ActionItem(index++, this, R.drawable.btn_icon_louzhu, "只看楼主"));
		actionItems.add(new ActionItem(index++, this, R.drawable.btn_icon_all, "显示全部"));

		mActionMenu = new ActionMenu(this, actionItems);
		mActionMenu.setMenuItemLine(R.drawable.btn_icon_line);
		mActionMenu.setPopMenuBackground(R.drawable.topic_new_menu_bc);
		int color = R.color.white;
		mActionMenu.setMenuItemTextColor(color);
		mActionMenu.setMenuItemTextSize(16f);

		mActionMenu.setOnMenuItemListener(new OnPopMenuItemListener() {
			@Override
			public void onClickItem(ActionItemView actionItemView, ActionItem actionItem, int position) {
				switch (position) {
				case 0:
					// 分享
					MobclickAgent.onEvent(getBaseContext(), EventContants.know, EventContants.know_share);
					mApplication.getUmSocialService().setShareImage(null);
					mApplication.getUmSocialService().setShareContent(msharedStrBuffer.toString());
					mApplication.getUmSocialService().openShare(TopicNewActivity.this);
					break;
				case 1:
					// 收藏
					String loginString = getLoginString();
					if (!TextUtils.isEmpty(loginString)) {
						if (!mIsFav) {
							processSave(loginString, "add", String.valueOf(discuz_id));
							TopicNewActivity.this.mActionItemView = actionItemView;
						} else {
							processSave(loginString, "del", String.valueOf(discuz_id));
							TopicNewActivity.this.mActionItemView = actionItemView;
						}

					} else {
						toLoginActivity();
					}
					break;
				case 2:
					// 换页
					showPageDialog();
					break;
				case 3:
					// 只看楼主
					mOnlyAnthor = true;
					currPage = 1;
					toPage(currPage);
					break;
				case 4:
					// 显示全部
					mOnlyAnthor = false;
					currPage = 1;
					toPage(currPage);
					break;
				default:
					break;
				}

			}

			@Override
			public void onLongClickItem(ActionItemView actionItemView, ActionItem actionItem, int position) {

			}
		});

		// 添加多对显示/隐藏配置
		ArrayList<ActionConfig> configs = new ArrayList<ActionConfig>();
		configs.add(new ActionConfig(3, 4));
		mActionMenu.addVisibleGoneConfig(configs);

		mActionMenu.iniPopMenu();

		setTitleMessage();
	}

	/**
	 * 跳转到登录界面
	 * 
	 * @author wangshuaibo
	 */
	private void toLoginActivity() {
		Intent intent = new Intent(mContext, LoginActivity.class);
		BabytreeUtil.launch(mContext, intent, false, 0);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		try {

			if (topicReplayBean != null && topicReplayBean.bitmap != null) {
				btnTakePhoto.setImageBitmap(null);
				topicReplayBean.bitmap.recycle();
				topicReplayBean.bitmap = null;
				topicReplayBean = null;
			}
			if (topicReplayFloorBean != null && topicReplayBean.bitmap != null) {
				btnTakePhoto.setImageBitmap(null);
				topicReplayFloorBean.bitmap.recycle();
				topicReplayFloorBean.bitmap = null;
				topicReplayFloorBean = null;
			}
		} catch (Exception e) {
		}
	}

	@Override
	public String getTitleString() {
		return "帖子详情";
	}

	@Override
	public int getBodyView() {
		return R.layout.topic_main;
	}

	/**
	 * 查看用户
	 * 
	 * @param encodeId
	 */
	private void nativeUserBrowse(Context context, String encodeId) {
		Intent intent = new Intent(context, UserinfoNewActivity.class);
		Bundle bl = new Bundle();
		bl.putString("user_encode_id", encodeId);
		intent.putExtras(bl);
		context.startActivity(intent);
	}

	/**
	 * 查看大图
	 * 
	 * @param url
	 */
	public static void nativePhotoBrowse(Context context, String url) {
		if (!ViewUtil.isGif(url)) {
			Intent intent = new Intent(context, PhotoActivity.class);
			intent.putExtra(PhotoActivity.BUNDLE_URL, url);
			context.startActivity(intent);
		}
	}

	/**
	 * 设置图片
	 * 
	 * @author wangshuaibo
	 * @param smallUrl
	 *            小图地址
	 * @param bigUrl
	 *            大图地址
	 * @param config
	 *            加载图片配置
	 * @param imageView
	 *            图片ImageView
	 */
	private void setImage(String smallUrl, BitmapDisplayConfig config, ImageView imageView) {
		if (ViewUtil.isGif(smallUrl)) {
			imageView.setScaleType(ScaleType.MATRIX);

			LayoutParams params = imageView.getLayoutParams();
			params.height = LayoutParams.WRAP_CONTENT;
			params.width = LayoutParams.WRAP_CONTENT;

			bitmapCache.display(imageView, smallUrl);

		} else {
			imageView.setScaleType(ScaleType.FIT_XY);

			LayoutParams params = imageView.getLayoutParams();
			params.height = config.getBitmapHeight();
			params.width = config.getBitmapWidth();

			bitmapCache.display(imageView, smallUrl, config);
		}

	}

	/**
	 * 收藏
	 * 
	 * @param loginString
	 * @param act
	 * @param topicId
	 */
	public void processSave(final String loginString, final String act, final String topicId) {
		favorTopic = new FavorTopic(this);
		favorTopic.execute(loginString, act, topicId);
	}

	/**
	 * 
	 * 收藏帖异步请求
	 * 
	 * @author wangbingqi
	 * 
	 */
	private class FavorTopic extends BabytreeAsyncTask {
		public FavorTopic(Context context) {
			super(context);
		}

		public String act = "";

		@Override
		protected String getDialogMessage() {
			return "提交中...";
		}

		@Override
		protected DataResult toNet(String[] params) {
			act = params[1];
			return TopicDetailsController.setFavorTopic(params[0], params[1], params[2]);
		}

		@Override
		protected void success(DataResult result) {
			if (act.equals("add")) {
				// 收藏
				mActionMenu.refreshItem(mActionItemView, R.drawable.btn_icon_shoucang, "已收藏");
				mIsFav = true;
				Toast.makeText(mContext, "收藏成功！", Toast.LENGTH_SHORT).show();
			} else if (act.equals("del")) {
				mActionMenu.refreshItem(mActionItemView, R.drawable.btn_icon_shoucang_no, "收藏");
				Toast.makeText(mContext, "取消收藏成功！", Toast.LENGTH_SHORT).show();
				mIsFav = false;
			}
		}

		@Override
		protected void failure(DataResult result) {
			ExceptionUtil.catchException(result.error, mContext);
			if (act.equals("add")) {
				mActionMenu.refreshItem(mActionItemView, R.drawable.btn_icon_shoucang_no, "收藏");
				Toast.makeText(mContext, "收藏失败！", Toast.LENGTH_SHORT).show();
			} else if (act.equals("del")) {
				mActionMenu.refreshItem(mActionItemView, R.drawable.btn_icon_shoucang, "已收藏");
				Toast.makeText(mContext, "取消收藏失败！", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@SuppressLint("HandlerLeak")
	Handler handerTmp = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				// 滚动到最低端
				int position = getPullRefreshListView().getRefreshableView().getCount();
				getPullRefreshListView().getRefreshableView().setSelection(position);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 删除帖子对话框
	 * 
	 * @param discusId
	 *            帖子ID
	 */
	private void showAlertDialog(final int discusId) {
		showAlertDialog("删除帖子", "确认删除帖子?删除后不能恢复。", null, "确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				deleteTz(String.valueOf(discusId));
			}

		}, "取消", null);

	}

	/**
	 * 删除帖子处理
	 */
	private void deleteTz(final String discusId) {
		deleteTz = new DeleteTz(this);
		deleteTz.execute(discusId);
	}

	/**
	 * 删帖异步请求
	 * 
	 * @author wangbingqi
	 * 
	 */
	public class DeleteTz extends BabytreeAsyncTask {
		public DeleteTz(Context context) {
			super(context);
		}

		@Override
		protected DataResult toNet(String[] params) {
			String loginString = getLoginString();
			return TopicDetailsController.deleteTz(loginString, params[0]);
		}

		@Override
		protected void success(DataResult result) {
			Toast.makeText(mContext, "删除成功！", Toast.LENGTH_SHORT).show();
			finish();
		}

		@Override
		protected void failure(DataResult result) {
			ExceptionUtil.catchException(result.error, mContext);
			Toast.makeText(mContext, result.message, Toast.LENGTH_SHORT).show();
		}

		@Override
		protected String getDialogMessage() {
			return "删除中，请稍后...";
		}

	}

	/**
	 * 回帖
	 * 
	 * @param loginString
	 * @param discuzId
	 *            帖子ID
	 * @param referId
	 *            回复或者引用的回复ID
	 * @param position
	 *            回复的楼层
	 * @param content
	 *            回复的内容
	 * @param photoPaht
	 *            回复图片地址
	 */
	private void process(final String loginString, final String discuzId, final String referId, final String position,
			final String content, final String photoPaht) {
		topicReply = new TopicReplyTask();
		topicReply.execute(loginString, discuzId, referId, position, content, photoPaht);
	}

	/**
	 * 异步回帖
	 * 
	 * @author wangbingqi
	 * 
	 */
	private class TopicReplyTask extends AsyncTask<String, Integer, DataResult> {
		/**
		 * 是否带图片
		 */
		private boolean isImage = false;

		@Override
		protected DataResult doInBackground(String... params) {
			String loginString = params[0];
			String discuzId = params[1];
			String referId = params[2];
			String position = params[3];
			String content = params[4];
			String photoPaht = params[5];
			DataResult ret = null;
			try {
				String str = BabytreeUtil.getExtraInfo(TopicNewActivity.this);
				MobclickAgent.onEvent(TopicNewActivity.this, "reply_start_merge", str + "");
				ret = TopicDetailsController.postReply(loginString, discuzId, referId, position, content, photoPaht);
			} catch (Exception e) {
				ret = new DataResult();
				ret.message = BaseController.SystemExceptionMessage;
				ret.status = BaseController.SystemExceptionCode;
				ret.error = ExceptionUtil.printException(e).toString();
			}
			// 回帖发送埋点
			if (photoPaht.equalsIgnoreCase("")) {
				isImage = false;
				UmKeys.UMonEvent(TopicNewActivity.this, UmKeys.REPLY_START_CONTENT);
			} else {
				isImage = true;
				UmKeys.UMonEvent(TopicNewActivity.this, UmKeys.REPLY_START_IMG);
			}
			return ret;
		}

		@Override
		protected void onPostExecute(DataResult result) {
			super.onPostExecute(result);
			dismissLoadingDialog();
			String str = BabytreeUtil.getExtraInfo(TopicNewActivity.this);
			if (result.status == BaseController.SUCCESS_CODE) {
				if (isReplayOriginal) {
					if (topicReplayBean.bitmap != null) {
						btnTakePhoto.setImageBitmap(null);
						topicReplayBean.bitmap.recycle();
					}
					topicReplayBean.bitmap = null;
					topicReplayBean.content = "";
					topicReplayBean.discuzId = "";
					topicReplayBean.photoPaht = "";
				} else {
					if (topicReplayFloorBean.bitmap != null) {
						btnTakePhoto.setImageBitmap(null);
						topicReplayFloorBean.bitmap.recycle();
					}
					topicReplayFloorBean.bitmap = null;
					topicReplayFloorBean.content = "";
					topicReplayFloorBean.discuzId = "";
					topicReplayFloorBean.photoPaht = "";
				}

				if (isImage) {
					UmKeys.UMonEvent(TopicNewActivity.this, UmKeys.REPLY_SUCCESS_IMG);
				} else {
					UmKeys.UMonEvent(TopicNewActivity.this, UmKeys.REPLY_SUCCESS_CONTENT);
				}

				MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.com_reply);
				Toast.makeText(TopicNewActivity.this, "回帖成功", Toast.LENGTH_SHORT).show();

				try {
					etAddComment.setText("");
					concealEditText();
					toPage(Integer.parseInt(discussion.page_count));
				} catch (Exception e) {
					setResult(RESULT_OK);
					finish();
				}

			} else {

				if (isImage) {
					UmKeys.UMonEvent(TopicNewActivity.this, UmKeys.REPLY_FAILD_IMG);
				} else {
					UmKeys.UMonEvent(TopicNewActivity.this, UmKeys.REPLY_FAILD_CONTENT);
				}

				MobclickAgent.onEvent(TopicNewActivity.this, "reply_faild_merge", str + " |" + result.message);
				ExceptionUtil.catchException(result.error, TopicNewActivity.this);
				Toast.makeText(TopicNewActivity.this, "回帖失败", Toast.LENGTH_SHORT).show();
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showLoadingDialog("提交中...");
		}

	}

	@Override
	public void onClick(ReplayFooterNode node) {
		showEditText(2);
		this.replayfooternode = node;
	}

	@Override
	protected BabyTreeBaseAdapter<Node> getAdapte() {
		mAdapter = new TopicBaseAdapter<Node>(mContext);
		mAdapter.setReplyOnClickListener(this);
		mAdapter.setImageLoadListener(this);
		return mAdapter;
	}

	@Override
	protected Mode onCreate() {
		setDivider(0);
		headView = LayoutInflater.from(mContext).inflate(R.layout.topic_list_header_item, null);
		linearLayout = (LinearLayout) headView.findViewById(R.id.topic_list_header_item_linearLayout);
		linearLayout.setVisibility(View.GONE);
		getPullRefreshListView().getRefreshableView().addHeaderView(headView, null, false);

		return Mode.BOTH;
	}

	@Override
	protected void onUpRefresh() {

		currPage++;
		if (currPage > totalPageCount) {
			isLastRefresh = true;
			currPage = totalPageCount;
		} else {
			isLastRefresh = false;
		}
		onNetStart();
	}

	@Override
	protected void onDownRefresh() {

		currPage--;
		if (currPage <= 0) {
			isFirstRefresh = true;
			currPage = 1;
		} else {
			isFirstRefresh = false;
		}
		onNetStart();
	}

	private void setPNTitle(int page) {
		String space = "，";
		String pPullTitle;
		String nPullTitle;
		String pReleaseTitle;
		String nReleaseTitle;
		if ((page - 1) == 0) {
			pPullTitle = "已经是第一页";
			pReleaseTitle = "松手刷新";
		} else {
			pPullTitle = "下拉加载第" + (page - 1) + "页" + space + "共" + totalPageCount + "页";
			pReleaseTitle = "松手加载第" + (page - 1) + "页" + space + "共" + totalPageCount + "页";
		}
		if ((page + 1) <= totalPageCount) {
			nPullTitle = "上拉加载第" + (page + 1) + "页" + space + "共" + totalPageCount + "页";
			nReleaseTitle = "松手加载第" + (page + 1) + "页" + space + "共" + totalPageCount + "页";
		} else {
			nPullTitle = "已经是最后一页";
			nReleaseTitle = "松手刷新";
		}

		getPullRefreshListView().getLoadingLayoutProxy(true, false).setPullLabel(pPullTitle);
		getPullRefreshListView().getLoadingLayoutProxy(false, true).setPullLabel(nPullTitle);
		getPullRefreshListView().getLoadingLayoutProxy(true, false).setReleaseLabel(pReleaseTitle);
		getPullRefreshListView().getLoadingLayoutProxy(false, true).setReleaseLabel(nReleaseTitle);
	}

	@Override
	protected void success(DataResult result) {
		PullToRefreshListView pullListView = getPullRefreshListView();

		TopicNewBean bean = (TopicNewBean) result.data;

		discussion = bean.discussion;

		totalPageCount = Integer.parseInt(discussion.page_count);

		setPNTitle(currPage);

		if ("1".equals(discussion.current_page)) {
			// 第一页
			// 初始化 帖子内容View
			initHeadView(headView, discussion);
			linearLayout.setVisibility(View.VISIBLE);

		} else {
			linearLayout.setVisibility(View.GONE);
		}

		if (isFirsLoading() && !isInit) {
			// 初始化右上角Button
			initpopMenuAction();
			initOthersView();
			isInit = true;
		}

		if (bean.nodeList.isEmpty()) {
			onEndRefresh();
			pullListView.getRefreshableView().setVisibility(View.VISIBLE);
		} else {
			clearData();
			setData(bean.nodeList);
			onRefresh();
		}
		Mode mode = pullListView.getCurrentMode();
		if (mode == Mode.PULL_FROM_END) {
			// 上拉
			if (!isLastRefresh) {
				pullListView.getRefreshableView().setSelection(0);
			}
		} else if (mode == Mode.PULL_FROM_START) {
			// 下拉
			if (!isFirstRefresh) {
				pullListView.getRefreshableView().setSelection(pullListView.getRefreshableView().getCount());
			}
		}
	}

	@Override
	protected DataResult getDataResult() {
		long currentTime = System.currentTimeMillis();
		DataResult dataResult = TopicDetailsController.getTopic(getLoginString(), discuz_id, currPage, mOnlyAnthor);
		BabytreeLog.i("Get topic time : " + (System.currentTimeMillis() - currentTime));
		return dataResult;
	}

	@Override
	protected void failure(DataResult result) {
		super.failure(result);
	}

	@Override
	protected int setPopWindowImage() {
		boolean b = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.TOPIC_MENGCENG, false);
		if (b) {
			return 0;
		}
		SharedPreferencesUtil.setValue(this, ShareKeys.TOPIC_MENGCENG, true);
		return R.drawable.topic_mengceng;
	}

	@Override
	protected void gestureSlideLeft() {
		// Nothing todo.

	}

	@Override
	protected void gestureSlideRight() {
		// 向左滑动
		finish();

	}

	@Override
	protected void gestureSlideUp() {
		// Nothing todo.

	}

	@Override
	protected void gestureSlideDown() {
		// Nothing todo.

	}

	@Override
	public void setBigImage(String smallUrl, final String bigUrl, BitmapDisplayConfig config, ImageView imageView) {
		setImage(smallUrl, config, imageView);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 查看大图
				TopicNewActivity.nativePhotoBrowse(mContext, bigUrl);
			}
		});

	}

	@Override
	public void setHrefImage(String smallUrl, final String href, BitmapDisplayConfig config, ImageView imageView) {
		setImage(smallUrl, config, imageView);
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到Webview
				Intent intent = new Intent();
				intent.setClass(mContext, BabyTreeWebviewActivity.class);
				intent.putExtra(BabyTreeWebviewActivity.BUNDLE_URL, href);
				mContext.startActivity(intent);
			}
		});
	}

	@Override
	public void setTopicImage(String smallUrl, final int topicId, BitmapDisplayConfig config, ImageView imageView) {
		setImage(smallUrl, config, imageView);
		imageView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 跳转到帖子页
				Intent intent = new Intent(mContext, TopicNewActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt(TopicNewActivity.BUNDLE_DISCUZ_ID, topicId);
				intent.putExtras(bundle);
				mContext.startActivity(intent);
			}
		});

	}

	@Override
	public void onUserClick(String userId) {
		nativeUserBrowse(mContext, userId);

	}

}
