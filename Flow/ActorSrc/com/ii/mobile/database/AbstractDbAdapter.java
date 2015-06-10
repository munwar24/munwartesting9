/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.database;

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

public abstract class AbstractDbAdapter {

	private String tableName = null;
	protected static DatabaseHelper mDbHelper;
	private static SQLiteDatabase mDb;
	public final Context context;

	// protected static final String TABLE_USERS = "users";
	public static final String TABLE_SOAP = "static_soap";
	public static final String TABLE_TASK = "task_soap";
	public static final String TABLE_ROOM = "ROOM_soap";

	private static List<String> tableNames = new ArrayList<String>();

	// protected static final String TABLE_CREATE_USERS =
	// "create table users (_id integer primary key autoincrement, "
	// +
	// "username text not null , password text not null, platform text not null, "
	// +
	// "mobilePIN text not null, mobileUserName text not null, taskNumber text not null, facilityID text not null, mobilePermisions text not null, "
	// +
	// "employeeID text not null, utCoffset text not null, functionalArea text not null, mobileuserID text not null, "
	// +
	// "autoAssign text not null, facility text not null, taskStatusID text not null, timeZone text not null, mobileRoles text not null, "
	// + "dayLightSavingTime text not null, employeeName text not null, "
	// + "SERVER_ID text, time_stamp text);";

	protected static final String TABLE_CREATE_SOAP = "create table static_soap (_id integer primary key autoincrement, "
			+ "soap_method text not null , employeeID text, facilityID text, taskNumber text, localTaskNumber text, "
			+ "time_stamp text, update_time_stamp text, json blob not null);";

	// protected static final String TABLE_CREATE_TASK =
	// "create table task_soap (_id integer primary key auto increment, "
	// +
	// "soap_method text not null , employeeID text not null, facilityID text not null, taskNumber text not null, localTaskNumber text, "
	// + "time_stamp text, json text not null);";

	// protected static final String TABLE_CREATE_ROOM =
	// "create table room_soap ( "
	// +
	// "facilityID integer not null , title text not null , hirNode integer not null);";

	public static final String KEY_ROWID = "_id";
	public static final String TIME_STAMP = "time_stamp";
	public static final String UPDATE_TIME_STAMP = "update_time_stamp";
	public static final String SERVER_ID = "SERVER_ID";
	public static final String USERNAME = "username";
	public static final String PASSWORD = "password";
	static boolean deleteDataBase = true;
	protected NetworkUploader networkUploader;

	protected static final String DATABASE_NAME = "crothallMobile155";
	protected static int databaseVersion = 6;

	public AbstractDbAdapter(Context context) {
		// L.out("this: " + this);
		this.context = context;
		open();
		networkUploader = NetworkUploader.register(this, context);

		// networkUploader = new NetworkUploader(this, context);
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
		networkUploader = NetworkUploader.getNetworkUploader();
		if (networkUploader != null) {
			networkUploader.onResume();
		}
		return result;
	}

	public long update(ContentValues values, String where) {
		// showEvents(getTableName());
		networkUploader = NetworkUploader.getNetworkUploader();
		// L.out("networkUploader: " + networkUploader);
		if (networkUploader != null) {
			networkUploader.onResume();
		}
		return 0;
	}

	public boolean delete(long rowId) {
		L.out("rowId: " + rowId);
		return getDB().delete(getTableName(), KEY_ROWID + "=" + rowId, null) > 0;
	}

	public int deleteAll() {
		return getDB().delete(getTableName(), "1", null);
	}

	/**
	 * Open or create the database.
	 * 
	 * @return this
	 * @throws SQLException
	 *             if the database could be neither opened or created
	 */
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
		int count = 1;
		L.outp("Table Name: " + tableName);
		String tmp = "";
		for (int i = 0; i < c.getColumnCount(); i++) {
			tmp += c.getColumnName(i) + " - ";
		}
		L.outp("table columns: " + tmp);
		c.moveToFirst();
		if (c.getCount() == 0) {
			L.outp("No records");
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
		values.put(AbstractDbAdapter.TIME_STAMP, (String) null);
		values.put(AbstractDbAdapter.UPDATE_TIME_STAMP, new GregorianCalendar().getTimeInMillis() + "");
		long updated = getDB().update(getTableName(), values, AbstractDbAdapter.KEY_ROWID + "=" + rowId, null);
		// showEvents(getTableName());
		L.out("updated the timestamp: " + updated);
	}

	public boolean uploadIfNeeded() {
		// L.out("getTableName: " + getTableName());
		return false;
	}

	public static SQLiteDatabase getDB() {
		return mDb;
	}

	protected static class DatabaseHelper extends SQLiteOpenHelper {

		@SuppressWarnings("unused")
		private AbstractDbAdapter dbAdapter = null;

		DatabaseHelper(AbstractDbAdapter dbAdapter, Context context) {
			super(context, DATABASE_NAME, null, databaseVersion);
			this.dbAdapter = dbAdapter;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			L.out("onCreate db: " + db + " name: " + DATABASE_NAME);
			// db.execSQL(TABLE_CREATE_USERS);
			db.execSQL(TABLE_CREATE_SOAP);
			// db.execSQL(TABLE_CREATE_TASK);
			// db.execSQL(TABLE_CREATE_ROOM);
			// listTables();
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			L.out("Upgrading database from version " + oldVersion + " to "
					+ newVersion + ", which will destroy all old data");
			// db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOAP);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_TASK);
			db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROOM);
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