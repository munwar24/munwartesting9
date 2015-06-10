package com.ii.mobile.flow.authenticate;

import java.net.MalformedURLException;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.widget.ProgressBar;

import com.ii.mobile.util.L;
import com.microsoft.windowsazure.mobileservices.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.MobileServiceUser;
import com.microsoft.windowsazure.mobileservices.NextServiceFilterCallback;
import com.microsoft.windowsazure.mobileservices.ServiceFilter;
import com.microsoft.windowsazure.mobileservices.ServiceFilterRequest;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponse;
import com.microsoft.windowsazure.mobileservices.ServiceFilterResponseCallback;
import com.microsoft.windowsazure.mobileservices.UserAuthenticationCallback;

public class Authenticate {

	public static final String USER_NAME = "userName";
	public static final String AUTHENTICATION_TOKEN = "authenticationToken";
	public static final String PREFERENCE_FILE = "testMobile";

	private Activity activity;
	private ProgressBar mProgressBar;

	public void authenticate(final Activity activity, final AuthenticateCallBack authenticateCallBack) {
		MobileServiceClient mobileServiceClient = null;
		this.activity = activity;
		L.out("creating authenticate");
		if (restoreFromSettings() != null) {
			authenticateCallBack.callBack(restoreFromSettings());
			return;
		}

		try {
			// Create the Mobile Service Client instance, using the provided
			// Mobile Service URL and key
			mobileServiceClient = new MobileServiceClient(
					"https://pulser.azure-mobile.net/",
					"TUFlJIBXYLsSTFfWIcOklPpNOylgor78",
					activity).withFilter(new ProgressFilter());

		} catch (MalformedURLException e) {
			createAndShowDialog(new
					Exception("There was an error creating the Mobile Service. Verify the URL"),
					"Error");
			L.out("error in Mobile Service: " + L.p());
		}
		L.out("ready to login: " + mobileServiceClient);
		// Login using the Google provider.

		if (mobileServiceClient != null)
			mobileServiceClient.login(MobileServiceAuthenticationProvider.MicrosoftAccount,
					new UserAuthenticationCallback() {

						@Override
						public void onCompleted(MobileServiceUser user,
								Exception exception, ServiceFilterResponse response) {
							L.out("response: " + response);

							if (exception == null) {
								L.out("user: " + user + " token : \n" + user.getAuthenticationToken());
								// createAndShowDialog(String.format(
								// "You are now logged in - %1$2s",
								// user.getUserId()), "Success");
								// createTable();
								// testTeamFlow(user.getAuthenticationToken());
								saveInSettings(user);
								authenticateCallBack.callBack(restoreFromSettings());
							} else {
								createAndShowDialog("You must log in. Login Required", "Error");
							}
						}
					});
	}

	private void saveInSettings(MobileServiceUser user) {
		L.out("userName: " + user.getUserId() + " token: " + user.getAuthenticationToken().length());
		SharedPreferences settings = activity.getSharedPreferences(PREFERENCE_FILE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(USER_NAME, user.getUserId());
		editor.putString(AUTHENTICATION_TOKEN, user.getAuthenticationToken());
		editor.commit();
	}

	public void clearSettings(Activity activity) {
		SharedPreferences settings = activity.getSharedPreferences(PREFERENCE_FILE, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(USER_NAME, null);
		editor.putString(AUTHENTICATION_TOKEN, null);
		editor.commit();
	}

	public AzureToken restoreFromSettings() {
		SharedPreferences sharedPreferences = activity.getSharedPreferences(PREFERENCE_FILE, 0);
		String userName = sharedPreferences.getString(USER_NAME, null);
		String authenticationToken = sharedPreferences.getString(AUTHENTICATION_TOKEN, null);

		if (userName != null) {
			AzureToken azureToken = new AzureToken(userName, authenticationToken);
			L.out("azureToken: " + azureToken);
			return azureToken;
		}
		L.out("settings not found");
		return null;
	}

	public AzureToken restoreFromSettings(Activity activity) {
		SharedPreferences sharedPreferences = activity.getSharedPreferences(
				PREFERENCE_FILE, 0);
		String userName = sharedPreferences.getString(USER_NAME, null);
		String authenticationToken = sharedPreferences.getString(AUTHENTICATION_TOKEN, null);

		if (userName != null) {
			AzureToken azureToken = new AzureToken(userName, authenticationToken);
			L.out("azureToken: " + azureToken);
			return azureToken;
		}
		L.out("settings not found");
		return null;
	}

	private void createAndShowDialog(Exception exception, String title) {
		createAndShowDialog(exception.toString(), title);
	}

	/**
	 * Creates a dialog and shows it
	 * 
	 * @param message
	 *            The dialog message
	 * @param title
	 *            The dialog title
	 */
	private void createAndShowDialog(String message, String title) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);

		builder.setMessage(message);
		builder.setTitle(title);
		builder.create().show();
	}

	private class ProgressFilter implements ServiceFilter {

		@Override
		public void handleRequest(ServiceFilterRequest request,
				NextServiceFilterCallback nextServiceFilterCallback,
				final ServiceFilterResponseCallback responseCallback) {
			activity.runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if (mProgressBar != null)
						mProgressBar.setVisibility(ProgressBar.VISIBLE);
				}
			});

			nextServiceFilterCallback.onNext(request, new ServiceFilterResponseCallback() {

				@Override
				public void onResponse(ServiceFilterResponse response, Exception exception) {
					activity.runOnUiThread(new Runnable() {

						@Override
						public void run() {
							if (mProgressBar != null)
								mProgressBar.setVisibility(ProgressBar.GONE);
						}
					});

					if (responseCallback != null)
						responseCallback.onResponse(response, exception);
				}
			});
		}

	}

}
