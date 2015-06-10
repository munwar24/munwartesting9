package com.ii.mobile.flow.types;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public class SelectLocations extends GJon {

	@Override
	public String toString() {
		if (selectLocationsInner == null)
			return "ERROR: selectLocationsInner is null";
		return selectLocationsInner.toString();
	}

	@SerializedName("SelectLocationsInner")
	public SelectLocationsInner selectLocationsInner = new SelectLocationsInner();

	public class SelectLocationsInner extends KeepNames {

		public SelectLocationsInner() {
			// L.out("created inner");
		}

		@Override
		public String toString() {
			String temp = "Stores: " + stores.size() + "\n";
			for (Stores store : stores) {
				temp += store.toString() + "\n";
			}
			return temp;
		}

		@SerializedName("stores")
		public List<Stores> stores = new ArrayList<Stores>();
	}

	public class Stores extends KeepNames {
		@Override
		public String toString() {
			String temp = "Items: " + items.size() + "\n";
			for (Items item : items) {
				temp += item.toString() + "\n";
			}
			return temp;
		}

		@SerializedName("items")
		public List<Items> items = new ArrayList<Items>();
	}

	public class Items extends KeepNames {
		// @SerializedName("name")
		// public String name;
		@SerializedName("facility_id")
		String facilityId;
		@SerializedName("label")
		String label;
		@SerializedName("geography")
		Geography geography = new Geography();
		@SerializedName("_id")
		public String _id;
		@SerializedName("description")
		public String description;
		@SerializedName("hirNode_id")
		public String hirNode_id;

		@Override
		public String toString() {
			return ""
					// +name: " + name
					+ "    description: " + description
					+ "    _id: " + _id
					+ "    hirNode_id: " + hirNode_id
					+ "    facilityId: " + facilityId
					+ "    geography: " + geography;
		}
	}

	public class Geography extends KeepNames {
		@SerializedName("facilityHirNode_id")
		boolean facilityId;
		@SerializedName("buildingHirNode_id")
		String buildingId;
		@SerializedName("floorHirNode_id")
		String floorId;

		@Override
		public String toString() {
			return "    buildingId: " + buildingId
					+ "    floorId: " + floorId;
		}
	}

	@Override
	public boolean validate() {
		// L.out("GetActionStatus: " + this);
		if (selectLocationsInner != null
				&& selectLocationsInner.stores != null)
			validated = true;
		else
			L.out("Unable to validate GetActionStatus");
		return validated;
	}

	static public SelectLocations getGJon(String json) {
		// L.out("json: " + json);
		// PrettyPrint.prettyPrint(json, true);
		// L.out("json: " + json);
		SelectLocations selectLocations = (SelectLocations) getJSonObject(json, SelectLocations.class);
		// L.out("output: " + selectLocations.getNewJson());
		// PrettyPrint.prettyPrint(selectLocations.getJson(), true);
		return selectLocations;
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

	public Items getRandomRoom() {
		// L.out("stores: " + selectLocationsInner.stores.size());
		Stores store = selectLocationsInner.stores.get(0);
		// L.out("items: " + store.items.size());
		Items item = store.items.get((int) (Math.random() * store.items.size()));
		// L.out("item: " + item.toString());
		return item;
	}

	public String getRoomId(String name) {
		Stores store = selectLocationsInner.stores.get(0);
		for (Items item : store.items) {
			if (name.equals(item.description)) {
				// L.out("getRoomId: " + item);
				return item.hirNode_id;
			}
		}
		L.out("ERROR: Not found Room: " + name);
		return null;
	}

	public String getRoomName(String roomId) {
		Stores store = selectLocationsInner.stores.get(0);
		// L.out("items: " + store.items.size());
		for (Items item : store.items) {
			if (roomId.equals(item.hirNode_id))
				return item.description;
		}
		return null;
	}
}
