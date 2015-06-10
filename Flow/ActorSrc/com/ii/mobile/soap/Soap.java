package com.ii.mobile.soap;

import org.json.JSONObject;
//import org.ksoap2.serialization.SoapObject;

import android.content.ContentValues;

import com.ii.mobile.soap.gson.GetCurrentTaskByEmployeeID;
import com.ii.mobile.soap.gson.GetEmployeeAndTaskStatusByEmployeeID;
import com.ii.mobile.soap.gson.GetFacilityInformationByFacilityID;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsDataForScreenByFacilityID;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ListDelayTypes;
import com.ii.mobile.soap.gson.ListFunctionalAreasByFacilityID;
import com.ii.mobile.soap.gson.ListRecentTasksByEmployeeID;
import com.ii.mobile.soap.gson.ListRoomsByFacilityID;
import com.ii.mobile.soap.gson.ListTaskClassesByFacilityID;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.util.L;
import com.ii.mobile.util.PrettyPrint;

public class Soap extends BaseSoap {

	public static Soap getSoap() {
		if (soap == null) {
			soap = new Soap();
		}
		return (Soap) soap;
	}

	public ValidateUser validateUserLogin(String username, String pin) {
		L.out("validateUserLogin: " + username + " " + pin);
		// String METHOD_NAME = "JSON_MobileValidateUser";
		String METHOD_NAME = "MobileValidateUser";
		ContentValues contentValues = new ContentValues();
		contentValues.put("UserName", username);
		contentValues.put("PIN", pin);

		JSONObject jSonObject = createJSONObject(METHOD_NAME, contentValues);
		if (jSonObject == null)
			return null;
		PrettyPrint.prettyPrint(jSonObject.toString(), true);
		return ValidateUser.getGJon(jSonObject.toString());
	}

	public ListDelayTypes listDelayTypes() {
		String METHOD_NAME = "MobileListDelayTypes";
		ContentValues contentValues = new ContentValues();
		JSONObject jSonObject = createJSONObject(METHOD_NAME, contentValues);
		if (jSonObject == null)
			return null;
		return ListDelayTypes.getGJon(jSonObject.toString());
	}

	public ListFunctionalAreasByFacilityID listFunctionalAreasByFacilityID(String facilityID) {
		String METHOD_NAME = "MobileListFunctionalAreasByFacilityID";
		ContentValues contentValues = new ContentValues();
		contentValues.put("FacilityID", facilityID);
		JSONObject jSonObject = createJSONObject(METHOD_NAME, contentValues);
		L.out("jSonObject: " + jSonObject);
		if (jSonObject == null)
			return null;
		return ListFunctionalAreasByFacilityID.getGJon(jSonObject.toString());
	}

	public ListTaskClassesByFacilityID listTaskClassesByFacilityID(String facilityID) {
		String METHOD_NAME = "MobileListTaskClassesByFacilityID";
		ContentValues contentValues = new ContentValues();
		contentValues.put("FacilityID", facilityID);
		JSONObject jSonObject = createJSONObject(METHOD_NAME, contentValues);
		if (jSonObject == null)
			return null;
		return ListTaskClassesByFacilityID.getGJon(jSonObject.toString());

	}

	public ListRecentTasksByEmployeeID listRecentTasksByEmployeeID(
			String employeeID) {
		String METHOD_NAME = "MobileListRecentTasksByEmployeeID";
		ContentValues contentValues = new ContentValues();
		contentValues.put("EmployeeID", employeeID);
		JSONObject jSonObject = createJSONObject(METHOD_NAME, contentValues);
		if (jSonObject == null)
			return null;
		return ListRecentTasksByEmployeeID.getGJon(jSonObject.toString());
	}

	public ListRoomsByFacilityID listRoomsByFacilityID(String facilityID) {
		String METHOD_NAME = "MobileListRoomsByFacilityID";
		ContentValues contentValues = new ContentValues();
		contentValues.put("FacilityID", facilityID);
		JSONObject jSonObject = createJSONObject(METHOD_NAME, contentValues);
		if (jSonObject == null)
			return null;
		return ListRoomsByFacilityID.getGJon(jSonObject.toString());
	}

	public GetTaskDefinitionFieldsDataForScreenByFacilityID getTaskDefinitionFieldsDataForScreenByFacilityID(
			String facilityID) {
		String METHOD_NAME = "MobileGetTaskDefinitionFieldsDataForScreenByFacilityID";
		ContentValues contentValues = new ContentValues();
		contentValues.put("FacilityID", facilityID);
		JSONObject jSonObject = createJSONObject(METHOD_NAME, contentValues);
		if (jSonObject == null)
			return null;
		// L.out("jSonObject: " + jSonObject.toString());
		return GetTaskDefinitionFieldsDataForScreenByFacilityID.getGJon(jSonObject.toString());
	}

	public GetCurrentTaskByEmployeeID getCurrentTaskByEmployeeID(
			String employeeID) {
		String METHOD_NAME = "MobileGetCurrentTaskByEmployeeID";
		ContentValues contentValues = new ContentValues();
		contentValues.put("EmployeeID", employeeID);
		JSONObject jSonObject = createJSONObject(METHOD_NAME, contentValues);
		if (jSonObject == null)
			return null;
		return GetCurrentTaskByEmployeeID.getGJon(jSonObject.toString());
	}

	public GetTaskDefinitionFieldsForScreenByFacilityID getTaskDefinitionFieldsForScreenByFacilityID(
			String facilityID) {
		String METHOD_NAME = "MobileGetTaskDefinitionFieldsForScreenByFacilityID";
		ContentValues contentValues = new ContentValues();
		contentValues.put("FacilityID", facilityID);
		JSONObject jSonObject = createJSONObject(METHOD_NAME, contentValues);
		if (jSonObject == null)
			return null;
		return GetTaskDefinitionFieldsForScreenByFacilityID.getGJon(jSonObject.toString());
	}

	public GetEmployeeAndTaskStatusByEmployeeID getEmployeeAndTaskStatusByEmployeeID(
			String employeeID) {
		String METHOD_NAME = "MobileGetEmployeeAndTaskStatusByEmployeeID";
		ContentValues contentValues = new ContentValues();
		contentValues.put("EmployeeID", employeeID);
		JSONObject jSonObject = createJSONObject(METHOD_NAME, contentValues);
		if (jSonObject == null) {
			L.out("JsonObject is null");
			return null;
		}
		return GetEmployeeAndTaskStatusByEmployeeID.getGJon(jSonObject.toString());
	}

	public GetFacilityInformationByFacilityID getFacilityInformationByFacilityID(String facilityID) {
		String METHOD_NAME = "MobileGetFacilityInformationByFacilityID";
		ContentValues contentValues = new ContentValues();
		contentValues.put("FacilityID", facilityID);
		JSONObject jSonObject = createJSONObject(METHOD_NAME, contentValues);
		if (jSonObject == null)
			return null;
		return GetFacilityInformationByFacilityID.getGJon(jSonObject.toString());
	}

	public GetTaskInformationByTaskNumberAndFacilityID getTaskInformationByTaskNumberAndFacilityID(
			String taskNumber, String facilityID) {
		String METHOD_NAME = "MobileGetTaskInformationByTaskNumberAndFacilityID";
		ContentValues contentValues = new ContentValues();
		contentValues.put("FacilityID", facilityID);
		contentValues.put("TaskNumber", taskNumber);
		JSONObject jSonObject = createJSONObject(METHOD_NAME, contentValues);
		if (jSonObject == null)
			return null;

		return GetTaskInformationByTaskNumberAndFacilityID.getGJon(jSonObject.toString());
	}

	// public GetTaskInformationByTaskNumberAndFacilityID
	// getTaskInformationByTaskNumberAndFacilityIDOld(
	// String taskNumber, String facilityID) {
	// String METHOD_NAME =
	// "JSON_MobileGetTaskInformationByTaskNumberAndFacilityID";
	// SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	// request.addProperty("TaskNumber", taskNumber);
	// request.addProperty("FacilityID", facilityID);
	// L.out("TaskNumber: " + taskNumber);
	// L.out("FacilityID: " + facilityID);
	// JSONObject jSonObject = callSoap(request);
	// // L.out("jSonObject: " + jSonObject);
	// if (jSonObject == null)
	// return null;
	// GregorianCalendar timer = L.startTimer();
	// // L.out("time: " + L.stopTimer(timer));
	// return
	// GetTaskInformationByTaskNumberAndFacilityID.getGJon(jSonObject.toString());
	// }

	// public GetFacilityInformationByFacilityID
	// getFacilityInformationByFacilityIDOld(String facilityID) {
	// String METHOD_NAME = "JSON_MobileGetFacilityInformationByFacilityID";
	// SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	// // request.addProperty("EmployeeID", employeeID);
	// request.addProperty("facilityID", facilityID);
	// JSONObject jSonObject = callSoap(request);
	// if (jSonObject == null)
	// return null;
	// // L.out("jSonObject: " + jSonObject.toString());
	// return GetFacilityInformationByFacilityID.getGJon(jSonObject.toString());
	// }

	// public GetEmployeeAndTaskStatusByEmployeeID
	// getEmployeeAndTaskStatusByEmployeeIDOld(
	// String employeeID) {
	// String METHOD_NAME = "JSON_MobileGetEmployeeAndTaskStatusByEmployeeID";
	// SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	// request.addProperty("EmployeeID", employeeID);
	// JSONObject jSonObject = callSoap(request);
	// if (jSonObject == null)
	// return null;
	// // L.out("jSonObject: " + jSonObject.toString());
	// return
	// GetEmployeeAndTaskStatusByEmployeeID.getGJon(jSonObject.toString());
	// }

	// public GetTaskDefinitionFieldsForScreenByFacilityID
	// getTaskDefinitionFieldsForScreenByFacilityIDOld(
	// String facilityID) {
	// String METHOD_NAME =
	// "JSON_MobileGetTaskDefinitionFieldsForScreenByFacilityID";
	// SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	// request.addProperty("FacilityID", facilityID);
	// JSONObject jSonObject = callSoap(request);
	// // L.out("jSonObject: " + jSonObject.toString());
	// if (jSonObject == null)
	// return null;
	// return
	// GetTaskDefinitionFieldsForScreenByFacilityID.getGJon(jSonObject.toString());
	// }

	// public GetCurrentTaskByEmployeeID getCurrentTaskByEmployeeIDOld(
	// String employeeID) {
	// String METHOD_NAME = "JSON_MobileGetCurrentTaskByEmployeeID";
	// SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	// request.addProperty("EmployeeID", employeeID);
	// JSONObject jSonObject = callSoap(request);
	// // L.out("jSonObject: " + jSonObject.toString());
	// if (jSonObject == null)
	// return null;
	// return GetCurrentTaskByEmployeeID.getGJon(jSonObject.toString());
	// }

	// public GetTaskDefinitionFieldsDataForScreenByFacilityID
	// getTaskDefinitionFieldsDataForScreenByFacilityIDOld(
	// String facilityID) {
	// String METHOD_NAME =
	// "JSON_MobileGetTaskDefinitionFieldsDataForScreenByFacilityID";
	// SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	// request.addProperty("FacilityID", facilityID);
	// JSONObject jSonObject = callSoap(request);
	// if (jSonObject == null)
	// return null;
	// // L.out("jSonObject: " + jSonObject.toString());
	// return
	// GetTaskDefinitionFieldsDataForScreenByFacilityID.getGJon(jSonObject.toString());
	// }

	// public ListRoomsByFacilityID listRoomsByFacilityIDOLD(String facilityID)
	// {
	// String METHOD_NAME = "JSON_MobileListRoomsByFacilityID";
	// SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	// request.addProperty("FacilityID", facilityID);
	// JSONObject jSonObject = callSoap(request);
	// if (jSonObject == null)
	// return null;
	// // L.out("jSonObject: " + jSonObject.toString());
	// return ListRoomsByFacilityID.getGJon(jSonObject.toString());
	// }

	// public ListRecentTasksByEmployeeIDOld listRecentTasksByEmployeeID(
	// String employeeID) {
	// String METHOD_NAME = "JSON_MobileListRecentTasksByEmployeeID";
	// SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	// request.addProperty("EmployeeID", employeeID);
	// JSONObject jSonObject = callSoap(request);
	// // L.out("jSonObject: " + jSonObject.toString());
	// if (jSonObject == null)
	// return null;
	// return ListRecentTasksByEmployeeID.getGJon(jSonObject.toString());
	// }

	// public ListTaskClassesByFacilityID listTaskClassesByFacilityIDOld(String
	// facilityID) {
	// String METHOD_NAME = "JSON_MobileListTaskClassesByFacilityID";
	// SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	// request.addProperty("FacilityID", facilityID);
	// JSONObject jSonObject = callSoap(request);
	// if (jSonObject == null)
	// return null;
	// return ListTaskClassesByFacilityID.getGJon(jSonObject.toString());
	//
	// }

	// public ListFunctionalAreasByFacilityID
	// listFunctionalAreasByFacilityIDOld(String facilityID) {
	// String METHOD_NAME = "JSON_MobileListFunctionalAreasByFacilityID";
	// SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	// request.addProperty("FacilityID", facilityID);
	// JSONObject jSonObject = callSoap(request);
	// if (jSonObject == null)
	// return null;
	// return ListFunctionalAreasByFacilityID.getGJon(jSonObject.toString());
	// }

	// public ListDelayTypes listDelayTypesOld(
	// String employeeID, String facilityID) {
	// String METHOD_NAME = "JSON_MobileListDelayTypes";
	// SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	// request.addProperty("EmployeeID", employeeID);
	// request.addProperty("FacilityID", facilityID);
	// JSONObject jSonObject = callSoap(request);
	// if (jSonObject == null)
	// return null;
	// // L.out("jSonObject: " + jSonObject.toString());
	// return ListDelayTypes.getGJon(jSonObject.toString());
	// }
	//
	// public ValidateUser validateUserLoginOld(String username, String pin) {
	// // String METHOD_NAME = "JSON_MobileValidateUser";
	// String METHOD_NAME = "MobileValidateUser";
	// SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	// request.addProperty("UserName", username);
	// request.addProperty("PIN", pin);
	// JSONObject jSonObject = callSoap(request);
	// if (jSonObject == null)
	// return null;
	// return ValidateUser.getGJon(jSonObject.toString());
	// }

	// broken
	// public GetIsDST getIsDST() {
	// String METHOD_NAME = "JSON_MobileGetIsDST";
	// SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	// GregorianCalendar now = new GregorianCalendar();
	// request.addProperty("Time", now.getTimeInMillis() + "");
	// JSONObject jSonObject = callSoap(request);
	//
	// if (jSonObject == null)
	// return null;
	// return GetIsDST.getGJon(jSonObject.toString());
	// }

	// broken

	// broken

	// writers
	// public TaskCompleteAndUpdateEmployeeStatus
	// taskCompleteAndUpdateEmployeeStatus(
	// String functionalAreaID, String taskNumber, String employeeID, String
	// facilityID,
	// String updatedBy, String employeeCustomStatus) {
	// String METHOD_NAME = "JSON_MobileTaskCompleteAndUpdateEmployeeStatus";
	// SoapObject request = new SoapObject(NAMESPACE, METHOD_NAME);
	// request.addProperty("EmployeeID", employeeID);
	// request.addProperty("facilityID", facilityID);
	// JSONObject jSonObject = callSoap(request);
	// if (jSonObject == null)
	// return null;
	// // L.out("jSonObject: " + jSonObject.toString());
	// return
	// TaskCompleteAndUpdateEmployeeStatus.getGJon(jSonObject.toString());
	// }

	public void printWriters() {
		debugOutput("\n*** \nSOAP Update Call\n");
		debugOutput("\nTaskCompleteAndUpdateEmployeeStatus: functionalAreaID taskNumber employeeID updatedBy employeeCustomStatus\n");
		debugOutput("\nTaskComplete: AutoAssignCheck SteFunctionalArea HirNode UpdatedBy SteEmployee TaskNumber\n");
		debugOutput("\nTaskDelay: SteFunctionalArea TaskNumber SteEmployee TskDelayType HirNode UpdatedBy\n");
		debugOutput("\nTaskStart: FunctionalArea FacilityID UpdatedBy EmployeeID TaskNumber\n");
		debugOutput("\nTaskUpdate: Status FunctionalArea ModeType RequesterName CostCodeID UpdatedBy Customfield2 Item StartLocation\n"
				+ "  PersistDay Customerfield1 RequestDate AutoAssignCheck CcnRequest CustomField4 PatientName TaskNumber IsolationPatient\n"
				+ "  EquipmentType ValidateRooms Notes RequestorEmail PatientDOB ScheduleDAte CustomField5 FacilityID TaskClass\n"
				+ "  DestinationLocation RequestorPhone TaskTypeID Frequencytype CustomField3 EmployeeID");
		debugOutput("\nUpdatePersonnelStatus: SteEmployeeID steHirNode EnteredBy Status TaskNumber\n");
		debugOutput("\nZoneAssign: FacilityID Digits: EmployeeID\n");

	}
}
