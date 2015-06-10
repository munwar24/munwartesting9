package com.ii.mobile.soap.gson;

import org.json.JSONObject;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.ii.mobile.model.Persist;
import com.ii.mobile.soap.BaseSoap;
import com.ii.mobile.soap.Soap;
import com.ii.mobile.util.L;

public class GJon extends Persist {
	transient protected boolean validated = false;
	transient public String json = null;
	public String tickled = null;

	public static final String TRUE_STRING = "true";
	public static final String FALSE_STRING = "false";

	public GJon() {
	}

	public GJon(String employeeID, String facilityID, String taskNumber, String json) {
		// this.employeeID = employeeID;
		// this.facilityID = facilityID;
		// this.taskNumber = taskNumber;
		this.json = json;
	}

	static public GJon getJSonObject(String json, Class<?> className) {
		BaseSoap.debugOutput("\n\n*** " + className + " ***\n");
		Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
		JsonParser parser = new JsonParser();
		GJon gJon = null;
		try {
			gJon = (GJon) gson.fromJson(parser.parse(json).getAsJsonObject().toString(), className);
		} catch (Exception e) {
			if (GetTaskInformationByTaskNumberAndFacilityID.class != className) {
				L.out("*** ERROR Failed: " + e + "\njson: " + json + " " + className);
			}
		}
		if (gJon == null) {
			BaseSoap.debugOutput("Failed to parse json for: " + className);
			return null;
		}
		gJon.json = json;
		if (gJon.validate()) {
			// need to uncomment to print to console
			// BaseSoap.debugOutput(json);
		}
		return gJon;
	}

	public boolean validate() {
		return true;
	}

	public boolean isValidated() {

		if (!validated) {
			// hack since returns a bad jSon when don't have task (in loop)
			if (!(this instanceof GetCurrentTaskByEmployeeID))
				L.out(this.getClass() + " was not properly initiated: \n" + json);
			printJson();
		}
		return validated;
	}

	public String getMethodName() {
		String temp = getClass().getName();
		return temp.substring(temp.lastIndexOf('.') + 1);
	}

	public String getNewJson() {
		Gson gson = new Gson();
		json = null;
		json = gson.toJson(this);
		return json;
	}

	public String getNewNullJson() {
		Gson gson = new GsonBuilder().serializeNulls().create();
		json = gson.toJson(this);
		return json;
	}

	public String getJson() {
		return json;
	}

	public void setJson(String newJson) {
		json = newJson;
	}

	public void printJson() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je = jp.parse(getJson());
		String prettyJsonString = gson.toJson(je);
		L.outp(prettyJsonString);
	}

	protected static String makeSureIsAnArray(String searchClassName, String json) {
		try {
			@SuppressWarnings("unused")
			JSONObject object = Soap.parseJSON(json);
			// L.out("makeSureIsAnArray before: " + object.toString(3));
			String match = searchClassName + "\":";
			int index = json.indexOf(match + "[");
			// L.out("index: " + index);
			if (index != -1)
				return json;
			// operate on the JSON string to put in the array
			json = json.replace(match, match + "[");
			json = json.replaceAll(searchClassName + "[^\\}]*.", "$0]");
			object = Soap.parseJSON(json);
			// L.out("makeSureIsAnArray after: " + object.toString(3));
		} catch (Exception e) {
			L.out("makeSureIsAnArray failed on: " + e + "\n" + json);
		}
		return json;
	}

	// @Override
	// public String toString() {
	// return getClass() + " employeeID: " + employeeID + " facilityID: " +
	// facilityID + " taskNumber: "
	// + taskNumber + " json: " + json;
	// }

	@Override
	public String toString() {
		return getClass() + " json: " + json;
	}
}
