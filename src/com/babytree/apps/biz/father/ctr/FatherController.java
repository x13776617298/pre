package com.babytree.apps.biz.father.ctr;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.ConnectException;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.babytree.apps.biz.father.tools.JsonParserForFather;
import com.babytree.apps.comm.config.UrlConstrants;
import com.babytree.apps.comm.ctr.BaseController;
import com.babytree.apps.comm.exception.ServerException;
import com.babytree.apps.comm.net.BabytreeHttp;
import com.babytree.apps.comm.tools.JsonParserTolls;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;

public class FatherController extends BaseController {

	private static String URL = UrlConstrants.HOST_URL;
	/**
	 * 获取绑定状态
	 */
	public static final String BIND_STATUS = URL + "/api/mobile_father/bind_status";
	/**
	 * 解除绑定 (这里有一个需要注意的地方，爸爸解除绑定以后需要登出用户)
	 */
	public static final String UNBIND = URL + "/api/mobile_father/unbind";
	/**
	 * 绑定用户
	 */
	public static final String BIND = URL + "/api/mobile_father/bind";
	/**
	 * 未读关爱提醒（爸爸和妈妈的私信）
	 */
	public static final String UNREAD_MESSAGE_COUNT = URL + "/api/mobile_father/unread_message_count";
	/**
	 * 取任务
	 */
	public static final String GET_TASK = URL + "/api/mobile_task/get_task";
	/**
	 * 完成任务
	 */
	public static final String DONE_TASK = URL + "/api/mobile_task/done_task";

	/**
	 * 获取绑定状态
	 * 
	 * @param login_string
	 * @return
	 */
	public static DataResult getBindStatus(String login_string, String gender) {

		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("login_string", login_string));
		params.add(new BasicNameValuePair("gender", gender));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(BIND_STATUS, params);
			// 整体的json
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has("status")) {
				String status = jsonObject.getString("status");
				if ("success".equalsIgnoreCase(status)) {
					result.status = 0;
					if (jsonObject.has("data")) {
						// data的json
						JSONObject dataJson = JsonParserTolls.getJsonObj(jsonObject, "data");
						result.data = JsonParserForFather.getBindStatus(dataJson);
					}
				} else {
					result.message = status;
					result.data = 0;
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}
		return result;

	}

	/**
	 * 绑定用户
	 * 
	 * @param code
	 * @return
	 */
	public static String bind(String code) {

		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("code", code));

		try {
			return BabytreeHttp.get(BIND, params);
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static DataResult getParseBindBoth(String code) {
		String jsonStr = bind(code);
		return parseBindBoth(jsonStr);
	}

	/**
	 * 解析綁定用戶
	 * @param jsonStr
	 * @return
	 */
	public static DataResult parseBindBoth(String jsonStr) {
		DataResult result = new DataResult();
		try {
			// 整体的json
			JSONObject jsonObject = new JSONObject(jsonStr);
			if (jsonObject.has("status")) {
				String status = jsonObject.getString("status");
				if (status.equalsIgnoreCase("success")) {
					result.status = 0;
					if (jsonObject.has("data")) {
						// data的json
						JSONObject dataJson = JsonParserTolls.getJsonObj(jsonObject, "data");
						result.data = JsonParserForFather.bind(dataJson);
					}
				} else {
					result.error = status;
					result.data = 0;
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, null, "");
		}
		return result;
	}

	/**
	 * 解除绑定
	 * 
	 * @param login_string
	 * 
	 * @return
	 */
	public static DataResult unbind(String login_string, String gender) {

		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("login_string", login_string));
		params.add(new BasicNameValuePair("gender", gender));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UNBIND, params);
			// 整体的json
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has("status")) {
				String status = jsonObject.getString("status");
				if (status.equalsIgnoreCase("success")) {
					result.status = SUCCESS_CODE;
					if (jsonObject.has("data")) {
						// data的json
						JSONObject dataJson = jsonObject.getJSONObject("data");
						result.data = JsonParserForFather.unBind(dataJson);
					}
				} else {
					result.message = BabytreeUtil.getMessage(status);
					result.error = status;
					result.data = 0;
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}
		return result;

	}

	/**
	 * 未读消息 妈妈
	 * 
	 * @param login_string
	 * @return
	 */
	public static DataResult getUnreadMsgCount(String login_string, String gender) {

		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("login_string", login_string));
		params.add(new BasicNameValuePair("gender", gender));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UNREAD_MESSAGE_COUNT, params);
			// 整体的json
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has("status")) {
				String status = jsonObject.getString("status");
				if ("success".equalsIgnoreCase(status)) {
					result.status = 0;
					if (jsonObject.has("data")) {
						// data的json
						JSONObject dataJson = JsonParserTolls.getJsonObj(jsonObject, "data");
						result.data = JsonParserForFather.getUnReadMsgCount(dataJson);
					}
				} else {
					result.message = status;
					result.data = 0;
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}
		return result;

	}

	/**
	 * 取任务列表
	 * 
	 * @param code
	 * @return
	 */
	public static String getTasks(String login_string) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("login_string", login_string));
		try {
			return BabytreeHttp.get(GET_TASK, params);
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (ServerException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 解析任务列表
	 * 
	 * @param jsonString
	 * @return
	 */
	public static DataResult parseTasks(String jsonString) {
		DataResult result = new DataResult();
		try {
			// 整体的json
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has("status")) {
				String status = jsonObject.getString("status");
				if ("success".equalsIgnoreCase(status)) {
					result.status = 0;
					if (jsonObject.has("data")) {
						// data的json
						JSONObject dataJson = JsonParserTolls.getJsonObj(jsonObject, "data");
						result.data = JsonParserForFather.getTasks(dataJson);
					}
				} else {
					result.message = status;
					result.data = 0;
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, null, "");
		}
		return result;

	}

	/**
	 * 完成任务
	 * 
	 * @param code
	 * @return
	 */
	public static DataResult doneTask(String login_string, String task_id) {

		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("login_string", login_string));
		params.add(new BasicNameValuePair("task_id", task_id));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(DONE_TASK, params);
			// 整体的json
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has("status")) {
				String status = jsonObject.getString("status");
				if ("success".equalsIgnoreCase(status)) {
					result.status = 0;
					if (jsonObject.has("data")) {
						// data的json
						JSONObject dataJson = JsonParserTolls.getJsonObj(jsonObject, "data");
						result.data = JsonParserForFather.doneTask(dataJson);
					}
				} else {
					result.message = status;
					result.data = 0;
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}
		return result;

	}
}
