package com.ii.mobile.home;

import android.app.Activity;
import android.content.res.Resources;
import android.widget.EditText;

//import com.ii.mobile.flow.authenticate.AuthenticateCallBack;
//import com.ii.mobile.flow.authenticate.AzureToken;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.service.TransportNotificationService;
import com.ii.mobile.transport.R;
import com.ii.mobile.util.L;

public class FragLoginActivity extends LoginActivity {

	private FragLoginActivity fragLoginActivity = null;

	@Override
	public Class<?> getTopLevelClass() {
		// MyToast.show("right one");
		return TransportActivity.class;
	}

	@Override
	public Class<?> getLoginClass() {
		return FragLoginActivity.class;
	}

	@Override
	public Class<?> getUnitTestActivity() {
		// MyToast.show("right one");
		return DevTestActivity.class;
	}

	@Override
	public Class<?> getNotificationService(Activity activity) {
		// MyToast.show("right one");
		this.fragLoginActivity = (FragLoginActivity) activity;
		L.out("fragLoginActivity: " + fragLoginActivity);
		return TransportNotificationService.class;
	}

	@Override
	public int getLayout() {
		L.out("got the right one");
		return R.layout.transport_ii_login;
	}

	@Override
	public void createGUI() {
		L.out("created the correct gui");
		// BeaconSample.test();
		// commonGUI();
		super.createGUI();

	}

	@Override
	protected void initCritter() {
		L.out("initing critter");
		UpdateController.INSTANCE.setActivity(this);
		// new Updater(this).checkForUpdate();
		// Critter.makeInstance(this, null);
		// boolean isProduction =
		// getResources().getBoolean(R.bool.isProduction);
		// new Updater(this).checkForUpdate();
		L.out("initCritter wantCrashReporting: " + getResources().getBoolean(R.bool.wantCrashReporting));
		Resources resources = getResources();
		L.out("isProduction: " + resources.getBoolean(R.bool.isProduction));
		L.out("isTestVersion: " + resources.getBoolean(R.bool.isCandidate));

		if (resources.getBoolean(R.bool.wantCrashReporting)) {
			if (resources.getBoolean(R.bool.isProduction))
				Critter.makeInstance(this, resources.getString(R.string.productionCrashReporting));
			else if (resources.getBoolean(R.bool.isCandidate))
				Critter.makeInstance(this, resources.getString(R.string.candidateCrashReporting));
			else
				Critter.makeInstance(this, resources.getString(R.string.dailyCrashReporting));

		}
	}

	@Override
	public void onPause() {
		L.out("onPause");
		super.onPause();
	}

	@Override
	public void onResume() {
		L.out("onResume");
		super.onResume();
	}

	@Override
	public void changePlatform() {
		L.out("change platform");
		UpdateController.clearStaticLoad();
	}

	@Override
	public void doLogout() {

		// ((EditText) this.findViewById(R.id.txtPassword)).setText("");
		new DialogLogout(this, (EditText) this.findViewById(R.id.txtPassword),
				(EditText) this.findViewById(R.id.txtUsername)).execute();

	}

	// @Override
	// public void callBack(AzureToken azureToken) {
	// MyToast.show("not used!");
	// }
}
