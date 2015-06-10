package com.ii.mobile.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.flowing.SyncCallback;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.transport.R; // same package
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class TitleFragment extends Fragment implements SyncCallback {
	public final static String FRAGMENT_TAG = "titleFragment";
	private LinearLayout ll;
	private TextView titleView = null;
	private TextView actionView = null;
	private final BroadcastReceiver receiver = null;
	private boolean updating = false;
	public static TitleFragment titleFragment = null;
	private static String HAVE_NETWORK = "#ffffff";
	private static String NO_NETWORK = "#ff9999";

	private static String NOT_UPDATING = "#ffffff";
	private static String UPDATING = "#aaaaaa";

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		ll = (LinearLayout) inflater.inflate(R.layout.frag_title, container, false);

		ll.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));

		titleView = (TextView) ll.findViewById(R.id.titleSelection);
		actionView = (TextView) ll.findViewById(R.id.titleAction);

		update(UpdateController.getActorStatus);
		titleFragment = this;
		return ll;
	}

	public void onReceive(WifiManager wifiManager) {
		int numberOfLevels = 5;
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
		int level = WifiManager.calculateSignalLevel(wifiInfo.getRssi(), numberOfLevels);
		System.out.println("Bars =" + level);
	}

	private void setUpdatingColor(final boolean updating) {
		L.out("updating: " + updating);
		if (getActivity() == null)
			return;

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (updating)
					actionView.setTextColor(Color.parseColor(UPDATING));
				else
					actionView.setTextColor(Color.parseColor(NOT_UPDATING));
			}
		});
	}

	@SuppressWarnings("unused")
	private void setInternetColor(final boolean onOrOff) {
		if (getActivity() == null)
			return;
		// MyToast.show("set internet color: " + onOrOff);
		final boolean newOnOrOff = isConnectedToInternet(getActivity());
		// MyToast.show("set internet color newOnOrOff: " + newOnOrOff);

		getActivity().runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (newOnOrOff)
					actionView.setTextColor(Color.parseColor(HAVE_NETWORK));
				else
					actionView.setTextColor(Color.parseColor(NO_NETWORK));
			}
		});
	}

	public static boolean isConnectedToInternet(Context context) {
		// L.out("activity: " + context);
		if (context == null) {
			L.out("no context yet");
			return false;
		}

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		boolean connected = info != null && info.isConnected();

		return connected;
	}

	public void setTitle(String title) {
		new UpdateDisplay().execute(null, title);
	}

	@Override
	public void onResume() {
		super.onResume();
		setUpdatingColor(updating);
		UpdateController.INSTANCE.registerCallback(this, FlowRestService.GET_ACTOR_STATUS);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		UpdateController.INSTANCE.unRegisterCallback(this, FlowRestService.GET_ACTOR_STATUS);
		if (receiver != null && getActivity() != null)
			getActivity().unregisterReceiver(receiver);
	}

	public void update(boolean updating) {
		this.updating = updating;
		// L.out("update: " + UpdateController.getActorStatus );
		setUpdatingColor(updating);
		update(UpdateController.getActorStatus);
	}

	public void update(GetActorStatus getActorStatus) {
		if (getActorStatus == null || getActorStatus.getActorStatusId() == null)
			return;
		String temp = null;
		if (UpdateController.getActionStatus != null)
			temp = StaticFlow.INSTANCE.findActorStatusName(UpdateController.getActionStatus.getActionStatusId());
		L.out("temp: " + temp);
		if (getActorStatus.getActorStatusId() != null
				&& getActorStatus.getActorStatusId().equals(StaticFlow.ACTOR_ASSIGNED)
				&& UpdateController.getActionStatus != null
				&& temp != null 
				&& ((temp.equals("Lunch") || temp.equals("Break"))))
			new UpdateDisplay().execute(StaticFlow.INSTANCE.findActionStatusName(UpdateController.getActionStatus.getActionStatusId()), null);
		else {
			String status = StaticFlow.INSTANCE.findActorStatusName(getActorStatus.getActorStatusId());
			if (status != null)
				new UpdateDisplay().execute(status, null);
			else
				L.out("Unable to find status for: " + getActorStatus.getActorStatusId());
		}
	}

	class UpdateDisplay extends AsyncTask<String, Integer, Long> {
		String status = null;
		String title = null;

		@Override
		protected Long doInBackground(String... params) {
			status = params[0];
			title = params[1];
			if (status == null)
				return 0l;

			return 0l;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			L.out("probably not used but executes in UI thread");
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Long l) {
			if (titleView != null && title != null)
				titleView.setText(title);
			if (actionView != null && status != null) {
				actionView.setText(status);
				if (User.getUser() != null) {
					String userName = User.getUser().getUsername();
					((TextView) ll.findViewById(R.id.userName)).setText(userName);
				}
			}
		}
	}

	@Override
	public void callback(GJon gJon, String payloadName) {
		L.out("callback: " + gJon.getClass().getSimpleName());
		update(UpdateController.getActorStatus);
	}

}