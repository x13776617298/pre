package com.babytree.apps.pregnancy.ui.handler;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.ctr.SignInController;
import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.ui.page.AbstractDataLoaderHandler;
import com.babytree.apps.comm.ui.page.AbstractDataResponseHandler;
import com.babytree.apps.comm.ui.page.AbstractDataResult;
import com.babytree.apps.comm.ui.widget.PullToRefreshListView;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.pregnancy.R;

public class AllMessageListHandler implements AbstractDataLoaderHandler<Base>,
		AbstractDataResponseHandler<AbstractDataResult<Base>> {

	private Activity mActivity;

	private boolean mLoading;

	private ArrayList<Base> mValues = new ArrayList();

	private DataLoadedCallback<Base> mCallback;

	/**
	 * 总数
	 */
	private int mMaxItems;

	private int pageNo;

	private String mLoginString;

	private String user_encode_id;

	private String start;

	private String limit;
	/**
	 * 每页大小
	 */
	private int mixNum = 10;

	public AllMessageListHandler(Activity activity, String loginString, String user_encode_id, String start,
			String limit) {
		mActivity = activity;
		mLoginString = loginString;
		this.user_encode_id = user_encode_id;
		this.start = start;
		this.limit = limit;
	}

	public ArrayList<Base> getValues() {
		return mValues;
	}

	@Override
	public void resultTopAvailable(int status, AbstractDataResult<Base> result) {
		if (status == REQUEST_SUCCESS) {
			if (result.values == null || result.values.size() == 0) {
				mCallback.refreshTop(null);
				return;
			}
			// mValues.clear();
			mCallback.clear();
			// mValues.addAll(result.values);
			mValues.addAll(0, result.values);
			// mCallback.refreshTop(result.values);
			mCallback.refreshTop(mValues);
		} else if (status == REQUEST_FAILED) {
			// 错误处理
			Toast.makeText(mActivity, result.message, Toast.LENGTH_SHORT).show();
			ExceptionUtil.catchException(result.error, mActivity);
			mCallback.refreshTop(null);
		}
	}

	@Override
	public void resultAvailable(int status, AbstractDataResult<Base> result) {
		if (status == REQUEST_SUCCESS) {
			mLoading = false;
			if (mMaxItems == 0)
				mMaxItems = result.maxItems;
			try {
				System.out.println("result.values:" + result.values);
				if (result.values == null) {
					return;
				}
			} catch (Exception e) {
				return;
			}
			if (result.values.size() == 0) {
				mMaxItems = mValues.size();
				mCallback.showLoading(false);
				return;
			}
			// mValues.addAll(result.values);
			// mCallback.dataLoaded(result.values);
			// 去除重复内容
			mValues = com.babytree.apps.comm.util.BabytreeUtil.removeDuplicate(mValues, result.values);
			mCallback.dataLoadedDuplicate(result.values);
		} else if (status == REQUEST_FIRST_SUCCESS) {
			if (mMaxItems < 1) {
				mActivity.findViewById(R.id.load).setVisibility(View.GONE);
				mActivity.findViewById(R.id.list).setVisibility(View.GONE);
				mActivity.findViewById(R.id.layout_empty).setVisibility(View.VISIBLE);
				mActivity.findViewById(R.id.reload).setVisibility(View.GONE);
			} else {
				mActivity.findViewById(R.id.load).setVisibility(View.GONE);
				mActivity.findViewById(R.id.list).setVisibility(View.VISIBLE);
				mActivity.findViewById(R.id.layout_empty).setVisibility(View.GONE);
				mActivity.findViewById(R.id.reload).setVisibility(View.GONE);
				mLoading = false;
				if (mMaxItems == 0)
					mMaxItems = result.maxItems;
				if (result.values.size() == 0) {
					mMaxItems = mValues.size();
					mCallback.showLoading(false);
					return;
				}
				mValues.addAll(result.values);
				mCallback.dataLoaded(result.values);

				try {
					PullToRefreshListView mListView = (PullToRefreshListView) mActivity.findViewById(R.id.list);
					mListView.getRefreshableView().setSelection(mListView.getRefreshableView().getCount() - 1);
				} catch (Exception e) {
					// TODO: handle exception
				}

			}
			mLoading = true;
		} else if (status == REQUEST_FIRST_FAILED) {
			// 错误处理
			Toast.makeText(mActivity, result.message, Toast.LENGTH_SHORT).show();
			ExceptionUtil.catchException(result.error, mActivity);
			mActivity.findViewById(R.id.load).setVisibility(View.GONE);
			final View reloadView = mActivity.findViewById(R.id.reload);
			reloadView.setVisibility(View.VISIBLE);
			Button btnReload = (Button) reloadView.findViewById(R.id.btn_reload);
			btnReload.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mActivity.findViewById(R.id.load).setVisibility(View.VISIBLE);
					reloadView.setVisibility(View.GONE);
					new FirstBackgroundTask(AllMessageListHandler.this).execute();
				}
			});
		} else if (status == REQUEST_FAILED) {
			// 错误处理
			Toast.makeText(mActivity, result.message, Toast.LENGTH_SHORT).show();
			ExceptionUtil.catchException(result.error, mActivity);
			mCallback.showLoading(false);
			mCallback.showReloading(true);
			View reloadingView = mCallback.getReloadingView();
			Button btnReload = (Button) reloadingView.findViewById(R.id.btn_reload);
			btnReload.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mCallback.showLoading(true);
					mCallback.showReloading(false);
					new BackgroundTask(AllMessageListHandler.this).execute(mValues.size());
				}
			});
		}
	}

	/**
	 * 添加数据
	 * 
	 * @param object
	 */
	public void addFooter(Base object) {
		mValues.add(object);
		mCallback.addFooter(object);
		try {
			mActivity.findViewById(R.id.load).setVisibility(View.GONE);
			mActivity.findViewById(R.id.list).setVisibility(View.VISIBLE);
			mActivity.findViewById(R.id.layout_empty).setVisibility(View.GONE);
			mActivity.findViewById(R.id.reload).setVisibility(View.GONE);
			mCallback.showLoading(false);
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	/**
	 * 删除数据
	 * 
	 * @param object
	 */
	public void removeItem(Base object) {
		mValues.remove(object);
		mCallback.removeItem(object);
	}

	@Override
	public int getMaxItems() {
		return mMaxItems;
	}

	@Override
	public void getValues(DataLoadedCallback<Base> callback) {
		if (mValues.isEmpty()) {
			mCallback = callback;
			new FirstBackgroundTask(this).execute();
		} else {
			callback.dataLoaded(mValues);
		}
	}

	@Override
	public void getNext(DataLoadedCallback<Base> callback) {
		// if (mValues.size() < mMaxItems) {
		// mLoading = true;
		// mCallback = callback;
		// new BackgroundTask(this).execute(mValues.size());
		// }

	}

	@Override
	public boolean isLoading() {
		return mLoading;
	}

	public void refersh() {
		mValues.clear();
		mCallback.clear();
		mActivity.findViewById(R.id.load).setVisibility(View.VISIBLE);
		mActivity.findViewById(R.id.layout_empty).setVisibility(View.GONE);
		mActivity.findViewById(R.id.reload).setVisibility(View.GONE);
		mCallback.showLoading(false);
		new FirstBackgroundTask(AllMessageListHandler.this).execute();
	}

	public void refershTop(long timestamp) {
		new TopBackgroundTask(AllMessageListHandler.this).execute(timestamp);
	}

	private class FirstBackgroundTask extends AsyncTask<Void, Void, AbstractDataResult<Base>> {
		private AbstractDataResponseHandler<AbstractDataResult<Base>> mResponseHandler;

		private FirstBackgroundTask(AbstractDataResponseHandler<AbstractDataResult<Base>> responseHandler) {
			mResponseHandler = responseHandler;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected AbstractDataResult<Base> doInBackground(Void... params) {
			AbstractDataResult<Base> result = new AbstractDataResult<Base>();
			try {
				if (BabytreeUtil.hasNetwork(mActivity)) {
					pageNo = 0;
					DataResult ret = SignInController.toSessonMessage(mLoginString, user_encode_id, pageNo + "", mixNum
							+ "");
					result.status = ret.status;
					if (ret.status == P_BabytreeController.SUCCESS_CODE) {
						mMaxItems = ret.totalSize;
						ArrayList<Base> values = (ArrayList<Base>) ret.data;
						result.maxItems = mMaxItems;
						result.values = values;
					} else {
						result.error = ret.error;
						result.message = ret.message;
					}
				} else {
					result.message = P_BabytreeController.NetworkExceptionMessage;
					result.status = P_BabytreeController.NetworkExceptionCode;
				}
				return result;
			} catch (Exception ex) {
				result.message = P_BabytreeController.SystemExceptionMessage;
				result.status = P_BabytreeController.SystemExceptionCode;
				return result;
			}
		}

		@Override
		protected void onPostExecute(AbstractDataResult<Base> result) {
			if (result.status == P_BabytreeController.SUCCESS_CODE) {
				mResponseHandler.resultAvailable(REQUEST_FIRST_SUCCESS, result);
			} else {
				mResponseHandler.resultAvailable(REQUEST_FIRST_FAILED, result);
			}
		}

	}

	private class TopBackgroundTask extends AsyncTask<Long, Void, AbstractDataResult<Base>> {
		private AbstractDataResponseHandler<AbstractDataResult<Base>> mResponseHandler;

		private TopBackgroundTask(AbstractDataResponseHandler<AbstractDataResult<Base>> responseHandler) {
			mResponseHandler = responseHandler;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected AbstractDataResult<Base> doInBackground(Long... params) {
			// 下拉回调
			AbstractDataResult<Base> result = new AbstractDataResult<Base>();
			try {
				if (BabytreeUtil.hasNetwork(mActivity)) {
					pageNo++;
					DataResult ret = SignInController.toSessonMessage(mLoginString, user_encode_id, pageNo * mixNum
							+ "", mixNum + "");
					result.status = ret.status;
					if (ret.status == P_BabytreeController.SUCCESS_CODE) {
						mMaxItems = ret.totalSize;
						ArrayList<Base> values = (ArrayList<Base>) ret.data;
						result.maxItems = mMaxItems;
						result.values = values;
					} else {
					}
				} else {
					result.message = P_BabytreeController.NetworkExceptionMessage;
					result.status = P_BabytreeController.NetworkExceptionCode;
				}
				return result;
			} catch (Exception ex) {
				result.message = P_BabytreeController.SystemExceptionMessage;
				result.status = P_BabytreeController.SystemExceptionCode;
				return result;
			}
		}

		@Override
		protected void onPostExecute(AbstractDataResult<Base> result) {
			if (result.status == P_BabytreeController.SUCCESS_CODE) {
				mResponseHandler.resultTopAvailable(REQUEST_SUCCESS, result);
			} else {
				mResponseHandler.resultTopAvailable(REQUEST_FAILED, result);
			}
		}

	}

	private class BackgroundTask extends AsyncTask<Integer, Void, AbstractDataResult<Base>> {
		private AbstractDataResponseHandler<AbstractDataResult<Base>> mResponseHandler;

		private BackgroundTask(AbstractDataResponseHandler<AbstractDataResult<Base>> responseHandler) {
			mResponseHandler = responseHandler;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected AbstractDataResult<Base> doInBackground(Integer... params) {
			AbstractDataResult<Base> result = new AbstractDataResult<Base>();
			return null;
		}

		@Override
		protected void onPostExecute(AbstractDataResult<Base> result) {
			if (result.status == P_BabytreeController.SUCCESS_CODE) {
				mResponseHandler.resultAvailable(REQUEST_SUCCESS, result);
			} else {
				mResponseHandler.resultAvailable(REQUEST_FAILED, result);
			}
		}
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "";
	}

}
