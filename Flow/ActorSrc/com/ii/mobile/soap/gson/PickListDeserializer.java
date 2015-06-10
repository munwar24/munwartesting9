package com.ii.mobile.soap.gson;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.ii.mobile.soap.gson.GetTaskDefinitionFieldsDataForScreenByFacilityID.PickList;
import com.ii.mobile.util.L;

public class PickListDeserializer implements JsonDeserializer<PickList[]>
{
	public PickList[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		if (json instanceof JsonArray)
		{
			// L.out("found an array");
			return new Gson().fromJson(json, PickList[].class);
		}
		L.out("PickListDeserializer found an element!");
		PickList pickList = context.deserialize(json, PickList.class);
		return new PickList[] { pickList };
	}

}