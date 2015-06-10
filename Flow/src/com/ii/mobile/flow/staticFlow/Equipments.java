package com.ii.mobile.flow.staticFlow;

import java.util.ArrayList;
import java.util.List;

import com.ii.mobile.util.L;

public enum Equipments {
	INSTANCE;
	public final String NOT_APPLICABLE = "51091d369336624902b580ad";
	List<Equipment> equipmentList = new ArrayList<Equipment>();

	Equipments() {
		equipmentList.add(new Equipment("51091d369336624902b580aa", "O2", "Oxygen"));
		equipmentList.add(new Equipment("51091d369336624902b580ab", "IV", "IV"));
		equipmentList.add(new Equipment("51091d369336624902b580ac", "Cart", "Cart"));
		equipmentList.add(new Equipment(NOT_APPLICABLE, "None", "None"));
		equipmentList.add(new Equipment("51091d369336624902b580ae", "IVA", "IVA"));
		equipmentList.add(new Equipment("51091d369336624902b580af", "VEN", "Vent"));
		equipmentList.add(new Equipment("51091d369336624902b580b0", "MON", "Monitor"));
	}

	public String getName(String objectId) {
		Equipment equipment = getObjectId(objectId);
		if (equipment != null)
			return equipment.name;
		return null;
	}

	public String getDescription(String objectId) {
		Equipment equipment = getObjectId(objectId);
		if (equipment != null)
			return equipment.description;
		return "";
	}

	public String getId(String description) {
		Equipment equipment = getDescriptionId(description);
		if (equipment != null)
			return equipment.objectId;
		// L.out("ERROR: Not found equipment: " + description);
		return NOT_APPLICABLE;
	}

	private Equipment getDescriptionId(String description) {
		if (description == null)
			return null;
		for (Equipment equipment : equipmentList) {
			if (equipment.description.equals(description))
				return equipment;
		}
		L.out("ERROR: Didn't find name: " + description);
		return null;
	}

	private Equipment getObjectId(String objectId) {
		if (objectId == null)
			return null;
		for (Equipment equipment : equipmentList) {
			if (equipment.objectId.equals(objectId))
				return equipment;
		}
		// L.out("ERROR: Didn't find name: " + objectId);
		return null;
	}

	class Equipment {
		String objectId;
		String name;
		String description;

		public Equipment(String objectId, String name, String description) {
			this.objectId = objectId;
			this.name = name;
			this.description = description;
		}
	}

	public String getRandomId() {
		Equipment equipment = equipmentList.get((int) (Math.random() * equipmentList.size()));
		// L.out("equipment: " + equipment);
		return equipment.objectId;
	}

	public String[] getEquipments() {
		String[] temp = new String[equipmentList.size() + 1];
		int count = 1;
		temp[0] = "";
		for (Equipment equipment : equipmentList) {
			temp[count] = equipment.description;
			count += 1;
		}
		return temp;

	}
}