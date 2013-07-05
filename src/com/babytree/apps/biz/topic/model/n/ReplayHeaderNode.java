package com.babytree.apps.biz.topic.model.n;

import com.babytree.apps.biz.topic.model.Node;
import com.babytree.apps.biz.topic.model.PositionUser;
import com.babytree.apps.biz.topic.model.UserInfo;

/**
 * 回复头node
 * 
 * @author wangbingqi
 * 
 */
public class ReplayHeaderNode extends Node {

	private static final long serialVersionUID = 1L;
	/**
	 * 地点
	 */
	public String city_name = "";
	/**
	 * 发表时间
	 */
	public String create_ts = "0";
	/**
	 * 楼层
	 */
	public String floor = "0";

	/**
	 * 用户信息
	 */
	public UserInfo user_info;
	/**
	 * 回复楼层，默认为楼主0层
	 */
	public String position = "0";
	/**
	 * 当前楼层的ID
	 */
	public String reply_id = "0";

	/**
	 * 回复楼层 用户信息
	 */
	public PositionUser position_user;
	
	/**
	 * 是否是楼主
	 */
	public boolean is_author;
}
