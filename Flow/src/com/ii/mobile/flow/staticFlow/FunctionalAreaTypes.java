package com.ii.mobile.flow.staticFlow;

import com.ii.mobile.util.L;

public enum FunctionalAreaTypes {
	INSTANCE;

	public final String TRANSPORTATION = "transportation";

	public final String[] functionalAreaTypeIds =
			new String[] {
					"510915224a7de6e90338c73c",
					"510915224a7de6e90338c73d",
					"510915224a7de6e90338c73e",
					"510915224a7de6e90338c73f",
					"513490144ddecb040826afe4",
					"510915224a7de6e90338c73b" };

	public final String[] functionalAreaNames =
			new String[] {
					"Environmental",
					"Other",
					"Laundry",
					"Custom",
					"Physio",
					"Transportation" };

	public String findFunctionalAreaName(String functionalAreaId) {
		int i = getStatus(functionalAreaTypeIds, functionalAreaId);
		if (i > -1)
			return functionalAreaNames[i];
		else
			L.out("didnt find in delayTypeIds: " + functionalAreaId);
		return null;
	}

	public String findFunctionalAreaId(String functionalAreaName) {
		int i = getStatus(functionalAreaNames, functionalAreaName);
		if (i > -1)
			return functionalAreaTypeIds[i];
		else
			L.out("didnt find in delayTypeNames: " + functionalAreaName);
		return "None";
	}

	private int getStatus(String[] functionalAreaStrings, String testField) {
		if (testField == null)
			return -1;
		for (int i = 0; i < functionalAreaStrings.length; i++) {
			if (testField.contains(functionalAreaStrings[i]))
				return i;
		}
		return -1;
	}

}
