package com.babytree.apps.comm.model;




import org.json.JSONException;
import org.json.JSONObject;

import com.babytree.apps.comm.model.Base;

public class Message extends Base {

    private static final long serialVersionUID = -3301534080552506899L;

    public String uid = "";

    public String avatar = "";

    public String nickname = "";

    public long createTs = 0;

    public int parent_id = 0;

    public boolean replied = false;

    public String content = "";

    public boolean unread = false;

    public final static String ID = "id";

    public final static String UID = "uid";

    public final static String AVATAR = "avatar";

    public final static String NICKNAME = "nickname";

    public final static String CREATE_TS = "create_ts";

    public final static String PARENT_ID = "parent_id";

    public final static String REPLIED = "replied";

    public final static String CONTENT = "content";

    public final static String UNREAD = "unread";

    public static Message parse(JSONObject jsonObject) throws JSONException {
        Message message = new Message();
        if (jsonObject.has(ID)) {
            message._id = getInt(jsonObject, ID);
        }
        if (jsonObject.has(UID)) {
            message.uid = jsonObject.getString(UID);
        }
        if (jsonObject.has(AVATAR)) {
            message.avatar = jsonObject.getString(AVATAR);
        }
        if (jsonObject.has(NICKNAME)) {
            message.nickname = jsonObject.getString(NICKNAME);
        }
        if (jsonObject.has(CREATE_TS)) {
            message.createTs = getLong(jsonObject, CREATE_TS);
        }
        if (jsonObject.has(PARENT_ID)) {
            message.parent_id = getInt(jsonObject, PARENT_ID);
        }
        if (jsonObject.has(REPLIED)) {
            message.replied = jsonObject.getBoolean(REPLIED);
        }
        if (jsonObject.has(CONTENT)) {
            message.content = jsonObject.getString(CONTENT);
        }
        if (jsonObject.has(UNREAD)) {
            message.unread = jsonObject.getBoolean(UNREAD);
        }
        return message;
    }

    @Override
    public String toString() {
        return String.valueOf(_id);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Message) {
            if (((Message)o)._id == super._id) {
                return true;
            }
        }
        return super.equals(o);
    }

}
