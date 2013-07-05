
package com.babytree.apps.comm.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Discussion {
    public int id;

    public String title = "";

    public int unread_count = 0;

    public String type = "";

    public static Discussion parse(JSONObject object) throws JSONException {
        Discussion bean = new Discussion();
        if (object.has("id")) {
            bean.id = object.getInt("id");
        }
        if (object.has("title")) {
            bean.title = object.getString("title");
        }
        if (object.has("unread_count")) {
            bean.unread_count = object.getInt("unread_count");
        }
        if (object.has("type")) {
            bean.type = object.getString("type");
        }
        return bean;
    }
}
