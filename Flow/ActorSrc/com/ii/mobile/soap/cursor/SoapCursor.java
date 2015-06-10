package com.ii.mobile.soap.cursor;

import java.util.List;

import com.ii.mobile.database.AbstractDbAdapter;
import com.ii.mobile.soap.StaticSoap.StaticSoapColumns;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.users.User.UserColumns;

/**
 * 
 * @author kfairchild
 */
public class SoapCursor extends MemoryCursor {

	public static final String[] columns = new String[] {
			UserColumns._ID, // 0
			StaticSoapColumns.SOAP_METHOD, // 1
			StaticSoapColumns.EMPLOYEE_ID, // 2
			StaticSoapColumns.FACILITY_ID, // 3
			StaticSoapColumns.TASK_NUMBER, // 4
			AbstractDbAdapter.TIME_STAMP, // 5
			StaticSoapColumns.JSON // 6
	};

	public SoapCursor(List<GJon> list) {
		super(list, columns);
	}

	@Override
	public String getColumnValue(int index) {
		// L.out("test SOAP_METHOD: " +
		// getColumnIndex(StaticSoapColumns.SOAP_METHOD));
		// L.out("test EMPLOYEE_ID: " +
		// getColumnIndex(StaticSoapColumns.EMPLOYEE_ID));
		// L.out("test JSON: " + getColumnIndex(StaticSoapColumns.JSON));

		// L.out("index: " + index + " position: " + getPosition());
		if (getList() == null) {
			return "SoapCursor error getList is null: " + index;
		}
		GJon gJon = (GJon) getList().get(getPosition());
		// L.out("gJon: " + gJon);
		// L.out("user key: " + user.getKeyId());
		if (gJon == null) {
			return "SoapCursor error gJon is null: " + index;
		}
		if (index == 1) {
			return gJon.getMethodName();
		}
		if (index == 6) {
			return gJon.getJson();
		}
		if (index == 3) {
			return "true";
		}

		return "SoapCursor error for index: " + index;
	}

}
