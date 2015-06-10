package com.ii.mobile.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.ii.mobile.transport.R; // same package
import com.ii.mobile.home.FragLoginActivity;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.tab.TabNavigationActivity;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class TransportNotificationService extends Service {

	private int NOTIF_ID;

	// Binder to given to WebWrapper activity
	// private final IBinder mBinder = new LocalBinder();

	/**
	
	 */
	/*
	 * public class LocalBinder extends Binder { StatusService getService() { //
	 * Return this instance of NotificationService so the WebWrapper activity
	 * can call its public methods return StatusService.this; } }
	 */

	@Override
	public IBinder onBind(Intent arg0) {
		L.out("IBinder: " + arg0);
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// Toast.makeText(this, "NotificationService created",
		// Toast.LENGTH_LONG).show();
		L.out("started notification service");
		createLaunchNotification();
		// createOut();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "NotificationService destroyed", Toast.LENGTH_LONG).show();
	}

	public void createLaunchNotification() {
		L.out("createLaunchNotification");
		final NotificationManager mNotifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// final CharSequence statusText = getText(R.string.im_notif);
		final CharSequence statusText = "TCFlow Started";
		Notification notifyDetails = new Notification(R.drawable.icon,
				statusText, System.currentTimeMillis());

		notifyDetails.flags |= Notification.FLAG_ONGOING_EVENT;
		// MyToast.show("user: " + User.getUser());
		// if (User.getUser() != null)
		// MyToast.show("user validate: " + User.getUser().getValidateUser());
		Intent notifyIntent = null;
		if (User.getUser() != null && User.getUser().getValidateUser() != null)
			notifyIntent = new Intent(this.getApplicationContext(),
					TransportActivity.class);
		else {
			L.out("applicationContent: " + this.getApplicationContext());
			L.out("loginActivity: " + FragLoginActivity.loginActivity);
			// notifyIntent = new Intent(this.getApplicationContext(),
			// FragLoginActivity.loginActivity.getClass());
			notifyIntent = new Intent(this.getApplicationContext(),
					FragLoginActivity.class);
		}

		final CharSequence contentTitle = getText(R.string.content_title);
		final CharSequence contentText = getText(R.string.content_text);

		PendingIntent intent = PendingIntent.getActivity(
				this,
				0,
				notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_SINGLE_TOP),
				PendingIntent.FLAG_CANCEL_CURRENT);
		notifyDetails.setLatestEventInfo(this, contentTitle, contentText,
				intent);

		mNotifManager.notify(NOTIF_ID, notifyDetails);
	}

	public void createOut() {
		L.out("createIMNotification");
		final NotificationManager mNotifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// final CharSequence statusText = getText(R.string.im_notif);
		final CharSequence statusText = "This is my message";
		Notification notifyDetails = new Notification(R.drawable.icon,
				statusText, System.currentTimeMillis());

		// notifyDetails.flags |= Notification.FLAG_ONGOING_EVENT;
		notifyDetails.flags = Notification.FLAG_AUTO_CANCEL;

		Intent notifyIntent = new Intent(this.getApplicationContext(),
				TabNavigationActivity.class);

		final CharSequence contentTitle = "this is the title";
		final CharSequence contentText = "this is the text";

		PendingIntent intent = PendingIntent.getActivity(
				this,
				0,
				notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_SINGLE_TOP),
				PendingIntent.FLAG_CANCEL_CURRENT);
		notifyDetails.setLatestEventInfo(this, contentTitle, contentText,
				intent);

		mNotifManager.notify(NOTIF_ID, notifyDetails);
	}

	public void createIMNotification() {
		L.out("createIMNotification");
		final NotificationManager mNotifManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		final CharSequence statusText = getText(R.string.im_notif);
		Notification notifyDetails = new Notification(R.drawable.icon,
				statusText, System.currentTimeMillis());

		// notifyDetails.flags |= Notification.FLAG_ONGOING_EVENT;
		notifyDetails.flags = Notification.FLAG_AUTO_CANCEL;

		Intent notifyIntent = new Intent(this.getApplicationContext(),
				TransportActivity.class);

		final CharSequence contentTitle = getText(R.string.content_title);
		final CharSequence contentText = getText(R.string.content_text);

		PendingIntent intent = PendingIntent.getActivity(
				this,
				0,
				notifyIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
						| Intent.FLAG_ACTIVITY_SINGLE_TOP),
				PendingIntent.FLAG_CANCEL_CURRENT);
		notifyDetails.setLatestEventInfo(this, contentTitle, contentText,
				intent);

		mNotifManager.notify(NOTIF_ID, notifyDetails);
	}

	public void createAlertNotification() {

	}

}