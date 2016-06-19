package debe.nukkitplugin.itemdisplay;

import java.util.ArrayList;
import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import debe.nukkitplugin.itemdisplay.entity.VirtualItem;
import debe.nukkitplugin.itemdisplay.listener.BlockTouchListener;
import debe.nukkitplugin.itemdisplay.listener.LevelLoadListener;
import debe.nukkitplugin.itemdisplay.listener.PlayerJoinQuitListener;
import debe.nukkitplugin.itemdisplay.listener.SaveCommandListener;
import debe.nukkitplugin.itemdisplay.task.touchtask.TouchTask;
import debe.nukkitplugin.itemdisplay.utils.FileUtils;

public class ItemDisplay extends PluginBase{
	private static ItemDisplay instance;
	private ArrayList<String> nametagViewers = new ArrayList<String>();
	private HashMap<String, VirtualItem> virtualItems = new HashMap<String, VirtualItem>();
	private HashMap<Player, TouchTask> touchTasks = new HashMap<Player, TouchTask>();

	public static ItemDisplay getInstance(){
		return ItemDisplay.instance;
	}

	@Override
	public void onLoad(){
		ItemDisplay.instance = this;
		this.loadAll();
		this.setTouchTasks(new HashMap<Player, TouchTask>());
		VirtualItem.spawnAllToAll();
	}

	@Override
	public void onEnable(){
		Server.getInstance().getLevels().values().forEach(FileUtils::loadData);
		this.getServer().getPluginManager().registerEvents(new BlockTouchListener(), this);
		this.getServer().getPluginManager().registerEvents(new LevelLoadListener(), this);
		this.getServer().getPluginManager().registerEvents(new PlayerJoinQuitListener(), this);
		this.getServer().getPluginManager().registerEvents(new SaveCommandListener(), this);
	}

	@Override
	public void onDisable(){
		this.saveAll();
		VirtualItem.despawnAllFromAll();
	}

	public void loadAll(){
		FileUtils.mkdirs();
		this.saveDefaultData(false);
		FileUtils.loadSetting();
		FileUtils.loadPermissions();
		FileUtils.loadCommandSetting();
		FileUtils.loadData();
	}

	public void saveAll(){
		FileUtils.mkdirs();
		FileUtils.saveData();
	}

	public void saveDefaultData(boolean replace){
		new ArrayList<String>(){
			{
				add("Data.json");
				add("Permissions.json");
				add("Setting.json");
				add("command/CommandSetting.json");
				add("command/SubCommandSetting.json");
				add("lang/eng.ini");
				add("lang/kor.ini");
			}
		}.forEach(fileName->this.saveResource("defaults/" + fileName, fileName, replace));
	}

	public HashMap<String, VirtualItem> getVirtualItems(){
		return this.virtualItems;
	}

	public void setVirtualItems(HashMap<String, VirtualItem> virtualItems){
		this.virtualItems = virtualItems;
	}

	public VirtualItem getVirtualItem(String name){
		return this.virtualItems.get(name);
	}

	public void addVirtualItem(String name, int itemId, int itemDamage, boolean enchanted, double x, double y, double z, String levelName){
		Item item = Item.get(itemId, itemDamage);
		if(enchanted){
			item.addEnchantment(Enchantment.get(Enchantment.ID_DURABILITY));
		}
		this.addVirtualItem(new VirtualItem(name, item, x, y, z, levelName));
	}

	public void addVirtualItem(String name, Item item, Position position){
		this.addVirtualItem(new VirtualItem(name, item, position.x, position.y, position.z, position.level.getFolderName()));
	}

	public void addVirtualItem(VirtualItem virtualItem){
		virtualItem.spawnToAll();
		this.virtualItems.put(virtualItem.getName(), virtualItem);
	}

	public void removeVirtualItem(VirtualItem virtualItem){
		this.virtualItems.remove(virtualItem.getName());
	}

	public void removeVirtualItem(String name){
		if(this.virtualItems.containsKey(name)){
			this.virtualItems.get(name).despawnFromAll();
			this.virtualItems.remove(name);
		}
	}

	public boolean existsVirtualItem(String name){
		return this.virtualItems.containsKey(name);
	}

	public ArrayList<String> getNametagViewers(){
		return this.nametagViewers;
	}

	public void setNametagViewers(ArrayList<String> nametagViewers){
		this.nametagViewers = nametagViewers;
	}

	public void addNametagViewer(Player player){
		this.addNametagViewer(player.getName());
	}

	public void addNametagViewer(String name){
		if(!this.isNametagViewer(name)){
			this.nametagViewers.add(name.toLowerCase());
			Player player = this.getServer().getPlayerExact(name);
			if(player instanceof Player){
				VirtualItem.respawnAllTo(player);
			}
		}
	}

	public void removeNametagViewer(Player player){
		this.removeNametagViewer(player.getName());
	}

	public void removeNametagViewer(String name){
		if(this.isNametagViewer(name)){
			this.nametagViewers.remove(name.toLowerCase());
			Player player = this.getServer().getPlayerExact(name);
			if(player instanceof Player){
				VirtualItem.respawnAllTo(player);
			}
		}
	}

	public boolean isNametagViewer(Player player){
		return this.isNametagViewer(player.getName());
	}

	public boolean isNametagViewer(String name){
		return this.nametagViewers.contains(name.toLowerCase());
	}

	public HashMap<Player, TouchTask> getTouchTasks(){
		return this.touchTasks;
	}

	public void setTouchTasks(HashMap<Player, TouchTask> touchTasks){
		this.touchTasks = touchTasks;
	}

	public TouchTask getTouchTask(Player player){
		return this.touchTasks.get(player);
	}

	public void addTouchTask(TouchTask task){
		this.touchTasks.put(task.getPlayer(), task);
	}

	public void removeTouchTask(TouchTask task){
		this.touchTasks.remove(task.getPlayer());
	}

	public boolean existTouchTask(Player player){
		return this.touchTasks.containsKey(player);
	}
}
