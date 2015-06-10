package com.ii.mobile.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ii.mobile.transport.R; // same package
import com.ii.mobile.util.L;

/**
 
 */
public class BlankFragment extends Fragment implements NamedFragment {
	public final static String FRAGMENT_TAG = "blankFragment";

	/**
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
	 *      android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		L.out("Create BlankFragment");
		if (container == null) {
			return null;
		}

		return inflater.inflate(R.layout.frag_blank, container, false);
	}

	@Override
	public String getTitle() {
		return "Action Home";
	}

	@Override
	public void update() {
		L.out("BlankFragment update");
	}

	@Override
	public boolean wantActions() {
		return true;
	}

	@Override
	public View getTopLevelView() {
		return null;
	}
}
