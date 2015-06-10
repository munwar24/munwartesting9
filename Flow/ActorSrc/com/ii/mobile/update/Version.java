package com.ii.mobile.update;

import com.google.gson.annotations.SerializedName;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public class Version extends GJon {

	@SerializedName("VersionNumber")
	String versionNumber = null;
	@SerializedName("VersionComment")
	String versionComment = null;
	@SerializedName("VersionRequired")
	boolean versionRequired = false;

	static public Version getGJon(String json) {
		L.out("json: " + json);
		Version version = (Version) getJSonObject(json, Version.class);
		return version;
	}

	@Override
	public boolean validate() {
		if (versionNumber != null)
			validated = true;
		else
			L.out("Unable to validate Version");
		return validated;
	}

	@Override
	public String toString() {
		return "Version: " + versionNumber + " comment: " + versionComment + " " + versionRequired;
	}
}
