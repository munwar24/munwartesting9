package com.ii.mobile.block.proximity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import com.ii.mobile.bus.BindService;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.util.L;

public class ProximityService extends BindService implements SensorEventListener {
	private SensorManager mSensorManager;

	boolean blackScreen = false;
	private float threshold;
	private boolean closeEnough = false;

	private final boolean enable = true;

	public static final String BRIGHTNESS = "brightness";
	public static final String FAR = "far";

	// public static boolean isRunning;

	@Override
	public void onCreate() {
		super.onCreate();

		// isRunning = true;
	}

	@Override
	protected void sideEffectRegisterClient() {
		if (!closeEnough)
			sendMessage(true);
	}

	private void sendMessage(boolean far) {
		if (!far)
			return;
		Bundle bundle = new Bundle();
		bundle.putBoolean(FAR, far);
		sendMessage(bundle);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// Toast.makeText(this, "started proximity service",
		// Toast.LENGTH_LONG).show();
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		if (mSensorManager == null || mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY) == null) {
			MyToast.show("Your device doesn't have a proximity sensor!");
			return;
		}
		if (!enable) {
			MyToast.show("Proximity sensor turned off!");
			return;
		}
		MyToast.show("Put in pocket to turn off display");
		@SuppressWarnings("unused")
		Sensor mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

		// threshold = mProximity.getMaximumRange() / 2.0f;
		threshold = mProximity.getMaximumRange() * .9f;
		mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_NORMAL);
		super.onStart(intent, startId);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		L.out("accuracyChanged: " + accuracy);
	}

	@Override
	public void onSensorChanged(SensorEvent event) {

		float values[] = event.values;

		closeEnough = (values[0] > threshold ? false : true);
		L.out("closeEnough: " + closeEnough);

		if (closeEnough) {
			if (!blackScreen) {
				blackScreen = true;
				// Toast.makeText(this, "Go away, you are too close!",
				// Toast.LENGTH_LONG).show();
				Intent intent = new Intent(getApplicationContext(), ProximityActivity.class);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				// L.out("intent: " + intent);
				startActivity(intent);
			}
		} else {
			sendMessage(true);
			blackScreen = false;
		}
	}

	@Override
	public void onDestroy() {
		L.out("on onDestroy");
		// Toast.makeText(this, "stopped", Toast.LENGTH_LONG).show();
		mSensorManager.unregisterListener(this);
		// isRunning = false;
		super.onDestroy();
	}
}
