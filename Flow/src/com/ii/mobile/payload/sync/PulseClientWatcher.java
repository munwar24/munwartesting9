package com.ii.mobile.payload.sync;

import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.payload.PayloadWrapper;
import com.ii.mobile.util.L;

public class PulseClientWatcher implements Runnable {

	private final PulseClientService pulseClientService;
	private String payload;
	private String lastPayload = null;
	private boolean killThread = false;
	private Thread thread = null;

	public PulseClientWatcher(PulseClientService pulseClientService) {
		this.pulseClientService = pulseClientService;
		new Thread(this).start();
	}

	public void run() {
		L.out("Started watcher");
		while (true) {

			if (killThread
					|| thread == null
					|| !thread.isAlive()) {
				L.out("thread: " + thread);
				if (thread != null) {
					try {
						L.out("trying to stop last thread: " + thread.getName());
						// thread.interrupt();
						pulseClientService.currentSocket.close();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						L.out("ERROR failed to close socket: " + e + L.p());
					}
				}
				L.out("setting and starting payload");
				killThread = false;

				if (payload == null || !killThread) {
					PayloadWrapper payloadWrapper = new PayloadWrapper();
					L.out("adding: " + UpdateController.INSTANCE.statusWrapper);
					L.out("adding: " + UpdateController.INSTANCE.statusWrapper.getJson());
					UpdateController.INSTANCE.statusWrapper.currentStatus.employeePIN = "123";
					payloadWrapper.addPayload(UpdateController.INSTANCE.statusWrapper, false);
					pulseClientService.payload = payloadWrapper.getNewJson();
					L.out("setting to the default: " + pulseClientService.payload);
				}
				lastPayload = pulseClientService.payload;
				thread = new Thread(pulseClientService);
				thread.start();
				L.sleep(1000);
			}
			L.sleep(5000);
		}
	}

	public void setPayLoad(String payload) {
		L.out("kill it");
		this.payload = payload;
		killThread = true;
	}

}
