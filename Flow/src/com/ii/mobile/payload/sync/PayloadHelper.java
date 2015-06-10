package com.ii.mobile.payload.sync;

import org.json.JSONException;
import org.json.JSONObject;

import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.payload.Monitor;
import com.ii.mobile.payload.PayloadWrapper;
import com.ii.mobile.payload.StatusWrapper;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.util.L;
import com.ii.mobile.util.M;

public class PayloadHelper {

	public PayloadHelper(PayloadWrapper payloadWrapper) {
		M.init();
		L.out("created payloadWrapper");
		PulseClientService.INSTANCE.setPayload(payloadWrapper.getNewJson());
	}

	public static PayloadWrapper createFullTest() {
		StatusWrapper statusWrapper = UpdateController.INSTANCE.statusWrapper;

		// statusWrapper.currentStatus.employeeName = "bay";
		// statusWrapper.currentStatus.employeePIN = "123";
		// statusWrapper.currentStatus.employeeStatus = "a";
		// statusWrapper.currentStatus.taskStatus = "b";
		// statusWrapper.currentStatus.taskNumber = "c";

		ValidateUser test = new ValidateUser();
		GetTaskInformationByTaskNumberAndFacilityID test2 = new
				GetTaskInformationByTaskNumberAndFacilityID();
		PayloadWrapper payloadWrapper = new PayloadWrapper();
		payloadWrapper.addPayload(statusWrapper, false);
		payloadWrapper.addPayload(test, false);
		payloadWrapper.addPayload(test2, false);
		Monitor monitor = new Monitor();
		monitor.monitorStatus.wantMonitors = "y";
		monitor.populate();
		// L.out("monitor: " + monitor);
		payloadWrapper.addPayload(monitor, false);
		return payloadWrapper;
	}

	public static PayloadWrapper createNormalTest() {
		StatusWrapper statusWrapper = UpdateController.INSTANCE.statusWrapper;
		// StatusWrapper statusWrapper = new StatusWrapper();
		// statusWrapper.currentStatus.employeeName = "bay";
		// statusWrapper.currentStatus.employeePIN = "123";
		// statusWrapper.currentStatus.employeeStatus = "a";
		// statusWrapper.currentStatus.taskStatus = "b";
		// statusWrapper.currentStatus.taskNumber = "c";

		PayloadWrapper payloadWrapper = new PayloadWrapper();
		payloadWrapper.addPayload(statusWrapper, false);
		if (UpdateController.INSTANCE.validateUser == null) {
			ValidateUser test = new ValidateUser();
			payloadWrapper.addPayload(test, false);
		}
		payloadWrapper.noLongPoll = "y";
		return payloadWrapper;
	}

	public static void prettyPrint(String json) {
		try {
			JSONObject jsonObject = new JSONObject(json);
			if (jsonObject != null)
				L.out("prettyPrint: \n" + jsonObject.toString(2));
		} catch (JSONException e) {
			L.out("failed: " + e);
			e.printStackTrace();
		}
	}

	public static void getRole(final PayloadWrapper payloadWrapper) {
		Thread t = new Thread("Thread1") {
			@Override
			public void run() {
				// L.out("some outer code");
				new PayloadHelper(payloadWrapper);
				// activity.runOnUiThread(new Runnable() {
				// public void run() {
				//
				// }
				// });
			}
		};
		t.start();
	}

	public static PayloadWrapper createMonitorTest() {
		StatusWrapper statusWrapper = UpdateController.INSTANCE.statusWrapper;

		PayloadWrapper payloadWrapper = new PayloadWrapper();
		payloadWrapper.addPayload(statusWrapper, false);
		Monitor monitor = new Monitor();
		monitor.monitorStatus.wantMonitors = "y";
		monitor.populate();
		L.out("monitor: " + monitor.getNewJson());
		payloadWrapper.noLongPoll = "y";
		payloadWrapper.addPayload(monitor, false);
		return payloadWrapper;
	}

	// @Override
	// public void run() {
	// int count = 0;
	// while (true) {
	//
	// count += 1;
	// L.sleep(1000);
	// }
	// }

}