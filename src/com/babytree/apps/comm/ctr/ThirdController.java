package com.babytree.apps.comm.ctr;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.babytree.apps.biz.user.model.User;
import com.babytree.apps.comm.config.UrlConstrants;
import com.babytree.apps.comm.net.BabytreeHttp;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;

/**
 * 关于第三方账号登录，新增，绑定等功能的网络请求类
 * 
 * @author ybq
 * 
 */
public class ThirdController extends BaseController {
	// =============================================================
	private static final String SUCCESS_STATUS = "success";
	// =============================================================

	/**
	 * 第三方登录接口
	 * 
	 * @param token
	 * @param type
	 *            1为新浪 2为腾讯
	 * @return
	 */
	public static DataResult thirdPartLogin(String uid, String openId,
			String token, String type) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uid", uid));
		params.add(new BasicNameValuePair("open_id", openId));
		params.add(new BasicNameValuePair(TOKEN, token));
		params.add(new BasicNameValuePair(TYPE, type));

		String jsonString = null;
		try {

			jsonString = BabytreeHttp.post(UrlConstrants.THIRD_PART_LOGIN,
					params);
			JSONObject jsonObject = new JSONObject(jsonString);
			String status = null;
			if (jsonObject.has(STATUS)) {
				status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					JSONObject obj = jsonObject.getJSONObject("data");
					User data = User.parse(obj);
					result.data = data;
				}
			}
			result.message = BabytreeUtil.getMessage(status);
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	/**
	 * 
	 * @param openId
	 *            字符串（第三方返回的uid）
	 * @param token
	 *            字符串（第三方返回的token）
	 * @param type
	 *            数值（类型:新浪,1;腾讯,2）
	 * @param nickName
	 *            字符串（昵称）
	 * @param email
	 *            字符串（邮箱）
	 * @param babybirthday
	 *            字符串（宝宝生日）
	 * @return
	 */
	public static DataResult newUserThirdBD(String openId, String token,
			String type, String nickName, String email, String babybirthday) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("open_id", openId));
		params.add(new BasicNameValuePair(TOKEN, token));
		params.add(new BasicNameValuePair(TYPE, type));
		params.add(new BasicNameValuePair(NICKNAME, nickName));
		params.add(new BasicNameValuePair(EMAIL, email));
		if (babybirthday != null)
			params.add(new BasicNameValuePair(BABY_BIRTHDAY, babybirthday));

		String jsonString = null;
		try {
			jsonString = BabytreeHttp.post(UrlConstrants.NEW_USER_THIRD_BD,
					params);
			JSONObject jsonObject = new JSONObject(jsonString);
			String status = null;
			if (jsonObject.has(STATUS)) {
				status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					JSONObject obj = jsonObject.getJSONObject("data");
					User data = User.parse(obj);
					result.data = data;
				}
			}
			result.message = BabytreeUtil.getMessage(status);
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	/**
	 * 
	 * @param openId
	 *            第三方授权后返回的uid
	 * @param token
	 *            第三方授权后返回的token
	 * @param type
	 *            新浪微博 1 腾讯微博2
	 * @param password
	 *            密码
	 * @param email
	 *            邮箱
	 * @return
	 */
	public static DataResult oldUserThirdBD(String openId, String token,
			String type, String password, String email) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("open_id", openId));
		params.add(new BasicNameValuePair(TOKEN, token));
		params.add(new BasicNameValuePair(TYPE, type));
		params.add(new BasicNameValuePair(PASSWORD, password));
		params.add(new BasicNameValuePair(EMAIL, email));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.post(UrlConstrants.OLD_USER_THIRD_BD,
					params);
			JSONObject jsonObject = new JSONObject(jsonString);
			String status = null;
			if (jsonObject.has(STATUS)) {
				status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					JSONObject obj = jsonObject.getJSONObject("data");
					User data = User.parse(obj);
					result.data = data;
				}
			}
			result.message = BabytreeUtil.getMessage(status);
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}


	// 获取腾讯微薄帐号
	public static String getTencentUsername(String access_token,
			String oauth_consumer_key, String openid) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("access_token", access_token));
		params.add(new BasicNameValuePair("oauth_consumer_key",
				oauth_consumer_key));
		params.add(new BasicNameValuePair("openid", openid));
		params.add(new BasicNameValuePair("format", "json"));
		String ret = null;
		try {
			String jsonString = BabytreeHttp.postHttps(
					"https://graph.qq.com/user/get_info", params);
			JSONObject jsonObject = new JSONObject(jsonString);
			int errcode = jsonObject.getInt("errcode");
			if (errcode == 0) {
				JSONObject data = jsonObject.getJSONObject("data");
				ret = data.getString("name");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;

	}

	// 分享到腾讯微薄帐号
	public static String sharToTencent(String access_token,
			String oauth_consumer_key, String openid, String content) {
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("access_token", access_token));
		params.add(new BasicNameValuePair("oauth_consumer_key",
				oauth_consumer_key));
		params.add(new BasicNameValuePair("openid", openid));
		params.add(new BasicNameValuePair("format", "json"));
		params.add(new BasicNameValuePair("content", content));
		String ret = null;
		try {
			String jsonString = BabytreeHttp.postHttps(
					"https://graph.qq.com/t/add_t", params);
			JSONObject jsonObject = new JSONObject(jsonString);
			int errcode = jsonObject.getInt("ret");
			if (errcode == 0) {
				JSONObject data = jsonObject.getJSONObject("data");
				ret = data.getString("id");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;

	}

}
