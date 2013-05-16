package sw10.spideybc.build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import sw10.spideybc.util.JVMModelDeserializer;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibm.wala.types.TypeName;

public class JVMModel {
	public int referenceSize;
	public Map<String, Integer> typeSizeByTypeName;
	
	public JVMModel() {
		this.typeSizeByTypeName = new HashMap<String, Integer>();
	}
	
	public int getSizeForQualifiedType(TypeName type) {
		String parsedKey = type.toString();
		if (parsedKey.startsWith("L")) {
			parsedKey = parsedKey.substring(1);
		}
		parsedKey = parsedKey.replace('/', '.');
		if (this.typeSizeByTypeName.containsKey(parsedKey)) {
			return this.typeSizeByTypeName.get(parsedKey);
		}
		else
		{
			throw new NoSuchElementException();
		}
	}
	
	public static JVMModel makeFromJson(String file) {
		String line;
		StringBuilder json = new StringBuilder();
		BufferedReader reader;
		
		try {
			File jsonFile = new File(file);
			reader = new BufferedReader(new FileReader(jsonFile));
			while((line = reader.readLine()) != null) {
				json.append(line);
			}
		} catch(NullPointerException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) { 
		}
		
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(JVMModel.class, new JVMModelDeserializer());
		Gson gson = gsonBuilder.create();
		JVMModel model = gson.fromJson(json.toString(), JVMModel.class);
		
		return model;
	}
}
