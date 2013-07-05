package com.babytree.apps.comm.config;

public final class ShareKeys {

	private ShareKeys() {
	}

	/**
	 * 判断进孕期或育儿标志值 (boolean)
	 */
	public static final String IS_PREGNANCY = "is_pregnancy";
	/**
	 * 孕产期毫秒数;宝宝生日;(long)
	 */
	public static final String BIRTHDAY_TIMESTAMP = "birthday_timestamp";
	/**
	 * 孕产期(如:201208)(String)
	 */
	public static final String BIRTHDAY = "birthday";
	/**
	 * 大于40周提醒一次(boolean)
	 */
	public static final String IS_NOTIFY = "is_notify";
	/**
	 * 孕期关爱提醒(最大怀孕天数)(int)
	 */
	public static final String PREGNANCY_MAX_DAYS = "pregnancy_max_days";
	/**
	 * 育儿关爱提醒(最大怀孕天数)(int)
	 */
	public static final String PARENTING_MAX_DAYS = "parenting_max_days";
	/**
	 * 是否需要重新登录(boolean)
	 */
	public static final String IS_NEED_LOGING = "is_need_login";
	/**
	 * 是否需要同步预产期(boolean)
	 */
	public static final String IS_NEED_PRE = "is_need_pre";
	/**
	 * 邮箱(String)
	 */
	public static final String EMAIL = "email";
	/**
	 * 登录token(String)
	 */
	public static final String LOGIN_STRING = "login_string";
	/**
	 * 用户ID(String)
	 */
	public static final String USER_ENCODE_ID = "user_encode_id";
	/**
	 * 昵称(String)
	 */
	public static final String NICKNAME = "nickname";
	/**
	 * 是否可以修改昵称
	 */
	public static final String CAN_MODIFY_NICKNAME = "can_modify_nickname";
	/**
	 * 头像地址(String)
	 */
	public static final String HEAD = "head";
	/**
	 * 注册时间(long)
	 */
	public static final String REG_TS = "reg_ts";
	/**
	 * 性别
	 */
	public static final String GENDER = "gender";
	/**
	 * 地址标识
	 */
	public static final String LOCATION = "location";
	/**
	 * 是否登录(boolean)
	 */
	public static final String ISLOGINSTR = "isLoginStr";
	/**
	 * 是否点击了选择医院朦层(boolean)
	 */
	public static final String CHOICEHOSPITALMC = "choiceHospitalMC";

	/**
	 * 是否点击了首页朦层(boolean)
	 */
	public static final String HOMEPAGEMC = "homepagemc";
	/**
	 * 育儿宝宝天数
	 */
	public static final String Y_HASDAYS = "y_hasDays";
	/**
	 * 是否提交了本地收藏
	 */
	public static final String MSTATEFORFAV = "mStateForFav";

	/**
	 * 网页Cookie
	 */
	public static final String COOKIE = "cookie";

	/**
	 * 是否显示切换育儿按钮标识
	 */
	public static final String IMG_CHANGE_APP_NOTICE = "img_change_app_notice";
	/**
	 * 本地头像地址
	 */
	public static final String IMAGE = "image";
	/**
	 * 是否选择了医院
	 */
	public static final String ISCHOICEHOSPITAL = "isChoiceHospital";
	/**
	 * 是否需要同步医院
	 */
	public static final String ISNESSARYSYN = "isNessarySyn";
	/**
	 * 医院名字
	 */
	public static final String HOSPITAL_NAME = "hospital_name";
	/**
	 * 医院地址
	 */
	public static final String LOCATION_FOR_HOSPITAL = "location_for_hospital";
	// 计算孕产期相关Begin==========================================================
	public static final String LAST_YUE_TXT_VALUE = "last_yue_txt_value";
	public static final String LAST_INPUT_TXT_VALUE = "last_input_txt_value";
	public static final String LAST_CYCLE_TXT_VALUE = "last_cycle_txt_value";
	// 计算孕产期相关End==========================================================
	/**
	 * 登录方式
	 */
	public static final String WHICH_THIRD = "whichThird";
	/**
	 * 医院圈子ID
	 */
	public static final String GROUP_ID = "group_id";
	/**
	 * 医院ID
	 */
	public static final String HOSPITAL_ID = "hospital_id";
	/**
	 * 宝宝生日(long)
	 */
	public static final String BABY_BIRTHDAY_TS = "baby_birthday_ts";
	// 设置医院相关Begin==========================================================
	public static final String ADD_HOSPITAL_PROVINCE = "add_hospital_province";
	public static final String ADD_HOSPITAL_CITY_CODE = "add_hospital_city_code";
	public static final String ADD_HOSPITAL_CITY = "add_hospital_city";
	// 设置医院相关Begin==========================================================

	// 推送相关Begin=========================================================================
	/**
	 * 是否开启声音提醒(boolean)
	 */
	public static final String NOTIFY_SOUND = "notify_sound";
	/**
	 * 是否开启震动提醒(boolean)
	 */
	public static final String NOTIFY_VIARATE = "notify_vibrate";
	/**
	 * 是否开启提醒(boolean)
	 */
	public static final String NOTIFY_AUTO = "notify_auto";
	/**
	 * 提醒间隔(分钟)(int)
	 */
	public static final String NOTIFY_ALERM = "notify_alert";

	// 推送相关End===========================================================================
	/**
	 * 提醒时段(int)<br>
	 * 1:全天 <br>
	 * 2:早9点到晚10点 <br>
	 * 3:早8点到晚10点 <br>
	 * 4:早7点到晚10点
	 */
	public static final String NOTIFY_TIME = "notify_time";

	/**
	 * 进入版本类型的key标记
	 */
	public static final String APP_TYPE_KEY = "app_type_key";
	/**
	 * 绑定成功时的json串
	 */
	public static final String FATHER_BIND_KEY = "bind_father_value_is_json_key";

	/**
	 * 获取任务的缓存列表
	 */
	public static final String TASK_CACHE_KEY = "task_cache_list_value_is_json_key";

	/**
	 * 缓存任务列表的时间截
	 */
	public static final String TASK_CACHE_TIME_KEY = "task_cache_time_key";

	/**
	 * 妈妈ID
	 */
	public static final String MOM_ID_KEY = "mom_id_key";

	/**
	 * 完成的任务ID
	 */
	public static final String TASK_ID_SAVE_KEY = "task_id_save_key";

	/**
	 * 妈妈昵称
	 */
	public static final String MOM_NICK_NAME_KEY = "mom_mick_name_key";

	/**
	 * 邀请码保存key
	 */
	public static final String INVITE_CODE_KEY = "invite_code_key";

	/**
	 * 宝宝生日
	 */
	public static final String BABY_BIRTHDAY_TS_KEY = "baby_birthday_ts_key";
	/**
	 * 是否点击了帖子详情页朦层(boolean)
	 */
	public static final String TOPIC_MENGCENG = "topic_mengceng";
}
