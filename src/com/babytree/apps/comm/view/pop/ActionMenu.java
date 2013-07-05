package com.babytree.apps.comm.view.pop;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;

public class ActionMenu {

	private Context mContext;
	private OnPopMenuItemListener onPopMenuItemListener;
	private PopupWindow popWindow;

	private int textColorId = -1;
	private float textSize = -1f;

	/**
	 * 标记popMenu中最后一项是否为隐藏/显示配置项
	 */
	boolean lastHasConfig = false;
	/**
	 * 菜单间隔线
	 */
	private Drawable drawableMenuItemLine;

	/**
	 * pop菜单背景
	 */
	private Drawable drawablePopBackground;

	/**
	 * popMenu Item背景
	 */
	private Drawable drawableMenuItemBg;

	/**
	 * ActionItemView对象列表
	 */
	private ArrayList<ActionItemView> mActionItemViews = new ArrayList<ActionItemView>();

	/**
	 * ActionItem对象列表
	 */
	private ArrayList<ActionItem> mActionItems = new ArrayList<ActionItem>();

	/**
	 * 需要隐藏显示的配对配置
	 */
	private ArrayList<ActionConfig> configs = new ArrayList<ActionConfig>();

	public ActionMenu(Context baseContext, ArrayList<ActionItem> mActionItems) {
		mContext = baseContext;
		this.mActionItems = mActionItems;
	}

	/**
	 * 设置popMenu Item间隔线条
	 * 
	 * @param drawableId
	 */
	public void setMenuItemLine(int drawableId) {
		Drawable d = mContext.getResources().getDrawable(drawableId);
		if (d != null) {
			drawableMenuItemLine = d;
		}
	}

	/**
	 * 设置popMenu菜单背景
	 * 
	 * @param drawableId
	 */
	public void setPopMenuBackground(int drawableId) {
		Drawable d = mContext.getResources().getDrawable(drawableId);
		if (d != null) {
			drawablePopBackground = d;
		}
	}

	/**
	 * 设置popMenu Item项背景
	 * 
	 * @param drawableId
	 */
	public void setMenuItemBackgroundResource(int drawableId) {
		Drawable d = mContext.getResources().getDrawable(drawableId);
		if (d != null) {
			drawableMenuItemBg = d;
		}
	}

	/**
	 * 设置popMenu Item项字体颜色
	 * 
	 * 若新版SDK提示错误，请添加注解@SuppressLint("ResourceAsColor")
	 * 
	 * @param textColorId
	 *            字体颜色
	 */
	public void setMenuItemTextColor(int textColorId) {
		this.textColorId = textColorId;
	}

	/**
	 * 设置popMenu Item项字体大小
	 * 
	 * @param textSize
	 *            字体大小
	 */
	public void setMenuItemTextSize(float textSize) {
		this.textSize = textSize;
	}

	/**
	 * 设置需要显示/隐藏 popMenu Item的配置
	 * 
	 * @param configs
	 *            隐藏/显示 配置信息
	 * @param lastHasConfig
	 *            标记popMenu中最后一项是否为隐藏/显示配置项
	 */
	public void addVisibleGoneConfig(ArrayList<ActionConfig> configs) {
		this.configs = configs;
		int max = -1;
		for (ActionConfig config : configs) {
			max = (max < config.itemVisPosition) ? config.itemVisPosition : max;
			max = (max < config.itemGonePosition) ? config.itemGonePosition : max;
		}

		this.lastHasConfig = (max == (this.mActionItems.size() - 1)) ? true : false;
	}

	/**
	 * 清空所有ActionItem
	 */
	public void clearAllActionItems() {
		if (!mActionItems.isEmpty()) {
			mActionItems.clear();
		}
	}

	/**
	 * 显示pop菜单
	 * 
	 * @param anchor
	 */
	public void show(View anchor) {
		if (!popWindow.isShowing() && popWindow != null) {
			popWindow.showAsDropDown(anchor);
		} else {
			popWindow.dismiss();
		}
	}

	/**
	 * 显示pop菜单
	 * 
	 * @param anchor
	 */
	public void show(View anchor, int xoff, int yoff) {
		if (!popWindow.isShowing() && popWindow != null) {
			popWindow.showAsDropDown(anchor, xoff, yoff);
		} else {
			popWindow.dismiss();
		}
	}

	/**
	 * 隐藏pop菜单
	 * 
	 * @param anchor
	 */
	public void dismiss() {
		if (popWindow.isShowing() && popWindow != null) {
			popWindow.dismiss();
		}
	}

	/**
	 * 刷新Item的左侧图片Drawable
	 * 
	 * @param actionItemView
	 * @param drawableId
	 */
	public void refreshItem(final ActionItemView actionItemView, int drawableId,String title) {
		final ActionItem actionItem = actionItemView.actionItem;
		Drawable d = mContext.getResources().getDrawable(drawableId);
		if (d != null) {
			actionItem.mDrawable = d;
		}
		if(!TextUtils.isEmpty(title)){
			actionItem.mTitle=title;
		}
		// 防止popMenu闪烁，此处在子线程刷新对应的值
		new Handler().post(new Runnable() {

			@Override
			public void run() {
				actionItemView.refresh(actionItem);
			}
		});
	}
	
	
	

	/**
	 * 刷新显示/隐藏状态
	 */
	private void refresh() {

		// 有隐藏/显示配置信息的就进行刷新操作
		for (ActionConfig config : configs) {
			boolean b1 = (config.itemGonePosition <= (mActionItems.size() - 1))
					&& (config.itemGonePosition <= (mActionItems.size() - 1));
			boolean b2 = (config.itemVisPosition != -1) && (config.itemGonePosition != -1);
			boolean b3 = config.itemVisPosition != config.itemGonePosition;
			if (b1 && b2 && b3) {
				LinearLayout layout = getContentView();
				if (layout != null) {
					// 隐藏itemView
					ActionItemView viewVis = getItemViewFirst(config.itemVisPosition);
					viewVis.setVisibility(View.VISIBLE);
					ActionItemView viewGone = getItemViewFirst(config.itemGonePosition);
					viewGone.setVisibility(View.GONE);

					if (drawableMenuItemLine != null) { // 有间隔线刷新处理
						// 获取间隔线的索引
						int visIndex = viewVis.actionItem.mIndex + 1;
						int goneIndex = viewGone.actionItem.mIndex + 1;
						// 隐藏间隔线
						getContentViewAt(visIndex).setVisibility(View.VISIBLE);
						getContentViewAt(goneIndex).setVisibility(View.GONE);
						// 隐藏最底部的间隔线
						int count = layout.getChildCount();
						if (getContentViewAt(count - 1).getVisibility() == View.VISIBLE) {
							getContentViewAt(count - 1).setVisibility(View.GONE);
						}

						// if (viewVis.actionItem.mIndex == (count - 3) ||
						// viewGone.actionItem.mIndex == (count - 3)) { //
						// 最后一项是VGConfig项
						// getContentViewAt(viewVis.actionItem.mIndex).setVisibility(View.GONE);
						// getContentViewAt(viewGone.actionItem.mIndex).setVisibility(View.GONE);
						// }

						if (lastHasConfig) {// 最后一项为隐藏/显示配置项
							getContentViewAt(count - 1).setVisibility(View.GONE);
							getContentViewAt(count - 3).setVisibility(View.GONE);
						} else {// 最后一项不是VGConfig项
							if (visIndex == (count - 1)) {
								getContentViewAt(visIndex).setVisibility(View.GONE);
							}
						}

					}

				}
			}
		}

	}

	/**
	 * 获取popMenu中的ActionItemView
	 * 
	 * @param position
	 * @return
	 */
	private ActionItemView getItemViewFirst(int position) {
		if (getContentView() != null) {
			int count = getContentView().getChildCount();
			for (int i = 0; i < count; i++) {
				View itemView = getContentView().getChildAt(i);
				if (itemView instanceof ActionItemView && itemView.getId() == position) {
					return (ActionItemView) itemView;
				}
			}
		}
		return null;
	}

	/**
	 * @param position
	 * @return
	 */
	private View getContentViewAt(int position) {
		if (getContentView() != null) {
			int count = getContentView().getChildCount();
			if (position > 0 || position < count) {
				View itemView = getContentView().getChildAt(position);
				return itemView;
			}
		}
		return null;
	}

	/**
	 * 获得popMenu的ContentView
	 * 
	 * @return
	 */
	private LinearLayout getContentView() {
		if (popWindow != null) {
			return (LinearLayout) ((ScrollView) popWindow.getContentView()).getChildAt(0);
		}
		return null;
	}

	/**
	 * 初始化pop菜单
	 */
	public void iniPopMenu() {
		if (popWindow == null) {
			initPopData();
			refresh();
		}
	}

	/**
	 * 
	 */
	private void initPopData() {
		// 填装popMenu的ActionItemView
		LinearLayout contentView = new LinearLayout(mContext);
		contentView.setOrientation(LinearLayout.VERTICAL);

		// 索引index
		int index = 0;
		int items = mActionItems.size();
		for (int i = 0; i < items; i++) {
			final ActionItem actionItem = mActionItems.get(i);
			actionItem.setIndex(index);// 设置item在布局容器中的索引index
			final ActionItemView actionItemView = new ActionItemView(mContext, actionItem);
			actionItemView.actionItem = actionItem;// 给itemView的数据ActionItem设置一个值
			if (textColorId != -1) {
				actionItemView.setTextColor(mContext.getResources().getColor(textColorId));
			}
			if (textSize != -1) {
				actionItemView.setTextSize(textSize);
			}

			if (drawableMenuItemBg != null) {
				actionItemView.setBackgroundDrawable(drawableMenuItemBg);
			}
			// 点击事件
			actionItemView.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					// 单击事件
					if (onPopMenuItemListener != null) {
						onPopMenuItemListener.onClickItem(actionItemView, actionItem, actionItemView.getId());
					}
					// 显示隐藏事件
					for (ActionConfig config : configs) {
						ActionItem item = ((ActionItemView) v).actionItem;
						if (item.mPosition == config.itemVisPosition || item.mPosition == config.itemGonePosition) {
							int tmp;
							tmp = config.itemVisPosition;
							config.itemVisPosition = config.itemGonePosition;
							config.itemGonePosition = tmp;
						}
						
					}
					dismiss();
					// 刷新显示状态
					refresh();
				}
			});
			// 长按事件
			actionItemView.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					if (onPopMenuItemListener != null) {
						onPopMenuItemListener.onLongClickItem(actionItemView, actionItem, actionItemView.getId());
						dismiss();
						return true;
					}
					return false;
				}
			});
			mActionItemViews.add(actionItemView);
			index++;
			contentView.addView(actionItemView);// 添加内容itemView
			// 添加间隔线
			if (drawableMenuItemLine != null) {
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
						LinearLayout.LayoutParams.WRAP_CONTENT);
				ImageView lineView = new ImageView(mContext);
				lineView.setLayoutParams(params);
				lineView.setImageDrawable(drawableMenuItemLine);
				index++;
				contentView.addView(lineView);// 添加间隔线
			}
		}
		ScrollView contentViewL = new ScrollView(mContext);
		if (drawablePopBackground != null) {
			contentViewL.setBackgroundDrawable(drawablePopBackground);
		}
//		contentViewL.setOverScrollMode(View.OVER_SCROLL_NEVER);
		if (Build.VERSION.SDK_INT >= 9) {// 2.3
			contentViewL.setHorizontalScrollBarEnabled(false);
			contentViewL.setVerticalScrollBarEnabled(false);
		}
		contentViewL.setHorizontalScrollBarEnabled(false);
		contentViewL.setVerticalScrollBarEnabled(false);
		contentViewL.addView(contentView);
		PopupWindow popWindow = new PopupWindow(contentViewL, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		popWindow.setFocusable(true);
		popWindow.setBackgroundDrawable(new BitmapDrawable());
		popWindow.update();
		this.popWindow = popWindow;
	}

	/**
	 * 设置popMenu菜单操作回调
	 */
	public void setOnMenuItemListener(OnPopMenuItemListener onPopMenuItemListener) {
		this.onPopMenuItemListener = onPopMenuItemListener;
	}

	/**
	 * popMenu item的操作监听器
	 * 
	 * @author pengxh
	 * 
	 */
	public interface OnPopMenuItemListener {

		/**
		 * 单击pop内部的item
		 * 
		 * @param actionItemView
		 *            item对应的actionItemView
		 * @param actionItem
		 *            item对象
		 * @param position
		 *            item条目初始化的position,从0开始递增
		 */
		void onClickItem(ActionItemView actionItemView, ActionItem actionItem, int position);

		/**
		 * 
		 * 长按pop内部的item
		 * 
		 * @param actionItemView
		 *            item对应的actionItemView
		 * @param actionItem
		 *            item对象
		 * @param position
		 *            item条目初始化的position,从0开始递增
		 */
		void onLongClickItem(ActionItemView actionItemView, ActionItem actionItem, int position);
	}
}
