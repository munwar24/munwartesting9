package com.ii.mobile.model;

import java.util.GregorianCalendar;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import com.ii.mobile.database.AbstractDbAdapter;
import com.ii.mobile.database.NetworkUploader;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.soap.ParsingSoap;
import com.ii.mobile.soap.Soap;
import com.ii.mobile.soap.SoapDbAdapter;
import com.ii.mobile.soap.StaticSoap;
import com.ii.mobile.soap.StaticSoap.StaticSoapColumns;
import com.ii.mobile.soap.gson.GetEmployeeAndTaskStatusByEmployeeID;
import com.ii.mobile.soap.gson.GetEmployeeAndTaskStatusByEmployeeID.Message;
import com.ii.mobile.soap.gson.GetEmployeeAndTaskStatusByEmployeeID.MobileMessage;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.tab.AudioPlayer;
import com.ii.mobile.tab.BreakActivity;
import com.ii.mobile.tab.IMActivity;
import com.ii.mobile.task.TaskSoap.TaskSoapColumns;
import com.ii.mobile.tickle.Tickler;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;
import com.ii.mobile.util.SecurityUtils;

public class TaskModel {

	private static int MAX_TRIES = 100;
	private static int DELAY_TIME = 3000;
	private boolean first = true;
	// private static String GET_TASK = "GetEmployeeAndTaskStatusByEmployeeID";
	private final SoapDbAdapter soapDbAdapter;
	// private final Hashtable<String, Boolean> hashtable = new
	// Hashtable<String, Boolean>();
	public final Uri uri;

	public TaskModel(SoapDbAdapter soapDbAdapter) {
		this.soapDbAdapter = soapDbAdapter;
		uri = Uri.parse("content://" + StaticSoap.AUTHORITY + "/" +
				ParsingSoap.GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID);
		L.out("uri: " + uri);

	}

	String lastReceivedDate = null;

	public void tickled(GetEmployeeAndTaskStatusByEmployeeID status) {
		if (User.getUser() == null || User.getUser().getValidateUser() == null) {
			L.out("User.getUser(): " + User.getUser());
		}
		boolean different = isDifferent(status, false);
		if (first) {
			L.out("first status: " + status);

			L.out("different: " + different);
			first = false;
		}
		// if (!different)
		// return;
		if (status.message != null) {
			Message message = status.message;
			if (message.mobileMessage == null)
				return;
			for (MobileMessage mobileMessage : message.mobileMessage) {
				Bundle data = new Bundle();
				String receivedDate = mobileMessage.receivedDate;
				L.out("mobileMessage.receivedDate: " + receivedDate);
				data.putString(Tickler.TEXT_MESSAGE, mobileMessage.textMessage);
				data.putString(Tickler.RECEIVED_DATE, mobileMessage.receivedDate);
				data.putString(Tickler.FROM_USER_NAME, mobileMessage.fromUserName);
				if (lastReceivedDate == null || !receivedDate.equals(lastReceivedDate))
					IMActivity.receivedMessage(data);
				lastReceivedDate = receivedDate;
			}
		}

		String facilityID = status.getFacilityID();
		String taskNumber = status.getTaskNumber();

		// debugPrint(status);
		// if (taskNumber != null && taskNumber.length() > 1 &&
		// hashtable.get(taskNumber) == null) {

		if (User.getUser() == null || User.getUser().getValidateUser() == null)
			return;
		ValidateUser validateUser = User.getUser().getValidateUser();
		String userName = User.getUser().getUsername();
		Cursor employeeCursor = queryEmployee(validateUser.getFacilityID(), userName);
		if (stillUpdating(employeeCursor, userName, true)) {
			String timeStamp = employeeCursor.getString(employeeCursor.getColumnIndexOrThrow(AbstractDbAdapter.TIME_STAMP));
			L.out("stillupdating: " + timeStamp);
			return;
		}
		boolean debugUpdate = stillUpdating(employeeCursor, userName, true);
		if (debugUpdate)
			L.out("stillUpdating: " + debugUpdate);
		if (User.getUser().getNeedLogout() && !stillUpdating(employeeCursor, userName, true)) {
			L.out("getNeedLogout: " + User.getUser().getNeedLogin());
			needLogout(status);
			return;
		}

		if (User.getUser().getNeedLogin() && !stillUpdating(employeeCursor, userName, true)) {
			L.out("getNeedLogin: " + User.getUser().getNeedLogin());
			needLogin(status);
			return;
		}

		// String timeStamp =
		// employeeCursor.getString(employeeCursor.getColumnIndexOrThrow(AbstractDbAdapter.TIME_STAMP));
		if (taskNumber != null && taskNumber.length() > 1) {
			// L.out("In task: " + taskNumber + " status: " +
			// status.getTaskStatus() + " userStatus: "
			// + validateUser.getTaskStatus());
			Cursor taskCursor = query(facilityID, taskNumber);
			if (taskCursor.getCount() == 0) {
				// MyToast.show("Do not have the task: " + taskNumber);
				// hashtable.put(taskNumber, true);
				new DownloadWorker().execute(status);
			} else {
				if (stillUpdating(taskCursor, taskNumber, false)) {
					L.out("stillUpdating: " + taskNumber);
					return;
				}
				String taskStatus = status.getTaskStatus();
				if (taskStatus == null || taskStatus.equals(" "))
					taskStatus = null;
				// L.out("different: " + validateUser.getTaskStatus() +
				// " taskStatus: " + taskStatus);
				// if (isDifferent(validateUser.getTaskStatus(), taskStatus))
				updateTask(status, taskCursor);
				// if (stillUpdating(employeeCursor, userName, true))
				// return;
				// updateValidateUser(status);
			}
		} else {
			// L.out("taskNumber: " + taskNumber);

		}
		if (stillUpdating(employeeCursor, userName, true))
			return;
		updateValidateUser(status);

	}

	private void needLogout(GetEmployeeAndTaskStatusByEmployeeID status) {
		if (status.getEmployeeStatus() == null)
			return;
		User.getUser().setNeedLogout(false);
		if (status.getEmployeeStatus().equals(BreakActivity.AVAILABLE)
				|| status.getEmployeeStatus().equals(BreakActivity.NOT_IN)) {
			updateEmployeeDataModel(BreakActivity.NOT_IN, false);
			User.getUser().setValidateUser(null);
			MyToast.show("Successfully Logged out");
		} else
			MyToast.show("Unable to log out!\nClear your task or break status!");
	}

	private void needLogin(GetEmployeeAndTaskStatusByEmployeeID status) {
		if (status.getEmployeeStatus() == null)
			return;
		User.getUser().setNeedLogin(false);
		if (status.getEmployeeStatus().equals(BreakActivity.NOT_IN)) {
			MyToast.show("Successfully Logged in");
			updateEmployeeDataModel(BreakActivity.AVAILABLE, false);
		} else {
			MyToast.show("Already Logged in!");
			updateEmployeeDataModel(status.getEmployeeStatus(), false);
		}

		// changed here! Test logging in with a task!
		// updateEmployeeDataModel(BreakActivity.AVAILABLE);

		soapDbAdapter.context.getContentResolver().notifyChange(uri, null);
	}

	private boolean isDifferent(GetEmployeeAndTaskStatusByEmployeeID status) {
		return isDifferent(status, false);
	}

	private boolean isDifferent(GetEmployeeAndTaskStatusByEmployeeID status, boolean wantDebug) {
		ValidateUser validateUser = User.getUser().getValidateUser();
		String taskNumber = status.getTaskNumber();
		if (taskNumber == null || taskNumber.equals(" "))
			taskNumber = null;
		String taskStatus = status.getTaskStatus();
		if (taskStatus == null || taskStatus.equals(" "))
			taskStatus = null;
		if (validateUser == null)
			return false;
		String myEmployeeStatus = validateUser.getEmployeeStatus();
		if (myEmployeeStatus == null || myEmployeeStatus.equals(" "))
			myEmployeeStatus = null;
		String myTaskStatus = validateUser.getTaskStatus();
		if (myTaskStatus == null || myTaskStatus.equals(" "))
			myTaskStatus = null;
		String employeeStatus = status.getEmployeeStatus();
		if (isDifferent(validateUser.getTaskNumber(), taskNumber)
				|| isDifferent(validateUser.getTaskStatus(), taskStatus)
				|| isDifferent(validateUser.getEmployeeStatus(), status.getEmployeeStatus())) {
			L.out("taskNumber (server:device): " + taskNumber + ":" + validateUser.getTaskNumber()
					+ " taskStatus: " + taskStatus + ":" + myTaskStatus
					+ " employeeStatus: " + employeeStatus + ":" + myEmployeeStatus);
			L.out("it is different needLogin: " + User.getUser().getNeedLogin() + " needLogout: "
					+ User.getUser().getNeedLogout());
			return true;
		}
		if (wantDebug)
			L.out("debug taskNumber (server:device): " + taskNumber + ":" + validateUser.getTaskNumber()
					+ " taskStatus: " + taskStatus + ":" + myTaskStatus
					+ " employeeStatus: " + employeeStatus + ":" + myEmployeeStatus);
		return false;
	}

	private final long WAIT_TIME = 5000;

	private boolean stillUpdating(Cursor cursor, String tag, boolean validateUser) {
		// L.out("stillUpdating: " + c.getCount());
		cursor.moveToFirst();
		if (cursor.getCount() < 1) {
			L.out("*** ERROR still Updating found no records!");
			return true;
		}
		if (validateUser && User.getUser().getWantBreak()) {
			L.out("waiting for getWantBreak");
			return false;
		}
		String timeStamp = cursor.getString(cursor.getColumnIndexOrThrow(AbstractDbAdapter.TIME_STAMP));
		String updateTimeStamp = cursor.getString(cursor.getColumnIndexOrThrow(AbstractDbAdapter.UPDATE_TIME_STAMP));
		// L.out("timeStamp: " + timeStamp);
		if (timeStamp != null) {
			L.out("updating task, ignore server update until done: " + timeStamp + " " + tag);
			return true;
		}
		Long now = new GregorianCalendar().getTimeInMillis();
		Long then = L.getLong(updateTimeStamp);
		if (then == 0) {
			// L.out("Error in parsing: " + updateTimeStamp);
			return false;
		}
		if (now - then < WAIT_TIME) {
			L.out("waiting ... " + (now - then) + " on " + tag);
			return true;
		}
		else {
			return false;
		}
	}

	private boolean updateTask(GetEmployeeAndTaskStatusByEmployeeID status, Cursor c) {
		String json = c.getString(c.getColumnIndex(StaticSoapColumns.JSON));
		// L.out("updateTask : " + status.getTaskNumber());
		if (json == null) {
			L.out("*** ERROR json is null - should not happen");
			return false;
		}
		if (SoapDbAdapter.WANT_SECURITY) {
			SoapDbAdapter.out("parse json: " + json);
			json = SecurityUtils.decryptAES(User.getUser().getPassword(), json);
			SoapDbAdapter.out("uncompressed: " + json);
		}

		GetTaskInformationByTaskNumberAndFacilityID task = GetTaskInformationByTaskNumberAndFacilityID.getGJon(json);
		task.setTaskStatusBrief(status.getTaskStatus());
		task.setTickled(true);
		ContentValues values = new ContentValues();
		String newJson = task.getNewJson();
		if (SoapDbAdapter.WANT_SECURITY) {
			// SoapDbAdapter.out("updateTask compressed newJson: " + newJson);
			newJson = SecurityUtils.encryptAES(User.getUser().getPassword(), newJson);
			// SoapDbAdapter.out("uncompressed: " + newJson);
		}
		values.put(StaticSoapColumns.JSON, newJson);
		long updated = SoapDbAdapter.getDB().update(soapDbAdapter.getTableName(), values,
				getWhere(status.getFacilityID(), status.getTaskNumber()), null);
		// L.out("updated result: " + updated);
		// soapDbAdapter.context.getContentResolver().notifyChange(uri, null);
		return true;
	}

	private boolean updateValidateUser(GetEmployeeAndTaskStatusByEmployeeID status) {

		ValidateUser validateUser = User.getUser().getValidateUser();
		if (validateUser == null) {
			L.out("validate user is null - not updating");
			// soapDbAdapter.context.getContentResolver().notifyChange(uri,
			// null);
			return false;
		}
		// strangely, the server returns a blank character if no task!
		String oldStatus = validateUser.getEmployeeStatus();
		if (isDifferent(status)) {
			String taskNumber = status.getTaskNumber();
			if (taskNumber == null || taskNumber.equals(" "))
				taskNumber = null;
			String taskStatus = status.getTaskStatus();
			if (taskStatus == null || taskStatus.equals(" "))
				taskStatus = null;
			validateUser.setTaskNumber(taskNumber);
			validateUser.setTaskStatus(taskStatus);
			validateUser.setEmployeeStatus(taskStatus);
			L.out("employeeStatus: " + taskStatus);
			updateBreak();
			updateEmployeeDataModel(status.getEmployeeStatus(), true);
			L.out("uri: " + uri);
			soapDbAdapter.context.getContentResolver().notifyChange(uri, null);
			if (taskNumber != null || !oldStatus.equals(status.getEmployeeStatus()))
				AudioPlayer.INSTANCE.playSound(AudioPlayer.DISPATCHER_CHANGE_STATE);
			// new
			// AudioPlayer(soapDbAdapter.context).playSound(AudioPlayer.DISPATCHER_CHANGE_STATE);
			return true;
		}
		return false;
	}

	private void updateBreak() {
		ValidateUser validateUser = User.getUser().getValidateUser();
		if (BreakActivity.breakActivity != null)
			BreakActivity.breakActivity.updateStatus(validateUser.getEmployeeStatus(), true);
	}

	private boolean isDifferent(String first, String second) {
		if (first == null && second == null)
			return false;
		if (first == null)
			return true;
		if (first.equals(second))
			return false;
		return true;
	}

	private Cursor query(String facilityID, String taskNumber) {
		Cursor cursor = SoapDbAdapter.getDB().query(soapDbAdapter.getTableName(), null, getWhere(facilityID, taskNumber), null, null, null, null);
		// L.out("cursor: " + cursor.getCount());
		return cursor;
	}

	private Cursor queryEmployee(String facilityID, String employeeID) {
		Cursor cursor = SoapDbAdapter.getDB().query(soapDbAdapter.getTableName(), null, getEmployeeWhere(facilityID, employeeID), null, null, null, null);
		// L.out("cursor: " + cursor.getCount());
		return cursor;
	}

	private String getEmployeeWhere(String facilityID, String employeeID) {
		// soapDbAdapter.showEvents(soapDbAdapter.getTableName());
		String temp = StaticSoapColumns.SOAP_METHOD + "='"
				+ ParsingSoap.VALIDATE_USER + "' AND "
				+ StaticSoapColumns.EMPLOYEE_ID + "='" + employeeID + "'";
		// L.out("where: " + temp);
		return temp;
	}

	private static String getWhere(String facilityID, String taskNumber) {
		String temp = StaticSoapColumns.SOAP_METHOD + "='"
				+ ParsingSoap.GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID + "' AND "
				+ StaticSoapColumns.FACILITY_ID + "='" + facilityID + "'";

		if (taskNumber != null)
			temp += " AND " + StaticSoapColumns.TASK_NUMBER + "='" + taskNumber + "'";
		// L.out("where: " + temp);
		return temp;
	}

	private static String getNotWhere(String facilityID, String taskNumber) {
		String temp = StaticSoapColumns.SOAP_METHOD + "='"
				+ ParsingSoap.GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID + "' AND "
				+ StaticSoapColumns.FACILITY_ID + "='" + facilityID + "'";

		if (taskNumber != null)
			temp += " AND " + StaticSoapColumns.TASK_NUMBER + " is not '" + taskNumber + "'";
		// L.out("where: " + temp);
		return temp;
	}

	// labels
	class DownloadWorker extends AsyncTask<GetEmployeeAndTaskStatusByEmployeeID, Integer, Long> {

		@Override
		protected Long doInBackground(GetEmployeeAndTaskStatusByEmployeeID... params) {
			Thread.currentThread().setName("ModelFetchThread");
			GetEmployeeAndTaskStatusByEmployeeID status = params[0];
			String taskNumber = status.getTaskNumber();
			String facilityID = status.getFacilityID();
			String employeeID = User.getUser().getValidateUser().getEmployeeID();
			Soap soap = new Soap();
			int count = 0;
			while (count < MAX_TRIES) {
				count += 1;
				if (NetworkUploader.isConnectedToInternet()) {
					GetTaskInformationByTaskNumberAndFacilityID task =
							soap.getTaskInformationByTaskNumberAndFacilityID(taskNumber,
									facilityID);
					if (task == null) {
						MyToast.show("ERROR Failed to get a task: " + taskNumber);
						ValidateUser validateUser = User.getUser().getValidateUser();
						validateUser.setTaskNumber(null);
						return 1l;
					} else {
						long result = soapDbAdapter.create(ParsingSoap.GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID,
								task.getJson(), employeeID, facilityID, taskNumber);
						L.out("result: " + result);
						// MyToast.show("Received a task: " + taskNumber +
						// " result: " + result);
						if (result != 0) {
							ValidateUser validateUser = User.getUser().getValidateUser();
							validateUser.setTaskNumber(taskNumber);
							validateUser.setTaskStatus(status.getTaskStatus());
							validateUser.setEmployeeStatus(status.getTaskStatus());
							// delete the previous tasks
							L.out("getNotWhere: " + getNotWhere(facilityID, taskNumber));
							result = AbstractDbAdapter.getDB().delete(soapDbAdapter.getTableName(), getNotWhere(facilityID, taskNumber), null);
							// soapDbAdapter.showEvents(soapDbAdapter.getTableName());
							// L.out("deleted result: " + result);
							return 0l;
						}
					}
					L.sleep(DELAY_TIME);
				}
			}
			return 0l;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			L.out("probably not used but executes in UI thread");
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(Long l) {
			L.out("uri: " + uri);
			if (l == 0l) {
				soapDbAdapter.context.getContentResolver().notifyChange(uri, null);
				// new
				// AudioPlayer(soapDbAdapter.context).playSound(AudioPlayer.NEW_TASK);
				AudioPlayer.INSTANCE.playSound(AudioPlayer.NEW_TASK);
			} else {
				MyToast.show("Ignore task not found!");
			}
		}

	}

	synchronized void updateEmployeeDataModel(String employeeStatus, boolean tickled) {
		ValidateUser validateUser = User.getUser().getValidateUser();
		L.out("update the updateEmployeeDataModel here: "
				+ User.getUser().getValidateUser().getEmployeeStatus() + " to " + employeeStatus);
		String facilityID = User.getUser().getPassword();
		String employeeID = User.getUser().getUsername();
		String taskNumber = User.getUser().getPlatform();
		String soapMethod = ParsingSoap.VALIDATE_USER;
		validateUser.setEmployeeStatus(employeeStatus);
		validateUser.setTickled(tickled);

		ContentValues values = new ContentValues();
		values.put(TaskSoapColumns.JSON, validateUser.getNewJson());
		values.put(TaskSoapColumns.FACILITY_ID, User.getUser().getPassword());
		values.put(TaskSoapColumns.EMPLOYEE_ID, User.getUser().getUsername());
		values.put(TaskSoapColumns.TASK_NUMBER, taskNumber);
		values.put(TaskSoapColumns.SOAP_METHOD, soapMethod);

		soapDbAdapter.update(values, SoapDbAdapter.getWhere(soapMethod, employeeID, facilityID, taskNumber));
		// validateUser.setEmployeeStatus(employeeStatus);
	}

}
