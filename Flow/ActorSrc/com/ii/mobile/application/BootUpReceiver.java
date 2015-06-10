package com.ii.mobile.application;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.ii.mobile.home.LoginActivity;

public class BootUpReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent i = new Intent(context, LoginActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}

}