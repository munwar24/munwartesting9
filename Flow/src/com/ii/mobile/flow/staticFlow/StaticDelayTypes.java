package com.ii.mobile.flow.staticFlow;

import com.ii.mobile.util.L;

public enum StaticDelayTypes {
	INSTANCE;
	public final String[] delayTypeIds =
			new String[] { "51091d379336624902b580d6", "51091d379336624902b580d7",
					"51091d379336624902b580d8", "51091d379336624902b580d9", "51091d379336624902b580da",
					"51091d379336624902b580db", "51091d379336624902b580dc", "51091d379336624902b580dd",
					"51091d379336624902b580de", "51091d379336624902b580df", "51091d379336624902b580e0",
					"51091d379336624902b580e1", "51091d379336624902b580e2", "51091d379336624902b580e3",
					"51091d379336624902b580e4", "51091d379336624902b580e5", "51091d379336624902b580e6",
					"51091d379336624902b580e7", "51091d379336624902b580e8", "51091d379336624902b580e9",
					"51091d379336624902b580ea", "51091d379336624902b580eb", "51091d379336624902b580ec",
					"51091d379336624902b580ed", "51091d379336624902b580ee", "51091d379336624902b580ef",
					"51091d379336624902b580f0", "51091d379336624902b580f1" };

	public final String[] delayTypeNames =
			new String[] { "Administration caused", "Area not accessible", "Area occupied",
					"Assistance needed", "Contract service", "Doctor caused",
					"Equipment or tools not available", "Linen not available", "Nursing Caused", "Other",
					"Patient caused", "Patient not located", "Patient Room occupied",
					"Repair parts not available", "Supplies not available", "Unable to repair",
					"Patient Refused", "CT Wait", "Elevators", "Equipment Change", "Family Caused",
					"Hall Pass", "O2 Assistance", "Waiting on Pt Ride", "X-Ray Wait", "Ticket to Ride",
					"Delay at test site", "Chart not available", };

	public String findDelayName(String delayTypeId) {
		L.out("delayTypeId: " + delayTypeId);
		int i = getStatusId(delayTypeIds, delayTypeId);
		L.out("findDelayName: " + i + " " + delayTypeId);
		if (i > -1)
			return delayTypeNames[i];
		else
			L.out("didnt find in delayTypeIds: " + delayTypeId);
		return null;
	}

	public String findDelayId(String delayTypeName) {
		int i = getStatus(delayTypeNames, delayTypeName);
		if (i > -1)
			return delayTypeIds[i];
		else
			L.out("didnt find in delayTypeNames: " + delayTypeName);
		return "None";
	}

	private int getStatusId(String[] delayTypeIds, String delayTypeId) {
		if (delayTypeId == null)
			return -1;
		for (int i = 0; i < delayTypeIds.length; i++) {
			if (delayTypeIds[i].equals(delayTypeId))
				return i;
		}
		return -1;
	}

	private int getStatus(String[] delayTypeStrings, String delayField) {
		if (delayField == null)
			return -1;
		for (int i = 0; i < delayTypeStrings.length; i++) {
			if (delayField.contains(delayTypeStrings[i]))
				return i;
		}
		return -1;
	}

}
