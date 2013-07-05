package com.babytree.apps.comm.config;

/**
 * 所有URL
 * 
 */
public class UrlConstrants {
	
	// 需要修改的URL Begin
	/**
	 * 线上接口地址(老地址)
	 */
	public static final String NET_URL = "http://test3.babytree.com/api/api.php";

	/**
	 * 接口地址HOST
	 */
	public static final String HOST_URL = "http://test3.babytree.com";
	/**
	 * 合并后上传图片地址
	 */
	public static final String UPLOAD_PHOTO = "http://upload.test3.babytree.com/api";
	
	// 需要修改的URL Begin

	/**
	 * 抽奖规则URL
	 */
	public static final String INVITE_HELP_URL = "http://m.babytree.com/pregnancy/rules.php";
	/**
	 * 孕期精彩活动地址
	 */
	public static final String ACTIVITY_URL = "http://m.babytree.com/promo/pregnancy.php";

	/**
	 * 申请米卡
	 */
	public static final String NET_URL_APPLY_MIKA = "http://www.mika123.com/product/apply_tryout.html";
	/**
	 * 育儿精彩活动地址
	 */
	public static final String Y_ACTIVITY_URL = "http://m.babytree.com/promo/parenting.php";

	/**
	 * 得到米卡用户评论
	 */
	public static final String NET_URL_GET_COMMENTS = HOST_URL + "/promo/mika_api/user_comment_api.php";

	/**
	 * 授权成功后进行第三方登录的接口
	 */
	public static final String THIRD_PART_LOGIN = HOST_URL + "/api/muser/third_part_login";
	/**
	 * 新用户第三方授权后注册绑定登录接口
	 */
	public static final String NEW_USER_THIRD_BD = HOST_URL + "/api/muser/third_part_reg";
	/**
	 * 第三方帐号绑定(绑定) 目前手机端支持新浪微博和腾讯微博,绑定规则和网站一致(可以参考现有的登录接口)
	 */
	public static final String OLD_USER_THIRD_BD = HOST_URL + "/api/muser/third_part_bind";

	/**
	 * 帖子页 分享当前帖子的地址host
	 */
	public static final String TOPIC_MOBILE_URL = HOST_URL + "/community/topic_mobile.php?id=";

	/**
	 * 获取医院列表or选择地区获取医院信息
	 */
	public static final String hospital_get_list_by_region = HOST_URL + "/api/hospital/get_list_by_region_simple";
	/**
	 * 设置用户医院信息
	 */
	public static final String hospital_set_hospital = HOST_URL + "/api/hospital/set_hospital";
	/**
	 * 医院搜索
	 */
	public static final String hospital_search = HOST_URL + "/api/hospital/search_simple";
	/**
	 * 医生列表
	 */
	public static final String hospital_get_doctor_list = HOST_URL + "/api/hospital/get_doctor_list";
	/**
	 * 医生帖子列表
	 */
	public static final String hospital_get_doctor_discuz_list = HOST_URL + "/api/hospital/get_doctor_discuz_list";
	/**
	 * 同医院孕妈列表
	 */
	public static final String hospital_get_user_list = HOST_URL + "/api/hospital/get_user_list";
	/**
	 * 医院信息
	 */
	public static final String hospital_get_info = HOST_URL + "/api/hospital/get_info";
	/**
	 * 广告列表
	 */
	public static final String advertising_get_banner_list = HOST_URL + "/api/advertising/get_banner_list";
	/**
	 * 获取孕妈数
	 */
	public static final String get_user_count = HOST_URL + "/api/hospital/get_user_count";
	/**
	 * 获取热门城市和分区医院
	 */
	public static final String get_sorted_list_by_region = HOST_URL + "/api/hospital/get_sorted_list_by_region_simple";

	/**
	 * 获取个人额外信息
	 */
	public static final String USER_INFO = HOST_URL + "/api/yunqi_mobile/user_info";
	/**
	 * 签到
	 */
	public static final String CHECKIN = HOST_URL + "/api/yunqi_mobile/checkin";
	/**
	 * 抽奖
	 */
	public static final String GET_LOTTERY = HOST_URL + "/api/yunqi_mobile/lottery";

	/**
	 * 水果
	 */
	public static final String GET_FRUIT = HOST_URL + "/api/muser/get_fruit_info";

	/**
	 * 对话列表
	 */
	public static final String SESSION_MESSAGE = HOST_URL + "/api/session_message/message_list";
	/**
	 * 删除短消息
	 */
	public static final String DLET_MESSAGE = HOST_URL + "/api/session_message/del_message";

	public static final String GET_APPLY = HOST_URL + "/api/yunqi_mobile/set_userinfo";
	/**
	 * 获取商店列表
	 */
	public static final String GET_PRODUCET_LIST = HOST_URL + "/api/yunqi_mobile/get_produce_list";
	/**
	 * 输入邀请码
	 */
	public static final String INVITE = HOST_URL + "/api/yunqi_mobile/invite";

	/**
	 * 月嫂URL
	 */
	public static final String YS_BASE_URL = "http://mapi.babytree.com";

	/**
	 * 取得全部月嫂信息
	 */
	public static final String GET_NURSES = YS_BASE_URL + "/nurse/get_nurses.php";

	/**
	 * 取得全部月嫂所在公司信息
	 */
	public static final String GET_COMPANY = YS_BASE_URL + "/nurse/get_company.php";

	/**
	 * 取得月嫂评论信息
	 */
	public static final String GET_NURSE_DISCUSS = YS_BASE_URL + "/nurse/get_nurse_discuss.php";

	/**
	 * 发表月嫂评论信息
	 */
	public static final String SET_NURSE_DISCUSS = YS_BASE_URL + "/nurse/set_nurse_discuss.php";

	public static final String GET_COMMENT_DETAIL = HOST_URL + "/promo/mika_api/get_comment_detail.php?";

	/**
	 * 申请BabyBox地址
	 */
	public static final String BABYBOX_URL = "http://m.babytree.com/babybox/index_new.php?show=app";

	/**
	 * 专家在线
	 */
	public static final String EXPERT_ON_LINE = "http://m.babytree.com/promo/expert_online.php?from=app";

	/**
	 * 关于我们HTML地址
	 */
	public static final String ABOUT_US = "file:///android_asset/others/about.html";
}
