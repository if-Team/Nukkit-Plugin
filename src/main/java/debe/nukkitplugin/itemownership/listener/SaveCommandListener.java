package debe.nukkitplugin.itemownership.listener;

import cn.nukkit.Server;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.server.ServerCommandEvent;
import debe.nukkitplugin.itemownership.ItemOwnership;
import debe.nukkitplugin.itemownership.utils.Translation;

public class SaveCommandListener implements Listener{
	public SaveCommandListener(){}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		if(event.getMessage().toLowerCase().startsWith("/save-all")){
			this.saveAll((CommandSender) event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onServerCommandEvent(ServerCommandEvent event){
		if(event.getCommand().toLowerCase().startsWith("save-all")){
			this.saveAll((CommandSender) event.getSender());
		}
	}

	public void saveAll(CommandSender sender){
		Command command = Server.getInstance().getCommandMap().getCommand("save-all");
		if(command != null && command.testPermissionSilent(sender)){
			ItemOwnership.getInstance().saveData();
			ItemOwnership.getInstance().getLogger().notice(Translation.translate("colors.success") + Translation.translate("commands.save.success"));
		}
	}
}
