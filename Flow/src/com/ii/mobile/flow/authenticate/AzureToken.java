package com.ii.mobile.flow.authenticate;

public class AzureToken {

	public String userName = null;
	public String authenticationToken = null;

	public AzureToken(String userName, String authenticationToken) {
		this.userName = userName;
		this.authenticationToken = authenticationToken;
	}

	@Override
	public String toString() {
		return "username: " + userName + " token: " + authenticationToken.length();
	}

	public String toStringLong() {
		return "username: " + userName + " token: " + authenticationToken;
	}
}
