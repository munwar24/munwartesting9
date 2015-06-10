package com.ii.mobile.block.blocker;

import android.content.ContentResolver;
import android.content.Context;
import android.net.ConnectivityManager;

public enum AntiSync {
	INSTANCE;

	public void stopSync(Context context) {
		// boolean backGroundDataState = getBackgroundDataState(context);
		// ContentResolver.setMasterSyncAutomatically(true);
		// boolean sync = getSync(context);
		// MyToast.show("Before data: " + backGroundDataState + " sync: " +
		// sync);
		// ConnectivityManager connManager = (ConnectivityManager)
		// context.getSystemService(Context.CONNECTIVITY_SERVICE);
		// connManager.setBackgroundDataSetting(true);
		ContentResolver.setMasterSyncAutomatically(false);
		// backGroundDataState = getBackgroundDataState(context);
		// sync = getSync(context);
		// MyToast.show("After data: " + backGroundDataState + " sync: " +
		// sync);
	}

	@SuppressWarnings("unused")
	private static boolean getBackgroundDataState(Context context) {
		ConnectivityManager connManager = (ConnectivityManager)
				context.getSystemService(Context.CONNECTIVITY_SERVICE);
		return connManager.getBackgroundDataSetting();
	}

	/**
	 * Gets the state of auto-sync.
	 * 
	 * @param context
	 * @return true if enabled
	 */
	@SuppressWarnings("unused")
	private static boolean getSync(Context context) {
		boolean sync = ContentResolver.getMasterSyncAutomatically();
		return sync;
	}
}
