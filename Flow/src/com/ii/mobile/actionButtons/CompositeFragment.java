package com.ii.mobile.actionButtons;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ii.mobile.flowing.SyncCallback;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.transport.R;
import com.ii.mobile.util.L;

/**

 */
public class CompositeFragment extends Fragment implements NamedFragment, SyncCallback {
	public final static String FRAGMENT_TAG = "compositeFragment";
	private View view;
	private FragmentActivity activity;
	private boolean visible;
	private boolean inited = false;
	public static ActionFragment actionFragment = null;
	public static BreakFragment breakFragment = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		L.out("onCreate");
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		this.activity = (FragmentActivity) activity;

	}

	@Override
	public void onDetach() {
		super.onDetach();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		visible = true;

		this.activity = getActivity();
		view = inflater.inflate(R.layout.composite, container, false);

		update();
		L.out("created view: ");
		// UpdateController.INSTANCE.registerCallback(this,
		// FlowRestService.GET_ACTOR_STATUS);
		return view;
	}

	@Override
	public String getTitle() {
		return "Not Used";
	}

	@Override
	public void update() {
		L.out("CompositeFragment update: " + visible + " " + inited);
	}

	@Override
	public void onPause() {
		L.out("onPause");
		super.onPause();

	}

	@Override
	public void onResume() {
		super.onResume();
		L.out("onResume: " + inited);
		if (!inited) {

			if (view != null) {
				FragmentTransaction ft = getActivity().getSupportFragmentManager()
						.beginTransaction();
				actionFragment = (ActionFragment) Fragment.instantiate(getActivity(), ActionFragment.class.getName());
				breakFragment = (BreakFragment) Fragment.instantiate(getActivity(), BreakFragment.class.getName());
				ft.add(R.id.action_header, actionFragment);
				ft.add(R.id.break_header, breakFragment);
				// ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				ft.commitAllowingStateLoss();
				inited = true;
			} else {
				L.out("view  is null");
			}
		}
		update();

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void callback(GJon gJon, String payloadName) {

		update();
	}

	@Override
	public View getTopLevelView() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean wantActions() {
		// TODO Auto-generated method stub
		return false;
	}
}
