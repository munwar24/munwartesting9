package com.ii.mobile.selfAction;

import android.content.Context;
import android.graphics.Color;
import android.widget.Button;

import com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Fields;

public class DateButton extends Button {

	private final Fields field;

	public DateButton(Context context, Fields field) {
		super(context);
		this.field = field;
		setTextSize(20);
		setTextColor(Color.parseColor("#000000"));
	}

	public void update(int year, int month, int day) {
		// L.out("date: " + month + 1 + "/" + day + "/" + year
		// + SelfActionFragment.getActionStatus.getDateEdited() + L.p());
		boolean edited = ((field.custom) ? SelfActionFragment.getActionStatus.getDateEdited(true)
				: SelfActionFragment.getActionStatus.getDateEdited());

		if (SelfActionFragment.getActionStatus != null && edited) {
			setText(getFlowDate(year, month, day));
			setTextColor(Color.parseColor("#000000"));
		}
		else {
			setTextColor(Color.parseColor(BaseWidget.REQUIRED_PRESENT));
			setText("None");
		}
	}

	private String getFlowDate(int year, int month, int day) {
		// L.out("getFlowDate: " + (month + 1) + "/" + day + "/" + year);
		return (month + 1) + "/" + day + "/" + year;
	}

}
