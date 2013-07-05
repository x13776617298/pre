package com.babytree.apps.comm.ui.activity;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.pregnancy.R;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 修改图片activity
 * @author wangbingqi
 *
 */
public class BabytreeModifyActivity extends BabytreeTitleAcitivty{

	/**
	 * 图片的路径
	 */
	private String img_url="";
	/**
	 * 缓存的图片
	 */
	private Bitmap tmp_bitmap=null;
	/**
	 * 显示的imageView
	 */
	private ImageView img_view;
	/**
	 * 内部的view
	 */
	private View view;
	/**
	 * 确定按钮
	 */
	private Button button_ok;
	/**
	 * 旋转按钮
	 */
	private Button button_modify;
	/**
	 * 动画
	 */
	private Animation animation;//= AnimationUtils.loadAnimation(mContext, R.anim....); 
	/**
	 * 记录角度
	 */
	private int num=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		img_url=getIntent().getStringExtra("img_url");
		img_view=(ImageView)findViewById(R.id.imageView1);
		button_ok=(Button)findViewById(R.id.button1);
		button_modify=(Button)findViewById(R.id.button2);
		button_ok.setOnClickListener(this);
		button_modify.setOnClickListener(this);
		if(img_url!=""||img_url!=null){
			setImg(img_url);
		}
	}
	
	private void onBack() {
			setResult(RESULT_OK, new Intent().putExtra("img_rotate", num*90));
			finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(tmp_bitmap!=null){
			tmp_bitmap.recycle();
			tmp_bitmap=null;
		}
	}
	
	/**
	 * 设置显示图片
	 * @param s
	 */
	private void setImg(String s){
		
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(s, opts);
		opts.inSampleSize = BabytreeUtil.computeSampleSize(opts, -1, 480 * 320);
		opts.inJustDecodeBounds = false;
		tmp_bitmap = BitmapFactory.decodeFile(s, opts);
		img_view.setImageBitmap(tmp_bitmap);
		
		
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			num++;
			if(num>3){
				num=0;
			}
			setDirection(num);
			break;
		case R.id.button2:
			onBack();
			break;
		default:
			break;
		}
	}
	
	private void setDirection(int d){
		switch (d) {
		case 1:
			animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.pic_rotate_90); 
			animation.setFillAfter(true); 
			img_view.setAnimation(animation);
			break;
		case 2:
			animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.pic_rotate_180); 
			animation.setFillAfter(true); 
			img_view.setAnimation(animation);
			break;
		case 3:
			animation= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.pic_rotate_270); 
			animation.setFillAfter(true); 
			img_view.setAnimation(animation);
			break;
		case 0:
			animation.setFillAfter(false); 
			img_view.setAnimation(animation);
			break;

		default:
			break;
		}
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
		return R.layout.modifyimage_activity;
	}

	
}
