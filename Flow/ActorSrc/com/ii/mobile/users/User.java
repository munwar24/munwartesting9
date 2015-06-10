/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.users;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.Uri;
import android.provider.BaseColumns;

import com.ii.mobile.application.ApplicationContext;
import com.ii.mobile.model.Persist;
import com.ii.mobile.soap.BaseSoap;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.transport.R; // same package
import com.ii.mobile.util.L;

/**
 * 
 * @author kfairchild
 */
public class User extends Persist {

	public static final String AUTHORITY = "com.ii.mobile.users.user";
	private String username = null;
	public String password = null;
	private String platform = null;
	private String employeeID = null;
	private String facilityID = null;
	private boolean reload = false;
	// private static User user = null;
	// get from strings for easy login
	private static String[] usernames = null;
	private static String[] passwords = null;
	private static String[] platforms = null;
	// private static Context context = null;
	// private static User user = null;
	private static int count = 0;
	public static final String PLATFORM = "platform";
	public static String PREFERENCE_FILE = "crothallMobile";
	public String PASSWORD = "password";
	public static ValidateUser validateUser = null;

	@Override
	public String toString() {
		return marshall()
				// + " platform: " + platform
				// + " employeeID: " + employeeID
				// + " needLogin: " + needLogin
				+ " needLogout: " + needLogout
				// + " wantBreak: " + wantBreak
				+ "\n   validateUser: " + (validateUser != null ? "true" : "false");
	}

	// public static void registerContext(Context context) {
	// User.context = context;
	// }
	public static User setUser(String username, String password) {
		return setUser(username, password, null);
	}

	private boolean needLogin = false;
	private boolean needLogout = false;
	private boolean wantBreak = false;

	// @Override
	// public String toString() {
	// return "ValidateUser:\n" + usersFacilityDetails.attributes.toString()
	// + "\n";
	// }

	public void setWantBreak(boolean wantBreak) {
		// L.out("wantBreak: " + wantBreak);
		this.wantBreak = wantBreak;
	}

	public boolean getWantBreak() {
		return wantBreak;
	}

	public boolean getNeedLogin() {
		return needLogin;
	}

	public void setNeedLogin(boolean needLogin) {
		// L.out("logout: " + this.needLogin + " to " + needLogin + L.p());
		this.needLogin = needLogin;
	}

	public boolean getNeedLogout() {
		return needLogout;
	}

	public void setNeedLogout(boolean needLogout) {
		L.out("logout: " + this.needLogout + " to " + needLogout);
		this.needLogout = needLogout;
	}

	public ValidateUser getValidateUser() {
		// if (validateUser == null)
		// L.out("getValidateUser error: " + validateUser + L.p());
		return validateUser;
	}

	public static User setUser(String username, String password, String platform) {
		L.out("platform: " + platform);
		String plat = platform;
		if (platforms == null) {
			loadPresetUsers();
		}
		if (platforms.length > 0 && platform == null) {
			plat = getPlatform(username);
			if (plat == null) {
				plat = platforms[0];
			}
		}
		if (plat == null) {
			ApplicationContext context = (ApplicationContext) ApplicationContext
					.getAppContext();
			Resources r = context.getResources();
			plat = r.getString(R.string.default_platform);
			L.out("null plat: " + plat);
		}
		BaseSoap.setPlatform(plat);
		ApplicationContext.user = new User(username, password, plat, true);
		// L.out("MyContext.user: " + MyContext.user);
		return ApplicationContext.user;
	}

	public static String getPlatform(String username) {
		// L.out("username: " + username);
		for (int i = 0; i < usernames.length; i++) {
			// L.out("usernames: " + usernames[i]);
			if (username.equals(usernames[i])) {
				return platforms[i];
			}
		}
		return null;
	}

	/**
	 * Return the status.
	 * 
	 * @return
	 */
	@Override
	public String getStatus() {
		return super.getStatus();
	}

	@Override
	public void setStatus(String newStatus) {
		status = newStatus;
	}

	public static User getUser() {
		// L.out("ApplicationContext.user: " + ApplicationContext.user);
		if (ApplicationContext.user != null) {
			return ApplicationContext.user;
		}
		return restoreUser();
	}

	public User(String username, String password, String platform,
			boolean insert) {
		this.username = username;
		this.password = password;
		this.platform = platform;
		this.saveUser();
	}

	public static void loadPresetUsers() {
		ApplicationContext context = (ApplicationContext) ApplicationContext
				.getAppContext();
		// L.out("loadPresetUsers: " + context);
		Resources r = context.getResources();
		usernames = r.getStringArray(R.array.username_array);
		passwords = r.getStringArray(R.array.password_array);
		platforms = r.getStringArray(R.array.platform_array);
		// L.out("usernames: " + usernames.length);
	}

	public static User next() {
		if (usernames == null) {
			loadPresetUsers();
		}
		// L.out("count: " + count);
		count += 1;
		if (count > usernames.length - 1) {
			count = 0;
		}
		String username = usernames[count];
		String password = passwords[count];
		String platform = platforms[count];

		ApplicationContext.user = new User(username, password, platform, true);
		// L.out("temp: " + MyContext.user);
		// L.out("count: " + count);
		return ApplicationContext.user;
	}

	public User(String username, String password, String platform) {
		// L.out("new user");
		this.username = username;
		this.password = password;
		this.platform = platform;
		initUpdate();
	}

	public void initUpdate() {
		ApplicationContext context = (ApplicationContext) ApplicationContext
				.getAppContext();
		SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_FILE, 0).edit();
		editor.putLong("lastUpdateTime", 0l);
		editor.commit();
	}

	public String getPlatform() {
		return platform;
	}

	public void setPlatform(String hostName) {
		this.platform = hostName;
	}

	public boolean getReload() {
		return reload;
	}

	public void setReload(boolean reload) {
		this.reload = reload;
	}

	public String marshall() {
		return username + ":" + "***";
	}

	public String getPassword() {
		return password;
	}

	public String getUsername() {
		return username;
	}

	public void saveUser() {
		ApplicationContext context = (ApplicationContext) ApplicationContext
				.getAppContext();
		// L.out("context: " + context + " " + this);
		SharedPreferences settings = context.getSharedPreferences(PREFERENCE_FILE, 0);
		// L.out("settings: " + settings);
		SharedPreferences.Editor editor = settings.edit();
		// L.out("getUserName: " + getUsername());
		editor.putString(UserColumns.USERNAME, getUsername());
		editor.putString(UserColumns.PASSWORD, getPassword());
		editor.putString(UserColumns.PLATFORM, getPlatform());
		if (getEmployeeID() != null)
			editor.putString(UserColumns.EMPLOYEE_ID, getEmployeeID());
		if (getFacilityID() != null)
			editor.putString(UserColumns.FACILITY_ID, getFacilityID());
		// L.out("employeeID: " + getEmployeeID());
		// editor.putString(PLATFORM, getStatus());
		editor.commit();
		// String temp = settings.getString(USERNAME, "yahoo");
		// L.out("temp: " + temp);
		// settings = context.getSharedPreferences(PREFERENCE_FILE, 0);
		// String temp2 = settings.getString(USERNAME, "yahoo");
		// L.out("temp2: " + temp);
	}

	private static User restoreUser() {
		ApplicationContext context = (ApplicationContext) ApplicationContext.getAppContext();
		L.out("restoreUser: ");
		if (context == null) {
			L.out("*** ERROR context: " + context);
			return null;
		}
		SharedPreferences settings = context.getSharedPreferences(
				PREFERENCE_FILE, 0);
		String userN = settings.getString(UserColumns.USERNAME, "");
		String pass = settings.getString(UserColumns.PASSWORD, "");
		String plat = settings.getString(UserColumns.PLATFORM, "");
		ApplicationContext.user = new User(userN, pass, plat);
		ApplicationContext.user.employeeID = settings.getString(UserColumns.EMPLOYEE_ID, "");
		ApplicationContext.user.facilityID = settings.getString(UserColumns.FACILITY_ID, "");
		// L.out("employeeID: " + ApplicationContext.user.employeeID);
		// L.out("**************************** restoreUser user: "
		// + ApplicationContext.user);
		return ApplicationContext.user;
	}

	public static final class UserColumns implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.parse("content://"
				+ AUTHORITY + "/user");
		public static final String ID = "id";
		public static final String USERNAME = "username";
		public static final String PASSWORD = "password";
		public static final String PLATFORM = "platform";
		public static final String EMPLOYEE_ID = "employeeID";
		public static final String FACILITY_ID = "facilityID";
		public static final String XML_STRING = "xml_string";
		public static final String STATUS = "status";
	}

	public void setValidateUser(ValidateUser validateUser) {
		L.out("validateUser: " + validateUser);
		if (validateUser != null)
			L.out("validateUser: " + validateUser.getEmployeeStatus());
		// else
		// L.out("Who is setting validateUser null? " + L.p());

		User.validateUser = validateUser;
		if (validateUser != null) {
			employeeID = validateUser.getEmployeeID();
			facilityID = validateUser.getFacilityID();
			saveUser();
		}
		// L.out("user: " + this);
	}

	public String getEmployeeID() {
		if (validateUser != null)
			return validateUser.getEmployeeID();
		return employeeID;
	}

	public String getFacilityID() {
		if (validateUser != null)
			return validateUser.getFacilityID();
		return facilityID;
	}

}
