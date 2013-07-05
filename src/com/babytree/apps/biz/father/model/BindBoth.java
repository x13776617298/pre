package com.babytree.apps.biz.father.model;

/**
 * 绑定成功的爸爸和妈妈
 * 
 * @author pengxh
 * 
 */
public class BindBoth extends BabytreeModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 妈妈的userId
	 */
	public String mom_user_id = "";

	/**
	 * 爸爸的userId
	 */
	public String father_user_id = "";
	
	public String image_url="";

	public BindUser bindUser;

	@Override
	public String toString() {
		return "BindBoth [mom_user_id=" + mom_user_id + ", father_user_id="
				+ father_user_id + ", bindUser=" + bindUser.toString() + "]";
	}
	
}
