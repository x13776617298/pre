package com.babytree.apps.biz.home;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.babytree.apps.biz.father.ui.FatherTitleBar;
import com.babytree.apps.biz.father.ui.FatherTitleBar.TitleBarOnClick;
import com.babytree.apps.biz.father.ui.UnaccreditedFatherFragment;
import com.babytree.apps.biz.home.ui.MommyMenuFragment;
import com.babytree.apps.biz.notice.NoticeActivity;
import com.babytree.apps.biz.user.LoginActivity;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.babytree.apps.slidingmenu.lib.SlidingFragmentActivity;
import com.babytree.apps.slidingmenu.lib.SlidingMenu;
import com.umeng.analytics.MobclickAgent;
import com.umeng.fb.NotificationType;
import com.umeng.fb.UMFeedbackService;

/**
 * 孕期首页
 * 
 * @author pengxh
 */
public class HomePageActivity extends SlidingFragmentActivity {
	private static final String TAG = HomePageActivity.class.getSimpleName();
	private Fragment mContent;
	private MommyMenuFragment menuFragment;
	private FatherTitleBar mTitleBar;
	private Context mContext;
	private String momId;
	private String momNickName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 关闭之前多余界面
		mContext = this;
		BabytreeUtil.closeOtherActivity(mContext);

		BabytreeLog.d(TAG + " 进入孕期首页-新版");
		// 友盟自动更新
		MobclickAgent.onError(mContext);
		BabytreeUtil.checkVersionUpdate(mContext, false);
		UMFeedbackService.enableNewReplyNotification(mContext, NotificationType.NotificationBar);

		setContentView(R.layout.content_frame);

		mTitleBar = (FatherTitleBar) findViewById(R.id.title_bar);
		mTitleBar.setTitleBarOnClick(mTitleBarOnClick);
		mTitleBar.setTitleBarTopLineVisibile(View.GONE);
		mTitleBar.setShadowVisibility(View.GONE);
		mTitleBar.setTitleTextSize(23);

		// 隐藏竖条
		mTitleBar.getLeftButton().setLeftDividerLineVisibility(View.GONE);
		mTitleBar.getRightButton().setRightDividerLineVisibility(View.GONE);

		if (BabytreeUtil.isPregnancy(mContext)) { // 育儿
			mTitleBar.setTitleBarName(mContext.getResources().getString(R.string.s_app_yuer));
			mTitleBar.setBackgroundResource(R.drawable.y_title_bg);
			mTitleBar.setTitleBarBackground(R.drawable.y_title_bg);
			mTitleBar.getLeftButton().setButtonImage(R.drawable.ic_home_left_yuer);
			mTitleBar.getRightButton().setButtonImage(R.drawable.ic_home_dialogue_yuer);
			mTitleBar.getLeftButton().setRightDividerLine(R.drawable.title_bar_devider_vertical_yuer);
			mTitleBar.getRightButton().setLeftDividerLine(R.drawable.title_bar_devider_vertical_yuer);
		} else { // 孕期
			mTitleBar.setTitleBarName(mContext.getResources().getString(R.string.s_app_pregnancy));
			mTitleBar.setBackgroundResource(R.drawable.title_bg);
			mTitleBar.setTitleBarBackground(R.drawable.title_bg);
			mTitleBar.getLeftButton().setButtonImage(R.drawable.ic_home_left);
			mTitleBar.getRightButton().setButtonImage(R.drawable.ic_home_dialogue);
			mTitleBar.getLeftButton().setRightDividerLine(R.drawable.title_bar_devider_vertical);
			mTitleBar.getRightButton().setLeftDividerLine(R.drawable.title_bar_devider_vertical);
		}
		mTitleBar.setTitleBarFill();

		if (savedInstanceState != null) {// 如果上次不是正常退出
			mContent = getSupportFragmentManager().getFragment(savedInstanceState, "mContent");
			BabytreeLog.d(TAG + " onCreate-onSaveInstanceState");
		} else {
			BabytreeLog.d(TAG + " onCreate-New");
			mContent = new MainFragement(mTitleBar);
		}

		// 主页Fragment
		changeFragment(R.id.content_frame, mContent);

		// 拖动菜单
		menuFragment = new MommyMenuFragment(mTitleBar);
		setBehindContentView(R.layout.menu_frame);
		changeFragment(R.id.menu_frame, menuFragment);

		// 初始化菜单项
		initSlidingMenu();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		if (hasFocus) {
			boolean b = SharedPreferencesUtil.getBooleanValue(this, ShareKeys.HOMEPAGEMC);
			if (!b) {
				BabytreeLog.d("出现蒙层");
				SharedPreferencesUtil.setValue(this, ShareKeys.HOMEPAGEMC, true);
				BabytreeUtil.showPopWindow(mContext, mTitleBar, R.drawable.mengceng_home);
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		getSupportFragmentManager().putFragment(outState, "mContent", mContent);
		BabytreeLog.d(TAG + " onSaveInstanceState");
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
	 * 显示/隐藏 - 左侧/右侧操作按钮
	 */
	public void showOperationAction(boolean showLeft, boolean showRight) {

		// 左侧
		if (showLeft) {
			mTitleBar.getLeftButton().setVisibility(View.VISIBLE);
		} else {
			mTitleBar.getLeftButton().setVisibility(View.INVISIBLE);
		}

		// 右侧
		if (showRight) {
			mTitleBar.getRightButton().setVisibility(View.VISIBLE);
		} else {
			mTitleBar.getRightButton().setVisibility(View.INVISIBLE);
		}
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
		getSupportFragmentManager().beginTransaction().replace(resId, fragment).commit();
		changeTitleBar(fragment.getClass().getSimpleName());
	}

	/**
	 * TitleBar的左右Button的点击处理事件。
	 */
	private TitleBarOnClick mTitleBarOnClick = new TitleBarOnClick() {
		@Override
		public void rightButtonOnClick() {
			mTitleBar.setRightButtonTagNum(false, R.drawable.point);
			mTitleBar.getLeftButton();

			if (BabytreeUtil.isLogin(mContext)) {
				Intent intent = new Intent(mContext, NoticeActivity.class);
				BabytreeUtil.launch((Activity) mContext, intent, false, 0);
			} else {
				Intent intent = new Intent(mContext, LoginActivity.class);
				BabytreeUtil.launch((Activity) mContext, intent, true, 111);
			}

		}

		@Override
		public void leftButtonOnClick() {
			mTitleBar.setLeftButtonTagNum(false, R.drawable.point);
			try {
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
						getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
			} catch (NullPointerException e) {
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
		// if (UnaccreditedFatherFragment.CALSS_NAME.equals(fragmentName)) {
		// mTitleBar.setRightEnabled(false);
		// mTitleBar.setShadowVisibility(View.GONE);
		// } else if (FatherHomeFragment.CALSS_NAME.equals(fragmentName)) {
		// mTitleBar.setRightEnabled(true);
		// mTitleBar.setShadowVisibility(View.VISIBLE);
		// }
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
		BabytreeLog.d(TAG + " onPause");
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
		BabytreeLog.d(TAG + " onResume");
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		BabytreeLog.d("onActivityResult HomePageaActivity Home page Message...req = " + requestCode + " ret = "
				+ resultCode);

		if (requestCode == 111 && resultCode == 888) {
			Intent intent = new Intent(mContext, NoticeActivity.class);
			BabytreeUtil.launch((Activity) mContext, intent, false, 0);
		}
	};

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

	/**
	 * 对出对话框
	 */
	private void dialog() {
		AlertDialog.Builder exitSystemDialog = new AlertDialog.Builder(mContext);
		exitSystemDialog.setTitle(R.string.dialog_title);
		exitSystemDialog.setMessage(R.string.s_dialog_exit_msg);
		exitSystemDialog.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface arg0, int arg1) {
				arg0.dismiss();
				// finish();
				android.os.Process.killProcess(android.os.Process.myPid());
				System.exit(0);
			}
		});
		exitSystemDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
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
		} else if (keyCode == KeyEvent.KEYCODE_BACK && !getSlidingMenu().isMenuShowing()) {
			dialog();
			return true;
		}
		return false;
	}

}
