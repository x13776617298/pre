package com.babytree.apps.biz.father.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.babytree.apps.biz.father.RoleSelectActivity;
import com.babytree.apps.biz.father.WelcomeFatherActivity;
import com.babytree.apps.biz.father.adapter.MenuAdapter;
import com.babytree.apps.biz.father.ctr.FatherController;
import com.babytree.apps.biz.father.util.Utils;
import com.babytree.apps.biz.knowledge.InformationActivity;
import com.babytree.apps.biz.knowledge.KitchenActivity;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ui.ForecastActivity;
import com.babytree.apps.comm.ui.UnionActivity;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

/**
 * 拖动菜单及菜单相关处理事件
 * 
 * @author gaierlin
 */
public class MenuFragment extends ListFragment {
	private static final int UNBIND_SUCCESS = 1;
	private static final int UNBIND_ERROR = 2;
	private static final int UNACCREDITED_CODE = 3;
	private static final int REELECT = 4;
	private static final String TAG = "MenuFragment";
	private MenuAdapter menuAdapter;
	Context mContext = null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.list, null);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		mContext = getActivity();
		menuAdapter = new MenuAdapter(mContext);
		setListAdapter(menuAdapter);
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		super.onListItemClick(lv, v, position, id);
		menuAdapter.updateMenuUIItem(position);
		switch (position) {
		case 0:// 每日知识
			startActivity(new Intent(mContext, InformationActivity.class));
			break;
		case 1:// 美食厨房
			startActivity(new Intent(mContext, KitchenActivity.class));
			break;
		case 2:// 小工具
			startActivity(new Intent(mContext, ForecastActivity.class));
			break;
		case 3:// 精彩应用推荐
			startActivity(new Intent(mContext, UnionActivity.class));
			break;
		case 4:
			if (menuAdapter.isBinding()) {
				Utils.dialog(mContext, mContext.getString(R.string.unbind_msg),
						mContext.getString(R.string.dialog_title), mContext.getString(R.string.ok),
						mContext.getString(R.string.cancel), unbind).show();
			} else {
				switchFragment(new UnaccreditedFatherFragment());
			}
			break;
		case 5:
			if (menuAdapter.isBinding()) {
				Utils.dialog(mContext, "请先取消与准妈妈的绑定", null, mContext.getString(R.string.ok), null, null).show();
			} else {
				myHandler.sendEmptyMessage(REELECT);
			}
			break;
		}
	}

	// 切换Fragment
	private void switchFragment(Fragment fragment) {
		if (mContext == null)
			return;

		if (mContext instanceof WelcomeFatherActivity) {
			WelcomeFatherActivity fca = (WelcomeFatherActivity) mContext;
			fca.menuSwitchFragment(fragment);
		}
	}

	/**
	 * 对话框的确定，取消铵钮
	 * 
	 * @author gaierlin
	 * 
	 */
	public interface DialogOnClickListener {
		void okOnClick();

		void cancelOnClick();
	}

	public DialogOnClickListener unbind = new DialogOnClickListener() {

		@Override
		public void okOnClick() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					String loginString = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.LOGIN_STRING, "");
					String role = SharedPreferencesUtil.getStringValue(mContext, ShareKeys.APP_TYPE_KEY,
							CommConstants.APP_TYPE_DADDY);
					DataResult result = FatherController.unbind(loginString, role);
					Message msg = new Message();
					if (result.status == 0) {
						clearBind();
						msg.what = UNBIND_SUCCESS;
					} else {
						msg.what = UNBIND_ERROR;
						msg.obj = result.error;
					}
					myHandler.sendMessage(msg);
				}
			}).start();
		}

		@Override
		public void cancelOnClick() {
		}
	};

	/**
	 * 刷新菜单
	 */
	public void refreshMenu() {
		if (menuAdapter != null) {
			menuUnBindUpdate(true);
		}
	}

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UNBIND_SUCCESS://
				menuUnBindUpdate(false);
				switchFragment(new UnaccreditedFatherFragment());
				Toast.makeText(mContext, R.string.unbind_success, Toast.LENGTH_SHORT).show();
				break;
			case UNBIND_ERROR:
				String error = (String) msg.obj;
				if ("father_edition_invalid_code".equals(error) || "father_edition_no_bind".equals(error)) {
					Utils.dialog(mContext, "对不起，邀请码失效，请使用最新邀请码绑定", mContext.getString(R.string.dialog_title),
							mContext.getString(R.string.ok), null, unaccredited).show();
					return;
				}
				Toast.makeText(mContext, R.string.unbind_error, Toast.LENGTH_SHORT).show();
				break;
			case UNACCREDITED_CODE:
				menuUnBindUpdate(false);
				switchFragment(new UnaccreditedFatherFragment());
				break;
			case REELECT:
				clearBind();
				menuUnBindUpdate(false);
				startActivity(new Intent(mContext, RoleSelectActivity.class));
				getActivity().finish();
				break;
			default:
				break;
			}
		}
	};

	DialogOnClickListener unaccredited = new DialogOnClickListener() {
		@Override
		public void okOnClick() {
			clearBind();
			myHandler.sendEmptyMessage(UNACCREDITED_CODE);
		}

		@Override
		public void cancelOnClick() {
		}
	};

	/**
	 * 清空绑定必要参数
	 */
	public void clearBind() {
		SharedPreferencesUtil.removeKeyArray(mContext, ShareKeys.FATHER_BIND_KEY, ShareKeys.MOM_ID_KEY,
				ShareKeys.LOGIN_STRING, ShareKeys.MOM_NICK_NAME_KEY, ShareKeys.INVITE_CODE_KEY,
				ShareKeys.TASK_ID_SAVE_KEY, ShareKeys.TASK_CACHE_KEY, ShareKeys.TASK_CACHE_TIME_KEY,
				ShareKeys.USER_ENCODE_ID, ShareKeys.NICKNAME);

	}

	/**
	 * 解除绑定时更新菜单
	 */
	public void menuUnBindUpdate(boolean isBind) {
		menuAdapter.setBinding(isBind);
		menuAdapter.updateAllMenuItem();
		menuAdapter.notifyDataSetChanged();
	}
}
