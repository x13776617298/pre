package com.babytree.apps.comm.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.ui.activity.BabytreeTitleAcitivty;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.view.BabyTreeTitleView;
import com.babytree.apps.comm.view.listener.OnClickBabyViewLeftButtonListener;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;
import com.umeng.common.Log;
/**
 * 修改拍照后的图片
 * @author 达达
 *
 */
public class ModifyImageViewActivity extends BabytreeTitleAcitivty implements OnClickListener{
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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		Log.e("dada", "ModifyImageViewActivity");
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
	
	
	/**
	 * 左上角返回键
	 */
	private void onBack() {
			setResult(RESULT_OK, new Intent().putExtra("img_rotate", num*90));
			finish();
			
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
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
		opts.inSampleSize = computeSampleSize(opts, -1, 200 * 200);
		opts.inJustDecodeBounds = false;
		tmp_bitmap = BitmapFactory.decodeFile(s, opts);
		img_view.setImageBitmap(tmp_bitmap);
		
		
	}
	
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}
	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
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
		// TODO Auto-generated method stub
		
	}


	@Override
	public void setRightButton(Button button) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public String getTitleString() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public int getBodyView() {
		// TODO Auto-generated method stub
		return R.layout.modifyimage_activity;
	}

	

	
}
