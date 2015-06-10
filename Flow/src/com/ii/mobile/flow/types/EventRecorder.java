package com.ii.mobile.flow.types;

import com.google.gson.annotations.SerializedName;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.util.L;

public class EventRecorder extends KeepNames {
	@SerializedName("pointer")
	public int pointer = 0;
	@SerializedName("events")
	// public ArrayList<Event> events = new ArrayList<Event>();
	public Event[] events = new Event[0];

	// public Event[] events = new Event[1];
	public void addEvent(String actionStatusId, String optionId, GetActionStatus getActionStatus) {

		addEvent(actionStatusId, optionId);
		L.out("events: " + events.length);
		if (TransportActivity.showToast)
			MyToast.show(toString(getActionStatus));
	}

	public String toString(GetActionStatus getActionStatus) {
		String temp = "EventRecorder for " + getActionStatus.getActionNumber() + "\nid: "
				+ getActionStatus.getActionId() + "\n";
		int index = 0;
		L.out("index: " + index);
		for (int i = 0; i < events.length; i++) {
			Event event = events[i];
			String point = "->";
			if (index != pointer)
				point = "__";
			String option = "";
			if (event.optionId != null)
				option = " *";
			temp += point + " Event: " + L.toDateSecond(L.getLong(event.time))
					+ " "
					+ StaticFlow.INSTANCE.findActionStatusName(event.actionStatusId)
					+ option
					+ "\n";
			index += 1;
		}
		return temp;
	}

	private void addEvent(String actionStatusId, String optionId) {
		Event[] temp = new Event[events.length + 1];
		for (int i = 0; i < events.length; i++)
			temp[i] = events[i];
		temp[events.length] = new Event(actionStatusId, optionId);
		events = temp;
	}
}
