package com.babytree.apps.biz.topic.model.n;

import java.util.ArrayList;

import com.babytree.apps.biz.topic.model.Discussion;
import com.babytree.apps.biz.topic.model.Node;

/**
 * 新的帖子详情数据 包括帖子主题 跟 详情node
 * 
 * @author wangbingqi
 * 
 */
public class TopicNewBean {
	/**
	 * 帖子主题
	 */
	public Discussion discussion;
	/**
	 * 详情
	 */
	public ArrayList<Node> nodeList = new ArrayList<Node>();
}
