package debe.nukkitplugin.showinfo.listener;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerCommandPreprocessEvent;
import cn.nukkit.event.server.ServerCommandEvent;
import debe.nukkitplugin.showinfo.ShowInfo;

public class CommandListener implements Listener{
	private ShowInfo plugin;

	public CommandListener(ShowInfo plugin){
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event){
		if(event.getMessage().split(" ")[0].equalsIgnoreCase("/save-all")){
			this.runSaveAll((CommandSender) event.getPlayer());
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onServerCommandEvent(ServerCommandEvent event){
		if(event.getCommand().split(" ")[0].equalsIgnoreCase("save-all")){
			this.runSaveAll((CommandSender) event.getSender());
		}
	}

	public void runSaveAll(CommandSender sender){
		Command command = this.plugin.getServer().getCommandMap().getCommand("save-all");
		if(command != null && command.testPermissionSilent(sender)){
			this.plugin.getLogger().notice("Saved the ShowInfo data");
			this.plugin.saveData();
		}
	}
}
