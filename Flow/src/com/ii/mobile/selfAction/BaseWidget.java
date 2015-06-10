package com.ii.mobile.selfAction;

import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.ii.mobile.actionView.PickField;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Fields;
import com.ii.mobile.flowing.UpdateController;

abstract public class BaseWidget extends PickField {

	public static final String TEXT = "Text";
	public static final String TYPE_AHEAD = "TypeAhead";
	public static final String PICK_LIST = "PickList";
	public static final String DATE_TIME_SPLIT = "DateTimeSplit";
	public static final String PATIENT_DOB = "TxtPatientDOB";
	public static final String DROP_MODE_ENTRY = "DrpModeEntry";
	public static final String CHECK_BOX = "CheckBox";

	// protected static final String REQUIRED = "#CC0000";
	protected static final String REQUIRED = "#FF0000";
	public static final String REQUIRED_PRESENT = "#CC9999";
	protected static final String OPTIONAL = "#000000";
	// protected static final String VALID = "#00AA00";
	protected static final String VALID = "#00FF00";
	protected static final String HAVE_TASK = "#FFA500";

	private static View topLevelView = null;
	private static boolean inited = false;
	public static boolean validated = false;

	// protected static GetActionStatus getActionStatus = null;

	protected String lastPick = null;

	// amazing that need to put here. Not initialize in super and then
	// initialized after to default! Since call createFieldLayout here, the
	// side-effects go away!
	// protected EditText editText = null;
	protected int month = 0;
	protected int day = 1;
	protected int year = 1970;
	protected Spinner spinner;
	protected CheckBox checkBox;
	protected DatePickerFragment datePickerFragment = null;
	protected AutoComplete autoComplete;
	boolean edited = false;

	protected Activity activity;

	// private final TableRow fieldLayout;
	protected final Fields field;
	protected TextView titleView;

	// protected static GetActionStatus getActionStatus = null;

	public BaseWidget(Activity activity, Fields field) {
		super(activity);
		this.activity = activity;
		this.field = field;
		// L.out(getClass().getSimpleName() + " field: " + field);

		// fieldLayout = createFieldLayout();
		// validate();
	}

	public void validateAll() {
		// L.out("validateAll: " + inited);
		if (SelfActionFragment.selfActionFragment == null)
			return;
		List<BaseWidget> baseWidgets = SelfActionFragment.selfActionFragment.baseWidgets;

		if (baseWidgets == null)
			return;
		validated = true;
		// L.out("baseFieldList: " + baseWidgets.size());
		for (BaseWidget baseWidget : baseWidgets) {
			boolean valid = baseWidget.validate();
			// L.out("valid: " + valid + " baseWidget: "
			// + baseWidget.getClass() + " "
			// + baseWidget.field);
			if (!valid)
				validated = false;
		}
		updateSubmitButton();
		// L.out("valididate valid: " + validated + " " + baseWidgets.size());
	}

	private void updateSubmitButton() {
		SelfActionFragment selfActionFragment = SelfActionFragment.selfActionFragment;
		// L.out("SelfActionFragment.selfActionFragment: " +
		// SelfActionFragment.selfActionFragment);

		if (selfActionFragment == null || selfActionFragment.submitButton == null)
			return;
		// L.out("SelfActionFragment.selfActionFragment: " +
		// SelfActionFragment.selfActionFragment.submitButton);
		Button button = selfActionFragment.submitButton;
		// L.out("have task: " + getCache().getTask());
		if (UpdateController.getActionStatus != null) {
			button.setTextColor(Color.parseColor(HAVE_TASK));
			return;
		}

		if (validated)
			button.setTextColor(Color.parseColor(VALID));
		else
			button.setTextColor(Color.parseColor(REQUIRED));
	}

	// public static void setFields(View topLevelView, ArrayList<BaseWidget>
	// baseFields) {
	// BaseWidget.baseFields = baseFields;
	// BaseWidget.topLevelView = topLevelView;
	//
	// }

	public abstract View createValueView();

	public abstract ContentValues addValue(ContentValues contentValues);

	public abstract ContentValues addFailValue(ContentValues contentValues);

	public abstract boolean validate();

	// public View getFieldView() {
	// return fieldLayout;
	// }

	@Override
	public String toString() {
		return "base field: " + field.control + " controlType: " + field.controlType;
	}

	public void setValue() {

		if (field.custom) {
			SelfActionFragment.getActionStatus.setNamedValue(field.control, field.controlType, getValue(), field.name, true);
			// L.out("setNamedValue: " + this + " " + getValue() + " \n" +
			// field);
		}
		else
			SelfActionFragment.getActionStatus.setNamedValue(field.control, getValue());
		setSideEffect(SelfActionFragment.getActionStatus);
	}

	protected void setSideEffect(GetActionStatus task) {
	}

	public Fields getField() {
		return field;
	}

	abstract public String getValue();

	public abstract void onSaveInstanceState(Bundle outState);

	public abstract void onRestoreInstanceState(Bundle outState);

	abstract void onRestoreInstanceState();

	// public Cache getCache() {
	// if (activity == null || !(activity instanceof Cache)) {
	// L.out("****** ERROR TransportActivity: " + activity);
	// return null;
	// }
	// return (Cache) activity;
	// }

	public void setTitleView(TextView titleView) {
		this.titleView = titleView;
	}

	public void setSlotView(TextView slotView) {
		titleView = slotView;
	}
}
