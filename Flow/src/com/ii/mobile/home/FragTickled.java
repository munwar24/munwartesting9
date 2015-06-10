package com.ii.mobile.home;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Messenger;

import com.ii.mobile.flow.authenticate.Login;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flow.types.GetActorStatus.InstantMessage;
import com.ii.mobile.flowing.FlowBinder;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.instantMessage.InstantMessageFragment;
import com.ii.mobile.instantMessage.OpsInstantMessageFragment;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.tickle.TickleService;
import com.ii.mobile.tickle.Tickler;
import com.ii.mobile.transport.R; // same package
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class FragTickled {
	private static FragTickled fragTickled = null;
	private Activity activity;
	private MediaPlayer mp = null;

	boolean isBound = false;

	Messenger messenger = null;

	public static FragTickled makeInstance(Activity activity) {
		if (fragTickled != null) {
			fragTickled.activity = activity;
			// fragTickled.checkIfServiceIsRunning();
			return fragTickled;
		}
		fragTickled = new FragTickled(activity);
		return fragTickled;
	}

	public FragTickled(Activity activity) {
		this.activity = activity;
		// checkIfServiceIsRunning();
	}

	public void checkTickle(Bundle data) {
		// printTickle(data);
		// checkInstantMessage(data);
		// String json = data.getString(Tickler.JSON);
		if (data.getString(Tickler.FLOW_JSON) == null)
			return;
		GetActorStatus getActorStatus = GetActorStatus.getGJon(data.getString(Tickler.FLOW_JSON));
		// L.out("getActorStatus: " + getActorStatus);

		getActorStatus.tickled = GJon.TRUE_STRING;
		// L.out("new getActorStatus: " + getActorStatus);
		if (!getActorStatus.isValidated() || getActorStatus.getNewJson() == null)
			return;
		boolean result = FlowBinder.updateLocalDatabase(FlowRestService.GET_ACTOR_STATUS, getActorStatus);
		if (!result)
			return;
		sendInstantMessage(getActorStatus);
		if (getActorStatus.getActorStatusId().equals(StaticFlow.ACTOR_NOT_IN)) {
			MyToast.show("Logged out by Dispatch!");
			User.getUser().setNeedLogout(false);
			Login.INSTANCE.reset();
			UserWatcher.INSTANCE.update(true);
			UserWatcher.INSTANCE.stop();
			User.getUser().setValidateUser(null);
			TransportActivity.transportActivity.finish();
			return;
		}
		if (TransportActivity.running) {
			UpdateController.INSTANCE.callback(getActorStatus, FlowRestService.GET_ACTOR_STATUS);
		} else {
			UpdateController.getActorStatus = getActorStatus;
		}
		// SoapDbAdapter.getSoapDbAdapter().tickled(data);
	}

	private void sendInstantMessage(GetActorStatus getActorStatus) {
		List<InstantMessage> instantMessages = getActorStatus.getInstantmessages();
		if (instantMessages.size() > 0) {
			Bundle data = new Bundle();
			for (InstantMessage instantMessage : instantMessages) {
				// if (instantMessage != null && instantMessage.message != null)
				// {
				// MyToast.show("mobileMessage: " + instantMessage.message);
				data.putString(Tickler.TEXT_MESSAGE, instantMessage.message);
				data.putString(Tickler.RECEIVED_DATE, instantMessage.mod.at);
				data.putString(Tickler.FROM_USER_NAME, instantMessage.send.by);
				// AudioPlayer.INSTANCE.playSound(AudioPlayer.NEW_MESSAGE);
				if (instantMessage.sourceType == null
						|| instantMessage.sourceType.equals(InstantMessageFragment.DISPATCH)) {
					InstantMessageFragment.receivedMessage(data);
					if (TransportActivity.running)
						TransportActivity.setNotify(TransportActivity.INSTANT_MESSAGE_DISPATCH);
				}
				else {
					OpsInstantMessageFragment.receivedMessage(data);
					TransportActivity.setNotify(TransportActivity.OPS_INSTANT_MESSAGE_DISPATCH);
				}

			}

		}
	}

	@SuppressWarnings("unused")
	private void printTickle(Bundle data) {
		String temp = "Tickled";
		if (data.getString(Tickler.TASK_NUMBER) != null)
			temp += Tickler.TASK_NUMBER + ": " + data.getString(Tickler.TASK_NUMBER);
		if (data.getString(Tickler.TASK_STATUS) != null)
			temp += " " + Tickler.TASK_STATUS + ": " + data.getString(Tickler.TASK_STATUS);
		MyToast.show(temp);
	}

	public void playSound(int sFile) {
		// set up MediaPlayer

		Thread thread = new Thread(new Runnable() {
			@Override
			public void run() {
				mp = MediaPlayer.create(activity.getApplicationContext(), R.raw.notify);
				mp.start();
			}
		});
		thread.start();
	}

	protected void onResume() {
		L.out("onResume");
		Intent intent = new Intent(activity, TickleService.class);

	}

	protected void onPause() {
		L.out("onPause: " + messenger + " bound: " + isBound);
		if (messenger != null) {
			// activity.unbindService(conn);
			isBound = false;
			messenger = null;
		}
	}

}
