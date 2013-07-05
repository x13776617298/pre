package com.babytree.apps.biz.push.model;

import org.json.JSONException;
import org.json.JSONObject;

public class PushMessage {
	public int serial_number;
	public int t; // 推送类型
	public String alert; // 推送的消息
	public int badge; // 推送的消息条数，一般默认是1
	public String expired_ts; // 过期时间
	public int id; // 帖子id

	public int ar; // 作者回复数量
	public int tr; // 总共回复数量
	public boolean c; // 用户是否收藏
	public int p; // 别人回复的楼层在第几页
	public String u; // 网页url完整地址
	public int loc_id; // 推送的城市限制 .如果没有限制,值为0
	public int prov_id; // 省id //默认为0
	public int week_type; // 区分育儿和孕期: 1为孕期 2 为育儿
	public int min_week; // 最小周 week_type==1 时表示孩子年纪最小周 ,week_type==2 表示孕期最小值
	public int max_week; // 最大周 week_type==1 时表示孩子年纪最大周 ,week_type==2 表示孕期最大值
	public int yunqi; // 爸爸给妈妈加的孕气值
	public int total_yunqi; // 加孕气后妈妈的总孕气值

	private static final String T = "t";
	private static final String SERIAL_NUMBER = "serial_number";
	private static final String ALERT = "alert";
	private static final String BADGE = "badge";
	private static final String EXPIRED_TS = "expired_ts";
	private static final String ID = "id";
	private static final String AR = "ar";
	private static final String TR = "tr";
	private static final String C = "c";
	private static final String P = "p";
	private static final String U = "u";
	private static final String LOC_ID = "loc_id";
	private static final String PROV_ID = "prov_id";
	private static final String WEEK_TYPE = "week_type";
	private static final String MIN_WEEK = "min_week";
	private static final String MAX_WEEK = "max_week";
	private static final String YUNQI = "yunqi";
	private static final String TOTAL_YUNQI = "total_yunqi";

	public static PushMessage parse(JSONObject object) throws JSONException {
		PushMessage bean = new PushMessage();
		if (object.has(T)) {
			bean.t = object.getInt(T);
		}
		if (object.has(SERIAL_NUMBER)) {
			bean.serial_number = object.getInt(SERIAL_NUMBER);
		}
		if (object.has(ALERT)) {
			bean.alert = object.getString(ALERT);
		}
		if (object.has(BADGE)) {
			bean.badge = object.getInt(BADGE);
		}
		if (object.has(EXPIRED_TS)) {
			bean.expired_ts = object.getString(EXPIRED_TS);
		}
		if (object.has(C)) {
			bean.c = object.getBoolean(C);
		}
		if (object.has(P)) {
			bean.p = object.getInt(P);
		}
		if (object.has(U)) {
			bean.u = object.getString(U);
		}
		if (object.has(ID)) {
			bean.id = object.getInt(ID);
		}
		if (object.has(AR)) {
			bean.ar = object.getInt(AR);
		}
		if (object.has(TR)) {
			bean.tr = object.getInt(TR);
		}
		if (object.has(LOC_ID)) {
			bean.loc_id = object.getInt(LOC_ID);
		}
		if (object.has(PROV_ID)) {
			bean.prov_id = object.getInt(PROV_ID);
		}
		if (object.has(WEEK_TYPE)) {
			bean.week_type = object.getInt(WEEK_TYPE);
		}
		if (object.has(MIN_WEEK)) {
			bean.min_week = object.getInt(MIN_WEEK);
		}
		if (object.has(MAX_WEEK)) {
			bean.max_week = object.getInt(MAX_WEEK);
		}
		if (object.has(YUNQI)) {
			bean.yunqi = object.getInt(YUNQI);
		}
		if (object.has(TOTAL_YUNQI)) {
			bean.total_yunqi = object.getInt(TOTAL_YUNQI);
		}
		return bean;
	}

}
