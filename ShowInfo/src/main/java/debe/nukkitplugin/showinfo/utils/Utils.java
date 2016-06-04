package debe.nukkitplugin.showinfo.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

public class Utils{
	public static String loadFile(File file){
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			String temp;
			StringBuilder stringBuilder = new StringBuilder();
			while((temp = reader.readLine()) != null){
				stringBuilder.append(temp).append("\n");
			}
			reader.close();
			return stringBuilder.toString().trim();
		}catch(Exception e){
			return "";
		}
	}

	public static boolean saveFile(File file, String content){
		try{
			if(file.exists() || file.createNewFile()){
				InputStream contentStream = new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8));
				FileOutputStream saveStream = new FileOutputStream(file);
				int length;
				byte[] buffer = new byte[1024];
				while((length = contentStream.read(buffer)) != -1){
					saveStream.write(buffer, 0, length);
				}
				saveStream.close();
				contentStream.close();
				return true;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}

	public static LinkedHashMap<String, String> parseProperties(String content, LinkedHashMap<String, String> defaultMap){
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(defaultMap);
		String[] block;
		for(String line : content.split("\n")){
			if(Pattern.compile("[^#][a-zA-Z0-9\\-_\\.]*+=+[^\\r\\n]*").matcher(line).matches()){
				block = line.split("=", -1);
				map.put(block[0], block[1].trim());
			}
		}
		return map;
	}

	public static boolean saveJSON(File file, LinkedHashMap<String, Object> content){
		return Utils.saveFile(file, new GsonBuilder().setPrettyPrinting().create().toJson(content));
	}

	public static LinkedHashMap<String, Object> parseJSON(String content, LinkedHashMap<String, Object> defaultMap){
		LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>(defaultMap);
		Gson gson = new GsonBuilder().create();
		LinkedHashMap<String, Object> parsedMap = gson.fromJson(content, new TypeToken<LinkedHashMap<String, Object>>(){}.getType());
		parsedMap.forEach((key, value)->map.put(key, value));
		return map;
	}

	public static LinkedHashMap<String, String> toStringMap(LinkedHashMap<String, Object> content){
		return content.entrySet().stream().collect(Collectors.toMap(x->x.getKey(), x->x.getValue().toString(), (x, y)->x, LinkedHashMap::new));
	}

	public static int toInt(String content){
		try{
			return (int) Double.parseDouble(content);
		}catch(Exception e){}
		return 0;
	}

	public static boolean toBoolean(String content){
		switch(content.toLowerCase().trim()){
			case "on":
			case "true":
			case "yes":
				return true;
			case "off":
			case "false":
			case "no":
				return false;
		}
		return false;
	}
}
