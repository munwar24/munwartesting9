package com.ii.mobile.home;

import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;

import com.ii.mobile.flow.authenticate.Login;
import com.ii.mobile.flow.db.FlowDbAdapter;
import com.ii.mobile.flowing.Flow;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

class DialogLogin extends AsyncTask<Void, Integer, Long> {
	// was false!
	boolean successfulLogin = false;
	ProgressDialog progressDialog;
	private final LoginActivity loginActivity;

	public DialogLogin(LoginActivity fragLoginActivity) {
		this.loginActivity = fragLoginActivity;
	}

	@Override
	protected Long doInBackground(Void... arg0) {
		User user = User.getUser();
		L.out("user: " + user + " " + user.getUsername() + ": ***");

		successfulLogin = Flow.getFlow().signOnWithPassword(user.getUsername(), user.getPassword());
		user.saveUser();
		user.password = null;
		if (successfulLogin) {
			// GetActorStatus getActorStatus =
			// Flow.getFlow().getActorStatus(User.getUser().getEmployeeID());
			// if (getActorStatus == null) {
			// //
			// MyToast.show("Error - able to login but could not get status!\nTry again!");
			// successfulLogin = false;
			// return 0l;
			// }

			User.validateUser = new ValidateUser();

		} else {
			Login.INSTANCE.reset();

		}
		return 0l;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		L.out("probably not used but executes in UI thread");
	}

	@Override
	protected void onPreExecute() {
		FlowDbAdapter.getFlowDbAdapter().deleteAll();
		progressDialog = new ProgressDialog(loginActivity);
		progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		progressDialog.setMessage("Validating Credentials ...");
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

		SharedPreferences settings = loginActivity.getSharedPreferences(User.PREFERENCE_FILE, 0);
		boolean staffUser = settings.getBoolean(FragLoginActivity.STAFF_USER, false);
		if (!staffUser) {
			// txtPassword.setText("");
		}
		if (successfulLogin) {
			L.out("success");
			loginActivity.loginSuccess();

		} else {

			loginActivity.loginFailed();
		}
	}
}
