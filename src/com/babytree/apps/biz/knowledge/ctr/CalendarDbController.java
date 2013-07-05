package com.babytree.apps.biz.knowledge.ctr;

import java.util.ArrayList;
import java.util.List;

import android.database.Cursor;
import android.util.Pair;

import com.babytree.apps.biz.knowledge.model.Knowledge;
import com.babytree.apps.comm.config.CommConstants;
import com.babytree.apps.comm.db.CalendarDbAdapter;
import com.babytree.apps.comm.tools.BabytreeLog;

public class CalendarDbController {
	private static final String TABLE_NAME_KNOWLEDGE = "knowledge";

	private CalendarDbAdapter mCalendarDbAdapter;

	public CalendarDbController(CalendarDbAdapter calendarDbAdapter) {
		mCalendarDbAdapter = calendarDbAdapter;
	}

	/**
	 * 取知识所有数据
	 */
	public List<Pair<String, ArrayList<Knowledge>>> getWeekListForKnowledge() {
		List<Pair<String, ArrayList<Knowledge>>> list = new ArrayList<Pair<String, ArrayList<Knowledge>>>();
		ArrayList<Knowledge> knowledge = getKnowledgeList();

		for (int i = 2; i <= 41; i++) {
			Pair<String, ArrayList<Knowledge>> pair = getPairForKnowledge(knowledge, i);
			if (pair.second.size() > 0) {
				list.add(pair);
			}
		}
		return list;
	}

	public List<Pair<String, ArrayList<Knowledge>>> getWeekList() {
		List<Pair<String, ArrayList<Knowledge>>> list = new ArrayList<Pair<String, ArrayList<Knowledge>>>();
		ArrayList<Knowledge> knowledge = getKnowledgeList();
		for (int i = 1; i <= 41; i++) {
			if (getPairForRemind(knowledge, i).second.size() > 0) {
				list.add(getPairForRemind(knowledge, i));
			}
		}
		return list;
	}

	public Pair<String, ArrayList<Knowledge>> getPairForKnowledge(ArrayList<Knowledge> knowledge, int index) {
		ArrayList<Knowledge> list = new ArrayList<Knowledge>();
		int count = knowledge.size();
		if (index == 2) {
			ArrayList<Knowledge> listBeiYun = new ArrayList<Knowledge>();
			Knowledge knowledgeBeiYun = new Knowledge();
			knowledgeBeiYun.category_id = 1;
			knowledgeBeiYun.type_id = 1;
			knowledgeBeiYun.summary_content = "欢迎使用宝宝树快乐孕期，快乐孕期的内容从怀孕3周开始。如果您还没有怀孕，可以点击此处查看备孕知识。";
			listBeiYun.add(knowledgeBeiYun);
			knowledgeBeiYun.list = listBeiYun;
			list.add(knowledgeBeiYun);
		} else {
			for (int i = 0; i < count; i++) {
				int day = knowledge.get(i).days_number;
				if (day > (index - 1) * 7 && day <= index * 7) {
					list.add(knowledge.get(i));
				}
			}
		}
		String str;
		if (index == 2) {
			str = "备孕期";
		} else {
			str = "  孕" + (index - 1) + "周";
		}
		return new Pair<String, ArrayList<Knowledge>>(str, list);
	}

	public Pair<String, ArrayList<Knowledge>> getPair(ArrayList<Knowledge> knowledge, int index) {
		ArrayList<Knowledge> list = new ArrayList<Knowledge>();
		int count = knowledge.size();
		for (int i = 0; i < count; i++) {
			int day = knowledge.get(i).days_number;
			if (day > (index - 1) * 7 && day <= index * 7) {
				list.add(knowledge.get(i));
			}
		}
		String str;
		if (index == 1) {
			str = "备孕周";
		} else {
			str = "  孕" + (index - 1) + "周";
		}
		return new Pair<String, ArrayList<Knowledge>>(str, list);
	}

	public ArrayList<Knowledge> getKnowledgeList() {
		ArrayList<Knowledge> ret = new ArrayList<Knowledge>();

		try {

			String sql = "SELECT _id,days_number FROM " + TABLE_NAME_KNOWLEDGE
					+ " where days_number>21 GROUP BY days_number ORDER BY days_number ASC";
			Cursor cursor = mCalendarDbAdapter.rawQuery(sql, null);

			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				Knowledge item = new Knowledge();
				item._id = cursor.getInt(0);
				item.days_number = cursor.getInt(1);
				String sqlTemp = "SELECT *  FROM " + TABLE_NAME_KNOWLEDGE + " WHERE days_number=" + item.days_number
						+ " ORDER BY _id ASC";
				Cursor cursorTemp = mCalendarDbAdapter.rawQuery(sqlTemp, null);
				ArrayList<Knowledge> knowList = new ArrayList<Knowledge>();
				for (cursorTemp.moveToFirst(); !cursorTemp.isAfterLast(); cursorTemp.moveToNext()) {
					Knowledge itemTemp = new Knowledge();
					itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
					itemTemp.days_number = cursorTemp.getInt(cursorTemp.getColumnIndex("days_number"));
					itemTemp.category_id = cursorTemp.getInt(cursorTemp.getColumnIndex("category_id"));
					itemTemp.title = cursorTemp.getString(cursorTemp.getColumnIndex("title"));
					itemTemp.summary_image = cursorTemp.getString(cursorTemp.getColumnIndex("summary_image"));
					itemTemp.summary_image = mb2smImg(itemTemp.summary_image);
					itemTemp.summary_content = cursorTemp.getString(cursorTemp.getColumnIndex("summary_content"));
					itemTemp.type_id = cursorTemp.getInt(cursorTemp.getColumnIndex("type_id"));
					itemTemp.topics = cursorTemp.getString(cursorTemp.getColumnIndex("topics"));
					itemTemp.is_important = cursorTemp.getInt(cursorTemp.getColumnIndex("is_important"));
					itemTemp.status = cursorTemp.getInt(cursorTemp.getColumnIndex("status"));
					knowList.add(itemTemp);
				}
				cursorTemp.close();
				ret.add(item);
				item.list = knowList;
			}
			cursor.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			mCalendarDbAdapter.close();
		}
		return ret;
	}

	public Knowledge getKnowledgeByID(int id) {
		Knowledge itemTemp = null;
		String sql = "SELECT * FROM " + TABLE_NAME_KNOWLEDGE + " WHERE _id = " + id;
		BabytreeLog.d("getKnowledgeByID SQL = " + sql);
		try {
			Cursor cursorTemp = mCalendarDbAdapter.rawQuery(sql, null);
			if (cursorTemp.getCount() > 0) {
				cursorTemp.moveToFirst();
				itemTemp = new Knowledge();
				itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
				itemTemp.days_number = cursorTemp.getInt(cursorTemp.getColumnIndex("days_number"));
				itemTemp.category_id = cursorTemp.getInt(cursorTemp.getColumnIndex("category_id"));
				itemTemp.title = cursorTemp.getString(cursorTemp.getColumnIndex("title"));
				itemTemp.summary_image = cursorTemp.getString(cursorTemp.getColumnIndex("summary_image"));
				itemTemp.summary_image = mb2smImg(itemTemp.summary_image);
				itemTemp.summary_content = cursorTemp.getString(cursorTemp.getColumnIndex("summary_content"));
				itemTemp.type_id = cursorTemp.getInt(cursorTemp.getColumnIndex("type_id"));
				itemTemp.topics = cursorTemp.getString(cursorTemp.getColumnIndex("topics"));
				itemTemp.is_important = cursorTemp.getInt(cursorTemp.getColumnIndex("is_important"));
				itemTemp.status = cursorTemp.getInt(cursorTemp.getColumnIndex("status"));
				itemTemp.type_name = cursorTemp.getString(cursorTemp.getColumnIndex("type_name"));
			}
			cursorTemp.close();
			mCalendarDbAdapter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return itemTemp;
	}

	/**
	 * 根据怀孕天数查找知识信息
	 * 
	 * @author wangshuaibo
	 * @param daynum
	 *            怀孕天数
	 * @return
	 */
	public Knowledge getKnowledgeByDaysNumber(int daynum) {
		Knowledge itemTemp = null;
		String sql = "SELECT * FROM " + TABLE_NAME_KNOWLEDGE + " WHERE days_number = " + daynum;
		BabytreeLog.d("getKnowledgeByID SQL = " + sql);
		try {
			Cursor cursorTemp = mCalendarDbAdapter.rawQuery(sql, null);
			if (cursorTemp.getCount() > 0) {
				cursorTemp.moveToFirst();
				itemTemp = new Knowledge();
				itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
				itemTemp.days_number = cursorTemp.getInt(cursorTemp.getColumnIndex("days_number"));
				itemTemp.category_id = cursorTemp.getInt(cursorTemp.getColumnIndex("category_id"));
				itemTemp.title = cursorTemp.getString(cursorTemp.getColumnIndex("title"));
				itemTemp.summary_image = cursorTemp.getString(cursorTemp.getColumnIndex("summary_image"));
				itemTemp.summary_image = mb2smImg(itemTemp.summary_image);
				itemTemp.summary_content = cursorTemp.getString(cursorTemp.getColumnIndex("summary_content"));
				itemTemp.type_id = cursorTemp.getInt(cursorTemp.getColumnIndex("type_id"));
				itemTemp.topics = cursorTemp.getString(cursorTemp.getColumnIndex("topics"));
				itemTemp.is_important = cursorTemp.getInt(cursorTemp.getColumnIndex("is_important"));
				itemTemp.status = cursorTemp.getInt(cursorTemp.getColumnIndex("status"));
				itemTemp.type_name = cursorTemp.getString(cursorTemp.getColumnIndex("type_name"));
			}
			cursorTemp.close();
			mCalendarDbAdapter.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		return itemTemp;
	}

	public ArrayList<Knowledge> getKnowledgeListForType(String type_name) {
		ArrayList<Knowledge> ret = new ArrayList<Knowledge>();
		String sqlTemp = "SELECT *  FROM " + TABLE_NAME_KNOWLEDGE + " WHERE type_name=" + "'" + type_name + "'"
				+ " ORDER BY _id ASC";
		Cursor cursorTemp = mCalendarDbAdapter.rawQuery(sqlTemp, null);
		for (cursorTemp.moveToFirst(); !cursorTemp.isAfterLast(); cursorTemp.moveToNext()) {
			Knowledge itemTemp = new Knowledge();
			itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
			itemTemp.days_number = cursorTemp.getInt(cursorTemp.getColumnIndex("days_number"));
			itemTemp.category_id = cursorTemp.getInt(cursorTemp.getColumnIndex("category_id"));
			itemTemp.title = cursorTemp.getString(cursorTemp.getColumnIndex("title"));
			itemTemp.summary_image = cursorTemp.getString(cursorTemp.getColumnIndex("summary_image"));
			itemTemp.summary_image = mb2smImg(itemTemp.summary_image);
			itemTemp.summary_content = cursorTemp.getString(cursorTemp.getColumnIndex("summary_content"));
			itemTemp.type_id = cursorTemp.getInt(cursorTemp.getColumnIndex("type_id"));
			itemTemp.topics = cursorTemp.getString(cursorTemp.getColumnIndex("topics"));
			itemTemp.is_important = cursorTemp.getInt(cursorTemp.getColumnIndex("is_important"));
			itemTemp.status = cursorTemp.getInt(cursorTemp.getColumnIndex("status"));
			itemTemp.type_name = cursorTemp.getString(cursorTemp.getColumnIndex("type_name"));
			ret.add(itemTemp);
		}
		cursorTemp.close();
		mCalendarDbAdapter.close();
		return ret;
	}

	public ArrayList<String> getType() {
		ArrayList<String> ret = new ArrayList<String>();
		String sql = "SELECT _id,type_name FROM " + TABLE_NAME_KNOWLEDGE
				+ " WHERE type_name IS NOT NULL and type_name <> ''" + "  GROUP BY type_name ORDER BY days_number ASC";
		Cursor cursor = mCalendarDbAdapter.rawQuery(sql, null);
		String type_name;
		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			type_name = cursor.getString(cursor.getColumnIndex("type_name"));
			ret.add(type_name);
		}
		cursor.close();
		mCalendarDbAdapter.close();
		return ret;
	}

	public ArrayList<Knowledge> getKnowledgeListByDays(int days, int typeId) {
		String sqlTemp = "SELECT *  FROM " + TABLE_NAME_KNOWLEDGE + " WHERE days_number=" + days + " and type_id="
				+ typeId + " ORDER BY _id ASC";
		Cursor cursorTemp = mCalendarDbAdapter.rawQuery(sqlTemp, null);
		ArrayList<Knowledge> knowList = new ArrayList<Knowledge>();
		for (cursorTemp.moveToFirst(); !cursorTemp.isAfterLast(); cursorTemp.moveToNext()) {
			Knowledge itemTemp = new Knowledge();
			itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
			itemTemp.days_number = cursorTemp.getInt(cursorTemp.getColumnIndex("days_number"));
			itemTemp.category_id = cursorTemp.getInt(cursorTemp.getColumnIndex("category_id"));
			itemTemp.title = cursorTemp.getString(cursorTemp.getColumnIndex("title"));
			itemTemp.summary_image = cursorTemp.getString(cursorTemp.getColumnIndex("summary_image"));
			itemTemp.summary_image = mb2smImg(itemTemp.summary_image);
			itemTemp.summary_content = cursorTemp.getString(cursorTemp.getColumnIndex("summary_content"));
			itemTemp.type_id = cursorTemp.getInt(cursorTemp.getColumnIndex("type_id"));
			itemTemp.topics = cursorTemp.getString(cursorTemp.getColumnIndex("topics"));
			itemTemp.is_important = cursorTemp.getInt(cursorTemp.getColumnIndex("is_important"));
			itemTemp.status = cursorTemp.getInt(cursorTemp.getColumnIndex("status"));
			knowList.add(itemTemp);
		}
		cursorTemp.close();
		mCalendarDbAdapter.close();
		return knowList;
	}

	public ArrayList<Knowledge> getKnowledgeListByDays(int start, int end, int typeId) {
		String sqlTemp = "SELECT *  FROM " + TABLE_NAME_KNOWLEDGE + " WHERE days_number>" + start
				+ " and days_number<=" + end + " and type_id=" + typeId + " and is_important=1"
				+ " ORDER BY _id,is_important " + "ASC";
		Cursor cursorTemp = mCalendarDbAdapter.rawQuery(sqlTemp, null);
		ArrayList<Knowledge> knowList = new ArrayList<Knowledge>();
		for (cursorTemp.moveToFirst(); !cursorTemp.isAfterLast(); cursorTemp.moveToNext()) {
			Knowledge itemTemp = new Knowledge();
			itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
			itemTemp.days_number = cursorTemp.getInt(cursorTemp.getColumnIndex("days_number"));
			itemTemp.category_id = cursorTemp.getInt(cursorTemp.getColumnIndex("category_id"));
			itemTemp.title = cursorTemp.getString(cursorTemp.getColumnIndex("title"));
			itemTemp.summary_image = cursorTemp.getString(cursorTemp.getColumnIndex("summary_image"));
			itemTemp.summary_image = mb2smImg(itemTemp.summary_image);
			itemTemp.summary_content = cursorTemp.getString(cursorTemp.getColumnIndex("summary_content"));
			itemTemp.type_id = cursorTemp.getInt(cursorTemp.getColumnIndex("type_id"));
			itemTemp.topics = cursorTemp.getString(cursorTemp.getColumnIndex("topics"));
			itemTemp.is_important = cursorTemp.getInt(cursorTemp.getColumnIndex("is_important"));
			itemTemp.status = cursorTemp.getInt(cursorTemp.getColumnIndex("status"));
			knowList.add(itemTemp);
		}
		cursorTemp.close();
		mCalendarDbAdapter.close();
		return knowList;
	}

	public List<Pair<String, ArrayList<Knowledge>>> getWeekListForRemind() {
		List<Pair<String, ArrayList<Knowledge>>> list = new ArrayList<Pair<String, ArrayList<Knowledge>>>();
		ArrayList<Knowledge> knowledge = getKnowledgeListForRemind();
		for (int i = 1; i <= 41; i++) {
			if (getPairForRemind(knowledge, i).second.size() > 0) {
				list.add(getPairForRemind(knowledge, i));
			}
		}
		return list;
	}

	public Pair<String, ArrayList<Knowledge>> getPairForRemind(ArrayList<Knowledge> knowledge, int index) {
		ArrayList<Knowledge> list = new ArrayList<Knowledge>();
		int count = knowledge.size();
		for (int i = 0; i < count; i++) {
			int day = knowledge.get(i).days_number;
			if (day > (index - 1) * 7 && day <= index * 7) {
				list.add(knowledge.get(i));
			}
		}
		String str = "";
		if (index == 1) {
			str = "备孕周";
		} else {
			str = "  孕" + (index - 1) + "周";
		}
		return new Pair<String, ArrayList<Knowledge>>(str, list);
	}

	public ArrayList<Knowledge> getKnowledgeListForRemind() {
		String sqlTemp = "SELECT *  FROM " + TABLE_NAME_KNOWLEDGE + " where type_id=" + CommConstants.TYPE_REMIND
				+ " ORDER BY days_number ASC";
		Cursor cursorTemp = mCalendarDbAdapter.rawQuery(sqlTemp, null);
		ArrayList<Knowledge> knowList = new ArrayList<Knowledge>();
		for (cursorTemp.moveToFirst(); !cursorTemp.isAfterLast(); cursorTemp.moveToNext()) {
			Knowledge itemTemp = new Knowledge();
			itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
			itemTemp.days_number = cursorTemp.getInt(cursorTemp.getColumnIndex("days_number"));
			itemTemp.category_id = cursorTemp.getInt(cursorTemp.getColumnIndex("category_id"));
			itemTemp.title = cursorTemp.getString(cursorTemp.getColumnIndex("title"));
			itemTemp.summary_image = cursorTemp.getString(cursorTemp.getColumnIndex("summary_image"));
			itemTemp.summary_image = mb2smImg(itemTemp.summary_image);
			itemTemp.summary_content = cursorTemp.getString(cursorTemp.getColumnIndex("summary_content"));
			itemTemp.type_id = cursorTemp.getInt(cursorTemp.getColumnIndex("type_id"));
			itemTemp.topics = cursorTemp.getString(cursorTemp.getColumnIndex("topics"));
			itemTemp.is_important = cursorTemp.getInt(cursorTemp.getColumnIndex("is_important"));
			itemTemp.status = cursorTemp.getInt(cursorTemp.getColumnIndex("status"));
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

	public ArrayList<Knowledge> getPreEducationList() {
		ArrayList<Knowledge> ret = new ArrayList<Knowledge>();

		String sql = "SELECT _id,days_number FROM " + TABLE_NAME_KNOWLEDGE
				+ "  WHERE category_id=6 or category_id=8 or category_id=9 or category_id=10 ORDER BY days_number ASC";
		Cursor cursor = mCalendarDbAdapter.rawQuery(sql, null);

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
			Knowledge item = new Knowledge();
			item._id = cursor.getInt(0);
			item.days_number = cursor.getInt(1);
			String sqlTemp = "SELECT *  FROM " + TABLE_NAME_KNOWLEDGE + " WHERE days_number=" + item.days_number
					+ " ORDER BY _id ASC";
			Cursor cursorTemp = mCalendarDbAdapter.rawQuery(sqlTemp, null);
			ArrayList<Knowledge> knowList = new ArrayList<Knowledge>();
			for (cursorTemp.moveToFirst(); !cursorTemp.isAfterLast(); cursorTemp.moveToNext()) {
				Knowledge itemTemp = new Knowledge();
				itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
				itemTemp.days_number = cursorTemp.getInt(cursorTemp.getColumnIndex("days_number"));
				itemTemp.category_id = cursorTemp.getInt(cursorTemp.getColumnIndex("category_id"));
				itemTemp.title = cursorTemp.getString(cursorTemp.getColumnIndex("title"));
				itemTemp.summary_image = cursorTemp.getString(cursorTemp.getColumnIndex("summary_image"));
				itemTemp.summary_image = mb2smImg(itemTemp.summary_image);
				itemTemp.summary_content = cursorTemp.getString(cursorTemp.getColumnIndex("summary_content"));
				itemTemp.type_id = cursorTemp.getInt(cursorTemp.getColumnIndex("type_id"));
				itemTemp.topics = cursorTemp.getString(cursorTemp.getColumnIndex("topics"));
				itemTemp.is_important = cursorTemp.getInt(cursorTemp.getColumnIndex("is_important"));
				itemTemp.status = cursorTemp.getInt(cursorTemp.getColumnIndex("status"));
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

	private String mb2smImg(String imgPath) {
		String str = "";
		if (imgPath != null) {
			str = imgPath.replace("_sm", "_mb");
		}
		return str;
	}
}
