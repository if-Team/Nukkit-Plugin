package debe.nukkitplugin.itemdisplay.task.touchtask;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import debe.nukkitplugin.itemdisplay.ItemDisplay;

public abstract class TouchTask{
	protected Player player;

	public TouchTask(Player player){
		this.player = player;
	}

	public Player getPlayer(){
		return this.player;
	}

	public void onTouch(Block block){
		ItemDisplay.getInstance().removeTouchTask(this);
	}
}
