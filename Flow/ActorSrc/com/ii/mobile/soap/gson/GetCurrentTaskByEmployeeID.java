package com.ii.mobile.soap.gson;

public class GetCurrentTaskByEmployeeID extends GJon {
	CurrrentTask currentTask;

	static class CurrrentTask {
		// not debugged yet!
		CurrentTaskByEmployee currentTaskByEmployee;
	}

	static class CurrentTaskByEmployee {
		// @SerializedName("@attributes")
		// Attributes attributes;
		String taskClass;
		String destinationLocation;
		String startLocation;
		String taskStatusType;
		String taskNumber;
		String enteredAt;

		@Override
		public String toString() {
			return "GetCurrentTaskByEmployeeID:\n  taskClass: " + taskClass
					+ "\n destinationLocation: " + destinationLocation
					+ "\n startLocation: " + startLocation
					+ "\n taskStatusType: " + taskStatusType
					+ "\n taskNumber: " + taskNumber
					+ "\n enteredAt: " + enteredAt;
		}
	}

	@Override
	public boolean validate() {
		// L.out("test: " + currentTask.currentTaskByEmployee);
		if (currentTask != null && currentTask.currentTaskByEmployee != null
				&& currentTask.currentTaskByEmployee.taskClass != null)
			validated = true;
		else {
			// L.out("Unable to validate GetCurrentTaskByEmployeeID");
		}
		return validated;
	}

	static public GetCurrentTaskByEmployeeID getGJon(String json) {
		// L.out("json: " + json);
		GetCurrentTaskByEmployeeID getCurrentTaskByEmployeeID = (GetCurrentTaskByEmployeeID) getJSonObject(json, GetCurrentTaskByEmployeeID.class);
		// L.out("getCurrentTaskByEmployeeID: " + getCurrentTaskByEmployeeID);
		return getCurrentTaskByEmployeeID;
	}

	@Override
	public String toString() {
		if (isValidated()) {
			return currentTask.currentTaskByEmployee.toString();
		}
		return null;
	}

	public String getTaskStatusType() {
		if (isValidated()) {
			return currentTask.currentTaskByEmployee.taskStatusType;
		}
		return null;
	}

	public String getDestinationLocation() {
		if (isValidated()) {
			return currentTask.currentTaskByEmployee.destinationLocation;
		}
		return null;
	}

	public String getStartLocation() {
		if (isValidated()) {
			return currentTask.currentTaskByEmployee.startLocation;
		}
		return null;
	}

	public String getEnteredAt() {
		if (isValidated()) {
			return currentTask.currentTaskByEmployee.enteredAt;
		}
		return null;
	}

	public String getTaskNumber() {
		if (isValidated()) {
			return currentTask.currentTaskByEmployee.taskNumber;
		}
		return null;
	}

	public String getTaskClass() {
		if (isValidated()) {
			return currentTask.currentTaskByEmployee.taskClass;
		}
		return null;
	}

}
