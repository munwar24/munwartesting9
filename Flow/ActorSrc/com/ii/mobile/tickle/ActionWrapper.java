package com.ii.mobile.tickle;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public class ActionWrapper extends GJon {

	public CurrentAction currentAction = new CurrentAction();

	public static class CurrentAction {
		public String employeeName = null;
		public String employeePIN = null;
		public String employeeStatus = null;
		public String taskStatus = null;
		public String taskNumber = null;
		public String actorId = null;
		public String actionId = null;

		@Override
		public String toString() {
			return "Status:\n"
					+ " employeeName: " + employeeName
					+ " employeePIN: " + employeePIN
					+ " employeeStatus: " + employeeStatus
					+ " taskStatus: " + taskStatus
					+ " actorId: " + actorId
					+ " actionId: " + actionId
					+ " taskNumber: " + taskNumber;
		}
	}

	static public ActionWrapper getGJon(String json) {
		// L.out("json: " + json);
		try {
			return (ActionWrapper) getJSonObject(json, ActionWrapper.class);
		} catch (Exception e) {
			L.out("*** ERROR (maybe): " + e);
			return null;
		}
	}

	@Override
	public String getNewJson() {
		Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
		JsonElement je = gson.toJsonTree(currentAction);
		JsonObject jo = new JsonObject();
		jo.add("CurrentStatus", je);
		json = jo.toString();
		// json = gson.toJson(taskInformations);
		return json;
	}

	@Override
	public String toString() {
		if (currentAction != null)
			return currentAction.toString();
		return "ERROR currentStatus is null!";
	}
}
