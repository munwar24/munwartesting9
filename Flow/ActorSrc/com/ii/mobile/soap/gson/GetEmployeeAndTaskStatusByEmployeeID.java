package com.ii.mobile.soap.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.ii.mobile.soap.BaseSoap;
import com.ii.mobile.util.L;

public class GetEmployeeAndTaskStatusByEmployeeID extends GJon {

	public EmployeeAndTaskStatusDetails employeeAndTaskStatusDetails;
	public Message message;

	public GetEmployeeAndTaskStatusByEmployeeID() {
		employeeAndTaskStatusDetails = new EmployeeAndTaskStatusDetails();
		// employeeAndTaskStatusDetails.employeeAndTaskStatus = new
		// EmployeeAndTaskStatus[1];
		employeeAndTaskStatusDetails.employeeAndTaskStatus[0] = new EmployeeAndTaskStatus();
	}

	public static GetEmployeeAndTaskStatusByEmployeeID init(String employeeStatus, String taskStatus) {
		GetEmployeeAndTaskStatusByEmployeeID status = new GetEmployeeAndTaskStatusByEmployeeID();
		EmployeeAndTaskStatus employeeAndTaskStatus = status.employeeAndTaskStatusDetails.employeeAndTaskStatus[0];
		employeeAndTaskStatus.employeeStatus = employeeStatus;
		employeeAndTaskStatus.taskStatus = taskStatus;
		return status;
	}

	static class EmployeeAndTaskStatusDetails {

		EmployeeAndTaskStatus[] employeeAndTaskStatus = new EmployeeAndTaskStatus[1];

		@Override
		public String toString() {
			return employeeAndTaskStatus[0].toString();
		}
	}

	static public class Message {

		public MobileMessage mobileMessage[];

		@Override
		public String toString() {
			if (mobileMessage == null)
				return "";
			String temp = "Messages: " + mobileMessage.length + "\n";
			for (MobileMessage mess : mobileMessage)
				temp += mess.toString() + "\n";
			return temp;
		}
	}

	public class MobileMessage {
		@SerializedName("MobileUserId")
		String mobileUserId;
		@SerializedName("MobileUserName")
		String mobileUserName;
		@SerializedName("TextMessage")
		public String textMessage;
		@SerializedName("ReceivedDate")
		public String receivedDate;
		@SerializedName("FromUserName")
		public String fromUserName;

		@Override
		public String toString() {
			return "Message: "
					+ "\n   mobileUserId: " + mobileUserId
					+ "\n   mobileUserName: " + mobileUserName
					+ "\n   textMessage: " + textMessage
					+ "\n   receivedDate: " + receivedDate
					+ "\n   fromUserName: " + fromUserName;
		}
	}

	class EmployeeAndTaskStatus {
		// @SerializedName("@attributes")
		// Attributes attributes;
		String userID;
		String mobileUserName;
		String userName;
		String taskNumber;
		String facilityID;
		String mobileUserID;
		String employeeStatus;
		String facility;
		String taskStatusID;
		String functionalAreaID;
		String taskStatus;
		String employeeName;

		@Override
		public String toString() {
			return " userID: " + userID
					+ "\n mobileUserName: " + mobileUserName
					+ "\n userName: " + userName
					+ "\n taskNumber: " + taskNumber
					+ "\n facilityID: " + facilityID
					+ "\n mobileUserID: " + mobileUserID
					+ "\n employeeStatus: " + employeeStatus
					+ "\n facility: " + facility
					+ "\n taskStatus: " + taskStatus
					+ "\n functionalAreaID: " + functionalAreaID
					+ "\n employeeName: " + employeeName;
		}
	}

	@Override
	public boolean validate() {
		if (employeeAndTaskStatusDetails != null
				&& employeeAndTaskStatusDetails.employeeAndTaskStatus != null)
			validated = true;
		else
			L.out("Unable to validate GetEmployeeAndTaskStatusByEmployeeID");
		return validated;
	}

	static public GetEmployeeAndTaskStatusByEmployeeID getGJon(String json) {
		String badMessage = "\"Message\":\"\",";
		// L.out("json: " + json);
		int index = json.indexOf(badMessage);
		// L.out("index: " + index);
		if (index != -1) {
			json = json.replace(badMessage, "");
		}

		json = makeSureIsAnArray("EmployeeAndTaskStatus", json);
		GetEmployeeAndTaskStatusByEmployeeID status;
		status = (GetEmployeeAndTaskStatusByEmployeeID) getJSonObjectArray(json, GetEmployeeAndTaskStatusByEmployeeID.class);
		// L.out("getEmployeeAndTaskStatusByEmployeeID: " + status);
		// Gson gson = new Gson();
		// String foo = gson.toJson(status);
		// L.out("json: " + foo);
		return status;
	}

	static protected GJon getJSonObjectArray(String json, Class<?> className) {
		BaseSoap.debugOutput("\n\n*** " + className + " ***\n");
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
		gsonBuilder.registerTypeAdapter(MobileMessage[].class, new MobileMessageDeserializer());

		Gson gson = gsonBuilder.create();
		JsonParser parser = new JsonParser();
		GJon gJon = null;
		try {
			gJon = (GJon) gson.fromJson(parser.parse(json).getAsJsonObject().toString(), className);
		} catch (Exception e) {

			L.out("*** ERROR Failed: " + e + "\njson: " + json + " " + className);
		}
		if (gJon == null) {
			L.out("Failed to parse json for: " + className);
			return null;
		}
		gJon.json = json;
		if (gJon.validate()) {
			// need to uncomment to print to console
			// BaseSoap.debugOutput(gJon.toString());
		}
		return gJon;
	}

	@Override
	public String toString() {
		if (isValidated()) {
			if (message != null)
				return employeeAndTaskStatusDetails.toString() + "\n" + message.toString();
			return employeeAndTaskStatusDetails.toString();
		}
		return null;
	}

	public String getEmployeeStatus() {
		return employeeAndTaskStatusDetails.employeeAndTaskStatus[0].employeeStatus;
	}

	public String getTaskStatus() {
		return employeeAndTaskStatusDetails.employeeAndTaskStatus[0].taskStatus;
	}

	public String getTaskNumber() {
		return employeeAndTaskStatusDetails.employeeAndTaskStatus[0].taskNumber;
	}

	public Message getMessage() {
		if (message == null)
			return null;
		return message;
	}

	public String getFacilityID() {
		return employeeAndTaskStatusDetails.employeeAndTaskStatus[0].facilityID;
	}

	public String getMobileUserName() {
		return employeeAndTaskStatusDetails.employeeAndTaskStatus[0].mobileUserName;
	}

	public void setEmployeeStatus(String employeeStatus) {
		employeeAndTaskStatusDetails.employeeAndTaskStatus[0].employeeStatus = employeeStatus;
	}

	public void setTaskStatus(String taskStatus) {
		employeeAndTaskStatusDetails.employeeAndTaskStatus[0].taskStatus = taskStatus;
	}

	public void setTaskNumber(String taskNumber) {
		employeeAndTaskStatusDetails.employeeAndTaskStatus[0].taskNumber = taskNumber;
	}

	public void setFacilityID(String facilityID) {
		employeeAndTaskStatusDetails.employeeAndTaskStatus[0].facilityID = facilityID;
	}

	public void setMobileUserName(String mobileUserName) {
		employeeAndTaskStatusDetails.employeeAndTaskStatus[0].mobileUserName = mobileUserName;
	}

}
