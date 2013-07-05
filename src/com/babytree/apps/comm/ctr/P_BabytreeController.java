package com.babytree.apps.comm.ctr;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;

import com.babytree.apps.biz.user.model.User;
import com.babytree.apps.comm.config.InterfaceConstants;
import com.babytree.apps.comm.config.UrlConstrants;
import com.babytree.apps.comm.model.DiscussionList;
import com.babytree.apps.comm.model.Discuz;
import com.babytree.apps.comm.model.Group;
import com.babytree.apps.comm.model.Message;
import com.babytree.apps.comm.model.MessageType;
import com.babytree.apps.comm.model.MikaComment;
import com.babytree.apps.comm.model.TopicGroup;
import com.babytree.apps.comm.model.TopicMessage;
import com.babytree.apps.comm.model.UserDiscuzCountList;
import com.babytree.apps.comm.net.BabytreeHttp;
import com.babytree.apps.comm.ui.category.PinnedHeaderListViewBean;
import com.babytree.apps.comm.util.DataResult;
import com.babytree.apps.comm.util.ExceptionUtil;
import com.babytree.apps.comm.util.Md5Util;
import com.babytree.apps.comm.util.SharedPreferencesUtil;

public class P_BabytreeController extends BaseController {

	public static DataResult register(String email, String password, String nickName) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.ACTION_REGISTER));
		params.add(new BasicNameValuePair(NICKNAME, nickName));
		params.add(new BasicNameValuePair(EMAIL, email));
		params.add(new BasicNameValuePair(PASSWORD, password));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.post(UrlConstrants.NET_URL + "?action=" + InterfaceConstants.ACTION_REGISTER,
					params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					if (jsonObject.has(LOGIN_STRING)) {
						User data = User.parse(jsonObject);
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

	public static DataResult login(String email, String password) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.ACTION_LOGIN));
		params.add(new BasicNameValuePair(EMAIL, email));
		params.add(new BasicNameValuePair(PASSWORD, password));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp
					.post(UrlConstrants.NET_URL + "?action=" + InterfaceConstants.ACTION_LOGIN, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					if (jsonObject.has(LOGIN_STRING)) {
						User data = User.parse(jsonObject);
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

	public static DataResult getCommunityGroupList(String loginString, String birthday) {
		DataResult result = new DataResult();
		ArrayList<PinnedHeaderListViewBean> data = new ArrayList<PinnedHeaderListViewBean>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.GET_COMMUNITY_GROUP_LIST));
		params.add(new BasicNameValuePair(BIRTHDAY, birthday));
		if (null != loginString && !"".equals(loginString)) {
			params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		}
		params.add(new BasicNameValuePair("v", "2"));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;

				if (status == SUCCESS_CODE) {
					if (jsonObject.has(MESSAGE)) {
						result.message = jsonObject.getString(MESSAGE);
					}
					if (jsonObject.has(LIST)) {
						JSONObject listObjct = jsonObject.getJSONObject(LIST);
						if (listObjct.has(BIRTH)) {
							JSONArray birthArray = listObjct.getJSONArray(BIRTH);
							for (int i = 0; i < birthArray.length(); i++) {
								TopicGroup group = TopicGroup.parse(birthArray.getJSONObject(i));
								group.title = BIRTH;
								group.status = 1;
								PinnedHeaderListViewBean bean = new PinnedHeaderListViewBean(group, BIRTH);
								data.add(bean);
							}
						}
						if (listObjct.has(OTHER_BIRTH)) {
							JSONArray birthArray = listObjct.getJSONArray(OTHER_BIRTH);
							for (int i = 0; i < birthArray.length(); i++) {
								TopicGroup group = TopicGroup.parse(birthArray.getJSONObject(i));
								group.title = OTHER_BIRTH;
								PinnedHeaderListViewBean bean = new PinnedHeaderListViewBean(group, OTHER_BIRTH);
								data.add(bean);
							}
						}
						if (listObjct.has(OTHER)) {
							JSONArray birthArray = listObjct.getJSONArray(OTHER);
							for (int i = 0; i < birthArray.length(); i++) {
								TopicGroup group = TopicGroup.parse(birthArray.getJSONObject(i));
								group.title = OTHER;
								PinnedHeaderListViewBean bean = new PinnedHeaderListViewBean(group, OTHER);
								data.add(bean);
							}
						}
					}
					result.data = data;
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	public static DataResult submitFav(String loginString, String detailIds) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.POST_FAV));
		params.add(new BasicNameValuePair("login_string", loginString));
		params.add(new BasicNameValuePair("fav_list", detailIds));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					result.data = true;
				} else {
					result.data = false;
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

	public static DataResult getDiscuzList(String loginString, int groupId, int page, String orderby, boolean isElite,
			int provinceId, int cityProvinceId, String birthday) {
		DataResult result = new DataResult();
		ArrayList<Discuz> data = new ArrayList<Discuz>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.GET_DISCUZ_LIST));
		if (groupId != 0) {
			params.add(new BasicNameValuePair(GROUP_ID, String.valueOf(groupId)));
		}
		params.add(new BasicNameValuePair(PAGE, String.valueOf(page)));
		if (provinceId != 0) {
			params.add(new BasicNameValuePair(PROVINCE_ID, String.valueOf(provinceId)));
		}
		if (cityProvinceId != 0) {
			params.add(new BasicNameValuePair(CITY_PROVINCE_ID, String.valueOf(cityProvinceId)));
		}
		if (orderby != null) {
			params.add(new BasicNameValuePair(ORDERBY, orderby));
		}
		if (isElite) {
			params.add(new BasicNameValuePair(IS_ELITE, "yes"));
		}
		if (birthday != null) {
			params.add(new BasicNameValuePair(BIRTHDAY, birthday));
		}
		if (null != loginString && !"".equals(loginString)) {
			params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		}

		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					if (jsonObject.has(TOTAL)) {
						result.totalSize = getInt(jsonObject, TOTAL);
					} else {
						result.totalSize = Integer.MAX_VALUE;
					}
					if (jsonObject.has(LIST)) {
						JSONArray jsonArray = jsonObject.getJSONArray(LIST);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.getJSONObject(i);
							Discuz discuz = Discuz.parse(object);
							data.add(discuz);
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

	public static DataResult getUserDiscuzCountList(String userEncodeId, String loginString) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.GET_USER_DISCUZ_COUNT_LIST));
		if (userEncodeId != null) {
			params.add(new BasicNameValuePair(USER_ENCODE_ID, userEncodeId));
		}
		if (loginString != null) {
			params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		}
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					UserDiscuzCountList data = UserDiscuzCountList.parse(jsonObject);
					result.data = data;
				}
			}
			if (jsonObject.has(MESSAGE)) {
				result.message = jsonObject.getString(MESSAGE);
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;
	}

	public static DataResult getUserDiscuzList(String userEncodeId, String loginString, String type, int pageNo) {
		DataResult result = new DataResult();
		ArrayList<Discuz> data = new ArrayList<Discuz>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.GET_USER_DISCUZ_LIST));
		params.add(new BasicNameValuePair(TYPE, type));
		if (userEncodeId != null) {
			params.add(new BasicNameValuePair(USER_ENCODE_ID, userEncodeId));
		}
		if (loginString != null && !"".equals(loginString)) {
			params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		}
		params.add(new BasicNameValuePair(PAGE, String.valueOf(pageNo)));
		params.add(new BasicNameValuePair(START, String.valueOf(((pageNo - 1) * PAGE_SIZE))));
		params.add(new BasicNameValuePair(LIMIT, String.valueOf(PAGE_SIZE)));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					if (jsonObject.has(TOTAL)) {
						result.totalSize = getInt(jsonObject, TOTAL);
					} else {
						result.totalSize = Integer.MAX_VALUE;
					}
					if (jsonObject.has(LIST)) {
						JSONArray jsonArray = jsonObject.getJSONArray(LIST);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.getJSONObject(i);
							Discuz discuz = Discuz.parse(object);
							data.add(discuz);
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

	public static DataResult userRegisterCheckNickname(String nickname) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.USER_REGISTER_CHECK));
		params.add(new BasicNameValuePair(NICKNAME, nickname));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					result.data = true;
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

	public static DataResult userRegisterCheckEmail(String email) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.USER_REGISTER_CHECK));
		params.add(new BasicNameValuePair(EMAIL, email));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					result.data = true;
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

	public static DataResult getDiscuzListByTag(String loginString, int page, String tag) {
		DataResult result = new DataResult();
		ArrayList<Discuz> data = new ArrayList<Discuz>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.GET_DISCUZ_LIST_BY_TAG));
		params.add(new BasicNameValuePair(TAGS, tag));
		params.add(new BasicNameValuePair(PG, String.valueOf(page)));
		if (null != loginString && !"".equals(loginString)) {
			params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		}

		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					if (jsonObject.has(TOTAL)) {
						result.totalSize = Integer.valueOf(getStringInt(jsonObject, TOTAL));
					} else {
						result.totalSize = Integer.MAX_VALUE;
					}
					if (jsonObject.has(LIST)) {
						JSONArray jsonArray = jsonObject.getJSONArray(LIST);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.getJSONObject(i);
							Discuz discuz = Discuz.parse(object);
							data.add(discuz);
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
	 * 发送短消息
	 */
	public static DataResult sendUserMessage(String loginString, String content, String toUserEncodeId) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.SEND_USER_MESSAGE));
		params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		params.add(new BasicNameValuePair(CONTENT, content));
		params.add(new BasicNameValuePair(TO_USER_ENCODE_ID, toUserEncodeId));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.post(UrlConstrants.NET_URL + "?action=" + InterfaceConstants.SEND_USER_MESSAGE,
					params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					result.data = true;
				} else {
					result.data = false;
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
	 * 删除短消息
	 */
	public static DataResult deleteUserMessageNew(String loginString, String messageId) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		params.add(new BasicNameValuePair("message_ids", String.valueOf(messageId)));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.DLET_MESSAGE, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				String status = jsonObject.getString(STATUS);
				if (status.equalsIgnoreCase("success")) {
					result.status = 0;
				} else {
					result.status = 1;
				}
			}
		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}
		return result;
	}

	public static DataResult getUserMessageList(String loginString, MessageType messageType, int pageNo) {
		DataResult result = new DataResult();
		ArrayList<Message> data = new ArrayList<Message>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.GET_USER_MESSAGE_LIST));
		params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		params.add(new BasicNameValuePair(PAGE, String.valueOf(pageNo)));
		params.add(new BasicNameValuePair(START, String.valueOf(((pageNo - 1) * PAGE_SIZE))));
		params.add(new BasicNameValuePair(LIMIT, String.valueOf(PAGE_SIZE)));
		if (messageType == MessageType.USER_INBOX) {
			params.add(new BasicNameValuePair(MESSAGE_TYPE, "user_inbox"));
		} else {
			params.add(new BasicNameValuePair(MESSAGE_TYPE, "user_outbox"));
		}
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					if (jsonObject.has(TOTAL)) {
						result.totalSize = getInt(jsonObject, TOTAL);
					} else {
						result.totalSize = Integer.MAX_VALUE;
					}
					if (jsonObject.has(MESSAGE_LIST)) {
						JSONArray jsonArray = jsonObject.getJSONArray(MESSAGE_LIST);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.getJSONObject(i);
							Message message = Message.parse(object);
							data.add(message);
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
	 * 孕某月的精华帖
	 */
	public static DataResult getDiscuzListByPregMonthOfElite(String loginString, int page, int pregMonth, boolean elite) {
		DataResult result = new DataResult();
		ArrayList<Discuz> data = new ArrayList<Discuz>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.GET_DISCUZ_LIST_BY_PREG_MONTH));
		params.add(new BasicNameValuePair(CLIENT_TYPE, "android"));
		params.add(new BasicNameValuePair(START, String.valueOf(((page - 1) * PAGE_SIZE))));
		params.add(new BasicNameValuePair(LIMIT, String.valueOf(PAGE_SIZE)));
		params.add(new BasicNameValuePair(PREG_MONTH, String.valueOf(pregMonth)));
		if (elite) {
			params.add(new BasicNameValuePair(IS_ELITE, "1"));
		}
		if (null != loginString && !"".equals(loginString)) {
			params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		}

		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					if (jsonObject.has(TOTAL)) {
						result.totalSize = getInt(jsonObject, TOTAL);
					} else {
						result.totalSize = Integer.MAX_VALUE;
					}
					if (jsonObject.has(LIST)) {
						JSONArray jsonArray = jsonObject.getJSONArray(LIST);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.getJSONObject(i);
							Discuz discuz = Discuz.parse(object);
							data.add(discuz);
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
	 * 所有怀孕 X月的帖子
	 */
	public static DataResult getDiscuzListByPregMonth(int page, int pregMonth) {
		DataResult result = new DataResult();
		ArrayList<Discuz> data = new ArrayList<Discuz>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.GET_DISCUZ_LIST_BY_PREG_MONTH));
		params.add(new BasicNameValuePair(PREG_MONTH, String.valueOf(pregMonth)));
		params.add(new BasicNameValuePair(START, String.valueOf(((page - 1) * PAGE_SIZE))));
		params.add(new BasicNameValuePair(LIMIT, String.valueOf(PAGE_SIZE)));

		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					if (jsonObject.has(TOTAL)) {
						result.totalSize = getInt(jsonObject, TOTAL);
					} else {
						result.totalSize = Integer.MAX_VALUE;
					}
					if (jsonObject.has(LIST)) {
						JSONArray jsonArray = jsonObject.getJSONArray(LIST);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.getJSONObject(i);
							Discuz discuz = Discuz.parse(object);
							data.add(discuz);
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

	public static DataResult getUserUnreadMessageList(String loginString) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.GET_USER_UNREAD_MESSAGE_LIST));
		params.add(new BasicNameValuePair(LOGIN_STRING, loginString));

		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					result.data = DiscussionList.parse(jsonObject);
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

	public static DataResult getNewTopic(String loginStr) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.GET_USER_UNREAD_MESSAGE_LIST));
		params.add(new BasicNameValuePair(LOGIN_STRING, loginStr));
		params.add(new BasicNameValuePair("session_id", Md5Util.md5(loginStr + System.currentTimeMillis())));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					TopicMessage tm = new TopicMessage();
					if (jsonObject.has("topic_message")) {
						JSONObject object = jsonObject.getJSONObject("topic_message");
						tm = TopicMessage.parse(object);
						result.data = tm;
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

	public static DataResult getFavDiscuzList(String loginString, String type, int pageNo) {
		DataResult result = new DataResult();
		ArrayList<Discuz> data = new ArrayList<Discuz>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.ACTION_GET_FAV_LIST));
		params.add(new BasicNameValuePair(FAV_TYPE, type));
		params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		params.add(new BasicNameValuePair(START, String.valueOf(((pageNo - 1) * PAGE_SIZE))));
		params.add(new BasicNameValuePair(LIMIT, String.valueOf(PAGE_SIZE)));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					if (jsonObject.has(TOTAL)) {
						result.totalSize = getInt(jsonObject, TOTAL);
					} else {
						result.totalSize = Integer.MAX_VALUE;
					}
					if (jsonObject.has(LIST)) {
						JSONArray jsonArray = jsonObject.getJSONArray(LIST);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.getJSONObject(i);
							Discuz discuz = Discuz.parse(object);
							data.add(discuz);
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

	public static DataResult reNameNickName(String loginString, String nickName) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.RENAME_NICKNAME));
		params.add(new BasicNameValuePair(NICKNAME, nickName));
		params.add(new BasicNameValuePair(LOGIN_STRING, loginString));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
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

	public static DataResult checkLoginCookie(Context context, String loginStr) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		String cookie = SharedPreferencesUtil.getStringValue(context, "cookie");
		Boolean isLoginStr = SharedPreferencesUtil.getBooleanValue(context, "isLoginStr");
		if (isLoginStr) {
			params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.Check_Login_By_Cookie));
			params.add(new BasicNameValuePair("cookie", cookie));

		} else {
			params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.Check_Login_By_LoginStr));
			params.add(new BasicNameValuePair("login_string", loginStr));
		}
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (result.status == SUCCESS_CODE) {
					if (jsonObject.has("cookie")) {
						result.data = jsonObject.getString("cookie");
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

	public static DataResult getHotGroup() {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(0, new BasicNameValuePair(ACTION, InterfaceConstants.GET_HOT_GROUP_LIST));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.get(UrlConstrants.NET_URL, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
				if (status == SUCCESS_CODE) {
					if (jsonObject.has(LIST)) {
						ArrayList<Group> data = new ArrayList<Group>();
						JSONArray jsonList = jsonObject.getJSONArray(LIST);
						for (int i = 0; i < jsonList.length(); i++) {
							JSONObject itemJson = jsonList.getJSONObject(i);
							data.add(Group.parse(itemJson));
						}
						result.data = data;
						result.totalSize = data.size();
					}
				} else {
					result.data = false;
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

	public static DataResult applyMika(String name, String mobile, String province, String city, String address,
			String zipcode) {
		DataResult result = new DataResult();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("name", name));
		params.add(new BasicNameValuePair("mobile", mobile));
		params.add(new BasicNameValuePair("prov", province));
		params.add(new BasicNameValuePair("city", city));
		params.add(new BasicNameValuePair("address", address));
		params.add(new BasicNameValuePair("zipcode", zipcode));
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.post(UrlConstrants.NET_URL_APPLY_MIKA, params);
			JSONObject jsonObject = new JSONObject(jsonString);
			if (jsonObject.has(STATUS)) {
				int status = jsonObject.getInt(STATUS);
				result.status = status;
			}
			if (jsonObject.has("msg")) {
				result.message = jsonObject.getString("msg");
			}

		} catch (Exception e) {
			return ExceptionUtil.switchException(result, e, params, jsonString);
		}

		return result;

	}

	/**
	 * 取得米卡用户点评信息
	 */
	public static DataResult getCommentsForMika(int pageNo, HashMap<String, String> paramMap) {
		DataResult result = new DataResult();
		ArrayList<MikaComment> data = new ArrayList<MikaComment>();
		ArrayList<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("start", String.valueOf(((pageNo - 1) * PAGE_MIKA_SIZE))));
		params.add(new BasicNameValuePair("limit", String.valueOf(PAGE_MIKA_SIZE)));
		if (paramMap != null) {
			for (String key : paramMap.keySet()) {
				params.add(new BasicNameValuePair(key, paramMap.get(key)));
			}
		}
		String jsonString = null;
		try {
			jsonString = BabytreeHttp.post(UrlConstrants.NET_URL_GET_COMMENTS, params);
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
					if (jsonObject.has("list")) {
						JSONArray jsonArray = jsonObject.getJSONArray("list");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.getJSONObject(i);
							MikaComment comment = MikaComment.parse(object);
							data.add(comment);
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

}
