package com.ii.mobile.block.blocker;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.ii.mobile.block.PhoneNumber;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.util.L;

public class BlockService extends Service {

	private Blocker blocker = null;
	public static boolean running = false;

	@Override
	public IBinder onBind(Intent intent) {
		L.out("intent: " + intent);
		return null;
	}

	@Override
	public void onDestroy() {
		// Toast.makeText(this, "Stopped IntentBlock Service",
		// Toast.LENGTH_SHORT).show();
		MyToast.show("Stopped IntentBlock Service");
		if (blocker != null)
			blocker.stop();
		PhoneNumber.setBlockCalls(false);
		running = false;
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// Toast.makeText(this, "Started IntentBlock Service",
		// Toast.LENGTH_SHORT).show();

		// SharedPreferences settings =
		// getSharedPreferences(User.PREFERENCE_FILE, 0);
		// boolean staffUser = settings.getBoolean(LoginActivity.STAFF_USER,
		// false);
		// if (staffUser)
		// return;
		L.out("Started IntentBlock Service");
		blocker = new Blocker(this);
		PhoneNumber.setBlockCalls(true);
		running = true;
		super.onStart(intent, startId);
	}
}
