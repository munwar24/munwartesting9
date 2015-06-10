package com.ii.mobile.soap.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ii.mobile.util.L;

public class NewXMLValidateUser extends GJon {

	ValidateUserLogin validateUserLogin;
	private long loginTime = 0;

	public static class ValidateUserLogin {

		MobileUsersFacilityDetails[] mobileUsersFacilityDetails;
		boolean tickled = false;

		@Override
		public String toString() {
			return "ValidateUser:\n" + mobileUsersFacilityDetails[0].toString() + "\n";
		}
	}

	static class MobileUsersFacilityDetails {
		// @SerializedName("@attributes")
		// Attributes attributes;

		// String mobilePIN;

		String employeePIN;
		String userID = null;
		// String mobileUserName;
		// String userName;
		String taskNumber;
		String facilityID;
		String mobilePermisions;

		String employeeID;
		String uTCoffset;
		String functionalArea;
		String mobileUserID;
		String employeeStatus;

		String employeeStatusID;
		String autoAssign;
		String facility;
		String taskStatusID;

		String functionAreaID;
		String taskStatus;
		String timeZone;
		String mobileRoles;
		String dayLightSavingTime;
		String employeeName;

		@Override
		public String toString() {
			// return "\n  mobilePin: " + mobilePIN
			return "\n  employeePIN: " + employeePIN
					+ "\n  nuserId: " + userID
					+ "\n  mobileUserName: " + employeeName
					+ "\n  taskNumber: " + taskNumber
					+ "\n  taskStatus: " + taskStatus
					+ "\n  facilityID: " + facilityID
					+ "\n  mobilePermisions: " + mobilePermisions
					+ "\n  employeeID: " + employeeID
					+ "\n  utCoffset: " + uTCoffset
					+ "\n  functionalArea: " + functionalArea
					+ "\n  mobileuserID: " + mobileUserID
					+ "\n  employeeStatus: " + employeeStatus
					+ "\n  employeeStatusID: " + employeeStatusID
					+ "\n  autoAssign: " + autoAssign
					+ "\n  facility: " + facility
					+ "\n  taskStatusID: " + taskStatusID
					+ "\n  timeZone: " + timeZone
					+ "\n  mobileRoles: " + mobileRoles
					+ "\n  dayLightSavingTime: " + dayLightSavingTime
					+ "\n  employeeName: " + employeeName;
		}
	}

	static public NewXMLValidateUser getGJon(String json) {
		json = makeSureIsAnArray("UsersFacilityDetails", json);
		NewXMLValidateUser gUser = (NewXMLValidateUser) getJSonObject(json, NewXMLValidateUser.class);
		L.out("validate: " + gUser);
		return gUser;
	}

	@Override
	public boolean validate() {
		if (validateUserLogin != null
				&& validateUserLogin.mobileUsersFacilityDetails != null)
			validated = true;
		return validated;
	}

	public String getFacilityID() {
		if (isValidated())
			return validateUserLogin.mobileUsersFacilityDetails[0].facilityID;
		else {
			return null;
		}
	}

	public String getEmployeeID() {
		if (isValidated())
			return validateUserLogin.mobileUsersFacilityDetails[0].employeeID;
		else {
			return null;
		}
	}

	public String getEmployeeStatus() {
		if (isValidated())
			return validateUserLogin.mobileUsersFacilityDetails[0].employeeStatus;
		else {
			return null;
		}
	}

	public String getTaskStatus() {
		if (isValidated())
			return validateUserLogin.mobileUsersFacilityDetails[0].taskStatus;
		else {
			return null;
		}
	}

	@Override
	public String toString() {
		if (!isValidated())
			return "";
		return validateUserLogin.toString();
	}

	public String getMobilePIN() {
		if (isValidated())
			return validateUserLogin.mobileUsersFacilityDetails[0].employeePIN;
		else {
			return null;
		}
	}

	public void setEmployeeStatus(String status) {
		if (isValidated())
			validateUserLogin.mobileUsersFacilityDetails[0].employeeStatus = status;

	}

	public void setTaskStatus(String status) {
		if (isValidated())
			validateUserLogin.mobileUsersFacilityDetails[0].taskStatus = status;
	}

	public boolean getTickled() {
		if (isValidated())
			return validateUserLogin.tickled;
		else {
			return false;
		}
	}

	public void setTickled(boolean flag) {
		if (isValidated())
			validateUserLogin.tickled = flag;

	}

	@Override
	public String getNewJson() {
		Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE).create();
		JsonElement je = gson.toJsonTree(validateUserLogin);
		JsonObject jo = new JsonObject();
		jo.add("ValidateUserLogin", je);
		json = jo.toString();
		json = json.replace("usersFacilityDetails", "UsersFacilityDetails");
		// json = gson.toJson(taskInformations);
		return json;
	}

	public void setLoginTime(long timeInMillis) {
		loginTime = timeInMillis;

	}

	public long getLoginTime() {
		return loginTime;
	}

	public void setTaskNumber(String taskNumber) {
		validateUserLogin.mobileUsersFacilityDetails[0].taskNumber = taskNumber;
	}

	public String getTaskNumber() {
		return validateUserLogin.mobileUsersFacilityDetails[0].taskNumber;
	}

	public String getMobileUserName() {
		return validateUserLogin.mobileUsersFacilityDetails[0].employeeName;
	}
}
