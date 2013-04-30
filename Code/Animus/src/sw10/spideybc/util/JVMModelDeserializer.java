package sw10.spideybc.util;

import java.lang.reflect.Type;
import java.util.Map.Entry;

import sw10.spideybc.build.JVMModel;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class JVMModelDeserializer implements JsonDeserializer<JVMModel> {
	@Override
	public JVMModel deserialize(JsonElement json, Type typeOfT,
			JsonDeserializationContext context) throws JsonParseException {
		final String REFERENCE_SIZE_KEY = "ReferenceSize";
		final String PRIMORDIAL_TYPES_ARRAY_KEY = "PrimordialTypeSizes";
		final String APPLICATION_TYPES_ARRAY_KEY = "ApplicationTypeSizes";
		
		JVMModel model = new JVMModel();
		model.referenceSize = json.getAsJsonObject().get(REFERENCE_SIZE_KEY).getAsInt();
		JsonArray primordialTypes = json.getAsJsonObject().getAsJsonArray(PRIMORDIAL_TYPES_ARRAY_KEY);
		for(JsonElement typeSizeEntry : primordialTypes) {
			for(Entry<String, JsonElement> e : typeSizeEntry.getAsJsonObject().entrySet()) {
				model.typeSizeByTypeName.put(e.getKey(), e.getValue().getAsInt());
			}
		}
		
		JsonArray ApplicationTypes = json.getAsJsonObject().getAsJsonArray(APPLICATION_TYPES_ARRAY_KEY);
		for(JsonElement typeSizeEntry : ApplicationTypes) {
			for(Entry<String, JsonElement> e : typeSizeEntry.getAsJsonObject().entrySet()) {
				model.typeSizeByTypeName.put(e.getKey(), e.getValue().getAsInt());
			}			
		}
		
		return model;
	}
}
