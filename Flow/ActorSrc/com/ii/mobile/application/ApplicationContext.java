/*
 * 
 */
package com.ii.mobile.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.ii.mobile.users.User;

/**
 * 
 * @author kfairchild
 */
public class ApplicationContext extends Application {

	// used by toast
	public static Activity activity = null;
	public static String PREFERENCE_FILE = "crothallMobile";

	private static ApplicationContext applicationContext = null;
	public static User user;
	public static boolean deleteDataBase = true;

	public ApplicationContext() {
		super();
		applicationContext = this;
		// L.out("applicationContext: " + applicationContext);
		// test

	}

	public static Context getAppContext() {
		// L.out("applicationContext: " + applicationContext);
		return applicationContext;
	}

}
