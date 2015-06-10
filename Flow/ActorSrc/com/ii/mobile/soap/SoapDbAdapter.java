/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.soap;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;

import com.ii.mobile.database.AbstractDbAdapter;
import com.ii.mobile.database.NetworkUploader;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.model.TaskModel;
import com.ii.mobile.soap.StaticSoap.StaticSoapColumns;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.soap.gson.GetEmployeeAndTaskStatusByEmployeeID;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.tab.BreakActivity;
import com.ii.mobile.tab.SelfTaskActivity;
import com.ii.mobile.tickle.Tickler;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;
import com.ii.mobile.util.SecurityUtils;

/**
 * 
 * @author kfairchild
 */
public class SoapDbAdapter extends AbstractDbAdapter {
	private static SoapDbAdapter soapDbAdapter;
	private static TaskModel taskModel;

	public static SoapDbAdapter getSoapDbAdapter() {
		return soapDbAdapter;
	}

	public final static boolean WANT_SECURITY = true;
	public final static boolean WANT_SECURITY_DEBUG = false;

	/**
	 * Constructor - takes the context to allow the database to be
	 * opened/created
	 * 
	 * @param context
	 *            the Context within which to work
	 */
	public SoapDbAdapter(Context context) {
		super(context);
		setTableName(TABLE_SOAP);
		L.out("created: " + this);
		SoapDbAdapter.setSoapDbAdapter(this);
		taskModel = new TaskModel(this);
	}

	/**
	 * Cache the json document giving the soap call
	 * 
	 * @param source
	 * @param destination
	 * @return rowId or -1 if failed
	 */
	public long create(String soapMethod, String json, String employeeID, String facilityID, String taskNumber) {
		ContentValues values = new ContentValues();
		values.put(StaticSoapColumns.SOAP_METHOD, soapMethod);
		values.put(StaticSoapColumns.EMPLOYEE_ID, employeeID);
		values.put(StaticSoapColumns.FACILITY_ID, facilityID);
		values.put(StaticSoapColumns.TASK_NUMBER, taskNumber);
		L.out("values: " + values);
		if (WANT_SECURITY) {
			out("before create json: " + json);
			json = SecurityUtils.encryptAES(User.getUser().getPassword(), json);
			values.put(StaticSoapColumns.JSON, json);
			out("after create json: " + json);
			if (WANT_SECURITY_DEBUG)
				out("test : " + SecurityUtils.decryptAES(User.getUser().getPassword(), json));
		}
		long result = getDB().insert(getTableName(), null, values);
		L.out("result: " + result);
		// constantly resetting this. Stop the output for this method
		// if (!soapMethod.equals(ParsingSoap.GET_CURRENT_TASK_BY_EMPLOYEE_ID))
		// showEvents(getTableName());
		// This one for when inserting
		showEvents(getTableName());
		return result;
	}

	public static void out(String output) {
		if (WANT_SECURITY_DEBUG)
			L.out(output);
	}

	/**
	 * Delete the route with the given rowId
	 * 
	 * @param rowId
	 * @return true if deleted, false otherwise
	 */
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

	public static String getWhere(String soapMethod, String employeeId, String facilityID, String taskNumber) {
		// return StaticSoapColumns.SOAP_METHOD + "='" + soapMethod + "' AND "
		// + StaticSoapColumns.EMPLOYEE_ID + "='" + employeeID + "' AND "
		// + StaticSoapColumns.FACILITY_ID + "='" + facilityID + "' AND "
		// + StaticSoapColumns.TASK_NUMBER + "='" + taskNumber + "'";

		String temp = StaticSoapColumns.SOAP_METHOD + "='" + soapMethod + "' AND "
				+ StaticSoapColumns.FACILITY_ID + "='" + facilityID + "'";
		if (employeeId != null)
			temp += " AND " + StaticSoapColumns.EMPLOYEE_ID + "='" + employeeId + "'";
		if (taskNumber != null)
			temp += " AND " + StaticSoapColumns.TASK_NUMBER + "='" + taskNumber + "'";
		L.out("where: " + temp);
		return temp;
	}

	/**
	 * Return a Cursor over the list of all SOAP JSON in the database
	 * 
	 * @return Cursor over all SOAP JSON
	 */
	public List<GJon> parse(Uri uri, String employeeID, String facilityID, String taskNumber) {
		// L.out("uri: " + uri);
		// showEvents(getTableName());
		String where = null;

		String soapMethod = uri.getPathSegments().get(0);
		if (uri != null) {
			where = getWhere(soapMethod, employeeID, facilityID, taskNumber);
			if (soapMethod.equals(ParsingSoap.VALIDATE_USER)) {
				where = getWhere(soapMethod, User.getUser().getUsername(), User.getUser().getPassword(), taskNumber);
			}
		}
		if (!soapMethod.equals(ParsingSoap.GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID)) {
			L.out("Parse where: " + uri + "\n " + where);
		}

		Cursor cursor = getDB().query(getTableName(), null, where, null, null, null, null);
		L.out("cursor: " + cursor.getCount());
		// is in database already
		// force the
		// if (cursor.getCount() != 0 && soapMethod.equals("ValidateUser"))
		List<GJon> soapList = null;
		if (cursor.getCount() != 0) {
			soapList = getFromDatabase(cursor, employeeID, facilityID, taskNumber);
			if (soapList != null)
				return soapList;
			L.out("failed to decrypt employeeID: " + employeeID);
		}
		return getFromServer(cursor, uri, employeeID, facilityID, taskNumber);
		// && !NetworkUploader.isConnectedToInternet()))

		// get from the server

	}

	private List<GJon> getFromDatabase(Cursor cursor, String employeeID, String facilityID, String taskNumber) {

		List<GJon> soapList = new ArrayList<GJon>();
		cursor.moveToFirst();
		do {
			String json = cursor.getString(cursor.getColumnIndexOrThrow(StaticSoapColumns.JSON));
			if (WANT_SECURITY) {
				out("parse json: " + json);
				json = SecurityUtils.decryptAES(User.getUser().getPassword(), json);
				out("uncompressed: " + json);
				if (json == null) {
					L.out("cannot decrypt with: " + User.getUser().getPassword());
					return null;
				}
			}
			// json = Encrypt.decryptIt(json, User.getUser().getPassword());
			GJon gJon = null;
			gJon = new GJon(employeeID, facilityID, taskNumber, json);
			soapList.add(gJon);
			if (true)
				return soapList;
		} while (cursor.moveToNext());
		// L.out("finished: " + cursor.getCount());
		return soapList;

	}

	private List<GJon> getFromServer(Cursor cursor, Uri uri, String employeeID, String facilityID,
			String taskNumber) {
		String soapMethod = uri.getPathSegments().get(0);
		if (NetworkUploader.isConnectedToInternet()) {
			// MyToast.show("Loading static soap...");
			List<GJon> soapList = new ParsingSoap().build(uri, employeeID, facilityID, taskNumber);

			if (soapList == null)
				return null;
			if (soapList != null) {
				// L.out("SoapList: " + soapList.size());
				// put in db
				for (GJon gjon : soapList) {
					// need the employeeID and facilityID
					if (soapMethod.equals(ParsingSoap.VALIDATE_USER)) {
						ValidateUser validateUser = ValidateUser.getGJon(gjon.getJson());
						employeeID = validateUser.getEmployeeID();
						facilityID = validateUser.getFacilityID();
						taskNumber = User.getUser().getPlatform();
						create(soapMethod, gjon.getJson(), User.getUser().getUsername(), User.getUser().getPassword(), taskNumber);
					} else {
						if (gjon == null) {
							MyToast.show("Failed to load JSON for: " + soapMethod + " and facilityID: "
									+ facilityID);
						} else {
							String json = gjon.getJson();
							String password = User.getUser().getPassword();
							L.out("password: " + password);

							create(soapMethod, json, employeeID, facilityID, taskNumber);
						}
					}
				}
				// MyToast.show("...Loaded " + L.getPlural(soapList.size(),
				// "Soap"));
				// showEvents(getTableName());
				// L.out("soapList: " + soapList);
				return soapList;
			}
		}
		// failure - no network!
		// Toast.makeText(context, "No network available for downloading Soap: "
		// + soapMethod, Toast.LENGTH_SHORT);
		L.out("no network available for downloading: " + soapMethod);
		return null;
	}

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
						StaticSoapColumns.DEFAULT_SORT_ORDER);
		L.out("mCursor: " + mCursor);
		if (mCursor != null) {
			L.out("rows: " + mCursor.getCount());
		}
		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	@Override
	public long update(ContentValues values, String where) {
		L.out("update values: " + values);
		values.put(TIME_STAMP, new GregorianCalendar().getTimeInMillis() + "");
		// L.out("values: " + values);
		L.out("where: " + where);
		// mDbHelper.listTables();
		L.out("values: " + values);
		String json = values.getAsString(StaticSoapColumns.JSON);
		if (WANT_SECURITY && json != null) {
			out("before update json: " + json);
			json = SecurityUtils.encryptAES(User.getUser().getPassword(), json);
			values.put(StaticSoapColumns.JSON, json);
			out("after update compressed: " + json);
			// test
			String test = SecurityUtils.decryptAES(User.getUser().getPassword(), json);
			out("uncompressed : " + test);
		}
		long updated = getDB().update(getTableName(), values, where, null);
		L.out("updated result: " + updated);
		// not in table
		if (updated == 0) {
			long result = getDB().insert(getTableName(), null, values);
			L.out("insert result: " + result);
		}
		// showEvents(getTableName());
		super.update(values, where);
		return updated;
	}

	@Override
	public boolean uploadIfNeeded() {

		// L.out("uploadIfNeeded");
		SQLiteDatabase db = getWritableDatabase();
		if (db == null) {
			L.out("db: " + db);
			return false;
		}

		String where = AbstractDbAdapter.TIME_STAMP + " is not null";
		// L.out("where: " + where);
		Cursor cursor = db.query(getTableName(), null, where, null, null,
				null, null);
		// // L.out("raw c: " + c.getCount());
		// if (c.getCount() > 0) {
		// L.out("count to update: " + cursor.getCount());
		// }
		if (cursor.getCount() < 1) {
			return false;
		} else {
			cursor = getFirstToUpload(cursor);
			// try {
			// cursor.moveToFirst();
			String soapMethod = cursor.getString(cursor.getColumnIndexOrThrow(StaticSoapColumns.SOAP_METHOD));
			String json = cursor.getString(cursor.getColumnIndexOrThrow(StaticSoapColumns.JSON));
			String timeStamp = cursor.getString(cursor.getColumnIndexOrThrow(AbstractDbAdapter.TIME_STAMP));
			String taskNumber = cursor.getString(cursor.getColumnIndexOrThrow(StaticSoapColumns.TASK_NUMBER));
			String rowId = cursor.getString(cursor.getColumnIndexOrThrow(AbstractDbAdapter.KEY_ROWID));
			String localTaskNumber = cursor.getString(cursor.getColumnIndexOrThrow(StaticSoapColumns.LOCAL_TASK_NUMBER));
			String employeeID = cursor.getString(cursor.getColumnIndexOrThrow(StaticSoapColumns.EMPLOYEE_ID));
			String facilityID = cursor.getString(cursor.getColumnIndexOrThrow(StaticSoapColumns.FACILITY_ID));
			L.out("rowId: " + rowId);
			// L.out("timeStamp: " + timeStamp);
			L.out("taskNumber: " + taskNumber + " soapMethod: " + soapMethod + " employeeID: " + employeeID);
			if (WANT_SECURITY) {
				json = SecurityUtils.decryptAES(User.getUser().getPassword(), json);
				out("uploadIfNeeded json: " + json);
			}
			if (soapMethod.equals(ParsingSoap.VALIDATE_USER) && User.getUser().getWantBreak())
				return true;

			if (localTaskNumber == null) {
				// L.out("update the existing record:");
				// showEvents(getTableName());
				String result = new RestWriter().updateRecord(employeeID, facilityID, new ParsingSoap().bind(soapMethod, json));
				L.out("update result: " + result);
				if (result != null) {
					// L.out("soapMethod success: " + soapMethod);

					updateTimeStamp(cursor, taskNumber, rowId, null, new ContentValues());
					// updateTimeStamp(cursor, result, rowId, null, new
					// ContentValues());
				} else {
					L.out("Update failure should increment counter: " + result);
					updateTimeStamp(cursor, taskNumber, rowId, null, new ContentValues());
				}
			} else {
				// L.out("create new record: ");
				GetTaskInformationByTaskNumberAndFacilityID task =
						(GetTaskInformationByTaskNumberAndFacilityID) new ParsingSoap().bind(soapMethod, json);
				String oldTaskNumber = task.getTaskNumber();
				task.setTaskNumber(null);

				String tskNumber = new RestWriter().updateRecord(employeeID, facilityID, task);
				L.out("create result: " + tskNumber);
				// MyToast.show("create result: " + tskNumber);
				if (tskNumber != null) {

					task.setTaskNumber(tskNumber);
					task.setJson(null);
					task.setJson(task.getNewJson());

					// task.printJson();
					ContentValues contentValues = new ContentValues();
					contentValues.put(StaticSoapColumns.JSON, doEncrypt(task.getJson()));
					contentValues.put(StaticSoapColumns.TASK_NUMBER, tskNumber);
					contentValues.put(StaticSoapColumns.LOCAL_TASK_NUMBER, (String) null);
					updateTimeStamp(cursor, tskNumber, rowId, null, contentValues);
					Uri uri = Uri.parse("content://" + StaticSoap.AUTHORITY + "/" +
							ParsingSoap.GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID);
					if (SelfTaskActivity.task != null && SelfTaskActivity.task.getTaskNumber().equals(oldTaskNumber)) {
						User.getUser().getValidateUser().setTaskNumber(tskNumber);
						soapDbAdapter.context.getContentResolver().notifyChange(uri, null);
					}

				} else {
					L.out("failured to update, try again: " + tskNumber);
					// should not update TimeStamp, counter instead!
					// updateTimeStamp(cursor, timeStamp, rowId, null, new
					// ContentValues());
				}
			}
			// } catch (Exception e) {
			// L.out("*** ERROR failed update: " + e);
			// }
			return true;
		}
	}

	private Cursor getFirstToUpload(Cursor cursor) {
		int position = 0;
		int index = 0;
		int value = 0;
		cursor.moveToFirst();
		do {
			// int index = cursor.getColumnIndex(StaticSoapColumns.JSON);
			// L.out("index: " + index + " " + cursor.getClass());
			String rowId = cursor.getString(cursor.getColumnIndexOrThrow(AbstractDbAdapter.KEY_ROWID));
			int id = (int) L.getLong(rowId);
			if (id < position || position == 0) {
				position = index;
				value = id;
			}
			String taskNumber = cursor.getString(cursor.getColumnIndexOrThrow(StaticSoapColumns.TASK_NUMBER));
			if (rowId != null) {
				L.out("index: " + index + " rowId: " + rowId + " taskNumber: " + taskNumber);
			}
			index += 1;

		} while (cursor.moveToNext());
		L.out("position: " + position + " value: " + value);
		cursor.moveToPosition(position);
		return cursor;
	}

	private String doEncrypt(String json) {
		if (WANT_SECURITY) {
			out("updateStatus uncompressed: " + json);
			json = SecurityUtils.encryptAES(User.getUser().getPassword(), json);
			out("updateStatus compressed: " + json);
		}
		return json;
	}

	public void delete(String soapMethod) {
		String where = StaticSoapColumns.SOAP_METHOD + "='" + soapMethod + "'";
		Cursor cursor = getDB().query(getTableName(), null, where, null, null, null, null);
		int count = cursor.getCount();
		// L.out("delete cursor: " + count);

		if (count != 0) {
			cursor.moveToFirst();
			String rowID = cursor.getString(cursor.getColumnIndexOrThrow(AbstractDbAdapter.KEY_ROWID));
			int rowIndex = (int) L.getLong(rowID);
			// L.out("delete cursor: " + count + " rowId: " + rowIndex);
			delete(rowIndex);
		} else {
			L.out("*** Unable to delete record: " + soapMethod);
		}
	}

	public static void setSoapDbAdapter(SoapDbAdapter soapDbAdapter) {
		SoapDbAdapter.soapDbAdapter = soapDbAdapter;
	}

	// public boolean waitingForUpdate() {
	// if (User.getUser().getNeedLogout())
	// return false;
	// GetEmployeeAndTaskStatusByEmployeeID status = getTickleStatus();
	// if (status == null)
	// return true;
	// if (status.getTaskNumber() != null && status.getTaskNumber().length() >
	// 1) {
	// ValidateUser validateUser = User.getUser().getValidateUser();
	// String employeeStatus = validateUser.getEmployeeStatus();
	// if (employeeStatus.equals(BreakActivity.AT_LUNCH)
	// || employeeStatus.equals(BreakActivity.ON_BREAK)) {
	// L.out("am waiting");
	// return true;
	// }
	// }
	// return false;
	//
	// }
	//
	// private boolean waitForUpdate(String soapMethod, String json, Cursor c,
	// String rowId) {
	// if (!soapMethod.equals(ParsingSoap.VALIDATE_USER))
	// return false;
	// L.out("soapMethod: " + soapMethod);
	// if (User.getUser().getNeedLogout())
	// return false;
	// GetEmployeeAndTaskStatusByEmployeeID status = getTickleStatus();
	// if (status == null)
	// return true;
	// L.out("status.getTaskNumber(): #" + status.getTaskNumber() + "#");
	//
	// if (status.getTaskNumber() != null && status.getTaskNumber().length() >
	// 1) {
	// ValidateUser testUser = ValidateUser.getGJon(json);
	// String employeeStatus = testUser.getEmployeeStatus();
	// if (employeeStatus.equals(BreakActivity.AT_LUNCH)
	// || employeeStatus.equals(BreakActivity.ON_BREAK)) {
	// MyToast.show("You have been assigned a task,\nyour break has been stopped by Dispatch");
	// MyToast.show("You have been assigned a task,\nyour break has been stopped by Dispatch");
	// L.out("DIDNT SEND THE: " + employeeStatus + " have task: "
	// + tickleStatus.getTaskNumber() + " serverStatus: "
	// + tickleStatus.getEmployeeStatus());
	// ValidateUser validateuser = User.getUser().getValidateUser();
	// validateuser.setEmployeeStatus(tickleStatus.getEmployeeStatus());
	// testUser.setTickled(true);
	// json = testUser.getNewJson();
	// ContentValues contentValues = new ContentValues();
	// contentValues.put(StaticSoapColumns.JSON, json);
	// updateTimeStamp(c, null, rowId, null, contentValues);
	// soapDbAdapter.context.getContentResolver().notifyChange(taskModel.uri,
	// null);
	// return true;
	// }
	// }
	// return false;
	// }

	private void checkUpdate(GetEmployeeAndTaskStatusByEmployeeID tickleStatus, Cursor cursor, String rowId) {

		if (tickleStatus == null || User.getUser() == null || User.getUser().getValidateUser() == null)
			return;
		ValidateUser validateuser = User.getUser().getValidateUser();
		String employeeStatus = validateuser.getEmployeeStatus();
		// L.out("status.getTaskNumber(): #" + tickleStatus.getTaskNumber() +
		// "#");

		if (tickleStatus.getTaskNumber() != null && tickleStatus.getTaskNumber().length() > 1) {

			if (employeeStatus.equals(BreakActivity.AT_LUNCH)
					|| employeeStatus.equals(BreakActivity.ON_BREAK)) {
				// MyToast.show("You have been assigned a task,\nyour break has been stopped by Dispatch");
				// MyToast.show("You have been assigned a task,\nyour break has been stopped by Dispatch");
				L.out("DIDNT SEND THE: " + employeeStatus + " have task: "
						+ tickleStatus.getTaskNumber() + " serverStatus: "
						+ tickleStatus.getEmployeeStatus());
				updateStatus(validateuser, tickleStatus.getEmployeeStatus(), cursor, rowId);
			}
		}
		User.getUser().setWantBreak(false);
	}

	private void updateStatus(ValidateUser validateuser, String employeeStatus, Cursor cursor, String rowId) {
		L.out("employeeStatus: " + employeeStatus);
		validateuser.setEmployeeStatus(employeeStatus);
		validateuser.setTickled(true);
		String json = validateuser.getNewJson();
		ContentValues contentValues = new ContentValues();
		if (WANT_SECURITY) {
			out("updateStatus uncompressed: " + json);
			json = SecurityUtils.encryptAES(User.getUser().getPassword(), json);
			out("updateStatus compressed: " + json);
		}
		contentValues.put(StaticSoapColumns.JSON, json);
		updateTimeStamp(cursor, null, rowId, null, contentValues);
		soapDbAdapter.context.getContentResolver().notifyChange(taskModel.uri, null);
	}

	// private GetEmployeeAndTaskStatusByEmployeeID tickleStatus = null;
	// private long lastTickleTime = 0;
	// private final long tickleTimeout = 7000;

	public void tickled(Bundle data) {
		String json = data.getString(Tickler.JSON);
		if (json != null) {
			GetEmployeeAndTaskStatusByEmployeeID tickleStatus = GetEmployeeAndTaskStatusByEmployeeID.getGJon(json);
			if (tickleStatus != null) {
				String userName = User.getUser().getUsername();
				Cursor cursor = queryEmployee(userName);
				if (cursor.getCount() != 0) {
					cursor.moveToFirst();
					String rowId = cursor.getString(cursor.getColumnIndexOrThrow(AbstractDbAdapter.KEY_ROWID));
					checkUpdate(tickleStatus, cursor, rowId);
				}
				taskModel.tickled(tickleStatus);
			} else
				L.out("*** ERROR tickle error task: " + json);
		} else
			L.out("*** ERROR tickle error json: " + json);
	}

	private Cursor queryEmployee(String employeeName) {
		Cursor cursor = SoapDbAdapter.getDB().query(soapDbAdapter.getTableName(), null, getEmployeeWhere(employeeName), null, null, null, null);
		// L.out("cursor: " + cursor.getCount());
		return cursor;
	}

	private String getEmployeeWhere(String employeeName) {
		// soapDbAdapter.showEvents(soapDbAdapter.getTableName());
		String temp = StaticSoapColumns.SOAP_METHOD + "='"
				+ ParsingSoap.VALIDATE_USER + "' AND "
				+ StaticSoapColumns.EMPLOYEE_ID + "='" + employeeName + "'";
		// L.out("where: " + temp);
		return temp;
	}

	// private GetEmployeeAndTaskStatusByEmployeeID getTickleStatus() {
	// // L.out("tickleStatus: " + tickleStatus);
	// if (tickleStatus == null)
	// return null;
	// long nows = new GregorianCalendar().getTimeInMillis();
	// L.out("tickle time: " + (nows - lastTickleTime));
	// long now = new GregorianCalendar().getTimeInMillis();
	// if (now - lastTickleTime > tickleTimeout) {
	// L.out("tickle timeout: " + (now - lastTickleTime));
	// tickleStatus = null;
	// }
	// return tickleStatus;
	// }
}
