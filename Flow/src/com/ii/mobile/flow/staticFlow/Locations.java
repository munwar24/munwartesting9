package com.ii.mobile.flow.staticFlow;

import java.util.List;

import com.ii.mobile.flow.types.SelectLocations;
import com.ii.mobile.flow.types.SelectLocations.Items;
import com.ii.mobile.flow.types.SelectLocations.Stores;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.util.L;

public enum Locations {
	INSTANCE;

	private Stores getStore() {
		SelectLocations selectLocations = UpdateController.selectLocations;
		if (selectLocations == null) {
			return null;
		}
		List<Stores> stores = selectLocations.selectLocationsInner.stores;
		if (stores == null)
			return null;
		Stores firstStore = stores.get(0);
		return firstStore;
	}

	public String[] getLocations() {
		Stores firstStore = getStore();
		if (firstStore == null) {
			L.out("ERROR: getLocation is null!");
			return null;
		}
		String[] temp = new String[firstStore.items.size() + 1];
		int count = 1;
		temp[0] = "";
		for (Items item : firstStore.items) {
			temp[count] = item.description;
			count += 1;
		}
		return temp;
	}

	public String findLocationId(String locationName) {
		Stores firstStore = getStore();
		if (firstStore == null)
			return null;
		for (Items item : firstStore.items) {
			if (locationName.equals(item.description))
				return item._id;
		}
		return null;
	}

}