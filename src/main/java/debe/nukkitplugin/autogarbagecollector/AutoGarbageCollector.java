package debe.nukkitplugin.autogarbagecollector;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.TaskHandler;
import debe.nukkitplugin.autogarbagecollector.command.AutoGarbageCollectorCommand;
import debe.nukkitplugin.autogarbagecollector.listener.SaveCommandListener;
import debe.nukkitplugin.autogarbagecollector.task.GarbageCollectorTask;
import debe.nukkitplugin.autogarbagecollector.utils.Translation;
import debe.nukkitplugin.autogarbagecollector.utils.Utils;

public class AutoGarbageCollector extends PluginBase{
	private static AutoGarbageCollector instance;
	private LinkedHashMap<String, Object> setting;
	private TaskHandler garbageCollectorTask;

	public static AutoGarbageCollector getInstance(){
		return AutoGarbageCollector.instance;
	}

	@Override
	public void onLoad(){
		AutoGarbageCollector.instance = this;
		new File(this.getDataFolder() + "/lang").mkdirs();
		this.saveDefaultData(false);
		this.loadData();
		Translation.load(this.setting.get("Language").toString().trim());
		this.updatePermissions();
		this.registerCommands();
	}

	@Override
	public void onEnable(){
		if(Utils.toBoolean(this.setting.get("Enable").toString())){
			this.taskStart();
		}
		this.getServer().getPluginManager().registerEvents(new SaveCommandListener(), this);
	}

	@Override
	public void onDisable(){
		this.taskStop();
		this.saveData();
	}

	public boolean isTaskStop(){
		return (this.garbageCollectorTask == null || this.garbageCollectorTask.isCancelled());
	}

	public void taskStart(){
		if(this.isTaskStop() != true){
			this.taskStop();
		}
		int period = Utils.toInt(this.setting.get("Period").toString());
		this.garbageCollectorTask = this.getServer().getScheduler().scheduleDelayedRepeatingTask(new GarbageCollectorTask(this), period, period);
	}

	public void taskStop(){
		if(this.isTaskStop() != true){
			this.getServer().getScheduler().cancelTask(this.garbageCollectorTask.getTaskId());
		}
	}

	public void updatePermissions(){
		HashMap<String, Object> permissionSettings = Utils.parseJSON(Utils.loadFile(new File(this.getDataFolder() + "/Permissions.json")), new LinkedHashMap<String, Object>(){
			{
				put("autogarbagecollector.command.autogarbagecollector", "OP");
				put("autogarbagecollector.command.autogarbagecollector.enable", "OP");
				put("autogarbagecollector.command.autogarbagecollector.disable", "OP");
				put("autogarbagecollector.command.autogarbagecollector.period", "OP");
				put("autogarbagecollector.command.autogarbagecollector.reload", "OP");
				put("autogarbagecollector.command.autogarbagecollector.save", "OP");
				put("autogarbagecollector.command.autogarbagecollector.reset", "FALSE");
			}
		});
		this.getDescription().getPermissions().forEach(permission->{
			permission.setDefault(Permission.getByName(String.valueOf(permissionSettings.get(permission.getName()))));
		});
	}

	public void registerCommands(){
		LinkedHashMap<String, Object> commandSetting = Utils.parseJSON(Utils.loadFile(new File(this.getDataFolder() + "/command/CommandSetting.json")), new LinkedHashMap<String, Object>(){
			{
				put("Command", "AutoGarbageCollector");
				put("Aliases", new String[]{"AGC", "AutoGC"});
			}
		});
		String command = commandSetting.get("Command").toString();
		String aliasesStr = commandSetting.get("Aliases").toString();
		this.getServer().getCommandMap().register(command, new AutoGarbageCollectorCommand(command, (String[]) aliasesStr.substring(1, aliasesStr.length() - 1).toLowerCase().split(", "), Utils.toStringMap(Utils.parseJSON(Utils.loadFile(new File(this.getDataFolder() + "/command/SubCommandSetting.json")), new LinkedHashMap<String, Object>(){
			{
				put("enable", "Enable");
				put("disable", "Disable");
				put("period", "Period");
				put("reload", "Reload");
				put("save", "Save");
				put("reset", "Reset");
			}
		}))));
	}

	public void loadData(){
		this.setting = Utils.parseJSON(Utils.loadFile(new File(this.getDataFolder() + "/Setting.json")), new LinkedHashMap<String, Object>(){
			{
				put("Language", "Default");
				put("Enable", true);
				put("Period", 12000);
			}
		});
	}

	public void saveData(){
		this.getDataFolder().mkdirs();
		Utils.saveJSON(new File(this.getDataFolder() + "/Setting.json"), this.setting);
	}

	public void saveDefaultData(boolean replace){
		new ArrayList<String>(){
			{
				add("Permissions.json");
				add("Setting.json");
				add("command/CommandSetting.json");
				add("command/SubCommandSetting.json");
				add("lang/eng.ini");
				add("lang/kor.ini");
			}
		}.forEach(fileName->this.saveResource("defaults/" + fileName, fileName, replace));
	}

	public LinkedHashMap<String, Object> getSetting(){
		return this.setting;
	}
}
