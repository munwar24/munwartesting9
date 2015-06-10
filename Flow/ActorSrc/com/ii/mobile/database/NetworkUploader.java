/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.database;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ii.mobile.tickle.Tickler;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

/**
 * 
 * @author kfairchild
 */
public class NetworkUploader implements Runnable {

	private static Context context;
	private static List<AbstractDbAdapter> abstractDbAdapters = new ArrayList<AbstractDbAdapter>();
	private Thread thread = null;
	private boolean lastConnection = false;
	private static NetworkUploader networkUploader = null;
	// private static AbstractDbAdapter abstractDbAdapter = null;
	private final Object mPauseLock = new Object();
	private boolean mPaused = false;
	private final int NO_CONNECTION_WAIT = 1000;

	// public static final int EXAMPLE = 0;
	// public static final int ANOTHER_EXAMPLE = 1;

	public static NetworkUploader register(AbstractDbAdapter abstractDbAdapter, Context context) {
		abstractDbAdapters.add(abstractDbAdapter);
		L.out("abstractDbAdapters: " + abstractDbAdapters);
		networkUploader = startNetworkUploader(context);
		return networkUploader;
	}

	public static NetworkUploader startNetworkUploader(Context context) {
		if (networkUploader != null) {
			return networkUploader;
		}
		networkUploader = new NetworkUploader(context);

		return networkUploader;
	}

	public static NetworkUploader getNetworkUploader() {
		return networkUploader;
	}

	private NetworkUploader(Context context) {
		NetworkUploader.context = context;
		this.thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		L.out("starting NetworkUploader!");
		// L.out("start activity: " + context);

		while (true) {
			boolean uploading = false;
			if (checkConnection()) {
				// make sure list doesn't change in the loop
				List<AbstractDbAdapter> temp = new ArrayList<AbstractDbAdapter>(abstractDbAdapters);

				for (AbstractDbAdapter dbAdapter : temp) {
					if (dbAdapter.uploadIfNeeded()) {
						L.out("dbAdapter: " + dbAdapter);
						uploading = true;
					}
				}
				L.out("uploading: " + uploading);
				if (!uploading) {
					// L.sleep(NOTHING_TO_DO_WAIT);
					Tickler.onResume();
					onPause();
					// L.out("mPaused: " + mPaused);
					synchronized (mPauseLock) {
						while (mPaused) {
							try {
								mPauseLock.wait();
							} catch (InterruptedException e) {
							}
						}
						// L.out("running mPaused: " + mPaused);
					}
				}
			} else {
				L.sleep(NO_CONNECTION_WAIT);
			}
		}
	}

	/**
	 * Call this on pause.
	 */
	public void onPause() {
		// L.out("onPause");
		synchronized (mPauseLock) {
			mPaused = true;
		}
	}

	/**
	 * Call this on resume.
	 */
	public void onResume() {
		// L.out("onResume");
		synchronized (mPauseLock) {
			mPaused = false;
			Tickler.onPause();
			mPauseLock.notifyAll();
		}
	}

	private boolean checkConnection() {
		if (!isConnectedToInternet() || User.getUser() == null) {
			if (lastConnection) {
				L.out("No connection to internet");
				// MyToast.show("Network unreachable\nData will be saved and\nuploaded automatically\nto Crothall");
				lastConnection = false;
			}
			L.sleep(NO_CONNECTION_WAIT);
		} else {
			if (!lastConnection) {
				L.out("lastConnection: " + lastConnection);
				// MyToast.show("Network Reachable\nUploading data to Crothall");
			}
			lastConnection = true;
		}
		return lastConnection;
	}

	private static boolean externalLastConnection = true;

	public static boolean isConnectedToInternet() {
		// L.out("activity: " + context);
		if (context == null) {
			L.out("no context yet");
			return false;
		}

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		boolean connected = info != null && info.isConnected();
		if (!connected)
			if (externalLastConnection) {
				// MyToast.show("No connection to Internet");
			}
		externalLastConnection = connected;
		return connected;
	}
}
