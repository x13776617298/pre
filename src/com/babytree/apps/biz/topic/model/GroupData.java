package com.babytree.apps.biz.topic.model;

import com.babytree.apps.comm.model.Base;

/**
 * 圈子信息 for 帖子详情
 * 
 */
public class GroupData extends Base {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 圈子ID
	 */
	public int id = 0;
	/**
	 * 圈子Title
	 */
	public String title = "";
	/**
	 * 圈子img_src
	 */
	public String img_src = "";
	/**
	 * 圈子topic_count
	 */
	public int topic_count = 0;
	/**
	 * 圈子user_count
	 */
	public int user_count = 0;

}
