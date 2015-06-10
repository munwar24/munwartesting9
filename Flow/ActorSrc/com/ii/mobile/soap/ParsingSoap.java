package com.ii.mobile.soap;

import java.util.ArrayList;
import java.util.List;

import android.net.Uri;

import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.soap.gson.GetCurrentTaskByEmployeeID;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsDataForScreenByFacilityID;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ListDelayTypes;
import com.ii.mobile.soap.gson.ListRecentTasksByEmployeeID;
import com.ii.mobile.soap.gson.ListRoomsByFacilityID;
import com.ii.mobile.soap.gson.ListTaskClassesByFacilityID;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class ParsingSoap {
	public final static String VALIDATE_USER = "ValidateUser";
	public final static String GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID = "GetTaskInformationByTaskNumberAndFacilityID";
	public final static String LIST_RECENT_TASKS_BY_EMPLOYEE_ID = "ListRecentTasksByEmployeeID";
	// public final static String = "getIsDST";
	public final static String LIST_FUNCTIONAL_AREAS_BY_FACILITY_ID = "ListFunctionalAreasByFacilityID";
	public final static String LIST_TASK_CLASSES_BY_FACILITY_ID = "ListTaskClassesByFacilityID";
	public final static String LIST_ROOMS_BY_FACILITY_ID = "ListRoomsByFacilityID";
	public final static String GET_TASK_DEFINITION_FIELDS_DATA_FOR_SCREEN_BY_FACILITY_ID = "GetTaskDefinitionFieldsDataForScreenByFacilityID";
	public final static String GET_TASK_DEFINITION_FIELDS_FOR_SCREEN_BY_FACILITYID = "GetTaskDefinitionFieldsForScreenByFacilityID";
	public final static String LIST_DELAY_TYPES = "ListDelayTypes";

	public final static String GET_CURRENT_TASK_BY_EMPLOYEE_ID = "GetCurrentTaskByEmployeeID";
	public final static String GET_EMPLOYEE_AND_TASK_STATUS_BY_EMPLOYEE_ID = "GetEmployeeAndTaskStatusByEmployeeID";
	public final static String GET_FACILITY_INFORMATION_BY_FACILITY_ID = "GetFacilityInformationByFacilityID";

	public final static String TASK_COMPLETE_AND_UPDATE_EMPLOYEE_STATUS = "TaskCompleteAndUpdateEmployeeStatus";

	public final static String NO_STRING_JSON = "{\"currentTask\":\"\"}";

	String[] allSoapMethods = new String[] {
			VALIDATE_USER, // 0
			GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID, // 1
			LIST_RECENT_TASKS_BY_EMPLOYEE_ID, // 2
			LIST_ROOMS_BY_FACILITY_ID, // 3
			GET_CURRENT_TASK_BY_EMPLOYEE_ID, // 4
			GET_TASK_DEFINITION_FIELDS_DATA_FOR_SCREEN_BY_FACILITY_ID, // 5
			LIST_ROOMS_BY_FACILITY_ID, // 6
			LIST_TASK_CLASSES_BY_FACILITY_ID, // 7
			GET_TASK_DEFINITION_FIELDS_FOR_SCREEN_BY_FACILITYID, // 8
			LIST_DELAY_TYPES, // 9

			LIST_FUNCTIONAL_AREAS_BY_FACILITY_ID,
			GET_EMPLOYEE_AND_TASK_STATUS_BY_EMPLOYEE_ID,
			GET_FACILITY_INFORMATION_BY_FACILITY_ID,
			GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID,

			TASK_COMPLETE_AND_UPDATE_EMPLOYEE_STATUS
	};

	public List<GJon> build(Uri uri, String employeeID, String facilityID, String taskNumber) {
		// L.out("uri: " + uri);
		String methodName = uri.getPathSegments().get(0);
		// L.out("methodName: " + methodName + " employeeID: " + employeeID +
		// " facilityID: " + facilityID
		// + " taskNumber: " + taskNumber);

		int index = findIndex(methodName);
		if (index == -1) {
			L.out("Error, unable to find index for: " + methodName);
			return null;
		}
		List<GJon> list = new ArrayList<GJon>();
		User user = User.getUser();
		Soap soap = new Soap();

		// validateUser
		if (index == 0) {
			String userName = user.getUsername();
			String pin = user.getPassword();
			L.out("user: " + user);
			ValidateUser validateUser = soap.validateUserLogin(userName, pin);
			if (validateUser == null)
				return null;
			list.add(validateUser);
			return list;
		}
		// GetTaskInformationByTaskNumberAndFacilityID
		if (index == 1) {
			GetTaskInformationByTaskNumberAndFacilityID getTaskInformationByTaskNumberAndFacilityID =
					soap.getTaskInformationByTaskNumberAndFacilityID(taskNumber,
							facilityID);
			// L.out("getTaskInformationByTaskNumberAndFacilityID: "
			// + getTaskInformationByTaskNumberAndFacilityID);
			if (getTaskInformationByTaskNumberAndFacilityID == null)
				return null;
			list.add(getTaskInformationByTaskNumberAndFacilityID);
			return list;
		}
		// listRecentTasksByEmployeeID
		if (index == 2) {
			ListRecentTasksByEmployeeID listRecentTasksByEmployeeID =
					soap.listRecentTasksByEmployeeID(employeeID);
			// L.out("listRecentTasksByEmployeeID: " +
			// listRecentTasksByEmployeeID);
			if (listRecentTasksByEmployeeID == null)
				return null;
			list.add(listRecentTasksByEmployeeID);
			return list;
		}
		// listRoomsByFacilityID
		if (index == 3) {
			ListRoomsByFacilityID listRoomsByFacilityID =
					soap.listRoomsByFacilityID(facilityID);
			// L.out("\n***\n" + listRoomsByFacilityID.toString());
			if (listRoomsByFacilityID == null) {
				return null;
			}
			list.add(listRoomsByFacilityID);
			return list;
		}
		// getCurrentTaskByEmployeeID
		if (index == 4) {
			GetCurrentTaskByEmployeeID getCurrentTaskByEmployeeID =
					soap.getCurrentTaskByEmployeeID(employeeID);
			// Need a dummy one if failed so don't keep trying - Tickler will
			// tell us to update
			if (getCurrentTaskByEmployeeID == null) {
				GJon gJon = new GJon();
				gJon.setJson(NO_STRING_JSON);
				// L.out("gJon.getJson: " + gJon.getJson());
				list.add(gJon);
				return list;
			}
			// return null;

			// L.out(getCurrentTaskByEmployeeID.toString());
			list.add(getCurrentTaskByEmployeeID);
			return list;
		}
		// getTaskDefinitionFieldsDataForScreenByFacilityID
		if (index == 5) {
			GetTaskDefinitionFieldsDataForScreenByFacilityID getTaskDefinitionFieldsDataForScreenByFacilityID =
					soap.getTaskDefinitionFieldsDataForScreenByFacilityID(facilityID);

			if (getTaskDefinitionFieldsDataForScreenByFacilityID == null)
				return null;
			// L.out(getTaskDefinitionFieldsDataForScreenByFacilityID.toString());
			list.add(getTaskDefinitionFieldsDataForScreenByFacilityID);
			return list;
		}

		// listRoomsByFacilityID
		if (index == 6) {
			ListRoomsByFacilityID listRoomsByFacilityID =
					soap.listRoomsByFacilityID(facilityID);

			if (listRoomsByFacilityID == null)
				return null;

			list.add(listRoomsByFacilityID);
			return list;
		}

		// getTaskDefinitionFieldsDataForScreenByFacilityID
		if (index == 7) {
			ListTaskClassesByFacilityID listTaskClassesByFacilityID =
					soap.listTaskClassesByFacilityID(facilityID);
			if (listTaskClassesByFacilityID == null)
				return null;
			list.add(listTaskClassesByFacilityID);
			return list;
		}

		if (index == 8) {
			GetTaskDefinitionFieldsForScreenByFacilityID getTaskDefinitionFieldsForScreenByFacilityID =
					soap.getTaskDefinitionFieldsForScreenByFacilityID(facilityID);
			list.add(getTaskDefinitionFieldsForScreenByFacilityID);
			return list;
		}

		if (index == 9) {
			ListDelayTypes listDelayTypes =
					soap.listDelayTypes();
			list.add(listDelayTypes);
			return list;
		}

		L.out("Index error for: " + methodName + " index: " + index);
		return null;
	}

	protected int findIndex(String method) {
		for (int i = 0; i < allSoapMethods.length; i++) {
			if (allSoapMethods[i].equals(method))
				return i;
		}
		L.out("Unable to find index for method: " + method);
		return -1;
	}

	public GJon bind(String soapMethod, String json) {
		int index = findIndex(soapMethod);
		if (index == -1) {
			L.out("Error, unable to find index for: " + soapMethod);
			return null;
		}
		if (index == 0) {
			return ValidateUser.getGJon(json);
		}
		if (index == 1) {
			return GetTaskInformationByTaskNumberAndFacilityID.getGJon(json);
		}
		if (index == 2) {
			return ListRecentTasksByEmployeeID.getGJon(json);
		}

		if (index == 5) {
			return GetTaskDefinitionFieldsDataForScreenByFacilityID.getGJon(json);
		}

		if (index == 7) {
			return ListTaskClassesByFacilityID.getGJon(json);
		}
		if (index == 8) {
			return GetTaskDefinitionFieldsForScreenByFacilityID.getGJon(json);
		}
		if (index == 9) {
			return ListDelayTypes.getGJon(json);
		}
		L.out("Index error for: " + soapMethod + " " + index);
		return null;
	}
}
