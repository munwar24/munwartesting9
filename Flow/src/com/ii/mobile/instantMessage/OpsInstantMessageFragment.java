package com.ii.mobile.instantMessage;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ii.mobile.flow.types.SendMessage;
import com.ii.mobile.flowing.FlowBinder;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

/**

 */
public class OpsInstantMessageFragment extends MessageFragment {
	public static MessageFragment messageFragment = null;
	private WifiManager mainWifiObj;
	// private WifiScanReceiver wifiReciever;
	private static List<Bundle> bundles = new ArrayList<Bundle>();

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		L.out("OpsInstantMessageFragment: " + bundle);
		// OpsInstantMessageFragment.opsInstantMessageFragment = this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		messageFragment = this;
		View view = super.onCreateView(inflater, container, bundle);
		L.out("savedInstanceState: " + bundle);
		L.out("restoreBundle: " + restoreBundle);
		onRestoreInstanceState(restoreBundle);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		L.out("onResume: " + bundles);
		if (getActivity() == null) {
			L.out("Unable to get activity for actionPager!");
			return;
		}
		for (Bundle bundle : bundles)
			receivedMessage(bundle);
		bundles.clear();
		// View view = getActivity().findViewById(R.id.actionPager);
		// if (view == null) {
		// L.out("Unable to get view for actionPager!");
		// return;
		// }
		// view.setVisibility(View.GONE);
	}

	@Override
	public void onDestroy() {
		messageFragment = null;
		super.onDestroy();
	}

	protected void onRestoreInstanceState(Bundle bundle) {
		L.out("onRestoreInstanceState: " + bundle);
		if (bundle != null) {
			String userName = bundle.getString(CURRENT_USER);
			L.out("userName: " + userName + " " + User.getUser().getUsername().equals(userName));
			if (bundle.getString(CURRENT_TEXT) != null)
				currentText = bundle.getString(CURRENT_TEXT);
			L.out("currentText: " + currentText);
			if (currentText == null || !User.getUser().getUsername().equals(userName))
				currentText = "";
			chatOutputWindow.setText(Html.fromHtml(currentText));
		}
	}

	@Override
	public void addDebugLongClick() {
		chatOutputWindow.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				// if (true)
				// return true;
				Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(1000);

				// mainWifiObj = (WifiManager)
				// getActivity().getSystemService(Context.WIFI_SERVICE);
				// wifiReciever = new WifiScanReceiver();
				// getActivity().registerReceiver(wifiReciever, new
				// IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
				// mainWifiObj.startScan();
				return true;
			}
		});
	}

	// class WifiScanReceiver extends BroadcastReceiver {
	// @Override
	// public void onReceive(Context c, Intent intent) {
	// L.out("gotit");
	// List<ScanResult> wifiScanList = mainWifiObj.getScanResults();
	// String[] wifis = new String[wifiScanList.size()];
	// for (int i = 0; i < wifiScanList.size(); i++) {
	// wifis[i] = ((wifiScanList.get(i)).toString());
	// printScanResult(wifiScanList.get(i));
	// }
	// getActivity().unregisterReceiver(wifiReciever);
	// }
	//
	// private void printScanResult(ScanResult scanResult) {
	// Bundle bundle = new Bundle();
	// WifiInfo foo = mainWifiObj.getConnectionInfo();
	// String tmp = "SSID: " + scanResult.SSID + " %: " + scanResult.level;
	// bundle.putString(Tickler.TEXT_MESSAGE, tmp);
	// bundle.putString(Tickler.RECEIVED_DATE, null);
	// bundle.putString(Tickler.FROM_USER_NAME, "");
	// receivedMessage(bundle);
	// }
	// }

	@Override
	protected void sendMessage(String message) {
		User user = User.getUser();
		String sentDate = L.toDateAMPM(new GregorianCalendar().getTimeInMillis());
		addMessage(user.getUsername(), sentDate, message, true);
		// String to = "kim.fairchild@iicorporate.com";
		String to = OPS;
		SendMessage sendMessage = new SendMessage(user.getUsername(), sentDate, message, to);
		FlowBinder.updateLocalDatabase(FlowRestService.SEND_MESSAGE, sendMessage);
		// Flow.getFlow().sendMessage(message, to);
	}

	@Override
	public String[] getDialog() {
		return SHORT_DIALOGUE;
	}

	public static final String[] SHORT_DIALOGUE =
	{
			"Your Flow welcomes you!"
	};

	@Override
	public void callback(GJon gJon, String payloadName) {
		L.out("callback: " + payloadName);
		// receiveMessage("I/O", gJon.getNewJson());
	}

	private static Bundle restoreBundle = new Bundle();

	@Override
	protected Bundle getBundle() {
		return restoreBundle;
	}

	public static void receivedMessage(Bundle bundle) {
		L.out("received message: " + bundle + " this: " + messageFragment);
		if (messageFragment == null) {
			L.out("*** ERROR No instantMessageFragment to receive message");
			bundles.add(bundle);
			return;
		}
		messageFragment.addMessage(bundle);
	}
}
