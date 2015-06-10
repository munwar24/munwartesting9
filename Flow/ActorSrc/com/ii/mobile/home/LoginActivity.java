package com.ii.mobile.home;

import java.util.regex.Pattern;

import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.crittercism.app.Crittercism;
import com.ii.mobile.application.ApplicationContext;
import com.ii.mobile.application.Shortcut;
import com.ii.mobile.block.blocker.BlockService;
import com.ii.mobile.bus.Binder;
import com.ii.mobile.flow.authenticate.Login;
import com.ii.mobile.flow.db.FlowDbAdapter;
import com.ii.mobile.flowing.FlowStaticLoader;
import com.ii.mobile.flowing.StaticLoaderCallBack;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.monitor.UnitTestActivity;
import com.ii.mobile.service.NotifyService;
import com.ii.mobile.soap.StaticSoap;
import com.ii.mobile.soap.StaticSoap.StaticSoapColumns;
import com.ii.mobile.tab.BreakActivity;
import com.ii.mobile.tab.PickField;
import com.ii.mobile.tab.SelfTaskActivity;
import com.ii.mobile.tab.TabNavigationActivity;
import com.ii.mobile.tickle.TickleService;
import com.ii.mobile.tickle.Tickler;
import com.ii.mobile.transport.R; // same package
import com.ii.mobile.update.Updater;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;
import com.ii.mobile.util.SecurityUtils;

public class LoginActivity extends FragmentActivity implements TextWatcher, OnEditorActionListener,
		StaticLoaderCallBack, LoginCallBack {

	private EditText txtUsername;
	private EditText txtPassword;
	public Button loginButton;
	private User user = null;
	protected Vibrator vibrator;
	private String platform = null;
	public static String STAFF_USER = "staffUser";
	private static long loginTime = 0l;
	private boolean addedLongClick = false;

	public static LoginActivity loginActivity = null;

	OnClickListener onClickListener;
	SharedPreferences prefs;
	Editor editor;

	private final Handler incomingHandler = new Handler() {
		@Override
		public void handleMessage(Message message) {
			L.out("message: " + message);
			Bundle data = message.getData();
			// L.out("data: " + data + "message.arg1 " + message.arg1 + " ");
			if (data != null) {
				getTickled().checkTickle(data);
			}
		}
	};
	private static FragTickled fragTickled;

	private FragTickled getTickled() {
		if (fragTickled != null)
			return fragTickled;
		fragTickled = new FragTickled(this);
		return fragTickled;
	}

	final Messenger incomingMessenger = new Messenger(incomingHandler);
	private Binder binder = null;

	protected void initCritter() {
		// new Updater(this).checkForUpdate();
		Resources resources = getResources();
		boolean isProduction = resources.getBoolean(R.bool.isProduction);
		// new Updater(this).checkForUpdate();
		L.out("initCritter: " + isProduction);

		if (resources.getBoolean(R.bool.wantCrashReporting)) {
			if (resources.getBoolean(R.bool.isProduction))
				Critter.makeInstance(this, resources.getString(R.string.productionCrashReporting));
			else if (resources.getBoolean(R.bool.isCandidate))
				Critter.makeInstance(this, resources.getString(R.string.candidateCrashReporting));
			else
				Critter.makeInstance(this, resources.getString(R.string.dailyCrashReporting));
		}
	}

	public boolean isStaffUser() {
		SharedPreferences settings = getSharedPreferences(User.PREFERENCE_FILE, 0);
		return settings.getBoolean(STAFF_USER, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loginActivity = this;
		boolean isProduction = getResources().getBoolean(R.bool.isProduction);
		if (isProduction)
			L.setDebug(false);
		else
			L.setDebug(true);
		// testFacilities();
		initCritter();
		prefs = getSharedPreferences(User.PREFERENCE_FILE, MODE_PRIVATE);
		editor = prefs.edit();
		if (!prefs.getBoolean("shortcutKey", false))
		{
			editor.putBoolean("shortcutKey", true).commit();
			Shortcut.INSTANCE.createShortCut(this);
		}

		// getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		L.out("LoginActivity Started");

		setContentView(getLayout());
		createGUI();
		MyToast.make(this);

		vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

		L.out("Starting Services");
		startService(new Intent(this, getNotificationService(this)));
		L.out("ApplicationContext.user: " + ApplicationContext.user);
		startService(new Intent(this, TickleService.class));
		binder = new Binder(this, incomingMessenger, TickleService.class);
		if (ApplicationContext.user != null
				&& ApplicationContext.user.getNeedLogout()) {
			Intent intent = new Intent().setClass(LoginActivity.this, getTopLevelClass());
			startActivity(intent);
		}
	}

	public int getLayout() {
		return R.layout.transport_ii_login;
	}

	@SuppressWarnings("unused")
	private void testFacilities() {
		String bay = "73916";
		String alaska = "120712";
		String test = "20867";

		String value = "value to encrypt ting 123ksjiajwoi  fhiudhwuhwiuhwiuwhiwu";
		L.out(" value: " + value + " " + value.length());
		String pw = "xyzzy123";
		try {
			String result = SecurityUtils.encryptAES(pw, value);
			L.out("result: " + result + " " + result.length());
			String tinged = SecurityUtils.decryptAES(pw, result);
			L.out("tinged: " + tinged + " " + tinged.length());

		} catch (Exception e) {
			L.out("encryption error: " + e + L.p());
		}
	}

	// @SuppressWarnings("unused")
	// private void testFacility(String facilityID) {
	// Soap.setPlatform(getString(R.string.default_platform));
	// GetTaskDefinitionFieldsForScreenByFacilityID foo =
	// Soap.getSoap().getTaskDefinitionFieldsForScreenByFacilityID(facilityID);
	// L.out("GetTaskDefinitionFieldsForScreenByFacilityID: " + facilityID +
	// "\n" + foo);
	// ListTaskClassesByFacilityID bar =
	// Soap.getSoap().listTaskClassesByFacilityID(facilityID);
	// L.out("GetTaskDefinitionFieldsForScreenByFacilityID: " + facilityID +
	// "\n" + bar);
	// }

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		L.out("onNewIntent: " + intent.getDataString());
	}

	public Class<?> getLoginClass() {
		return LoginActivity.class;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		L.out("onActivityResult: " + requestCode + " " + resultCode);
		// make sure we are starting
		Intent intent = new Intent(this.getApplicationContext(),
				getLoginClass());
		// L.out("starting");
		startActivity(intent);
	}

	@Override
	public void onPause() {
		super.onPause();
		// L.out("onPause");
		// setStaleTime();
	}

	@Override
	protected void onResume() {
		super.onResume();
		L.out("onResume");
		// make sure UserWatcher has this activity
		// User.getUser().setNeedLogin(false);
		new Updater(this).checkForUpdate();
		UserWatcher.INSTANCE.login(this);
		UserWatcher.INSTANCE.doUpdate(false);
		// TaskActivity.initDataCache();
		PickField.initDataCache();
	}

	@Override
	protected void onDestroy() {
		// WakeLocker.INSTANCE.stop();
		if (binder != null)
			binder.onDestroy();
		ApplicationContext.user = User.getUser();
		L.out("onDestroy: " + User.getUser());

		super.onDestroy();
	}

	public Class<?> getTopLevelClass() {
		return TabNavigationActivity.class;
	}

	public Class<?> getNotificationService(Activity activity) {
		return NotifyService.class;
	}

	protected void commonGUI() {
		// TextView versionView = (TextView)
		// this.findViewById(R.id.versionMonitor);
		// versionView.setText(getResources().getString(R.string.crothall_version));
	}

	public void createGUI() {
		commonGUI();
		// try {
		txtUsername = (EditText) this.findViewById(R.id.txtUsername);
		txtUsername.addTextChangedListener(this);
		txtPassword = (EditText) this.findViewById(R.id.txtPassword);
		txtPassword.addTextChangedListener(this);
		txtPassword.setOnEditorActionListener(this);

		loginButton = (Button) this.findViewById(R.id.btnLogin);
		platform = getResources().getString(R.string.default_platform);
		// user = User.setUser(txtUsername.getText().toString(),
		// txtPassword.getText().toString(), platform);
		L.out("login: " + user);

		String longClickonLogin = getResources().getString(R.string.long_click_on_login);
		L.out("longClickonLogin: " + longClickonLogin);

		SharedPreferences settings = getSharedPreferences(User.PREFERENCE_FILE, 0);
		boolean staffUser = settings.getBoolean(STAFF_USER, false);
		platform = settings.getString(User.PLATFORM, platform);
		L.out("platform: " + platform);
		String username = settings.getString(User.UserColumns.USERNAME, "");
		L.out("username: " + username);
		String employeeID = settings.getString(User.UserColumns.EMPLOYEE_ID, "");
		L.out("employeeID: " + employeeID);
		// String facilityID = settings.getString(User.UserColumns.FACILITY_ID,
		// "");

		txtUsername.setText(username);

		// platform = settings.getString(User.PLATFORM, "");
		L.out("staffUser: " + staffUser);
		if (staffUser) {
			longClickonLogin = "true";
			String password = settings.getString(User.UserColumns.PASSWORD, "");
			L.out("password: " + password);
			txtPassword.setText(password);
			addLongClick();
		}

		// addClearDataLongClick();

		onClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				vibrator.vibrate(200);

				if (view.equals(loginButton) && loginButton.getText().equals(getString(R.string.log_out))) {
					doLogout();
					return;
				}
				SharedPreferences settings = getSharedPreferences(
						User.PREFERENCE_FILE, 0);
				boolean staffUser = settings.getBoolean(STAFF_USER, false);
				if (txtPassword.getText().toString().equals("frank"))
					IntentionalCrash.intentionalCrash();
				L.out("platform: " + platform);
				user = User.setUser(txtUsername.getText().toString(),
						txtPassword.getText().toString(), platform);
				if (getResources().getBoolean(R.bool.wantCrashReporting))
					registerCrittercism();
				// Crittercism.setUsername(User.getUser().getUsername());
				L.out("user: " + user);
				if (txtPassword.getText().toString().equals("zxc")) {
					SharedPreferences.Editor editor = settings.edit();
					editor.putBoolean(STAFF_USER, true);
					editor.commit();
					txtPassword.setText("");
					// stopService(new Intent(LoginActivity.this,
					// BlockService.class));
					addLongClick();
					return;
				}
				if (txtPassword.getText().toString().equals("")) {

					if (staffUser) {
						MyToast.show("No longer staff user");
						SharedPreferences.Editor editor = settings.edit();
						editor.putBoolean(STAFF_USER, false);
						editor.commit();
						txtPassword.setText("");
					} else {
						MyToast.show("Invalidate Login: Enter a Password!");
					}
					return;
				}
				FlowDbAdapter.getFlowDbAdapter().deleteAll();
				UpdateController.clearStaticLoad();
				SelfTaskActivity.initDataCache();
				new DialogLogin(LoginActivity.this).execute();
				// doLogIn();
			}
		};
		loginButton.setOnClickListener(onClickListener);

		OnClickListener enterClickListener = new OnClickListener() {

			@Override
			public void onClick(View view) {
				vibrator.vibrate(200);
				// User.getUser().setNeedLogin(true);
				// User.getUser().setNeedLogout(false);

				// SelfTaskActivity.initDataCache();
				// UpdateController.clearStaticLoad();
				// SelfTaskActivity.initDataCache();

				Intent intent = new Intent().setClass(LoginActivity.this, getTopLevelClass());
				startActivity(intent);
			}
		};
		Button enterButton = (Button) this.findViewById(R.id.buttonEnter);
		// MyToast.show("enterButton: " + enterButton);
		if (enterButton != null)
			enterButton.setOnClickListener(enterClickListener);
	}

	public void doLogIn() {
		// new DoLogin().execute();
	}

	public void doLogout() {
		L.out("legacy");
		user = User.getUser();
		if (!user.getValidateUser().getEmployeeStatus().equals(BreakActivity.AVAILABLE)) {
			MyToast.show("Unable to logout if you have a task or on delay!");
			return;
		}
		int updatesLeft = FlowDbAdapter.getFlowDbAdapter().somethingToUpdate();
		if (updatesLeft != 0) {
			MyToast.show("Unable to logout have " + L.getPlural(updatesLeft, "update") + " pending!");
			return;
		}
		user.setNeedLogout(false);
		// System.out.println("<<<--->>> Current 2 : " +
		// prefs.getString("current_user", "null"));
		editor.putString("previous_user", prefs.getString("current_user", "null")).commit();
		if (user.getValidateUser().getEmployeeStatus().equals(BreakActivity.AVAILABLE))
			UserWatcher.INSTANCE.doUpdate(false);
		txtPassword.setText("");
		UserWatcher.INSTANCE.stop();
		// user.getValidateUser().setEmployeeStatus(BreakActivity.NOT_IN);
		SelfTaskActivity.setEmployeeStatus(BreakActivity.NOT_IN, false, LoginActivity.this);
		MyToast.show("Logged out");
	}

	private final String lastPlatform = null;

	// long click
	public void sayClick(View v) {
		user = User.next();
		L.out("user: " + user);
		txtUsername.setText(user.getUsername());
		txtPassword.setText(user.getPassword());
		platform = user.getPlatform();
		if (platform.equals(lastPlatform))
			changePlatform();
		L.out("platform: " + platform);
		// MyToast.show("Platform is: " + shortName(user.getPlatform()),
		// Toast.LENGTH_SHORT);
		MyToast.show("Facility is: " + user.getPlatform(),
				Toast.LENGTH_SHORT);
		// MyToast.show("Changing user to: " + user.getUsername()
		// + "\nPlatform: " + user.getPlatform(), Toast.LENGTH_SHORT);
	}

	public void changePlatform() {
		L.out("do nothing change platform");
	}

	public void sayClickIntentBlock(View v) {
		// vibrator.vibrate(200);
		L.out("BlockService.running: " + BlockService.running);
		if (!BlockService.running) {
			MyToast.show("Started IntentBlock Service");
			// startService(new Intent(this, BlockService.class));
		} else {
			MyToast.show("Stopped IntentBlock Service");
			// stopService(new Intent(this, BlockService.class));
		}
		// startedIntentBlocker = !startedIntentBlocker;
	}

	@Override
	public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
	}

	@Override
	public void onTextChanged(CharSequence text, int arg1, int arg2, int arg3) {
		// L.out("onTextChanged: " + text);
		// if (text.toString().length() > 0) {
		// // setStaleTime();
		// }
	}

	public void afterTextChangedold(Editable arg0) {
		// L.out("afterTextChanged: " + arg0);
		String text = arg0.toString();
		if (text.length() != 0)
			User.getUser().setValidateUser(null);
		UserWatcher.INSTANCE.doUpdate(false);
	}

	private static final String PATTERN = "^[a-zA-Z 0-9?\\+!\\*\\$%#@_\\-\\?,_]*$";
	private boolean editing = false;

	@Override
	public void afterTextChanged(Editable editable) {
		L.out("afterTextChanged: " + editing);
		if (editing)
			return;
		String text = editable.toString();
		int length = text.length();

		if (!Pattern.matches(PATTERN, text)) {
			editing = true;
			editable.delete(length - 1, length);
			editing = false;
		}

		if (text.length() != 0) {
			User.getUser().setValidateUser(null);

		}
		UserWatcher.INSTANCE.doUpdate(false);
	}

	// private void addClearDataLongClick() {
	// TextView textView = (TextView) findViewById(R.id.fullScreen);
	// if (textView == null)
	// return;
	// textView.setOnLongClickListener(new View.OnLongClickListener() {
	//
	// @Override
	// public boolean onLongClick(View v) {
	// vibrator.vibrate(400);
	// new ClearData().show(LoginActivity.this);
	// return false;
	// }
	// });
	// }

	private void addLongClick() {
		TextView textView = (TextView) findViewById(R.id.txtUsername);
		L.out("textView: " + textView);
		if (!addedLongClick) {
			addedLongClick = true;
			MyToast.show("Running in staff mode");
			textView.setOnLongClickListener(new View.OnLongClickListener() {

				@Override
				public boolean onLongClick(View view) {
					// L.out("long view: " + view);
					if (isStaffUser()) {
						vibrator.vibrate(400);
						sayClick(view);
					}
					return true;
				}
			});
			// View view = findViewById(R.id.scrollView);
			// view.setOnLongClickListener(new View.OnLongClickListener() {
			//
			// @Override
			// public boolean onLongClick(View view) {
			// if (isStaffUser()) {
			// vibrator.vibrate(400);
			// sayClickIntentBlock(view);
			// }
			// return true;
			// }
			// });
			// View unitTestView = findViewById(R.id.unitTestView);
			// unitTestView.setOnLongClickListener(new
			// View.OnLongClickListener() {
			//
			// @Override
			// public boolean onLongClick(View view) {
			// if (isStaffUser()) {
			// vibrator.vibrate(400);
			// Intent intent = new Intent().setClass(LoginActivity.this,
			// getUnitTestActivity());
			// startActivity(intent);
			// }
			// return true;
			// }
			// });
		} else {
			MyToast.show("Running in staff mode");
		}
	}

	public Class<?> getUnitTestActivity() {
		// MyToast.show("right one");
		return UnitTestActivity.class;
	}

	public static String getJSon(String methodName, Activity activity) {
		return getJSon(methodName, activity, null);
	}

	synchronized public static String getJSon(String methodName, Activity activity, String taskNumber) {
		// L.out("methodName: " + methodName);
		String facilityID = User.getUser().getFacilityID();
		String employeeID = User.getUser().getEmployeeID();
		// if
		// (methodName.equals(ParsingSoap.GET_TASK_DEFINITION_FIELDS_DATA_FOR_SCREEN_BY_FACILITY_ID)
		// ||
		// methodName.equals(ParsingSoap.GET_TASK_DEFINITION_FIELDS_FOR_SCREEN_BY_FACILITYID)
		// || methodName.equals(ParsingSoap.LIST_TASK_CLASSES_BY_FACILITY_ID)
		// || methodName.equals(ParsingSoap.LIST_DELAY_TYPES))
		// employeeID = null;
		Intent intent = activity.getIntent();
		String[] selectionArgs = new String[] { employeeID, facilityID, taskNumber };
		intent.setData(Uri.parse("content://" + StaticSoap.AUTHORITY + "/" +
				methodName));
		Cursor cursor = activity.managedQuery(activity.getIntent().getData(), null, null, selectionArgs, null);
		if (cursor != null) {
			cursor.moveToFirst();
			do {
				// int index = cursor.getColumnIndex(StaticSoapColumns.JSON);
				// L.out("index: " + index + " " + cursor.getClass());
				String json = cursor.getString(cursor.getColumnIndex(StaticSoapColumns.JSON));
				if (json != null) {
					// L.out("json: " + json.length());
				}
				else
					L.out("*** ERROR json: " + json);
				return json;

			} while (cursor.moveToNext());
		}
		return null;
	}

	public static boolean isConnectedToInternet(Context context) {
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

	public static long getLoginTime() {
		return loginTime;
	}

	@Override
	public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		L.out("action: " + actionId);
		onClickListener.onClick(v);
		return false;
	}

	private void registerCrittercism() {
		if (User.getUser().getValidateUser() != null) {
			try {
				JSONObject metadata = new JSONObject();
				// add arbitrary metadata
				metadata.put("user_id", User.getUser().getValidateUser().getEmployeeID());
				metadata.put("facility_id", User.getUser().getValidateUser().getFacilityID());
				metadata.put("name", User.getUser().getUsername());
				metadata.put("username", User.getUser().getUsername());
				metadata.put("android", android.os.Build.VERSION.RELEASE);
				metadata.put("MANUFACTURER", android.os.Build.MANUFACTURER);
				metadata.put("BRAND", android.os.Build.BRAND);
				metadata.put("DEVICE", android.os.Build.DEVICE);
				metadata.put("BOARD", android.os.Build.BOARD);
				// send metadata to crittercism (asynchronously)
				Crittercism.setMetadata(metadata);
				Crittercism.setUsername(User.getUser().getUsername());
			} catch (Exception e) {
				L.out("crittercism error: " + e);
			}
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();

		if (User.getUser().getValidateUser() != null)
		{
			// Toast.makeText(this, "Disabled", Toast.LENGTH_LONG).show();
			txtUsername.setEnabled(false);
			txtPassword.setEnabled(false);
		}
		else
		{
			// Toast.makeText(this, "Enabled", Toast.LENGTH_LONG).show();
			txtUsername.setEnabled(true);
			txtPassword.setEnabled(true);
		}
	}

	@Override
	public void staticLoaderSuccess() {
		L.out("success static load");
		User.getUser().setNeedLogout(true);
		UserWatcher.INSTANCE.doUpdate(false);
		Tickler.onResume();
		Intent intent = new Intent().setClass(LoginActivity.this,
				getTopLevelClass());
		startActivity(intent);
	}

	@Override
	public void staticLoaderFail() {
		L.out("failure static load");
		boolean reachable = FlowStaticLoader.isReachable(this);
		String temp = "Failed loading Static Content!"
				+ "\nYou are welcome to try again"
				+ "\n(just press Enter)."
				+ "\nOr you may wait until"
				+ "\nyou have better WI-FI."
				+ "\n";
		temp += ((reachable) ? "The server is reachable." : "The server is not reachable.");
		MyToast.show(temp, Toast.LENGTH_LONG);
		MyToast.show(temp, Toast.LENGTH_LONG);
	}

	@Override
	public void loginFailed() {
		boolean reachable = FlowStaticLoader.isReachable(this);
		String temp = ((reachable) ? "The server is reachable." : "The server is not reachable.");
		if (Login.cookie != null) {
			MyToast.show("Credentials accepted \nbut unable to become Available!\n" + temp);
			return;
		}
		if (!FragLoginActivity.isConnectedToInternet(loginActivity))
			MyToast.show("Unable to connect!\n" + temp);
		else
			MyToast.show("Invalid Login\n" + temp);
		User.getUser().password = null;
		// User.getUser().setValidateUser(null);
	}

	@Override
	public void loginSuccess() {
		MyToast.show("Successful Login");
		SharedPreferences prefs = loginActivity.getSharedPreferences(User.PREFERENCE_FILE, FragLoginActivity.MODE_PRIVATE);
		prefs.edit().putString("current_user", User.getUser().getUsername()).commit();
		new FlowStaticLoader(this).execute(true, this);
	}
}
