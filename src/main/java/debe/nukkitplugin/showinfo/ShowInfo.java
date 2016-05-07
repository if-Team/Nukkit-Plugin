package debe.nukkitplugin.showinfo;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.TaskHandler;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.ConfigSection;
import cn.nukkit.utils.Utils;
import debe.nukkitplugin.showinfo.command.ShowInfoCommand;
import debe.nukkitplugin.showinfo.event.SendInfoEvent;
import debe.nukkitplugin.showinfo.listener.CommandListener;
import debe.nukkitplugin.showinfo.task.ShowInfoTask;

public class ShowInfo extends PluginBase{
	private String format;
	private Config offPlayersConfig;
	private Config settingConfig;
	private TaskHandler showinfoTask;

	@Override
	public void onEnable(){
		this.loadData();
		this.getServer().getCommandMap().register("showinfo", new ShowInfoCommand(this));
		this.getServer().getPluginManager().registerEvents(new CommandListener(this), this);
	}

	@Override
	public void onDisable(){
		this.taskStop();
		this.saveData();
	}

	public void onTaskRun(){
		StringBuilder formatBuilder;
		Integer push = this.getSettingConfig().getInt("PushLevel");
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
		SendInfoEvent event;
		for(Player player : new ArrayList<>(this.getServer().getOnlinePlayers().values())){
			String playerName = player.getName();
			if(!this.getOffPlayersConfig().exists(playerName, true) && player.isAlive() && player.spawned){
				event = new SendInfoEvent(player, format);
				this.getServer().getPluginManager().callEvent(event);
				if(!event.isCancelled()){
					player.sendPopup(event.getInfo());
				}
			}
		}
	}

	public Boolean isTaskStop(){
		return (this.showinfoTask == null || this.showinfoTask.isCancelled());
	}

	public void taskStart(){
		if(this.isTaskStop() != true){
			this.taskStop();
		}
		this.showinfoTask = this.getServer().getScheduler().scheduleRepeatingTask(new ShowInfoTask(this), this.getSettingConfig().getInt("Tick"));
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

	public void loadData(){
		this.saveDefaultData(false);
		this.offPlayersConfig = new Config(new File(this.getDataFolder(), "/OffPlayers.yml"));
		this.settingConfig = new Config(new File(this.getDataFolder(), "/Setting.yml"), Config.YAML, new ConfigSection(new LinkedHashMap<String, Object>(){
			{
				put("Enable", true);
				put("OpInRank", false);
				put("PushLevel", 0);
				put("Tick", 20);
			}
		}));
		try{
			this.format = Utils.readFile(new File(this.getDataFolder(), "Format.txt"));
		}catch(IOException e){
			this.getServer().getLogger().logException(e);
		}
		if(this.getSettingConfig().getBoolean("Enable")){
			this.getLogger().info("ShowInfo is running!");
			this.taskStart();
		}else{
			this.getLogger().info("ShowInfo is not run...");
		}
	}

	public void saveData(){
		this.offPlayersConfig.save();
		this.settingConfig.save();
	}

	public void saveDefaultData(Boolean replace){
		this.saveResource("Format.txt", "Format.txt", replace);
		this.saveResource("OffPlayers.yml", "OffPlayers.yml", replace);
		this.saveResource("Setting.yml", "Setting.yml", replace);
	}

	public String getFomat(){
		return this.format;
	}

	public Config getOffPlayersConfig(){
		return this.offPlayersConfig;
	}

	public Config getSettingConfig(){
		return this.settingConfig;
	}
}
