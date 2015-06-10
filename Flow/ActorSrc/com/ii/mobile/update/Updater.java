package com.ii.mobile.update;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;

import com.ii.mobile.application.ApplicationContext;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.transport.R;
import com.ii.mobile.util.L;

public class Updater {
	private final Handler mHandler;
	private final Activity activity;
	private final String configFile = "version.json";
	private final String directory = "/mobileDirectory/";

	private Version currentVersion;
	private Version newVersion;

	public Updater(Activity activity) {
		this.activity = activity;
		mHandler = new Handler();
	}

	private static int compareVersions(Version fromVersion, Version toVersion) {
		L.out("From: " + fromVersion.versionNumber + " to " + toVersion.versionNumber);
		int compared = compareVersions(fromVersion.versionNumber, toVersion.versionNumber);
		// L.out("compared: " + compared);
		return compared;
	}

	public static int compareVersions(String v1, String v2) {
		String[] components1 = v1.split("\\.");
		// L.out("components1: " + components1.length + " " + components1[0]);
		String[] components2 = v2.split("\\.");
		int length = Math.max(components1.length, components2.length);
		for (int i = 0; i < length; i++) {
			Integer one = Integer.valueOf(0);
			if(components1.length > i+1)
			one = Integer.valueOf(components1[i]);
			Integer two = Integer.valueOf(0);
			
			if(components2.length > i+1)
				two = Integer.valueOf(components2[i]);
			
			int result = Integer.valueOf(one.compareTo(two));
			if (result != 0) {
				return result;
			}
		}
		return 0;
	}

	private Version getCurrentVersion() {
		Version version = new Version();
		Resources resources = activity.getResources();
		version.versionNumber = resources.getString(R.string.crothall_version);
		version.versionComment = resources.getString(R.string.crothall_comment);
		L.out("version: " + version);

		String json = version.getNewJson();
		L.out("json: " + json);
		return version;
	}

	public void checkForUpdate() {
		checkUpdate.start();
	}

	private boolean shouldUpdate() {
		SharedPreferences sharedPreferences = activity.getSharedPreferences(ApplicationContext.PREFERENCE_FILE, 0);
		long lastUpdateTime = sharedPreferences.getLong("lastUpdateTime", 0);
		L.out("lastUpdateTime: " + lastUpdateTime);
		if ((lastUpdateTime + (1 * 30 * 60 * 1000)) < System.currentTimeMillis()) {
			lastUpdateTime = System.currentTimeMillis();
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putLong("lastUpdateTime", lastUpdateTime);
			editor.commit();
			return true;
		}
		return false;
	}

	private final Thread checkUpdate = new Thread() {

		@Override
		public void run() {
			try {
				Resources resources = activity.getResources();
				String downloadURL = getDownloadURL();

				L.out("downloadURL: " + downloadURL);
				String appFile = resources.getString(R.string.app_file);

				URL updateURL = new URL(downloadURL + appFile + "_" + configFile);
				L.out("updateURL: " + updateURL);
				URLConnection conn = updateURL.openConnection();
				InputStream is = conn.getInputStream();

				BufferedReader reader = new BufferedReader(new InputStreamReader(is));
				String sResponse;
				StringBuilder s = new StringBuilder();

				while ((sResponse = reader.readLine()) != null) {
					s = s.append(sResponse);
				}
				String responseString = s.toString();

				L.out("responseString: " + s);
				newVersion = Version.getGJon(responseString);
				L.out("newVersion: " + newVersion);
				currentVersion = getCurrentVersion();
				L.out("currentVersion: " + currentVersion);

				int result = compareVersions(newVersion, currentVersion);
				L.out("result: " + result);
				if (result > 0) {
					if (shouldUpdate())
						if (newVersion.versionRequired)
							mHandler.post(showUpdateForced);
						else
							mHandler.post(showUpdate);
					else {
						String message = "An update is available\nfrom " + currentVersion.versionNumber
								+ "\nto "
								+ newVersion.versionNumber
								+ "\nLogin and Select Options to Update";
						MyToast.show(message);
					}
				} else if (result == 0) {
					MyToast.show("Version is up to date");
				}
			} catch (Exception e) {
				L.out("Update error: " + e + L.p());
			}
		}
	};

	private String getDownloadURL() {
		Resources resources = activity.getResources();

		String downloadURL;
		if (resources.getBoolean(R.bool.isProduction))
			downloadURL = resources.getString(R.string.production_download_url);
		else if (resources.getBoolean(R.bool.isCandidate))
			downloadURL = resources.getString(R.string.candidate_download_url);
		else
			downloadURL = resources.getString(R.string.daily_download_url);
		return downloadURL;
	}

	private final Runnable showUpdate = new Runnable() {
		@Override
		public void run() {
			Resources resources = activity.getResources();
			final String downloadURL = getDownloadURL();

			final String appFile = resources.getString(R.string.app_file)
					+"."+newVersion.versionNumber
					+ ".apk";
			String message = "An update is available from " + currentVersion.versionNumber + " to "
					+ newVersion.versionNumber;

			if (currentVersion.versionComment != null)
				message += "\nDescription: " + newVersion.versionComment;
			message += "\nClick to update and install normally";
			
			if (currentVersion.versionComment != null)
				message += "\nDescription: " + newVersion.versionComment;
			message += "\nClick to update and install normally";
			// activity.stopService(new Intent(activity, BlockService.class));
			
			if (newVersion.versionRequired){
				new AlertDialog.Builder(activity)
						.setIcon(R.drawable.icon)
						.setTitle("Update Available")
						.setMessage(message)
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								L.out("downloading: " + downloadURL + appFile);
								new DownLoadAndInstall().execute(appFile);
							}
						})
						.show();
			}
			else
			{
				new AlertDialog.Builder(activity)
						.setIcon(R.drawable.icon)
						.setTitle("Update Available")
						.setMessage(message)
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								L.out("downloading: " + downloadURL + appFile);
								new DownLoadAndInstall().execute(appFile);
							}
						})
						.setNegativeButton("No", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int whichButton) {
								MyToast.show("Use the options menu to install later");
							}
						})
						.show();
			}
			
			new AlertDialog.Builder(activity)
					.setIcon(R.drawable.icon)
					.setTitle("Update Available")
					.setMessage(message)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int whichButton) {
							L.out("downloading: " + downloadURL + appFile);
							new DownLoadAndInstall().execute(appFile);
						}
					})
					.setNegativeButton("No", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int whichButton) {
							MyToast.show("Use the options menu to install later");
						}
					})
					.show();
		}
	};

	private final Runnable showUpdateForced = new Runnable() {
		@Override
		public void run() {
			Resources resources = activity.getResources();
			final String downloadURL = getDownloadURL();

			final String appFile = resources.getString(R.string.app_file)
					+ ".apk";
			String message = "An update is available from " + currentVersion.versionNumber + " to "
					+ newVersion.versionNumber;

			if (currentVersion.versionComment != null)
				message += "\nDescription: " + newVersion.versionComment;
			message += "\nClick to update and install normally";
			new AlertDialog.Builder(activity)
					.setIcon(R.drawable.icon)
					.setTitle("Update Available")
					.setMessage(message)
					.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int whichButton) {
							L.out("downloading: " + downloadURL + appFile);
							new DownLoadAndInstall().execute(appFile);
						}
					})
					.show();
		}
	};

	private class DownLoadAndInstall extends AsyncTask<String, Integer, Long> {
		ProgressDialog myDialog;

		@Override
		protected void onPreExecute() {
			myDialog = new ProgressDialog(activity);
			myDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			myDialog.setMax(100);
			myDialog.show();
		}

		@Override
		protected void onPostExecute(Long result) {
			L.out("finished");
			myDialog.dismiss();
		}

		@Override
		protected Long doInBackground(String... string) {
			Thread.currentThread().setName("UpdaterVersionThread");
			String appFile = string[0];

			L.out("appFile: " + appFile);
			boolean result = downloadApplication(appFile);
			if (result) {
				installApplication(appFile);
			} else {
				MyToast.show("Download failed - try again with internet access!");
			}

			return 0l;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			// L.out("progress: " + progress[0]);
			myDialog.setMax(progress[1]);
			myDialog.setProgress(progress[0]);
		}

		private void installApplication(String appFile) {
			L.out("installing: " + appFile);

			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + directory
					+ appFile)), "application/vnd.android.package-archive");
			activity.startActivityForResult(intent, 2);
		}

		private boolean downloadApplication(String appFile) {
			try {
				final String downloadURL = getDownloadURL();
				URL url = new URL(downloadURL + appFile);
				URLConnection connection = url.openConnection();
				InputStream inputStream = connection.getInputStream();
				int applicationFileSize = connection.getContentLength();
				L.out("length: " + applicationFileSize);

				String PATH = Environment.getExternalStorageDirectory() + directory;
				File file = new File(PATH);
				if (!file.exists()) {
					file.mkdirs();
				}
				File outputFile = new File(file, appFile);
				FileOutputStream fileOutputStream = new FileOutputStream(outputFile);

				byte[] buffer = new byte[1024];
				int lengthRead = 0;
				int downloaded = 0;
				while ((lengthRead = inputStream.read(buffer)) != -1) {
					fileOutputStream.write(buffer, 0, lengthRead);
					downloaded += lengthRead;
					publishProgress(downloaded, applicationFileSize);
				}
				fileOutputStream.close();
				inputStream.close();
			} catch (IOException e)
			{
				L.out("Download Error: " + e);
				return false;
			}
			return true;
		}
	}
}
