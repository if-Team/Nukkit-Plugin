package debe.nukkitplugin.showinfo.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import debe.nukkitplugin.showinfo.ShowInfo;
import debe.nukkitplugin.showinfo.command.subcommand.PlayerSubCommand;
import debe.nukkitplugin.showinfo.command.subcommand.SubCommand;
import debe.nukkitplugin.showinfo.utils.Translation;
import debe.nukkitplugin.showinfo.utils.Utils;

public class ShowInfoCommand extends Command{
	private LinkedHashMap<String, SubCommand> subCommands = new LinkedHashMap<String, SubCommand>();

	public ShowInfoCommand(String name, String[] aliases, Map<String, String> subCommands){
		super(name);
		this.setPermission("showinfo.command.showinfo");
		this.setAliases(aliases);
		this.registerSubCommands(subCommands);
		this.description = this.getUsage();
		this.usageMessage = this.getUsage();
	}

	@Override
	public boolean execute(CommandSender sender, String commandLabel, String[] args){
		if(!this.testPermission(sender)){
			return true;
		}else if(args.length == 0){
			sender.sendMessage(Translation.translate("commands.generic.usage", this.getUsage(sender)));
			return true;
		}else{
			SubCommand subCommand = this.getSubCommand(args[0]);
			if(subCommand == null){
				sender.sendMessage(Translation.translate("commands.generic.notFound", this.getUsage(sender)));
			}else if(!subCommand.hasPermission(sender)){
				sender.sendMessage(Translation.translate("commands.generic.permission"));
			}else{
				subCommand.run(sender, Arrays.copyOfRange(args, 1, args.length));
			}
		}
		return true;
	}

	@Override
	public String getUsage(){
		return "/" + this.getLabel() + " <" + String.join(" | ", this.subCommands.values().stream().map(SubCommand::getName).collect(Collectors.toList())) + ">";
	}

	public String getUsage(CommandSender sender){
		return "/" + this.getLabel() + " <" + String.join(" | ", this.subCommands.values().stream().filter(subCommand->subCommand.hasPermission(sender)).map(SubCommand::getName).collect(Collectors.toList())) + ">";
	}

	public void registerSubCommands(Map<String, String> subCommands){
		ShowInfo plugin = ShowInfo.getInstance();
		this.registerSubCommand(new PlayerSubCommand(this, subCommands.getOrDefault("on", "On"), "showinfo.command.showinfo.on"){
			public void execute(CommandSender sender, String[] args){
				if(plugin.getData().containsKey((sender.getName()))){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.on.failed"));
				}else{
					plugin.getData().remove(sender.getName().toLowerCase());
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.on.success"));
				}
			}
		});
		this.registerSubCommand(new PlayerSubCommand(this, subCommands.getOrDefault("off", "Off"), "showinfo.command.showinfo.off"){
			public void execute(CommandSender sender, String[] args){
				if(!plugin.getData().containsKey(sender.getName())){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.off.failed"));
				}else{
					plugin.getData().put(sender.getName().toLowerCase(), true);
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.off.success"));
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("enable", "Enable"), "showinfo.command.showinfo.enable"){
			public void execute(CommandSender sender, String[] args){
				if(!plugin.isTaskStop()){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.enable.failed"));
				}else{
					plugin.getSetting().put("Enable", true);
					plugin.taskStart();
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.enable.success"));
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("disable", "Disable"), "showinfo.command.showinfo.disable"){
			public void execute(CommandSender sender, String[] args){
				if(plugin.isTaskStop()){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.disable.failed"));
				}else{
					plugin.getSetting().put("Enable", false);
					plugin.taskStop();
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.disable.success"));
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("push", "Push"), "showinfo.command.showinfo.push", Translation.translate("commands.push.usage"), 1){
			public void execute(CommandSender sender, String[] args){
				if(!Pattern.matches("(^-[0-9]*$)|(^[0-9]+$)", args[0])){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.generic.invalidNumber", args[0]));
				}else{
					int pushLevel = Utils.toInt(args[0]);
					plugin.getSetting().put("PushLevel", pushLevel);
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.push.success", String.valueOf(pushLevel)));
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("period", "Period"), "showinfo.command.showinfo.period", Translation.translate("commands.period.usage"), 1){
			public void execute(CommandSender sender, String[] args){
				if(!Pattern.matches("(^[1-9][0-9]*$)|(^[1-9]+$)", args[0])){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.generic.invalidNumber", args[0]));
				}else{
					int period = Utils.toInt(args[0]);
					plugin.getSetting().put("Period", period);
					if(Utils.toBoolean(plugin.getSetting().get("Enable").toString())){
						plugin.taskStart();
					}
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.period.success", String.valueOf(period)));
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("reload", "Reload"), "showinfo.command.showinfo.reload"){
			public void execute(CommandSender sender, String[] args){
				new File(plugin.getDataFolder() + "/lang").mkdirs();
				plugin.saveDefaultData(false);
				plugin.loadData();
				Translation.load(plugin.getSetting().get("Language").toString().trim());
				plugin.updatePermissions();
				plugin.registerCommands();
				sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.reload.success"));
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("save", "Save"), "showinfo.command.showinfo.save"){
			public void execute(CommandSender sender, String[] args){
				plugin.saveData();
				sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.save.success"));
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("reset", "Reset"), "showinfo.command.showinfo.reset"){
			public void execute(CommandSender sender, String[] args){
				plugin.saveDefaultData(true);
				plugin.loadData();
				if(Utils.toBoolean(plugin.getSetting().get("Enable").toString())){
					plugin.taskStart();
				}
				sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.reset.success"));
			}
		});
	}

	public void registerSubCommand(SubCommand subCommand){
		this.subCommands.put(subCommand.getName().toLowerCase(), subCommand);
	}

	public SubCommand getSubCommand(String name){
		return this.subCommands.get(name.toLowerCase());
	}

	public ArrayList<SubCommand> getSubCommands(){
		return new ArrayList<SubCommand>(this.subCommands.values());
	}
}
