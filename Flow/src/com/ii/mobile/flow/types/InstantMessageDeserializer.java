package com.ii.mobile.flow.types;

import java.lang.reflect.Type;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.ii.mobile.flow.types.GetActorStatus.InstantMessage;

public class InstantMessageDeserializer implements JsonDeserializer<InstantMessage[]>
{
	@Override
	public InstantMessage[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		if (json instanceof JsonArray)
		{
			// L.out("InstantMessage found an array");
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
			Gson gson = gsonBuilder.create();
			return gson.fromJson(json, InstantMessage[].class);
		}
		// L.out("InstantMessage found an element!");
		InstantMessage instantMessage = context.deserialize(json, InstantMessage.class);
		return new InstantMessage[] { instantMessage };
	}

}