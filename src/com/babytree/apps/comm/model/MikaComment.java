
package com.babytree.apps.comm.model;

import org.json.JSONException;
import org.json.JSONObject;


public class MikaComment extends Base {

    private static final long serialVersionUID = 1L;

    public int id = 0;

    public String title = "";

    public String babyage = "";

    public String comment = "";
    
    public String nickname = "";

    public String content_id = "";

    public String content_type = "";


    private static final String ID = "id";

    private static final String TITLE = "title";

    private static final String BABYAGE = "babyage";
    
    private static final String COMMENT = "comment";
    
    private static final String NICKNAME = "nickname";

    private static final String CONTENT_ID = "content_id";

    private static final String CONTENT_TYPE = "content_type";

    public static MikaComment parse(JSONObject jsonObject) throws JSONException {
        MikaComment bean = new MikaComment();

        if (jsonObject.has(ID)) {
            bean.id = getInt(jsonObject, ID);
        }
        if (jsonObject.has(TITLE)) {
            bean.title = jsonObject.getString(TITLE);
        }
        if (jsonObject.has(BABYAGE)) {
            bean.babyage = jsonObject.getString(BABYAGE);
        }
        if (jsonObject.has(NICKNAME)) {
            bean.nickname = jsonObject.getString(NICKNAME);
        }
        if (jsonObject.has(COMMENT)) {
            bean.comment = jsonObject.getString(COMMENT);
        }
        if (jsonObject.has(CONTENT_ID)) {
            bean.content_id = jsonObject.getString(CONTENT_ID);
        }
        if (jsonObject.has(CONTENT_TYPE)) {
            bean.content_type = jsonObject.getString(CONTENT_TYPE);
        }
        return bean;
    }
}
