package com.ii.mobile.tab;

public class StatusType {
	String type = null;
	String typeID = null;

	public StatusType(String title, String typeID) {
		this.type = title;
		this.typeID = typeID;
	}

	public static String lookUp(String key) {
		for (int i = 0; i < BreakActivity.statusTypes.length; i++)
			if (BreakActivity.statusTypes[i].type.equals(key))
				return BreakActivity.statusTypes[i].typeID;
		return null;
	}

}