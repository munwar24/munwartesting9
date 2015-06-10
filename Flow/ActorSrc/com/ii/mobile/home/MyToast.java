/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.home;

import android.app.Activity;
import android.widget.Toast;

import com.ii.mobile.application.ApplicationContext;
import com.ii.mobile.util.L;

/**
 * 
 * @author kfairchild
 */
public class MyToast {
	// private static Activity activity;
	public static void make(Activity activity) {
		ApplicationContext.activity = activity;
	}

	public static void show(String text) {
		show(text, Toast.LENGTH_LONG);
	}

	public static void show(final String text, final int length) {
		if (ApplicationContext.activity == null) {
			L.out("toast: " + text);
			return;
		}
		L.outpp("text: " + text);
		ApplicationContext.activity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(ApplicationContext.activity.getApplicationContext(),
						text, length).show();
			}
		});
	}

}
