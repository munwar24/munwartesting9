package com.ii.mobile.timers;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;

import com.ii.mobile.home.MyToast;
import com.ii.mobile.util.L;

public enum Ticker implements Runnable {
	INSTANCE;

	private final int UPDATE_SECONDS = 1;
	private static final int SPEED_UP = 5;
	public boolean demoMode = false;

	private final List<Clock> clocks = new ArrayList<Ticker.Clock>();

	private Thread updateThread;
	private Activity activity;

	@Override
	public void run() {

		L.out("starting Timer every " + UPDATE_SECONDS + " second");

		if (demoMode)
			MyToast.show("Demo Mode At " + SPEED_UP + " Times Speed");

		while (true) {
			L.sleep(UPDATE_SECONDS * 1000);

			if (activity != null)
				activity.runOnUiThread(new Runnable() {
					@Override
					public void run() {
						for (Clock clock : clocks) {

							int elapsedTime = (int) (new GregorianCalendar().getTimeInMillis() - clock.getStartTime()) / 1000;
							if (demoMode)
								elapsedTime = elapsedTime * SPEED_UP;

							boolean completed = clock.update(elapsedTime);
							if (completed && demoMode) {
								// clock.swoosh.setStart(false);
								// clock.setStartTime(new
								// GregorianCalendar().getTimeInMillis());
							}
						}
					}
				});

		}
		// showToast("Finished Demo " + maxCount + " Iterations");
		// demoMode = false;
	}

	public Clock register(Swoosh swoosh, Activity activity, long startTime) {
		this.activity = activity;
		L.out("registering a clock: ");
		Clock clock = haveClock(swoosh);
		if (clock != null) {
			L.out("Already contains the clock: " + swoosh);
			clock.setStartTime(new GregorianCalendar().getTimeInMillis());
			swoosh.startTime = clock.getStartTime();
			return clock;
		}
		clock = new Clock(swoosh);
		clocks.add(clock);
		if (startTime == 0l)
			clock.setStartTime(new GregorianCalendar().getTimeInMillis());
		else
			clock.setStartTime(startTime);
		swoosh.startTime = clock.getStartTime();
		clock.update(0l);
		if (updateThread == null) {
			updateThread = new Thread(this);
			updateThread.start();
		}
		return clock;
	}

	public void unregister(Swoosh swoosh) {
		removeClock(swoosh);
	}

	private boolean removeClock(Swoosh swoosh) {
		for (Clock clock : clocks) {
			if (clock.swoosh == swoosh) {
				clocks.remove(clock);
				return true;
			}
		}
		return false;
	}

	private Clock haveClock(Swoosh swoosh) {
		for (Clock clock : clocks) {
			if (clock.swoosh == swoosh) {
				return clock;
			}
		}
		return null;
	}

	public class Clock {
		private long startTime;
		private final Swoosh swoosh;

		public Clock(Swoosh swoosh) {
			this.swoosh = swoosh;
		}

		public long getStartTime() {
			return startTime;
		}

		public boolean update(long elapsedTime) {
			// swoosh.setArrived((int) elapsedTime);
			return swoosh.update(elapsedTime);
		}

		private void setStartTime(long startTime) {
			this.startTime = startTime;
		}
	}
}
