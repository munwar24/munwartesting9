package com.ii.mobile.soap.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.ii.mobile.soap.BaseSoap;
import com.ii.mobile.util.L;

public class GetTaskDefinitionFieldsDataForScreenByFacilityID extends GJon {

	TaskDefinitionFieldsDataForScreen taskDefinitionFieldsDataForScreen;
	private static int maxRecordsPrinted = 15;

	static class TaskDefinitionFieldsDataForScreen {

		Geography[] geography;

		@Override
		public String toString() {

			String temp = printPickListSource();
			for (int i = 0; i < geography.length; i++) {

				temp += geography[i].toString() + "\n";
			}
			return temp;
		}

		String printPickListSource() {
			int size = 0;
			for (int i = 0; i < geography.length; i++)
				size += geography[i].pickList.length;
			String temp = "TaskDefinitionFieldsDataForScreen(" + size + "):\n\n";
			for (int i = 0; i < geography.length; i++) {
				temp += i + ": " + geography[i].pickListSource + "\n";
			}
			return temp += "\n";
		}
	}

	static public class Geography {
		public PickList[] pickList;
		// @SerializedName("@attributes")
		// GeorgraphyAttributes attributes;
		public String pickListSource;

		@Override
		public String toString() {
			String temp = "PickList: " + pickListSource + "\n\n";
			for (int i = 0; i < pickList.length; i++) {
				if (i > maxRecordsPrinted) {
					temp += "Truncated " + (pickList.length - i) + " records out of "
							+ pickList.length + " rooms";
					return temp;
				}
				temp += pickList[i].toString() + "\n";
			}
			return temp;
		}
	}

	public static class PickList {
		// @SerializedName("@attributes")
		// Attributes attributes;
		@SerializedName("ValuePart")
		public String valuePart;
		@SerializedName("TextPart")
		public String textPart;

		@Override
		public String toString() {
			return "  valuePart: " + valuePart + " textPart: " + textPart;
		}
	}

	@Override
	public boolean validate() {
		if (taskDefinitionFieldsDataForScreen != null
				&& taskDefinitionFieldsDataForScreen.geography != null)
			validated = true;
		else
			L.out("Unable to validate ListTaskClassesByFacilityID");
		return validated;
	}

	static public GetTaskDefinitionFieldsDataForScreenByFacilityID getGJon(String json) {
		GetTaskDefinitionFieldsDataForScreenByFacilityID functionalAreas =
				(GetTaskDefinitionFieldsDataForScreenByFacilityID) getJSonObjectArray(json, GetTaskDefinitionFieldsDataForScreenByFacilityID.class);
		// BaseSoap.debugOutput("\n\n*** GetTaskDefinitionFieldsDataForScreenByFacilityID \n"
		// + json + " ***\n");
		return functionalAreas;
	}

	static protected GJon getJSonObjectArray(String json, Class<?> className) {
		BaseSoap.debugOutput("\n\n*** " + className + " ***\n");
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
		gsonBuilder.registerTypeAdapter(PickList[].class, new PickListDeserializer());
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
			// BaseSoap.debugOutput(gJon.toString());
		}
		return gJon;
	}

	@Override
	public String toString() {
		if (isValidated()) {
			return taskDefinitionFieldsDataForScreen.toString();
		}
		return null;
	}

	public Geography getGeography(String pickListName) {
		// L.out("pickListName: " + pickListName);
		if (isValidated()) {
			Geography[] geography = taskDefinitionFieldsDataForScreen.geography;
			for (int i = 0; i < geography.length; i++) {
				if (geography[i].pickListSource.equals(pickListName)) {
					// L.out("foundIt: " + pickListName);
					return geography[i];
				}
			}
			L.out("*** ERROR Unable to find Geography named: " + pickListName + "\n"
					+ taskDefinitionFieldsDataForScreen.printPickListSource());
		}
		return null;
	}
}
