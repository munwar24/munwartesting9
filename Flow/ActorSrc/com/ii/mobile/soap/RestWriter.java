package com.ii.mobile.soap;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.os.StrictMode;

import com.ii.mobile.home.MyToast;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.soap.gson.GetEmployeeAndTaskStatusByEmployeeID;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.tab.BreakActivity;
import com.ii.mobile.tab.SelfTaskActivity;
import com.ii.mobile.tab.StatusType;
import com.ii.mobile.tab.TabNavigationActivity;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class RestWriter extends RestService {
	public static final String SUCCESSFUL = "success";
	public static final String FAILURE = "fail";

	public static final String TASK_START = "MobileTaskStart";
	public static final String TASK_DELAY = "MobileTaskDelay";
	public static final String TASK_COMPLETE = "MobileTaskComplete";
	public static final String TASK_COMPLETE_AND_UPDATE_EMPLOYEE_STATUS = "MobileTaskCompleteAndUpdateEmployeeStatus";
	public static final String UPDATE_TASK_METHOD = "MobileTaskUpdate";
	public final static String MOBILE_UPDATE_PERSONNEL_STATUS = "MobileUpdatePersonnelStatus";
	public final static String MOBILE_TASK_UPDATE = "MobileTaskUpdate";

	public RestWriter() {
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	public String updateRecord(String employeeID, String facilityID, GJon record) {
		// no idea why doesn't call the correct specialization directly! kmf
		// L.out("*** ERROR Should be specialized: " + record.getClass());
		if (record instanceof ValidateUser)
			return updateValidateUser((ValidateUser) record);
		else if (record instanceof GetTaskInformationByTaskNumberAndFacilityID)
			return updateTask(employeeID, facilityID, (GetTaskInformationByTaskNumberAndFacilityID) record);
		else
			L.out("RestWriter.updateRecord error: \n" + record);
		return null;
	}

	public String updateValidateUser(ValidateUser validateUser) {

		String updateMethod = MOBILE_UPDATE_PERSONNEL_STATUS;
		String status = validateUser.getEmployeeStatus();
		L.out("updateRecord status: " + status);
		if (validateUser.getTickled()) {
			L.out("RestWriter validateUser was tickled ignoring update: " + status);
			return SUCCESSFUL;
		}

		if (checkIfTheCoastIsClear(validateUser)) {
			String statusCode = StatusType.lookUp(status);
			L.out("updateRecord statusCode: " + statusCode);
			List<Arg> values = new ArrayList<RestWriter.Arg>();
			values.add(new Arg("Status", statusCode));
			values.add(new Arg("TaskNumber", ""));
			values.add(new Arg("SteEmployeeID", validateUser.getEmployeeID()));
			values.add(new Arg("SteHirNode", validateUser.getFacilityID()));
			values.add(new Arg("EnteredBy", validateUser.getMobileUserName()));

			L.out("RestWriter.updateRecord validateUser: " + updateMethod);
			if (TabNavigationActivity.showToast)
				MyToast.show("Update Actor to "
						+ "Status: " + status);

			String result = mobileUpdateRecord(BaseSoap.URL + "/" + updateMethod, getNameValuePairs(values));
			L.out("result: " + result);
			return result;
		} else {
			// MyToast.show("Ignoring change to break:\nTask assigned!");
			// MyToast.show("Ignoring change to break:\nTask assigned!");
			return null;
		}
	}

	private boolean checkIfTheCoastIsClear(ValidateUser validateUser) {
		String currentEmployeeStatus = validateUser.getEmployeeStatus();
		if (true || currentEmployeeStatus.equals(BreakActivity.AT_LUNCH)
				|| currentEmployeeStatus.equals(BreakActivity.ON_BREAK)) {
			// MyToast.show("getting it");
			GetEmployeeAndTaskStatusByEmployeeID taskStatus =
					Soap.getSoap().getEmployeeAndTaskStatusByEmployeeID(validateUser.getEmployeeID());

			L.out("taskStatus: " + taskStatus);

			if (taskStatus == null) {
				L.out("*** ERROR getEmployeeAndTaskStatusByEmployeeID is null! "
						+ validateUser);
			}
			String taskNumber = taskStatus.getTaskNumber();
			if (taskNumber != null) {
				MyToast.show("Ignore Actor update\nServer has taskNumber:  " + taskNumber);
			}
			return false;
		}
		return true;
	}

	public String updateTask(String employeeID, String facilityID,
			GetTaskInformationByTaskNumberAndFacilityID task) {
		// L.out("RestWriter.updateTask: " + task);
		if (task.getTickled()) {
			L.out("RestWriter Task was tickled ignoring update: " + task.getTaskStatusBrief());
			return SUCCESSFUL;
		}
		String taskNumber = task.getTaskNumber();
		L.out("updateTask status: " + taskNumber);
		if (taskNumber == null || L.getLong(taskNumber) != 0l)
			return createRecord(task);
		String updateMethod = null;
		String status = task.getTaskStatusBrief();
		L.out("updateTask status: " + status);
		ValidateUser validateUser = User.getUser().getValidateUser();
		List<Arg> values = new ArrayList<RestWriter.Arg>();
		values.add(new Arg("FunctionalAreaID", SelfTaskActivity.getFunctionalArea()));
		values.add(new Arg("TaskNumber", task.getTaskNumber()));
		values.add(new Arg("EmployeeID", employeeID));
		values.add(new Arg("FacilityID", facilityID));
		String mobileUserName = task.getMobileUserName();
		if (validateUser != null)
			mobileUserName = validateUser.getMobileUserName();
		L.out("task.getMobileUserName(): " + task.getMobileUserName() + " "
				+ (validateUser != null ? validateUser.getMobileUserName() : "NULL VALIDATE"));
		values.add(new Arg("UpdatedBy", mobileUserName));

		if (status.equals(SelfTaskActivity.ASSIGNED)) {
		}
		if (status.equals(SelfTaskActivity.ACTIVE)) {
			updateMethod = TASK_START;
			if (TabNavigationActivity.showToast)
				MyToast.show("Active Action: " + task.getTaskNumber());
		}
		if (status.equals(SelfTaskActivity.DELAYED)) {
			updateMethod = TASK_DELAY;
			L.out("delayed: " + task.getDelayType());
			if (TabNavigationActivity.showToast)
				MyToast.show("Delay Action: " + task.getDelayType());
			values.add(new Arg("TskDelayType", task.getDelayType()));
		}
		if (status.equals(SelfTaskActivity.COMPLETED)) {
			// updateMethod = TASK_COMPLETE;
			updateMethod = TASK_COMPLETE_AND_UPDATE_EMPLOYEE_STATUS;
			L.out("completed: " + task.getCompleteTo());
			// values.add(new Arg("AutoAssignCheck", "false"));
			if (TabNavigationActivity.showToast)
				MyToast.show("Complete to Action: " + task.getCompleteTo());
			values.add(new Arg("EmployeeCustomStatus", task.getCompleteTo()));
		}
		if (status.equals(SelfTaskActivity.CANCELED)) {
		}
		if (updateMethod == null) {
			L.out("Nothing to do for this status: " + status);
			return SUCCESSFUL;
		}
		L.out("update task");
		for (Arg arg : values) {
			L.out("pair: " + arg.type + " " + arg.value);
		}
		if (TabNavigationActivity.showToast)
			MyToast.show("Update Action to "
					+ "Status: " + status);

		String result = mobileUpdateRecord(BaseSoap.URL + "/" + updateMethod, getNameValuePairs(values));
		L.out("result: " + result);
		if (TabNavigationActivity.showToast)
			MyToast.show("Server update result: " + result);
		return result;
	}

	Arg[] args = new Arg[] {
			new Arg("FacilityID", "20867"),
			new Arg("StartLocation", "109213"),
			new Arg("DestinationLocation", "109211"),
			new Arg("Notes", "NoNoteyet"),
			new Arg("TaskClass", "2503"),
			new Arg("RequestorName", "Sadeesh"),
			new Arg("RequestorEmail", "Sadeesh@surisoft.com"),
			new Arg("RequestorPhone", "123456789"),
			new Arg("FuntionalAreaID", "1"),
			new Arg("TaskTypeID", "1"),
			new Arg("UpdatedBy", "mobile2"),
			new Arg("AutoAssignCheck", "false"),
			new Arg("ValidateRooms", "false"),
			new Arg("EmployeeID", "9269"),
	};

	private String createRecord(GetTaskInformationByTaskNumberAndFacilityID task) {
		// ValidateUser validateUser = User.getUser().getValidateUser();
		List<Arg> values = new ArrayList<RestWriter.Arg>();
		String updateMethod = MOBILE_TASK_UPDATE;
		values.add(new Arg("FacilityID", task.getFacilityID()));
		values.add(new Arg("StartLocation", task.getHirStartLocationNode()));
		values.add(new Arg("DestinationLocation", task.getHirDestLocationNode()));
		values.add(new Arg("Notes", task.getNotes()));
		values.add(new Arg("TaskClass", task.getTskTaskClass()));
		values.add(new Arg("RequestorName", task.getRequestorName()));
		values.add(new Arg("RequestorEmail", task.getRequestorEmail()));
		values.add(new Arg("RequestorPhone", task.getRequestorPhone()));
		values.add(new Arg("FunctionalAreaID", SelfTaskActivity.getFunctionalArea()));
		values.add(new Arg("TaskTypeID", "1"));
		values.add(new Arg("UpdatedBy", User.getUser().getUsername()));
		values.add(new Arg("AutoAssignCheck", "false"));
		values.add(new Arg("ValidateRooms", "false"));
		values.add(new Arg("EmployeeID", task.getEmployeeID()));
		values.add(new Arg("TaskNumber", ""));
		values.add(new Arg("ModeType", task.getTskModeType()));
		values.add(new Arg("EquipmentType", task.getTaskEquipmentType()));
		values.add(new Arg("FrequencyType", task.getTskFrequencyType()));
		values.add(new Arg("PatientName", task.getPatientName()));
		values.add(new Arg("PatientDOB", task.getPatientDOB()));
		values.add(new Arg("CostCodeID", task.getSteCostCode()));
		values.add(new Arg("PersistDay", ""));
		values.add(new Arg("IsolationPatient", task.getIsolationPatient()));
		// values.add(new Arg("ScheduleDate", task.getScheduleDate()));
		values.add(new Arg("ScheduleDate", ""));
		values.add(new Arg("CcnRequest", task.getCcnRequest()));
		values.add(new Arg("RequestDate", task.getRequestDate()));
		values.add(new Arg("Status", task.getTaskStatusBrief()));
		values.add(new Arg("Item", task.getItem()));
		values.add(new Arg("CustomField1", task.getCustomField1()));
		values.add(new Arg("CustomField2", task.getCustomField2()));
		values.add(new Arg("CustomField3", task.getCustomField3()));
		values.add(new Arg("CustomField4", task.getCustomField4()));
		values.add(new Arg("CustomField5", task.getCustomField5()));
		L.out("create task");
		// for (Arg arg : values) {
		// L.out("pair: " + arg.type + " " + arg.value);
		// }
		if (TabNavigationActivity.showToast)
			MyToast.show("Create Action: " + task.getTaskNumber());
		String result = mobileUpdateRecord(BaseSoap.URL + "/" + updateMethod, getNameValuePairs(values));
		L.out("here is the result: " + result + " " + task.getTaskStatusBrief());
		MyToast.show("updated the server: " + result);
		task.setTaskNumber(result);
		// get here if running in airplane mode!
		if (result != null && !task.getTaskStatusBrief().equals(SelfTaskActivity.ASSIGNED)) {
			// task.setTaskNumber(result);
			L.out("am updating task: " + task.getTaskNumber());
			updateTask(task.getEmployeeID(), task.getFacilityID(), task);
		}
		return result;
	}

	private List<NameValuePair> getNameValuePairs(List<Arg> values) {
		int count = values.size();
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(count);
		for (Arg arg : values) {
			if (arg.value == null)
				arg.value = "";
			nameValuePairs.add(new BasicNameValuePair(arg.type, arg.value));
			// L.out("pair: " + arg.type + " " + arg.value);
		}
		return nameValuePairs;
	}

	public String createTask(GetTaskInformationByTaskNumberAndFacilityID task) {
		// String result = updateTask(getCreateRestURL(task,
		// UPDATE_TASK_METHOD), task);
		L.out("result: " + "not implemented");
		return SUCCESSFUL;
	}

	private String mobileUpdateRecord(String urlString, List<NameValuePair> nameValuePairs) {
		// L.out(task.toString());
		StringBuilder s = new StringBuilder();
		try {
			HttpClient httpclient = new DefaultHttpClient();

			L.out("urlString: " + urlString);
			HttpPost httppost = new HttpPost(urlString);
			httppost.setHeader("Accept", "*/*");
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost);
			BufferedReader reader = new BufferedReader(
					new InputStreamReader(
							response.getEntity().getContent(), "UTF-8"));
			String sResponse;

			while ((sResponse = reader.readLine()) != null) {
				s = s.append(sResponse);
			}
			String responseString = s.toString();
			L.out("RESTful Response: " + responseString);
			return getTaskNumber(responseString);
			// return SUCCESSFUL;

		} catch (Exception e) {
			L.out("exception: " + e);
			L.out("stringBuilder: " + s);
			return null;
		}
	}

	private String getTaskNumber(String responseString) {
		// Pure Kludge. Rather than write a parser for EmployeeAndTaskStatus
		// We just grab the TaskNumber out of it. Since this will change, this
		// is temp code!
		int index = responseString.indexOf("TaskNumber") + 12;
		// L.out("index: " + index);
		if (index == -1)
			return null;
		// int jindex = responseString.indexOf("\"", index);
		// L.out("jindex: " + jindex);
		String temp = responseString.substring(index, responseString.indexOf("\"", index));
		L.out("getTaskNumber: " + temp);
		return temp;
	}

	class Arg {
		String type = null;
		String value = null;

		Arg(String title, String value) {
			this.type = title;
			this.value = value;
		}
	}

}
