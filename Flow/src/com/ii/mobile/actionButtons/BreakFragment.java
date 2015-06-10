package com.ii.mobile.actionButtons;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.flowing.SyncCallback;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.home.LoginActivity;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.tab.StatusType;
import com.ii.mobile.timers.Swoosh;
import com.ii.mobile.timers.Ticker;
import com.ii.mobile.transport.R;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

/**

 */
public class BreakFragment extends ActionFragment implements NamedFragment, SyncCallback {
	public final static String FRAGMENT_TAG = "breakFragment";

	public static String AVAILABLE = "Available";
	public static String ACTIVE = "Active";
	public static String ASSIGNED = "Assigned";
	public static String DELAYED = "Delayed";
	public static String AT_LUNCH = "Lunch";
	public static String ON_BREAK = "Break";
	public static String NOT_IN = "Not In";

	public static StatusType[] statusTypes = new StatusType[] {
			new StatusType(AVAILABLE, "1"),
			new StatusType(AT_LUNCH, "5"),
			new StatusType(ON_BREAK, "6"),
			new StatusType(NOT_IN, "7"),
	};

	private View topLevelView = null;

	private Swoosh breakSwoosh = null;

	private static final String TITLE = "On Break";
	public static final int BACKGROUND = R.drawable.frag_blue_swoosh;
	private static final int SHORT_BREAK_TIME = 15 * 60;
	private static final String SHORT_BREAK_NAME = "Break";
	private static final String LUNCH_BREAK_NAME = "Lunch";
	private static final int LONG_BREAK_TIME = 30 * 60;

	private final boolean demoMode = false;
	// private static Bundle myBundle = new Bundle();

	private String lastStatus = null;

	private boolean paused;
	SharedPreferences preferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.out("onCreate");
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		preferences = activity.getSharedPreferences("timer", Context.MODE_PRIVATE);
		L.out("onAttach");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		L.out("onDetach");
	}

	@Override
	protected Bundle getBundle() {
		return UpdateController.breakBundle;
	}

	private void initClock() {
		// String employeeStatus =
		// UpdateController.INSTANCE.statusWrapper.currentStatus.employeeStatus;
		if (StaticFlow.ACTOR_BREAK.equals(UpdateController.getActorStatus.getActorStatusId())) {
			breakSwoosh.setTitle(SHORT_BREAK_NAME);
			startClock(SHORT_BREAK_TIME);
		} else {
			startClock(LONG_BREAK_TIME);
			breakSwoosh.setTitle(LUNCH_BREAK_NAME);
		}
	}

	public void startClock(int breakTime) {
		L.out("breakTime: " + breakTime);
		breakSwoosh.setTarget(breakTime);
		breakSwoosh.setForecast(breakTime);
		breakSwoosh.initArrived();
		breakSwoosh.setStarted(true);
		breakSwoosh.setBackground(BACKGROUND);
		breakSwoosh.setWantSubLabel(false);
		Ticker.INSTANCE.register(breakSwoosh, activity, 0l);
	}

	private void addLongClick() {
		// L.out("addedLongClick: " + addedLongClick);
		// MyToast.show("Long click is enabled");
		topLevelView.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				// L.out("long view: " + view);
				Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(200);

				if (Ticker.INSTANCE.demoMode)
					MyToast.show("Demo Mode Ended");
				else
					MyToast.show("Demo Mode Started");
				Ticker.INSTANCE.demoMode = !Ticker.INSTANCE.demoMode;
				return true;
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		visible = true;

		this.activity = getActivity();
		topLevelView = inflater.inflate(R.layout.frag_break, container, false);
		SharedPreferences settings = activity.getSharedPreferences(User.PREFERENCE_FILE, 0);
		boolean staffUser = settings.getBoolean(LoginActivity.STAFF_USER, false);
		if (staffUser)
			addLongClick();
		// L.out("onCreateView topLevelView: " + topLevelView);
		breakSwoosh = (Swoosh) topLevelView.findViewById(R.id.breakSwoosh);
		// Ticker.INSTANCE.register(breakSwoosh, getActivity());
		// initClock();
		Button shortBreakButton = (Button)
				topLevelView.findViewById(R.id.shortBreakButton);
		shortBreakButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onShortBreak(v);
				setPosition();
			}
		});

		Button lunchButton = (Button)
				topLevelView.findViewById(R.id.lunchBreakButton);
		lunchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onLunchBreak(v);
				setPosition();
			}
		});

		Button finishButton = (Button)
				topLevelView.findViewById(R.id.completeBreakButton);
		finishButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onFinishBreakClick(v);
				setPosition();
			}
		});

		update();
		// L.out("created view: ");
		// UpdateController.INSTANCE.registerCallback(this,
		// FlowRestService.GET_ACTOR_STATUS);
		onRestoreInstanceState(UpdateController.breakBundle);
		return topLevelView;
	}

	private static int delayTime = 5 * 60;
	private static String DELAY_NAME = "Delayed";

	private final String START_TIME = "startTime";
	private final String DELAY_TIME = "DelayTime";
	private final String STARTED = "started";

	private void onRestoreInstanceState(Bundle savedInstanceState) {

		// L.out("onRestoreInstanceState: " + savedInstanceState);
		if (savedInstanceState != null && savedInstanceState.getLong(START_TIME) != 0L) {
			Ticker.INSTANCE.register(delaySwoosh, getActivity(), savedInstanceState.getLong(START_TIME));
			delayTime = (int) savedInstanceState.getLong(DELAY_TIME);
			// L.out("DELAY_TIME: " + DELAY_TIME);

			delaySwoosh.setTitle(savedInstanceState.getString(DELAY_NAME));
			delaySwoosh.setTarget(delayTime);
			delaySwoosh.setStarted(true);
			delaySwoosh.setForecast(delayTime);
			delaySwoosh.initArrived();
			delaySwoosh.setBackground(BreakFragment.BACKGROUND);
			delaySwoosh.setWantSubLabel(false);
		}
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		// L.out("onSaveInstanceState: " + bundle + " " + delaySwoosh + L.p());

		super.onSaveInstanceState(bundle);
		// L.out("onSaveInstanceState: " + bundle + " " + delaySwoosh);
		if (delaySwoosh != null) {
			bundle.putLong(START_TIME, delaySwoosh.startTime);
			bundle.putBoolean(STARTED, delaySwoosh.started);
			bundle.putString(DELAY_NAME, delaySwoosh.title);
			bundle.putString(DELAY_NAME, delaySwoosh.title);
			// L.out("bundle: " + bundle);
		}
	}

	private void onFinishBreakClick(View v) {
		setEmployeeStatus(StaticFlow.ACTOR_AVAILABLE, false);
		// ((Cache) getActivity()).updateStatusTitle();
		// ((Cache) activity).update();
		Ticker.INSTANCE.unregister(breakSwoosh);
	}

	private void onShortBreak(View v) {
		// L.out("onShortBreak: " + v);
		setEmployeeStatus(StaticFlow.ACTOR_BREAK, false);
		delayTime = SHORT_BREAK_TIME;
		// ((Cache) getActivity()).updateStatusTitle();
		startClock(SHORT_BREAK_TIME);
		// ((Cache) activity).update();
	}

	private void onLunchBreak(View v) {
		L.out("onLunchBreak: " + v);
		setEmployeeStatus(StaticFlow.ACTOR_LUNCH, false);
		// ((Cache) getActivity()).updateStatusTitle();
		delayTime = LONG_BREAK_TIME;
		startClock(LONG_BREAK_TIME);
		// ((Cache) activity).update();
	}

	@Override
	public String getTitle() {
		return "Not Used";
	}

	@Override
	public void update() {
		if (topLevelView == null)
			return;
		LinearLayout takeBreakLayout = (LinearLayout) topLevelView.findViewById(R.id.takeBreakLayout);
		LinearLayout finishBreakLayout = (LinearLayout) topLevelView.findViewById(R.id.finishBreakLayout);

		GetActorStatus getActorStatus = UpdateController.getActorStatus;
		if (getActorStatus != null && lastStatus != null
				&& lastStatus.equals(getActorStatus.getActorStatusId()))
			return;
		if (getActorStatus != null)
			lastStatus = getActorStatus.getActorStatusId();
		if (getActorStatus == null
				|| getActorStatus.getActorStatusId() == null
				|| getActorStatus.getActorStatusId().equals(StaticFlow.ACTOR_AVAILABLE)) {
			takeBreakLayout.setVisibility(View.VISIBLE);
			finishBreakLayout.setVisibility(View.GONE);
		}
		else {

			takeBreakLayout.setVisibility(View.GONE);
			finishBreakLayout.setVisibility(View.VISIBLE);
			initClock();
		}
	}

	@Override
	public void onPause() {
		L.out("onPause");
		super.onPause();
		paused = true;
		// UpdateController.INSTANCE.unRegisterCallback(this,
		// FlowRestService.GET_ACTOR_STATUS);
	}

	@Override
	public void onResume() {
		super.onResume();
		L.out("onResume");
		UpdateController.INSTANCE.registerCallback(this, FlowRestService.GET_ACTOR_STATUS);
		paused = false;
		update();

	}

	// @Override
	// public void onDestroy() {
	// onSaveInstanceState(UpdateController.breakBundle);
	// super.onDestroy();
	//
	// }

	@Override
	public void callback(GJon gJon, String payloadName) {
		update();
	}

	@Override
	public View getTopLevelView() {
		return topLevelView;
	}
}
