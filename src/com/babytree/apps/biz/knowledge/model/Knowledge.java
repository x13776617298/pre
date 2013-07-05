package com.babytree.apps.biz.knowledge.model;

import java.util.ArrayList;

import com.babytree.apps.comm.model.Base;

/**
 * 孕期知识
 *
 */
public class Knowledge extends Base {

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

	public String type_name;

	public ArrayList<Knowledge> list = new ArrayList<Knowledge>();

	@Override
	public String toString() {
		return "Knowledge [title=" + title + ", status=" + status + ", topics=" + topics + ", list=" + list + "]";
	}
	
}
