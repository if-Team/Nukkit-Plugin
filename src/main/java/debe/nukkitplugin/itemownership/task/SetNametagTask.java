package debe.nukkitplugin.itemownership.task;

import java.util.Arrays;

import cn.nukkit.Server;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.data.ByteEntityData;
import cn.nukkit.entity.data.EntityMetadata;
import cn.nukkit.entity.data.StringEntityData;
import cn.nukkit.scheduler.PluginTask;
import debe.nukkitplugin.itemownership.ItemOwnership;
import debe.nukkitplugin.itemownership.entity.EntityOwnedItem;
import debe.nukkitplugin.itemownership.utils.Translation;
import debe.nukkitplugin.itemownership.utils.Utils;

public class SetNametagTask extends PluginTask<ItemOwnership>{
	public SetNametagTask(ItemOwnership owner){
		super(owner);
	}

	@Override
	public void onRun(int currentTick){
		if(Utils.toBoolean(ItemOwnership.getInstance().getSetting().get("Enable").toString())){
			Server.getInstance().getLevels().values().forEach(level->Arrays.stream(level.getEntities()).filter(entity->entity instanceof EntityOwnedItem).forEach(entity->{
				EntityOwnedItem ownedItem = (EntityOwnedItem) entity;
				int age = ownedItem.getAge();
				if(Utils.toInt(ItemOwnership.getInstance().getSetting().get("Span").toString()) > age && !ItemOwnership.getInstance().getData().containsKey(ownedItem.getOwnerPlayer().getName().toLowerCase())){
					int remainTime = (Utils.toInt(ItemOwnership.getInstance().getSetting().get("Span").toString()) - age) / 20;
					String[] params = new String[]{this.owner.getName(), String.valueOf(remainTime)};
					String toOwner = Translation.translate("showNametag.toOwner", params);
					String toOther = Translation.translate("showNametag.toOther", params);
					if(toOwner.trim().equals("") && toOther.trim().equals("")){
						ownedItem.setNameTagVisible(false);
					}else{
						int distance = Utils.toInt(ItemOwnership.getInstance().getSetting().get("Distance").toString());
						ownedItem.getViewers().values().forEach(player->{
							EntityMetadata data = new EntityMetadata();
							if(ownedItem.distance(player) < distance){
								if(ownedItem.isOwner(player)){
									if(toOther.trim().equals("")){
										data.put(new ByteEntityData(Entity.DATA_SHOW_NAMETAG, 0));
									}else{
										data.put(new ByteEntityData(Entity.DATA_SHOW_NAMETAG, 1));
										data.put(new StringEntityData(Entity.DATA_NAMETAG, toOwner));
									}
								}else if(toOther.trim().equals("")){
									data.put(new ByteEntityData(Entity.DATA_SHOW_NAMETAG, 0));
								}else{
									data.put(new ByteEntityData(Entity.DATA_SHOW_NAMETAG, 1));
									data.put(new StringEntityData(Entity.DATA_NAMETAG, toOther));
								}
							}else{
								data.put(new ByteEntityData(Entity.DATA_SHOW_NAMETAG, 0));
							}
							ownedItem.sendData(player, data);
						});
					}
				}else{
					ownedItem.setNameTagVisible(false);
				}
			}));
		}
	}
}
