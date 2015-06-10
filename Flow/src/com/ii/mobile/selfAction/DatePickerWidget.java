package com.ii.mobile.selfAction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;

import com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Fields;
import com.ii.mobile.transport.R; // same package
import com.ii.mobile.util.L;

public class DatePickerWidget extends BaseWidget implements OnItemClickListener {

	DateButton dateButton;
	final Calendar calendar = Calendar.getInstance();

	public DatePickerWidget(Activity activity, Fields field) {
		super(activity, field);
		// L.out("field: " + field);
	}

	@Override
	public View createValueView() {
		// L.out("field: " + field);
		onRestoreInstanceState();
		dateButton = new DateButton(activity, field);

		year = calendar.get(Calendar.YEAR);
		month = calendar.get(Calendar.MONTH);
		day = calendar.get(Calendar.DAY_OF_MONTH);
		// L.out("createValueView: " + year + "/" + month + "/" + day);

		dateButton.update(year, month, day);
		final DatePickerWidget datePickerWidget = this;
		datePickerFragment = new DatePickerFragment(datePickerWidget, dateButton, calendar);
		dateButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (!datePickerFragment.isVisible())
					datePickerFragment.show(((FragmentActivity) activity).getSupportFragmentManager(), "datePicker");
			}
		});
		// L.out("field: " + field);
		setValue();
		return dateButton;
	}

	public boolean isCustom() {
		return field.custom;
	}

	public ArrayAdapter<String> setSuggestionSource(String[] hints) {
		// L.out("from: " + hints.length);
		return new ArrayAdapter<String>(activity.getApplicationContext(),
				R.layout.list_item, hints);
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		edited = true;
		// L.out("onItemClick: " + arg1 + " " + arg2 + " " + arg3 + " field: " +
		// field);
		setValue();
		validateAll();
	}

	@Override
	public boolean validate() {
		// L.out("DOB: " + edited);
		if (titleView == null) {
			// L.out("titleView is null");
			return false;
		}
		if (field.required) {
			if (!edited) {
				titleView.setTextColor(Color.parseColor(REQUIRED));
				return false;
			}
			else
				titleView.setTextColor(Color.parseColor(REQUIRED_PRESENT));

		} else
			titleView.setTextColor(Color.parseColor(OPTIONAL));

		return true;
	}

	public void update(int year, int month, int day) {
		// L.out("************************************** update: "
		// + year + " "
		// + month + " " + day + L.p());
		if (year == 0)
			return;
		this.year = year;
		this.month = month;
		this.day = day;
		setValue();
	}

	@Override
	public void setValue() {
		// L.out("setNamedValue: " + this + " " + getValue() + " \n" + field);
		if (field.custom)
			SelfActionFragment.getActionStatus.setNamedValue(field.control, field.controlType, getValue(), field.name, true);
		else
			SelfActionFragment.getActionStatus.setNamedValue(field.control, getValue());
		// SelfActionFragment.getActionStatus.setNamedValue(field.control,
		// getValue());
		// setSideEffect(SelfActionFragment.getActionStatus);
	}

	public static String createDateString(int days, int months, int years) {
		String temp = years + "/" + (months + 1) + "/" + days;
		// L.out("createDateString: " + temp + L.p());
		return temp;
	}

	@Override
	public ContentValues addValue(ContentValues contentValues) {
		// String text = year + "/" + (month + 1) + "/" + day;
		String text = createDateString(day, month, year);
		// L.out("addValue: " + text);
		edited = getEdited();
		L.out("edited: " + edited);
		if (edited && (text != null && !text.equals(""))) {
			contentValues.put(field.fieldName, text);
		}
		return contentValues;
	}

	private boolean getEdited() {
		if (field.custom)
			return SelfActionFragment.getActionStatus.getDateEdited(true);
		return SelfActionFragment.getActionStatus.getDateEdited();
	}

	@Override
	public ContentValues addFailValue(ContentValues contentValues) {
		if (field.required && !edited) {
			contentValues.put(field.fieldName, "Date not changed from default!");
		}
		return contentValues;
	}

	@Override
	public String getValue() {

		// String stringDate =
		// SelfActionFragment.getActionStatus.getNamedValue(field.control);
		// String stringDate = (month + 1) + "/" + day + "/" + year;
		// String stringDate = year + "/" + (month + 1) + "/" + day;
		String stringDate = createDateString(day, month, year);
		// L.out("getValue: " + stringDate);
		return stringDate;
		// return getFlowDate();
		// String text = "" + year + " " + (month + 1) + " " + day;
		// String text = (month + 1) + "/" + day + "/" + year;
		// if (edited && text != null && !text.equals("")) {
		// return getFlowDate();
		// }
		// return "";
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putString(field.fieldName + "month", month + "");
		outState.putString(field.fieldName + "day", day + "");
		outState.putString(field.fieldName + "year", year + "");
		// L.out("onSaveInstanceState: " + year + " " + month + " " + day + " "
		// + field.fieldName);
		// String temp = calendar.get(Calendar.YEAR) + "/" +
		// (calendar.get(Calendar.MONTH) + 1) + "/"
		// + calendar.get(Calendar.DAY_OF_MONTH);
		String temp = createDateString(calendar.get(Calendar.DAY_OF_MONTH), calendar.get(Calendar.MONTH), calendar.get(Calendar.YEAR));
		// L.out("temp :   " + temp);
		if (field.custom)
			SelfActionFragment.getActionStatus.setNamedValue(field.control, field.controlType, temp, field.name, true);
		else
			SelfActionFragment.getActionStatus.setNamedValue(field.control, temp);
	}

	@Override
	public void onRestoreInstanceState(Bundle outState) {
		month = (int) L.getLong(outState.getString(field.fieldName + "month"));
		day = (int) L.getLong(outState.getString(field.fieldName + "day"));
		year = (int) L.getLong(outState.getString(field.fieldName + "year"));
		// L.out("onRestoreInstanceState: " + createDateString(day, month,
		// year));
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, day);
		calendar.set(Calendar.YEAR, year);
		datePickerFragment.update(year, month, day);
		dateButton.update(year, month, day);

	}

	private String getActionValue() {
		String stringDate;
		if (field.custom)
			stringDate = SelfActionFragment.getActionStatus.getNamedValue(field.control, true);
		else
			stringDate = SelfActionFragment.getActionStatus.getNamedValue(field.control);
		if (stringDate == null || stringDate.equals("")) {
			// stringDate = "1/1/1965";
			GregorianCalendar gregorianCalendar = new GregorianCalendar();
			int year = gregorianCalendar.get(Calendar.YEAR);
			int month = gregorianCalendar.get(Calendar.MONTH);
			int day = gregorianCalendar.get(Calendar.DAY_OF_MONTH);
			stringDate = createDateString(day, month, year);
			// stringDate = "1965/1/1";
		}
		// L.out("stringDate :   " + stringDate);
		return stringDate;

	}

	@Override
	void onRestoreInstanceState() {
		String stringDate = getActionValue();
		// L.out("stringDate: " + stringDate);
		SimpleDateFormat simpleDateFormat = new
				SimpleDateFormat("yyyy/MM/dd");
		try {
			calendar.setTime(simpleDateFormat.parse(stringDate));
			// L.out("calendar: " + calendar);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public void setEdited(boolean b) {
		edited = b;
		L.out("setEdited: " + b + " ");
		// MyToast.show("edited: " + field);
		if (field.custom)
			SelfActionFragment.getActionStatus.setDateEdited(edited, true);
		else
			SelfActionFragment.getActionStatus.setDateEdited(edited);
		if (dateButton == null)
			return;

	}
}
