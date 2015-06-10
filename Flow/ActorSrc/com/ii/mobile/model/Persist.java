package com.ii.mobile.model;

public class Persist {

	transient private int id = -1;
	transient protected String status = "Available";
	transient private String accessTime = "";
	transient public String ider;

	public int get_Id() {
		return id;
	}

	public void set_Id(int id) {
		this.id = id;
	}

	public String getAccessTime() {
		return accessTime;
	}

	public void setAccessTime(String accessTime) {
		this.accessTime = accessTime;
	}

	protected String getStatus() {
		return status;
	}

	public String getSimpleName() {
		String temp = getClass().getName();
		int index = temp.lastIndexOf(".");
		// System.out.println("getSimpleName: " + temp + " " + index);
		String foo = temp.substring(index + 1, temp.length());
		foo = foo.toLowerCase();
		// System.out.println("getSimpleName result: " + foo);
		return foo;
	}

	public void setStatus(String status) {
		this.status = status;

	}

	@Override
	public String toString() {
		return "Persist (" + id + ")";
	}

}
