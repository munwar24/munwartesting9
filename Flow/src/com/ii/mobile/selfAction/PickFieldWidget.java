package com.ii.mobile.selfAction;

import android.app.Activity;

//import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsDataForScreenByFacilityID.PickList;
import com.ii.mobile.util.L;

public class PickFieldWidget {

	protected Activity activity;

	public PickFieldWidget(Activity activity) {
		this.activity = activity;
	}

	public String lookUpFromValue(String pickList, String value) {
//		PickList[] picks = ((Cache) activity).getPickListGeography(pickList).pickList;
//		// L.out("pick" + picks.length);
//		// L.out("pick1" + picks[0].textPart + " " + picks[0].valuePart);
//		for (int i = 0; i < picks.length; i++) {
//			if (picks[i].valuePart.equals(value)) {
//				return picks[i].textPart;
//			}
//		}
		L.out("Unable to find: " + value);
		return value;
	}

	public String lookUpFromText(String pickList, String text) {
//		if (text == null || text.equals(""))
//			return "";
//		PickList[] picks = ((Cache) activity).getPickListGeography(pickList).pickList;
//		// L.out("pick" + picks.length);
//		// L.out("pick1" + picks[0].textPart + " " + picks[0].valuePart);
//		for (int i = 0; i < picks.length; i++) {
//			if (picks[i].textPart.equals(text)) {
//				return picks[i].valuePart;
//			}
//		}
		L.out("Unable to find: " + text);
		return text;
	}
}
