package debe.nukkitplugin.autogarbagecollector.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import debe.nukkitplugin.autogarbagecollector.AutoGarbageCollector;
import debe.nukkitplugin.autogarbagecollector.command.subcommand.SubCommand;
import debe.nukkitplugin.autogarbagecollector.utils.Translation;
import debe.nukkitplugin.autogarbagecollector.utils.Utils;

public class AutoGarbageCollectorCommand extends Command{
	private LinkedHashMap<String, SubCommand> subCommands = new LinkedHashMap<String, SubCommand>();

	public AutoGarbageCollectorCommand(String name, String[] aliases, Map<String, String> subCommands){
		super(name);
		this.setPermission("autogarbagecollector.command.autogarbagecollector");
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
		AutoGarbageCollector plugin = AutoGarbageCollector.getInstance();
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("enable", "Enable"), "autogarbagecollector.command.autogarbagecollector.enable"){
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
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("disable", "Disable"), "autogarbagecollector.command.autogarbagecollector.disable"){
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
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("period", "Period"), "autogarbagecollector.command.autogarbagecollector.period", Translation.translate("commands.period.usage"), 1){
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
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("reload", "Reload"), "autogarbagecollector.command.autogarbagecollector.reload"){
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
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("save", "Save"), "autogarbagecollector.command.autogarbagecollector.save"){
			public void execute(CommandSender sender, String[] args){
				plugin.saveData();
				sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.save.success"));
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("reset", "Reset"), "autogarbagecollector.command.autogarbagecollector.reset"){
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
