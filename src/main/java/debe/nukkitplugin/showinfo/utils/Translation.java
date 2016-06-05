package debe.nukkitplugin.showinfo.utils;

import java.io.File;
import java.util.LinkedHashMap;

import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import debe.nukkitplugin.showinfo.ShowInfo;

public class Translation{
	protected static LinkedHashMap<String, String> langs;

	public static void load(String lang){
		lang = lang.equalsIgnoreCase("default") ? Server.getInstance().getLanguage().getLang() : lang.toLowerCase();
		File file = new File(ShowInfo.getInstance().getDataFolder() + "/lang/" + lang + ".ini");
		if(file.exists() && file.isFile()){
			Translation.langs = Utils.parseProperties(Utils.loadFile(file), new LinkedHashMap<String, String>(){
				{
					put("prefix", "[ShowInfo]");
					put("colors.success", TextFormat.GREEN);
					put("colors.failed", TextFormat.RED);
					put("commands.generic.permission", "You do not have permission to use this command");
					put("commands.generic.ingame", "You can only perform this command as a player");
					put("commands.generic.notFound", "Unknown command. Usage : {%0}");
					put("commands.generic.invalidNumber", "You have entered an invalid number : {%0}");
					put("commands.generic.usage", "Usage: {%0}");
					put("commands.generic.usagas", "/{%0} {%1} {%2}");
					put("commands.on.success", "Now information is display.");
					put("commands.on.failed", "Information already displayed.");
					put("commands.off.success", "Now information is not display.");
					put("commands.off.failed", "Information is already not displayed.");
					put("commands.enable.success", "Send infomation is enable.");
					put("commands.enable.failed", "Send infomation is already enabled.");
					put("commands.disable.success", "Send infomation is disable.");
					put("commands.disable.failed", "Send infomation is already disabled.");
					put("commands.push.usage", "<PushLevel>");
					put("commands.push.success", "Push level is set to {%0} Level.");
					put("commands.tick.usage", "<Tick>  (1second per 20tick)");
					put("commands.tick.success", "Show delay is set to {%0} Ticks.");
					put("commands.reload.success", "Reloaded ShowInfo data.");
					put("commands.save.success", "Saved ShowInfo data.");
					put("commands.reset.success", "Reseted ShowInfo data.");
				}
			});
		}else{
			ShowInfo.getInstance().getLogger().error("Error on load language setting file.");
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
