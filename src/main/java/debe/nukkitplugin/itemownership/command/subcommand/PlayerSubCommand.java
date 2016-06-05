package debe.nukkitplugin.itemownership.command.subcommand;

import cn.nukkit.Player;
import cn.nukkit.command.CommandSender;
import debe.nukkitplugin.itemownership.command.ItemOwnershipCommand;
import debe.nukkitplugin.itemownership.utils.Translation;

public abstract class PlayerSubCommand extends SubCommand{
	public PlayerSubCommand(){}

	public PlayerSubCommand(ItemOwnershipCommand mainCommand, String name, String permission){
		super(mainCommand, name, permission);
	}

	public PlayerSubCommand(ItemOwnershipCommand mainCommand, String name, String permission, String usage, int needArgCount){
		super(mainCommand, name, permission, usage, needArgCount);
	}

	public void run(CommandSender sender, String[] args){
		if(!(sender instanceof Player)){
			sender.sendMessage(Translation.translate("colors.failed") + Translation.translate("prefix") + " " + Translation.translate("commands.generic.ingame"));
		}else{
			super.run(sender, args);
		}
	}
}
