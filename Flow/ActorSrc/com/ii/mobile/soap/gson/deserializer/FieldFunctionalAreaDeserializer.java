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
import com.ii.mobile.soap.gson.GClassDeserializer;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.FunctionalArea;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.GClass;
import com.ii.mobile.util.L;

public class FieldFunctionalAreaDeserializer implements JsonDeserializer<FunctionalArea[]>
{
	public FunctionalArea[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		if (json instanceof JsonArray)
		{
			// L.out("FieldFunctionalAreaDeserializer found an array");
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
			gsonBuilder.registerTypeAdapter(GClass[].class, new GClassDeserializer());
			// gsonBuilder.registerTypeAdapter(Field[].class, new
			// FieldDeserializer());
			Gson gson = gsonBuilder.create();
			return gson.fromJson(json, FunctionalArea[].class);
		}
		L.out("FieldFunctionalAreaDeserializer found an element!");
		FunctionalArea functionalArea = context.deserialize(json, FunctionalArea.class);
		return new FunctionalArea[] { functionalArea };
	}

}