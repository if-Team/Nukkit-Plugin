package debe.nukkitplugin.itemownership;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.PluginBase;
import debe.nukkitplugin.itemownership.command.ItemOwnershipCommand;
import debe.nukkitplugin.itemownership.listener.ItemEventListener;
import debe.nukkitplugin.itemownership.listener.SaveCommandListener;
import debe.nukkitplugin.itemownership.task.SetNametagTask;
import debe.nukkitplugin.itemownership.utils.Translation;
import debe.nukkitplugin.itemownership.utils.Utils;

public class ItemOwnership extends PluginBase{
	private static ItemOwnership instance;
	private LinkedHashMap<String, Object> data;
	private LinkedHashMap<String, Object> setting;

	public static ItemOwnership getInstance(){
		return ItemOwnership.instance;
	}

	@Override
	public void onLoad(){
		ItemOwnership.instance = this;
		new File(this.getDataFolder() + "/lang").mkdirs();
		this.saveDefaultData(false);
		this.loadData();
		Translation.load(this.setting.get("Language").toString().trim());
		this.updatePermissions();
		this.registerCommands();
	}

	@Override
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(new SaveCommandListener(), this);
		this.getServer().getPluginManager().registerEvents(new ItemEventListener(), this);
		this.getServer().getScheduler().scheduleRepeatingTask(new SetNametagTask(this), 20);
	}

	@Override
	public void onDisable(){
		this.saveData();
	}

	public void updatePermissions(){
		HashMap<String, Object> permissionSettings = Utils.parseJSON(Utils.loadFile(new File(this.getDataFolder() + "/Permissions.json")), new LinkedHashMap<String, Object>(){
			{
				put("itemownership.event.pickupitem", "OP");
				put("itemownership.command.itemownership", "TRUE");
				put("itemownership.command.itemownership.on", "TRUE");
				put("itemownership.command.itemownership.off", "TRUE");
				put("itemownership.command.itemownership.enable", "OP");
				put("itemownership.command.itemownership.disable", "OP");
				put("itemownership.command.itemownership.distance", "OP");
				put("itemownership.command.itemownership.span", "OP");
				put("itemownership.command.itemownership.reload", "OP");
				put("itemownership.command.itemownership.save", "OP");
				put("itemownership.command.itemownership.reset", "FALSE");
			}
		});
		this.getDescription().getPermissions().forEach(permission->{
			permission.setDefault(Permission.getByName(String.valueOf(permissionSettings.get(permission.getName()))));
		});
	}

	public void registerCommands(){
		LinkedHashMap<String, Object> commandSetting = Utils.parseJSON(Utils.loadFile(new File(this.getDataFolder() + "/command/CommandSetting.json")), new LinkedHashMap<String, Object>(){
			{
				put("Command", "ItemOwnership");
				put("Aliases", new String[]{"IO", "ItemOwn"});
			}
		});
		String command = commandSetting.get("Command").toString();
		String aliasesStr = commandSetting.get("Aliases").toString();
		this.getServer().getCommandMap().register(command, new ItemOwnershipCommand(command, (String[]) aliasesStr.substring(1, aliasesStr.length() - 1).toLowerCase().split(", "), Utils.toStringMap(Utils.parseJSON(Utils.loadFile(new File(this.getDataFolder() + "/command/SubCommandSetting.json")), new LinkedHashMap<String, Object>(){
			{
				put("on", "On");
				put("off", "Off");
				put("enable", "Enable");
				put("disable", "Disable");
				put("span", "Span");
				put("reload", "Reload");
				put("save", "Save");
				put("reset", "Reset");
			}
		}))));
	}

	public void loadData(){
		this.data = Utils.parseJSON(Utils.loadFile(new File(this.getDataFolder() + "/Data.json")), new LinkedHashMap<String, Object>());
		this.setting = Utils.parseJSON(Utils.loadFile(new File(this.getDataFolder() + "/Setting.json")), new LinkedHashMap<String, Object>(){
			{
				put("Language", "Default");
				put("Enable", true);
				put("Span", 200);
				put("Distance", 2);
			}
		});
	}

	public void saveData(){
		this.getDataFolder().mkdirs();
		Utils.saveJSON(new File(this.getDataFolder() + "/Data.json"), this.data);
		Utils.saveJSON(new File(this.getDataFolder() + "/Setting.json"), this.setting);
	}

	public void saveDefaultData(boolean replace){
		new ArrayList<String>(){
			{
				add("Data.json");
				add("Permissions.json");
				add("Setting.json");
				add("command/CommandSetting.json");
				add("command/SubCommandSetting.json");
				add("lang/eng.ini");
				add("lang/kor.ini");
			}
		}.forEach(fileName->this.saveResource("defaults/" + fileName, fileName, replace));
	}

	public LinkedHashMap<String, Object> getData(){
		return this.data;
	}

	public LinkedHashMap<String, Object> getSetting(){
		return this.setting;
	}
}
