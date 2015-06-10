package com.ii.mobile.monitor;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ii.mobile.flow.types.Logger;
import com.ii.mobile.transport.R;
import com.ii.mobile.util.L;

public class NetworkMonitorFragment extends Fragment implements Runnable {

	private static final int TIME_TO_SLEEP = 5000;
	protected ViewGroup rootView;

	private Thread thread;
	protected TextView backgroundTextView;
	private boolean stopThread = false;
	private static boolean lastNetworkConnection;
	private static boolean lastIsReachable;
	private static boolean first = true;

	public NetworkMonitorFragment() {
		L.out("NetworkMonitorFragment");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.out("NetworkMonitorFragment");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = (ViewGroup) inflater.inflate(R.layout.network_monitor_layout, container, false);
		backgroundTextView = ((TextView) rootView.findViewById(R.id.versionMonitor));
		backgroundTextView.setText(getActivity().getResources().getString(R.string.crothall_version));

		return rootView;
	}

	@Override
	public void onResume() {
		L.out("onResume");
		super.onResume();
		stopThread = false;
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void onPause() {
		L.out("onPause");
		rootView = null;
		stopThread = true;
		super.onPause();
	}

	@Override
	public void onDestroy() {
		L.out("onDestroy");
		thread.interrupt();
		stopThread = true;
		super.onDestroy();
	}

	@Override
	public void run() {
		while (!stopThread) {
			update(getActivity());
			L.sleep(TIME_TO_SLEEP);
		}
		L.out("Interrupted.");
	}

	protected void update(FragmentActivity activity) {
		if (activity == null)
			return;
		boolean networkConnection = hasNetworkConnection(activity);
		boolean isReachable = isReachable(activity);
		int color = Color.parseColor("#FFFF0000");
		if (networkConnection)
			color = Color.parseColor("#FF0000FF");
		if (isReachable)
			color = Color.parseColor("#FFAAAAAA");
		setColor(color, activity);
		addToLog(networkConnection, isReachable);
	}

	protected void addToLog(boolean networkConnection, boolean isReachable) {
		if (first
				|| lastNetworkConnection != networkConnection
				|| lastIsReachable != isReachable)
			Logger.getLogger().networkStats.addNetworkState("network: " + networkConnection
					+ " reachable: " + isReachable);
		lastNetworkConnection = networkConnection;
		lastIsReachable = isReachable;
		first = false;
	}

	protected void setColor(final int color, FragmentActivity activity) {
		// L.out("setColor: " + color);
		activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (backgroundTextView != null)
					backgroundTextView.setTextColor(color);
			}
		});

	}

	protected boolean hasNetworkConnection(FragmentActivity activity) {
		if (activity == null)
			return false;
		final ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo netInfo = connMgr.getActiveNetworkInfo();

		if (netInfo != null && netInfo.isConnected())
			return true;
		return false;
	}

	protected boolean isReachable(FragmentActivity activity) {
		// First, check we have any sort of connectivity
		if (activity == null)
			return false;
		final ConnectivityManager connMgr = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		boolean isReachable = false;

		if (netInfo != null && netInfo.isConnected()) {
			// Some sort of connection is open, check if server is reachable
			try {
				URL url = new URL("http://www.google.com");
				// URL url = new URL("http://10.0.2.2");
				HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
				urlc.setRequestProperty("User-Agent", "Android Application");
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(2 * 1000);
				urlc.connect();
				isReachable = (urlc.getResponseCode() == 200);
			} catch (IOException e) {
				// Log.e(TAG, e.getMessage());
			}
		}
		return isReachable;
	}
}
