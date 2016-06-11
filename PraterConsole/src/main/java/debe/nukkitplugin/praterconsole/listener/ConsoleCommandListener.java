package debe.nukkitplugin.praterconsole.listener;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.server.ServerCommandEvent;
import debe.nukkitplugin.praterconsole.PraterConsole;

public class ConsoleCommandListener implements Listener{
	public ConsoleCommandListener(){}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onServerCommandEvent(ServerCommandEvent event){
		String command = event.getCommand();
		if(command.startsWith(".")){
			event.setCancelled();
			Server.getInstance().broadcastMessage(PraterConsole.getFormat().replace("{%0}", command.substring(1, command.length())));
		}
	}
}
