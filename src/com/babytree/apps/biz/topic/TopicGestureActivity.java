package com.babytree.apps.biz.topic;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.Button;

import com.babytree.apps.comm.ui.activity.PhotoUpAndDownRefreshActivity;

public abstract class TopicGestureActivity<Node> extends PhotoUpAndDownRefreshActivity<Node> {

	// -----------------------------------------------
	/**
	 * 划动的最小距离
	 */
	private static final int SWIPE_MIN_DISTANCE = 150;

	/**
	 * 划动的最大距离
	 */
	private static final int SWIPE_MAX_OFF_PATH = 250;

	/**
	 * 划动的阈值速度
	 */
	private static final int SWIPE_THRESHOLD_VELOCITY = 60;

	/**
	 * 手势触摸器
	 */
	private GestureDetector gestureDetector;

	// -----------------------------------------------

	// 手势监听器
	OnGestureListener gestureListener = new OnGestureListener() {

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
			if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH) {
				return false;
			}
			if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				gestureSlideLeft();
			} else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
				gestureSlideRight();
			} else if (e1.getY() - e2.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				gestureSlideUp();
			} else if (e2.getY() - e1.getY() > SWIPE_MIN_DISTANCE && Math.abs(velocityY) > SWIPE_THRESHOLD_VELOCITY) {
				gestureSlideDown();
			}
			return true;
		}

		@Override
		public void onLongPress(MotionEvent e) {

		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
	};

	/**
	 * DO what - 向左滑动
	 */
	abstract protected void gestureSlideLeft();

	/**
	 * DO what - 向右滑动
	 */
	abstract protected void gestureSlideRight();

	/**
	 * DO what - 向上滑动
	 */
	abstract protected void gestureSlideUp();

	/**
	 * DO what - 向下滑动
	 */
	abstract protected void gestureSlideDown();

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		// 初始化手势GestureDetector
		gestureDetector = new GestureDetector(gestureListener);

		super.onCreate(savedInstanceState);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void setLeftButton(Button button) {

	}

	@Override
	public void setRightButton(Button button) {
	}

	@Override
	public String getTitleString() {
		return null;
	}

	@Override
	public int getBodyView() {
		return 0;
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		gestureDetector.onTouchEvent(ev);
		return super.dispatchTouchEvent(ev);
	}
}
