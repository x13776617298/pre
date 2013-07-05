package com.babytree.apps.biz.topic.view;

import java.util.Locale;

import android.content.Context;
import android.text.TextUtils;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.babytree.apps.biz.father.util.Utils;
import com.babytree.apps.comm.view.EmojiTextView;
import com.babytree.apps.pregnancy.R;

public class ViewUtil {

	/**
	 * 图片宽高
	 */
	public static float IMAGE_DIP = 200F;

	public static TextView createContentTextView(Context context, String content) {
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		// Emoji textview.
		EmojiTextView tv = new EmojiTextView(context);
		tv.setLayoutParams(params);
		tv.setTextSize(17F); // 字体大小
		tv.setTextColor(context.getResources().getColor(R.color.topic_content_color));
		tv.setLineSpacing(context.getResources().getDimension(R.dimen.topic_content_line_spacing), 1);
		// Set emoji text.
		tv.setEmojiText(content);
		return tv;
	}

	public static ImageView createContentImageView(Context context) {
		int imagePxWidth = Utils.dip2px(context, IMAGE_DIP);
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imagePxWidth, imagePxWidth);
		params.gravity = Gravity.CENTER;
		ImageView iv = new ImageView(context);
		iv.setLayoutParams(params);
		return iv;
	}

	/**
	 * 检查URL是否为GIF图片
	 * @author wangshuaibo
	 * @param url
	 * @return
	 */
	public static boolean isGif(String url) {
		if (TextUtils.isEmpty(url)) {
			return true;
		}
		if (url.toLowerCase(Locale.CHINA).endsWith(".gif")) {
			return true;
		}
		return false;
	}

}
