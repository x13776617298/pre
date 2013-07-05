package com.babytree.apps.comm.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class Y_CalendarDbAdapter extends SQLiteAssetHelper {
	private static final String TAG = Y_CalendarDbAdapter.class.getSimpleName();

	private static final String DATABASE_NAME = "y_pregnancy_calendar";

	/**
	 * 育儿知识数据库版本号 10:V3.3,V3.4
	 */
	private static final int DBVERSION = 10;

	private SQLiteDatabase _db;

	public Y_CalendarDbAdapter(Context context) {
		super(context, DATABASE_NAME, null, DBVERSION);
		// _db = super.getWritableDatabase();
	}

	public void execSQL(String sql) {
		getDatabase().execSQL(sql);
	}

	public void execSQL(String sql, Object[] bindArgs) {
		getDatabase().execSQL(sql, bindArgs);
	}

	public long insert(String table, String nullColumnHack, ContentValues values) {
		return getDatabase().insert(table, nullColumnHack, values);
	}

	public Cursor rawQuery(String sql, String[] bindArgs) {
		return getDatabase().rawQuery(sql, bindArgs);
	}

	public void close() {
		if (_db != null) {
			Log.d(TAG, DATABASE_NAME + " close.");
			_db.close();
		}
	}

	private void setDBValue() {
			_db = super.getWritableDatabase();
	}

	public synchronized SQLiteDatabase getDatabase() {

		if (_db == null || !_db.isOpen()) {
			setDBValue();
		}

		return _db;
	}
}
