package com.babytree.apps.biz.notice.ctr;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.text.TextUtils;

import com.babytree.apps.biz.notice.model.UserMessageListBean;
import com.babytree.apps.comm.config.UrlConstrants;
import com.babytree.apps.comm.ctr.BaseController;
import com.babytree.apps.comm.net.BabytreeHttp;
import com.babytree.apps.comm.tools.JsonParserTolls;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;

public class NoticeController extends BaseController {

	/**
	 * 删除和某用户的全部站短URL
	 */
	public static final String DEL_ALL_USER_MESSAGE = UrlConstrants.HOST_URL
			+ "/api/session_message/del_all_user_message";

	/**
	 * 用户消息列表URL
	 */
	public static final String USER_LIST = UrlConstrants.HOST_URL + "/api/session_message/user_list";

	/**
	 * 删除和某用户的全部站短
	 * http://www.babytree.com/api/session_message/del_all_user_message
	 * 
	 * @author wangshuaibo
	 * @param loginString
	 *            用户的授权token
	 * @param userEncodeId
	 *            我和谁的站短
	 * @return
	 */
	public static DataResult delAllUserMessage(String loginString, String userEncodeId) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		params.add(new BasicNameValuePair(USER_ENCODE_ID, userEncodeId));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(DEL_ALL_USER_MESSAGE, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (SUCCESS_STATUS.equals(status)) {
					result.status = SUCCESS_CODE;
				} else {
					result.message = BabytreeUtil.getMessage(status);
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;

	}

	/**
	 * 用户消息列表
	 * 
	 * @param loginStr
	 * @return
	 */
	public static DataResult toNotice(String loginString, int start, int limit) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		List<UserMessageListBean> list = new ArrayList<UserMessageListBean>();
		if (!TextUtils.isEmpty(loginString)) {
			params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		}
		params.add(new BasicNameValuePair(START, String.valueOf(start)));
		params.add(new BasicNameValuePair(LIMIT, String.valueOf(limit)));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(USER_LIST, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			String status = JsonParserTolls.getStr(jsonObject, STATUS);
			if (status.equalsIgnoreCase(SUCCESS_STATUS)) {
				result.status = SUCCESS_CODE;
				JSONObject jsobj = JsonParserTolls.getJsonObj(jsonObject, DATA);
				JSONArray array = JsonParserTolls.getJsonArray(jsobj, LIST);
				if (array != null) {
					for (int i = 0; i < array.length(); i++) {
						JSONObject itemJson = array.getJSONObject(i);
						UserMessageListBean bean = new UserMessageListBean();
						bean.user_encode_id = JsonParserTolls.getStr(itemJson, "user_encode_id");
						bean.nickname = JsonParserTolls.getStr(itemJson, "nickname");
						bean.last_ts = JsonParserTolls.getLong(itemJson, "last_ts", 0);
						bean.content = JsonParserTolls.getStr(itemJson, "content");
						bean.user_avatar = JsonParserTolls.getStr(itemJson, "user_avatar");
						bean.unread_count = JsonParserTolls.getInt(itemJson, "unread_count", 0);
						list.add(bean);
					}
				}
				result.data = list;
				result.totalSize = JsonParserTolls.getInt(jsobj, "total_size", Integer.MAX_VALUE);
			} else {
				result.message = BabytreeUtil.getMessage(status);
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}
		return result;

	}
}
