package com.ii.mobile.flowing;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.ii.mobile.flow.db.FlowDbAdapter;
import com.ii.mobile.home.TransportActivity;
import com.ii.mobile.tab.SelfTaskActivity;

public class StaticChangedDialog {

	private final String title = "Class definition data has changed";

	public void show(final TransportActivity loginActivity, String error) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				loginActivity);

		// set title
		alertDialogBuilder.setTitle(title);

		// set dialog message
		alertDialogBuilder
				.setMessage(error.replace(":", ":\n") + "\nPlease recreate the task\nClick yes to relogin")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						FlowDbAdapter flowDbAdapter = FlowDbAdapter.getFlowDbAdapter();
						if (flowDbAdapter == null)
							return;
						flowDbAdapter.deleteAll();
						SelfTaskActivity.initDataCache();
						UpdateController.clearStaticLoad();
						loginActivity.finish();
						// MyToast.show("Cleared local data\nWill reload on login");
					}
				});
		// .setNegativeButton("No", new DialogInterface.OnClickListener() {
		// @Override
		// public void onClick(DialogInterface dialog, int id) {
		// // if this button is clicked, just close
		// // the dialog box and do nothing
		// dialog.cancel();
		// MyToast.show("Aborted reload data");
		// }
		// }

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}
}
