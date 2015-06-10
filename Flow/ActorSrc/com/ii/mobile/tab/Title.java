package com.ii.mobile.tab;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ii.mobile.transport.R; // same package
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class Title {

	private final Activity activity;
	TextView textView = null;

	private BroadcastReceiver receiver = null;
	private RelativeLayout backgroundLayout = null;
	private TextView employeeTextStatus = null;
	private TextView taskTextStatus = null;
	// private static String HAVE_NETWORK = "#888888";
	private static String HAVE_NETWORK = "#FFFFFF";
	// private static String NO_NETWORK = "#997777";
	private static String NO_NETWORK = "#FFAAAA";

	// final TextView myTitleText;

	public Title(Activity activity, boolean customTitleSupported) {
		this.activity = activity;

		if (customTitleSupported) {
			activity.getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.titlebar);
		}

		// myTitleText = (TextView) activity.findViewById(R.id.branding);
		backgroundLayout = (RelativeLayout) activity.findViewById(R.id.backgroundColor);
		employeeTextStatus = (TextView) activity.findViewById(R.id.employeeStatus);
		taskTextStatus = (TextView) activity.findViewById(R.id.taskStatus);
		IntentFilter intentFilter = new IntentFilter("android.intent.action.SERVICE_STATE");
		receiver = new BroadcastReceiver() {
			@SuppressWarnings("unused")
			boolean last = true;

			@Override
			public void onReceive(Context context, Intent intent) {
				boolean onOff = isConnectedToInternet(context);
				// if (last != onOff)
				// L.out("Title AirplaneMode is " + (onOff ? "on" :
				// "off"));
				if (onOff) {
					// backgroundLayout.setBackgroundColor(Color.parseColor(HAVE_NETWORK));
					employeeTextStatus.setTextColor(Color.parseColor(HAVE_NETWORK));
					taskTextStatus.setTextColor(Color.parseColor(HAVE_NETWORK));
				}
				else {
					// backgroundLayout.setBackgroundColor(Color.parseColor(NO_NETWORK));
					employeeTextStatus.setTextColor(Color.parseColor(NO_NETWORK));
					taskTextStatus.setTextColor(Color.parseColor(NO_NETWORK));
				}

				last = onOff;
			}
		};
		activity.registerReceiver(receiver, intentFilter);
		update();

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

	void update() {
		User user = User.getUser();

		if (user.getValidateUser() == null)
			return;
		String employeeStatus = user.getValidateUser().getEmployeeStatus();
		if (employeeStatus == null)
			employeeStatus = BreakActivity.AVAILABLE;
		String taskStatus = "None";
		if (SelfTaskActivity.getTaskActivity() != null) {
			SelfTaskActivity.getTaskActivity();
			// L.out("Have activity");
			GetTaskInformationByTaskNumberAndFacilityID task = SelfTaskActivity.task;
			if (task != null)
				taskStatus = task.getTaskStatusBrief();
		}

		TextView textView;
		textView = (TextView) activity.findViewById(R.id.employeeStatus);
		textView.setText(user.getUsername() + ": " + employeeStatus);
		// textView.setTextColor(ColorCodes.getColor(employeeStatus));
		// textView.setTextColor(Color.WHITE);
		textView = (TextView) activity.findViewById(R.id.taskStatus);
		textView.setText("Task: " + taskStatus);
		// textView.setTextColor(ColorCodes.getColor(taskStatus));
		// textView.setTextColor(Color.WHITE);
	}

	public void onDestroy() {
		L.out("destroy receiver");
		try {
			if (receiver != null && activity != null)
				activity.unregisterReceiver(receiver);
			receiver = null;
		} catch (Exception e) {
		}
	}
}
