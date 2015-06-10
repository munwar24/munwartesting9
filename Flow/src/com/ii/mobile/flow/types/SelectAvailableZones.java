package com.ii.mobile.flow.types;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public class SelectAvailableZones extends GJon {

	@Override
	public String toString() {
		if (selectAvailableZonesInner == null)
			return "ERROR: selectLocationsInner is null";
		return "AssignedZone: " + assignedZone + "\n" + selectAvailableZonesInner.toString();
	}

	@SerializedName("SelectAvailableZonesInner")
	public SelectAvailableZonesInner selectAvailableZonesInner = new SelectAvailableZonesInner();
	@SerializedName("assignedZone")
	public String assignedZone = "";

	public class SelectAvailableZonesInner extends KeepNames {

		public SelectAvailableZonesInner() {
			// L.out("created inner");
		}

		@Override
		public String toString() {
			String temp = "Targets: " + targets.size() + "\n";
			for (Targets target : targets) {
				temp += target.toString() + "\n";
			}
			return temp;
		}

		@SerializedName("targets")
		public List<Targets> targets = new ArrayList<Targets>();

	}

	public class Targets extends KeepNames {
		@Override
		public String toString() {
			return ""
					// +name: " + name
					+ "    brief: " + brief
					+ "    _id: " + _id
					+ "    assignedZone: " + assignedZone
					+ "    name: " + name;
		}

		@SerializedName("name")
		public String name;
		@SerializedName("brief")
		public String brief;
		@SerializedName("_id")
		public String _id;
		@SerializedName("assignedZone")
		public String assignedZone = "";
	}

	public class Items extends KeepNames {
		@SerializedName("name")
		public String name;
		@SerializedName("brief")
		public String brief;
		// @SerializedName("facility_id")
		// String facilityId;
		// @SerializedName("label")
		// String label;
		// @SerializedName("geography")
		// Geography geography = new Geography();
		@SerializedName("_id")
		public String _id;

		// @SerializedName("description")
		// public String description;
		// @SerializedName("hirNode_id")
		// public String hirNode_id;

		@Override
		public String toString() {
			return ""
					// +name: " + name
					+ "    brief: " + brief
					+ "    _id: " + _id
					+ "    name: " + name;
		}
	}

	@Override
	public boolean validate() {
		// L.out("GetActionStatus: " + this);
		if (selectAvailableZonesInner != null
				&& selectAvailableZonesInner.targets != null)
			validated = true;
		else
			L.out("Unable to validate GetActionStatus");
		return validated;
	}

	static public SelectAvailableZones getGJon(String json) {
		L.out("json: " + json);
		// PrettyPrint.prettyPrint(json, true);
		// L.out("json: " + json);
		SelectAvailableZones selectAvailableZones = (SelectAvailableZones) getJSonObject(json, SelectAvailableZones.class);
		// L.out("output: " + selectLocations.getNewJson());
		// PrettyPrint.prettyPrint(selectLocations.getJson(), true);
		if (!selectAvailableZones.validate())
			return null;
		selectAvailableZones.cleanUp();
		return selectAvailableZones;
	}

	private void cleanUp() {
		if (validate()) {
			for (Targets target : selectAvailableZonesInner.targets) {
				if (target.assignedZone != null) {
					this.assignedZone = target.assignedZone;
					selectAvailableZonesInner.targets.remove(target);
					return;
				}
			}
		}

	}

	public static String n(String string, String value, int space) {
		return space(space) + string + ": " + value + "\n";
	}

	public static String space(int space) {
		String temp = "";
		for (int i = 0; i < space; i++) {
			temp += "    ";
		}
		return temp;
	}

	public String getZoneId(String name) {

		for (Targets target : selectAvailableZonesInner.targets) {
			if (name.equals(target.name)) {
				L.out("getZoneId: " + target._id);
				return target._id;
			}
		}
		L.out("ERROR: Not found Zone: " + name);
		return null;
	}

	public int getZoneIndex(String zoneId) {
		L.out("zoneId: " + zoneId);
		int i = 0;
		for (Targets target : selectAvailableZonesInner.targets) {
			if (zoneId.equals(target._id))
				return i;
			i += 1;
		}
		return -1;
	}
}
