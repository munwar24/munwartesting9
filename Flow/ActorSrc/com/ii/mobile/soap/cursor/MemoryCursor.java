/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.soap.cursor;

import java.util.List;

import android.database.AbstractCursor;
import android.os.Bundle;

import com.ii.mobile.model.Persist;
import com.ii.mobile.util.L;

/**
 * 
 * @author kfairchild
 */
public class MemoryCursor extends AbstractCursor {

	private final List<? extends Persist> list;
	private final String[] columns;
	private final Bundle bundle = new Bundle();

	// List<DataSetObserver> dataSetObservers = new
	// ArrayList<DataSetObserver>();

	public MemoryCursor(List<? extends Persist> list, String[] columns) {
		super();
		this.list = list;
		this.columns = columns;
		moveToFirst();
	}

	public Bundle getBundle() {
		return bundle;
	}

	public List<? extends Persist> getList() {
		return list;
	}

	@Override
	public int getCount() {
		if (list == null) {
			return 0;
		}
		return list.size();
	}

	@Override
	public String[] getColumnNames() {
		// L.out("getColumnNames: ");
		return columns;
	}

	@Override
	public String getString(int arg0) {
		return getColumnValue(arg0);
	}

	@Override
	public short getShort(int arg0) {
		throw new UnsupportedOperationException("getShort Not supported yet.");
	}

	@Override
	public int getInt(int arg0) {
		throw new UnsupportedOperationException("getInt Not supported yet.");
	}

	@Override
	public long getLong(int arg0) {
		return L.getLong(getColumnValue(arg0));
	}

	@Override
	public float getFloat(int arg0) {
		throw new UnsupportedOperationException("getFloat Not supported yet.");
	}

	@Override
	public double getDouble(int arg0) {
		String temp = getColumnValue(arg0);
		return L.getDouble(temp);
	}

	@Override
	public boolean isNull(int arg0) {
		// L.out("isNull: " + arg0);
		if (true) {
			return false;
		}
		throw new UnsupportedOperationException("isNull Not supported yet.");
	}

	public String getColumnValue(int index) {
		throw new UnsupportedOperationException("move Not supported yet.");
	}
}
