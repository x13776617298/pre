package com.babytree.apps.biz.knowledge.ctr;

import java.util.ArrayList;
import java.util.List;

import com.babytree.apps.biz.knowledge.model.Y_Knowledge;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.db.Y_CalendarDbAdapter;
import com.babytree.apps.comm.tools.BabytreeLog;

import android.database.Cursor;
import android.util.Pair;

public class Y_CalendarDbController {
	private static final String TABLE_NAME_KNOWLEDGE = "knowledge";
	private Y_CalendarDbAdapter mCalendarDbAdapter;

	public Y_CalendarDbController(Y_CalendarDbAdapter calendarDbAdapter) {
		mCalendarDbAdapter = calendarDbAdapter;
	}

	public List<Pair<String, ArrayList<Y_Knowledge>>> getWeekList() {
		List<Pair<String, ArrayList<Y_Knowledge>>> list = new ArrayList<Pair<String, ArrayList<Y_Knowledge>>>();
		ArrayList<Y_Knowledge> knowledge = getKnowledgeList();
		for (int i = 1; i <= 52; i++) {
			if (getPair(knowledge, i).second.size() > 0) {
				list.add(getPair(knowledge, i));
			}
		}
		return list;
	}

	public Pair<String, ArrayList<Y_Knowledge>> getPair(ArrayList<Y_Knowledge> knowledge, int index) {
		ArrayList<Y_Knowledge> list = new ArrayList<Y_Knowledge>();
		int count = knowledge.size();
		for (int i = 0; i < count; i++) {
			int day = knowledge.get(i).days_number;
			if (index <= 51) {
				if (day > (index - 1) * 7 && day <= index * 7) {
					list.add(knowledge.get(i));
				}
			} else {
				if (day > (index - 1) * 6 && day <= index * 6) {
					list.add(knowledge.get(i));
				}
			}

		}
		String str;
		// if(index == 1){
		// str = "备孕周";
		// }else{
		str = "  婴儿期第" + index + "周";
		// }
		return new Pair<String, ArrayList<Y_Knowledge>>(str, list);
	}

	public ArrayList<Y_Knowledge> getKnowledgeList() {
		ArrayList<Y_Knowledge> ret = new ArrayList<Y_Knowledge>();

		String sql = "SELECT _id,days_number FROM " + TABLE_NAME_KNOWLEDGE
				+ "  GROUP BY days_number ORDER BY days_number ASC";
		Cursor cursor = mCalendarDbAdapter.rawQuery(sql, null);

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Y_Knowledge item = new Y_Knowledge();
			item._id = cursor.getInt(0);
			item.days_number = cursor.getInt(1);
			String sqlTemp = "SELECT *  FROM " + TABLE_NAME_KNOWLEDGE + " WHERE days_number=" + item.days_number
					+ " ORDER BY _id ASC";
			Cursor cursorTemp = mCalendarDbAdapter.rawQuery(sqlTemp, null);
			ArrayList<Y_Knowledge> knowList = new ArrayList<Y_Knowledge>();
			for (cursorTemp.moveToFirst(); !cursorTemp.isAfterLast(); cursorTemp.moveToNext()) {
				Y_Knowledge itemTemp = new Y_Knowledge();
				itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
				itemTemp.days_number = cursorTemp.getInt(cursorTemp.getColumnIndex("days_number"));
				itemTemp.category_id = cursorTemp.getInt(cursorTemp.getColumnIndex("category_id"));
				itemTemp.title = cursorTemp.getString(cursorTemp.getColumnIndex("title"));
				itemTemp.summary_image = cursorTemp.getString(cursorTemp.getColumnIndex("summary_image"));
				itemTemp.summary_image = this.sm2mbImg(itemTemp.summary_image);
				itemTemp.summary_content = cursorTemp.getString(cursorTemp.getColumnIndex("summary_content"));
				itemTemp.type_id = cursorTemp.getInt(cursorTemp.getColumnIndex("type_id"));
				itemTemp.topics = cursorTemp.getString(cursorTemp.getColumnIndex("topics"));
				itemTemp.is_important = cursorTemp.getInt(cursorTemp.getColumnIndex("is_important"));
				itemTemp.status = cursorTemp.getInt(cursorTemp.getColumnIndex("status"));
				itemTemp.view_type = cursorTemp.getInt(cursorTemp.getColumnIndex("view_type"));
				itemTemp.sort = cursorTemp.getInt(cursorTemp.getColumnIndex("sort"));
				knowList.add(itemTemp);
			}
			cursorTemp.close();
			ret.add(item);
			item.list = knowList;
		}
		cursor.close();
		mCalendarDbAdapter.close();
		return ret;
	}

	/**
	 * 通过id查询育儿知识Y_Knowledge
	 * 
	 * @param id
	 * @return Y_Knowledge
	 */
	public Y_Knowledge getKnowledgeByID(int id) {
		Y_Knowledge itemTemp = null;
		String sql = "SELECT * FROM " + TABLE_NAME_KNOWLEDGE + " WHERE _id = " + id;
		BabytreeLog.d("getKnowledgeByID SQL = " + sql);
		try {
			Cursor cursorTemp = mCalendarDbAdapter.rawQuery(sql, null);
			if (cursorTemp.getCount() > 0) {
				cursorTemp.moveToFirst();
				itemTemp = new Y_Knowledge();
				itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
				itemTemp.days_number = cursorTemp.getInt(cursorTemp.getColumnIndex("days_number"));
				itemTemp.category_id = cursorTemp.getInt(cursorTemp.getColumnIndex("category_id"));
				itemTemp.title = cursorTemp.getString(cursorTemp.getColumnIndex("title"));
				itemTemp.summary_image = cursorTemp.getString(cursorTemp.getColumnIndex("summary_image"));
				itemTemp.summary_image = this.sm2mbImg(itemTemp.summary_image);
				itemTemp.summary_content = cursorTemp.getString(cursorTemp.getColumnIndex("summary_content"));
				itemTemp.type_id = cursorTemp.getInt(cursorTemp.getColumnIndex("type_id"));
				itemTemp.topics = cursorTemp.getString(cursorTemp.getColumnIndex("topics"));
				itemTemp.is_important = cursorTemp.getInt(cursorTemp.getColumnIndex("is_important"));
				itemTemp.status = cursorTemp.getInt(cursorTemp.getColumnIndex("status"));
				itemTemp.view_type = cursorTemp.getInt(cursorTemp.getColumnIndex("view_type"));
				itemTemp.sort = cursorTemp.getInt(cursorTemp.getColumnIndex("sort"));
			}
			cursorTemp.close();
			mCalendarDbAdapter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return itemTemp;
	}

	public ArrayList<Y_Knowledge> getKnowledgeListByDays(int days, int typeId) {
		String sqlTemp = "SELECT *  FROM " + TABLE_NAME_KNOWLEDGE + " WHERE days_number=" + days + " and type_id="
				+ typeId + " ORDER BY _id ASC";
		Cursor cursorTemp = mCalendarDbAdapter.rawQuery(sqlTemp, null);
		ArrayList<Y_Knowledge> knowList = new ArrayList<Y_Knowledge>();
		for (cursorTemp.moveToFirst(); !cursorTemp.isAfterLast(); cursorTemp.moveToNext()) {
			Y_Knowledge itemTemp = new Y_Knowledge();
			itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
			itemTemp.days_number = cursorTemp.getInt(cursorTemp.getColumnIndex("days_number"));
			itemTemp.category_id = cursorTemp.getInt(cursorTemp.getColumnIndex("category_id"));
			itemTemp.title = cursorTemp.getString(cursorTemp.getColumnIndex("title"));
			itemTemp.summary_image = cursorTemp.getString(cursorTemp.getColumnIndex("summary_image"));
			itemTemp.summary_image = this.sm2mbImg(itemTemp.summary_image);
			itemTemp.summary_content = cursorTemp.getString(cursorTemp.getColumnIndex("summary_content"));
			itemTemp.type_id = cursorTemp.getInt(cursorTemp.getColumnIndex("type_id"));
			itemTemp.topics = cursorTemp.getString(cursorTemp.getColumnIndex("topics"));
			itemTemp.is_important = cursorTemp.getInt(cursorTemp.getColumnIndex("is_important"));
			itemTemp.status = cursorTemp.getInt(cursorTemp.getColumnIndex("status"));
			itemTemp.view_type = cursorTemp.getInt(cursorTemp.getColumnIndex("view_type"));
			itemTemp.sort = cursorTemp.getInt(cursorTemp.getColumnIndex("sort"));
			knowList.add(itemTemp);
		}
		cursorTemp.close();
		mCalendarDbAdapter.close();
		return knowList;
	}

	public ArrayList<Y_Knowledge> getKnowledgeListByDays(int start, int end, int typeId) {
		String sqlTemp = "SELECT *  FROM " + TABLE_NAME_KNOWLEDGE + " WHERE days_number>" + start
				+ " and days_number<=" + end + " and type_id=" + typeId + " and is_important=1"
				+ " ORDER BY _id,is_important " + "ASC";
		Cursor cursorTemp = mCalendarDbAdapter.rawQuery(sqlTemp, null);
		ArrayList<Y_Knowledge> knowList = new ArrayList<Y_Knowledge>();
		for (cursorTemp.moveToFirst(); !cursorTemp.isAfterLast(); cursorTemp.moveToNext()) {
			Y_Knowledge itemTemp = new Y_Knowledge();
			itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
			itemTemp.days_number = cursorTemp.getInt(cursorTemp.getColumnIndex("days_number"));
			itemTemp.category_id = cursorTemp.getInt(cursorTemp.getColumnIndex("category_id"));
			itemTemp.title = cursorTemp.getString(cursorTemp.getColumnIndex("title"));
			itemTemp.summary_image = cursorTemp.getString(cursorTemp.getColumnIndex("summary_image"));
			itemTemp.summary_image = this.sm2mbImg(itemTemp.summary_image);
			itemTemp.summary_content = cursorTemp.getString(cursorTemp.getColumnIndex("summary_content"));
			itemTemp.type_id = cursorTemp.getInt(cursorTemp.getColumnIndex("type_id"));
			itemTemp.topics = cursorTemp.getString(cursorTemp.getColumnIndex("topics"));
			itemTemp.is_important = cursorTemp.getInt(cursorTemp.getColumnIndex("is_important"));
			itemTemp.status = cursorTemp.getInt(cursorTemp.getColumnIndex("status"));
			itemTemp.view_type = cursorTemp.getInt(cursorTemp.getColumnIndex("view_type"));
			itemTemp.sort = cursorTemp.getInt(cursorTemp.getColumnIndex("sort"));
			knowList.add(itemTemp);
		}
		cursorTemp.close();
		mCalendarDbAdapter.close();
		return knowList;
	}

	public List<Pair<String, ArrayList<Y_Knowledge>>> getWeekListForRemind() {
		List<Pair<String, ArrayList<Y_Knowledge>>> list = new ArrayList<Pair<String, ArrayList<Y_Knowledge>>>();
		ArrayList<Y_Knowledge> knowledge = getKnowledgeListForRemind();
		for (int i = 1; i <= 52; i++) {
			if (getPair(knowledge, i).second.size() > 0) {
				list.add(getPair(knowledge, i));
			}
		}
		return list;
	}

	public Pair<String, ArrayList<Y_Knowledge>> getPairForRemind(ArrayList<Y_Knowledge> knowledge, int index) {
		ArrayList<Y_Knowledge> list = new ArrayList<Y_Knowledge>();
		int count = knowledge.size();
		for (int i = 0; i < count; i++) {
			int day = knowledge.get(i).days_number;
			if (day > (index - 1) * 7 && day <= index * 7) {
				list.add(knowledge.get(i));
			}
		}
		String str = "怀孕第" + index + "周";
		return new Pair<String, ArrayList<Y_Knowledge>>(str, list);
	}

	public ArrayList<Y_Knowledge> getKnowledgeListForRemind() {
		String sqlTemp = "SELECT *  FROM " + TABLE_NAME_KNOWLEDGE + " where type_id=" + CommConstants.TYPE_REMIND
				+ " ORDER BY days_number ASC";
		Cursor cursorTemp = mCalendarDbAdapter.rawQuery(sqlTemp, null);
		ArrayList<Y_Knowledge> knowList = new ArrayList<Y_Knowledge>();
		for (cursorTemp.moveToFirst(); !cursorTemp.isAfterLast(); cursorTemp.moveToNext()) {
			Y_Knowledge itemTemp = new Y_Knowledge();
			itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
			itemTemp.days_number = cursorTemp.getInt(cursorTemp.getColumnIndex("days_number"));
			itemTemp.category_id = cursorTemp.getInt(cursorTemp.getColumnIndex("category_id"));
			itemTemp.title = cursorTemp.getString(cursorTemp.getColumnIndex("title"));
			itemTemp.summary_image = cursorTemp.getString(cursorTemp.getColumnIndex("summary_image"));
			itemTemp.summary_image = this.sm2mbImg(itemTemp.summary_image);
			itemTemp.summary_content = cursorTemp.getString(cursorTemp.getColumnIndex("summary_content"));
			itemTemp.type_id = cursorTemp.getInt(cursorTemp.getColumnIndex("type_id"));
			itemTemp.topics = cursorTemp.getString(cursorTemp.getColumnIndex("topics"));
			itemTemp.is_important = cursorTemp.getInt(cursorTemp.getColumnIndex("is_important"));
			itemTemp.status = cursorTemp.getInt(cursorTemp.getColumnIndex("status"));
			itemTemp.view_type = cursorTemp.getInt(cursorTemp.getColumnIndex("view_type"));
			itemTemp.sort = cursorTemp.getInt(cursorTemp.getColumnIndex("sort"));
			knowList.add(itemTemp);
		}
		cursorTemp.close();
		mCalendarDbAdapter.close();
		return knowList;
	}

	public void updateKnowledge(int _id, int status) {
		String sql = "update " + TABLE_NAME_KNOWLEDGE + " set status=" + status + " where _id=" + _id;
		mCalendarDbAdapter.execSQL(sql);

	}

	public ArrayList<Y_Knowledge> getPreEducationList() {
		ArrayList<Y_Knowledge> ret = new ArrayList<Y_Knowledge>();

		String sql = "SELECT _id,days_number FROM " + TABLE_NAME_KNOWLEDGE
				+ "  WHERE category_id=6 or category_id=8 or category_id=9 or category_id=10 ORDER BY days_number ASC";
		Cursor cursor = mCalendarDbAdapter.rawQuery(sql, null);

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Y_Knowledge item = new Y_Knowledge();
			item._id = cursor.getInt(0);
			item.days_number = cursor.getInt(1);
			String sqlTemp = "SELECT *  FROM " + TABLE_NAME_KNOWLEDGE + " WHERE days_number=" + item.days_number
					+ " ORDER BY _id ASC";
			Cursor cursorTemp = mCalendarDbAdapter.rawQuery(sqlTemp, null);
			ArrayList<Y_Knowledge> knowList = new ArrayList<Y_Knowledge>();
			for (cursorTemp.moveToFirst(); !cursorTemp.isAfterLast(); cursorTemp.moveToNext()) {
				Y_Knowledge itemTemp = new Y_Knowledge();
				itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
				itemTemp.days_number = cursorTemp.getInt(cursorTemp.getColumnIndex("days_number"));
				itemTemp.category_id = cursorTemp.getInt(cursorTemp.getColumnIndex("category_id"));
				itemTemp.title = cursorTemp.getString(cursorTemp.getColumnIndex("title"));
				itemTemp.summary_image = cursorTemp.getString(cursorTemp.getColumnIndex("summary_image"));
				itemTemp.summary_image = sm2mbImg(itemTemp.summary_image);
				itemTemp.summary_content = cursorTemp.getString(cursorTemp.getColumnIndex("summary_content"));
				itemTemp.type_id = cursorTemp.getInt(cursorTemp.getColumnIndex("type_id"));
				itemTemp.topics = cursorTemp.getString(cursorTemp.getColumnIndex("topics"));
				itemTemp.is_important = cursorTemp.getInt(cursorTemp.getColumnIndex("is_important"));
				itemTemp.status = cursorTemp.getInt(cursorTemp.getColumnIndex("status"));
				itemTemp.view_type = cursorTemp.getInt(cursorTemp.getColumnIndex("view_type"));
				itemTemp.sort = cursorTemp.getInt(cursorTemp.getColumnIndex("sort"));
				knowList.add(itemTemp);
			}
			cursorTemp.close();
			ret.add(item);
			item.list = knowList;
		}
		cursor.close();
		mCalendarDbAdapter.close();
		return ret;
	}

	private String sm2mbImg(String imgPath) {
		String str = "";
		if (imgPath != null) {
			imgPath.replace("_sm", "_mb");
		}
		return str;
	}

}
