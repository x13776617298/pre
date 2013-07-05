package com.babytree.apps.comm.ui;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.model.Message;
import com.babytree.apps.comm.model.SessionMessageListBean;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.ui.widget.PullToRefreshBase.OnRefreshListener;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.pregnancy.ui.adapter.AllMessageListAdapter;
import com.babytree.apps.pregnancy.ui.adapter.SesseonMessageListener;
import com.babytree.apps.pregnancy.ui.handler.AllMessageListHandler;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 消息页面
 */
public class AllTalkListActivity extends BabytreeTitleAcitivty implements OnRefreshListener, SesseonMessageListener,
		OnClickListener {

	private PullToRefreshListView mListView;

	private AllMessageListHandler mHandler;

	private AllMessageListAdapter mAdapter;

	private String mLoginString;

	private ProgressDialog mDialog;
	/**
	 * 语音对话框
	 */
	private RecognizerDialog iatDialog;

	/**
	 * 语音输入按钮
	 */
	private Button btnXunfei;

	// ---------------------

	private ArrayList<Base> list;

	/**
	 * 对方的ID
	 */
	private String user_encode_id;
	/**
	 * 对方的名字
	 */
	private String othernickname;
	/**
	 * 发送消息按钮
	 */
	private Button mButtonSend;
	/**
	 * 发送消息内容
	 */
	private EditText mEditTextSendMessage;
	/**
	 * 自己的ID
	 */
	private String mainID;
	/**
	 * 自己的名字
	 */
	private String nickName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.inbox_list_activity);

		user_encode_id = getIntent().getStringExtra("user_encode_id");
		othernickname = getIntent().getStringExtra("nickname");

		mLoginString = SharedPreferencesUtil.getStringValue(this, ShareKeys.LOGIN_STRING);
		mainID = SharedPreferencesUtil.getStringValue(this, ShareKeys.USER_ENCODE_ID);
		nickName = SharedPreferencesUtil.getStringValue(this, ShareKeys.NICKNAME);
		String name = othernickname;
		if (othernickname == null || othernickname.equalsIgnoreCase("")) {
			othernickname = "";
		} else {
			// 判断是否是老公的昵称
			if (othernickname.equals(nickName + "88")) {
				othernickname = "老公";
			}
			if (othernickname.length() > 5) {
				name = name.substring(0, 5) + "...";
			}

		}
		// 设置标题
		setTitleString(othernickname);

		mListView = (PullToRefreshListView) findViewById(R.id.list);

		mHandler = new AllMessageListHandler(this, mLoginString, user_encode_id, "0", "5");

		list = mHandler.getValues();
		mAdapter = new AllMessageListAdapter(mListView, this, R.layout.loading, R.layout.reloading, mHandler, list,
				user_encode_id);
		// mListView.getRefreshableView().setDivider(getResources().getDrawable(R.drawable.driver));
		mListView.getRefreshableView().setDivider(null);
		mListView.getRefreshableView().setAdapter(mAdapter);
		mListView.getRefreshableView().setOnScrollListener(mAdapter);
		mListView.setOnRefreshListener(this);
		mAdapter.setListener(this);

		mButtonSend = (Button) findViewById(R.id.btn_send);
		mEditTextSendMessage = (EditText) findViewById(R.id.et_sendmessage);
		mButtonSend.setOnClickListener(this);

		btnXunfei = (Button) findViewById(R.id.btn_xunfei);
		btnXunfei.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				showRecognizerDialog();
			}
		});

	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.btn_send:
			if (!mEditTextSendMessage.getText().toString().trim().equalsIgnoreCase("")) {
				if (mainID != null && !mainID.equalsIgnoreCase("")) {
					showLoadingDialog("发送中...");
					sendMessage(mEditTextSendMessage.getText().toString(), user_encode_id);
				}
			}else{
				Toast.makeText(this, "内容不能为空", Toast.LENGTH_SHORT).show();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void onClickTextView(SessionMessageListBean msg) {
		System.out.println("onClickTextView user_encode_id:" + user_encode_id);
		dialog(msg);

	}

	@Override
	public void onClickImageView(String user_encode_id) {
		System.out.println("onClickImageView user_encode_id:" + user_encode_id);
		// 判断是否为爸爸版,更改UI样式
		if (mIsFather) {
			return;
		}
		Intent intent = new Intent(getApplicationContext(), UserinfoNewActivity.class);
		Bundle bl = new Bundle();
		bl.putString("user_encode_id", user_encode_id);
		intent.putExtras(bl);
		startActivity(intent);
	}

	/**
	 * 发消息
	 * 
	 * @param content
	 *            内容
	 * @param mToUserEncodeId
	 */
	private void sendMessage(final String content, final String mToUserEncodeId) {
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				android.os.Message message = new android.os.Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(AllTalkListActivity.this)) {
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

	}

	private Handler sendHandler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			closeDialog();
			DataResult ret = (DataResult) msg.obj;
			switch (ret.status) {
			case P_BabytreeController.SUCCESS_CODE:
				// Umeng Evert
				MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.com_message_send);
				Toast.makeText(getBaseContext(), "发送成功", Toast.LENGTH_SHORT).show();
				SessionMessageListBean bean = new SessionMessageListBean();
				bean.content = mEditTextSendMessage.getText().toString();
				bean.user_encode_id = mainID;
				Long time = System.currentTimeMillis();
				bean.last_ts = com.babytree.apps.comm.util.BabytreeUtil.timestempToStringMore2(time + "");
				mHandler.addFooter(bean);
				mListView.getRefreshableView().setSelection(mListView.getRefreshableView().getCount() - 1);
				// mListView.setSelection(mListView.getCount() - 1);
				mEditTextSendMessage.setText("");

				break;
			default:
				ExceptionUtil.catchException(ret.error, getApplicationContext());
				Toast.makeText(getApplicationContext(), ret.message, Toast.LENGTH_SHORT).show();
				break;
			}
		}

	};

	@Override
	public void onRefresh() {
		mHandler.refershTop(System.currentTimeMillis());
	}

	private void dialog(final SessionMessageListBean message) {
		AlertDialog.Builder exitSystemDialog = new AlertDialog.Builder(this);
		exitSystemDialog.setTitle("操作");
		exitSystemDialog.setMessage("确认删除吗?");
		exitSystemDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				deleteMessage(message);
			}
		});
		exitSystemDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int i) {
				dialog.dismiss();
			}
		});
		exitSystemDialog.create().show();
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(android.os.Message msg) {
			DataResult ret = (DataResult) msg.obj;
			switch (ret.status) {
			case P_BabytreeController.SUCCESS_CODE:
				// 删除item
				SessionMessageListBean message = (SessionMessageListBean) ret.data;
				mHandler.removeItem(message);
				Toast.makeText(AllTalkListActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
				break;
			default:
				ExceptionUtil.catchException(ret.error, AllTalkListActivity.this);
				Toast.makeText(AllTalkListActivity.this, ret.message, Toast.LENGTH_SHORT).show();
				break;
			}

		}

	};

	private void deleteMessage(final SessionMessageListBean msg) {
		new Thread() {

			@Override
			public void run() {
				DataResult ret = null;
				android.os.Message message = new android.os.Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(AllTalkListActivity.this)) {
						// ret =
						// BabytreeController.deleteUserMessage(mLoginString,
						// MessageType.USER_INBOX, msg._id);

						ret = P_BabytreeController.deleteUserMessageNew(mLoginString, msg.message_id);
						ret.data = msg;
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

	AlertDialog selectDialog = null;

	public void selectDialog(Message item3) {
		final Message message = item3;
		if (selectDialog == null) {
			android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// 设置对话框的图标
			// builder.setIcon(R.drawable.header);
			// 设置对话框的标题
			builder.setTitle(R.string.operation);
			// 添加按钮
			builder.setItems(R.array.message_operate, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					// String
					// hoddy=getResources().getStringArray(R.array.message_operate)[which];
					switch (which) {
					case 0:
						sendMessageDialog(message);
						break;
					case 1:
						// dialog(message);
						break;
					default:
						break;
					}
					selectDialog.dismiss();
				}
			});

			// 创建选择操作对话框
			selectDialog = builder.create();
		}
		selectDialog.show();
	}

	private AlertDialog sendMessageDialog = null;

	public void sendMessageDialog(Message item2) {
		final Message item = item2;
		if (sendMessageDialog == null) {

			android.app.AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle(R.string.send_message);
			View view = View.inflate(getApplicationContext(), R.layout.dialog_send_message, null);
			final EditText sendMessageContent = (EditText) view.findViewById(R.id.et_send_message_content);
			builder.setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					showDialog(null, "发送中，请稍后...", null, null, true, null, null);
					final Handler handler2 = new Handler() {

						public void handleMessage(android.os.Message msg) {
							DataResult ret = (DataResult) msg.obj;
							switch (msg.what) {
							case P_BabytreeController.SUCCESS_CODE:
								cancelDialog();
								// 如果是爸爸版,增加爸爸消息发出数埋点
								String role = SharedPreferencesUtil.getStringValue(getApplicationContext(),
										ShareKeys.APP_TYPE_KEY, CommConstants.APP_TYPE_UNKNOW);
								if (role.equalsIgnoreCase(CommConstants.APP_TYPE_DADDY)) {
									MobclickAgent.onEvent(AllTalkListActivity.this, EventContants.f_m_send_count);
								}
								Toast.makeText(getApplicationContext(), R.string.send_message_success,
										Toast.LENGTH_LONG).show();
								break;
							default:
								ExceptionUtil.catchException(ret.error, AllTalkListActivity.this);
								Toast.makeText(AllTalkListActivity.this, ret.message, Toast.LENGTH_SHORT).show();
								break;
							}
							sendMessageContent.setText("");
						}
					};
					new Thread() {

						public void run() {
							DataResult ret = null;
							android.os.Message message = new android.os.Message();
							try {
								if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(AllTalkListActivity.this)) {
									ret = P_BabytreeController.sendUserMessage(mLoginString, sendMessageContent
											.getText().toString(), item.uid);
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
							handler2.sendMessage(message);
						}

					}.start();
					sendMessageDialog.dismiss();
				}
			});
			builder.setNegativeButton(R.string.dialog_cancle, new DialogInterface.OnClickListener() {

				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					sendMessageDialog.dismiss();
				}
			});
			// 设置对话框的图标
			builder.setView(view);
			sendMessageDialog = builder.create();
		}
		sendMessageDialog.show();
	}

	private void showDialog(String title, String content, String okText, String cancleText, boolean cancelable,
			OnCancelListener btnCancle, OnClickListener btnOk) {
		mDialog = new ProgressDialog(this);
		mDialog.setTitle(title);
		mDialog.setMessage(content);
		mDialog.setCancelable(cancelable);
		mDialog.show();
	}

	private void cancelDialog() {
		if (mDialog != null && !this.isFinishing()) {
			mDialog.dismiss();
		}
	}

	@Override
	public void setRightButton(Button button) {
		button.setVisibility(View.VISIBLE);
		// button.setText("刷新");
		if (BabytreeUtil.isPregnancy(this)) {
			button.setBackgroundResource(R.drawable.y_btn_message_shauxin);
		} else if (CommConstants.APP_TYPE_DADDY.equals(SharedPreferencesUtil.getStringValue(this,
				ShareKeys.APP_TYPE_KEY))) {
			button.setBackgroundResource(R.drawable.f_btn_message_shauxin);
		} else {
			button.setBackgroundResource(R.drawable.btn_message_shauxin);
		}
		button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mHandler.refersh();
			}
		});
	}

	@Override
	public String getTitleString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBodyView() {
		// TODO Auto-generated method stub
		return R.layout.talk_activity;
	}

	@Override
	public void setLeftButton(Button button) {
		// TODO Auto-generated method stub

	}

	/**
	 * 显示语音输入对话框 文档详见doc文件夹(科大讯飞MSC开发指南(Android))
	 * 
	 * @author wangshuaibo
	 */
	private void showRecognizerDialog() {
		if (iatDialog == null) {
			iatDialog = new RecognizerDialog(this, "appid=" + CommConstants.XUNFEI_APPID);
			iatDialog.setListener(new RecognizerDialogListener() {

				@Override
				public void onResults(ArrayList<RecognizerResult> results, boolean isLast) {
					StringBuilder builder = new StringBuilder();
					for (RecognizerResult recognizerResult : results) {
						builder.append(recognizerResult.text);
					}
					mEditTextSendMessage.append(builder);
					mEditTextSendMessage.setSelection(mEditTextSendMessage.length());
				}

				@Override
				public void onEnd(SpeechError arg0) {

				}
			});
			// 设置转写Dialog的引擎和poi参数.
			iatDialog.setEngine("sms", "", null);
			// 设置采样率参数，由于绝大部分手机只支持8K和16K，所以设置11K和22K采样率将无法启动录音.
			iatDialog.setSampleRate(RATE.rate16k);
		}
		iatDialog.show();
	}

}
