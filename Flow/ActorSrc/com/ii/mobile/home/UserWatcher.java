package com.ii.mobile.home;

import java.util.GregorianCalendar;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ii.mobile.application.ApplicationContext;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.tab.BreakActivity;
import com.ii.mobile.transport.R; // same package
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

// other useful methods here

public enum UserWatcher {
	INSTANCE;

	private final int SLEEP_TIME = 3000;
	private final int TIMEOUT_TIME = 60 * 60;
	private final int PROMPT_TIME = 30;
	boolean running = false;

	private LoginActivity loginActivity = null;

	private class UpdateTask extends AsyncTask<Boolean, Integer, Long> {
		boolean finish;

		@Override
		protected Long doInBackground(Boolean... flags) {
			Thread.currentThread().setName("UserWatcherUpdateThread");
			finish = flags[0];
			return 0l;
		}

		@Override
		protected void onPostExecute(Long result) {
			// L.out("start");
			doUpdate(finish);
			// L.out("finished");
		}
	}

	private class WatcherTask extends AsyncTask<String, Integer, Long> {
		boolean showedPrompt = false;
		private long startTime = new GregorianCalendar().getTimeInMillis();

		@Override
		protected Long doInBackground(String... urls) {
			Thread.currentThread().setName("UserWatcherThread");
			running = true;
			run();
			return 0l;
		}

		@Override
		protected void onPostExecute(Long result) {
			// L.out("finished");
		}

		private void run() {
			startTime = new GregorianCalendar().getTimeInMillis();
			try {
				while (running) {
					checkTimeout();
					L.sleep(SLEEP_TIME);
				}

			} catch (Exception e) {
				L.out("error: " + e);
			}
			// L.out("Stop UserWatcher");
		}

		int count = 0;

		private void checkTimeout() {
			// quick kill for demo version
			if (true)
				return;
			User user = User.getUser();
			Vibrator vibrator = (Vibrator) loginActivity.getSystemService(Context.VIBRATOR_SERVICE);
			long now = new GregorianCalendar().getTimeInMillis();
			int time = (int) (TIMEOUT_TIME - ((now - startTime) / 1000));
			count = count + 1;
			// if (count % 10 == 0)
			// L.out("time: " + time);
			if (user.getValidateUser().getTaskNumber() != null) {
				running = false;
				return;
			} else {
				if (!showedPrompt && ((now - startTime) / 1000) + PROMPT_TIME > TIMEOUT_TIME) {
					vibrator.vibrate(500);
					showedPrompt = true;
					if (user.getValidateUser().getEmployeeStatus().equals(BreakActivity.NOT_IN))
						MyToast.show("Automatic password expiration in " + PROMPT_TIME + " seconds!");
					else
						MyToast.show("Automatic logout in " + PROMPT_TIME + " seconds!");
				} else if (((now - startTime) / 1000) > TIMEOUT_TIME) {
					running = false;
					vibrator.vibrate(1000);
					if (user.getValidateUser().getEmployeeStatus().equals(BreakActivity.NOT_IN))
						MyToast.show("Automatic password expiration!");
					else {
						MyToast.show("Automatic logout!");
						User.getUser().setNeedLogout(true);
					}
					update(true);
				}
			}
		}
	}

	public void login(LoginActivity loginActivity) {
		this.loginActivity = loginActivity;
	}

	public void start() {
		ValidateUser validateUser = User.getUser().getValidateUser();
		if (validateUser == null || validateUser.getEmployeeStatus() == null)
			return;
		// L.out("start: " + running + " " +
		// !(validateUser.getEmployeeStatus().equals(BreakActivity.AVAILABLE)));
		// L.out("update: " +
		// User.getUser().getValidateUser().getEmployeeStatus());
		if (!running) {
			if (!(validateUser.getEmployeeStatus().equals(BreakActivity.AVAILABLE))
					&& !User.getUser().getNeedLogin())
				return;
			WatcherTask watcherTask = new WatcherTask();
			watcherTask.execute();
		}
		update(false);
	}

	void update(boolean finish) {
		new UpdateTask().execute(finish);
	}

	public synchronized void doUpdate(boolean finish) {
		L.out("update: " + User.getUser());
		if (loginActivity == null) {
			L.out("no login activity");
			return;
		}
		if (finish) {
			((EditText) loginActivity.findViewById(R.id.txtPassword)).setEnabled(true);
			((EditText) loginActivity.findViewById(R.id.txtUsername)).setEnabled(true);
			((EditText) loginActivity.findViewById(R.id.txtPassword)).setText("");
		}
		// String password = ((EditText)
		// loginActivity.findViewById(R.id.txtPassword)).getText().toString();
		User user = ApplicationContext.user;
		// L.out("update: " + user.getNeedLogout());
		// L.out("update: " + user.getValidateUser().getEmployeeStatus());
		// ValidateUser validateUser = user.getValidateUser();
		// if (validateUser != null)
		// L.out("validateUser: " + validateUser);
		// if (user.getNeedLogout()
		// || validateUser == null
		// || validateUser.getEmployeeStatus() == BreakActivity.NOT_IN) {

		if (!user.getNeedLogout()) {
			Button button = (Button) loginActivity.findViewById(R.id.btnLogin);
			if (button == null) {
				L.out("not btnLogin!");
				return;
			}
			((EditText) loginActivity.findViewById(R.id.txtPassword)).setEnabled(true);
			((EditText) loginActivity.findViewById(R.id.txtUsername)).setEnabled(true);
			((Button) loginActivity.findViewById(R.id.btnLogin)).setText(loginActivity.getString(R.string.log_in));
			((Button) loginActivity.findViewById(R.id.buttonEnter)).setVisibility(View.GONE);
		} else {
			((Button) loginActivity.findViewById(R.id.btnLogin)).setText(loginActivity.getString(R.string.log_out));
			((Button) loginActivity.findViewById(R.id.buttonEnter)).setVisibility(View.VISIBLE);
		}
	}

	public void stop() {
		// L.out("stop: " + running);
		running = false;
		// if (backGroundTask != null)
		// backGroundTask.cancel(true);
	}
};
