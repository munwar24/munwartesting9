package com.ii.mobile.flow.types;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public class ActionSelect extends GJon {

	@Override
	public String toString() {
		if (actionSelectInner == null)
			return "ERROR: ActionSelect is null";
		return actionSelectInner.toString();
	}

	@SerializedName("ActionSelect")
	public ActionSelectInner actionSelectInner = new ActionSelectInner();

	public class ActionSelectInner extends KeepNames {

		public ActionSelectInner() {
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
		@SerializedName("name")
		public String name;
		@SerializedName("facility_id")
		public String facilityId;
		@SerializedName("label")
		public String label;
		@SerializedName("geography")
		Geography geography = new Geography();
		@SerializedName("_id")
		public String _id;

		@Override
		public String toString() {
			return "   name: " + name
					+ "    _id: " + _id
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
		if (actionSelectInner != null
				&& actionSelectInner.stores != null)
			validated = true;
		else
			L.out("Unable to validate GetActionStatus");
		return validated;
	}

	static public ActionSelect getGJon(String json) {
		L.out("json: " + json);
		ActionSelect actionSelect = (ActionSelect) getJSonObject(json, ActionSelect.class);
		L.out("output: " + actionSelect.getNewJson());
		return actionSelect;
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
		Stores store = actionSelectInner.stores.get(0);
		// L.out("items: " + store.items.size());
		Items item = store.items.get((int) (Math.random() * store.items.size()));
		// L.out("item: " + item.toString());
		return item;
	}

	public String getRoomName(String roomId) {
		Stores store = actionSelectInner.stores.get(0);
		// L.out("items: " + store.items.size());
		for (Items item : store.items) {
			if (roomId.equals(item._id))
				return item.name;
		}
		return null;
	}
}
