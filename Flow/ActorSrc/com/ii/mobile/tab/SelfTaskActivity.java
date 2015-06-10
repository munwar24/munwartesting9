/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.tab;

import java.util.GregorianCalendar;
import java.util.Hashtable;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.GestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import com.ii.mobile.transport.R; // same package
import com.ii.mobile.home.LoginActivity;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.soap.ParsingSoap;
import com.ii.mobile.soap.SoapDbAdapter;
import com.ii.mobile.soap.StaticSoap;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.Field;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ListDelayTypes;
import com.ii.mobile.soap.gson.ListDelayTypes.TaskDelayType;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.task.TaskSoap.TaskSoapColumns;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

/**
 * 
 * @author kfairchild
 */
public class SelfTaskActivity extends Activity implements AdapterView.OnItemSelectedListener {
	static SelfTaskActivity taskActivity = null;
	// String taskStatus = null;
	private static CountDownTimer countDownTimer;
	private static long activeStartTime = 1l;
	private static long assignedStartTime = 1l;
	String pauseMessage = "Pause Message";

	TaskDelayType[] taskDelayType = null;

	int count = 0;
	long elapsedAssignedTime = 0;
	long elapsedActiveTime = 0;

	GestureDetector detector;
	private Spinner spin = null;
	private Spinner completeSpin = null;

	public static String NO_TASK = "No Task";
	public static String UNASSIGNED = "Unassigned";
	public static String ASSIGNED = "Assigned";
	public static String ACTIVE = "Active";
	public static String DELAYED = "Delayed";
	public static String COMPLETED = "Completed";
	public static String CANCELED = "Canceled";

	public static String TRANSPORT = "1";
	public static String EVS = "2";

	String[] completeStates = new String[] { BreakActivity.AVAILABLE, BreakActivity.ON_BREAK,
			BreakActivity.AT_LUNCH, BreakActivity.NOT_IN };
	// private TaskController taskController = null;

	static GetTaskInformationByTaskNumberAndFacilityID lastTask = null;
	public static GetTaskInformationByTaskNumberAndFacilityID task;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.current_task_view);

		taskActivity = this;

		spin = (Spinner) findViewById(R.id.spinner);

		spin.setOnItemSelectedListener(this);
		taskDelayType = getTaskDelayType();
		ArrayAdapter<String> aa =
				new ArrayAdapter<String>(this, R.layout.list_item, getDisplayItems(taskDelayType));
		aa.setDropDownViewResource(
				R.layout.list_item);
		spin.setAdapter(aa);

		completeSpin = (Spinner) findViewById(R.id.completeToSpinner);
		completeSpin.setOnItemSelectedListener(this);
		aa = new ArrayAdapter<String>(this, R.layout.list_item, completeStates);
		aa.setDropDownViewResource(R.layout.list_item);
		completeSpin.setAdapter(aa);
		// taskController = new TaskController(this);
		// updateView();
		new TaskController(this);
		startTimer();
	}

	// @Override
	// public boolean onKeyDown(int keyCode, KeyEvent event) {
	// TabNavigationActivity tab =
	// TabNavigationActivity.getTabNavigationActivity();
	// return tab.onKeyDown(keyCode, event);
	// }

	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id) {
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	void updateView() {
		// getTaskInformationByTaskNumberAndFacilityID =
		// getTaskInformationByTaskNumberAndFacilityID();
		TabNavigationActivity.updateTitle();
		// L.out("getCurrentTaskByEmployeeID: " + getCurrentTaskByEmployeeID);
		L.out("taskStatus: " + getTaskStatus());
		// updateDataModel();
		boolean taskMessage = false;
		boolean taskTable = false;
		boolean bottomLine = false;
		boolean createRoundTripTaskButton = false;
		boolean createTransportationTaskButton = false;
		boolean resumeTaskButton = false;
		// boolean delayTaskButton = false;
		boolean taskCompleteButton = false;
		boolean startTaskButton = false;
		boolean spinner = false;

		TextView textView = (TextView) findViewById(R.id.createTransportationTaskText);
		if (getFunctionalArea() == EVS) {
			textView.setText("Create EVS Task");
		} else {
			textView.setText("Create Transport Task");
		}

		String taskStatus = getTaskStatus();

		if (taskStatus != null && taskStatus.equals(COMPLETED)) {
			L.out("Shouldn't be completed here: "
					+ task.getTaskNumber());
			taskStatus = null;
			task = null;
		}

		if (taskStatus == null) {
			taskMessage = true;
			createRoundTripTaskButton = true;
			createTransportationTaskButton = true;
		} else if (taskStatus.equals(ASSIGNED)) {
			bottomLine = true;
			taskTable = true;
			startTaskButton = true;
		}
		else if (taskStatus.equals(ACTIVE)) {
			bottomLine = true;
			taskTable = true;
			// delayTaskButton = true;
			taskCompleteButton = true;
			spinner = true;
		}
		else if (taskStatus.equals(DELAYED)) {
			taskTable = true;
			resumeTaskButton = true;
			bottomLine = true;
		}
		setVisiblity(taskMessage, findViewById(R.id.taskMessage));
		if (taskTable)
			// bindTask();
			generateFieldView();
		setVisiblity(taskTable, findViewById(R.id.taskTable));
		setVisiblity(bottomLine, findViewById(R.id.bottomLine));
		setVisiblity(createRoundTripTaskButton, findViewById(R.id.createRoundTripTaskButton));
		setVisiblity(createRoundTripTaskButton, findViewById(R.id.createReturnTripTaskText));
		setVisiblity(createTransportationTaskButton, findViewById(R.id.createTransportationTaskButton));
		setVisiblity(createTransportationTaskButton, findViewById(R.id.createTransportationTaskText));
		setVisiblity(resumeTaskButton, findViewById(R.id.resumeTaskButton));
		setVisiblity(resumeTaskButton, findViewById(R.id.resumeTaskText));
		setVisiblity(taskCompleteButton, findViewById(R.id.taskCompleteText));
		setVisiblity(taskCompleteButton, findViewById(R.id.completeToSpinner));
		setVisiblity(startTaskButton, findViewById(R.id.startTaskButton));
		setVisiblity(startTaskButton, findViewById(R.id.startTaskText));
		setVisiblity(spinner, findViewById(R.id.spinner));
		setVisiblity(spinner, findViewById(R.id.spinnerText));
		// BreakActivity.updateStatus(taskStatus);
	}

	public void finishDelayButtonClick(View view) {
		setTaskStatus(ACTIVE);
		setEmployeeStatus(ACTIVE, true, this);
		updateView();
	}

	public void startTaskButtonClick(View view) {
		// L.out("startTaskButtonClick: " + view.getClass());
		if (User.getUser() == null || User.getUser().getValidateUser() == null) {
			MyToast.show("ERROR: Unable to set user status to active!\nShould not occur!");
		} else
			User.getUser().getValidateUser().setTaskStatus(ACTIVE);
		setEmployeeStatus(ACTIVE, true, this);
		setTaskStatus(ACTIVE);
		updateView();
		startActiveTimer();
	}

	public void taskCompleteTaskButtonClick(View view) {
		// L.out("taskCompleteTaskButtonClick: " + view.getClass());
		User.getUser().getValidateUser().setTaskNumber(null);
		setTaskStatus(COMPLETED);
		// setEmployeeStatus(BreakActivity.AVAILABLE, true, this);
		stopTimer();
		updateView();
	}

	public void resumeTaskButtonClick(View view) {
		// L.out("resumeTaskButtonClick: " + view.getClass());
		User.getUser().getValidateUser().setTaskStatus(ACTIVE);
		setTaskStatus(ACTIVE);
		setEmployeeStatus(ACTIVE, true, this);
		updateView();
	}

	public void delayButtonClick(View view) {
		// L.out("spinnerButtonClick: " + view.getClass());
		String delayText = (String) spin.getSelectedItem();
		// MyToast.show("delayText: " + delayText);
		String delayTextID = lookupTaskDelayType(delayText);
		// MyToast.show("delayText: " + delayText + " " + delayTextID);
		task.setDelayType(delayTextID);
		setTaskStatus(DELAYED);
		User.getUser().getValidateUser().setTaskStatus(DELAYED);
		setEmployeeStatus(DELAYED, true, this);
		if (BreakActivity.breakActivity != null)
			BreakActivity.breakActivity.updateStatus(DELAYED, true);
		updateView();
	}

	public void createRoundTripTaskButtonClick(View view) {
		// L.out("createRoundTripTaskButtonClick: " + view.getClass());
		createTestTask();
		L.out("removed assigned. dont think want this");
		setTaskStatus(ASSIGNED);
		ValidateUser validateUser = User.getUser().getValidateUser();
		if (validateUser == null)
			return;
		validateUser.setEmployeeStatus(ASSIGNED);
		updateView();
	}

	public void createTransportationTaskButtonClick(View view)
	{
		Intent intent = new Intent(SelfTaskActivity.this, SelfTaskActivity.class);
		startActivity(intent);
	}

	private void setVisiblity(boolean show, View view) {
		if (show)
			view.setVisibility(View.VISIBLE);
		else
			view.setVisibility(View.GONE);
	}

	@Override
	protected void onResume() {
		super.onResume();
		L.out("resume taskStatus: " + getTaskStatus());
		updateView();
		startTimer();

	}

	@Override
	protected void onPause() {
		super.onPause();
		L.out("onPause: " + getTaskStatus());
		// stopTimer();
		pauseTimer();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		// outState.putString("taskStatus", getTaskStatus());
		outState.putLong("activeStartTime", activeStartTime);
		outState.putLong("assignedStartTime", assignedStartTime);
		L.out("onSaveInstanceState activeStartTime: " + activeStartTime + " taskStatus: " + getTaskStatus());
	}

	@Override
	protected void onRestoreInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
		activeStartTime = outState.getLong("activeStartTime");
		assignedStartTime = outState.getLong("assignedStartTime");
		// taskStatus = outState.getString("taskStatus");
		L.out("onRestoreInstanceState assignedStartTime : " + assignedStartTime + " activeStartTime: "
				+ activeStartTime + " taskStatus: "
				+ getTaskStatus());
		// updateView();
		if (getTaskStatus() != null) {
			// takeBreak(taskStatus);
		}
	}

	private void pauseTimer() {
		L.out("pauseTimer: " + countDownTimer);
		if (countDownTimer != null) {
			countDownTimer.cancel();
			countDownTimer = null;
		}
		// assignedStartTime = 1l;
		// activeStartTime = 1l;
	}

	void stopTimer() {
		L.out("stopTimer: " + countDownTimer);
		if (countDownTimer != null) {
			countDownTimer.cancel();
			countDownTimer = null;
		}
		assignedStartTime = 1l;
		activeStartTime = 1l;
	}

	public void startActiveTimer() {
		L.out("startActiveTimer: " + activeStartTime);
		// activeStartTime = 0l;
		if (activeStartTime == 1l)
			activeStartTime = new GregorianCalendar().getTimeInMillis();
		startTimer();
	}

	public void startAssignedTimer() {
		L.out("startAssignedTimer: " + assignedStartTime);
		if (assignedStartTime == 1l)
			assignedStartTime = new GregorianCalendar().getTimeInMillis();
		startTimer();
	}

	public void startTimer() {
		if (countDownTimer != null)
			return;

		countDownTimer = new CountDownTimer(99999999, 900) {
			@Override
			public void onTick(long millisUntilFinished) {

				TextView textView1 = (TextView) findViewById(R.id.statusOne);
				if (assignedStartTime != 1l)
					elapsedAssignedTime = new GregorianCalendar().getTimeInMillis() - assignedStartTime;
				count += 1;
				if (textView1 != null)
					if (assignedStartTime != 1l)
						textView1.setText("Assigned: " + L.getElapsedTime(elapsedAssignedTime));
				// else
				// textView.setText("");

				TextView textView2 = (TextView) findViewById(R.id.statusThree);
				if (activeStartTime != 1l)
					elapsedActiveTime = new GregorianCalendar().getTimeInMillis() - activeStartTime;
				count += 1;
				if (textView2 != null)
					if (activeStartTime != 1l)
						textView2.setText("Active: " + L.getElapsedTime(elapsedActiveTime));
					else
						textView2.setText("");

			}

			@Override
			public void onFinish() {
				// if you would like to execute something when time finishes
			}
		}.start();

	}

	private TaskDelayType[] getTaskDelayType() {

		if (taskDelayType != null)
			return taskDelayType;

		String json = LoginActivity.getJSon(ParsingSoap.LIST_DELAY_TYPES, this);
		if (json != null) {
			ListDelayTypes listDelayTypes = ListDelayTypes.getGJon(json);
			if (listDelayTypes == null) {
				MyToast.show("Failed to load delay types!");
				finish();
				return null;
			}
			// L.out("listTaskClassesByFacilityID: " +
			// listTaskClassesByFacilityID);
			TaskDelayType[] taskDelayType = listDelayTypes.getTaskDelayType();
			// for (int i = 0; i < taskDelayType.length; i++) {
			// L.out("taskDelayType: " + taskDelayType[i]);
			// }
			return taskDelayType;
		}
		return null;
	}

	private String lookupTaskDelayType(String key) {
		TaskDelayType[] taskDelayTypes = getTaskDelayType();
		if (taskDelayTypes == null)
			return "No DelayTypes: " + key;
		for (int i = 0; i < taskDelayTypes.length; i++)
			if (taskDelayTypes[i].taskDelay.equals(key))
				return taskDelayTypes[i].taskDelayID;
		return "DelayType not found: " + key;
	}

	private String[] getDisplayItems(TaskDelayType[] taskDelayType) {
		String[] items = new String[taskDelayType.length];
		for (int i = 0; i < taskDelayType.length; i++) {
			items[i] = taskDelayType[i].taskDelay;
			// L.out(i + " item: " + items[i]);
		}
		return items;
	}

	private String getTaskStatus() {
		if (task != null)
			return task.getTaskStatusBrief();
		return null;
	}

	static private String FUNCTIONAL_AREA = "1";

	static private Hashtable<String, Field[]> classFieldHashtable = null;

	private void generateFieldView() {
		if (task == null)
			return;
		String taskClassID = task.getTskTaskClass();
		L.out("taskClassID: " + taskClassID);
		Field[] field = getTaskField(taskClassID);
		// printField(field);
		List<BaseDisplayField> baseDisplayFields = BaseDisplayField.getViews(this, field, task);
		TableLayout tableLayout = (TableLayout) findViewById(R.id.topLevel);
		tableLayout.removeAllViews();

		if (baseDisplayFields != null)
			for (BaseDisplayField baseDisplayField : baseDisplayFields) {
				// L.out(count++ + ": " +
				// baseDisplayField.field.toStringShort());
				tableLayout.addView(baseDisplayField.getFieldView());
			}
	}

	public static void initDataCache() {
		L.out("initDataCache");
		classFieldHashtable = null;
	}

	private synchronized Field[] getTaskField(String classID) {
		if (classFieldHashtable == null) {
			L.out("initDataCache new Hashtable");
			classFieldHashtable = new Hashtable<String, Field[]>();
		}
		Field[] field = classFieldHashtable.get(classID);
		if (field != null)
			return field;

		String json = LoginActivity.getJSon(ParsingSoap.GET_TASK_DEFINITION_FIELDS_FOR_SCREEN_BY_FACILITYID, this);
		if (json != null) {
			// L.out("json: " + json);
			GetTaskDefinitionFieldsForScreenByFacilityID screens = GetTaskDefinitionFieldsForScreenByFacilityID.getGJon(json);
			// L.out("getTaskDefinitionFieldsForScreenByFacilityID: " +
			// screens);
			field = screens.getClassField(getFunctionalArea(), classID);
			L.out("classID: " + classID + " field: " + field);
			if (classID == null || field == null) {
				// classID = "IAN_BUG tell Kim!";
				L.out("ERROR: ignoring the null field IAN_BUG:" + classID + " fields[]:" + field);
			}
			if (classID != null && field != null)
				classFieldHashtable.put(classID, field);
			return field;
		}
		return null;
	}

	public static String getFunctionalArea() {
		ValidateUser validateUser = User.getUser().getValidateUser();
		if (validateUser == null)
			return null;
		if (validateUser.getFunctionalArea().equals("ENV"))
			return EVS;
		return TRANSPORT;
	}

	@SuppressWarnings("unused")
	private void printField(Field[] field) {
		for (int i = 0; i < field.length; i++) {
			L.out(i + " field: " + field[i]);
		}
	}

	public static void createSelfTask(GetTaskInformationByTaskNumberAndFacilityID task) {
		// L.out("taskActivity: " + taskActivity);
		if (taskActivity == null) {
			L.out("*** taskActivity: " + taskActivity);
			return;
		}
		taskActivity.selfTask(task);
	}

	private void selfTask(GetTaskInformationByTaskNumberAndFacilityID task) {
		task.setJson(null);
		task.setTaskStatusBrief(ASSIGNED);
		task.setEmployeeID(User.getUser().getEmployeeID());
		task.setFacilityID(User.getUser().getFacilityID());
		task.setTaskNumber(new GregorianCalendar().getTimeInMillis() + "");
		task.setTickled(false);
		GetTaskInformationByTaskNumberAndFacilityID newTask = GetTaskInformationByTaskNumberAndFacilityID.getGJon(task.getNewJson());
		newTask = GetTaskInformationByTaskNumberAndFacilityID.getGJon(task.getNewJson());
		newTask.printJson();
		L.out("newTask: " + newTask);
		// deleteCurrentTask();
		// stopTimer();
		SelfTaskActivity.task = newTask;
		lastTask = newTask;
		User.getUser().getValidateUser().setTaskNumber(newTask.getTaskNumber());
		User.getUser().getValidateUser().setTaskStatus(newTask.getTaskStatusBrief());
		setEmployeeStatus(BreakActivity.ASSIGNED, false, this);
		updateView();
		updateDataModel(true);
		startAssignedTimer();
	}

	private void createTestTask() {
		if (lastTask == null) {
			MyToast.show("Must have a recent task in order\nto create a return trip!");
			AudioPlayer.INSTANCE.playSound(AudioPlayer.ERROR);
			// new AudioPlayer(this).playSound(AudioPlayer.ERROR);
			return;
		}
		GetTaskInformationByTaskNumberAndFacilityID task = lastTask;
		// L.out("test: " + test);
		// test.printJson();
		String temp = task.getHirStartLocationNode();
		task.setHirStartLocationNode(task.getHirDestLocationNode());
		task.setHirDestLocationNode(temp);
		task.setTaskNumber(new GregorianCalendar().getTimeInMillis() + "");
		task.setTaskStatusBrief(ASSIGNED);
		task.setEmployeeID(User.getUser().getEmployeeID());
		task.setFacilityID(User.getUser().getFacilityID());
		task.setTickled(false);
		// L.out("**** new json: ");
		task.setJson(null);
		task.setJson(task.getNewJson());
		// test.printJson();

		GetTaskInformationByTaskNumberAndFacilityID newTask = GetTaskInformationByTaskNumberAndFacilityID.getGJon(task.getNewJson());
		selfTask(newTask);
	}

	private void setTaskStatus(String status) {
		setTaskStatus(status, false);
	}

	private void setTaskStatus(String status, boolean tickled) {
		if (task != null) {
			String oldStatus = task.getTaskStatusBrief();
			task.setTaskStatusBrief(status);
			task.setTickled(tickled);
			// MyToast.show("status: " + oldStatus + " to " + status);
			L.out("status: " + oldStatus + " to " + status);
			if (status != null && status.equals(oldStatus)) {
				L.out("Not Ignoring status change since equal: " + status);
				// return;
			}
			// L.out("**** originally: ");
			// getTaskInformationByTaskNumberAndFacilityID.printJson();
			// L.out("**** new json: ");
			if (status.equals(COMPLETED)) {
				String completeTo = (String) completeSpin.getSelectedItem();
				L.out("completed: " + completeTo);
				task.setCompleteTo(StatusType.lookUp(completeTo));
				User.getUser().getValidateUser().setTaskNumber(null);
				User.getUser().getValidateUser().setTaskStatus(null);
				setEmployeeStatus(completeTo, true, this);
				// task = null;
			} else if (status.equals(ASSIGNED)) {
				L.out("assigned: " + oldStatus);
				task.setTaskStatusBrief(oldStatus);
				task.setTickled(false);
			}
			task.setJson(task.getNewJson());
			// getTaskInformationByTaskNumberAndFacilityID.printJson();s
			// L.out(getTaskInformationByTaskNumberAndFacilityID + "");
			// L.out("*** update the server here! " + status);

			updateDataModel(false);
		}
		if (getTaskStatus() != null && getTaskStatus().equals(COMPLETED)) {
			// deleteCurrentTask();
			stopTimer();
			task = null;
			// setEmployeeStatus(BreakActivity.AVAILABLE, false, this);
		}
		String completeTo = (String) completeSpin.getSelectedItem();
		if (status.equals(COMPLETED) && completeTo.equals(BreakActivity.NOT_IN)) {
			User.getUser().getValidateUser().setTaskNumber(null);
			task = null;
			setEmployeeStatus(completeTo, true, this);
			// already saved but make sure don't have task for login check
			L.out("taskactivity called");
			// User.getUser().setNeedLogout(true);
			// TaskActivity.setEmployeeStatus(BreakActivity.NOT_IN, true,
			// taskActivity);
			Intent intent = new Intent().setClass(this,
					LoginActivity.class);
			startActivity(intent);
		}
	}

	void updateDataModel(boolean create) {
		L.out("updateDataModel create: " + create);
		if (task == null)
			return;
		L.out("updateDataModel: " + task.getTaskStatusBrief() + " create: " + create);
		String facilityID = User.getUser().getFacilityID();
		String employeeID = User.getUser().getEmployeeID();
		String taskNumber = task.getTaskNumber();
		String soapMethod = ParsingSoap.GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID;

		ContentValues values = new ContentValues();
		values.put(TaskSoapColumns.JSON, task.getNewJson());
		values.put(TaskSoapColumns.FACILITY_ID, facilityID);
		values.put(TaskSoapColumns.EMPLOYEE_ID, employeeID);
		values.put(TaskSoapColumns.TASK_NUMBER, taskNumber);
		values.put(TaskSoapColumns.SOAP_METHOD, soapMethod);
		if (create || L.getLong(taskNumber) != 0) {
			L.out("taskNumber on create: " + taskNumber);
			values.put(TaskSoapColumns.LOCAL_TASK_NUMBER, taskNumber);
		}
		// long localTaskNumber = L.getLong(taskNumber);
		String[] selectionArgs = new String[] { employeeID, facilityID, taskNumber };
		Intent intent = getIntent();
		intent.setData(Uri.parse("content://" + StaticSoap.AUTHORITY + "/" + soapMethod));
		getContentResolver().update(intent.getData(), values, SoapDbAdapter.getWhere(soapMethod, employeeID, facilityID, taskNumber), selectionArgs);
	}

	public static void setEmployeeStatus(String status, boolean tickled, Activity activity) {
		L.out("setEmployeeStatus: " + status + " " + tickled);
		ValidateUser validateUser = User.getUser().getValidateUser();
		// L.out("before: " + validateUser);
		String oldStatus = validateUser.getEmployeeStatus();
		validateUser.setEmployeeStatus(status);
		// MyToast.show("setEmployeeStatus: " + oldStatus + " to " + status);
		L.out("status: " + oldStatus + " to " + status + " tickled: " + tickled);
		// if (false && status != null && status.equals(oldStatus)) {
		// L.out("Ignoring employee status change since equal: " + status);
		// return;
		// }
		validateUser.setTickled(tickled);
		validateUser.setJson(validateUser.getNewJson());
		// L.out("after: " + validateUser);
		// L.out("**** employee status after: ");
		// validateUser.printJson();
		// User.getUser().setValidateUser(ValidateUser.getGJon(validateUser.getJson()));

		L.out("*** update the updateEmployeeDataModel here! " + status + " tickled: " + tickled);
		if (BreakActivity.breakActivity != null)
			BreakActivity.breakActivity.updateStatus(status, true);

		updateEmployeeDataModel(activity);
		// LoginActivity.relogin(activity);
	}

	public static void updateEmployeeDataModel(Activity activity) {
		L.out("update the updateEmployeeDataModel here: "
				+ User.getUser().getValidateUser().getEmployeeStatus());
		String facilityID = User.getUser().getFacilityID();
		String employeeID = User.getUser().getEmployeeID();
		String taskNumber = null;
		String soapMethod = ParsingSoap.VALIDATE_USER;

		ContentValues values = new ContentValues();
		values.put(TaskSoapColumns.JSON, User.getUser().getValidateUser().getNewJson());
		values.put(TaskSoapColumns.FACILITY_ID, User.getUser().getPassword());
		values.put(TaskSoapColumns.EMPLOYEE_ID, User.getUser().getUsername());
		values.put(TaskSoapColumns.TASK_NUMBER, User.getUser().getPlatform());
		values.put(TaskSoapColumns.SOAP_METHOD, soapMethod);
		String[] selectionArgs = new String[] { employeeID, facilityID, taskNumber };
		Intent intent = activity.getIntent();
		intent.setData(Uri.parse("content://" + StaticSoap.AUTHORITY + "/" + soapMethod));
		activity.getContentResolver().update(intent.getData(), values, SoapDbAdapter.getWhere(soapMethod, employeeID, facilityID, taskNumber), selectionArgs);
	}

	public static SelfTaskActivity getTaskActivity() {
		return taskActivity;
	}
}
