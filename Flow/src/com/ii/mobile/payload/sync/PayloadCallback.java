package com.ii.mobile.payload.sync;

import com.google.gson.Gson;

public interface PayloadCallback {
	void callback(Gson gson);
}