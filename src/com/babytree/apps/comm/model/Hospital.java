package com.babytree.apps.comm.model;

import org.json.JSONException;
import org.json.JSONObject;


public class Hospital extends Base {

	private static final long serialVersionUID = -200605627528470366L;

	public String id;
	public String location_id;
	public String name;
	public String address;
	public String province;
	public String city = "";
	public String tel;
	public String type;
	public String type2;
	public String x;
	public String y;
	public String route;
	public String description;
	public String fuchanke_description;
	public String has_group;
	public String group_id;
	public String strategy_id;
	public String status;
	public String create_ts;
	public String update_ts;
	public String topic_count;
	public String user_count;
	public Discuz1 discus ;

	private static final String ID = "id";
	private static final String LOCATION_ID = "location_id";
	private static final String NAME = "name";
	private static final String ADDRESS = "address";
	private static final String PROVINCE = "province";
	private static final String CITY = "city";
	private static final String TEL = "tel";
	private static final String TYPE = "type";
	private static final String TYPE2 = "type2";
	private static final String X = "x";
	private static final String Y = "y";
	private static final String ROUTE = "route";
	private static final String DESCRIPTION = "description";
	private static final String FUCHANKE_DESCRIPTION = "fuchanke_description";
	private static final String HAS_GROUP = "has_group";
	private static final String GROUP_ID = "group_id";
	private static final String STRATEGY_ID = "strategy_id";
	private static final String STATUS = "status";
	private static final String CREATE_TS = "create_ts";
	private static final String UPDATE_TS = "update_ts";
    private static final String TOPIC_COUT = "topic_count";
    private static final String USER_COUNT = "user_count";
    private static final String TOPIC_DATA = "topic_data";
	public static Hospital parse(JSONObject jsonObject) throws JSONException {
		Hospital bean = new Hospital();

		if (jsonObject.has(ID)) {
			bean.id = jsonObject.getString(ID);
		}
		if (jsonObject.has(LOCATION_ID)) {
			bean.location_id = jsonObject.getString(LOCATION_ID);
		}
		if (jsonObject.has(NAME)) {
			bean.name = jsonObject.getString(NAME);
		}
		if (jsonObject.has(ADDRESS)) {
			bean.address = jsonObject.getString(ADDRESS);
		}
		if (jsonObject.has(PROVINCE)) {
			bean.province = jsonObject.getString(PROVINCE);
		}
		if (jsonObject.has(CITY)) {
			bean.city = jsonObject.getString(CITY);
		}
		if (jsonObject.has(TEL)) {
			bean.tel = jsonObject.getString(TEL);
		}
		if (jsonObject.has(TYPE)) {
			bean.type = jsonObject.getString(TYPE);
		}
		if (jsonObject.has(TYPE2)) {
			bean.type2 = jsonObject.getString(TYPE2);
		}
		if (jsonObject.has(X)) {
			bean.x = jsonObject.getString(X);
		}
		if (jsonObject.has(Y)) {
			bean.y = jsonObject.getString(Y);
		}
		if (jsonObject.has(ROUTE)) {
			bean.route = jsonObject.getString(ROUTE);
		}
		if (jsonObject.has(DESCRIPTION)) {
			bean.description = jsonObject.getString(DESCRIPTION);
		}
		if (jsonObject.has(FUCHANKE_DESCRIPTION)) {
			bean.fuchanke_description = jsonObject.getString(FUCHANKE_DESCRIPTION);
		}
		if (jsonObject.has(HAS_GROUP)) {
			bean.has_group = jsonObject.getString(HAS_GROUP);
		}
		if (jsonObject.has(GROUP_ID)) {
			bean.group_id = jsonObject.getString(GROUP_ID);
		}
		if (jsonObject.has(STRATEGY_ID)) {
			bean.strategy_id = jsonObject.getString(STRATEGY_ID);
		}
		if (jsonObject.has(STATUS)) {
			bean.status = jsonObject.getString(STATUS);
		}
		if (jsonObject.has(CREATE_TS)) {
			bean.create_ts = jsonObject.getString(CREATE_TS);
		}
		if (jsonObject.has(UPDATE_TS)) {
			bean.update_ts = jsonObject.getString(UPDATE_TS);
		}
		if (jsonObject.has(TOPIC_COUT)) {
			bean.topic_count = jsonObject.getString(TOPIC_COUT);
		}
		if (jsonObject.has(USER_COUNT)) {
			bean.user_count = jsonObject.getString(USER_COUNT);
		}
        if(jsonObject.has(TOPIC_DATA)){
        	bean.discus = Discuz1.parse(jsonObject.getJSONObject(TOPIC_DATA));
        }
		return bean;
	}
}
