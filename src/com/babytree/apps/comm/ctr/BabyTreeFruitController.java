package com.babytree.apps.comm.ctr;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;


import com.babytree.apps.comm.config.UrlConstrants;
import com.babytree.apps.comm.model.Total;
import com.babytree.apps.comm.net.BabytreeHttp;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;

/**
 * 水果API
 * 
 * @author wangbingqi
 * 
 */
public class BabyTreeFruitController extends BaseController {
	/**
	 * 水果
	 * 
	 * @param loginStr
	 * @return
	 */
	public static DataResult getFruit(String loginStr) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		if (loginStr != null && !loginStr.equals("")) {
			params.add(new BasicNameValuePair("login_string", loginStr));
		}
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.post(UrlConstrants.GET_FRUIT, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString("status");
				Total total = new Total();
				if (status.equalsIgnoreCase("success")) {
					JSONObject jsobj = jsonObject.getJSONObject("data");
					if (jsobj != null) {
						if (jsobj.has("fruit_total")) {
							total.fruit_total = jsobj.getString("fruit_total");
						}
						if (jsobj.has("msg")) {
							total.msg = jsobj.getString("msg");
						}
						result.data = total;
					}
					result.status = 0;
				} else {
					result.status = 1;
					JSONObject jsobj = jsonObject.getJSONObject("data");
					if (jsobj != null) {
						if (jsobj.has("msg")) {
							total.msg = jsobj.getString("msg");
							result.message = total.msg;
						}
					} else {
						result.message = "出错";
					}
				}

			}
		}  catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}
		return result;

	}
}
