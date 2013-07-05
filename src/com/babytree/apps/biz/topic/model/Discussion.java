package com.babytree.apps.biz.topic.model;

import com.babytree.apps.comm.model.Base;

/**
 * 帖子主题 for 帖子详情
 * 
 * @author wangbingqi
 * 
 */
public class Discussion extends Base {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 收藏状态
	 */
	public String is_fav = "0";
	/**
	 * 当前页数
	 */
	public String current_page = "1";
	/**
	 * 共多少页
	 */
	public String page_count = "1";
	/**
	 * 帖子标题
	 */
	public String discussion_title = "";
	/**
	 * 浏览数
	 */
	public String view_count = "0";
	/**
	 * 回复数
	 */
	public String reply_count = "0";
	/**
	 * 地点
	 */
	public String city_name = "";
	/**
	 * 发表时间
	 */
	public String create_ts = "0";
	/**
	 * 用户信息 for 帖子详情
	 */
	public UserInfo user_info;
	/**
	 * 圈子信息 for 帖子详情
	 */
	public GroupData group_data;
	/**
	 * 帖子详情 all node list
	 */
	public DiscussionContent discussion_content;

}
