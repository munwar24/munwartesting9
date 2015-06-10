package com.ii.mobile.flowing;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import com.ii.mobile.flow.authenticate.Login;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.CustomField;
import com.ii.mobile.flow.types.Event;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flow.types.SelectClassTypesByFacilityId;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.instantMessage.InstantMessageFragment;
import com.ii.mobile.util.L;
import com.ii.mobile.util.PrettyPrint;

public class FlowRestServiceOld {
	public static final String SIGN_ON = "signOn";
	public static final String GET_ACTOR_STATUS = "GetActorStatus";
	public static final String GET_ACTION_STATUS = "GetActionStatus";
	public static final String GET_ACTION_HISTORY = "GetActionHistory";
	public static final String SELECT_CLASS_TYPES_BY_FACILITY_ID = "SelectClassTypesByFacilityId";
	public static final String SELECT_LOCATIONS = "SelectLocations";
	public static final String SELECT_AVAILABLE_ZONES = "SelectAvailableZones";
	public static final String UPDATE_SELECT_AVAILABLE_ZONES = "UpdateSelectAvailableZones";
	public static final String ACTOR_STATUS_UPDATE = "ActorStatusUpdate";
	public static final String ACTION_STATUS_UPDATE = "ActionStatusUpdate";
	public static final String CREATE_ACTION = "CreateAction";
	public static final String ACTION_SELECT = "ActionSelect";
	public static final String SEND_MESSAGE = "SendMessage";
	//
	// public static String SERVER =
	// "http://syncpulse.cloudapp.net:1337/flow/";

	// public static String SERVER = "http://66.155.100.40:1337/flow/";
	public static String SERVER = "https://npc.crothall.com/flow/";
	// public static String SERVER =
	// "http://192.168.1.3:1337/flow/";

	private static final int TIME_OUT = 5000;
	private static String emailAddress = null;

	int tryCount = 0;

	public String execute(String flowCallName, GetActorStatus getActorStatus) {
		// SERVER = User.getUser().getPlatform();
		if (Login.authorization == null) {
			L.out("authorization: " + Login.authorization);
			return "";
		}
		L.out("execute flowCallName: " + flowCallName);
		return execute(getContent(flowCallName, getActorStatus));
	}

	// public String execute(String flowCallName, GetActionStatus
	// getActionStatus) {
	// if (Login.authorization == null)
	// return "";
	// L.out("execute flowCallName: " + flowCallName);
	// return execute(getContent(flowCallName, getActionStatus));
	// }

	public String execute(GetActionStatus getActionStatus, Event event) {
		// SERVER = User.getUser().getPlatform();
		if (Login.authorization == null)
			return null;
		return execute(getContent(getActionStatus, event));
	}

	public void loginWithPasswordOld(String userName, String password) {

		emailAddress = userName;

		String urlString =
				SERVER + "signOn";
		L.out("urlString: " + urlString);
		try {
			String content = "mobileLogin=1&user_id=" + userName + "&password=" +
					password;
			if (TransportActivity.showToast)
				MyToast.show("UrlString for login: " + urlString);
			L.out("content: " + content);
			URL url = new URL(urlString);
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection httpURLConnection = (HttpURLConnection)
					url.openConnection();
			httpURLConnection.setConnectTimeout(TIME_OUT);
			httpURLConnection.setReadTimeout(TIME_OUT);
			httpURLConnection.setInstanceFollowRedirects(false);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestProperty("Accept", "application/json");
			httpURLConnection.addRequestProperty("Accept-Language",
					"en-US,en;q=0.8");
			httpURLConnection.addRequestProperty("User-Agent", "Mozilla");

			L.out("ready write");
			byte[] newBytes = content.getBytes();
			OutputStream outputStream = httpURLConnection.getOutputStream();
			outputStream.write(newBytes);
			outputStream.close();

			L.out("doing cookies");
			getCookies(httpURLConnection);
			if (httpURLConnection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ httpURLConnection.getResponseCode());
			}
			L.out("did cookies");
			httpURLConnection.disconnect();

		} catch (Exception e) {
			L.out("error:" + e + " urlString: " + urlString + L.p());

		}
	}

	public final static boolean loginToast = false;

	public void loginWithPassword(String userName, String password) {
		if (loginToast)
			MyToast.show("New Login step 1: " + userName);
		loginWithPasswordStepOne(userName, password);

		String temp = "{\"criteria\":{\"store\":\"cycligent.startup.set\"},\"target\":\"iiCache\",\"request\":1,\"location\":\"flow.\"}";
		loginWithPasswordStepTwo(temp);
	}

	private void loginWithPasswordStepOne(String userName, String password) {

		emailAddress = userName;

		String urlString =
				SERVER + "signOn?noRedirect=true&mobileLogin=1";
		L.out("urlString: " + urlString);
		try {
			String content = "user_id=" + userName + "&password=" + password;
			if (TransportActivity.showToast)
				MyToast.show("UrlString for 1st login: " + urlString);
			L.out("content: " + content);
			URL url = new URL(urlString);
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setConnectTimeout(TIME_OUT);
			httpURLConnection.setReadTimeout(TIME_OUT);
			httpURLConnection.setInstanceFollowRedirects(false);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestProperty("Accept", "application/json");
			httpURLConnection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			httpURLConnection.addRequestProperty("User-Agent", "Mozilla");

			L.out("ready write");
			byte[] newBytes = content.getBytes();
			OutputStream outputStream = httpURLConnection.getOutputStream();
			outputStream.write(newBytes);
			outputStream.close();

			L.out("doing cookies");
			getCookies(httpURLConnection);
			if (httpURLConnection.getResponseCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : "
						+ httpURLConnection.getResponseCode());
			}
			L.out("did cookies");
			httpURLConnection.disconnect();

		} catch (Exception e) {
			L.out("error:" + e + " urlString: " + urlString + L.p());
		}
	}

	public String loginWithPasswordStepTwo(String parsedContent) {
		// L.out("execute server read");
		// String urlString =
		// "http://chp-s1-web-cs.cloudapp.net/flow/provider.aspx";
		// String urlString = "http://192.168.1.6:1337/flow/provider.aspx";
		// String urlString =
		// "http://syncpulse.cloudapp.net:1337/flow/provider.aspx";
		String urlString =
				SERVER + "provider.aspx";
		if (loginToast)
			MyToast.show("New Login step 2: " + urlString);
		Login login = Login.INSTANCE;
		if (login == null || Login.cookie == null) {
			if (loginToast)
				MyToast.show("ERROR: login is null! : " + login);
			return null;
		}
		try {

			// L.out("urlString: " + urlString);
			// L.out("cookie: " + cookie);
			URL url = new URL(urlString);
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setInstanceFollowRedirects(false);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestProperty("Accept", "application/json");
			httpURLConnection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			httpURLConnection.addRequestProperty("User-Agent", "Mozilla");
			L.out("cookie: " + USER_ROLE + "=" + Login.cookie);
			httpURLConnection.setRequestProperty("Cookie", USER_ROLE + "=" + Login.cookie);

			byte[] newBytes = parsedContent.getBytes();
			// L.out("newBytes: " + newBytes.length);
			// L.out("ready to write: \n" + parsedContent);
			// UpdateController.INSTANCE.doCallback(new PlainString("Write: " +
			// parsedContent), UpdateController.PLAIN_STRING);

			OutputStream outputStream = httpURLConnection.getOutputStream();
			outputStream.write(newBytes);
			outputStream.close();
			L.out("done write");

			InputStream inputStream = httpURLConnection.getInputStream();
			String responseMessage = httpURLConnection.getResponseMessage();
			if (!responseMessage.equals("OK")) {
				L.out("ERROR: response message is not ok: " + responseMessage);
				return null;
			}
			String reply = convertStreamToString(inputStream);
			// L.out("read: " + reply.length());
			// L.out("read: " + reply);
			parseReplyForCredential(reply);
			// UpdateController.INSTANCE.doCallback(new
			// PlainString("selectLocations: "
			// + PrettyPrint.formatPrint(reply)),
			// UpdateController.PLAIN_STRING);

			inputStream.close();
			httpURLConnection.disconnect();

			if (httpURLConnection.getResponseCode() != 200) {

				throw new RuntimeException("Failed : HTTP error code : "
						+ httpURLConnection.getResponseCode());
			}
			return reply;
		} catch (Exception e) {
			L.out("error:" + e + " urlString: " + urlString + L.p());
			return null;
		}
	}

	private static String USER_ROLE = "user_role";
	private static String AUTHORIZATION = "authorization";

	private void getCookies(HttpURLConnection httpURLConnection) {
		// L.out("getting cookies: " + httpURLConnection);
		// Map<String, List<String>> foo = httpURLConnection.getHeaderFields();
		// L.out("foo: " + foo);
		List<String> headers =
				httpURLConnection.getHeaderFields().get("Set-Cookie");

		// L.out("headers: " + headers);
		if (headers != null)
			for (String entry : headers)
			{
				// L.out("Prop: " + entry);
				parsePropList(entry.split(";"));
			}
		else {
			L.out("fail headers: " + headers);
		}
	}

	private final String authorizationToken = "authorization\":\"";

	private void parseReplyForCredential(String reply) {
		int index = reply.indexOf(authorizationToken);
		// L.out("index: " + index);
		if (index != -1) {
			int jIndex = reply.indexOf("\"", index + authorizationToken.length() + 1);
			// L.out("jIndex: " + jIndex);

			String authorization = reply.substring(index + authorizationToken.length(), jIndex);
			// L.out("authorization: " + authorization);
			Login.INSTANCE.setLogin(null, authorization);
		}
	}

	private void parsePropList(String[] split) {
		String authorization = null;
		String cookie = null;

		for (int i = 0; i < split.length; i++) {
			String temp = split[i];
			if (temp.contains(USER_ROLE)) {
				cookie = temp.substring(temp.indexOf(USER_ROLE) + USER_ROLE.length() + 1, temp.length());
			}
		}
		if (authorization != null || cookie != null)
			Login.INSTANCE.setLogin(cookie, authorization);
		else
			L.out("failed to get data from: " + split);
	}

	// ActionStatus update

	private String getContent(GetActionStatus getActionStatus, Event event) {
		String content = "";
		// String routine = "7069521ceeaf561b2081aeda";

		// MyToast.show("getContent optionId: " + event.getOptionId());

		content = "{\"call\":{\"id\":25,\"name\":\"@action.actionStatusUpdate\",\"data\":{"
				+ getItem("_id", getActionStatus.getActionId(), true)
				+ getItem("actionStatusType_id", event.actionStatusId, true)
				+ event.getDelayType()
				+ getItem("actionDate", toDateNew(event.time), false)
				// + getItem("actionType_id", routine, false)
				+ "}},"
				+ "\"target\":\"iiCall\",\"request\":14,\"location\":\"flow.testCenter.unitTester.\","
				+ "\"authorization\":\""
				+ Login.authorization
				+ "\"}";

		PrettyPrint.prettyPrint(content, true);
		return content;
	}

	// actionDate: "02/12/2014 09:30 AM"
	/**
	 * Convert the time into a string date form.
	 * 
	 * @param time
	 * @return
	 */
	public static String toDate(String stringTime) {
		// L.out("toDate: " + stringTime);
		// L.out("toDateNew: " + toDateNew(stringTime));
		long time = L.getLong(stringTime);
		// System.out.println("L.toDate obsolete: "+L.p(10));
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
		sdf.setTimeZone(calendar.getTimeZone());
		return sdf.format(calendar.getTime());
	}

	private static final SimpleDateFormat zuluFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

	public static String toDateNew(String stringTime) {
		long time = L.getLong(stringTime);
		// System.out.println("L.toDate obsolete: "+L.p(10));
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);

		// SimpleDateFormat sdf = new
		// SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
		zuluFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		String test = zuluFormat.format(calendar.getTime());
		InstantMessageFragment.convertDate(test);
		return test;
	}

	public static String toNowDate() {
		try {
			GregorianCalendar calendar = new GregorianCalendar();
			zuluFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			String test = zuluFormat.format(calendar.getTime());
			// InstantMessageFragment.convertDate(test);
			return test;
		} catch (Exception e) {
			L.out("toNowDate error: " + e);
		}
		return null;
	}

	public static String toCreationDate(String stringTime) {
		// L.out("toCreationDate: " + stringTime);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss aa");
		Date date;
		try {
			date = simpleDateFormat.parse(stringTime);
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setGregorianChange(date);
			zuluFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			String test = zuluFormat.format(calendar.getTime());
			// InstantMessageFragment.convertDate(test);
			return test;
		} catch (ParseException ex) {

		}
		return null;
	}

	public static String toDOB(String stringTime) {
		// L.out("stringTime: " + stringTime);
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		Date date;
		try {
			date = simpleDateFormat.parse(stringTime);
			// L.out("date: " + date.getYear());
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.set(date.getYear() + 1900, date.getMonth(), date.getDay() + 1);
			// calendar.setGregorianChange(date);
			zuluFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			String test = zuluFormat.format(calendar.getTime());
			// InstantMessageFragment.convertDate(test);
			test = (date.getYear() + 1900) + "/" + (date.getMonth() + 1) + "/" + (date.getDay() + 1);
			test = (date.getMonth() + 1) + "/" + (date.getDay() + 1) + "/" + (date.getYear() + 1900);
			// L.out("test: " + test);
			return test;
		} catch (ParseException ex) {
			L.out("exception in date: " + ex);
		}
		return null;
	}

	public String execute(String flowCallName, GetActionStatus getActionStatus, boolean create) {
		if (Login.authorization == null)
			return "";
		// L.out("execute flowCallName: " + flowCallName);
		// String temp = getContent(flowCallName, getActionStatus, create);

		return execute(getContent(flowCallName, getActionStatus, create));

	}

	public String execute(String flowCallName, String arg1, String arg2) {
		if (Login.authorization == null)
			return "";
		// L.out("execute flowCallName: " + flowCallName);
		return execute(getContent(flowCallName, arg1, arg2));
	}

	// actionStatus create

	private String getContent(String flowCallName, GetActionStatus getActionStatus, boolean create) {
		// L.out("getActionStatus: " + getActionStatus.getCreatedDate());
		String content = "";
		// GetActorStatus getActorStatus = UpdateController.getActorStatus;
		// if (getActorStatus == null)
		// return null;
		// boolean isolation = getIsolationBoolean(getActionStatus);

		String start = getStart(getActionStatus);
		String destination = getDestination(getActionStatus);
		String dob = "";
		// L.out("dob check: " + getActionStatus.getDateEdited());
		// L.out("dob: check " + getActionStatus.getPatientBirthDate());
		if (getActionStatus.getDateEdited()) {
			dob = getActionStatus.getTarget().patient.birthDate;
			// L.out("dob: " + dob);
			if (dob != null)
				dob = toDOB(dob);
			// dob = reverseDOB(dob);
			// L.out("reverseDOB: " + dob);
			// dob = toDateString(dob);
			// L.out("toDateNew: " + dob);
		}

		// String routine = "7069521ceeaf561b2081aeda";
		// if (getActionStatus.getStatAction())
		// routine = "7069521ceeaf561b2081aedb";
		// L.out("functionalAreaType_id" +
		// getActionStatus.getFunctionalAreaTypeId());

		String note = "";
		if (getActionStatus.getNotes() != null)
			// note = getActionStatus.getNotes().replace("\n", " ");
			note = getActionStatus.getNotes();
		// L.out("note: " + note.length() + " #" + note + "#");
		note = note.replaceAll("\n", " ");
		// L.out("test now date: " + note);

		String scheduleDate = addScheduleDate(getActionStatus);
		// L.out("scheduleDate: " + scheduleDate);

		content = "{\"call\":{\"id\":2,\"name\":\"@action.actionCreate\",\"data\":{"

				+ getItem("facility_id", getActionStatus.getFacilityId(), true)
				+ getItem("actionClassType_id", getActionStatus.getClassTypeId(), true)
				+ getItem("actionClassType_name", getActionStatus.getClassName(), true)
				+ getItem("applicationType_id", "52a9c4a105f44e11ffd99eca", true)
				+ getHeader("created")
				// + getItem("oldat", getActionStatus.getCreatedDate(), true)
				+ getItem("at", toCreationDate(getActionStatus.getCreatedDate()), true)
				+ getItem("by", getActionStatus.getActorName(), false)
				+ getFooter()
				+ scheduleDate
				+ getItem("selfTask", true, true)
				+ getItem("actor_id", getActionStatus.getActorId(), true)
				+ getItem("note", note, true)
				+ getItem("equipmentType_id", getActionStatus.getEquipmentId(), true)
				+ getItem("isolation", getActionStatus.getIsolationPatient(), true)
				+ getItem("modeType_id", getActionStatus.getModeId(), true)
				+ getItem("actionType_id", getActionStatus.getActionTypeId(), true)
				// + getItem("scheduleDate", "", true)
				+ getItem("priority", getActionStatus.getPriority(), true)
				+ getItem("actionStatusType_id", StaticFlow.ACTION_ASSIGNED, true)
				+ getItem("functionalAreaType_id", getActionStatus.getFunctionalAreaTypeId(), true)
				+ getHeader("requestor")
				+ getItem("name", getActionStatus.getActorName(), true)
				+ getItem("phone", "999-999-9999", true)
				+ getItem("email", "default@crothall.com", false)
				+ getFooter()
				+ start
				+ getHeader("patient")
				+ getItem("medicalRecordNumber", getActionStatus.getMedicalRecordNumber(), true)
				+ getItem("genderType_id", getActionStatus.getGenderTypeId(), true)
				+ getItem("_id", getActionStatus.getPatientId(), true)
				+ getItem("firstName", getActionStatus.getPatientFirstName(), true)
				+ getItem("lastName", getActionStatus.getPatientLastName(), true)
				+ getItem("birthDate", dob, false)
				+ getFooter()
				+ getCustomFields(getActionStatus)
				+ destination
				+ getHeader("classType")

				+ getItem("_id", getActionStatus.getClassTypeId(), true)
				+ getItem("name", getActionStatus.getClassName(), false)
				+ "}}},"
				+ "\"target\":\"iiCall\",\"authorization\":\""
				+ Login.authorization
				+ "\",\"request\":3,\"location\":\"flow.testCenter.unitTester."
				+ "\"}";
		// L.out("content: " + content);
		// add created
		PrettyPrint.prettyPrint(content);
		return content;
	}

	private String getCustomFields(GetActionStatus getActionStatus) {
		CustomField[] customField = getActionStatus.getTarget().customField;
		if (customField == null || customField.length == 0)
			return "";
		String temp = getHeader("customField");
		boolean first = true;
		for (CustomField custom : customField) {
			L.out("custom: " + custom);
			if (custom.value != null && !custom.value.equals("")) {
				if (!custom.control.startsWith("date") || getActionStatus.getDateEdited(true)) {
					if (!first)
						temp += ",";
					String name = custom.name;
					if (custom.control.startsWith("date"))
						name = toDOB(name);
					temp += "\"" + custom.name + "\":\"" + custom.value + "\"";
					first = false;
				}
			}
		}
		if (first)
			return "";
		temp += getFooter();
		// MyToast.show("temp: " + temp);
		return temp;
	}

	private String addScheduleDate(GetActionStatus getActionStatus) {
		String actionClassType_id = getActionStatus.getClassTypeId();
		// L.out("actionClassType_id: " + actionClassType_id);
		if (UpdateController.selectClassTypesByFacilityId == null
				|| UpdateController.selectClassTypesByFacilityId.haveScheduleDate(actionClassType_id))
			return getItem(SelectClassTypesByFacilityId.SCHEDULE_DATE, toNowDate(), true);
		return "";
	}

	private String getDestination(GetActionStatus getActionStatus) {
		// L.out("getActionStatus.getDestinationId(): " +
		// getActionStatus.getDestinationId());
		if (getActionStatus.getDestinationId() == null)
			return "";
		return getHeader("destination")
				+ getItem("_id", getActionStatus.getDestinationId(), true)
				+ getItem("name", getActionStatus.getDestinationName(), false)
				+ getFooter();
	}

	private String getStart(GetActionStatus getActionStatus) {
		// L.out("getActionStatus.getStartId(): " +
		// getActionStatus.getStartId());
		if (getActionStatus.getStartId() == null)
			return "";
		return getHeader("start")
				+ getItem("_id", getActionStatus.getStartId(), true)
				+ getItem("name", getActionStatus.getStartName(), false)
				+ getFooter();
	}

	private String getContent(String flowCallName, GetActorStatus getActorStatus) {
		// L.out("flowCallName: " + flowCallName);
		// L.out("getActorStatus: " + getActorStatus);
		String content = "";
		// if (flowCallName.equals(ACTION_STATUS_UPDATE)) {
		// content =
		// "{\"call\":{\"id\":25,\"name\":\"@action.actionStatusUpdate\",\"data\":{"
		// + "\"_id\":\""
		// + getActorStatus.getActionId()
		// + "\",\"actionStatusType_id\":\""
		// + getActorStatus.getActionStatusId()
		//
		// // + ","
		// // + getDelayItem(statusWrapper)
		// + "\"}},"
		// +
		// "\"target\":\"iiCall\",\"request\":14,\"location\":\"flow.testCenter.unitTester.\","
		// + "\"authorization\":\""
		// + Login.authorization
		// + "\"}";
		// PrettyPrint.prettyPrint(content, true);
		//
		// } else
		if (flowCallName.equals(GET_ACTION_HISTORY)) {
			// String date =
			// "\"requestedDate\":{\"$gt\":\"ISODate(\"2013-10-29\")\"}";
			content = "{\"call\":{\"id\":10,\"name\":\"@action.mobileSelectActions\",\"data\":{"
					+ getItem("actor._id", getActorStatus.getActorId(), false)
					// + getItem("facility._id", getActorStatus.getFacilityId(),
					// true)
					// + getItem("actionStatusType_id",
					// StaticFlow.ACTION_COMPLETED, false)
					// + date
					// + ",\"limit\":5"
					+ "}},\"target\":\"iiCall\",\"request\":11,\"location\":\"flow.testCenter.unitTester.\","
					+ "\"authorization\":\""
					+ Login.authorization
					+ "\"}";
			// L.out("GET_ACTION_HISTORY: " + content);
		}
		else if (flowCallName.equals(ACTOR_STATUS_UPDATE)) {
			content = "{\"call\":{\"id\":13,\"name\":\"@action.actorStatusUpdate\",\"data\":{\"_id\":\""
					+ getActorStatus.getActorId()
					+ "\",\"actorStatusType_id\":\""
					+ getActorStatus.getActorStatusId()
					+ "\"}},"
					+ "\"target\":\"iiCall\",\"request\":14,\"location\":\"flow.testCenter.unitTester.\",\"authorization\":\""
					+ Login.authorization
					+ "\"}";
			PrettyPrint.prettyPrint(content, true);
		}
		// else if (flowCallName.equals(ACTION_SELECT)) {
		// content =
		// "{\"call\":{\"id\":2,\"name\":\"@action.actionSelect\",\"data\":{"
		// + getItem("actor._id", statusWrapper.currentStatus.actorId, true)
		//
		// + getItem("facility_id", statusWrapper.currentStatus.facilityId,
		// false)
		// + "}},"
		// +
		// "\"target\":\"iiCall\",\"request\":3,\"location\":\"flow.testCenter.unitTester.\",\"authorization\":\""
		// + login.authorization
		// + "\"}";
		// PrettyPrint.prettyPrint(content, true);
		// }
		else {
			L.out("ERROR: flowCallName is not known: " + flowCallName);
		}
		// L.out("content: " + content);
		return content;
	}

	// private String getDelayItem(StatusWrapper statusWrapper) {
	// if
	// (!statusWrapper.currentStatus.actionStatusId.equals(StaticFlow.ACTION_DELAYED))
	// return "";
	// if (statusWrapper.delayId == null)
	// return "";
	// return getItem("delayType.id", statusWrapper.delayId, false);
	// }

	private String getItem(String name, boolean value, boolean comma) {
		String temp = "\"" + name + "\":" + value + "";
		if (comma)
			temp += ",";
		return temp;
	}

	public static String getItem(String name, String value, boolean comma) {
		if (value == null)
			value = "";
		String temp = "\"" + name + "\":\"" + value + "\"";
		if (comma)
			temp += ",";
		return temp;
	}

	private String getHeader(String name) {
		if (name == null || name.equals("null"))
			name = "";
		return "\"" + name + "\":{";
	}

	private String getFooter() {
		return "},";
	}

	private String getContent(String flowCallName, String arg1, String arg2) {
		// L.out("flowCallName: " + flowCallName);
		Login login = Login.INSTANCE;
		if (login == null || Login.authorization == null) {
			L.out("ERROR: login is null! : " + login);
			return null;
		}
		String content = "";

		if (flowCallName.equals(GET_ACTOR_STATUS)) {
			content = "{\"call\":{\"id\":27,\"name\":\"@action.actorStatusSummary\",\"data\":{\"user_id\":\""
					+ emailAddress + "\"}},"
					+ "\"target\":\"iiCall\",\"authorization\":\""
					+ Login.authorization + "\", \"request\":12}";
			L.out("content: " + content);
		}

		else if (flowCallName.equals(UPDATE_SELECT_AVAILABLE_ZONES)) {
			content = "{\"call\":{\"id\":27,\"name\":\"@action.actorZoneUpdate\",\"data\":{\"user_id\":\""
					+ emailAddress + "\","
					+ getItem("zone_id", arg1, false)
					+ "}},"
					+ "\"target\":\"iiCall\",\"authorization\":\""
					+ Login.authorization + "\", \"request\":12}";
			// L.out("content: " + content);
		}

		else if (flowCallName.equals(SELECT_LOCATIONS)) {
			content = "{\"data\":{\"facility_id\": \""
					+ arg1
					+ "\"},"
					+ "\"criteria\":{\"store\":\"@action.selectLocations\"},\"target\":\"iiCache\","
					+ "\"request\":17,\"location\":\"flow.testCenter.unitTester.\","
					+ "\"authorization\":\""
					+ Login.authorization
					+ "\"}";
			// PrettyPrint.prettyPrint(content, true);
		}

		else if (flowCallName.equals(SELECT_AVAILABLE_ZONES)) {
			content = "{\"call\":{\"id\":1,\"name\":\"@action.selectAvailableZones\",\"data\":{\"facility_id\":\""
					+ arg2
					+ "\","
					+ getItem("actor_id", arg1, false)
					+ "}},\"target\":\"iiCall\",\"request\":2,\"location\":\"flow.testCenter.unitTester.\","
					+ "\"authorization\":\""
					+ Login.authorization
					+ "\"}";
			// PrettyPrint.prettyPrint(content, true);

		}
		else if (flowCallName.equals(GET_ACTION_STATUS)) {
			content = "{\"call\":{\"id\":10,\"name\":\"@action.mobileActionSelect\",\"data\":{\"_id\":\""
					+ arg1
					+ "\"}},\"target\":\"iiCall\",\"request\":11,\"location\":\"flow.testCenter.unitTester.\","
					+ "\"authorization\":\""
					+ Login.authorization
					+ "\"}";
			// PrettyPrint.prettyPrint(content, true);

		} else if (flowCallName.equals(SELECT_CLASS_TYPES_BY_FACILITY_ID)) {
			content = "{\"call\":{\"id\":2,\"name\":\"@action.mobileActorClasses\",\"data\":{\"facilityHirNode_id\": \""
					+ arg1
					+ "\","
					+ getItem("actor_id", arg2, false)
					+ "}},"
					+ "\"target\":\"iiCall\",\"authorization\":\""
					+ Login.authorization
					+ "\",\"request\":3,\"location\":\"flow.testCenter.unitTester."
					+ "\"}";
			// PrettyPrint.prettyPrint(content, true);
		} else if (flowCallName.equals(SEND_MESSAGE)) {
			String facilityId = UpdateController.getActorStatus.getFacilityId();
			content = "{\"call\":{\"id\":4,\"name\":\"@common.imMessageUpdate\",\"data\":{"
					+ getItem("facility_id", facilityId, true)
					+ getItem("message", arg1, true)
					+ getItem("target", arg2, false)
					+ "}},"
					+ "\"target\":\"iiCall\",\"authorization\":\""
					+ Login.authorization
					+ "\",\"request\":5,\"location\":\"flow.testCenter.unitTester."
					+ "\"}";
			// L.out("imMessageUpdate: " + content);
		}
		else {
			L.out("ERROR: flowCallName is not known: " + flowCallName);
		}
		// L.out("content: " + content);
		return content;
	}

	public String convertStreamToString(InputStream is)
			throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();
			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(
						new InputStreamReader(is, "UTF-8"));
				int n;
				int current = 0;
				while ((n = reader.read(buffer)) != -1) {
					current += n;
					// L.out("writer n: " + n + " " + current);
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			// L.out("writer: " + writer.toString().length());
			return writer.toString();
		} else {
			return "";
		}
	}

	public String execute(String parsedContent) {
		// L.out("execute server read");
		// String urlString =
		// "http://chp-s1-web-cs.cloudapp.net/flow/provider.aspx";
		// String urlString = "http://192.168.1.6:1337/flow/provider.aspx";
		// String urlString =
		// "http://syncpulse.cloudapp.net:1337/flow/provider.aspx";
		String urlString =
				SERVER + "provider.aspx";

		Login login = Login.INSTANCE;
		if (login == null || Login.authorization == null || Login.cookie == null) {
			L.out("ERROR: login is null! : " + login);
			return null;
		}
		try {

			// L.out("urlString: " + urlString);
			// L.out("cookie: " + cookie);
			URL url = new URL(urlString);
			HttpURLConnection.setFollowRedirects(false);
			HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
			httpURLConnection.setInstanceFollowRedirects(false);
			httpURLConnection.setRequestMethod("POST");
			httpURLConnection.setDoInput(true);
			httpURLConnection.setDoOutput(true);
			httpURLConnection.setUseCaches(false);
			httpURLConnection.setRequestProperty("Accept", "application/json");
			httpURLConnection.addRequestProperty("Accept-Language", "en-US,en;q=0.8");
			httpURLConnection.addRequestProperty("User-Agent", "Mozilla");
			// L.out("cookie: " + USER_ROLE + "=" + login.cookie);
			httpURLConnection.setRequestProperty("Cookie", USER_ROLE + "=" + Login.cookie);

			byte[] newBytes = parsedContent.getBytes();
			// L.out("newBytes: " + newBytes.length);
			// L.out("ready to write: \n" + parsedContent);
			// UpdateController.INSTANCE.doCallback(new PlainString("Write: " +
			// parsedContent), UpdateController.PLAIN_STRING);

			OutputStream outputStream = httpURLConnection.getOutputStream();
			outputStream.write(newBytes);
			outputStream.close();
			// L.out("done write");

			InputStream inputStream = httpURLConnection.getInputStream();
			String responseMessage = httpURLConnection.getResponseMessage();
			if (!responseMessage.equals("OK")) {
				L.out("ERROR: response message is not ok: " + responseMessage);
				return null;
			}
			String reply = convertStreamToString(inputStream);
			// L.out("read: " + reply.length());

			// UpdateController.INSTANCE.doCallback(new
			// PlainString("selectLocations: "
			// + PrettyPrint.formatPrint(reply)),
			// UpdateController.PLAIN_STRING);

			inputStream.close();
			httpURLConnection.disconnect();

			if (httpURLConnection.getResponseCode() != 200) {

				throw new RuntimeException("Failed : HTTP error code : "
						+ httpURLConnection.getResponseCode());
			}
			return reply;
		} catch (Exception e) {
			L.out("error:" + e + " urlString: " + urlString + L.p());
			return null;
		}
	}

	public String execute(String mETHOD_NAME, String assignedZone) {
		// TODO Auto-generated method stub
		return null;
	}

}
