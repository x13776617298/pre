package com.babytree.apps.biz.father.ui;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.babytree.apps.biz.father.SendMessageActivity;
import com.babytree.apps.biz.father.WelcomeFatherActivity;
import com.babytree.apps.biz.father.adapter.TaskAdapter;
import com.babytree.apps.biz.father.ctr.FatherController;
import com.babytree.apps.biz.father.model.BindBoth;
import com.babytree.apps.biz.father.model.Task;
import com.babytree.apps.biz.father.model.UnReadMsg;
import com.babytree.apps.biz.father.ui.MenuFragment.DialogOnClickListener;
import com.babytree.apps.biz.father.util.Utils;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

/**
 * 爸爸版主页
 * 
 * @author gaierlin
 */
@SuppressLint("ValidFragment")
public class FatherHomeFragment extends Fragment implements OnItemClickListener {
	public static final String CALSS_NAME = FatherHomeFragment.class.getSimpleName();
	private static final String TAG = CALSS_NAME;
	private BindBoth mBindBoth;
	private PregnancyTipView mPregnancyTipView;
	private ListView mTaskList;
	private TaskAdapter mTaskAdapter;
	private ListItemBar mListHaderView;
	private AlertDialog tipDialog;
	private View reLoading;
	private Button reBut;
	private boolean isLoading = false;

	public FatherHomeFragment() {
	}

	public FatherHomeFragment(BindBoth bind) {
		setBindBoth(bind);
	}

	public void setBindBoth(BindBoth bind) {
		this.mBindBoth = bind;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View result = inflater.inflate(R.layout.father_home_fragment, null);

		reLoading = result.findViewById(R.id.reloading);
		reLoading.setOnClickListener(reload);
		reBut = (Button) result.findViewById(R.id.btn_reload);
		reBut.setOnClickListener(reload);

		mPregnancyTipView = (PregnancyTipView) result.findViewById(R.id.pregnancy_tip);
		mTaskList = (ListView) result.findViewById(R.id.list);
		mTaskAdapter = new TaskAdapter(getActivity());
		mListHaderView = (ListItemBar) inflater.inflate(R.layout.list_item_bar, null);
		mListHaderView.setVisibility(View.GONE);
		mListHaderView.setOnClickListener(reload);
		mTaskList.addFooterView(mListHaderView);
		mTaskList.setAdapter(mTaskAdapter);
		mTaskList.setOnItemClickListener(this);

		return result;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		tipDialog = Utils.dialog(getActivity(), "对不起，邀请码失效，请使用最新邀请码绑定", getActivity().getString(R.string.dialog_title),
				getActivity().getString(R.string.ok), null, unaccredited);
	}

	@Override
	public void onResume() {
		super.onResume();
		loadData();
	}

	/**
	 * 加载数据
	 */
	private void loadData() {
		String task = SharedPreferencesUtil.getStringValue(getActivity(), ShareKeys.TASK_CACHE_KEY, "");

		if (!BabytreeUtil.hasNetwork(getActivity()) && mTaskAdapter.isEmpty() && TextUtils.isEmpty(task)) {
			reLoading.setVisibility(View.VISIBLE);
			return;
		}

		reLoading.setVisibility(View.GONE);

		showListFooter(true, "正在加载提醒信息……");
		if (!TextUtils.isEmpty(task)) {
			DataResult result = FatherController.parseTasks(task);
			updateTaskList(result);
		}

		if (!isLoading) {
			new GetAllTask().executeTask();
		}
	}

	/**
	 * 获取任务
	 * 
	 * @author gaierlin
	 * 
	 */
	private class GetAllTask extends AsyncTask<String, View, Void> {
		private DataResult taskResult = null;
		private DataResult msgCoutResult = null;
		private DataResult bindReuslt = null;
		private boolean isUpdateTask = false;
		private BindBoth bindBoth = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		public void executeTask(String... params) {
			isLoading = true;
			this.execute(params);
		}

		@Override
		protected Void doInBackground(String... params) {
			String loginString = SharedPreferencesUtil.getStringValue(getActivity(), ShareKeys.LOGIN_STRING, "");
			String role = SharedPreferencesUtil.getStringValue(getActivity(), ShareKeys.APP_TYPE_KEY,
					CommConstants.APP_TYPE_DADDY);
			String code = SharedPreferencesUtil.getStringValue(getActivity(), ShareKeys.INVITE_CODE_KEY, "");

			long babyBirthadyTmp = Long.valueOf(mBindBoth.bindUser.baby_birthday_ts);

			// 取绑定
			String json = FatherController.bind(code);
			bindReuslt = FatherController.parseBindBoth(json);
			if (bindReuslt.status == 0 && bindReuslt.data != null) {// 成功
				BindBoth both = (BindBoth) bindReuslt.data;
				Context context = getActivity();
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put(ShareKeys.FATHER_BIND_KEY, json);
				map.put(ShareKeys.LOGIN_STRING, both.bindUser.login_string);
				map.put(ShareKeys.MOM_ID_KEY, both.mom_user_id);
				map.put(ShareKeys.MOM_NICK_NAME_KEY, both.bindUser.nickname);
				map.put(ShareKeys.USER_ENCODE_ID, both.father_user_id);
				map.put(ShareKeys.INVITE_CODE_KEY, code);
				map.put(ShareKeys.BABY_BIRTHDAY_TS_KEY, both.bindUser.baby_birthday_ts);

				long birthdayTimestamp = Long.parseLong(both.bindUser.baby_birthday_ts + "000");
				// 存妈妈版预产期
				map.put(ShareKeys.BIRTHDAY_TIMESTAMP, birthdayTimestamp);

				SharedPreferencesUtil.setValue(context, map);

				bindBoth = both;
			}

			// 取任务
			String tasks = FatherController.getTasks(loginString);
			taskResult = FatherController.parseTasks(tasks);
			if (taskResult.status == 0 && taskResult.data != null) {
				int time = SharedPreferencesUtil.getIntValue(getActivity(), ShareKeys.TASK_CACHE_TIME_KEY, 0);

				long babyBirthady = SharedPreferencesUtil.getLongValue(getActivity(), ShareKeys.BABY_BIRTHDAY_TS_KEY);

				if (!Utils.isThanDay(time) || babyBirthady != babyBirthadyTmp) {
					isUpdateTask = true;
					SharedPreferencesUtil.setValue(getActivity(), ShareKeys.TASK_CACHE_KEY, tasks);
					SharedPreferencesUtil.setValue(getActivity(), ShareKeys.TASK_CACHE_TIME_KEY, Calendar.getInstance(Locale.CHINA)
							.get(Calendar.DAY_OF_YEAR));
					SharedPreferencesUtil.removeKey(getActivity(), ShareKeys.TASK_ID_SAVE_KEY);
				}
			}

			// 取消息
			msgCoutResult = FatherController.getUnreadMsgCount(loginString, role);

			if (bindBoth != null) {
				mBindBoth = bindBoth;
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (isUpdateTask) {
				mTaskAdapter.clear();
				updateTaskList(taskResult);
			}

			WelcomeFatherActivity wfa = (WelcomeFatherActivity) getActivity();
			if (msgCoutResult.status == 0 && msgCoutResult.data != null) {
				UnReadMsg msg = (UnReadMsg) msgCoutResult.data;
				if (wfa != null) {
					wfa.titleBarTipMsg(false, false, msg.unread_message);
				}
			}

			mPregnancyTipView.showDate(mBindBoth);
			if (taskResult != null && taskResult.status == 1 || msgCoutResult != null && msgCoutResult.status == 1
					|| bindReuslt != null && bindReuslt.status == 1) {
				Toast.makeText(getActivity(), "亲,您的网络不给力啊", Toast.LENGTH_SHORT).show();
			}

			if ("father_edition_no_bind".equals(bindReuslt.error)
					|| "father_edition_invalid_code".equals(bindReuslt.error)) {
				if (tipDialog != null && !tipDialog.isShowing()) {
					tipDialog.show();
				}
			}
			isLoading = false;
		}
	}

	DialogOnClickListener unaccredited = new DialogOnClickListener() {
		@Override
		public void okOnClick() {
			WelcomeFatherActivity wfa = (WelcomeFatherActivity) getActivity();
			if (wfa != null) {
				wfa.invalidToAuthorize();
			}
		}

		@Override
		public void cancelOnClick() {
		}
	};

	private int memTaskPostion = 0;

	@Override
	public void onItemClick(AdapterView<?> adapter, View v, int position, long id) {
		memTaskPostion = position;
		Task task = mTaskAdapter.getItem(position);
		Intent intent = new Intent(getActivity(), SendMessageActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable("task", task);
		intent.putExtras(bundle);
		this.startActivityForResult(intent, 100);
	}

	/**
	 * 更新Adapter中的任务列表
	 * 
	 * @param taskResult
	 */
	public void updateTaskList(DataResult taskResult) {
		if (taskResult.status == 0 && taskResult.data != null) {
			showListFooter(false, "");

			mTaskAdapter.setTaskList((ArrayList<Task>) taskResult.data);
			mTaskAdapter.notifyDataSetChanged();
		} else {
			showListFooter(true, "获取提醒失败，请重新获取");
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (data == null) {
			return;
		}

		String taskIds = SharedPreferencesUtil.getStringValue(getActivity(), ShareKeys.TASK_ID_SAVE_KEY, "");
		String id = data.getStringExtra("task_id");
		if (!TextUtils.isEmpty(id)) {
			if (!TextUtils.isEmpty(taskIds)) {
				id = deleteRepeatID(id, taskIds);
			}

			SharedPreferencesUtil.setValue(getActivity(), ShareKeys.TASK_ID_SAVE_KEY, id);

			mTaskAdapter.refreshAdapterTaskIds(id);
			mTaskAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 显示ListView 的Footer脚标
	 * 
	 * @param isShow
	 * @param title
	 */
	private void showListFooter(boolean isShow, String title) {
		mListHaderView.setItemTitle(title);
		mListHaderView.setLeftImageVisible(View.GONE);
		mListHaderView.setRightImageVisible(View.GONE);
		mListHaderView.setItemTitleGravity(Gravity.CENTER);
		mListHaderView.setItemBarBg(R.drawable.task_done);
		mListHaderView.setItemTitleColor(0xff000000);
		if (isShow) {
			mListHaderView.setVisibility(View.VISIBLE);
		} else {
			mListHaderView.setVisibility(View.GONE);
		}
	}

	/**
	 * 去除指定重復的id。
	 * 
	 * @param newId
	 * @param org
	 * @param ids
	 * @return
	 */
	private static String deleteRepeatID(String newId, String ids) {
		if (ids != null && ids.length() != 0) {
			String taskId = newId.substring(0, newId.indexOf("|"));
			int pos = ids.indexOf(taskId);
			int len = (taskId.length() + 4);
			StringBuffer sb = new StringBuffer(ids);
			while (pos != -1) {
				sb.replace(pos, pos + len, "");
				pos = sb.indexOf(taskId);
			}
			sb.append("," + newId);
			return sb.toString();
		}
		return newId;
	}

	private OnClickListener reload = new OnClickListener() {
		@Override
		public void onClick(View v) {
			loadData();
		}
	};
}
