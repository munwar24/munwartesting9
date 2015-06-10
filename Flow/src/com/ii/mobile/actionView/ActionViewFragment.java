package com.ii.mobile.actionView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.ii.mobile.flow.staticFlow.Equipments;
import com.ii.mobile.flow.staticFlow.Modes;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flow.types.SelectClassTypesByFacilityId;
import com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Fields;
import com.ii.mobile.flow.types.SelectClassTypesByFacilityId.Targets;
import com.ii.mobile.flow.types.SelectLocations;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.flowing.SyncCallback;
import com.ii.mobile.flowing.UnitTest;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.home.LoginActivity;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.tab.SelfTaskActivity;
import com.ii.mobile.tickle.Tickler;
import com.ii.mobile.transport.R;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

// same package

/**

 */
public class ActionViewFragment extends Fragment implements NamedFragment, SyncCallback {
	public final static String FRAGMENT_TAG = "ActionViewFragment";
	private FragmentActivity activity = null;
	// private Cache cache = null;
	private View topLevelView = null;
	private TableLayout actionViewTable = null;
	private Vibrator vibrator = null;

	public final static String NO_TASK = "No Task";
	public final static String UNASSIGNED = "Unassigned";
	public final static String ASSIGNED = "Assigned";
	public final static String ACTIVE = "Active";
	public final static String DELAYED = "Delayed";
	public final static String COMPLETED = "Completed";
	public final static String CANCELED = "Canceled";

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		activity = getActivity();
		vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);

		// this.cache = (Cache) activity;
		// cache.setTask(createTestTask());
		topLevelView = inflater.inflate(R.layout.transport_action_view, container, false);
		actionViewTable = (TableLayout) topLevelView.findViewById(R.id.actionViewTable);
		L.out("topLevelView: " + topLevelView);
		UpdateController.INSTANCE.registerCallback(this, FlowRestService.GET_ACTOR_STATUS);
		SharedPreferences settings = activity.getSharedPreferences(User.PREFERENCE_FILE, 0);
		boolean staffUser = settings.getBoolean(LoginActivity.STAFF_USER, false);
		if (staffUser)
			addTestListener(topLevelView.findViewById(R.id.actionViewTable));
		update();
		return topLevelView;
	}

	private void addTestListener(View view) {

		view.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {
				// L.out("double click");
				vibrator.vibrate(400);
				MyToast.show("Flow data integrity Unit Test Easter Egg\nClearing Action and reloading from Flow");
				GetActorStatus getActorStatus = UpdateController.getActorStatus;
				getActorStatus.setActionStatusId(StaticFlow.ACTOR_AVAILABLE);
				UpdateController.removeActionStatus(getActorStatus.getActionId());
				getActorStatus.setActionId(null);
				Tickler.lastGetActorStatus = null;
				UpdateController.INSTANCE.callback(getActorStatus, FlowRestService.GET_ACTOR_STATUS);
				return true;
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		update();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		UpdateController.INSTANCE.unRegisterCallback(this, FlowRestService.GET_ACTOR_STATUS);
	}

	private void updateTable(GetActionStatus getActionStatus) {
		actionViewTable.removeAllViews();
		updateStaticTable(getActionStatus);
		updateDynamicTable(getActionStatus);
	}

	private String trim(String temp, int length) {
		if (temp == null)
			return "";
		if (temp.length() < length)
			return temp;
		return temp.substring(0, length - 1);
	}

	public static String convertDate(String stringDate) {
		// L.out("stringDate: " + stringDate);
		if (stringDate == null || stringDate.equals(""))
			return "";
		SimpleDateFormat simpleDateFormat = new
				SimpleDateFormat("yyyy/MM/dd");
		try {
			Calendar calendar = Calendar.getInstance();

			calendar.setTime(simpleDateFormat.parse(stringDate));
			// L.out("calendar: " + calendar);
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH);
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			return (month + 1) + "/" + day + "/" + year;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "";
	}

	@SuppressLint("DefaultLocale")
	private void updateStaticTable(GetActionStatus getActionStatus) {
		// L.out("update table: " + getActionStatus);
		addRow("Patient:", getActionStatus.getPatientName(), null);
		// addRow("DOB / MRN:", getActionStatus.getPatientBirthDate(),
		// trim(getActionStatus.getPatientMRN(), 4));
		// String dob = getActionStatus.getPatientBirthDate();
		// if (dob != null)
		// dob.replace("-", "//");
		String dob = getActionStatus.getPatientBirthDate();
		if (true) {
			dob = convertDate(dob);
			if (dob != null && dob.startsWith("0"))
				dob = dob.substring(1, dob.length());
			addRow("DOB:", dob, null);
		}
		addRow("MRN:", getActionStatus.getPatientMRN(), null);
		addIsolationRow(getActionStatus.getIsolationPatient());
		addStatRow(getActionStatus.getActionTypeId());
		addActionNumberRow(getActionStatus);
		// addRow("Action#:", getActionStatus.getActionNumber(), null);
		addRow(null, null, null);
		addRow("Type:", getActionStatus.getClassName(), null);
		addRow("Start:", trim(getActionStatus.getStartName(), 23), null);
		addRow("Dest:", trim(getActionStatus.getDestinationName(), 23), null);
		addRow("Mode:", Modes.INSTANCE.getDescription(getActionStatus.getModeId()), null);
		// addRow("Mode:", getActionStatus.getModeId() + ": "
		// + Modes.INSTANCE.getDescription(getActionStatus.getModeId()), null);
		addRow("Equipment:", Equipments.INSTANCE.getDescription(getActionStatus.getEquipmentId()), null);
		addRow(null, null, null);
		// String notes = getActionStatus.getNotes();
		// if (notes != null)
		// notes = notes.toUpperCase();
		// addNote(notes);
		// addRow(null, null, null);
	}

	private void addActionNumberRow(GetActionStatus getActionStatus) {
		String temp = getActionStatus.getActionNumber();
		// L.out("temp: " + temp);
		boolean local = false;
		if (temp != null && temp.startsWith("L")) {
			// temp = getActionStatus.getActionId();
			local = true;
			temp = "Local";
		}
		addRow("Action#:", temp, null, local);
	}

	private final String NOTE_NAME = "Note";

	private void updateDynamicTable(GetActionStatus getActionStatus) {
		String classTypeId = getActionStatus.getClassTypeId();
		// L.out("classId: " + classId);
		SelectClassTypesByFacilityId selectClassTypesByFacilityId = UpdateController.selectClassTypesByFacilityId;
		if (selectClassTypesByFacilityId == null)
			return;
		Targets fields = selectClassTypesByFacilityId.getClassId(classTypeId);
		if (fields == null) {
			String tmp = "Action Class not found: " + getActionStatus.getClassName()
					+ "\nPlease login again to load the updated class list from Flow";
			reset(tmp, activity);
			return;
		}

		// L.out("fields: \n" + fields);
		Targets temp = fields.copy();
		// L.out("temp: \n" + temp);
		removeField(temp, "Patient DOB");
		removeField(temp, "Patient Name");
		removeField(temp, "Start");
		removeField(temp, "Destination");
		removeField(temp, "Mode");
		removeField(temp, "Equipment");
		removeField(temp, "Isolation");
		removeField(temp, "Display");
		removeField(temp, "MRN");
		removeField(temp, "Stat");
		// L.out("removeField: \n" + temp);
		// L.out("adding fields");
		for (Fields field : temp.fields) {
			// L.out("adding fields: " + field);
			if (field.name.equals(NOTE_NAME)) {
				addNote(getActionStatus.getNamedValue(field.name));
			} else {
				String name = field.name;
				if (name.equals("Patient Gender"))
					name = "Gender";
				if (field.custom) {
					// L.out("custom fields: " + field);
					if (field.controlType.equalsIgnoreCase("datePickerControl")) {
						if (true || getActionStatus.getDateEdited(true)) {
							String dob = getActionStatus.getNamedValue(field.control, true);
							// dob = convertDate(dob);
							if (dob != null && dob.startsWith("0"))
								dob = dob.substring(1, dob.length());
							addRow(name + ":", dob, null);
						}
					}
					else
						addRow(name + ":", getActionStatus.getNamedValue(field.control, true), null);
				}
				else
					addRow(name + ":", getActionStatus.getNamedValue(name), null);
			}
		}
	}

	public static void reset(String tmp, FragmentActivity fragmentActivity) {
		MyToast.show(tmp);
		MyToast.show(tmp);
		// UpdateController.selectClassTypesByFacilityId = null;
		// UpdateController.selectLocations = null;
		// FlowDbAdapter.getFlowDbAdapter().deleteAll();
		UpdateController.clearStaticLoad();
		SelfTaskActivity.initDataCache();
		// FlowBinder.deleteLocalDatabase(FlowRestService.SELECT_CLASS_TYPES_BY_FACILITY_ID);
		// FlowBinder.deleteLocalDatabase(FlowRestService.SELECT_LOCATIONS);
		fragmentActivity.finish();
	}

	@SuppressLint("DefaultLocale")
	private void removeField(Targets targets, String name) {
		for (int i = 0; i < targets.fields.size(); i++) {
			String test = targets.fields.get(i).name.toLowerCase();
			name = name.toLowerCase();
			if (test.startsWith(name)) {
				// L.out("removing: " + targets.fields.get(i));
				targets.fields.remove(i);
				i -= 1;
			}
		}
	}

	private void addStatRow(String actionTypeId) {
		// L.out("actionTypeId: " + actionTypeId);
		String temp = "Yes";
		boolean hightlight = true;

		if (actionTypeId == null || actionTypeId.equals(GetActionStatus.ROUTINE)) {
			temp = "No";
			hightlight = false;
		}
		addRow("Stat:", temp, null, hightlight);
	}

	private void addIsolationRow(boolean isolationPatient) {
		// L.out("isolationPatient: " + isolationPatient);
		TableRow tableRow = (TableRow) activity.getLayoutInflater().inflate(R.layout.transport_action_row, actionViewTable, false);
		((TextView) tableRow.findViewById(R.id.first)).setText("Isolation:");

		int color = Color.parseColor("#FF0000");
		String isolation = "Yes";
		if (!isolationPatient) {
			isolation = "No";
			color = Color.parseColor("#000000");
		}
		TextView textView = ((TextView) tableRow.findViewById(R.id.second));
		textView.setTextColor(color);
		textView.setText(isolation);
		actionViewTable.addView(tableRow);
	}

	private void addNote(String notes) {
		TableRow tableRow = (TableRow) activity.getLayoutInflater().inflate(R.layout.transport_action_note, actionViewTable, false);
		((TextView) tableRow.findViewById(R.id.first)).setText("Notes:");
		((TextView) tableRow.findViewById(R.id.second)).setText(notes);
		actionViewTable.addView(tableRow);
	}

	private void addRow(String first, String second, String third) {
		addRow(first, second, third, false);
	}

	private void addRow(String first, String second, String third, boolean highlight) {
		TableRow tableRow = (TableRow) activity.getLayoutInflater().inflate(R.layout.transport_action_row, actionViewTable, false);
		if (first != null) {
			first = first.replace("Requestor ", "");
			if (first.indexOf("Schedule") != -1) {
				second = parseDate(second);
			}
		}
		TextView textView = ((TextView) tableRow.findViewById(R.id.first));
		textView.setText(first);

		textView = ((TextView) tableRow.findViewById(R.id.second));
		textView.setText(second);
		if (highlight)
			textView.setTextColor(Color.RED);

		if (third != null)
			((TextView) tableRow.findViewById(R.id.third)).setText(third);
		actionViewTable.addView(tableRow);
	}

	public static String toDateDayHour(long time) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm");
		sdf.setTimeZone(calendar.getTimeZone());
		// System.out.println("L.toDate: "+sdf.format(calendar.getTime()));
		return sdf.format(calendar.getTime());
	}

	private String parseDate(String receivedDate) {
		if (receivedDate == null)
			return "";
		Long temp = L.getLong(receivedDate);
		if (temp != 0)
			return L.toDateSecond(temp);
		int index = receivedDate.indexOf("T");
		if (index == -1)
			return receivedDate;
		int jindex = receivedDate.indexOf(".", index);
		if (jindex == -1)
			return receivedDate;
		return receivedDate.substring(index + 1, jindex);
	}

	@Override
	public void update() {
		// L.out("topLevelView: " );
		if (topLevelView == null) {
			L.out("topLevelView is null");
			return;
		}

		GetActionStatus getActionStatus = UpdateController.getActionStatus;
		L.out("getActionStatus update: ");
		// L.out("getActionStatus update: " + getActionStatus);

		if (getActionStatus == null) {
			updateNoAction();
		} else
			updateHaveAction(getActionStatus);
	}

	int index = 0;

	private GetActionStatus makeSample() {
		GetActorStatus getActorStatus = UpdateController.getActorStatus;
		String facilityId = getActorStatus.getFacilityId();
		SelectLocations selectLocations = UpdateController.selectLocations;
		SelectClassTypesByFacilityId selectClassTypesByFacilityId = UpdateController.selectClassTypesByFacilityId;
		if (selectClassTypesByFacilityId == null || facilityId == null) {
			L.out("selectClassTypesByFacilityId: " + selectClassTypesByFacilityId);
			return null;
		}

		List<Targets> classTypes = selectClassTypesByFacilityId.getClassTypes(facilityId);
		index = (index % classTypes.size());
		// L.out("index: " + index);
		GetActionStatus getActionStatus = new UnitTest().createTestActionStatus(facilityId, classTypes.get(index), selectLocations);
		index += 1;
		// GetActionStatus resultStatus =
		// Flow.getFlow().createAction(getActionStatus);
		// L.out("resultStatus: " + resultStatus);
		return getActionStatus;
	}

	private void updateNoAction() {
		topLevelView.findViewById(R.id.notice).setVisibility(View.VISIBLE);
		topLevelView.findViewById(R.id.logo).setVisibility(View.VISIBLE);
		topLevelView.findViewById(R.id.actionViewTable).setVisibility(View.GONE);
		// topLevelView.findViewById(R.id.taskTable).setVisibility(View.GONE);
	}

	private void updateHaveAction(GetActionStatus getActionStatus) {
		updateTable(getActionStatus);
		topLevelView.findViewById(R.id.notice).setVisibility(View.GONE);
		topLevelView.findViewById(R.id.logo).setVisibility(View.GONE);
		topLevelView.findViewById(R.id.actionViewTable).setVisibility(View.VISIBLE);
		// topLevelView.findViewById(R.id.taskTable).setVisibility(View.VISIBLE);
	}

	@Override
	public String getTitle() {
		return "Action Home";
	}

	// public static String lookUpRoomFromValue(Cache cache, String value) {
	// String ROOMS_SOURCE = "hrcAjaxRoomsSelectByFacilityAsKeyValue";
	// L.out("cache: " + cache);
	// if (cache == null) {
	// L.out("rooms not yet available");
	// return value;
	// }
	// PickList[] picks = cache.getPickListGeography(ROOMS_SOURCE).pickList;
	// // L.out("pick" + picks.length);
	// // L.out("pick1" + picks[0].textPart + " " + picks[0].valuePart);
	// for (int i = 0; i < picks.length; i++) {
	// if (picks[i].valuePart.equals(value)) {
	// return picks[i].textPart;
	// }
	// }
	// L.out("Unable to find: " + value);
	// return value;
	// }

	@Override
	public void callback(GJon gJon, String payloadName) {
		GetActionStatus getActionStatus = UpdateController.getActionStatus;
		String actionNumber = null;
		if (getActionStatus != null)
			actionNumber = getActionStatus.getActionNumber();
		L.out("TaskFragment: " + actionNumber);
		update();
	}

	@Override
	public boolean wantActions() {
		return true;
	}

	@Override
	public View getTopLevelView() {
		return topLevelView;
	}
}
