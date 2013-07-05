package com.babytree.apps.comm.util;

/**
 * 知识列表 取Title
 * 
 * @author wangbingqi
 * 
 */
public final class BabytreeTitleUtil {
	private BabytreeTitleUtil() {
	}

	/**
	 * 孕期的 TITLE 标识
	 */
	public static final String[] TYPE_TITLE = { "", "你的变化", "宝宝的发育", "温馨提示",
			"注意事项", "营养食谱", "亲子时光", "给准爸爸的建议", "故事胎教", "音乐胎教", "语言胎教", "饮食与营养" };

	/**
	 * 育儿的TITLE标识
	 */
	public static final String[] Y_TYPE_TITLE = { "", "发育指标", "宝宝里程碑", "婴儿喂养",
			"婴儿护理", "游戏活动与早教", "特别关注", "妈妈生活", "新爸爸学堂", "购物指南", "胎教时光", "怀孕禁忌",
			"营养与喂养", "日常照料", "健康与安全", "", "", "", "快乐家庭" };

	/**
	 * 获取标题
	 * 
	 * @param typeId
	 * @param title
	 *            标题数组
	 * @return
	 * @author wangbingqi
	 */
	public final static String switchTitle(int categroyId, String[] title) {
		String ret = "";

		try {
			ret = title[categroyId];
		} catch (Exception e) {
		}

		return ret;
	}

}
