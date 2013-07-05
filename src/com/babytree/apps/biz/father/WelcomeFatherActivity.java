package com.babytree.apps.biz.father;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.babytree.apps.biz.father.ctr.FatherController;
import com.babytree.apps.biz.father.model.BindBoth;
import com.babytree.apps.biz.father.ui.FatherHomeFragment;
import com.babytree.apps.biz.father.ui.FatherTitleBar;
import com.babytree.apps.biz.father.ui.FatherTitleBar.TitleBarOnClick;
import com.babytree.apps.biz.father.ui.MenuFragment;
import com.babytree.apps.biz.father.ui.UnaccreditedFatherFragment;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ui.AllTalkListActivity;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.slidingmenu.lib.SlidingFragmentActivity;
import com.babytree.apps.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;

/**
 * 爸爸版入口Activity
 * 
 * @author gaierlin
 */
public class WelcomeFatherActivity extends SlidingFragmentActivity {
	private static final String TAG = "WelcomeFatherActivity";
	private Fragment mContent;
	private MenuFragment menuFragment;
	private FatherTitleBar mTitleBar;
	private BindBoth mBindBoth;
	private Context mContext;
	private String momId;
	private String momNickName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		if (savedInstanceState != null)// 如果上次不是正常退出
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");

		if (mContent == null) { // 根据绑定情况，展示页面。
			mContent = (isFatherBind() ? new FatherHomeFragment(mBindBoth)
					: new UnaccreditedFatherFragment());
		}

		setContentView(R.layout.content_frame);

		mTitleBar = (FatherTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitleBarOnClick(mTitleBarOnClick);
		// 主页Fragment
		changeFragment(R.id.content_frame, mContent);

		// 拖动菜单
		menuFragment = new MenuFragment();
		setBehindContentView(R.layout.menu_frame);
		changeFragment(R.id.menu_frame, menuFragment);

		// 初始化菜单项
		initSlidingMenu();

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
	}

	/**
	 * 点击菜单项中的item进行切换Fragment
	 * 
	 * @param fragment
	 */
	public void menuSwitchFragment(Fragment fragment) {
		mContent = fragment;
		changeFragment(R.id.content_frame, fragment);
		getSlidingMenu().showContent();
	}

	/**
	 * 普通切换过程
	 * 
	 * @param fragment
	 */
	public void switchFragment(Fragment fragment) {
		mContent = fragment;
		changeFragment(R.id.content_frame, fragment);
	}

	/**
	 * 曾经是否绑定成功过
	 * 
	 * @return
	 */
	public boolean isFatherBind() {
		boolean result = false;
		String str = SharedPreferencesUtil.getStringValue(
				getApplicationContext(), ShareKeys.FATHER_BIND_KEY, null);
		if (str != null) {
			DataResult tmpData = FatherController.parseBindBoth(str);
			if (tmpData.status == 0 && tmpData.data != null) {
				mBindBoth = (BindBoth) tmpData.data;
				result = true;
			}
		}
		return result;
	}

	/**
	 * 初始化拖动菜单相关参数
	 */
	private void initSlidingMenu() {
		SlidingMenu sm = getSlidingMenu();
		sm.setShadowWidthRes(R.dimen.shadow_width);
		sm.setShadowDrawable(R.drawable.shadow);
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setFadeDegree(0.35f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_FULLSCREEN);
	}

	/**
	 * 变更Fragment
	 * 
	 * @param resId
	 * @param fragment
	 */
	private void changeFragment(int resId, Fragment fragment) {
		getSupportFragmentManager().beginTransaction().replace(resId, fragment)
				.commit();
		changeTitleBar(fragment.getClass().getSimpleName());
	}

	/**
	 * TitleBar的左右Button的点击处理事件。
	 */
	private TitleBarOnClick mTitleBarOnClick = new TitleBarOnClick() {
		@Override
		public void rightButtonOnClick() {
			momId = SharedPreferencesUtil.getStringValue(mContext,
					ShareKeys.MOM_ID_KEY, null);
			momNickName = "老婆";/*SharedPreferencesUtil.getStringValue(mContext,
					ShareKeys.MOM_NICK_NAME_KEY, null);*/
			String userId = SharedPreferencesUtil.getStringValue(mContext,
					ShareKeys.USER_ENCODE_ID, null);

			if (momId == null || momNickName == null || userId == null){
				return;
			}
			System.out.println("momNickName: "+momNickName+" momId: "+momId);

			Intent intent = new Intent(mContext, AllTalkListActivity.class);
			intent.putExtra("user_encode_id", momId);
			intent.putExtra("nickname", momNickName);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			startActivity(intent);
		}

		@Override
		public void leftButtonOnClick() {
			try{
				((InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			}catch(NullPointerException e){
				e.printStackTrace();
			}
			showSlidingMenu();
		}
	};

	/**
	 * 根据fragment改变titleBar
	 * 
	 * @param fragmentName
	 *            Fragment的名字
	 */
	private void changeTitleBar(String fragmentName) {
		if (UnaccreditedFatherFragment.CALSS_NAME.equals(fragmentName)) {
			mTitleBar.setRightEnabled(false);
			mTitleBar.setShadowVisibility(View.GONE);
		} else if (FatherHomeFragment.CALSS_NAME.equals(fragmentName)) {
			mTitleBar.setRightEnabled(true);
			mTitleBar.setShadowVisibility(View.VISIBLE);
		}
	}

	/**
	 * 是否展示titleBar上的信息提示
	 * 
	 * @param isTitleLeftButton
	 *            指定TitleButton的左边还是右边的铵钮。
	 * @param isShowNum
	 *            是否在消息圈内展示数值，数量数值在100以内。
	 * @param count
	 *            消息的数量
	 */
	public void titleBarTipMsg(boolean isTitleLeftButton, boolean isShowNum,
			int count) {
		boolean isShow = (count == 0 ? false : true);
		if (isTitleLeftButton) {
			if (isShowNum) {
				mTitleBar.setLeftButtonTagNum(count);
			} else {
				mTitleBar.setLeftButtonTagNum(isShow);
			}
		} else {
			if (isShowNum) {
				mTitleBar.setRightButtonTagNum(count);
			} else {
				mTitleBar.setRightButtonTagNum(isShow);
			}

		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	/**
	 * 显示展示内容
	 */
	public void showContent() {
		getSlidingMenu().showContent();
	}

	/**
	 * 显示菜单
	 */
	public void showSlidingMenu() {
		getSlidingMenu().showMenu();
	}

	public void refreshMenu() {
		menuFragment.refreshMenu();
	}

	/**
	 * 无效邀请码，重新返回邀请页
	 */
	public void invalidToAuthorize() {
		menuFragment.clearBind();
		menuFragment.menuUnBindUpdate(false);
		changeFragment(R.id.content_frame, new UnaccreditedFatherFragment());
	}
	
	private void dialog() {
		AlertDialog.Builder exitSystemDialog = new AlertDialog.Builder(this);
		exitSystemDialog.setTitle("提示");
		exitSystemDialog.setMessage("确定退出吗?");
		exitSystemDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				finish();
				android.os.Process.killProcess(android.os.Process.myPid());

				System.exit(0);
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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && getSlidingMenu().isMenuShowing()) {
			showContent();
			return true;
		}else if(keyCode == KeyEvent.KEYCODE_BACK && !getSlidingMenu().isMenuShowing()){
			dialog();
			return true;
		}
		return false;
	}
}
