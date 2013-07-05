package com.babytree.apps.biz.user.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.babytree.apps.comm.model.Base;

public class User extends Base {

	private static final long serialVersionUID = 1L;

	public String login_string = "";

	public String nickname = "";

	public String nick_name = "";

	public String email = "";

	public String location = "";

	public String gender = "";

	public String status = "";

	public String email_status = "";

	public String avatar_url = "";

	public String enc_user_id = "";

	public String reg_ts = "";

	public String can_modify_nickname = "";
	public String can_write_invitation_code = "";
	public String hospital_id;
	public String hospital_name;
	public String baby_birthday_ts;
	public String group_id;
	public String locationName;

	private static final String LOGIN_STRING = "login_string";

	private static final String NICK_NAME = "nick_name";

	private static final String NICKNAME = "nickname";

	private static final String EMAIL = "email";

	private static final String LOCATION = "location";

	private static final String GENDER = "gender";

	private static final String STATUS = "status";

	private static final String EMAIL_STATUS = "email_status";

	private static final String AVATAR_URL = "avatar_url";

	private static final String ENC_USER_ID = "enc_user_id";

	private static final String USER_INFO = "user_info";

	private static final String REG_TS = "reg_ts";

	private static final String LOCATION_NAME = "location_name";

	private static final String CAN_MODIFY_NICKNAME = "can_modify_nickname";
	private static final String CAN_WRITE_INVITATION_CODE = "can_write_invitation_code";
	private static final String HOSPITAL_ID = "hospital_id";
	private static final String HOSPITAL_NAME = "hospital_name";
	private static final String BABY_BIRTHDAY_TS = "baby_birthday_ts";
	private static final String GROUP_ID = "group_id";

	public static User parse(JSONObject jsonObject) throws JSONException {
		User bean = new User();
		if (jsonObject.has(HOSPITAL_ID)) {
			bean.hospital_id = jsonObject.getString(HOSPITAL_ID);
		}
		if (jsonObject.has(HOSPITAL_NAME)) {
			bean.hospital_name = jsonObject.getString(HOSPITAL_NAME);
		}
		if (jsonObject.has(BABY_BIRTHDAY_TS)) {
			bean.baby_birthday_ts = jsonObject.getString(BABY_BIRTHDAY_TS);
		}
		if (jsonObject.has(GROUP_ID)) {
			bean.group_id = jsonObject.getString(GROUP_ID);
		}
		if (jsonObject.has(LOGIN_STRING)) {
			bean.login_string = jsonObject.getString(LOGIN_STRING);
		}
		if (jsonObject.has(REG_TS)) {
			bean.reg_ts = jsonObject.getString(REG_TS);
		}
		if (jsonObject.has(CAN_MODIFY_NICKNAME)) {
			bean.can_modify_nickname = jsonObject.getString(CAN_MODIFY_NICKNAME);
		}
		if (jsonObject.has(CAN_WRITE_INVITATION_CODE)) {
			bean.can_modify_nickname = jsonObject.getString(CAN_WRITE_INVITATION_CODE);
		}

		if (jsonObject.has(USER_INFO)) {
			JSONObject userInfo = jsonObject.getJSONObject(USER_INFO);
			if (userInfo.has(NICKNAME)) {
				bean.nickname = userInfo.getString(NICKNAME);
			}

			if (userInfo.has(NICK_NAME)) {
				bean.nick_name = userInfo.getString(NICK_NAME);
			}
			if (userInfo.has(REG_TS)) {
				bean.reg_ts = userInfo.getString(REG_TS);
			}
			if (userInfo.has("user_name")) {
				bean.nickname = userInfo.getString("user_name");
			}
			if (userInfo.has(EMAIL)) {
				bean.email = userInfo.getString(EMAIL);
			}
			if (userInfo.has(GENDER)) {
				bean.gender = userInfo.getString(GENDER);
			}
			if (userInfo.has(LOCATION)) {
				bean.location = userInfo.getString(LOCATION);
			}
			if (userInfo.has(STATUS)) {
				bean.status = userInfo.getString(STATUS);
			}
			if (userInfo.has(EMAIL_STATUS)) {
				bean.email_status = userInfo.getString(EMAIL_STATUS);
			}
			if (userInfo.has(AVATAR_URL)) {
				bean.avatar_url = userInfo.getString(AVATAR_URL);
			}
			if (userInfo.has("avatar")) {
				bean.avatar_url = userInfo.getString("avatar");
			}
			if (userInfo.has(ENC_USER_ID)) {
				bean.enc_user_id = userInfo.getString(ENC_USER_ID);
			}
			if (userInfo.has("encode_id")) {
				bean.enc_user_id = userInfo.getString("encode_id");
			}
			if (userInfo.has(HOSPITAL_ID)) {
				bean.hospital_id = userInfo.getString(HOSPITAL_ID);
			}
			if (userInfo.has(HOSPITAL_NAME)) {
				bean.hospital_name = userInfo.getString(HOSPITAL_NAME);
			}
			if (userInfo.has(BABY_BIRTHDAY_TS)) {
				bean.baby_birthday_ts = userInfo.getString(BABY_BIRTHDAY_TS);
			}
			if (userInfo.has(GROUP_ID)) {
				bean.group_id = userInfo.getString(GROUP_ID);
			}
			if (userInfo.has(REG_TS)) {
				bean.reg_ts = userInfo.getString(REG_TS);
			}
			if (userInfo.has(LOCATION_NAME)) {
				bean.locationName = userInfo.getString(LOCATION_NAME);
			}
		}
		return bean;
	}
}
