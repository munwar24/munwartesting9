package com.ii.mobile.flowing;

import org.json.JSONObject;

import com.ii.mobile.soap.ContentValues;
import com.ii.mobile.util.L;

public class BaseFlow {
	private static final boolean WANT_DEBUG = false;

	boolean showJSON = false;
	protected static final String OUTPUT_FILE = "out.txt";
	protected static final boolean DEBUG_OUTPUT = false;

	protected static final String II_URL = "http://www.syncpulse.cloudapp.net/HRCAjax/DOM/StateEngineWebSite/StateEngineWebService.asmx";
	public static String URL = II_URL;

	static BaseFlow flow = null;

	// private static boolean inited = false;
	// private static boolean inited = true;
	static int counter = 0;

	public JSONObject createJSONObject(String methodName, ContentValues contentValues) {

		JSONObject jSonObject = null;

		try {
			// L.out("URL: " + URL);
			// need to turn this on to get output in file!
			// L.out("\n" + counter++ + ": " + methodName + " " +
			// contentValues + " " + URL);
			// result is a JSON string
			String result = new FlowRestService().execute(methodName, null, null);

			// debugOutput("result: " + result);
			if (result == null || result.equals("")) {
				debugOutput("*** Error recieved a null message!", true);
				return null;
			}
			jSonObject = parseJSON(result);
			if (jSonObject == null) {
				debugOutput("\njSonObject is " + jSonObject + "\n", true);
				return null;
			}
			// need to turn this on to get output in file!
			// debugOutput(jSonObject.toString(3));
		} catch (Exception e) {

			L.out("*** ERROR in Flow call: " + e + " " + methodName + " " + contentValues);
			return null;
		}

		return jSonObject;
	}

	// public JSONObject createJSONObjectold(String methodName, ContentValues
	// contentValues) {
	//
	// JSONObject jSonObject = null;
	//
	// try {
	// // L.out("URL: " + URL);
	// // need to turn this on to get output in file!
	// // L.out("\n" + counter++ + ": " + methodName + " " +
	// // contentValues + " " + URL);
	// // result is a JSON string
	// String result = new FlowRestService().execute(methodName);
	//
	// // debugOutput("result: " + result);
	// if (result == null || result.equals("")) {
	// debugOutput("*** Error recieved a null message!", true);
	// return null;
	// }
	// jSonObject = parseJSON(result);
	// if (jSonObject == null) {
	// debugOutput("\njSonObject is " + jSonObject + "\n", true);
	// return null;
	// }
	// // need to turn this on to get output in file!
	// // debugOutput(jSonObject.toString(3));
	// } catch (Exception e) {
	//
	// L.out("*** ERROR in Flow call: " + e + " " + methodName + " " +
	// contentValues);
	// return null;
	// }
	//
	// return jSonObject;
	// }

	public static JSONObject parseJSON(String temp) {
		L.out("temp: " + temp);
		JSONObject jSONObject = null;
		try {
			jSONObject = new JSONObject(temp);

		} catch (Exception e) {
			L.out("*** ERROR parsing: " + e + " \n" + temp);
		}
		return jSONObject;
	}

	public static void debugOutput(String json) {
		debugOutput(json, true);
	}

	private static int MAX_LENGTH = 20000;

	@SuppressWarnings("unused")
	public static void debugOutput(String json, boolean printMessage) {
		// L.out("WANT_DEBUG: " + WANT_DEBUG);
		if (!WANT_DEBUG)
			return;
		if (DEBUG_OUTPUT || printMessage) {
			String temp = json;
			if (json.length() > MAX_LENGTH) {
				temp = temp.substring(0, MAX_LENGTH);
				temp += "\nTruncated " + (json.length() - MAX_LENGTH) + " characters out of "
						+ json.length() + " characters";
			}
			L.out("-> " + temp + "\n");
			if (DEBUG_OUTPUT)
				L.debugOutput(temp, OUTPUT_FILE);
		}
	}
}
