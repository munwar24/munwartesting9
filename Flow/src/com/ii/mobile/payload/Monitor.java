package com.ii.mobile.payload;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.BatteryManager;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ii.mobile.transport.R; // same package
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public class Monitor extends GJon {

	transient static private Battery battery = null;

	public MonitorStatus monitorStatus = new MonitorStatus();
	private static Activity activity;

	public Monitor(Activity activity) {
		Monitor.activity = activity;
		battery = new Battery(activity);
	}

	public Monitor() {

	}

	public static class MonitorStatus {
		public String wantMonitors = null;

		public List<String> monitorSlots = null;
		public List<String> monitorValues = null;
		public List<String> monitorReports = null;

		@Override
		public String toString() {
			if (monitorSlots == null || monitorSlots.size() == 0)
				return "No Slot/Values";
			String temp = "";
			for (int i = 0; i < monitorSlots.size(); i++)
				temp += monitorSlots.get(i) + " : " + monitorValues.get(i) + " ";
			return "Status:\n"
					+ "\n" + temp;
		}

		public void addSlotValue(String slot, String value) {
			if (monitorSlots == null) {
				monitorSlots = new ArrayList<String>();
				monitorValues = new ArrayList<String>();
			}
			monitorSlots.add("_" + slot);
			monitorValues.add("_" + value);
		}
	}

	public void populate() {
		Resources resources = activity.getResources();
		monitorStatus.addSlotValue("_User", "Bay");
		monitorStatus.addSlotValue("_Tasks", "7");
		monitorStatus.addSlotValue("_OnJob", "2h30m");
		monitorStatus.addSlotValue("_Break", "s-l-s-s");
		monitorStatus.addSlotValue("_Bat Lev", battery.batteryLevel + "");
		monitorStatus.addSlotValue("_Bat v", battery.batteryVoltage + "");
		monitorStatus.addSlotValue("_Temp", battery.batteryExtraVoltage + "");
		monitorStatus.addSlotValue("_Prod", resources.getBoolean(R.bool.isProduction) + "");
		monitorStatus.addSlotValue("_Ver", resources.getString(R.string.crothall_version) + "");
	}

	static public Monitor getGJon(String json) {
		L.out("json: " + json);

		try {
			return (Monitor) getJSonObject(json, Monitor.class);
		} catch (Exception e) {
			L.out("*** ERROR (maybe): " + e);
			return null;
		}
	}

	public String getPrettyReport() {
		String temp = "<br/>";
		List<String> states = monitorStatus.monitorReports;
		L.out("states: " + states.size());
		for (String state : states) {
			String[] splits = state.split("_");
			L.out("doing splits: " + splits.length);
			int i = 1;
			int lineCount = 0;
			while (i < splits.length - 1) {
				String split = splits[i] + ": " + splits[i + 1];
				temp += split + " ";
				lineCount += 1;
				if (lineCount % 3 == 0)
					temp += "<br/>";
				i += 2;
			}

		}
		L.out("temp: " + temp);
		return temp;
	}

	@Override
	public String getNewJson() {

		Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
		JsonElement je = gson.toJsonTree(monitorStatus);
		JsonObject jo = new JsonObject();
		jo.add("MonitorStatus", je);
		json = jo.toString();
		// json = gson.toJson(taskInformations);
		return json;
	}

	@Override
	public String toString() {
		if (monitorStatus != null)
			return monitorStatus.toString();
		return "ERROR MonitorStatus is null!";
	}

	// public void addReport(Monitor monitor) {
	// if (monitorStatus.monitorReports == null)
	// monitorStatus.monitorReports = new ArrayList<String>();
	// monitorStatus.monitorReports.add(monitor.getJson());
	// }

	public void addReport(Monitor monitor) {
		if (monitorStatus.monitorReports == null)
			monitorStatus.monitorReports = new ArrayList<String>();
		monitorStatus.monitorReports.add(monitor.getLongString());
	}

	private String getLongString() {
		if (monitorStatus.monitorSlots.size() == 0)
			return "Noslot_Values";
		String temp = "";
		for (int i = 0; i < monitorStatus.monitorSlots.size(); i++)
			temp += monitorStatus.monitorSlots.get(i).replaceFirst("_", "") + "|"
					+ monitorStatus.monitorValues.get(i).replaceFirst("_", "") + " ";

		return temp;
	}

}

class Battery {
	int batteryScale = -1;
	int batteryLevel = -1;
	int batteryVoltage = -1;
	int batteryExtraVoltage = -1;

	public Battery(Activity activity) {
		BroadcastReceiver batteryReceiver = new BroadcastReceiver() {

			@Override
			public void onReceive(Context context, Intent intent) {
				batteryLevel = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
				batteryScale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
				batteryExtraVoltage = intent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
				batteryVoltage = intent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
				L.out("level is " + batteryLevel + "/" + batteryScale + ", temp is "
						+ batteryExtraVoltage / 10. + " C"
						+ ", voltage is " + batteryVoltage / 1000.0f + " V");
			}
		};
		IntentFilter filter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
		activity.registerReceiver(batteryReceiver, filter);
	}

}
