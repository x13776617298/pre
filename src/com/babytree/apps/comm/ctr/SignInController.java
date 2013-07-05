package com.babytree.apps.comm.ctr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.babytree.apps.comm.config.UrlConstrants;
import com.babytree.apps.comm.model.Prize;
import com.babytree.apps.comm.model.SessionMessageListBean;
import com.babytree.apps.comm.model.UserAddInfo;
import com.babytree.apps.comm.net.BabytreeHttp;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;

public class SignInController extends BaseController {
	private static final String SUCCESS_STATUS = "success";

	// =============================================================

	public static DataResult getUserAddInfo(String longStr) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("login_string", longStr));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.USER_INFO, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					if (jsonObject.has("data")) {
						String dataStr = jsonObject.getString("data");
						JSONObject jsonObj = new JSONObject(dataStr);
						if (jsonObj.has("user_info")) {
							String userInfoStr = jsonObj.getString("user_info");
							UserAddInfo data = UserAddInfo.parse(new JSONObject(userInfoStr));
							result.data = data;
						}
					}
				}
				result.message = BabytreeUtil.getMessage(status);
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	public static DataResult signInAndAddLucky(String longStr) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("login_string", longStr));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.CHECKIN, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					if (jsonObject.has("data")) {
						String jsonStr = jsonObject.getString("data");
						JSONObject jsonObj = new JSONObject(jsonStr);
						result.data = jsonObj.getString("result");
					}
				}
				result.message = BabytreeUtil.getMessage(status);
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	/**
	 * 抽奖
	 * 
	 * @param loginStr
	 * @return
	 */
	public static DataResult toLottery(String loginStr) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		if (loginStr != null && !loginStr.equals("")) {
			params.add(new BasicNameValuePair("login_string", loginStr));
		}
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.GET_LOTTERY, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString("status");
				Prize prize = new Prize();
				if (status.equalsIgnoreCase("success")) {
					JSONObject jsobj = jsonObject.getJSONObject("data");
					if (jsobj != null) {
						if (jsobj.has("prize_name")) {
							prize.prizename = jsobj.getString("prize_name");
						}
						if (jsobj.has("prize_image")) {
							prize.prizeimage = jsobj.getString("prize_image");
						}
						if (jsobj.has("prize_count")) {
							prize.prizecount = jsobj.getString("prize_count");
						}
						if (jsobj.has("prize_type")) {
							prize.prizetype = jsobj.getString("prize_type");
						}
						result.data = prize;
					}
					result.status = 0;
				} else {
					result.status = 1;
					result.message = "出错";
				}

			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}
		return result;

	}

	/**
	 * 申领
	 * 
	 * @param loginStr
	 * @param name
	 *            名字
	 * @param mobile
	 *            手机号
	 * @param address
	 *            地址
	 * @return
	 */
	public static DataResult toApply(String loginStr, String name, String mobile, String address) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		if (loginStr != null && !loginStr.equals("")) {
			params.add(new BasicNameValuePair("login_string", loginStr));
		}
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.GET_APPLY, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString("status");
				if (status.equalsIgnoreCase("success")) {
					result.status = 0;
				} else {
					result.status = 1;
					result.message = "申领失败!";
				}

			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}
		return result;

	}

	/**
	 * 获取孕气商店列表
	 * 
	 * @return
	 */
	public static DataResult getProductList() {
		DataResult result = new DataResult();
		ArrayList<String> urlList = new ArrayList<String>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.GET_PRODUCET_LIST, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					if (jsonObject.has("data")) {
						JSONObject obj = jsonObject.getJSONObject("data");
						JSONArray array = obj.getJSONArray("list");
						for (int i = 0; i < array.length(); i++) {
							JSONObject o = array.getJSONObject(i);
							if (o.has("pic_path")) {
								String path_url = o.getString("pic_path");
								urlList.add(path_url);
							}
						}
					}
					result.data = urlList;
				} else {
					result.status = FAILED_CODE;
					result.message = "网络请求失败啦！";
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	/**
	 * 输入邀请码
	 * 
	 * @return
	 */
	public static DataResult invite(String loginStr, String invateCode) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		if (loginStr != null && !loginStr.equals("")) {
			params.add(new BasicNameValuePair("login_string", loginStr));
		}
		params.add(new BasicNameValuePair("invitation_code", invateCode));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.post(UrlConstrants.INVITE, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
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
	 * 对话列表
	 * 
	 * @param loginStr
	 * @param user_encode_id
	 *            用户ID
	 * @param start
	 * @param limit
	 * @return
	 */
	public static DataResult toSessonMessage(String loginStr, String user_encode_id, String start, String limit) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		List<SessionMessageListBean> list = new ArrayList<SessionMessageListBean>();
		if (loginStr != null && !loginStr.equals("")) {
			params.add(new BasicNameValuePair("login_string", loginStr));
			params.add(new BasicNameValuePair("user_encode_id", user_encode_id));
			params.add(new BasicNameValuePair("start", start));
			params.add(new BasicNameValuePair("limit", limit));
		}
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.SESSION_MESSAGE, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString("status");
				if (status.equalsIgnoreCase("success")) {
					JSONObject jsobj = jsonObject.getJSONObject("data");
					if (jsobj != null) {

						if (jsobj.has("list")) {
							JSONArray array = jsobj.getJSONArray("list");
							for (int i = 0; i < array.length(); i++) {
								JSONObject o = array.getJSONObject(i);
								SessionMessageListBean bean = new SessionMessageListBean();
								if (o.has("user_encode_id")) {
									bean.user_encode_id = o.getString("user_encode_id");
								}
								if (o.has("nickname")) {
									bean.nickname = o.getString("nickname");
								}
								if (o.has("last_ts")) {
									bean.last_ts = o.getString("last_ts");

									bean.last_ts = com.babytree.apps.comm.util.BabytreeUtil
											.timestempToStringMore(bean.last_ts);
								}
								if (o.has("content")) {
									bean.content = o.getString("content");
								}
								if (o.has("user_avatar")) {
									bean.user_avatar = o.getString("user_avatar");
								}
								if (o.has("message_id")) {
									bean.message_id = o.getString("message_id");
								}
								list.add(bean);
							}
							Collections.reverse(list);
							result.data = list;

						}
						if (jsobj.has("total_size")) {
							result.totalSize = jsobj.getInt("total_size");
						}
					}
					result.status = 0;
				} else {
					result.status = 1;
					result.message = "出错";
				}

			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}
		return result;

	}

}
