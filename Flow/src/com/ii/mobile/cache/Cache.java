package com.ii.mobile.cache;

import android.support.v4.app.FragmentManager;

import com.ii.mobile.paging.PageFragmentController;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsDataForScreenByFacilityID.Geography;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.Field;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ListDelayTypes.TaskDelayType;
import com.ii.mobile.soap.gson.ListRecentTasksByEmployeeID.EmployeeRecentTasksList;
import com.ii.mobile.soap.gson.ListTaskClassesByFacilityID.TaskClass;

public interface Cache {
	String ASSIGNED_TIMER = "Assigned_Timer";
	String ACTIVE_TIMER = "Active_Timer";
	// a hack, fix when have ops as well
	public final int INSTANT_MESSAGE_DISPATCH_PAGE = 1;

	public static String UNASSIGNED = "Unassigned";
	public static String AVAILABLE = "Available";
	public static String ASSIGNED = "Assigned";
	public static String ACTIVE = "Active";
	public static String DELAYED = "Delayed";
	public static String COMPLETED = "Completed";
	public static String CANCELED = "Canceled";
	public static String AT_LUNCH = "At Lunch";
	public static String ON_BREAK = "On Break";
	public static String NOT_IN = "Not In";

	public TaskDelayType[] getTaskDelayType();

	public Field[] getTaskField(String classID);

	public void printField(Field[] field);

	public TaskClass[] getTaskClasses();

	public Geography getPickListGeography(String geographyName);

	public void setTask(GetTaskInformationByTaskNumberAndFacilityID task);

	public GetTaskInformationByTaskNumberAndFacilityID getTask();

	public GetTaskInformationByTaskNumberAndFacilityID getTask(String taskNumber);

	public EmployeeRecentTasksList[] getEmployeeRecentTasksList();

	public EmployeeRecentTasksList[] addRecentTask(EmployeeRecentTasksList newTask);

	// public Activity getActivity();

	public FragmentManager getSupportFragmentManager();

	public void updateStatusTitle();

	public void updatePageTitle(String title);

	// public void startTask();

	public PageFragmentController getPageFragmentController();

	public void update();

	public boolean isRunning();

	// public void setPagePosition(int position);
}
