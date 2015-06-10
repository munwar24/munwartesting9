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
package com.ii.mobile.soap;

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;

import com.ii.mobile.model.Persist;

/**
 * Convenience definitions for Soap
 */
public final class StaticSoap extends Persist {

	public static final String AUTHORITY = "com.ii.mobile.soap.Soap";
	private String soapName = null;
	private String json = null;

	public StaticSoap() {
	}

	StaticSoap(Cursor c) {
		soapName = c.getString(c.getColumnIndexOrThrow(StaticSoapColumns.SOAP_METHOD));
		json = c.getString(c.getColumnIndexOrThrow(StaticSoapColumns.JSON));
		set_Id(c.getInt(c.getColumnIndexOrThrow(StaticSoapColumns._ID)));
	}

	@Override
	public String toString() {
		return "StaticSoap: " + soapName + " json: " + json + " id: " + get_Id();
	}

	/**
	 * Static Soap table
	 */
	public static final class StaticSoapColumns implements BaseColumns {

		private StaticSoapColumns() {
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
		public static final String TASK_NUMBER = "taskNumber";
		public static final String LOCAL_TASK_NUMBER = "localTaskNumber";

	}
}
