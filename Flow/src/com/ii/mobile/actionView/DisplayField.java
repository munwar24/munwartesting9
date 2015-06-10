package com.ii.mobile.actionView;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.View;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.Field;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.util.L;

public class DisplayField extends PickField {

	// private static GetTaskDefinitionFieldsDataForScreenByFacilityID
	// getTaskDefinitionFieldsDataForScreenByFacilityID = null;

	private static ArrayList<DisplayField> baseFieldList;
	// private static boolean inited = false;
	private final Activity activity;

	private final TableRow fieldLayout;
	protected final Field field;
	protected TextView titleView;
	protected TextView valueView;
	private final GetTaskInformationByTaskNumberAndFacilityID task;

	public String header;

	public DisplayField(Activity activity, Field field,
			GetTaskInformationByTaskNumberAndFacilityID task,
			String header) {
		super(activity);

		if (task == null)
			L.out("*** ERROR task is null");
		this.activity = activity;
		this.field = field;
		this.header = header;
		this.task = task;
		fieldLayout = createFieldLayout();
	}

	private TableRow createFieldLayout() {
		TableRow tableRow = new TableRow(activity);
		tableRow.addView(createTitleView());
		tableRow.addView(createValueView());
		tableRow.addView(createSpaceView());
		return tableRow;
	}

	private TextView createTitleView() {
		titleView = new TextView(activity);
		LayoutParams layoutParams =
				new TableRow.LayoutParams(
						TableRow.LayoutParams.FILL_PARENT,
						TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
		layoutParams.setMargins(10, 0, 0, 0);
		titleView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		titleView.setLayoutParams(layoutParams);
		titleView.setTextSize(16);
		titleView.setTextColor(Color.parseColor("#FF8398a9"));

		if (header == null)
			header = field.header;
		titleView.setText(header + ": ");
		return titleView;
	}

	private TextView createValueView() {
		valueView = new TextView(activity);
		LayoutParams layoutParams =
				new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,
						TableRow.LayoutParams.WRAP_CONTENT, 1.5f);
		layoutParams.setMargins(0, 0, 10, 0);
		valueView.setGravity(Gravity.CENTER_VERTICAL);
		valueView.setLayoutParams(layoutParams);
		valueView.setTextSize(16);

		valueView.setTextColor(Color.parseColor("#FF000000"));
		valueView.setTypeface(null, Typeface.BOLD);

		if (header == null)
			header = field.header;
		String value = task.getNamedValue(header);
		if (header.equals("Destination") || header.equals("Start")) {
			String geographyName = "hrcAjaxRoomsSelectByFacilityAsKeyValue";
			value = lookUpFromValue(geographyName, value);
		} else {
			if (header.equals("Task Number") && L.getLong(value) != 0) {
				value = "Local";
				valueView.setBackgroundColor(Color.parseColor("#FF0000"));
			}
		}
		// L.out("value: " + value);
		valueView.setText(value);
		return valueView;
	}

	private TextView createSpaceView() {
		TextView spaceView = new TextView(activity);
		LayoutParams layoutParams =
				new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,
						TableRow.LayoutParams.WRAP_CONTENT, 1.0f);
		layoutParams.setMargins(0, 0, 10, 0);
		spaceView.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);
		spaceView.setLayoutParams(layoutParams);
		spaceView.setTextColor(Color.parseColor("#FFFFFFFF"));
		spaceView.setText("000078127");
		return spaceView;
	}

	public static List<DisplayField> getViews(Activity activity, Field[] fields,
			GetTaskInformationByTaskNumberAndFacilityID getTaskInformationByTaskNumberAndFacilityID) {

		baseFieldList = new ArrayList<DisplayField>();
		baseFieldList.add(new DisplayField(activity, null, getTaskInformationByTaskNumberAndFacilityID, "Class"));
		baseFieldList.add(new DisplayField(activity, null, getTaskInformationByTaskNumberAndFacilityID, "Task Number"));
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			// L.out(i + " field: " + field.customHeader + " controlType: " +
			// field.controlType);
			if (!field.header.equals("Persist"))
				baseFieldList.add(new DisplayField(activity, field, getTaskInformationByTaskNumberAndFacilityID, null));
		}
		baseFieldList.add(new DisplayField(activity, null, getTaskInformationByTaskNumberAndFacilityID, "Notes"));
		// inited = true;
		return baseFieldList;
	}

	public View getFieldView() {
		return fieldLayout;
	}

	public Field getField() {
		return field;
	}

	@Override
	public String toString() {
		return "BaseDisplayField: " + field.customHeader + " controlType: " + field.controlType;
	}
}
