package com.ii.mobile.tab;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Vibrator;

import com.ii.mobile.transport.R; // same package
import com.ii.mobile.home.LoginActivity;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public enum AudioPlayer {
	INSTANCE;
	private Context context = null;

	private Vibrator vibrator = null;

	public static String ERROR = "Available";
	public static String NEW_TASK = "NewTask";
	public static String NEW_MESSAGE = "NewMessage";
	public static String OPS_MESSAGE = "OpsMessage";
	public static String DISPATCHER_CHANGE_STATE = "DispatcherChangeState";

	private MediaPlayer mp = null;

	public float percent = .75f;

	private boolean setup = false;

	// String resumeBreakType = AVAILABLE;

	public static AudioType[] audioTypes = new AudioType[] {
			new AudioType(ERROR, R.raw.bleep_bleep_bleep),
			new AudioType(NEW_TASK, R.raw.long_bleeps),
			// new AudioType(NEW_MESSAGE, R.raw.new_message),
			new AudioType(NEW_MESSAGE, R.raw.attention),
			new AudioType(OPS_MESSAGE, R.raw.byc2),
			new AudioType(DISPATCHER_CHANGE_STATE, R.raw.long_bleeps)
	};

	private AudioPlayer() {
		setUp(LoginActivity.loginActivity);
	}

	/**
	 * Used to play IM notification and alert sounds that are locally stored on
	 * the device.
	 * 
	 * @param sFile
	 */
	private void setUp(Context context) {
		if (setup)
			return;
		setup = true;
		// L.out("setup: " + context);
		this.context = context;
		AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		float maxSteamVolume =
				mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		float maxRingVolume =
				mAudioManager.getStreamMaxVolume(AudioManager.STREAM_RING);
		// int currentVolume =
		// mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);

		float currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_RING);
		int percent = (int) ((currentVolume / maxRingVolume) * maxSteamVolume);

		L.out("currentVolume: " + currentVolume + " " + maxSteamVolume + " " + maxRingVolume + " "
				+ percent);
		// if (percent < 75)
		// percent = 100;
		// percent = percent / 2;
		// percent = (int) (maxRingVolume / 2);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) (maxSteamVolume / 2), AudioManager.FLAG_VIBRATE);
		mAudioManager.setStreamVolume(AudioManager.STREAM_RING, (int) (maxRingVolume / 2), AudioManager.FLAG_VIBRATE);
		// mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume,
		// AudioManager.FLAG_SHOW_UI);
		// mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
		// scale(maxVolume, .75f), AudioManager.FLAG_VIBRATE);
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	}

	public void playSound(String audioFile) {
		setUp(LoginActivity.loginActivity);
		L.outp("audioFile: " + audioFile);
		int sound = lookUp(audioFile);
		// if (true)
		// return;
		if (sound != 0)
			playSoundLocal(sound);
		else
			L.out("Sound not found: " + audioFile);
		if (!isStaffUser())
			vibrate();
	}

	public boolean isStaffUser() {
		SharedPreferences settings = context.getSharedPreferences(User.PREFERENCE_FILE, 0);
		return settings.getBoolean(LoginActivity.STAFF_USER, false);
	}

	private int scale(int value, float percent) {
		return (int) (value * percent);
	}

	// @SuppressWarnings("unused")
	// private int scale(float value, float percent) {
	// return (int) (value * percent);
	// }

	private void vibrate() {
		// This example will cause the phone to vibrate "SOS" in Morse Code
		// In Morse Code, "s" = "dot-dot-dot", "o" = "dash-dash-dash"
		// There are pauses to separate dots/dashes, letters, and words
		// The following numbers represent millisecond lengths
		int dot = 100; // Length of a Morse Code "dot" in milliseconds
		int dash = 250; // Length of a Morse Code "dash" in milliseconds
		int short_gap = 100; // Length of Gap Between dots/dashes
		int medium_gap = 250; // Length of Gap Between Letters
		int long_gap = 500; // Length of Gap Between Words
		dot = scale(dot, percent);
		dash = scale(dash, percent);
		short_gap = scale(short_gap, percent);
		medium_gap = scale(medium_gap, percent);
		long_gap = scale(long_gap, percent);

		long[] pattern = {
				0, // Start immediately
				dot, short_gap, dot, short_gap, dot, // s
				medium_gap,
				dash, short_gap, dash, short_gap, dash, // o
				medium_gap,
				dot, short_gap, dot, short_gap, dot, // s
				long_gap,
				0, // Start immediately
				dot, short_gap, dot, short_gap, dot, // s
				medium_gap,
				dash, short_gap, dash, short_gap, dash, // o
				medium_gap,
				dot, short_gap, dot, short_gap, dot, // s
				long_gap
		};

		vibrator.vibrate(pattern, -1);

	}

	private void playSoundLocal(final int sFile) {
		// set up MediaPlayer
		// mp = MediaPlayer.create(LoginActivity.loginActivity, sFile);
		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				// L.out("sFile: " + sFile);
				try {
					// mp.reset();
					mp = MediaPlayer.create(context, sFile);
					// mp.prepare();
					mp.start();
					// mp.setOnPreparedListener(new OnPreparedListener() {
					//
					// public void onPrepared(MediaPlayer mp) {
					// L.out("prepared");
					// mp.start();
					// }
					// });
					mp.setOnCompletionListener(new OnCompletionListener() {

						@Override
						public void onCompletion(MediaPlayer arg0) {
							// L.out("finished: " + arg0);
						}
					});
					// mp = MediaPlayer.create(LoginActivity.loginActivity,
					// sFile);
					// mp.prepare();
					// mp.start();
				} catch (Exception e) {
					L.out("*** ERROR failed to play file: " + sFile);
				}
			}
		});
		thread.start();

	}

	public static int lookUp(String key) {
		for (int i = 0; i < audioTypes.length; i++)
			if (audioTypes[i].key.equals(key))
				return audioTypes[i].fileResource;
		return 0;
	}
}

class AudioType {
	String key;
	int fileResource;

	public AudioType(String key, int fileResource) {
		this.key = key;
		this.fileResource = fileResource;
	}

}
