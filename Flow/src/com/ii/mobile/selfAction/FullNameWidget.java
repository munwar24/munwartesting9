package com.ii.mobile.selfAction;

import java.util.StringTokenizer;

import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.widget.EditText;

import com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Fields;

public class FullNameWidget extends TextWidget {
	public FullNameWidget(FragmentActivity activity, Fields field, boolean number) {
		super(activity, field, number);

	}

	@Override
	public boolean validate() {
		if (editText == null) {
			// L.out("editText is null for " + field.toString());
			return false;
		}

		// if (field.name.startsWith("Patient Name")) {
		// MyToast.show("validate foo: " + field.name + " text: #" + text +
		// "#");
		// }
		// L.out("validate textField: #" + text + "#" + text.length());
		if (titleView == null) {
			// L.out("titleView is null");
			return false;
		}

		String text = editText.getText().toString();

		if (field.required) {
			if (text == null || text.equals("") || text.length() < 2
					|| new StringTokenizer(text).countTokens() < 2) {
				// L.out("required foo: " + field.name + " text: #" + text +
				// "#");
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
	public EditText createValueView() {
		super.createValueView();
		editText.setHint("First and Last Name");
		return editText;
	}
}
