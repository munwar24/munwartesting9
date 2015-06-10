package com.ii.mobile.soap.gson;

import com.google.gson.annotations.SerializedName;

public class ListFunctionalAreasByFacilityID extends GJon {

	@SerializedName("FunctionalAreas")
	GFunctionalAreas gFunctionalAreas;

	static class GFunctionalAreas {
		FunctionalArea[] functionalArea;
	}

	static class FunctionalArea {
		// @SerializedName("@attributes")
		// Attributes attributes;
		String functionalAreaID;
		String title;
		String brief;

		@Override
		public String toString() {
			return "functionalAreaID: " + functionalAreaID + " title: " + title
					+ " brief: " + brief;
		}
	}

	@Override
	public boolean validate() {

		if (gFunctionalAreas != null
				&& gFunctionalAreas.functionalArea != null
				&& gFunctionalAreas.functionalArea[0] != null)
			validated = true;
		return validated;
	}

	static public ListFunctionalAreasByFacilityID getGJon(String json) {
		// BaseSoap.debugOutput("\n\n*** ListFunctionalAreasByFacilityID \n" +
		// json + " ***\n", true);
		ListFunctionalAreasByFacilityID functionalAreas =
				(ListFunctionalAreasByFacilityID) getJSonObject(json, ListFunctionalAreasByFacilityID.class);
		return functionalAreas;
	}

	@Override
	public String toString() {
		if (isValidated()) {

			FunctionalArea[] functionalAreas = gFunctionalAreas.functionalArea;
			String temp = "";
			for (int i = 0; i < functionalAreas.length; i++) {
				temp += functionalAreas[i] + "\n";
			}
			return temp;
		}
		return null;
	}
}
