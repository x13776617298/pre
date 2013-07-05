package com.babytree.apps.comm.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.babytree.apps.comm.view.listener.OnClickBabyViewLeftButtonListener;
import com.babytree.apps.comm.view.listener.OnClickBabyViewRightButtonListener;
import com.babytree.apps.pregnancy.R;

/**
 * 自定义标题栏View
 * 
 * 这个类是一个带标题栏以及按钮的view 以后写东西可以直接setContentView(view)
 * 然后view.get...Laytou.addView(你新的布局)
 * 
 * @author 达达
 * 
 */
public class BabyTreeTitleView extends RelativeLayout implements OnClickListener {
	private View view;
	private Button button_left;
	private Button button_right;
	private TextView textView;
	private RelativeLayout relativeLayout;
	private FrameLayout framelayout;
	private LinearLayout linearlayout;
	private OnClickBabyViewLeftButtonListener listener;
	private OnClickBabyViewRightButtonListener rListener;

	public BabyTreeTitleView(Context context) {
		super(context);
		view = LayoutInflater.from(context).inflate(R.layout.babytree_title_view, null);
		button_left = (Button) view.findViewById(R.id.baby_title_view_button_left);
		button_right = (Button) view.findViewById(R.id.baby_title_view_button_right);
		textView = (TextView) view.findViewById(R.id.baby_title_view_text);
		relativeLayout = (RelativeLayout) view.findViewById(R.id.baby_title_view_linearlayout);
		framelayout = (FrameLayout) view.findViewById(R.id.baby_title_view_framelayout);
		linearlayout = (LinearLayout) view.findViewById(R.id.baby_title_view_linearlayout_top);
		view.setLayoutParams(new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
		addView(view);
		button_left.setOnClickListener(this);
		button_right.setOnClickListener(this);
		button_right.setVisibility(View.INVISIBLE);

	}

	/**
	 * 获取标题栏下部的布局
	 * 
	 * @return RelativeLayout
	 */
	public RelativeLayout getRelativeLayout() {
		return relativeLayout;
	}

	/**
	 * 获取标题TextView
	 * 
	 * @return TextView
	 */
	public TextView getTextView() {
		return textView;
	}

	/**
	 * 获取标题左边button
	 * 
	 * @return Button
	 */
	public Button getButton_left() {
		return button_left;
	}

	public Button getButton_right() {
		return button_right;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.baby_title_view_button_left:
			if (listener != null)
				listener.onClick();

			break;
		case R.id.baby_title_view_button_right:
			if (rListener != null)
				rListener.onClick();
			break;
		default:
			break;
		}
	}

	/**
	 * 设置左上角按钮监听
	 * 
	 * @param listener
	 */
	public void setLeftButtonListener(OnClickBabyViewLeftButtonListener listener) {
		this.listener = listener;
	}

	public void setRightButtonListener(OnClickBabyViewRightButtonListener listener) {
		this.rListener = listener;
	}

	/**
	 * 获取 title 标题布局
	 * 
	 * @return FrameLayout
	 */
	public FrameLayout getFramelayout() {
		return framelayout;
	}

	/**
	 * 添加view
	 */
	public void addRelativeLayoutView(View child) {
		if (relativeLayout != null)
			relativeLayout.addView(child);
	}

	/**
	 * 获取标题栏中间位置的view
	 * 
	 * @return
	 * @author wangbingqi
	 */
	public LinearLayout getLinearlayout() {
		return linearlayout;
	}

}
