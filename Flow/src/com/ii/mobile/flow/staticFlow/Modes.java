package com.ii.mobile.flow.staticFlow;

import java.util.ArrayList;
import java.util.List;

import com.ii.mobile.util.L;

public enum Modes {
	INSTANCE;
	public final String NOT_APPLICABLE = "51091d379336624902b580b5";
	List<Mode> modeList = new ArrayList<Mode>();

	Modes() {
		modeList.add(new Mode("51091d379336624902b580b1", "AMB", "Ambulatory"));
		modeList.add(new Mode("51091d379336624902b580b2", "BED", "Bed"));
		modeList.add(new Mode("51091d379336624902b580b3", "STR", "Stretcher"));
		modeList.add(new Mode("51091d379336624902b580b4", "WC", "Wheelchair"));
		modeList.add(new Mode(NOT_APPLICABLE, "NA", "None"));
		modeList.add(new Mode("51091d379336624902b580b6", "ALR", "Already on Equipment"));
		modeList.add(new Mode("51091d379336624902b580b7", "BIG", "Bariatric WC"));
		modeList.add(new Mode("51091d379336624902b580b8", "BRI", "Bring WC"));
		modeList.add(new Mode("51091d379336624902b580b9", "BRN", "Bring Stretcher"));
		modeList.add(new Mode("51091d379336624902b580ba", "CRB", "Crib"));
		modeList.add(new Mode("51091d379336624902b580bb", "BCR", "Bring Crib"));
		modeList.add(new Mode("51091d379336624902b580bc", "CCR", "Cage Crib"));
		modeList.add(new Mode("51091d379336624902b580bd", "JCR", "Junior Crib"));
		modeList.add(new Mode("51091d379336624902b580be", "SPB", "Sport Bed"));
		modeList.add(new Mode("51091d379336624902b580bf", "WGN", "Wagon"));
	}

	public String getName(String objectId) {
		Mode flow = getObjectId(objectId);
		if (flow != null)
			return flow.name;
		return null;
	}

	public String getDescription(String objectId) {
		Mode mode = getObjectId(objectId);
		if (mode != null)
			return mode.description;
		return "";
	}

	public String getId(String description) {
		Mode flow = getDescriptionId(description);
		if (flow != null)
			return flow.objectId;
		return NOT_APPLICABLE;
	}

	// public String getId(String description) {
	// Mode Mode = getObjectId(description);
	// if (Mode != null)
	// return Mode.objectId;
	// L.out("ERROR: Not found Mode: " + description);
	// return null;
	// }

	private Mode getDescriptionId(String description) {
		for (Mode flow : modeList) {
			if (flow.description.equals(description))
				return flow;
		}
		L.out("ERROR: Didn't find description: " + description);
		return null;
	}

	private Mode getObjectId(String objectId) {
		for (Mode flow : modeList) {
			if (flow.objectId.equals(objectId))
				return flow;
		}
		// L.out("ERROR: Didn't find objectId: " + objectId);
		return null;
	}

	public String getRandomId() {
		Mode mode = modeList.get((int) (Math.random() * modeList.size()));
		L.out("mode: " + mode);
		return mode.objectId;
	}

	public String[] getModes() {
		String[] temp = new String[modeList.size() + 1];
		int count = 1;
		temp[0] = "";
		for (Mode mode : modeList) {
			temp[count] = mode.description;
			count += 1;
		}
		return temp;
	}

	class Mode {
		String objectId;
		String name;
		String description;

		public Mode(String objectId, String name, String description) {
			this.objectId = objectId;
			this.name = name;
			this.description = description;
		}

	}

}