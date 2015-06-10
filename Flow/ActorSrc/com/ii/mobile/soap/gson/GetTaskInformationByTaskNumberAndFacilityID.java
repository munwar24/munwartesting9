package com.ii.mobile.soap.gson;

//import android.os.Bundle;

import android.os.Bundle;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.ii.mobile.util.L;

public class GetTaskInformationByTaskNumberAndFacilityID extends GJon {

	public TaskInformations taskInformations;

	public GetTaskInformationByTaskNumberAndFacilityID() {
		taskInformations = new TaskInformations();
		taskInformations.mobileGetTaskInformations = new MobileGetTaskInformations();
		validate();
	}

	static class TaskInformations {

		String mobileUserName = "none";

		MobileGetTaskInformations mobileGetTaskInformations;
		String delayType = null;
		@SerializedName("employeeID")
		String employeeID = null;
		@SerializedName("facilityID")
		String facilityID = null;
		boolean tickled = false;

		public String completeTo = null;

		@Override
		public String toString() {
			return " delayType: " + delayType
					+ "\n employeeID: " + employeeID
					+ "\n facilityID: " + facilityID
					+ "\n" + mobileGetTaskInformations.toString();
		}
	}

	static class MobileGetTaskInformations {
		// @SerializedName("@attributes")
		// Attributes attributes;
		String fontColor;
		String tskFrequencyType;
		String requestDate;
		String requestorEmail;
		@SerializedName("hirStartLocationNode")
		String hirStartLocationNode;
		String cancelDate;
		String taskDelayType;
		@SerializedName("tskTaskClass")
		String tskTaskClass;
		String frequencyBrief;
		String costBrief;
		String item;
		@SerializedName("tskEquipmentType")
		String tskEquipmentType;
		@SerializedName("steCostCode")
		String steCostCode;
		@SerializedName("PatientDOB")
		String patientDOB = "NA";
		@SerializedName("PatientName")
		String patientName;
		String destBrief;
		@SerializedName("hirDestLocationNode")
		String hirDestLocationNode;
		String activeDate;
		String requestorPhone;
		String requestorName;
		String modeBrief;
		String taskStatusBrief;
		String assignedDate;
		@SerializedName("CustomField1")
		String customField1;
		@SerializedName("CustomField2")
		String customField2;
		@SerializedName("CustomField3")
		String customField3;
		@SerializedName("CustomField4")
		String customField4;
		@SerializedName("CustomField5")
		String customField5;
		String taskBrief;
		@SerializedName("IsolationPatent")
		String isolationPatent = "No";
		@SerializedName("ClassBrief")
		String classBrief;
		@SerializedName("TaskNumber")
		String taskNumber;
		@SerializedName("EquipmentBrief")
		String equipmentBrief;
		String priority;
		@SerializedName("tskStatusType")
		String tskStatusType;
		String notes;
		String areaBrief;
		String startBrief;
		String scheduleDate;
		@SerializedName("tskArea")
		String tskArea;
		@SerializedName("tskModeType")
		String tskModeType;
		String closeDate;
		String delayDate;
		// added for Transport Flow
		String mrn = "NA";
		String isolation;

		@Override
		public String toString() {
			return "  patientName: " + patientName

					+ "\n patientDOB: " + patientDOB
					+ "\n requestDate: " + requestDate
					+ "\n notes: " + notes
					+ "\n requestDate: " + requestDate
					+ "\n requestorEmail: " + requestorEmail
					+ "\n hirStartLocationNode: " + hirStartLocationNode
					+ "\n cancelDate: " + cancelDate
					+ "\n tskTaskClass: " + tskTaskClass
					+ "\n item: " + item
					+ "\n tskEquipmentType: " + tskEquipmentType
					+ "\n destBrief: " + destBrief
					+ "\n hirDestLocationNode: " + hirDestLocationNode
					+ "\n activeDate: " + activeDate
					+ "\n requestorPhone: " + requestorPhone
					+ "\n modeBrief: " + modeBrief
					+ "\n taskStatusBrief: " + taskStatusBrief
					+ "\n assignedDate: " + assignedDate
					+ "\n requestorName: " + requestorName
					+ "\n isolationPatent: " + isolationPatent
					+ "\n classBrief: " + classBrief
					+ "\n taskNumber: " + taskNumber
					+ "\n equipmentBrief: " + equipmentBrief
					+ "\n priority: " + priority
					+ "\n tskStatusType: " + tskStatusType
					+ "\n notes: " + notes
					+ "\n areaBrief: " + areaBrief
					+ "\n tskArea: " + tskArea
					+ "\n tskModeType: " + tskModeType
					+ "\n closeDate: " + closeDate
					+ "\n delayDate: " + delayDate;
		}

	}

	@Override
	public boolean validate() {
		if (taskInformations != null
				&& taskInformations.mobileGetTaskInformations != null)
			validated = true;
		else {
			L.out("Unable to validate GetTaskInformationByTaskNumberAndFacilityID:");

			printJson();
		}
		return validated;
	}

	static public GetTaskInformationByTaskNumberAndFacilityID getGJon(String json) {
		// L.out("json: " + json);
		try {
			return (GetTaskInformationByTaskNumberAndFacilityID) getJSonObject(json, GetTaskInformationByTaskNumberAndFacilityID.class);
		} catch (Exception e) {
			L.out("*** ERROR (maybe): " + e);
			return null;
		}
	}

	@Override
	public String toString() {
		if (isValidated()) {
			return taskInformations.toString();
		}
		return null;
	}

	public String getTaskNumber() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.taskNumber;
		return null;
	}

	public String getEmployeeID() {
		return taskInformations.employeeID;
	}

	public String getFacilityID() {
		return taskInformations.facilityID;
	}

	public String getFontColor() {
		return taskInformations.mobileGetTaskInformations.fontColor;
	}

	public String getRequestDate() {
		return taskInformations.mobileGetTaskInformations.requestDate;
	}

	public String getRequesterEmail() {
		return taskInformations.mobileGetTaskInformations.requestorEmail;
	}

	public String getHirStartLocationNode() {
		return taskInformations.mobileGetTaskInformations.hirStartLocationNode;
	}

	public String getCancelDate() {
		return taskInformations.mobileGetTaskInformations.cancelDate;
	}

	public String getTskTaskClass() {
		return taskInformations.mobileGetTaskInformations.tskTaskClass;
	}

	public String getTskEquipmentClass() {
		L.out("error? returning: " + " instead of: "
				+ taskInformations.mobileGetTaskInformations.equipmentBrief);
		return taskInformations.mobileGetTaskInformations.tskEquipmentType;
	}

	public String getHirDestLocationNode() {
		return taskInformations.mobileGetTaskInformations.hirDestLocationNode;
	}

	public String getActiveDate() {
		return taskInformations.mobileGetTaskInformations.activeDate;
	}

	public String getRequesterPhone() {
		return taskInformations.mobileGetTaskInformations.requestorPhone;
	}

	public String getModeBrief() {
		return taskInformations.mobileGetTaskInformations.modeBrief;
	}

	public String getAssignedDate() {
		return taskInformations.mobileGetTaskInformations.assignedDate;
	}

	public String getRequestorName() {
		return taskInformations.mobileGetTaskInformations.requestorName;
	}

	public String getIsolationPatient() {
		return taskInformations.mobileGetTaskInformations.isolationPatent;
	}

	public String getClassBrief() {
		return taskInformations.mobileGetTaskInformations.classBrief;
	}

	public String getEquipmentBrief() {
		return taskInformations.mobileGetTaskInformations.equipmentBrief;
	}

	public String getPriority() {
		return taskInformations.mobileGetTaskInformations.priority;
	}

	public String getTskStatusType() {
		return taskInformations.mobileGetTaskInformations.tskStatusType;
	}

	public String getNotes() {
		return taskInformations.mobileGetTaskInformations.notes;
	}

	public String getAreaBrief() {
		return taskInformations.mobileGetTaskInformations.areaBrief;
	}

	public String getTskArea() {
		return taskInformations.mobileGetTaskInformations.tskArea;
	}

	public String getTskModeType() {
		return taskInformations.mobileGetTaskInformations.tskModeType;
	}

	public String getCloseDate() {
		return taskInformations.mobileGetTaskInformations.closeDate;
	}

	public String getDelayDate() {
		return taskInformations.mobileGetTaskInformations.delayDate;
	}

	public String getTaskStatusBrief() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.taskStatusBrief;
		return null;
	}

	public void setTaskStatusBrief(String status) {
		if (validated)
			taskInformations.mobileGetTaskInformations.taskStatusBrief = status;
	}

	public String getItem() {
		return taskInformations.mobileGetTaskInformations.item;
	}

	public void setHirStartLocationNode(String hirStartLocationNode) {
		if (validated)
			taskInformations.mobileGetTaskInformations.hirStartLocationNode = hirStartLocationNode;
	}

	public void setHirDestLocationNode(String hirDestLocationNode) {
		if (validated)
			taskInformations.mobileGetTaskInformations.hirDestLocationNode = hirDestLocationNode;
	}

	public void setTaskNumber(String taskNumber) {
		if (validated)
			taskInformations.mobileGetTaskInformations.taskNumber = taskNumber;
	}

	@Override
	public String getNewJson() {
		Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
		JsonElement je = gson.toJsonTree(taskInformations);
		JsonObject jo = new JsonObject();
		jo.add("TaskInformations", je);
		json = jo.toString();
		json = json.replace("mobileGetTaskInformations", "MobileGetTaskInformations");
		// json = gson.toJson(taskInformations);
		return json;
	}

	// What a perfect place for reflection!
	public void setNamedValue(String header, String value) {
		// L.out("header: #" + header + "#");
		if (header.equals("Isolation Patient"))
			taskInformations.mobileGetTaskInformations.isolationPatent = value;
		else if (header.equals("Destination"))
			taskInformations.mobileGetTaskInformations.hirDestLocationNode = value;
		else if (header.equals("Start"))
			taskInformations.mobileGetTaskInformations.hirStartLocationNode = value;
		else if (header.equals("Mode"))
			taskInformations.mobileGetTaskInformations.modeBrief = value;
		else if (header.equals("Equipment"))
			taskInformations.mobileGetTaskInformations.equipmentBrief = value;
		else if (header.equals("Item"))
			taskInformations.mobileGetTaskInformations.item = value;
		else if (header.equals("Schedule Date"))
			taskInformations.mobileGetTaskInformations.assignedDate = value;
		else if (header.equals("Class"))
			taskInformations.mobileGetTaskInformations.classBrief = value;
		else if (header.equals("Patient Name"))
			taskInformations.mobileGetTaskInformations.patientName = value;
		else if (header.equals("Patient DOB"))
			taskInformations.mobileGetTaskInformations.patientDOB = value;
		// if (header.equals("Persist"))
		// return taskInformations.mobileGetTaskInformations.persist;
		else if (header.equals("C-Code"))
			taskInformations.mobileGetTaskInformations.steCostCode = value;
		else if (header.equals("CustomField1"))
			taskInformations.mobileGetTaskInformations.customField1 = value;
		else if (header.equals("CustomField2"))
			taskInformations.mobileGetTaskInformations.customField2 = value;
		else if (header.equals("CustomField3"))
			taskInformations.mobileGetTaskInformations.customField3 = value;
		else if (header.equals("CustomField4"))
			taskInformations.mobileGetTaskInformations.customField4 = value;
		else if (header.equals("CustomField5"))
			taskInformations.mobileGetTaskInformations.customField5 = value;
		else if (header.equals("Task Number"))
			taskInformations.mobileGetTaskInformations.taskNumber = value;
		else if (header.equals("Notes"))
			taskInformations.mobileGetTaskInformations.notes = value;
		else
			L.out("*** ERROR Unable to find header to set value: " + header + " " + value);
	}

	// What a perfect place for reflection!
	public String getNamedValue(String header) {
		// L.out("header: #" + header + "#");
		if (header.equals("Isolation Patient"))
			return taskInformations.mobileGetTaskInformations.isolationPatent;
		if (header.equals("Destination"))
			return taskInformations.mobileGetTaskInformations.hirDestLocationNode;
		if (header.equals("Start"))
			return taskInformations.mobileGetTaskInformations.hirStartLocationNode;
		if (header.equals("Mode"))
			return taskInformations.mobileGetTaskInformations.modeBrief;
		if (header.equals("Equipment"))
			return taskInformations.mobileGetTaskInformations.equipmentBrief;
		if (header.equals("Item"))
			return taskInformations.mobileGetTaskInformations.item;
		if (header.equals("Schedule Date"))
			return taskInformations.mobileGetTaskInformations.assignedDate;
		if (header.equals("Class"))
			return taskInformations.mobileGetTaskInformations.classBrief;
		if (header.equals("Patient Name"))
			return taskInformations.mobileGetTaskInformations.patientName;
		if (header.equals("Patient DOB"))
			return taskInformations.mobileGetTaskInformations.patientDOB;
		// if (header.equals("Persist"))
		// return taskInformations.mobileGetTaskInformations.persist;
		if (header.equals("C-Code"))
			return taskInformations.mobileGetTaskInformations.steCostCode;
		if (header.equals("CustomField1"))
			return taskInformations.mobileGetTaskInformations.customField1;
		if (header.equals("CustomField2"))
			return taskInformations.mobileGetTaskInformations.customField2;
		if (header.equals("CustomField3"))
			return taskInformations.mobileGetTaskInformations.customField3;
		if (header.equals("CustomField4"))
			return taskInformations.mobileGetTaskInformations.customField4;
		if (header.equals("CustomField5"))
			return taskInformations.mobileGetTaskInformations.customField5;
		if (header.equals("Task Number"))
			return taskInformations.mobileGetTaskInformations.taskNumber;
		if (header.equals("Notes"))
			return taskInformations.mobileGetTaskInformations.notes;
		return header;
	}

	// What a perfect place for reflection!
	public void setSideEffect(String header, String value) {
		// L.out("setSideEffect header: #" + header + "# value: " + value);
		if (header.equals("Mode"))
			taskInformations.mobileGetTaskInformations.tskModeType = value;
		else if (header.equals("Equipment"))
			taskInformations.mobileGetTaskInformations.tskEquipmentType = value;
		else if (header.equals("Persist"))
			L.out("Persist not implemented");
		else if (header.equals("C-Code"))
			taskInformations.mobileGetTaskInformations.steCostCode = value;
		else
			L.out("*** ERROR Unable to find header to setSideEffect value: " + header + " " + value);
	}

	public String getDelayType() {
		return taskInformations.delayType;
	}

	public void setDelayType(String delayType) {
		taskInformations.delayType = delayType;
	}

	public void setTickled(boolean b) {
		taskInformations.tickled = b;
	}

	public boolean getTickled() {
		return taskInformations.tickled;
	}

	public String getTaskEquipmentType() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.tskEquipmentType;
		return null;
	}

	public String getTskFrequencyType() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.tskFrequencyType;
		return null;
	}

	public String getPatientName() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.patientName;
		return null;
	}

	public String getPatientDOB() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.patientDOB;
		return null;
	}

	public String getSteCostCode() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.steCostCode;
		return null;
	}

	public String getScheduleDate() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.scheduleDate;
		return null;
	}

	public String getCcnRequest() {
		if (validated)
			return "";

		return null;
	}

	public void setCompleteTo(String completeTo) {
		taskInformations.completeTo = completeTo;
	}

	public String getCompleteTo() {
		if (validated)
			return taskInformations.completeTo;
		return null;
	}

	public void setEmployeeID(String employeeID) {
		taskInformations.employeeID = employeeID;
	}

	public void setFacilityID(String facilityID) {
		taskInformations.facilityID = facilityID;
	}

	public void setTskTaskClass(String tskTaskClass) {
		taskInformations.mobileGetTaskInformations.tskTaskClass = tskTaskClass;
	}

	public void setRequestorName(String requestorName) {
		taskInformations.mobileGetTaskInformations.requestorName = requestorName;
	}

	public void setRequestorPhone(String requestorPhone) {
		taskInformations.mobileGetTaskInformations.requestorPhone = requestorPhone;
	}

	public String getRequestorEmail() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.requestorEmail;
		return null;
	}

	public String getRequestorPhone() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.requestorPhone;
		return null;
	}

	public String getCustomField1() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.customField1;
		return null;
	}

	public String getCustomField2() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.customField2;
		return null;
	}

	public String getCustomField3() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.customField3;
		return null;
	}

	public String getCustomField4() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.customField4;
		return null;
	}

	public String getCustomField5() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.customField5;
		return null;
	}

	public void setClassBrief(String classBrief) {
		taskInformations.mobileGetTaskInformations.classBrief = classBrief;
	}

	public void setPatientName(String patientName) {
		taskInformations.mobileGetTaskInformations.patientName = patientName;

	}

	public void setPatientMRN(String mrn) {
		taskInformations.mobileGetTaskInformations.mrn = mrn;

	}

	public String getPatientMRN() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.mrn;
		return null;
	}

	public void setIsolation(String isolation) {
		taskInformations.mobileGetTaskInformations.isolation = isolation;

	}

	public String getIsolation() {
		if (validated)
			return taskInformations.mobileGetTaskInformations.isolation;
		return null;
	}

	public void setNotes(String notes) {
		taskInformations.mobileGetTaskInformations.notes = notes;

	}

	public void setModeBrief(String modeBrief) {
		taskInformations.mobileGetTaskInformations.modeBrief = modeBrief;

	}

	public void setEquipmentBrief(String equipmentBrief) {
		taskInformations.mobileGetTaskInformations.equipmentBrief = equipmentBrief;

	}

	public void setMobileUserName(String mobileUserName) {
		taskInformations.mobileUserName = mobileUserName;

	}

	public String getMobileUserName() {
		if (validated)
			return taskInformations.mobileUserName;
		return null;
	}

	public Bundle getAllNamedValues() {
		Bundle bundle = new Bundle();
		bundle.putString("Destination",
				taskInformations.mobileGetTaskInformations.hirDestLocationNode);
		bundle.putString("Start",
				taskInformations.mobileGetTaskInformations.hirStartLocationNode);
		bundle.putString("Mode",
				taskInformations.mobileGetTaskInformations.modeBrief);
		bundle.putString("Equipment",
				taskInformations.mobileGetTaskInformations.equipmentBrief);
		bundle.putString("Start",
				taskInformations.mobileGetTaskInformations.equipmentBrief);
		bundle.putString("Item",
				taskInformations.mobileGetTaskInformations.item);
		bundle.putString("Schedule Date",
				taskInformations.mobileGetTaskInformations.assignedDate);
		bundle.putString("Class",
				taskInformations.mobileGetTaskInformations.classBrief);
		bundle.putString("Patient Name",
				taskInformations.mobileGetTaskInformations.patientName);
		bundle.putString("Patient DOB",
				taskInformations.mobileGetTaskInformations.patientDOB);
		bundle.putString("C-Code",
				taskInformations.mobileGetTaskInformations.steCostCode);
		bundle.putString("CustomField1",
				taskInformations.mobileGetTaskInformations.customField1);
		bundle.putString("CustomField2",
				taskInformations.mobileGetTaskInformations.customField2);
		bundle.putString("CustomField3",
				taskInformations.mobileGetTaskInformations.customField3);
		bundle.putString("CustomField4",
				taskInformations.mobileGetTaskInformations.customField4);
		bundle.putString("CustomField5",
				taskInformations.mobileGetTaskInformations.customField5);
		bundle.putString("Task Number",
				taskInformations.mobileGetTaskInformations.taskNumber);
		bundle.putString("Notes",
				taskInformations.mobileGetTaskInformations.notes);
		return bundle;
	}

}
