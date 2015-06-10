package com.ii.mobile.instantMessage;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flow.types.GetActionStatus;
import com.ii.mobile.flow.types.GetActorStatus;
import com.ii.mobile.flow.types.Logger;
import com.ii.mobile.flow.types.SendMessage;
import com.ii.mobile.flowing.Flow;
import com.ii.mobile.flowing.FlowBinder;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.flowing.SyncCallback;
import com.ii.mobile.flowing.UpdateController;
import com.ii.mobile.fragments.NamedFragment;
import com.ii.mobile.home.LoginActivity;
import com.ii.mobile.home.MyToast;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.tickle.Tickler;
import com.ii.mobile.transport.R; // same package
import com.ii.mobile.users.User;
import com.ii.mobile.util.L;
import com.ii.mobile.util.PrettyPrint;

/**

 */
public class MessageFragment extends Fragment implements NamedFragment, SyncCallback {
	protected EditText chatInputWindow = null;
	protected TextView chatOutputWindow = null;
	protected String currentText = "";
	private View view;
	private String title = "Message";

	private final boolean wantSampleText = false;
	public final static String OPS = "OPS";
	public final static String DISPATCH = "DISPATCH";
	protected static final String CURRENT_TEXT = "currentText";
	protected static final String CURRENT_USER = "currentUser";

	// private boolean running = false;

	@Override
	public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		L.out("InstantMessageFragment: " + bundle);
		// instantMessageFragment = this;
	}

	@Override
	public void onPause() {
		super.onPause();
		// running = false;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	public void onDestroy() {
		onSaveInstanceState(getBundle());
		UpdateController.INSTANCE.unRegisterCallback(this, FlowRestService.GET_ACTOR_STATUS);
		super.onDestroy();
	}

	protected Bundle getBundle() {
		return null;
	}

	public void addDebugLongClick() {

		chatOutputWindow.setOnLongClickListener(new View.OnLongClickListener() {

			@Override
			public boolean onLongClick(View view) {

				Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
				vibrator.vibrate(400);

				GetActorStatus getActorStatus = Flow.getFlow().getActorStatus("foo");
				String tmp = "No Actor Status";
				if (getActorStatus != null) {
					// Flow.getFlow().getActionHistory(getActorStatus);
					getActorStatus.json = null;
					addMessage(PrettyPrint.formatPrint(getActorStatus.getNewJson()), "#00ff00");
					tmp = "Actor Status: "
							+ StaticFlow.INSTANCE.findActorStatusName(getActorStatus.getActorStatusId());
					String actionId = getActorStatus.getActionId();
					if (actionId != null) {
						tmp += "\nAction Status: "
								+ StaticFlow.INSTANCE.findActionStatusName(getActorStatus.getActionStatusId());
						GetActionStatus getActionStatus = Flow.getFlow().getActionStatus(actionId);
						if (getActionStatus != null) {
							tmp += "\nAction actionNumber: " + getActionStatus.getActionNumber();
							// getActionStatus.json = null;
							addMessage(PrettyPrint.formatPrint(getActionStatus.getJson()), "#0000ff");
						} else {
							addMessage("failed to get ActionStatus", "#ff0000");
						}
					}
					MyToast.show(tmp);
				} else {
					addMessage("failed to get ActorStatus", "#ff0000");
				}
				if (getActorStatus != null)
					addMessage(PrettyPrint.formatPrintNormal(getActorStatus.toString()), "#000000");
				return true;
			}
		});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}

		view = inflater.inflate(R.layout.instant_message, container, false);
		chatInputWindow = (EditText) view.findViewById(R.id.chatInputWindow);

		chatInputWindow.addTextChangedListener(new TextValidator() {
		});

		chatOutputWindow = (TextView) view.findViewById(R.id.chatOutputWindow);
		final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollView);

		// scrollView.setFadingEdgeLength(150);
		TextView.OnEditorActionListener inputListener = new TextView.OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView editView, int actionId, KeyEvent event) {
				L.out("rehello: " + editView.getText());
				String temp = editView.getText().toString();
				temp = temp.replace("\"", "");
				sendMessage(temp);
				editView.setText("");
				InputMethodManager in = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				in.hideSoftInputFromWindow(chatInputWindow.getApplicationWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
				scrollView.post(new Runnable() {

					@Override
					public void run() {
						L.out("running");
						scrollView.fullScroll(ScrollView.FOCUS_DOWN);
					}
				});
				return true;
			}
		};
		Activity activity = getActivity();
		SharedPreferences settings = activity.getSharedPreferences(User.PREFERENCE_FILE, 0);
		boolean staffUser = settings.getBoolean(LoginActivity.STAFF_USER, false);
		if (staffUser)
			addDebugLongClick();

		chatInputWindow.setOnEditorActionListener(inputListener);

		SharedPreferences prefs = activity.getSharedPreferences(User.PREFERENCE_FILE, activity.MODE_PRIVATE);
		if (currentText.length() == 0) {
			if (wantSampleText)
				testDisplay();
		}
		else
		{
			String prev = prefs.getString("previous_user", "empty");
			if (prev.equals("empty") || !prev.equals(prefs.getString("current_user", "null")))
				currentText = "";
			chatOutputWindow.setText(Html.fromHtml(currentText));
		}
		scrollView.post(new Runnable() {

			@Override
			public void run() {
				// L.out("running");
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});

		UpdateController.INSTANCE.registerCallback(this, FlowRestService.GET_ACTOR_STATUS);
		return view;
	}

	@Override
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	private void testDisplay() {
		String[] dialog = getDialog();
		for (int i = 0; i < dialog.length; i++) {
			String temp = dialog[i];
			receiveMessage("henry", temp);
		}
		// sendMessage("I NEED A BREAK");
		// receiveMessage("ivan", "I see by your IPS you are on break!");
	}

	public String[] getDialog() {
		return SHORT_DIALOGUE;
	}

	protected void sendMessage(String message) {
		User user = User.getUser();
		String sentDate = L.toDateAMPM(new GregorianCalendar().getTimeInMillis());
		addMessage(user.getUsername(), sentDate, message, true);
		// String to = "kim.fairchild@iicorporate.com";
		String to = DISPATCH;
		SendMessage sendMessage = new SendMessage(user.getUsername(), sentDate, message, to);
		FlowBinder.updateLocalDatabase(FlowRestService.SEND_MESSAGE, sendMessage);
		// Flow.getFlow().sendMessage(message, to);
	}

	public static String convertDate(String input) {
		if (input == null)
			input = "1999-03-11T07:01:00.000Z";
		TimeZone utc = TimeZone.getTimeZone("UTC");
		SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		f.setTimeZone(utc);
		GregorianCalendar gregorianCalendar = new GregorianCalendar(utc);
		try {
			gregorianCalendar.setTime(f.parse(input));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String tmp = "Ivan's Special toast! :)\nFlow Time: " + input
		// + "\nMobile Time: " + gregorianCalendar.getTime()
		// + "\nOutput Time: " +
		// L.toDateAMPM(gregorianCalendar.getTimeInMillis());
		// L.out(tmp);
		// MyToast.show(tmp);
		// MyToast.show(tmp);
		return L.toDateAMPM(gregorianCalendar.getTimeInMillis());
	}

	protected void receiveMessage(String fromUserName, String message) {
		String receivedDate = L.toDateAMPM(new GregorianCalendar().getTimeInMillis());
		addMessage(fromUserName, receivedDate, message, false);
	}

	public static void receivedMessage(Bundle data) {
	}

	protected void addMessage(Bundle bundle) {
		L.out("addMessage: " + bundle);
		String message = bundle.getString(Tickler.TEXT_MESSAGE);

		String receivedDate = bundle.getString(Tickler.RECEIVED_DATE);
		String fromUserName = bundle.getString(Tickler.FROM_USER_NAME);
		receivedDate = convertDate(receivedDate);
		addMessage(fromUserName, receivedDate, message, false);
		if (message.contains("#p"))
			doTagProcessing();
		if (message.contains("#n"))
			doNetworkTagProcessing();
		if (view == null)
			return;
		final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollView);
		scrollView.post(new Runnable() {

			@Override
			public void run() {
				L.out("running");
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
		// AudioPlayer.INSTANCE.playSound(AudioPlayer.NEW_MESSAGE);
	}

	private void doTagProcessing() {
		GetActorStatus getActorStatus = UpdateController.getActorStatus;
		if (getActorStatus != null) {
			Logger logger = Logger.getLogger(getActorStatus);
			clear();
			addMessage("Remote Ping: ", "#00ff00");
			logger.logQueue.clear();
			addMessage(PrettyPrint.formatPrint(logger.getNewJson()),
					"#0000ff");
			FlowBinder.updateLocalDatabase(FlowRestService.SEND_LOGGER, logger);
			Logger.clear();
		}
	}

	private void doNetworkTagProcessing() {
		GetActorStatus getActorStatus = UpdateController.getActorStatus;
		if (getActorStatus != null) {
			Logger logger = Logger.getLogger(getActorStatus);
			clear();
			addMessage("Remote Network Ping: ", "#00ff00");
			addMessage(PrettyPrint.formatPrint(logger.networkStats.getNewJson()),
					"#0000ff");
			sendMessage(logger.networkStats.toString().replace("\n", " "));
			FlowBinder.updateLocalDatabase(FlowRestService.SEND_LOGGER, logger);
			Logger.clear();
		}
	}

	protected void addMessage(String fromUserName, String receivedDate, String message, boolean me) {
		String color = "#2c96f7";
		// String color = "#0000FF";
		if (me)
			color = "#000000";
		currentText += "<br>" + "<font color=" + color + ">" + receivedDate
				+ " " + fromUserName.replace("@iicorporate.com", "")
				+ ": "
				+ message + "</font>";
		if (chatOutputWindow != null)
			chatOutputWindow.setText(Html.fromHtml(currentText));
	}

	protected void clear() {
		currentText = "";
		chatOutputWindow.setText(Html.fromHtml(currentText));
	}

	protected void addMessage(String message, String color) {
		currentText += "<br>" + "<font color=" + color + ">"
				+ message + "</font>";
		if (chatOutputWindow != null)
			chatOutputWindow.setText(Html.fromHtml(currentText));
		final ScrollView scrollView = (ScrollView) view.findViewById(R.id.scrollView);
		scrollView.post(new Runnable() {

			@Override
			public void run() {
				scrollView.fullScroll(ScrollView.FOCUS_DOWN);
			}
		});
	}

	@Override
	public void update() {
		L.out("InstantMessageFragment update");
	}

	public static final String[] SHORT_DIALOGUE =
	{
			"Now is the winter of our discontent",
			"Made glorious summer by this sun of York;",
			"And all the clouds that lour'd upon our house",
			"In the deep bosom of the ocean buried.",
			"Now are our brows bound with victorious wreaths;",
			"Our bruised arms hung up for monuments;",
			"Our stern alarums changed to merry meetings,",
			"Dive, thoughts, down to my soul: here",
			"Clarence comes."
	};

	public static final String[] DIALOGUE =
	{
			"Now is the winter of our discontent",
			"Made glorious summer by this sun of York;",
			"And all the clouds that lour'd upon our house",
			"In the deep bosom of the ocean buried.",
			"Now are our brows bound with victorious wreaths;",
			"Our bruised arms hung up for monuments;",
			"Our stern alarums changed to merry meetings,",
			"Our dreadful marches to delightful measures.",
			"Grim-visaged war hath smooth'd his wrinkled front;",
			"And now, instead of mounting barded steeds",
			"To fright the souls of fearful adversaries,",
			"He capers nimbly in a lady's chamber",
			"To the lascivious pleasing of a lute.",
			"But I, that am not shaped for sportive tricks,",
			"Nor made to court an amorous looking-glass;",
			"I, that am rudely stamp'd, and want love's majesty",
			"To strut before a wanton ambling nymph;",
			"I, that am curtail'd of this fair proportion,",
			"Cheated of feature by dissembling nature,",
			"Deformed, unfinish'd, sent before my time",
			"Into this breathing world, scarce half made up,",
			"And that so lamely and unfashionable",
			"That dogs bark at me as I halt by them;",
			"Why, I, in this weak piping time of peace,",
			"Have no delight to pass away the time,",
			"Unless to spy my shadow in the sun",
			"And descant on mine own deformity:",
			"And therefore, since I cannot prove a lover,",
			"To entertain these fair well-spoken days,",
			"I am determined to prove a villain",
			"And hate the idle pleasures of these days.",
			"Plots have I laid, inductions dangerous,",
			"By drunken prophecies, libels and dreams,",
			"To set my brother Clarence and the king",
			"In deadly hate the one against the other:",
			"And if King Edward be as true and just",
			"As I am subtle, false and treacherous,",
			"This day should Clarence closely be mew'd up,",
			"About a prophecy, which says that 'G'",
			"Of Edward's heirs the murderer shall be.",
			"Dive, thoughts, down to my soul: here",
			"Clarence comes."
	};

	@Override
	public void callback(GJon gJon, String payloadName) {
		L.out("callback: " + payloadName);
		// receiveMessage("I/O", gJon.getNewJson());
	}

	@Override
	public boolean wantActions() {
		return true;
	}

	@Override
	public View getTopLevelView() {
		return null;
	}

	@Override
	public void onSaveInstanceState(Bundle bundle) {
		L.out("onSaveInstanceState: " + currentText + " this: " + this);
		bundle.putString(CURRENT_TEXT, currentText);
		bundle.putString(CURRENT_USER, User.getUser().getUsername());
	}

}
