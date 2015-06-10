/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.ii.mobile.actionHistory;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Vibrator;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.ii.mobile.flow.staticFlow.Equipments;
import com.ii.mobile.flow.staticFlow.Modes;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.GetActionStatus.Targets;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.home.LoginActivity;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.selfAction.SelfActionFragment;
import com.ii.mobile.transport.R;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;

public class ActionHistoryCursorAdapter extends ArrayAdapter<Targets> {

	private final FragmentActivity fragmentActivity;

	private final Vibrator vibrator;

	// private Targets[] items = null;

	static class ViewHolder {
		// the views
		protected TextView topView = null;
		@SuppressWarnings("unused")
		private ImageView colorShapeView;
		private TextView view1;
		private TextView view2;
		@SuppressWarnings("unused")
		private ImageView historyShapeView;

		private TextView view3;
		private TextView view4;
		private TextView view5;
		private TextView view6;

		// private TextView view7;

		private ViewHolder cacheViews(View view) {
			// topView = (TextView) view.findViewById(R.id.topView);
			view1 = (TextView) view.findViewById(R.id.view1);
			view2 = (TextView) view.findViewById(R.id.view2);
			view3 = (TextView) view.findViewById(R.id.view3);
			view4 = (TextView) view.findViewById(R.id.view4);
			view5 = (TextView) view.findViewById(R.id.view5);
			view6 = (TextView) view.findViewById(R.id.view6);
			// view7 = (TextView) view.findViewById(R.id.view7);
			historyShapeView = (ImageView) view.findViewById(R.id.historyShape);
			colorShapeView = (ImageView) view.findViewById(R.id.colorShape);
			// L.out("viewHolder: " + this);
			// L.out("first staff: " + staff);
			return this;
		}
	}

	public ActionHistoryCursorAdapter(FragmentActivity fragmentActivity,
			ActionHistoryFragment taskListFragment,
			int resourceId, Targets[] items) {
		super(fragmentActivity, resourceId, items);
		// this.items = items;
		this.fragmentActivity = fragmentActivity;
		vibrator = (Vibrator) (fragmentActivity.getSystemService(Context.VIBRATOR_SERVICE));
	}

	@Override
	public int getCount() {
		Targets[] items = UpdateController.getActionHistory.getTargets();
		return items.length;
	}

	@Override
	public Targets getItem(int i) {

		Targets[] items = UpdateController.getActionHistory.getTargets();
		// L.out("items.length: " + items.length);
		if (items == null || i < 0 || i > items.length - 1) {
			L.out("Error in getItem: " + i + " " + items);
			return null;
		}
		i = items.length - i - 1;
		return items[i];
	}

	public int getSize() {
		return 0;
	}

	private String trim(String temp, int length) {
		if (temp == null)
			return "";
		if (temp.length() < length)
			return temp;
		return temp.substring(0, length - 1);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		ViewHolder holder = null;

		final Targets target = getItem(position);
		// L.out("position: " + position);
		// L.out("target: " + target);
		// createSampleData(target);
		LayoutInflater mInflater = (LayoutInflater) fragmentActivity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.action_item, null);
			holder = new ViewHolder();
			holder.cacheViews(convertView);
			convertView.setTag(holder);
			SharedPreferences settings = fragmentActivity.getSharedPreferences(User.PREFERENCE_FILE, 0);
			boolean staffUser = settings.getBoolean(LoginActivity.STAFF_USER, false);
			if (staffUser)
				convertView.setOnLongClickListener(new OnLongClickListener() {
					@Override
					public boolean onLongClick(View v) {
						vibrator.vibrate(200);

						ListView parent = (ListView) (v.getParent());
						int pos = parent.getPositionForView(v);

						Targets target = getItem(pos);
						// L.out("target: " + target);
						String inspection = getInspection(target);
						MyToast.show(inspection);
						MyToast.show(inspection);
						return true;
					}

					private String getInspection(Targets target) {
						return "Action: \n" + target.toStringShort();
					}
				});

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					vibrator.vibrate(200);

					ListView parent = (ListView) (v.getParent());
					int pos = parent.getPositionForView(v);
					v.setSelected(true);
					// MyToast.show("Click-" + pos);
					Targets item = getItem(pos);
					// L.out("item: " + item);
					SelfActionFragment selfActionFragment = SelfActionFragment.selfActionFragment;
					if (selfActionFragment != null) {
						GetActionStatus getActionStatus = new GetActionStatus();
						getActionStatus.replaceTarget(item);
						getActionStatus.getActionStatusInner.init();

						getActionStatus = GetActionStatus.getGJon(getActionStatus.getNewJson());
						// L.out("getActionStatus: " + getActionStatus);
						reverseEndPoints(getActionStatus);
						selfActionFragment.setAction(getActionStatus);
						TransportActivity.setNotify(TransportActivity.SELF_ACTION_PAGE);
					}
				}
			});
		} else
			holder = (ViewHolder) convertView.getTag();

		// 1 - top left, 2 - top middle, 3 - top left, 4 - bottom left, 5 -
		// bottom middle, 6 - bottom right
		// holder.view1.setText(item.actionNumber);

		String start = "No Start";
		if (target.start != null && !target.start.equals(""))
			start = target.start.name;
		holder.view1.setText(trim(start, 20));

		String patient = "No Patient";
		if (target.patient != null && (!target.patient.equals("")
				|| !target.patient.equals(" ")))
			patient = target.getPatientName();
		holder.view2.setText(patient);
		if (target.isolation)
			holder.view2.setTextColor(Color.RED);
		else
			holder.view2.setTextColor(Color.BLACK);

		String destination = "No Dest";
		if (target.destination != null && !target.destination.equals(""))
			destination = target.destination.name;
		holder.view3.setText(trim(destination, 20));

		String classType = "No Class";
		if (target.classType != null && target.classType.name != null && !target.classType.name.equals(""))
			classType = target.classType.name;
		classType = classType.replace(" Transport", "");
		holder.view4.setText(classType);

		String modeType = Modes.INSTANCE.getDescription(target.modeType_id);
		modeType = modeType.replace("Already o", "O");

		holder.view5.setText(modeType + "/"
				+ Equipments.INSTANCE.getDescription(target.equipmentTypeId));

		String actionNumber = target.actionNumber;
		if (actionNumber != null && actionNumber.startsWith("L"))
			actionNumber = null;
		if (actionNumber == null) {
			actionNumber = "local";
			holder.view6.setTextColor(Color.RED);
		}
		else
			holder.view6.setTextColor(Color.BLACK);
		holder.view6.setText(actionNumber);
		return convertView;
	}

	// private void createSampleData(Targets target) {
	//
	// L.out("modeId: " + target.modeType_id);
	// if (target.modeType_id == null || target.modeType_id.equals("0")
	// || target.modeType_id.equals(Modes.INSTANCE.NOT_APPLICABLE))
	// target.modeType_id = Modes.INSTANCE.getRandomId();
	// L.out("equipmentId: " + target.equipmentTypeId);
	// if (target.equipmentTypeId == null || target.equipmentTypeId.equals("0")
	// || target.equipmentTypeId.equals(Equipments.INSTANCE.NOT_APPLICABLE))
	// target.equipmentTypeId = Equipments.INSTANCE.getRandomId();
	// }

	private void reverseEndPoints(GetActionStatus getActionStatus) {
		String startId = getActionStatus.getStartId();
		L.out("start: " + startId);
		String startName = getActionStatus.getStartName();
		L.out("startName: " + startName);
		L.out("getActionStatus.getDestinationName(): " + getActionStatus.getDestinationName());
		L.out("getActionStatus.getDestinationId(): " + getActionStatus.getDestinationId());
		getActionStatus.setStartId(getActionStatus.getDestinationId());
		getActionStatus.setStartName(getActionStatus.getDestinationName());
		getActionStatus.setDestinationId(startId);
		getActionStatus.setDestinationName(startName);
	}

	@Override
	public void notifyDataSetChanged() {
		L.out("notifyDataSetChanged!");
		Targets[] items = UpdateController.getActionHistory.getTargets();
		L.out("size of list: " + items.length);
		// if (UpdateController.INSTANCE.getActionHistory != null)
		// items = ActionHistoryFragment.getActionList();
		super.notifyDataSetChanged();

	}

}
