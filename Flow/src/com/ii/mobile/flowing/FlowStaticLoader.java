package com.ii.mobile.flowing;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.widget.Toast;

import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.util.L;

public class FlowStaticLoader {

	Activity activity;
	private StaticLoaderCallBack staticLoaderCallBack;

	public FlowStaticLoader(Activity activity) {
		L.out("FlowStaticLoader: " + activity);
		this.activity = activity;

	}

	public synchronized void execute(boolean resetCache, StaticLoaderCallBack staticLoaderCallBack) {
		L.out("execute: " + activity);
		if (resetCache) {
			UpdateController.clearStaticLoad();
			// FlowDbAdapter.getFlowDbAdapter().deleteAll();
		}
		this.staticLoaderCallBack = staticLoaderCallBack;
		new DownloadFlowStaticTask().execute();
	}

	class DownloadFlowStaticTask extends AsyncTask<Void, Integer, Long> {
		private final long LOADING = 0;
		private final long ERROR = 1;
		private final long LOADED_ALREADY = 2;
		private ProgressDialog progressDialog = null;

		@Override
		protected Long doInBackground(Void... arg0) {
			Thread.currentThread().setName("StaticLoaderThread");
			L.out("doInBackground: ");
			boolean success = UpdateController.INSTANCE.staticLoad();
			L.out("success: " + success);
			if (!success)
				return ERROR;
			return LOADED_ALREADY;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			L.out("probably not used but executes in UI thread");
		}

		@Override
		protected void onPreExecute() {
			L.out("onPreExecute: " + activity);
			progressDialog = new ProgressDialog(activity);
			progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			progressDialog.setMessage("Loading of TeamFlow content ...");
			progressDialog.setCancelable(true);
			progressDialog.show();
			L.out("done onPreExecute: " + activity);
		}

		@Override
		protected void onPostExecute(Long l) {
			L.out("onPostExecute: " + l);
			GetActorStatus getActorStatus = UpdateController.getActorStatus;
			if (getActorStatus == null) {
				MyToast.show("Failed to load getActorStatus", Toast.LENGTH_LONG);
				activity.finish();
				return;
			}

			if (progressDialog != null) {
				try {
					progressDialog.dismiss();
				} catch (Exception e) {
					L.out("dismissed exception: " + e);
				}
				// if (UpdateController.INSTANCE.statusWrapper == null) {
				// MyToast.show("Failed to load statusWrapper",
				// Toast.LENGTH_LONG);
				// activity.finish();
				// return;
				// }
				if (l == LOADING) {
					MyToast.show("... Loaded static content  ");
				}
				if (l == ERROR) {

					staticLoaderCallBack.staticLoaderFail();
					return;
				}
			}
			if (l == LOADED_ALREADY) {
				L.out("updating");
				staticLoaderCallBack.staticLoaderSuccess();
				UpdateController.INSTANCE.callback(UpdateController.getActorStatus, FlowRestService.GET_ACTOR_STATUS);
			}

		}
	}

	public static boolean isReachable(Context context) {
		// First, check we have any sort of connectivity
		final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		final NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
		boolean isReachable = false;

		if (netInfo != null && netInfo.isConnected()) {
			// Some sort of connection is open, check if server is reachable
			try {
				URL url = new URL("http://www.google.com");
				// URL url = new URL("http://10.0.2.2");
				HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
				urlc.setRequestProperty("User-Agent", "Android Application");
				urlc.setRequestProperty("Connection", "close");
				urlc.setConnectTimeout(2 * 1000);
				urlc.connect();
				isReachable = (urlc.getResponseCode() == 200);
			} catch (IOException e) {
				// Log.e(TAG, e.getMessage());
			}
		}

		return isReachable;
	}
}
