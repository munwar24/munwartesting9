package com.ii.mobile.flow.staticFlow;

import com.ii.mobile.util.L;

public enum StaticFlow {
	INSTANCE;
	public final static String ACTOR_AVAILABLE = "50f55167ec02555879567fde";
	public final static String ACTOR_ACTIVE = "513894394ddecb0dd8705150";
	public final static String ACTOR_IDLE = "5138945e4ddecb0dd8705151";
	public final static String ACTOR_LUNCH = "513894744ddecb0dd8705152";
	public final static String ACTOR_BREAK = "513894964ddecb0dd8705153";
	public final static String ACTOR_ACTION = "513894bf4ddecb0dd8705154";
	public final static String ACTOR_ASSIGNED = "516e6a7a7097d30d5c98b556";
	public final static String ACTOR_NOT_IN = "516e6b057097d30d5c98b557";
	public final static String ACTOR_DELAYED = "516ea6877097d31744404b30";

	public final static String[] actorStatusIds =
			new String[] { ACTOR_AVAILABLE, ACTOR_ACTIVE, ACTOR_IDLE,
					ACTOR_LUNCH, ACTOR_BREAK, ACTOR_ACTION,
					ACTOR_ASSIGNED, ACTOR_NOT_IN, ACTOR_DELAYED };

	public final static String[] actorStatusNames =
			new String[] { "Available", "Active", "Idle",
					"Lunch", "Break", "ERROR",
					"Assigned", "Not In", "Delayed" };

	public final static String ACTION_UNASSIGNED = "50d184ad3990411630e42bd5";
	public final static String ACTION_ASSIGNED = "50f55167ec02555879567fdd";
	public final static String ACTION_ACTIVE = "513d9e5c4ddecb1cf0917f25";
	public final static String ACTION_COMPLETED = "513d9e794ddecb1cf0917f26";
	public final static String ACTION_DELAYED = "513d9e994ddecb1cf0917f27";
	public final static String ACTION_CANCELLED = "513d9eb04ddecb1cf0917f28";

	public final static String[] actionStatusIds =
			new String[] { ACTION_UNASSIGNED, ACTION_ASSIGNED, ACTION_ACTIVE,
					ACTION_COMPLETED, ACTION_DELAYED, ACTION_CANCELLED };

	public final static String[] actionStatusNames =
			new String[] { "Unassigned", "Assigned", "Active",
					"Completed", "Delayed", "Cancelled" };

	public String findActorStatusId(String actionStatus) {
		int i = getStatus(actorStatusIds, actionStatus);
		if (i > -1)
			return actorStatusIds[i];
		else
			L.out("didnt find in json: " + actionStatus);
		return null;
	}

	public String findActionStatusId(String json) {
		int i = getStatus(actionStatusIds, json);
		if (i > -1)
			return actionStatusIds[i];
		else
			L.out("didnt find in json: " + json);
		return "None";
	}

	public String getIdForStatusName(String statusName) {
		int index = getIndex(statusName);
		L.out("index: " + index + " statusName: " + statusName);
		if (index != -1) {
			L.out("getIdForStatusName: " + actorStatusIds[index]);
			return actorStatusIds[index];
		}
		return null;
	}

	public String getIdForActionStatusName(String statusName) {
		int index = getIndex(statusName);
		L.out("index: " + index + " statusName: " + statusName);
		if (index != -1) {
			L.out("getIdForActionStatusName: " + actionStatusIds[index]);
			return actionStatusIds[index];
		}
		return null;
	}

	public int getActionIndex(String statusName) {

		for (int i = 0; i < actionStatusNames.length; i++) {
			if (actionStatusNames[i].equals(statusName)) {
				return i;
			}
		}
		return -1;
	}

	public int getIndex(String statusName) {

		for (int i = 0; i < actorStatusNames.length; i++) {
			if (actorStatusNames[i].equals(statusName)) {
				return i;
			}
		}
		return -1;
	}

	private int getStatus(String[] statusIds, String json) {
		if (json == null)
			return -1;
		for (int i = 0; i < statusIds.length; i++) {
			if (json.contains(statusIds[i]))
				return i;
		}
		return -1;
	}

	public String findActorStatusName(String flowId) {
		int i = getStatusName(actorStatusIds, flowId);
		if (i > -1)
			return actorStatusNames[i];
		else
			L.out("didnt find flowId: " + flowId);
		return null;
	}

	public String findActionStatusName(String flowId) {
		int i = getStatusName(actionStatusIds, flowId);
		if (i > -1)
			return actionStatusNames[i];
		else if (flowId != null)
			L.outp("didnt find flowId: " + flowId);
		return null;
	}

	private int getStatusName(String[] statusIds, String statusId) {
		if (statusId == null)
			return -1;
		for (int i = 0; i < statusIds.length; i++) {
			if (statusId.equals(statusIds[i]))
				return i;
		}
		return -1;
	}

}
