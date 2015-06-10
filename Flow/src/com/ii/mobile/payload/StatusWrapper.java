package com.ii.mobile.payload;

import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ii.mobile.flow.types.GetActorStatus.InstantMessage;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.soap.gson.GetEmployeeAndTaskStatusByEmployeeID;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.util.L;

public class StatusWrapper extends GJon {

	public CurrentStatus currentStatus = new CurrentStatus();
	public String delayId = null;

	public static class CurrentStatus {
		public String employeeName = null;
		public String employeePIN = null;
		public String employeeStatus = null;
		public String taskStatus = null;
		public String taskNumber = null;
		public String actorId = null;
		public String actionId = null;
		public String actionStatusId = null;
		public String actorStatusId = null;
		public String facilityId;
		public List<InstantMessage> instantMessages;

		// public String _id = null;

		// public List<String> messages = new ArrayList<String>();

		// public List<InstantMessage> instantMessages = new
		// ArrayList<InstantMessages>()

		@Override
		public String toString() {
			return "Status:\n"
					+ " employeeName: " + employeeName
					+ " employeePIN: " + employeePIN
					+ " employeeStatus: " + employeeStatus + "\n"
					+ " actionStatus: " + taskStatus
					+ " actorId: " + actorId
					+ " actorStatusId: " + actorStatusId + "\n"
					+ " actionId: " + actionId
					+ " facilityId: " + facilityId
					+ " actionNumber: " + taskNumber;
			// + " _id: " + _id;
		}
	}

	public static StatusWrapper init(String employeeStatus, String taskStatus) {
		StatusWrapper statusWrapper = new StatusWrapper();
		statusWrapper.currentStatus.employeeStatus = employeeStatus;
		statusWrapper.currentStatus.taskStatus = taskStatus;
		return statusWrapper;
	}

	public StatusWrapper(GetEmployeeAndTaskStatusByEmployeeID employeeAndTaskStatus, ValidateUser validateUser) {
		// currentStatus.employeeName =
		// employeeAndTaskStatus.getMobileUserName();
		currentStatus.employeeStatus = employeeAndTaskStatus.getEmployeeStatus();
		currentStatus.taskStatus = employeeAndTaskStatus.getTaskStatus();
		currentStatus.taskNumber = employeeAndTaskStatus.getTaskNumber();
		currentStatus.taskNumber = validateUser.getMobilePIN();
	}

	public StatusWrapper() {
		// L.out("created a default status wrapper");
	}

	static public StatusWrapper getGJon(String json) {
		// L.out("json: ");
		// PrettyPrint.prettyPrint(json, true);
		try {
			return (StatusWrapper) getJSonObject(json, StatusWrapper.class);
		} catch (Exception e) {
			L.out("*** ERROR (maybe): " + e);
			return null;
		}
	}

	@Override
	public String getNewJson() {
		Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
		JsonElement je = gson.toJsonTree(currentStatus);
		JsonObject jo = new JsonObject();
		jo.add("CurrentStatus", je);
		json = jo.toString();
		// json = gson.toJson(taskInformations);
		return json;
	}

	public StatusWrapper copy() {
		StatusWrapper statusWrapper = new StatusWrapper();
		statusWrapper.currentStatus.employeeStatus = currentStatus.employeeStatus;
		statusWrapper.currentStatus.taskStatus = currentStatus.taskStatus;
		statusWrapper.currentStatus.employeeName = currentStatus.employeeName;
		statusWrapper.currentStatus.employeePIN = currentStatus.employeePIN;
		statusWrapper.currentStatus.taskNumber = currentStatus.taskNumber;
		statusWrapper.currentStatus.actorId = currentStatus.actorId;
		statusWrapper.currentStatus.actionId = currentStatus.actionId;
		statusWrapper.currentStatus.actorId = currentStatus.actorId;
		statusWrapper.currentStatus.actionStatusId = currentStatus.actionStatusId;
		statusWrapper.currentStatus.actorStatusId = currentStatus.actorStatusId;
		return statusWrapper;
	};

	@Override
	public String toString() {
		if (currentStatus != null)
			return currentStatus.toString();
		return "ERROR currentStatus is null!";
	}
}
