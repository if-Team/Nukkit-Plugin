package debe.nukkitplugin.showinfo;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import cn.nukkit.permission.Permission;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.TaskHandler;
import debe.nukkitplugin.showinfo.command.ShowInfoCommand;
import debe.nukkitplugin.showinfo.event.SendInfoEvent;
import debe.nukkitplugin.showinfo.listener.SaveCommandListener;
import debe.nukkitplugin.showinfo.task.ShowInfoTask;
import debe.nukkitplugin.showinfo.utils.Translation;
import debe.nukkitplugin.showinfo.utils.Utils;

public class ShowInfo extends PluginBase{
	private static ShowInfo instance;
	private String format;
	private LinkedHashMap<String, Object> data;
	private LinkedHashMap<String, Object> setting;
	private TaskHandler showinfoTask;

	public static ShowInfo getInstance(){
		return ShowInfo.instance;
	}

	@Override
	public void onLoad(){
		ShowInfo.instance = this;
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
		this.getServer().getPluginManager().registerEvents(new SaveCommandListener(this), this);
	}

	@Override
	public void onDisable(){
		this.taskStop();
		this.saveData();
	}

	public void onTaskRun(){
		StringBuilder formatBuilder;
		int push = Utils.toInt(this.setting.get("PushLevel").toString());
		if(push == 0){
			formatBuilder = new StringBuilder(this.format.toString());
		}else{
			StringBuilder pushBuilder = new StringBuilder("");
			for(int i = 0; i < Math.abs(push); i++){
				pushBuilder.append(" ");
			}
			if(push > 0){
				formatBuilder = new StringBuilder(pushBuilder.toString()).append(this.format.replace("\n", "\n" + pushBuilder.toString()));
			}else{
				formatBuilder = new StringBuilder(this.format.replace("\n", pushBuilder.toString() + "\n")).append(pushBuilder.toString());
			}
		}
		String format = formatBuilder.toString();
		this.getServer().getOnlinePlayers().values().stream().filter(player->(!this.data.containsKey(player.getName().toLowerCase()) && player.isAlive() && player.spawned)).forEach(player->{
			SendInfoEvent event = new SendInfoEvent(player, format);
			this.getServer().getPluginManager().callEvent(event);
			if(!event.isCancelled()){
				player.sendPopup(event.getInfo());
			}
		});
	}

	public boolean isTaskStop(){
		return (this.showinfoTask == null || this.showinfoTask.isCancelled());
	}

	public void taskStart(){
		if(this.isTaskStop() != true){
			this.taskStop();
		}
		this.showinfoTask = this.getServer().getScheduler().scheduleRepeatingTask(new ShowInfoTask(this), Utils.toInt(this.setting.get("Tick").toString()));
	}

	public void setTaskPeriod(Integer period){
		if(this.isTaskStop() != true){
			this.showinfoTask.setPeriod(period);
		}
	}

	public void taskStop(){
		if(this.isTaskStop() != true){
			this.getServer().getScheduler().cancelTask(this.showinfoTask.getTaskId());
		}
	}

	public void updatePermissions(){
		HashMap<String, Object> permissionSettings = Utils.parseJSON(Utils.loadFile(new File(this.getDataFolder() + "/Permissions.json")), new LinkedHashMap<String, Object>(){
			{
				put("showinfo.command.showinfo", "TRUE");
				put("showinfo.command.showinfo.on", "TRUE");
				put("showinfo.command.showinfo.off", "TRUE");
				put("showinfo.command.showinfo.start", "OP");
				put("showinfo.command.showinfo.stop", "OP");
				put("showinfo.command.showinfo.push", "OP");
				put("showinfo.command.showinfo.tick", "OP");
				put("showinfo.command.showinfo.oprank", "OP");
				put("showinfo.command.showinfo.reload", "OP");
				put("showinfo.command.showinfo.save", "OP");
				put("showinfo.command.showinfo.reset", "FALSE");
			}
		});
		this.getDescription().getPermissions().forEach(permission->{
			permission.setDefault(Permission.getByName(String.valueOf(permissionSettings.get(permission.getName()))));
		});
	}

	public void registerCommands(){
		LinkedHashMap<String, Object> commandSetting = Utils.parseJSON(Utils.loadFile(new File(this.getDataFolder() + "/command/CommandSetting.json")), new LinkedHashMap<String, Object>(){
			{
				put("Command", "ShowInfo");
				put("Aliases", new String[]{"SI", "ShowInformation"});
			}
		});
		String command = commandSetting.get("Command").toString();
		String aliasesStr = commandSetting.get("Aliases").toString();
		this.getServer().getCommandMap().register(command, new ShowInfoCommand(command, (String[]) aliasesStr.substring(1, aliasesStr.length() - 1).toLowerCase().split(", "), Utils.toStringMap(Utils.parseJSON(Utils.loadFile(new File(this.getDataFolder() + "/command/SubCommandSetting.json")), new LinkedHashMap<String, Object>(){
			{
				put("on", "On");
				put("off", "Off");
				put("start", "Start");
				put("stop", "Stop");
				put("push", "Push");
				put("tick", "Tick");
				put("reload", "Reload");
				put("save", "Save");
				put("reset", "Reset");
			}
		}))));
	}

	public void loadData(){
		this.format = Utils.loadFile(new File(this.getDataFolder() + "/Format.txt"));
		this.data = Utils.parseJSON(Utils.loadFile(new File(this.getDataFolder() + "/Data.json")), new LinkedHashMap<String, Object>());
		this.setting = Utils.parseJSON(Utils.loadFile(new File(this.getDataFolder() + "/Setting.json")), new LinkedHashMap<String, Object>(){
			{
				put("Language", "Default");
				put("Enable", true);
				put("OpInRank", false);
				put("PushLevel", 0);
				put("Tick", 20);
			}
		});
		this.format = Utils.loadFile(new File(this.getDataFolder() + "/Format.txt"));
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
				add("Format.txt");
				add("Permissions.json");
				add("Setting.json");
				add("command/CommandSetting.json");
				add("command/SubCommandSetting.json");
				add("lang/eng.ini");
				add("lang/kor.ini");
			}
		}.forEach(fileName->this.saveResource("defaults/" + fileName, fileName, replace));
	}

	public String getFomat(){
		return this.format;
	}

	public LinkedHashMap<String, Object> getData(){
		return this.data;
	}

	public LinkedHashMap<String, Object> getSetting(){
		return this.setting;
	}
}
