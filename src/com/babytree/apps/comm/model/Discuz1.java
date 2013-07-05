
package com.babytree.apps.comm.model;

import org.json.JSONException;
import org.json.JSONObject;


public class Discuz1 extends Base {

    private static final long serialVersionUID = -200605627528470366L;

    public String discuz_id;

    public String response_count;
    
    public String author_response_count;
    
    public String is_fav;

    private static final String DISCUZ_ID = "id";

    private static final String RESPONSE_COUNT = "response_count";
    
    private static final String AUTHOR_RESPONSE_COUNT ="author_response_count";

    private static final String IS_FAV = "is_fav";

    public static Discuz1 parse(JSONObject jsonObject) throws JSONException {
        Discuz1 bean = new Discuz1();
        if (jsonObject.has(DISCUZ_ID)) {
            bean.discuz_id = jsonObject.getString(DISCUZ_ID);
        }
        if (jsonObject.has(RESPONSE_COUNT)) {
            bean.response_count = jsonObject.getString(RESPONSE_COUNT);
        }
        if (jsonObject.has(AUTHOR_RESPONSE_COUNT)){
        	bean.author_response_count = jsonObject.getString(AUTHOR_RESPONSE_COUNT);
        }
        if (jsonObject.has(IS_FAV)){
        	bean.is_fav = jsonObject.getString(IS_FAV);
        }
        return bean;
    }
}
