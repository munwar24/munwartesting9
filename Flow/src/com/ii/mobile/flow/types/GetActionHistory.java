package com.ii.mobile.flow.types;

import java.util.ArrayList;

import com.ii.mobile.flowing.FlowBinder;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.util.L;

public class GetActionHistory extends GetActionStatus {

	static public GetActionHistory getGJon(String json) {
		// L.out("GetActionStatus: " + "");
		// L.out("GetActionStatus: " + json);
		// PrettyPrint.prettyPrint(json, false);

		json = json.replace("\"isolation\":{},", "");
		GetActionHistory getActionHistory = (GetActionHistory) getJSonObjectArray(json, GetActionHistory.class);
		// getActionStatus.json = null;
		if (getActionHistory == null)
			return null;
		getActionHistory.getActionStatusInner.init();
		// L.out("output: " + getActionHistory.getTargets().length);
		getActionHistory.removeBreaks();
		getActionHistory.getNewJson();
		return getActionHistory;
	}

	private static final String LUNCH = "Lunch";
	private static final String BREAK = "Break";

	private void removeBreaks() {
		Targets[] targets = getTargets();
		ArrayList<Targets> temp = new ArrayList<Targets>();
		for (int i = 0; i < targets.length; i++) {
			Targets target = targets[i];
			if (!(target == null
					|| target.classType == null
					|| target.classType.name == null
					|| target.classType.name.equals(LUNCH)
					|| target.classType.name.equals(BREAK)))
				temp.add(target);
		}
		L.out("length: " + getActionStatusInner.targets.length);
		getActionStatusInner.targets = temp.toArray(new Targets[temp.size()]);
		L.out("after: " + getActionStatusInner.targets.length);
	}

	private final int MAX_ACTIONS = 25;

	private void add(Targets target) {
		Targets[] targets = getTargets();
		L.out("targets: " + target.actionNumber + " length: " + targets.length);
		int length = targets.length + 1;

		ArrayList<Targets> temp = new ArrayList<Targets>();
		if (length > MAX_ACTIONS) {
			for (Targets tmpTarget : targets)
				temp.add(tmpTarget);
			temp.add(target);
		} else {
			boolean first = true;
			for (Targets tmpTarget : targets)
				if (!first)
					temp.add(tmpTarget);
				else
					first = false;
			temp.add(target);
		}

		getActionStatusInner.targets = temp.toArray(new Targets[temp.size()]);
		L.out("targets: " + " length: " + targets.length);
	}

	private boolean alreadyHave(Targets target) {
		Targets[] targets = getTargets();
		L.out("targets: " + targets.length);
		for (int i = 0; i < targets.length; i++) {
			Targets historyTarget = targets[i];
			// L.out(i + " xtargets: " + historyTarget + " " +
			// target.actionNumber);
			if (historyTarget != null
					&& historyTarget.actionNumber != null
					&& historyTarget.actionNumber.equals(target.actionNumber)) {
				return true;
			}
		}
		L.out("didn't already have: " + target.actionNumber);
		return false;
	}

	public void reverseTargets() {
		// L.out("original");
		// printTargets();
		Targets[] targets = getTargets();
		Targets[] newTargets = new Targets[targets.length];
		for (int i = 0; i < targets.length; i++) {
			newTargets[i] = targets[targets.length - i - 1];
		}
		getActionStatusInner.targets = newTargets;
		// L.out("reversed");
		// printTargets();
	}

	public void printTargets() {
		Targets[] targets = getTargets();

		for (int i = 0; i < targets.length; i++) {
			Targets historyTarget = targets[i];
			L.out(i + " targets: " + historyTarget.actionNumber + " " + historyTarget.localActionId);
		}
	}

	public void addAction(GetActionStatus getActionStatus) {
		L.out("getActionStatus: " + getActionStatus.getActionNumber());
		if (alreadyHave(getActionStatus.getTarget()))
			return;
		if (!replaceTarget(getActionStatus)) {
			L.out("added getActionStatus: " + getActionStatus.getActionNumber());
			add(getActionStatus.getTarget());
		}
		FlowBinder.updateLocalDatabase(FlowRestService.GET_ACTION_HISTORY, this);
		UpdateController.INSTANCE.doCallback(this, FlowRestService.GET_ACTION_HISTORY);
	}

	private boolean replaceTarget(GetActionStatus getActionStatus) {
		Targets newTarget = getActionStatus.getTarget();
		// L.out("getActionStatus.getLocalActionId(): " +
		// getActionStatus.getLocalActionId());
		Targets[] targets = getTargets();
		L.out("targets: " + targets.length);
		for (int i = 0; i < targets.length; i++) {
			Targets historyTarget = targets[i];
			// L.out(i + " targets: " + historyTarget.localActionId + " " +
			// newTarget.localActionId);
			if (historyTarget != null
					&& historyTarget.localActionId != null
					&& historyTarget.localActionId.equals(newTarget.localActionId)) {
				L.out("replaced: " + newTarget.localActionId);
				targets[i] = newTarget;
				getActionStatusInner.targets = targets;
				// printTargets();
				return true;
			}
		}
		return false;
	}
}