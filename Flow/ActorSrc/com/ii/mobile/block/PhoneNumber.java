package com.ii.mobile.block;

import com.ii.mobile.util.L;

public class PhoneNumber {
	private String blockedPhoneNumber = null;
	private static boolean blockCalls = false;

	public String getBlockedPhoneNumber() {
		return blockedPhoneNumber;
	}

	public void setBlockedPhoneNumber(String blockedPhoneNumber) {
		this.blockedPhoneNumber = blockedPhoneNumber;
	}

	public static boolean getBlockCalls() {
		return blockCalls;
	}

	public static void setBlockCalls(boolean flag) {
		L.out("blockCalls: " + flag);
		blockCalls = flag;
	}
}
