package com.ii.mobile.flow.types;

import java.util.GregorianCalendar;

import com.google.gson.annotations.SerializedName;
import com.ii.mobile.flow.staticFlow.CancelReasons;
import com.ii.mobile.flow.staticFlow.StaticDelayTypes;
import com.ii.mobile.flow.staticFlow.StaticFlow;
import com.ii.mobile.flowing.FlowRestService;
import com.ii.mobile.util.L;

public class Event extends KeepNames {
	@SerializedName("actionStatusId")
	public String actionStatusId = null;
	@SerializedName("optionId")
	public String optionId = null;
	@SerializedName("time")
	public String time = "";

	public Event(String actionStatusId, String optionId) {
		this.actionStatusId = actionStatusId;
		this.optionId = optionId;
		GregorianCalendar gregorianCalendar = new GregorianCalendar();
		time = gregorianCalendar.getTimeInMillis() + "";
	}

	public String getOptionId() {
		L.out("optionId: " + optionId);
		L.out("actionStatusId: " + actionStatusId);

		if (actionStatusId.equals(StaticFlow.ACTION_CANCELLED))
			return FlowRestService.getItem("id", optionId, true);
		// L.out("cancelType: " + cancelType);
		if (actionStatusId.equals(StaticFlow.ACTION_DELAYED))
			return FlowRestService.getItem("id", optionId, true);
		return "";
	}

	private String getHeader() {
		if (actionStatusId.equals(StaticFlow.ACTION_CANCELLED))
			return "cancelType";
		// L.out("cancelType: " + cancelType);
		if (actionStatusId.equals(StaticFlow.ACTION_DELAYED))
			return "delayType";
		return "";
	}

	public String getOptionName() {
		if (actionStatusId == null)
			return "No Status!";
		L.out("optionId: " + optionId);
		if (optionId == null)
			return "";
		L.out("actionStatusId: " + actionStatusId);
		if (actionStatusId.equals(StaticFlow.ACTION_CANCELLED))
			return FlowRestService.getItem("name", CancelReasons.INSTANCE.getName(optionId), false);
		// L.out("cancelType: " + cancelType);
		if (actionStatusId.equals(StaticFlow.ACTION_DELAYED))
			return FlowRestService.getItem("name", StaticDelayTypes.INSTANCE.findDelayName(optionId), false);
		return "";
	}

	public String getOptionSimpleName() {
		L.out("optionId: " + optionId);
		L.out("actionStatusId: " + actionStatusId);
		if (optionId == null)
			return null;

		if (actionStatusId.equals(StaticFlow.ACTION_CANCELLED))
			return "cancelType_id: " + CancelReasons.INSTANCE.getName(optionId);
		if (actionStatusId.equals(StaticFlow.ACTION_DELAYED))
			return "delayType_id: " + StaticDelayTypes.INSTANCE.findDelayName(optionId);
		return null;
	}

	public String getDelayType() {
		if (optionId == null)
			return "";
		String delayId = getOptionId();
		String delayName = getOptionName();
		return "\"" + getHeader() + "\":{" + getOptionId() + getOptionName() + "},";
	}
}