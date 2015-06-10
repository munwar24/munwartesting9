package com.ii.mobile.tab;

import android.app.Activity;

import com.ii.mobile.home.LoginActivity;
import com.ii.mobile.soap.ParsingSoap;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsDataForScreenByFacilityID;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsDataForScreenByFacilityID.Geography;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsDataForScreenByFacilityID.PickList;
import com.ii.mobile.util.L;

public class PickField {
	private static GetTaskDefinitionFieldsDataForScreenByFacilityID fieldData = null;

	protected Activity activity;

	public PickField(Activity activity) {
		this.activity = activity;
	}

	public static void initDataCache() {
		fieldData = null;
	}

	synchronized static protected Geography getPickListGeography(String geographyName, Activity activity) {
		if (fieldData == null) {
			String json = LoginActivity.getJSon(ParsingSoap.GET_TASK_DEFINITION_FIELDS_DATA_FOR_SCREEN_BY_FACILITY_ID, activity);
			L.out("json: " + json.length());
			fieldData = GetTaskDefinitionFieldsDataForScreenByFacilityID.getGJon(json);
			L.out("getTaskDefinitionFieldsDataForScreenByFacilityID:");
			if (fieldData == null)
				return null;
		}
		return fieldData.getGeography(geographyName);
	}

	public static String lookUpFromValue(String pickList, String value, Activity activity) {
		PickList[] picks = getPickListGeography(pickList, activity).pickList;
		// L.out("pick" + picks.length);
		// L.out("pick1" + picks[0].textPart + " " + picks[0].valuePart);
		for (int i = 0; i < picks.length; i++) {
			if (picks[i].valuePart.equals(value)) {
				return picks[i].textPart;
			}
		}
		L.out("Unable to find: " + value);
		return value;
	}

	public static String lookUpFromText(String pickList, String text, Activity activity) {
		if (text == null || text.equals(""))
			return "";
		PickList[] picks = getPickListGeography(pickList, activity).pickList;
		// L.out("pick" + picks.length);
		// L.out("pick1" + picks[0].textPart + " " + picks[0].valuePart);
		for (int i = 0; i < picks.length; i++) {
			if (picks[i].textPart.equals(text)) {
				return picks[i].valuePart;
			}
		}
		L.out("Unable to find: " + text);
		return text;
	}
}
