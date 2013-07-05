package com.babytree.apps.biz.push.ctr;

import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.babytree.apps.biz.push.model.PushMessage;
import com.babytree.apps.comm.ctr.BaseController;
import com.babytree.apps.comm.net.BabytreeHttp;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;

public class PushController extends BaseController {

	private static final String MESSAGE_NET_URL = "http://msg.babytree.com/message/";

	/**
	 * 获取推送消息
	 * 
	 * @author wangshuaibo
	 * @param mkey
	 * @return
	 */
	public static DataResult getMessage(String mkey) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("mkey", mkey));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(MESSAGE_NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has("succ")) {
				int status = jsonObject.getInt("succ");
				if (status == 0) {
					result.status = 0;
					if (jsonObject.has("data")) {
						ArrayList<PushMessage> data = new ArrayList<PushMessage>();
						JSONArray array = jsonObject.getJSONArray("data");
						int serial_number = 0;
						for (int i = 0; i < array.length(); i++) {
							PushMessage item = PushMessage.parse(array.getJSONObject(i));
							if (item.serial_number > serial_number) {
								serial_number = item.serial_number;
							}

							data.add(item);
						}
						result.totalSize = serial_number;
						result.data = data;
					} else if (jsonObject.has("list")) {
						ArrayList<PushMessage> data = new ArrayList<PushMessage>();

						JSONArray array = jsonObject.getJSONArray("list");
						int serial_number = 0;
						for (int i = 0; i < array.length(); i++) {
							PushMessage item = PushMessage.parse(array.getJSONObject(i));
							if (item.serial_number > serial_number) {
								serial_number = item.serial_number;
							}
							data.add(item);
						}
						result.totalSize = serial_number;
						result.data = data;
					}

				}

			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}
}
