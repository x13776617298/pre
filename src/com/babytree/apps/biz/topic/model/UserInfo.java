package com.babytree.apps.biz.topic.model;

import com.babytree.apps.comm.model.Base;

/**
 * 用户信息 for 帖子详情
 * 
 * @author wangbingqi
 * 
 */
public class UserInfo extends Base {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 用户ID
	 */
	public String author_enc_user_id = "";
	/**
	 * 用户昵称
	 */
	public String author_name = "";
	/**
	 * 用户头像
	 */
	public String author_avatar = "";
	/**
	 * 用户级别
	 */
	public String user_level = "";
	/**
	 * 用户级别图像
	 */
	public String user_level_img = "";
}
