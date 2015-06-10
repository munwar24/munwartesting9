package com.ii.mobile.soap.gson;

import com.ii.mobile.util.L;

public class ListDelayTypes extends GJon {

	TaskDelayTypes taskDelayTypes;

	static class TaskDelayTypes extends GJon {

		TaskDelayType[] taskDelayType;

		@Override
		public String toString() {
			String temp = "ListDelayTypes: \n";
			for (int i = 0; i < taskDelayType.length; i++) {
				temp += i + ": " + taskDelayType[i].toString() + "\n";
			}
			return temp;
		}
	}

	static public class TaskDelayType extends GJon {
		// @SerializedName("@attributes")
		// Attributes attributes;
		public String taskDelay;
		public String taskDelayID;

		@Override
		public String toString() {
			return "taskDelay: " + taskDelay + " taskDelayID: " + taskDelayID;
		}
	}

	@Override
	public boolean validate() {
		if (taskDelayTypes != null
				&& taskDelayTypes.taskDelayType != null)
			validated = true;
		else
			L.out("Unable to validate ListTaskClassesByFacilityID");
		return validated;
	}

	static public ListDelayTypes getGJon(String json) {
		// BaseSoap.debugOutput("\n\n*** ListDelayTypes \n" + json + " ***\n");
		ListDelayTypes listDelayTypes = (ListDelayTypes) getJSonObject(json, ListDelayTypes.class);
		return listDelayTypes;
	}

	public TaskDelayType[] getTaskDelayType() {
		return taskDelayTypes.taskDelayType;
	}

	@Override
	public String toString() {
		if (isValidated()) {
			return taskDelayTypes.toString();
		}
		return null;
	}
}
