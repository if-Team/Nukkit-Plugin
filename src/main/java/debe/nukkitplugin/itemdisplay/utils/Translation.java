package debe.nukkitplugin.itemdisplay.utils;

import java.io.File;
import java.util.LinkedHashMap;

import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import debe.nukkitplugin.itemdisplay.ItemDisplay;

public class Translation{
	protected static LinkedHashMap<String, String> langs;

	public static void load(String lang){
		lang = lang.equalsIgnoreCase("default") ? Server.getInstance().getLanguage().getLang() : lang.toLowerCase();
		File file = new File(ItemDisplay.getInstance().getDataFolder() + "/lang/" + lang + ".ini");
		if(file.exists() && file.isFile()){
			Translation.langs = FileUtils.parseProperties(FileUtils.loadFile(file), new LinkedHashMap<String, String>(){
				{
					put("prefix", "[ItemDisplay]");
					put("colors.success", TextFormat.GREEN);
					put("colors.failed", TextFormat.RED);
					put("showNametag.toViewer", "§aName : {%0} \n§aItemID : {%1}:{%2}");
					put("touchTask.add.success", "Imaginary item {%0} is added. ID: {%1}:{%2}, Pos: [X:{%1}, Y:{%2}, Z:{%3}, World:{%4}]");
					put("commands.generic.permission", "You do not have permission to use this command");
					put("commands.generic.ingame", "You can only perform this command as a player");
					put("commands.generic.notFound", "Unknown command. Usage : {%0}");
					put("commands.generic.usage", "Usage: {%0}");
					put("commands.generic.usages", "/{%0} {%1} {%2}");
					put("commands.add.usage", "<Name> <ItemID> [Enchanted]");
					put("commands.add.success", "Now please touch the target block. (Name: {%0}, ItemID: {%1}:{%2})");
					put("commands.add.failed.alreadyExists", "{%0} is already exists name.");
					put("commands.add.failed.invalidItemID", "You have entered an invalid Item ID : {%0}.\nPlease enter ItemID or \"*\".\n   * is means item in your hand");
					put("commands.remove.usage", "<Name>");
					put("commands.remove.success", "Imaginary item {%0} is removed.");
					put("commands.remove.failed.notFound", "{%0} is Unknown name.");
					put("commands.cancel.success", "Cancelled your task.");
					put("commands.cancel.failed", "You not have any task.");
					put("commands.view.enable", "Now you can view the imaginary item's nametag.");
					put("commands.view.disable", "Now you can't view the imaginary item's nametag.");
					put("commands.list.usage", "[Page]");
					put("commands.list.success.title", "ItemDisplay list. (Page {%0} of {%1}) (Count: {%2})");
					put("commands.list.success.entry", "[{%0}] Name: {%1}, ID: {%2}, Pos: [X: {%3}, Y: {%4}, Z: {%5}, World: {%6}]");
					put("commands.list.failed.empty", "List is empty");
					put("commands.list.failed.invalidPage", "You have entered an invalid page : {%0}");
					put("commands.reload.success", "Reloaded ItemDisplay data.");
					put("commands.save.success", "Saved ItemDisplay data.");
					put("commands.reset.success", "Reseted ItemDisplay data.");
				}
			});
		}else{
			ItemDisplay.getInstance().getLogger().error("Error on load language setting file." + lang);
		}
	}

	public static String translate(String langText){
		return Translation.translate(langText, new String[]{});
	}

	public static String translate(String langText, String param){
		return Translation.translate(langText, new String[]{param});
	}

	public static String translate(String langText, String[] params){
		String text = Translation.langs.getOrDefault(langText, "");
		if(text.equals("")){
			return null;
		}else{
			for(int i = 0; i < params.length; i++){
				text = text.replace("{%" + i + "}", params[i]);
			}
			return text.replace("\\n", "\n");
		}
	}
}
