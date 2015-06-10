package com.ii.mobile.tickle;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.ii.mobile.flow.authenticate.Login;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flow.types.GetActorStatus.InstantMessage;
import com.ii.mobile.flow.types.Logger;
import com.ii.mobile.flowing.Flow;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.instantMessage.InstantMessageFragment;
import com.ii.mobile.tab.AudioPlayer;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class Tickler implements Runnable {

	public static final String TASK_NUMBER = "TaskNumber";

	public static final String JSON = "json";
	public static final String FLOW_JSON = "flowJson";
	public static final String ACTION_JSON = "actionJson";

	public static final String TASK_STATUS = "TaskStatus";
	public static final String TEXT_MESSAGE = "TextMessage";
	public static final String RECEIVED_DATE = "ReceivedDate";
	public static final String FROM_USER_NAME = "FromUserName";
	public static final String EMPLOYEE_STATUS = "EmployeeStatus";
	public static final String IM_STATUS = "IMStatus";

	private static Context context;
	private Thread thread = null;
	private boolean lastConnection = false;
	private static Tickler tickler = null;
	// private static AbstractDbAdapter abstractDbAdapter = null;
	// private final Object mPauseLock = new Object();
	// private final boolean mPaused = false;
	private final int NO_CONNECTION_WAIT = 1000;
	public static final int EXAMPLE = 0;
	public static final int ANOTHER_EXAMPLE = 1;

	// For Crothall Testing
	// public static final int POLLING_INTERVAL = 5000;
	public static final int POLLING_INTERVAL = 2000;
	// public static final int POLLING_INTERVAL = 20;

	static int count = 0;
	@SuppressWarnings("unused")
	// private GetEmployeeAndTaskStatusByEmployeeID lastTickle;
	public static GetActorStatus lastGetActorStatus = null;

	private final TickleService tickleService;

	public static Tickler startTickler(TickleService tickleService, Context context) {
		// L.out("context: " + context);
		if (tickler != null) {
			return tickler;
		}
		tickler = new Tickler(tickleService, context);
		tickler.thread.setName("TickleThread");
		return tickler;
	}

	public static Tickler getTickler() {
		return tickler;
	}

	private Tickler(TickleService tickleService, Context context) {
		this.tickleService = tickleService;
		Tickler.context = context;
		this.thread = new Thread(this);
		thread.start();
	}

	@Override
	public void run() {
		L.out("starting Tickler!");

		while (true) {
			// boolean downLoading = true;
			if (checkConnection()) {
				// if ((count % 100) == 0)
				// L.out(count + ": tickling ");
				count += 1;
				pollTickler();
				L.sleep(POLLING_INTERVAL);
			}
			synchronized (mPauseLock) {
				while (mPaused) {
					try {
						L.out("started pauselock");
						mPauseLock.wait();
					} catch (InterruptedException e) {
					}
					L.out("finished pauselock");
				}
				// L.out("running mPaused: " + mPaused);
			}
		}
	}

	private static final Object mPauseLock = new Object();
	private static boolean mPaused = true;

	public static void onPause() {
		L.out("tickler onPause: " + mPaused);
		if (!mPaused)
			// if (TransportActivity.showToast)
			// MyToast.show("Tickler Paused", Toast.LENGTH_SHORT);
			synchronized (mPauseLock) {
				mPaused = true;
			}
	}

	public static void onResume() {
		L.out("tickler onResume: " + mPaused);
		if (mPaused)
			// if (TransportActivity.showToast)
			// MyToast.show("Tickler Resumed", Toast.LENGTH_SHORT);
			synchronized (mPauseLock) {
				mPaused = false;
				lastGetActorStatus = null;
				mPauseLock.notifyAll();
			}
	}

	private void pollTickler() {
		GetActorStatus getActorStatus = null;
		if (mPaused)
			return;
		try {

			if ((count % 100) == 0) {
				L.out(count
						+ ": tickling "
						+ lastGetActorStatus);
				// AudioPlayer.INSTANCE.playSound(AudioPlayer.NEW_TASK);

			}
			Login login = Login.INSTANCE;
			if (login == null || Login.authorization == null || Login.cookie == null) {
				// L.out("ERROR: login is null! : " + login);
				return;
			}

			// StatusWrapper statusWrapper =
			// Flow.getFlow().getStatusWrapper(null);
			getActorStatus = Flow.getFlow().getStatus();
			// PrettyPrint.prettyPrint(statusWrapper.getNewJson(), true);
			// L.out("statusWrapper: " + statusWrapper);
			if (getActorStatus == null || !getActorStatus.validate()) {
				// L.out("*** ERROR StatusWrapper is null! "
				// + getActorStatus);
				Logger.getLogger().networkStats.addFailTickle();
				return;
			}
			sendTaskUpdate(getActorStatus);
		} catch (Exception e) {
			MyToast.show("*** Error: " + e + L.p());
		}
	}

	private void sendTaskUpdate(GetActorStatus getActorStatus) {
		ContentValues values = new ContentValues();

		int result = 0;
		if (getActorStatus != null) {
			Logger.getLogger().networkStats.addTotalTickle();
			// isDifferentTest(lastGetActorStatus, getActorStatus);
			if (UpdateController.getActionHistory != null
					&& isDifferent(lastGetActorStatus, getActorStatus)) {
				L.out("lastGetActorStatus: " + getActorStatus.toString());
				values.put(FLOW_JSON, getActorStatus.getNewJson());
				if (getActorStatus.getActionId() != null) {
					// L.out("get: " + getActorStatus.getActionId());
					GetActionStatus getActionStatus = UpdateController.getActionStatus(getActorStatus.getActionId());
					// L.out("found: " + getActionStatus);

					if (getActionStatus == null) {
						L.out("getting action from server for: " + getActorStatus.getActionId());
						getActionStatus = Flow.getFlow().getActionStatus(getActorStatus.getActionId());

						if (getActionStatus != null) {

							UpdateController.putActionStatus(getActionStatus);
							AudioPlayer.INSTANCE.playSound(AudioPlayer.NEW_TASK);
						}

					} else {
						if (lastGetActorStatus != null)
							AudioPlayer.INSTANCE.playSound(AudioPlayer.DISPATCHER_CHANGE_STATE);
					}
				} else {
					L.out("playSound: ");
					if (lastGetActorStatus != null)
						AudioPlayer.INSTANCE.playSound(AudioPlayer.DISPATCHER_CHANGE_STATE);
				}
				List<InstantMessage> instantMessages = getActorStatus.getInstantmessages();
				if (instantMessages.size() > 0) {
					InstantMessage instantMessage = instantMessages.get(0);
					if (instantMessage.sourceType == null
							|| instantMessage.sourceType.equals(InstantMessageFragment.DISPATCH))
						AudioPlayer.INSTANCE.playSound(AudioPlayer.NEW_MESSAGE);
					else
						AudioPlayer.INSTANCE.playSound(AudioPlayer.OPS_MESSAGE);
				}
				result = tickleService.sendMessage(values);
				L.out("result: " + result);
				if (result != 0)
					lastGetActorStatus = getActorStatus;
				else {
					while ((result == 0)) {
						L.sleep(1000);
						L.out("result: " + result);
						result = tickleService.sendMessage(values);
					}
				}
			}
		}

	}

	// private boolean isDifferentTest(GetActorStatus last, GetActorStatus
	// current) {
	// if (last == null) {
	// L.out("1");
	// return true;
	// }
	// if (current.getInstantmessages().size() > 0) {
	// L.out("2");
	// return true;
	// }
	// if (!last.getActorStatusId().equals(current.getActorStatusId())) {
	// L.out("3");
	// return true;
	// }
	// if (last.getActionStatusId() != null
	// && !last.getActionStatusId().equals(current.getActionStatusId()))
	// {
	// L.out("4");
	// return true;
	// }
	// if (current.getActionStatusId() != null
	// && !current.getActionStatusId().equals(last.getActionStatusId()))
	// {
	// L.out("5");
	// return true;
	// }
	// return false;
	// }

	private boolean isDifferent(GetActorStatus last, GetActorStatus current) {
		if (last == null)
			return true;
		if (current.getInstantmessages().size() > 0)
			return true;
		if (!last.getActorStatusId().equals(current.getActorStatusId()))
			return true;
		if (last.getActionStatusId() != null
				&& !last.getActionStatusId().equals(current.getActionStatusId()))
			return true;
		if (current.getActionStatusId() != null
				&& !current.getActionStatusId().equals(last.getActionStatusId()))
			return true;
		return false;
	}

	private boolean checkConnection() {
		if (!isConnectedToInternet() || User.getUser() == null) {
			if (lastConnection) {
				L.out("No connection to internet");
				// MyToast.show("Network unreachable\nData will be saved and\nuploaded automatically\nto Crothall");
				lastConnection = false;
				// lastTickle = null;
			}
			L.sleep(NO_CONNECTION_WAIT);
		} else {
			if (!lastConnection) {
				L.out("lastConnection: " + lastConnection);
				// MyToast.show("Network Reachable\nUploading data to Crothall");
			}
			lastConnection = true;
		}
		return lastConnection;
	}

	public static boolean isConnectedToInternet() {
		// L.out("activity: " + context);
		if (context == null) {
			L.out("no context yet");
			return false;
		}

		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = connectivityManager.getActiveNetworkInfo();
		return info != null && info.isConnected();
	}
}
