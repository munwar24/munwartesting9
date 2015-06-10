package com.ii.mobile.tab;

import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.AsyncTask;

import com.ii.mobile.home.LoginActivity;
import com.ii.mobile.soap.ParsingSoap;
import com.ii.mobile.soap.StaticSoap;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.soap.gson.ValidateUser;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class TaskController {
	private final SelfTaskActivity taskActivity;
	private GetTaskInformationByTaskNumberAndFacilityID task = null;

	public TaskController(SelfTaskActivity taskActivity) {
		this.taskActivity = taskActivity;
		if (checkForExistingTask()) {
			if (SelfTaskActivity.task.getTaskStatusBrief().equals(SelfTaskActivity.ASSIGNED))
				taskActivity.startAssignedTimer();
			else if (SelfTaskActivity.task.getTaskStatusBrief().equals(SelfTaskActivity.ACTIVE)
					|| SelfTaskActivity.task.getTaskStatusBrief().equals(SelfTaskActivity.DELAYED)) {
				taskActivity.startActiveTimer();
				// taskActivity.startAssignedTimer();
			}
			else if (SelfTaskActivity.task.getTaskStatusBrief().equals(SelfTaskActivity.COMPLETED))
				taskActivity.stopTimer();
		}
		MyContentObserver contentObserver = new MyContentObserver();
		Uri uri = Uri.parse("content://" + StaticSoap.AUTHORITY + "/" +
				ParsingSoap.GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID);
		taskActivity.getApplicationContext().getContentResolver().registerContentObserver(uri, true, contentObserver);
	}

	private boolean checkForExistingTask() {
		ValidateUser validateUser = User.getUser().getValidateUser();
		if (validateUser != null)
			if (validateUser.getTaskNumber() != null) {
				return createNewTask(null, null);
			}
		return false;
	}

	private boolean createNewTask(String taskStatus, String delayType) {
		ValidateUser validateUser = User.getUser().getValidateUser();
		L.out("create new task: " + validateUser.getTaskNumber() + " delayType: " + delayType);
		String json = null;
		task = null;
		if (validateUser.getTaskNumber() != null && validateUser.getTaskNumber().length() > 1)
			json = LoginActivity.getJSon(ParsingSoap.GET_TASK_INFORMATION_BY_TASK_NUMBER_AND_FACILITY_ID, taskActivity, validateUser.getTaskNumber());
		if (json != null) {
			task = GetTaskInformationByTaskNumberAndFacilityID.getGJon(json);
			task.setMobileUserName(validateUser.getMobileUserName());
			SelfTaskActivity.task = task;
			SelfTaskActivity.lastTask = task;
			SelfTaskActivity.task.setTickled(true);
			// if (taskStatus != null) {
			// String serverStatus = task.getTaskStatusBrief();
			// if (serverStatus != null && !serverStatus.equals(taskStatus)) {
			// task.setTaskStatusBrief(taskStatus);
			// task.setTickled(true);
			// validateUser.setTickled(true);
			// task.setDelayType(delayType);
			// // TaskActivity.updateEmployeeDataModel(taskActivity);
			// L.out("updating server with new Status: " + taskStatus);
			// }
			// }
			// validateUser.setTaskNumber(task.getTaskNumber());
			L.out("task.getTaskNumber(): " + task.getTaskNumber());
			// L.out("validateUser: " + validateUser.toString());
			// validateUser.setStatus(task.getTaskStatusBrief());
			// TaskActivity.setEmployeeStatus(task.getTaskStatusBrief(),
			// true, taskActivity);
			// taskActivity.updateDataModel(false);
			return true;
		} else {
			L.out("ERROR: json is null for: " + validateUser.getTaskNumber());
		}
		return false;
	}

	private class MyContentObserver extends ContentObserver {

		public MyContentObserver() {
			super(null);
		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			L.out("*** Received change: " + selfChange);
			if (selfChange)
				return;
			ValidateUser validateUser = User.getUser().getValidateUser();
			if (validateUser.getEmployeeStatus().equals(BreakActivity.NOT_IN)) {
				new PerformLogout().execute();
			}
			GetTaskInformationByTaskNumberAndFacilityID currentTask = SelfTaskActivity.task;
			String taskStatus = null;
			String delayType = null;
			if (currentTask != null) {
				taskStatus = currentTask.getTaskStatusBrief();
				delayType = currentTask.getDelayType();
				if (!currentTask.getTaskNumber().equals(validateUser.getTaskNumber())) {
					L.out("*** UPDATE - havetaskNumber and getting new one: " + currentTask.getTaskNumber()
							+ " and new:" + validateUser.getTaskNumber());
					// TaskActivity.task = null;
					// new UpdateView().execute();
					// return;
					if (validateUser.getTaskNumber() == null
							&& validateUser.getEmployeeStatus().equals(BreakActivity.AVAILABLE)) {
						if (currentTask != null && L.getLong(currentTask.getTaskNumber()) == 0) {
							L.out("*** TASK WAS CANCELLED!");
							SelfTaskActivity.task = null;
						}
					}
				}
			}
			createNewTask(taskStatus, delayType);
			// taskActivity.updateView();
			new UpdateView().execute();
		}

	}

	class PerformLogout extends AsyncTask<Integer, Integer, Long> {

		@Override
		protected Long doInBackground(Integer... params) {
			Thread.currentThread().setName("TaskControllerLogoutThread");
			return 0l;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			L.out("probably not used but executes in UI thread");
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Long l) {
			ValidateUser validateUser = User.getUser().getValidateUser();
			if (validateUser != null)
				validateUser.setTaskNumber(null);
			// TaskActivity.setEmployeeStatus(BreakActivity.NOT_IN, true,
			// taskActivity);
			Intent intent = new Intent().setClass(taskActivity, LoginActivity.class);
			taskActivity.startActivity(intent);
		}
	}

	class UpdateView extends AsyncTask<Integer, Integer, Long> {

		@Override
		protected Long doInBackground(Integer... params) {
			Thread.currentThread().setName("TaskControllerUpdateViewThread");
			return 0l;
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			L.out("probably not used but executes in UI thread");
		}

		@Override
		protected void onPreExecute() {
		}

		@Override
		protected void onPostExecute(Long l) {

			if (SelfTaskActivity.task != null) {
				if (SelfTaskActivity.task.getTaskStatusBrief().equals(SelfTaskActivity.ASSIGNED))
					taskActivity.startAssignedTimer();
				else if (SelfTaskActivity.task.getTaskStatusBrief().equals(SelfTaskActivity.ACTIVE)
						|| SelfTaskActivity.task.getTaskStatusBrief().equals(SelfTaskActivity.DELAYED)) {
					taskActivity.startActiveTimer();
					// taskActivity.startAssignedTimer();
				}
				else if (SelfTaskActivity.task.getTaskStatusBrief().equals(SelfTaskActivity.COMPLETED))
					taskActivity.stopTimer();
			}
			taskActivity.updateView();
		}
	}
}
