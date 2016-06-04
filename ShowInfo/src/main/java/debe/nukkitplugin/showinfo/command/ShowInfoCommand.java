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
		this.registerSubCommand(new PlayerSubCommand(this, subCommands.getOrDefault("on", "On"), "showinfo.command.showinfo.on"){
			public void execute(CommandSender sender, String[] args){
				if(ShowInfo.getInstance().getData().containsKey((sender.getName()))){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.on.failed"));
				}else{
					ShowInfo.getInstance().getData().remove(sender.getName().toLowerCase());
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.on.success"));
				}
			}
		});
		this.registerSubCommand(new PlayerSubCommand(this, subCommands.getOrDefault("off", "Off"), "showinfo.command.showinfo.off"){
			public void execute(CommandSender sender, String[] args){
				if(!ShowInfo.getInstance().getData().containsKey(sender.getName())){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.off.failed"));
				}else{
					ShowInfo.getInstance().getData().put(sender.getName().toLowerCase(), true);
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.off.success"));
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("start", "Start"), "showinfo.command.showinfo.start"){
			public void execute(CommandSender sender, String[] args){
				if(!ShowInfo.getInstance().isTaskStop()){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.start.failed"));
				}else{
					ShowInfo.getInstance().getSetting().put("Enable", true);
					ShowInfo.getInstance().taskStart();
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.start.success"));
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("stop", "Stop"), "showinfo.command.showinfo.stop"){
			public void execute(CommandSender sender, String[] args){
				if(ShowInfo.getInstance().isTaskStop()){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.stop.failed"));
				}else{
					ShowInfo.getInstance().getSetting().put("Enable", false);
					ShowInfo.getInstance().taskStop();
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.stop.success"));
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("push", "Push"), "showinfo.command.showinfo.push", Translation.translate("commands.push.usage"), 1){
			public void execute(CommandSender sender, String[] args){
				if(!Pattern.matches("(^-[0-9]*$)|(^[0-9]+$)", args[0])){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.generic.invalidNumber", args[0]));
				}else{
					ShowInfo.getInstance().getSetting().put("PushLevel", Integer.parseInt(args[0]));
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.push.success", args[0]));
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("tick", "Tick"), "showinfo.command.showinfo.tick", Translation.translate("commands.tick.usage"), 1){
			public void execute(CommandSender sender, String[] args){
				if(!Pattern.matches("(^[1-9][0-9]*$)|(^[1-9]+$)", args[0])){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.generic.invalidNumber", args[0]));
				}else{
					ShowInfo.getInstance().getSetting().put("Tick", Integer.parseInt(args[0]));
					ShowInfo.getInstance().setTaskPeriod(Integer.parseInt(args[0]));
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.tick.success", args[0]));
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("reload", "Reload"), "showinfo.command.showinfo.reload"){
			public void execute(CommandSender sender, String[] args){
				new File(ShowInfo.getInstance().getDataFolder() + "/lang").mkdirs();
				ShowInfo.getInstance().saveDefaultData(false);
				ShowInfo.getInstance().loadData();
				Translation.load(ShowInfo.getInstance().getSetting().get("Language").toString().trim());
				ShowInfo.getInstance().updatePermissions();
				ShowInfo.getInstance().registerCommands();
				sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.push.success"));
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("save", "Save"), "showinfo.command.showinfo.save"){
			public void execute(CommandSender sender, String[] args){
				ShowInfo.getInstance().saveData();
				sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.save.success"));
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.getOrDefault("reset", "Reset"), "showinfo.command.showinfo.reset"){
			public void execute(CommandSender sender, String[] args){
				ShowInfo.getInstance().saveDefaultData(true);
				ShowInfo.getInstance().loadData();
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
