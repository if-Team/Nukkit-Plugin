package debe.nukkitplugin.itemdisplay.task.touchtask;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.item.Item;
import debe.nukkitplugin.itemdisplay.ItemDisplay;
import debe.nukkitplugin.itemdisplay.utils.Translation;

public class AddTouchTask extends TouchTask{
	protected String name;
	protected Item item;

	public AddTouchTask(Player player, String name, Item item){
		super(player);
		this.name = name;
		this.item = item;
	}

	public Item getItem(){
		return this.item;
	}

	@Override
	public void onTouch(Block block, int side){
		Block targetBlock = block.getSide(side);
		ItemDisplay.getInstance().addVirtualItem(this.name, this.item, targetBlock);
		this.player.sendMessage(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate("touchTask.add.success", new String[]{this.name, String.valueOf(this.item.getId()), String.valueOf(this.item.getDamage()), String.valueOf(targetBlock.x), String.valueOf(targetBlock.y), String.valueOf(targetBlock.z), targetBlock.level.getFolderName()}));
		super.onTouch(block, side);
	}
}
