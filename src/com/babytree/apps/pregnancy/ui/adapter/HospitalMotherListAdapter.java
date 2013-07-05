package com.babytree.apps.pregnancy.ui.adapter;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.biz.user.LoginActivity;
import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.model.HospitalMother;
import com.babytree.apps.comm.ui.UserinfoNewActivity;
import com.babytree.apps.comm.ui.page.AbstractDataLoaderHandler;
import com.babytree.apps.comm.ui.page.AbstractPageableAdapter;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

public class HospitalMotherListAdapter extends AbstractPageableAdapter<Base> {
	private BabytreeBitmapCache bitmapCache;// 缓存对象
	private Context mContext;

	public HospitalMotherListAdapter(PullToRefreshListView listView, Context context, int loadingViewResourceId,
			int reloadViewResourceId, AbstractDataLoaderHandler<Base> handler) {
		super(listView, context, loadingViewResourceId, reloadViewResourceId, handler);
		this.mContext = context;
		// ---------------------缓存模块start--------------------------
		bitmapCache = BabytreeBitmapCache.create(context);
		// ---------------------缓存模块end----------------------------
	}

	public HospitalMotherListAdapter(PullToRefreshListView listView, Context context, int loadingViewResourceId,
			int reloadViewResourceId, AbstractDataLoaderHandler<Base> handler, ArrayList<Base> values) {
		super(listView, context, loadingViewResourceId, reloadViewResourceId, handler);
		this.mContext = context;
		// ---------------------缓存模块start--------------------------
		bitmapCache = BabytreeBitmapCache.create(context);
		// ---------------------缓存模块end----------------------------
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewCache viewCache;
		final HospitalMother bean = (HospitalMother) getItem(position);
		if (convertView == null) {
			convertView = View.inflate(mContext, R.layout.hospital_mother_list_activity_item, null);
			viewCache = new ViewCache(convertView);
			convertView.setTag(viewCache);
		} else {
			viewCache = (ViewCache) convertView.getTag();
		}
		String head = bean.avatar_url;
		// @pengxh 关闭MD5
		ImageView imageView = viewCache.getHeadImg();
		imageView.setVisibility(View.VISIBLE);

		// ---------------------缓存模块start--------------------------
		bitmapCache.display(imageView, head);
		// ---------------------缓存模块end----------------------------

		viewCache.getHeadImg().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, UserinfoNewActivity.class);
				Bundle bl = new Bundle();
				bl.putString("user_encode_id", bean.enc_user_id);
				intent.putExtras(bl);
				mContext.startActivity(intent);
			}
		});

		TextView tvPregDay = viewCache.getPreDay();// ???
		tvPregDay.setText(bean.baby_age);

		TextView tvName = viewCache.getName();
		tvName.setText(bean.nickname);
		tvName.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, UserinfoNewActivity.class);
				Bundle bl = new Bundle();
				bl.putString("user_encode_id", bean.enc_user_id);
				intent.putExtras(bl);
				mContext.startActivity(intent);
			}
		});

		ImageView imgSend = viewCache.getSendImg();
		imgSend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String loginString = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOGIN_STRING);
				if (loginString == null) {
					Intent intent = new Intent(mContext, LoginActivity.class);
					intent.putExtra("user_encode_id", bean.enc_user_id);
					intent.putExtra("return", UserinfoNewActivity.class);
					mContext.startActivity(intent);
				} else {
					sendMessage(loginString, bean);
				}
			}
		});
		LinearLayout mLayoutItem = viewCache.getLayoutItem();
		mLayoutItem.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Intent intent = new Intent(mContext, UserinfoNewActivity.class);
				Bundle bl = new Bundle();
				bl.putString("user_encode_id", bean.enc_user_id);
				intent.putExtras(bl);
				mContext.startActivity(intent);
			}

		});
		return convertView;
	}

	private Handler sendHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			DataResult ret = (DataResult) msg.obj;
			switch (ret.status) {
			case P_BabytreeController.SUCCESS_CODE:
				// Umeng Evert
				MobclickAgent.onEvent(mContext, EventContants.com, EventContants.com_message_send);
				Toast.makeText(mContext, "发送成功", Toast.LENGTH_SHORT).show();
				break;
			default:
				ExceptionUtil.catchException(ret.error, mContext);
				Toast.makeText(mContext, ret.message, Toast.LENGTH_SHORT).show();
				break;
			}
		}

	};

	private void sendMessage(final String mLoginString, final HospitalMother bean) {
		LayoutInflater factory = LayoutInflater.from(mContext);
		View view = factory.inflate(R.layout.mine_message_layout, null);
		final TextView txtUsername = (TextView) view.findViewById(R.id.txt_username);
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setTitle("发送消息");
		builder.setView(view);
		builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// Umeng Event
				MobclickAgent.onEvent(mContext, EventContants.com, EventContants.com_message);
				final String content = txtUsername.getText().toString().trim();
				if (content.equals("")) {
					Toast.makeText(mContext, "请输入消息", Toast.LENGTH_SHORT).show();
				} else {
					new Thread() {

						@Override
						public void run() {
							DataResult ret = null;
							Message message = new Message();
							try {
								if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(mContext)) {
									ret = P_BabytreeController.sendUserMessage(mLoginString, content, bean.enc_user_id);
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

	static class ViewCache {
		private View baseView;

		private TextView tvName;

		private TextView tvPregDay;

		private LinearLayout layout;

		private ImageView imgHead;

		private ImageView imgSendMsg;

		public ViewCache(View view) {
			baseView = view;
		}

		public TextView getName() {
			if (tvName == null) {
				tvName = (TextView) baseView.findViewById(R.id.tv_name_user);
			}
			return tvName;
		}

		public TextView getPreDay() {
			if (tvPregDay == null) {
				tvPregDay = (TextView) baseView.findViewById(R.id.tv_preg_day);
			}
			return tvPregDay;
		}

		public LinearLayout getLayoutItem() {
			if (layout == null) {
				layout = (LinearLayout) baseView.findViewById(R.id.layout);
			}
			return layout;
		}

		public ImageView getHeadImg() {
			if (imgHead == null) {
				imgHead = (ImageView) baseView.findViewById(R.id.img_head_user);
			}
			return imgHead;
		}

		public ImageView getSendImg() {
			if (imgSendMsg == null) {
				imgSendMsg = (ImageView) baseView.findViewById(R.id.img_send_msg);
			}
			return imgSendMsg;
		}

	}
}
