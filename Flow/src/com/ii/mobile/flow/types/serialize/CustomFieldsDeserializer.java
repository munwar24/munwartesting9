package com.ii.mobile.flow.types.serialize;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.ii.mobile.flow.types.CustomField;

public class CustomFieldsDeserializer implements JsonDeserializer<CustomField[]>
{
	@Override
	public CustomField[] deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
			throws JsonParseException
	{

		if (json instanceof JsonArray)
		{
			// L.out("CustomFieldsDeserializer found an array: " + json);
			GsonBuilder gsonBuilder = new GsonBuilder();
			gsonBuilder.registerTypeAdapter(CustomField.class, new
					CustomFieldDeserializer());
			gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
			Gson gson = gsonBuilder.create();
			return gson.fromJson(json, CustomField[].class);
		}
		// L.out("CustomFieldsDeserializer found an element: " + json);

		// CustomField[] customField = context.deserialize(json,
		// CustomField.class);
		CustomField[] customField = getCustomFields(json);
		// L.out("customfield: " + customField);
		// return new CustomField[] { customField };
		return customField;
	}

	private CustomField[] getCustomFields(JsonElement json) {
		JsonObject jsonObject = json.getAsJsonObject();
		List<CustomField> temp = new ArrayList<CustomField>();

		for (Entry<String, JsonElement> entry : jsonObject.entrySet()) {
			String key = entry.getKey();
			JsonElement value = entry.getValue();
			// L.out("key: " + key);
			CustomField customField = new CustomField();
			if (key.equals("control")) {
				GsonBuilder gsonBuilder = new GsonBuilder();
				gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.UPPER_CAMEL_CASE);
				Gson gson = gsonBuilder.create();
				customField = gson.fromJson(json, CustomField.class);
				// L.out("customField: " + customField);
				return new CustomField[] { customField };
			} else {
				// L.out("value: " + value);
				customField.name = key;
				customField.value = value.getAsString();
				temp.add(customField);
			}
		}
		CustomField[] output = new CustomField[temp.size()];
		int i = 0;
		for (CustomField customField : temp) {
			output[i] = customField;
			i += 1;
		}
		// L.out("customField: " + temp);
		return output;
	}

}