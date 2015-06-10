package com.ii.mobile.selfAction;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableRow;

import com.ii.mobile.flow.staticFlow.CostCodes;
import com.ii.mobile.flow.staticFlow.Equipments;
import com.ii.mobile.flow.staticFlow.Genders;
import com.ii.mobile.flow.staticFlow.Locations;
import com.ii.mobile.flow.staticFlow.Modes;
import com.ii.mobile.flow.staticFlow.PersistTypes;
import com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Fields;
import com.ii.mobile.transport.R; // same package
import com.ii.mobile.util.L;

public class PickListWidget extends BaseWidget implements AdapterView.OnItemSelectedListener {

	static String[] items = { "error1", "error2",
		"error3"
	};

	public PickListWidget(Activity activity, Fields field) {
		super(activity, field);
		// L.out("field: " + field);
	}

	@Override
	public View createValueView() {
		spinner = new Spinner(activity);
		// String value = getValue();
		// L.out("value: " + value);
		String[] pickList = getPickListArray();

		ArrayAdapter<Object> arrayAdapter =
				new ArrayAdapter<Object>(activity, R.layout.transport_gray_spinner, pickList);

		arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spinner.setAdapter(arrayAdapter);
		String prompt = field.control;
		if(prompt.contains("Mode"))
			prompt = "Select Mode";
		else
			if(prompt.contains("Equipment"))
				prompt = "Select Equipment";
		spinner.setPrompt(prompt);
		spinner.setFadingEdgeLength(50);
		// spinner.setBackgroundResource(android.R.drawable.editbox_background);
		onRestoreInstanceState();
		// spinner.setPadding(0, 0, 0, 10);

		TableRow.LayoutParams layoutParams = new
				TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,
						TableRow.LayoutParams.MATCH_PARENT);
		spinner.setLayoutParams(layoutParams);
		spinner.setOnItemSelectedListener(this);
		return spinner;
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String pick = (String) spinner.getSelectedItem();
		// L.out("pick: " + pick + " " + field);
		// L.out("lastPick: " + lastPick);
		if (field.required) {
			if (lastPick != null && lastPick != pick)
				validateAll();
			lastPick = pick;
		}
		setValue();
		validateAll();
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
	}

	protected String[] getPickListArray() {
		if (field.fieldName.equals("modeType_id"))
			return Modes.INSTANCE.getModes();
		if (field.fieldName.equals("equipmentType_id"))
			return Equipments.INSTANCE.getEquipments();
		if (field.fieldName.equals("frequencyType"))
			return PersistTypes.INSTANCE.getPersistTypes();
		if (field.fieldName.equals("patient.genderType_id"))
			return Genders.INSTANCE.getGenders();
		if (field.fieldName.equals("costcode"))
			return CostCodes.INSTANCE.getCostCodes();
		if (field.controlType.equals("searchList"))
			return Locations.INSTANCE.getLocations();

		L.out("ERROR: getPickListArray not found for: " + field);
		return items;
	}

	@Override
	public String getValue() {
		if (spinner == null) {
			// L.out("spinner text: " + spinner);
			return "";
		}
		String text = (String) spinner.getSelectedItem();
		// L.out("getValue spinner text: " + field.fieldName
		// + "\n    text: " + text);
		return text;
	}

	@Override
	public boolean validate() {
		if (spinner == null) {
			// L.out("spinner text: " + spinner);
			return false;
		}
		String text = (String) spinner.getSelectedItem();
		// L.out("spinner text: #" + text + "# " + field.required);

		if (field.required) {
			if (text.equals("")) {
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
	public ContentValues addValue(ContentValues contentValues) {
		if (spinner == null) {
			// L.out("addValue spinner text: " + spinner);
			return contentValues;
		}
		String text = (String) spinner.getSelectedItem();
		if (text != null && !text.equals("")) {
			contentValues.put(field.fieldName, text);
		}
		return contentValues;
	}

	@Override
	public ContentValues addFailValue(ContentValues contentValues) {
		if (field.required) {
			String text = (String) spinner.getSelectedItem();

			if (text == null || text.equals("")) {
				// L.out("text: " + text);
				contentValues.put(field.fieldName, "Not Selected!");
			}
		}
		return contentValues;
	}

	protected void setSideEffect() {
		// L.out("setSideEffect is turned off: ");
	}

	// private String lookUp(PickList[] pickList, String textPart) {
	// for (int i = 0; i < pickList.length; i++) {
	// if (pickList[i].textPart.equals(textPart))
	// return pickList[i].valuePart;
	// }
	// return null;
	// }

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(field.control, getValue());
	}

	@Override
	public void onRestoreInstanceState(Bundle outState) {
		String value = outState.getString(field.control);
		// L.out("onRestoreInstanceState bundle: " + value);
		int index = findSelectedValue(value);
		if (index != -1)
			spinner.setSelection(index, true);
		else
			L.out("*** ERROR can't find spinner selection: " + value);
	}

	String getTaskValue() {
		String temp = SelfActionFragment.getActionStatus.getNamedValue(field.fieldName);
		// L.out("temp: " + temp);
		return temp;
	}

	@Override
	void onRestoreInstanceState() {
		String value = getTaskValue();
		// L.out("onRestoreInstanceState: " + value);
		int index = findSelectedValue(value);
		if (index != -1)
			spinner.setSelection(index, true);
		else
			L.out("*** ERROR can't find spinner selection: " + value);
	}

	int findSelectedValue(String key) {
		// L.out("spinner: " + key + " " + spinner);
		if (spinner == null)
			return -1;
		// L.out("spinner: " + key + " " + spinner.getCount());
		for (int i = 0; i < spinner.getCount(); i++) {
			// L.out("spinner.: " + spinner.getAdapter().getItem(i));
			if (spinner.getAdapter().getItem(i).equals(key))
				return i;
		}
		return -1;
	}

}
