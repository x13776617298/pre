package com.babytree.apps.comm.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Discuz extends Base {

	private static final long serialVersionUID = -200605627528470366L;

	public int discuz_id = 0;

	public String title = "";

	public String summary = "";

	public String url = "";

	public String author_id = "";

	public String author_name = "";
	
	public String author_avatar = "";

	public long create_ts = 0;

	public long update_ts = 0;

	public int response_count = 0;

	public int author_response_count = 0;

	public long last_response_ts = 0;

	public long pv_count = 0;

	public int is_top;

	public int is_elite;

	public int is_newbie;

	public int has_pic;

	public int is_fav;

	public String last_response_user_id = "";

	public String last_response_user_name = "";

	private static final String DISCUZ_ID = "id";

	private static final String TITLE = "title";

	private static final String SUMMARY = "summary";

	private static final String URL = "url";

	private static final String AUTHOR_ID = "author_id";

	private static final String AUTHOR_NAME = "author_name";
	
	private static final String AUTHOR_AVATAR = "author_avatar";

	private static final String CREATE_TS = "create_ts";

	private static final String UPDATE_TS = "update_ts";

	private static final String RESPONSE_COUNT = "response_count";

	private static final String AUTHOR_RESPONSE_COUNT = "author_response_count";

	private static final String LAST_RESPONSE_TS = "last_response_ts";

	private static final String LAST_RESPONSE_USER_ID = "last_response_user_id";

	private static final String LAST_RESPONSE_USER_NAME = "last_response_user_name";

	private static final String PV_COUNT = "pv_count";

	private static final String IS_TOP = "is_top";

	private static final String IS_ELITE = "is_elite";

	private static final String IS_NEWBIE = "is_newbie";

	private static final String HAS_PIC = "has_pic";

	private static final String IS_FAV = "is_fav";

	public static Discuz parse(JSONObject jsonObject) throws JSONException {
		Discuz bean = new Discuz();
		if (jsonObject.has(DISCUZ_ID)) {
			bean.discuz_id = getInt(jsonObject, DISCUZ_ID);
		}
		if (jsonObject.has(TITLE)) {
			bean.title = jsonObject.getString(TITLE).trim();
		}
		if (jsonObject.has(SUMMARY)) {
			bean.summary = jsonObject.getString(SUMMARY).trim();
		}
		if (jsonObject.has(URL)) {
			bean.url = jsonObject.getString(URL).trim();
		}
		if (jsonObject.has(AUTHOR_ID)) {
			bean.author_id = jsonObject.getString(AUTHOR_ID).trim();
		}
		if (jsonObject.has(AUTHOR_NAME)) {
			bean.author_name = jsonObject.getString(AUTHOR_NAME).trim();
		}
		if (jsonObject.has(AUTHOR_AVATAR)) {
			bean.author_avatar = jsonObject.getString(AUTHOR_AVATAR).trim();
		}
		if (jsonObject.has(CREATE_TS)) {
			bean.create_ts = getLong(jsonObject, CREATE_TS);
		}
		if (jsonObject.has(UPDATE_TS)) {
			bean.update_ts = getLong(jsonObject, UPDATE_TS);
		}
		if (jsonObject.has(RESPONSE_COUNT)) {
			bean.response_count = getInt(jsonObject, RESPONSE_COUNT);
		}
		if (jsonObject.has(AUTHOR_RESPONSE_COUNT)) {
			bean.author_response_count = jsonObject
					.getInt(AUTHOR_RESPONSE_COUNT);
		}
		if (jsonObject.has(PV_COUNT)) {
			bean.pv_count = getInt(jsonObject, PV_COUNT);
		}
		if (jsonObject.has(IS_TOP)) {
			bean.is_top = getInt(jsonObject, IS_TOP);
		}
		if (jsonObject.has(IS_NEWBIE)) {
			bean.is_newbie = getInt(jsonObject, IS_NEWBIE);
		}
		if (jsonObject.has(IS_ELITE)) {
			bean.is_elite = getInt(jsonObject, IS_ELITE);
		}
		if (jsonObject.has(IS_FAV)) {
			bean.is_fav = getInt(jsonObject, IS_FAV);
		}
		if (jsonObject.has(HAS_PIC)) {
			bean.has_pic = getInt(jsonObject, HAS_PIC);
		}
		if (jsonObject.has(LAST_RESPONSE_TS)) {
			bean.last_response_ts = getLong(jsonObject, LAST_RESPONSE_TS);
			if (bean.last_response_ts == 0 || bean.response_count == 0) {
				bean.last_response_ts = getLong(jsonObject, CREATE_TS);
			}
		}
		if (jsonObject.has(LAST_RESPONSE_USER_ID)) {
			bean.last_response_user_id = jsonObject.getString(
					LAST_RESPONSE_USER_ID).trim();
		}
		if (jsonObject.has(LAST_RESPONSE_USER_NAME)) {
			bean.last_response_user_name = jsonObject.getString(
					LAST_RESPONSE_USER_NAME).trim();
		}
		return bean;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Discuz) {
			Discuz d = (Discuz) o;
			if (d.discuz_id == discuz_id) {
				return true;
			}
		}
		return false;
	}
}
