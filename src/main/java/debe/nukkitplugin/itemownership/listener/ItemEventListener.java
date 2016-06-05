package debe.nukkitplugin.itemownership.listener;

import java.util.Arrays;
import java.util.Random;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.block.BlockBreakEvent;
import cn.nukkit.event.inventory.InventoryPickupItemEvent;
import cn.nukkit.item.Item;
import cn.nukkit.nbt.NBTIO;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import debe.nukkitplugin.itemownership.ItemOwnership;
import debe.nukkitplugin.itemownership.entity.EntityOwnedItem;
import debe.nukkitplugin.itemownership.utils.Utils;

public class ItemEventListener implements Listener{
	public ItemEventListener(){}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInventoryPickupItem(InventoryPickupItemEvent event){
		if(event.getItem() instanceof EntityOwnedItem && event.getInventory().getHolder() instanceof Player && !((EntityOwnedItem) event.getItem()).canPickUp((Player) event.getInventory().getHolder())){
			event.setCancelled();
		}
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onBlockBreak(BlockBreakEvent event){
		Player player = event.getPlayer();
		if(player.isSurvival() && !(event.getInstaBreak() && event.isFastBreak()) && !ItemOwnership.getInstance().getData().containsKey(player.getName().toLowerCase()) && Utils.toBoolean(ItemOwnership.getInstance().getSetting().get("Enable").toString())){
			Block block = event.getBlock();
			Arrays.asList(event.getDrops()).forEach(item->new EntityOwnedItem(block.level.getChunk((int) block.getX() >> 4, (int) block.getZ() >> 4, true), new CompoundTag(){
				{
					putList(new ListTag<DoubleTag>("Pos"){
						{
							add(new DoubleTag("", block.getX() + 0.5));
							add(new DoubleTag("", block.getY() + 0.5));
							add(new DoubleTag("", block.getZ() + 0.5));
						}
					});
					putList(new ListTag<DoubleTag>("Motion"){
						{
							add(new DoubleTag("", new Random().nextDouble() * 0.2 - 0.1));
							add(new DoubleTag("", 0.2));
							add(new DoubleTag("", new Random().nextDouble() * 0.2 - 0.1));
						}
					});
					putList(new ListTag<FloatTag>("Rotation"){
						{
							add(new FloatTag("", new Random().nextFloat() * 360));
							add(new FloatTag("", 0));
						}
					});
					putShort("Health", 5);
					putCompound("Item", (CompoundTag) NBTIO.putItemHelper(item).setName("Item"));
					putShort("PickupDelay", 10);
				}
			}, player).spawnToAll());
			event.setDrops(new Item[0]);
		}
	}
}
