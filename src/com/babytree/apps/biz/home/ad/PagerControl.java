package com.babytree.apps.biz.home.ad;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.TransitionDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.babytree.apps.biz.father.util.Utils;
import com.babytree.apps.pregnancy.R;

public class PagerControl extends LinearLayout {
	private static final String TAG = "PagerControl";

	private int mDotDrawableId;
	private static final int MARGIN = 3;
	private boolean isInit = false;

	private int numPages, currentPage;

	public static final int INDICATOR_SCROLL_BAR_PAGER = 0;
	public static final int INDICATOR_DOT_PAGER = 1;
	public static final int INDICATOR_TEXT_PAGER = 2;
	private int indicatorType = INDICATOR_SCROLL_BAR_PAGER;

	private static final int INDICATOR_TEXT_COLOR = 0xff000000;

	private ScrollIndicatorPager scrollIndicatorPager;
	private Context mContext;

	public PagerControl(Context context) {
		super(context);
		mContext = context;
	}

	public PagerControl(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public void lazyInit() {
		lazyInit(indicatorType);
	}

	public void lazyInit(int indicatorType) {
		this.indicatorType = indicatorType;
		isInit = true;
		switch (indicatorType) {
		case INDICATOR_SCROLL_BAR_PAGER:
			initScrollIndicatorPager();
			break;
		case INDICATOR_DOT_PAGER:
			initIndicatorDotPager();
			break;
		case INDICATOR_TEXT_PAGER:

			break;
		}
	}

	private void initIndicatorDotPager() {
		setFocusable(false);
		setWillNotDraw(false);
		mDotDrawableId = R.drawable.pager_dots;
	}

	/**
	 * 滚动指示器完全模仿的是滚动条
	 */
	private void initScrollIndicatorPager() {
		scrollIndicatorPager = new ScrollIndicatorPager(mContext);
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				Utils.dip2px(mContext, 3));
		scrollIndicatorPager.setLayoutParams(p);
		this.addView(scrollIndicatorPager);
	}

	public void setPagesIndicatorType(int numPages) {
		if (!isInit) {
			throw new IllegalArgumentException(
					"没有明确初始化数据，请调用lazyInit()方法进行初始化参数。");
		}
		if (numPages <= 0) {
			Log.d(TAG, " --->  numPages 不能比零小。");
			return;
		}
		this.numPages = numPages;

		switch (indicatorType) {
		case INDICATOR_SCROLL_BAR_PAGER:
			scrollIndicatorPager.setNumPages(numPages);
			break;
		case INDICATOR_DOT_PAGER:
			createIndicatorDotLayout();
			break;
		case INDICATOR_TEXT_PAGER:
			createIndicatorTextLayout();
			break;
		}
	}

	public int getNumPages() {
		return numPages;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public int getIndicatorType() {
		return indicatorType;
	}

	public void setCurrentPager(int currentPager) {
		if (currentPager != this.currentPage) {
			this.currentPage = currentPager;
			Log.d(TAG, ".......... Current pager = " + currentPager);
			switch (indicatorType) {
			case INDICATOR_SCROLL_BAR_PAGER:
				scrollIndicatorPager.setCurrentPage(currentPager);
				break;
			case INDICATOR_DOT_PAGER:
				updateIndicatorDotLayout();
				break;
			case INDICATOR_TEXT_PAGER:
				updateIndicatorTextLayout();
				break;
			}
		}
	}

	private void updateIndicatorDotLayout() {
		int offset = 0;
		for (int i = 0; i < getChildCount(); i++) {
			View child = getChildAt(i);
			if (child instanceof ImageView) {
				TransitionDrawable tmp = (TransitionDrawable) ((ImageView) child)
						.getDrawable();
				if (i - offset == currentPage) {
					tmp.startTransition(50);
				} else {
					tmp.resetTransition();
				}
			} else {
				offset++;
			}
		}
	}

	public void createIndicatorDotLayout() {
		Log.d(TAG, " ---> createLayout");
		Log.d(TAG, numPages + " 个指示器！");
		int margin = Utils.dip2px(getContext(), MARGIN);

		LinearLayout.LayoutParams lineParam = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout lineLayout = new LinearLayout(getContext());
		lineLayout.setLayoutParams(lineParam);

		int count = this.getChildCount();
		for (int i = count; i < count + numPages; i++) {
			ImageView indicatorDot = new ImageView(getContext());
			TransitionDrawable td;
			td = (TransitionDrawable) getResources()
					.getDrawable(mDotDrawableId);
			td.setCrossFadeEnabled(true);
			indicatorDot.setImageDrawable(td);
			LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.WRAP_CONTENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			p.setMargins(margin, margin, margin, margin);
			indicatorDot.setLayoutParams(p);
			if (i - count == currentPage) {
				TransitionDrawable tmp = (TransitionDrawable) indicatorDot
						.getDrawable();
				tmp.startTransition(200);
			}
			this.addView(indicatorDot, i);
		}
		postInvalidate();
	}

	private void updateIndicatorTextLayout() {
		View child = getChildAt(0);
		if (child instanceof TextView) {
			((TextView) child).setText((currentPage + 1) + "/" + numPages);
		}
	}

	public void calculationPosition(int position) {
		if (indicatorType == INDICATOR_SCROLL_BAR_PAGER) {
			scrollIndicatorPager.setPosition(position);
		}
	}

	public void createIndicatorTextLayout() {
		TextView indicatorText = new TextView(getContext());
		LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		int margin = Utils.dip2px(getContext(), MARGIN);
		p.setMargins(margin, margin, margin, margin);
		indicatorText.setLayoutParams(p);
		indicatorText.setTextColor(INDICATOR_TEXT_COLOR);
		indicatorText.setText((currentPage + 1) + "/" + numPages);
		this.addView(indicatorText);
		postInvalidate();
	}

	/**
	 * 滚动的指示器这个部分是完全模仿android水平滚动条的实现方式
	 */
	private class ScrollIndicatorPager extends View {
		// 长指示器的背景颜色
		private static final int DEFAULT_BAR_COLOR = 0xaa777777;
		// 指示器高光部分的颜色
		private static final int DEFAULT_HIGHLIGHT_COLOR = 0xaa00ff00;
		// 指示器延迟消失时间
		private static final int DEFAULT_FADE_DELAY = 0;// 0:代表不消失
		// 指示器淡入淡出动画时间
		private static final int DEFAULT_FADE_DURATION = 0;// 0:代表即时展示
		// 指示器总数，当前指示器，当前指示器应展示的位置
		private int numPages, currentPage, position;
		// 绘制指示器及指示器里高光部分
		private Paint barPaint, highlightPaint;
		// 指示器 延迟时间，消失时间
		private int fadeDelay, fadeDuration;
		// 指示器两边的弧度
		private float ovalRadius;
		// 指示器淡入淡出动画
		private Animation fadeOutAnimation;

		public ScrollIndicatorPager(Context context) {
			super(context);
			init();
		}

		private void init() {
			int barColor = DEFAULT_BAR_COLOR;
			int highlightColor = DEFAULT_HIGHLIGHT_COLOR;
			fadeDelay = DEFAULT_FADE_DELAY;
			fadeDuration = DEFAULT_FADE_DURATION;
			ovalRadius = 0f;

			barPaint = new Paint();
			barPaint.setColor(barColor);

			highlightPaint = new Paint();
			highlightPaint.setColor(highlightColor);

			fadeOutAnimation = new AlphaAnimation(1f, 0f);
			fadeOutAnimation.setDuration(fadeDuration);
			fadeOutAnimation.setRepeatCount(0);
			fadeOutAnimation.setInterpolator(new LinearInterpolator());
			fadeOutAnimation.setFillEnabled(true);
			fadeOutAnimation.setFillAfter(true);
		}

		public void setNumPages(int numPages) {
			if (numPages <= 0) {
				throw new IllegalArgumentException("numPages must be positive");
			}
			this.numPages = numPages;
			invalidate();
			fadeOut();
		}

		private void fadeOut() {
			if (fadeDuration > 0) {
				clearAnimation();
				fadeOutAnimation.setStartTime(AnimationUtils
						.currentAnimationTimeMillis() + fadeDelay);
				setAnimation(fadeOutAnimation);
			}
		}

		public void setCurrentPage(int currentPage) {
			if (currentPage < 0 || currentPage >= numPages) {
				throw new IllegalArgumentException(
						"currentPage parameter out of bounds");
			}
			if (this.currentPage != currentPage) {
				this.currentPage = currentPage;
				this.position = currentPage * getPageWidth();
				invalidate();
				fadeOut();
			}
		}

		public int getPageWidth() {
			return getWidth() / numPages;
		}

		public void setPosition(int position) {
			if (this.position != position) {
				this.position = position;
				invalidate();
				fadeOut();
			}
		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()),
					ovalRadius, ovalRadius, barPaint);
			canvas.drawRoundRect(new RectF(position, 0, position
					+ (getWidth() / numPages), getHeight()), ovalRadius,
					ovalRadius, highlightPaint);
		}
	}
}
