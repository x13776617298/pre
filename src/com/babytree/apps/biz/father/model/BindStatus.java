package com.babytree.apps.biz.father.model;

/**
 * 绑定状态
 * 
 * @author pengxh
 * 
 */
public class BindStatus extends BabytreeModel {

	private static final long serialVersionUID = 1L;

	/**
	 * 绑定状态/解绑状态 0:没有绑定/解绑失败，1:绑定成功/解绑成功
	 */
	private String bindStatus;
	
	/**
	 * 邀请码 注：只有"妈妈版"时才返回邀请码
	 */
	private String code;

	/**
	 * 爸爸版任务页面上面的图片
	 */
	private String imageUrl;

	public String getBindStatus() {
		return bindStatus;
	}

	public void setBindStatus(String status) {
		this.bindStatus = status;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String url) {
		this.imageUrl = url;
	}
}
