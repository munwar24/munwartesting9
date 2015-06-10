package com.ii.mobile.home;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.StrictMode;

import com.crittercism.app.Crittercism;
import com.ii.mobile.util.L;

public class Critter {
	private static boolean inited = false;

	public static void makeInstance(Activity activity, String crittercismKey) {
		// TODO Auto-generated method stub
		// tell crittercism to collect logcat
		if (inited) {
			L.out("Ignoring: " + crittercismKey);
			return;
		}
		inited = true;
		L.out("Initing: " + crittercismKey);

		// // Turn off the key-guard!
		// KeyguardManager keyguardManager = (KeyguardManager)
		// activity.getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
		// KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
		// keyguardLock.disableKeyguard();
		//
		// // Don't allow display to sleep!
		// PowerManager pm = (PowerManager)
		// activity.getApplicationContext().getSystemService(Context.POWER_SERVICE);
		// WakeLock wakeLock =
		// pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK
		// | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP),
		// "DoNjfdhotDimScreen");
		// wakeLock.acquire();

		// don't give error if blocking UI thread. Should comment this out and
		// make sure not blocking!
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);

		if (crittercismKey == null)
			return;
		JSONObject crittercismConfig = new JSONObject();
		try
		{
			crittercismConfig.put("shouldCollectLogcat", true);
		} catch (JSONException je) {
			L.out("critter error: " + je);
		}

		// tell crittercism we want it!
		Crittercism.initialize(activity.getApplicationContext(), crittercismKey);
	}
}
