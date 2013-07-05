package com.babytree.apps.biz.topicpost.ctr;

import java.io.File;
import java.util.ArrayList;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.babytree.apps.comm.config.UrlConstrants;
import com.babytree.apps.comm.ctr.BaseController;
import com.babytree.apps.comm.net.BabytreeHttp;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;

public class TopicPostController extends BaseController {

	/**
	 * 发帖子上传图片接口地址
	 */
	private static final String UPLOAD_PHOTO_SERVER = UrlConstrants.UPLOAD_PHOTO
			+ "/mobile_community/create_photo_post";

	/**
	 * 发帖子
	 * 
	 * @param loginString
	 * @param description
	 * @param filepath
	 * @param groupId
	 * @param title
	 * @param content
	 * @param birthday
	 * @param doctorName
	 * @param with_photo
	 * @return
	 * 
	 *         圈子ID跟生日不能同时传
	 */
	public static DataResult postPhotoNew(String loginString, String description, String filepath, String groupId,
			String title, String content, String birthday, String doctorName, String with_photo) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("description", description));
		if (!groupId.equalsIgnoreCase("0")) {
			params.add(new BasicNameValuePair(GROUP_ID, groupId));
		} else {
			params.add(new BasicNameValuePair(BIRTHDAY, birthday));
		}
		params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		params.add(new BasicNameValuePair(TITLE, title));
		if (doctorName != null && !doctorName.equals("")) {
			params.add(new BasicNameValuePair(DOCTOR_NAME, doctorName));
		}
		params.add(new BasicNameValuePair(CONTENT, content));
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
