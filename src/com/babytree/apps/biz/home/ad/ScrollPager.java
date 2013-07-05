package com.babytree.apps.biz.home.ad;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.widget.LinearLayout;
import android.widget.Scroller;

import com.babytree.apps.comm.tools.BabytreeLog;

public class ScrollPager<T> extends LinearLayout {
	private static final String TAG = "HorizontalPager";
	private static final boolean DEBUG = false;

	private static final int[] HORIZONTAL_PAGER = { 0x7f010000 };
	private static final int HORIZONTAL_PAGE_DEFAULT_WIDTH = 0;
	// 无效屏
	private static final int INVALID_SCREEN = -1;
	public static final int SPEC_UNDEFINED = -1;

	/**
	 * 手势速度
	 */
	private static final int SNAP_VELOCITY = 400;

	// 页面宽度的间隔大小，页面宽度
	private int pageWidthSpec, pageWidth;

	private boolean mFirstLayout = true;

	// 当前页
	private int mCurrentPage;
	// 下一页
	private int mNextPage = INVALID_SCREEN;

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;

	private int mTouchSlop;
	private int mMaximumVelocity;

	private float mLastMotionX;
	private float mLastMotionY;
	// 初始状态
	private final static int TOUCH_STATE_REST = 0;
	// 滚动中状态
	private final static int TOUCH_STATE_SCROLLING = 1;

	private int mTouchState = TOUCH_STATE_REST;

	private boolean mAllowLongPress;

	private static final int SNAP_TO_DESTINATION = 0;
	private static final int AUTO_SCROLL_PAGER = 1;

	private static final int SEND_AUTO_SCROLL_PAGER = 5000;
	private static final int SCROLL_FINISH_WAIT_TIME_AUTO_PAGER = 5000;
	private boolean isTouch = false;
	private List<T> data = null;
	protected ScrollPagerHandler scrollPagerHandler;
	private Set<OnScrollListener> mListeners = new HashSet<OnScrollListener>();
	private OnCreateChildView<T> mOnCreateChildView = null;

	public ScrollPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		TypedArray a = context.obtainStyledAttributes(attrs, HORIZONTAL_PAGER);
		pageWidthSpec = a.getDimensionPixelSize(HORIZONTAL_PAGE_DEFAULT_WIDTH, SPEC_UNDEFINED);
		a.recycle();

		init();
		scrollPagerHandler = new ScrollPagerHandler();
	}

	/**
	 * 初始化工作区中的各项参数
	 */
	private void init() {
		mScroller = new Scroller(getContext());
		mCurrentPage = 0;

		final ViewConfiguration configuration = ViewConfiguration.get(getContext());
		mTouchSlop = configuration.getScaledTouchSlop();
		mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();

	}

	/**
	 * 返回当前所在页的页码
	 */
	public int getCurrentPage() {
		return mCurrentPage;
	}

	/**
	 * 设置当前页码
	 */
	public void setCurrentPage(int currentPage) {
		mCurrentPage = Math.max(0, Math.min(currentPage, getChildCount()));
		scrollTo(getScrollXForPage(mCurrentPage), 0);
		invalidate();
	}

	/**
	 * 获取页面的Width
	 * 
	 * @return
	 */
	public int getPageWidth() {
		return pageWidth;
	}

	/**
	 * 设置页面的宽度
	 * 
	 * @param pageWidth
	 */
	public void setPageWidth(int pageWidth) {
		this.pageWidthSpec = pageWidth;
	}

	/**
	 * 获取滚动条应该滚动到的页面位置
	 */
	private int getScrollXForPage(int whichPage) {
		return (whichPage * pageWidth) - pageWidthPadding();
	}

	@Override
	public void computeScroll() {
		if (mScroller.computeScrollOffset()) {
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		} else if (mNextPage != INVALID_SCREEN) {
			mCurrentPage = mNextPage;
			mNextPage = INVALID_SCREEN;
			clearChildrenCache();
		}
	}

	@Override
	protected void dispatchDraw(Canvas canvas) {

		final long drawingTime = getDrawingTime();

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			drawChild(canvas, getChildAt(i), drawingTime);
		}

		for (OnScrollListener mListener : mListeners) {
			int adjustedScrollX = getScrollX() + pageWidthPadding();
			mListener.onScroll(adjustedScrollX);
			if (adjustedScrollX % pageWidth == 0) {
				mListener.onViewScrollFinished(adjustedScrollX / pageWidth);
			}
		}
	}

	/**
	 * 子视图之间的padding间隔
	 * 
	 * @return
	 */
	int pageWidthPadding() {
		return ((getMeasuredWidth() - pageWidth) / 2);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);

		pageWidth = pageWidthSpec == SPEC_UNDEFINED ? getMeasuredWidth() : pageWidthSpec;
		pageWidth = Math.min(pageWidth, getMeasuredWidth());
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			getChildAt(i).measure(MeasureSpec.makeMeasureSpec(pageWidth, MeasureSpec.EXACTLY), heightMeasureSpec);
		}

		if (mFirstLayout) {
			scrollTo(getScrollXForPage(mCurrentPage), 0);
			mFirstLayout = false;
		}
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		int childLeft = 0;

		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View child = getChildAt(i);
			if (child.getVisibility() != View.GONE) {
				final int childWidth = child.getMeasuredWidth();
				child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
				childLeft += childWidth;
			}
		}
	}

	@Override
	public boolean requestChildRectangleOnScreen(View child, Rect rectangle, boolean immediate) {
		int screen = indexOfChild(child);
		if (screen != mCurrentPage || !mScroller.isFinished()) {
			return true;
		}
		return false;
	}

	@Override
	protected boolean onRequestFocusInDescendants(int direction, Rect previouslyFocusedRect) {
		int focusableScreen;
		if (mNextPage != INVALID_SCREEN) {
			focusableScreen = mNextPage;
		} else {
			focusableScreen = mCurrentPage;
		}
		getChildAt(focusableScreen).requestFocus(direction, previouslyFocusedRect);
		return false;
	}

	@Override
	public boolean dispatchUnhandledMove(View focused, int direction) {
		if (direction == View.FOCUS_LEFT) {
			if (getCurrentPage() > 0) {
				snapToPage(getCurrentPage() - 1);
				return true;
			}
		} else if (direction == View.FOCUS_RIGHT) {
			if (getCurrentPage() < getChildCount() - 1) {
				snapToPage(getCurrentPage() + 1);
				return true;
			}
		}
		return super.dispatchUnhandledMove(focused, direction);
	}

	@Override
	public void addFocusables(ArrayList<View> views, int direction) {
		getChildAt(mCurrentPage).addFocusables(views, direction);
		if (direction == View.FOCUS_LEFT) {
			if (mCurrentPage > 0) {
				getChildAt(mCurrentPage - 1).addFocusables(views, direction);
			}
		} else if (direction == View.FOCUS_RIGHT) {
			if (mCurrentPage < getChildCount() - 1) {
				getChildAt(mCurrentPage + 1).addFocusables(views, direction);
			}
		}
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		if ((action == MotionEvent.ACTION_MOVE) && (mTouchState != TOUCH_STATE_REST)) {
			return true;
		}

		final float x = ev.getX();
		final float y = ev.getY();

		switch (action) {
		case MotionEvent.ACTION_MOVE:
			if (mTouchState == TOUCH_STATE_REST) {
				checkStartScroll(x, y);
			}
			break;

		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			mAllowLongPress = true;

			mTouchState = mScroller.isFinished() ? TOUCH_STATE_REST : TOUCH_STATE_SCROLLING;

			// 如下代码在自编译的代码中可以用到
			// 如果一个具有滚动组件是ScrollPager的父组件，
			// 如下代码可以消除组件间的滚动干扰。
			 if (mParent != null)
				 mParent.requestDisallowInterceptTouchEvent(true);
			break;

		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			clearChildrenCache();
			snapToDestination();
			mTouchState = TOUCH_STATE_REST;
			break;
		}
		return mTouchState != TOUCH_STATE_REST;
	}

	/**
	 * 检查滚动
	 * 
	 * @param x
	 * @param y
	 */
	private void checkStartScroll(float x, float y) {
		final int xDiff = (int) Math.abs(x - mLastMotionX);
		final int yDiff = (int) Math.abs(y - mLastMotionY);

		boolean xMoved = xDiff > mTouchSlop;
		boolean yMoved = yDiff > mTouchSlop;

		if (xMoved || yMoved) {

			if (xMoved) {
				mTouchState = TOUCH_STATE_SCROLLING;
				enableChildrenCache();
			}
			if (mAllowLongPress) {
				mAllowLongPress = false;
				final View currentScreen = getChildAt(mCurrentPage);
				currentScreen.cancelLongPress();
			}
		}
	}

	/**
	 * 缓存子View的
	 */
	void enableChildrenCache() {
		setChildrenDrawingCacheEnabled(true);
		setChildrenDrawnWithCacheEnabled(true);
	}

	/**
	 * 清理子View的缓存
	 */
	void clearChildrenCache() {
		setChildrenDrawnWithCacheEnabled(false);
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);

		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		isTouch = true;
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			Log.d("ScrollPager", "onTouchEvent MotionEvent.ACTION_DOWN ");
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}

			mLastMotionX = x;
			break;
		case MotionEvent.ACTION_MOVE:
			Log.d("ScrollPager", "onTouchEvent MotionEvent.ACTION_MOVE ");
			if (mTouchState == TOUCH_STATE_REST) {
				checkStartScroll(x, y);

			} else if (mTouchState == TOUCH_STATE_SCROLLING) {// 在断续移动手指
				int deltaX = (int) (mLastMotionX - x);
				mLastMotionX = x;

				if (getScrollX() < 0 || getScrollX() > getChildAt(getChildCount() - 1).getLeft()) {
					deltaX /= 2;
				}

				scrollBy(deltaX, 0);

				scrollPagerHandler.clearHandleMessageQueue();
				scrollPagerHandler.sendEmptyMessageDelayed(AUTO_SCROLL_PAGER, SCROLL_FINISH_WAIT_TIME_AUTO_PAGER);
			}

			break;
		case MotionEvent.ACTION_UP:
			Log.d("ScrollPager", "onTouchEvent MotionEvent.ACTION_UP ");
			if (mTouchState == TOUCH_STATE_SCROLLING) {
				final VelocityTracker velocityTracker = mVelocityTracker;
				velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
				int velocityX = (int) velocityTracker.getXVelocity();

				if (velocityX > SNAP_VELOCITY && mCurrentPage > 0) {
					snapToPage(mCurrentPage - 1);
				} else if (velocityX < -SNAP_VELOCITY && mCurrentPage < getChildCount() - 1) {
					snapToPage(mCurrentPage + 1);
				} else {
					snapToDestination();
				}

				if (mVelocityTracker != null) {
					mVelocityTracker.recycle();
					mVelocityTracker = null;
				}
				scrollPagerHandler.clearHandleMessageQueue();
				scrollPagerHandler.sendEmptyMessageDelayed(AUTO_SCROLL_PAGER, SCROLL_FINISH_WAIT_TIME_AUTO_PAGER);
			}

			mTouchState = TOUCH_STATE_REST;
			break;
		case MotionEvent.ACTION_CANCEL:
			Log.d("ScrollPager", "onTouchEvent MotionEvent.ACTION_CANCEL ");
			mTouchState = TOUCH_STATE_REST;
		}

		return true;
	}

	/**
	 * 当你的滑动超过HorizontalPager的8分之1宽的时候，去下一页。 如果不超过，退回到原始位置
	 */
	private void snapToDestination() {
		final int startX = getScrollXForPage(mCurrentPage);
		int whichPage = mCurrentPage;
		if (getScrollX() < startX - getWidth() / 8) {
			whichPage = Math.max(0, whichPage - 1);
		} else if (getScrollX() > startX + getWidth() / 8) {
			whichPage = Math.min(getChildCount() - 1, whichPage + 1);
		}
		snapToPage(whichPage);
	}

	/**
	 * 到指定页面
	 * 
	 * @param whichPage
	 */
	private void snapToPage(int whichPage) {
		enableChildrenCache();

		boolean changingPages = whichPage != mCurrentPage;

		mNextPage = whichPage;

		View focusedChild = getFocusedChild();
		if (focusedChild != null && changingPages && focusedChild == getChildAt(mCurrentPage)) {
			focusedChild.clearFocus();
		}

		final int newX = getScrollXForPage(whichPage);
		final int delta = newX - getScrollX();

		if (DEBUG)
			BabytreeLog.d(TAG, " --> mCurrent page = " + mCurrentPage);

		if (whichPage == 0 && !isTouch) {
			scrollTo(newX, 0);
		} else {
			mScroller.extendDuration(200);
			mScroller.startScroll(getScrollX(), 0, delta, 0, Math.abs(delta) * 2);
		}
		isTouch = false;
		invalidate();
	}

	/**
	 * 为外部按键事件操作向左滚动屏幕，调用该方法。
	 */
	public void scrollLeft() {
		if (mNextPage == INVALID_SCREEN && mCurrentPage > 0 && mScroller.isFinished()) {
			snapToPage(mCurrentPage - 1);
		}
	}

	/**
	 * 为外部按键事件操作向右滚动屏幕，调用该方法。
	 */
	public void scrollRight() {
		if (mNextPage == INVALID_SCREEN && mCurrentPage < getChildCount() - 1 && mScroller.isFinished()) {
			snapToPage(mCurrentPage + 1);
		}
	}

	/**
	 * 得到子View所在HorizontalPager中的位置
	 * 
	 * @param v
	 * @return
	 */
	public int getScreenForView(View v) {
		int result = -1;
		if (v != null) {
			ViewParent vp = v.getParent();
			int count = getChildCount();
			for (int i = 0; i < count; i++) {
				if (vp == getChildAt(i)) {
					return i;
				}
			}
		}
		return result;
	}

	public boolean allowLongPress() {
		return mAllowLongPress;
	}

	/**
	 * HorizontalPager滚动时监听事件
	 * 
	 * @author gaierlin
	 */
	public static interface OnScrollListener {
		/**
		 * 滚动中
		 * 
		 * @param scrollX
		 */
		void onScroll(int scrollX);

		/**
		 * 滚动已经停止
		 * 
		 * @param currentPage
		 */
		void onViewScrollFinished(int currentPage);
	}

	/**
	 * 创建子视图的接口类。
	 * 
	 * @author gaierlin
	 */
	public interface OnCreateChildView<T> {
		View createChildView(T t);
	}

	/**
	 * 向ScrollPage中添加子视图，可以是不同的子视图。
	 * 
	 * @param createChildView
	 */
	public void addOnCreateChildView(OnCreateChildView<T> createChildView) {
		mOnCreateChildView = createChildView;
	}

	/**
	 * 设置ScrollPage滚动时要向谁发出通知信息。
	 * 
	 * @param listener
	 */
	public void addOnScrollListener(OnScrollListener listener) {
		mListeners.add(listener);
	}

	/**
	 * 移除某向滚动通知的Listener
	 * 
	 * @param listener
	 */
	public void removeOnScrollListener(OnScrollListener listener) {
		mListeners.remove(listener);
	}

	/**
	 * 自动滚动
	 */
	private void autoScrollPager() {
		if (mCurrentPage == getChildCount() - 1) {
			mCurrentPage = 0;
		} else {
			mCurrentPage++;
		}
		snapToPage(mCurrentPage);
		scrollPagerHandler.sendEmptyMessageDelayed(AUTO_SCROLL_PAGER, SEND_AUTO_SCROLL_PAGER);
	}

	/**
	 * 滚动装置Handler
	 * 
	 * @author gaierlin
	 */
	class ScrollPagerHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			int which = msg.what;
			switch (which) {
			case SNAP_TO_DESTINATION:
				snapToDestination();
				break;
			case AUTO_SCROLL_PAGER:
				startAutoScroll();
				break;
			}
		}

		/**
		 * 清除MessageQueue中未执行的Message。
		 */
		public void clearHandleMessageQueue() {
			this.removeMessages(SNAP_TO_DESTINATION);
			this.removeMessages(AUTO_SCROLL_PAGER);
		}

		/**
		 * 开始页面的滚动工作
		 */
		public void startAutoScroll() {
			clearHandleMessageQueue();
			autoScrollPager();
		}
	}

	/**
	 * 设置数据
	 * 
	 * @param data
	 */
	public void setData(List<T> data) {
		this.data = data;
		if (data == null) {
			throw new IllegalArgumentException("所传入参数不能为null值");
		}

		int size = data.size();
		View childView = null;
		this.removeAllViews();

		for (int i = 0; i < size; i++) {
			if (mOnCreateChildView != null) {
				childView = mOnCreateChildView.createChildView(data.get(i));
				this.addView(childView);
			}
		}

		autoScrollView();
	}

	/**
	 * 自动滚动
	 */
	public void autoScrollView() {
		scrollPagerHandler.sendEmptyMessageDelayed(AUTO_SCROLL_PAGER, SEND_AUTO_SCROLL_PAGER);
	}
}
