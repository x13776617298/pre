package com.babytree.apps.biz.home.ui;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.biz.center.MainCenterFragement;
import com.babytree.apps.biz.father.RoleSelectActivity;
import com.babytree.apps.biz.father.ctr.FatherController;
import com.babytree.apps.biz.father.ui.FatherTitleBar;
import com.babytree.apps.biz.home.HomePageActivity;
import com.babytree.apps.biz.home.MainFragement;
import com.babytree.apps.biz.home.adapter.HomeNotifyListAdapter;
import com.babytree.apps.biz.home.adapter.MommyMenuAdapter;
import com.babytree.apps.biz.home.ctr.HomeController;
import com.babytree.apps.biz.home.model.Notify;
import com.babytree.apps.biz.setting.SettingFragment;
import com.babytree.apps.biz.setting.Y_SettingFragment;
import com.babytree.apps.biz.topic.TopicNewActivity;
import com.babytree.apps.biz.user.LoginActivity;
import com.babytree.apps.biz.welcome.Y_WelcomeActivity;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.model.TopicComment;
import com.babytree.apps.comm.model.TopicReply;
import com.babytree.apps.comm.net.BabytreeAsyncTask;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.comm.view.BabyTreeFragment;
import com.babytree.apps.pregnancy.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;

/**
 * 侧边栏菜单-新版首页-妈妈版
 * 
 * @author pengxh
 */
@SuppressLint("ValidFragment")
public class MommyMenuFragment extends BabyTreeFragment implements OnClickListener {
	private static final String TAG = MommyMenuFragment.class.getSimpleName();
	private static final int UNBIND_SUCCESS = 1;
	private static final int UNBIND_ERROR = 2;
	private static final int UNACCREDITED_CODE = 3;
	private static final int REELECT = 4;
	private MommyMenuAdapter menuAdapter;
	private HomeNotifyListAdapter notifyListAdapter;
	private ListView listViewMenu;
	private ListView listViewNotify;
	private NotifyHeaderView headerView;

	private boolean resumeLoaded = false;

	/**
	 * 加载失败文本提示
	 */
	private TextView mFailedTextView;
	private PullToRefreshScrollView mPullToRefreshScrollView;

	/**
	 * 退出登录广播
	 */
	private LogoutBroadcast mLogoutBroadcast;

	/**
	 * 退出广播action
	 */
	public static final String ACTION_LOGOUT = "com.babytree.apps.pregnancy.logout";

	/**
	 * 登录广播action
	 */
	public static final String ACTION_LOGIN = "com.babytree.apps.pregnancy.login";

	/**
	 * 刷新消息通知action
	 */
	public static final String ACTION_NOTICE_REFRESH = "com.babytree.apps.pregnancy.notice.refresh";

	/**
	 * 预产期变动
	 */
	public static final String ACTION_PRENANCY_CHANGED = "com.babytree.apps.pregnancy.prenancy.changed";

	/**
	 * 当前列表的数据页码
	 */
	private int pageNo = 1;

	/**
	 * 当前消息列表所在的行
	 */
	private int scrollY = 0;

	/**
	 * 当前消息列表所在的行
	 */
	private int lastVisibleItemPosition = 0;

	/**
	 * 通知列表
	 */
	private ArrayList<Notify> notifyList = new ArrayList<Notify>();

	/**
	 * bar 条
	 */
	private FatherTitleBar mTitleBar;

	/**
	 * 首页Fragement - 孕期/育儿
	 */
	private MainFragement mainFragement;

	/**
	 * 孕期设置Fragement
	 */
	private SettingFragment mSettingFragement;

	/**
	 * 育儿设置Fragement
	 */
	private Y_SettingFragment mSettingFragement_Y;

	/**
	 * 用户中心Fragement
	 */
	private MainCenterFragement mUserCenterFragement;

	public MommyMenuFragment(FatherTitleBar titleBar) {
		this.mTitleBar = titleBar;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		BabytreeLog.d(TAG + " onCreate");
		initLogoutBroadcast();// 注册广播
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		BabytreeLog.d(TAG + " onCreateView");
		View view = inflater.inflate(R.layout.slide_menu_mommy, null);
		mPullToRefreshScrollView = (PullToRefreshScrollView) view;
		// mPullToRefreshScrollView.setMode(Mode.PULL_UP_TO_REFRESH);
		mPullToRefreshScrollView.setOnRefreshListener(downUpRefreshListener);
		listViewMenu = (ListView) view.findViewById(R.id.slide_menu_list);
		listViewNotify = (ListView) view.findViewById(R.id.slide_menu_list_notify);
		headerView = (NotifyHeaderView) view.findViewById(R.id.slide_menu_list_header_view);
		mFailedTextView = (TextView) view.findViewById(R.id.notify_fail);
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		BabytreeLog.d(TAG + " onActivityCreated");
		menuAdapter = new MommyMenuAdapter(mContext);
		listViewMenu.setAdapter(menuAdapter);
		headerView.setEnabled(false);
		// listViewNotify.addHeaderView(headerView);
		listViewMenu.setOnItemClickListener(onItemClickListener);
		notifyListAdapter = new HomeNotifyListAdapter(mContext, notifyList);
		listViewNotify.setOnItemClickListener(onItemClickListener);
		listViewNotify.setOnScrollListener(new AbsListView.OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				lastVisibleItemPosition = (visibleItemCount > 1) ? (visibleItemCount - 1) : visibleItemCount;
				BabytreeLog.d("正在滚动 firstVisibleItem = " + firstVisibleItem + " - " + visibleItemCount + " - "
						+ totalItemCount + " - " + lastVisibleItemPosition);
			}
		});
		mFailedTextView.setOnClickListener(this);
		if (BabytreeUtil.isLogin(mContext)) {

			mPullToRefreshScrollView.setMode(Mode.PULL_UP_TO_REFRESH);

			getCommentReplyList(pageNo); // 防止用户在使用过程中切换登录早成数据错误问题
			// menuAdapter.updateMenuNickNameItem();// 刷新用户item (用户头像和用户昵称)
		} else {
			mPullToRefreshScrollView.setMode(Mode.DISABLED);
			headerView.setVisibility(View.INVISIBLE);
			BabytreeLog.d(TAG + " onResume " + resumeLoaded + " -- " + "未登录 不加载数据  不设置上拉属性");
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		BabytreeLog.d(TAG + " onResume");
		if (BabytreeUtil.isLogin(mContext)) {

			mPullToRefreshScrollView.setMode(Mode.PULL_UP_TO_REFRESH);

			// if (!resumeLoaded) { // 加载消息列表只启用一次 - 逻辑需求
			// BabytreeLog.d(TAG + " onResume " + resumeLoaded + " -- " +
			// "登录 加载一次数据 设置为true");
			// getCommentReplyList(pageNo); // 防止用户在使用过程中切换登录早成数据错误问题
			// menuAdapter.updateMenuNickNameItem();// 刷新用户item (用户头像和用户昵称)
			// resumeLoaded = true;
			// } else {
			// BabytreeLog.d(TAG + " onResume " + resumeLoaded + " -- " +
			// "登录 加载过一次数据 设置为true " + pageNo);
			// }
		} else {
			mPullToRefreshScrollView.setMode(Mode.DISABLED);
			headerView.setVisibility(View.INVISIBLE);
			BabytreeLog.d(TAG + " onResume " + resumeLoaded + " -- " + "未登录 不加载数据  不设置上拉属性");
		}
		// if (BabytreeUtil.isLogin(mContext)) {
		// headerView.setVisibility(View.VISIBLE);
		// } else {
		// headerView.setVisibility(View.GONE);
		// }
	}

	public void onPause() {
		super.onPause();
		BabytreeLog.d(TAG + " onPause");
	};

	@Override
	public void onStop() {
		// pageNo = 1;// 从别的页面回来重新刷新
		// notifyList.clear();
		BabytreeLog.d(TAG + " onStop");
		super.onStop();
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(mContext.getApplicationContext()).unregisterReceiver(mLogoutBroadcast);
	}

	AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> lv, View v, int position, long id) {
			// TODO Auto-generated method stub
			switch (lv.getId()) {
			case R.id.slide_menu_list: // 菜单导航列表
				doLeftMenu(position);
				break;
			case R.id.slide_menu_list_notify:// 菜单消息通知列表
				doNotifyList(position);
				break;

			default:
				break;
			}
		}

		/**
		 * 处理通知列表
		 * 
		 * @param position
		 */
		public void doNotifyList(int position) {
			BabytreeLog.d(TAG + " 菜单消息通知列表 - slide_menu_list_notify, position = " + position);
			Notify notify = notifyList.get(position);
			switch (notify.getType()) {
			case 1:
				TopicReply topicReply = (TopicReply) notify.getData();

				TopicNewActivity.launch(mContext, Integer.parseInt(topicReply.topic_id),
						Integer.parseInt(topicReply.topic_reply_page));

				break;

			case 2:
				TopicComment topicComment = (TopicComment) notify.getData();

				TopicNewActivity.launch(mContext, Integer.parseInt(topicComment.topic_id),
						Integer.parseInt(topicComment.topic_reply_page));

				break;

			default:
				break;
			}
		}

		/**
		 * 处理左侧菜单
		 * 
		 * @param position
		 */
		public void doLeftMenu(int position) {
			BabytreeLog.d(TAG + " 菜单导航列表 - slide_menu_list, position = " + position);
			// menuAdapter.updateMenuUIItem(position);
			menuAdapter.setSelectItemPosition(position);
			switch (position) {
			case 0:// 首页
				goHomeFragement();
				break;
			case 1:// 用户中心
				goUserCenter();
				break;
			case 2:// 切换到育儿
				int p = BabytreeUtil.getPregnancyWeeks(mContext);
				if (BabytreeUtil.isPregnancy(mContext)) {
					goSettingFragement();
				} else {
					if (p < 36) {
						goSettingFragement();
					} else {
						goYuer();
					}
				}
				break;
			case 3:// 设置
				goSettingFragement();
				break;
			default:
				break;
			}
		}
	};

	PullToRefreshBase.OnDownUpRefreshListener downUpRefreshListener = new PullToRefreshBase.OnDownUpRefreshListener<View>() {

		@Override
		public void onDownToRefresh(PullToRefreshBase<View> refreshView) {
			BabytreeLog.d(TAG + " onDownToRefresh");
		}

		@Override
		public void onUpToRefresh(PullToRefreshBase<View> refreshView) {
			BabytreeLog.d(TAG + " onUpToRefresh");
			getCommentReplyList(++pageNo);
		}
	};

	/**
	 * 数据加载完成
	 */
	private void onRefreshComplete() {
		mPullToRefreshScrollView.onRefreshComplete();
		BabytreeLog.d(TAG + "onRefreshComplete 当前页码 - " + pageNo);
	}

	/**
	 * 切换到育儿
	 */
	private void goYuer() {
		Resources r = mContext.getResources();
		String message = r.getString(R.string.s_switch_2_yuer);
		String textLeft = r.getString(R.string.sure);
		DialogInterface.OnClickListener leftListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {

				SharedPreferencesUtil.setValue(mContext, ShareKeys.IS_PREGNANCY, true);
				Intent intent = new Intent(mContext, Y_WelcomeActivity.class);
				intent.putExtra("is_from_change_btn", "yes");
				// 关闭其它界面 TODO
				// closeOtherActivity();
				BabytreeUtil.launch((Activity) mContext, intent, false, 0);
				MommyMenuFragment.this.getActivity().finish();
			}
		};
		String textRight = r.getString(R.string.dialog_cancle);
		DialogInterface.OnClickListener rightListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		};
		// 显示普通对话框
		showAlertDialog("", message, null, textLeft, leftListener, textRight, rightListener);
	}

	/**
	 * 切换到设置Fragement
	 */
	private void goSettingFragement() {
		if (BabytreeUtil.isPregnancy(mContext)) {
			if (mSettingFragement_Y == null) {
				BabytreeLog.d(TAG + " create - 育儿设置 Frg");
				mSettingFragement_Y = new Y_SettingFragment();
			}
			switchFragment(mSettingFragement_Y);
		} else {
			if (mSettingFragement == null) {
				BabytreeLog.d(TAG + " create - 孕期设置 Frg");
				mSettingFragement = new SettingFragment();
			}
			switchFragment(mSettingFragement);
		}
	}

	/**
	 * 切换到首页Fragement
	 */
	private void goHomeFragement() {
		if (mainFragement == null) {
			BabytreeLog.d(TAG + " create - 首页 Frg");
			mainFragement = new MainFragement(mTitleBar);
		}
		switchFragment(mainFragement);
	}

	/**
	 * 个人中心Fragement
	 */
	private void goUserCenter() {
		if (BabytreeUtil.isLogin(mContext)) {
			if (mUserCenterFragement == null) {
				BabytreeLog.d(TAG + " create - 个人中心 Frg");
				mUserCenterFragement = new MainCenterFragement();
			}
			switchFragment(mUserCenterFragement);
		} else {
			Intent intent = new Intent(mContext, LoginActivity.class);
			BabytreeUtil.launch(getActivity(), intent, true, 111);
		}
	}

	/**
	 * 切换Fragment
	 * 
	 * @param fragment
	 */
	private void switchFragment(Fragment fragment) {
		if (mContext == null)
			return;

		if (mContext instanceof HomePageActivity) {
			HomePageActivity homePageActivity = (HomePageActivity) mContext;

			// 首页显示左右侧操作按钮
			if (fragment instanceof MainFragement) { // 首页显示左右操作
				homePageActivity.showOperationAction(true, true);
			} else {
				homePageActivity.showOperationAction(true, false);
			}

			homePageActivity.menuSwitchFragment(fragment);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.notify_fail:
			getCommentReplyList(pageNo);
			break;

		default:
			break;
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

	/**
	 * 刷新所有菜单项
	 * 
	 * @param context
	 */
	public void refreshAllMenuItem(Context context) {
		if (menuAdapter != null) {
			menuAdapter.refreshAllMenuItem(context);
			menuAdapter.notifyDataSetChanged();
		}
	}

	private Handler myHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case UNBIND_SUCCESS://
				menuUnBindUpdate(false);
				Toast.makeText(mContext, R.string.unbind_success, Toast.LENGTH_SHORT).show();
				break;
			case UNBIND_ERROR:
				String error = (String) msg.obj;
				if ("father_edition_invalid_code".equals(error) || "father_edition_no_bind".equals(error)) {
					return;
				}
				Toast.makeText(mContext, R.string.unbind_error, Toast.LENGTH_SHORT).show();
				break;
			case UNACCREDITED_CODE:
				menuUnBindUpdate(false);
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

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		BabytreeLog.d("onActivityResult MommyMenuFragement to invite Login...req = " + requestCode + " ret = "
				+ resultCode);

		if (requestCode == 111 && resultCode == 888) {
			// loginStr = SharedPreferencesUtil.getStringValue(mContext,
			// ShareKeys.LOGIN_STRING);
			goUserCenter();// 切换到用户中心
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
				ShareKeys.USER_ENCODE_ID);

	}

	/**
	 * 解除绑定时更新菜单
	 */
	public void menuUnBindUpdate(boolean isBind) {
		// menuAdapter.setBinding(isBind);
		// menuAdapter.updateAllMenuItem();
		menuAdapter.notifyDataSetChanged();
	}

	/**
	 * 获取评论回复列表
	 */
	private void getCommentReplyList(int pageNo) {

		if (BabytreeUtil.isLogin(mContext)) {
			BabytreeLog.d(TAG + " 刷新消息列表 - " + notifyList.size());
			new CommentReplyListTask(mContext).execute(getLoginString(), pageNo + "");
		} else {
			showNotifyList(View.GONE);
			headerView.setVisibility(View.GONE);
		}
	}

	/**
	 * 评论回复列表
	 */
	private class CommentReplyListTask extends BabytreeAsyncTask {

		public CommentReplyListTask(Context context) {
			super(context);
		}

		@Override
		protected void onPreExecute() {
			scrollY = mPullToRefreshScrollView.getScrollY();
			BabytreeLog.d("滚动的坐标Y = " + scrollY);
			headerView.setVisibility(View.VISIBLE);
			headerView.setProgressVisiable(View.VISIBLE);
			mFailedTextView.setText(mContext.getResources().getString(R.string.s_home_loading));
		}

		@Override
		protected DataResult toNet(String[] params) {
			int pageNum = 1;
			try {
				pageNum = Integer.parseInt(params[1]);
			} catch (NumberFormatException e) {
				pageNum = 1;
			}
			return HomeController.getMessageListForCommentReply(params[0], pageNum);
		}

		@Override
		protected void success(DataResult result) {
			try {
				ArrayList<Notify> notifyTmp = (ArrayList<Notify>) result.data;
				notifyList.addAll(notifyTmp);
				BabytreeLog.d("首页评论回复列表成功" + notifyList.toString());
				onRefreshComplete();// 刷新完成
				notifyListAdapter = new HomeNotifyListAdapter(mContext, notifyList);
				listViewNotify.setAdapter(notifyListAdapter);
				showNotifyList(View.VISIBLE);
				mFailedTextView.setVisibility(View.GONE);
				headerView.setVisibility(View.VISIBLE);
				headerView.setProgressVisiable(View.GONE);
				notifyListAdapter.notifyDataSetChanged();
				// listViewNotify.setSelectionFromTop(0, 0);
				listViewNotify.setSelection(lastVisibleItemPosition);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void failure(DataResult result) {
			--pageNo;// 加载失败恢复页码值
			pageNo = (pageNo == 0) ? 1 : pageNo; // 防止页码出现为0的情况，api不返回数据
			headerView.setProgressVisiable(View.GONE);
			headerView.setVisibility(View.VISIBLE);
			showNotifyList(View.GONE);
			mFailedTextView.setText(mContext.getResources().getString(R.string.s_home_load_fail));
			mFailedTextView.setVisibility(View.VISIBLE);
			listViewNotify.setVisibility(View.GONE);
			onRefreshComplete();// 刷新完成
		}

		@Override
		protected String getDialogMessage() {
			return "";
		}
	}

	/**
	 * 显示消息通知列表
	 */
	private void showNotifyList(int visible) {
		listViewNotify.setVisibility(visible);
	}

	/**
	 * 退出登录广播
	 * 
	 * @author pengxh
	 * 
	 */
	public class LogoutBroadcast extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equalsIgnoreCase(ACTION_LOGOUT)) {
				BabytreeLog.d("已收到退出登录的广播 - 开始刷新状态");
				menuAdapter.updateMenuNickNameItem(); // 刷新菜单中用户信息
				notifyList.clear();
				notifyListAdapter.notifyDataSetChanged();
				headerView.setVisibility(View.INVISIBLE);
				showNotifyList(View.INVISIBLE);
				mPullToRefreshScrollView.setMode(Mode.DISABLED);
				resumeLoaded = false;
			} else if (action.equalsIgnoreCase(ACTION_LOGIN)) {
				BabytreeLog.d("已收到登录成功的广播 - 开始刷新状态");
				menuAdapter.updateMenuNickNameItem(); // 刷新菜单中用户信息
				pageNo = 1;
				headerView.setVisibility(View.VISIBLE);
				getCommentReplyList(pageNo);// 刷新通知列表
			} else if (action.equalsIgnoreCase(ACTION_PRENANCY_CHANGED)) {
				refreshAllMenuItem(mContext);
				BabytreeLog.d("已收到预产期变更的广播 - 开始刷新菜单状态 End");
			} else if (action.equalsIgnoreCase(ACTION_NOTICE_REFRESH)) {
				BabytreeLog.d("已收到刷新消息列表的广播 - 开始刷新消息列表");
				pageNo = 1;
				notifyList.clear();
				headerView.setVisibility(View.VISIBLE);
				getCommentReplyList(pageNo);
			} else {

			}
		}

	}

	/**
	 * 初始化广播
	 * <p>
	 * 用来刷新首页左侧菜单用户信息项
	 */
	private void initLogoutBroadcast() {
		BabytreeLog.d("注册用户退出登录广播....");
		mLogoutBroadcast = new LogoutBroadcast();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_LOGOUT);// 注销
		filter.addAction(ACTION_LOGIN);// 登录
		filter.addAction(ACTION_PRENANCY_CHANGED);// 预产期变动
		filter.addAction(ACTION_NOTICE_REFRESH);// 左侧消息列表有新消息
		LocalBroadcastManager.getInstance(mApplication).registerReceiver(mLogoutBroadcast, filter);

	}

}