package com.ii.mobile.soap.gson;

import com.google.gson.annotations.SerializedName;
import com.ii.mobile.util.L;

public class GetIsDST extends GJon {
	@SerializedName("ValidateUserLogin")
	GValidateUserLogin gValidateUserLogin;

	public class GValidateUserLogin {
		UsersFacilityDetails usersFacilityDetails;
	}

	static class UsersFacilityDetails {
		@SerializedName("@attributes")
		Attributes attributes;
	}

	static class Attributes {

	}

	static public GetIsDST getGJon(String json) {
		L.out("json: " + json);
		GetIsDST gUser = (GetIsDST) getJSonObject(json, GetIsDST.class);
		return gUser;
	}

	@Override
	public boolean validate() {
		if (gValidateUserLogin != null
				&& gValidateUserLogin.usersFacilityDetails != null
				&& gValidateUserLogin.usersFacilityDetails.attributes != null)
			validated = true;
		return validated;
	}

	@Override
	public String toString() {
		return "no tostring";
	}

}
