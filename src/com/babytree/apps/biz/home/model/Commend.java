package com.babytree.apps.biz.home.model;

import com.babytree.apps.biz.father.model.BabytreeModel;

/**
 * 宝宝树推荐 model
 * 
 * @author pengxh
 * 
 */
public class Commend extends BabytreeModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 列表id
	 */
	private String id;

	/**
	 * 列表图标icon
	 */
	private String img;

	/**
	 * 列表title
	 */
	private String title;

	/**
	 * 日期
	 */
	private long date;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String iconUrl) {
		this.img = iconUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

}
