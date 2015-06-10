package com.ii.mobile.soap.gson;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.ii.mobile.soap.gson.GetEmployeeAndTaskStatusByEmployeeID.MobileMessage;

public class MobileMessageDeserializer implements JsonDeserializer<MobileMessage[]>
{
	public MobileMessage[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		if (json instanceof JsonArray)
		{
			// L.out("found an array");
			return new Gson().fromJson(json, MobileMessage[].class);
		}
		// L.out("MobileMessageDeserializer found an element!");
		MobileMessage mobileMessage = context.deserialize(json, MobileMessage.class);
		return new MobileMessage[] { mobileMessage };
	}
}