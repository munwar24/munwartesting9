package com.ii.mobile.payload.sync;

public class StatusType {
	String type = null;
	String typeID = null;

	public StatusType(String title, String typeID) {
		this.type = title;
		this.typeID = typeID;
	}

	public static String AVAILABLE = "Available";
	public static String ACTIVE = "Active";
	public static String ASSIGNED = "Assigned";
	// public static String ACTIVE = "Active";
	public static String DELAYED = "Delayed";
	public static String AT_LUNCH = "At Lunch";
	public static String ON_BREAK = "On Break";
	public static String NOT_IN = "Not In";
	public static StatusType[] statusTypes = new StatusType[] {
			new StatusType(AVAILABLE, "1"),
			new StatusType(AT_LUNCH, "5"),
			new StatusType(ON_BREAK, "6"),
			new StatusType(NOT_IN, "7"),
	};

	public static String lookUp(String key) {
		for (int i = 0; i < statusTypes.length; i++)
			if (statusTypes[i].type.equals(key))
				return statusTypes[i].typeID;
		return null;
	}

}