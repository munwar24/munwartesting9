package com.ii.mobile.flow.types;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.soap.BaseSoap;
import com.ii.mobile.soap.gson.GJon;
import com.ii.mobile.util.L;

public class GetActorStatus extends GJon {

	public static String TRANSPORT_FACILITY = "510915224a7de6e90338c73b";

	@SerializedName("GetActorStatusInner")
	public GetActorStatusInner getActorStatusInner = new GetActorStatusInner();

	public class GetActorStatusInner extends KeepNames {
		@SerializedName("targets")
		public Targets targets = new Targets();
		@SerializedName("target")
		public String target;
		@SerializedName("name")
		public String name;
		@SerializedName("_id")
		public String _id = null;

		@Override
		public String toString() {
			return " name: " + name
					+ "\n _id: " + _id
					+ "\n target: " + target
					+ "\n targets: " + targets;
		}

		public class Targets extends KeepNames {
			@SerializedName("actor_name")
			public String actor_name;
			@SerializedName("actorStatusType_id")
			public String actorStatusType_id;
			@SerializedName("actionStatusType_id")
			public String actionStatusType_id;
			@SerializedName("actor_id")
			public String actor_id;
			@SerializedName("action_id")
			public String action_id;
			@SerializedName("actionNumber")
			String actionNumber;
			@SerializedName("facilityHirNode_id")
			public String facilityId;
			@SerializedName("im")
			public InstantMessage[] instantMessages;
			@SerializedName("actorStatus")
			public String actorStatus = null;
			@SerializedName("actionStatus")
			public String actionStatus = null;
			public String mobilePIN = null;
			@SerializedName("functionalAreaType_id")
			public String functionalAreaTypeId = null;

			// @SerializedName("im")
			// public List<InstantMessage> instantMessages = new
			// ArrayList<InstantMessage>();

			@Override
			public String toString() {
				String ims = "";
				if (instantMessages == null)
					instantMessages = new InstantMessage[0];
				for (InstantMessage instantMessage : instantMessages)
					ims += instantMessage.toString();
				return "actor_name: " + actor_name
						+ "\n actor_id: " + actor_id
						+ " action_id: " + action_id
						+ "\n actorStatus: " + actorStatus
						+ " actorStatusType_id: " + actorStatusType_id
						+ "\n actionStatus: " + actionStatus
						+ " actionStatusType_id: " + actionStatusType_id
						+ "\n facilityId: " + facilityId
						+ "\n actionNumber: " + actionNumber
						+ "\n functionalAreaTypeId: " + functionalAreaTypeId
						+ " mobilePIN: " + mobilePIN
						+ ims;
			}
		}

	}

	public class InstantMessage extends KeepNames {
		@Override
		public String toString() {
			if (message == null)
				return "";
			return "  Message\n"
					+ n("Message", message, 2)
					+ n("send.to", send.to, 2)
					+ n("send.by", send.by, 2)
					+ n("sourceType", sourceType, 2)
					+ n("mod", mod.at, 2);
		}

		@SerializedName("sourceType")
		public String sourceType = null;

		@SerializedName("message")
		public String message = null;

		@SerializedName("send")
		public Send send = new Send();
		@SerializedName("mod")
		public Mod mod = new Mod();
	}

	public static String n(String string, String value, int space) {
		return space(space) + string + ": " + value + "\n";
	}

	public static String space(int space) {
		String temp = "";
		for (int i = 0; i < space; i++) {
			temp += "    ";
		}
		return temp;
	}

	public class Send extends KeepNames {

		@Override
		public String toString() {

			return "  Send\n"
					+ n("to", to, 2)

					+ n("by", by, 2);
		}

		@SerializedName("to")
		public String to = null;

		@SerializedName("by")
		public String by = null;

	}

	public class Mod extends KeepNames {
		@Override
		public String toString() {

			return "  Send\n"
					+ n("at", at, 2)
					+ n("by", by, 2);
		}

		@SerializedName("at")
		public String at = null;

		@SerializedName("by")
		public String by = null;
	}

	@Override
	public boolean validate() {
		if (getActorStatusInner != null
				&& getActorStatusInner.targets != null)
			validated = true;
		else
			L.out("Unable to validate GetActorStatus");
		return validated;
	}

	@Override
	public String toString() {
		if (getActorStatusInner == null)
			return "ERROR: getflowStatusInner is null";
		return getActorStatusInner.toString();
	}

	@Override
	public boolean isValidated() {

		if (!validated) {
			// hack since returns a bad jSon when don't have task (in loop)
			if (getActorStatusInner.targets == null || getActorStatusInner.targets.actor_id == null)
				L.out(this.getClass() + " was not properly initiated: \n" + json);
			printJson();
		}
		return validated;
	}

	static public GetActorStatus getGJon(String json) {
		// L.out("GetActorStatus: ");
		// PrettyPrint.prettyPrint(json, true);
		GetActorStatus getActorStatus = (GetActorStatus) getJSonObjectArray(json, GetActorStatus.class);
		// if (getActorStatus != null)
		// L.out("output: " + getActorStatus.getNewJson());
		// L.out("output: " + getActorStatus);
		return getActorStatus;
	}

	static protected GJon getJSonObjectArray(String json, Class<?> className) {
		BaseSoap.debugOutput("\n\n*** " + className + " ***\n");
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
		gsonBuilder.registerTypeAdapter(InstantMessage[].class, new InstantMessageDeserializer());
		Gson gson = gsonBuilder.create();
		JsonParser parser = new JsonParser();
		GJon gJon = null;
		try {
			gJon = (GJon) gson.fromJson(parser.parse(json).getAsJsonObject().toString(), className);
		} catch (Exception e) {

			L.out("*** ERROR Failed: " + e + "\njson: " + json + " " + className);
		}
		if (gJon == null) {
			BaseSoap.debugOutput("Failed to parse json for: " + className);
			return null;
		}
		gJon.json = json;
		if (gJon.validate()) {
			// need to uncomment to print to console
			BaseSoap.debugOutput(gJon.toString());
		}
		return gJon;
	}

	public String getActorId() {
		return getActorStatusInner.targets.actor_id;
	}

	public String getActorName() {
		return getActorStatusInner.targets.actor_name;
	}

	public String getActorStatusId() {
		return getActorStatusInner.targets.actorStatusType_id;
	}

	public String getActionId() {
		return getActorStatusInner.targets.action_id;
	}

	public String getActionStatusId() {
		return getActorStatusInner.targets.actionStatusType_id;
	}

	public String getFunctionalAreaTypeId() {
		return getActorStatusInner.targets.functionalAreaTypeId;
	}

	public String getFacilityId() {
		if (getActorStatusInner == null
				|| getActorStatusInner.targets == null)
			return null;
		return getActorStatusInner.targets.facilityId;
	}

	public String getActorStatus() {
		return getActorStatusInner.targets.actorStatus;
	}

	public List<InstantMessage> getInstantmessages() {
		List<InstantMessage> temp = new ArrayList<InstantMessage>();
		for (int i = 0; i < getActorStatusInner.targets.instantMessages.length; i++) {
			if (getActorStatusInner.targets.instantMessages[i] != null
					&& getActorStatusInner.targets.instantMessages[i].message != null)
				temp.add(getActorStatusInner.targets.instantMessages[i]);
		}
		return temp;
	}

	public void setActorStatusId(String actorStatusId) {
		getActorStatusInner.targets.actorStatusType_id = actorStatusId;
		getActorStatusInner.targets.actorStatus = StaticFlow.INSTANCE.findActorStatusName(actorStatusId);
	}

	public void setActionStatusId(String actionStatusId) {
		getActorStatusInner.targets.actionStatusType_id = actionStatusId;
		getActorStatusInner.targets.actionStatus = StaticFlow.INSTANCE.findActionStatusName(actionStatusId);
	}

	public void setPIN(String mobilePIN) {
		getActorStatusInner.targets.mobilePIN = mobilePIN;

	}

	public void setActionId(String actionId) {
		getActorStatusInner.targets.action_id = actionId;
	}

	@Override
	public GetActorStatus clone() {
		GetActorStatus getActorStatus = new GetActorStatus();
		getActorStatus.setActorStatusId(getActorStatusInner.targets.actorStatusType_id);
		getActorStatus.setActionStatusId(getActorStatusInner.targets.actionStatusType_id);
		return getActorStatus;
	}

	public boolean isDifferent(GetActorStatus getActorStatus) {
		if (getActorStatus == null
				|| !getActorStatus.getActorStatusId().equals(getActorStatusId())
				|| (getActorStatus.getActionStatusId() == null)
				^ (getActionStatusId() == null)
				|| !(getActorStatus.getActionStatusId() != null
				&& getActorStatus.getActionStatusId().equals(getActionStatusId())))
			return true;
		return false;
	}
}
