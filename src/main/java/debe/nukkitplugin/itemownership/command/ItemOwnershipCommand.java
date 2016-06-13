package debe.nukkitplugin.itemownership.command;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import debe.nukkitplugin.itemownership.ItemOwnership;
import debe.nukkitplugin.itemownership.command.subcommand.PlayerSubCommand;
import debe.nukkitplugin.itemownership.command.subcommand.SubCommand;
import debe.nukkitplugin.itemownership.utils.Translation;
import debe.nukkitplugin.itemownership.utils.Utils;

public class ItemOwnershipCommand extends Command{
	private LinkedHashMap<String, SubCommand> subCommands = new LinkedHashMap<String, SubCommand>();

	public ItemOwnershipCommand(String name, String[] aliases, Map<String, String> subCommands){
		super(name);
		this.setPermission("itemownership.command.itemownership");
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
		this.registerSubCommand(new PlayerSubCommand(this, subCommands.getOrDefault("on", "On"), "itemownership.command.itemownership.on"){
			public void execute(CommandSender sender, String[] args){
				if(ItemOwnership.getInstance().getData().containsKey((sender.getName()))){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.on.failed"));
				}else{
					ItemOwnership.getInstance().getData().remove(sender.getName().toLowerCase());
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.on.success"));
				}
			}
		});
		this.registerSubCommand(new PlayerSubCommand(this, subCommands.getOrDefault("off", "Off"), "itemownership.command.itemownership.off"){
			public void execute(CommandSender sender, String[] args){
				if(!ItemOwnership.getInstance().getData().containsKey(sender.getName())){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.off.failed"));
				}else{
					ItemOwnership.getInstance().getData().put(sender.getName().toLowerCase(), true);
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.off.success"));
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("enable", "Enable"), "itemownership.command.itemownership.enable"){
			public void execute(CommandSender sender, String[] args){
				if(Utils.toBoolean(ItemOwnership.getInstance().getSetting().get("Enable").toString())){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.enable.failed"));
				}else{
					ItemOwnership.getInstance().getSetting().put("Enable", true);
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.enable.success"));
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("Disable", "Disable"), "itemownership.command.itemownership.disable"){
			public void execute(CommandSender sender, String[] args){
				if(!Utils.toBoolean(ItemOwnership.getInstance().getSetting().get("Enable").toString())){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.disable.failed"));
				}else{
					ItemOwnership.getInstance().getSetting().put("Enable", false);
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.disable.success"));
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("distance", "Distance"), "itemownership.command.itemownership.distance", Translation.translate("commands.span.usage"), 1){
			public void execute(CommandSender sender, String[] args){
				if(!Pattern.matches("(^[1-9][0-9]*$)|(^[1-9]+$)", args[0])){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.generic.invalidNumber", args[0]));
				}else{
					ItemOwnership.getInstance().getSetting().put("Distance", Integer.parseInt(args[0]));
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.distance.success", args[0]));
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("span", "Span"), "itemownership.command.itemownership.span", Translation.translate("commands.span.usage"), 1){
			public void execute(CommandSender sender, String[] args){
				if(!Pattern.matches("(^[1-9][0-9]*$)|(^[1-9]+$)", args[0])){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.generic.invalidNumber", args[0]));
				}else{
					ItemOwnership.getInstance().getSetting().put("Span", Integer.parseInt(args[0]));
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.span.success", args[0]));
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("reload", "Reload"), "itemownership.command.itemownership.reload"){
			public void execute(CommandSender sender, String[] args){
				new File(ItemOwnership.getInstance().getDataFolder() + "/lang").mkdirs();
				ItemOwnership.getInstance().saveDefaultData(false);
				ItemOwnership.getInstance().loadData();
				Translation.load(ItemOwnership.getInstance().getSetting().get("Language").toString().trim());
				ItemOwnership.getInstance().updatePermissions();
				ItemOwnership.getInstance().registerCommands();
				sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.reload.success"));
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("save", "Save"), "itemownership.command.itemownership.save"){
			public void execute(CommandSender sender, String[] args){
				ItemOwnership.getInstance().saveData();
				sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.save.success"));
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("reset", "Reset"), "itemownership.command.itemownership.reset"){
			public void execute(CommandSender sender, String[] args){
				ItemOwnership.getInstance().saveDefaultData(true);
				ItemOwnership.getInstance().loadData();
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
