package com.ii.mobile.tickle;

import java.util.Hashtable;

import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flowing.Flow;

public enum ActionWrapperCache {
	INSTANCE;
	Hashtable<String, GetActionStatus> actionCache = new Hashtable<String, GetActionStatus>();

	public GetActionStatus fetch(String actionId, boolean returnIt) {
		// L.out("getActionStatus: " + actionId);
		if (actionId == null)
			return null;
		if (actionCache.get(actionId) != null) {
			if (returnIt)
				return actionCache.get(actionId);
			return null;
		}
		// L.out("creating actionStatus");
		GetActionStatus actionStatus = Flow.getFlow().getActionStatus(actionId);
		// L.out("getActionStatus: " + actionCache.get(actionId));
		actionCache.put(actionId, actionStatus);
		return actionStatus;
	}
}
