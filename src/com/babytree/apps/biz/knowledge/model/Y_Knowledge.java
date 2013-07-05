package com.babytree.apps.biz.knowledge.model;

import java.util.ArrayList;

import com.babytree.apps.comm.model.Base;


public class Y_Knowledge extends Base {

	private static final long serialVersionUID = 1L;

	public int _id = 0;

	public int days_number = 0;

	public int category_id = 0;

	public String title = "";

	public String summary_image = "";

	public String summary_content = "";

	public int type_id = 0;

	public String topics = "";

	public int is_important = 0;

	public int status = 0;

	public int view_type;

	public int sort;

	public ArrayList<Y_Knowledge> list = new ArrayList<Y_Knowledge>();
}
