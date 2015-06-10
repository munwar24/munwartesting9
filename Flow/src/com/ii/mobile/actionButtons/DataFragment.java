package com.ii.mobile.actionButtons;

import android.support.v4.app.Fragment;

import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flowing.FlowBinder;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public class DataFragment extends Fragment {

	public boolean visible = true;

	public void setEmployeeStatus(String statusId, boolean tickled) {
		L.out("setEmployeeStatus: " + statusId + " " + tickled);

		final GetActorStatus getActorStatus = UpdateController.getActorStatus;

		getActorStatus.setActorStatusId(statusId);
		getActorStatus.tickled = GJon.FALSE_STRING;
		L.out("new getActorStatus: " + getActorStatus);
		FlowBinder.updateLocalDatabase(FlowRestService.GET_ACTOR_STATUS, getActorStatus);
		UpdateController.INSTANCE.callback(getActorStatus, FlowRestService.GET_ACTOR_STATUS);

		// new Thread() {
		// @Override
		// public void run() {
		// boolean foo = Flow.getFlow().actorStatusUpdate(getActorStatus);
		// L.out("foo: " + foo);
		// }
		// }.start();
	}

	public static void setPosition() {
		int position = TransportActivity.SWOOSH_PAGE;
		if (TransportActivity.transportActivity == null) {
			L.out("ERROR: Unable to setPosition: " + position);
			return;
		}
		if (TransportActivity.running)
			TransportActivity.transportActivity.actionFragmentController.setPosition(position);
	}
}
