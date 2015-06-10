package com.ii.mobile.paging;

import java.util.Vector;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;

import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.timers.TimerFragment;

abstract public class FragmentController {

	protected Vector<Fragment> fragments = new Vector<Fragment>();
	protected SliderAdapter sliderAdapter;

	protected FragmentActivity fragmentActivity;
	protected NamedFragment currentFragment = null;
	protected TimerFragment timerFragment = null;

	public FragmentController(Activity activity) {

		fragmentActivity = (FragmentActivity) activity;
		initialize();
	}

	abstract protected void initialize();

	abstract public void setPosition(int i);

}
