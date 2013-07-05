package com.babytree.apps.biz.photo;

import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.ZoomControls;

import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.bitmap.BabytreeBitmapCache.BitmapLoadCallable;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.ui.widget.ImageZoomView;
import com.babytree.apps.comm.ui.widget.ZoomState;
import com.babytree.apps.pregnancy.R;

/**
 * 图片预览
 * 
 * @author wangbingqi
 * 
 */
public class PhotoActivity extends BabytreeTitleAcitivty implements OnClickListener {

	public static final String BUNDLE_URL = "url";

	private BabytreeBitmapCache bitmapCache;

	private ImageZoomView mZoomView;

	private ZoomState mZoomState;

	private Bitmap mBitmap;

	private ImageZoomListener mZoomListener;

	private ProgressBar mProgressBar;

	private ZoomControls mZoomCtrls;

	private String mUrl;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		mUrl = getIntent().getStringExtra(BUNDLE_URL);

		super.onCreate(savedInstanceState);

		// ---------------------缓存模块start--------------------------
		bitmapCache = BabytreeBitmapCache.create(mContext);
		// ---------------------缓存模块end----------------------------

		BabytreeLog.d("查看大图 接收的地址 = " + mUrl);
		mZoomView = (ImageZoomView) findViewById(R.id.zoomView);
		mProgressBar = (ProgressBar) findViewById(R.id.progress_large);
		mProgressBar.setVisibility(View.VISIBLE);
		mZoomState = new ZoomState();
		loadBitmap(mUrl);

		mZoomCtrls = (ZoomControls) findViewById(R.id.zoomCtrl);
		mZoomCtrls.setOnZoomInClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				float z = mZoomState.getZoom() + 0.25f;
				mZoomState.setZoom(z);
				mZoomState.notifyObservers();
			}
		});
		mZoomCtrls.setOnZoomOutClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				float z = mZoomState.getZoom() - 0.25f;
				mZoomState.setZoom(z);
				mZoomState.notifyObservers();
			}
		});
		regularlyMonitoringZoom();
	}

	@SuppressLint("HandlerLeak")
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				if (mBitmap != null) {
					mProgressBar.setVisibility(View.GONE);
					mZoomView.setImage(mBitmap);
					mZoomState = new ZoomState();
					mZoomView.setZoomState(mZoomState);
					mZoomListener = new ImageZoomListener();
					mZoomListener.setZoomState(mZoomState);
					mZoomView.setOnTouchListener(mZoomListener);
					resetZoomState();
				} else {
					mProgressBar.setVisibility(View.GONE);
					mZoomCtrls.setVisibility(View.GONE);
					Toast.makeText(PhotoActivity.this, "图片读取失败", Toast.LENGTH_SHORT).show();
				}
				break;

			default:
				mProgressBar.setVisibility(View.GONE);
				mZoomCtrls.setVisibility(View.GONE);
				Toast.makeText(PhotoActivity.this, "图片读取失败", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};

	private void loadBitmap(final String bigImageUrl) {

		// ---------------------缓存模块start--------------------------
		mBitmap = bitmapCache.fetchBitmap(bigImageUrl, new BitmapLoadCallable() {

			@Override
			public void loadSuccessfully(Bitmap bitmap) {
				mBitmap = bitmap;
				handler.sendEmptyMessage(0);
			}

			@Override
			public void loadFailed() {
				handler.sendEmptyMessage(-1);
			}
		});
		if (mBitmap != null) {
			handler.sendEmptyMessage(0);
		}
		// ---------------------缓存模块end----------------------------

	}

	/**
	 * 定时监测Zoom
	 */
	@SuppressLint("HandlerLeak")
	private void regularlyMonitoringZoom() {
		Timer timer = new Timer();
		final Handler handler = new Handler() {
			public void handleMessage(Message msg) {
				setZoomOutEnabled(mZoomState, mZoomCtrls);
			}
		};
		TimerTask timerTask = new TimerTask() {
			@Override
			public void run() {
				handler.sendEmptyMessage(0);
			}
		};
		timer.schedule(timerTask, 0/* 延迟0毫秒执行 */, 500/* 每500毫秒执行1次 */);
	}

	public class ImageZoomListener implements OnTouchListener {

		private ZoomState mState;

		private float mX;

		private float mY;

		public void setZoomState(ZoomState state) {
			mState = state;
		}

		public boolean onTouch(View v, MotionEvent event) {
			final int action = event.getAction();
			final float x = event.getX();
			final float y = event.getY();
			switch (action) {
			case MotionEvent.ACTION_DOWN:
				mX = x;
				mY = y;
				break;
			case MotionEvent.ACTION_MOVE: {
				final float dx = (x - mX) / v.getWidth();
				final float dy = (y - mY) / v.getHeight();
				mState.setPanX(mState.getPanX() - dx);
				mState.setPanY(mState.getPanY() - dy);
				mState.notifyObservers();
				mX = x;
				mY = y;
				break;
			}
			}

			return true;
		}

	}

	/**
	 * 设置缩小按钮是否可以被点击
	 */
	private void setZoomOutEnabled(ZoomState zoomState, ZoomControls zoomControls) {
		if (zoomState != null) {
			if (zoomState.getZoom() <= 1.0f) {
				zoomControls.setIsZoomOutEnabled(false);
			} else {
				zoomControls.setIsZoomOutEnabled(true);
			}
		}
	}

	private void resetZoomState() {
		mZoomState.setPanX(0.5f);
		mZoomState.setPanY(0.5f);
		mZoomState.setZoom(1f);
		mZoomState.notifyObservers();
	}

	@Override
	public void setLeftButton(Button button) {

	}

	@Override
	public void setRightButton(Button button) {

	}

	@Override
	public String getTitleString() {
		return "图片预览";
	}

	@Override
	public int getBodyView() {
		return R.layout.photo_activity;
	}
}
