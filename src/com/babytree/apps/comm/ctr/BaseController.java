package com.babytree.apps.comm.ctr;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class BaseController extends BabytreeController {

	public static final int PAGE_SIZE = 20;

	public static final int PAGE_MIKA_SIZE = 10;

	public static final int FAILED_CODE = -100;

	protected final static String DOCTOR_NAME = "doctor_name";

	protected final static String REPLY_LIST = "reply_list";

	protected final static String STATUS = "status";

	protected final static String ACTION = "action";

	protected final static String START = "start";

	protected final static String OFFSET = "offset";

	protected final static String LIMIT = "limit";

	protected final static String LENGTH = "length";

	protected final static String MESSAGE = "message";

	protected final static String TOTAL = "total";

	protected final static String LIST = "list";

	protected final static String LOGIN_STRING = "login_string";

	protected final static String MAC = "mac";

	protected final static String TOKEN = "token";

	protected final static String BIRTHDAY = "birthday";

	protected final static String GROUP_ID = "group_id";

	protected final static String DISCUZ_ID = "discuz_id";

	protected final static String POSITION = "position";

	protected final static String REFER_ID = "refer_id";

	protected final static String PAGE = "page";

	protected final static String TITLE = "title";

	protected final static String CONTENT = "content";

	protected final static String EMAIL = "email";

	protected final static String NICKNAME = "nickname";

	protected final static String PASSWORD = "password";

	protected final static String TYPE = "type";

	protected final static String BIRTH = "birth";

	protected final static String OTHER_BIRTH = "other_birth";

	protected final static String OTHER = "other";

	protected final static String UPLOAD_RESULT = "upload_result";

	protected final static String PHOTO_ID = "photo_id";

	protected final static String ORDERBY = "orderby";

	protected final static String IS_ELITE = "is_elite";

	protected final static String PROVINCE_ID = "province_id";

	protected final static String CITY_PROVINCE_ID = "city_province_id";

	protected final static String PG = "pg";

	protected final static String TAGS = "tag";

	protected final static String MESSAGE_ID = "message_id";

	protected final static String MESSAGE_TYPE = "message_type";

	protected final static String USER_ENCODE_ID = "user_encode_id";

	protected final static String USER_INFO = "user_info";

	protected final static String TO_USER_ENCODE_ID = "to_user_encode_id";

	protected final static String MESSAGE_LIST = "message_list";

	protected final static String INBOX_TOTAL_COUNT = "inbox_total_count";
	protected final static String OUTBOX_TOTAL_COUNT = "outbox_total_count";
	protected final static String USER_UNREAD_COUNT = "user_unread_count";
	protected final static String TOTAL_COUNT = "total_count";

	protected final static String ID = "id";

	protected final static String PREG_MONTH = "preg_month";

	protected final static String APP_NAME = "app_name";
	protected final static String CLIENT_TYPE = "client_type";

	protected final static String PLATFORM = "paltform";

	protected final static String UUID = "uuid";

	protected final static String SOURCE = "source";
	protected final static String INTERVAL = "interval";
	protected final static String FAV_TYPE = "fav_type";
	protected final static String GENDER = "gender";
	protected final static String LOCATION = "location";
	protected final static String BABY_BIRTHDAY = "baby_birthday";
	protected final static String ACT = "act";
	protected final static String VERSION_NAME = "version_name";
	protected final static String VERSION_CODE = "version_code";
	protected final static String DEVICE_ID = "device_id";
	protected final static String DATA = "data";
	protected final static String AD_ID = "ad_id";
	protected final static String MATE_ID = "mate_id";
	protected final static String URL = "url";
	
	public static final String SUCCESS_STATUS = "success";
	
	/**
	 * json取int值
	 * 
	 * @param jsonObject
	 * @param key
	 * @return
	 */
	protected static int getInt(JSONObject jsonObject, String key) {

		int tmp = 0;
		try {
			tmp = jsonObject.getInt(key);
		} catch (JSONException e) {
			tmp = 0;
		}
		return tmp;
	}
	
	/**
	 * json取int值
	 * 
	 * @param jsonObject
	 * @param key
	 * @return
	 */
	protected static String getStringInt(JSONObject jsonObject, String key) {

		String tmp = "0";
		try {
			tmp = jsonObject.getString(key);
		} catch (JSONException e) {
			tmp = "0";
		}
		return tmp;
	}

}