package com.ii.mobile.soap.gson;

import java.lang.reflect.Type;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.ii.mobile.soap.gson.ListTaskClassesByFacilityID.TaskClass;
import com.ii.mobile.util.L;

public class TaskClassDeserializer implements JsonDeserializer<TaskClass[]>
{
	public TaskClass[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		if (json instanceof JsonArray)
		{

			// L.out("TaskClassDeserializer found an array");
			return new Gson().fromJson(json, TaskClass[].class);
		}
		L.out("TaskClassDeserializer found an element!");
		TaskClass taskClass = context.deserialize(json, TaskClass.class);
		return new TaskClass[] { taskClass };
	}

}