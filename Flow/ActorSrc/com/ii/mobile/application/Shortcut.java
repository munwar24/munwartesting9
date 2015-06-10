package com.ii.mobile.application;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;

import com.ii.mobile.transport.R; // same package
import com.ii.mobile.users.User;

public enum Shortcut {
	INSTANCE;
	private final String PREFERENCE_FILE = User.PREFERENCE_FILE;
	private final String SHORTCUT_KEY = "shortcutKey";

	private final String APP_NAME = "Crothall\nTeamFlow";

	public void createShortCut(Activity activity) {
		SharedPreferences settings = activity.getSharedPreferences(PREFERENCE_FILE, 0);
		// boolean inserted = settings.getBoolean(SHORTCUT_KEY, false);
		// L.out("shortcut inserted: " + inserted);
		// if (inserted)
		// return;

		SharedPreferences.Editor editor = settings.edit();
		editor.putBoolean(SHORTCUT_KEY, true);
		editor.commit();

		removeShortCut(activity);
		addShortcut(activity);

		// Intent shortcutIntent;
		// shortcutIntent = new Intent();
		// L.out("shortcut: " + activity.getClass().getName());
		// shortcutIntent.setClassName("com.ii.mobile",
		// activity.getClass().getName());
		//
		// // shortcutIntent.setClassName("com.ii.mobile",
		// // "com.ii.mobile.home.LoginActivity");
		// // removeShortCut(activity, shortcutIntent);
		// shortcutIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
		// // shortcutIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
		// // Intent.FLAG_ACTIVITY_SINGLE_TOP);
		// Intent addIntent = new Intent();
		// addIntent
		// .putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		// addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "HelloWorldShortcut");
		// activity.sendBroadcast(shortcutIntent);
		// final Intent putShortCutIntent = new Intent();
		// putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT,
		// shortcutIntent);
		// putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, APP_NAME);
		// putShortCutIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
		// Intent.ShortcutIconResource.fromContext(activity, R.drawable.icon));
		// // putShortCutIntent.putExtra("duplicate", false);
		// //
		// putShortCutIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
		// // // this one doesn't seem to be working!
		// // activity.sendBroadcast(putShortCutIntent);
		// putShortCutIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		// activity.sendBroadcast(putShortCutIntent);
	}

	private void addShortcut(Activity activity) {
		// Adding shortcut for MainActivity
		// on Home screen
		Intent shortcutIntent = new Intent();
		shortcutIntent.setClassName("com.ii.mobile.transport", activity.getClass().getName());
		shortcutIntent.setAction(Intent.ACTION_MAIN);

		Intent addIntent = new Intent();
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, APP_NAME);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
				Intent.ShortcutIconResource.fromContext(activity,
						R.drawable.icon));
		addIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
		activity.sendBroadcast(addIntent);
	}

	private void removeShortCut(Activity activity) {
		Intent removeIntent = new Intent();
		removeIntent.setClassName("com.ii.mobile.transport", activity.getClass().getName());
		removeIntent.setAction(Intent.ACTION_MAIN);

		Intent addIntent = new Intent();
		addIntent
				.putExtra(Intent.EXTRA_SHORTCUT_INTENT, removeIntent);
		addIntent.putExtra(Intent.EXTRA_SHORTCUT_NAME, APP_NAME);

		addIntent.setAction("com.android.launcher.action.UNINSTALL_SHORTCUT");
		activity.sendBroadcast(addIntent);

	}
}
