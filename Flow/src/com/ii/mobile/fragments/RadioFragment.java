package com.ii.mobile.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.ii.mobile.transport.R; // same package

/**

 */
public class RadioFragment extends Fragment {

	private LinearLayout radioLayout;
	private RadioGroup radioGroup = null;
	public final static String RADIO_FRAGMENT_TAG = "radioFragment";

	// public static TitleFragment titleFragment = null;

	// @Override
	// public void onCreate(Bundle bundle) {
	// // TitleFragment.titleFragment = this;
	// // getActivity().getSupportFragmentManager().beginTransaction().
	// // add(0, this, TITLE_FRAGMENT_TAG).
	// // commit();
	// // Object result =
	// //
	// getActivity().getSupportFragmentManager().findFragmentByTag(TITLE_FRAGMENT_TAG);
	// // L.out("******************************** result: " + result +
	// // " getTag: " + getTag());
	// }

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// if (container == null) {
		// return null;
		// }
		// L.out("before");
		radioLayout = (LinearLayout) inflater.inflate(R.layout.frag_radio_frank, container, false);
		radioGroup = (RadioGroup) radioLayout.findViewById(R.id.radioGroup);
		radioGroup.setOnCheckedChangeListener(radioListener);
		// L.out("after");
		return radioLayout;
	}

	boolean ignore = false;

	public RadioFragment setPosition(int position) {
		// L.out("position: " + position);
		RadioButton radioButton = (RadioButton) radioGroup.getChildAt(position);
		// L.out("radioButton: " + radioButton);
		if (radioButton != null && !radioButton.isChecked())
			radioGroup.check(radioButton.getId());
		// ignore = true;
		return this;
	}

	private final OnCheckedChangeListener radioListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
			// L.out("radioButton: " + checkedId + " " + ignore);
			// int index =
			// radioGroup.indexOfChild(radioLayout.findViewById(checkedId));
			// if (!ignore)
			// ((Cache) getActivity()).setPagePosition(index);
			ignore = false;
		}
	};

	public void setNotify(int position) {
		RadioButton radioButton = (RadioButton) radioGroup.getChildAt(position);
		// L.out("radioButton: " + radioButton);
		radioButton.setTextColor(Color.CYAN);
	}
}