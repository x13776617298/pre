package com.babytree.apps.comm.model;

import org.json.JSONException;
import org.json.JSONObject;

public class TopicReply extends Base{
    private static final long serialVersionUID = -200605627528470366L;
    
    /////////////////////////////
    public String type = "";
    public String topic_title = "";
    public String topic_last_reply_ts = "";
    public String author_response_count = "";
    
    public String topic_id = "";
    public String response_count = "";
    public String is_fav = "";
    
    public String topic_reply_page = "1";
    
    public String topic_reply_unread_count = "0";
    
   /* type: "1",//回复帖子，类型不同数据有所不同
    reply_user_ecode_id: "u1574251033",//回复人的userEncodeId
    reply_user_avatar: "http://pic01.babytreeimg.com/foto3/thumbs/2012/1114/64/7/5dd52a193ec4de9155fb2_hs.jpg",//回复人的头像地址
    reply_user_nickname: "cleverhack2",//回复人的昵称
    topic_title: "hi!",//帖子的标题
    topic_id: "10",//帖子id
    topic_author_reply_count: "0",//帖子作者回复数
    topic_total_reply_count:"0",//帖子
    topic_is_fav: "0",//我是否收藏这个帖子
    topic_reply_page: "10",//回复评论所在的页码
    topic_last_reply_ts: "1348724124",//	帖子最后被回复的时间
    topic_reply_unread_count: "0",//帖子回复未读数
*/
    private static final String TYPE = "type";
    private static final String TOPIC_TITLE = "topic_title";
    private static final String TOPIC_LAST_REPLY_TS = "topic_last_reply_ts";
    private static final String AUTHOR_RESPONSE_COUNT = "author_response_count";

    private static final String TOPIC_ID = "topic_id";
    private static final String RESPONSE_COUNT = "response_count";
    private static final String IS_FAV = "is_fav";
    private static final String TOPIC_REPLY_PAGE = "topic_reply_page";
    private static final String TOPIC_REPLY_UNREAD_COUNT = "topic_reply_unread_count";
    
    public static TopicReply parse(JSONObject jsonObject) throws JSONException {
        TopicReply bean = new TopicReply();
        if(jsonObject.has(TYPE)){
            bean.type = jsonObject.getString(TYPE).trim();
        }
        if(jsonObject.has(TOPIC_TITLE)){
            bean.topic_title = jsonObject.getString(TOPIC_TITLE).trim();
        }
        if (jsonObject.has(TOPIC_LAST_REPLY_TS)) {
            bean.topic_last_reply_ts = jsonObject.getString(TOPIC_LAST_REPLY_TS).trim();
        }
        if (jsonObject.has(AUTHOR_RESPONSE_COUNT)) {
            bean.author_response_count = jsonObject.getString(AUTHOR_RESPONSE_COUNT).trim();
        }
        
        if(jsonObject.has(TOPIC_ID)){
            bean.topic_id = jsonObject.getString(TOPIC_ID).trim();
        }
        if (jsonObject.has(RESPONSE_COUNT)) {
            bean.response_count = jsonObject.getString(RESPONSE_COUNT).trim();
        }
        if (jsonObject.has(IS_FAV)) {
            bean.is_fav = jsonObject.getString(IS_FAV).trim();
        }
        if (jsonObject.has(TOPIC_REPLY_PAGE)) {
            bean.topic_reply_page = jsonObject.getString(TOPIC_REPLY_PAGE).trim();
        }
        if (jsonObject.has(TOPIC_REPLY_UNREAD_COUNT)) {
            bean.topic_reply_unread_count = jsonObject.getString(TOPIC_REPLY_UNREAD_COUNT).trim();
        }
        return bean;
    }

	@Override
	public String toString() {
		return "TopicReply [type=" + type + ", topic_title=" + topic_title + ", topic_last_reply_ts="
				+ topic_last_reply_ts + ", author_response_count=" + author_response_count + ", topic_id=" + topic_id
				+ ", response_count=" + response_count + ", is_fav=" + is_fav + ", topic_reply_page="
				+ topic_reply_page + ", topic_reply_unread_count=" + topic_reply_unread_count + "]";
	}
    
    
}
