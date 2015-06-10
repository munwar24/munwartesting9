/*

 */
package com.ii.mobile.selfAction;

import android.content.Context;
import android.text.InputType;
import android.view.KeyEvent;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.ii.mobile.util.L;

public class AutoComplete extends AutoCompleteTextView implements OnEditorActionListener {
	private static AutoComplete autoComplete = null;
	TypeAheadWidget typeAheadWidget = null;

	static AutoCompleteTextView getStatic() {
		return autoComplete;
	}

	public AutoComplete(Context context, TypeAheadWidget typeAheadWidget) {
		super(context);
		autoComplete = this;
		this.typeAheadWidget = typeAheadWidget;
		// L.out("context: " + context);

		setSingleLine();
		setHapticFeedbackEnabled(true);
		setSoundEffectsEnabled(true);
		setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
				| InputType.TYPE_TEXT_FLAG_AUTO_COMPLETE
				| InputType.TYPE_TEXT_FLAG_AUTO_CORRECT);
		setRawInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
		// addTextChangedListener(this);
		setThreshold(0);
		setOnEditorActionListener(this);
	}

	@Override
	public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
		L.out("arg1: " + arg1);
		typeAheadWidget.setValue();
		return true;
	}
}
