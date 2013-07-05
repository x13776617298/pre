package com.babytree.apps.comm.model;



import org.json.JSONException;
import org.json.JSONObject;

import com.babytree.apps.comm.model.Base;

public class TopicGroup extends Base {

    private static final long serialVersionUID = 3391531396623858948L;

    public int group_id = 0;

    public String name = "";

    public int discussion_count = 0;

    public String description = "";

    public String cover = "";

    public String type = "";

    // 标识
    public int status = 0;

    // 标题
    public String title = "";

    private static final String GROUP_ID = "id";

    private static final String NAME = "name";

    private static final String DISCUSSION_COUNT = "discussion_count";

    private static final String DECRIPTION = "description";

    private static final String COVER = "cover";

    private static final String TYPE = "type";

    public static TopicGroup parse(JSONObject jsonObject) throws JSONException {
        TopicGroup bean = new TopicGroup();
        if (jsonObject.has(GROUP_ID)) {
        	bean.group_id = getInt(jsonObject, GROUP_ID);      
        }
        if (jsonObject.has(NAME)) {
            bean.name = jsonObject.getString(NAME).trim();
        }
        if (jsonObject.has(DISCUSSION_COUNT)) {
			bean.discussion_count = getInt(jsonObject, DISCUSSION_COUNT);
        }
        if (jsonObject.has(DECRIPTION)) {
            bean.description = jsonObject.getString(DECRIPTION).trim();
        }
        if (jsonObject.has(COVER)) {
            bean.cover = jsonObject.getString(COVER).trim();
        }
        if (jsonObject.has(TYPE)) {
            bean.type = jsonObject.getString(TYPE).trim();
        }
        return bean;
    }

    @Override
    public String toString() {

        return String.valueOf(group_id);
    }

}
