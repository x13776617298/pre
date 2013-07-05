package com.babytree.apps.comm.model;

import org.json.JSONException;
import org.json.JSONObject;


public class HospitalMother extends Base {

	private static final long serialVersionUID = 1L;


	public String nickname = "";
	public String enc_user_id = "";
	public String avatar_url = "";
	public String babybirthday = "";
	public String baby_age = "";
	private static final String ENC_USER_ID = "enc_user_id";
	private static final String NICKNAME = "nickname";
	private static final String AVATAR_URL = "avatar_url";
	private static final String BABYBIRTHDAY = "babybirthday";
	private static final String BABY_AGE = "baby_age";
	public static HospitalMother parse(JSONObject jsonObject) throws JSONException {
		HospitalMother bean = new HospitalMother();
		if (jsonObject.has(ENC_USER_ID)) {
			bean.enc_user_id = jsonObject.getString(ENC_USER_ID);
		}
		if (jsonObject.has(NICKNAME)) {
			bean.nickname = jsonObject.getString(NICKNAME);
		}
		if (jsonObject.has(AVATAR_URL)) {
			bean.avatar_url = jsonObject.getString(AVATAR_URL);
		}
		if (jsonObject.has(BABYBIRTHDAY)) {
			bean.babybirthday = jsonObject.getString(BABYBIRTHDAY);
		}
		if (jsonObject.has(BABY_AGE)) {
			bean.baby_age = jsonObject.getString(BABY_AGE);
		}
		return bean;
	}
}
