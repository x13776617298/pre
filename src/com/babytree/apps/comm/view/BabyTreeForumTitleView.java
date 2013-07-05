package com.babytree.apps.comm.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.babytree.apps.pregnancy.R;
/**
 * 带舌头的按钮  
 * @author luozheng
 *
 */
public class BabyTreeForumTitleView extends RelativeLayout{
	private View view;
	/**
	 * 标题
	 */
	private TextView mTvCenter;
	/**
	 * 按下按钮
	 */
	private ImageView mIvItemDown;
	/**
	 * 按钮舌头外框
	 */
	private ImageView mIvItemOut;
	


	/**
	 * 按钮舌头
	 */
	private ImageView mIvItenIn;
	public BabyTreeForumTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}
	
	public BabyTreeForumTitleView(Context context){
		super(context);
		init(context);
	}

	
	private void init(Context context){
		
		view=LayoutInflater.from(context).inflate(R.layout.forum_title,
				null);
		mTvCenter=(TextView)view.findViewById(R.id.txt_center);
		mIvItemOut=(ImageView)view.findViewById(R.id.iv_item_bg_out);
		mIvItenIn=(ImageView)view.findViewById(R.id.img_icon);
		mIvItemDown=(ImageView)view.findViewById(R.id.iv_item_bg_in);
		
		addView(view);
	}
	
	/**
	 * 获取中间的文字
	 * @return
	 * @author luozheng
	 */
	public TextView getmTvCenter() {
		return mTvCenter;
	}

	/**
	 * 下拉箭头
	 * @return
	 * @author luozheng
	 */
	public ImageView getmIvItemDown() {
		return mIvItemDown;
	}

	/**
	 * 舌头外框
	 * @return
	 * @author luozheng
	 */
	public ImageView getmIvItemOut() {
		return mIvItemOut;
	}

	/**
	 * 舌头
	 * @return
	 * @author luozheng
	 */
	public ImageView getmIvItenIn() {
		return mIvItenIn;
	}

}
