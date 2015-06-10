package com.ii.mobile.soap.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.ii.mobile.soap.BaseSoap;
import com.ii.mobile.soap.gson.deserializer.FunctionalAreaDeserializer;
import com.ii.mobile.util.L;

public class ListTaskClassesByFacilityID extends GJon {

	TaskClasses taskClasses = null;

	static class TaskClasses {
		public FunctionalArea[] functionalArea;

		@Override
		public String toString() {
			String temp = "";
			for (int i = 0; i < functionalArea.length; i++) {
				temp += functionalArea[i].toString() + "\n";
			}
			return temp;
		}
	}

	public static class FunctionalArea {
		public TaskClass[] taskClass;

		// @SerializedName("@attributes")
		// FunctionalAttributes functionalAttributes;
		String functionalAreaID;

		@Override
		public String toString() {
			String temp = "FunctionalAreaID: " + functionalAreaID + "\n";
			if (taskClass != null)
				for (int i = 0; i < taskClass.length; i++) {
					temp += taskClass[i] + "\n";
				}
			return temp;
		}
	}

	static public class TaskClass {
		@SerializedName("TaskClassID")
		public String taskClassID;
		@SerializedName("Brief")
		public String brief;

		@Override
		public String toString() {
			return "  taskClassID: " + taskClassID
					+ "  brief: " + brief;
		}
	}

	@Override
	public boolean validate() {
		if (taskClasses != null
				&& taskClasses.functionalArea != null)
			validated = true;
		else
			L.out("Unable to validate ListTaskClassesByFacilityID");
		return validated;
	}

	static public ListTaskClassesByFacilityID getGJon(String json) {

		return (ListTaskClassesByFacilityID) getJSonObjectArray(json, ListTaskClassesByFacilityID.class);
	}

	static protected GJon getJSonObjectArray(String json, Class<?> className) {
		BaseSoap.debugOutput("\n\n*** " + className + " ***\n");
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
		// gsonBuilder.registerTypeAdapter(TaskClass[].class, new
		// TaskClassDeserializer());
		gsonBuilder.registerTypeAdapter(FunctionalArea[].class, new FunctionalAreaDeserializer());
		Gson gson = gsonBuilder.create();
		JsonParser parser = new JsonParser();
		GJon gJon = null;
		try {
			gJon = (GJon) gson.fromJson(parser.parse(json).getAsJsonObject().toString(), className);
		} catch (Exception e) {
			if (GetCurrentTaskByEmployeeID.class != className)
				L.out("*** ERROR Failed: " + e + "\njson: " + json + " " + className);
		}
		if (gJon == null) {
			BaseSoap.debugOutput("Failed to parse json for: " + className);
			return null;
		}
		gJon.json = json;
		if (gJon.validate()) {
			// need to uncomment to print to console
			BaseSoap.debugOutput(gJon.toString());
		}
		return gJon;
	}

	@Override
	public String toString() {
		if (isValidated()) {
			return taskClasses.toString();
		}
		return null;
	}

	public TaskClass[] getTaskClasses(String index) {
		L.out("index: " + index + " " + taskClasses.functionalArea.length);
		// L.out("json: " + json);
		// L.out("newjson: " + getNewJson());
		FunctionalArea[] functionalArea = taskClasses.functionalArea;
		for (int i = 0; i < functionalArea.length; i++) {
			// L.out(i + " " + functionalArea[i].toString());
			if (functionalArea[i].functionalAreaID == null) {
				L.out("*** functionalAreaID not found for: " + index);
			} else if (functionalArea[i].functionalAreaID.equals(index)) {
				return functionalArea[i].taskClass;
			}
		}
		L.out("*** Error index not found in functionalAreas: " + index + " size: "
				+ taskClasses.functionalArea.length);
		return null;
	}
}
