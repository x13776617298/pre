package com.babytree.apps.biz.notify;

import com.babytree.apps.pregnancy.R;

import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;

public class AnimationShowWindow {
	private ImageView imgAnnimation;
	private TextView txtMessage;
	private AnimationSet animationSet;
	private TranslateAnimation translateAnimation;
	private AnimationDrawable annimation;
	private WindowManager.LayoutParams wmParams;
	private WindowManager wm;
	private View v;

	/**
	 * 显示加运气的动画
	 * 
	 * @param context
	 * @param yunqi
	 */
	public AnimationShowWindow(Context context, int yunqi) {
		v = LayoutInflater.from(context).inflate(R.layout.animation_notify_activity, null);
		// 获取WindowManager
		wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		wmParams = new WindowManager.LayoutParams();
		// 该类型提供与用户交互，置于所有应用程序上方，但是在状态栏后面
		wmParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		// 不接受任何按键事件
		wmParams.flags |= WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		wmParams.x = 0;
		wmParams.y = wm.getDefaultDisplay().getWidth() / 2;
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.FILL_PARENT;
		wmParams.format = PixelFormat.RGBA_8888;
		// wm.addView(v, wmParams);
		imgAnnimation = (ImageView) v.findViewById(R.id.img_annimation_notify);
		txtMessage = (TextView) v.findViewById(R.id.txt_message);
		txtMessage.setText("孕气+" + yunqi);
		annimation = (AnimationDrawable) imgAnnimation.getDrawable();
		// 动画时间
		int durationMillis = 3000;
		// 位移动画
		float fromXDelta = 0F;
		float toXDelta = 0F;
		float fromYDelta = 0F;
		float toYDelta = -(wm.getDefaultDisplay().getWidth() / 2);
		translateAnimation = new TranslateAnimation(fromXDelta, toXDelta, fromYDelta, toYDelta);

		translateAnimation.setDuration(durationMillis);

		// 渐变动画
		AlphaAnimation alphaAnimation = new AlphaAnimation(0.3F, 1);
		alphaAnimation.setDuration(durationMillis);

		animationSet = new AnimationSet(true);
		animationSet.addAnimation(translateAnimation);
		animationSet.addAnimation(alphaAnimation);
		animationSet.setDuration(durationMillis);
		animationSet.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationRepeat(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {

				imgAnnimation.clearAnimation();
				txtMessage.clearAnimation();
				annimation.stop();
				// wm.removeView(v);
				// v.setVisibility(View.GONE);
				hander.sendMessage(new Message());
			}
		});

	}

	public void show() {
		imgAnnimation.startAnimation(animationSet);
		txtMessage.startAnimation(translateAnimation);
		wm.addView(v, wmParams);
		annimation.start();
	}

	private Handler hander = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				wm.removeView(v);
			} catch (Exception e) {
			}
		}
	};

}
