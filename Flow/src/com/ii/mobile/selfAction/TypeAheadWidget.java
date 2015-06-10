package com.ii.mobile.selfAction;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;

import com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Fields;
import com.ii.mobile.util.L;

public class TypeAheadWidget extends PickListWidget implements OnItemClickListener {

	private boolean edited = false;

	public TypeAheadWidget(FragmentActivity fragmentActivity, Fields field) {
		super(fragmentActivity, field);
	}

	@Override
	public View createValueView() {
		// String value = getValue();
		// L.out("value: " + value);
		autoComplete = new AutoComplete(activity.getApplicationContext(), this);
		String[] pickListArray = getPickListArray();
		if (pickListArray == null)
			L.out("Unable to set the suggestion: " + field);
		else
			autoComplete.setAdapter(setSuggestionSource(getPickListArray()));
		onRestoreInstanceState();
		autoComplete.setOnItemClickListener(this);
		autoComplete.setTextColor(Color.parseColor("#000000"));
		autoComplete.setHint(field.name);
		autoComplete.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		return autoComplete;
	}

	public ArrayAdapter<String> setSuggestionSource(String[] options) {
		// L.out("from: " + hints.length);
		return new ArrayAdapter<String>(activity.getApplicationContext(),
				android.R.layout.simple_dropdown_item_1line, options);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		edited = true;
		setValue();
		validateAll();
	}

	@Override
	public void setValue() {
		// L.out("setNamedValue: " + this + " " + getValue() + " \n" + field);
		// L.out("getTaskValue(): " + getTaskValue());
		int result = findSelectedValue(getValue());
		// L.out("result: " + result);
		if (result == -1) {
			// MyToast.show("failed: " + getValue());
			autoComplete.setText("");
		}
		super.setValue();
		validateAll();
	}

	@Override
	public boolean validate() {
		// L.out("TypeAhead: " + edited + " required: " + field.required);
		if (titleView == null) {
			// L.out("titleView is null");
			return false;
		}
		int result = findSelectedValue(getValue());
		// L.out("result: " + result);
		if (field.required) {
			if (findSelectedValue(getValue()) == -1) {
				titleView.setTextColor(Color.parseColor(REQUIRED));
				return false;
			}
			else
				titleView.setTextColor(Color.parseColor(REQUIRED_PRESENT));
		} else
			titleView.setTextColor(Color.parseColor(OPTIONAL));

		return true;
	}

	@Override
	public String getValue() {
		if (autoComplete == null) {
			// L.out("autoComplete text: " + autoComplete);
			return "";
		}
		String locationName = autoComplete.getText().toString();
		// L.out("getValue spinner typehead for field: " + field +
		// " locationId: " +
		// locationName);

		// String locationId = Locations.INSTANCE.findLocationId(locationName);
		// if (locationId == null) {
		// return "broken on getValue";
		// }
		return locationName;
	}

	@Override
	public ContentValues addValue(ContentValues contentValues) {
		String text = autoComplete.getText().toString();

		if (text != null && !text.equals("")) {
			contentValues.put(field.fieldName, text);
		}
		return contentValues;
	}

	@Override
	public ContentValues addFailValue(ContentValues contentValues) {
		String text = autoComplete.getText().toString();
		if (field.required && text.equals("")) {
			contentValues.put(field.fieldName, "Not selected from dropdown list!");
		}
		return contentValues;
	}

	//
	// @Override
	// protected void setSideEffect(GetTaskInformationByTaskNumberAndFacilityID
	// task) {
	// // TODO Auto-generated method stub
	//
	// }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		String text = autoComplete.getText().toString();
		outState.putString(field.fieldName, text);
		// L.out("text: " + text);
	}

	@Override
	public void onRestoreInstanceState(Bundle outState) {
		String value = outState.getString(field.fieldName);

		// L.out("value: " + value);
		autoComplete.setText(value);
		if (value != null && value.length() > 0)
			edited = true;
		// int index = findSelectedValue(value);
		// if (index != -1)
		// spinner.setSelection(index, true);
		// else
		// L.out("*** ERROR can't find spinner selection: " + value);
	}

	@Override
	public String getTaskValue() {
		// L.out("field: " + field);
		String text = SelfActionFragment.getActionStatus.getNamedValue(field.fieldName);
		if (text == null)
			text = "";
		return text;
	}

	@Override
	void onRestoreInstanceState() {
		String value = getTaskValue();
		// L.out("value: " + value);

		autoComplete.setText(value);
		if (value.length() > 0)
			edited = true;
	}

	@Override
	int findSelectedValue(String key) {
		ListAdapter listAdapter = autoComplete.getAdapter();
		// L.out("autoComplete.getAdapter(): " + key);

		if (autoComplete.getAdapter() == null)
			return -1;

		// L.out("listAdapter: " + key + " " + listAdapter.getCount());
		for (int i = 0; i < listAdapter.getCount(); i++) {
			// L.out("listAdapter.: " + listAdapter.getItem(i));
			if (!listAdapter.getItem(i).equals(""))
				if (listAdapter.getItem(i).equals(key))
					return i;
		}
		return -1;
	}

}
