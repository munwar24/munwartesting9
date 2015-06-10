package com.ii.mobile.home;

import android.app.AlertDialog;
import android.content.DialogInterface;

import com.ii.mobile.flow.db.FlowDbAdapter;

public class ClearData {

	private final String title = "Remove all local data";

	public void show(LoginActivity context) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);

		// set title
		alertDialogBuilder.setTitle(title);

		// set dialog message
		alertDialogBuilder
				.setMessage("Click yes to reload")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						FlowDbAdapter flowDbAdapter = FlowDbAdapter.getFlowDbAdapter();
						if (flowDbAdapter == null)
							return;
						flowDbAdapter.deleteAll();
						MyToast.show("Cleared local data\nWill reload on login");
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int id) {
						// if this button is clicked, just close
						// the dialog box and do nothing
						dialog.cancel();
						MyToast.show("Aborted reload data");
					}
				});

		// create alert dialog
		AlertDialog alertDialog = alertDialogBuilder.create();

		// show it
		alertDialog.show();
	}

}
