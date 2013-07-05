package com.babytree.apps.comm.config;

/**
 * 全局配置文件
 * 
 * 
 */
public class CommConstants {

	// ==============================================
	/**
	 * 提醒categroy_id
	 */
	public static final int TYPE_REMIND = 2;
	/**
	 * 讨论categroy_id
	 */
	public static final int TYPE_TOPIC = 3;
	/**
	 * 知识categroy_id
	 */
	public static final int TYPE_KNOW = 1;
	/**
	 * 早教categroy_id
	 */
	public static final int TYPE_KITCHEN19 = 19;
	/**
	 * 厨房categroy_id
	 */
	public static final int TYPE_KITCHEN5 = 5;
	// ==============================================

	/**
	 * 百度Key
	 */
	public static final String BMAP_KEY = "D5DBC688E2970307DF9723BA883E0B973BE6A26B";
	// ==============================================
	/**
	 * 活动服务请求间隔
	 */
	public static final long ACTIVITY_ALERM_INTERVAL = 1 * 60L * 60L * 1000L; // 1小时
	/**
	 * 本地重要提醒请求间隔
	 */
	public static final long LOCAL_ALERM_INTERVAL = 30L * 60L * 1000L; // 30分钟

	/**
	 * 快乐孕期活动 只打开应用
	 */
	public static final String android_promo_pregnancy_1 = "android_promo_pregnancy_1";
	/**
	 * 快乐孕期活动 打开帖子
	 */
	public static final String android_promo_pregnancy_2 = "android_promo_pregnancy_2";
	/**
	 * 快乐孕期活动 打开一个url页面
	 */
	public static final String android_promo_pregnancy_3 = "android_promo_pregnancy_3";
	// ==============================================

	/**
	 * 全局Tag标签
	 */
	public static final String COMM_TAG = "BabytreeTag";

	/**
	 * QQ登录应用ID
	 */
	public static final String TENCENT_APPID = "100246272";

	/**
	 * QQ登录
	 */
	public static final String TENCENT_CALLBACK = "auth://tauth.qq.com/";

	/**
	 * QQ登录可使用的api权限
	 */
	public static final String TENCENT_SCOPE = "get_user_info,get_user_profile,add_share,add_topic,list_album,upload_pic,add_album,add_t,add_pic_t,get_info";// 授权范围
	/**
	 * 科大讯飞APPID
	 */
	public static final String XUNFEI_APPID = "510a0b43";

	/**
	 * 爸爸版标记
	 */
	public static final String APP_TYPE_DADDY = "1";

	/**
	 * 妈妈版标记
	 */
	public static final String APP_TYPE_MOMMY = "0";

	/**
	 * 默认标记(爸爸妈妈版选择界面)
	 */
	public static final String APP_TYPE_UNKNOW = "-1";

	/**
	 * 爸爸版 APK下载URL地址
	 */
	public static final String APK_DADDY_LOAD_URL = "http://r.babytree.com/yu0cl4";

	/**
	 * 妈妈版首页大图
	 */
	public static final String HOME_PIC_MOMMY_KEY_PREGNANCY = "0";

	/**
	 * 爸爸版首页大图
	 */
	public static final String HOME_PIC_DADDY_KEY_PREGNANCY = "1";

	/**
	 * 育儿版首页大图
	 */
	public static final String HOME_PIC_MOMMY_KEY_YUER = "2";

}
