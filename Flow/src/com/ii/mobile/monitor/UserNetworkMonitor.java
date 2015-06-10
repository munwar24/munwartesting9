package com.ii.mobile.monitor;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ii.mobile.transport.R;

public class UserNetworkMonitor extends NetworkMonitorFragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout containing a title and body text.
		rootView = (ViewGroup) inflater.inflate(R.layout.user_network_monitor, container, false);
		backgroundTextView = ((TextView) rootView.findViewById(R.id.userName));

		return rootView;
	}

	@Override
	protected void update(FragmentActivity activity) {
		if (activity == null)
			return;
		int color = Color.parseColor("#FFFF0000");
		boolean networkConnection = hasNetworkConnection(activity);
		if (networkConnection)
			color = Color.parseColor("#FF0000FF");
		boolean isReachable = isReachable(activity);
		if (isReachable)
			color = Color.parseColor("#FF000000");
		setColor(color, activity);
		addToLog(networkConnection, isReachable);
	}
}
