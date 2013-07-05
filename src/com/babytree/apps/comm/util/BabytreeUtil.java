package com.babytree.apps.comm.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.config.ShareKeys;
import com.babytree.apps.comm.ui.activity.BabytreeActivity;
import com.babytree.apps.pregnancy.R;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateResponse;

public final class BabytreeUtil {

	private BabytreeUtil() {
	}

	private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
	private static final SimpleDateFormat HM_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.CHINA);
	private final static SimpleDateFormat ONLY_HM_FORMAT = new SimpleDateFormat("HH:mm", Locale.CHINA);

	private static final long MINUTE_AGO = 1 * 60 * 1000; // 1分钟以前

	private static final long HOUR_AGO = 1 * 60 * 60 * 1000; // 1小时以前

	private static final long DAY_AGO = 24 * 60 * 60 * 1000; // 24小时以前

	private static final long FIVE_DAY_AGO = 5 * 24 * 60 * 60 * 1000; // 5天以前

	private static final DisplayMetrics displayMetrics = new DisplayMetrics();

	/**
	 * 计算宝宝生日
	 * 
	 * @param birthdayTs
	 *            毫秒数
	 * @return
	 */
	public static String getBabyBirthday(long birthdayTs) {
		Calendar birthday = Calendar.getInstance(Locale.CHINA);
		birthday.setTimeInMillis(birthdayTs);
		Calendar now = Calendar.getInstance(Locale.CHINA);
		if (birthday.getTimeInMillis() > now.getTimeInMillis()) {
			return "您的宝宝还没有出生";
		}

		int day = now.get(Calendar.DAY_OF_MONTH) - birthday.get(Calendar.DAY_OF_MONTH);
		int month = now.get(Calendar.MONTH) - birthday.get(Calendar.MONTH);
		int year = now.get(Calendar.YEAR) - birthday.get(Calendar.YEAR);
		if (day < 0) {
			month -= 1;
			now.add(Calendar.MONTH, -1);// 得到上一个月，用来得到上个月的天数。
			day = day + now.getActualMaximum(Calendar.DAY_OF_MONTH);
		}
		if (month < 0) {
			month = (month + 12) % 12;
			year--;
		}
		if (birthday.getTimeInMillis() == now.getTimeInMillis()) {
			return "您的宝宝已经" + day + "天";
		}
		if (year == 0 && month == 0) {
			return "您的宝宝已经" + day + "天";
		}
		if (year == 0) {
			return "您的宝宝已经" + month + "个月" + day + "天";
		}

		return "您的宝宝已经" + year + "岁" + month + "个月";

	}

	/**
	 * 计算两个时间的间隔天数
	 * 
	 * @author wangshuaibo
	 * @param timestamp1
	 *            毫秒数
	 * @param timestamp2
	 *            毫秒数
	 * @return
	 */
	public static int getBetweenDays(long timestamp1, long timestamp2) {
		int betweenDays = 0;
		Calendar c1 = Calendar.getInstance(Locale.CHINA);
		Calendar c2 = Calendar.getInstance(Locale.CHINA);
		c1.setTimeInMillis(timestamp1);
		c2.setTimeInMillis(timestamp2);
		// 保证第二个时间一定大于第一个时间
		if (c1.after(c2)) {
			c1 = c2;
			c2.setTimeInMillis(timestamp1);
		}
		int betweenYears = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
		betweenDays = c2.get(Calendar.DAY_OF_YEAR) - c1.get(Calendar.DAY_OF_YEAR);
		for (int i = 0; i < betweenYears; i++) {
			c1.set(Calendar.YEAR, (c1.get(Calendar.YEAR) + 1));
			betweenDays += c1.getMaximum(Calendar.DAY_OF_YEAR);
		}
		return betweenDays;
	}

	/**
	 * 计算预产期
	 * 
	 * @param birthdayTs
	 *            毫秒
	 * @return
	 */
	public static String getPregrancy(long birthdayTs) {

		int hasDaysNum = getBetweenDays(System.currentTimeMillis(), birthdayTs);

		if (hasDaysNum > 0 && hasDaysNum < 281) {

			int week = (281 - hasDaysNum) / 7;

			int day = (281 - hasDaysNum) % 7;

			return (day == 0) ? (week + "周") : (week + "周" + day + "天");

		} else if (hasDaysNum >= 281) {
			return 0 + "周" + 0 + "天";
		} else {
			return "您的宝宝已经出生";
		}

	}

	/**
	 * 返回当前程序版本名
	 */
	public static String getAppVersionName(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return pi.versionName;
		} catch (Exception e) {
			return "";
		}
	}

	/**
	 * 返回当前程序版本号
	 */
	public static String getAppVersionCode(Context context) {
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			return String.valueOf(pi.versionCode);
		} catch (Exception e) {
			e.printStackTrace();
			return "0";
		}
	}

	/**
	 * 返回mac地址
	 */
	public static String getMacAddress(Context context) {
		String mac = "";
		try {
			WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
			WifiInfo info = wifi.getConnectionInfo();
			mac = info.getMacAddress();
			if (mac == null || mac.equalsIgnoreCase("null")) {
				mac = "";
			}
			return mac;
		} catch (Exception e) {
			return mac;
		}
	}

	/**
	 * 通过服务器返回的信息status来判定message是什么提示
	 */
	public static String getMessage(String status) {
		String message = "";
		if (status != null) {
			if (status.equals("failed")) {
				message = "操作失败";
			} else if (status.equals("nonLogin")) {
				message = "没有登录";
			} else if (status.equals("invalidParams")) {
				message = "参数错误";
			} else if (status.equals("invalid_token")) {
				message = "token验证不通过";
			} else if (status.equals("no_bind")) {
				message = "没有绑定用户";
			} else if (status.equals("already_binded")) {
				message = "已经绑定了";
			} else if (status.equals("wrong_email_or_password")) {
				message = "用户名或密码错误";
			} else if (status.equals("bind_failed")) {
				message = "绑定失败";
			} else if (status.equals("email_empty")) {
				message = "您还没有填写邮箱";
			} else if (status.equals("email_format_illegal")) {
				message = "邮箱的格式有误";
			} else if (status.equals("email_to_lang")) {
				message = "邮箱过长";
			} else if (status.equals("email_already_exists")) {
				message = "该邮箱已有人注册";
			} else if (status.equals("email_blocked")) {
				message = "邮箱中含有违禁语";
			} else if (status.equals("nickname_empty")) {
				message = "您还没有填写昵称";
			} else if (status.equals("nickname_too_short")) {
				message = "昵称过短";
			} else if (status.equals("nickname_too_long")) {
				message = "昵称过长";
			} else if (status.equals("nickname_too_invalid")) {
				message = "此昵称不可用";
			} else if (status.equals("nickname_blocked")) {
				message = "昵称中含有违禁语";
			} else if (status.equals("nickname_special_char")) {
				message = "昵称中不能包含特殊符号、空格";
			} else if (status.equals("nickname_whitescpace")) {
				message = "昵称中不能包含空格";
			} else if (status.equals("nickname_alpeady_exists")) {
				message = "该昵称已有人注册，您可以在昵称后面加上一些数字来避免重复，如宝宝生日或您的纪念日";
			} else if (status.equals("reg_failed")) {
				message = "注册失败";
			} else if (status.equals("father_edition_invalid_code")) {
				message = "邀请码无效";
			} else if (status.equals("father_edition_no_bind")) {
				message = "还没有绑定";
			} else {
				message = "系统错误";
			}
		}
		return message;
	}

	/**
	 * 旋转一张图片
	 * 
	 * @param srcBitmap
	 * @param degrees
	 * @return
	 */
	public final static Bitmap rotationBitmap(Bitmap srcBitmap, float degrees) {
		Bitmap result = null;
		if (degrees != 0 && srcBitmap != null) {
			Matrix m = new Matrix();
			m.setRotate(degrees, (float) srcBitmap.getWidth() / 2, (float) srcBitmap.getHeight() / 2);
			try {
				Bitmap b2 = Bitmap.createBitmap(srcBitmap, 0, 0, srcBitmap.getWidth(), srcBitmap.getHeight(), m, true);
				if (srcBitmap != b2) {
					srcBitmap.recycle();
					srcBitmap = b2;
				}
				result = b2;
			} catch (OutOfMemoryError ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}

	/**
	 * 根据原图缩放出一张制定尺寸的Bitmap
	 * 
	 * @param src
	 *            原始图片
	 * @param newWidth
	 *            新宽度
	 * @param newHeight
	 *            新高度,被忽略，采用等比例缩放
	 * @return 缩放后的Bitmap
	 * */
	public static final Bitmap scaleBitmap(Bitmap src, int newWidth, int newHeight) {
		// 获得图片的宽高
		float width = src.getWidth();
		float height = src.getHeight();
		// // 设置想要的大小
		newHeight = (int) ((newWidth / width) * height);
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);

		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(src, 0, 0, (int) width, (int) height, matrix, true);
		return newbm;
	}

	public static int computeSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength, maxNumOfPixels);
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

	private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength),
				Math.floor(h / minSideLength));
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

	/**
	 * 清除cookies
	 * 
	 * @param context
	 */
	public static void clearCookies(Context context) {
		CookieSyncManager.createInstance(context); // 得到同步cookie的对象
		CookieManager cookieManager = CookieManager.getInstance(); // 管理cookie的对象
		cookieManager.removeAllCookie();
	}

	/**
	 * 设置cookies
	 * 
	 * @param context
	 * @param url
	 *            需要设置的网页地址
	 * @param cookie
	 *            内容
	 */
	public static void setCookies(Context context, String url, String cookie) {
		if (cookie != null && !cookie.trim().equals("")) {
			CookieSyncManager.createInstance(context); // 得到同步cookie的对象
			CookieManager cookieManager = CookieManager.getInstance(); // 管理cookie的对象
			// 设置cookie
			cookieManager.setCookie(url, "NL=" + cookie);
			cookieManager.setCookie(url, "domain=" + ".babytree.com");
			cookieManager.setCookie(url, "version=" + "0");
			cookieManager.setCookie(url, "path=" + "/");
			cookieManager.setCookie(url, "expiry=" + "86400");
			CookieSyncManager.getInstance().sync();
		}
	}

	/**
	 * 判断应用是孕期还是育儿 true:育儿 ，否则孕期 默认为孕期
	 */
	public static boolean isPregnancy(Context context) {
		boolean isPregnancy = SharedPreferencesUtil.getBooleanValue(context, ShareKeys.IS_PREGNANCY, false);
		return isPregnancy;
	}

	/**
	 * 判断是否登录
	 * <p>
	 * false - 未登录
	 * <p>
	 * true - 已登录
	 */
	public static boolean isLogin(Context context) {
		String loginString = SharedPreferencesUtil.getStringValue(context, ShareKeys.LOGIN_STRING);
		return (loginString == null || loginString.equalsIgnoreCase("")) ? false : true;
	}

	/**
	 * 去除List重复内容
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static void removeDuplicate(ArrayList arlList) {
		HashSet h = new HashSet(arlList);
		arlList.clear();
		arlList.addAll(h);
	}

	/**
	 * 去除List重复内容
	 * 
	 * @param arlList
	 * @param list
	 * @return 返回去除重复之后的list
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static ArrayList removeDuplicate(ArrayList arlList, ArrayList list) {
		for (Object object : list) {
			if (!arlList.contains(object)) {
				arlList.add(object);
			}
		}
		return arlList;
	}

	/**
	 * 格式化时间
	 * 
	 * @author wangshuaibo
	 * @param time
	 *            秒数
	 * @return 1分钟:X秒前;1小时:X分钟前;1天:X小时前;5天内:X天前;>5天:yyyy-MM-dd;
	 */
	public static String formatTimestamp(long time) {
		long currentTime = System.currentTimeMillis();
		long serverTime = time * 1000;
		long intervalTime = currentTime - serverTime;
		if (intervalTime < MINUTE_AGO) {
			long l = (intervalTime % (1000 * 60)) / 1000;
			if (l > 0)
				return l + "秒前";
			else
				return "1秒前";
		} else if (intervalTime < HOUR_AGO) {
			return (intervalTime % (1000 * 60 * 60)) / (1000 * 60) + "分钟前";
		} else if (intervalTime < DAY_AGO) {
			return (intervalTime % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60) + "小时前";
		} else if (intervalTime < FIVE_DAY_AGO) {
			return intervalTime / (1000 * 60 * 60 * 24) + "天前";
		} else {
			return FORMAT.format(new Date(serverTime));
		}
	}

	/**
	 * 格式化时间
	 * 
	 * @author wangshuaibo
	 * @param time
	 *            秒数
	 * @return yyyy-MM-dd格式时间
	 */
	public static String timestempToString(long time) {
		return FORMAT.format(new Date(time * 1000));
	}

	/**
	 * 格式化时间
	 * 
	 * @author wangshuaibo
	 * @param time
	 *            秒数
	 * @return yyyy-MM-dd格式时间
	 */
	public static String timestempToString(String time) {
		return FORMAT.format(new Date(Long.parseLong(time) * 1000));
	}

	/**
	 * 格式化时间
	 * 
	 * @author wangshuaibo
	 * @param time
	 *            秒数
	 * @return yyyy-MM-dd HH:mm格式时间
	 */
	public static String timestempToStringMore(String time) {
		return HM_FORMAT.format(new Date(Long.parseLong(time) * 1000));
	}

	/**
	 * 格式化时间
	 * 
	 * @author wangshuaibo
	 * @param time
	 *            毫秒数
	 * @return yyyy-MM-dd HH:mm格式时间
	 */
	public static String timestempToStringMore2(String time) {
		return HM_FORMAT.format(new Date(Long.parseLong(time)));
	}

	/**
	 * 返回屏幕宽(px)
	 */
	public static int getScreenWidth(Activity activity) {
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.widthPixels;
	}

	/**
	 * 返回屏幕高(px)
	 */
	public static int getScreenHeight(Activity activity) {
		activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
		return displayMetrics.heightPixels;
	}

	/**
	 * 网络状态判断
	 * 
	 * @param context
	 * @return
	 */
	public static boolean hasNetwork(Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getApplicationContext().getSystemService(
				Context.CONNECTIVITY_SERVICE);
		if (manager == null) {
			return false;
		}
		NetworkInfo networkinfo = manager.getActiveNetworkInfo();
		if (networkinfo == null || !networkinfo.isAvailable()) {
			return false;
		}
		return true;

	}

	/**
	 * 获取网络
	 * 
	 * @param context
	 * @return
	 */
	public static String getExtraInfo(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo nwInfo = connectivity.getActiveNetworkInfo();
		if (nwInfo == null) {
			return null;
		}
		String extraInfo = nwInfo.getExtraInfo();
		String typeName = nwInfo.getTypeName();
		if (typeName != null && typeName.equalsIgnoreCase("WIFI")) {
			return typeName;
		}
		return extraInfo;
	}

	/**
	 * 大于2天显示完整时间 2天内显示昨天 HH:mm 1天内显示 HH:mm
	 * 
	 * @param time
	 *            时间秒数
	 * @return
	 */
	public static String formatTimestampForNotice(long time) {
		long currentTime = System.currentTimeMillis();
		long serverTime = time * 1000;
		long intervalTime = currentTime - serverTime;
		if (intervalTime / DAY_AGO < 1) {
			return ONLY_HM_FORMAT.format(new Date(serverTime));
		} else if (intervalTime / DAY_AGO < 2) {
			return "昨天 " + ONLY_HM_FORMAT.format(new Date(serverTime));
		} else {
			return HM_FORMAT.format(new Date(serverTime));
		}
	}

	/**
	 * 调用系统自带应用分享
	 * 
	 * @author wangshuaibo
	 * @param context
	 */
	public static void shareApp(Context context) {
		Intent it = new Intent(Intent.ACTION_SEND);
		it.setType("text/plain");
		it.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(R.string.share_title));
		String content = context.getResources().getString(R.string.share_content)
				.replace("${URL}", CommConstants.APK_DADDY_LOAD_URL);
		it.putExtra(Intent.EXTRA_TEXT, content);
		context.startActivity(Intent.createChooser(it, ""));
	}

	/**
	 * 取得字段真实长度（中文2个字符，英文1个字符）的方法
	 * 
	 * @param value
	 * @return
	 */
	public static int getStringLength(String value) {
		if (null == value || "".equals(value)) {
			return 0;
		} else {
			int valueLength = 0;
			String chinese = "[\u4e00-\u9fa5]";
			for (int i = 0; i < value.length(); i++) {
				String temp = value.substring(i, i + 1);
				if (temp.matches(chinese)) {
					valueLength += 2;
				} else {
					valueLength += 1;
				}
			}
			return valueLength;
		}

	}

	/**
	 * 判断是否是选择province_name还是city_name true 为province_name false 为city_name
	 */
	public static boolean ProvinceORcity(String location) {
		if ("北京市".equals(location) || "天津市".equals(location) || "重庆市".equals(location) || "上海市".equals(location)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 拷贝文件
	 * 
	 * @param fromFile
	 * @param toFile
	 * @param rewrite
	 * @author wangbingqi
	 */
	public static void copyFile(File fromFile, File toFile, Boolean rewrite) {
		if (!fromFile.exists()) {
			return;
		}
		if (!fromFile.isFile()) {
			return;
		}
		if (!fromFile.canRead()) {
			return;
		}
		if (!toFile.getParentFile().exists()) {
			toFile.getParentFile().mkdirs();
		}
		if (toFile.exists() && rewrite) {
			toFile.delete();
		}

		try {
			java.io.FileInputStream fosfrom = new java.io.FileInputStream(fromFile);
			java.io.FileOutputStream fosto = new FileOutputStream(toFile);
			byte bt[] = new byte[1024];
			int c;
			while ((c = fosfrom.read(bt)) > 0) {
				fosto.write(bt, 0, c);
			}
			fosfrom.close();
			fosto.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	/**
	 * 检查版本更新(umeng)
	 * 
	 * @param isShowMsg
	 *            是否提示检查结果信息
	 * @author wangshuaibo
	 */
	public static void checkVersionUpdate(final Context mContext, final boolean isShowMsg) {
		UmengUpdateAgent.update(mContext);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus, UpdateResponse updateInfo) {
				switch (updateStatus) {
				case 0: // has update
					UmengUpdateAgent.showUpdateDialog(mContext, updateInfo);
					break;
				case 1: // has no update
					if (isShowMsg) {
						Toast.makeText(mContext, "没有检测到新版本", Toast.LENGTH_SHORT).show();
					}
					break;
				case 2: // none wifi
					if (isShowMsg) {
						Toast.makeText(mContext, "没有wifi连接， 只在wifi下更新", Toast.LENGTH_SHORT).show();
					}
					break;
				case 3: // time out
					if (isShowMsg) {
						Toast.makeText(mContext, "超时,请重试", Toast.LENGTH_SHORT).show();
					}
					break;
				}
			}
		});
	}

	/**
	 * 判断android设备中是否有相应的应用来处理这个Intent。
	 * 
	 * @param context
	 * @param intent
	 * @return
	 */
	public static boolean hasIntentActivities(Context context, Intent intent) {
		PackageManager packageManager = context.getPackageManager();
		List<ResolveInfo> list = packageManager.queryIntentActivities(intent, 0);

		return list.size() > 0;
	}

	/**
	 * 启动新的activity
	 * 
	 * @param activity
	 * @param intent
	 * @param forResult
	 *            是否启用startActivityForResult方法
	 * @param requestCode
	 *            请求码:若forResult为false时候，不需要使用startActivityForResult方法，此设置无效
	 */
	public static void launch(Activity activity, Intent intent, boolean forResult, int requestCode) {
		if (forResult) {
			activity.startActivityForResult(intent, requestCode);
		} else {
			activity.startActivity(intent);
		}
	}

	/**
	 * 设置html格式的颜色值
	 * 
	 * @param text
	 *            需要设置的文本
	 * @param color
	 *            需要设置的颜色：#ffffff
	 * @return
	 */
	public static String setHtmlColor(String text, String color) {
		return "<font color=\"" + color + "\">" + text + "</font>";
	}

	/**
	 * 关闭所有实现broadcastReceiverForPregnancy的界面
	 * 
	 * @author wangshuaibo
	 */
	public static void closeOtherActivity(Context context) {
		Intent intent = new Intent();
		intent.setAction(BabytreeActivity.ACTION_EXITAPP);
		intent.setPackage(context.getPackageName());
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
	}

	public static int getMeasuredWidth(View v) {
		int leftw = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int lefth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		v.measure(leftw, lefth);
		return v.getMeasuredWidth();
	}

	public static int getMeasuredHeight(View v) {
		int leftw = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		int lefth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		v.measure(leftw, lefth);
		return v.getMeasuredHeight();
	}

	private static PopupWindow pop = null;

	/**
	 * 显示蒙层
	 * 
	 * @param context
	 * @param parent
	 * @param drawableId
	 */
	public static void showPopWindow(Context context, View parent, int drawableId) {
		if (drawableId == 0) {
			return;
		}
		if (pop == null) {
			ImageView imageView = null;
			imageView = new ImageView(context);
			imageView.setBackgroundResource(drawableId);
			pop = new PopupWindow(imageView, LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
			imageView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (pop != null) {
						pop.dismiss();
						pop = null;
					}
				}
			});
		}
		if (!pop.isShowing()) {
			pop.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

		}
	}

	/**
	 * 设置Selector
	 * <p>
	 * 不需要设置 传-1
	 * 
	 * @param context
	 * @param idNormal
	 * @param idPressed
	 * @param idFocused
	 * @param idUnable
	 * @return
	 */
	public static StateListDrawable newSelector(Context context, int idNormal, int idPressed, int idFocused,
			int idUnable) {
		StateListDrawable bg = new StateListDrawable();
		Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);
		Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);
		Drawable focused = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);
		Drawable unable = idUnable == -1 ? null : context.getResources().getDrawable(idUnable);
		// View.PRESSED_ENABLED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, pressed);
		// View.ENABLED_FOCUSED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_enabled, android.R.attr.state_focused }, focused);
		// View.ENABLED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_enabled }, normal);
		// View.FOCUSED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_focused }, focused);
		// View.WINDOW_FOCUSED_STATE_SET
		bg.addState(new int[] { android.R.attr.state_window_focused }, unable);
		// View.EMPTY_STATE_SET
		bg.addState(new int[] {}, normal);
		return bg;
	}

	/**
	 * 获取怀孕的周数
	 * 
	 * @return
	 */
	public static int getPregnancyWeeks(Context context) {
		long birthdayTs = SharedPreferencesUtil.getLongValue(context, ShareKeys.BIRTHDAY_TIMESTAMP);
		int hasDaysNum = getBetweenDays(System.currentTimeMillis(), birthdayTs);

		if (hasDaysNum > 0 && hasDaysNum < 281) {

			int week = (281 - hasDaysNum) / 7;

			// int day = (281 - hasDaysNum) % 7;

			return week;

		} else if (hasDaysNum >= 281) {
			return 0;
		} else {
			return 41; // 宝宝已经出生，怀孕周数大于40
		}

	}
}
