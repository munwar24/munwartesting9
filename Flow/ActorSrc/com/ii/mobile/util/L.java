/*
 * 
 */
package com.ii.mobile.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;
import java.util.concurrent.TimeUnit;

import android.os.Environment;
import android.util.Log;

import com.ii.mobile.flow.types.Logger;

/**
 * 
 * @author kfairchild
 */
public class L {

	public static void setTag(Object object) {
		getSimpleName(object);
	}

	public static String getSimpleName(Object object) {
		String temp = object.getClass().getName();
		int index = temp.lastIndexOf(".");
		// System.out.println("getSimpleName: " + temp + " " + index);
		String foo = temp.substring(index + 1, temp.length());
		// System.out.println("getSimpleName result: " + foo);
		return foo;
	}

	/**
	 * Convert a String to a long. If have an error return 0.
	 * 
	 * @param string
	 * @return
	 */
	public static long getLong(String string) {
		try {
			long l = Long.parseLong(string.trim());
			// System.out.println("long l = " + l);
			return l;
		} catch (Exception e) {
			System.out
					.println("L.getLong NumberFormatException: " + e + string);
			System.out.println("String was: #" + string + "#");
		}
		return 0;
	}

	/**
	 * Convert a String to a long. If have an error return 0.
	 * 
	 * @param string
	 * @return
	 */
	public static double getDouble(String string) {
		try {
			double l = Double.parseDouble(string.trim());
			// System.out.println("long l = " + l);
			return l;
		} catch (Exception e) {
			System.out.println("L.getdouble NumberFormatException: " + e
					+ string);
			System.out.println("String was: #" + string + "#");
		}
		return 0;
	}

	public static void out(String string) {
		Logger.out(string);
		String temp = pop();
		Log.e(temp, string);
		debugOutput(string);
	}

	public static void outp(String string) {
		String temp = pop(3);
		Log.e(temp, string);
		debugOutput(string);
	}

	public static void outpp(String string) {
		String temp = pop(4);
		Log.e(temp, string);
		debugOutput(string);
	}

	public static void out(String string, boolean pop) {
		Log.e(pop(3), string);
	}

	/*
	 * The default method, just return the last "depth" messages.
	 */

	public static String p() {
		// System.out.println("L.p() executing");

		int depth = 10;
		return p(depth, pop());
	}

	/**
	 * Return the string containing the last method calls in a format giving the
	 * class method and location in the file. Depth gives the depth in the stack
	 * to print. Note: With Sip-Communicator, the depth could be 20 but a depth
	 * of 3-5 is more useful.
	 * 
	 * @param depth
	 * @return
	 */
	@SuppressWarnings("unused")
	public static String p(int depth, String tag) {
		String temp = "";
		// int depth = 5;
		Exception e = new Exception();
		StackTraceElement elements[] = e.getStackTrace();
		// ystem.out.println("p: "+elements.length);
		for (int i = 2, n = Math.min(elements.length, depth); i < n; i++) {
			String tmp = elements[i].getFileName() + ":"
					+ elements[i].getLineNumber() + " == >  "
					+ elements[i].getMethodName() + " (  ) ";
			Log.e(tag, tmp);
			// System.err.println(
			temp += "\n" + i + " " + elements[i].getFileName() + ":"
					+ elements[i].getLineNumber() + " "
					+ elements[i].getMethodName() + "()";
		}
		if (true) {
			return "";
		}
		return temp;
	}

	public static String pop() {
		String temp = "";
		Exception e = new Exception();
		StackTraceElement elements[] = e.getStackTrace();
		if (elements.length > 1) {
			int i = 2;
			temp += getSimpleClassName(elements[i].getFileName()) + "."
					+ elements[i].getMethodName() + ":"
					+ elements[i].getLineNumber();
		}
		return temp;
	}

	public static String pop(int level) {
		String temp = "";
		Exception e = new Exception();
		StackTraceElement elements[] = e.getStackTrace();
		if (elements.length > 1) {
			int i = level;
			temp += getSimpleClassName(elements[i].getFileName()) + "."
					+ elements[i].getMethodName() + ":"
					+ elements[i].getLineNumber();
		}
		return temp;
	}

	private static String getSimpleClassName(String fileName) {
		if (fileName != null) {
			return fileName.replace(".java", "");
		}
		return "<none>";
	}

	public static String parseDate(String string) {
		try {
			// L.out("string: " + string);
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = formatter.parse(string);
			SimpleDateFormat sdf = new SimpleDateFormat("MM/dd");
			return sdf.format(date.getTime());
		} catch (ParseException e) {
			System.out.println("Exception :" + e);
		}
		// return "Bad Date: " + string;
		return "______";
	}

	public static boolean isLate(String string) {
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = formatter.parse(string);
			GregorianCalendar now = new GregorianCalendar();
			GregorianCalendar then = new GregorianCalendar();
			then.setTime(date);
			// L.out("now: " + now.getTimeInMillis()+" "+now);
			// L.out("then: " + then.getTimeInMillis()+" "+then);
			if (now.compareTo(then) < 0) {
				return false;
			}
			return true;

		} catch (ParseException e) {
			L.out("e: " + e);
			// System.out.println("Exception :" + e);
			return false;
		}
	}

	public static GregorianCalendar startTimer() {
		return new GregorianCalendar();

	}

	public static String stopTimer(GregorianCalendar started) {
		GregorianCalendar now = new GregorianCalendar();
		long diff = now.getTimeInMillis() - started.getTimeInMillis();
		// L.out("internal: " + now.getTimeInMillis() + " " +
		// started.getTimeInMillis());
		return (diff) + "";
	}

	public static long getTimeInMillis(String string) {
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = formatter.parse(string);
			GregorianCalendar then = new GregorianCalendar();
			then.setTime(date);
			return then.getTimeInMillis();
		} catch (ParseException e) {
			L.out("e: " + e);
			// System.out.println("Exception :" + e);
			return 0;
		}
	}

	public static boolean isToday(String string) {
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = formatter.parse(string);
			GregorianCalendar now = new GregorianCalendar();
			now.clear(GregorianCalendar.HOUR_OF_DAY);
			now.clear(GregorianCalendar.MINUTE);
			now.clear(GregorianCalendar.SECOND);
			GregorianCalendar then = new GregorianCalendar();
			then.setTime(date);
			// L.out("now: " + now.getTimeInMillis()+" "+now);
			// L.out("then: " + then.getTimeInMillis()+" "+then);
			if (now.compareTo(then) == 0) {
				return true;
			}
			return false;

		} catch (ParseException e) {
			L.out("e: " + e);
			// System.out.println("Exception :" + e);
			return false;
		}
	}

	public static boolean isTomorrow(String string) {
		try {
			DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			Date date = formatter.parse(string);
			GregorianCalendar now = new GregorianCalendar();
			now.clear(GregorianCalendar.HOUR_OF_DAY);
			now.clear(GregorianCalendar.MINUTE);
			now.clear(GregorianCalendar.SECOND);
			now.add(GregorianCalendar.DAY_OF_MONTH, 1);
			GregorianCalendar then = new GregorianCalendar();
			then.setTime(date);
			// L.out("now: " + now.getTimeInMillis()+" "+now);
			// L.out("then: " + then.getTimeInMillis()+" "+then);
			if (now.compareTo(then) == 0) {
				return true;
			}
			return false;

		} catch (ParseException e) {
			L.out("e: " + e);
			// System.out.println("Exception :" + e);
			return false;
		}
	}

	static public String customFormat(String value) {
		// System.out.println("customFormat: "+value);
		return customFormat("#.###", getDouble(value));
	}

	static public String customFormat(double value) {
		return customFormat("#.###", value);
	}

	static public String customFormat(String pattern, double value) {
		DecimalFormat myFormatter = new DecimalFormat(pattern);
		String output = myFormatter.format(value);
		// System.out.println(value + "  " + pattern + "  " + output);
		return output;
	}

	static public void sleep(int count) {
		try {
			Thread.sleep(count);
		} catch (InterruptedException ex) {
		}
	}

	public static String getPlural(int size, String string) {
		if (size == 1) {
			return size + " " + string;
		}
		return size + " " + string + "s";
	}

	/**
	 * Convert the time into a string date form.
	 * 
	 * @param time
	 * @return
	 */
	public static String toDate(GregorianCalendar calendar) {
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm zzz yyyy");
		sdf.setTimeZone(calendar.getTimeZone());
		// System.out.println("L.toDate: "+sdf.format(calendar.getTime()));
		return sdf.format(calendar.getTime());
		// return sdf.format(new Date(calendar.getTimeInMillis())) + " " +
		// calendar.getTimeZone().getID();
	}

	/**
	 * Convert the time into a string date form.
	 * 
	 * @param time
	 * @return
	 */
	public static String toDate(long time) {
		// System.out.println("L.toDate obsolete: "+L.p(10));
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		return toDate(calendar);
	}

	public static String toDateDay(Long time) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd");
		sdf.setTimeZone(calendar.getTimeZone());
		// System.out.println("L.toDate: "+sdf.format(calendar.getTime()));
		return sdf.format(calendar.getTime());
	}

	public static String toDateHour(Long time) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
		sdf.setTimeZone(calendar.getTimeZone());
		// System.out.println("L.toDate: "+sdf.format(calendar.getTime()));
		return sdf.format(calendar.getTime());
	}

	public static String toDateSecond(Long time) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		sdf.setTimeZone(calendar.getTimeZone());
		// System.out.println("L.toDate: "+sdf.format(calendar.getTime()));
		return sdf.format(calendar.getTime());
	}

	public static String toDateAMPM(Long time) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
		sdf.setTimeZone(calendar.getTimeZone());
		// System.out.println("L.toDate: "+sdf.format(calendar.getTime()));
		return sdf.format(calendar.getTime());
	}

	public static String toDateSecondAMPM(Long time) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss aa");
		sdf.setTimeZone(calendar.getTimeZone());
		// System.out.println("L.toDate: "+sdf.format(calendar.getTime()));
		return sdf.format(calendar.getTime());
	}

	public static String toDateDayHour(long time) {
		GregorianCalendar calendar = new GregorianCalendar();
		calendar.setTimeInMillis(time);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd HH:mm");
		sdf.setTimeZone(calendar.getTimeZone());
		// System.out.println("L.toDate: "+sdf.format(calendar.getTime()));
		return sdf.format(calendar.getTime());
	}

	public static String prettyCapitalization(String string) {
		StringTokenizer st = new StringTokenizer(string);
		String temp = "";
		while (st.hasMoreTokens()) {
			String token = st.nextToken().toLowerCase();
			char capLetter = Character.toUpperCase(token.charAt(0));
			if (token.length() > 1) {
				token = capLetter + token.substring(1, token.length());
			} else {
				token = capLetter + "";
			}
			temp += token + " ";
		}
		return temp.trim();
	}

	public static String getElapsedTime(Long l) {
		final long hr = TimeUnit.MILLISECONDS.toHours(l);
		final long min = TimeUnit.MILLISECONDS.toMinutes(l - TimeUnit.HOURS.toMillis(hr));
		final long sec = TimeUnit.MILLISECONDS.toSeconds(l - TimeUnit.HOURS.toMillis(hr)
				- TimeUnit.MINUTES.toMillis(min));
		// final long ms = TimeUnit.MILLISECONDS.toMillis(l -
		// TimeUnit.HOURS.toMillis(hr)
		// - TimeUnit.MINUTES.toMillis(min) - TimeUnit.SECONDS.toMillis(sec));
		return String.format("%02d:%02d:%02d", hr, min, sec);

	}

	private static File createTemporaryFile(String part, boolean init) throws Exception {

		File parent = Environment.getExternalStorageDirectory();
		// String directory = (String) getText(R.string.mobile_directory);
		String directory = "/mobileDirectory/";

		// L.out("Environment.getExternalStorageState(): " +
		// Environment.getExternalStorageState());
		File tempDir = new File(parent.getAbsolutePath() + directory);
		// L.out("tempDir: " + tempDir + " exists: " + tempDir.exists());
		if (!tempDir.exists()) {
			boolean foo = tempDir.mkdirs();
			// out("created directories: " + tempDir + " " + foo);
		}
		File file = new File(parent.getAbsolutePath() + directory + part);
		if (init) {
			file.delete();
			file = new File(parent.getAbsolutePath() + directory + part);
			// out("Reinitted: " + parent.getAbsolutePath() + directory + part);
		}
		return file;
	}

	protected static final String OUTPUT_FILE = "out.txt";
	protected static boolean wantDebug = true;

	public static void setDebug(boolean flag) {
		wantDebug = flag;
		if (wantDebug) {
			try {
				createTemporaryFile(OUTPUT_FILE, true);
			} catch (Exception e) {
				out("Failed to delete: " + OUTPUT_FILE);
				e.printStackTrace();
			}
		}
	}

	public static void debugOutput(boolean init, String outputFile) {
		try {
			File file = createTemporaryFile(outputFile, false);
			L.out("file: " + file, true);
			file.delete();
		} catch (Exception e) {
			out("Failed to delete: " + outputFile);
			e.printStackTrace();
		}

	}

	public static void debugOutput(String string) {
		if (!wantDebug)
			return;
		try {
			// // Create file
			// File file = createTemporaryFile("out.txt");
			// L.out("path: " + file.getAbsolutePath());
			// L.out("path: " + file.getPath());
			// L.out("debugOutput: " + json.length());

			FileWriter fstream = new FileWriter(createTemporaryFile(OUTPUT_FILE, false), true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(pop(3) + ": " + string + "\n");

			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}

	public static void debugOutput(String json, String outputFile) {
		try {
			// // Create file
			// File file = createTemporaryFile("out.txt");
			// L.out("path: " + file.getAbsolutePath());
			// L.out("path: " + file.getPath());
			// L.out("debugOutput: " + json.length());

			FileWriter fstream = new FileWriter(createTemporaryFile(outputFile, false), true);
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(json + "\n");

			// Close the output stream
			out.close();
		} catch (Exception e) {// Catch exception if any
			System.err.println("Error: " + e.getMessage());
		}
	}
}
