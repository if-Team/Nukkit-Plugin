package debe.nukkitplugin.itemdisplay.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import cn.nukkit.Server;
import cn.nukkit.permission.Permission;
import debe.nukkitplugin.itemdisplay.ItemDisplay;
import debe.nukkitplugin.itemdisplay.command.ItemDisplayCommand;
import debe.nukkitplugin.itemdisplay.entity.ImaginaryItem;

public class FileUtils{
	public static void mkdirs(){
		new File(ItemDisplay.getInstance().getDataFolder() + "/lang").mkdirs();
	}

	public static String getResource(String fileName){
		ItemDisplay plugin = ItemDisplay.getInstance();
		File file = new File(plugin.getDataFolder() + "/" + fileName);
		plugin.saveResource("defaults/" + fileName, fileName, false);
		return FileUtils.loadFile(file);
	}

	public static void loadSetting(){
		try{
			JSONObject jsonSetting = (JSONObject) new JSONParser().parse(FileUtils.getResource("Setting.json"));
			Translation.load(jsonSetting.containsKey("Language") ? (String) jsonSetting.get("Language") : "Default");
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void loadPermissions(){
		try{
			JSONObject jsonPermissions = (JSONObject) new JSONParser().parse(FileUtils.getResource("Permissions.json"));
			ItemDisplay.getInstance().getDescription().getPermissions().stream().filter(permission->jsonPermissions.containsKey(permission.getName())).forEach(permission->permission.setDefault(Permission.getByName(String.valueOf(jsonPermissions.get(permission.getName())))));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void loadCommandSetting(){
		try{
			JSONObject jsonCommandSetting = (JSONObject) new JSONParser().parse(FileUtils.getResource("command/CommandSetting.json"));
			String command = (String) jsonCommandSetting.get("Command");
			JSONArray jsonAliases = (JSONArray) jsonCommandSetting.get("Aliases");
			String[] aliases = new String[jsonAliases.size()];
			for(int i = 0; i < jsonAliases.size(); i++){
				aliases[i] = ((String) jsonAliases.get(i)).toLowerCase();
			}
			JSONObject jsonSubCommandSetting = (JSONObject) new JSONParser().parse(FileUtils.getResource("command/SubCommandSetting.json"));
			HashMap<String, String> subCommands = new HashMap<String, String>();
			new HashMap<String, String>(){
				{
					put("add", "Add");
					put("remove", "Remove");
					put("cancel", "Cancel");
					put("view", "View");
					put("list", "List");
					put("reload", "Reload");
					put("save", "Save");
					put("reset", "Reset");
				}
			}.forEach((subCommandName, defualtValue)->subCommands.put(subCommandName, jsonSubCommandSetting.containsKey(subCommandName) ? (String) jsonSubCommandSetting.get(subCommandName) : defualtValue));
			Server.getInstance().getCommandMap().register(command, new ItemDisplayCommand(command, aliases, subCommands));
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public static void loadData(){
		try{
			ItemDisplay plugin = ItemDisplay.getInstance();
			JSONObject jsonData = (JSONObject) new JSONParser().parse(FileUtils.getResource("Data.json"));
			HashMap<String, ImaginaryItem> imaginaryItems = new HashMap<String, ImaginaryItem>();
			ArrayList<String> nametagViewers = new ArrayList<String>();
			JSONObject jsonImaginaryItems = (JSONObject) jsonData.get("Items");
			for(Object key : jsonImaginaryItems.keySet()){
				String name = (String) key;
				ImaginaryItem imaginaryItem = plugin.parseImaginaryItem(name, (String) jsonImaginaryItems.get(key));
				if(imaginaryItem instanceof ImaginaryItem){
					imaginaryItems.put(name, imaginaryItem);
				}
			}
			plugin.setImaginaryItems(imaginaryItems);
			for(Object nametagViewer : ((JSONArray) jsonData.get("NametagViewers"))){
				nametagViewers.add((String) nametagViewer);
			}
			plugin.setNametagViewers(nametagViewers);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public static void saveData(){
		ItemDisplay plugin = ItemDisplay.getInstance();
		JSONObject jsonData = new JSONObject();
		JSONObject jsonImaginaryItems = new JSONObject();
		plugin.getImaginaryItems().forEach((name, imaginaryItem)->{
			jsonImaginaryItems.put(name, imaginaryItem.getItem().getId() + ":" + imaginaryItem.getItem().getDamage() + ":" + imaginaryItem.getItem().hasEnchantments() + ":" + imaginaryItem.x + ":" + imaginaryItem.y + ":" + imaginaryItem.z + ":" + imaginaryItem.level.getFolderName());
		});
		jsonData.put("Items", jsonImaginaryItems);
		JSONArray jsonNametagViewers = new JSONArray();
		plugin.getNametagViewers().forEach(jsonNametagViewers::add);
		jsonData.put("NametagViewers", jsonNametagViewers);
		FileUtils.saveFile(new File(plugin.getDataFolder() + "/Data.json"), new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(jsonData.toJSONString())));
	}

	public static String loadFile(File file){
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
			String temp;
			StringBuilder stringBuilder = new StringBuilder();
			while((temp = reader.readLine()) != null){
				stringBuilder.append(temp).append("\n");
			}
			reader.close();
			return stringBuilder.toString();
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
}
