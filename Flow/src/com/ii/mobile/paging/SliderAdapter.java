package com.ii.mobile.paging;

import java.util.List;

import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ii.mobile.util.L;

/**
 * The <code>PagerAdapter</code> serves the fragments when paging.
 */

public class SliderAdapter extends FragmentStatePagerAdapter {

	private final List<Fragment> fragments;
	@SuppressWarnings("unused")
	private final FragmentManager fragmentManager;

	public SliderAdapter(FragmentManager fm, List<Fragment> fragments) {
		super(fm);
		this.fragments = fragments;
		fragmentManager = fm;
	}

	@Override
	public Parcelable saveState() {
		L.out("doing nothing");
		return null;

	}

	@Override
	public int getItemPosition(Object object) {

		int position = fragments.indexOf(object);
		// L.out("position: " + position);
		if (position != -1) {
			return POSITION_UNCHANGED;
		}
		// L.out("object: " + object + " " + POSITION_NONE);
		return POSITION_NONE;
	}

	@Override
	public Fragment getItem(int item) {
		// L.out("item: " + item + " " + fragments.get(item));
		if (item >= fragments.size()) {
			return null;
		}
		return fragments.get(item);
	}

	@Override
	public int getCount() {
		// L.out("getCount: " + fragments.size() + " " + fragments);
		return fragments.size();
	}

}
