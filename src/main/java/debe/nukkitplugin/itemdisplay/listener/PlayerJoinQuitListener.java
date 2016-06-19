package debe.nukkitplugin.itemdisplay.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.event.player.PlayerRespawnEvent;
import cn.nukkit.event.player.PlayerTeleportEvent;
import debe.nukkitplugin.itemdisplay.entity.VirtualItem;

public class PlayerJoinQuitListener implements Listener{
	public PlayerJoinQuitListener(){}

	@EventHandler()
	public void onPlayerRespawn(PlayerRespawnEvent event){
		VirtualItem.respawnAllTo(event.getPlayer());
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerTeleportEvent(PlayerTeleportEvent event){
		VirtualItem.respawnAllTo(event.getPlayer());
	}

	@EventHandler()
	public void onPlayerQuit(PlayerQuitEvent event){
		VirtualItem.despawnAllFrom(event.getPlayer());
	}
}
