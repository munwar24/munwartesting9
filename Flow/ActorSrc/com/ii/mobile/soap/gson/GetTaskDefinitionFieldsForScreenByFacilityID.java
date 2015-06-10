package com.ii.mobile.soap.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.ii.mobile.soap.BaseSoap;
import com.ii.mobile.soap.gson.deserializer.FieldFunctionalAreaDeserializer;
import com.ii.mobile.util.L;

public class GetTaskDefinitionFieldsForScreenByFacilityID extends GJon {

	TaskDefinitionFieldsForScreen taskDefinitionFieldsForScreen;

	static class TaskDefinitionFieldsForScreen {

		public FunctionalArea[] functionalArea;

		@Override
		public String toString() {
			String temp = "TaskDefinitionFieldsForScreen: " + "\n\n";
			for (int i = 0; i < functionalArea.length; i++) {
				temp += functionalArea[i].toString() + "\n";
			}
			return temp;
		}
	}

	public static class FunctionalArea {

		@SerializedName("Class")
		GClass[] gClass;
		String areaID;
		String areaTitle;
		String imageSource;
		String smallImageSource;
		String sortByStatTask;

		// @Override
		// public String toString() {
		// String temp = "FunctionalArea(" + gClass.length + "): " +
		// getAttributes() + "\n";
		//
		// for (int i = 0; i < gClass.length; i++) {
		// temp += i + ": Class: " + gClass[i].toString() + "\n";
		// }
		// return temp;
		// }

		@Override
		public String toString() {
			String temp = "AreaID: " + areaID + " areaTitle: " + areaTitle + "\n";
			for (int i = 0; i < gClass.length; i++)
				temp += gClass[i].toString();
			return temp;
		}

		@SuppressWarnings("unused")
		private String getAttributes() {
			return "\n areaID: " + areaID
					+ "\n areaTitle: " + areaTitle
					+ "\n imageSouce: " + imageSource
					+ "\n smallImageSource: " + smallImageSource
					+ "\n sortByStatTask: " + sortByStatTask
					+ "\n";
		}
	}

	static public class GClass {
		// @SerializedName("@attributes")
		// ClassAttributes attributes;
		@SerializedName("Field")
		Field[] field;
		@SerializedName("ClassID")
		String classID;
		@SerializedName("ClassBrief")
		String classBrief;
		@SerializedName("IsStatDisplayed")
		String isStatDisplayed;
		private final int maxRecordsPrinted = 2;

		@Override
		public String toString() {

			String temp = getAttributes() + "\n";

			for (int i = 0; i < field.length; i++) {
				if (i > maxRecordsPrinted) {
					temp += "Truncated " + (field.length - i) + " fields out of "
							+ field.length + " fields\n";
					return temp;
				}
				temp += "  " + i + ": Field " + field[i].toString() + "\n";
			}
			return temp;
		}

		private String getAttributes() {
			return "\n classID: " + classID
					+ "\n classBrief: " + classBrief
					+ "\n isStatDisplayed: " + isStatDisplayed
					+ "\n";
		}
	}

	static public class Field {
		// @SerializedName("@attributes")
		// Attributes attributes;
		@SerializedName("IsSiteSpecific")
		public String isSiteSpecific;
		@SerializedName("Required")
		public String required;
		@SerializedName("TextLength")
		public String textLength;
		@SerializedName("Header")
		public String header;
		@SerializedName("PickListSource")
		public String pickListSource;
		@SerializedName("ForceField")
		public String forceField;
		@SerializedName("ControlType")
		public String controlType;
		@SerializedName("ControlName")
		public String controlName;
		@SerializedName("CustomHeader")
		public String customHeader;
		// added for production
		@SerializedName("Displayorder")
		public String displayOrder;

		@Override
		public String toString() {
			return "\n    isSitespecific: " + isSiteSpecific
					+ "\n    controltype: " + controlType
					+ "\n    controlName: " + controlName
					+ "\n    customHeader: " + customHeader
					+ "\n    required: " + required
					+ "\n    textLength: " + textLength
					+ "\n    header: " + header
					+ "\n    pickListSource: " + pickListSource
					+ "\n    forceField: " + forceField
					+ "\n    displayOrder: " + displayOrder + "\n";
		}

		public String toStringShort() {
			return " customHeader: " + customHeader
					+ " controltype: " + controlType
					+ " controlName: " + controlName
					+ " required: " + required
					+ "\n";
		}
	}

	@Override
	public boolean validate() {
		if (taskDefinitionFieldsForScreen != null
				&& taskDefinitionFieldsForScreen.functionalArea != null)
			validated = true;
		else
			L.out("*** ERROR Unable to validate GetTaskDefinitionFieldsForScreenByFacilityID");
		return validated;
	}

	static public GetTaskDefinitionFieldsForScreenByFacilityID getGJon(String json) {
		// json = getOnlyTransportJson(json);

		// BaseSoap.debugOutput("\n\n*** GetTaskDefinitionFieldsForScreenByFacilityID \n"
		// + json + " ***\n");
		GetTaskDefinitionFieldsForScreenByFacilityID functionalAreas =
				(GetTaskDefinitionFieldsForScreenByFacilityID)
				getJSonObjectArray(json, GetTaskDefinitionFieldsForScreenByFacilityID.class);
		return functionalAreas;
	}

	static protected GJon getJSonObjectArray(String json, Class<?> className) {
		BaseSoap.debugOutput("\n\n*** " + className + " ***\n");
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);

		// gsonBuilder.registerTypeAdapter(Field[].class, new
		// FieldDeserializer());
		gsonBuilder.registerTypeAdapter(FunctionalArea[].class, new FieldFunctionalAreaDeserializer());
		// gsonBuilder.registerTypeAdapter(GClass[].class, new
		// GClassDeserializer());
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

	// static private String getOnlyTransportJson(String json) {
	// JsonElement element = (JsonObject) new JsonParser().parse(json);
	// JsonObject jsonObject = element.getAsJsonObject();
	// jsonObject.remove(property)
	//
	// return null;
	// }

	@Override
	public String toString() {
		if (isValidated()) {
			return taskDefinitionFieldsForScreen.toString();
		}
		return null;
	}

	private GClass[] getFunctionalArea(String functionalAreaID) {
		for (int i = 0; i < taskDefinitionFieldsForScreen.functionalArea.length; i++) {
			if (taskDefinitionFieldsForScreen.functionalArea[i].areaID.equals(functionalAreaID))
			{
				return taskDefinitionFieldsForScreen.functionalArea[i].gClass;
			}
		}

		return null;
	}

	private Field[] getClassField(GClass[] gClass, String classID) {
		for (int i = 0; i < gClass.length; i++) {
			if (gClass[i].classID.equals(classID)) {
				return gClass[i].field;
			}
		}
		L.out("*** ERROR Unable to find  class: " + classID);
		return null;
	}

	public Field[] getClassField(String functionalAreaID, String classID) {
		if (validated) {
			GClass[] gClass = getFunctionalArea(functionalAreaID);
			if (gClass != null) {
				Field[] classField = getClassField(gClass, classID);
				if (classField != null)
					L.out("field[]: " + classField.length);
				return classField;
			}
		}
		L.out("*** ERROR Unable to find fields for class: " + functionalAreaID + " " + classID);
		return null;
	}

}
