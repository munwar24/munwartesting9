package com.ii.mobile.util;

import org.json.JSONException;
import org.json.JSONObject;

public class PrettyPrint {

	public static String prettyPrint(String json, boolean printout) {
		if (json == null)
			return "ERROR - PrettyPrint json: null!";

		try {
			JSONObject jsonObject = new JSONObject(json);
			if (jsonObject != null) {
				String temp = jsonObject.toString(3);
				if (printout)
					L.outp("prettyPrint: \n" + jsonObject.toString(3));
				return temp;

			}
		} catch (JSONException e) {
			L.outp("failed: " + e);
			e.printStackTrace();
		}
		return null;
	}

	public static String prettyPrint(String json) {
		if (json == null)
			return "ERROR - PrettyPrint json: null!";

		try {
			JSONObject jsonObject = new JSONObject(json);
			if (jsonObject != null) {
				String temp = jsonObject.toString(3);
				L.outp("prettyPrint: \n" + jsonObject.toString(3));
				return temp;

			}
		} catch (JSONException e) {
			L.outp("failed: " + e);
			e.printStackTrace();
		}
		return null;
	}

	public static String formatPrintNormal(String json) {
		String temp = json;
		temp = temp.replaceAll("\t", "<pre>   </pre>");
		temp = temp.replaceAll(" ", "<pre> </pre>");
		return temp.replace("\n", "<br/>");
	}

	public static String formatPrint(String json) {
		String temp = prettyPrint(json, false).replaceAll("\n", "<br/>").replaceAll(" ", "&nbsp;");
		// L.out("prettyPrint: \n" + temp);
		return temp;

	}
}