package com.ii.mobile.flow.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.ii.mobile.flow.db.StaticFlowProvider.StaticFlowColumns;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flow.types.SelectAvailableZones;
import com.ii.mobile.flow.types.SendMessage;
import com.ii.mobile.flowing.Flow;
import com.ii.mobile.flowing.FlowBinder;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.tickle.Tickler;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;
import com.ii.mobile.util.SecurityUtils;

public class Uploader {

	public FlowDbAdapter flowDbAdapter;

	public boolean update(Cursor cursor, FlowDbAdapter flowDbAdapter, boolean wantSecurity) {

		this.flowDbAdapter = flowDbAdapter;
		String flowMethod = cursor.getString(cursor.getColumnIndexOrThrow(StaticFlowColumns.FLOW_METHOD));

		L.out("flowMethod: " + flowMethod);
		String json = cursor.getString(cursor.getColumnIndexOrThrow(StaticFlowColumns.JSON));
		if (wantSecurity) {
			json = SecurityUtils.decryptAES(User.getUser().getPassword(), json);
			// L.out("update json: " + json);
		}
		boolean result = doUpdate(flowMethod, cursor, json);
		// MyToast.show("result: " + result);
		if (result && !flowMethod.equals(FlowRestService.SEND_MESSAGE)) {
			String rowId = cursor.getString(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.KEY_ROWID));
			flowDbAdapter.updateTimeStamp(cursor, (String) null, rowId, null, new ContentValues());
		}
		return result;
	}

	private boolean doUpdate(String flowMethod, Cursor cursor, String json) {
		L.out("flowMethod: " + flowMethod);
		if (flowMethod.equals(FlowRestService.GET_ACTOR_STATUS))
			return updateActorStatus(cursor, json);

		if (flowMethod.equals(FlowRestService.GET_ACTION_STATUS)) {
			// String localActionNumber =
			// cursor.getString(cursor.getColumnIndexOrThrow(StaticFlowColumns.LOCAL_ACTION_NUMBER));
			// L.out("localActionNumber: " + localActionNumber);
			return updateActionStatus(cursor, json);
		}

		if (flowMethod.equals(FlowRestService.SELECT_AVAILABLE_ZONES)) {
			// String localActionNumber =
			// cursor.getString(cursor.getColumnIndexOrThrow(StaticFlowColumns.LOCAL_ACTION_NUMBER));
			// L.out("localActionNumber: " + localActionNumber);
			return updateSelectAvailableZones(cursor, json);
		}

		if (flowMethod.equals(FlowRestService.SEND_MESSAGE))
			return sendMessage(cursor, json);

		if (flowMethod.equals(FlowRestService.SEND_LOGGER))
			return sendLogger(cursor, json);

		L.out("ERROR: Unable to update: " + flowMethod);

		return false;
	}

	private boolean updateSelectAvailableZones(Cursor cursor, String json) {
		SelectAvailableZones selectAvailableZones = SelectAvailableZones.getGJon(json);

		if (selectAvailableZones == null) {
			L.out("ERROR: Unable to create GetActionStatus from json: " + json);
			return false;
		}

		// boolean success = Flow.getFlow().actionStatusUpdate(getActionStatus);
		boolean success = Flow.getFlow().sendSelectAvailableZones(selectAvailableZones.assignedZone);
		L.out("success: " + success);
		if (success)
		{
			long rowId = cursor.getLong(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.KEY_ROWID));
			L.out("delete: " + rowId);
			flowDbAdapter.delete(rowId);
		}
		return success;

		// boolean success = Flow.getFlow().actionStatusUpdate(getActionStatus);
		// // L.out("success: " + success);
		// // return success;
		// return false;
	}

	private boolean sendMessage(Cursor cursor, String json) {
		SendMessage sendMessage = SendMessage.getGJon(json);
		L.out("sendMessage: " + sendMessage);
		boolean success = Flow.getFlow().sendMessage(sendMessage.getMessage(), sendMessage.getTo());
		L.out("success: " + success);
		if (success)
		{
			long rowId = cursor.getLong(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.KEY_ROWID));
			L.out("delete: " + rowId);
			flowDbAdapter.delete(rowId);
		}
		return success;
	}

	private boolean sendLogger(Cursor cursor, String json) {

		boolean success = Flow.getFlow().sendLogger(json);
		L.out("success: " + success);
		if (success)
		{
			long rowId = cursor.getLong(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.KEY_ROWID));
			L.out("delete: " + rowId);
			flowDbAdapter.delete(rowId);
		}
		return success;
	}

	private boolean updateActorStatus(Cursor cursor, String json) {
		GetActorStatus getActorStatus = GetActorStatus.getGJon(json);
		if (getActorStatus == null) {
			L.out("ERROR: Unable to create GetActorStatus from json: " + json);
			return false;
		}

		boolean success = Flow.getFlow().actorStatusUpdate(getActorStatus);
		L.out("success: " + success);
		return success;
	}

	private boolean updateActionStatus(Cursor cursor, String json) {
		GetActionStatus getActionStatus = GetActionStatus.getGJon(json);

		if (getActionStatus == null) {
			L.out("ERROR: Unable to create GetActionStatus from json: " + json);
			return false;
		}
		L.out("getActionStatus: " + getActionStatus.eventRecorder.events);
		// PrettyPrint.prettyPrint(json, true);
		String actionId =
				cursor.getString(cursor.getColumnIndexOrThrow(StaticFlowColumns.ACTION_ID));
		getActionStatus.setActionId(actionId);
		if (actionId == null) {
			MyToast.show("ERROR updateActionStatus actionId: " + actionId);
			flowDbAdapter.showAllEvents();
			return false;
		}
		if (actionId.startsWith("L"))
			return createActionStatus(cursor, json);
		// boolean success = Flow.getFlow().actionStatusUpdate(getActionStatus);
		boolean success = new PlayEvents(getActionStatus, flowDbAdapter, cursor).play();
		L.out("success: " + success);
		return success;

		// boolean success = Flow.getFlow().actionStatusUpdate(getActionStatus);
		// // L.out("success: " + success);
		// // return success;
		// return false;
	}

	private boolean createActionStatus(Cursor cursor, String json) {
		L.out("createActionStatus: ");
		GetActionStatus getActionStatus = GetActionStatus.getGJon(json);
		if (getActionStatus == null) {
			L.out("ERROR: Unable to create GetActionStatus from json: " + json);
			return false;
		}
		// L.out("actionNumber: " + getActionStatus.getActionNumber());

		String result = Flow.getFlow().createAction(getActionStatus);
		// MyToast.show("result: " + result);
		if (result != null) {
			GetActionStatus getActionStatusResult = GetActionStatus.getGJon(result);
			if (getActionStatusResult == null)
				return false;
			L.out("getActionStatusResult.getActionNumber: " + getActionStatusResult.getActionNumber());
			L.out("getActionStatusResult.getActionId: " + getActionStatusResult.getActionId());
			L.out("getActionStatus.getActionNumber: " + getActionStatus.getActionNumber());
			if (getActionStatusResult.getActionId() == null) {
				L.out("getActionStatusResult: " + getActionStatusResult.getJson());
				// MyToast.show("ERROR: Failed to create action!: \nPlease report to Crothall with action details");
				// MyToast.show("ERROR: Failed to create action!: \nPlease report to Crothall with action details");
				String rowId = cursor.getString(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.KEY_ROWID));
				L.out("rowId: " + rowId);
				flowDbAdapter.deleteRow(rowId);
				GetActorStatus getActorStatus = UpdateController.getActorStatus;
				getActorStatus.setActionStatusId(StaticFlow.ACTOR_AVAILABLE);
				// UpdateController.removeActionStatus(getActorStatus.getActionId());
				getActorStatus.setActionId(null);
				Tickler.lastGetActorStatus = null;
				UpdateController.INSTANCE.callback(getActorStatus, FlowRestService.GET_ACTOR_STATUS);
				return false;
			}

			String oldLocalActionId = getActionStatus.getActionId();
			getActionStatus.setActionId(getActionStatusResult.getActionId());
			getActionStatus.setActionNumber(getActionStatusResult.getActionNumber());

			if (UpdateController.getActionStatus != null)
				MyToast.show("Created action: " + getActionStatus.getActionNumber());
			// + " old: " + oldLocalActionId
			// + " current: " +
			// UpdateController.getActionStatus.getTarget().localActionId);
			updateDataBase(cursor, getActionStatus, true);
			UpdateController.getActionStatus = getActionStatus;
			updateFrontEnd(getActionStatus, oldLocalActionId);

			return true;

			// boolean success = new PlayEvents(getActionStatus, flowDbAdapter,
			// cursor).play();
			// L.out("success: " + success);
			// return success;
		}
		L.out("ERROR: failed to createActionStatus");
		return false;
	}

	private void updateFrontEnd(GetActionStatus getActionStatus, String oldLocalActionId) {
		getActionStatus.setLocalActionId(oldLocalActionId);
		GetActionStatus currentGetActionStatus = UpdateController.getActionStatus;
		UpdateController.putActionStatus(getActionStatus);
		if (currentGetActionStatus == null
				|| !oldLocalActionId.equals(currentGetActionStatus.getTarget().localActionId)) {
			// MyToast.show("Ignoring updateFrontEnd for old Action: " +
			// getActionStatus.getActionNumber());
			return;
		}
		GetActorStatus getActorStatus = UpdateController.getActorStatus;
		if (getActorStatus == null)
			return;
		getActorStatus.setActionId(getActionStatus.getActionId());

		getActorStatus.tickled = GJon.TRUE_STRING;
		// MyToast.show("new getActorStatus: " +
		// getActionStatus.getActionNumber());
		FlowBinder.updateLocalDatabase(FlowRestService.GET_ACTOR_STATUS, getActorStatus);
		UpdateController.INSTANCE.callback(getActorStatus, null);
	}

	public void updateDataBase(Cursor cursor, GetActionStatus getActionStatus) {
		ContentValues values = new ContentValues();

		values.put(StaticFlowColumns.JSON, getActionStatus.getNewJson());
		values.put("actionId", getActionStatus.getActionId());
		String rowId = cursor.getString(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.KEY_ROWID));
		long updated = flowDbAdapter.update(values, AbstractFlowDbAdapter.KEY_ROWID + "=" + rowId);
		L.out("updated: " + updated);
	}

	public void updateDataBase(Cursor cursor, GetActionStatus getActionStatus, boolean wantTimeStampKept) {
		ContentValues contentValues = new ContentValues();
		// L.out("create database before");
		// flowDbAdapter.showAllEvents();
		contentValues.put(StaticFlowColumns.JSON, getActionStatus.getNewJson());
		contentValues.put("actionId", getActionStatus.getActionId());
		String rowId = cursor.getString(cursor.getColumnIndexOrThrow(AbstractFlowDbAdapter.KEY_ROWID));
		long updated = flowDbAdapter.update(contentValues, AbstractFlowDbAdapter.KEY_ROWID + "=" + rowId, true);
		L.out("updated: " + updated);
		// L.out("create database after");
		flowDbAdapter.showAllEvents();
	}
}
