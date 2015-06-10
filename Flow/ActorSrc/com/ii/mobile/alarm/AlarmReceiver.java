package com.ii.mobile.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

import com.ii.mobile.tickle.TickleService;
import com.ii.mobile.util.L;

public class AlarmReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Alarm");
		wakeLock.acquire();

		// L.out("TickleService: " + TickleService.isRunning());
		if (!TickleService.isRunning()) {
			L.out("Started TickleService: " + TickleService.isRunning());
			Intent scheduledIntent = new Intent(context, TickleService.class);
			context.startService(scheduledIntent);
		}
		wakeLock.release();
	}
}