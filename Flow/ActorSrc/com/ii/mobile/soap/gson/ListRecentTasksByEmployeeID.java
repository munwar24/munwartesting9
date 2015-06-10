package com.ii.mobile.soap.gson;

import com.ii.mobile.util.L;

public class ListRecentTasksByEmployeeID extends GJon {

	RecentTasks recentTasks;

	class RecentTasks {

		public EmployeeRecentTasksList[] employeeRecentTasksList;

		@Override
		public String toString() {
			String temp = "ListRecentTasksByEmployeeID: " + "\n";
			for (int i = 0; i < employeeRecentTasksList.length; i++) {
				temp += employeeRecentTasksList[i].toString() + "\n";
			}
			return temp;
		}
	}

	static public class EmployeeRecentTasksList {
		// @SerializedName("@attributes")
		// Attributes attributes;
		public String startLocation;
		public String taskNumber;
		public String taskClass;
		public String destinationLocation;

		@Override
		public String toString() {
			return "EmployeeRecentTasksList\n  taskClass: " + taskClass
					+ "\n  destinationLocation: " + destinationLocation
					+ "\n  startLocation: " + startLocation
					+ "\n  taskNumber: " + taskNumber;
		}

		public String getTaskNumber() {
			return taskNumber;
		}

		public String getTaskClass() {
			return taskClass;
		}

		public String getDestinationLocation() {
			return destinationLocation;
		}

		public String getStartLocation() {
			return startLocation;
		}
	}

	// class Attributes {
	// String taskClass;
	// String destinationLocation;
	// String startLocation;
	// String taskNumber;
	// }

	@Override
	public boolean validate() {
		if (recentTasks != null
				&& recentTasks.employeeRecentTasksList != null)
			validated = true;
		else
			L.out("Unable to validate ListRecentTasksByEmployeeID");
		return validated;
	}

	static public ListRecentTasksByEmployeeID getGJon(String json) {
		ListRecentTasksByEmployeeID functionalAreas = (ListRecentTasksByEmployeeID) getJSonObject(json, ListRecentTasksByEmployeeID.class);
		return functionalAreas;
	}

	@Override
	public String toString() {
		if (isValidated()) {
			return recentTasks.toString();
		}
		return null;
	}

	public EmployeeRecentTasksList[] getEmployeeRecentTasksList() {
		return recentTasks.employeeRecentTasksList;

	}
}
