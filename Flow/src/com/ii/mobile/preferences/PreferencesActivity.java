package com.ii.mobile.preferences;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import com.ii.mobile.transport.R; // same package

public class PreferencesActivity extends PreferenceActivity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
	}
}
