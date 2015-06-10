package com.ii.mobile.block.blocker;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.ii.mobile.util.L;

public class Blocker {

	private static final int SLEEP_TIME = 5000;
	boolean running = true;

	private final Context context;
	String[] killList = {
			// "com.android.email",
			// "com.android.phone",
			"com.android.browser",
			// "com.android.contacts"
	};

	private BackGroundTask backGroundTask = null;

	// private final Thread thread;

	public Blocker(Context context) {
		this.context = context;
		L.out("Created Blocker");
		// thread = new Thread(this);
		// thread.start();
		backGroundTask = new BackGroundTask();
		backGroundTask.execute();
	}

	private class BackGroundTask extends AsyncTask<String, Integer, Long> {
		@Override
		protected Long doInBackground(String... urls) {
			Thread.currentThread().setName("IntentBlocker");
			run();
			return 0l;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {

		}

		@Override
		protected void onPostExecute(Long result) {
			L.out("finished");
		}
	}

	public void run() {
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		try {
			while (running) {
				blockActivities(activityManager);
				L.sleep(SLEEP_TIME);
			}

		} catch (Exception e) {
			L.out("error: " + e);
		}
	}

	private void blockActivities(ActivityManager activityManager) {
		// L.out("\nDump\n");
		List<RunningAppProcessInfo> apps = activityManager.getRunningAppProcesses();
		for (RunningAppProcessInfo appInfo : apps) {
			if (onKillList(appInfo.processName)) {
				// L.out("killing: " + appInfo.processName);
				Intent i = new Intent(context, KillActivity.class);
				i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				i.putExtra("pkg", appInfo.processName);
				context.startActivity(i);
			}
			// L.out("app: " + appInfo.processName + " on kill List: " +
			// onKillList(appInfo.processName));
		}
	}

	private boolean onKillList(String processName) {
		for (int i = 0; i < killList.length; i++) {
			if (processName.equals(killList[i]))
				return true;
		}
		return false;
	}

	public void stop() {
		L.out("Stop Blocker");
		running = false;
		// if (backGroundTask != null)
		// backGroundTask.cancel(true);
	}
}
