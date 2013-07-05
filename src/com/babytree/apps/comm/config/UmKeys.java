package com.babytree.apps.comm.config;

import android.content.Context;

import com.babytree.apps.comm.util.BabytreeUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 友盟埋点统计key
 * @author wangbingqi
 *
 */
public class UmKeys {
	//------------------发帖回帖统计----------------------------
	//---------------------↓↓↓↓↓----------------------------
	/**
	 * 带图片和内容的回帖  请求发起
	 */
	public static final String REPLY_START_IMG="reply_start_img";
	/**
	 * 带内容的回帖 请求发起
	 */
	public static final String REPLY_START_CONTENT="reply_start_content";
	/**
	 * 带图片和内容的回帖 成功
	 */
	public static final String REPLY_SUCCESS_IMG="reply_success_img";
	/**
	 * 带内容的回帖 成功
	 */
	public static final String REPLY_SUCCESS_CONTENT="reply_success_content"; 
	/**
	 * 带图片和内容的回帖 失败
	 */
	public static final String REPLY_FAILD_IMG="reply_faild_img";
	/**
	 * 带内容的回帖 失败
	 */
	public static final String REPLY_FAILD_CONTENT="reply_faild_content";
	/**
	 * 带图片和内容的发帖 请求发起
	 */
	public static final String POST_START_IMG="post_start_img";
	/**
	 * 带内容的发帖 请求发起
	 */
	public static final String POST_START_CONTENT="post_start_content";
	/**
	 * 带图片和内容的发帖 成功
	 */
	public static final String POST_SUCCESS_IMG="post_success_img";
	/**
	 * 带内容的发帖 成功
	 */
	public static final String POST_SUCCESS_CONTENT="post_success_content"; 
	/**
	 * 带图片和内容的发帖 失败
	 */
	public static final String POST_FAILD_IMG="post_faild_img";
	/**
	 * 带内容的发帖 失败
	 */
	public static final String POST_FAILD_CONTENT="post_faild_content";
	//---------------------↑↑↑↑↑----------------------------
	//------------------发帖回帖统计----------------------------
	
	
	
	
	/**
	 * 友盟埋点
	 * @param key       key 
	 */
	public static void UMonEvent(Context context,String key){
		//网络信息
		String str = BabytreeUtil.getExtraInfo(context);
		MobclickAgent.onEvent(context,key,str);
	}
}
