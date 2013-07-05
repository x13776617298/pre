package com.babytree.apps.biz.topic.ctr;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.text.TextUtils;

import com.babytree.apps.biz.topic.model.n.TopicNewBean;
import com.babytree.apps.biz.topic.tools.JsonParserForTopic;
import com.babytree.apps.comm.config.InterfaceConstants;
import com.babytree.apps.comm.config.UrlConstrants;
import com.babytree.apps.comm.ctr.BaseController;
import com.babytree.apps.comm.net.BabytreeHttp;
import com.babytree.apps.comm.util.BabytreeUtil;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;

public class TopicDetailsController extends BaseController {

	/**
	 * 帖子详情接口地址
	 */
	public static final String MOBILE_COMMUNITY = UrlConstrants.HOST_URL + "/api/mobile_community/get_topic_data";

	/**
	 * 回帖上传图片接口地址
	 */
	private static final String UPLOAD_PHOTO_SERVER = UrlConstrants.UPLOAD_PHOTO
			+ "/mobile_community/create_photo_reply";

	/**
	 * 删除帖子接口地址
	 */
	private static final String DELETE_TZ = UrlConstrants.HOST_URL + "/api/mobile_community/delete";

	/**
	 * 删除帖子
	 * 
	 * @param loginStr
	 * @param topicId
	 * @return
	 */
	public static DataResult deleteTz(String loginStr, String topicId) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(LOGIN_STRING, loginStr));
		params.add(new BasicNameValuePair("topic_id", topicId));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(DELETE_TZ, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			String status = null;
			if (jsonObject.has(STATUS)) {
				status = jsonObject.getString(STATUS);
				if (status.equals(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
				}
			}
			result.message = BabytreeUtil.getMessage(status);
			if (jsonObject.has("data")) {
				JSONObject dataJson = jsonObject.getJSONObject("data");
				if (dataJson.has("message")) {
					result.message = dataJson.getString("message");
				}
			}

		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	/**
	 * 收藏
	 * 
	 * @param loginString
	 * @param act
	 * @param topicId
	 * @return
	 */
	public static DataResult setFavorTopic(String loginString, String act, String topicId) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.USER_APTUSERINFOACTION_FAVTOPIC));
		params.add(new BasicNameValuePair(ACT, act));
		params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		params.add(new BasicNameValuePair(ID, topicId));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}
		return result;

	}

	/**
	 * 获取帖子详情
	 * 
	 * @param login_string
	 * @param topic_id
	 * @param pg
	 * @param b
	 * @return
	 */
	public static DataResult getTopic(String login_string, int topicId, int pg, boolean onlyAnthor) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("topic_id", String.valueOf(topicId)));
		if (!TextUtils.isEmpty(login_string)) {
			params.add(new BasicNameValuePair("login_string", login_string));
		}
		params.add(new BasicNameValuePair("pg", String.valueOf(pg)));
		int b = onlyAnthor ? 1 : 0;
		params.add(new BasicNameValuePair("b", String.valueOf(b)));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(MOBILE_COMMUNITY, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equalsIgnoreCase(SUCCESS_STATUS)) {
					result.status = SUCCESS_CODE;
					if (jsonObject.has(DATA)) {
						// data的json
						JSONObject dataJson = jsonObject.getJSONObject(DATA);
						TopicNewBean bean = JsonParserForTopic.getNodeData(dataJson);
						result.data = bean;
						int current_page_num = Integer.parseInt(bean.discussion.current_page);
						int page_count_num = Integer.parseInt(bean.discussion.page_count);
						if (current_page_num < page_count_num) {
							result.totalSize = Integer.MAX_VALUE;
						} else {
							result.totalSize = bean.nodeList.size();
						}

					}
				} else {
					result.message = BabytreeUtil.getMessage(status);
					result.status=1;
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}
		return result;
	}

	/**
	 * 回帖
	 * 
	 * @param loginString
	 * @param discuzId
	 *            帖子ID
	 * @param referId
	 *            回复或者引用的回复ID
	 * @param position
	 *            回复的楼层
	 * @param content
	 *            回复内容
	 * @param filepath
	 *            图片路径
	 * @return
	 */
	public static DataResult postReply(String loginString, String discuzId, String referId, String position,
			String content, String filepath) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.POST_REPLY));
		params.add(new BasicNameValuePair(DISCUZ_ID, discuzId));
		params.add(new BasicNameValuePair(REFER_ID, referId));
		params.add(new BasicNameValuePair(POSITION, position));
		params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		params.add(new BasicNameValuePair(CONTENT, content));
		String with_photo = "2";
		if (!filepath.equalsIgnoreCase("")) {
			with_photo = "1";
		}
		params.add(new BasicNameValuePair("with_photo", with_photo));
		String jsonString = null;
		try {
			if (with_photo.equalsIgnoreCase("1")) {
				jsonString = BabytreeHttp.postPhoto(UPLOAD_PHOTO_SERVER, params, new File(filepath));
			} else {
				jsonString = BabytreeHttp.post(UPLOAD_PHOTO_SERVER, params);
			}
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				int st = SUCCESS_CODE;
				if (status.equalsIgnoreCase("success")) {
					st = SUCCESS_CODE;
				} else {
					st = 1;
				}
				result.status = st;
				if (st == SUCCESS_CODE) {
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
