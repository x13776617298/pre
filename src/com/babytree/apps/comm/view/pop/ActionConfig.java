package com.babytree.apps.comm.view.pop;

public class ActionConfig {

	/**
	 * 可见popMenu Item的标记position
	 */
	public int itemVisPosition = -1;

	/**
	 * 隐藏popMenu Item的标记position
	 */
	public int itemGonePosition = -1;
	

	/**
	 * 
	 * @param itemVisPosition
	 *            可见popMenu Item的标记position
	 * @param itemGonePosition
	 *            隐藏popMenu Item的标记position
	 */
	public ActionConfig(int itemVisPosition, int itemGonePosition) {
		this.itemVisPosition = itemVisPosition;
		this.itemGonePosition = itemGonePosition;
	}
}
