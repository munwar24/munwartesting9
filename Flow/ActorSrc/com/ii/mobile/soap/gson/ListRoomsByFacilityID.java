package com.ii.mobile.soap.gson;

import com.google.gson.annotations.SerializedName;
import com.ii.mobile.util.L;

public class ListRoomsByFacilityID extends GJon {
	private static int maxRecordsPrinted = 10;
	Rooms rooms;

	static class Rooms {
		@SerializedName("steLocations")
		SteLocations[] steLocations;

		@Override
		public String toString() {
			String temp = "ListRoomsByFacilityID (" + steLocations.length + "): " + "\n";
			for (int i = 0; i < steLocations.length; i++) {
				if (i > maxRecordsPrinted) {
					temp += "Truncated " + (steLocations.length - i) + " records out of "
							+ steLocations.length + " rooms";
					return temp;
				}
				temp += steLocations[i].toString();
			}
			return temp;
		}
	}

	static public class SteLocations {
		// @SerializedName("@attributes")
		// Attributes attributes;
		public String title;
		public String hirNode;

		@Override
		public String toString() {
			return " Title: " + title + " hirNode: " + hirNode + "\n";
		}
	}

	// class Attributes {
	// String title;
	// String hirNode;
	// }

	@Override
	public boolean validate() {
		if (rooms != null
				&& rooms.steLocations != null
				&& rooms.steLocations[0] != null)
			validated = true;
		else
			L.out("Unable to validate ListTaskClassesByFacilityID");
		return validated;
	}

	static public ListRoomsByFacilityID getGJon(String json) {
		ListRoomsByFacilityID functionalAreas = (ListRoomsByFacilityID) getJSonObject(json, ListRoomsByFacilityID.class);
		return functionalAreas;
	}

	@Override
	public String toString() {
		if (isValidated()) {
			return rooms.toString();
		}
		return null;
	}

	public String lookUp(String node) {
		if (isValidated()) {
			for (int i = 0; i < rooms.steLocations.length; i++) {
				if (rooms.steLocations[i].hirNode.equals(node))
					return rooms.steLocations[i].title;
			}
		}
		return "Not found";
	}

	public SteLocations[] getSteLocations() {
		return rooms.steLocations;
	}
}
