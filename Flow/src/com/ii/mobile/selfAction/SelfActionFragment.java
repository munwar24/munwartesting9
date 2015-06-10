package com.ii.mobile.selfAction;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ii.mobile.actionButtons.DataFragment;
import com.ii.mobile.actionView.ActionViewFragment;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flow.types.SelectClassTypesByFacilityId;
import com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Fields;
import com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Targets;
import com.ii.mobile.flowing.FlowBinder;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.flowing.SyncCallback;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.home.LoginActivity;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.transport.R; // same package
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class SelfActionFragment extends DataFragment implements NamedFragment, OnItemClickListener,
		OnItemSelectedListener, SyncCallback {
	public final static String FRAGMENT_TAG = "selfTaskFragment";

	public static final String TEXT = "text";
	public static final String MULTIPLE_LINE_TEXT = "multipleLineText";
	public static final String TYPE_AHEAD = "typeAhead";
	public static final String PICK_LIST = "pickList";
	public static final String DATE_TIME_SPLIT = "dateTimeSplit";
	public static final String PATIENT_DOB = "TxtPatientDOB";
	public static final String DROP_MODE_ENTRY = "DrpModeEntry";
	public static final String CHECK_BOX = "checkbox";
	public static final String RADIO = "radio";
	private static final String DATE_PICKER_CONTROL = "datePickerControl";
	private static final String SEARCH_LIST = "searchList";

	public static final String START = "start";
	public static final String DESINATION = "destination";

	public static final String CURRENT_POSITION = "currentPosition";

	// private final Bundle outState = null;
	private FragmentActivity activity = null;
	private static int currentPosition = 0;
	private Vibrator vibrator;

	private View topLevelView = null;
	// private static ArrayList<BaseField> baseFields;
	protected static boolean isVisible = false;

	public static GetActionStatus getActionStatus = null;
	Spinner spin = null;
	protected List<BaseWidget> baseWidgets = null;

	private boolean validated;

	protected Button submitButton = null;

	// private Bundle bundle;

	private static final String CREATE_ACTION = "Create Action";

	private static final String CREATE_TRANSPORT = "Create Transport";

	public static SelfActionFragment selfActionFragment = null;

	public SelfActionFragment() {
		setArguments(new Bundle());
	}

	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		L.out("created: " + getActivity());
		vibrator = (Vibrator) (getActivity().getSystemService(Context.VIBRATOR_SERVICE));
		selfActionFragment = this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		L.out("onCreateView: " + container + " " + bundle);
		spin = null;
		// this.bundle = bundle;
		// if (bundle != null) {
		// String position = bundle.getString(CURRENT_POSITION);
		// L.out("position: " + position);
		// if (position != null) {
		// currentPosition = (int) L.getLong(position);
		// L.out("test currentPosition: " + currentPosition);
		// if (getSpinnerSize() >= currentPosition + 1)
		// currentPosition = 0;
		// }
		// }
		// createSpinner();
		// getActionStatus = getDefaultActionStatus();
		topLevelView = inflater.inflate(R.layout.transport_self_action, container, false);

		submitButton = (Button) topLevelView.findViewById(R.id.submitButton);
		GetActorStatus getActorStatus = UpdateController.getActorStatus;
		if (true || getActorStatus != null
				&& !GetActorStatus.TRANSPORT_FACILITY.equals(getActorStatus.getFunctionalAreaTypeId()))
			submitButton.setText(CREATE_ACTION);
		else
			submitButton.setText(CREATE_TRANSPORT);

		submitButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				submitButtonClick(v);
			}
		});

		// updateView(((Cache) activity).getTaskClasses()[currentPosition],
		// null);

		View topLevel = topLevelView.findViewById(R.id.topLevel);
		SharedPreferences settings = getActivity().getSharedPreferences(User.PREFERENCE_FILE, 0);
		boolean staffUser = settings.getBoolean(LoginActivity.STAFF_USER, false);
		if (staffUser)
			topLevel.setOnLongClickListener(new OnLongClickListener() {

				@Override
				public boolean onLongClick(View v) {
					vibrator.vibrate(100);
					TransportActivity.showToast = !TransportActivity.showToast;
					if (TransportActivity.showToast) {
						MyToast.show("Event Toasts turned on");
					} else {
						MyToast.show("Event Toasts turned off");
					}
					// String className = getActionStatus.getClassName();
					// String classId = getActionStatus.getClassId();
					// getActionStatus = initAction(classId, className);
					// updateView(null);
					return false;
				}
			});
		activity = getActivity();

		UpdateController.INSTANCE.registerCallback(this, FlowRestService.SELECT_LOCATIONS);
		UpdateController.INSTANCE.registerCallback(this, FlowRestService.GET_ACTOR_STATUS);
		// updateView(bundle);
		return topLevelView;
	}

	private GetActionStatus getDefaultActionStatus() {
		SelectClassTypesByFacilityId selectClassTypesByFacilityId =
				UpdateController.selectClassTypesByFacilityId;
		if (selectClassTypesByFacilityId == null)
			return null;

		String[] classNames = selectClassTypesByFacilityId.getClassNames();
		int index = 0;
		// if (spin != null) {
		// index = spin.getSelectedItemPosition();
		// L.out("spin location: " + index);
		// }
		index = currentPosition;
		if (classNames.length < 1)
			return null;
		String className = classNames[index];
		String classId = selectClassTypesByFacilityId.getClassIds()[index];
		L.out("className: " + className + " classId: " + classId);
		GetActionStatus actionStatus = initAction(classId, className);
		// need to get to support env
		int functionalAreaIndex = 0;
		String functionalAreaId = selectClassTypesByFacilityId.getFunctionalAreaTypeId(functionalAreaIndex);
		actionStatus.setFunctionalAreaTypeId(functionalAreaId);
		return actionStatus;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		L.out("attached: " + getActivity());
	}

	@Override
	public void onDetach() {
		super.onDetach();
		L.out("onDetach: " + getActivity());
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		L.out("onDestroy");
		Spinner spinner = (Spinner) topLevelView.findViewById(R.id.spinner);
		L.out("SAVING: " + spinner.getSelectedItemPosition());
		getArguments().putString(CURRENT_POSITION, spinner.getSelectedItemPosition() + "");
		UpdateController.INSTANCE.unRegisterCallback(this, FlowRestService.SELECT_LOCATIONS);
		UpdateController.INSTANCE.unRegisterCallback(this, FlowRestService.GET_ACTOR_STATUS);
	}

	@Override
	public void onPause() {
		super.onPause();
		L.out("onPause");
		Spinner spinner = (Spinner) topLevelView.findViewById(R.id.spinner);
		L.out("SAVING: " + spinner.getSelectedItemPosition());
		getArguments().putString(CURRENT_POSITION, spinner.getSelectedItemPosition() + "");
	}

	// @Override
	// public void setUserVisibleHint(boolean isVisibleToUser) {
	// super.setUserVisibleHint(isVisibleToUser);
	// if (isVisibleToUser) { }
	// else { }
	// }

	@Override
	public void onHiddenChanged(boolean isVisibleToUser) {
		super.onHiddenChanged(isVisibleToUser);
		L.out("onHiddenChanged: " + isVisibleToUser);
		View view = getActivity().findViewById(R.id.actionPager);
		if (view == null) {
			L.out("Unable to get view for actionPager!");
			return;
		}
		if (!isVisibleToUser)
			view.setVisibility(View.VISIBLE);
		else
			view.setVisibility(View.INVISIBLE);
	}

	// public void setUserVisibleHint(boolean hidden) {
	// super.setUserVisibleHint(hidden);
	// L.out("onHiddenChanged: " + hidden);
	// View view = getActivity().findViewById(R.id.actionPager);
	// if (view == null) {
	// L.out("Unable to get view for actionPager!");
	// return;
	// }
	// if (hidden)
	// view.setVisibility(View.VISIBLE);
	// else
	// view.setVisibility(View.INVISIBLE);
	// }

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);

		// Spinner spinner = (Spinner) topLevelView.findViewById(R.id.spinner);
		// L.out("SAVING: " + spinner.getSelectedItemPosition());
		// outState.putString(CURRENT_POSITION,
		// spinner.getSelectedItemPosition() + "");
		// List<BaseField> baseFields = BaseField.getBaseFieldList();
		if (baseWidgets == null) {
			L.out("ERROR: baseWidgets is null on saveInstanceState!");
			return;
		}
		for (BaseWidget baseWidget : baseWidgets) {
			baseWidget.onSaveInstanceState(outState);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		L.out("onResume:  ");
		if (getActivity() == null) {
			L.out("Unable to get activity for selfActionFragment!");
			return;
		}
		// if (bundle != null) {
		// String position = bundle.getString(CURRENT_POSITION);
		// L.out("position: " + position);
		// // if (position != null) {
		// // currentPosition = (int) L.getLong(position);
		// // L.out("test currentPosition: " + currentPosition);
		// // if (getSpinnerSize() >= currentPosition + 1)
		// // currentPosition = 0;
		// // }
		// }

		String position = getArguments().getString(CURRENT_POSITION);
		L.out("position: " + position);
		if (position != null) {
			currentPosition = (int) L.getLong(position);
			L.out("test currentPosition: " + currentPosition);
			if (getSpinnerSize() >= currentPosition + 1)
				currentPosition = 0;
		}

		createSpinner();
		getActionStatus = getDefaultActionStatus();
		updateView(null);
	}

	// public void onRestoreInstanceState(Bundle outState) {
	// L.out("restore instance");
	// // Spinner spin = (Spinner) getActivity().findViewById(R.id.spinner);
	// if (outState == null)
	// return;
	// int itemPosition = (int) L.getLong(outState.getString("TaskClass"));
	// L.out("itemPosition: " + itemPosition);
	// setSpinner(itemPosition);
	// // resetting = true;
	// // spin.setSelection(itemPosition);
	// for (BaseWidget baseWidget : baseWidgets) {
	// baseWidget.onRestoreInstanceState(outState);
	// }
	// }

	private int getSpinnerSize() {
		SelectClassTypesByFacilityId selectClassTypesByFacilityId =
				UpdateController.selectClassTypesByFacilityId;
		if (selectClassTypesByFacilityId == null) {
			L.out("ERROR - unable to find SelectClassTypesByFacilityId!");
			return 0;
		}
		return selectClassTypesByFacilityId.getClassNames().length;
	}

	private void createSpinner() {
		// MyToast.show("try createSpinner: ");
		if (spin != null || activity == null)
			return;

		// get the classes of self-tasks
		SelectClassTypesByFacilityId selectClassTypesByFacilityId =
				UpdateController.selectClassTypesByFacilityId;
		if (selectClassTypesByFacilityId == null) {
			L.out("ERROR - unable to find SelectClassTypesByFacilityId!");
			return;
		}
		spin = (Spinner) topLevelView.findViewById(R.id.spinner);
		spin.setOnItemSelectedListener(this);
		String[] actionNames = selectClassTypesByFacilityId.getClassNames();
		L.out("createSpinner: " + actionNames.length);
		// for (int i = 0; i < actionNames.length; i++)
		// L.out("className: " + actionNames[i]);

		ArrayAdapter<String> arrayAdapter =
				new ArrayAdapter<String>(activity, R.layout.transport_blue_spinner, actionNames);

		arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spin.setAdapter(arrayAdapter);
		setSpinner(currentPosition);
	}

	public static boolean isReallyVisible() {
		return isVisible;
	}

	protected void setSpinner(int position) {
		if (spin == null) {
			L.out("spinner is null for: " + position);
			return;
		}
		L.out("position: " + position);
		if (getSpinnerSize() < position + 1) {
			L.out("ERROR: changed classes. spinner size: " + getSpinnerSize() + " position: " + position);
			position = 0;
		}
		currentPosition = position;
		setSpinnerSelectionWithoutCallingListener(spin, position);
	}

	private void setSpinnerSelectionWithoutCallingListener(final Spinner spinner, final int selection) {
		final OnItemSelectedListener onItemSelectedListener = spinner.getOnItemSelectedListener();
		spinner.setOnItemSelectedListener(null);
		spinner.post(new Runnable() {

			@Override
			public void run() {
				L.out("selection: " + selection);
				spinner.setSelection(selection);

				spinner.post(new Runnable() {

					@Override
					public void run() {
						spinner.setOnItemSelectedListener(onItemSelectedListener);
					}
				});
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
		// Intent intent = new Intent().setClass(this,
		// AdmissionsTaskActivity.class);
		// startActivity(intent);
		// L.out("onItemCLICK????");
	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {
		// L.out("position: " + position + " " + currentPosition);

		if (currentPosition != position || true) {
			List<Targets> targets = getClassTypes();
			// L.out("targets: " + targets);
			Targets target = targets.get(position);
			// L.out("target: " + target);
			getActionStatus = initAction(target._id, target.name);
			SelectClassTypesByFacilityId selectClassTypesByFacilityId = UpdateController.selectClassTypesByFacilityId;
			String functionalAreaTypeId = selectClassTypesByFacilityId.getFunctionalAreaTypeId(position);
			getActionStatus.setFunctionalAreaTypeId(functionalAreaTypeId);
			// L.out("functionalAreaType_id" +
			// getActionStatus.getFunctionalAreaTypeId());
			updateView(null);
			currentPosition = position;
		}
	}

	public void updateView(Bundle existingBundle) {
		if (activity == null)
			return;
		// L.out("taskClassID: " + taskClassID);
		// L.out("brief: " + brief);
		// L.out("existingBundle: " + existingBundle);
		// L.out("getActionStatus: " + getActionStatus);
		// L.out("getActionStatus: updateView");
		if (getActionStatus == null || getActionStatus.getClassTypeId() == null) {
			getActionStatus = getDefaultActionStatus();
			// L.out("retry getActionStatus: " + getActionStatus);
			if (getActionStatus == null)
				return;
		}
		Targets target = getClassFields(getActionStatus.getClassTypeId());
		// BaseField.setTask(selfTask);
		// L.out("target: " + target);
		if (target == null)
			return;
		baseWidgets = getViews(target.fields);
		validateAll();
		// L.out("baseWidgets: " + baseWidgets.size());
		TableLayout tableLayout = (TableLayout) topLevelView.findViewById(R.id.topLevel);
		tableLayout.removeAllViews();
		tableLayout.setWeightSum(baseWidgets.size());
		// int count = 0;
		for (BaseWidget baseField : baseWidgets) {
			// L.out(count++ + ": " + baseField.field.toStringShort());
			// linearLayout.addView(baseField.getFieldView());
			tableLayout.addView(getSlotValueView(baseField));
		}
		// List<BaseField> baseFields = BaseField.getBaseFieldList();
		// L.out("task: " + selfTask);
		// L.out("test baseFields: " + baseFields.size());
		// Bundle bundle = outState;
		// if (existingBundle != null)
		// bundle = existingBundle;

		if (existingBundle != null) {
			String position = existingBundle.getString("TaskClass");
			L.out("position: " + position);
			if (position != null) {
				int itemPosition = (int) L.getLong(position);
				L.out("test itemPosition: " + itemPosition);
			}
			for (BaseWidget baseField : baseWidgets) {
				baseField.onRestoreInstanceState(existingBundle);
			}
		} else {
			for (BaseWidget baseField : baseWidgets) {
				baseField.onRestoreInstanceState();
			}
		}
		if (baseWidgets.size() > 0) {
			baseWidgets.get(0).validateAll();
		}
	}

	private List<BaseWidget> getViews(List<Fields> fields) {
		List<BaseWidget> baseFieldList = new ArrayList<BaseWidget>();
		for (Fields field : fields) {
			// L.out(" field: " + field);
			String controlType = field.controlType;
			if (controlType.equals(PICK_LIST) || controlType.equals(DROP_MODE_ENTRY)
					|| controlType.equals(SEARCH_LIST)) {
				if (field.fieldName.equals(START) || field.fieldName.equals(DESINATION))
					baseFieldList.add(new TypeAheadWidget(activity, field));
				else
					baseFieldList.add(new PickListWidget(activity, field));
			} else if (controlType.equals(DATE_PICKER_CONTROL) | controlType.equals(DATE_TIME_SPLIT)) {
				// L.out("datePicker field: " + field);
				baseFieldList.add(new DatePickerWidget(activity, field));
			} else if (controlType.equals(TEXT)) {
				String control = field.control;
				boolean number = false;
				if (control.equals("txtMedicalRecordNumber")
						|| control.equals("txtPriority"))
					number = true;
				// number = true;
				if (field.name.startsWith("Patient Name"))
					baseFieldList.add(new FullNameWidget(activity, field, number));
				else
					baseFieldList.add(new TextWidget(activity, field, number));
			} else if (controlType.equals(MULTIPLE_LINE_TEXT)) {
				TextWidget textWidget = new TextWidget(activity, field, false);
				textWidget.numLines = 5;
				baseFieldList.add(textWidget);
			} else if (controlType.equals(TYPE_AHEAD)) {
				baseFieldList.add(new TypeAheadWidget(activity, field));
			} else if (controlType.equals(DATE_TIME_SPLIT)) {
				// L.out("date time ignored: " + field);
			} else if (controlType.equals(CHECK_BOX) || controlType.equals(RADIO)) {
				// L.out("field: " + field);
				if (field.name.contains("Isolation"))
					field.name = "Isolation";
				else if (field.name.contains("Display Stat"))
					field.name = "Stat";
				baseFieldList.add(new CheckBoxWidget(activity, field));
			} else {
				L.out("*** ERROR in controlType: " + controlType);
			}
		}
		// baseFieldList.add(makeNoteTextWidget());
		// if (baseFieldList.size() > 1)
		// baseFieldList.get(0).validateAll();
		return baseFieldList;
	}

	// private TextWidget makeNoteTextWidget() {
	// SelectClassTypesByFacilityId selectClassTypesByFacilityId =
	// UpdateController.selectClassTypesByFacilityId;
	// Fields field = selectClassTypesByFacilityId.new Fields();
	// return new TextWidget(activity, field);
	// }

	public void validateAll() {

		if (baseWidgets == null)
			return;
		validated = true;
		// L.out("baseWidgets: " + baseWidgets.size());
		for (BaseWidget baseWidget : baseWidgets) {
			boolean valid = baseWidget.validate();
			// L.out("valid: " + valid + " BaseField: " +
			// baseWidget.getClass() + " "
			// + baseWidget.field);
			if (!valid)
				validated = false;
		}
		updateSubmitButton();
		// L.out("valididate valid: " + validated + " " + baseWidgets.size());
	}

	protected static final String REQUIRED = "#CC0000";
	protected static final String VALID = "#00AA00";
	protected static final String HAVE_TASK = "#00AAAA";

	private void updateSubmitButton() {
		Button button = (Button) topLevelView.findViewById(R.id.submitButton);
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

	private TableRow getSlotValueView(BaseWidget baseField) {
		TableRow tableRow = new TableRow(activity);
		LayoutParams layoutParams =
				new TableRow.LayoutParams(
						TableRow.LayoutParams.MATCH_PARENT,
						TableRow.LayoutParams.MATCH_PARENT, 1.0f);
		tableRow.setLayoutParams(layoutParams);
		tableRow.addView(getSlot(baseField));
		tableRow.addView(baseField.createValueView());
		return tableRow;
	}

	private TextView getSlot(BaseWidget baseField) {
		TextView viewSlot = new TextView(activity);
		LayoutParams layoutParams =
				new TableRow.LayoutParams(
						TableRow.LayoutParams.WRAP_CONTENT,
						TableRow.LayoutParams.MATCH_PARENT);
		viewSlot.setGravity(Gravity.CENTER_VERTICAL);
		// viewSlot.setGravity(Gravity.TOP | Gravity.LEFT);
		viewSlot.setLayoutParams(layoutParams);
		viewSlot.setTextSize(15);
		// viewSlot.setTextColor(getColor(baseField));
		viewSlot.setText(baseField.getField().name);
		baseField.setTitleView(viewSlot);
		// baseField.validate();
		return viewSlot;
	}

	@SuppressWarnings("unused")
	private int getColor(BaseWidget baseWidget) {
		if (baseWidget.getField().required) {
			// L.out("baseField.getValue(): " + baseWidget.getValue());
			if (baseWidget.getValue().equals("")) {
				return Color.parseColor(BaseWidget.REQUIRED);
			}
			else
				return Color.parseColor(BaseWidget.REQUIRED_PRESENT);
		} else
			return Color.parseColor(BaseWidget.OPTIONAL);
	}

	private GetActionStatus initAction(String actionClassId, String className) {
		// L.out("initAction: " + actionClassId + " " + className);
		if (getActionStatus == null)
			getActionStatus = new GetActionStatus();
		getActionStatus.setClassTypeId(actionClassId, className);
		return getActionStatus;
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// L.out("nothing: " + arg0);
	}

	public void submitButtonClick(View view) {
		GetActorStatus getActorStatus = UpdateController.getActorStatus;
		if (getActorStatus.getActionId() != null) {
			MyToast.show("Unable to Self Action when have an existing Action!");
			return;
		}
		getActionStatus.setFacilityId(UpdateController.getActorStatus.getFacilityId());
		getActionStatus.setActorId(getActorStatus.getActorId());
		// GetActionStatus resultStatus =
		// Flow.getFlow().createAction(getActionStatus);
		// L.out("resultStatus: " + resultStatus);

		// UpdateController.INSTANCE.statusWrapper = statusWrapper;
		// UpdateController.INSTANCE.doCallback(statusWrapper,
		// UpdateController.INSTANCE.STATUS_WRAPPER);
		// L.out("did callback: " + getActionStatus);

		for (BaseWidget baseField : baseWidgets) {
			// side-effects
			baseField.setValue();
			// L.out("contentValue: " + contentValues.size());
		}
		// createRecord(task);
		// L.out("submit task: " + selfTask);

		ContentValues contentValues = new ContentValues();
		if (BaseWidget.validated) {

			for (BaseWidget baseField : baseWidgets) {
				baseField.addValue(contentValues);
				// L.out("contentValue: " + contentValues.size());
			}
			String temp = "Created Task: ";
			Set<Entry<String, Object>> valueSet = contentValues.valueSet();
			// L.out("valuseSet: " + valueSet.size());
			Iterator<Entry<String, Object>> iter = valueSet.iterator();
			while (iter.hasNext()) {
				Entry<?, ?> entry = iter.next();
				temp += "\n" + entry.getKey() + ": " + entry.getValue();
				// L.out("Entry: " + entry.getKey() + " " + entry.getValue());
			}
			L.out(temp);
			selfTask(getActionStatus);
			// finish();
		}
		else {
			for (BaseWidget baseField : baseWidgets) {
				baseField.addFailValue(contentValues);
			}
			String temp = "";
			Set<Entry<String, Object>> valueSet = contentValues.valueSet();
			// L.out("valuseSet: " + valueSet.size());
			Iterator<Entry<String, Object>> iter = valueSet.iterator();
			while (iter.hasNext()) {
				Entry<?, ?> entry = iter.next();
				temp += "\n" + entry.getKey() + ": " + entry.getValue();
				// L.out("Entry: " + entry.getKey() + " " + entry.getValue());
			}
			// MyToast.show(temp, Toast.LENGTH_LONG);
			showDialog(temp);
		}
	}

	private void showDialog(String msg) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				getActivity());

		// set title
		alertDialogBuilder.setTitle("Invalid Action");

		// set dialog message
		alertDialogBuilder
				.setMessage(msg)
				.setCancelable(false)
				.setNeutralButton("Ok", null);

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

	private void selfTask(GetActionStatus copyActionStatus) {
		// reverseEndPoints(getActionStatus);
		GetActorStatus getActorStatus = UpdateController.getActorStatus;
		String className = copyActionStatus.getClassName();
		String classTypeId = copyActionStatus.getClassTypeId();
		String functionalAreaTypeId = copyActionStatus.getFunctionalAreaTypeId();

		String localActionId = getLocalActionId();
		getActionStatus.setActionId(localActionId);
		getActionStatus.setActionNumber(localActionId);
		getActionStatus.setLocalActionId(localActionId);

		getActionStatus.setActorId(getActorStatus.getActorId());
		getActionStatus.setCreatedDate();
		getActionStatus.setActorName(User.getUser().getUsername());

		if (!getActionStatus.getDateEdited())
			getActionStatus.setPatientBirthDate("");
		if (!getActionStatus.getDateEdited(true))
			getActionStatus.clearCustomDate();

		getActionStatus.tickled = GJon.FALSE_STRING;
		// L.out("getActionStatus: " + getActionStatus);

		getActorStatus.setActionId(localActionId);
		// if (!getActionStatus.getDateEdited()) {
		// getActionStatus.setPatientBirthDate("");
		// }

		getActorStatus.tickled = GJon.TRUE_STRING;
		getActorStatus.setActionStatusId(StaticFlow.ACTION_ASSIGNED);
		getActorStatus.setActorStatusId(StaticFlow.ACTOR_ASSIGNED);
		// getActionStatus.setDateEdited(true);
		L.out("putActionHashtable: " + getActionStatus.getActionId());
		UpdateController.putActionStatus(getActionStatus);
		UpdateController.getActionStatus = getActionStatus;
		// UpdateController.getActionHistory.addTarget(getActionStatus.getTarget(0));

		FlowBinder.updateLocalDatabase(FlowRestService.GET_ACTOR_STATUS, getActorStatus);
		FlowBinder.updateLocalDatabase(FlowRestService.GET_ACTION_STATUS, getActionStatus, localActionId);
		TransportActivity.setNotify(TransportActivity.ACTION_VIEW_PAGE);
		UpdateController.INSTANCE.callback(getActorStatus, FlowRestService.GET_ACTOR_STATUS);

		getActionStatus = new GetActionStatus();
		getActionStatus.getActionStatusInner.init();
		getActionStatus.setActionStatusId(StaticFlow.ACTION_ASSIGNED, null, true);
		getActionStatus.setClassTypeId(classTypeId, className);
		getActionStatus.setFunctionalAreaTypeId(functionalAreaTypeId);
		getActionStatus.setFacilityId(copyActionStatus.getFacilityId());
		getActionStatus.setDateEdited(false);
		getActionStatus.setDateEdited(false, true);
		validateAll();
		updateView(null);
	}

	private String getLocalActionId() {
		return "L" + new GregorianCalendar().getTimeInMillis();
	}

	@Override
	public String getTitle() {
		return "Self Action";
	}

	public List<Targets> getClassTypes() {
		String facilityId = UpdateController.getActorStatus.getFacilityId();
		SelectClassTypesByFacilityId selectClassTypesByFacilityId = UpdateController.selectClassTypesByFacilityId;
		List<Targets> classTypes = selectClassTypesByFacilityId.getClassTypes(facilityId);
		return classTypes;
	}

	private int findPosition(GetActionStatus actionStatus) {
		List<Targets> targets = getClassTypes();
		int position = 0;
		String classId = actionStatus.getClassTypeId();
		if (classId == null) {
			L.out("ERROR: in ActionStatus - no ClassId: " + actionStatus);
			return 0;
		}
		for (Targets target : targets) {
			if (classId.equals(target._id)) {
				// L.out("found it: " + targets);
				return position;
			}
			position += 1;
		}
		return position;
	}

	public Targets getClassFields(String classId) {
		SelectClassTypesByFacilityId selectClassTypesByFacilityId = UpdateController.selectClassTypesByFacilityId;
		if (selectClassTypesByFacilityId == null) {
			String tmp = "Action Class not found: " + getActionStatus.getClassName()
					+ "\nPlease login again to load the updated class list from Flow";
			ActionViewFragment.reset(tmp, activity);
			return null;
		}
		Targets target = selectClassTypesByFacilityId.getClassId(classId);
		return target;
	}

	@Override
	public void callback(GJon gJon, String payloadName) {
		if (UpdateController.getActorStatus == null
				|| UpdateController.getActorStatus.getFacilityId() == null)
			return;

		createSpinner();
		// update();
		updateView(null);
	}

	public void setAction(GetActionStatus actionStatus) {
		SelfActionFragment.getActionStatus = actionStatus;
		// List<Targets> targets = getClassTypes();
		if (actionStatus.getBirthDate() != null)
			L.out("birthDate: " + actionStatus.getBirthDate());

		if (actionStatus.getBirthDate() != null && !actionStatus.getBirthDate().equals(""))
			actionStatus.setDateEdited(true);
		int position = findPosition(actionStatus);

		setSpinner(position);
		updateView(null);
	}

	@Override
	public boolean wantActions() {
		return false;
	}

	@Override
	public void update() {
		// L.out("Update called on selfActionFragment: " + L.p());
		updateSubmitButton();
	}

	@Override
	public View getTopLevelView() {
		return null;
	}
}

class Arg {
	String type = null;
	String value = null;

	Arg(String title, String value) {
		this.type = title;
		this.value = value;
	}
}
