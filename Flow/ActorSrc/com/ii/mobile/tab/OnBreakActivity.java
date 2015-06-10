package com.ii.mobile.tab;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ii.mobile.transport.R; // same package
import com.ii.mobile.users.User;

public class OnBreakActivity extends Activity {
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.on_break);

		Intent intent = getIntent();
		String breakType = intent.getStringExtra("breakType");
		TextView textview = (TextView) findViewById(R.id.title);
		textview.setText(breakType);
	}

	public void finishBreakButtonClick(View view) {
		User.getUser().setWantBreak(false);
		finish();
	}
}
