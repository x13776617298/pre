package com.babytree.apps.comm.model;

import org.json.JSONException;
import org.json.JSONObject;

public class TopicComment extends Base{
    private static final long serialVersionUID = -200605627528470366L;
    
    ////////////////////////////
    public String type = "";
    public String reply_user_nickname = "";
    public String topic_title = "";
    public String reply_user_ts = "0";
    public String reply_user_content = "";
    public String my_reply_content = "";
    
    public String topic_id = "";
    public String author_response_count = "";
    public String is_fav = "";
    public String response_count = "";
    public String topic_reply_page = "1";
    

    private static final String TYPE = "type";
    private static final String REPLY_USER_NICKNAME = "reply_user_nickname";

    private static final String TOPIC_TITLE = "topic_title";

    private static final String REPLY_USER_TS = "reply_user_ts";

    private static final String REPLY_USER_CONTENT = "reply_user_content";

    private static final String MY_REPLY_CONTENT = "my_reply_content";
    
    private static final String TOPIC_ID = "topic_id";

    private static final String AUTHOR_RESPONSE_COUNT = "author_response_count";

    private static final String IS_FAV = "is_fav";

    private static final String RESPONSE_COUNT = "response_count";
    
    private static final String TOPIC_REPLY_PAGE = "topic_reply_page";
    public static TopicComment parse(JSONObject jsonObject) throws JSONException {
        TopicComment bean = new TopicComment();
        if(jsonObject.has(TYPE)){
            bean.type = jsonObject.getString(TYPE).trim();
        }
        if (jsonObject.has(REPLY_USER_NICKNAME)) {
            bean.reply_user_nickname = jsonObject.getString(REPLY_USER_NICKNAME).trim();
        }
        if (jsonObject.has(TOPIC_TITLE)) {
            bean.topic_title = jsonObject.getString(TOPIC_TITLE).trim();
        }
        if (jsonObject.has(REPLY_USER_TS)) {
            bean.reply_user_ts = jsonObject.getString(REPLY_USER_TS).trim();
        }
        if (jsonObject.has(REPLY_USER_CONTENT)) {
            bean.reply_user_content = jsonObject.getString(REPLY_USER_CONTENT).trim();
        }
        if (jsonObject.has(MY_REPLY_CONTENT)) {
            bean.my_reply_content = jsonObject.getString(MY_REPLY_CONTENT).trim();
        }
        
        if (jsonObject.has(TOPIC_ID)) {
            bean.topic_id = jsonObject.getString(TOPIC_ID).trim();
        }
        if (jsonObject.has(AUTHOR_RESPONSE_COUNT)) {
            bean.author_response_count = jsonObject.getString(AUTHOR_RESPONSE_COUNT).trim();
        }
        if (jsonObject.has(IS_FAV)) {
            bean.is_fav = jsonObject.getString(IS_FAV).trim();
        }
        if (jsonObject.has(RESPONSE_COUNT)) {
            bean.response_count = jsonObject.getString(RESPONSE_COUNT).trim();
        }
        if (jsonObject.has(TOPIC_REPLY_PAGE)) {
            bean.topic_reply_page = jsonObject.getString(TOPIC_REPLY_PAGE).trim();
        }
        return bean;
    }
	@Override
	public String toString() {
		return "TopicComment [type=" + type + ", reply_user_nickname=" + reply_user_nickname + ", topic_title="
				+ topic_title + ", reply_user_ts=" + reply_user_ts + ", reply_user_content=" + reply_user_content
				+ ", my_reply_content=" + my_reply_content + ", topic_id=" + topic_id + ", author_response_count="
				+ author_response_count + ", is_fav=" + is_fav + ", response_count=" + response_count
				+ ", topic_reply_page=" + topic_reply_page + "]";
	}
    
}
