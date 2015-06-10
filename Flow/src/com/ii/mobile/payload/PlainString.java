package com.ii.mobile.payload;

import com.ii.mobile.soap.gson.GJon;

public class PlainString extends GJon {
	String plainString = "";

	public PlainString(String plainString) {
		this.plainString = plainString;
	}

	@Override
	public String toString() {
		return plainString;
	}
}