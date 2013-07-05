package com.babytree.apps.biz.notice;

import java.util.ArrayList;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.babytree.apps.biz.notice.adapter.NoticeAdapter;
import com.babytree.apps.biz.notice.ctr.NoticeController;
import com.babytree.apps.biz.notice.model.UserMessageListBean;
import com.babytree.apps.comm.net.BabytreeAsyncTask;
import com.babytree.apps.comm.ui.AllTalkListActivity;
import com.babytree.apps.comm.ui.activity.UpAndDownRefreshActivity;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.pregnancy.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.internal.BabyTreeBaseAdapter;

/**
 * 消息列表
 * 
 */
public class NoticeActivity extends UpAndDownRefreshActivity<UserMessageListBean> implements OnItemLongClickListener {

	/**
	 * 分页大小
	 */
	private static int LIMIT = 100;

	private int pageNo = 1;

	private String mLoginString;

	private NoticeAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mLoginString = getLoginString();
	}

	@Override
	public void setLeftButton(Button button) {

	}

	@Override
	public void setRightButton(Button button) {
		button.setVisibility(View.GONE);
	}

	@Override
	public String getTitleString() {
		return "消息列表";
	}

	@Override
	protected BabyTreeBaseAdapter<UserMessageListBean> getAdapte() {
		mAdapter = new NoticeAdapter(mContext);
		return mAdapter;
	}

	@Override
	protected Mode onCreate() {
		setDivider(R.drawable.c_notice_list_divider);
		setListViewSelector(R.drawable.c_notice_list_divider);
		getPullRefreshListView().setOnItemClickListener(this);
		getPullRefreshListView().getRefreshableView().setOnItemLongClickListener(this);
		getPullRefreshListView().getLoadingLayoutProxy().setPullLabel("下拉刷新");
		return Mode.PULL_FROM_START;
	}

	@Override
	protected void onDownRefresh() {
		pageNo = 1;
		onNetStart();

	}

	@Override
	protected void onUpRefresh() {
		pageNo++;
		onNetStart();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void success(DataResult result) {
		ArrayList<UserMessageListBean> list = (ArrayList<UserMessageListBean>) result.data;
		if (list.isEmpty()) {
			onEndRefresh();
			showEmptyView();
		} else {
			if (pageNo == 1) {
				clearData();
			}
			setData(list);
			onRefresh();
		}
	}

	@Override
	protected void failure(DataResult result) {
		super.failure(result);
		if (pageNo != 1)
			pageNo--;
	}

	@Override
	protected DataResult getDataResult() {
		return NoticeController.toNotice(mLoginString, getStart(pageNo), LIMIT);
	}

	private int getStart(int p) {
		if (p == 1) {
			return 0;
		} else {
			return (p - 1) * LIMIT;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		// 包含头部Positon-1
		position = position - 1;
		Resources r = this.getResources();
		String message = "是否删除与该用户的聊天记录?";
		String textLeft = r.getString(R.string.sure);
		final UserMessageListBean bean = mAdapter.getItem(position);
		DialogInterface.OnClickListener leftListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// 删除操作
				new DelUserMessageTask(mContext, bean).execute(getLoginString(), bean.user_encode_id);
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
		return false;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

		super.onItemClick(parent, view, position, id);

		// 包含头部Positon-1
		position = position - 1;

		UserMessageListBean bean = mAdapter.getItem(position);

		// 跳转到用户聊天页面
		Intent intent = new Intent(mContext, AllTalkListActivity.class);
		intent.putExtra("user_encode_id", bean.user_encode_id);
		intent.putExtra("nickname", bean.nickname);

		startActivity(intent);

	}

	/**
	 * 删除消息
	 */
	private class DelUserMessageTask extends BabytreeAsyncTask {
		private UserMessageListBean mUserMessageListBean;

		public DelUserMessageTask(Context context, UserMessageListBean bean) {
			super(context);

			mUserMessageListBean = bean;
		}

		@Override
		protected DataResult toNet(String[] params) {
			return NoticeController.delAllUserMessage(params[0], params[1]);
		}

		@Override
		protected void success(DataResult result) {
			mAdapter.removeData(mUserMessageListBean);
			mAdapter.notifyDataSetChanged();
		}

		@Override
		protected void failure(DataResult result) {
			Toast.makeText(mContext, result.message, Toast.LENGTH_SHORT).show();
		}
	}

}
