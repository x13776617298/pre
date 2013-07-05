package com.babytree.apps.comm.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 抽奖响应数据对象
 * @author Administrator
 *
 */
public class Prize extends Base {
	private static final long serialVersionUID = 1L;

	/**
	 * 中奖名称
	 */
	public String prizename="";
	/**
	 * 中奖图片
	 */
	public String prizeimage="";
	/**
	 * 剩余抽奖次数
	 */
	public String prizecount="";
	/**
	 * 抽奖类型0代表孕期值，1代表实物
	 */
	public String prizetype="";
	
	public static Prize parse(JSONObject jsonObject) throws JSONException {
		Prize bean = new Prize();
		if (jsonObject.has("prize_name")) {
			bean.prizename = jsonObject.getString("prize_name").trim();
		}
		if (jsonObject.has("prize_image")) {
			bean.prizeimage = jsonObject.getString("prize_image").trim();
		}
		if (jsonObject.has("prize_count")) {
			bean.prizecount = jsonObject.getString("prize_count").trim();
		}
		if (jsonObject.has("prize_type")) {
			bean.prizetype = jsonObject.getString("prize_type").trim();
		}
		return bean;
	}
}
