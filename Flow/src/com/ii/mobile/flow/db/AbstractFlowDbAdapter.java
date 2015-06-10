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
import android.database.sqlite.SQLiteOpenHelper;

import com.ii.mobile.util.L;

public abstract class AbstractFlowDbAdapter {

	private String tableName = null;
	protected static DatabaseHelper mDbHelper;
	private static SQLiteDatabase mDb;
	public final Context context;

	// protected static final String TABLE_USERS = "users";
	public static final String TABLE_FLOW = "table_flow";

	private static List<String> tableNames = new ArrayList<String>();

	protected static final String TABLE_CREATE_FLOW = "create table table_flow (_id integer primary key autoincrement, "
			+ "flowMethod text not null , actorId text, facilityId text, actionId text, localActionNumber text, "
			+ "time_stamp text, update_time_stamp text, json blob not null);";

	public static final String KEY_ROWID = "_id";
	public static final String TIME_STAMP = "time_stamp";
	public static final String UPDATE_TIME_STAMP = "update_time_stamp";
	public static final String SERVER_ID = "SERVER_ID";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";

	public static final String TICKLED = "tickled";
	static boolean deleteDataBase = true;
	protected FlowUploader flowUploader;

	protected static final String DATABASE_NAME = "crothallTransport142";
	protected static int databaseVersion = 6;

	public AbstractFlowDbAdapter(Context context) {
		// L.out("this: " + this);
		this.context = context;
		open();
		flowUploader = FlowUploader.register(this, context);
	}

	public long create(ContentValues values, String tableName) {
		values.put(TIME_STAMP, new GregorianCalendar().getTimeInMillis() + "");
		values.put(SERVER_ID, (String) null);
		// args.put(KEY_SOURCE,source);
		// args.put(KEY_DESTINATION,destination);
		L.out("values: " + values);
		L.out("mDb: " + getDB() + " table: " + tableName);

		long result = getDB().update(tableName, values, null, null);
		L.out("result: " + result);
		showEvents(tableName);
		flowUploader = FlowUploader.getNetworkUploader();
		if (flowUploader != null) {
			flowUploader.onResume();
		}
		return result;
	}

	public long update(ContentValues values, String where) {
		// showEvents(getTableName());
		flowUploader = FlowUploader.getNetworkUploader();
		// L.out("networkUploader: " + networkUploader);
		if (flowUploader != null) {
			flowUploader.onResume();
		}
		return 0;
	}

	public boolean delete(long rowId) {
		L.out("rowId: " + rowId);
		return getDB().delete(getTableName(), KEY_ROWID + "=" + rowId, null) > 0;
	}

	public int deleteAll() {
		int result = getDB().delete(getTableName(), null, null);
		showAllEvents();
		return result;
	}

	synchronized private void open() throws SQLException {
		// L.out("mDbHelper: " + mDbHelper);
		// L.out("mDb: " + mDb);
		if (mDbHelper == null) {
			mDbHelper = new DatabaseHelper(this, context);
			mDb = mDbHelper.getWritableDatabase();
			mDbHelper.listTables();
			// showEvents(getTableName());
		}
	}

	public SQLiteDatabase getWritableDatabase() {
		return getDB();
	}

	public void close() {
		mDbHelper.close();
	}

	private Cursor getEvents(String tableName) {
		SQLiteDatabase db = mDbHelper.getReadableDatabase();
		Cursor cursor = db.query(tableName, null, null, null, null, null, null);
		// startManagingCursor(cursor);
		return cursor;
	}

	public void showEvents(String tableName) {
		Cursor c = getEvents(tableName);
		// int count = 1;
		L.outp("Table Name: " + tableName);
		String tmp = "";
		for (int i = 0; i < c.getColumnCount(); i++) {
			tmp += c.getColumnName(i) + " - ";
		}
		L.outp("table columns: " + tmp);
		c.moveToFirst();
		if (c.getCount() == 0) {
			L.out("No records");
			return;
		}
		do {
			tmp = c.getColumnName(0) + ": " + c.getInt(0) + " ";
			for (int i = 1; i < c.getColumnCount(); i++) {
				tmp += c.getColumnName(i) + ": " + shorten(c.getString(i))
						+ " ";
			}
			// L.out(count++ + " : " + tmp);
			L.outp(tmp);
		} while (c.moveToNext());
	}

	private static int MAX_STRING = 40;

	private String shorten(String string) {
		if (string == null) {
			return string;
		}
		if (string.length() > MAX_STRING) {
			return "" + string.substring(0, MAX_STRING) + "";
		}
		return string;
	}

	public void showAllEvents() {
		for (String table : tableNames) {
			showEvents(table);
		}
	}

	public String getTableName() {
		return tableName;
	}

	public void setTableName(String tableName) {
		this.tableName = tableName;
		tableNames.add(tableName);
	}

	public void updateTimeStamp(Cursor c, String timeStamp, String rowId,
			String result, ContentValues values) {
		values.put(AbstractFlowDbAdapter.TIME_STAMP, (String) null);
		values.put(AbstractFlowDbAdapter.UPDATE_TIME_STAMP, new GregorianCalendar().getTimeInMillis() + "");
		long updated = getDB().update(getTableName(), values, AbstractFlowDbAdapter.KEY_ROWID + "=" + rowId, null);
		// showEvents(getTableName());
		L.out("updated the timestamp: " + updated);
	}

	public boolean uploadIfNeeded() {
		return false;
	}

	public boolean anyToUpload() {
		return false;
	}

	public static SQLiteDatabase getDB() {
		return mDb;
	}

	protected static class DatabaseHelper extends SQLiteOpenHelper {

		@SuppressWarnings("unused")
		private AbstractFlowDbAdapter dbAdapter = null;

		DatabaseHelper(AbstractFlowDbAdapter dbAdapter, Context context) {
			super(context, DATABASE_NAME, null, databaseVersion);
			this.dbAdapter = dbAdapter;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			L.out("onCreate db: " + db + " name: " + DATABASE_NAME);
			db.execSQL(TABLE_CREATE_FLOW);
			// listTables();
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			L.out("Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			// db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_FLOW);

		}

		public ArrayList<Object> listTables() {
			ArrayList<Object> tableList = new ArrayList<Object>();
			String SQL_GET_ALL_TABLES = "SELECT name FROM " + "sqlite_master"
					+ " WHERE type='table' ORDER BY name";
			Cursor cursor = getDB().rawQuery(SQL_GET_ALL_TABLES, null);
			cursor.moveToFirst();
			if (!cursor.isAfterLast()) {
				do {
					tableList.add(cursor.getString(0));
				} while (cursor.moveToNext());
			}
			cursor.close();
			L.out("tableList: " + tableList);
			return tableList;
		}
	}

}