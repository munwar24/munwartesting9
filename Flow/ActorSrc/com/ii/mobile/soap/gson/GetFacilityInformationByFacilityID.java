package com.ii.mobile.soap.gson;

import com.ii.mobile.util.L;

public class GetFacilityInformationByFacilityID extends GJon {
	FacilityInformations facilityInformations = null;

	static class FacilityInformations {
		FacilityInformation facilityInformation;

		@Override
		public String toString() {
			return facilityInformation.toString();
		}
	}

	static class FacilityInformation {

		// @SerializedName("@attributes")
		// Attributes attributes;
		String timeZone;
		String facilityName;

		@Override
		public String toString() {
			return "  facilityName: " + facilityName
					+ "\n  timeZone: " + timeZone;
		}
	}

	// class Attributes {
	// String timeZone;
	// String facilityName;
	//
	// @Override
	// public String toString() {
	// return "  facilityName: " + facilityName
	// + "\n  timeZone: " + timeZone;
	// }
	// }

	@Override
	public boolean validate() {

		if (facilityInformations != null
				&& facilityInformations.facilityInformation != null)
			validated = true;
		else
			L.out("Unable to validate GetFacilityInformationByFacilityID");
		return validated;
	}

	static public GetFacilityInformationByFacilityID getGJon(String json) {
		GetFacilityInformationByFacilityID getFacilityInformationByFacilityID =
				(GetFacilityInformationByFacilityID) getJSonObject(json, GetFacilityInformationByFacilityID.class);
		return getFacilityInformationByFacilityID;
	}

	@Override
	public String toString() {
		if (isValidated()) {
			return facilityInformations.toString();
		}
		return null;
	}
}
