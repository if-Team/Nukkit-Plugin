package debe.nukkitplugin.itemdisplay.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.AddItemEntityPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import debe.nukkitplugin.itemdisplay.ItemDisplay;
import debe.nukkitplugin.itemdisplay.utils.Translation;

public class ImaginaryItem extends Position{
	protected Map<Integer, Player> viewers = new HashMap<Integer, Player>();
	protected String name;
	protected Item item;
	protected long id;
	protected long riderId;

	public ImaginaryItem(String name, Item item, double x, double y, double z, Level level){
		super(x, y, z, level);
		this.setName(name);
		this.setItem(item);
		this.id = Entity.entityCount++;
		this.riderId = Entity.entityCount++;
	}

	public ArrayList<Player> getViewers(){
		return new ArrayList<Player>(this.viewers.values());
	}

	public boolean canSpawn(Player player){
		return this.level.equals(player.level) && player.usedChunks.containsKey(Level.chunkHash((int) this.x >> 4, (int) this.z >> 4));
	}

	public boolean isSpawned(Player player){
		if(this.viewers.containsKey(player.getLoaderId())){
			if(player.isAlive() && player.spawned){
				return true;
			}else{
				this.viewers.remove(player.getLoaderId());
			}
		}
		return false;
	}

	public void spawnTo(Player player){
		if(!this.isSpawned(player) && this.canSpawn(player)){
			this.viewers.put(player.getLoaderId(), player);
			AddItemEntityPacket addItemEntityPk = new AddItemEntityPacket();
			addItemEntityPk.eid = this.id;
			addItemEntityPk.x = (float) this.x + 0.5f;
			addItemEntityPk.y = (float) this.y;
			addItemEntityPk.z = (float) this.z + 0.5f;
			addItemEntityPk.speedX = 0;
			addItemEntityPk.speedY = 0;
			addItemEntityPk.speedZ = 0;
			addItemEntityPk.item = this.getItem();
			AddEntityPacket addEntityPk = new AddEntityPacket();
			addEntityPk.type = 69;
			addEntityPk.eid = this.riderId;
			addEntityPk.x = addItemEntityPk.x;
			addEntityPk.y = addItemEntityPk.y;
			addEntityPk.z = addItemEntityPk.z;
			addEntityPk.speedX = 0;
			addEntityPk.speedY = 0;
			addEntityPk.speedZ = 0;
			EntityMetadata metadata = new EntityMetadata();
			metadata.put(new ByteEntityData(Entity.DATA_NO_AI, 1));
			if(ItemDisplay.getInstance().isNametagViewer(player)){
				metadata.put(new ByteEntityData(Entity.DATA_SHOW_NAMETAG, 1));
				metadata.put(new StringEntityData(Entity.DATA_NAMETAG, Translation.translate("showNametag.toViewer", new String[]{this.name, String.valueOf(this.item.getId()), String.valueOf(this.item.getDamage())})));
			}
			addEntityPk.metadata = metadata;
			SetEntityLinkPacket setEntityLinkPk = new SetEntityLinkPacket();
			setEntityLinkPk.rider = this.riderId;
			setEntityLinkPk.riding = this.id;
			setEntityLinkPk.type = 1;
			player.dataPacket(addItemEntityPk);
			player.dataPacket(addEntityPk);
			player.dataPacket(setEntityLinkPk);
		}
	}

	public void despawnFrom(Player player){
		if(this.isSpawned(player)){
			this.viewers.remove(player.getLoaderId());
			RemoveEntityPacket removeEntityPk = new RemoveEntityPacket();
			removeEntityPk.eid = this.id;
			RemoveEntityPacket removeRiderEntityPk = new RemoveEntityPacket();
			removeRiderEntityPk.eid = this.riderId;
			player.dataPacket(removeEntityPk);
			player.dataPacket(removeRiderEntityPk);
		}
	}

	public void despawnFromAll(){
		this.viewers.values().forEach(this::despawnFrom);
	}

	public void respawnFrom(Player player){
		this.despawnFrom(player);
		this.spawnTo(player);
	}

	public void respawnFromAll(){
		this.viewers.values().forEach(this::respawnFrom);
	}

	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public Item getItem(){
		return item;
	}

	public void setItem(Item item){
		this.item = item;
	}
}
