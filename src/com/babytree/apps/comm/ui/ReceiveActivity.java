package com.babytree.apps.comm.ui;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.ctr.SignInController;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.Md5Util;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

/**
 * 申请领取页面
 * 
 * @author Administrator
 * 
 */
public class ReceiveActivity extends BabytreeTitleAcitivty implements OnClickListener {

	private EditText mEditParentsName, mEditPhoneNum, mEditEmailAddress, mEditPostCode;
	private Button mBtnProvinces, mBtnApplyMika;
	private ProgressDialog mDialog;
	private String mProvinceStr;
	private String mCityStr;

	/**
	 * 显示图片地址
	 */
	private String pic_name;
	/**
	 * 奖品图片
	 */
	private ImageView iv_pic;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		pic_name = getIntent().getStringExtra("invite_pic");
		iv_pic = (ImageView) findViewById(R.id.imageView1);
		setImage(iv_pic, pic_name);

		mEditParentsName = (EditText) findViewById(R.id.et_parents_name);
		mEditPhoneNum = (EditText) findViewById(R.id.et_phones_number);
		mEditEmailAddress = (EditText) findViewById(R.id.et_email_address);
		mEditPostCode = (EditText) findViewById(R.id.et_postcode);

		mBtnProvinces = (Button) findViewById(R.id.btn_provinces);
		mBtnApplyMika = (Button) findViewById(R.id.btn_mika_apply);
		mBtnProvinces.setOnClickListener(this);
		mBtnApplyMika.setOnClickListener(this);

	}

//	private void setImage(final ImageView iv, String url) {
//		iv.setTag(Md5Util.md5(url));
//		iv.setVisibility(View.VISIBLE);
//		AsyncImageLoader mAsyncImageLoader = new AsyncImageLoader();
//		Drawable cacheDrawable = mAsyncImageLoader.loadDrawable(url, this, true, new ImageCallback() {
//			
//			@Override
//			public void imageLoaded(Drawable imageDrawable, String imageUrl) {
//				ImageView tagImage = (ImageView) iv.findViewWithTag(imageUrl);
//				if (tagImage != null) {
//					if (imageDrawable != null) {
//						tagImage.setImageDrawable(imageDrawable);
//					}
//				}
//			}
//		});
//		if (cacheDrawable != null) {
//			iv.setImageDrawable(cacheDrawable);
//		}
//	}

	private void setImage(final ImageView iv, String url) {
		// ---------------------缓存模块start--------------------------
		BabytreeBitmapCache bitmapCache = BabytreeBitmapCache.create(this);
		// ---------------------缓存模块end----------------------------
		// @pengxh 关闭MD5
		// viewCache.getHeadImg().setTag(Md5Util.md5(headUrl));
		iv.setTag(Md5Util.md5(url));
		iv.setVisibility(View.VISIBLE);
		// ---------------------缓存模块start--------------------------
		bitmapCache.display(iv, url);
		// ---------------------缓存模块end----------------------------
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_provinces:
			startActivityForResult(new Intent(this, ReceiveLocationListActivity.class), 0);
			break;
		case R.id.btn_mika_apply:
			String name = mEditParentsName.getText().toString();
			String mobile = mEditPhoneNum.getText().toString();
			String address = mEditEmailAddress.getText().toString();
			String postcode = mEditPostCode.getText().toString();
			String provinces = mBtnProvinces.getText().toString();

			if (judgeNotEmpty(name) && judgeNotEmpty(mobile) /*
															 * &&
															 * judgeNotEmpty(address
															 * )
															 */
					/* && judgeNotEmpty(postcode) */&& judgeNotEmpty(provinces)) {
				toNet(name, mobile, address);
			} else {
				showAlertDialog("请将信息填写完整。");
			}
			break;
		}
	}

	private void toNet(final String name, final String mobile, final String address) {
		showDialog(null, "提交中...", null, null, true, null, null);
		new Thread() {
			@Override
			public void run() {
				DataResult ret = null;
				Message message = new Message();
				try {
					if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(ReceiveActivity.this)) {
						String mLoginString = SharedPreferencesUtil.getStringValue(ReceiveActivity.this,
								ShareKeys.LOGIN_STRING);
						// String
						// mLoginString="u8537139711_21179cd974730bc40caca48230121177_1354271156";
						ret = SignInController.toApply(mLoginString, name, mobile, address);
					} else {
						ret = new DataResult();
						ret.message = P_BabytreeController.NetworkExceptionMessage;
						ret.status = P_BabytreeController.NetworkExceptionCode;
					}
				} catch (Exception e) {
					ret = new DataResult();
					ret.message = P_BabytreeController.SystemExceptionMessage;
					ret.status = P_BabytreeController.SystemExceptionCode;
					ret.error = ExceptionUtil.printException(e).toString();
				}
				message.obj = ret;
				message.what = 10;
				mHandler.sendMessage(message);
			}
		}.start();

	}

	Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			if (mDialog != null)
				mDialog.dismiss();

			switch (msg.what) {
			case 1:

				break;
			case 10:
				DataResult ret = (DataResult) msg.obj;
				if (ret.status == P_BabytreeController.SUCCESS_CODE) {
					Toast.makeText(ReceiveActivity.this, "提交信息成功!", Toast.LENGTH_SHORT).show();
					finish();
				} else {
					Toast.makeText(ReceiveActivity.this, ret.message, Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				break;
			}

		}
	};

	private void ApplyMiKa(final String name, final String mobile, final String province, final String city,
			final String address, final String zipcode) {
		showDialog(null, "提交中...", null, null, true, null, null);
		new Thread() {
			@Override
			public void run() {
				// DataResult ret = null;
				// Message message = new Message();
				//
				// try {
				// if (BabytreeUtil.hasNetwork(ApplyFreeMikaActivity.this)) {
				// // ret = BabytreeController.getAdsList(birthday);
				// ret = BabytreeController.applyMika(name, mobile, province,
				// city, address, zipcode);
				// } else {
				// ret = new DataResult();
				// ret.message = BabytreeController.NetworkExceptionMessage;
				// ret.status = BabytreeController.NetworkExceptionCode;
				// }
				// } catch (Exception e) {
				// ret = new DataResult();
				// ret.message = BabytreeController.SystemExceptionMessage;
				// ret.status = BabytreeController.SystemExceptionCode;
				// }
				// message.obj = ret;
				// mHandler.sendMessage(message);
			}
		}.start();

	}

	public void showDialog(String title, String content, String okText, String cancleText, boolean cancelable,
			OnCancelListener btnCancle, OnClickListener btnOk) {
		mDialog = new ProgressDialog(this);
		mDialog.setTitle(title);
		mDialog.setMessage(content);
		mDialog.setCancelable(cancelable);
		mDialog.show();
	}

	private void showAlertDialog(String msg) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(msg);
		builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		builder.create().show();
	}

	private boolean judgeNotEmpty(String tmp) {
		if (tmp == null || tmp.equals("")) {
			return false;
		} else {
			return true;
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			String mpositionStr = data.getStringExtra("name");
			mProvinceStr = data.getStringExtra("province");
			mCityStr = data.getStringExtra("city");
			mBtnProvinces.setText(mpositionStr);
		}
	}

	@Override
	public void setLeftButton(Button button) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setRightButton(Button button) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTitleString() {
		// TODO Auto-generated method stub
		return "申请领取";
	}

	@Override
	public int getBodyView() {
		// TODO Auto-generated method stub
		return R.layout.receive_activity;
	}

}
