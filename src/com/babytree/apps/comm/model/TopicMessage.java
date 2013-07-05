package com.babytree.apps.comm.model;


import org.json.JSONException;
import org.json.JSONObject;

import com.babytree.apps.comm.model.Base;

public class TopicMessage extends Base{
    private static final long serialVersionUID = -200605627528470366L;
    
    public String hasReply = "";
    public String title = "";

    public String topicId = "";

    public String topicPage = "";

    public String topicTotalPage = "";

    private static final String HAS_REPLY = "has_reply";
    private static final String TITLE = "title";

    private static final String TOPIC_ID = "topic_id";

    private static final String TOPIC_PAGE = "reply_index";

    private static final String TOPIC_TOTAL_PAGE = "reponse_count";

    public static TopicMessage parse(JSONObject jsonObject) throws JSONException {
        TopicMessage bean = new TopicMessage();
        if(jsonObject.has(HAS_REPLY)){
            bean.hasReply = jsonObject.getString(HAS_REPLY).trim();
        }
        if (jsonObject.has(TITLE)) {
            bean.title = jsonObject.getString(TITLE).trim();
        }
        if (jsonObject.has(TOPIC_ID)) {
            bean.topicId = jsonObject.getString(TOPIC_ID).trim();
        }
        if (jsonObject.has(TOPIC_PAGE)) {
            bean.topicPage = jsonObject.getString(TOPIC_PAGE).trim();
        }
        if (jsonObject.has(TOPIC_TOTAL_PAGE)) {
            bean.topicTotalPage = jsonObject.getString(TOPIC_TOTAL_PAGE).trim();
        }
        return bean;
    }
}
