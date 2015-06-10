package com.ii.mobile.flow.types.serialize;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ii.mobile.flow.types.CustomField;

public class CustomFieldDeserializer implements JsonDeserializer<CustomField>
{
	@Override
	public CustomField deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{
		// L.out("CustomFieldDeserializer found an element: " + json + " " +
		// json.getClass());
		JsonObject jsonObject = json.getAsJsonObject();
		CustomField customField = new CustomField();

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			// L.out("key: " + key);
			if (key.equals("control")) {
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
				Gson gson = gsonBuilder.create();
				customField = gson.fromJson(json, CustomField.class);
				// L.out("customField: " + customField);
				return customField;
			} else {
				// L.out("value: " + value);
				customField.name = key;
				customField.value = value.getAsString();
				return customField;
			}
		}
		// L.out("customField: " + customField);
		return customField;
	}
}