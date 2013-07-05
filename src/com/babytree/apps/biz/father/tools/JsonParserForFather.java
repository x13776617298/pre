package com.babytree.apps.biz.father.tools;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.babytree.apps.biz.father.model.BindBoth;
import com.babytree.apps.biz.father.model.BindStatus;
import com.babytree.apps.biz.father.model.BindUser;
import com.babytree.apps.biz.father.model.Task;
import com.babytree.apps.biz.father.model.UnReadMsg;
import com.babytree.apps.comm.tools.JsonParserTolls;

/**
 * 爸爸版功能数据解析
 * 
 * @author pengxh
 * 
 */
public class JsonParserForFather {

	/**
	 * 获取绑定状态数据
	 * 
	 * @param dataJson
	 * @return
	 */
	public static BindStatus getBindStatus(JSONObject dataJson) {
		// 创建绑定状态对象
		BindStatus status = new BindStatus();
		// 设置绑定状态数据
		status.setBindStatus(JsonParserTolls.getStr(dataJson, "bind_status")) ;
		status.setCode(JsonParserTolls.getStr(dataJson, "code"));
		status.setImageUrl(JsonParserTolls.getStr(dataJson, "image_url"));
		return status;
	}

	/**
	 * 获取绑定成功的详细数据
	 * 
	 * @param dataJson
	 * @return
	 */
	public static BindBoth bind(JSONObject dataJson) {
		// 创建绑定成功对象
		BindBoth status = new BindBoth();
		status.mom_user_id = JsonParserTolls.getStr(dataJson, "mom_user_id");
		status.father_user_id = JsonParserTolls.getStr(dataJson, "father_user_id");
		status.image_url = JsonParserTolls.getStr(dataJson, "image_url");
		// 设置绑定用户详细数据
		status.bindUser = getBindUserInfo(dataJson, "user_info");
		return status;
	}

	/**
	 * 获取绑定用户信息
	 * 
	 * @param user_info_json
	 *            个人信息json
	 * @param key
	 * @return
	 */
	private static BindUser getBindUserInfo(JSONObject data_json, String key) {
		JSONObject user_info_json = JsonParserTolls.getJsonObj(data_json, key);
		// 创建绑定用户对象
		BindUser user_info = new BindUser();
		user_info.enc_user_id = JsonParserTolls.getStr(user_info_json, "enc_user_id");
		user_info.nickname = JsonParserTolls.getStr(user_info_json, "nickname");
		user_info.baby_name = JsonParserTolls.getStr(user_info_json, "baby_name");
		user_info.email = JsonParserTolls.getStr(user_info_json, "email");
		user_info.email_status = JsonParserTolls.getStr(user_info_json, "email_status");
		user_info.avatar_url = JsonParserTolls.getStr(user_info_json, "avatar_url");
		user_info.baby_sex = JsonParserTolls.getStr(user_info_json, "baby_sex");
		user_info.baby_age = JsonParserTolls.getStr(user_info_json, "baby_age");
		user_info.baby_birthday = JsonParserTolls.getStr(user_info_json, "baby_birthday");
		user_info.follow_count = JsonParserTolls.getStr(user_info_json, "follow_count");
		user_info.fans_count = JsonParserTolls.getStr(user_info_json, "fans_count");
		user_info.location = JsonParserTolls.getStr(user_info_json, "location");
		user_info.gender = JsonParserTolls.getStr(user_info_json, "gender");
		user_info.reg_ts = JsonParserTolls.getStr(user_info_json, "reg_ts");
		user_info.is_followed = JsonParserTolls.getStr(user_info_json, "is_followed");
		user_info.status = JsonParserTolls.getStr(user_info_json, "status");
		user_info.location_name = JsonParserTolls.getStr(user_info_json, "location_name");
		user_info.group_id = JsonParserTolls.getStr(user_info_json, "group_id");
		user_info.hospital_id = JsonParserTolls.getStr(user_info_json, "hospital_id");
		user_info.hospital_name = JsonParserTolls.getStr(user_info_json, "hospital_name");
		user_info.baby_birthday_ts = JsonParserTolls.getStr(user_info_json, "baby_birthday_ts");
		user_info.login_string = JsonParserTolls.getStr(user_info_json, "login_string");
		return user_info;
	}

	/**
	 * 获取未读消息总数
	 * 
	 * @param dataJson
	 * @return
	 */
	public static UnReadMsg getUnReadMsgCount(JSONObject dataJson) {
		// 创建未读消息对象
		UnReadMsg unRead = new UnReadMsg();
		// 设置绑定状态数据
		unRead.unread_message = JsonParserTolls.getInt(dataJson, "unread_message", 0);
		return unRead;
	}

	/**
	 * 解除绑定
	 * 
	 * @param dataJson
	 * @return
	 */
	public static BindStatus unBind(JSONObject dataJson) {
		// 创建绑定状态对象
		BindStatus status = new BindStatus();
		// 设置绑定状态数据
		status.setBindStatus(JsonParserTolls.getStr(dataJson, "bind_status"));
		status.setCode(JsonParserTolls.getStr(dataJson, "code"));
		return status;
	}

	/**
	 * 获取任务
	 * 
	 * @param taskJson
	 * @return
	 */
	private static Task getTask(JSONObject taskJson) {
		// 创建任务对象
		Task task = new Task();
		task.task_id = JsonParserTolls.getStr(taskJson, "task_id");
		task.task_title = JsonParserTolls.getStr(taskJson, "task_title");
		task.task_content = JsonParserTolls.getStr(taskJson, "task_content");
		task.task_send_text = JsonParserTolls.getStr(taskJson, "task_send_text");
		task.task_yunqi = JsonParserTolls.getStr(taskJson, "task_yunqi");
		task.task_type = JsonParserTolls.getStr(taskJson, "task_type");
		// 设置绑定状态数据
		return task;
	}

	/**
	 * 获取任务列表
	 * 
	 * @param dataJson
	 * @return
	 */
	public static ArrayList<Task> getTasks(JSONObject dataJson) {

		JSONArray taskArray = JsonParserTolls.getJsonArray(dataJson, "task_list");

		if (taskArray != null) {
			// 创建任务列表对象
			ArrayList<Task> tasks = new ArrayList<Task>();
			int count = taskArray.length();
			for (int i = 0; i < count; i++) {
				tasks.add(getTask(JsonParserTolls.getJsonObj(taskArray, i)));
			}
			return tasks;
		}
		return null;
	}

	/**
	 * 完成任务
	 * 
	 * @param dataJson
	 * @return
	 */
	public static Task doneTask(JSONObject dataJson) {

		// 创建任务对象
		Task task = new Task();
		task.task_yunqi = JsonParserTolls.getStr(dataJson, "task_yunqi");
		BindUser bindUser = new BindUser();
		bindUser.total_yunqi = JsonParserTolls.getStr(dataJson, "total_yunqi");
		task.bindUser = bindUser;
		return task;
	}
}
