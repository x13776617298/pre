package com.babytree.apps.comm.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class LocationDbAdapter extends SQLiteAssetHelper {
	private static final String TAG = LocationDbAdapter.class.getSimpleName();

	private static final String DATABASE_NAME = "location";

	private static final int DBVERSION = 2;

	private SQLiteDatabase _db;

	public LocationDbAdapter(Context context) {
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
