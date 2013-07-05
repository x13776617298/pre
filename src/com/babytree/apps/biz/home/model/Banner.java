package com.babytree.apps.biz.home.model;

import org.json.JSONException;
import org.json.JSONObject;

import com.babytree.apps.comm.model.Base;

public class Banner extends Base {
	private static final long serialVersionUID = 1L;
	public String id;
	public int banner;
	public String background;
	public String title;
	public int selectType;
	public int topicId;
	public String imgUrl;
	public int start;
	public int end;
	public int updateTs;
	public int createTs;
	public String url;
	private static final String ID = "id";
	private static final String BANNER = "banner";
	private static final String BACKGROUND = "background";
	private static final String TITLE = "title";
	private static final String SELECT_TYPE = "select_type";
	private static final String TOPIC_ID = "topic_id";
	private static final String IMG_URL = "img_url";
	private static final String START = "start";
	private static final String END = "end";
	private static final String UPDATE_TS = "update_ts";
	private static final String CREATE_TS = "create_ts";
	private static final String URL = "url";

	public static Banner parse(JSONObject object) throws JSONException {
		Banner bean = new Banner();
		if (object.has(ID)) {
			bean.id = object.getString(ID);
		}
		if (object.has(BANNER)) {
			bean.banner = object.getInt(BANNER);
		}
		if (object.has(BACKGROUND)) {
			bean.background = object.getString(BACKGROUND);
		}
		if (object.has(TITLE)) {
			bean.title = object.getString(TITLE);
		}
		if (object.has(SELECT_TYPE)) {
			bean.selectType = object.getInt(SELECT_TYPE);
		}
		if (object.has(TOPIC_ID)) {
			bean.topicId = object.getInt(TOPIC_ID);
		}
		if (object.has(IMG_URL)) {
			bean.imgUrl = object.getString(IMG_URL);
		}
		if (object.has(START)) {
			bean.start = object.getInt(START);
		}
		if (object.has(END)) {
			bean.end = object.getInt(END);
		}
		if (object.has(UPDATE_TS)) {
			bean.updateTs = object.getInt(UPDATE_TS);
		}
		if (object.has(CREATE_TS)) {
			bean.createTs = object.getInt(CREATE_TS);
		}
		if (object.has(URL)) {
			bean.url = object.getString(URL);
		}
		return bean;
	}
}
