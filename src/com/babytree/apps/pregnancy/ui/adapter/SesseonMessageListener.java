package com.babytree.apps.pregnancy.ui.adapter;

import com.babytree.apps.comm.model.SessionMessageListBean;

/**
 * 对话消息监听
 * @author Administrator
 *
 */
public interface SesseonMessageListener {
	/**
	 * 长按消息对话框
	 * @param user_encode_id
	 */
	public void onClickTextView(SessionMessageListBean msg);
	/**
	 * 点击消息头像
	 * @param user_encode_id
	 */
	public void onClickImageView(String user_encode_id);
}
