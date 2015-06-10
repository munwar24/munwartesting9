package com.ii.mobile.payload;

import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public class PayloadWrapper extends GJon {

	public Payloads payloads = new Payloads();

	public Socket socket = null;

	public String noLongPoll = null;

	public void addPayload(GJon gJon, boolean wantNulls) {
		payloads.addPayload(gJon, wantNulls);
	}

	public static class Payloads {
		public List<Payload> loads = new ArrayList<Payload>();

		public void addPayload(GJon gJon, boolean wantNulls) {
			loads.add(new Payload(gJon, wantNulls));
		}
	}

	public static class Payload {
		public String jSonName = null;
		public String jSon = null;

		public Payload(GJon gJon, boolean wantNulls) {
			jSonName = gJon.getClass().getSimpleName();
			if (wantNulls)
				jSon = gJon.getNewNullJson();
			else
				jSon = gJon.getNewJson();
		}

		@Override
		public String toString() {
			// return "ValidateUser:\n" +
			// mobileUsersFacilityDetails[0].toString() + "\n";
			return "PayloadWrapper:\n" + "payload" + "\n";
		}
	}

	static public PayloadWrapper getGJon(String json) {
		// L.out("json: " + json);
		try {
			return (PayloadWrapper) getJSonObject(json, PayloadWrapper.class);
		} catch (Exception e) {
			L.out("*** ERROR (maybe): " + e);
			return null;
		}
	}

	@Override
	public String getNewJson() {
		Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
		JsonElement je = gson.toJsonTree(payloads);
		JsonObject jo = new JsonObject();
		jo.add("Payloads", je);
		json = jo.toString();
		// json = gson.toJson(taskInformations);
		return json;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}

}
