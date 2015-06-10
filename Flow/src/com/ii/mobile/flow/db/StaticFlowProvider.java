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
package com.ii.mobile.flow.db;

import android.net.Uri;
import android.provider.BaseColumns;

import com.ii.mobile.model.Persist;

public final class StaticFlowProvider extends Persist {

	public static final String AUTHORITY = "com.ii.mobile.flow.Flower";

	private final String flowMethod = null;
	private final String json = null;

	public StaticFlowProvider() {
	}

	@Override
	public String toString() {
		return "flowMethod: " + flowMethod + " json: " + json + " id: " + get_Id();
	}

	public static final class StaticFlowColumns implements BaseColumns {

		private StaticFlowColumns() {
		}

		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);
		public static final String JSON = "json";
		public static final String FLOW_METHOD = "flowMethod";
		public static final String DEFAULT_SORT_ORDER = " ASC";
		public static final String ACTOR_ID = "actorId";
		public static final String FACILITY_ID = "facilityId";
		public static final String ACTION_ID = "actionId";
		public static final String LOCAL_ACTION_NUMBER = "localActionNumber";

	}
}
