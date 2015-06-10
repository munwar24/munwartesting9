package com.ii.mobile.flow.authenticate;

import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.home.MyToast;

public enum Login {
	INSTANCE;

	public static String authorization = null;
	public static String cookie = null;
	public static AzureToken azureToken = null;
	public static String userName = null;

	public void setLogin(String cookie, String authorization) {

		if (authorization != null) {
			if (FlowRestService.loginToast)
				MyToast.show("authorization: " + authorization.substring(0, 10) + "...");
			Login.authorization = authorization;
		}
		if (cookie != null) {
			if (FlowRestService.loginToast)
				MyToast.show("cookie: " + cookie.substring(0, 10) + "...");
			Login.cookie = cookie;
		}
	}

	public void reset() {
		Login.authorization = null;
		Login.cookie = null;
	}

	@Override
	public String toString() {
		return "authorization: " + authorization + "\n"
				+ " cookie: " + cookie;
	}
}
