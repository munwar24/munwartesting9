package com.ii.mobile.flow.types;

import com.google.gson.annotations.SerializedName;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public class SendMessage extends GJon {
	@SerializedName("sendMessageInner")
	public SendMessageInner sendMessageInner = new SendMessageInner();

	public SendMessage(String userName, String sentDate, String message, String to) {
		tickled = GJon.FALSE_STRING;
		sendMessageInner = new SendMessageInner(userName, sentDate, message, to);
	}

	@Override
	public String toString() {
		return sendMessageInner.toString();
	}

	@Override
	public boolean validate() {
		return true;
	}

	static public SendMessage getGJon(String json) {
		L.out("json: " + json);
		SendMessage sendMessage = (SendMessage) getJSonObject(json, SendMessage.class);
		L.out("output: " + sendMessage.getNewJson());
		return sendMessage;
	}

	public String getMessage() {
		return sendMessageInner.message;
	}

	public String getTo() {
		return sendMessageInner.to;
	}
}

class SendMessageInner extends KeepNames {
	@SerializedName("userName")
	public String userName;
	@SerializedName("message")
	public String message;
	@SerializedName("sentDate")
	public String sentDate;
	@SerializedName("to")
	public String to;

	SendMessageInner(String userName, String sentDate, String message, String to) {
		this.userName = userName;
		this.sentDate = sentDate;
		this.message = message;
		this.to = to;

	}

	public SendMessageInner() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "name: " + userName + " sent: " + sentDate + " to: " + to + ": " + message;
	}
}
