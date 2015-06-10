package com.ii.mobile.timers;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.flowing.SyncCallback;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.home.LoginActivity;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

/**
 
 */
public class TimerFragment extends Fragment implements SyncCallback {
	// private final boolean demoMode = false;

	GetActorStatus lastGetActorStatus = null;

	public final static String FRAGMENT_TAG = "timerFragment";
	private Swoosh swooshOne;
	private Swoosh swooshTwo;
	private View view;

	// Swoosh One
	private final String TITLE = "Arrival";
	private final int TARGET = 10 * 60;
	// private final int ACTIVE = 2 * 60;
	private final int FORECAST = 10 * 60;
	private final int BACKGROUND = com.ii.mobile.transport.R.drawable.frag_red_swoosh;

	private final String DELAY_STARTS = "delayStarts";
	private final String DELAY_ENDS = "delayEnds";
	private final String COMPLETE_DELAY_STARTS = "completeDelayStarts";
	private final String COMPLETE_DELAY_ENDS = "completeDelayEnds";

	private final String START_TIME = "startTime";
	private final String COMPLETE_START_TIME = "completeStartTime";

	// Swoosh Two

	private final String COMPLETE_TITLE = "Complete";
	private final int COMPLETE_TARGET = 20 * 60;
	private final int COMPLETE_FORECAST = 20 * 60;
	private final int COMPLETE_HAND_COLOR = 0xaf006600;

	private final String ARRIVED = "arrived";

	private static final Bundle myBundle = new Bundle();

	// private final int UPDATE_SECONDS = 1;

	// private long startTime;

	// private Thread updateThread;
	// private static final float SPEED_UP = 60.0f;

	public TimerFragment() {
		L.out("finished create");
	}

	@Override
	public void onAttach(final Activity activity) {
		super.onAttach(activity);
		L.out("onAttach");
		// Thread thread = new Thread(this);
		// thread.start();
	}

	@Override
	public void onDestroy() {
		L.out("onDestroy");
		onSaveInstanceState(myBundle);
		super.onDestroy();

		Ticker.INSTANCE.unregister(swooshOne);
		Ticker.INSTANCE.unregister(swooshTwo);
		swooshOne.onDestroy();
		swooshTwo.onDestroy();
		UpdateController.INSTANCE.unRegisterCallback(this, FlowRestService.GET_ACTOR_STATUS);
	}

	private void addLongClick() {
		// L.out("addedLongClick: " + addedLongClick);
		// MyToast.show("Long click is enabled");
		view.setOnLongClickListener(new View.OnLongClickListener() {
			// LOOK HERE notice the flag on demo mode - remove for prod

			@Override
			public boolean onLongClick(View view) {
				// L.out("long view: " + view);
				Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(200);

				boolean demoMode = Ticker.INSTANCE.demoMode;
				// if (demoMode)
				// MyToast.show("Demo Mode Ended");
				// else
				// MyToast.show("Demo Mode Started");
				Ticker.INSTANCE.demoMode = !demoMode;
				return true;
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		L.out("Create TimerFragment");
		// if (container == null) {
		// return null;
		// }
		view = inflater.inflate(com.ii.mobile.transport.R.layout.frag_timer, container, false);
		swooshOne = (Swoosh) view.findViewById(com.ii.mobile.transport.R.id.swooshOne);
		swooshTwo = (Swoosh) view.findViewById(com.ii.mobile.transport.R.id.swooshTwo);
		swooshOne.setTitle(TITLE);
		swooshOne.setTarget(TARGET);
		swooshOne.setForecast(FORECAST);
		// swooshOne.setArrived(TARGET);
		swooshOne.setArrived();
		swooshOne.setBackground(BACKGROUND);

		swooshTwo.setTitle(COMPLETE_TITLE);
		swooshTwo.setTarget(COMPLETE_TARGET);
		// swooshOne.setArrived(TARGET);
		swooshOne.setArrived();
		swooshTwo.setForecast(COMPLETE_FORECAST);
		swooshTwo.setHandColor(COMPLETE_HAND_COLOR);

		swooshOne.update(0);
		swooshTwo.update(0);
		// init();
		SharedPreferences settings = getActivity().getSharedPreferences(User.PREFERENCE_FILE, 0);
		boolean staffUser = settings.getBoolean(LoginActivity.STAFF_USER, false);
		if (staffUser)
			addLongClick();
		// updateThread = new Thread(this);
		// updateThread.start();
		UpdateController.INSTANCE.registerCallback(this, FlowRestService.GET_ACTOR_STATUS);
		// Ticker.INSTANCE.demoMode = true;
		onRestoreInstanceState(myBundle);
		return view;
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		super.onSaveInstanceState(bundle);
		L.out("onSaveInstanceState");
		bundle.putString(TITLE, swooshOne.getTitle());
		bundle.putInt(TARGET + "", swooshOne.getTarget());
		bundle.putInt(FORECAST + "", swooshOne.getForecast());
		// bundle.putInt(BACKGROUND + "", swooshOne.getSwooshBackground());
		bundle.putInt(COMPLETE_HAND_COLOR + "", swooshOne.getHandcolor());
		bundle.putString(COMPLETE_TITLE, swooshTwo.getTitle());
		bundle.putInt(COMPLETE_TARGET + "", swooshTwo.getTarget());
		bundle.putInt(COMPLETE_FORECAST + "", swooshTwo.getForecast());
		bundle.putInt(COMPLETE_HAND_COLOR + "", swooshTwo.getHandcolor());
		bundle.putInt(BACKGROUND + "", swooshTwo.getHandcolor());
		// bundle.putString(TITLE, swooshTwo.getTitle());
		// bundle.putString(TITLE, swooshTwo.getTitle());

		bundle.putIntArray(DELAY_STARTS, getDelayStarts(swooshOne));
		bundle.putIntArray(DELAY_ENDS, getDelayEnds(swooshOne));

		bundle.putIntArray(COMPLETE_DELAY_STARTS, getDelayStarts(swooshTwo));
		bundle.putIntArray(COMPLETE_DELAY_ENDS, getDelayEnds(swooshTwo));

		bundle.putLong(START_TIME, swooshOne.startTime);
		bundle.putLong(COMPLETE_START_TIME, swooshTwo.startTime);

		bundle.putInt(ARRIVED, swooshOne.arrived);
	}

	private void onRestoreInstanceState(Bundle savedInstanceState) {
		L.out("onRestoreInstanceState: " + savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.getString(TITLE) != null) {
			swooshOne.init();
			swooshTwo.init();

			swooshOne.title = savedInstanceState.getString(TITLE);
			swooshOne.target = savedInstanceState.getInt(TARGET + "");
			swooshOne.forecast = savedInstanceState.getInt(FORECAST + "");
			swooshOne.swooshBackground = savedInstanceState.getInt(BACKGROUND + "");
			swooshOne.setHandColor(savedInstanceState.getInt(COMPLETE_HAND_COLOR + ""));

			swooshTwo.title = savedInstanceState.getString(COMPLETE_TITLE);
			swooshTwo.target = savedInstanceState.getInt(COMPLETE_TARGET + "");
			swooshTwo.forecast = savedInstanceState.getInt(COMPLETE_FORECAST + "");
			swooshTwo.swooshBackground = savedInstanceState.getInt(BACKGROUND + "");
			swooshTwo.setHandColor(savedInstanceState.getInt(COMPLETE_HAND_COLOR + ""));

			swooshOne.setDelays(swooshOne, savedInstanceState, DELAY_STARTS, DELAY_ENDS);
			swooshTwo.setDelays(swooshTwo, savedInstanceState, COMPLETE_DELAY_STARTS, COMPLETE_DELAY_ENDS);
			swooshOne.setStarted(true);
			swooshTwo.setStarted(true);

			swooshOne.arrived = savedInstanceState.getInt(ARRIVED);
			swooshTwo.arrived = savedInstanceState.getInt(ARRIVED);

			Ticker.INSTANCE.register(swooshOne, getActivity(), savedInstanceState.getLong(START_TIME));
			Ticker.INSTANCE.register(swooshTwo, getActivity(), savedInstanceState.getLong(COMPLETE_START_TIME));
		}
	}

	private int[] getDelayStarts(Swoosh swoosh) {
		int starts[] = new int[swoosh.delays.size()];
		for (int i = 0; i < swoosh.delays.size(); i++) {
			L.out("getDelayStarts: " + swoosh.delays.get(i));
			starts[i] = (int) swoosh.delays.get(i).startDelay;
		}
		return starts;
	}

	private int[] getDelayEnds(Swoosh swoosh) {
		int stops[] = new int[swoosh.delays.size()];
		for (int i = 0; i < swoosh.delays.size(); i++)
			stops[i] = (int) swoosh.delays.get(i).stopDelayed;
		return stops;
	}

	@Override
	public void onPause() {
		super.onPause();
		L.out("paused timer");
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public void startClock(boolean started) {
		L.out("started: " + started + " " + swooshOne.getStarted());
		if (swooshOne.getStarted())
			return;
		swooshOne.setTitle(TITLE);
		swooshOne.setTarget(TARGET);
		swooshOne.setForecast(FORECAST);
		// swooshOne.setArrived(ACTIVE);
		swooshOne.initArrived();
		swooshOne.setBackground(BACKGROUND);

		swooshTwo.setTitle(COMPLETE_TITLE);
		swooshTwo.setTarget(COMPLETE_TARGET);
		swooshTwo.setForecast(COMPLETE_FORECAST);
		swooshTwo.setHandColor(COMPLETE_HAND_COLOR);

		swooshOne.setStarted(started);
		swooshTwo.setStarted(started);

		// swooshOne.setArrived(TARGET);
		// swooshTwo.setArrived(TARGET);
		swooshTwo.initArrived();

		swooshOne.clearDelayed();
		swooshTwo.clearDelayed();

		swooshOne.update(0);
		swooshTwo.update(0);

		Ticker.INSTANCE.register(swooshOne, getActivity(), 0l);
		Ticker.INSTANCE.register(swooshTwo, getActivity(), 0l);
	}

	// private void init() {
	// swooshOne.setTitle(TITLE);
	// swooshOne.setTarget(TARGET);
	// swooshOne.setForecast(FORECAST);
	// swooshOne.setArrived(ACTIVE);
	// swooshOne.setBackground(BACKGROUND);
	//
	// swooshTwo.setTitle(COMPLETE_TITLE);
	// swooshTwo.setTarget(COMPLETE_TARGET);
	// swooshTwo.setForecast(COMPLETE_FORECAST);
	// swooshTwo.setHandColor(COMPLETE_HAND_COLOR);
	// }

	@SuppressWarnings("unused")
	private void randomInit() {
		swooshOne.setTarget(TARGET);
		swooshOne.setForecast(random(FORECAST));
		// int active = random(ACTIVE);
		// showToast("Random Active At " + active + " Seconds");
		// swooshOne.setArrived(active);
		swooshTwo.setTarget(COMPLETE_TARGET);
		swooshTwo.setForecast(random(COMPLETE_FORECAST));
	}

	private int random(int value) {
		final float percent = .20f;
		return (int) (value - value * percent + value * Math.random() * percent * 2);
	}

	// private void setStartTime(long startTime) {
	// this.startTime = startTime;
	// }

	// private long getStartTime() {
	// return startTime;
	// }

	public String getTitle() {
		return "No title for timer";
	}

	//
	// public void update() {
	// L.out("TimerFragment update");
	// }

	// public synchronized void
	// update(GetTaskInformationByTaskNumberAndFacilityID task) {
	// if (swooshOne == null) {
	// L.out("update hasn't inited yet");
	// return;
	// }
	// // startClock(false);
	// L.out("update: " + " current task: " + this.task
	// + ((task == null) ? "no task" : (task.getClassBrief() + " : " +
	// task.getTaskStatusBrief())));
	// if (this.task == null && task != null) {
	// GregorianCalendar now = new GregorianCalendar();
	// setStartTime(now.getTimeInMillis());
	// // taskInit();
	// startTask();
	// startClock(true);
	// setDemoMode(false);
	// } else if (task == null
	// || (task != null && task.getTaskStatusBrief().equals(Cache.COMPLETED))) {
	// swooshOne.setStarted(false);
	// swooshTwo.setStarted(false);
	// } else if (task != null &&
	// !task.getTaskStatusBrief().equals(Cache.ASSIGNED)) {
	// swooshOne.setStarted(false);
	// }
	// this.task = task;
	// }

	private synchronized void update() {
		if (UpdateController.getActorStatus == null)
			return;
		if (swooshOne == null) {
			L.out("update hasn't inited yet");
			return;
		}

		GetActorStatus getActorStatus = UpdateController.getActorStatus;

		// startClock(false);
		// L.out("update: " + " current task: " + this.task
		// + ((task == null) ? " no task" : (task.getClassBrief() + " : " +
		// task.getTaskStatusBrief())));

		if (getActorStatus.getActionStatusId() == null
				|| getActorStatus.getActionStatusId().equals(StaticFlow.ACTION_COMPLETED)
				|| getActorStatus.getActionStatusId().equals(StaticFlow.ACTION_CANCELLED)) {
			L.out("stop clock");
			swooshOne.setStarted(false);
			swooshTwo.setStarted(false);
			lastGetActorStatus = getActorStatus.clone();
			return;
		}

		if ((lastGetActorStatus == null || getActorStatus.getActionStatusId() != null)) {
			L.out("start clock");
			// GregorianCalendar now = new GregorianCalendar();
			// setStartTime(now.getTimeInMillis());
			// taskInit();
			startClock(true);
			if (getActorStatus.getActionStatusId() != null
					&& !getActorStatus.getActionStatusId().equals(StaticFlow.ACTION_ASSIGNED)) {
				L.out("make active immediately");
				activeAction();
			}
			// setDemoMode(true);
			// swooshOne.setStarted(true);
			// swooshTwo.setStarted(true);
			// swooshTwo.setStarted(false);
		}

		// if
		// (getActorStatus.getActionStatusId().equals(StaticFlow.ACTION_ACTIVE))
		// {
		// L.out("make active check if active already");
		// swooshOne.setStarted(false);
		// }

		if (lastGetActorStatus == null
				&& (getActorStatus.getActionStatusId().equals(StaticFlow.ACTION_ACTIVE)
				|| getActorStatus.getActionStatusId().equals(StaticFlow.ACTION_DELAYED))) {
			L.out("make active if got on load");
			activeAction();

			// } else if (statusWrapper.currentStatus.actionStatusId != null
			// &&
			// statusWrapper.currentStatus.actionStatusId.equals(StaticFlow.INSTANCE.ACTION_ASSIGNED))
			// {
			// swooshOne.setStarted(false);
		}
		// L.out("lastGetActorStatus: " + lastGetActorStatus);
		if (lastGetActorStatus != null) {

			if (getActorStatus.getActionStatusId().equals(StaticFlow.ACTION_DELAYED)
					&& !lastGetActorStatus.getActionStatusId().equals(StaticFlow.ACTION_DELAYED)) {
				L.out("start Delay");
				startDelay();
				swooshOne.printDelayed();
				swooshTwo.printDelayed();
			} else if (!getActorStatus.getActionStatusId().equals(StaticFlow.ACTION_DELAYED)
					&& StaticFlow.ACTION_DELAYED.equals(lastGetActorStatus.getActionStatusId())) {
				L.out("end delay");
				stopDelay();
				swooshOne.printDelayed();
				swooshTwo.printDelayed();
			}
		}
		lastGetActorStatus = getActorStatus.clone();
	}

	public void activeAction() {
		// GetActionStatus getActionStatus = UpdateController.getActionStatus;
		// L.out("getActorStatus: " + getActionStatus);
		// if (swooshOne.getArrived() != 0) {
		// L.out("ignoring activeAction");
		// }
		// GregorianCalendar now = new GregorianCalendar();
		// float elapsedTime = (now.getTimeInMillis() - getStartTime()) /
		// 1000.0f;
		if (swooshOne != null && swooshOne.getArrived() == 0) {
			swooshOne.setArrived();

			Ticker.INSTANCE.unregister(swooshOne);
			swooshTwo.setArrived();

		}
	}

	public void startDelay() {
		// taskInit();
		// GregorianCalendar now = new GregorianCalendar();
		// float elapsedTime = (now.getTimeInMillis() - getStartTime()) /
		// 1000.0f;
		L.out("startDelay");
		if (swooshOne != null)
			swooshOne.startDelayed();
		if (swooshTwo != null)
			swooshTwo.startDelayed();
	}

	public void stopDelay() {
		L.out("stopDelay: " + swooshOne + " " + swooshTwo);
		// taskInit();
		// GregorianCalendar now = new GregorianCalendar();
		// float elapsedTime = (now.getTimeInMillis() - getStartTime()) /
		// 1000.0f;
		if (swooshOne != null)
			swooshOne.stopDelayed();
		if (swooshTwo != null)
			swooshTwo.stopDelayed();
	}

	@Override
	public void callback(GJon gJon, String payloadName) {
		L.out("callback: ");
		update();
	}
}
