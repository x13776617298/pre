package com.babytree.apps.comm.view;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.babytree.apps.pregnancy.R;

/**
 * 弹出的菜单
 * 
 * @author pengxh
 * 
 */
public class BabyTreePopMenuView extends RelativeLayout implements View.OnClickListener {

	/**
	 * 弹出的菜单
	 */
	private PopupWindow mMenu;
	/**
	 * 菜单的listView
	 */
	private ListView mListView;
	/**
	 * 菜单内容适配器
	 */
	private PopMenuAdapter mAdapter;

	/**
	 * 列表显示内容
	 */
	private List<String> mList;

	private BabyTreePopMenuItemlistener listener;

	private Context context;
	
	
	private LinearLayout mLinearLayout;
	
	private BabyTreePopMenuView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// 暂时不留外面直接使用
	}

	/**
	 * 弹出菜单控件
	 * 
	 * @param context
	 * @param list
	 *            菜单内容
	 */
	public BabyTreePopMenuView(Context context, List<String> list, BabyTreePopMenuItemlistener listener) {
		super(context);
		this.listener = listener;
		this.context=context;
		init(context, list,null,0);
	}
	

	/**
	 * 弹出菜单控件
	 * 
	 * @param context
	 * @param list
	 *            菜单内容
	 */
	public BabyTreePopMenuView(Context context, List<String> list,int[] id,int background, BabyTreePopMenuItemlistener listener) {
		super(context);
		this.listener = listener;
		this.context=context;
		init(context, list,id,background);
	}
	

	/**
	 * 初始化
	 * @param context
	 * @param list
	 * @param id  layout
	 */
	private void init(Context context, List<String> list,int[] id,int background) {
		this.mList = list;
		View popwindow_view = null;
		popwindow_view=View.inflate(context, R.layout.topic_beta_more_menu, null);
		mLinearLayout=(LinearLayout)popwindow_view.findViewById(R.id.topic_beta_more_menu_linearlayout);
		if(background==0){
		}else{
			mLinearLayout.setBackgroundResource(background);
		}
		mMenu = new PopupWindow(popwindow_view, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		mListView = (ListView) popwindow_view.findViewById(R.id.forum_menu_listView1);

		if (mList == null) {
			mAdapter = null;
		} else {
			mAdapter = new PopMenuAdapter(context, mList,id);
		}
		mListView.setAdapter(mAdapter);

	}
	
	/**
	 * 开启菜单
	 */
	public void showPopwindow(View anchor) {
		if (mMenu != null && !mMenu.isShowing()) {
			mMenu.showAsDropDown(anchor, 3, 8);
		} else {
			mMenu.dismiss();
		}
	}

	/**
	 * 关闭菜单,需要在ondistory/onpause里面调用
	 */
	public void closePopwindow() {
		if (mMenu != null && mMenu.isShowing()) {
			mMenu.dismiss();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.txt_center:
			if (mMenu != null) {
				if (mMenu.isShowing()) {
					closePopwindow();
				}
			}
			break;
		default:
			break;
		}

	}

	class PopMenuAdapter extends BaseAdapter {
		private List<String> list;
		private Context context;
		private int[] id;

		public PopMenuAdapter(Context context, List<String> list,int[] id) {
			this.list = list;
			this.id=id;
			this.context = context;
		}

		@Override
		public int getCount() {
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			return list.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final String item = list.get(position);
			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(context).inflate(R.layout.topic_beta_more_item, null);
				viewHolder = new ViewHolder();
				viewHolder.textView = (TextView) convertView.findViewById(R.id.topic_pop_menu_item_textview);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			int icon=0;
			if(id!=null){
				icon=id[position];
			}
			
			if(icon!=0){
				viewHolder.textView.append(Html.fromHtml("<img src=\"" + icon + "\">", imageGetter, null));
			}
			viewHolder.textView.setText(item);
			viewHolder.textView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					closePopwindow();
					if (listener != null) {
						listener.onClick(position);
					}
				}
			});
			return convertView;
		}

		class ViewHolder {
			TextView textView;
		}

	}

	private ImageGetter imageGetter = new ImageGetter() {
		@Override
		public Drawable getDrawable(String source) {
			int id = Integer.parseInt(source);
			Drawable d = context.getResources().getDrawable(id);
			d.setBounds(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
			return d;
		}
	};
	
	/**
	 * 菜单列表点击监听
	 * 
	 * 
	 */
	public interface BabyTreePopMenuItemlistener {
		/**
		 * 选中的id 0~N
		 * 
		 * @param id
		 */
		void onClick(int id);
	}

}
