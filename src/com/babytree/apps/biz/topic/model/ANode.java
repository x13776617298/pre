package com.babytree.apps.biz.topic.model;
/**
 * 超链接节点
 * @author wangbingqi
 *
 */
public class ANode extends Node{
	private static final long serialVersionUID = 1L;
	/**
	 * 站内帖子 / 站外链接
	 */
	public String type="";
	/**
	 * 帖子ID  
	 */
	public String topic_id="";
	/**
	 * 站外链接
	 */
	public String href="";
	/**
	 * 布局节点
	 */
	public Node node;
}
