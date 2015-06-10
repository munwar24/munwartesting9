package com.ii.mobile.flow.staticFlow;

import java.util.ArrayList;
import java.util.List;

import com.ii.mobile.util.L;

public enum Genders {
	INSTANCE;

	List<Gender> genderList = new ArrayList<Gender>();

	private final String NOT_APPLICABLE = "51fb76457097d30340253e30";

	Genders() {
		genderList.add(new Gender("50f55167ec02555879567fe9", "Male"));
		genderList.add(new Gender("5136f1a74ddecb07c8b56b39", "Female"));
		genderList.add(new Gender("5136f1b44ddecb07c8b56b3a", "Other"));
		genderList.add(new Gender(NOT_APPLICABLE, "N/A"));
	}

	public String getDescription(String objectId) {
		Gender flow = getObjectId(objectId);
		if (flow != null)
			return flow.description;
		return "";
	}

	private Gender getObjectId(String objectId) {
		for (Gender flow : genderList) {
			if (flow.objectId.equals(objectId))
				return flow;
		}
		// L.out("ERROR: Didn't find objectId: " + objectId);
		return null;
	}

	public String getGenderId(String genderName) {
		L.out("genderName: " + genderName);
		if (genderName == null)
			return NOT_APPLICABLE;
		for (Gender flow : genderList) {
			if (flow.description.equals(genderName))
				return flow.objectId;
		}
		// L.out("ERROR: Didn't find getGenderId: " + genderName);
		return null;
	}

	public String getRandomId() {
		Gender mode = genderList.get((int) (Math.random() * genderList.size()));
		// L.out("mode: " + mode);
		return mode.objectId;
	}

	public String[] getGenders() {
		String[] temp = new String[genderList.size() + 1];
		int count = 1;
		temp[0] = "";
		for (Gender mode : genderList) {
			temp[count] = mode.description;
			count += 1;
		}
		return temp;
	}

	class Gender {
		String objectId;

		String description;

		public Gender(String objectId, String description) {
			this.objectId = objectId;
			this.description = description;
		}

	}

}