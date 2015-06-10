package com.ii.mobile.soap.gson;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsForScreenByFacilityID.Field;
import com.ii.mobile.util.L;

public class FieldDeserializer implements JsonDeserializer<Field[]>
{
	public Field[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		if (json instanceof JsonArray)
		{
			// L.out("FieldDeserializer found an array");
			return new Gson().fromJson(json, Field[].class);
		}
		L.out("FieldDeserializer found an element!");
		Field field = context.deserialize(json, Field.class);
		return new Field[] { field };
	}

}