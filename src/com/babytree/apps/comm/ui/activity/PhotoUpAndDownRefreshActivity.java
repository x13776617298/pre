package com.babytree.apps.comm.ui.activity;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.comm.net.BabytreeAsyncTask;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.util.DataResult;
import com.example.droidfusdklib.R;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnDownUpRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.internal.BabyTreeBaseAdapter;
import com.handmark.pulltorefresh.library.internal.ListFooterView;

/**
 * 上拉下拉刷新框架(带拍照功能)
 * 
 * @author wangbingqi
 * 
 */
public abstract class PhotoUpAndDownRefreshActivity<T> extends BabytreePhotographActivity implements
		OnDownUpRefreshListener<ListView>, OnItemClickListener, OnClickListener {

	private PullToRefreshListView mPullRefreshListView;

	private AsyncTask<String, Integer, DataResult> dataAsyncTask;

	private BabyTreeBaseAdapter<T> mBaseAdapter;

	private ListFooterView loadingView;

	private TextView emptyView;

	/**
	 * 是否第一次加载
	 */
	private boolean isFirstLoading = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPullRefreshListView = (PullToRefreshListView) findViewById(R.id.pull_refresh_list);
		loadingView = (ListFooterView) LayoutInflater.from(this).inflate(R.layout.list_footer_view, null);
		loadingView.setGravity(Gravity.CENTER);
		emptyView = new TextView(PhotoUpAndDownRefreshActivity.this);
		emptyView.setGravity(Gravity.CENTER);
		emptyView.setOnClickListener(this);
		mPullRefreshListView.setMode(onCreate());
		initAdapter(getAdapte());
		onCreateEnd();
	}

	/**
	 * 初始化 adapter
	 * 
	 * @param adapter
	 */
	private void initAdapter(BabyTreeBaseAdapter<T> adapter) {
		this.mBaseAdapter = adapter;
		mPullRefreshListView.setAdapter(mBaseAdapter);
		mPullRefreshListView.setOnRefreshListener(this);
		mPullRefreshListView.setOnItemClickListener(this);
	}

	/**
	 * 设置item颜色
	 * 
	 * @param imgId
	 */
	protected final void setDivider(int imgId) {
		if (imgId == 0) {
			mPullRefreshListView.getRefreshableView().setDivider(null);
		} else {
			mPullRefreshListView.getRefreshableView().setDivider(this.getResources().getDrawable(imgId));
		}
	}

	/**
	 * 设置item之间的DividerHeight
	 * 
	 * @param height
	 */
	protected final void setDividerHeight(int height) {
		mPullRefreshListView.getRefreshableView().setDividerHeight(height);
	}

	/**
	 * 设置是否是第一次加载
	 * 
	 * @param isb
	 */
	protected final void setFirsLoading(boolean isb) {
		this.isFirstLoading = isb;
	}
	
	/**
	 * 设置是否是第一次加载
	 * 
	 * @param isb
	 */
	protected final boolean isFirsLoading() {
		return isFirstLoading;
	}

	@Override
	public void onDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		BabytreeLog.i("onDownToRefresh");
		if (!isFirstLoading) {
			onDownRefresh();
		}
	}

	@Override
	public void onUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		BabytreeLog.i("onUpToRefresh");
		if (!isFirstLoading) {
			onUpRefresh();
		}
	}

	/**
	 * 设置适配器
	 * 
	 * @return
	 */
	protected abstract BabyTreeBaseAdapter<T> getAdapte();

	/**
	 * 初始化
	 * 
	 */
	protected abstract Mode onCreate();

	/**
	 * 加载结束
	 */
	protected void onCreateEnd() {
		onNetStart();
	};

	/**
	 * 下拉刷新事件
	 */
	protected abstract void onDownRefresh();

	/**
	 * 上拉刷新时间
	 */
	protected abstract void onUpRefresh();

	/**
	 * 加载数据
	 */
	protected final void onNetStart() {
		dataAsyncTask = new DownRefreshAsyncTask(this);
		dataAsyncTask.execute();
	}

	/**
	 * 刷新数据
	 */
	protected final void onRefresh() {
		mPullRefreshListView.removeOldEmptyView(loadingView);
		mPullRefreshListView.setDataLoadingState(false);
		mBaseAdapter.notifyDataSetChanged();
		mPullRefreshListView.onRefreshComplete();
	}

	/**
	 * 加载结束关闭刷新动画
	 */
	protected final void onEndRefresh() {
		mPullRefreshListView.setDataLoadingState(false);
		mPullRefreshListView.removeOldEmptyView(loadingView);
		mPullRefreshListView.onRefreshComplete();
	}

	/**
	 * 清除数据
	 */
	protected final void clearData() {
		mBaseAdapter.clear();
	}
	
	/**
	 * 清除数据
	 */
	protected final void notifyDataSetChanged() {
		mBaseAdapter.notifyDataSetChanged();
	}

	/**
	 * 设置数据
	 * 
	 * @param data
	 */
	protected final void setData(List<T> data) {
		mBaseAdapter.setData(data);
	}

	/**
	 * 联网成功
	 * 
	 * @param result
	 */
	protected abstract void success(DataResult result);

	/**
	 * 获取联网数据
	 * 
	 * @return
	 */
	protected abstract DataResult getDataResult();

	/**
	 * 联网失败
	 * 
	 * @param result
	 */
	protected void failure(DataResult result) {
		if (result.message.equalsIgnoreCase("")) {
			result.message = "加载失败";
		}
		Toast.makeText(PhotoUpAndDownRefreshActivity.this, result.message, Toast.LENGTH_SHORT).show();
		if (isFirstLoading) {
			emptyView.setText(result.message + "\n点击重新加载");
			mPullRefreshListView.removeOldEmptyView(loadingView);
			mPullRefreshListView.setEmptyView(emptyView);
		}
		onEndRefresh();
	}

	/**
	 * 设置联网请求文字
	 * 
	 * @return
	 */
	protected String getDialogMessage() {
		return "";
	}

	/**
	 * 列表
	 * 
	 * @author wangbingqi
	 * 
	 */
	final class DownRefreshAsyncTask extends BabytreeAsyncTask {

		public DownRefreshAsyncTask(Context context) {
			super(context);
			// 第一次加载设置
			if (isFirstLoading) {
				mPullRefreshListView.removeOldEmptyView(emptyView);
				mPullRefreshListView.setEmptyView(loadingView);
			}
		}

		@Override
		protected DataResult toNet(String[] params) {
			return PhotoUpAndDownRefreshActivity.this.getDataResult();
		}

		@Override
		protected void success(DataResult result) {
			PhotoUpAndDownRefreshActivity.this.success(result);
			isFirstLoading = false;
		}

		@Override
		protected void failure(DataResult result) {
			PhotoUpAndDownRefreshActivity.this.failure(result);
		}

		@Override
		protected String getDialogMessage() {
			return PhotoUpAndDownRefreshActivity.this.getDialogMessage();
		}

	}

	@Override
	public int getBodyView() {
		return R.layout.babytree_list_view;
	}

	/**
	 * 获取PullToRefreshListView
	 * 
	 * @return
	 */
	public PullToRefreshListView getPullRefreshListView() {
		return mPullRefreshListView;
	}

	/**
	 * 得到ListFooterView的加载view
	 * 
	 * @return
	 */
	public ListFooterView getLoadingView() {
		return loadingView;
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		
		if(v == emptyView){
			onNetStart();
			mPullRefreshListView.removeOldEmptyView(emptyView);
		}
	}
	
	public void showEmptyView(){
		mPullRefreshListView.removeOldEmptyView(loadingView);
		emptyView.setText("数据为空");
		mPullRefreshListView.setEmptyView(emptyView);
	}
}
