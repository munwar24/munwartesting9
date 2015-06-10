package com.ii.mobile.paging;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;

import com.ii.mobile.actionButtons.CompositeFragment;
import com.ii.mobile.flowing.SyncCallback;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.timers.TimerFragment;
import com.ii.mobile.transport.R; // same package
import com.ii.mobile.util.L;

public class ActionFragmentController extends FragmentController implements SyncCallback {

	// private ActionFragment actionFragment;
	// private BreakFragment breakFragment;

	public ActionFragmentController(Activity activity) {
		super(activity);
		L.out("create ActionFragmentController");
	}

	// public void update() {
	//
	// L.out("update ActionFragmentController: " +
	// UpdateController.getActorStatus);
	// if (true)
	// return;
	// GetActorStatus getActorStatus = UpdateController.getActorStatus;
	// if (currentFragment == null) {
	// L.out("no currentFragment");
	// return;
	// }
	// if (getActorStatus == null || getActorStatus.getActionId() == null) {
	// L.out("no task");
	// setBreakFragment();
	//
	// } else {
	//
	// L.out("remove BreakFragment and add ActionFragment");
	// setActionFragment();
	// }
	// }
	//
	// private void setBreakFragment() {
	// View actionLayout = fragmentActivity.findViewById(R.id.action_header);
	// View breakLayout = fragmentActivity.findViewById(R.id.break_header);
	// if (actionLayout != null)
	// actionLayout.setVisibility(View.GONE);
	// if (breakLayout != null)
	// breakLayout.setVisibility(View.VISIBLE);
	// }
	//
	// private void setActionFragment() {
	// View actionLayout = fragmentActivity.findViewById(R.id.action_header);
	// View breakLayout = fragmentActivity.findViewById(R.id.break_header);
	// if (actionLayout != null)
	// actionLayout.setVisibility(View.VISIBLE);
	// if (breakLayout != null)
	// breakLayout.setVisibility(View.GONE);
	// }

	@Override
	protected void initialize() {
		currentFragment = (NamedFragment) Fragment.instantiate(fragmentActivity, CompositeFragment.class.getName());
		fragments.add((Fragment) currentFragment);

		timerFragment = (TimerFragment) Fragment.instantiate(fragmentActivity, TimerFragment.class.getName());
		fragments.add(timerFragment);
		L.out("timerFragment: " + timerFragment);

		sliderAdapter = new SliderAdapter(fragmentActivity.getSupportFragmentManager(), fragments);
		ViewPager pager = (ViewPager) fragmentActivity.findViewById(R.id.actionPager);

		pager.setAdapter(sliderAdapter);
		setPosition(1);

	}

	@Override
	public void setPosition(int position) {
		ViewPager pager = (ViewPager) fragmentActivity.findViewById(R.id.actionPager);
		pager.setCurrentItem(position);

	}

	@Override
	public void callback(GJon gJon, String payloadName) {
		// L.out("callback: " + gJon.getClass().getSimpleName());
		// update();
	}
}
