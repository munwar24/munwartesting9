/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.flow.db;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ii.mobile.flow.authenticate.Login;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.fragments.TitleFragment;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.tickle.Tickler;
import com.ii.mobile.util.L;

/**
 * 
 * @author kfairchild
 */
public class FlowUploader implements Runnable {

	private static Context context;
	private static List<AbstractFlowDbAdapter> abstractDbAdapters = new ArrayList<AbstractFlowDbAdapter>();
	private Thread thread = null;
	private boolean lastConnection = false;
	private static FlowUploader networkUploader = null;
	// private static AbstractDbAdapter abstractDbAdapter = null;
	private final Object mPauseLock = new Object();
	private boolean mPaused = false;
	private final int NO_CONNECTION_WAIT = 1000;

	// public static final int EXAMPLE = 0;
	// public static final int ANOTHER_EXAMPLE = 1;

	public static FlowUploader register(AbstractFlowDbAdapter abstractDbAdapter, Context context) {
		abstractDbAdapters.add(abstractDbAdapter);
		L.out("abstractDbAdapters: " + abstractDbAdapters);
		networkUploader = startNetworkUploader(context);
		return networkUploader;
	}

	public static FlowUploader startNetworkUploader(Context context) {
		if (networkUploader != null) {
			return networkUploader;
		}
		networkUploader = new FlowUploader(context);

		return networkUploader;
	}

	public static FlowUploader getNetworkUploader() {
		return networkUploader;
	}

	private FlowUploader(Context context) {
		FlowUploader.context = context;
		this.thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		L.out("starting NetworkUploader!");
		// L.out("start activity: " + context);

		while (true) {
			boolean uploading = false;
			if (checkConnection() && Login.authorization != null) {
				// make sure list doesn't change in the loop
				List<AbstractFlowDbAdapter> temp = new ArrayList<AbstractFlowDbAdapter>(abstractDbAdapters);

				for (AbstractFlowDbAdapter dbAdapter : temp) {
					if (dbAdapter.uploadIfNeeded()) {
						L.out("dbAdapter: " + dbAdapter);
						uploading = true;
					}
				}
				L.out("uploading: " + uploading);
				if (!uploading) {
					// MyToast.show("FlowUploader done uploading");
					// L.sleep(NOTHING_TO_DO_WAIT);
					if (TitleFragment.titleFragment != null)
						TitleFragment.titleFragment.update(false);
					onPause();

					GetActorStatus getActorStatus = UpdateController.getActorStatus;
					if (getActorStatus != null
							&& !getActorStatus.getActorStatusId().equals(StaticFlow.ACTOR_NOT_IN))
						Tickler.onResume();

					L.out("mPaused: " + mPaused);
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
				// if (TitleFragment.titleFragment != null)
				// TitleFragment.titleFragment.update(false);
			}
		}
	}

	public void onPause() {
		// L.out("onPause");
		synchronized (mPauseLock) {
			mPaused = true;
		}
	}

	public void onResume() {
		// L.out("onResume");
		synchronized (mPauseLock) {
			if (TitleFragment.titleFragment != null)
				TitleFragment.titleFragment.update(true);
			mPaused = false;
			Tickler.onPause();
			mPauseLock.notifyAll();
		}
	}

	private boolean checkConnection() {
		if (!isConnectedToInternet()) {
			if (lastConnection) {
				if (TransportActivity.showToast)
					MyToast.show("No Connection to Internet");
				// MyToast.show("Network unreachable\nData will be saved and\nuploaded automatically\nto Crothall");
				lastConnection = false;
				// if (TitleFragment.titleFragment != null)
				// TitleFragment.titleFragment.update();
			}
			L.sleep(NO_CONNECTION_WAIT);
		} else {
			if (!lastConnection) {
				L.out("lastConnection: " + lastConnection);
				if (TransportActivity.showToast)
					MyToast.show("Network Reachable\nUploading Data to Crothall");
				// if (TitleFragment.titleFragment != null)
				// TitleFragment.titleFragment.update();
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
