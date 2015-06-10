package com.ii.mobile.instantMessage;

import java.util.regex.Pattern;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class TextValidator implements TextWatcher {
	private static final String PATTERN = "^[a-zA-Z 0-9?\\.\\+!;:\\*\\$%#@_\\-\\?,_\\n]*$";
	private boolean editing = false;

	public TextValidator() {
	}

	@Override
	public void afterTextChanged(Editable editable) {
		if (editing)
			return;
		String text = editable.toString();
		int length = text.length();

		if (!Pattern.matches(PATTERN, text)) {
			editing = true;
			editable.delete(length - 1, length);
			editing = false;
		}
	}

	@Override
	final public void beforeTextChanged(CharSequence s, int start, int count, int after) {
	}

	@Override
	final public void onTextChanged(CharSequence s, int start, int before, int count) {
	}
}