package com.babytree.apps.comm.ui.activity.listener;
/**
 * 关闭acitivty广播监听
 * @author wangbingqi
 *
 */
public interface BabytreeCloseListener {
	/**
	 * 注册 初始化调用
	 */
	void onCloseCreate();
	/**
	 * 注销 结束时调用
	 */
	void onCloseDestroy();
}
