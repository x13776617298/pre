package com.babytree.apps.comm.model;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;

public class DiscussionList {
	private static final String TOPIC_ID = "topic_id";
	private static final String REPLY_INDEX = "reply_index";
	private static final String TITLE = "title";
	private static final String REPONSE_COUNT = "reponse_count";
	public String topicId;
	public String replyIndex;
	public String title;
	public String responseCount;
	public ArrayList<Discussion> discussion_list = new ArrayList<Discussion>();

	public static DiscussionList parse(JSONObject object) throws JSONException {
		DiscussionList bean = new DiscussionList();
		if (object.has(TOPIC_ID)) {
			bean.topicId = object.getString(TOPIC_ID);
		}
		if (object.has(REPLY_INDEX)) {
			bean.replyIndex = object.getString(REPLY_INDEX);
		}
		if (object.has(TITLE)) {
			bean.title = object.getString(TITLE);
		}
		if (object.has(REPONSE_COUNT)) {
			bean.responseCount = object.getString(REPONSE_COUNT);
		}
		return bean;
	}
}
