package com.ii.mobile.util;

public class M {
	static String buffer = "";

	public static void init() {
		buffer = "";
	}

	public static String print() {
		L.out(buffer);
		return buffer;
	}

	public static String getBuffer() {
		return buffer;
	}

	public static void out(String string) {
		buffer += "\n" + string;
		L.out(string);
	}
}
