package com.babytree.apps.biz.notify;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.babytree.apps.pregnancy.R;

public class AnimationNotifyActivity extends Activity {

	/**
	 * 孕气值
	 */
	private int yunqi;

	/**
	 * 红心图片
	 */
	private ImageView imgAnnimation;

	/**
	 * 显示孕气值
	 */
	private TextView txtMessage;

	/**
	 * 帧动画
	 */
	private AnimationDrawable annimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 设置背景透明
		setTheme(R.style.TransparentTheme);

		setContentView(R.layout.animation_notify_activity);

		yunqi = getIntent().getIntExtra("yunqi", 0);

		imgAnnimation = (ImageView) findViewById(R.id.img_annimation_notify);
		txtMessage = (TextView) findViewById(R.id.txt_message);
		txtMessage.setText("孕气+" + yunqi);

		annimation = (AnimationDrawable) imgAnnimation.getDrawable();

		// 动画时间
		int durationMillis = 3000;

		// 位移动画
		float fromXDelta = 0F;
		float toXDelta = 0F;
		float fromYDelta = 0F;
		float toYDelta = -300F;
		TranslateAnimation translateAnimation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);

		translateAnimation.setDuration(durationMillis);

		// 渐变动画
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.3F, 1);
		alphaAnimation.setDuration(durationMillis);

		AnimationSet animationSet = new AnimationSet(true);
		animationSet.addAnimation(translateAnimation);
		animationSet.addAnimation(alphaAnimation);
		animationSet.setDuration(durationMillis);
		animationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				Toast.makeText(AnimationNotifyActivity.this, "onAnimationStart", Toast.LENGTH_LONG).show();

			}

			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				imgAnnimation.clearAnimation();
				txtMessage.clearAnimation();
				annimation.stop();

				finish();

			}
		});

		imgAnnimation.startAnimation(animationSet);
		txtMessage.startAnimation(translateAnimation);
		annimation.start();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		imgAnnimation.clearAnimation();
		txtMessage.clearAnimation();
		annimation.stop();

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		if (event.getAction() == MotionEvent.ACTION_UP) {
			finish();
			return true;
		}
		return super.onTouchEvent(event);
	}
}
