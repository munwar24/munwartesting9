package com.ii.mobile.instantMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flow.types.Logger;
import com.ii.mobile.flow.types.SendMessage;
import com.ii.mobile.flowing.Flow;
import com.ii.mobile.flowing.FlowBinder;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;
import com.ii.mobile.util.PrettyPrint;

/**

 */
public class InstantMessageFragment extends MessageFragment {
	private final String title = "Instant Message";

	private final boolean wantSampleText = false;
	public final static String OPS = "OPS";
	public final static String DISPATCH = "DISPATCH";
	public static MessageFragment messageFragment = null;
	private static List<Bundle> bundles = new ArrayList<Bundle>();

	// private boolean running = false;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		L.out("InstantMessageFragment: " + bundle);
		// instantMessageFragment = this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		messageFragment = this;
		View view = super.onCreateView(inflater, container, savedInstanceState);
		L.out("savedInstanceState: " + savedInstanceState);
		L.out("restoreBundle: " + restoreBundle);
		addLoggerLongClick();
		onRestoreInstanceState(restoreBundle);
		return view;
	}

	@Override
	public void onPause() {
		super.onPause();
		// running = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		// running = true;
		if (getActivity() == null) {
			L.out("Unable to get activity for actionPager!");
			return;
		}
		for (Bundle bundle : bundles)
			receivedMessage(bundle);
		bundles.clear();
		// View view = getActivity().findViewById(R.id.actionPager);
		// if (view == null) {
		// L.out("Unable to get view for actionPager!");
		// return;
		// }
		// view.setVisibility(View.GONE);
	}

	@Override
	public void onDestroy() {
		messageFragment = null;
		super.onDestroy();
	}

	protected void onRestoreInstanceState(Bundle bundle) {
		L.out("onRestoreInstanceState: " + bundle);
		if (bundle != null) {
			String userName = bundle.getString(CURRENT_USER);
			L.out("userName: " + userName + " " + User.getUser().getUsername().equals(userName));
			if (bundle.getString(CURRENT_TEXT) != null)
				currentText = bundle.getString(CURRENT_TEXT);
			L.out("currentText: " + currentText);
			if (currentText == null || !User.getUser().getUsername().equals(userName))
				currentText = "";
			chatOutputWindow.setText(Html.fromHtml(currentText));
		}
	}

	@Override
	public void addDebugLongClick() {

		chatOutputWindow.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {

				Vibrator vibrator = (Vibrator) getActivity().getSystemService(
						Context.VIBRATOR_SERVICE);
				vibrator.vibrate(400);

				GetActorStatus getActorStatus = Flow.getFlow().getActorStatus(
						"foo");
				String tmp = "No Actor Status";
				if (getActorStatus != null) {
					// Flow.getFlow().getActionHistory(getActorStatus);
					getActorStatus.json = null;
					addMessage(PrettyPrint.formatPrint(getActorStatus
							.getNewJson()), "#00ff00");
					tmp = "Actor Status: "
							+ StaticFlow.INSTANCE
									.findActorStatusName(getActorStatus
											.getActorStatusId());
					String actionId = getActorStatus.getActionId();
					if (actionId != null) {
						tmp += "\nAction Status: "
								+ StaticFlow.INSTANCE
										.findActionStatusName(getActorStatus
												.getActionStatusId());
						GetActionStatus getActionStatus = Flow.getFlow()
								.getActionStatus(actionId);
						if (getActionStatus != null) {
							tmp += "\nAction actionNumber: "
									+ getActionStatus.getActionNumber();
							// getActionStatus.json = null;
							addMessage(PrettyPrint.formatPrint(getActionStatus
									.getJson()), "#0000ff");
						} else {
							addMessage("failed to get ActionStatus", "#ff0000");
						}
					}
					MyToast.show(tmp);
				} else {
					addMessage("failed to get ActorStatus", "#ff0000");
				}
				if (getActorStatus != null)
					addMessage(PrettyPrint.formatPrintNormal(getActorStatus
							.toString()), "#000000");
				return true;
			}
		});
	}

	public void addLoggerLongClick() {

		chatInputWindow.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {

				Vibrator vibrator = (Vibrator) getActivity().getSystemService(
						Context.VIBRATOR_SERVICE);
				vibrator.vibrate(400);

				GetActorStatus getActorStatus = UpdateController.getActorStatus;
				if (getActorStatus != null) {
					Logger logger = Logger.getLogger(getActorStatus);

					clear();
					logger.logQueue.clear();
					addMessage(PrettyPrint.formatPrint(logger.getNewJson()),
							"#0000ff");
					FlowBinder.updateLocalDatabase(FlowRestService.SEND_LOGGER, logger);
					Logger.clear();
				}
				return true;
			}
		});
	}

	@Override
	public String[] getDialog() {
		return SHORT_DIALOGUE;
	}

	@Override
	protected void sendMessage(String message) {
		User user = User.getUser();
		String sentDate = L.toDateAMPM(new GregorianCalendar()
				.getTimeInMillis());
		addMessage(user.getUsername(), sentDate, message, true);
		// String to = "kim.fairchild@iicorporate.com";
		String to = DISPATCH;
		SendMessage sendMessage = new SendMessage(user.getUsername(), sentDate,
				message, to);
		FlowBinder.updateLocalDatabase(FlowRestService.SEND_MESSAGE,
				sendMessage);
		// Flow.getFlow().sendMessage(message, to);
	}

	public static String convertDate(String input) {
		if (input == null)
			input = "1999-03-11T07:01:00.000Z";
		TimeZone utc = TimeZone.getTimeZone("UTC");
		SimpleDateFormat f = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		f.setTimeZone(utc);
		GregorianCalendar gregorianCalendar = new GregorianCalendar(utc);
		try {
			gregorianCalendar.setTime(f.parse(input));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String tmp = "Ivan's Special toast! :)\nFlow Time: " + input
				+ "\nMobile Time: " + gregorianCalendar.getTime()
				+ "\nOutput Time: "
				+ L.toDateAMPM(gregorianCalendar.getTimeInMillis());
		L.out(tmp);
		// MyToast.show(tmp);
		// MyToast.show(tmp);
		return L.toDateAMPM(gregorianCalendar.getTimeInMillis());
	}

	@Override
	public void callback(GJon gJon, String payloadName) {
		L.out("callback: " + payloadName);
		// receiveMessage("I/O", gJon.getNewJson());
	}

	private static Bundle restoreBundle = new Bundle();

	@Override
	protected Bundle getBundle() {
		return restoreBundle;
	}

	public static void receivedMessage(Bundle bundle) {
		L.out("received message: " + bundle);

		if (messageFragment == null) {
			bundles.add(bundle);
			L.out("*** ERROR No instantMessageFragment to receive message: " + bundles);
			return;
		}
		messageFragment.addMessage(bundle);
	}
}
