package debe.nukkitplugin.autogarbagecollector.utils;

import java.io.File;
import java.util.LinkedHashMap;

import cn.nukkit.Server;
import cn.nukkit.utils.TextFormat;
import debe.nukkitplugin.autogarbagecollector.AutoGarbageCollector;

public class Translation{
	protected static LinkedHashMap<String, String> langs;

	public static void load(String lang){
		lang = lang.equalsIgnoreCase("default") ? Server.getInstance().getLanguage().getLang() : lang.toLowerCase();
		File file = new File(AutoGarbageCollector.getInstance().getDataFolder() + "/lang/" + lang + ".ini");
		if(file.exists() && file.isFile()){
			Translation.langs = Utils.parseProperties(Utils.loadFile(file), new LinkedHashMap<String, String>(){
				{
					put("prefix", "[AutoGarbageCollector]");
					put("colors.success", TextFormat.GREEN);
					put("colors.failed", TextFormat.RED);
					put("consloe.notice.garbageCollect.memory", "{%0}MB memory was collected.");
					put("consloe.notice.garbageCollect.chunks", "{%0} chunks was collected.");
					put("consloe.notice.garbageCollect.entities", "{%0} entitie was collected.");
					put("consloe.notice.garbageCollect.blockEntities", "{%0} blockentities was collected.");
					put("commands.generic.permission", "You do not have permission to use this command");
					put("commands.generic.notFound", "Unknown command. Usage : {%0}");
					put("commands.generic.invalidNumber", "You have entered an invalid number : {%0}");
					put("commands.generic.usage", "Usage: {%0}");
					put("commands.generic.usages", "/{%0} {%1} {%2}");
					put("commands.enable.success", "Auto garbage collect is enable.");
					put("commands.enable.failed", "Auto garbage collect is already enabled.");
					put("commands.disable.success", "Auto garbage collect is disable.");
					put("commands.disable.failed", "Auto garbage collect is already disabled.");
					put("commands.period.usage", "<Tick>  (1second per 20tick)");
					put("commands.period.success", "Collect repeat period is set to {%0} Ticks.");
					put("commands.reload.success", "Reloaded AutoGarbageCollector data.");
					put("commands.save.success", "Saved AutoGarbageCollector data.");
					put("commands.reset.success", "Reseted AutoGarbageCollector data.");
				}
			});
		}else{
			AutoGarbageCollector.getInstance().getLogger().error("Error on load language setting file.");
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
