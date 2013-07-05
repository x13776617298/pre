package com.babytree.apps.biz.home.model;

import com.babytree.apps.biz.father.model.BabytreeModel;

/**
 * 消息列表model
 * 
 * @author pengxh
 * 
 */
public class Notify extends BabytreeModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 区别类型
	 */
	private int type;

	/**
	 * 数据
	 */
	private Object data;

	public Notify() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Notify(int type, Object data) {
		super();
		this.type = type;
		this.data = data;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

}
