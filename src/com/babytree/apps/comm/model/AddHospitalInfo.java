package com.babytree.apps.comm.model;

import org.json.JSONException;
import org.json.JSONObject;

public class AddHospitalInfo {
	public String hospital_id;
	public String hospital_name;
	public String baby_birthday_ts;
	public String group_id;
	
	private static final String HOSPITAL_ID = "hospital_id";
	private static final String HOSPITAL_NAME = "hospital_name";
	private static final String BABY_BIRTHDAY_TS = "baby_birthday_ts";
	private static final String GROUP_ID = "group_id";
	
	public static AddHospitalInfo parse(JSONObject jsonObject) throws JSONException {
		AddHospitalInfo info = new AddHospitalInfo();
		if (jsonObject.has(HOSPITAL_ID)) {
			info.hospital_id = jsonObject.getString(HOSPITAL_ID);
		}
		if (jsonObject.has(HOSPITAL_NAME)) {
			info.hospital_name = jsonObject.getString(HOSPITAL_NAME);
		}
		if (jsonObject.has(BABY_BIRTHDAY_TS)) {
			info.baby_birthday_ts = jsonObject.getString(BABY_BIRTHDAY_TS);
		}
		if (jsonObject.has(GROUP_ID)) {
			info.group_id = jsonObject.getString(GROUP_ID);
		}
		return info;
	}
}
