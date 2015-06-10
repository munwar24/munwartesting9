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

import com.ii.mobile.payload.sync.StatusType;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.util.L;

public class PulseWriter {
	public static final String SUCCESSFUL = "success";
	public static final String FAILURE = "fail";

	public static final String TASK_START = "MobileTaskStart";
	public static final String TASK_DELAY = "MobileTaskDelay";
	public static final String TASK_COMPLETE = "MobileTaskComplete";
	public static final String TASK_COMPLETE_AND_UPDATE_EMPLOYEE_STATUS = "MobileTaskCompleteAndUpdateEmployeeStatus";
	public static final String UPDATE_TASK_METHOD = "MobileTaskUpdate";
	public final static String MOBILE_UPDATE_PERSONNEL_STATUS = "MobileUpdatePersonnelStatus";
	public final static String MOBILE_TASK_UPDATE = "MobileTaskUpdate";

	public PulseWriter() {

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
		String statusCode = StatusType.lookUp(status);
		L.out("updateRecord statusCode: " + statusCode);
		List<Arg> values = new ArrayList<PulseWriter.Arg>();
		values.add(new Arg("Status", statusCode));
		values.add(new Arg("TaskNumber", ""));
		values.add(new Arg("SteEmployeeID", validateUser.getEmployeeID()));
		values.add(new Arg("SteHirNode", validateUser.getFacilityID()));
		values.add(new Arg("EnteredBy", validateUser.getMobileUserName()));

		L.out("RestWriter.updateRecord validateUser: " + updateMethod);
		String result = mobileUpdateRecord(BaseSoap.URL + "/" + updateMethod, getNameValuePairs(values));
		L.out("result: " + result);
		return result;
	}

	public static String NO_TASK = "No Task";
	public static String UNASSIGNED = "Unassigned";
	public static String ASSIGNED = "Assigned";
	public static String ACTIVE = "Active";
	public static String DELAYED = "Delayed";
	public static String COMPLETED = "Completed";
	public static String CANCELED = "Canceled";

	public static String TRANSPORT = "1";
	public static String EVS = "2";

	public String updateTask(String employeeID, String facilityID,
			GetTaskInformationByTaskNumberAndFacilityID task) {
		// L.out("RestWriter.updateTask: " + task);
		if (task.getTickled()) {
			L.out("RestWriter Task was tickled ignoring update: " + task.getTaskStatusBrief());
			return SUCCESSFUL;
		}
		String taskNumber = task.getTaskNumber();
		if (taskNumber == null || L.getLong(taskNumber) != 0l)
			return createRecord(task);
		String updateMethod = null;
		String status = task.getTaskStatusBrief();
		L.out("updateTask status: " + status);
		// ValidateUser validateUser = User.getUser().getValidateUser();
		ValidateUser validateUser = null; // will die
		List<Arg> values = new ArrayList<PulseWriter.Arg>();
		values.add(new Arg("FunctionalAreaID", TRANSPORT));
		values.add(new Arg("TaskNumber", task.getTaskNumber()));
		values.add(new Arg("EmployeeID", employeeID));
		values.add(new Arg("FacilityID", facilityID));
		String mobileUserName = task.getMobileUserName();
		if (validateUser != null)
			mobileUserName = validateUser.getMobileUserName();
		L.out("task.getMobileUserName(): " + task.getMobileUserName() + " "
				+ (validateUser != null ? validateUser.getMobileUserName() : "NULL VALIDATE"));
		values.add(new Arg("UpdatedBy", mobileUserName));

		if (status.equals(ASSIGNED)) {
		}
		if (status.equals(ACTIVE)) {
			updateMethod = TASK_START;
		}
		if (status.equals(DELAYED)) {
			updateMethod = TASK_DELAY;
			L.out("delayed: " + task.getDelayType());
			values.add(new Arg("TskDelayType", task.getDelayType()));
		}
		if (status.equals(COMPLETED)) {
			// updateMethod = TASK_COMPLETE;
			updateMethod = TASK_COMPLETE_AND_UPDATE_EMPLOYEE_STATUS;
			L.out("completed: " + task.getCompleteTo());
			// values.add(new Arg("AutoAssignCheck", "false"));
			values.add(new Arg("EmployeeCustomStatus", task.getCompleteTo()));
		}
		if (status.equals(CANCELED)) {
		}
		if (updateMethod == null) {
			L.out("Nothing to do for this status: " + status);
			return SUCCESSFUL;
		}

		String result = mobileUpdateRecord(BaseSoap.URL + "/" + updateMethod, getNameValuePairs(values));
		L.out("result: " + result);
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
		List<Arg> values = new ArrayList<PulseWriter.Arg>();
		String updateMethod = MOBILE_TASK_UPDATE;
		values.add(new Arg("FacilityID", task.getFacilityID()));
		values.add(new Arg("StartLocation", task.getHirStartLocationNode()));
		values.add(new Arg("DestinationLocation", task.getHirDestLocationNode()));
		values.add(new Arg("Notes", task.getNotes()));
		values.add(new Arg("TaskClass", task.getTskTaskClass()));
		values.add(new Arg("RequestorName", task.getRequestorName()));
		values.add(new Arg("RequestorEmail", task.getRequestorEmail()));
		values.add(new Arg("RequestorPhone", task.getRequestorPhone()));
		values.add(new Arg("FunctionalAreaID", TRANSPORT));
		values.add(new Arg("TaskTypeID", "1"));
		values.add(new Arg("UpdatedBy", "bay"));
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

		// for (Arg arg : values) {
		// L.out("pair: " + arg.type + " " + arg.value);
		// }
		String result = mobileUpdateRecord(BaseSoap.URL + "/" + updateMethod, getNameValuePairs(values));
		L.out("here is the result: " + result + " " + task.getTaskStatusBrief());

		// get here if running in airplane mode!
		if (!task.getTaskStatusBrief().equals("Assigned")) {
			task.setTaskNumber(result);
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
			L.out("pair: " + arg.type + " " + arg.value);
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
