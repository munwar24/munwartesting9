package com.ii.mobile.flowing;

import com.ii.mobile.soap.gson.GJon;

public interface SyncCallback {
	void callback(GJon gJon, String payloadName);
}