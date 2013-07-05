package com.babytree.apps.comm.model;
/**
 * 对话消息数据
 * @author Administrator
 *
 */
public class SessionMessageListBean extends Base{

	/**
	 * 消息ID
	 */
	public String message_id="";
	/**
	 * 昵称
	 */
	public String nickname="";
	/**
	 * 消息创建时间
	 */
	public String last_ts="";
	/**
	 * 内容
	 */
	public String content="";
	/**
	 * 用户ID
	 */
	public String user_encode_id="";
	/**
	 * 用户头像
	 */
	public String user_avatar="";
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof SessionMessageListBean) {
			SessionMessageListBean d = (SessionMessageListBean) o;
			if (d.message_id .equalsIgnoreCase( message_id )) {
				return true;
			}
		}
		return false;
	}
}
