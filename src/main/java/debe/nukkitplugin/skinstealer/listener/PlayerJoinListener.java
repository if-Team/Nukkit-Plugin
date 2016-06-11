package debe.nukkitplugin.skinstealer.listener;

import cn.nukkit.Server;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import debe.nukkitplugin.skinstealer.SkinStealer;
import debe.nukkitplugin.skinstealer.task.SkinStealAsyncTask;

public class PlayerJoinListener implements Listener{
	protected SkinStealer plugin;

	public PlayerJoinListener(SkinStealer plugin){
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerJoin(PlayerJoinEvent event){
		Server.getInstance().getScheduler().scheduleAsyncTask(new SkinStealAsyncTask(this.plugin, event.getPlayer()));
	}
}
