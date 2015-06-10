package com.ii.mobile.flow.types;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.StringTokenizer;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.ii.mobile.flow.staticFlow.CostCodes;
import com.ii.mobile.flow.staticFlow.Equipments;
import com.ii.mobile.flow.staticFlow.Genders;
import com.ii.mobile.flow.staticFlow.Modes;
import com.ii.mobile.flow.staticFlow.PersistTypes;
import com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Fields;
import com.ii.mobile.flow.types.serialize.CustomFieldDeserializer;
import com.ii.mobile.flow.types.serialize.CustomFieldsDeserializer;
import com.ii.mobile.flow.types.serialize.TargetsDeserializer;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public class GetActionStatus extends GJon {

	public GetActionStatus() {
		super();
		// Targets targets = new Targets();
		getActionStatusInner.targets = new Targets[1];
		getActionStatusInner.targets[0] = new Targets();
	}

	@Override
	public String toString() {
		if (getActionStatusInner == null)
			return "ERROR: GetActionStatusInner is null";
		return getActionStatusInner.toString();
	}

	@SerializedName("eventRecorder")
	public EventRecorder eventRecorder = new EventRecorder();

	public Targets[] getTargets() {
		return getActionStatusInner.targets;
	}

	public Targets getTarget() {
		return getTarget(0);
	}

	public Targets getTarget(int index) {
		// L.out("getActionStatusInner.targets.size: " +
		// getActionStatusInner.targets.size());
		if (getActionStatusInner.targets.length < index + 1) {
			L.out("Error in targets size: " + getActionStatusInner.targets.length + " index: " + index);
			return null;
		}
		return getActionStatusInner.targets[index];
	}

	@SerializedName("GetActionStatusInner")
	public GetActionStatusInner getActionStatusInner = new GetActionStatusInner();

	public class GetActionStatusInner extends KeepNames {

		// @SerializedName("actionCache")
		// ActionCache actionCache = null;

		public GetActionStatusInner() {
			// L.out("created inner");
		}

		public Targets getFirstTarget() {
			int index = 0;
			// L.out("targets.size: " + targets.size());

			if (targets == null || targets.length < index + 1) {
				L.out("Error in targets size: " + targets + " index: "
						+ index);
				targets = new Targets[1];
				Targets target = new Targets();
				init();
				targets[0] = target;
				return targets[0];
			}
			// L.out("index: " + index);
			return targets[0];
		}

		public void init() {

			Targets target = getFirstTarget();
			// L.out("getActionStatusInner.targets.size: " + targets.length);
			if (target == null)
				return;

			if (target.start == null)
				target.start = new Start();
			if (target.destination == null)
				target.destination = new Destination();

			if (target.patient == null)
				target.patient = new Patient();
			if (target.actionStatus == null)
				target.actionStatus = new ActionStatus();
			if (target.requestor == null)
				target.requestor = new Requestor();
			if (target.actor == null)
				target.actor = new Actor();

			// L.out("actor: " + target.actor);
			//
			// L.out("assigned: " + target.assigned);

			if (target.assigned == null)
				target.assigned = new Assigned();

			if (target.classType == null)
				target.classType = new ClassType();

			cleanDateOfBirth(target.patient);
			for (Targets temp : targets)
				initializeCustomFields(temp);
			// printCustomFields();
		}

		private void printCustomFields() {
			L.out("customFields: ");
			for (Targets target : targets)
				if (target.customField != null)
					for (CustomField customField : target.customField) {
						L.out("customField: " + customField);
					}
		}

		private CustomField[] removeNulls(CustomField[] customFields) {
			// L.out("customFields: " + customFields);
			List<CustomField> temp = new ArrayList<CustomField>();
			for (CustomField customField : customFields)
				if (customField.name != null)
					temp.add(customField);
			CustomField[] tempField = new CustomField[temp.size()];
			int i = 0;
			for (CustomField customField : temp) {
				tempField[i] = customField;
				i += 1;
			}

			return tempField;
		}

		private void initializeCustomFields(Targets target) {
			if (target.customField == null)
				return;
			// printCustomFields();
			target.customField = removeNulls(target.customField);
			// printCustomFields();
			com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Targets classTargets = getClassFields(target.classType.classTypeId);
			if (classTargets == null) {
				L.out("ERROR classTargets: " + classTargets);
				return;
			}
			// L.out("target.customField: " + target.customField);
			// if (target.customField != null)
			// L.out("target.customField: " + target.customField.length);
			for (CustomField customField : target.customField) {
				if (customField != null) {
					Fields field = classTargets.getCustomField(customField.name);
					if (field == null) {
						L.out("ERROR: Can't find class field for: " + customField);
					} else {
						customField.control = field.control;
						customField.controlType = field.controlType;
					}
					// L.out("customField: " + customField);
				}
			}

		}

		public com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Targets getClassFields(String classId) {
			SelectClassTypesByFacilityId selectClassTypesByFacilityId = UpdateController.selectClassTypesByFacilityId;
			if (selectClassTypesByFacilityId == null) {
				return null;
			}
			com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Targets target = selectClassTypesByFacilityId.getClassId(classId);
			return target;
		}

		private final String ZULU = "T00";

		private void cleanDateOfBirth(Patient patient) {
			// L.out("target.birthDate: " + patient.birthDate);
			if (patient.birthDate != null && patient.birthDate.contains(ZULU)) {
				int index = patient.birthDate.indexOf(ZULU);
				patient.birthDate = patient.birthDate.substring(0, index).replace("-", "/");
				// L.out("after target.birthDate: " + patient.birthDate);
			}
		}

		@Override
		public String toString() {
			return "\nAction Status\n"
					+ n("status", status, 1)
					+ getFirstTarget().toString();
		}

		@SerializedName("status")
		String status;

		@SerializedName("targets")
		public Targets[] targets = null;

	}

	@Override
	public boolean validate() {
		// L.out("GetActionStatus: " + this);
		if (getActionStatusInner != null
				&& getActionStatusInner.targets != null)
			validated = true;
		else
			L.out("Unable to validate GetActionStatus");
		return validated;
	}

	public class Patient extends KeepNames {
		// @Override
		@Override
		public String toString() {
			return "  Patient" + "\n"
					+ n("medicalRecordNumber", medicalRecordNumber, 2)
					+ n("birthDate", birthDate, 2)
					+ n("genderType_id", genderType_id, 2)
					+ n("_id", _id, 2)
					+ n("firstName", firstName, 2)
					+ n("lastName", lastName, 2);

		}

		@SerializedName("medicalRecordNumber")
		public String medicalRecordNumber;

		@SerializedName("birthDate")
		public String birthDate = "";

		@SerializedName("name")
		public String name = null;
		@SerializedName("firstName")
		public String firstName = "";
		@SerializedName("lastName")
		public String lastName = "";
		@SerializedName("genderType_id")
		public String genderType_id;
		@SerializedName("_id")
		public String _id;
	}

	public class Destination extends KeepNames {
		// @Override
		@Override
		public String toString() {
			return "  Destination" + "\n"
					+ n("_id", _id, 2)
					+ n("name", name, 2);
		}

		@SerializedName("_id")
		public String _id;

		@SerializedName("name")
		public String name;
	}

	public class Start extends KeepNames {
		// @Override
		@Override
		public String toString() {
			return "  Start" + "\n"
					+ n("_id", _id, 2)
					+ n("name", name, 2);
		}

		@SerializedName("_id")
		public String _id;

		@SerializedName("name")
		public String name;
	}

	public class Requestor extends KeepNames {
		// @Override
		@Override
		public String toString() {
			return "  Requestor" + "\n"
					+ n("phone", phone, 2)
					+ n("email", email, 2)
					+ n("name", name, 2);
		}

		@SerializedName("phone")
		public String phone;

		@SerializedName("email")
		public String email;

		@SerializedName("name")
		public String name;
	}

	class Isolation extends KeepNames {
		// @Override
		@Override
		public String toString() {
			return "  Isolation" + "\n"
					+ n("isolation", isolation, 2);
		}

		@SerializedName("isolation")
		String isolation = null;
	}

	public class Targets extends KeepNames {

		public Targets() {
		}

		public String toStringShort() {
			String temp = n("functionalAreaType_id", functionalAreaId, 1)
					+ n("costCode", costCodeId, 1)
					// + n("actorResponseThreshold", actorResponseThreshold, 1)
					// + n("completeNeeded", completeNeeded, 1)
					+ n("statAction", statAction + "", 1)
					+ n("facility_id", facility_id + "", 1)
					// + actor.toString()
					// + (isolation != null ? isolation.toString() : "")
					+ n("isolation", isolation + "", 1)
					+ (patient != null ? patient.toString() : "")
					+ (start != null ? start.toString() : "")
					+ (destination != null ? destination.toString() : "")
					+ classType.toString()
					// + n("actionStatus", (actionStatus == null ? "" :
					// actionStatus.toString()), 1)
					// + n("dispatchNeeded", dispatchNeeded, 1)
					+ n("item", item, 1)
					+ n("genderTypeId", genderTypeId + " : " + Genders.INSTANCE.getDescription(genderTypeId), 1)
					+ n("modeType_id", modeType_id + " : " + Modes.INSTANCE.getDescription(modeType_id), 1)
					+ n("equipment_id", equipmentTypeId + " : "
							+ Equipments.INSTANCE.getDescription(equipmentTypeId), 1)
					// + n("deletedOn", deletedOn, 1)
					// + n("actionDispatchThreshold", actionDispatchThreshold,
					// 1)
					+ n("actionNumber", actionNumber, 1)
					// + n("persist_Id", persist_Id, 1)
					+ n("scheduleDate", scheduleDate, 1);
			// + n("actionType_id", actionTypeId + " : "
			// + StaticFlow.INSTANCE.findActionStatusName(actionTypeId), 1);
			return temp;
		}

		@Override
		public String toString() {

			String temp = n("functionalAreaType_id", functionalAreaId, 1)
					+ n("costCode", costCodeId, 1)
					+ n("actorResponseThreshold", actorResponseThreshold, 1)
					+ n("completeNeeded", completeNeeded, 1)
					+ n("statAction", statAction + "", 1)
					+ n("facility_id", facility_id + "", 1)
					+ actor.toString()
					+ n("isolation", isolation + "", 1)
					+ (patient != null ? patient.toString() : "")
					+ (start != null ? start.toString() : "")
					+ (destination != null ? destination.toString() : "")
					+ classType.toString()
					+ n("actionStatus", (actionStatus == null ? "" : actionStatus.toString()), 1)
					+ n("dispatchNeeded", dispatchNeeded, 1)
					+ n("item", item, 1)
					+ n("genderTypeId", genderTypeId + " : " + Genders.INSTANCE.getDescription(genderTypeId), 1)
					+ n("modeType_id", modeType_id + " : " + Modes.INSTANCE.getDescription(modeType_id), 1)
					+ n("equipment_id", equipmentTypeId + " : "
							+ Equipments.INSTANCE.getDescription(equipmentTypeId), 1)
					+ n("deletedOn", deletedOn, 1)
					+ n("actionDispatchThreshold", actionDispatchThreshold, 1)
					+ n("actionNumber", actionNumber, 1)
					+ n("persist_Id", persist_Id, 1)
					+ n("priority", priority, 1)
					+ requestor
					+ n("note", note, 1)
					+ n("actionType_id", actionTypeId, 1)
					+ outString(hotTranTime, "hotTranTime")
					+ outString(completeNeeded, "completeNeeded")
					+ outString(requestedDate, "requestedDate")
					+ outString(responseNeeded, "responseNeeded")
					+ outString(dispatchNeeded, "dispatchNeeded")
					+ outString(scheduleDate, "scheduleDate");
			// + assigned.toString();
			return temp;

		}

		private String outString(String value, String name) {
			return name + " " + value + "\n";
		}

		@SerializedName("assigned")
		Assigned assigned;

		@SerializedName("hotTranTime")
		String hotTranTime;

		@SerializedName("requestedDate")
		String requestedDate;

		@SerializedName("responseNeeded")
		String responseNeeded;

		@SerializedName("functionalAreaType_id")
		public String functionalAreaId;

		@SerializedName("costCode")
		public String costCodeId;

		@SerializedName("actorResponseThreshold")
		public String actorResponseThreshold;

		@SerializedName("completeNeeded")
		public String completeNeeded;

		@SerializedName("statAction ")
		public boolean statAction;

		@SerializedName("actor")
		public Actor actor = new Actor();

		@SerializedName("actionStatus")
		public ActionStatus actionStatus = new ActionStatus();

		@SerializedName("facility_id")
		public String facility_id;

		@SerializedName("patient")
		public Patient patient = new Patient();

		@SerializedName("start")
		public Start start = new Start();

		@SerializedName("requestor")
		public Requestor requestor = new Requestor();

		@SerializedName("destination")
		public Destination destination = new Destination();

		@SerializedName("modeType_id")
		public String modeType_id;

		@SerializedName("priority")
		public String priority = "1000";

		@SerializedName("equipmentType_id")
		public String equipmentTypeId;

		@SerializedName("item")
		public String item;
		//
		// @SerializedName("isolation")
		// public Isolation isolation = new Isolation();

		@SerializedName("isolation")
		public boolean isolation;

		@SerializedName("classType")
		public ClassType classType = new ClassType();

		@SerializedName("dispatchNeeded")
		public String dispatchNeeded;

		@SerializedName("deletedOn")
		public String deletedOn;

		@SerializedName("actionDispatchThreshold")
		public String actionDispatchThreshold;

		@SerializedName("actionNumber")
		public String actionNumber;

		@SerializedName("_id")
		public String actionId = null;

		@SerializedName("scheduleDate")
		public String scheduleDate;

		@SerializedName("persist")
		public String persist_Id;

		@SerializedName("actionType_id")
		public String actionTypeId;

		@SerializedName("genderType_id")
		public String genderTypeId;

		@SerializedName("note")
		String note = "";

		@SerializedName("cancelTypeId")
		public String cancelTypeId;

		@SerializedName("delayTypeId")
		public String delayTypeId;

		@SerializedName("completeTo")
		public String completeTo;

		@SerializedName("actorId")
		public String actorId;

		@SerializedName("actorName")
		public String actorName;

		@SerializedName("createdDate")
		public String createdDate;

		@SerializedName("localActionId")
		public String localActionId = null;
		@SerializedName("edited")
		public boolean edited = false;

		@SerializedName("customEdited")
		public boolean customEdited = false;

		@SerializedName("customField")
		// public List<CustomField> customField = new ArrayList<CustomField>();
		public CustomField[] customField = null;

		public String getPatientName() {
			String temp = patient.firstName + " " + patient.lastName;
			if (temp.length() < 2)
				temp = "";
			return temp;
		}
	}

	class Actor extends KeepNames {
		@Override
		public String toString() {
			return "  Actor" + "\n"
					+ n("actorStatusType_id", actorStatusType_id, 2);
		}

		@SerializedName("actorStatusType_id")
		String actorStatusType_id;
	}

	class ActionStatus extends KeepNames {

		@Override
		public String toString() {

			return "\n"
					// + patient.toString();
					+ n("responseNeeded", responseNeeded, 2)
					// + n("priority", priority, 2)
					+ n("actionAdditionalEquipment", actionAdditionalEquipment, 2)
					+ n("actionStatusType_id", actionStatusType_id, 2)
					+ n("actionCompleteThreshold", actionCompleteThreshold, 2)
					+ n("note", note, 2)
					+ n("applicationType_id", applicationType_id, 2)
					+ statusTimes.toString();

		}

		@SerializedName("statusTimes")
		StatusTimes statusTimes = new StatusTimes();

		@SerializedName("requestor")
		Requestor requestor = new Requestor();

		// @SerializedName("patient")
		// Patient patient = new Patient();

		@SerializedName("responseNeeded")
		String responseNeeded;

		// @SerializedName("priority")
		// String priority;

		@SerializedName("actionAdditionalEquipment")
		String actionAdditionalEquipment;

		@SerializedName("actionStatusType_id")
		String actionStatusType_id;

		@SerializedName("actionCompleteThreshold")
		String actionCompleteThreshold;

		@SerializedName("note")
		String note;

		@SerializedName("applicationType_id")
		String applicationType_id;
		@SerializedName("selfTask")
		boolean selfTask;
	}

	class StatusTimes extends KeepNames {
		@Override
		public String toString() {
			return "  Status Times" + "\n"
					+ n("totalActiveTime", totalActiveTime, 2)
					+ n("rawComplete", rawComplete, 2)
					+ n("receiptToComplete", receiptToComplete, 2)
					+ n("activeCnt", activeCnt, 2)
					+ n("dispatchToComplete", dispatchToComplete, 2)
					+ n("totalAssignedTime", totalAssignedTime, 2)
					+ n("rawComplete", rawComplete, 2)
					+ n("totalUnassignedTime", totalUnassignedTime, 2)
					+ n("totalDelayedTime", totalDelayedTime, 2)
					+ n("receiptToArrival", receiptToArrival, 2)
					+ n("delayCnt", delayCnt, 2);
		}

		@SerializedName("totalActiveTime")
		String totalActiveTime;

		@SerializedName("rawComplete")
		String rawComplete;

		@SerializedName("receiptToComplete")
		String receiptToComplete;

		@SerializedName("activeCnt")
		String activeCnt;

		@SerializedName("dispatchToComplete")
		String dispatchToComplete;

		@SerializedName("totalAssignedTime")
		String totalAssignedTime;

		@SerializedName("totalUnassignedTime")
		String totalUnassignedTime;

		@SerializedName("totalDelayedTime")
		String totalDelayedTime;

		@SerializedName("receiptToArrival")
		String receiptToArrival;

		@SerializedName("delayCnt")
		String delayCnt;
	}

	public class ClassType extends KeepNames {
		@Override
		public String toString() {
			return "  Class Type" + "\n"
					+ n("classTypeId", classTypeId, 2)
					+ n("name", name, 2);
		}

		@SerializedName("name")
		public String name;

		@SerializedName("_id")
		public String classTypeId;

	}

	static public GetActionStatus getGJon(String json) {
		// L.out("GetActionStatus: " + "");
		// PrettyPrint.prettyPrint(json, false);
		// GetActionStatus getActionStatus = (GetActionStatus)
		// getJSonObject(json, GetActionStatus.class);
		if (json == null)
			return null;
		json = json.replace("\"isolation\":{},", "");

		GetActionStatus getActionStatus = (GetActionStatus) getJSonObjectArray(json, GetActionStatus.class);
		// getActionStatus.json = null;
		if (getActionStatus == null)
			return null;
		getActionStatus.getActionStatusInner.init();

		getActionStatus.getNewJson();
		return getActionStatus;
	}

	static protected GJon getJSonObjectArray(String json, Class<?> className) {
		// L.out("\n\n*** " + className + " ***\n");
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
		// gsonBuilder.registerTypeHierarchyAdapter(Targets[].class, new
		// TargetsDeserializer());
		gsonBuilder.registerTypeAdapter(Targets[].class, new
				TargetsDeserializer());

		gsonBuilder.registerTypeAdapter(CustomField[].class, new
				CustomFieldsDeserializer());
		gsonBuilder.registerTypeAdapter(CustomField.class, new
				CustomFieldDeserializer());

		Gson gson = gsonBuilder.create();
		JsonParser parser = new JsonParser();
		GJon gJon = null;
		try {
			gJon = (GJon) gson.fromJson(parser.parse(json).getAsJsonObject().toString(), className);
		} catch (Exception e) {

			L.out("*** ERROR Failed: " + e + "\njson: " + json + " " + className);
		}
		if (gJon == null) {
			L.out("Failed to parse json for: " + className);
			return null;
		}
		gJon.json = json;
		if (gJon.validate()) {
			// need to uncomment to print to console
			// L.out(gJon.toString());
		}
		return gJon;
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

	public String getPatientName() {
		return getTarget().getPatientName();
	}

	public void setPatientName(String name) {
		getTarget().patient.name = name;
	}

	public String getPatientBirthDate() {
		return getTarget().patient.birthDate;
	}

	public void setPatientBirthDate(String birthDate) {
		getTarget().patient.birthDate = birthDate;
	}

	public String getPatientMRN() {
		return getTarget().patient.medicalRecordNumber;
	}

	public void setPatientMRN(String medicalRecordNumber) {
		getTarget().patient.medicalRecordNumber = medicalRecordNumber;
	}

	public boolean getIsolationPatient() {
		// return getTarget().isolation.isolation;
		return getTarget().isolation;
	}

	public String getStat() {
		// return getTarget().isolation.isolation;
		if (getTarget().statAction)
			return "yes";
		return "no";
	}

	public boolean getStatAction() {
		// return getTarget().isolation.isolation;
		return getTarget().statAction;
	}

	public String getClassName() {
		if (getTarget() == null || getTarget().classType == null)
			return null;
		return getTarget().classType.name;
	}

	public String getStartName() {
		return getTarget().start.name;
	}

	public String getDestinationName() {
		return getTarget().destination.name;
	}

	public String getStartId() {
		return getTarget().start._id;
	}

	public String getDestinationId() {
		return getTarget().destination._id;
	}

	public void setStartId(String id) {
		getTarget().start._id = id;
	}

	public void setDestinationId(String id) {
		getTarget().destination._id = id;
	}

	public void setStartName(String name) {
		getTarget().start.name = name;
	}

	public void setDestinationName(String name) {
		getTarget().destination.name = name;
	}

	public String getMode() {
		return Modes.INSTANCE.getDescription(getTarget().modeType_id);
	}

	public String getModeId() {
		return getTarget().modeType_id;
	}

	public void setModeId(String modeId) {
		getTarget().modeType_id = modeId;
	}

	// public String getEquipmentName() {
	// return
	// Equipments.INSTANCE.getDescription(targets.equipmentId);
	// }

	public String getEquipmentId() {
		return getTarget().equipmentTypeId;
	}

	public String getNotes() {
		return getTarget().note;
	}

	public String getFacilityId() {
		return getTarget().facility_id;
	}

	public String getFunctionalAreaTypeId() {
		return getTarget().functionalAreaId;
	}

	public void setFacilityId(String facilityId) {
		getTarget().facility_id = facilityId;
	}

	public void setFunctionalAreaTypeId(String functionalAreaId) {
		getTarget().functionalAreaId = functionalAreaId;
	}

	public String getPriority() {

		return getTarget().priority;
	}

	public void setClassTypeId(String classTypeId, String name) {
		L.out("classTypeId: " + classTypeId + " name: " + name);
		getTarget().classType.classTypeId = classTypeId;
		getTarget().classType.name = name;
	}

	public String getClassTypeId() {
		return getTarget().classType.classTypeId;
	}

	public void actionTypeId(String actionTypeId) {
		getTarget().actionTypeId = actionTypeId;
	}

	public String getActionTypeId() {
		if (getTarget().actionTypeId == null || getTarget().actionTypeId.equals(""))
			return ROUTINE;
		return getTarget().actionTypeId;
	}

	public void setNotes(String note) {
		getTarget().actionStatus.note = note;
	}

	public void setEquipmentId(String equipmentId) {
		getTarget().equipmentTypeId = equipmentId;
	}

	public void setPersist(String persist) {
		getTarget().persist_Id = persist;
	}

	public String getPersist() {
		return getTarget().persist_Id;
	}

	public void setItem(String item) {
		L.out("setItem: " + item);
		getTarget().item = item;
	}

	public String getItem() {
		return getTarget().item;
	}

	public void setCostCodeId(String costCodeId) {
		getTarget().costCodeId = costCodeId;
	}

	public String getCostCodeId() {
		return getTarget().costCodeId;
	}

	private String getScheduleDate() {
		return getTarget().scheduleDate;
	}

	public void setScheduleDate(String scheduleDate) {
		getTarget().scheduleDate = scheduleDate;
	}

	public String getMedicalRecordNumber() {
		return getTarget().patient.medicalRecordNumber;
	}

	public String getGenderTypeId() {
		L.out("genderId: " + getTarget().patient.genderType_id);
		return getTarget().patient.genderType_id;
	}

	public String getBirthDate() {
		return getTarget().patient.birthDate;
	}

	public String getPatientFirstName() {
		return getTarget().patient.firstName;
	}

	public String getPatientLastName() {
		return getTarget().patient.lastName;
	}

	public String getPatientId() {
		return getTarget().patient._id;
	}

	public String getActionNumber() {
		return getTarget().actionNumber;
	}

	public void setActionNumber(String actionNumber) {
		getTarget().actionNumber = actionNumber;
	}

	public String getNamedValue(String control, boolean custom) {
		// L.out("getCustomNamedValue: " + control);
		if (getTarget().customField == null)
			return "";
		for (CustomField customField : getTarget().customField) {
			L.out("customField: " + customField.control);
			if (customField.control != null && customField.control.equalsIgnoreCase(control))
				return customField.value;
		}
		L.out("getNamedValue not defined return empty string: " +
				control);
		return "";
	}

	public String getNamedValue(String name) {
		// L.out("getNamedValue: " + name);
		if (name.equals("Note")) {
			// L.out("getNamedValue: " + name + " " + getTarget().note);
			return getTarget().note;
		}
		if (name.equals("C-Code"))
			return CostCodes.INSTANCE.getName(getCostCodeId());

		if (name.equals("Schedule Date"))
			return getScheduleDate();

		if (name.equals("txtPatientName"))
			return getTarget().getPatientName();

		if (name.equals("modeType_id")) {
			// L.out("modeType_id: " +
			// Modes.INSTANCE.getName(getTarget().modeType_id));
			return Modes.INSTANCE.getDescription(getTarget().modeType_id);

		}
		if (name.equals("equipmentType_id")) {
			// L.out("equipmentType_id: " +
			// Equipments.INSTANCE.getName(getTarget().equipmentId));
			return Equipments.INSTANCE.getDescription(getTarget().equipmentTypeId);
		}

		if (name.equals("isolation")) {
			// return getTarget().isolation.isolation;
			Boolean isolation = getTarget().isolation;
			if (isolation)
				return "Yes";
			return "No";
		}

		if (name.equals("txtItem"))
			return getTarget().item;

		if (name.equals("txtMedicalRecordNumber"))
			return getTarget().patient.medicalRecordNumber;

		if (name.equals("destination"))
			return getTarget().destination.name;

		if (name.equals("start"))
			return getTarget().start.name;

		if (name.equals("Priority") || name.equals("" +
				"txtPriority"))
			return getTarget().priority;

		if (name.equals("Gender") || name.equals("patient.genderType_id")) {
			String gender = Genders.INSTANCE.getDescription(getTarget().patient.genderType_id);
			L.out("gender: " + gender);
			return gender;
		}

		if (name.equals("Persist"))
			return PersistTypes.INSTANCE.getName(getTarget().persist_Id);

		if (name.equals("Requestor Email"))
			return getTarget().requestor.email;

		if (name.equals("Requestor Phone"))
			return getTarget().requestor.phone;
		if (name.equals("Requestor Name"))
			return getTarget().requestor.name;

		if (name.equals("Persist"))
			return PersistTypes.INSTANCE.getName(getTarget().persist_Id);

		if (name.equals("Stat")) {
			if (getTarget().statAction)
				return "True";
			return "False";
		}
		if (name.equals("rdActionType") || name.equals("actionType_id")) {
			// L.out("actionTypeId: " + getTarget().actionTypeId);
			return getTarget().actionTypeId;
		}

		if (name.equals("txtPatientDOB"))
			return getTarget().patient.birthDate;

		L.out("getNamedValue didn't find: " + name);
		return "ERROR: " + name;
	}

	public void setNamedValue(String control, String controlType, String value, String name, boolean custom) {
		L.out("setCustomNamedValue name: #" + control + "#" + value);
		if (getTarget().customField == null)
			// getTarget().customField = new ArrayList<CustomField>();
			getTarget().customField = new CustomField[0];
		for (CustomField customField : getTarget().customField) {
			// L.out("customField: " + customField.control);
			if (customField.control.equals(control)) {
				customField.value = value;
				// L.out("setCustomNamedValue set: " + control + " to " +
				// value);
				return;
			}
		}
		CustomField customField = new CustomField();
		customField.control = control;
		customField.controlType = controlType;
		customField.name = name;
		customField.value = value;

		CustomField[] temp = new CustomField[getTarget().customField.length + 1];
		for (int i = 0; i < getTarget().customField.length; i++)
			temp[i] = getTarget().customField[i];
		temp[getTarget().customField.length] = customField;
		getTarget().customField = temp;
		// getTarget().customField.add(customField);
		// L.out("setCustomNamedValue created: " + customField);
		getActionStatusInner.printCustomFields();
		return;
	}

	public static final String ROUTINE = "7069521ceeaf561b2081aeda";
	public static final String STAT = "7069521ceeaf561b2081aedb";

	public void setNamedValue(String header, String value) {
		// L.out("header: #" + header + "#" + value);
		if (header.equals("txtPatientName")) {
			// L.out("header: #" + header + "#" + value);
			PatientNameKludge kludge = new PatientNameKludge(value);
			getTarget().patient.firstName = kludge.firstName;
			getTarget().patient.lastName = kludge.lastName;
		}
		else if (header.equals("Note")) {
			// L.out("note: #" + header + "#" + value);
			getTarget().note = value;
		}
		else if (header.equals("txtMedicalRecordNumber"))
			getTarget().patient.medicalRecordNumber = value;
		else if (header.equals("selectGender")) {
			String temp = Genders.INSTANCE.getGenderId(value);
			getTarget().patient.genderType_id = temp;
			// L.out("getTarget().patient.genderType_id: " + temp);
		}
		else if (header.equals("selectStart")) {
			SelectLocations selectLocations = UpdateController.selectLocations;
			getTarget().start._id = selectLocations.getRoomId(value);
			getTarget().start.name = value;
			// L.out("getTarget().start._id: #" + getTarget().start._id + "#" +
			// getTarget().start.name);
		}
		else if (header.equals("selectDestination")) {
			SelectLocations selectLocations = UpdateController.selectLocations;
			getTarget().destination._id = selectLocations.getRoomId(value);
			getTarget().destination.name = value;
			// L.out("getTarget().destination._id: #" +
			// getTarget().destination._id + "#"
			// + getTarget().destination.name);
		}
		else if (header.equals("Priority") || header.equals("txtPriority"))
			getTarget().priority = value;
		else if (header.equals("chkIsolationPatient")) {
			L.out("chkIsolationPatient: " + value);
			if (value != null && value.equals("Yes"))
				getTarget().isolation = true;
			else
				getTarget().isolation = false;
		}
		else if (header.equals("selectModeType")) {
			getTarget().modeType_id = Modes.INSTANCE.getId(value);
			// L.out("selectModeType: " + getTarget().modeType_id);
		}
		else if (header.equals("Requestor Phone"))
			getTarget().requestor.phone = value;
		else if (header.equals("Requestor Email"))
			getTarget().requestor.email = value;
		else if (header.equals("Requestor Name"))
			getTarget().requestor.name = value;
		else if (header.equals("selectEquipmentType")) {
			// L.out("equipmentType_id: " +
			// Equipments.INSTANCE.getName(getTarget().equipmentId));
			getTarget().equipmentTypeId = Equipments.INSTANCE.getId(value);
		}
		else if (header.equals("rdActionType") || header.equals("actionType_id")) {
			// L.out("rdActionType: #" + header + "# " + value);
			if (value != null && value.equals("Yes"))
				getTarget().actionTypeId = STAT;
			else
				getTarget().actionTypeId = ROUTINE;
			// L.out("rdActionType: " + getTarget().actionTypeId);
		}
		else if (header.equals("txtPatientDOB")) {
			getTarget().patient.birthDate = value;
			L.out("txtPatientDOB: " + getTarget().patient.birthDate);
		}
		else
			L.out("*** ERROR Unable to find header to set value: $" + header + "# " + value);
	}

	public void setSideEffect(String header, String value) {
		L.out("*** ERROR Unable to find header to setSideEffect value: " + header + " " + value);
	}

	public void replaceTarget(Targets item) {
		getActionStatusInner.targets[0] = item;
	}

	private class PatientNameKludge {
		String lastName = "";
		String firstName = "";
		String fullName = "";

		public PatientNameKludge(String fullName) {
			if (fullName == null)
				return;
			this.fullName = fullName;
			String first = "";
			StringTokenizer tokenizer = new StringTokenizer(fullName);
			while (tokenizer.hasMoreTokens()) {
				String token = tokenizer.nextToken();
				if (tokenizer.hasMoreTokens()) {
					firstName += first + token;
					first = " ";
				} else
					lastName = token;
			}
			// MyToast.show(toString());
		}

		@Override
		public String toString() {
			return "fullName: " + fullName
					+ "\nfirstName: " + firstName
					+ "\nlastName: " + lastName;
		}
	}

	public class ActionCache extends KeepNames {
		// @Override
		@Override
		public String toString() {
			return "  ActionCache:" + "\n"
					+ n("actionId", actionId, 2)
					+ n("actionStatusId", actionStatusId, 2);
		}

		@SerializedName("actionId")
		public String actionId;

		@SerializedName("actionStatusId")
		public String actionStatusId;

	}

	public void setActionStatusId(String actionStatusId, String optionId, boolean clear) {
		if (clear)
			eventRecorder.events = new Event[0];

		setActionStatusId(actionStatusId, optionId);
	}

	public void setActionStatusId(String actionStatusId, String optionId) {
		setActionStatusId(actionStatusId);
		eventRecorder.addEvent(actionStatusId, optionId, this);
	}

	public void setCompleteToId(String completeTo) {
		getTarget().completeTo = completeTo;
	}

	public String getCompleteToId() {
		return getTarget().completeTo;
	}

	public String getActionStatusId() {
		return getTarget().actor.actorStatusType_id;
	}

	public void setActionStatusId(String actionStatusId) {
		getTarget().actor.actorStatusType_id = actionStatusId;
	}

	public void setActionId(String actionId) {
		getTarget().actionId = actionId;
	}

	public String getActionId() {
		return getTarget().actionId;

	}

	public void setActorId(String actorId) {
		// L.out("setting actorId: " + actorId);
		getTarget().actorId = actorId;
	}

	public String getActorId() {
		return getTarget().actorId;
	}

	public void setActorName(String actorName) {
		// L.out("setting actorName: " + actorName);
		getTarget().actorName = actorName;

	}

	public String getActorName() {
		return getTarget().actorName;
	}

	public String now() {
		GregorianCalendar now = new GregorianCalendar();
		return toDate(now);
	}

	public static String toDate(GregorianCalendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
		// sdf.setTimeZone(calendar.getTimeZone());
		System.out.println("L.toDate: " + sdf.format(calendar.getTime()));
		return sdf.format(calendar.getTime());
		// return sdf.format(new Date(calendar.getTimeInMillis())) + " " +
		// calendar.getTimeZone().getID();
	}

	public void setCreatedDate() {
		getTarget().createdDate = now();
		// L.out("created date: " + getTarget().createdDate);
	}

	public String getCreatedDate() {
		return getTarget().createdDate;
	}

	public void setDateEdited(boolean b, boolean custom) {
		// L.out("setDateEdited: customEdited " + b);
		getTarget().customEdited = b;
	}

	public void setDateEdited(boolean b) {
		// L.out("setDateEdited: " + b);
		getTarget().edited = b;
	}

	public boolean getDateEdited(boolean custom) {
		// L.out("getDateEdited customEdited: " + getTarget().edited);
		return getTarget().customEdited;
	}

	public boolean getDateEdited() {
		// L.out("getDateEdited: " + getTarget().edited);
		return getTarget().edited;
	}

	public String getLocalActionId() {
		return getTarget().localActionId;
	}

	public void setLocalActionId(String localActionId) {
		// L.out("localActionId: " + localActionId + " actionNumber: " +
		// getActionNumber());
		getTarget().localActionId = localActionId;
	}

	public void clearCustomDate() {
		if (getTarget().customField == null)
			return;
		for (CustomField customField : getTarget().customField) {
			// L.out("customField: " + customField);
			if (customField.controlType != null
					&& customField.controlType.equalsIgnoreCase("datePickerControl"))
				customField.value = "";
		}
	}

	public class Assigned extends KeepNames {

		@Override
		public String toString() {
			return "Assigned" + "\n"
					+ n("at", at, 2)
					+ n("by", by, 2);
		}

		@SerializedName("at")
		public String at;

		@SerializedName("by")
		public String by;
	}
}
