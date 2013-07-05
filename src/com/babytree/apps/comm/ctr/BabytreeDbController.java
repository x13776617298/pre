package com.babytree.apps.comm.ctr;

import com.babytree.apps.comm.db.DbAdapter;
import com.babytree.apps.comm.db.DbAdapterForOld;
import com.babytree.apps.comm.model.Discuz;
import com.babytree.apps.comm.model.Group;
import com.babytree.apps.comm.model.Location;
import com.babytree.apps.comm.ui.category.PinnedHeaderListViewBean;
import com.babytree.apps.comm.model.TopicGroup;

import android.content.ContentValues;
import android.database.Cursor;

import java.util.ArrayList;

public class BabytreeDbController {
	private static final String TABLE_NAME_DISCUZ = "my_discuz"; // 我喜欢的帖子
	private static final String TABLE_NAME_GROUP = "my_group"; // 我喜欢的圈子

	private static final String TABLE_NAME_LOCATION = "my_location"; // 我经常访问的地区

	private DbAdapter mDbAdapter;
	private DbAdapterForOld mDbAdapterForOld;

	public BabytreeDbController(DbAdapter dbAdapter) {
		mDbAdapter = dbAdapter;
	}

	public BabytreeDbController(DbAdapterForOld DbAdapterForOld) {
		mDbAdapterForOld = DbAdapterForOld;
	}

	public long addDiscuz(Discuz discuz) {
		ContentValues values = new ContentValues();
		values.put("discuz_id", discuz.discuz_id);
		values.put("title", discuz.title);
		values.put("summary", discuz.summary);
		values.put("author_id", discuz.author_id);
		values.put("author_name", discuz.author_name);
		values.put("create_ts", discuz.create_ts);
		values.put("update_ts", discuz.update_ts);
		values.put("response_count", discuz.response_count);
		values.put("last_response_ts", discuz.last_response_ts);
		values.put("last_response_user_id", discuz.last_response_user_id);
		values.put("last_response_user_name", discuz.last_response_user_name);
		values.put("local_status", 0);
		values.put("local_create_ts", System.currentTimeMillis());
		values.put("local_update_ts", 0);
		return mDbAdapter.insert(TABLE_NAME_DISCUZ, null, values);
	}

	public void deleteDiscuzByDiscuzId(int discuzId) {
		mDbAdapter.execSQL("DELETE FROM " + TABLE_NAME_DISCUZ + " WHERE discuz_id=" + discuzId);
	}

	public Discuz getDiscuzById(long _id) {
		Discuz discuz = new Discuz();
		Cursor cursor = mDbAdapter.rawQuery("SELECT * FROM " + TABLE_NAME_DISCUZ + " WHERE _id=" + _id, null);
		if (cursor.moveToFirst()) {
			discuz._id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
			discuz.title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
			discuz.url = cursor.getString(cursor.getColumnIndexOrThrow("url"));
			discuz.discuz_id = cursor.getInt(cursor.getColumnIndexOrThrow("discuz_id"));
			discuz.summary = cursor.getString(cursor.getColumnIndexOrThrow("summary"));
			discuz.author_id = cursor.getString(cursor.getColumnIndexOrThrow("author_id"));
			discuz.author_name = cursor.getString(cursor.getColumnIndexOrThrow("author_name"));
			discuz.create_ts = cursor.getLong(cursor.getColumnIndexOrThrow("create_ts"));
			discuz.update_ts = cursor.getLong(cursor.getColumnIndexOrThrow("update_ts"));
			discuz.response_count = cursor.getInt(cursor.getColumnIndexOrThrow("response_count"));
			discuz.last_response_ts = cursor.getLong(cursor.getColumnIndexOrThrow("last_response_ts"));
			cursor.close();
		}
		mDbAdapter.close();
		return discuz;
	}

	public ArrayList<Discuz> getFavDiscuz() {
		try {
			Cursor cursor = mDbAdapterForOld.rawQuery("SELECT * FROM " + TABLE_NAME_DISCUZ, null);
			ArrayList<Discuz> list = new ArrayList<Discuz>();
			for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
				Discuz discuz = new Discuz();
				discuz._id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
				discuz.title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
				discuz.url = cursor.getString(cursor.getColumnIndexOrThrow("url"));
				discuz.discuz_id = cursor.getInt(cursor.getColumnIndexOrThrow("discuz_id"));
				discuz.summary = cursor.getString(cursor.getColumnIndexOrThrow("summary"));
				discuz.author_id = cursor.getString(cursor.getColumnIndexOrThrow("author_id"));
				discuz.author_name = cursor.getString(cursor.getColumnIndexOrThrow("author_name"));
				discuz.create_ts = cursor.getLong(cursor.getColumnIndexOrThrow("create_ts"));
				discuz.update_ts = cursor.getLong(cursor.getColumnIndexOrThrow("update_ts"));
				discuz.response_count = cursor.getInt(cursor.getColumnIndexOrThrow("response_count"));
				discuz.last_response_ts = cursor.getLong(cursor.getColumnIndexOrThrow("last_response_ts"));
				list.add(discuz);
			}
			cursor.close();
			mDbAdapter.close();
			return list;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	public Discuz getDiscuzByDiscuzId(int discuzId) {
		Discuz discuz = new Discuz();
		Cursor cursor = mDbAdapter
				.rawQuery("SELECT * FROM " + TABLE_NAME_DISCUZ + " WHERE discuz_id=" + discuzId, null);
		if (cursor.moveToFirst()) {
			discuz._id = cursor.getInt(cursor.getColumnIndexOrThrow("_id"));
			discuz.title = cursor.getString(cursor.getColumnIndexOrThrow("title"));
			discuz.url = cursor.getString(cursor.getColumnIndexOrThrow("url"));
			discuz.discuz_id = cursor.getInt(cursor.getColumnIndexOrThrow("discuz_id"));
			discuz.summary = cursor.getString(cursor.getColumnIndexOrThrow("summary"));
			discuz.author_id = cursor.getString(cursor.getColumnIndexOrThrow("author_id"));
			discuz.author_name = cursor.getString(cursor.getColumnIndexOrThrow("author_name"));
			discuz.create_ts = cursor.getLong(cursor.getColumnIndexOrThrow("create_ts"));
			discuz.update_ts = cursor.getLong(cursor.getColumnIndexOrThrow("update_ts"));
			discuz.response_count = cursor.getInt(cursor.getColumnIndexOrThrow("response_count"));
			discuz.last_response_ts = cursor.getLong(cursor.getColumnIndexOrThrow("last_response_ts"));
			cursor.close();
		}
		mDbAdapter.close();
		return discuz;
	}

	public int getDiscuzCountByDiscuzId(int discuzId) {
		int count = 0;
		Cursor cursor = mDbAdapter.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME_DISCUZ + " WHERE discuz_id="
				+ discuzId, null);
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
			cursor.close();
		}
		mDbAdapter.close();
		return count;
	}

	public int getDiscuzCount() {
		int count = 0;
		Cursor cursor = mDbAdapter.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME_DISCUZ, null);
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
			cursor.close();
		}
		mDbAdapter.close();
		return count;
	}

	public long addGroup(Group group) {
		ContentValues values = new ContentValues();
		values.put("group_id", group.group_id);
		values.put("name", group.name);
		values.put("discussion_count", group.discussion_count);
		values.put("description", group.description);
		values.put("cover", group.cover);
		values.put("type", group.type);
		values.put("local_status", 0);
		values.put("local_create_ts", System.currentTimeMillis());
		values.put("local_update_ts", 0);

		long i = mDbAdapter.insert(TABLE_NAME_GROUP, null, values);
		mDbAdapter.close();
		return i;
	}

	public long addTopicGroup(TopicGroup group) {

		ContentValues values = new ContentValues();
		values.put("group_id", group.group_id);
		values.put("name", group.name);
		values.put("discussion_count", group.discussion_count);
		values.put("description", group.description);
		values.put("cover", group.cover);
		values.put("type", group.type);
		values.put("title", group.title);
		values.put("local_status", 0);
		values.put("local_create_ts", System.currentTimeMillis());
		values.put("local_update_ts", 0);

		long ret = mDbAdapter.insert(TABLE_NAME_GROUP, null, values);
		mDbAdapter.close();
		return ret;
	}

	public void deleteTopicGroup(int _id) {
		String sql = "delete from " + TABLE_NAME_GROUP + " where _id=" + _id;
		mDbAdapter.execSQL(sql);
	}

	public void deleteTopicGroupForOther() {
		String sql = "delete from " + TABLE_NAME_GROUP + " where title=" + " 'other' ";
		mDbAdapter.execSQL(sql);
		mDbAdapter.close();
	}

	public ArrayList<PinnedHeaderListViewBean> getTopicGroupListForOther() {
		String sqlTemp = "SELECT *  FROM " + TABLE_NAME_GROUP + " WHERE title=" + " 'other' ";
		Cursor cursorTemp = mDbAdapter.rawQuery(sqlTemp, null);

		ArrayList<PinnedHeaderListViewBean> list = new ArrayList<PinnedHeaderListViewBean>();
		for (cursorTemp.moveToFirst(); !cursorTemp.isAfterLast(); cursorTemp.moveToNext()) {
			TopicGroup itemTemp = new TopicGroup();
			itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
			itemTemp.group_id = cursorTemp.getInt(cursorTemp.getColumnIndex("group_id"));
			itemTemp.name = cursorTemp.getString(cursorTemp.getColumnIndex("name"));
			itemTemp.discussion_count = cursorTemp.getInt(cursorTemp.getColumnIndex("discussion_count"));
			itemTemp.description = cursorTemp.getString(cursorTemp.getColumnIndex("description"));
			itemTemp.cover = cursorTemp.getString(cursorTemp.getColumnIndex("cover"));
			itemTemp.type = cursorTemp.getString(cursorTemp.getColumnIndex("type"));
			itemTemp.title = cursorTemp.getString(cursorTemp.getColumnIndex("title")); // 我喜欢的圈子标识
			itemTemp.status = 1;
			PinnedHeaderListViewBean bean = new PinnedHeaderListViewBean(itemTemp, "other");
			list.add(bean);
		}
		cursorTemp.close();
		mDbAdapter.close();
		return list;
	}

	public ArrayList<PinnedHeaderListViewBean> getTopicGroupList() {
		String sqlTemp = "SELECT *  FROM " + TABLE_NAME_GROUP + " ORDER BY _id ASC";

		Cursor cursorTemp = mDbAdapter.rawQuery(sqlTemp, null);
		ArrayList<PinnedHeaderListViewBean> list = new ArrayList<PinnedHeaderListViewBean>();
		for (cursorTemp.moveToFirst(); !cursorTemp.isAfterLast(); cursorTemp.moveToNext()) {
			TopicGroup itemTemp = new TopicGroup();
			itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
			itemTemp.group_id = cursorTemp.getInt(cursorTemp.getColumnIndex("group_id"));
			itemTemp.name = cursorTemp.getString(cursorTemp.getColumnIndex("name"));
			itemTemp.discussion_count = cursorTemp.getInt(cursorTemp.getColumnIndex("discussion_count"));
			itemTemp.description = cursorTemp.getString(cursorTemp.getColumnIndex("description"));
			itemTemp.cover = cursorTemp.getString(cursorTemp.getColumnIndex("cover"));
			itemTemp.type = cursorTemp.getString(cursorTemp.getColumnIndex("type"));
			itemTemp.title = "other_birth"; // 我喜欢的圈子标识
			itemTemp.status = 1;
			PinnedHeaderListViewBean bean = new PinnedHeaderListViewBean(itemTemp, "birth");
			list.add(bean);
		}
		cursorTemp.close();
		mDbAdapter.close();
		return list;
	}

	public int getGroupCount() {
		int count = 0;
		Cursor cursor = mDbAdapter.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME_GROUP, null);
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
			cursor.close();
		}
		mDbAdapter.close();
		return count;
	}

	public int getLocationCount() {
		int count = 0;
		Cursor cursor = mDbAdapter.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME_LOCATION, null);
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
			cursor.close();
		}
		mDbAdapter.close();
		return count;
	}

	public int getLocationCount(int _id) {
		int count = 0;
		Cursor cursor = mDbAdapter.rawQuery("SELECT COUNT(*) FROM " + TABLE_NAME_LOCATION + " where _id=" + _id, null);
		if (cursor.moveToFirst()) {
			count = cursor.getInt(0);
			cursor.close();
		}
		mDbAdapter.close();
		return count;
	}

	public long addLocation(Location location) {
		int count = getLocationCount();
		if (count >= 3) { // 超过3个最常用
			String sql = "delete from " + TABLE_NAME_LOCATION + " where _id=(select _id from " + TABLE_NAME_LOCATION
					+ " order by local_create_ts asc limit 0,1)";
			mDbAdapter.execSQL(sql);
		}
		if (getLocationCount(location._id) == 0) {
			ContentValues values = new ContentValues();
			values.put("_id", location._id);
			values.put("type", location.type);
			values.put("name", location.name);
			values.put("longname", location.longname);
			values.put("active", location.active);
			values.put("province", location.province);
			values.put("[order]", location.order);
			values.put("postalcode", location.postalcode);
			values.put("dropdownorder", location.dropdownorder);
			values.put("idverifyorder", location.idverifyorder);

			values.put("local_status", 0);
			values.put("local_create_ts", System.currentTimeMillis());
			values.put("local_update_ts", 0);

			long ret = mDbAdapter.insert(TABLE_NAME_LOCATION, null, values);
			mDbAdapter.close();
			return ret;
		} else {
			return 1;
		}
	}

	public ArrayList<Location> getLocationList() {
		String sqlTemp = "SELECT *  FROM " + TABLE_NAME_LOCATION + " ORDER BY _id DESC";
		Cursor cursorTemp = mDbAdapter.rawQuery(sqlTemp, null);
		ArrayList<Location> list = new ArrayList<Location>();
		for (cursorTemp.moveToFirst(); !cursorTemp.isAfterLast(); cursorTemp.moveToNext()) {
			Location itemTemp = new Location();
			itemTemp._id = cursorTemp.getInt(cursorTemp.getColumnIndex("_id"));
			itemTemp.type = cursorTemp.getString(cursorTemp.getColumnIndex("type"));
			itemTemp.name = cursorTemp.getString(cursorTemp.getColumnIndex("name"));
			itemTemp.longname = cursorTemp.getString(cursorTemp.getColumnIndex("longname"));
			itemTemp.active = cursorTemp.getString(cursorTemp.getColumnIndex("active"));
			itemTemp.province = cursorTemp.getString(cursorTemp.getColumnIndex("province"));
			itemTemp.order = cursorTemp.getString(cursorTemp.getColumnIndex("order"));
			itemTemp.postalcode = cursorTemp.getString(cursorTemp.getColumnIndex("postalcode"));
			itemTemp.dropdownorder = cursorTemp.getString(cursorTemp.getColumnIndex("dropdownorder"));
			itemTemp.idverifyorder = cursorTemp.getString(cursorTemp.getColumnIndex("idverifyorder"));
			list.add(itemTemp);
		}
		cursorTemp.close();
		mDbAdapter.close();
		return list;
	}

}
