package com.babytree.apps.comm.ui.widget;

import com.babytree.apps.comm.ui.adapter.PinnedHeaderListViewAdapter;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;

public class PinnedHeaderListView extends ListView{
    private PinnedHeaderListViewAdapter adapter;
    View listFooter;
    boolean footerViewAttached = false;
    private static final int MAX_ALPHA = 255;

    private View mHeaderView;
    private boolean mHeaderViewVisible;

    private int mHeaderViewWidth;

    private int mHeaderViewHeight;
    public PinnedHeaderListView(Context context) {
        super(context);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PinnedHeaderListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    public void setPinnedHeaderView(View view) {
        mHeaderView = view;
        if (mHeaderView != null) {
            setFadingEdgeLength(0);
        }
        requestLayout();
    }
    @Override
    public void setAdapter(ListAdapter adapter) {
//        super.setAdapter(adapter);
//        mAdapter = (PinnedHeaderAdapter)adapter;
        if (!(adapter instanceof PinnedHeaderListViewAdapter)) {
            throw new IllegalArgumentException(PinnedHeaderListViewAdapter.class.getSimpleName() + " must use adapter of type " + PinnedHeaderListViewAdapter.class.getSimpleName());
        }
        
        // previous adapter
        if (this.adapter != null) {
            this.setOnScrollListener(null);
        }
        
        this.adapter = (PinnedHeaderListViewAdapter) adapter;
        this.setOnScrollListener((PinnedHeaderListViewAdapter) adapter);
        View dummy = new View(getContext());
        super.addFooterView(dummy);
        super.setAdapter(adapter);
        super.removeFooterView(dummy);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mHeaderView != null) {
            measureChild(mHeaderView, widthMeasureSpec, heightMeasureSpec);
            mHeaderViewWidth = mHeaderView.getMeasuredWidth();
            mHeaderViewHeight = mHeaderView.getMeasuredHeight();
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mHeaderView != null) {
            mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
            configureHeaderView(getFirstVisiblePosition());
        }
    }

    public void configureHeaderView(int position) {
        if (mHeaderView == null) {
            return;
        }

        int state = adapter.getPinnedHeaderState(position);
        switch (state) {
            case PinnedHeaderListViewAdapter.PINNED_HEADER_GONE: {
                mHeaderViewVisible = false;
                break;
            }

            case PinnedHeaderListViewAdapter.PINNED_HEADER_VISIBLE: {
//                adapter.bindSectionHeader(mHeaderView, position, true);
                adapter.configurePinnedHeader(mHeaderView, position, MAX_ALPHA);
                if (mHeaderView.getTop() != 0) {
                    mHeaderView.layout(0, 0, mHeaderViewWidth, mHeaderViewHeight);
                }
                mHeaderViewVisible = true;
                break;
            }

            case PinnedHeaderListViewAdapter.PINNED_HEADER_PUSHED_UP: {
                View firstView = null;
                firstView = getChildAt(0);
                if(firstView != null){
                    int bottom = firstView.getBottom();
//                  int itemHeight = firstView.getHeight();
                  int headerHeight = mHeaderView.getHeight();
                  int y;
                  int alpha;
                  if (bottom < headerHeight) {
                      y = (bottom - headerHeight);
                      alpha = MAX_ALPHA * (headerHeight + y) / headerHeight;
                  } else {
                      y = 0;
                      alpha = MAX_ALPHA;
                  }
//                  adapter.bindSectionHeader(mHeaderView, position, true);
                  adapter.configurePinnedHeader(mHeaderView, position, alpha);
                  if (mHeaderView.getTop() != y) {
                      mHeaderView.layout(0, y, mHeaderViewWidth, mHeaderViewHeight + y);
                  }
                  mHeaderViewVisible = true;
                  break;  
                }else{
                    break;
                }
                
            }
        }
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        if (mHeaderViewVisible) {
            drawChild(canvas, mHeaderView, getDrawingTime());
        }
    }

    }  
    
