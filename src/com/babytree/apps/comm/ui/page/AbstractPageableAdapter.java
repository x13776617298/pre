package com.babytree.apps.comm.ui.page;

import com.babytree.apps.comm.ui.page.AbstractDataLoaderHandler.DataLoadedCallback;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.comm.util.BabytreeUtil;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public abstract class AbstractPageableAdapter<T> extends BaseAdapter implements
		AbsListView.OnScrollListener, DataLoadedCallback<T> {

	private AbstractDataLoaderHandler<T> mDataLoaderHandler;

	private PullToRefreshListView mListView;

	private View mLoadingView;

	private View mReloadingView;

	private Context mContext;

	private ArrayList<T> mList = new ArrayList<T>();

	public ArrayList<T> getmList() {
		return mList;
	}

	public void setmList(ArrayList<T> mList) {
		this.mList = mList;
	}

	/**
	 * @param listView
	 *            The list view that this adapter will be added to.
	 * @param context
	 *            The activity context.
	 * @param loadingViewResourceId
	 *            The layout to use when displaying the "busy" status
	 * @param handler
	 *            The handler to use when loading more data to the list view.
	 */
	public AbstractPageableAdapter(PullToRefreshListView listView,
			Context context, int loadingViewResourceId,
			int reloadViewResourceId, AbstractDataLoaderHandler<T> handler) {
		mContext = context;
		mDataLoaderHandler = handler;
		mListView = listView;
		mLoadingView = LayoutInflater.from(context).inflate(
				loadingViewResourceId, null);
		mReloadingView = LayoutInflater.from(context).inflate(
				reloadViewResourceId, null);
		showLoading(true);
		mDataLoaderHandler.getValues(this);
	}

	/**
	 * The {@link Context} passed in the constructor.
	 * 
	 * @return
	 */
	public final Context getContext() {
		return mContext;
	}

	public final int getCount() {
		return mList.size();
	}

	public Object getItem(int position) {
		return mList.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// Nothing to do I suppose...
		if (totalItemCount <= 0) {
			return;
		}
		if (firstVisibleItem + visibleItemCount >= totalItemCount) {
			final int maxItems = mDataLoaderHandler.getMaxItems();
			final int first = mListView.getRefreshableView()
					.getFirstVisiblePosition();
			final int count = mListView.getRefreshableView().getChildCount();
			final int total = getCount();

			if (first + count < total || mDataLoaderHandler.isLoading())
				return;

			if (total < maxItems) {
				showLoading(true);
				mDataLoaderHandler.getNext(this);
			}
		}
	}

	public void onScrollStateChanged(final AbsListView view,
			final int scrollState) {
		// switch (scrollState) {
		// case SCROLL_STATE_IDLE: {
		// if (view.getLastVisiblePosition() == (view.getCount() - 1)) {
		// final int maxItems = mDataLoaderHandler.getMaxItems();
		// final int first = mListView.getFirstVisiblePosition();
		// final int count = mListView.getChildCount();
		// final int total = getCount();
		//
		// if (first + count < total || mDataLoaderHandler.isLoading())
		// return;
		//
		// if (total < maxItems) {
		// showLoading(true);
		// mDataLoaderHandler.getNext(this);
		// }
		// break;
		// }
		// }
		// }
	}

	public void showLoading(boolean show) {
		if (show)
			mListView.getRefreshableView().addFooterView(mLoadingView, null,
					false);
		else
			mListView.getRefreshableView().removeFooterView(mLoadingView);
	}

	public View getReloadingView() {
		return mReloadingView;
	}

	public void showReloading(boolean show) {
		if (show)
			mListView.getRefreshableView().addFooterView(mReloadingView);
		else
			mListView.getRefreshableView().removeFooterView(mReloadingView);
	}

	public void clear() {
		mList.clear();
		notifyDataSetChanged();
	}

	public void dataLoaded(ArrayList<T> values) {

		mList.addAll(values);
		showLoading(false);
		notifyDataSetChanged();
	}

	/**
	 * 剔除重复内容
	 */
	@SuppressWarnings("unchecked")
	public void dataLoadedDuplicate(ArrayList<T> values) {

		mList = BabytreeUtil.removeDuplicate(mList, values);
		showLoading(false);
		notifyDataSetChanged();
	}

	public void refreshTop(ArrayList<T> values) {
		if (values != null) {
			mList.addAll(values);
			notifyDataSetChanged();
		}

		mListView.onRefreshComplete();

	}

	/**
	 * 添加头
	 * 
	 * @param values
	 */
	public void addTop(T values) {
		if (values != null) {
			mList.add(0, values);
			notifyDataSetChanged();
		}
	}

	/**
	 * 添加底部
	 * 
	 * @param values
	 */
	public void addFooter(T values) {
		if (values != null) {
			mList.add(values);
			notifyDataSetChanged();
		}
	}

	/**
	 * 删除对象
	 * 
	 * @param values
	 */
	public void removeItem(T values) {
		if (values != null) {
			mList.remove(values);
			notifyDataSetChanged();
		}

	}

}
