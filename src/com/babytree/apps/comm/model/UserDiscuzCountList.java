
package com.babytree.apps.comm.model;

import org.json.JSONException;
import org.json.JSONObject;



public class UserDiscuzCountList extends Base {

    private static final long serialVersionUID = 1L;

    public int post_total = 0;

    public int reply_total = 0;
    
    public String encode_id;
    
    public String avatar;
    
    public String baby_age;
    
    public String full_city;
    
    public String hospital_name;
    
    public String nick_name;
    
    private static final String POST_TOTAL = "post_total";
    
    private static final String REPLY_TOTAL = "reply_total";

    private static final String ENCODE_ID = "encode_id";
    
    private static final String AVATAR = "avatar";
    
    private static final String BABY_AGE = "baby_age";
    
    private static final String FULL_CITY = "full_city";
    
    private static final String HOSPITAL_NAME = "hospital_name";
    
    private static final String NICK_NAME = "nick_name";

    public static UserDiscuzCountList parse(JSONObject jsonObject) throws JSONException {
        UserDiscuzCountList bean = new UserDiscuzCountList();
        if (jsonObject.has(POST_TOTAL)) {
            bean.post_total = getInt(jsonObject, POST_TOTAL);
        }
        if (jsonObject.has(REPLY_TOTAL)) {
            bean.reply_total = getInt(jsonObject, REPLY_TOTAL);
        }
        JSONObject  object = jsonObject.getJSONObject("user_info");
        if(object.has(ENCODE_ID)){
        	bean.encode_id = object.getString(ENCODE_ID);
        }
        if(object.has(AVATAR)){
        	bean.avatar = object.getString(AVATAR);
        }
        if(object.has(BABY_AGE)){
        	bean.baby_age = object.getString(BABY_AGE);
        }
        if(object.has(FULL_CITY)){
        	bean.full_city = object.getString(FULL_CITY);
        }
        if(object.has(HOSPITAL_NAME)){
        	bean.hospital_name = object.getString(HOSPITAL_NAME);
        }
        if(object.has(NICK_NAME)){
        	bean.nick_name = object.getString(NICK_NAME);
        }
        return bean;
    }

}
