package com.ii.mobile.tab;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.Field;
import com.ii.mobile.soap.gson.GetTaskInformationByTaskNumberAndFacilityID;
import com.ii.mobile.util.L;

public class BaseDisplayField extends PickField {

	// private static GetTaskDefinitionFieldsDataForScreenByFacilityID
	// getTaskDefinitionFieldsDataForScreenByFacilityID = null;

	private static ArrayList<BaseDisplayField> baseFieldList;
	// private static boolean inited = false;
	private final Activity activity;

	private final TableRow fieldLayout;
	protected final Field field;
	protected TextView titleView;
	protected TextView valueView;
	private final GetTaskInformationByTaskNumberAndFacilityID task;

	private String header;

	public BaseDisplayField(Activity activity, Field field,
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
		return tableRow;
	}

	private TextView createTitleView() {
		titleView = new TextView(activity);
		LayoutParams layoutParams =
				new TableRow.LayoutParams(
						TableRow.LayoutParams.WRAP_CONTENT,
						TableRow.LayoutParams.WRAP_CONTENT);
		titleView.setGravity(Gravity.CENTER_VERTICAL);
		titleView.setLayoutParams(layoutParams);
		titleView.setTextSize(15);
		titleView.setTextColor(Color.parseColor("#FFadd8e6"));

		if (header == null)
			header = field.header;
		titleView.setText(header);
		return titleView;
	}

	private TextView createValueView() {
		valueView = new TextView(activity);
		LayoutParams layoutParams =
				new TableRow.LayoutParams(
						TableRow.LayoutParams.WRAP_CONTENT,
						TableRow.LayoutParams.WRAP_CONTENT);
		valueView.setGravity(Gravity.CENTER_VERTICAL);
		valueView.setLayoutParams(layoutParams);

		valueView.setTextSize(15);
		if (header == null)
			header = field.header;
		String value = "";
		if (task != null)
			value = task.getNamedValue(header);
		if (header.equals("Destination") || header.equals("Start")) {
			String geographyName = "hrcAjaxRoomsSelectByFacilityAsKeyValue";
			value = lookUpFromValue(geographyName, value, activity);
		} else {
			if (header.equals("Task Number") && L.getLong(value) != 0) {
				value = "Local";
				valueView.setBackgroundColor(Color.parseColor("#FF0000"));
			}
		}
		// L.out("value: " + value);
		valueView.setText(value);
		layoutParams = (LayoutParams) valueView.getLayoutParams();
		layoutParams.setMargins(10, 0, 0, 0);
		return valueView;
	}

	public static List<BaseDisplayField> getViews(Activity activity, Field[] fields,
			GetTaskInformationByTaskNumberAndFacilityID getTaskInformationByTaskNumberAndFacilityID) {

		baseFieldList = new ArrayList<BaseDisplayField>();
		baseFieldList.add(new BaseDisplayField(activity, null, getTaskInformationByTaskNumberAndFacilityID, "Class"));
		baseFieldList.add(new BaseDisplayField(activity, null, getTaskInformationByTaskNumberAndFacilityID, "Task Number"));
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			// L.out(i + " field: " + field.customHeader + " controlType: " +
			// field.controlType);
			if (!field.header.equals("Persist"))
				baseFieldList.add(new BaseDisplayField(activity, field, getTaskInformationByTaskNumberAndFacilityID, null));
		}
		baseFieldList.add(new BaseDisplayField(activity, null, getTaskInformationByTaskNumberAndFacilityID, "Notes"));
		// inited = true;
		return baseFieldList;
	}

	public View getFieldView() {
		return fieldLayout;
	}

	@Override
	public String toString() {
		return "BaseDisplayField: " + field.customHeader + " controlType: " + field.controlType;
	}
}
