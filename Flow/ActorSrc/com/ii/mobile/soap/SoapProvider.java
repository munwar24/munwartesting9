/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.soap;

import java.util.Hashtable;
import java.util.List;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;

import com.ii.mobile.soap.cursor.SoapCursor;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.task.TaskSoap.TaskSoapColumns;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

/**
 * 
 * @author kfairchild
 */
public class SoapProvider extends ContentProvider {

	private SoapDbAdapter soapDbAdapter;

	private final Hashtable<String, List<GJon>> hashtable = new Hashtable<String, List<GJon>>();

	@Override
	public boolean onCreate() {
		// L.out("onCreate");
		soapDbAdapter = new SoapDbAdapter(getContext());
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
			String sortOrder) {
		String soapMethod = uri.getLastPathSegment();
		// L.out("soapMethod: " + soapMethod);
		String employeeID = null;
		String facilityID = null;
		String taskNumber = null;
		if (selectionArgs != null && selectionArgs.length > 0)
			employeeID = selectionArgs[0];
		if (selectionArgs != null && selectionArgs.length > 1)
			facilityID = selectionArgs[1];
		if (selectionArgs != null && selectionArgs.length > 2)
			taskNumber = selectionArgs[2];

		List<GJon> gJonList = hashtable.get(getSignature(soapMethod, employeeID, facilityID, taskNumber));
		// gJonList = null;
		if (gJonList == null) {
			// L.out("uri: " + uri + " employeeID: " + employeeID +
			// " facilityID: " + facilityID + " "
			// + taskNumber);
			gJonList = soapDbAdapter.parse(uri, employeeID, facilityID, taskNumber);
			// L.out("gJonList: " + gJonList);
			if (gJonList == null)
				return null;
			// L.out("gJonList: " + gJonList);
			for (GJon gjon : gJonList) {
				if (soapMethod.equals(ParsingSoap.VALIDATE_USER)) {
					L.out("gjon: " + gjon);
					updateValidateUser(gjon);
					employeeID = User.getUser().getEmployeeID();
					facilityID = User.getUser().getFacilityID();
				}
			}
		}
		Cursor c = new SoapCursor(gJonList);
		return c;
	}

	private String getSignature(String soapMethod, String employeeID, String facilityID, String taskNumber) {
		return soapMethod + "_" + employeeID + "_" + facilityID + "_" + taskNumber;
	}

	@Override
	public String getType(Uri uri) {
		L.out("uri: " + uri);
		return null;
	}

	@Override
	public Uri insert(Uri arg0, ContentValues arg1) {
		L.out("insert");
		throw new UnsupportedOperationException("insert Not supported yet.");
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] values) {
		String employeeID = values[0];
		String facilityID = values[1];
		String soapMethod = values[2];
		// L.out("delete: " + soapMethod);
		List<GJon> gJonList = hashtable.get(getSignature(soapMethod, employeeID, facilityID, null));
		if (gJonList != null)
			hashtable.remove(gJonList);
		else {
			// L.out("*** unable to find and remove in hash: "
			// + hashtable.get(getSignature(soapMethod, employeeID, facilityID,
			// null)));
		}
		soapDbAdapter.delete(soapMethod);
		return 0;
	}

	@Override
	public int update(Uri uri, ContentValues values, String arg, String[] arg3) {
		L.out(" update uri: " + uri);
		String taskNumber = values.getAsString(TaskSoapColumns.TASK_NUMBER);
		String employeeID = values.getAsString(TaskSoapColumns.EMPLOYEE_ID);
		String facilityID = values.getAsString(TaskSoapColumns.FACILITY_ID);
		String soapMethod = values.getAsString(TaskSoapColumns.SOAP_METHOD);
		String json = values.getAsString(TaskSoapColumns.JSON);
		values.put(TaskSoapColumns.JSON, json);
		// L.out("before update json: " + json);
		// json = SecurityUtils.encryptAES(User.getUser().getPassword(), json);
		// values.put(TaskSoapColumns.JSON, json);
		// L.out("update json: " + json);

		List<GJon> gJonList = hashtable.get(getSignature(soapMethod, employeeID, facilityID, taskNumber));
		if (gJonList != null)
			hashtable.remove(gJonList);
		soapDbAdapter.update(values, SoapDbAdapter.getWhere(soapMethod, employeeID, facilityID, taskNumber));
		// getContext().getContentResolver().notifyChange(uri, null);
		return 1;
	}

	private void updateValidateUser(GJon gjon) {
		ValidateUser validateUser = ValidateUser.getGJon(gjon.getJson());
		User.getUser().setValidateUser(validateUser);
	}

}
