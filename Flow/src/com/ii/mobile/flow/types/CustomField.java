package com.ii.mobile.flow.types;

import com.google.gson.annotations.SerializedName;

public class CustomField extends KeepNames {
	@SerializedName("name")
	public String name;

	@SerializedName("control")
	public String control;

	@SerializedName("value")
	public String value;
	@SerializedName("controlType")
	public String controlType;

	@Override
	public String toString() {
		return "   name: " + name
				+ " value: " + value
				+ " controlType: " + controlType
				+ " control: " + control;
	}

	public CustomField() {
	}
}