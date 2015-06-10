package com.ii.mobile.home;

import java.util.Enumeration;
import java.util.Hashtable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ii.mobile.soap.ParsingSoap;
import com.ii.mobile.soap.StaticSoap;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class StaticLoader {

	private static boolean singleTime = false;
	Activity activity;
	static Hashtable<StaticState, String> hashtable = null;

	private boolean success;

	String[] soapMethods = new String[] {
			// ParsingSoap.VALIDATE_USER, // 0
			// ParsingSoap.LIST_RECENT_TASKS_BY_EMPLOYEE_ID, // 2
			// ParsingSoap.LIST_ROOMS_BY_FACILITY_ID, // 3
			// old step 3
			// ParsingSoap.GET_CURRENT_TASK_BY_EMPLOYEE_ID, // 4
			ParsingSoap.LIST_TASK_CLASSES_BY_FACILITY_ID, // 7
			ParsingSoap.GET_TASK_DEFINITION_FIELDS_DATA_FOR_SCREEN_BY_FACILITY_ID, // 5
			// ParsingSoap.LIST_ROOMS_BY_FACILITY_ID, // 6

			ParsingSoap.LIST_DELAY_TYPES, // 9
			ParsingSoap.GET_TASK_DEFINITION_FIELDS_FOR_SCREEN_BY_FACILITYID, // 8

	};

	// public static void initHashtable() {
	// hashtable = null;
	// }

	class StaticState {
		String methodName;
		Cursor cursor = null;
		String facilityID = null;

		public StaticState(String methodName) {
			this.methodName = methodName;
		}

		@Override
		public String toString() {
			return "StaticState: " + methodName + " cursor: " + cursor + " " + facilityID;
		}
	}

	public StaticLoader(Activity activity) {
		this.activity = activity;
		getHashTable();
	}

	private void printTable() {
		Hashtable<StaticState, String> hashtable = getHashTable();
		Enumeration<StaticState> elements = hashtable.keys();
		while (elements.hasMoreElements()) {
			StaticState foo = elements.nextElement();
			L.out("foo: " + foo);
		}
	}

	private Hashtable<StaticState, String> getHashTable() {
		if (hashtable != null)
			return hashtable;
		hashtable = new Hashtable<StaticState, String>();
		for (int i = 0; i < soapMethods.length; i++) {
			L.out("soapMethods: " + soapMethods[i]);
			hashtable.put(new StaticState(soapMethods[i]), soapMethods[i]);
		}
		return hashtable;
	}

	public void execute() {
		success = true;
		new DownloadStaticTask().execute();
	}

	public Cursor getCursor(String methodName, String taskNumber) {
		L.out("getCursor: " + methodName);
		// String facilityID = User.getUser().getValidateUser().getFacilityID();
		// String employeeID = User.getUser().getValidateUser().getEmployeeID();
		String facilityID = User.getUser().getFacilityID();
		String employeeID = User.getUser().getEmployeeID();
		L.out("facilityID: " + facilityID);
		L.out("employeeID: " + employeeID);
		if (User.getUser().getValidateUser() == null) {
			L.out("intentional crash!");
			// int i = 100 / 0;
			return null;
		}
		Intent intent = activity.getIntent();
		// if
		// (methodName.equals(ParsingSoap.GET_TASK_DEFINITION_FIELDS_DATA_FOR_SCREEN_BY_FACILITY_ID)
		// ||
		// methodName.equals(ParsingSoap.GET_TASK_DEFINITION_FIELDS_FOR_SCREEN_BY_FACILITYID)
		// || methodName.equals(ParsingSoap.LIST_TASK_CLASSES_BY_FACILITY_ID)
		// || methodName.equals(ParsingSoap.LIST_DELAY_TYPES))
		// employeeID = null;
		String[] selectionArgs = new String[] { employeeID, facilityID, taskNumber };
		intent.setData(Uri.parse("content://" + StaticSoap.AUTHORITY + "/" +
				methodName));
		Cursor cursor = activity.managedQuery(activity.getIntent().getData(), null, null, selectionArgs, null);
		if (cursor != null && cursor.getCount() > 0)
			return cursor;
		return null;
	}

	class DownloadStaticTask extends AsyncTask<Void, Integer, Long> {
		private final long LOADING = 0;
		private final long ERROR = 1;
		private final long LOADED_ALREADY = 2;
		private ProgressDialog progressDialog = null;

		@Override
		protected Long doInBackground(Void... arg0) {
			Thread.currentThread().setName("StaticLoaderThread");
			printTable();
			long count = LOADING;
			Enumeration<StaticState> e = hashtable.keys();

			while (e.hasMoreElements()) {
				StaticState staticState = e.nextElement();
				L.out("staticState: " + staticState);
				String facilityID = User.getUser().getFacilityID();
				if (true || staticState.cursor == null
						|| staticState.facilityID == null
						|| facilityID == null
						|| !facilityID.equals(staticState.facilityID)) {
					staticState.cursor = getCursor(staticState.methodName, null);
					staticState.facilityID = facilityID;
					// if (Math.random() > .55) {
					// L.out("failure inserted for: " + staticState);
					// staticState.cursor = null;
					// }
					if (staticState.cursor == null || staticState.cursor.getCount() == 0) {
						L.out("Failed to load: " + staticState.methodName);
						success = false;
						count = ERROR;
					}

				} else {
					L.out("already have: " + staticState);
					// count = LOADED_ALREADY;
				}
			}

			// MyToast.show("...Loaded static data");
			printTable();
			L.out("count: " + count);
			return count;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			L.out("probably not used but executes in UI thread");
		}

		@Override
		protected void onPreExecute() {
			if (missingAny() || true) {
				// MyToast.show("Loading static content ...");
				progressDialog = new ProgressDialog(activity);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setMessage("One-time load of static content ...");
				progressDialog.setCancelable(false);
				progressDialog.show();

			}
		}

		private boolean missingAny() {
			Enumeration<StaticState> e = hashtable.keys();
			while (e.hasMoreElements()) {
				StaticState staticState = e.nextElement();
				if (staticState.cursor == null)
					return true;
			}
			L.out("not missing any!");
			return false;
		}

		@Override
		protected void onPostExecute(Long l) {
			if (l == LOADING) {
				// MyToast.show("... Loaded static content  ");
			}
			if (l == ERROR) {
				String temp = "Failed loading Static Content!"
						+ "\nYou are welcome to try again"
						+ "\n(just press Enter)."
						+ "\nOr you may wait until"
						+ "\nyou have better WI-FI."
						+ "\n";
				MyToast.show(temp, Toast.LENGTH_LONG);
				MyToast.show(temp, Toast.LENGTH_LONG);

				activity.finish();
			}
			if (l == LOADED_ALREADY) {
				// MyToast.show("already loaded");
			}
			if (progressDialog != null) {
				try {
					progressDialog.dismiss();
				} catch (Exception e) {
					L.out("dismissed exception: " + e);
				}
			}
		}
	}

}
