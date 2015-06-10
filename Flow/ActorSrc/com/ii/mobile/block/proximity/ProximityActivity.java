package com.ii.mobile.block.proximity;

import android.app.Activity;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager.WakeLock;
import android.view.Window;
import android.view.WindowManager;

import com.ii.mobile.transport.R; // same package
import com.ii.mobile.bus.Binder;
import com.ii.mobile.util.L;

public class ProximityActivity extends Activity {

	Binder binder = null;
	private WakeLock wakeLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		L.out("created black activity");

		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.black_screen);

		// Turn off the key-guard!
		KeyguardManager keyguardManager = (KeyguardManager) getApplicationContext().getSystemService(Context.KEYGUARD_SERVICE);
		KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("TAG");
		keyguardLock.disableKeyguard();

		// Don't allow display to sleep!
		// PowerManager pm = (PowerManager)
		// getApplicationContext().getSystemService(Context.POWER_SERVICE);
		// wakeLock = pm.newWakeLock((PowerManager.SCREEN_BRIGHT_WAKE_LOCK
		// | PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP),
		// "TAG");
		// wakeLock.acquire();

		// doBindService();

		binder = new Binder(this, incomingMessenger, ProximityService.class);
	}

	final Messenger incomingMessenger = new Messenger(new IncomingHandler());

	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			Bundle bundle = message.getData();
			boolean far = bundle.getBoolean(ProximityService.FAR);

			L.out("far: " + far);
			// sendMessageToService("Yahoo");
			if (far)
				finish();
		}
	}

	@Override
	public void onDestroy() {
		L.out("onDestroy");
		binder.onDestroy();
		// wakeLock.release();
		// doUnbindService();
		super.onDestroy();
	}
}
