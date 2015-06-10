package com.ii.mobile.flow.types;

import java.util.LinkedList;
import java.util.Queue;

import com.google.gson.annotations.SerializedName;
import com.ii.mobile.soap.gson.GJon;

public class Logger extends GJon {

	private static int MAX_LOG = 100;

	@SerializedName("networkStats")
	public NetworkStats networkStats = new NetworkStats();

	@SerializedName("GetActorStatus")
	public GetActorStatus getActorStatus = new GetActorStatus();
	@SerializedName("logQueue")
	public Queue<String> logQueue = new LinkedList<String>();

	private static Logger logger = null;

	public static Logger getLogger() {
		if (logger == null)
			logger = new Logger();
		return logger;
	}

	public static Logger getLogger(GetActorStatus getActorStatus) {
		getLogger().getActorStatus = getActorStatus;
		getLogger().getActorStatus.tickled = GJon.FALSE_STRING;
		// queue.add("hello");
		return getLogger();
	}

	public Logger() {
		tickled = GJon.FALSE_STRING;
	}

	@Override
	public String toString() {
		if (getActorStatus == null)
			return "ERROR: getActorStatus is null";
		return getActorStatus.toString();
	}

	public static void out(String message) {
		Logger logger = getLogger();
		logger.logQueue.add(message);
		if (logger.logQueue.size() > MAX_LOG)
			logger.logQueue.remove();
	}

	public static void clear() {
		Logger logger = getLogger();
		logger.logQueue.clear();
		logger.networkStats.clear();
	}

	public class NetworkStats extends GJon {
		public int totalTickles = 0;
		public int failTickles = 0;

		public int totalUpdates = 0;
		public int failUpdates = 0;
		@SerializedName("networkChanges")
		public Queue<String> networkChanges = new LinkedList<String>();

		public void addFailTickle() {
			failTickles += 1;
		}

		public void addTotalTickle() {
			totalTickles += 1;
		}

		public void addFailUpdate() {
			failUpdates += 1;
		}

		public void addTotalUpdate() {
			totalUpdates += 1;
		}

		public void addNetworkState(String state) {
			networkChanges.add(state);
		}

		public void clear() {
			totalTickles = 0;
			failTickles = 0;
			totalUpdates = 0;
			failUpdates = 0;
			networkChanges = new LinkedList<String>();
		}

		@Override
		public String toString() {
			return "Fail Tickles: " + failTickles + "/" + totalTickles + " " + ((float) failTickles)
					/ totalTickles + "%"
					+ "Fail Updates: " + failUpdates + "/" + totalUpdates + " " + ((float) failUpdates)
					/ totalUpdates + "%"
					+ "Network Changes: " + networkChanges.size() + "\n";
		}
	}

}
