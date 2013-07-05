package com.babytree.apps.biz.father;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.biz.father.ctr.FatherController;
import com.babytree.apps.biz.father.model.Task;
import com.babytree.apps.biz.father.ui.FatherTitleBar;
import com.babytree.apps.biz.father.ui.FatherTitleBar.TitleBarOnClick;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.net.BabytreeAsyncTask;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

/**
 * 准爸爸 发送消息任务页面
 * 
 * @author gaierlin
 * 
 */
public class SendMessageActivity extends BabytreeActivity implements OnClickListener {
	private static final String TAG = "SendMessageActivity";
	private static final String TASK_TYPE_2_NOT_SEND_MSG = "2";// 对于任务类别为2的不显示发送消息框
	private static final int TASK_SUCCESS = 1;
	private static final int TASK_ERROR = 0;
	private FatherTitleBar mTitleBar;
	private TextView mTaskTitle;
	private WebView mTaskContent;
	private TextView mRewards;
	private TextView mPregnancyValue;
	private Button mSendOk;
	private EditText mEdsendMsg;

	private boolean isSendSuccess = false;// 消息发送成功 true成功
	private String mSendToMomId;
	private Task mTask;
	private Context mContext;
	private Intent mResultIntent = new Intent();
	private String[] taskStatusPool = { "0", "0", "0" };// 0:task_id,1:task_done_status,2:send_msg_status

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.send_message_activity);
		mContext = this;
		Bundle bundle = getIntent().getExtras();
		if (bundle == null) {
			finish();
			return;
		}

		mTask = bundle.getParcelable("task");

		taskStatusPool[0] = "-" + mTask.task_id + "-";
		taskStatusPool[1] = mTask.task_status + "";
		taskStatusPool[2] = mTask.send_status + "";

		mSendToMomId = SharedPreferencesUtil.getStringValue(this, ShareKeys.MOM_ID_KEY, "");
		mTitleBar = (FatherTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setRightButtonVisibility(View.GONE);
		mTitleBar.setLeftButtonImage(R.drawable.btn_pre_page_normal);
		mTitleBar.setTitleBarOnClick(mTitleBarOnClick);
		mTitleBar.setTitleBarName(getString(R.string.father_fragments_title_name));

		mTaskTitle = (TextView) findViewById(R.id.task_title);
		mTaskContent = (WebView) findViewById(R.id.task_content);
		mRewards = (TextView) findViewById(R.id.rewards);
		mPregnancyValue = (TextView) findViewById(R.id.pregnancy_value);
		mSendOk = (Button) findViewById(R.id.send_ok);
		mSendOk.setOnClickListener(this);
		mSendOk.setText(R.string.send_message);
		mEdsendMsg = (EditText) findViewById(R.id.send_msg);

		mTaskTitle.setText(mTask.task_title);
		mTaskContent.loadDataWithBaseURL(null, mTask.task_content, "text/html", "utf-8", null);
		mTaskContent.setBackgroundColor(getResources().getColor(android.R.color.white));
		mEdsendMsg.setHint(mTask.task_send_text);
		mPregnancyValue.setText(getString(R.string.pregnancy_value, mTask.task_yunqi));

		if (!TASK_TYPE_2_NOT_SEND_MSG.equals(mTask.task_type) || TASK_SUCCESS == mTask.task_status
				|| TASK_SUCCESS == mTask.send_status) {
			mEdsendMsg.setVisibility(View.GONE);
			isSendSuccess = true;
			mSendOk.setText(R.string.send_done);
		} else {
			mEdsendMsg.setVisibility(View.VISIBLE);
		}

		if (mTask.task_status == TASK_SUCCESS) {// 完成任务
			mSendOk.setEnabled(false);
			mSendOk.setBackgroundResource(R.drawable.notlogintask_pressed);
		} else {
			mSendOk.setEnabled(true);
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.send_ok:
			String loginString = SharedPreferencesUtil.getStringValue(SendMessageActivity.this, ShareKeys.LOGIN_STRING);
			// 发送按钮
			if (isSendSuccess) {
				// 发送成功
				new DoneTask(mContext).execute(loginString, mTask.task_id);
			} else {
				// 发送消息
				String content = mEdsendMsg.getText().toString().trim();
				if (TextUtils.isEmpty(content)) {
					content = (String) mEdsendMsg.getHint();
				}

				if (TextUtils.isEmpty(content)) {// 这个方法或许永远不会走到，这样写是为了防止部分民间ROM对EidtText的修改
					Toast.makeText(this, R.string.message_boby_null, Toast.LENGTH_SHORT).show();
					return;
				}
				new SendMessage(mContext).execute(loginString, content, mSendToMomId);
			}
			break;

		default:
			break;
		}

	}

	/**
	 * 设置输入框显示隐藏
	 * 
	 * @param visibility
	 */
	private void setEdsendMsgVisibility(int visibility) {
		mEdsendMsg.setVisibility(visibility);
	}

	/**
	 * 设置按钮文字
	 * 
	 * @param str
	 */
	private void setButtonText(String str) {
		mSendOk.setText(str);
	}

	/**
	 * TitleBar的左右Button的点击处理事件。
	 */
	private TitleBarOnClick mTitleBarOnClick = new TitleBarOnClick() {
		@Override
		public void rightButtonOnClick() {
		}

		@Override
		public void leftButtonOnClick() {
			finish();
		}
	};

	/**
	 * 发送消息异步联网
	 * 
	 * @author gaierlin
	 * 
	 * @param loginString
	 * @param content
	 *            发送在内容
	 * @param toUserEncodeId
	 *            接收在ID
	 */
	class SendMessage extends BabytreeAsyncTask {

		public SendMessage(Context context) {
			super(context);
		}

		@Override
		protected DataResult toNet(String[] params) {
			String loginString = params[0];
			String content = params[1];
			String toUserEncodeId = params[2];
			return P_BabytreeController.sendUserMessage(loginString, content, toUserEncodeId);
		}

		@Override
		protected void success(DataResult result) {
			setEdsendMsgVisibility(View.INVISIBLE);
			setButtonText(getString(R.string.send_done));
			taskStatusPool[2] = TASK_SUCCESS + "";
			isSendSuccess = true;
			Toast.makeText(mContext, "消息发送成功", Toast.LENGTH_SHORT).show();
		}

		@Override
		protected void failure(DataResult result) {
			taskStatusPool[2] = TASK_ERROR + "";
			Toast.makeText(mContext, "网络问题，发送失败", Toast.LENGTH_SHORT).show();
		}

		public String getLoginString() {
			return "传送中...";
		}

	}

	/**
	 * 完成任务异步请求
	 * 
	 * @author gaierlin
	 * 
	 */
	class DoneTask extends BabytreeAsyncTask {

		public DoneTask(Context context) {
			super(context);
		}

		@Override
		protected DataResult toNet(String[] params) {
			String login_string = params[0];
			String task_id = params[1];
			return FatherController.doneTask(login_string, task_id);
		}

		@Override
		protected void success(DataResult result) {
			taskStatusPool[1] = TASK_SUCCESS + "";
			Task task = (Task) result.data;
			int yunqi = Integer.valueOf(task.task_yunqi);
			if (yunqi > 0) {
				Toast.makeText(mContext, "给妈妈加了" + yunqi + "点孕气值", Toast.LENGTH_LONG).show();
			}
			returnResult();
		}

		@Override
		protected void failure(DataResult result) {
			taskStatusPool[1] = TASK_ERROR + "";
			Toast.makeText(mContext, "网络问题，未完成", Toast.LENGTH_SHORT).show();
		}

		public String getLoginString() {
			return "传送中...";
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if ("1".equals(taskStatusPool[2])) {
				returnResult();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}

	private void returnResult() {
		String task = taskStatusPool[0] + "|" + taskStatusPool[1] + "|" + taskStatusPool[2];
		mResultIntent.putExtra("task_id", task);
		setResult(Activity.RESULT_OK, mResultIntent);
		finish();
	}
}
