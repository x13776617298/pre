package com.babytree.apps.biz.father.ui;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.babytree.apps.biz.father.model.BindBoth;
import com.babytree.apps.comm.bitmap.BabytreeBitmapCache;
import com.babytree.apps.comm.tools.BabytreeLog;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.pregnancy.R;

/**
 * 爸爸版主页大图区
 * 
 * @author gaierlin
 */
public class PregnancyTipView extends RelativeLayout {
	private static final String TAG = "PregnancyTipView";
	private Context mContext;

	private BabytreeBitmapCache mBitmapCache;
	/**
	 * tipView大图
	 */
	private ImageView mBabyImg;
	private TextView mDayDate;
	private TextView mMamaTip;
	private TextView mPregnancyDay;

	private SimpleDateFormat sdf = null;

	public PregnancyTipView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		mBitmapCache = BabytreeBitmapCache.create(mContext);
		sdf = new SimpleDateFormat(mContext.getString(R.string.format_date), Locale.CHINA);
	}

	private void init(Context context) {
		mBabyImg = (ImageView) findViewById(R.id.baby_img);
		mDayDate = (TextView) findViewById(R.id.day_date);
		mMamaTip = (TextView) findViewById(R.id.mama_tip);
		mPregnancyDay = (TextView) findViewById(R.id.pregnancy_day);
	}

	@Override
	public void onFinishInflate() {
		super.onFinishInflate();
		init(mContext);
	}

	/**
	 * 爸爸版显示预产期
	 * 
	 * @param bindStatus
	 */
	public void showDate(BindBoth bindStatus) {
		if (bindStatus == null)
			return;
		Bitmap tmp = BitmapFactory.decodeResource(getResources(), R.drawable.moren);
		mBitmapCache.display(mBabyImg, bindStatus.image_url, tmp, tmp);
		mDayDate.setText(sdf.format(new Date()));
		long babyBirthday = Long.valueOf(bindStatus.bindUser.baby_birthday_ts) * 1000L;
		Log.d(TAG, "both.bindUser.baby_birthday_ts = " + babyBirthday);
		Log.d(TAG, "bindBoth toString = " + bindStatus.toString());
		String birthday = BabytreeUtil.getPregrancy(babyBirthday);
		if ("您的宝宝已经出生".equals(birthday)) {
			mMamaTip.setText("");
			mMamaTip.setVisibility(View.INVISIBLE);
		} else {
			mMamaTip.setText(mContext.getString(R.string.mama_tip));
			mMamaTip.setVisibility(View.VISIBLE);
		}
		mPregnancyDay.setText(birthday);
	}

	/**
	 * 妈妈版显示预产期
	 */
	public void showDateMommy(long babyBirthday) {
		BabytreeLog.d("计算妈妈的预产期 - " + babyBirthday + "");
		mDayDate.setText(sdf.format(new Date()));
		String birthday = BabytreeUtil.getPregrancy(babyBirthday);
		if ("您的宝宝已经出生".equals(birthday)) {
			mMamaTip.setText("");
			mMamaTip.setVisibility(View.INVISIBLE);
		} else {
			mMamaTip.setText(mContext.getString(R.string.mama_tip));
			mMamaTip.setVisibility(View.VISIBLE);
		}
		mPregnancyDay.setText(birthday);
	}

	/**
	 * 育儿版显示宝宝当前年龄大小
	 */
	public void showBabyBirthday(long babyBirthday) {
		BabytreeLog.d("显示baby年龄大小 - " + babyBirthday + "");
		mDayDate.setText(sdf.format(new Date()));
		String birthday = BabytreeUtil.getBabyBirthday(babyBirthday);
		if ("您的宝宝还没有出生".equals(birthday)) {
			mMamaTip.setText("");
			mMamaTip.setVisibility(View.INVISIBLE);
		} else {
			mMamaTip.setText(mContext.getString(R.string.mama_tip));
			mMamaTip.setVisibility(View.VISIBLE);
		}
		mMamaTip.setText("");
		mPregnancyDay.setText(birthday);
	}

	/**
	 * 更新tipView大图
	 * 
	 * @param url
	 */
	public void updateTipViewPicture(String url) {
		Bitmap tmp = BitmapFactory.decodeResource(getResources(), R.drawable.moren);
		mBitmapCache.display(mBabyImg, url, tmp, tmp);
	}

	/**
	 * 为孕期提示文字添加点击事件
	 * 
	 * @param onClickListener
	 */
	public void setPregnancyClickListener(View.OnClickListener onClickListener) {
		if (onClickListener != null) {
			mPregnancyDay.setOnClickListener(onClickListener);
		}
	}

	/**
	 * 移除孕期提示文字添加点击事件
	 * 
	 * @param onClickListener
	 */
	public void removePregnancyClickListener() {
		mPregnancyDay.setOnClickListener(null);
	}

}
