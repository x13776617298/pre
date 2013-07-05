package com.babytree.apps.comm.ctr;

import java.util.ArrayList;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
import com.babytree.apps.comm.model.Discuz;
import com.babytree.apps.comm.model.InOutBox;
import com.babytree.apps.comm.model.TopicComment;
import com.babytree.apps.comm.model.TopicReply;
import com.babytree.apps.comm.net.BabytreeHttp;
import com.babytree.apps.comm.ui.category.PinnedHeaderListViewBean;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.config.InterfaceConstants;
import com.babytree.apps.comm.config.UrlConstrants;

/**
 * 关于第三方账号登录，新增，绑定等功能的网络请求类
 * 
 * @author ybq
 * 
 */
public class MessageController extends BaseController {
	public static DataResult getMessageListForCommentReply(String loginString,
			int pageNo) {
		DataResult result = new DataResult();
		ArrayList<PinnedHeaderListViewBean> data = new ArrayList<PinnedHeaderListViewBean>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.GET_DISCUZ));
		params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		params.add(new BasicNameValuePair(PAGE, String.valueOf(pageNo)));
		params.add(new BasicNameValuePair(START, String
				.valueOf(((pageNo - 1) * PAGE_SIZE))));
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
						JSONArray jsonArray = jsonObject
								.getJSONArray(REPLY_LIST);
						if (jsonArray.length() == 0) {
							result.totalSize = 0;
						}
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.getJSONObject(i);
							if (object.getInt("type") == 1) {
								TopicReply topicReply = TopicReply
										.parse(object);
								PinnedHeaderListViewBean bean = new PinnedHeaderListViewBean(
										topicReply, "1");
								data.add(bean);
							} else if (object.getInt("type") == 2) {
								TopicComment topicComment = TopicComment
										.parse(object);
								PinnedHeaderListViewBean bean = new PinnedHeaderListViewBean(
										topicComment, "2");
								data.add(bean);
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
	 * 未读消息数目
	 */
	public static DataResult getUnreadMessageCount(String loginString) {
		DataResult result = new DataResult();
		InOutBox io = new InOutBox();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION,
				InterfaceConstants.GET_USER_UNREAD_MESSAGE_COUNT));
		params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					io = InOutBox.parse(jsonObject);
					result.data = io;
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

	public static DataResult getFeedBack(String loginString) {
		DataResult result = new DataResult();
		Discuz discuz = new Discuz();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.GET_DISCUZ));
		if (null != loginString && !"".equals(loginString)) {
			params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		}
		params.add(new BasicNameValuePair(MESSAGE_TYPE, "topic"));
		params.add(new BasicNameValuePair("topic_id", "4493188"));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (jsonObject.has("topic")) {
					discuz = Discuz.parse(new JSONObject(jsonObject
							.getString("topic")));
				}
				result.data = discuz;
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;

	}

}
