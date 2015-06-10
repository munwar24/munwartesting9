package com.ii.mobile.zone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.ii.mobile.flow.types.SelectAvailableZones;
import com.ii.mobile.flow.types.SelectAvailableZones.Targets;
import com.ii.mobile.flowing.FlowBinder;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.flowing.SyncCallback;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.transport.R; // same package
import com.ii.mobile.util.L;

public class ZoneFragment extends Fragment implements NamedFragment, SyncCallback {
	private View view;
	private Spinner spin;
	private String[] zoneNames = new String[] { "zone one", "zone two", "zone three", "zone four",
			"zone five", "zone six", "zone seven" };
	private int currentPosition;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		if (container == null) {

			return null;
		}

		view = inflater.inflate(R.layout.zone, container, false);

		// Button dataButton = (Button)
		// view.findViewById(R.id.dataButton);
		// dataButton.setOnClickListener(new View.OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// SampleFragment.sampleFragment.selectData();
		// }
		// });

		spin = (Spinner) view.findViewById(R.id.zoneSpinner);

		L.out("created SampleActionFragment view ");

		createSpinner();

		return view;
	}

	public void addToSpinner(String text) {
		String[] newActionNames = new String[zoneNames.length + 1];
		for (int i = 0; i < zoneNames.length; i++)
			newActionNames[i + 1] = zoneNames[i];
		newActionNames[0] = text;
		zoneNames = newActionNames;
		createSpinner();

	}

	public void addToSpinner() {
	}

	@Override
	public void onPause() {
		L.out("onPause");
		super.onPause();

	}

	@Override
	public void onResume() {
		super.onResume();
		L.out("onResume");
		UpdateController.INSTANCE.registerCallback(this, FlowRestService.GET_ACTOR_STATUS);

		update();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	protected void setNameSelection(String name) {
		L.out("string: " + name);
		SelectAvailableZones selectAvailableZones = UpdateController.selectAvailableZones;
		String zoneId = selectAvailableZones.getZoneId(name);
		L.out("zoneId: " + zoneId);
		selectAvailableZones.assignedZone = zoneId;
		selectAvailableZones.tickled = GJon.FALSE_STRING;
		FlowBinder.updateLocalDatabase(FlowRestService.SELECT_AVAILABLE_ZONES, selectAvailableZones);
	}

	public void createSpinner() {
		// MyToast.show("try createSpinner: ");
		zoneNames = getZoneNames();
		// MyToast.show("did try createSpinner: ");
		spin.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				// MyToast.show("beep: " + position);
				setNameSelection(zoneNames[position]);
			}

			@Override
			public void onNothingSelected(AdapterView<?> parent) {

			}
		});

		if (zoneNames == null || zoneNames.length == 0) {
			zoneNames = new String[1];
			zoneNames[0] = "default";
		}

		L.out("createSpinner: " + zoneNames.length);
		// for (int i = 0; i < actionNames.length; i++)
		// L.out("className: " + actionNames[i]);

		ArrayAdapter<String> arrayAdapter =
				new ArrayAdapter<String>(getActivity(), R.layout.transport_blue_spinner, zoneNames);

		arrayAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
		spin.setAdapter(arrayAdapter);
		SelectAvailableZones selectAvailableZones = UpdateController.selectAvailableZones;
		int index = selectAvailableZones.getZoneIndex(selectAvailableZones.assignedZone);
		L.out("index: " + index);
		if (index != -1)
			currentPosition = index;
		setSpinnerSelectionWithoutCallingListener(spin, currentPosition);
	}

	private String[] getZoneNames() {
		SelectAvailableZones selectAvailableZones = UpdateController.selectAvailableZones;
		String[] temp = new String[selectAvailableZones.selectAvailableZonesInner.targets.size()];
		int index = 0;
		for (Targets target : selectAvailableZones.selectAvailableZonesInner.targets) {
			temp[index] = target.name;
			index += 1;
		}
		return temp;
	}

	protected void setSpinner(int position) {
		if (spin == null) {
			L.out("spinner is null for: " + position);
			return;
		}
		L.out("position: " + position + " " + zoneNames.length);
		if (zoneNames.length > position) {
			L.out("ERROR: changed spinner size: " + zoneNames.length + " position: " + position);
			position = 0;
		}
		currentPosition = position;
		// setSpinnerSelectionWithoutCallingListener(spin, position);
		spin.setSelection(position);
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
	public void callback(GJon gJon, String payloadName) {
		update();
	}

	@Override
	public String getTitle() {
		return "Zone Selection";
	}

	@Override
	public View getTopLevelView() {

		return null;
	}

	@Override
	public void update() {

	}

	@Override
	public boolean wantActions() {

		return false;
	}

}