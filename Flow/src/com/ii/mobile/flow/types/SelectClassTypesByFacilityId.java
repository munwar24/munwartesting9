package com.ii.mobile.flow.types;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.SerializedName;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public class SelectClassTypesByFacilityId extends GJon {

	@SerializedName("SelectClassTypesByFacilityIdInner")
	SelectClassTypesByFacilityIdInner selectClassTypesByFacilityIdInner = new SelectClassTypesByFacilityIdInner();

	public class SelectClassTypesByFacilityIdInner extends KeepNames {
		@SerializedName("targets")
		List<Targets> targets = new ArrayList<Targets>();

		@Override
		public String toString() {
			String temp = "SelectClassTypesByFacilityId targets: " + targets.size() + "\n";
			int count = 0;
			for (Targets target : targets) {
				temp += count + " " + target.toString() + "\n";
				// L.out("count: " + count);
				count += 1;
			}
			return temp;
		}
	}

	public class Targets extends KeepNames {
		@SerializedName("name")
		public String name;
		@SerializedName("facility_id")
		public String facilityId;
		@SerializedName("functionalAreaType_id")
		public String functionalAreaTypeId;
		@SerializedName("_id")
		public String _id;

		@SerializedName("fields")
		public List<Fields> fields = new ArrayList<Fields>();

		@SerializedName("customFields")
		public List<CustomFields> customFields = new ArrayList<CustomFields>();
		@SerializedName("haveScheduledDate")
		public boolean haveScheduledDate = false;

		private String printFields() {
			String temp = "";
			if (fields == null)
				return "";
			for (Fields field : fields) {
				temp += field.toString() + "\n";
			}
			return temp;
		}

		public Fields getCustomField(String name) {
			for (Fields field : fields)
				if (field.custom && field.fieldName.equalsIgnoreCase(name))
					return field;
			return null;
		}

		// private String printCustomFields() {
		// String temp = "";
		// if (customfields == null)
		// return "";
		// for (CustomFields field : customfields) {
		// temp += field.toString() + "\n";
		// }
		// return temp;
		// }

		@Override
		public String toString() {
			return "name: " + name
					+ " facilityId: " + facilityId
					+ " functionalAreaId: " + functionalAreaTypeId
					+ " _id: " + _id + "\n"
					+ printFields();
		}

		public Targets copy() {
			Targets temp = new Targets();
			for (Fields field : fields) {
				temp.fields.add(field);
			}
			return temp;
		}
	}

	public class Fields extends KeepNames {
		@SerializedName("name")
		public String name;
		@SerializedName("control")
		public String control;
		@SerializedName("label")
		public String label;
		@SerializedName("controlType")
		public String controlType;
		@SerializedName("displayOrder")
		public int displayOrder;
		@SerializedName("required")
		public boolean required;
		@SerializedName("dataType")
		public String dataType;
		@SerializedName("fieldName")
		public String fieldName;
		@SerializedName("custom")
		public boolean custom;

		@Override
		public String toString() {
			return "   fieldName: " + fieldName
					+ " control: " + control
					+ " label: " + label
					+ " custom: " + custom
					+ " controlType: " + controlType
					+ " dataType: " + dataType
					+ " required: " + required
					+ " displayOrder: " + displayOrder
					+ " name: " + name;
		}

		public Fields() {

		}
	}

	public class CustomFields extends KeepNames {
		@SerializedName("name")
		public String name;
		@SerializedName("control")
		public String control;
		@SerializedName("label")
		public String label;
		@SerializedName("dataType")
		public String dataType;
		@SerializedName("controlType")
		public String controlType;
		@SerializedName("displayOrder")
		public int displayOrder;
		@SerializedName("required")
		public boolean required;
		@SerializedName("fieldName")
		public String fieldName;

		@Override
		public String toString() {
			return "   fieldName: " + fieldName
					+ " control: " + control
					+ " label: " + label
					+ " controlType: " + controlType
					+ " required: " + required
					+ " dataType: " + dataType
					+ " displayOrder: " + displayOrder
					+ " name: " + name;
		}

		public CustomFields() {

		}
	}

	@Override
	public boolean validate() {
		if (selectClassTypesByFacilityIdInner != null
				&& selectClassTypesByFacilityIdInner.targets != null)
			validated = true;
		else
			L.out("Unable to validate SelectClassTypesByFacilityId");
		return validated;
	}

	@Override
	public String toString() {
		if (selectClassTypesByFacilityIdInner == null)
			return "ERROR: getflowStatusInner is null";
		return selectClassTypesByFacilityIdInner.toString();
	}

	static public SelectClassTypesByFacilityId getGJon(String json) {
		// L.out("json: ");
		// PrettyPrint.prettyPrint(json, true);
		SelectClassTypesByFacilityId selectClassTypesByFacilityId = (SelectClassTypesByFacilityId) getJSonObject(json, SelectClassTypesByFacilityId.class);
		if (!selectClassTypesByFacilityId.validate())
			return null;
		selectClassTypesByFacilityId.addNote();
		selectClassTypesByFacilityId.removeUnWantedFields();
		selectClassTypesByFacilityId.removeUnWantedClasses();
		selectClassTypesByFacilityId.copyCustomFields();
		// L.out("output: " + selectClassTypesByFacilityId);
		return selectClassTypesByFacilityId;
	}

	public final String NOTE_NAME = "Note";
	public static final String NOTE_TEXT = "text";
	// defined in SelfActionFragment. But avoiding dependency
	public static final String MULTIPLE_LINE_TEXT = "multipleLineText";

	private void addNote() {
		for (Targets target : selectClassTypesByFacilityIdInner.targets) {
			Fields field = new Fields();
			if (haveNote(target.fields))
				return;
			field.name = NOTE_NAME;
			field.fieldName = NOTE_NAME;
			field.controlType = MULTIPLE_LINE_TEXT;
			field.control = NOTE_NAME;
			target.fields.add(field);
		}
	}

	public static final String SCHEDULE_DATE = "scheduleDate";
	public static final String REQUESTOR = "requestor";
	public static final String COST_CODE = "costCode";
	public static final String PERSIST = "frequencyType";

	private void removeUnWantedFields() {
		for (Targets target : selectClassTypesByFacilityIdInner.targets) {

			for (int i = 0; i < target.fields.size(); i++) {
				Fields field = target.fields.get(i);
				if (field.fieldName.equals(SCHEDULE_DATE)) {
					target.fields.remove(field);
					target.haveScheduledDate = true;
				} else if (field.fieldName.equals(COST_CODE)
						// || field.fieldName.equals(SCHEDULE_DATE)
						|| field.fieldName.equals(PERSIST)
						|| field.fieldName.startsWith(REQUESTOR)) {
					// L.out("found: " + field.fieldName);
					target.fields.remove(field);
					i--;
				}
			}
		}
	}

	private void copyCustomFields() {
		for (Targets target : selectClassTypesByFacilityIdInner.targets) {
			if (target.customFields != null) {
				for (int i = 0; i < target.customFields.size(); i++) {
					CustomFields customField = target.customFields.get(i);
					Fields field = new Fields();
					field.name = customField.name;
					field.controlType = customField.controlType;
					field.custom = true;
					field.required = customField.required;
					field.control = customField.control;
					field.dataType = customField.dataType;
					field.fieldName = customField.fieldName.replace("customField.", "");
					// L.out("copied custom field: " + customField +
					// " to \nfield: " + field);
					target.fields.add(field);
				}
				target.customFields = null;
			}
		}
	}

	private void removeUnWantedClasses() {
		List<Targets> newTargets = new ArrayList<Targets>();
		GetActorStatus getActorStatus = UpdateController.getActorStatus;
		if (getActorStatus == null)
			return;
		String actorFunctionalAreaTypeId = getActorStatus.getFunctionalAreaTypeId();
		// L.out("foo: " + actorFunctionalAreaTypeId);
		// L.out("size: " + selectClassTypesByFacilityIdInner.targets.size());
		for (Targets target : selectClassTypesByFacilityIdInner.targets) {
			String targetFunctionalAreaTypeId = target.functionalAreaTypeId;
			if (actorFunctionalAreaTypeId.equals(targetFunctionalAreaTypeId)) {
				newTargets.add(target);
				// L.out("added: " + target.name);
			} else {
				// L.out("ignored: " + target.name);
			}
		}
		selectClassTypesByFacilityIdInner.targets = newTargets;
		// L.out("size: " + selectClassTypesByFacilityIdInner.targets.size());
	}

	private boolean haveNote(List<Fields> fields) {
		for (Fields field : fields) {
			if (field.name.equals(NOTE_NAME))
				return true;
		}
		return false;
	}

	public boolean haveScheduleDate(String classId) {
		Targets targets = getClassId(classId);
		if (targets == null)
			return false;
		L.out("haveScheduledDate: " + targets.haveScheduledDate);
		if (targets.haveScheduledDate)
			return true;
		return false;
	}

	// private boolean haveScheduleDate(List<Fields> fields) {
	// for (Fields field : fields) {
	// L.out("field: " + field);
	// if (field.fieldName.equals(SCHEDULE_DATE))
	// return true;
	// }
	// return false;
	// }

	public List<Targets> getClassTypes(String facilityId) {
		List<Targets> targets = new ArrayList<Targets>();
		for (Targets target : selectClassTypesByFacilityIdInner.targets) {
			if (facilityId.equals(target.facilityId))
				targets.add(target);
		}
		return targets;
	}

	public String[] getClassNames() {
		String[] temp = new String[selectClassTypesByFacilityIdInner.targets.size()];
		int i = 0;
		for (Targets target : selectClassTypesByFacilityIdInner.targets) {
			temp[i] = target.name;
			i += 1;
		}
		return temp;
	}

	public String[] getClassIds() {
		String[] temp = new String[selectClassTypesByFacilityIdInner.targets.size()];
		int i = 0;
		for (Targets target : selectClassTypesByFacilityIdInner.targets) {
			temp[i] = target._id;
			i += 1;
		}
		return temp;
	}

	public Targets getClassId(String classId) {
		for (Targets target : selectClassTypesByFacilityIdInner.targets) {
			if (classId.equals(target._id))
				return target;
		}
		for (Targets target : selectClassTypesByFacilityIdInner.targets) {
			L.out("target: " + target);
		}
		L.out("ERROR: classId was not found. you are going to DIE!: " + classId);
		L.out("selectClassTypesByFacilityIdInner.targets!: " + selectClassTypesByFacilityIdInner.targets);
		return null;
	}

	public String getFunctionalAreaTypeId(int i) {
		return selectClassTypesByFacilityIdInner.targets.get(i).functionalAreaTypeId;
	}
}
