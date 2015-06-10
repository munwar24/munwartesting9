package com.ii.mobile.alarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

public class Alarm {
	final public static String ONE_TIME = "onetime";

	public void resetAlarm(Activity activity) {
		cancelAlarm(activity);
		setAlarm(activity); //note
	}

	private void setAlarm(Context context)
	{
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		Intent intent = new Intent(context, AlarmReceiver.class);
		intent.putExtra(ONE_TIME, Boolean.FALSE);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 20, pendingIntent);
	}

	private void cancelAlarm(Context context)
	{
		Intent intent = new Intent(context, AlarmReceiver.class);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
		AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarmManager.cancel(pendingIntent);
	}
}
