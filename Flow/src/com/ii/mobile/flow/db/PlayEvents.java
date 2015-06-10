package com.ii.mobile.flow.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.ii.mobile.flow.db.StaticFlowProvider.StaticFlowColumns;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.Event;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flowing.Flow;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public class PlayEvents {

	GetActionStatus getActionStatus = null;
	private final FlowDbAdapter flowDbAdapter;
	private final Cursor cursor;

	public PlayEvents(GetActionStatus getActionStatus, FlowDbAdapter flowDbAdapter, Cursor cursor) {
		this.getActionStatus = getActionStatus;
		this.flowDbAdapter = flowDbAdapter;
		this.cursor = cursor;
	}

	public boolean play() {
		// MyToast.show("Playing: " +
		// getActionStatus.eventRecorder.toString(getActionStatus));
		int pointer = getActionStatus.eventRecorder.pointer;
		L.out("pointer: " + pointer);
		if (getActionStatus.eventRecorder.events.length < getActionStatus.eventRecorder.pointer + 1) {
			L.out("nothing to play: " + getActionStatus.getActionNumber());
			return true;
		}
		Event event = getActionStatus.eventRecorder.events[pointer];
		if (event.actionStatusId.equals(StaticFlow.ACTION_ASSIGNED)) {
			L.out("ignore assigned");
			incrementPointer();
		} else {
			boolean success = Flow.getFlow().actionStatusUpdate(getActionStatus, event);
			if (success) {
				if (TransportActivity.showToast) {
					String temp = "";
					if (event.getOptionSimpleName() != null)
						temp = "\n" + event.getOptionSimpleName();
					MyToast.show("Update action: " + getActionStatus.getActionNumber()
							+ "\nto: " + StaticFlow.INSTANCE.findActionStatusName(event.actionStatusId)
							+ "\nat: " + FlowRestService.toDate(event.time)
							+ temp);
				}
				incrementPointer();
				if (getActionStatus.eventRecorder.pointer == getActionStatus.eventRecorder.events.length) {
					L.out("finished: " + getActionStatus.getActionNumber());
				}
			}
		}

		return false;
	}

	private void incrementPointer() {
		getActionStatus.eventRecorder.pointer = getActionStatus.eventRecorder.pointer + 1;
		updateDataBase(cursor, getActionStatus);
		if (UpdateController.getActionStatus != null) {
			L.out("current action: "
					+ UpdateController.getActionStatus.getActionId() + " "
					+ UpdateController.getActionStatus.getActionNumber()
					+ "  new action: " + getActionStatus.getActionId());
		}
		if (UpdateController.getActionStatus != null
				&& UpdateController.getActionStatus.getActionId().equals(getActionStatus.getActionId())) {
			L.out("active action is updated: " + getActionStatus.getActionNumber());
			UpdateController.getActionStatus.eventRecorder = getActionStatus.eventRecorder;
		}

	}

	public void updateDataBase(Cursor cursor, GetActionStatus getActionStatus, boolean tickled) {
		ContentValues values = new ContentValues();
		values.put(AbstractFlowDbAdapter.TICKLED, GJon.FALSE_STRING);
		L.out("udpate database before");
		// flowDbAdapter.showAllEvents();
		values.put(StaticFlowColumns.JSON, getActionStatus.getNewJson());
		// values.put(AbstractFlowDbAdapter.TICKLED, GJon.TRUE_STRING);
		String rowId = cursor.getString(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.KEY_ROWID));
		long updated = flowDbAdapter.update(values, AbstractFlowDbAdapter.KEY_ROWID + "=" + rowId, true);
		L.out("updated: " + updated);
		L.out("udpate database after");
		// flowDbAdapter.showAllEvents();
	}

	public void updateDataBase(Cursor cursor, GetActionStatus getActionStatus) {
		ContentValues values = new ContentValues();
		L.out("udpate database before");
		// flowDbAdapter.showAllEvents();
		values.put(StaticFlowColumns.JSON, getActionStatus.getNewJson());
		// values.put(AbstractFlowDbAdapter.TICKLED, GJon.TRUE_STRING);
		String rowId = cursor.getString(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.KEY_ROWID));
		long updated = flowDbAdapter.update(values, AbstractFlowDbAdapter.KEY_ROWID + "=" + rowId, true);
		L.out("updated: " + updated);
		L.out("udpate database after");
		// flowDbAdapter.showAllEvents();
	}
}
