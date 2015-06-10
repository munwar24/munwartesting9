package com.ii.mobile.soap.gson;

import java.lang.reflect.Type;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.Field;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.GClass;
import com.ii.mobile.util.L;

public class GClassDeserializer implements JsonDeserializer<GClass[]>
{
	public GClass[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		if (json instanceof JsonArray)
		{
			// L.out("found an array");
			L.out("GClassDeserializer found an array");
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
			gsonBuilder.registerTypeAdapter(Field[].class, new FieldDeserializer());
			Gson gson = gsonBuilder.create();
			return gson.fromJson(json, GClass[].class);
		}
		L.out("GClassDeserializer found an element!");
		GClass gClass = context.deserialize(json, GClass.class);
		return new GClass[] { gClass };
	}

}