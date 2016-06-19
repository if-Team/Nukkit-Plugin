package debe.nukkitplugin.itemdisplay.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import debe.nukkitplugin.itemdisplay.ItemDisplay;
import debe.nukkitplugin.itemdisplay.task.touchtask.AddTouchTask;
import debe.nukkitplugin.itemdisplay.task.touchtask.TouchTask;

public class BlockTouchListener implements Listener{
	public BlockTouchListener(){}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPlayerInteract(PlayerInteractEvent event){
		if(event.getAction() == PlayerInteractEvent.RIGHT_CLICK_BLOCK){
			TouchTask touchTask = ItemDisplay.getInstance().getTouchTask(event.getPlayer());
			if(touchTask instanceof AddTouchTask){
				((AddTouchTask) touchTask).onTouch(event.getBlock(), event.getFace());
				event.setCancelled();
			}
		}
	}
}
