package com.babytree.apps.biz.father.util;

import java.util.Calendar;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.view.View;

import com.babytree.apps.biz.father.ui.MenuFragment.DialogOnClickListener;

public class Utils {
	/**
	 * 判断resid大于0的为合法
	 * 
	 * @return
	 */
	public static boolean checkResId(int resId) {
		return resId > 0;
	}

	/**
	 * 判断drawable不为空为合法
	 * 
	 * @return
	 */
	public static boolean checkDrawable(Drawable drawable) {
		return drawable != null;
	}

	/**
	 * 检查View的可视值是否合法
	 * 
	 * @return
	 */
	public static boolean checkVisibility(int visibility) {
		return (View.VISIBLE == visibility || View.GONE == visibility || View.INVISIBLE == visibility);
	}

	/**
	 * 将Dip转换为px
	 * 
	 * @param context
	 * @param dip
	 * @return
	 */
	public static int dip2px(Context context, float dip) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dip * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 判断是否是同一天
	 * 
	 * @param time
	 * @return
	 */
	public static boolean isThanDay(int day) {
		int tmp = Calendar.getInstance(Locale.CHINA).get(Calendar.DAY_OF_YEAR);
		return day == tmp;
	}

	public static AlertDialog dialog(Context mContext, String message, String title, String buttonOk,
			String buttonCancel, final DialogOnClickListener listener) {
		AlertDialog.Builder builder = new Builder(mContext);
		builder.setMessage(message);

		if (!TextUtils.isEmpty(title)) {
			builder.setTitle(title);
		}

		if (!TextUtils.isEmpty(buttonOk)) {
			builder.setPositiveButton(buttonOk, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if (listener != null)
						listener.okOnClick();
				}
			});
		}

		if (!TextUtils.isEmpty(buttonCancel)) {
			builder.setNegativeButton(buttonCancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					if (listener != null)
						listener.cancelOnClick();
				}
			});
		}

		return builder.create();
	}
}
