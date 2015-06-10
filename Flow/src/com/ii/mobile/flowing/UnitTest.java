package com.ii.mobile.flowing;

import java.util.List;

import com.ii.mobile.flow.staticFlow.CostCodes;
import com.ii.mobile.flow.staticFlow.Equipments;
import com.ii.mobile.flow.staticFlow.Modes;
import com.ii.mobile.flow.staticFlow.PersistTypes;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flow.types.SelectClassTypesByFacilityId;
import com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Targets;
import com.ii.mobile.flow.types.SelectLocations;
import com.ii.mobile.flow.types.SelectLocations.Items;
import com.ii.mobile.payload.PlainString;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;
import com.ii.mobile.util.PrettyPrint;

public class UnitTest {

	public void unitTest() {
		GetActorStatus getActorStatus = Flow.getFlow().getActorStatus("foo");
		L.out("getActorStatus: " + getActorStatus);
		getActorStatus.json = null;
		String json = getActorStatus.getNewJson();
		UpdateController.INSTANCE.callback(new PlainString("getActorStatus: "
				+ PrettyPrint.formatPrint(json)), UpdateController.PLAIN_STRING);

		// StatusWrapper statusWrapper = getActorStatus.inject();
		// L.out("statusWrapper: " + statusWrapper);
		// UpdateController.INSTANCE.callback(new
		// PlainString("statusWrapper: "
		// + statusWrapper.toString().replaceAll("\n", "<br/>").replaceAll(" ",
		// "&nbsp;")), UpdateController.PLAIN_STRING);

		String actionId = getActorStatus.getActionId();
		L.out("actionId: " + actionId);
		if (actionId != null) {
			GetActionStatus getActionStatus = Flow.getFlow().getActionStatus(actionId);
			L.out("getActionStatus: " + getActionStatus);
			getActionStatus.json = null;
			json = getActionStatus.getNewJson();
			UpdateController.INSTANCE.callback(new PlainString("getActionStatus: "
					+ PrettyPrint.formatPrint(json)), UpdateController.PLAIN_STRING);
		}

		String facilityId = getActorStatus.getFacilityId();
		SelectClassTypesByFacilityId selectClassTypesByFacilityId = Flow.getFlow().selectClassTypes(facilityId, null);
		// L.out("selectClassTypesByFacilityId: " +
		// selectClassTypesByFacilityId);
		selectClassTypesByFacilityId.json = null;
		json = selectClassTypesByFacilityId.getNewJson();
		UpdateController.INSTANCE.callback(new PlainString("selectClassTypesByFacilityId: "
				+ PrettyPrint.formatPrint(json)), UpdateController.PLAIN_STRING);

		SelectLocations selectLocations = Flow.getFlow().selectLocations(facilityId);
		L.out("selectLocations: " + selectLocations);
		// selectLocations.json = null;
		// json = selectLocations.getNewJson();
		// UpdateController.INSTANCE.callback(new
		// PlainString("selectLocations: "
		// + PrettyPrint.formatPrint(json)), UpdateController.PLAIN_STRING);
		prettyPrint("selectLocations", selectLocations);

		List<Targets> classTypes = selectClassTypesByFacilityId.getClassTypes(facilityId);

		String temp = "";
		for (Targets target : classTypes) {
			temp += "\n" + target.toString();
		}
		// L.out("classTypes: " + temp);
		UpdateController.INSTANCE.callback(new PlainString("classTypes: "
				+ temp.replaceAll("\n", "<br/>").replaceAll(" ", "&nbsp;")), UpdateController.PLAIN_STRING);

		GetActionStatus testActionStatus = createTestActionStatus(facilityId, classTypes.get(2), selectLocations);
		Object getActionStatus = Flow.getFlow().createAction(testActionStatus);
		L.out("getActionStatus: " + getActionStatus);
		// prettyPrint("createAction", getActionStatus);
	}

	public GetActionStatus createTestActionStatus(String facilityId, Targets target,
			SelectLocations selectLocations) {

		GetActionStatus getActionStatus = new GetActionStatus();
		getActionStatus.setFacilityId(facilityId);
		getActionStatus.setFunctionalAreaTypeId(target.functionalAreaTypeId);
		getActionStatus.setClassTypeId(target._id, target.name);
		Items start = selectLocations.getRandomRoom();
		Items destination = selectLocations.getRandomRoom();
		getActionStatus.setStartId(start._id);
		getActionStatus.setStartName(start.description);
		getActionStatus.setDestinationId(destination._id);
		getActionStatus.setDestinationName(destination.description);
		getActionStatus.setNotes("Isolation Patient Notify radiology prior to bringing patient to radiology");
		getActionStatus.setEquipmentId(Equipments.INSTANCE.getRandomId());
		getActionStatus.setModeId(Modes.INSTANCE.getRandomId());
		getActionStatus.setPatientMRN("000078127");
		getActionStatus.setPatientBirthDate("05/30/1964");
		getActionStatus.setPatientName("Kim Fairchild");
		getActionStatus.setPersist(PersistTypes.INSTANCE.getRandomId());
		getActionStatus.setScheduleDate("05/30/1964");
		getActionStatus.setItem("Extra Pillow");
		getActionStatus.setCostCodeId(CostCodes.INSTANCE.getRandomId());
		// L.out("createTestActionStatus: " + getActionStatus);
		return getActionStatus;
	}

	private void prettyPrint(String header, GJon gJon) {
		gJon.json = null;
		String json = gJon.getNewJson();
		UpdateController.INSTANCE.callback(new PlainString(header + ": "
				+ PrettyPrint.formatPrint(json)), UpdateController.PLAIN_STRING);
	}
}
