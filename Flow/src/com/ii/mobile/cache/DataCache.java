package com.ii.mobile.cache;

import java.util.Hashtable;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v4.app.FragmentManager;

import com.ii.mobile.home.LoginActivity;
import com.ii.mobile.paging.PageFragmentController;
import com.ii.mobile.soap.ParsingSoap;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsDataForScreenByFacilityID;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsDataForScreenByFacilityID.Geography;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.Field;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ListDelayTypes;
import com.ii.mobile.soap.gson.ListDelayTypes.TaskDelayType;
import com.ii.mobile.soap.gson.ListRecentTasksByEmployeeID;
import com.ii.mobile.soap.gson.ListRecentTasksByEmployeeID.EmployeeRecentTasksList;
import com.ii.mobile.soap.gson.ListTaskClassesByFacilityID;
import com.ii.mobile.soap.gson.ListTaskClassesByFacilityID.TaskClass;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class DataCache implements Cache {

	private static Activity activity;

	public static final String TRANSPORT = "1";
	public static final String EVS = "2";
	private TaskClass[] taskClass = null;
	private GetTaskDefinitionFieldsDataForScreenByFacilityID fieldData = null;
	private final TaskDelayType[] taskDelayType = null;
	private Hashtable<String, Field[]> classFieldHashtable = null;
	private static String lastEmployeeID = "";

	private static DataCache dataCache = null;

	private GetTaskInformationByTaskNumberAndFacilityID task = null;

	public static DataCache makeInstance(Activity activity) {

		return new DataCache(activity, "12345");

	}

	public static DataCache makeInstanceOld(Activity activity) {
		String employeeID = User.getUser().getValidateUser().getEmployeeID();

		if (dataCache == null || !lastEmployeeID.equals(employeeID))
			return new DataCache(activity, employeeID);
		return dataCache;
	}

	private DataCache(Activity activity, String employeeID) {
		DataCache.activity = activity;
		DataCache.dataCache = this;

		L.out("Creating DataCache!");
		lastEmployeeID = employeeID;
		// new ProgressCache().execute();

	}

	@Override
	public GetTaskInformationByTaskNumberAndFacilityID getTask() {
		return task;
	}

	@Override
	public void setTask(GetTaskInformationByTaskNumberAndFacilityID task) {
		this.task = task;
	}

	@Override
	public TaskDelayType[] getTaskDelayType() {

		if (taskDelayType != null)
			return taskDelayType;

		String json = LoginActivity.getJSon(ParsingSoap.LIST_DELAY_TYPES, activity);
		if (json != null) {
			ListDelayTypes listDelayTypes = ListDelayTypes.getGJon(json);
			TaskDelayType[] taskDelayType = listDelayTypes.getTaskDelayType();
			// for (int i = 0; i < taskDelayType.length; i++) {
			// L.out("taskDelayType: " + taskDelayType[i]);
			// }
			// L.out("cached ListDelayTypes: " + taskDelayType.length);
			return taskDelayType;
		}
		return null;
	}

	@Override
	public Field[] getTaskField(String classID) {
		if (classFieldHashtable == null) {
			L.out("initDataCache new Hashtable");
			classFieldHashtable = new Hashtable<String, Field[]>();
		}
		if (classID == null)
			return null;
		Field[] field = classFieldHashtable.get(classID);
		if (field != null)
			return field;

		String json = LoginActivity.getJSon(ParsingSoap.GET_TASK_DEFINITION_FIELDS_FOR_SCREEN_BY_FACILITYID, activity);
		if (json != null) {
			GetTaskDefinitionFieldsForScreenByFacilityID screens = GetTaskDefinitionFieldsForScreenByFacilityID.getGJon(json);
			// L.out("cached getTaskDefinitionFieldsForScreenByFacilityID: " +
			// json.length());
			field = screens.getClassField(getFunctionalArea(), classID);
			// L.out("classID: " + classID + " field: " + field);
			if (classID == null || field == null) {
				L.out("error: classID: " + classID + " field: " + field);
			} else
				classFieldHashtable.put(classID, field);
			return field;
		}
		return null;
	}

	private String getFunctionalArea() {
		// L.out("validateUser: " + User.getUser().getValidateUser());
		String functionalAreaID = User.getUser().getValidateUser().getFunctionalArea();
		L.out("functionalArea: " + functionalAreaID);
		return getFunctionalArea(functionalAreaID);
	}

	public String getFunctionalArea(String functionalAreaID) {
		if (functionalAreaID == null)
			return null;
		if (functionalAreaID.equals("ENV"))
			return EVS;
		return TRANSPORT;
	}

	EmployeeRecentTasksList recentTasks[];

	@Override
	public EmployeeRecentTasksList[] getEmployeeRecentTasksList() {
		if (recentTasks == null) {
			String json = LoginActivity.getJSon(ParsingSoap.LIST_RECENT_TASKS_BY_EMPLOYEE_ID, activity);
			ListRecentTasksByEmployeeID listRecentTasksByEmployeeID = ListRecentTasksByEmployeeID.getGJon(json);
			if (listRecentTasksByEmployeeID != null && listRecentTasksByEmployeeID.isValidated()) {
				recentTasks = listRecentTasksByEmployeeID.getEmployeeRecentTasksList();
			}
			if (json == null)
				L.out("ERROR JSON is null");
			else
				L.out("cached getEmployeeRecentTasksList:" + json.length());
			if (recentTasks == null || recentTasks == null)
				return null;
		}
		return recentTasks;
	}

	public void setEmployeeRecentTasksList(EmployeeRecentTasksList recentTasks[]) {
		this.recentTasks = recentTasks;
	}

	@Override
	public Geography getPickListGeography(String geographyName) {
		if (fieldData == null) {
			String json = LoginActivity.getJSon(ParsingSoap.GET_TASK_DEFINITION_FIELDS_DATA_FOR_SCREEN_BY_FACILITY_ID, activity);
			fieldData = GetTaskDefinitionFieldsDataForScreenByFacilityID.getGJon(json);
			// L.out("cached getTaskDefinitionFieldsDataForScreenByFacilityID:"
			// + json.length());
			if (fieldData == null || geographyName == null)
				return null;
		}
		return fieldData.getGeography(geographyName);
	}

	@Override
	public TaskClass[] getTaskClasses() {
		if (taskClass != null)
			return taskClass;

		String json = LoginActivity.getJSon(ParsingSoap.LIST_TASK_CLASSES_BY_FACILITY_ID, activity);
		if (json != null) {
			ListTaskClassesByFacilityID listTaskClassesByFacilityID = ListTaskClassesByFacilityID.getGJon(json);
			// L.out("listTaskClassesByFacilityID: " +
			// listTaskClassesByFacilityID);
			taskClass = listTaskClassesByFacilityID.getTaskClasses(getFunctionalArea());
			if (taskClass == null) {
				L.out("class is null for functionalArea: " + getFunctionalArea());
			}
			L.out("cached listTaskClassesByFacilityID: " + taskClass.length);
			for (int i = 0; i < taskClass.length; i++) {
				// L.out("Class: " + taskClass[i]);
			}
			return taskClass;
		}
		return null;
	}

	@Override
	public void printField(Field[] field) {
		for (int i = 0; i < field.length; i++) {
			L.out(i + " field: " + field[i]);
		}
	}

	// labels
	class ProgressCache extends AsyncTask<Void, Integer, Long> {

		ProgressDialog progressDialog = null;

		@Override
		protected Long doInBackground(Void... arg0) {
			Thread.currentThread().setName("DataCacheThread");
			getTaskDelayType();
			getTaskField(null);
			getPickListGeography(null);
			getTaskClasses();
			return 0l;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			L.out("probably not used but executes in UI thread");
		}

		@Override
		protected void onPreExecute() {
			if (missingAny()) {
				progressDialog = new ProgressDialog(activity);
				progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				progressDialog.setMessage("One-time cache of static content ...");
				progressDialog.setCancelable(false);
				progressDialog.show();
			} else {
				L.out("Not missing any");
			}
		}

		private boolean missingAny() {

			if (taskClass == null
					&& fieldData == null
					&& taskDelayType == null
					&& classFieldHashtable == null)
				return true;
			return false;
		}

		@Override
		protected void onPostExecute(Long l) {

			if (progressDialog != null) {
				try {
					progressDialog.dismiss();
				} catch (Exception e) {
					L.out("dismissed exception: " + e);
				}
			}
		}
	}

	public Activity getActivity() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public FragmentManager getSupportFragmentManager() {
		// TODO Auto-generated method stub
		return null;
	}

	//
	// public void setPagePosition(int position) {
	// // TODO Auto-generated method stub
	// }

	@Override
	public void updateStatusTitle() {
		// TODO Auto-generated method stub
	}

	@Override
	public void updatePageTitle(String title) {
		// TODO Auto-generated method stub
	}

	public void startTask() {
		// TODO Auto-generated method stub
	}

	@Override
	public void update() {
		// TODO Auto-generated method stub

	}

	@Override
	public GetTaskInformationByTaskNumberAndFacilityID getTask(String taskNumber) {
		// L.out("taskNumber: " + taskNumber);
		ValidateUser validateUser = User.getUser().getValidateUser();
		String json = null;
		GetTaskInformationByTaskNumberAndFacilityID task = null;
		json = LoginActivity.getJSon(ParsingSoap.GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID, activity, taskNumber);
		// L.out("json: " + json);
		task = GetTaskInformationByTaskNumberAndFacilityID.getGJon(json);
		if (task != null && task.isValidated()) {
			task.setMobileUserName(validateUser.getMobileUserName());
			task.setTickled(true);
			return task;
		}

		return null;
	}

	public EmployeeRecentTasksList[] getEmploydeeRecentTasksList() {
		if (recentTasks == null) {
			String json = LoginActivity.getJSon(ParsingSoap.LIST_RECENT_TASKS_BY_EMPLOYEE_ID, activity);
			ListRecentTasksByEmployeeID listRecentTasksByEmployeeID = ListRecentTasksByEmployeeID.getGJon(json);
			if (listRecentTasksByEmployeeID != null && listRecentTasksByEmployeeID.isValidated()) {
				recentTasks = listRecentTasksByEmployeeID.getEmployeeRecentTasksList();
			}
			L.out("cached getEmployeeRecentTasksList:" + json.length());
			if (recentTasks == null || recentTasks == null)
				return null;
		}
		return recentTasks;
	}

	@Override
	public EmployeeRecentTasksList[] addRecentTask(EmployeeRecentTasksList newTask) {
		EmployeeRecentTasksList[] newList = new EmployeeRecentTasksList[recentTasks.length + 1];
		newList[0] = newTask;
		for (int i = 0; i < recentTasks.length; i++) {
			newList[i + 1] = recentTasks[i];
		}
		L.out("oldList: " + recentTasks.length + " newList: " + newList.length);
		recentTasks = newList;
		return newList;
	}

	@Override
	public PageFragmentController getPageFragmentController() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

}
