/**
 * 
 */
package com.ii.watcher;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;

import android.os.Bundle;

import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.flowing.SyncCallback;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.tab.IMActivity;
import com.ii.mobile.tickle.Tickler;
import com.ii.mobile.util.L;

public enum ActionWatcher implements SyncCallback {
	INSTANCE;

	Hashtable<String, ActionTimes> actions = new Hashtable<String, ActionTimes>();

	public void start() {
		UpdateController.INSTANCE.registerCallback(this, FlowRestService.GET_ACTOR_STATUS);
	}

	public void stop() {
		UpdateController.INSTANCE.unRegisterCallback(this, FlowRestService.GET_ACTOR_STATUS);
	}

	@Override
	public void callback(GJon gJon, String payloadName) {
		L.out("payloadName: " + payloadName);
		process((GetActorStatus) gJon);
	}

	private void process(GetActorStatus getActorStatus) {
		String actorId = getActorStatus.getActionId();
		L.out("actionNumber: " + actorId);
		ActionTimes actionTime = actions.get(actorId);
		if (actionTime == null)
			actionTime = new ActionTimes(actorId);
		actionTime.add(getActorStatus.getActionStatusId());
	}

	public void out(Times times) {
		Bundle bundle = new Bundle();
		bundle.putString(Tickler.TEXT_MESSAGE, times.status);
		bundle.putString(Tickler.RECEIVED_DATE, times.time + "");
		bundle.putString(Tickler.FROM_USER_NAME, "Debug");
		IMActivity.receivedMessage(bundle);
	}

	public static void out(ActionTimes actionTimes) {
		Bundle bundle = new Bundle();
		bundle.putString(Tickler.TEXT_MESSAGE, "Created: " + actionTimes.actionNumber);
		bundle.putString(Tickler.RECEIVED_DATE, new GregorianCalendar().getTimeInMillis() + "");
		bundle.putString(Tickler.FROM_USER_NAME, "Debug");
		IMActivity.receivedMessage(bundle);
	}
}

class ActionTimes {
	String actionNumber = null;
	List<Times> times = new ArrayList<Times>();

	public ActionTimes(String actionNumber) {
		this.actionNumber = actionNumber;
		ActionWatcher.out(this);
	}

	public void add(String actionStatus) {
		String lastStatus = null;
		if (times.size() > 1)
			lastStatus = times.get(times.size() - 1).status;
		if (lastStatus == null || lastStatus.equals(actionStatus))
			times.add(new Times(actionStatus));
		else
			L.out("Ignoring: " + actionStatus);
	}
}

class Times {
	long time = 0l;
	String status = null;

	Times(String status) {
		GregorianCalendar now = new GregorianCalendar();
		time = now.getTimeInMillis();
		this.status = status;
	}
}