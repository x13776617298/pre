package com.babytree.apps.biz.topic.model.replay;

import android.graphics.Bitmap;

/**
 * 回帖对象
 * @author wangbingqi
 *
 */
public class TopicReplayBean {

	/**
	 * 帖子ID
	 */
	public String discuzId;
	/**
	 * 回复楼层的帖子的帖子ID
	 */
	public String referId="";
	/**
	 * 楼层
	 */
	public String position="";
	/**
	 * 回复内容
	 */
	public String content="";
	/**
	 * 回复照片路径
	 */
	public String photoPaht="";
	
	/**
	 * 临时添加 备份图片
	 */
	public Bitmap bitmap;

}
