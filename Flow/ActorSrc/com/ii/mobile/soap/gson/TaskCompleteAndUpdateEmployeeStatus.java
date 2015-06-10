package com.ii.mobile.soap.gson;

import com.google.gson.annotations.SerializedName;
import com.ii.mobile.util.L;

public class TaskCompleteAndUpdateEmployeeStatus extends GJon {

	EmployeeAndTaskStatusDetails employeeAndTaskStatusDetails;

	static class EmployeeAndTaskStatusDetails {

		EmployeeAndTaskStatus employeeAndTaskStatus;

		@Override
		public String toString() {
			return "GetEmployeeAndTaskStatusByEmployeeID: \n" + employeeAndTaskStatus.toString();
		}
	}

	static class EmployeeAndTaskStatus {
		@SerializedName("@attributes")
		Attributes attributes;

		@Override
		public String toString() {
			return attributes.toString();
		}
	}

	static class Attributes {
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
			return "userID: " + userID + " mobileUserName: " + mobileUserName + " userName: " + userName
					+ "\ntaskNumber: " + taskNumber + " facilityID: " + facilityID + " mobileUserID: "
					+ mobileUserID
					+ "\nemployeeStatus: " + employeeStatus + " facility: " + facility + " taskStatus: "
					+ taskStatus
					+ "\nfunctionalAreaID: " + functionalAreaID + " taskStatus: " + taskStatus
					+ " employeeName: " + employeeName;
		}
	}

	@Override
	public boolean validate() {
		if (employeeAndTaskStatusDetails != null
				&& employeeAndTaskStatusDetails.employeeAndTaskStatus != null)
			validated = true;
		else
			L.out("Unable to validate ListTaskClassesByFacilityID");
		return validated;
	}

	static public TaskCompleteAndUpdateEmployeeStatus getGJon(String json) {
		L.out("json: " + json);
		TaskCompleteAndUpdateEmployeeStatus functionalAreas = (TaskCompleteAndUpdateEmployeeStatus) getJSonObject(json, TaskCompleteAndUpdateEmployeeStatus.class);
		return functionalAreas;
	}

	@Override
	public String toString() {
		if (isValidated()) {
			return employeeAndTaskStatusDetails.toString();
		}
		return null;
	}
}
