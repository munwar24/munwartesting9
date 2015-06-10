package com.ii.mobile.soap.gson;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.ii.mobile.util.L;

public class ValidateUser extends GJon {

	ValidateUserLogin validateUserLogin = new ValidateUserLogin();
	private long loginTime = 0;

	public class ValidateUserLogin {
		UsersFacilityDetails[] usersFacilityDetails = new UsersFacilityDetails[1];
		// MobileUsersFacilityDetails[] mobileUsersFacilityDetails;
		boolean tickled = false;

		@Override
		public String toString() {
			// return "ValidateUser:\n" +
			// mobileUsersFacilityDetails[0].toString() + "\n";
			return "ValidateUser:\n" + usersFacilityDetails[0].toString() + "\n";
		}
	}

	public ValidateUser() {
		validateUserLogin.usersFacilityDetails[0] = new UsersFacilityDetails();
		validated = true;
	}

	static class UsersFacilityDetails {
		// static class MobileUsersFacilityDetails {
		// @SerializedName("@attributes")
		// Attributes attributes;

		String mobilePIN;

		// String employeePIN;
		String userID = null;
		String mobileUserName;
		String userName;
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
		String functionalAreaID;
		String taskStatus;
		String timeZone;
		String mobileRoles;
		String dayLightSavingTime;
		String employeeName;

		@Override
		public String toString() {
			// return "\n  mobilePin: " + mobilePIN
			return "\n  mobilePIN: " + mobilePIN
					+ "\n  nuserId: " + userID
					+ "\n  mobileUserName: " + mobileUserName
					+ "\n  taskNumber: " + taskNumber
					+ "\n  taskStatus: " + taskStatus
					+ "\n  facilityID: " + facilityID
					+ "\n  mobilePermisions: " + mobilePermisions
					+ "\n  employeeID: " + employeeID
					+ "\n  utCoffset: " + uTCoffset
					+ "\n  functionalArea: " + functionalArea
					+ "\n  functionAreaID: " + functionAreaID
					+ "\n  functionalAreaID: " + functionalAreaID
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

	static public ValidateUser getGJon(String json) {
		json = makeSureIsAnArray("UsersFacilityDetails", json);
		ValidateUser gUser = (ValidateUser) getJSonObject(json, ValidateUser.class);
		// L.out("validate: " + gUser);
		return gUser;
	}

	@Override
	public boolean validate() {
		if (validateUserLogin != null
				&& validateUserLogin.usersFacilityDetails != null)
			validated = true;
		return validated;
	}

	public String getFacilityID() {
		if (isValidated())
			return validateUserLogin.usersFacilityDetails[0].facilityID;
		else {
			return null;
		}
	}

	public String getEmployeeID() {
		if (isValidated())
			return validateUserLogin.usersFacilityDetails[0].employeeID;
		else {
			return null;
		}
	}

	public String getEmployeeStatus() {
		if (isValidated())
			return validateUserLogin.usersFacilityDetails[0].employeeStatus;
		else {
			return null;
		}
	}

	public String getTaskStatus() {
		if (isValidated())
			return validateUserLogin.usersFacilityDetails[0].taskStatus;
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
			return validateUserLogin.usersFacilityDetails[0].mobilePIN;
		else {
			return null;
		}
	}

	public void setEmployeeStatus(String status) {
		if (isValidated())
			validateUserLogin.usersFacilityDetails[0].employeeStatus = status;

	}

	public void setTaskStatus(String status) {
		L.out("setTaskStatus: " + status);
		if (isValidated())
			validateUserLogin.usersFacilityDetails[0].taskStatus = status;
		else {
			L.out("Unable to setTaskStatus: " + status);
		}
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
		// L.out("taskNumber: " + taskNumber + L.p());
		validateUserLogin.usersFacilityDetails[0].taskNumber = taskNumber;
	}

	public String getTaskNumber() {
		return validateUserLogin.usersFacilityDetails[0].taskNumber;
	}

	public String getMobileUserName() {
		return validateUserLogin.usersFacilityDetails[0].employeeName;
	}

	public String getFunctionalArea() {
		if (isValidated())
			return validateUserLogin.usersFacilityDetails[0].functionalArea;
		else {
			return null;
		}
	}
}
