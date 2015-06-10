/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.tab;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import com.ii.mobile.home.LoginActivity;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.tickle.Tickler;
import com.ii.mobile.transport.R; // same package
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class IMActivity extends Activity {
	private Vibrator vibrator;
	private static IMActivity imActivity;
	private static final String MESSAGES = "messages";

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		L.out("creating IMActivity");
		IMActivity.imActivity = this;
		setContentView(R.layout.instant_message_view);
		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		SharedPreferences settings = getSharedPreferences(User.PREFERENCE_FILE, 0);
		boolean staffUser = settings.getBoolean(LoginActivity.STAFF_USER, false);
		if (staffUser)
			addLongClick();

		IntentFilter intentFilter = new IntentFilter("android.intent.action.SERVICE_STATE");

		BroadcastReceiver receiver = new BroadcastReceiver() {
			boolean last = true;

			@Override
			public void onReceive(Context context, Intent intent) {
				boolean onOff = isAirplaneModeOn(context);
				if (last != onOff)
					if (onOff)
						MyToast.show("AirplaneMode is " + (onOff ? "on" : "off"));
				last = onOff;
			}
		};
		registerReceiver(receiver, intentFilter);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		TextView textView = (TextView) findViewById(R.id.chatWindow);
		outState.putString(MESSAGES, (String) textView.getText());
	}

	@Override
	protected void onRestoreInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		String messages = outState.getString(MESSAGES);
		TextView textView = (TextView) findViewById(R.id.chatWindow);
		textView.setText(messages);
	}

	private void addLongClick() {
		TextView textView = (TextView) findViewById(R.id.chatWindow);

		// MyToast.show("Airplane Mode long click is enabled");
		textView.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {

				SharedPreferences settings = getSharedPreferences(User.PREFERENCE_FILE, 0);
				boolean staffUser = settings.getBoolean(LoginActivity.STAFF_USER, false);
				if (!staffUser)
					return false;
				vibrator.vibrate(400);
				sayClick(view);
				return true;
			}
		});
	}

	private static boolean isAirplaneModeOn(Context context) {
		// Toggle airplane mode.

		return Settings.System.getInt(context.getContentResolver(),
				Settings.System.AIRPLANE_MODE_ON, 0) != 0;

	}

	void sayClick(View view) {
		boolean isEnabled = Settings.System.getInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0) == 1;
		L.out("long view: " + view + " airplane is: " + isEnabled);
		Settings.System.putInt(getContentResolver(), Settings.System.AIRPLANE_MODE_ON, isEnabled ? 0 : 1);

		// Post an intent to reload.
		Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
		intent.putExtra("state", !isEnabled);

		MyToast.show("Airplane mode not available!");
		// sendBroadcast(intent);
	}

	void turnNetworkOnOff(boolean flag) {
		MyToast.show("network: " + flag);
		WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		wifiManager.setWifiEnabled(flag);
	}

	public static IMActivity getIMActivity() {
		return imActivity;
	}

	public static void receivedMessage(Bundle data) {
		if (imActivity == null) {
			L.out("*** ERROR No IMActivity to receive message");
			return;
		}
		imActivity.addMessage(data);
		// if (!SelfTaskActivity.isVisible()) {
		// TabNavigationActivity.tabHost.setCurrentTab(2);
		// }
	}

	private void addMessage(Bundle data) {
		String message = data.getString(Tickler.TEXT_MESSAGE);
		L.out("addMessage: " + message);
		String receivedDate = data.getString(Tickler.RECEIVED_DATE);
		L.out("received Date: " + receivedDate);
		String fromUserName = data.getString(Tickler.FROM_USER_NAME);
		TextView textView = (TextView) findViewById(R.id.chatWindow);
		textView.setText(textView.getText() + "\n " + parseDate(receivedDate) + " " + fromUserName + ": "
				+ message);
		AudioPlayer.INSTANCE.playSound(AudioPlayer.NEW_MESSAGE);
		// new AudioPlayer(this).playSound(AudioPlayer.NEW_MESSAGE);
	}

	private String parseDate(String receivedDate) {
		if (receivedDate == null)
			return "";
		Long temp = L.getLong(receivedDate);
		if (temp != 0)
			return L.toDateSecond(temp);
		int index = receivedDate.indexOf("T");
		if (index == -1)
			return receivedDate;
		int jindex = receivedDate.indexOf(".", index);
		if (jindex == -1)
			return receivedDate;
		return receivedDate.substring(index + 1, jindex);
	}

}
