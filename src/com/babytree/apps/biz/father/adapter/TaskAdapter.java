package com.babytree.apps.biz.father.adapter;

import java.util.ArrayList;

import com.babytree.apps.biz.father.adapter.MenuAdapter.ViewHolder;
import com.babytree.apps.biz.father.model.Task;
import com.babytree.apps.biz.father.ui.ListItemBar;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class TaskAdapter extends BaseAdapter {
	private static final String TAG = "TaskAdapter";
	private ArrayList<Task> taskList;
	private static int DIGITAL_ZERO = 0;// 表示无数据。
	private static int DEFAULT_INIT_VALUE = -1;

	private Context mContext;
	private LayoutInflater mInflater;
	private int mCount = DEFAULT_INIT_VALUE;// 为-1，防止取余时被0除，而报错误。
	private String taskIds;

	public TaskAdapter(Context context) {
		this.mContext = context;
		mInflater = LayoutInflater.from(mContext);
		taskList = new ArrayList<Task>();
		taskIds = SharedPreferencesUtil.getStringValue(mContext,
				ShareKeys.TASK_ID_SAVE_KEY, "");

		Log.d(TAG, "task ids = " + taskIds);
	}

	@Override
	public int getCount() {
		int result = DIGITAL_ZERO;
		if (taskList != null && taskList.size() != DIGITAL_ZERO)
			result = taskList.size();
		mCount = (result == DIGITAL_ZERO ? DEFAULT_INIT_VALUE : result);
		return result;
	}

	@Override
	public Task getItem(int position) {
		if (isEmpty()) {// 防止取余时被0除
			return null;
		}
		return taskList.get(position % mCount);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder;
		if (convertView == null) {
			ListItemBar bar = (ListItemBar) mInflater.inflate(
					R.layout.list_item_bar, parent, false);
			convertView = bar;
			holder = new ViewHolder();
			holder.itemBar = bar;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		if (isEmpty()) {// 防止取余时被0除
			return null;
		}

		int mode = position % mCount;
		Task task = taskList.get(mode);
		holder.itemBar.setItemTitle(task.task_title);

		holder.itemBar.setBackgroundColor(0xffe2e2e2);
		String taskId = "-" + task.task_id + "-";
		int pos = taskIds.indexOf(taskId);
		String[] tmp = { "", "", "" };
		if (pos != -1) {// 完成的任务
			tmp = taskStatus(taskIds, taskId, pos);
		}

		setTaskStatus(task, tmp);

		if (task.task_status == 1) {
			holder.itemBar.setListItemLeftImage(R.drawable.done);
			holder.itemBar.setRightItemLeftImage(R.drawable.arrow_blue);
			holder.itemBar.setItemBarBg(R.drawable.task_done);
			holder.itemBar.setItemTitleColor(0xff8c8c8c);
		} else {
			holder.itemBar.setListItemLeftImage(R.drawable.undone);
			holder.itemBar.setRightItemLeftImage(R.drawable.arrow_white);
			holder.itemBar.setItemBarBg(R.drawable.task);
			holder.itemBar.setItemTitleColor(0xffffffff);
		}

		return convertView;
	}

	public void refreshAdapterTaskIds(String taskIds) {
		this.taskIds = taskIds;
		Task task = null;
		for (int i = 0; i < mCount; i++) {
			task = getItem(i);
			String taskId = "-" + task.task_id + "-";
			int pos = taskIds.indexOf(taskId);
			if (pos != -1) {
				String[] tmp = taskStatus(taskIds, taskId, pos);
				setTaskStatus(task, tmp);
				break;
			}
		}
	}

	/**
	 * 设备任务属性
	 * 
	 * @param task
	 * @param array
	 */
	private void setTaskStatus(Task task, String[] array) {

		try {
			if (TextUtils.isEmpty(array[1])) {
				task.task_status = 0;
			} else {
				task.task_status = Integer.valueOf(array[1]);
			}

			if (TextUtils.isEmpty(array[2])) {
				task.send_status = 0;
			} else {
				task.send_status = Integer.valueOf(array[2]);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String[] taskStatus(String taskIds, String taskId, int pos) {
		String subTask = taskIds.substring(pos, pos + (taskId.length() + 4));
		String[] array = { "", "", "" };
		StringBuffer sb = new StringBuffer();
		int i = 0;
		for (char c : subTask.toCharArray()) {
			if (c != '|') {
				sb.append(c);
			} else {
				array[i] = sb.toString();
				sb = new StringBuffer();
				i++;
			}
		}
		array[i] = sb.toString();
		return array;
	}

	static class ViewHolder {
		ListItemBar itemBar;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	public void setTaskList(ArrayList<Task> task) {
		if (task != null && task.size() != 0) {
			this.taskList = task;
		}
	}

	public void clear() {
		if (taskList != null){
			taskList.clear();
			taskIds="";			
		}
	}
}
