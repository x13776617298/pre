package com.babytree.apps.biz.father.ui;

import com.babytree.apps.biz.father.util.Utils;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.pregnancy.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Style;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * TitleBar的左右Button
 * 
 * @author gaierlin
 */
public class FatherTitleButton extends RelativeLayout {
	private LinearLayout mButton;

	private ImageView mButtonImg;
	private ImageView mButtonTag;
	private ImageView mLeftDividerLine;
	private ImageView mRightDividerLine;
	private View mLayerEnabled;

	private Context mContext;

	public FatherTitleButton(Context context) {
		super(context);
		mContext = context;
	}

	public FatherTitleButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	@Override
	protected void onFinishInflate() {
		super.onFinishInflate();

		init(mContext);
	}

	private void init(Context context) {

		mButtonImg = (ImageView) findViewById(R.id.button_img);
		mLeftDividerLine = (ImageView) findViewById(R.id.left_v_line);
		mRightDividerLine = (ImageView) findViewById(R.id.right_v_line);

		mButton = (LinearLayout) findViewById(R.id.button);
		mButtonTag = (ImageView) findViewById(R.id.button_tag);
		mLayerEnabled = findViewById(R.id.layer_enabled);
	}

	public void setButtonImage(int resid) {
		if (Utils.checkResId(resid)) {
			mButtonImg.setImageResource(resid);
		}
	}

	public void setButtonImage(Drawable drawable) {
		if (Utils.checkDrawable(drawable)) {
			mButtonImg.setImageDrawable(drawable);
		}
	}

	public void setLeftDividerLine(int resId) {
		if (Utils.checkResId(resId)) {
			mLeftDividerLine.setImageResource(resId);
		}
	}

	public void setLeftDividerLine(Drawable drawable) {
		if (Utils.checkDrawable(drawable)) {
			mLeftDividerLine.setImageDrawable(drawable);
		}
	}

	public void setRightDividerLine(int resId) {
		if (Utils.checkResId(resId)) {
			mRightDividerLine.setImageResource(resId);
		}
	}

	public void setRightDividerLine(Drawable drawable) {
		if (Utils.checkDrawable(drawable)) {
			mRightDividerLine.setImageDrawable(drawable);
		}
	}

	public void setBackgroundResource(int resId) {
		if (Utils.checkResId(resId)) {
			mButton.setBackgroundResource(resId);
		}
	}

	@SuppressWarnings("deprecation")
	public void setBackgroundDrawable(Drawable drawable) {
		if (Utils.checkDrawable(drawable)) {
			mButton.setBackgroundDrawable(drawable);
		}
	}

	@Override
	public void setOnClickListener(OnClickListener listener) {
		if (listener != null) {
			mButton.setOnClickListener(listener);
		}
	}

	public void setLeftDividerLineVisibility(int visibility) {
		if (Utils.checkVisibility(visibility)) {
			mLeftDividerLine.setVisibility(visibility);
		}
	}

	public void setRightDividerLineVisibility(int visibility) {
		if (Utils.checkVisibility(visibility)) {
			mRightDividerLine.setVisibility(visibility);
		}
	}

	/**
	 * 显示右上角数字提示 - 使用默认图片
	 * 
	 * @param isShow
	 */
	public void setButtonNumUpicon(boolean isShow) {
		if (isShow) {
			mButtonTag.setImageResource(R.drawable.message_img);
		} else {
			mButtonTag.setImageBitmap(null);
		}
	}

	/**
	 * 显示右上角数字提示 - 使用自定义图片
	 * 
	 * @param isShow
	 * @param resId
	 *            图片资源Id
	 */
	public void setButtonNumUpicon(boolean isShow, int resId) {
		if (isShow) {
			mButtonTag.setImageResource(resId);
		} else {
			mButtonTag.setImageBitmap(null);
		}
	}

	/**
	 * 设置右上角数字提示 - 使用默认图片
	 * 
	 * @param num
	 * @param context
	 */
	public void setButtonNumUpIcon(int num, Context context) {
		Bitmap img = generateDigitalBitmap(num, context);// 生成数字图片

		if (num > 0 && img != null) {
			mButtonTag.setImageBitmap(img);
			mButtonTag.setBackgroundResource(R.drawable.message_img);
		} else {
			mButtonTag.setImageBitmap(null);
			mButtonTag.setBackgroundDrawable(null);
		}
	}

	/**
	 * 设置右上角数字提示 - 使用自定义图片
	 * 
	 * @param num
	 * @param context
	 * @param resId
	 *            图片资源Id
	 */
	public void setButtonNumUpIcon(int num, Context context, int resId) {
		Bitmap img = generateDigitalBitmap(num, context);// 生成数字图片

		if (num > 0 && img != null) {
			mButtonTag.setImageBitmap(img);
			mButtonTag.setBackgroundResource(resId);
		} else {
			mButtonTag.setImageBitmap(null);
			mButtonTag.setBackgroundDrawable(null);
		}
	}

	public void setEnabled(boolean enabled) {
		mButtonImg.setEnabled(enabled);
		mButton.setEnabled(enabled);
		mLayerEnabled.setVisibility(enabled ? View.GONE : View.VISIBLE);
	}

	/**
	 * 数字图片生成器
	 * 
	 * @return
	 */
	private Bitmap generateDigitalBitmap(int num, Context context) {
		Bitmap result = Bitmap.createBitmap(Utils.dip2px(context, 18), Utils.dip2px(context, 18), Config.ARGB_8888);

		Canvas canvas = new Canvas();
		canvas.setBitmap(result);

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setColor(0x00ffffff);

		canvas.drawRoundRect(new RectF(Utils.dip2px(context, 2), Utils.dip2px(context, 2), Utils.dip2px(context, 16),
				Utils.dip2px(context, 16)), Utils.dip2px(context, 5), Utils.dip2px(context, 5), paint);

		paint.setStyle(Style.FILL);
		paint.setColor(Color.WHITE);

		if (num < 10) {
			paint.setTextSize(Utils.dip2px(context, 11));
			canvas.drawText("" + num, Utils.dip2px(context, 6), Utils.dip2px(context, 13), paint);
		} else {
			paint.setTextSize(Utils.dip2px(context, 8));
			canvas.drawText("" + num, Utils.dip2px(context, 5), Utils.dip2px(context, 12), paint);
		}

		return result;
	}
}
