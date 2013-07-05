package com.babytree.apps.biz.topicpost;

import java.util.ArrayList;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.biz.topicpost.ctr.TopicPostController;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.config.UmKeys;
import com.babytree.apps.comm.ctr.BaseController;
import com.babytree.apps.comm.listener.BabytreeOnClickListenner;
import com.babytree.apps.comm.ui.activity.BabytreePhotographActivity;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.iflytek.speech.RecognizerResult;
import com.iflytek.speech.SpeechConfig.RATE;
import com.iflytek.speech.SpeechError;
import com.iflytek.ui.RecognizerDialog;
import com.iflytek.ui.RecognizerDialogListener;
import com.umeng.analytics.MobclickAgent;

/**
 * 发帖
 */
public class TopicPostNewActivity extends BabytreePhotographActivity {

	/**
	 * Int
	 */
	public static final String BUNDLE_GROUP_ID = "group_id";
	/**
	 * String
	 */
	public static final String BUNDLE_NAME = "name";
	/**
	 * String
	 */
	public static final String BUNDLE_DOCTOR_NAME = "doctor_name";
	/**
	 * String
	 */
	public static final String BUNDLE_BIRTHDAY = "birthday";
	/**
	 * String
	 */
	public static final String BUNDLE_TITLE = "title";
	/**
	 * String
	 */
	public static final String BUNDLE_TOPIC_TITLE = "topic_title";
	/**
	 * String
	 */
	public static final String BUNDLE_CONTENT_TIP = "content_tip";

	/**
	 * 语音输入按钮
	 */
	private Button btnXunfei;
	/**
	 * 语音对话框
	 */
	private RecognizerDialog iatDialog;
	/**
	 * 用户登录Token
	 */
	private String mLoginString;

	/**
	 * 圈子ID
	 */
	private int mGroupId;
	/**
	 * 同龄圈信息(如:201305)
	 */
	private String mBirthday;
	/**
	 * 发表到XXX名称
	 */
	private String mName;
	/**
	 * 医生名称
	 */
	private String mDoctorName;

	/**
	 * 发帖页的导航栏显示文字，默认为“发表新帖”
	 */
	private String mTitle;

	/**
	 * 帖子标题
	 */
	private String mTopicTitle;

	/**
	 * 显示在内容区的提示文字
	 */
	private String mContentTip;

	private EditText mTxtTitle;
	private EditText mTxtContent;
	private ImageView mBtnPhoto;
	private TextView mTvSelectGroup;
	private String mPhotoPaht;

	/**
	 * 是否可以删除图片
	 */
	private boolean isCanDel = false;

	/**
	 * 标识用户是否点击了标题框,默认为true true:点击了标题框 false:点击了内容框
	 */
	private boolean isTitleChecked = true;

	/**
	 * 
	 * @author wangshuaibo
	 * @param context
	 * @param groupId
	 *            圈子ID
	 * @param birthday
	 *            同龄圈
	 * @param name
	 *            圈子名称
	 * @param doctorName
	 *            医生名字
	 * @param title
	 *            标题
	 * @param topicTitle
	 *            帖子标题
	 * @param contentTip
	 *            内容提示文字
	 */
	public static void launch(Activity context, int groupId, String birthday, String name, String doctorName,
			String title, String topicTitle, String contentTip, boolean forResult, int requestCode) {
		Intent intent = new Intent(context, TopicPostNewActivity.class);
		Bundle bundle = new Bundle();
		bundle.putInt(BUNDLE_GROUP_ID, groupId);
		bundle.putString(BUNDLE_BIRTHDAY, birthday);
		bundle.putString(BUNDLE_NAME, name);
		bundle.putString(BUNDLE_DOCTOR_NAME, doctorName);
		bundle.putString(BUNDLE_TITLE, title);
		bundle.putString(BUNDLE_TOPIC_TITLE, topicTitle);
		bundle.putString(BUNDLE_CONTENT_TIP, contentTip);
		intent.putExtras(bundle);
		BabytreeUtil.launch(context, intent, forResult, requestCode);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// 取值
		mGroupId = getIntent().getIntExtra(BUNDLE_GROUP_ID, 0);
		mName = getIntent().getStringExtra(BUNDLE_NAME);
		mDoctorName = getIntent().getStringExtra(BUNDLE_DOCTOR_NAME);
		mBirthday = getIntent().getStringExtra(BUNDLE_BIRTHDAY);
		mTitle = getIntent().getStringExtra(BUNDLE_TITLE);
		mTopicTitle = getIntent().getStringExtra(BUNDLE_TOPIC_TITLE);
		mContentTip = getIntent().getStringExtra(BUNDLE_CONTENT_TIP);

		super.onCreate(savedInstanceState);

		if (!TextUtils.isEmpty(mBirthday)) {
			String tempName = "";
			try {
				tempName = mBirthday.substring(0, 4) + "年" + mBirthday.substring(4, mBirthday.length()) + "月";
			} catch (Exception ex) {

			}
			mName = tempName + "同龄圈";
		}
		// 获取Token
		mLoginString = SharedPreferencesUtil.getStringValue(this, ShareKeys.LOGIN_STRING);

		// 标题,内容输入框
		mTxtTitle = (EditText) findViewById(R.id.txt_title);
		mTxtContent = (EditText) findViewById(R.id.txt_content);

		// 发布到XXX
		mTvSelectGroup = (TextView) findViewById(R.id.tv_select_group);
		mTvSelectGroup.setText(mName);

		// 拍照按钮
		mBtnPhoto = (ImageView) findViewById(R.id.btn_photo);
		mBtnPhoto.setOnClickListener(this);

		// 语音输入
		btnXunfei = (Button) findViewById(R.id.btn_xunfei);
		btnXunfei.setOnClickListener(this);

		// 标题焦点事件
		mTxtTitle.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					isTitleChecked = true;
				}
			}
		});

		// 内容焦点事件
		mTxtContent.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					isTitleChecked = false;
				}

			}
		});

		// Babybox 跳转过来的发帖
		if (!TextUtils.isEmpty(mTopicTitle)) {
			mTxtTitle.setText(mTopicTitle);
			mTxtTitle.setEnabled(false);
		}

		if (!TextUtils.isEmpty(mContentTip)) {
			mTxtContent.setHint(mContentTip);
		}
	}

	private void showBackDialog() {
		String title = mTxtTitle.getText().toString();
		String content = mTxtContent.getText().toString();
		if (!content.equals("") || !title.equals("")) {

			showAlertDialog("提示", "还有没有发布的内容,是否返回?", null, "确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					finish();
				}
			}, "取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int i) {
					dialog.dismiss();
				}
			});

		} else {
			finish();
		}

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			showBackDialog();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onClick(View v) {

		super.onClick(v);

		switch (v.getId()) {
		case R.id.btn_photo:
			// 插入图片
			MobclickAgent.onEvent(this, EventContants.com, EventContants.communicate_createTopicToCamera);
			if (!isCanDel) {
				showPhotoMenu(1027, 768);
			} else {
				showPhotoMenu(1027, 768, "删除图片");
			}
			break;

		case R.id.btn_xunfei:
			showRecognizerDialog();
			break;

		default:
			break;
		}
	}

	private void process(String loginString, int groupId, String title, String content) {
		String with_photo = "2";
		if (mPhotoPaht != null) {
			with_photo = "1";
		}
		String mcontent = content;
		if (!TextUtils.isEmpty(mDoctorName)) {
			StringBuffer sb = new StringBuffer();
			sb.append(content).append("有关").append(mDoctorName).append("的讨论");
			mcontent = sb.toString();
		}
		TopicPost mPost = new TopicPost();
		mPost.execute(loginString, "", mPhotoPaht, groupId + "", title, mcontent, mBirthday, mDoctorName, with_photo);

	}

	/**
	 * 删除照片
	 */
	private void cleanPhotoPaht() {
		mPhotoPaht = null;
		mBtnPhoto.setImageBitmap(null);
	}

	/**
	 * 设置显示的图片
	 * 
	 * @param bmp
	 */
	private void setBitmap(Bitmap bmp) {
		mPhotoPaht = mBitmapPath;
		mBtnPhoto.setImageBitmap(bmp);
	}

	@Override
	public void setLeftButton(Button button) {
		button.setOnClickListener(new BabytreeOnClickListenner() {

			@Override
			public void onClick(View v) {
				super.onClick(v);

				showBackDialog();
			}
		});
	}

	@Override
	public void setRightButton(Button button) {
		button.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_comm1));
		button.setVisibility(View.VISIBLE);
		button.setOnClickListener(new BabytreeOnClickListenner() {

			@Override
			public void onClick(View v) {

				super.onClick(v);

				String title = mTxtTitle.getText().toString();
				String content = mTxtContent.getText().toString();
				if (TextUtils.isEmpty(title)) {
					Toast.makeText(mContext, "请输入标题", Toast.LENGTH_SHORT).show();
					return;
				}
				if (TextUtils.isEmpty(content)) {
					Toast.makeText(mContext, "请输入内容", Toast.LENGTH_SHORT).show();
					return;
				}
				if (content.length() < 5) {
					Toast.makeText(mContext, "内容至少5个汉字", Toast.LENGTH_SHORT).show();
					return;
				}
				process(mLoginString, mGroupId, title, content);
			}
		});
	}

	@Override
	public String getTitleString() {
		return TextUtils.isEmpty(mTitle) ? "发表新帖" : mTitle;
	}

	@Override
	public int getBodyView() {
		return R.layout.topic_post_activity;
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
					if (isTitleChecked) {
						mTxtTitle.append(builder);
						mTxtTitle.setSelection(mTxtTitle.length());
					} else {
						mTxtContent.append(builder);
						mTxtContent.setSelection(mTxtContent.length());
					}
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

	@Override
	protected void getBitmap(Bitmap bitmap) {
		isCanDel = true;
		setBitmap(bitmap);
	}

	@Override
	public void otherdoing() {
		super.otherdoing();
		isCanDel = false;
		cleanPhotoPaht();
	}

	private class TopicPost extends AsyncTask<String, Integer, DataResult> {

		private String with_photo = "2";

		@Override
		protected DataResult doInBackground(String... params) {
			String loginString = params[0];
			String description = params[1];
			String filepath = params[2];
			String groupId = params[3];
			String title = params[4];
			String content = params[5];
			String birthday = params[6];
			String doctorName = params[7];
			String with_photo = params[8];
			DataResult ret = null;
			this.with_photo = with_photo;
			try {
				if (with_photo.equalsIgnoreCase("1")) {
					UmKeys.UMonEvent(mContext, UmKeys.POST_START_IMG);
				} else {
					UmKeys.UMonEvent(mContext, UmKeys.POST_START_CONTENT);
				}
				ret = TopicPostController.postPhotoNew(loginString, description, filepath, groupId, title, content,
						birthday, doctorName, with_photo);
			} catch (Exception e) {
				ret = new DataResult();
				ret.message = BaseController.SystemExceptionMessage;
				ret.status = BaseController.SystemExceptionCode;
				ret.error = ExceptionUtil.printException(e).toString();
			}
			return ret;
		}

		@Override
		protected void onPostExecute(DataResult result) {
			super.onPostExecute(result);
			dismissLoadingDialog();
			if (result.status == BaseController.SUCCESS_CODE) {
				if (with_photo.equalsIgnoreCase("1")) {
					UmKeys.UMonEvent(mContext, UmKeys.POST_SUCCESS_IMG);
				} else {
					UmKeys.UMonEvent(mContext, UmKeys.POST_SUCCESS_CONTENT);
				}
				MobclickAgent.onEvent(getBaseContext(), EventContants.com, EventContants.com_post);
				Toast.makeText(mContext, "发帖成功", Toast.LENGTH_SHORT).show();
				finish();
			} else {
				Toast.makeText(mContext, "发帖失败", Toast.LENGTH_SHORT).show();
				if (with_photo.equalsIgnoreCase("1")) {
					UmKeys.UMonEvent(mContext, UmKeys.POST_FAILD_IMG);
				} else {
					UmKeys.UMonEvent(mContext, UmKeys.POST_FAILD_CONTENT);
				}
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			showLoadingDialog("提交中...");
		}

	}
}
