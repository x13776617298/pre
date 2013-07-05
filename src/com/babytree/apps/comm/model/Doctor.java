
package com.babytree.apps.comm.model;

import org.json.JSONException;
import org.json.JSONObject;


public class Doctor extends Base {

    private static final long serialVersionUID = -200605627528470366L;

    public String name = "";
    
    public String title = "";

    public int topic_count = 0;
    
    private static final String NAME = "name";

    private static final String TITLE = "title";

    private static final String TOPIC_COUNT = "topic_count";
    
    public static Doctor parse(JSONObject jsonObject) throws JSONException {
        Doctor bean = new Doctor();
        if (jsonObject.has(NAME)) {
            bean.name = jsonObject.getString(NAME).trim();
        }
        if (jsonObject.has(TITLE)) {
            bean.title = jsonObject.getString(TITLE).trim();
        }
        if (jsonObject.has(TOPIC_COUNT)) {
            bean.topic_count = getInt(jsonObject, TOPIC_COUNT);
        }
        return bean;
    }
}
