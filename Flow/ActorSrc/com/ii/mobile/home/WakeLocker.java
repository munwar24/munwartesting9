package com.ii.mobile.home;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

import com.ii.mobile.util.L;

public enum WakeLocker {
	INSTANCE;

	private WakeLock wakeLock = null;

	public void start(Activity activity) {
		// Turn off the key-guard!
		KeyguardManager keyguardManager = (KeyguardManager) activity.getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("DoNjfdhotDimScreenkeyguard");
		keyguardLock.disableKeyguard();

		// Don't allow display to sleep!
		PowerManager pm = (PowerManager) activity.getApplicationContext().getSystemService(Context.POWER_SERVICE);
		wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK
				| PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP), "DoNjfdhotDimScreen");
		L.out("aquiring wakeLock");
		wakeLock.acquire();
	}

	public void stop() {
		L.out("releasing wakeLock");
		try {
			if (wakeLock.isHeld())
				wakeLock.release();
		} catch (Exception e) {
			L.out("Failed to release lock: " + e);
		}
	}

}
