package com.babytree.apps.biz.home.ctr;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.babytree.apps.biz.home.model.Banner;
import com.babytree.apps.biz.home.model.Notify;
import com.babytree.apps.biz.topic.tools.JsonParserForTopic;
import com.babytree.apps.comm.config.InterfaceConstants;
import com.babytree.apps.comm.config.UrlConstrants;
import com.babytree.apps.comm.ctr.BaseController;
import com.babytree.apps.comm.model.TopicComment;
import com.babytree.apps.comm.model.TopicReply;
import com.babytree.apps.comm.net.BabytreeHttp;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;

public class HomeController extends BaseController {

	public static final String LOADICONURL = UrlConstrants.UPLOAD_PHOTO + "/muser/modify_avatar";
	/**
	 * 获取大图地址
	 */
	public static final String HOME_PICTURE_URL = UrlConstrants.HOST_URL + "/api/mobile_image/get_image";

	/**
	 * 获取首页推荐列表地址
	 */
	public static final String RECOMMENDS_URL = UrlConstrants.HOST_URL + "/api/mobile_recommend/get_recommend_topic";

	/**
	 * 保存用户信息
	 * 
	 * @param loginString
	 * @param sexStr
	 * @param positionStr
	 * @param mBirthday
	 * @return
	 */
	public static DataResult savePersonalInfo(String loginString, String sexStr, String positionStr, String mBirthday) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.SET_USER_INFO));
		if (null != sexStr && !"".equals(sexStr)) {
			params.add(new BasicNameValuePair(GENDER, sexStr));
		}

		params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		if (null != positionStr && !"".equals(positionStr)) {
			params.add(new BasicNameValuePair(LOCATION, positionStr));
		}
		if (null != mBirthday && !"".equals(mBirthday)) {
			params.add(new BasicNameValuePair(BABY_BIRTHDAY, mBirthday));
		}
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.post(UrlConstrants.NET_URL + "?action=" + InterfaceConstants.SET_USER_INFO,
					params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (jsonObject.has(MESSAGE)) {
					result.message = jsonObject.getString(MESSAGE);
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;

	}

	/**
	 * 上传头像
	 * 
	 * @param loginString
	 * @param filepath
	 * @return
	 */
	public static DataResult postPhotoForMain(String loginString, String filepath) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.ACTION_UPLOAD_PHOTO));
		params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		params.add(new BasicNameValuePair("description", ""));
		params.add(new BasicNameValuePair("privacy", "open"));
		params.add(new BasicNameValuePair("client_type", "android"));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.postPhoto(LOADICONURL, params, new File(filepath));
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equalsIgnoreCase(SUCCESS_STATUS)) {
					result.status = 0;

					if (jsonObject.has("data")) {
						JSONObject dataJson = jsonObject.getJSONObject("data");
						if (dataJson.has("url")) {
							result.data = dataJson.getString("url");
						}
					}
				} else {
					result.status = 1;
					result.message = status;
				}

			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	/**
	 * 获取评论回复列表
	 * 
	 * @param loginString
	 * @param pageNo
	 * @return
	 */
	public static DataResult getMessageListForCommentReply(String loginString, int pageNo) {
		DataResult result = new DataResult();
		ArrayList<Notify> data = new ArrayList<Notify>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.GET_DISCUZ));
		params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		params.add(new BasicNameValuePair(PAGE, String.valueOf(pageNo)));
		params.add(new BasicNameValuePair(START, String.valueOf(((pageNo - 1) * PAGE_SIZE))));
		params.add(new BasicNameValuePair(LIMIT, String.valueOf(PAGE_SIZE)));
		params.add(new BasicNameValuePair(MESSAGE_TYPE, "user_reply_list"));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					if (jsonObject.has(TOTAL_COUNT)) {
						result.totalSize = jsonObject.getInt(TOTAL_COUNT);
					} else {
						result.totalSize = Integer.MAX_VALUE;
					}
					if (jsonObject.has(REPLY_LIST)) {
						JSONArray jsonArray = jsonObject.getJSONArray(REPLY_LIST);
						if (jsonArray.length() == 0) {
							result.totalSize = 0;
						}
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.getJSONObject(i);
							if (object.getInt("type") == 1) {
								TopicReply topicReply = TopicReply.parse(object);
								// 回复
								data.add(new Notify(1, topicReply));
							} else if (object.getInt("type") == 2) {
								TopicComment topicComment = TopicComment.parse(object);
								// 评论
								data.add(new Notify(2, topicComment));
							}
						}
						result.data = data;
					}
				}
				if (jsonObject.has(MESSAGE)) {
					result.message = jsonObject.getString(MESSAGE);
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	/**
	 * 获取首页大图图片
	 * 
	 * @param loginString
	 * @param picNo
	 * @return
	 */
	public static DataResult getPicture(String loginString, String picNo) {

		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		params.add(new BasicNameValuePair("key", picNo));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(HOME_PICTURE_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equalsIgnoreCase(SUCCESS_STATUS)) {
					result.status = 0;

					if (jsonObject.has("data")) {
						JSONObject dataJson = jsonObject.getJSONObject("data");
						if (dataJson.has("src")) {
							result.data = dataJson.getString("src");
						}
					}
				} else {
					result.status = 1;
					result.message = status;
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	/**
	 * 获取首页推荐列表
	 * 
	 * @param loginString
	 * @param picNo
	 * @return
	 */
	public static DataResult getRecommendTopics() {

		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("start", 0 + ""));
		params.add(new BasicNameValuePair("limit", 1 + ""));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(RECOMMENDS_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equalsIgnoreCase(SUCCESS_STATUS)) {
					result.status = 0;

					if (jsonObject.has("data")) {
						JSONObject dataJson = jsonObject.getJSONObject("data");
						result.data = JsonParserForTopic.getCommendTopics(dataJson);
					}
				} else {
					result.status = 1;
					result.message = status;
				}

			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	/**
	 * 请求广告
	 * 
	 * @param birthday
	 *            宝宝生日(秒数)
	 * @param appId
	 *            应用id
	 * @return
	 * @author wangshuaibo
	 */
	public static DataResult getBannerList(String birthday) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		List<Banner> list = new ArrayList<Banner>();
		params.add(new BasicNameValuePair("birthday_ts", birthday));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.post(UrlConstrants.advertising_get_banner_list, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					if (jsonObject.has("data")) {
						JSONArray array = jsonObject.getJSONArray("data");
						for (int i = 0; i < array.length(); i++) {
							JSONObject object = array.getJSONObject(i);
							Banner banner = Banner.parse(object);
							list.add(banner);
						}

					}
				}
				result.data = list;
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}
}
