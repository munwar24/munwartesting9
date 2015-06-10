package com.ii.mobile.tickle;

import java.util.Map.Entry;
import java.util.Set;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;

import com.ii.mobile.bus.BindService;
import com.ii.mobile.util.L;

public class TickleService extends BindService {
	// public static final String FILENAME = "fileName";
	// public static final String URLPATH = "urlPath";
	// public static final String RESULTPATH = "urlPath";
	// private static int result = Activity.RESULT_OK;
	private static boolean isRunning = false;

	// // Used to receive messages from the Activity
	// private final Messenger inMessenger = new Messenger(new
	// IncomingHandler());
	// // Use to send message to the Activity
	// private static Messenger outMessenger = null;

	// private final IBinder mBinder = new MyBinder();

	public TickleService() {
		super();
		L.out("Started TickleService");
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
				.permitAll().build();
		StrictMode.setThreadPolicy(policy);
	}

	// class IncomingHandler extends Handler {
	//
	// @Override
	// public void handleMessage(Message msg) {
	// L.out("Got message: " + msg);
	// Bundle data = msg.getData();
	// String urlPath = data.getString(TickleService.URLPATH);
	// String fileName = data.getString(TickleService.FILENAME);
	// String outputPath = "message from activity: " + urlPath + " " + fileName;
	//
	// Message backMsg = Message.obtain();
	// backMsg.arg1 = TickleService.result;
	// Bundle bundle = new Bundle();
	// bundle.putString(RESULTPATH, outputPath);
	// // MyToast.show("sending: " + outputPath + " " + outMessenger);
	// sendMessage(backMsg);
	// // backMsg.setData(bundle);
	// // try {
	// // sendMessage(backMsg);
	// // } catch (android.os.RemoteException e1) {
	// // L.out("*** ERRROR: " + e1);
	// // }
	// // }
	// }
	// }

	public int sendMessage(ContentValues values) {
		// Message backMsg = Message.obtain();
		// backMsg.arg1 = result;
		Bundle bundle = new Bundle();
		Set<Entry<String, Object>> set = values.valueSet();
		for (Entry<String, Object> entry : set) {
			bundle.putString(entry.getKey(), (String) entry.getValue());
			// L.out("sending: " + entry.getKey() + " " + entry.getValue());
		}
		int clients = sendMessage(bundle);
		return clients;
		// // MyToast.show("sending: " + type + " " + value);
		// backMsg.setData(bundle);
		// try {
		// if (outMessenger != null) {
		// outMessenger.send(backMsg);
		// return true;
		// }
		// else
		// L.out("*** ERROR outMessenger is null!");
		// } catch (android.os.RemoteException e1) {
		// L.out("*** ERROR: " + e1);
		// }
		// return false;
	}

	// public void sendMessage(String type, String value) {
	// Message backMsg = Message.obtain();
	// backMsg.arg1 = result;
	// Bundle bundle = new Bundle();
	// bundle.putString(type, value);
	// // MyToast.show("sending: " + type + " " + value);
	// backMsg.setData(bundle);
	// try {
	// if (outMessenger != null)
	// outMessenger.send(backMsg);
	// else
	// L.out("*** ERROR outMessenger is null!");
	// } catch (android.os.RemoteException e1) {
	// L.out("*** ERROR: " + e1);
	// }
	// }

	@Override
	public void onCreate() {
		super.onCreate();
		L.out("TickleService started");
		isRunning = true;
		Tickler.startTickler(this, getApplicationContext());
		// inMessenger = new Messenger(new IncomingHandler());
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// L.out("Received start id " + startId + ": " + intent);
		return START_STICKY; // run until explicitly stopped.
	}

	public static boolean isRunning()
	{
		return isRunning;
	}

	// @Override
	// public IBinder onBind(Intent intent) {
	// Bundle extras = intent.getExtras();
	// // Get message from the Activity
	// // L.out("extras: " + extras);
	// if (extras != null) {
	// outMessenger = (Messenger) extras.get("MESSENGER");
	// L.out("binding: " + " " + outMessenger);
	// }
	// // Return our messenger to the Activity to get commands
	// return inMessenger.getBinder();
	// }

	@Override
	public void onDestroy() {
		L.out("onDestroy!");
		isRunning = false;
		super.onDestroy();
		// Toast.makeText(this, "My Service Stopped", Toast.LENGTH_LONG).show();
	}

	@Override
	public void onStart(Intent intent, int startid) {
		L.out("onStart!");
	}
}
