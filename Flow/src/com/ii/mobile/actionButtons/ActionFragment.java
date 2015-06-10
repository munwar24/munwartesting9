package com.ii.mobile.actionButtons;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ii.mobile.flow.staticFlow.CancelReasons;
import com.ii.mobile.flow.staticFlow.StaticDelayTypes;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flowing.FlowBinder;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.flowing.SyncCallback;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.timers.Swoosh;
import com.ii.mobile.timers.Ticker;
import com.ii.mobile.transport.R;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

// same package

/**
 * Ting9641 ddd
 */
public class ActionFragment extends DataFragment implements NamedFragment, SyncCallback {
	public final static String FRAGMENT_TAG = "actionFragment";
	// private static final String DELAY_START_TEXT = "Start";
	// private static final String DELAY_DELAY_TEXT = "Delay";
	private static final String START_TASK = "Start Action";
	private static final String COMPLETE_TASK = "Complete To Available";
	// private static final String FINISH_DELAY = "Finish Delay";

	private static final int DELAY_TIME = 5 * 60;
	private static final String DELAY_NAME = "Delayed";

	private final String START_TIME = "startTime";
	private final String STARTED = "started";

	public boolean visible = false;

	// public final String[] completeStates = new String[] {
	// BreakFragment.AVAILABLE, BreakFragment.ON_BREAK,
	// BreakFragment.AT_LUNCH, BreakFragment.NOT_IN };

	public final String[] completeStates = new String[] { BreakFragment.AVAILABLE, BreakFragment.ON_BREAK,
			BreakFragment.AT_LUNCH };
	private View view;
	protected FragmentActivity activity = null;
	protected Swoosh delaySwoosh;
	private static Bundle myBundle = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.out("onCreateView actionFragment: ");
		if (myBundle == null)
			myBundle = new Bundle();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		activity = getActivity();

		L.out("onCreateView actionFragment: ");
		view = inflater.inflate(R.layout.frag_actions, container, false);
		L.out("onCreateView view: " + view);
		delaySwoosh = (Swoosh) view.findViewById(R.id.delaySwoosh);

		final Button delayButton = (Button) view.findViewById(R.id.actionDelayButton);
		// here
		delayButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onDelayClick(v);
				setPosition();
			}
		});

		final Button completeOtherButton = (Button) view.findViewById(R.id.actionCompleteOtherButton);
		completeOtherButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onCompleteOtherClick(v);
				setPosition();
			}
		});

		final Button completeButton = (Button) view.findViewById(R.id.actionCompleteButton);
		completeButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (completeButton.getText().equals(START_TASK)) {
					onStartClick(v);
				} else
					onCompleteClick(v);
				setPosition();
			}
		});

		final Button cancellationButton = (Button) view.findViewById(R.id.actionRequestCancellationButton);
		cancellationButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				onCancelClick(v);
				setPosition();
			}
		});

		final Button completeDelayButton = (Button) view.findViewById(R.id.actionCompleteDelayButton);
		completeDelayButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				onStartClick(v);
				setPosition();
			}
		});
		L.out("registered actionFragment: ");
		UpdateController.INSTANCE.registerCallback(this, FlowRestService.GET_ACTOR_STATUS);
		onRestoreInstanceState(myBundle);
		update();
		return view;
	}

	private void onRestoreInstanceState(Bundle savedInstanceState) {
		// L.out("onRestoreInstanceState: " + savedInstanceState + L.p());
		if (savedInstanceState != null) {
			Ticker.INSTANCE.register(delaySwoosh, getActivity(), savedInstanceState.getLong(START_TIME));
			// L.out("DELAY_TIME: " + DELAY_TIME);
			delaySwoosh.setTitle(DELAY_NAME);
			delaySwoosh.setTarget(DELAY_TIME);
			delaySwoosh.setStarted(true);
			delaySwoosh.setForecast(DELAY_TIME);
			delaySwoosh.initArrived();
			delaySwoosh.setBackground(BreakFragment.BACKGROUND);
			delaySwoosh.setWantSubLabel(false);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		// L.out("onSaveInstanceState: " + bundle + " " + delaySwoosh);
		if (delaySwoosh != null) {
			bundle.putLong(START_TIME, delaySwoosh.startTime);
			bundle.putBoolean(STARTED, delaySwoosh.started);
		}
	}

	public void onCompleteOtherClick(View w) {
		final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item, completeStates);
		arrayAdapter.setDropDownViewResource(R.layout.list_item);
		new AlertDialog.Builder(getActivity())
				.setTitle(getString(R.string.completeOther))
				.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						L.out("which: " + which);
						String completeTo = arrayAdapter.getItem(which);
						L.out("completeTo: " + completeTo);
						// String statusId =
						// StaticFlow.INSTANCE.findActorStatusId(completeTo);
						// setEmployeeStatus(statusId, true);
						// UpdateController.getActionStatus.setCompleteToId(completeTo);
						if (completeTo.equals(BreakFragment.AVAILABLE)) {
							completeTo = null;
							L.out("completeTo set to null: " + completeTo);
						}
						setActionStatus(StaticFlow.ACTION_COMPLETED, completeTo, false);
						dialog.dismiss();
						setPosition();
					}
				}).create().show();
	}

	public void onStartClick(View w) {
		setActionStatus(StaticFlow.ACTION_ACTIVE, null, false);
		update();
	}

	public void onDelayClick(View w) {
		final ArrayAdapter<String> arrayAdapter =
				new ArrayAdapter<String>(getActivity(), R.layout.list_item, StaticDelayTypes.INSTANCE.delayTypeNames);
		arrayAdapter.setDropDownViewResource(R.layout.list_item);
		new AlertDialog.Builder(getActivity())
				.setTitle(getString(R.string.delayReason))
				.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						L.out("which: " + which);
						String delayReason = arrayAdapter.getItem(which);
						L.out("delayReason: " + delayReason);
						String delayId = StaticDelayTypes.INSTANCE.findDelayId(delayReason);
						L.out("delayId: " + delayId);
						// task.setDelayType(delayID);
						// User.getUser().getValidateUser().setTaskStatus(TaskFragment.DELAYED);
						// L.out("User.getUser().getValidateUser().getTaskStatus: "
						// + User.getUser().getValidateUser().getTaskStatus());
						// UpdateController.getActionStatus.setDelayTypeId(delayId);
						setActionStatus(StaticFlow.ACTION_DELAYED, delayId, false);

						// ((Cache) activity).updateStatusTitle();
						update();
						dialog.dismiss();
						setPosition();
					}
				}).create().show();
	}

	public void onCancelClick(View w) {
		final ArrayAdapter<String> arrayAdapter =
				new ArrayAdapter<String>(getActivity(), R.layout.list_item, CancelReasons.INSTANCE.getCancelReasons());
		arrayAdapter.setDropDownViewResource(R.layout.list_item);
		new AlertDialog.Builder(getActivity())
				.setTitle(getString(R.string.cancelReason))
				.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						L.out("which: " + which);
						String cancelReason = arrayAdapter.getItem(which);
						L.out("delayReason: " + cancelReason);
						String cancelReasonId = CancelReasons.INSTANCE.getId(cancelReason);
						L.out("cancelReasonId: " + cancelReasonId);
						// task.setDelayType(delayID);
						// User.getUser().getValidateUser().setTaskStatus(TaskFragment.DELAYED);
						// L.out("User.getUser().getValidateUser().getTaskStatus: "
						// + User.getUser().getValidateUser().getTaskStatus());
						// UpdateController.getActionStatus.setCancelTypeId(cancelReasonId);
						setActionStatus(StaticFlow.ACTION_CANCELLED, cancelReasonId, false);
						update();
						dialog.dismiss();
						setPosition();
					}
				}).create().show();
	}

	private void onCompleteClick(View v) {
		L.out("onCompleteClick: " + v);
		// setEmployeeStatus(StaticFlow.ACTOR_AVAILABLE, false);
		setActionStatus(StaticFlow.ACTION_COMPLETED, null, false);
	}

	// private void onCancellationClick(View v) {
	// L.out("onCancellationClick: " + v);
	// setActionStatus(StaticFlow.ACTION_CANCELLED, null, false);
	// }

	@Override
	public String getTitle() {
		return "Action Home";
	}

	private String getActorStatusFromActionStatus(GetActionStatus getActionStatus, String optionId) {

		L.out("optionId: " + optionId);
		String actionStatusId = getActionStatus.getActionStatusId();
		L.out("getActionStatus.getActionStatusId(): " + getActionStatus.getActionStatusId());
		if (actionStatusId.equals(StaticFlow.ACTION_COMPLETED)) {
			if (optionId == null)
				return StaticFlow.ACTOR_AVAILABLE;
			String statusId = StaticFlow.INSTANCE.getIdForStatusName(optionId);
			L.out("statusId: " + statusId + " " + StaticFlow.INSTANCE.findActorStatusName(statusId));
			if (statusId == null)
				return StaticFlow.ACTOR_AVAILABLE;
			return statusId;
			// L.out("optionId: " + optionId);
			// if (optionId.equals(BreakFragment.ON_BREAK))
			// return StaticFlow.ACTOR_BREAK;
			// if (optionId.equals(BreakFragment.AT_LUNCH))
			// return StaticFlow.ACTOR_LUNCH;
			// if (optionId.equals(BreakFragment.NOT_IN))
			// // return StaticFlow.ACTOR_NOT_IN;
			// return StaticFlow.ACTOR_AVAILABLE;

		}
		L.out("actionStatusId: " + actionStatusId);
		// not sure what use is actor_action but using for error

		if (actionStatusId.equals(StaticFlow.ACTION_ASSIGNED))
			return StaticFlow.ACTOR_ASSIGNED;
		if (actionStatusId.equals(StaticFlow.ACTION_ACTIVE))
			return StaticFlow.ACTOR_ACTIVE;

		if (actionStatusId.equals(StaticFlow.ACTION_DELAYED))
			return StaticFlow.ACTOR_DELAYED;

		if (actionStatusId.equals(StaticFlow.ACTION_CANCELLED))
			return StaticFlow.ACTOR_AVAILABLE;
		return StaticFlow.ACTOR_ACTION;
	}

	private void setActionStatus(final String statusId, String optionId, boolean tickled) {
		L.out("optionId: " + optionId);
		GetActionStatus getActionStatus = UpdateController.getActionStatus;
		if (getActionStatus == null) {
			MyToast.show("Ignoring null Action operation!");
			return;
		}
		GetActorStatus getActorStatus = UpdateController.getActorStatus;
		getActionStatus.setActionStatusId(statusId, optionId);
		getActorStatus.setActionStatusId(statusId);
		getActorStatus.setActorStatusId(getActorStatusFromActionStatus(getActionStatus, optionId));
		// L.out("getActorStatus: " + getActorStatus);
		getActionStatus.tickled = GJon.FALSE_STRING;
		getActorStatus.tickled = GJon.TRUE_STRING;
		// if (optionId != null)
		// getActorStatus.tickled = GJon.FALSE_STRING;
		if (statusId.equals(StaticFlow.ACTION_COMPLETED)
				&& (optionId != null && optionId != BreakFragment.AVAILABLE))
			getActorStatus.tickled = GJon.FALSE_STRING;
		FlowBinder.updateLocalDatabase(FlowRestService.GET_ACTION_STATUS, getActionStatus);
		FlowBinder.updateLocalDatabase(FlowRestService.GET_ACTOR_STATUS, getActorStatus);

		if (statusId.equals(StaticFlow.ACTION_COMPLETED)
				|| statusId.equals(StaticFlow.ACTION_CANCELLED)) {
			L.out("removing action!");
			UpdateController.getActionStatus = null;
			getActorStatus.setActionId(null);
			// }
		}

		UpdateController.INSTANCE.callback(UpdateController.getActorStatus, FlowRestService.GET_ACTOR_STATUS);
		if (getActorStatus.getActorStatusId().equals(StaticFlow.ACTOR_NOT_IN)) {
			User.getUser().setValidateUser(null);

			activity.finish();
		}
	}

	private void updateComplete(GetActorStatus actorStatus) {
		Button completeOtherButton = (Button) view.findViewById(R.id.actionCompleteOtherButton);
		if (actorStatus.getActorStatusId().equals(StaticFlow.ACTOR_ACTIVE)) {
			completeOtherButton.setEnabled(true);
			completeOtherButton.setTextColor(Color.WHITE);
		}
		else {
			completeOtherButton.setEnabled(false);
			completeOtherButton.setTextColor(Color.LTGRAY);
		}
	}

	public void updateFragment() {
		// L.out("update ActionFragmentController: " +
		// UpdateController.getActorStatus);
		GetActorStatus getActorStatus = UpdateController.getActorStatus;

		if (getActorStatus == null || getActorStatus.getActionId() == null) {
			// L.out("setBreak");
			setBreakFragment();

		} else {
			// L.out("setAction");
			setActionFragment();
		}
	}

	private void setBreakFragment() {
		View actionLayout = getActivity().findViewById(R.id.action_header);
		View breakLayout = getActivity().findViewById(R.id.break_header);
		if (actionLayout != null)
			actionLayout.setVisibility(View.GONE);
		if (breakLayout != null)
			breakLayout.setVisibility(View.VISIBLE);
	}

	private void setActionFragment() {
		View actionLayout = getActivity().findViewById(R.id.action_header);
		View breakLayout = getActivity().findViewById(R.id.break_header);
		if (actionLayout != null)
			actionLayout.setVisibility(View.VISIBLE);
		if (breakLayout != null)
			breakLayout.setVisibility(View.GONE);
	}

	@Override
	public void update() {
		L.out("ActionFragment update: " + visible);
		// if (!visible)
		// return;
		if (getActivity() == null)
			return;
		updateFragment();
		GetActorStatus getActorStatus = UpdateController.getActorStatus;
		// L.out("getActorStatus: " + getActorStatus);
		if (getActorStatus == null || getActorStatus.getActionId() == null) {
			// L.out("getActorStatus has no action");
			return;
		}

		updateComplete(getActorStatus);

		Button completeButton = (Button) view.findViewById(R.id.actionCompleteButton);
		if (getActorStatus.getActionStatusId() == null)
			return;
		if (getActorStatus.getActionStatusId().equals(StaticFlow.ACTION_DELAYED)) {
			((LinearLayout) view.findViewById(R.id.actionLayout)).setVisibility(View.INVISIBLE);
			((LinearLayout) view.findViewById(R.id.finishDelayLayout)).setVisibility(View.VISIBLE);
			if (!delaySwoosh.started) {
				startClock();
			}

		} else {
			delaySwoosh.setStarted(false);
			((LinearLayout) view.findViewById(R.id.actionLayout)).setVisibility(View.VISIBLE);
			((LinearLayout) view.findViewById(R.id.finishDelayLayout)).setVisibility(View.INVISIBLE);
			if (getActorStatus.getActionStatusId().equals(StaticFlow.ACTION_ASSIGNED)) {
				completeButton.setText(START_TASK);
			} else {
				completeButton.setText(COMPLETE_TASK);
			}
		}
	}

	public void startClock() {
		L.out("DELAY_TIME: " + DELAY_TIME);
		delaySwoosh.setTitle(DELAY_NAME);
		delaySwoosh.setTarget(DELAY_TIME);
		delaySwoosh.setStarted(true);
		delaySwoosh.setForecast(DELAY_TIME);
		delaySwoosh.initArrived();
		delaySwoosh.setBackground(BreakFragment.BACKGROUND);
		delaySwoosh.setWantSubLabel(false);
		Ticker.INSTANCE.register(delaySwoosh, activity, 0l);
	}

	@Override
	public void onResume() {
		super.onResume();

	}

	protected Bundle getBundle() {
		return myBundle;
	}

	@Override
	public void onDestroy() {
		L.out("onDestroy: " + delaySwoosh);
		onSaveInstanceState(getBundle());
		super.onDestroy();
		UpdateController.INSTANCE.unRegisterCallback(this, FlowRestService.GET_ACTOR_STATUS);
		Ticker.INSTANCE.unregister(delaySwoosh);
	}

	@Override
	public void callback(GJon gJon, String payloadName) {
		// StatusWrapper statusWrapper = (StatusWrapper) gJon;
		// L.out("statusWrapper: " + statusWrapper.getClass().getSimpleName());
		update();
	}

	@Override
	public boolean wantActions() {
		return true;
	}

	@Override
	public View getTopLevelView() {
		return view;
	}
}
