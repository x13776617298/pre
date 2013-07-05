package com.babytree.apps.comm.model;

import org.json.JSONException;
import org.json.JSONObject;

public class InOutBox {
	private final static String INBOX_TOTAL_COUNT =  "inbox_total_count";
	private final static String OUTBOX_TOTAL_COUNT =  "outbox_total_count";
	private final static String USER_UNREAD_COUNT =  "user_unread_count";
	private final static String INBOX_CONTENT =  "inbox_content";
	private final static String OUTBOX_CONTENT =  "outbox_content";
	private final static String COMMENT_REPLY_CONTENT =  "comment_reply_content";
	private final static String USER_COMMENT_REPLY_UNREAD_COUNT =  "user_comment_reply_unread_count";
	private final static String COMMENT_REPLY_TOTAL_COUNT =  "comment_reply_total_count";

	
	
	
	public String inboxTotalCount = "0";

    public String outboxTotalCount = "0";

    public String unreadCount = "0";
    
    public String inbox_content = "";
    public String outbox_content = "";
    public String comment_reply_content = "";
    public String user_comment_reply_unread_count = "0";
    public String comment_reply_total_count = "0";
    public static InOutBox parse(JSONObject object) throws JSONException {
    	InOutBox bean = new InOutBox();
        if (object.has(INBOX_TOTAL_COUNT)) {
            bean.inboxTotalCount = object.getString(INBOX_TOTAL_COUNT);
        }
        if (object.has(OUTBOX_TOTAL_COUNT)) {
            bean.outboxTotalCount = object.getString(OUTBOX_TOTAL_COUNT);
        }
        if (object.has(USER_UNREAD_COUNT)) {
            bean.unreadCount = object.getString(USER_UNREAD_COUNT);
        }
        if (object.has(INBOX_CONTENT)) {
            bean.inbox_content = object.getString(INBOX_CONTENT);
        }
        if (object.has(OUTBOX_CONTENT)) {
            bean.outbox_content = object.getString(OUTBOX_CONTENT);
        }
        if (object.has(COMMENT_REPLY_CONTENT)) {
            bean.comment_reply_content = object.getString(COMMENT_REPLY_CONTENT);
        }
        if (object.has(USER_COMMENT_REPLY_UNREAD_COUNT)) {
            bean.user_comment_reply_unread_count = object.getString(USER_COMMENT_REPLY_UNREAD_COUNT);
        }
        if (object.has(COMMENT_REPLY_TOTAL_COUNT)) {
            bean.comment_reply_total_count = object.getString(COMMENT_REPLY_TOTAL_COUNT);
        }
        return bean;
    }
}
