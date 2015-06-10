package com.ii.mobile.flow.types.serialize;

import java.lang.reflect.Type;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.ii.mobile.flow.types.CustomField;
import com.ii.mobile.flow.types.GetActionStatus.Targets;

public class TargetsDeserializer implements JsonDeserializer<Targets[]>
{
	@Override
	public Targets[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		// JsonObject jsonObject = json.getAsJsonObject();
		// L.out("jsonObject: " + jsonObject + " " + jsonObject.getClass());
		// JsonArray customObject = jsonObject.getAsJsonArray("customField");
		//
		// L.out("customObject: " + customObject + " " +
		// customObject.getClass());
		if (json instanceof JsonArray)
		{
			// L.out("TargetDeserializer found an array: " + json);
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(CustomField[].class, new
					CustomFieldsDeserializer());
			gsonBuilder.registerTypeAdapter(CustomField.class, new
					CustomFieldDeserializer());
			gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
			Gson gson = gsonBuilder.create();

			return gson.fromJson(json, Targets[].class);
		}
		// L.out("TargetDeserializer found an element: " + json);
		// GsonBuilder gsonBuilder = new GsonBuilder();
		// gsonBuilder.registerTypeHierarchyAdapter(CustomField[].class, new
		// CustomFieldDeserializer());
		// // gsonBuilder.registerTypeAdapter(CustomField[].class, new
		// // CustomFieldDeserializer());
		// gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
		// Gson gson = gsonBuilder.create();
		// Targets foo = gson.fromJson(json, Targets.class);
		// L.out("foo: " + foo + " " + foo.getClass());
		// CustomField bar = gson.fromJson(json, CustomField.class);
		// L.out("bar: " + bar + " " + bar.getClass());
		Targets targets = context.deserialize(json, Targets.class);
		return new Targets[] { targets };
	}

	// private Targets[] createCustomFields(Targets[] targets) {
	// for (Targets target : targets) {
	//
	// }
	// return targets;
	// }

}