package com.ii.mobile.home;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.EditText;

import com.ii.mobile.flow.authenticate.Login;
import com.ii.mobile.flow.db.FlowDbAdapter;
import com.ii.mobile.flowing.Flow;
import com.ii.mobile.tickle.Tickler;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

class DialogLogout extends AsyncTask<Void, Integer, Long> {
	// was false!
	boolean successfulLogout = false;
	ProgressDialog progressDialog;
	private FragLoginActivity fragLoginActivity = null;
	private final EditText editText, editText2;

	public DialogLogout(FragLoginActivity fragLoginActivity, EditText editText,
			EditText editText2) {
		this.fragLoginActivity = fragLoginActivity;
		this.editText = editText;
		this.editText2 = editText2;
	}

	@Override
	protected Long doInBackground(Void... arg0) {

		User user = User.getUser();
		// User.getUser().setNeedLogout(false);
		// L.out("doLogout: " + User.getUser());
		// UserWatcher.INSTANCE.doUpdate(false);
		// UserWatcher.INSTANCE.stop();
		// user.getValidateUser().setEmployeeStatus(BreakActivity.NOT_IN);
		successfulLogout = Flow.getFlow().signOff();
		L.out("user: " + user + " " + user.getUsername() + " " + user.getPassword());
		if (successfulLogout) {
			Login.authorization = null;
			Tickler.onPause();
			MyToast.show(User.getUser().getUsername() + " Logged out");
		}
		return 0l;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		L.out("probably not used but executes in UI thread");
	}

	@Override
	protected void onPreExecute() {
		progressDialog = new ProgressDialog(fragLoginActivity);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("Logging out ...");
		progressDialog.setCancelable(false);
		progressDialog.show();
		L.out("progress");
	}

	@Override
	protected void onPostExecute(Long l) {
		L.out("dismissing");
		try {
			progressDialog.dismiss();
		} catch (Exception e) {
			MyToast.show("ERROR on dismiss: " + e);
		}
		if (successfulLogout) {
			L.out("success");
			User.getUser().setNeedLogout(false);
			L.out("doLogout: " + User.getUser());
			UserWatcher.INSTANCE.doUpdate(false);
			Tickler.onPause();
			Tickler.lastGetActorStatus = null;
			SharedPreferences prefs = fragLoginActivity.getSharedPreferences(User.PREFERENCE_FILE, FragLoginActivity.MODE_PRIVATE);
			prefs.edit().putString("previous_user", prefs.getString("current_user", "null")).commit();
			// UserWatcher.INSTANCE.doUpdate(false);
			editText.setText("");
			editText.setEnabled(true);
			editText2.setEnabled(true);
			FlowDbAdapter.getFlowDbAdapter().deleteAll();
		} else {
			if (!FragLoginActivity.isConnectedToInternet(fragLoginActivity))
				MyToast.show("Need a connection to internet\nto logout!");
			else
				MyToast.show("Failed to logout!");
		}
	}
}
