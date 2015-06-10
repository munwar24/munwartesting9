package com.ii.mobile.selfAction;

import java.util.Calendar;
import java.util.GregorianCalendar;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import com.ii.mobile.home.MyToast;
import com.ii.mobile.util.L;

@SuppressLint("ValidFragment")
public class DatePickerFragment extends DialogFragment
		implements DatePickerDialog.OnDateSetListener {
	DateButton dateButton = null;
	private Calendar calendar = null;
	private DatePickerWidget datePickerWidget = null;

	public DatePickerFragment(DatePickerWidget datePickerWidget, DateButton dateButton, Calendar calendar) {
		super();
		this.dateButton = dateButton;
		this.calendar = calendar;
		this.datePickerWidget = datePickerWidget;
	}

	public DatePickerFragment() {
		super();
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		int year = calendar.get(Calendar.YEAR);
		int month = calendar.get(Calendar.MONTH);
		int day = calendar.get(Calendar.DAY_OF_MONTH);
		L.out("created: " + DatePickerWidget.createDateString(month, day, year));
		// return new DatePickerDialog(getActivity(), this,
		// R.style.DatePickerTheme, month, day);

		return new DatePickerDialog(getActivity(), android.R.style.Theme_Holo_Light_Dialog_NoActionBar, this, year, month, day);
	}

	@Override
	public void onDateSet(DatePicker view, int year, int month, int day) {
		L.out("onDateSet picked: " + year + " " + month + " " + day);

		if (datePickerWidget.isCustom() || validDate(year, month, day)) {
			dateButton.update(year, month, day);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, day);
			calendar.set(Calendar.YEAR, year);
			datePickerWidget.update(year, month, day);

			datePickerWidget.setEdited(true);
			datePickerWidget.validateAll();
		} else {
			datePickerWidget.setEdited(false);
			MyToast.show("Date of birth cannot be in the future!");
		}
	}

	public static boolean validDate(int year, int month, int day) {
		GregorianCalendar now = new GregorianCalendar();
		GregorianCalendar then = new GregorianCalendar();
		then.set(year, month, day);
		// L.out("now: " + now.getTimeInMillis()+" "+now);
		// L.out("then: " + then.getTimeInMillis()+" "+then);
		if (then.compareTo(now) <= 0) {
			return true;
		}
		return false;
	}

	public void update(int year, int month, int day) {
		L.out("update: " + DatePickerWidget.createDateString(month, day, year));
		if (year == 0)
			return;
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.YEAR, year);
	}
}