package com.ii.mobile.flowing;

import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;

import com.ii.mobile.flow.authenticate.Login;
import com.ii.mobile.flow.db.AbstractFlowDbAdapter;
import com.ii.mobile.flow.db.FlowDbAdapter;
import com.ii.mobile.flow.db.StaticFlowProvider;
import com.ii.mobile.flow.db.StaticFlowProvider.StaticFlowColumns;
import com.ii.mobile.flow.types.GetActionHistory;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flow.types.SelectAvailableZones;
import com.ii.mobile.flow.types.SelectClassTypesByFacilityId;
import com.ii.mobile.flow.types.SelectLocations;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public enum FlowBinder {
	INSTANCE;

	public static Activity activity;

	public synchronized boolean staticLoad() {
		L.out("start the staticLoad");
		if (UpdateController.getActorStatus == null) {
			UpdateController.getActorStatus = (GetActorStatus) getGJon(FlowRestService.GET_ACTOR_STATUS, activity);
			if (UpdateController.getActorStatus == null)
				UpdateController.getActorStatus = Flow.getFlow().getActorStatus("DUMMY_EMPLOYEE_ID");
			L.out("getActorStatus: " + UpdateController.getActorStatus);
			if (UpdateController.getActorStatus == null)
				return false;
		}
		if (UpdateController.selectClassTypesByFacilityId == null) {
			UpdateController.selectClassTypesByFacilityId = (SelectClassTypesByFacilityId) getGJon(FlowRestService.SELECT_CLASS_TYPES_BY_FACILITY_ID, activity);
			if (UpdateController.selectClassTypesByFacilityId == null) {
				String facilityId = UpdateController.getActorStatus.getActorStatusInner.targets.facilityId;
				L.out("facilityId: " + facilityId);
				String actorId = UpdateController.getActorStatus.getActorStatusInner.targets.actor_id;
				UpdateController.selectClassTypesByFacilityId = Flow.getFlow().selectClassTypes(facilityId, actorId);
				// L.out("selectClassTypesByFacilityId: " +
				// selectClassTypesByFacilityId);
			}
			if (UpdateController.selectClassTypesByFacilityId == null)
				return false;
		}
		if (UpdateController.selectLocations == null) {
			UpdateController.selectLocations = (SelectLocations) getGJon(FlowRestService.SELECT_LOCATIONS, activity);
			if (UpdateController.selectLocations == null) {
				String facilityId = UpdateController.getActorStatus.getActorStatusInner.targets.facilityId;
				L.out("facilityId: " + facilityId);
				UpdateController.selectLocations = Flow.getFlow().selectLocations(facilityId);
			}
			// L.out("selectLocations: " + selectLocations);
			if (UpdateController.selectLocations == null)
				return false;
		}
		if (UpdateController.selectAvailableZones == null) {
			UpdateController.selectAvailableZones = (SelectAvailableZones) getGJon(FlowRestService.SELECT_AVAILABLE_ZONES, activity);
			if (UpdateController.selectAvailableZones == null) {
				String facilityId = UpdateController.getActorStatus.getActorStatusInner.targets.facilityId;
				L.out("facilityId: " + facilityId);
				String actorId = UpdateController.getActorStatus.getActorStatusInner.targets.actor_id;
				L.out("actorId: " + actorId);
				UpdateController.selectAvailableZones = Flow.getFlow().selectAvailableZones(facilityId, actorId);
			}
			// L.out("selectLocations: " + selectLocations);
			if (UpdateController.selectAvailableZones == null)
				return false;
		}
		// UpdateController.INSTANCE.doCallback(UpdateController.getActionHistory,
		// FlowRestService.SELECT_LOCATIONS);

		if (UpdateController.getActionStatus == null) {
			String actionId = UpdateController.getActorStatus.getActionId();
			if (actionId != null) {
				UpdateController.getActionStatus = (GetActionStatus) getGJon(FlowRestService.GET_ACTION_STATUS, activity);
				if (UpdateController.getActionStatus == null) {
					UpdateController.getActionStatus = Flow.getFlow().getActionStatus(actionId);
					// L.out("getActionStatus: " +
					// UpdateController.getActionStatus);
				}
				if (UpdateController.getActionStatus == null)
					return false;
				// L.out("actionId: " + actionId + " " +
				// UpdateController.getActionStatus.getActionId());
				// PrettyPrint.prettyPrint(UpdateController.getActionStatus.getJson(),
				// true);
				// UpdateController.getActionStatus.setActionId(actionId);
				UpdateController.putActionStatus(UpdateController.getActionStatus);
				// AudioPlayer.INSTANCE.playSound(AudioPlayer.NEW_TASK);
				UpdateController.INSTANCE.callback(UpdateController.getActorStatus,
						FlowRestService.GET_ACTOR_STATUS);
			}
		}
		if (UpdateController.getActionHistory == null) {
			UpdateController.getActionHistory = (GetActionHistory) getGJon(FlowRestService.GET_ACTION_HISTORY, activity);
			if (UpdateController.getActionHistory == null) {
				UpdateController.getActionHistory = Flow.getFlow().getActionHistory(UpdateController.getActorStatus);
			}
			if (UpdateController.getActionHistory == null)
				return false;
			L.out("getActionHistory: " + UpdateController.getActionHistory.getTargets());
			if (UpdateController.getActionStatus != null)
				UpdateController.putActionStatus(UpdateController.getActionStatus);
			UpdateController.INSTANCE.doCallback(UpdateController.getActionHistory, FlowRestService.GET_ACTION_HISTORY);
		}

		return true;
	}

	synchronized public static GJon getGJon(String methodName, Activity activity) {
		L.out("methodName: " + methodName);
		if (methodName.equals(FlowRestService.GET_ACTOR_STATUS)) {
			String userName = Login.userName;
			String json = getJSon(activity, methodName, userName, null, null);
			L.out("json: " + json);
			if (json == null)
				return null;
			GJon gJon = GetActorStatus.getGJon(json);
			if (gJon == null)
				L.out("ERROR: getGJon failed to bind json: " + json);
			// L.out("GetActorStatus: " + gJon);
			return gJon;
		}
		if (methodName.equals(FlowRestService.SELECT_CLASS_TYPES_BY_FACILITY_ID)) {
			GetActorStatus getActorStatus = UpdateController.getActorStatus;
			String actorId = getActorStatus.getActorStatusInner.targets.actor_id;
			String facilityId = getActorStatus.getFacilityId();
			String functionalArea = getActorStatus.getFunctionalAreaTypeId();
			String json = getJSon(activity, methodName, actorId, facilityId, functionalArea);
			if (json == null)
				return null;
			GJon gJon = SelectClassTypesByFacilityId.getGJon(json);
			if (gJon == null)
				L.out("ERROR: getGJon failed to bind json: " + json);
			// L.out("SelectClassTypesByFacilityId: " + gJon);
			return gJon;
		}
		if (methodName.equals(FlowRestService.SELECT_LOCATIONS)) {
			GetActorStatus getActorStatus = UpdateController.getActorStatus;
			String actorId = getActorStatus.getActorStatusInner.targets.actor_id;
			String facilityId = getActorStatus.getFacilityId();
			String functionalArea = getActorStatus.getFunctionalAreaTypeId();
			String json = getJSon(activity, methodName, actorId, facilityId, functionalArea);
			// String json = getJSon(activity, methodName, actorId, null, null);
			if (json == null)
				return null;
			GJon gJon = SelectLocations.getGJon(json);
			if (gJon == null)
				L.out("ERROR: getGJon failed to bind json: " + json);
			// L.out("SelectLocations: " + gJon);
			return gJon;
		}
		if (methodName.equals(FlowRestService.SELECT_AVAILABLE_ZONES)) {
			GetActorStatus getActorStatus = UpdateController.getActorStatus;
			String actorId = getActorStatus.getActorStatusInner.targets.actor_id;
			String facilityId = getActorStatus.getFacilityId();
			String functionalArea = getActorStatus.getFunctionalAreaTypeId();
			String json = getJSon(activity, methodName, actorId, facilityId, functionalArea);
			// String json = getJSon(activity, methodName, actorId, null, null);
			if (json == null)
				return null;
			GJon gJon = SelectAvailableZones.getGJon(json);
			if (gJon == null)
				L.out("ERROR: getGJon failed to bind json for SelectAvailableZones: " + json);
			// L.out("SelectLocations: " + gJon);
			return gJon;
		}
		if (methodName.equals(FlowRestService.GET_ACTION_STATUS)) {
			GetActorStatus getActorStatus = UpdateController.getActorStatus;
			String actionId = getActorStatus.getActorStatusInner.targets.action_id;
			String json = getJSon(activity, methodName, null, null, actionId);
			if (json == null)
				return null;
			GJon gJon = GetActionStatus.getGJon(json);
			if (gJon == null)
				L.out("ERROR: getGJon failed to bind json: " + json);
			// L.out("GetActionStatus: " + gJon);
			return gJon;
		}
		if (methodName.equals(FlowRestService.GET_ACTION_HISTORY)) {
			GetActorStatus getActorStatus = UpdateController.getActorStatus;
			String actorId = getActorStatus.getActorStatusInner.targets.actor_id;
			String facilityId = getActorStatus.getFacilityId();
			String functionalArea = getActorStatus.getFunctionalAreaTypeId();
			String json = getJSon(activity, methodName, actorId, facilityId, functionalArea);
			// String json = getJSon(activity, methodName, actorId, null, null);
			// L.out("json: " + json);
			if (json == null)
				return null;
			GJon gJon = GetActionHistory.getGJon(json);
			if (gJon == null)
				L.out("ERROR: getGJon failed to bind json: " + json);
			L.out("GetActionStatus history: " + gJon);
			return gJon;
		}
		L.out("ERROR: getGJon cannot find methodName: " + methodName);
		return null;
	}

	public static String getJSon(Activity activity, String methodName, String employeeId,
			String facilityId, String actionId) {
		L.out("methodName: " + methodName + " facilityId: " + facilityId + " actionId: " + actionId);
		// User user = User.getUser();
		// L.out("user: " + user);
		// String facilityId = user.getFacilityID();
		// String employeeId = user.getEmployeeID();

		Intent intent = activity.getIntent();
		String[] selectionArgs = new String[] { employeeId, facilityId, actionId };
		intent.setData(Uri.parse("content://" + StaticFlowProvider.AUTHORITY + "/" +
				methodName));
		Cursor cursor = activity.managedQuery(activity.getIntent().getData(), null, null, selectionArgs, null);
		if (cursor != null) {
			cursor.moveToFirst();
			do {
				int index = cursor.getColumnIndex(StaticFlowColumns.JSON);
				// L.out("index: " + index + " " + cursor.getClass());
				String json = cursor.getString(cursor.getColumnIndex(StaticFlowColumns.JSON));
				if (json != null) {
					// L.out("json: " + json.length());
				}
				else
					L.out("ERROR: Json is null for methodName: " + methodName);
				return json;

			} while (cursor.moveToNext());
		}
		return null;
	}

	public static boolean updateLocalDatabase(String methodName, GJon gJon) {
		return updateLocalDatabase(methodName, gJon, null);
	}

	public static boolean updateLocalDatabase(String methodName, GJon gJon, String localActionNumber) {
		L.out("methodName: " + methodName + " tickled: " + gJon.tickled);
		GetActorStatus getActorStatus = UpdateController.getActorStatus;
		if (getActorStatus == null || gJon.getNewJson() == null) {
			L.out("ERROR: Unable to update, getActorStatus is null  or getNewJson is null for methodName: "
					+ methodName);
			return false;
		}

		String facilityId = getActorStatus.getActorStatusInner.targets.facilityId;
		String actorId = getActorStatus.getActorStatusInner.targets.actor_id;
		String actionId = getActorStatus.getActorStatusInner.targets.action_id;
		String functionalArea = UpdateController.getActorStatus.getFunctionalAreaTypeId();
		if (localActionNumber != null)
			actionId = localActionNumber;
		if (methodName.equals(FlowRestService.GET_ACTOR_STATUS)) {
			facilityId = null;
			actionId = null;
			actorId = Login.userName;
		}
		if (!methodName.equals(FlowRestService.GET_ACTION_STATUS)) {
			actionId = functionalArea;
			// L.out("!GetActionStatus Update: " + gJon);
		}

		if (methodName.equals(FlowRestService.SEND_MESSAGE)) {
			GregorianCalendar calendar = new GregorianCalendar();
			actionId = calendar.getTimeInMillis() + "";
		}

		ContentValues values = new ContentValues();
		values.put(StaticFlowColumns.JSON, gJon.getNewJson());
		values.put(StaticFlowColumns.FACILITY_ID, facilityId);
		values.put(StaticFlowColumns.ACTOR_ID, actorId);
		values.put(StaticFlowColumns.ACTION_ID, actionId);
		values.put(StaticFlowColumns.FLOW_METHOD, methodName);
		if (gJon.tickled != null && gJon.tickled.equals(GJon.FALSE_STRING))
			values.put(AbstractFlowDbAdapter.TICKLED, GJon.FALSE_STRING);
		// if (localActionNumber != null)
		// values.put(StaticFlowColumns.LOCAL_ACTION_NUMBER, localActionNumber);
		String[] selectionArgs = new String[] { actorId, facilityId, localActionNumber };
		Intent intent = activity.getIntent();
		intent.setData(Uri.parse("content://" + StaticFlowProvider.AUTHORITY + "/" + methodName));
		activity.getContentResolver().update(intent.getData(), values, FlowDbAdapter.getWhere(methodName, actorId, facilityId, actionId), selectionArgs);
		return true;
	}

	public static void deleteLocalDatabase(String methodName) {
		L.out("methodName: " + methodName);

		GetActorStatus getActorStatus = UpdateController.getActorStatus;
		if (getActorStatus == null) {
			L.out("ERROR: Unable to delete, getActorStatus is null for methodName: " + methodName);
			return;
		}

		String facilityId = getActorStatus.getActorStatusInner.targets.facilityId;
		String actorId = getActorStatus.getActorStatusInner.targets.actor_id;

		String[] selectionArgs = new String[] { actorId, null, methodName };
		Intent intent = activity.getIntent();
		intent.setData(Uri.parse("content://" + StaticFlowProvider.AUTHORITY + "/" + methodName));
		activity.getContentResolver().delete(intent.getData(), FlowDbAdapter.getWhere(methodName, actorId, facilityId, null), selectionArgs);
	}
}
