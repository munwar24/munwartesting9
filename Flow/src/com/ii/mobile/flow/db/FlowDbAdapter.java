/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.flow.db;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.ii.mobile.flow.db.StaticFlowProvider.StaticFlowColumns;
import com.ii.mobile.flow.types.Logger;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;
import com.ii.mobile.util.SecurityUtils;

public class FlowDbAdapter extends AbstractFlowDbAdapter {
	private static FlowDbAdapter flowDbAdapter;

	public static FlowDbAdapter getFlowDbAdapter() {
		return flowDbAdapter;
	}

	public final static boolean WANT_SECURITY = true;
	public final static boolean WANT_SECURITY_DEBUG = false;

	public FlowDbAdapter(Context context) {
		super(context);
		setTableName(TABLE_FLOW);
		L.out("created: " + this);
		FlowDbAdapter.setFlowDbAdapter(this);
	}

	public long create(String flowMethod, String json, String employeeID, String facilityID, String actionId) {
		ContentValues values = new ContentValues();
		values.put(StaticFlowColumns.FLOW_METHOD, flowMethod);
		values.put(StaticFlowColumns.ACTOR_ID, employeeID);
		values.put(StaticFlowColumns.FACILITY_ID, facilityID);
		values.put(StaticFlowColumns.ACTION_ID, actionId);
		L.out("values: " + values);
		if (WANT_SECURITY) {
			out("before create json: " + json + " user: " + User.getUser());
			json = SecurityUtils.encryptAES(User.getUser().getPassword(), json);
			values.put(StaticFlowColumns.JSON, json);
			out("after create json: " + json);
			if (WANT_SECURITY_DEBUG)
				out("test : " + SecurityUtils.decryptAES(User.getUser().getPassword(), json));
		}
		long result = getDB().insert(getTableName(), null, values);
		L.out("result: " + result);

		showEvents(getTableName());
		return result;
	}

	public static void out(String output) {
		if (WANT_SECURITY_DEBUG)
			L.out(output);
	}

	@Override
	public boolean delete(long rowID) {
		L.out("delete: " + rowID);
		// showEvents(getTableName());
		boolean result = getDB().delete(getTableName(), KEY_ROWID + "=" + rowID, null) > 0;
		// L.out("after delete: " + result);
		// showEvents(getTableName());
		if (!result) {
			L.out("Deletion failed on " + rowID);
			showEvents(getTableName());
		}
		return result;
	}

	public static String getWhere(String flowMethod, String employeeID, String facilityID, String actionId) {
		String temp = StaticFlowColumns.FLOW_METHOD + "='" + flowMethod + "'";
		if (facilityID != null)
			temp += " AND " + StaticFlowColumns.FACILITY_ID + "='" + facilityID + "'";
		if (employeeID != null)
			temp += " AND " + StaticFlowColumns.ACTOR_ID + "='" + employeeID + "'";
		if (actionId != null)
			temp += " AND " + StaticFlowColumns.ACTION_ID + "='" + actionId + "'";
		L.out("where: " + temp);
		return temp;
	}

	public List<GJon> parse(Uri uri, String actorId, String facilityId, String actionId) {
		L.out("parse uri: " + uri);
		showEvents(getTableName());
		String where = null;

		String flowMethod = uri.getPathSegments().get(0);
		if (uri != null)
			where = getWhere(flowMethod, actorId, facilityId, actionId);

		Cursor cursor = getDB().query(getTableName(), null, where, null, null, null, null);
		L.out("cursor: " + cursor.getCount());

		List<GJon> flowList = null;
		if (cursor.getCount() != 0) {
			flowList = getFromDatabase(cursor, actorId, facilityId, actionId);
			if (flowList != null)
				return flowList;
			L.out("failed to decrypt employeeID: " + actorId);
		}
		return null;
		// return getFromServer(cursor, uri, actorId, facilityId, actionId);
	}

	private List<GJon> getFromDatabase(Cursor cursor, String actorId, String facilityId, String actionId) {

		List<GJon> flowList = new ArrayList<GJon>();
		cursor.moveToFirst();
		do {
			String json = cursor.getString(cursor.getColumnIndexOrThrow(StaticFlowColumns.JSON));
			if (WANT_SECURITY) {
				out("parse json: " + json);
				json = SecurityUtils.decryptAES(User.getUser().getPassword(), json);
				out("uncompressed: " + json);
				if (json == null) {
					L.out("cannot decrypt with: " + User.getUser().getPassword());
					return null;
				}
			}

			GJon gJon = null;
			gJon = new GJon(actorId, facilityId, actionId, json);
			flowList.add(gJon);
			// return only one
			return flowList;
		} while (cursor.moveToNext());
	}

	// private List<GJon> getFromServer(Cursor cursor, Uri uri, String actorId,
	// String facilityId,
	// String actionId) {
	// String flowMethod = uri.getPathSegments().get(0);
	// if (FlowUploader.isConnectedToInternet()) {
	// // MyToast.show("Loading static flow...");
	// List<GJon> flowList = new ParsingSoap().build(uri, actorId, facilityId,
	// actionId);
	//
	// if (flowList == null)
	// return null;
	// if (flowList != null) {
	// // L.out("SoapList: " + soapList.size());
	// // put in db
	// for (GJon gjon : flowList) {
	// // need the employeeID and facilityID
	//
	// if (gjon == null) {
	// MyToast.show("Failed to load JSON for: " + flowMethod +
	// " and facilityID: "
	// + facilityId);
	// } else {
	// String json = gjon.getJson();
	// String password = User.getUser().getPassword();
	// L.out("password: " + password);
	//
	// create(flowMethod, json, actorId, facilityId, actionId);
	// }
	// }
	// // MyToast.show("...Loaded " + L.getPlural(flowList.size(),
	// // "Flow"));
	// // showEvents(getTableName());
	// // L.out("flowList: " + flowList);
	// return flowList;
	// }
	// }
	// // failure - no network!
	// // Toast.makeText(context, "No network available for downloading Soap: "
	// // + soapMethod, Toast.LENGTH_SHORT);
	// L.out("no network available for downloading: " + flowMethod);
	// return null;
	// }

	/**
	 * Return a Cursor positioned at the route that matches the given rowId
	 * 
	 * @param rowId
	 *            id of route to retrieve
	 * @return Cursor positioned to matching route, if found
	 * @throws SQLException
	 *             if route could not be found/retrieved
	 */
	public Cursor fetch(long rowId) throws SQLException {
		L.out("rowId: " + rowId);
		Cursor mCursor =
				getDB().query(getTableName(), null, null, null, null, null,
						StaticFlowColumns.DEFAULT_SORT_ORDER);
		L.out("mCursor: " + mCursor);
		if (mCursor != null) {
			L.out("rows: " + mCursor.getCount());
		}
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	public long update(ContentValues values, String where, boolean leaveTimeStamp) {
		L.out("update leaveTimeStamp: " + leaveTimeStamp);
		// values.remove(TICKLED);
		// L.out("values: " + values);
		L.out("where: " + where);
		// mDbHelper.listTables();
		// L.out("values: " + values);
		String json = values.getAsString(StaticFlowColumns.JSON);
		if (WANT_SECURITY && json != null) {
			// out("before update json: " + json);
			json = SecurityUtils.encryptAES(User.getUser().getPassword(), json);
			values.put(StaticFlowColumns.JSON, json);
			// out("after update compressed: " + json);
			// test
			// String test =
			// SecurityUtils.decryptAES(User.getUser().getPassword(), json);
			// out("uncompressed : " + test);
		}
		if (json == null) {
			L.out("updated error: " + values);
			return -1;
		}
		long updated = getDB().update(getTableName(), values, where, null);
		L.out("updated result: " + updated);
		// not in table
		if (updated == 0) {
			long result = getDB().insert(getTableName(), null, values);
			L.out("insert result: " + result);
		}
		// showEvents(getTableName());
		if (!leaveTimeStamp)
			super.update(values, where);
		return updated;
	}

	@Override
	public long update(ContentValues values, String where) {
		// L.out("update values: " + values);
		String timeStamp = null;
		// boolean tickled = values.getAsString(TICKLED) != null
		// && values.getAsString(TICKLED).equals(GJon.TRUE_STRING);
		if (values.getAsString(TICKLED) != null && values.getAsString(TICKLED).equals(GJon.FALSE_STRING))
			timeStamp = new GregorianCalendar().getTimeInMillis() + "";
		values.put(TIME_STAMP, timeStamp);
		values.remove(TICKLED);
		// L.out("values: " + values);
		L.out("where: " + where);
		// mDbHelper.listTables();
		// L.out("values: " + values);
		String json = values.getAsString(StaticFlowColumns.JSON);
		if (WANT_SECURITY && json != null) {
			// out("before update json: " + json);
			json = SecurityUtils.encryptAES(User.getUser().getPassword(), json);
			values.put(StaticFlowColumns.JSON, json);
			// out("after update compressed: " + json);
			// test
			// String test =
			// SecurityUtils.decryptAES(User.getUser().getPassword(), json);
			// out("uncompressed : " + test);
		}
		long updated = getDB().update(getTableName(), values, where, null);
		L.out("updated result: " + updated);
		// not in table
		if (updated == 0) {
			long result = getDB().insert(getTableName(), null, values);
			L.out("insert result: " + result);
		}
		// showEvents(getTableName());
		// MyToast.show("timeStamp: " + timeStamp);
		if (timeStamp != null)
			super.update(values, where);
		return updated;
	}

	public int somethingToUpdate() {
		L.out("somethingToUpdate");
		SQLiteDatabase db = getWritableDatabase();
		if (db == null) {
			L.out("db: " + db);
			return 0;
		}

		String where = AbstractFlowDbAdapter.TIME_STAMP + " is not null";
		// L.out("where: " + where);
		Cursor cursor = db.query(getTableName(), null, where, null, null,
				null, TIME_STAMP + " ASC");
		// // L.out("raw c: " + c.getCount());
		// if (c.getCount() > 0) {
		// L.out("count to update: " + cursor.getCount());
		// }
		return cursor.getCount();

	}

	@Override
	public boolean anyToUpload() {
		L.out("anyToUpload");
		SQLiteDatabase db = getWritableDatabase();
		if (db == null) {
			L.out("db: " + db);
			return false;
		}

		String where = AbstractFlowDbAdapter.TIME_STAMP + " is not null";
		Cursor cursor = db.query(getTableName(), null, where, null, null,
				null, TIME_STAMP + " ASC");
		if (cursor.getCount() < 1)
			return false;
		return true;

	}

	@Override
	public boolean uploadIfNeeded() {

		L.out("uploadIfNeeded");
		SQLiteDatabase db = getWritableDatabase();
		if (db == null) {
			L.out("db: " + db);
			return false;
		}

		String where = AbstractFlowDbAdapter.TIME_STAMP + " is not null";
		// L.out("where: " + where);
		Cursor cursor = db.query(getTableName(), null, where, null, null,
				null, TIME_STAMP + " ASC");
		// // L.out("raw c: " + c.getCount());
		// if (c.getCount() > 0) {
		// L.out("count to update: " + cursor.getCount());
		// }

		if (cursor.getCount() < 1)
			return false;

		cursor.moveToFirst();
		// String actionIda =
		// cursor.getString(cursor.getColumnIndexOrThrow(StaticFlowColumns.ACTION_ID));
		// String rowIda =
		// cursor.getString(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.KEY_ROWID));
		// String timeStampa =
		// cursor.getString(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.TIME_STAMP));
		// L.out("actionId: " + actionIda + " rowId: " + rowIda +
		// " timeStampe: " + timeStampa);
		L.out("cursor: " + cursor.getCount());
		while (cursor.moveToNext()) {
			String actionId = cursor.getString(cursor.getColumnIndexOrThrow(StaticFlowColumns.ACTION_ID));
			String rowId = cursor.getString(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.KEY_ROWID));
			String timeStamp = cursor.getString(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.TIME_STAMP));
			L.out("actionId: " + actionId + " rowId: " + rowId + " timeStampe: " + timeStamp);

		}

		// try {
		cursor.moveToFirst();
		String flowMethod = cursor.getString(cursor.getColumnIndexOrThrow(StaticFlowColumns.FLOW_METHOD));
		String json = cursor.getString(cursor.getColumnIndexOrThrow(StaticFlowColumns.JSON));
		// String timeStamp =
		// cursor.getString(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.TIME_STAMP));
		String actionId = cursor.getString(cursor.getColumnIndexOrThrow(StaticFlowColumns.ACTION_ID));
		// String rowId =
		// cursor.getString(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.KEY_ROWID));
		// String localActionNumber =
		// cursor.getString(cursor.getColumnIndexOrThrow(StaticFlowColumns.LOCAL_ACTION_NUMBER));
		String actorId = cursor.getString(cursor.getColumnIndexOrThrow(StaticFlowColumns.ACTOR_ID));
		// String facilityID =
		// cursor.getString(cursor.getColumnIndexOrThrow(StaticFlowColumns.FACILITY_ID));
		// L.out("rowId: " + rowId);
		// L.out("timeStamp: " + timeStamp);
		L.out("actionId: " + actionId + " flowMethod: " + flowMethod + " actorId: " + actorId);
		if (WANT_SECURITY) {
			json = SecurityUtils.decryptAES(User.getUser().getPassword(), json);
			out("uploadIfNeeded json: " + json);
		}
		// L.out("Uploaded stubbed out: " + flowMethod);
		boolean success = new Uploader().update(cursor, this, WANT_SECURITY);
		if (!success) {
			Logger.getLogger().networkStats.addFailUpdate();
			L.out("Failed to upload: " + flowMethod);
		}
		else
			Logger.getLogger().networkStats.addTotalUpdate();
		return true;
	}

	public void deleteRow(String rowId) {
		L.out("delete: " + rowId);
		String where = StaticFlowColumns._ID + "='" + rowId + "'";
		Cursor cursor = getDB().query(getTableName(), null, where, null, null, null, null);
		int count = cursor.getCount();
		// L.out("delete cursor: " + count);

		if (count != 0) {
			cursor.moveToFirst();
			String rowID = cursor.getString(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.KEY_ROWID));
			int rowIndex = (int) L.getLong(rowID);
			L.out("delete rowID: " + rowIndex);
			delete(rowIndex);
		} else {
			L.out("*** Unable to delete record: " + rowId);
		}
		L.out("after delete: " + rowId);
		showEvents(getTableName());
	}

	public void delete(String flowMethod) {
		L.out("delete: " + flowMethod);
		showEvents(getTableName());
		String where = StaticFlowColumns.FLOW_METHOD + "='" + flowMethod + "'";
		Cursor cursor = getDB().query(getTableName(), null, where, null, null, null, null);
		int count = cursor.getCount();
		// L.out("delete cursor: " + count);

		if (count != 0) {
			cursor.moveToFirst();
			String rowID = cursor.getString(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.KEY_ROWID));
			int rowIndex = (int) L.getLong(rowID);
			// L.out("delete cursor: " + count + " rowId: " + rowIndex);
			delete(rowIndex);
		} else {
			L.out("*** Unable to delete record: " + flowMethod);
		}
		L.out("after delete: " + flowMethod);
		showEvents(getTableName());
	}

	public static void setFlowDbAdapter(FlowDbAdapter flowDbAdapter) {
		FlowDbAdapter.flowDbAdapter = flowDbAdapter;
	}
}
