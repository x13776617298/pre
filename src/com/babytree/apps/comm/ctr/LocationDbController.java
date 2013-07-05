package com.babytree.apps.comm.ctr;

import com.babytree.apps.comm.db.LocationDbAdapter;
import com.babytree.apps.comm.model.Location;

import android.database.Cursor;

/**
 * 地区选择
 * 
 * @author wangbingqi
 * 
 */
public class LocationDbController {
	private static final String TABLE_NAME_LOCATION = "china_location_utf8";

	private LocationDbAdapter mLocationDbAdapter;

	public LocationDbController(LocationDbAdapter locationDbAdapter) {
		mLocationDbAdapter = locationDbAdapter;
	}

	public Location getLocationById(int _id) {
		String sqlTemp = "SELECT *  FROM " + TABLE_NAME_LOCATION + " WHERE _id=" + _id;
		Cursor cursorTemp = mLocationDbAdapter.rawQuery(sqlTemp, null);
		Location itemTemp = new Location();
		if (cursorTemp != null) {
			if (cursorTemp.moveToFirst()) {
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

			}
			cursorTemp.close();
		}
		mLocationDbAdapter.close();
		return itemTemp;
	}

}
