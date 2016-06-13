package debe.nukkitplugin.itemdisplay.task.touchtask;

import cn.nukkit.Player;
import cn.nukkit.item.Item;
import cn.nukkit.level.Position;
import debe.nukkitplugin.itemdisplay.ItemDisplay;
import debe.nukkitplugin.itemdisplay.utils.Translation;

public class AddTouchTask extends TouchTask{
	protected String name;
	protected Item item;
	protected Position position;

	public AddTouchTask(Player player, String name, Item item){
		super(player);
		this.name = name;
		this.item = item;
	}

	public Item getItem(){
		return this.item;
	}

	public AddTouchTask setPosition(Position position){
		this.position = position;
		return this;
	}

	@Override
	public void onRun(){
		ItemDisplay.getInstance().addImaginaryItem(this.name, this.item, this.position);
		this.player.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("touchTask.add.success", new String[]{this.name, String.valueOf(this.item.getId()), String.valueOf(this.item.getDamage()), String.valueOf(this.position.x), String.valueOf(this.position.y), String.valueOf(this.position.z), this.position.level.getName()}));
		super.onRun();
	}
}
