package com.ii.mobile.paging;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.ii.mobile.actionHistory.ActionHistoryFragment;
import com.ii.mobile.actionView.ActionViewFragment;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.fragments.RadioFragment;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.instantMessage.InstantMessageFragment;
import com.ii.mobile.instantMessage.MessageFragment;
import com.ii.mobile.instantMessage.OpsInstantMessageFragment;
import com.ii.mobile.selfAction.SelfActionFragment;
import com.ii.mobile.transport.R; // same package
import com.ii.mobile.util.L;
import com.ii.mobile.zone.ZoneFragment;

public class PageFragmentController extends FragmentController {
	private int focusedPage = TransportActivity.ACTION_VIEW_PAGE;
	private TransportActivity transportActivity = null;

	public PageFragmentController(Activity activity) {
		super(activity);
		this.transportActivity = (TransportActivity) activity;
	}

	@Override
	protected void initialize() {
		Fragment fragment = Fragment.instantiate(fragmentActivity,
				OpsInstantMessageFragment.class.getName());
		// OpsInstantMessageFragment.messageFragment = (MessageFragment)
		// fragment;
		((MessageFragment) fragment).setTitle("Instant Message Ops");
		fragments.add(fragment);
		fragment = Fragment.instantiate(fragmentActivity, InstantMessageFragment.class.getName());
		// InstantMessageFragment.messageFragment = (MessageFragment) fragment;
		((MessageFragment) fragment).setTitle("Instant Message Dispatch");
		// ((InstantMessageFragment) fragment).setPrimary();
		fragments.add(fragment);

		currentFragment = (NamedFragment) Fragment.instantiate(fragmentActivity, ActionViewFragment.class.getName());
		fragments.add((Fragment) currentFragment);
		fragments.add(Fragment.instantiate(fragmentActivity, SelfActionFragment.class.getName()));
		fragments.add(Fragment.instantiate(fragmentActivity, ActionHistoryFragment.class.getName()));
		fragments.add(Fragment.instantiate(fragmentActivity, ZoneFragment.class.getName()));
		// fragments.add(Fragment.instantiate(fragmentActivity,
		// StandingsFragment.class.getName()));

		sliderAdapter = new SliderAdapter(fragmentActivity.getSupportFragmentManager(), fragments);

		ViewPager pager = (ViewPager) fragmentActivity.findViewById(R.id.viewpager);
		pager.setAdapter(sliderAdapter);

		pager.setOnPageChangeListener(new MyPageChangeListener());
		L.out("focusedPage: " + focusedPage);
		// int position = focusedPage;
		// pager.setCurrentItem(position);
		// setRadioPosition(position);
	}

	@Override
	public void setPosition(int position) {
		ViewPager pager = (ViewPager) fragmentActivity.findViewById(R.id.viewpager);
		pager.setCurrentItem(position);
		setRadioPosition(position);
		NamedFragment namedFragment = (NamedFragment) fragments.get(position);
		L.out("setPosition focusedPage: " + focusedPage + " " +
				namedFragment.getTitle());
		transportActivity.updatePageTitle(namedFragment.getTitle());
	}

	public class MyPageChangeListener extends ViewPager.SimpleOnPageChangeListener {
		@Override
		public void onPageSelected(int position) {

			NamedFragment namedFragment = (NamedFragment) fragments.get(position);
			L.out("MyPageChangeListener focusedPage: " + focusedPage + " " +
					namedFragment.getTitle());
			transportActivity.updatePageTitle(namedFragment.getTitle());
			if (position != focusedPage)
				setRadioPosition(position);
			focusedPage = position;

			controlActionFragment(namedFragment.wantActions());
		}

		private void controlActionFragment(boolean wantActions) {

			View view = fragmentActivity.findViewById(R.id.actionPager);
			if (view == null) {
				L.out("Unable to get view for actionPager!");
				return;
			}
			L.out("wantActions: " + wantActions + " view: " + view.getVisibility());
			if (wantActions)
				view.setVisibility(View.VISIBLE);
			else
				view.setVisibility(View.GONE);
		}
	}

	public void setRadioPosition(int position) {
		RadioFragment radioFragment = (RadioFragment)
				fragmentActivity.getSupportFragmentManager().findFragmentByTag(RadioFragment.RADIO_FRAGMENT_TAG);
		// L.out("position: " + position + " focusedPage: " + focusedPage);
		if (radioFragment != null)
			// if (position != focusedPage)
			radioFragment.setPosition(position);

		else
			L.out("*** ERROR RadioFragment not found!");
	}
}
