package com.ii.mobile.selfAction;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.TableRow;

import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Fields;

public class CheckBoxWidget extends BaseWidget {

	public CheckBoxWidget(FragmentActivity activity, Fields field) {
		super(activity, field);
	}

	@Override
	public CheckBox createValueView() {
		checkBox = new CheckBox(activity);

		TableRow.LayoutParams layoutParams = new
				TableRow.LayoutParams(
						TableRow.LayoutParams.FILL_PARENT,
						TableRow.LayoutParams.FILL_PARENT);

		// layoutParams.setMargins(0, 0, 10, 0);
		checkBox.setLayoutParams(layoutParams);
		// layoutParams = (LayoutParams) editText.getLayoutParams();
		// layoutParams.setMargins(20, 0, 40, 0);
		// checkBox.setHint(field.customHeader);
		checkBox.setTextSize(15);
		checkBox.setImeOptions(EditorInfo.IME_FLAG_NO_EXTRACT_UI);
		onRestoreInstanceState();
		if (field.required)
			checkBox.setHintTextColor(Color.parseColor("#FFAAAA"));
		else
			checkBox.setHintTextColor(Color.parseColor("#AAAAAA"));

		checkBox.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (((CheckBox) v).isChecked()) {
					setValue();
					// L.out("value set to: " + getValue());
				}
			}
		});

		return checkBox;
	}

	@Override
	public boolean validate() {
		// L.out("validate foo: " + foobar + " edit: " + editText);
		if (checkBox == null) {
			// L.out("checkBox is null for " + field.toString());
			return false;
		}
		titleView.setTextColor(Color.parseColor(OPTIONAL));
		setValue();

		return true;
	}

	@Override
	public ContentValues addValue(ContentValues contentValues) {
		String text = getValue();
		if (text != null && !text.equals("")) {
			// L.out("text: " + text);
			contentValues.put(field.fieldName, text);
		}
		return contentValues;
	}

	@Override
	public ContentValues addFailValue(ContentValues contentValues) {
		if (field.required) {
			String text = checkBox.getText().toString();
			if (text == null || text.equals("")) {
				// L.out("text: " + text);
				contentValues.put(field.fieldName, "Not Entered!");
			}
		}
		return contentValues;
	}

	@Override
	public String getValue() {
		if (checkBox.isChecked())
			return "Yes";
		return "No";

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		if (field.custom)
			outState.putString(field.control, getValue());
		else
			outState.putString(field.fieldName, getValue());
	}

	public String getTaskValue() {
		if (field.custom) {
			// L.out("field.control: " + field.control);
			return SelfActionFragment.getActionStatus.getNamedValue(field.control, true);
		}
		String text = SelfActionFragment.getActionStatus.getNamedValue(field.fieldName);
		return text;
	}

	@Override
	void onRestoreInstanceState() {
		String text = getTaskValue();
		// L.out("text: " + text);
		setState(text);
	}

	private void setState(String text) {
		// L.out("setState: " + text);
		if (text != null && (text.equals("Yes")
				|| text.equals(GetActionStatus.STAT)))
			checkBox.setChecked(true);
		else
			checkBox.setChecked(false);
	}

	@Override
	public void onRestoreInstanceState(Bundle outState) {
		String text = outState.getString(field.fieldName);
		// L.out("text: " + text);
		setState(text);
		// checkBox.setText(outState.getString(field.customHeader));
	}
}
