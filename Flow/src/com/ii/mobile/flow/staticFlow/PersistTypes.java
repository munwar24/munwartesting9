package com.ii.mobile.flow.staticFlow;

import java.util.ArrayList;
import java.util.List;

import com.ii.mobile.util.L;

public enum PersistTypes {
	INSTANCE;

	List<PersistType> persistTypesList = new ArrayList<PersistType>();

	PersistTypes() {
		persistTypesList.add(new PersistType("51091d379336624902b580c0", "Annual"));
		persistTypesList.add(new PersistType("51091d379336624902b580c1", "Biannual"));
		persistTypesList.add(new PersistType("51091d379336624902b580c2", "BiMonthly"));
		persistTypesList.add(new PersistType("51091d379336624902b580c3", "Daily"));
		persistTypesList.add(new PersistType("51091d379336624902b580c4", "Monthly"));
		persistTypesList.add(new PersistType("51091d379336624902b580c5", "Quarterly"));
		persistTypesList.add(new PersistType("51091d379336624902b580c6", "SemiAnnual"));
		persistTypesList.add(new PersistType("51091d379336624902b580c7", "TriAnnual"));
		persistTypesList.add(new PersistType("51091d379336624902b580c8", "Weekly"));
	}

	public String getName(String objectId) {
		PersistType flow = getObjectId(objectId);
		if (flow != null)
			return flow.name;
		return null;
	}

	private PersistType getObjectId(String objectId) {
		if (objectId == null)
			return null;
		for (PersistType persistType : persistTypesList) {
			if (persistType.objectId.equals(objectId))
				return persistType;
		}
		L.out("ERROR: Didn't find objectId: " + objectId);
		return null;
	}

	class PersistType {
		String objectId;
		String name;

		// String description;

		public PersistType(String objectId, String name) {
			this.objectId = objectId;
			this.name = name;
			// this.description = description;
		}
	}

	public String getRandomId() {
		PersistType persistType = persistTypesList.get((int) (Math.random() * persistTypesList.size()));
		// L.out("persistType: " + persistType);
		return persistType.objectId;
	}

	public String[] getPersistTypes() {
		String[] temp = new String[persistTypesList.size() + 1];
		int count = 1;
		temp[0] = "";
		for (PersistType persistType : persistTypesList) {
			temp[count] = persistType.name;
			count += 1;
		}
		return temp;

	}

}