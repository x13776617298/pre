package com.babytree.apps.comm.ui;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.biz.topic.TopicNewActivity;
import com.babytree.apps.biz.user.LoginActivity;
import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.model.Discuz;
import com.babytree.apps.comm.model.UserDiscuzCountList;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.comm.view.EmojiTextView;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.PostReplyDiscuzListActivity;
import com.umeng.analytics.MobclickAgent;

/**
 * 用户信息
 * 
 * @author wangbingqi
 * 
 */
public class UserinfoNewActivity extends BabytreeTitleAcitivty implements OnClickListener, OnItemClickListener,
		OnScrollListener {
	private BabytreeBitmapCache bitmapCache;// 缓存对象

	private String mLoginString;

	private ImageView mImgHead;

	private TextView mTxtNickname;

	private TextView mTxtLocation;

	private TextView mTxtHospital;

	private TextView mTxtPregancyDate;

	private Button mBtnSend;

	private String mToUserEncodeId;

	private ListView mListView;
	private View mView;
	private PerAdapter pAdapter;
	private List<Discuz> postList = new ArrayList<Discuz>();
	private List<Discuz> replyList = new ArrayList<Discuz>();
	private ProgressDialog mDialog;
	private Button postBtn, replyBtn;

	private Button loadMoreBtn;
	private int buttonType = BUTTON_POST;
	private static final int BUTTON_POST = 10;
	private static final int BUTTON_REPLY = 11;
	private int pageNo = 1;
	private int visibleLastIndex = 0; // 最后的可视项索引
	private boolean replyChangable;
	private boolean postChangable = true;
	private boolean replyAddAble, postAddAble;

	/**
	 * 自己的ID
	 */
	private String mainID = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mLoginString = SharedPreferencesUtil.getStringValue(this, ShareKeys.LOGIN_STRING);
		mToUserEncodeId = getIntent().getStringExtra("user_encode_id");

		getPostList(mToUserEncodeId, null, mLoginString, "post");

		mainID = SharedPreferencesUtil.getStringValue(this, ShareKeys.USER_ENCODE_ID);

		// ---------------------缓存模块start--------------------------
		bitmapCache = BabytreeBitmapCache.create(mContext);
		// ---------------------缓存模块end----------------------------

		mView = View.inflate(this, R.layout.userinfo_head, null);
		mListView = (ListView) findViewById(R.id.user_listview);
		loadMoreBtn = (Button) getLayoutInflater().inflate(R.layout.load_more, null).findViewById(R.id.btn_load_more);
		mListView.setOnItemClickListener(this);
		mListView.addHeaderView(mView);
		pAdapter = new PerAdapter(replyList);
		mListView.setAdapter(pAdapter);
		mListView.addFooterView(loadMoreBtn);
		mListView.setOnScrollListener(this);
		postBtn = (Button) mView.findViewById(R.id.main_post_btn);
		replyBtn = (Button) mView.findViewById(R.id.main_reply_btn);

		loadMoreBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				loadMoreBtn.setText("加载中...");
				loadMoreBtn.setEnabled(false);
				if (buttonType == BUTTON_POST) {
					postAddAble = true;
					pageNo++;
					getPostList(mToUserEncodeId, null, mLoginString, "post");
				}
				if (buttonType == BUTTON_REPLY) {
					replyAddAble = true;
					pageNo++;
					getReplyList(mToUserEncodeId, null, mLoginString, "reply");
				}
			}

		});
		postBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MobclickAgent.onEvent(getBaseContext(), EventContants.index, EventContants.index_post);
				buttonType = BUTTON_POST;
				pageNo = 1;
				postChangable = true;
				replyChangable = false;
				postBtn.setBackgroundResource(R.drawable.user_info_left_select);
				replyBtn.setBackgroundResource(R.drawable.user_info_middle_no_select);
				showDialog(null, "加载中...", null, null, true, null, null);
				getPostList(mToUserEncodeId, null, mLoginString, "post");
			}

		});
		replyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				MobclickAgent.onEvent(getBaseContext(), EventContants.index, EventContants.index_reply);
				buttonType = BUTTON_REPLY;
				pageNo = 1;
				postChangable = false;
				replyChangable = true;
				postBtn.setBackgroundResource(R.drawable.user_info_middle_no_select);
				replyBtn.setBackgroundResource(R.drawable.user_info_left_select);

				showDialog(null, "加载中...", null, null, true, null, null);
				getReplyList(mToUserEncodeId, null, mLoginString, "reply");
			}

		});

		mImgHead = (ImageView) mView.findViewById(R.id.head_img);
		mBtnSend = (Button) mView.findViewById(R.id.btn_send);
		mTxtNickname = (TextView) mView.findViewById(R.id.txt_nickname);
		mTxtPregancyDate = (TextView) mView.findViewById(R.id.pregnancy_date_tv);
		mTxtHospital = (TextView) mView.findViewById(R.id.txt_hospital);
		mTxtLocation = (TextView) mView.findViewById(R.id.txt_location);
		mBtnSend.setOnClickListener(this);

		// btn_back = (Button) findViewById(R.id.btn_left);
		// btn_back.setOnClickListener(this);
		// fl_title = (FrameLayout)findViewById(R.id.title);
		// if(com.babytree.apps.comm.util.BabytreeUtil.isPregnancy(this)){
		// fl_title.setBackgroundResource(R.drawable.y_title_bg);
		// btn_back.setBackgroundResource(R.drawable.y_btn_back);
		// }

		process(mLoginString);

		if (mToUserEncodeId.equalsIgnoreCase(mainID)) {
			mBtnSend.setVisibility(View.INVISIBLE);

		} else {
			mBtnSend.setVisibility(View.VISIBLE);
		}
		// System.out.println("mToUserEncodeId:"+mToUserEncodeId+"  mainID:"+mainID);

	}

	private void process(final String loginString) {
		new Thread() {

			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(UserinfoNewActivity.this)) {
						ret = P_BabytreeController.getUserDiscuzCountList(mToUserEncodeId, null);
					} else {
						ret = new DataResult();
						ret.message = P_BabytreeController.NetworkExceptionMessage;
						ret.status = P_BabytreeController.NetworkExceptionCode;
					}
				} catch (Exception e) {
					ret = new DataResult();
					ret.message = P_BabytreeController.SystemExceptionMessage;
					ret.status = P_BabytreeController.SystemExceptionCode;
				}
				message.obj = ret;
				handler.sendMessage(message);
			}

		}.start();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			DataResult ret = (DataResult) msg.obj;
			switch (ret.status) {
			case P_BabytreeController.SUCCESS_CODE:
				UserDiscuzCountList list = (UserDiscuzCountList) ret.data;
				// Nickname Header
				String nickname = list.nick_name;
				String head = list.avatar;
				String pregancyDate = list.baby_age;
				String location = list.full_city;
				String hospitalName = list.hospital_name;
				if (nickname != null && !"".equals(nickname)) {
					mTxtNickname.setText(nickname);
				}
				// 显示头像
				bitmapCache.display(mImgHead, head);
				if (pregancyDate != null && !"".equals(pregancyDate)) {
					mTxtPregancyDate.setText(pregancyDate);
				}
				if (location != null && !"".equals(location)) {
					mTxtLocation.setText(location);
				}
				if (hospitalName != null && !"".equals(hospitalName)) {
					mTxtHospital.setText(hospitalName);
				}
				break;
			default:
				ExceptionUtil.catchException(ret.error, UserinfoNewActivity.this);
				Toast.makeText(UserinfoNewActivity.this, ret.message, Toast.LENGTH_SHORT).show();
				break;
			}
		}

	};

	// ==========UMENG Begin===========
	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.btn_send) {
			// 是否登录
			String loginString = SharedPreferencesUtil.getStringValue(this, ShareKeys.LOGIN_STRING);
			if (loginString == null) {
				Intent intent = new Intent(this, LoginActivity.class);
				intent.putExtra("user_encode_id", mToUserEncodeId);
				intent.putExtra("return", UserinfoNewActivity.class);
				startActivity(intent);
			} else {
				sendMessage();
			}
			return;
		}

		Intent intent = new Intent();
		if (v.getId() == R.id.layout_post) {
			intent.setClass(this, PostReplyDiscuzListActivity.class);
			intent.putExtra("type", "post");
			intent.putExtra("flag", "userinfo");
			intent.putExtra("user_encode_id", mToUserEncodeId);
		} else if (v.getId() == R.id.layout_reply) {
			intent.setClass(this, PostReplyDiscuzListActivity.class);
			intent.putExtra("user_encode_id", mToUserEncodeId);
			intent.putExtra("type", "reply");
			intent.putExtra("flag", "userinfo");
		} else if (v.getId() == R.id.layout_like) {
		}
		startActivity(intent);
	}

	private void sendMessage() {
		LayoutInflater factory = LayoutInflater.from(this);
		View view = factory.inflate(R.layout.mine_message_layout, null);
		final TextView txtUsername = (TextView) view.findViewById(R.id.txt_username);
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("发送消息");
		builder.setView(view);
		builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Umeng Event
				MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.com_message);
				final String content = txtUsername.getText().toString().trim();
				if (content.equals("")) {
					Toast.makeText(getApplicationContext(), "请输入消息", Toast.LENGTH_SHORT).show();
				} else {
					new Thread() {

						@Override
						public void run() {
							DataResult ret = null;
							Message message = new Message();
							try {
								if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(UserinfoNewActivity.this)) {
									ret = P_BabytreeController.sendUserMessage(mLoginString, content, mToUserEncodeId);
								} else {
									ret = new DataResult();
									ret.message = P_BabytreeController.NetworkExceptionMessage;
									ret.status = P_BabytreeController.NetworkExceptionCode;
								}
							} catch (Exception e) {
								ret = new DataResult();
								ret.message = P_BabytreeController.SystemExceptionMessage;
								ret.status = P_BabytreeController.SystemExceptionCode;
							}
							message.obj = ret;
							sendHandler.sendMessage(message);
						}

					}.start();

					dialog.dismiss();

				}
			}
		});
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
				dialog.dismiss();
			}
		});
		AlertDialog dialog = builder.create();
		// 显示之前用反射重新设置handler,阻止点击按钮关闭dialog
		try {
			Field field = dialog.getClass().getDeclaredField("mAlert");
			field.setAccessible(true);
			Object obj = field.get(dialog);
			field = obj.getClass().getDeclaredField("mHandler");
			field.setAccessible(true);
			field.set(obj, new ButtonHandler(dialog));

		} catch (Exception e) {
			e.printStackTrace();
		}

		dialog.show();
	}

	class ButtonHandler extends Handler {

		private WeakReference<DialogInterface> mDialog;

		public ButtonHandler(DialogInterface dialog) {
			mDialog = new WeakReference<DialogInterface>(dialog);
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case DialogInterface.BUTTON_POSITIVE:
			case DialogInterface.BUTTON_NEGATIVE:
			case DialogInterface.BUTTON_NEUTRAL:
				((DialogInterface.OnClickListener) msg.obj).onClick(mDialog.get(), msg.what);
				break;
			}
		}
	}

	private Handler sendHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			DataResult ret = (DataResult) msg.obj;
			switch (ret.status) {
			case P_BabytreeController.SUCCESS_CODE:
				// Umeng Evert
				MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.com_message_send);
				Toast.makeText(getBaseContext(), "发送成功", Toast.LENGTH_SHORT).show();
				break;
			default:
				ExceptionUtil.catchException(ret.error, getApplicationContext());
				Toast.makeText(getApplicationContext(), ret.message, Toast.LENGTH_SHORT).show();
				break;
			}
		}

	};

	Handler postHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mDialog != null && !UserinfoNewActivity.this.isFinishing()) {
				mDialog.dismiss();
			}
			DataResult ret = (DataResult) msg.obj;
			loadMoreBtn.setEnabled(true);
			if (ret.status == P_BabytreeController.SUCCESS_CODE) {
				if (null != ret.data) {
					if (postChangable) {
						postAddAble = false;
						postChangable = false;
						postList = (List<Discuz>) ret.data;
						if (postList.size() >= 20) {
							loadMoreBtn.setVisibility(View.VISIBLE);
						} else {
							loadMoreBtn.setVisibility(View.GONE);
						}
						pAdapter.list.clear();
						pAdapter.list = postList;
						pAdapter.notifyDataSetChanged();
					}
					if (postAddAble) {
						postList = (List<Discuz>) ret.data;
						if (postList.size() >= 20) {
							loadMoreBtn.setVisibility(View.VISIBLE);
						} else {
							Toast.makeText(UserinfoNewActivity.this, "已经加载完毕！", Toast.LENGTH_SHORT).show();
							loadMoreBtn.setVisibility(View.GONE);
						}
						pAdapter.list.addAll(postList);
						pAdapter.notifyDataSetChanged();
					}
				} else {
					pAdapter.list.clear();
					pAdapter.notifyDataSetChanged();
				}
				loadMoreBtn.setText("查看下20条");
			} else {
				ExceptionUtil.catchException(ret.error, UserinfoNewActivity.this);
				Toast.makeText(UserinfoNewActivity.this, ret.message, Toast.LENGTH_SHORT).show();
			}
		}
	};

	private void getPostList(final String mToUserEncodeId, final String object, final String loginStr2,
			final String string) {
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(UserinfoNewActivity.this)) {
						ret = P_BabytreeController.getUserDiscuzList(mToUserEncodeId, loginStr2, string, pageNo);
					} else {
						ret = new DataResult();
						ret.message = P_BabytreeController.NetworkExceptionMessage;
						ret.status = P_BabytreeController.NetworkExceptionCode;
					}
				} catch (Exception e) {
					ret = new DataResult();
					ret.message = P_BabytreeController.SystemExceptionMessage;
					ret.status = P_BabytreeController.SystemExceptionCode;
				}
				message.obj = ret;
				postHandler.sendMessage(message);
			}
		}.start();

	}

	private void getReplyList(final String mToUserEncodeId, final String object, final String loginStr2,
			final String string) {
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(UserinfoNewActivity.this)) {
						ret = P_BabytreeController.getUserDiscuzList(mToUserEncodeId, loginStr2, string, pageNo);
					} else {
						ret = new DataResult();
						ret.message = P_BabytreeController.NetworkExceptionMessage;
						ret.status = P_BabytreeController.NetworkExceptionCode;
					}
				} catch (Exception e) {
					ret = new DataResult();
					ret.message = P_BabytreeController.SystemExceptionMessage;
					ret.status = P_BabytreeController.SystemExceptionCode;
				}
				message.obj = ret;
				replyHandler.sendMessage(message);
			}
		}.start();

	}

	Handler replyHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mDialog != null && !UserinfoNewActivity.this.isFinishing()) {
				mDialog.dismiss();
			}
			DataResult ret = (DataResult) msg.obj;
			loadMoreBtn.setEnabled(true);
			if (ret.status == P_BabytreeController.SUCCESS_CODE) {
				if (null != ret.data) {
					if (replyChangable) {
						replyAddAble = false;
						replyChangable = false;
						replyList = (List<Discuz>) ret.data;
						if (replyList.size() >= 20) {
							loadMoreBtn.setVisibility(View.VISIBLE);
						} else {
							loadMoreBtn.setVisibility(View.GONE);
						}
						pAdapter.list.clear();
						pAdapter.list = replyList;
						pAdapter.notifyDataSetChanged();
					}
					if (replyAddAble) {
						replyList = (List<Discuz>) ret.data;
						if (replyList.size() >= 20) {
							loadMoreBtn.setVisibility(View.VISIBLE);
						} else {
							Toast.makeText(UserinfoNewActivity.this, "已经加载完毕！", Toast.LENGTH_SHORT).show();
							loadMoreBtn.setVisibility(View.GONE);
						}
						pAdapter.list.addAll(replyList);
						pAdapter.notifyDataSetChanged();
					}
				} else {
					pAdapter.list.clear();
					pAdapter.notifyDataSetChanged();
				}
				loadMoreBtn.setText("查看下20条");
			} else {
				ExceptionUtil.catchException(ret.error, UserinfoNewActivity.this);
				Toast.makeText(UserinfoNewActivity.this, ret.message, Toast.LENGTH_SHORT).show();
			}
		}
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

	}

	public void showDialog(String title, String content, String okText, String cancleText, boolean cancelable,
			OnCancelListener btnCancle, OnClickListener btnOk) {
		mDialog = new ProgressDialog(this);
		mDialog.setTitle(title);
		mDialog.setMessage(content);
		mDialog.setCancelable(cancelable);
		mDialog.show();
	}

	private class PerAdapter extends BaseAdapter {
		public List<Discuz> list = new ArrayList<Discuz>();

		public PerAdapter(List<Discuz> list) {
			this.list = list;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {

			ViewCache viewCache;
			final Discuz bean = (Discuz) getItem(position);
			if (convertView == null) {
				convertView = View.inflate(UserinfoNewActivity.this, R.layout.forum_item, null);
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

			final ImageView headImg = viewCache.getHeadImg();
			headImg.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(UserinfoNewActivity.this, UserinfoNewActivity.class);
					Bundle bl = new Bundle();
					bl.putString("user_encode_id", bean.author_id);
					intent.putExtras(bl);
					startActivity(intent);
					finish();
				}
			});
			String headUrl = bean.author_avatar;
			bitmapCache.display(headImg, headUrl);

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
			LinearLayout mLayoutItem = viewCache.getLayoutItem();

			mLayoutItem.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Discuz discuz = (Discuz) pAdapter.list.get(position);

					TopicNewActivity.launch(mContext, discuz.discuz_id, 1);
				}

			});
			return convertView;
		}

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

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		visibleLastIndex = firstVisibleItem + visibleItemCount - 1;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		int itemsLastIndex = pAdapter.getCount() - 1; // 数据集最后一项的索引
		int lastIndex = itemsLastIndex + 1; // 加上底部的loadMoreView项
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE && visibleLastIndex == lastIndex) {
			// 如果是自动加载,可以在这里放置异步加载数据的代码
			// Log.i("LOADMORE", "loading...");
			if (null != mLoginString && !"".equals(mLoginString)) {
				if (buttonType == BUTTON_POST) {
					pageNo++;
					getPostList(mToUserEncodeId, null, mLoginString, "post");
				}
				if (buttonType == BUTTON_REPLY) {
					pageNo++;
					getReplyList(mToUserEncodeId, null, mLoginString, "reply");
				}
			}
		}
	}

	@Override
	public void setLeftButton(Button button) {

	}

	@Override
	public void setRightButton(Button button) {

	}

	@Override
	public String getTitleString() {
		return "用户信息";
	}

	@Override
	public int getBodyView() {
		return R.layout.userinfo_new_activity;
	}
}
