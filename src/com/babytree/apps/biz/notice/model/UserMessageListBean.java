package com.babytree.apps.biz.notice.model;

import com.babytree.apps.comm.model.Base;

/**
 * 用户消息列表Model
 * 
 */
public class UserMessageListBean extends Base {
	private static final long serialVersionUID = 1L;
	/**
	 * 昵称
	 */
	public String nickname;
	/**
	 * 最后收发时间
	 */
	public long last_ts;
	/**
	 * 最后收发内容
	 */
	public String content;
	/**
	 * 用户ID
	 */
	public String user_encode_id;
	/**
	 * 头像 url
	 */
	public String user_avatar;

	/**
	 * 未读消息数
	 */
	public int unread_count;

}
