package com.ii.mobile.flow.staticFlow;

import java.util.ArrayList;
import java.util.List;

import com.ii.mobile.util.L;

public enum CostCodes {
	INSTANCE;

	List<CostCode> costCodeList = new ArrayList<CostCode>();

	CostCodes() {
		costCodeList.add(new CostCode("511e1a633a6aeb36b5562373", "Accounts"));
		costCodeList.add(new CostCode("511e1a633a6aeb36b5562374", "Books"));
		costCodeList.add(new CostCode("511e1a633a6aeb36b5562375", "Computers"));
		costCodeList.add(new CostCode("511e1a633a6aeb36b5562376", "Correspondence"));
		costCodeList.add(new CostCode("511e1a633a6aeb36b5562377", "Health Care"));
		costCodeList.add(new CostCode("511e1a633a6aeb36b5562378", "Home Care"));
		costCodeList.add(new CostCode("511e1a633a6aeb36b5562379", "Management"));
		costCodeList.add(new CostCode("511e1a633a6aeb36b556237a", "Maintainence"));
		costCodeList.add(new CostCode("511e1a633a6aeb36b556237b", "NoteBook"));
	}

	public String getName(String objectId) {
		CostCode flow = getObjectId(objectId);
		if (flow != null)
			return flow.name;
		return null;
	}

	private CostCode getObjectId(String objectId) {
		if (objectId == null)
			return null;
		for (CostCode costCode : costCodeList) {
			if (costCode.objectId.equals(objectId))
				return costCode;
		}
		L.out("ERROR: Didn't find objectId: " + objectId);
		return null;
	}

	public String[] getCostCodes() {
		String[] temp = new String[costCodeList.size() + 1];
		int count = 1;
		temp[0] = "";
		for (CostCode costCode : costCodeList) {
			temp[count] = costCode.name;
			count += 1;
		}
		return temp;

	}

	class CostCode {
		String objectId;
		String name;

		// String description;

		public CostCode(String objectId, String name) {
			this.objectId = objectId;
			this.name = name;
			// this.description = description;
		}
	}

	public String getRandomId() {
		CostCode costCode = costCodeList.get((int) (Math.random() * costCodeList.size()));
		// L.out("costCode: " + costCode);
		return costCode.objectId;
	}
}