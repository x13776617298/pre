package com.babytree.apps.comm.model;

import org.json.JSONException;
import org.json.JSONObject;

public class UserAddInfo extends Base {
	private static final long serialVersionUID = -200605627528470366L;

	public String pre_value = "";

	public String invitation_code = "";

	public String invite_value = "";

	public String lottery_value = "";

	public String sign_value = "";

	public String sign_days = "";

	public String tree_image = "";

	public String is_sign = "0";

	/**
	 * 爸爸给加的孕气
	 */
	public int baba_yunqi = 0;

	private static final String PRE_VALUE = "pre_value";

	private static final String INVITATION_CODE = "invitation_code";

	private static final String INVITE_VALUE = "invite_value";

	private static final String LOTTERY_VALUE = "lottery_value";

	private static final String SIGN_VALUE = "sign_value";

	private static final String SIGN_DAYS = "sign_days";

	private static final String TREE_IMAGE = "tree_image";

	private static final String IS_SIGN = "is_sign";

	private static final String BABA_YUNQI = "baba_yunqi";

	public static UserAddInfo parse(JSONObject jsonObject) throws JSONException {
		UserAddInfo bean = new UserAddInfo();
		if (jsonObject.has(PRE_VALUE)) {
			bean.pre_value = jsonObject.getString(PRE_VALUE).trim();
		}
		if (jsonObject.has(INVITATION_CODE)) {
			bean.invitation_code = jsonObject.getString(INVITATION_CODE).trim();
		}
		if (jsonObject.has(INVITE_VALUE)) {
			bean.invite_value = jsonObject.getString(INVITE_VALUE).trim();
		}
		if (jsonObject.has(LOTTERY_VALUE)) {
			bean.lottery_value = jsonObject.getString(LOTTERY_VALUE).trim();
		}
		if (jsonObject.has(SIGN_VALUE)) {
			bean.sign_value = jsonObject.getString(SIGN_VALUE).trim();
		}
		if (jsonObject.has(SIGN_DAYS)) {
			bean.sign_days = jsonObject.getString(SIGN_DAYS).trim();
		}
		if (jsonObject.has(TREE_IMAGE)) {
			bean.tree_image = jsonObject.getString(TREE_IMAGE).trim();
		}
		if (jsonObject.has(IS_SIGN)) {
			bean.is_sign = jsonObject.getString(IS_SIGN).trim();
		}
		// 爸爸添加孕气解析
		if (jsonObject.has(BABA_YUNQI)) {
			bean.baba_yunqi = getInt(jsonObject, BABA_YUNQI);
		}

		return bean;
	}
}