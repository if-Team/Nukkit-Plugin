package debe.nukkitplugin.itemdisplay.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.math.Vector3;
import cn.nukkit.network.protocol.AddEntityPacket;
import cn.nukkit.network.protocol.AddItemEntityPacket;
import cn.nukkit.network.protocol.RemoveEntityPacket;
import cn.nukkit.network.protocol.SetEntityLinkPacket;
import debe.nukkitplugin.itemdisplay.ItemDisplay;
import debe.nukkitplugin.itemdisplay.utils.Translation;
import debe.nukkitplugin.itemdisplay.utils.Utils;

public class VirtualItem extends Vector3{
	protected Map<Integer, Player> viewers = new HashMap<Integer, Player>();
	protected String name;
	protected Item item;
	protected long id;
	protected long riderId;
	private String levelName;

	public static VirtualItem fromString(String name, String levelName, String data){
		String[] params = data.split(":");
		if(params.length == 6){
			Item item = Item.get(Utils.toInt(params[0]), Utils.toInt(params[1]));
			if(Utils.toBoolean(params[2])){
				item.addEnchantment(Enchantment.get(Enchantment.ID_DURABILITY));
			}
			return new VirtualItem(name, item, Utils.toDouble(params[3]), Utils.toDouble(params[4]), Utils.toDouble(params[5]), levelName);
		}
		return null;
	}

	public static void spawnAllTo(Player player){
		ItemDisplay.getInstance().getVirtualItems().values().forEach(virtualItem->virtualItem.spawnTo(player));
	}

	public static void spawnAllToAll(){
		Server.getInstance().getOnlinePlayers().values().forEach(VirtualItem::spawnAllTo);
	}

	public static void despawnAllFrom(Player player){
		ItemDisplay.getInstance().getVirtualItems().values().forEach(virtualItem->virtualItem.despawnFrom(player));
	}

	public static void despawnAllFromAll(){
		Server.getInstance().getOnlinePlayers().values().forEach(VirtualItem::despawnAllFrom);
	}

	public static void respawnAllTo(Player player){
		VirtualItem.despawnAllFrom(player);
		VirtualItem.spawnAllTo(player);
	}

	public static void respawnAllToAll(){
		Server.getInstance().getOnlinePlayers().values().forEach(VirtualItem::respawnAllTo);
	}

	public VirtualItem(String name, Item item, double x, double y, double z, String levelName){
		super(x, y, z);
		this.name = name;
		this.item = item;
		this.levelName = levelName;
		this.id = Entity.entityCount++;
		this.riderId = Entity.entityCount++;
	}

	public ArrayList<Player> getViewers(){
		return new ArrayList<Player>(this.viewers.values());
	}

	public boolean canSee(Player player){
		return this.levelName.equals(player.level.getFolderName());
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
		if(!this.isSpawned(player) && this.canSee(player)){
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

	public void spawnToAll(){
		Server.getInstance().getOnlinePlayers().values().forEach(this::spawnTo);
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

	public void respawnTo(Player player){
		this.despawnFrom(player);
		this.spawnTo(player);
	}

	public void respawnFromAll(){
		this.viewers.values().forEach(this::respawnTo);
	}

	public String getName(){
		return this.name;
	}

	public void setName(String name){
		this.name = name;
	}

	public Item getItem(){
		return this.item;
	}

	public void setItem(Item item){
		this.item = item;
	}

	@Override
	public String toString(){
		return String.join(":", String.valueOf(this.item.getId()), String.valueOf(this.item.getDamage()), String.valueOf(this.item.hasEnchantments()), String.valueOf(this.x), String.valueOf(this.y), String.valueOf(this.z));
	}

	public String getLevelName(){
		return this.levelName;
	}
}
