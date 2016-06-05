package debe.nukkitplugin.itemownership.entity;

import cn.nukkit.Player;
import cn.nukkit.entity.item.EntityItem;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import debe.nukkitplugin.itemownership.ItemOwnership;
import debe.nukkitplugin.itemownership.utils.Utils;

public class EntityOwnedItem extends EntityItem{
	public Player owner;

	public EntityOwnedItem(FullChunk chunk, CompoundTag nbt, Player owner){
		super(chunk, nbt);
		this.owner = owner;
	}

	public boolean isOwner(Player player){
		return this.owner.getName().equalsIgnoreCase(player.getName());
	}

	public boolean canPickUp(Player player){
		return this.isOwner(player) || player.hasPermission("itemownership.event.pickupitem") || this.age > Utils.toInt(ItemOwnership.getInstance().getSetting().get("Span").toString());
	}

	public Player getOwnerPlayer(){
		return this.owner;
	}

	public int getAge(){
		return this.age;
	}
}
