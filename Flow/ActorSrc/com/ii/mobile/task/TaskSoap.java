/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.ii.mobile.task;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.ii.mobile.model.Persist;
import com.ii.mobile.soap.StaticSoap.StaticSoapColumns;

/**
 * Convenience definitions for Soap
 */
public final class TaskSoap extends Persist {

	public static final String AUTHORITY = "com.ii.mobile.task.Task";
	private String soapName = null;
	private String json = null;
	private String taskNumber;

	public TaskSoap() {
	}

	TaskSoap(Cursor c) {
		soapName = c.getString(c.getColumnIndexOrThrow(StaticSoapColumns.SOAP_METHOD));
		json = c.getString(c.getColumnIndexOrThrow(StaticSoapColumns.JSON));
		taskNumber = c.getString(c.getColumnIndexOrThrow(StaticSoapColumns.JSON));
		set_Id(c.getInt(c.getColumnIndexOrThrow(StaticSoapColumns._ID)));
	}

	@Override
	public String toString() {
		return "StaticSoap: " + soapName + " json: " + json + " taskNumber: " + taskNumber + " id: "
				+ get_Id();
	}

	/**
	 * Static Soap table
	 */
	public static final class TaskSoapColumns implements BaseColumns {

		private TaskSoapColumns() {
		}

		/**
		 * The content:// style URL for this table
		 */
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
		public static final String JSON = "json";
		public static final String USERNAME = "username";
		public static final String ID = "id";
		public static final String SOAP_METHOD = "soap_method";
		public static final String DEFAULT_SORT_ORDER = " ASC";
		public static final String EMPLOYEE_ID = "employeeID";
		public static final String FACILITY_ID = "facilityID";
		public static final String VISIBLE = "visible";
		public static final String FONT_COLOR = "fontColor";
		public static final String REQUEST_DATE = "requestDate";
		public static final String REQUESTER_EMAIL = "requestorEmail";
		public static final String HIR_START_LOCATION_NODE = "hirStartLocationNode";
		public static final String CANCEL_DATE = "cancelDate";
		public static final String TSK_TASK_CLASS = "tskTaskClass";
		public static final String ITEM = "item";
		public static final String TSK_EQUIPMENT_CLASS = "tskEquipmentType";
		public static final String DEST_BRIEF = "destBrief";
		public static final String HIR_DEST_LOCATION_NODE = "hirDestLocationNode";
		public static final String ACTIVE_DATE = "activeDate";
		public static final String REQUESTOR_PHONE = "requestorPhone";
		public static final String MODE_BRIEF = "modeBrief";
		public static final String TASK_STATUS_BRIEF = "taskStatusBrief";
		public static final String ASSIGNED_DATE = "assignedDate";
		public static final String REQUESTOR_NAME = "requestorName";
		public static final String ISOLATION_PATENT = "isolationPatent";
		public static final String CLASS_BRIEF = "classBrief";
		public static final String TASK_NUMBER = "taskNumber";
		public static final String EQUIPMENT_BRIEF = "equipmentBrief";
		public static final String PRIORITY = "priority";
		public static final String TSK_STATUS_TYPE = "tskStatusType";
		public static final String NOTES = "notes";
		public static final String AREA_BRIEF = "areaBrief";
		public static final String TASK_AREA = "tskArea";
		public static final String TASK_MODE_TYPE = "tskModeType";
		public static final String CLOSE_DATE = "closeDate";
		public static final String DELAY_DATE = "delayDate";
		// if a self-task
		public static final String LOCAL_TASK_NUMBER = "localTaskNumber";

		// for sorting
		public static final String SORT_ORDER = "sort_order";
		public static final String SORT_DUE_DATE = "Date";
		public static final String SORT_SERVICE_TYPE = "Service Type";
		public static final String SORT_DEFAULT = SORT_DUE_DATE;

	}
}
