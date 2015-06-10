package com.ii.mobile.soap.gson.deserializer;

import java.lang.reflect.Type;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.ii.mobile.soap.gson.ListTaskClassesByFacilityID.FunctionalArea;
import com.ii.mobile.soap.gson.ListTaskClassesByFacilityID.TaskClass;
import com.ii.mobile.soap.gson.TaskClassDeserializer;
import com.ii.mobile.util.L;

public class FunctionalAreaDeserializer implements JsonDeserializer<FunctionalArea[]>
{
	public FunctionalArea[] deserialize(JsonElement jsonElement, Type typeOfT,
			JsonDeserializationContext context)
			throws JsonParseException
	{
		if (jsonElement instanceof JsonArray)
		{
			// L.out("FunctionalAreaDeserializer found an array");
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
			gsonBuilder.registerTypeAdapter(TaskClass[].class, new TaskClassDeserializer());
			Gson gson = gsonBuilder.create();
			return gson.fromJson(jsonElement, FunctionalArea[].class);
		}
		L.out("FunctionalAreaDeserializer found an element!");
		FunctionalArea functionalArea = context.deserialize(jsonElement, FunctionalArea.class);
		return new FunctionalArea[] { functionalArea };
	}

}