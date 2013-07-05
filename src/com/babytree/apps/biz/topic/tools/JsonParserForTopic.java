package com.babytree.apps.biz.topic.tools;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.babytree.apps.biz.home.model.Commend;
import com.babytree.apps.biz.topic.model.ANode;
import com.babytree.apps.biz.topic.model.Discussion;
import com.babytree.apps.biz.topic.model.DiscussionContent;
import com.babytree.apps.biz.topic.model.GroupData;
import com.babytree.apps.biz.topic.model.ImgNode;
import com.babytree.apps.biz.topic.model.Node;
import com.babytree.apps.biz.topic.model.PositionUser;
import com.babytree.apps.biz.topic.model.ReplyContent;
import com.babytree.apps.biz.topic.model.TextNode;
import com.babytree.apps.biz.topic.model.UserInfo;
import com.babytree.apps.biz.topic.model.n.ReplayFooterNode;
import com.babytree.apps.biz.topic.model.n.ReplayHeaderNode;
import com.babytree.apps.biz.topic.model.n.TopicNewBean;
import com.babytree.apps.comm.tools.JsonParserTolls;

/**
 * 帖子解析
 * 
 * @author wangbingqi
 * 
 */
public class JsonParserForTopic {
	/**
	 * 获取个人信息
	 * 
	 * @param user_info_json
	 *            个人信息json
	 * @return
	 */
	private static UserInfo getUserInfo(JSONObject discussion_json, String key) {
		JSONObject user_info_json = JsonParserTolls.getJsonObj(discussion_json, key);
		UserInfo user_info = new UserInfo();
		if (user_info_json != null) {
			user_info.author_enc_user_id = JsonParserTolls.getStr(user_info_json, "author_enc_user_id");
			user_info.author_name = JsonParserTolls.getStr(user_info_json, "author_name");
			user_info.author_avatar = JsonParserTolls.getStr(user_info_json, "author_avatar");
			user_info.user_level = JsonParserTolls.getStr(user_info_json, "user_level");
			user_info.user_level_img = JsonParserTolls.getStr(user_info_json, "user_level_img");
		}
		return user_info;
	}

	/**
	 * 获取圈子信息
	 * 
	 * @return
	 */
	private static GroupData getGroupData(JSONObject discussion_json, String key) {
		JSONObject group_data_json = JsonParserTolls.getJsonObj(discussion_json, key);
		GroupData group_data = new GroupData();
		if (group_data_json != null) {
			group_data.id = JsonParserTolls.getInt(group_data_json, "id", 0);
			group_data.title = JsonParserTolls.getStr(group_data_json, "title");
			group_data.img_src = JsonParserTolls.getStr(group_data_json, "img_src");
			group_data.topic_count = JsonParserTolls.getInt(group_data_json, "topic_count", 0);
			group_data.user_count = JsonParserTolls.getInt(group_data_json, "user_count", 0);
		}
		return group_data;
	}

	/**
	 * 获取帖子详情
	 * 
	 * @param discussion_json
	 * @param key
	 * @return
	 */
	private static DiscussionContent getDiscussionContent(JSONObject discussion_json, String key) {
		JSONArray discussion_jsonArray = JsonParserTolls.getJsonArray(discussion_json, key);
		if (discussion_jsonArray != null) {
			DiscussionContent discussion_content = new DiscussionContent();
			discussion_content.list = new ArrayList<Node>();
			for (int i = 0; i < discussion_jsonArray.length(); i++) {
				JSONObject json = JsonParserTolls.getJsonObj(discussion_jsonArray, i);
				String tag = JsonParserTolls.getStr(json, "tag");
				Node node = null;
				if (tag != null && !tag.equalsIgnoreCase("")) {
					if (tag.equalsIgnoreCase("text")) {
						TextNode node_test = new TextNode();
						node_test.tag = "text";
						node_test.text = JsonParserTolls.getStr(json, "text");
						node = node_test;
					} else if (tag.equalsIgnoreCase("img")) {
						ImgNode node_img = new ImgNode();
						node_img.tag = "img";
						node_img.big_src = JsonParserTolls.getStr(json, "big_src");
						node_img.small_src = JsonParserTolls.getStr(json, "small_src");
						node = node_img;
					} else if (tag.equalsIgnoreCase("a")) {
						ANode node_a = new ANode();
						node_a.tag = "a";
						node_a.type = JsonParserTolls.getStr(json, "type");
						node_a.href = JsonParserTolls.getStr(json, "href");
						node_a.topic_id = JsonParserTolls.getStr(json, "topic_id");
						JSONObject jsonObjNode = JsonParserTolls.getJsonObj(json, "content");
						if (jsonObjNode != null) {
							String node_a_tag = JsonParserTolls.getStr(jsonObjNode, "tag");
							if (node_a_tag.equalsIgnoreCase("text")) {
								TextNode node_a_text = new TextNode();
								node_a_text.tag = "text";
								node_a_text.text = JsonParserTolls.getStr(jsonObjNode, "text");
								node_a.node = node_a_text;
							} else if (node_a_tag.equalsIgnoreCase("img")) {
								ImgNode node_a_img = new ImgNode();
								node_a_img.tag = "img";
								node_a_img.big_src = JsonParserTolls.getStr(jsonObjNode, "big_src");
								node_a_img.small_src = JsonParserTolls.getStr(jsonObjNode, "small_src");
								node_a.node = node_a_img;
							}
						}
						node = node_a;
					} else {
						node = new Node();
						node.tag = "error";
					}
				}
				discussion_content.list.add(i, node);
			}
			return discussion_content;
		} else {
			return null;
		}
	}

	/**
	 * 获取回帖详情
	 * 
	 * @param ReplyContent_json
	 * @param key
	 * @return
	 */
	private static ReplyContent getReplyContent(JSONObject ReplyContent_json, String key) {
		JSONArray discussion_jsonArray = JsonParserTolls.getJsonArray(ReplyContent_json, key);
		if (discussion_jsonArray != null) {
			ReplyContent discussion_content = new ReplyContent();
			discussion_content.list = new ArrayList<Node>();
			for (int i = 0; i < discussion_jsonArray.length(); i++) {
				JSONObject json = JsonParserTolls.getJsonObj(discussion_jsonArray, i);
				String tag = JsonParserTolls.getStr(json, "tag");
				Node node = null;
				if (tag != null && !tag.equalsIgnoreCase("")) {
					if (tag.equalsIgnoreCase("text")) {
						TextNode node_test = new TextNode();
						node_test.tag = "text";
						node_test.text = JsonParserTolls.getStr(json, "text");
						node = node_test;
					} else if (tag.equalsIgnoreCase("img")) {
						ImgNode node_img = new ImgNode();
						node_img.tag = "img";
						node_img.big_src = JsonParserTolls.getStr(json, "big_src");
						node_img.small_src = JsonParserTolls.getStr(json, "small_src");
						node = node_img;
					} else if (tag.equalsIgnoreCase("a")) {
						ANode node_a = new ANode();
						node_a.tag = "a";
						node_a.type = JsonParserTolls.getStr(json, "type");
						node_a.href = JsonParserTolls.getStr(json, "href");
						node_a.topic_id = JsonParserTolls.getStr(json, "topic_id");
						JSONObject jsonObjNode = JsonParserTolls.getJsonObj(json, "content");
						if (jsonObjNode != null) {
							String node_a_tag = JsonParserTolls.getStr(jsonObjNode, "tag");
							if (node_a_tag.equalsIgnoreCase("text")) {

								TextNode node_a_text = new TextNode();
								node_a_text.tag = "text";
								node_a_text.text = JsonParserTolls.getStr(jsonObjNode, "text");
								node_a.node = node_a_text;
							} else if (node_a_tag.equalsIgnoreCase("img")) {
								ImgNode node_a_img = new ImgNode();
								node_a_img.tag = "img";
								node_a_img.big_src = JsonParserTolls.getStr(json, "big_src");
								node_a_img.small_src = JsonParserTolls.getStr(json, "small_src");
								node_a.node = node_a_img;
							}
						}
						node = node_a;
					} else {
						node = new Node();
						node.tag = "error";
					}
				}
				discussion_content.list.add(i, node);
			}
			return discussion_content;
		} else {
			return null;
		}
	}

	/**
	 * 获取帖子详情所有Node
	 * 
	 * @param dataJson
	 * @return
	 */
	public static TopicNewBean getNodeData(JSONObject dataJson) {
		// 创建帖子对象
		TopicNewBean bean = new TopicNewBean();
		// 取发帖的公共数据
		JSONObject discussion_json = JsonParserTolls.getJsonObj(dataJson, "discussion");
		// 创建帖子主题
		bean.discussion = new Discussion();
		bean.discussion.is_fav = JsonParserTolls.getStr(discussion_json, "is_fav", "0");
		bean.discussion.current_page = JsonParserTolls.getStr(discussion_json, "current_page", "1");
		bean.discussion.page_count = JsonParserTolls.getStr(discussion_json, "page_count", "1");
		bean.discussion.discussion_title = JsonParserTolls.getStr(discussion_json, "discussion_title");
		bean.discussion.view_count = JsonParserTolls.getStr(discussion_json, "view_count");
		bean.discussion.reply_count = JsonParserTolls.getStr(discussion_json, "reply_count");
		bean.discussion.city_name = JsonParserTolls.getStr(discussion_json, "city_name");
		bean.discussion.create_ts = JsonParserTolls.getStr(discussion_json, "create_ts");
		// 取发帖的个人信息
		bean.discussion.user_info = getUserInfo(discussion_json, "user_info");
		// 取发帖的圈子信息
		bean.discussion.group_data = getGroupData(discussion_json, "group_data");
		// 取发帖的内容
		bean.discussion.discussion_content = getDiscussionContent(discussion_json, "discussion_content");

		// 取回帖公共数据
		JSONArray replay_jsonArray = JsonParserTolls.getJsonArray(dataJson, "reply_list");

		if (replay_jsonArray != null) {
			for (int i = 0; i < replay_jsonArray.length(); i++) {
				// 取回帖的内容
				JSONObject reply_list_item = JsonParserTolls.getJsonObj(replay_jsonArray, i);
				// 创建当前回复
				ReplayHeaderNode replyHaHeaderNode = new ReplayHeaderNode();
				replyHaHeaderNode.reply_id = JsonParserTolls.getStr(reply_list_item, "reply_id");
				replyHaHeaderNode.floor = JsonParserTolls.getStr(reply_list_item, "floor");
				replyHaHeaderNode.position = JsonParserTolls.getStr(reply_list_item, "position");
				replyHaHeaderNode.position_user = new PositionUser();
				JSONObject position_user_json = JsonParserTolls.getJsonObj(reply_list_item, "position_user");
				if (position_user_json != null) {
					replyHaHeaderNode.position_user.nickname = JsonParserTolls.getStr(position_user_json, "nickname",
							"");
				}
				replyHaHeaderNode.create_ts = JsonParserTolls.getStr(reply_list_item, "create_ts");
				replyHaHeaderNode.city_name = JsonParserTolls.getStr(reply_list_item, "city_name");
				// 取回帖个人信息
				replyHaHeaderNode.user_info = getUserInfo(reply_list_item, "user_info");
				replyHaHeaderNode.tag = "reply_header";
				replyHaHeaderNode.is_author = replyHaHeaderNode.user_info.author_enc_user_id
						.equals(bean.discussion.user_info.author_enc_user_id) ? true : false;
				// 存头
				bean.nodeList.add(replyHaHeaderNode);
				// 取回帖详情
				ReplyContent replyContent = getReplyContent(reply_list_item, "reply_content");
				bean.nodeList.addAll(replyContent.list);
//				for (int y = 0; y < replyContent.list.size(); y++) {
//					if (replyContent.list.get(y).tag.equalsIgnoreCase("text")) {
//						TextNode node = ((TextNode) replyContent.list.get(y));
//						String str = node.text;
//						List<TextNode> list = getTextNodeList(str, 50);
//						for (int x = 0; x < list.size(); x++) {
//							bean.nodeList.add(list.get(x));
//						}
//					} else {
//						bean.nodeList.add(replyContent.list.get(y));
//					}
//
//				}
				// 取回复结尾内容
				ReplayFooterNode replayFooterNode = new ReplayFooterNode();
				replayFooterNode.reply_id = replyHaHeaderNode.reply_id;
				replayFooterNode.floor = replyHaHeaderNode.floor;
				replayFooterNode.position = replyHaHeaderNode.position;
				replayFooterNode.create_ts = replyHaHeaderNode.create_ts;
				replayFooterNode.city_name = replyHaHeaderNode.city_name;
				// 取回帖个人信息
				replayFooterNode.user_info = replyHaHeaderNode.user_info;
				replayFooterNode.tag = "reply_footer";
				bean.nodeList.add(replayFooterNode);
			}
		}

		return bean;
	}

	/**
	 * 得到一个TextNode
	 * 
	 * @param str
	 * @return
	 */
	public static TextNode getTextNode(String str) {
		TextNode textNode = new TextNode();
		textNode.tag = "text";
		textNode.text = str;
		return textNode;
	}

	/**
	 * 切割大文本
	 * 
	 * @param str
	 *            文本
	 * @param n
	 *            切割的长度
	 * @return
	 */
	public static List<TextNode> getTextNodeList(String str, int n) {
		List<TextNode> list = new ArrayList<TextNode>();
		int mainL = str.length();
		if (mainL <= n) {
			list.add(getTextNode(str));
			return list;
		}
		int time = mainL / n;
		if (mainL % n == 0) {
			time = mainL / n;
		} else {
			time = (mainL / n) + 1;
		}
		StringBuffer tmpStr = new StringBuffer(str);
		for (int i = 0; i < time; time++) {
			String nodeStr = tmpStr.substring(0, n);
			list.add(getTextNode(nodeStr));
			tmpStr = tmpStr.delete(0, n);
			if (tmpStr.length() < n) {
				list.add(getTextNode(tmpStr.toString()));
				return list;
			}
		}
		return list;
	}

	/**
	 * 解析推荐帖子列表
	 * 
	 * @param dataJson
	 * @return
	 */
	public static ArrayList<Commend> getCommendTopics(JSONObject dataJson) {
		ArrayList<Commend> commends = new ArrayList<Commend>();
		JSONArray recommendListArray = JsonParserTolls.getJsonArray(dataJson, "recommend_list");
		JSONObject jsonObj = JsonParserTolls.getJsonObj(recommendListArray, 0);

		JSONArray listArray = JsonParserTolls.getJsonArray(jsonObj, "list");
		for (int i = 0; i < listArray.length(); i++) {

			Commend commend = new Commend();

			commend.setDate(JsonParserTolls.getLong(jsonObj, "date", 0L));

			JSONObject jsonObjCommend = JsonParserTolls.getJsonObj(listArray, i);
			String id = JsonParserTolls.getStr(jsonObjCommend, "id");
			String img = JsonParserTolls.getStr(jsonObjCommend, "img");
			String title = JsonParserTolls.getStr(jsonObjCommend, "title");
			commend.setId(id);
			commend.setImg(img);
			commend.setTitle(title);
			commends.add(commend);
		}
		return commends;
	}

}
