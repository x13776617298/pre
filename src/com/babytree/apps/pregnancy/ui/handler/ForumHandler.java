package com.babytree.apps.pregnancy.ui.handler;

import java.util.ArrayList;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.comm.config.EventContants;
import com.babytree.apps.comm.ctr.P_BabytreeController;
import com.babytree.apps.comm.model.Base;
import com.babytree.apps.comm.ui.page.AbstractDataLoaderHandler;
import com.babytree.apps.comm.ui.page.AbstractDataResponseHandler;
import com.babytree.apps.comm.ui.page.AbstractDataResult;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

public class ForumHandler implements AbstractDataLoaderHandler<Base>,
		AbstractDataResponseHandler<AbstractDataResult<Base>> {

	private Activity mActivity;

	private boolean mLoading;

	private ArrayList<Base> mValues = new ArrayList<Base>();

	private DataLoadedCallback<Base> mCallback;

	private int mMaxItems;

	private int pageNo;

	private int mGroupId;

	private String mOrderby;

	private boolean mIsElite;

	private int mProvinceId;

	private int mCityProvinceId;

	private String mBirthday;

	private String mName;

	private TextView mTxtMessage;

	private String mPregMonth;
	private String loginStr;

	public ForumHandler(Activity activity, int groupId, TextView txtMessage, String orderby, boolean isElite,
			String birthday, String name, String loginStr) {
		mActivity = activity;
		mGroupId = groupId;
		mTxtMessage = txtMessage;
		mOrderby = orderby;
		mIsElite = isElite;
		mProvinceId = 0;
		mCityProvinceId = 0;
		mBirthday = birthday;
		mName = name;
		if (null != loginStr && !"".equals(loginStr)) {
			this.loginStr = loginStr;
		}

	}

	public ArrayList<Base> getValues() {
		return mValues;
	}

	public String getName() {
		return mName;
	}

	@Override
	public void resultTopAvailable(int status, AbstractDataResult<Base> result) {
		if (status == REQUEST_SUCCESS) {
			MobclickAgent.onEvent(mActivity, EventContants.com, EventContants.communicate_topicListSuccess);
			mValues.clear();
			mCallback.clear();
			mValues.addAll(result.values);
			mCallback.refreshTop(result.values);
		} else if (status == REQUEST_FAILED) {
			MobclickAgent.onEvent(mActivity, EventContants.com, EventContants.communicate_topicListFail);
			// 错误处理
			mCallback.refreshTop(null);
			mTxtMessage.setText("");
			Toast.makeText(mActivity, result.message, Toast.LENGTH_SHORT).show();
			ExceptionUtil.catchException(result.error, mActivity);

		}
	}

	@Override
	public void resultAvailable(int status, AbstractDataResult<Base> result) {
		if (status == REQUEST_SUCCESS) {
			MobclickAgent.onEvent(mActivity, EventContants.com, EventContants.communicate_topicListSuccess);
			mLoading = false;
			if (mMaxItems == 0)
				mMaxItems = result.maxItems;
			if (result.values.size() == 0) {
				mMaxItems = mValues.size();
				mCallback.showLoading(false);
				return;
			}
			// 去除重复内容
			mValues = com.babytree.apps.comm.util.BabytreeUtil.removeDuplicate(mValues, result.values);
			mCallback.dataLoadedDuplicate(result.values);
		} else if (status == REQUEST_FIRST_SUCCESS) {
			MobclickAgent.onEvent(mActivity, EventContants.com, EventContants.communicate_topicListSuccess);
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
				// mCallback.refreshTop(null);//一定要加，不然刷新后头上有个点击刷新
			}
		} else if (status == REQUEST_FIRST_FAILED) {
			MobclickAgent.onEvent(mActivity, EventContants.com, EventContants.communicate_topicListFail);

			mCallback.refreshTop(null);
			mTxtMessage.setText("");
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
					new FirstBackgroundTask(ForumHandler.this).execute();
				}
			});
		} else if (status == REQUEST_FAILED) {
			MobclickAgent.onEvent(mActivity, EventContants.com, EventContants.communicate_topicListFail);
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
					new BackgroundTask(ForumHandler.this).execute(mValues.size());
				}
			});
		}
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
		if (mValues.size() < mMaxItems) {
			mLoading = true;
			mCallback = callback;
			new BackgroundTask(this).execute(mValues.size());
		}
	}

	@Override
	public boolean isLoading() {
		return mLoading;
	}

	public void refersh(String orderby, boolean isElite, int provinceId, int cityProvinceId, String birthday,
			String pregMonth) {
		mOrderby = orderby;
		mIsElite = isElite;
		mProvinceId = provinceId;
		mCityProvinceId = cityProvinceId;
		mBirthday = birthday;
		mPregMonth = pregMonth;

		mValues.clear();
		mCallback.clear();
		mActivity.findViewById(R.id.load).setVisibility(View.VISIBLE);
		mActivity.findViewById(R.id.layout_empty).setVisibility(View.GONE);
		mActivity.findViewById(R.id.reload).setVisibility(View.GONE);
		mCallback.showLoading(false);
		new FirstBackgroundTask(ForumHandler.this).execute();
	}

	public void refersh(String orderby, boolean isElite, int provinceId, int cityProvinceId, String birthday) {
		mOrderby = orderby;
		mIsElite = isElite;
		mProvinceId = provinceId;
		mCityProvinceId = cityProvinceId;
		mBirthday = birthday;
		mPregMonth = null;

		mValues.clear();
		mCallback.clear();
		mActivity.findViewById(R.id.load).setVisibility(View.VISIBLE);
		mActivity.findViewById(R.id.layout_empty).setVisibility(View.GONE);
		mActivity.findViewById(R.id.reload).setVisibility(View.GONE);
		mCallback.showLoading(false);
		new FirstBackgroundTask(ForumHandler.this).execute();
	}

	public void refersh(int groupId, TextView txtMessage, String orderby, boolean isElite, String birthday, String name) {
		mGroupId = groupId;
		mTxtMessage = txtMessage;
		mOrderby = orderby;
		mIsElite = isElite;
		mProvinceId = 0;
		mCityProvinceId = 0;
		mBirthday = birthday;
		mName = name;

		mValues.clear();
		mCallback.clear();
		mActivity.findViewById(R.id.load).setVisibility(View.VISIBLE);
		mActivity.findViewById(R.id.layout_empty).setVisibility(View.GONE);
		mActivity.findViewById(R.id.reload).setVisibility(View.GONE);
		mCallback.showLoading(false);
		new FirstBackgroundTask(ForumHandler.this).execute();
	}

	public void refershTop(long timestamp) {
		new TopBackgroundTask(ForumHandler.this).execute(timestamp);
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
			DataResult ret = new DataResult();
			try {
				if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(mActivity)) {
					pageNo = FIRST_START;
					mValues.clear();
					if (mPregMonth == null) {
						ret = P_BabytreeController.getDiscuzList(loginStr, mGroupId, pageNo, mOrderby, mIsElite,
								mProvinceId, mCityProvinceId, mBirthday);
					} else {
						ret = P_BabytreeController.getDiscuzListByPregMonthOfElite(loginStr, pageNo,
								Integer.parseInt(mPregMonth), true);
					}
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
			AbstractDataResult<Base> result = new AbstractDataResult<Base>();
			DataResult ret = new DataResult();
			try {
				if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(mActivity)) {
					if (mPregMonth == null) {
						ret = P_BabytreeController.getDiscuzList(loginStr, mGroupId, pageNo, mOrderby, mIsElite,
								mProvinceId, mCityProvinceId, mBirthday);
					} else {
						ret = P_BabytreeController.getDiscuzListByPregMonthOfElite(loginStr, pageNo,
								Integer.parseInt(mPregMonth), true);
					}
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
			DataResult ret = new DataResult();
			try {
				if (com.babytree.apps.comm.util.BabytreeUtil.hasNetwork(mActivity)) {
					pageNo++;
					if (mPregMonth == null) {
						ret = P_BabytreeController.getDiscuzList(loginStr, mGroupId, pageNo, mOrderby, mIsElite,
								mProvinceId, mCityProvinceId, mBirthday);
					} else {
						ret = P_BabytreeController.getDiscuzListByPregMonthOfElite(loginStr, pageNo,
								Integer.parseInt(mPregMonth), true);
					}
					result.status = ret.status;
					if (ret.status == P_BabytreeController.SUCCESS_CODE) {
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
				mResponseHandler.resultAvailable(REQUEST_SUCCESS, result);
			} else {
				mResponseHandler.resultAvailable(REQUEST_FAILED, result);
			}
		}
	}
}
