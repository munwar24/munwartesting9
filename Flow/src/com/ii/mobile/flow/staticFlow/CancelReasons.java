package com.ii.mobile.flow.staticFlow;

import java.util.ArrayList;
import java.util.List;

import com.ii.mobile.util.L;

public enum CancelReasons {
	INSTANCE;

	List<CancelReason> cancelReasons = new ArrayList<CancelReason>();

	CancelReasons() {
		cancelReasons.add(new CancelReason("51091d379336624902b580c9", "Already Done"));
		cancelReasons.add(new CancelReason("51091d379336624902b580ca", "Delay too long"));
		cancelReasons.add(new CancelReason("51091d379336624902b580cb", "Doctor Cancel"));
		cancelReasons.add(new CancelReason("51091d379336624902b580cc", "Duplicate Task"));
		cancelReasons.add(new CancelReason("51091d379336624902b580cd", "Nursing Cancel"));
		cancelReasons.add(new CancelReason("51091d379336624902b580ce", "Other"));
		cancelReasons.add(new CancelReason("51091d379336624902b580cf", "Occupied Bed"));
		cancelReasons.add(new CancelReason("51091d379336624902b580d0", "Patient Refused"));
		cancelReasons.add(new CancelReason("51091d379336624902b580d1", "Patient in another procedure"));
		cancelReasons.add(new CancelReason("51091d379336624902b580d2", "Patient Gone"));
		cancelReasons.add(new CancelReason("51091d379336624902b580d3", "Clinical Staff Transported Patient"));
		cancelReasons.add(new CancelReason("51091d379336624902b580d4", "Task Completed by Requestor"));
		cancelReasons.add(new CancelReason("51091d379336624902b580d5", "Ticket to Ride"));
	}

	public String getName(String objectId) {
		CancelReason cancelReason = getObjectId(objectId);
		if (cancelReason != null)
			return cancelReason.name;
		return null;
	}

	private CancelReason getObjectId(String objectId) {
		if (objectId == null)
			return null;
		for (CancelReason cancelCode : cancelReasons) {
			if (cancelCode.objectId.equals(objectId))
				return cancelCode;
		}
		L.out("ERROR: Didn't find objectId: " + objectId);
		return null;
	}

	public String getId(String name) {
		CancelReason cancelReason = getNameId(name);
		if (cancelReason != null)
			return cancelReason.objectId;
		return null;
	}

	private CancelReason getNameId(String name) {
		if (name == null)
			return null;
		for (CancelReason cancelCode : cancelReasons) {
			if (cancelCode.name.equals(name))
				return cancelCode;
		}
		L.out("ERROR: Didn't find objectId: " + name);
		return null;
	}

	public String[] getCancelReasons() {
		String[] temp = new String[cancelReasons.size() + 1];
		int count = 1;
		temp[0] = "";
		for (CancelReason cancelReason : cancelReasons) {
			temp[count] = cancelReason.name;
			count += 1;
		}
		return temp;

	}

	class CancelReason {
		String objectId;
		String name;

		// String description;

		public CancelReason(String objectId, String name) {
			this.objectId = objectId;
			this.name = name;
			// this.description = description;
		}
	}

	public String getRandomId() {
		CancelReason costCode = cancelReasons.get((int) (Math.random() * cancelReasons.size()));
		// L.out("costCode: " + costCode);
		return costCode.objectId;
	}
}