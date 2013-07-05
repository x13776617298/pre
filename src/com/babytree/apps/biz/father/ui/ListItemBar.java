package com.babytree.apps.biz.father.ui;

import com.babytree.apps.biz.father.util.Utils;
import com.babytree.apps.pregnancy.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ListItemBar extends RelativeLayout {
	private static final String TAG = "TaskListItem";
	
	private Context mContext;
	private View itembg;
	private ImageView mLeftImage;
	private ImageView mRightImage;
	private TextView mTaskTitle;
	
	public ListItemBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}
	
	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();
		init(mContext);
	}

	private void init(Context context){
		itembg = findViewById(R.id.item_bg);
		mLeftImage = (ImageView)findViewById(R.id.left);
		mRightImage = (ImageView)findViewById(R.id.right);
		mTaskTitle = (TextView)findViewById(R.id.task_title);
	}
	
	public void setItemBarBg(int resId){
		if(Utils.checkResId(resId)){
			itembg.setBackgroundResource(resId);
		}
	}
	
	public void setLeftImageVisible(int visible){
		if(Utils.checkVisibility(visible)){
			mLeftImage.setVisibility(visible);
		}
	}
	
	public void setRightImageVisible(int visible){
		if(Utils.checkVisibility(visible)){
			mRightImage.setVisibility(visible);
		}
	}
	
	public void setItemTitle(String title){
		mTaskTitle.setText(title);
	}
	
	public void setItemTitleGravity(int gravity){
		mTaskTitle.setGravity(gravity);
	}
	
	public void setItemTitleColor(int color){
		mTaskTitle.setTextColor(color);
	}
	
	public void setListItemLeftImage(int resId){
		if(Utils.checkResId(resId)){
			mLeftImage.setImageResource(resId);
		}
	}
	
	public void setRightItemLeftImage(int resId){
		if(Utils.checkResId(resId)){
			mRightImage.setImageResource(resId);
		}
	}
}
