package debe.nukkitplugin.itemdisplay.task;

import java.util.ArrayList;
import java.util.function.Predicate;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.PluginTask;
import debe.nukkitplugin.itemdisplay.ItemDisplay;

public class ItemDisplayTask extends PluginTask<ItemDisplay>{
	public ItemDisplayTask(ItemDisplay owner){
		super(owner);
	}

	@Override
	public void onRun(int currentTick){
		ArrayList<Player> players = new ArrayList<Player>(Server.getInstance().getOnlinePlayers().values());
		this.owner.getImaginaryItems().values().forEach(imaginaryItem->{
			players.stream().filter(((Predicate<Player>) (imaginaryItem::canSpawn)).negate().and(imaginaryItem::isSpawned)).forEach(imaginaryItem::despawnFrom);
			players.stream().filter(((Predicate<Player>) (imaginaryItem::isSpawned)).negate().and(imaginaryItem::canSpawn)).forEach(imaginaryItem::spawnTo);
		});
	}
}
