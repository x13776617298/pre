package com.babytree.apps.biz.father;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.babytree.apps.biz.home.HomePageActivity;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ui.CalculatorActivity;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.comm.util.SharedPreferencesUtil;
import com.babytree.apps.pregnancy.R;
import com.umeng.analytics.MobclickAgent;

/**
 * 准爸爸 准妈妈选择页面
 * 
 * @author pengxh
 */

public class RoleSelectActivity extends BabytreeActivity {

	/**
	 * 左侧布局（准爸爸）
	 */
	private LinearLayout layoutLeft;

	/**
	 * 右侧布局（准妈妈）
	 */
	private LinearLayout layoutRight;

	/**
	 * 左侧内容
	 */
	private RelativeLayout layoutContentLeft;

	/**
	 * 右侧内容
	 */
	private RelativeLayout layoutContentRight;

	/**
	 * 爸爸按钮
	 */
	private Button mBtnDaddy;
	private Button mBtnDaddy_b;

	/**
	 * 妈妈按钮
	 */
	private Button mBtnMommy;
	private Button mBtnMommy_b;

	// 爸爸妈妈选择图片
	private ImageView mIvDaddy;
	private ImageView mIvMommy;

	private Animation mLeftInAnimation;
	private Animation mRightInAnimation;
	private Animation mLeftOutAnimation;
	private Animation mRightOutAnimation;

	/**
	 * 左侧布局参数
	 */
	private ViewGroup.LayoutParams leftP;

	/**
	 * 右侧布局参数
	 */
	private ViewGroup.LayoutParams rightP;

	private TextView mTipView;

	/**
	 * 状态 1:展开 0:关闭
	 */
	private int mStatus = 0;

	private Context mContext;

	private Resources res;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 关闭之前多余界面
		closeOtherActivity();
		// 加入关闭通知监听(需要在onCreate之前调用)
		babytreecloselistener = this;

		mContext = this;
		res = mContext.getResources();
		setContentView(R.layout.father_mather_select_activity);

		// 初始化动画
		// mLeftInAnimation = AnimationUtils.loadAnimation(this,
		// R.anim.slide_left_in);
		// mRightInAnimation = AnimationUtils.loadAnimation(this,
		// R.anim.slide_right_in);
		mLeftInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_left_in);
		mRightInAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right_in);
		mLeftOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_left_out);
		mRightOutAnimation = AnimationUtils.loadAnimation(this, R.anim.slide_right_out);

		mLeftInAnimation.setAnimationListener(animationListener);
		mRightInAnimation.setAnimationListener(animationListener);
		mLeftOutAnimation.setAnimationListener(animationListener);
		mRightOutAnimation.setAnimationListener(animationListener);

		layoutContentLeft = (RelativeLayout) findViewById(R.id.layout_content_left);
		layoutContentRight = (RelativeLayout) findViewById(R.id.layout_content_right);

		layoutLeft = (LinearLayout) findViewById(R.id.layout_left);
		layoutRight = (LinearLayout) findViewById(R.id.layout_right);
		layoutLeft.setPadding(1, 1, 1, 1);
		layoutRight.setPadding(1, 1, 1, 1);
		layoutContentLeft.setPadding(1, 1, 1, 1);
		layoutContentRight.setPadding(1, 1, 1, 1);

		leftP = layoutLeft.getLayoutParams();
		rightP = layoutRight.getLayoutParams();

		mBtnDaddy = (Button) findViewById(R.id.btn_daddy);
		mBtnMommy = (Button) findViewById(R.id.btn_mommy);
		mBtnDaddy.setOnClickListener(onClickListener);
		mBtnMommy.setOnClickListener(onClickListener);

		mIvDaddy = (ImageView) findViewById(R.id.ic_daddy);
		mIvMommy = (ImageView) findViewById(R.id.ic_mommy);

		mTipView = (TextView) findViewById(R.id.tv_tip);
		mTipView.setText(mContext.getResources().getString(R.string.role_select));

		// 设置加粗
		mBtnDaddy.getPaint().setFakeBoldText(true);
		mBtnMommy.getPaint().setFakeBoldText(true);
		mTipView.getPaint().setFakeBoldText(true);

		mBtnDaddy_b = (Button) findViewById(R.id.btn_daddy_b);
		mBtnMommy_b = (Button) findViewById(R.id.btn_mommy_b);
		mBtnDaddy_b.getPaint().setFakeBoldText(true);
		mBtnMommy_b.getPaint().setFakeBoldText(true);
	}

	View.OnClickListener onClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {

			// 移除操作事件
			removeOnClickListener();

			ViewGroup.LayoutParams fillParams = new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.FILL_PARENT);

			switch (v.getId()) {

			case R.id.ic_daddy:
			case R.id.btn_daddy:
				if (res.getString(R.string.s_role_father).equalsIgnoreCase(mBtnDaddy.getText().toString())) {
					setViewVisible(mTipView, View.GONE);
					setStatus(1);// 设置为打开状态
					setViewVisible(layoutRight, View.GONE);
					addOnClickListener(layoutLeft);
					addOnClickListener(mIvDaddy);
					layoutLeft.setLayoutParams(fillParams);
					// layoutLeft.startAnimation(mLeftInAnimation);
					executeAnimation(layoutLeft, mLeftInAnimation);
				} else {
					// 存储爸爸版的标记
					SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.APP_TYPE_KEY,
							CommConstants.APP_TYPE_DADDY);
					startActivity(new Intent(getApplicationContext(), WelcomeFatherActivity.class));
					// 结束
					stopSelf();
				}

				break;

			case R.id.ic_mommy:
			case R.id.btn_mommy:
				if (res.getString(R.string.s_role_mother).equalsIgnoreCase(mBtnMommy.getText().toString())) {
					setViewVisible(mTipView, View.GONE);
					setStatus(1);// 设置为打开状态
					setViewVisible(layoutLeft, View.GONE);
					addOnClickListener(layoutRight);
					addOnClickListener(mIvMommy);
					layoutRight.setLayoutParams(fillParams);
					// layoutRight.startAnimation(mRightInAnimation);
					executeAnimation(layoutRight, mRightInAnimation);
				} else {
					// 存储妈妈版的标记
					SharedPreferencesUtil.setValue(getApplicationContext(), ShareKeys.APP_TYPE_KEY,
							CommConstants.APP_TYPE_MOMMY);
					String mBirthday = SharedPreferencesUtil.getStringValue(getApplicationContext(),
							ShareKeys.BIRTHDAY_TIMESTAMP);
					if (mBirthday == null) {
						Intent intent = new Intent(mContext, CalculatorActivity.class).putExtra("first", true);
						startActivity(intent);
					} else {
						startActivity(new Intent(getApplicationContext(), HomePageActivity.class));
					}
					stopSelf();
				}

				break;

			case R.id.layout_left:
				setStatus(0);// 设置为关闭状态
				setViewVisible(layoutContentLeft, View.GONE);
				layoutLeft.setLayoutParams(leftP);
				// layoutLeft.startAnimation(mLeftOutAnimation);
				executeAnimation(layoutLeft, mLeftOutAnimation);
				break;

			case R.id.layout_right:
				setStatus(0);// 设置为关闭状态
				setViewVisible(layoutContentRight, View.GONE);
				layoutRight.setLayoutParams(rightP);
				// layoutRight.startAnimation(mRightOutAnimation);
				executeAnimation(layoutRight, mRightOutAnimation);
				break;

			default:
				break;
			}
		}
	};

	/**
	 * 移除监听事件
	 */
	private void removeOnClickListener() {
		layoutLeft.setOnClickListener(null);
		layoutRight.setOnClickListener(null);
		mIvDaddy.setOnClickListener(null);
		mIvMommy.setOnClickListener(null);
	}

	/**
	 * 添加监听事件
	 * 
	 * @param view
	 */
	private void addOnClickListener(View view) {
		view.setOnClickListener(onClickListener);
	}

	/**
	 * 动画监听器
	 */
	Animation.AnimationListener animationListener = new Animation.AnimationListener() {

		@Override
		public void onAnimationStart(Animation animation) {

		}

		@Override
		public void onAnimationRepeat(Animation animation) {

		}

		@Override
		public void onAnimationEnd(Animation animation) {
			// 执行关闭时
			if (animation == mLeftOutAnimation || animation == mRightOutAnimation) {
				setViewVisible(layoutContentLeft, View.VISIBLE);
				setViewVisible(layoutContentRight, View.VISIBLE);
			}
			if (mStatus == 0) {// 关闭状态
				setViewVisible(mTipView, View.VISIBLE);
				setViewVisible(layoutLeft, View.VISIBLE);
				setViewVisible(layoutRight, View.VISIBLE);
			} else {
				setViewVisible(mTipView, View.GONE);
			}
		}
	};

	/**
	 * 设置View的可见性
	 */
	private void setViewVisible(View view, int visible) {
		if (view != null) {
			view.setVisibility(visible);
		}
	}

	/**
	 * 执行动画
	 * 
	 * @param view
	 * @param animation
	 */
	private void executeAnimation(View view, Animation animation) {
		if (view != null && animation != null) {
			view.setPadding(1, 1, 1, 1);
			view.startAnimation(animation);
		}
	}

	/**
	 * 设置状态
	 * 
	 * @param status
	 *            0 关闭 1打开
	 */
	private void setStatus(int status) {
		mStatus = status;
		if (mStatus == 0) {
			mBtnDaddy.setText(res.getString(R.string.s_role_father));
			mBtnMommy.setText(res.getString(R.string.s_role_mother));
		} else {
			mBtnDaddy.setText(res.getString(R.string.s_role_father_in));
			mBtnMommy.setText(res.getString(R.string.s_role_mother_in));
		}
	}

	/**
	 * 结束页面
	 */
	private void stopSelf() {
		finish();
	}
}