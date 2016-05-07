package debe.nukkitplugin.showinfo.command;

import java.util.regex.Pattern;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.TranslationContainer;
import cn.nukkit.utils.TextFormat;
import debe.nukkitplugin.showinfo.ShowInfo;

public class ShowInfoCommand extends Command{
	private ShowInfo plugin;
	private String[] subs = new String[]{"On", "Off", "Start", "Stop", "Push", "Tick", "OpRank", "Reload", "Save", "Reset"};

	public ShowInfoCommand(ShowInfo plugin){
		super("showinfo", "Comamnd of ShowInfo", "/ShowInfo <On | Off | Start | Stop | Push | Tick| OpRank | Reload | Save | Reset>");
		this.setPermission("showinfo.command.showinfo");
		this.setAliases(new String[]{"si", "info"});
		this.plugin = plugin;
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args){
		if(!this.testPermission(sender)){
			return true;
		}else if(args.length == 0){
			sender.sendMessage(new TranslationContainer("commands.generic.usage", this.getUsage(sender)));
			return true;
		}else{
			Boolean checkedPermission = false;
			for(String sub : this.subs){
				if(sub.equalsIgnoreCase(args[0])){
					if(!sender.hasPermission("showinfo.command.showinfo." + sub.toLowerCase())){
						sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.permission"));
						return true;
					}else{
						checkedPermission = true;
						break;
					}
				}
			}
			if(!checkedPermission){
				sender.sendMessage(new TranslationContainer("commands.generic.usage", this.getUsage(sender)));
				return true;
			}
			switch(args[0].toLowerCase()){
				case "on":
					if(!(sender instanceof Player)){
						sender.sendMessage(TextFormat.RED + "[ShowInfo] Please use this command only in the game.");
					}else if(!this.plugin.getOffPlayersConfig().exists(sender.getName(), true)){
						sender.sendMessage(TextFormat.RED + "[ShowInfo] Information already displayed");
					}else{
						this.plugin.getOffPlayersConfig().remove(sender.getName().toLowerCase());
						sender.sendMessage(TextFormat.AQUA + "[ShowInfo] Now information is displayed.");
					}
					break;
				case "off":
					if(!(sender instanceof Player)){
						sender.sendMessage(TextFormat.RED + "[ShowInfo] Please use this command only in the game.");
					}else if(this.plugin.getOffPlayersConfig().exists(sender.getName(), true)){
						sender.sendMessage(TextFormat.RED + "[ShowInfo] Information already not displayed");
					}else{
						this.plugin.getOffPlayersConfig().set(sender.getName().toLowerCase(), true);
						sender.sendMessage(TextFormat.AQUA + "[ShowInfo] Now information is not displayed.");
					}
					break;
				case "start":
					if(!this.plugin.isTaskStop()){
						sender.sendMessage(TextFormat.RED + "[ShowInfo] Information is already displayed");
					}else{
						this.plugin.getSettingConfig().set("Enable", true);
						this.plugin.taskStart();
						sender.sendMessage(TextFormat.AQUA + "[ShowInfo] Now information is displayed to players");
					}
					break;
				case "stop":
					if(this.plugin.isTaskStop()){
						sender.sendMessage(TextFormat.RED + "[ShowInfo] Already information is not displayed.");
					}else{
						this.plugin.getSettingConfig().set("Enable", false);
						this.plugin.taskStop();
						sender.sendMessage(TextFormat.AQUA + "[ShowInfo] Now information is not displayed to players");
					}
					break;
				case "push":
					if(args.length <= 1 || args[1].equals("")){
						sender.sendMessage(new TranslationContainer("commands.generic.usage", "/ShowInfo Push <PushLevel>"));
					}else if(!Pattern.matches("(^-[0-9]*$)|(^[0-9]+$)", args[1])){
						sender.sendMessage(TextFormat.RED + "[ShowInfo] " + args[1] + " is invalid number.");
					}else{
						this.plugin.getSettingConfig().set("PushLevel", Integer.parseInt(args[1]));
						sender.sendMessage(TextFormat.AQUA + "[ShowInfo] Push level is set to " + args[1] + " Level.");
					}
					break;
				case "tick":
					if(args.length <= 1 || args[1].equals("")){
						sender.sendMessage(new TranslationContainer("commands.generic.usage", "/ShowInfo Tick <Tick>"));
					}else if(!Pattern.matches("(^[1-9][0-9]*$)|(^[1-9]+$)", args[1])){
						sender.sendMessage(TextFormat.RED + "[ShowInfo] " + args[1] + " is invalid number.");
					}else{
						this.plugin.getSettingConfig().set("Tick", Integer.parseInt(args[1]));
						this.plugin.setTaskPeriod(Integer.parseInt(args[1]));
						sender.sendMessage(TextFormat.AQUA + "[ShowInfo] Show delay is set to " + args[1] + " Ticks." + TextFormat.DARK_AQUA + "  (1Second = 20Tick)");
					}
					break;
				case "oprank":
					Boolean isOpInRank = this.plugin.getSettingConfig().getBoolean("OpInRank");
					this.plugin.getSettingConfig().set("OpInRank", !isOpInRank);
					sender.sendMessage(TextFormat.AQUA + "[ShowInfo] Now op is " + (isOpInRank ? "not" : "") + " including rank.");
					break;
				case "reload":
					this.plugin.loadData();
					sender.sendMessage(TextFormat.AQUA + "[ShowInfo] Reloaded ShowInfo data.");
					break;
				case "save":
					this.plugin.saveData();
					sender.sendMessage(TextFormat.AQUA + "[ShowInfo] Saved ShowInfo data.");
					break;
				case "reset":
					this.plugin.saveDefaultData(true);
					this.plugin.loadData();
					sender.sendMessage(TextFormat.AQUA + "[ShowInfo] Reseted ShowInfo data.");
					if(this.plugin.isTaskStop()){
						this.plugin.getLogger().info("[ShowInfo] ShowInfo is running!");
					}else{
						this.plugin.taskStop();
					}
					this.plugin.taskStart();
					break;
				default:
					sender.sendMessage(new TranslationContainer("commands.generic.usage", this.getUsage(sender)));
					break;
			}
		}
		return true;
	}

	public String getUsage(CommandSender sender){
		StringBuilder usageBuilder = new StringBuilder("/ShowInfo <");
		Boolean hasUsage = false;
		for(String sub : this.subs){
			if(sender.hasPermission("showinfo.command.showinfo." + sub.toLowerCase())){
				if(hasUsage){
					usageBuilder.append(" | ").append(sub);
				}else{
					hasUsage = true;
					usageBuilder.append(sub);
				}
			}
		}
		return usageBuilder.append(">").toString();
	}
}
