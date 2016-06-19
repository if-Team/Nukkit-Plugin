package debe.nukkitplugin.itemdisplay.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import debe.nukkitplugin.itemdisplay.ItemDisplay;
import debe.nukkitplugin.itemdisplay.command.subcommand.PlayerSubCommand;
import debe.nukkitplugin.itemdisplay.command.subcommand.SubCommand;
import debe.nukkitplugin.itemdisplay.command.subcommand.SubCommandData;
import debe.nukkitplugin.itemdisplay.entity.VirtualItem;
import debe.nukkitplugin.itemdisplay.task.touchtask.AddTouchTask;
import debe.nukkitplugin.itemdisplay.utils.FileUtils;
import debe.nukkitplugin.itemdisplay.utils.Translation;
import debe.nukkitplugin.itemdisplay.utils.Utils;

public class ItemDisplayCommand extends Command{
	private LinkedHashMap<String, SubCommand> subCommands = new LinkedHashMap<String, SubCommand>();

	public ItemDisplayCommand(String name, String[] aliases, Map<String, SubCommandData> subCommands){
		super(name);
		this.setPermission("itemdisplay.command.itemdisplay");
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

	public void registerSubCommands(Map<String, SubCommandData> subCommands){
		final ItemDisplay plugin = ItemDisplay.getInstance();
		this.registerSubCommand(new PlayerSubCommand(this, subCommands.get("add"), "itemdisplay.command.itemdisplay.add", Translation.translate("commands.add.usage"), 2){
			@Override
			public void execute(Player player, String[] args){
				String itemName = args[0].toLowerCase();
				if(plugin.exsisVirtualItem(itemName)){
					player.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.add.failed.alreadyExists", args[0]));
				}else{
					Item item = args[1].equals("*") ? player.getInventory().getItemInHand() : Item.fromString(args[1]);
					if(!(item instanceof Item) || item.getId() == 0){
						player.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.add.failed.invalidItemID", args[0]));
					}else{
						if(args.length >= 3 && Utils.toBoolean(args[2])){
							item.addEnchantment(new Enchantment[]{Enchantment.get(Enchantment.ID_DURABILITY)});
						}
						plugin.addTouchTask(new AddTouchTask(player, itemName, item));
						player.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.add.success", new String[]{itemName, String.valueOf(item.getId()), String.valueOf(item.getDamage())}));
					}
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.get("remove"), "itemdisplay.command.itemdisplay.remove", Translation.translate("commands.remove.usage"), 1){
			@Override
			public void execute(CommandSender sender, String[] args){
				String itemName = args[0].toLowerCase();
				if(!plugin.exsisVirtualItem(itemName)){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.remove.failed.notFound", args[0]));
				}else{
					plugin.removeVirtualItem(itemName);
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.remove.success", itemName));
				}
			}
		});
		this.registerSubCommand(new PlayerSubCommand(this, subCommands.get("cancel"), "itemdisplay.command.itemdisplay.cancel"){
			@Override
			public void execute(Player player, String[] args){
				if(!plugin.existTouchTask(player)){
					player.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.cancel.success"));
				}else{
					plugin.removeTouchTask(plugin.getTouchTask(player));
					player.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.cancel.success"));
				}
			}
		});
		this.registerSubCommand(new PlayerSubCommand(this, subCommands.get("view"), "itemdisplay.command.itemdisplay.view"){
			@Override
			public void execute(Player player, String[] args){
				if(plugin.isNametagViewer(player)){
					plugin.removeNametagViewer(player);
					player.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.view.disable"));
				}else{
					plugin.addNametagViewer(player);
					player.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.view.enable"));
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.get("list"), "itemdisplay.command.itemdisplay.span", Translation.translate("commands.list.usage"), 0){
			@Override
			public void execute(CommandSender sender, String[] args){
				ArrayList<VirtualItem> virtualItems = new ArrayList<VirtualItem>(plugin.getVirtualItems().values());
				int itemCount = virtualItems.size();
				if(itemCount == 0){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.list.failed.empty"));
				}else if(args.length >= 1 && !Utils.isInt(args[0])){
					sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.list.failed.invalidPage", args[0]));
				}else{
					int maxPage = itemCount / 5;
					int page = Math.min(args.length >= 1 ? Utils.toInt(args[0]) - 1 : 0, maxPage);
					sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.list.success.title", new String[]{String.valueOf(page + 1), String.valueOf(maxPage + 1), String.valueOf(itemCount)}));
					for(int index = page * 5; index < page * 5 + 5 && index < itemCount; index++){
						VirtualItem virtualItem = virtualItems.get(index);
						sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.list.success.entry", new String[]{String.valueOf(index + 1), virtualItem.getName(), String.valueOf(virtualItem.getItem().getId()) + ":" + virtualItem.getItem().getDamage(), String.valueOf(virtualItem.x), String.valueOf(virtualItem.y), String.valueOf(virtualItem.z), virtualItem.getLevelName()}));
					}
				}
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.get("reload"), "itemdisplay.command.itemdisplay.reload"){
			@Override
			public void execute(CommandSender sender, String[] args){
				VirtualItem.despawnAllFromAll();
				plugin.loadAll();
				Server.getInstance().getLevels().values().forEach(FileUtils::loadData);
				sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.reload.success"));
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.get("save"), "itemdisplay.command.itemdisplay.save"){
			@Override
			public void execute(CommandSender sender, String[] args){
				plugin.saveAll();
				sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.save.success"));
			}
		});
		this.registerSubCommand(new SubCommand(this, subCommands.get("reset"), "itemdisplay.command.itemdisplay.reset"){
			@Override
			public void execute(CommandSender sender, String[] args){
				VirtualItem.despawnAllFromAll();
				plugin.setVirtualItems(new HashMap<String, VirtualItem>());
				plugin.saveAll();
				plugin.saveDefaultData(true);
				plugin.loadAll();
				sender.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("commands.reset.success"));
			}
		});
	}

	public void registerSubCommand(SubCommand subCommand){
		this.subCommands.put(subCommand.getName().toLowerCase(), subCommand);
	}

	public SubCommand getSubCommand(String command){
		for(SubCommand subCommand : this.subCommands.values()){
			if(subCommand.getData().equals(command)){
				return subCommand;
			}
		}
		return null;
	}

	public ArrayList<SubCommand> getSubCommands(){
		return new ArrayList<SubCommand>(this.subCommands.values());
	}
}
