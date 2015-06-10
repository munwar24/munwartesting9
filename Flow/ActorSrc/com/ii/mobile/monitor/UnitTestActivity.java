/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.monitor;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ii.mobile.transport.R; // same package
import com.ii.mobile.home.MyToast;
import com.ii.mobile.soap.Soap;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsDataForScreenByFacilityID;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID;
import com.ii.mobile.soap.gson.ListDelayTypes;
import com.ii.mobile.soap.gson.ListTaskClassesByFacilityID;
import com.ii.mobile.util.L;

/**
 * 
 * @author kfairchild
 */
public class UnitTestActivity extends Activity implements Runnable {
	// private Vibrator vibrator;
	private boolean running = false;
	private Thread thread = null;
	// private static UnitTestActivity unitTestActivity;
	private static final String MESSAGES = "messages";
	private final List<Facility> facilities = new ArrayList<Facility>();
	private String currentText = "";

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		L.out("creating UnitTestActivity");
		// UnitTestActivity.unitTestActivity = this;
		setContentView(R.layout.unit_test_view);
		// vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
		initFacilities();
	}

	private void initFacilities() {
		facilities.add(new Facility("Bayonne", "73916"));
		facilities.add(new Facility("Alaska", "120712"));
		facilities.add(new Facility("CTest", "20867"));
		facilities.add(new Facility("Ellis", "26636"));
		facilities.add(new Facility("EVS", "73916"));

		String temp = "  Facilties: <br>";

		for (Facility facility : facilities) {
			temp += "     " + facility + "<br/>";
		}
		addMessage("<tab>" + temp + "</tab>");
	}

	public void unitTestButtonClick(View view) {
		if (!running) {
			this.thread = new Thread(this);
			thread.start();
		} else {
			MyToast.show("Already running Unit Tests!");
		}
	}

	public void run() {
		String color = "#2c96f7";
		running = true;
		boolean validated = true;
		long start = new GregorianCalendar().getTimeInMillis();
		for (Facility facility : facilities) {
			String temp = "Static Loading from : " + facility;
			addMessage(setColor(temp, color));
			long now = new GregorianCalendar().getTimeInMillis();
			validated = runFacilityTest(facility, new GregorianCalendar().getTimeInMillis());
			addMessage(printFacility(facility, validated, now));
		}
		running = false;
		addMessage(setColor("<br/>Total time for " + facilities.size() + " facilities" + getSeconds(start), color)
				+ "<br/>");
	}

	private String setColor(String text, String color) {
		return "<font color=" + color + ">" + text + "</font>";
	}

	private void addMessage(final String string) {
		Thread t = new Thread("addMessage") {
			@Override
			public void run() {

				runOnUiThread(new Runnable() {
					public void run() {
						TextView textView = (TextView) findViewById(R.id.unitTestWindow);
						currentText += "<br/>&nbsp;&nbsp;&nbsp; " + string;
						textView.setText(Html.fromHtml(currentText));
						final ScrollView scrollView = (ScrollView) findViewById(R.id.unitTestScrollView);
						scrollView.post(new Runnable() {

							public void run() {
								L.out("running");
								scrollView.fullScroll(ScrollView.FOCUS_DOWN);
							}
						});
					}
				});

			}
		};
		t.start();

	}

	private boolean runFacilityTest(Facility facility, long started) {
		boolean succeeded = true;
		String facilityID = facility.facilityID;
		Soap.setPlatform(getString(R.string.default_platform));

		long now = new GregorianCalendar().getTimeInMillis();
		addMessage("GetTaskDefinitionFieldsForScreenByFacilityID ... ");
		GetTaskDefinitionFieldsForScreenByFacilityID fields = Soap.getSoap().getTaskDefinitionFieldsForScreenByFacilityID(facilityID);
		if (fields == null || !fields.validate())
			succeeded = false;
		addMessage(printTime(fields, now));

		now = new GregorianCalendar().getTimeInMillis();
		addMessage("ListTaskClassesByFacilityID ... ");
		ListTaskClassesByFacilityID classes = Soap.getSoap().listTaskClassesByFacilityID(facilityID);
		if (classes == null || !classes.validate())
			succeeded = false;
		addMessage(printTime(classes, now));

		now = new GregorianCalendar().getTimeInMillis();
		addMessage("GetTaskDefinitionFieldsDataForScreenByFacilityID ... ");
		GetTaskDefinitionFieldsDataForScreenByFacilityID data = Soap.getSoap().getTaskDefinitionFieldsDataForScreenByFacilityID(facilityID);
		if (data == null || !data.validate())
			succeeded = false;
		addMessage(printTime(data, now));

		now = new GregorianCalendar().getTimeInMillis();
		addMessage("ListDelayTypes ... ");
		ListDelayTypes delays = Soap.getSoap().listDelayTypes();
		if (delays == null || !delays.validate())
			succeeded = false;
		addMessage(printTime(delays, now));

		return succeeded;
	}

	private String getSeconds(long started) {
		long elapsed = ((new GregorianCalendar().getTimeInMillis() + 500) - started) / 1000;
		return (" in " + elapsed + " second" + ((elapsed == 1) ? "" : "s"));
	}

	private String printTime(GJon gJon, long started) {
		String color = "#FF0000";
		if (gJon != null && gJon.validate()) {
			color = "#000000";
		}
		return setColor("  ... " + validate(gJon) + getSeconds(started), color);
	}

	private String printFacility(Facility facility, boolean validated, long started) {
		String color = "#2c96f7";
		String temp = facility.facilityName + " " + ((validated) ? "validated" : "failed") + " in "
				+ getSeconds(started) + "<br>";
		return setColor(temp, color);
	}

	private String validate(GJon gJon) {
		if (gJon == null) {
			return "Failed no data";
		}
		// String className = gJon.getClass().getSimpleName();
		return ((gJon.validate()) ? "Validated" : "Failed");
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		// TextView textView = (TextView) findViewById(R.id.unitTestWindow);
		outState.putString(MESSAGES, currentText);
	}

	@Override
	protected void onRestoreInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		currentText = outState.getString(MESSAGES);
		TextView textView = (TextView) findViewById(R.id.unitTestWindow);
		textView.setText(currentText);
	}

	class Facility {
		String facilityName;
		String facilityID;

		Facility(String facilityName, String facilityID) {
			this.facilityName = facilityName;
			this.facilityID = facilityID;
		}

		@Override
		public String toString() {
			String color = "#2c96f7";
			String temp = facilityName + " - " + facilityID;
			return setColor(temp, color);
		}
	}
}
