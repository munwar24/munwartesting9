package com.ii.mobile.fragments;

import android.view.View;

public interface NamedFragment {

	String getTitle();

	public View getTopLevelView();

	void update();

	boolean wantActions();
}
