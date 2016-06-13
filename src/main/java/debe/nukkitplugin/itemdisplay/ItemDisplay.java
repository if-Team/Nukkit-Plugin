package debe.nukkitplugin.itemdisplay;

import java.util.ArrayList;
import java.util.HashMap;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerInteractEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.item.Item;
import cn.nukkit.item.enchantment.Enchantment;
import cn.nukkit.level.Level;
import cn.nukkit.level.Position;
import cn.nukkit.plugin.PluginBase;
import debe.nukkitplugin.itemdisplay.entity.ImaginaryItem;
import debe.nukkitplugin.itemdisplay.listener.SaveCommandListener;
import debe.nukkitplugin.itemdisplay.task.ItemDisplayTask;
import debe.nukkitplugin.itemdisplay.task.touchtask.AddTouchTask;
import debe.nukkitplugin.itemdisplay.task.touchtask.TouchTask;
import debe.nukkitplugin.itemdisplay.utils.FileUtils;
import debe.nukkitplugin.itemdisplay.utils.Utils;

public class ItemDisplay extends PluginBase{
	private static ItemDisplay instance;
	private ArrayList<String> nametagViewers = new ArrayList<String>();
	private HashMap<String, ImaginaryItem> imaginaryItems = new HashMap<String, ImaginaryItem>();
	private HashMap<Player, TouchTask> touchTasks = new HashMap<Player, TouchTask>();

	public static ItemDisplay getInstance(){
		return ItemDisplay.instance;
	}

	@Override
	public void onLoad(){
		ItemDisplay.instance = this;
	}

	@Override
	public void onEnable(){
		this.loadAll();
		this.setTouchTasks(new HashMap<Player, TouchTask>());
		this.getServer().getPluginManager().registerEvents(new SaveCommandListener(), this);
		this.getServer().getPluginManager().registerEvents(new Listener(){
			@EventHandler(priority = EventPriority.HIGHEST)
			public void onPlayerInteract(PlayerInteractEvent event){
				if(event.getAction() == PlayerInteractEvent.RIGHT_CLICK_BLOCK){
					TouchTask touchTask = ItemDisplay.getInstance().getTouchTask(event.getPlayer());
					if(touchTask instanceof AddTouchTask){
						((AddTouchTask) touchTask).setPosition(event.getBlock().getSide(event.getFace())).onRun();
						event.setCancelled();
					}
				}
			}

			@EventHandler(priority = EventPriority.HIGHEST)
			public void onPlayerQuit(PlayerQuitEvent event){
				Player player = event.getPlayer();
				ItemDisplay.getInstance().getImaginaryItems().values().stream().filter(imaginaryItem->imaginaryItem.isSpawned(player)).forEach(imaginaryItem->imaginaryItem.despawnFrom(player));
			}
		}, this);
		this.getServer().getScheduler().scheduleRepeatingTask(new ItemDisplayTask(this), 20);
	}

	@Override
	public void onDisable(){
		this.saveAll();
		this.getImaginaryItems().values().forEach(ImaginaryItem::despawnFromAll);
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

	public HashMap<String, ImaginaryItem> getImaginaryItems(){
		return this.imaginaryItems;
	}

	public void setImaginaryItems(HashMap<String, ImaginaryItem> imaginaryItems){
		this.imaginaryItems = imaginaryItems;
	}

	public ImaginaryItem getImaginaryItem(String name){
		return this.imaginaryItems.get(name);
	}

	public void addImaginaryItem(String name, int itemId, int itemDamage, boolean enchanted, double x, double y, double z, Level level){
		Item item = Item.get(itemId, itemDamage);
		if(enchanted){
			item.addEnchantment(Enchantment.get(Enchantment.ID_DURABILITY));
		}
		this.addImaginaryItem(name, item, new Position(x, y, z, level));
	}

	public void addImaginaryItem(String name, Item item, Position position){
		this.addImaginaryItem(new ImaginaryItem(name, item, position.x, position.y, position.z, position.level));
	}

	public void addImaginaryItem(ImaginaryItem imaginaryItem){
		this.imaginaryItems.put(imaginaryItem.getName(), imaginaryItem);
	}

	public void removeImaginaryItem(ImaginaryItem imaginaryItem){
		this.imaginaryItems.remove(imaginaryItem.getName());
	}

	public void removeImaginaryItem(String name){
		if(this.imaginaryItems.containsKey(name)){
			this.imaginaryItems.get(name).despawnFromAll();
			this.imaginaryItems.remove(name);
		}
	}

	public boolean exsisImaginaryItem(String name){
		return this.imaginaryItems.containsKey(name);
	}

	public ImaginaryItem parseImaginaryItem(String name, String data){
		String[] params = data.split(":");
		if(params.length == 7){
			Item item = Item.get(Utils.toInt(params[0]), Utils.toInt(params[1]));
			if(Utils.toBoolean(params[2])){
				item.addEnchantment(Enchantment.get(Enchantment.ID_DURABILITY));
			}
			Level level = this.getServer().getLevelByName(params[6]);
			if(level instanceof Level){
				return new ImaginaryItem(name, item, Utils.toDouble(params[3]), Utils.toDouble(params[4]), Utils.toDouble(params[5]), level);
			}
		}
		return null;
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
				this.getImaginaryItems().values().stream().filter(imaginaryItem->imaginaryItem.isSpawned(player)).forEach(imaginaryItem->imaginaryItem.respawnFrom(player));
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
				this.getImaginaryItems().values().stream().filter(imaginaryItem->imaginaryItem.isSpawned(player)).forEach(imaginaryItem->imaginaryItem.respawnFrom(player));
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
