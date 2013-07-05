package com.babytree.apps.comm.test;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.babytree.apps.comm.config.UrlConstrants;
import com.babytree.apps.comm.ctr.BaseController;
import com.babytree.apps.comm.net.BabytreeHttp;
import com.babytree.apps.comm.util.DataResult;

/**
 * 测试联网
 * 
 * @author wangbingqi
 * 
 */
public class BabytreeTestContrllor extends BaseController {

	/**
	 * 取随机数地址
	 */
	public static final String GET_PRODUCT_PROPORTION = UrlConstrants.HOST_URL
			+ "/api/mobile_statistics/get_product_proportion";

	/**
	 * 取随机数
	 * 
	 * @return
	 */
	public static DataResult getProductProportion() {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(GET_PRODUCT_PROPORTION, params);
			// 整体的json
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has("status")) {
				String status = jsonObject.getString("status");
				int st = 0;
				if (status.equalsIgnoreCase("success")) {
					st = 0;
				} else {
					st = 1;
				}
				result.status = st;
				if (st == 0) {
					if (jsonObject.has("data")) {
						// data的json
						JSONObject dataJson = jsonObject.getJSONObject("data");
						JSONArray dataArray = dataJson.getJSONArray("product_list");
						if (dataArray != null && dataArray.length() != 0) {
							result.data = dataArray;
						} else {
							result.data = null;
						}
					}
				} else {
					result.message = status;
					result.data = 0;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			return result;
		}
		return result;
	}
}
