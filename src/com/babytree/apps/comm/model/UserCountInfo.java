package com.babytree.apps.comm.model;

import org.json.JSONException;
import org.json.JSONObject;

public class UserCountInfo {

	public String hospital_prenant_count = "";

	public String is_show = "";

	private static final String HOSPITAL_PRENANT_COUNT = "hospital_prenant_count";

	private static final String IS_SHOW = "is_show";

	public static UserCountInfo parse(JSONObject jsonObject) throws JSONException {
		UserCountInfo bean = new UserCountInfo();
		if (jsonObject.has(HOSPITAL_PRENANT_COUNT)) {
			bean.hospital_prenant_count = jsonObject.getString(HOSPITAL_PRENANT_COUNT);
		}
		if (jsonObject.has(IS_SHOW)) {
			bean.is_show = jsonObject.getString(IS_SHOW);
		}
		return bean;
	}
}
