package com.ii.mobile.actionHistory;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;

import com.ii.mobile.flow.types.GetActionHistory;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.GetActionStatus.Targets;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.flowing.SyncCallback;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.selfAction.SelfActionFragment;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.transport.R;
import com.ii.mobile.util.L;

/**

 */
public class ActionHistoryFragment extends ListFragment implements NamedFragment, SyncCallback {

	private LinearLayout ll;

	private Activity activity;

	private boolean haveListAdapter = false;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		L.out("create view");
		ll = (LinearLayout) inflater.inflate(R.layout.transport_list_actions, container, false);
		return ll;
	}

	@Override
	public String getTitle() {
		return "Action History";
	}

	// @Override
	// public void onListItemClick(ListView l, View v, int position, long id) {
	// super.onListItemClick(l, v, position, id);
	// MyToast.show("click: " + position);
	// }

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		L.out("onActivityCreated");
		activity = getActivity();
		UpdateController.INSTANCE.registerCallback(this, FlowRestService.GET_ACTION_HISTORY);
	}

	private void setListAdapter() {
		if (haveListAdapter)
			return;
		Targets[] items = getActionList();
		if (items != null)
			L.out("items: " + items.length);
		if (items != null) {
			haveListAdapter = true;
			L.out("created listAdapter: " + items.length);
			setListAdapter(new ActionHistoryCursorAdapter((FragmentActivity)
					activity, this, R.layout.transport_list_actions, items));
			getListView().setDivider(null);
			getListView().setDividerHeight(0);
			getListView().setTextFilterEnabled(true);
			getListView().setFastScrollEnabled(true);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		UpdateController.INSTANCE.unRegisterCallback(this, FlowRestService.GET_ACTION_HISTORY);
	}

	static Targets[] getActionList() {
		GetActionHistory getActionHistory = UpdateController.getActionHistory;
		// L.out("getActionHistory: " + getActionHistory);
		if (getActionHistory == null) {
			L.out("ERROR: getActionHistory is null!");
			return new Targets[0];
		}

		//
		Targets[] items = getActionHistory.getTargets();
		L.out("items: " + items.length);

		return items;
	}

	@Override
	public void onResume() {
		super.onResume();

		if (getActivity() == null) {
			L.out("Unable to get activity for actionPager!");
			return;
		}
		GetActionStatus getActionHistory = UpdateController.getActionHistory;
		if (getActionHistory == null)
			return;
		L.out("actionSelect: " + getActionHistory.getTargets().length);
		View view = getActivity().findViewById(R.id.actionPager);
		if (view == null) {
			L.out("Unable to get view for actionPager!");
			return;
		}
		view.setVisibility(View.VISIBLE);
		update();
	}

	public void updateSelfActionFragment(GetActionStatus getActionStatus) {
		// Bundle bundle = newTask.getAllNamedValues();
		// SelfTaskFragment selfTaskFragment = (SelfTaskFragment)
		// getActivity().getSupportFragmentManager().findFragmentByTag(SelfTaskFragment.FRAGMENT_TAG);
		SelfActionFragment selfActionFragment = SelfActionFragment.selfActionFragment;
		if (selfActionFragment != null) {
			L.out("selfActionFragment not found");
			selfActionFragment.setAction(getActionStatus);
		} else {
			L.out("selfActionFragment not found");
		}
	}

	@Override
	public void update() {
		// L.out("obsolete update: " + L.p());
		setListAdapter();
		if (getListAdapter() != null)
			((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public void callback(GJon gJon, String payloadName) {
		// MyToast.show("callback: " + payloadName);
		setListAdapter();
		if (getListAdapter() != null)
			((ArrayAdapter<?>) getListAdapter()).notifyDataSetChanged();
	}

	@Override
	public boolean wantActions() {
		return true;
	}

	@Override
	public View getTopLevelView() {
		return null;
	}
}