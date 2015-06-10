package com.ii.mobile.block.blocker;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Bundle;

import com.ii.mobile.util.L;

public class KillActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Bundle extras = getIntent().getExtras();
		// L.out("kill: " + extras);
		if ((extras != null) && extras.containsKey("pkg")) {
			L.out("kill: " + extras.getString("pkg"));
			// // Intent startMain = new Intent(Intent.ACTION_MAIN);
			// // startMain.addCategory(Intent.CATEGORY_HOME);
			// // startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// // startActivity(startMain);
			// Intent startMain = new Intent(this, LoginActivity.class);
			// startMain.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
			// // startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
			// // Intent.FLAG_ACTIVITY_SINGLE_TOP);
			// startActivity(startMain);

			String pkg = extras.getString("pkg");
			ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			activityManager.killBackgroundProcesses(pkg);
		}
		else
			L.out("kill error: " + extras);
	}

	@Override
	protected void onResume() {
		super.onResume();
		finish();
	}
}
