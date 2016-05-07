package debe.nukkitplugin.showinfo.event;

import cn.nukkit.Player;
import cn.nukkit.event.Cancellable;
import cn.nukkit.event.HandlerList;
import cn.nukkit.event.player.PlayerEvent;

public class SendInfoEvent extends PlayerEvent implements Cancellable{
	private static final HandlerList handlers = new HandlerList();
	protected String info = "";

	public static HandlerList getHandlers(){
		return SendInfoEvent.handlers;
	}

	public SendInfoEvent(Player player, String info){
		this.player = player;
		this.info = info;
	}

	public String getInfo(){
		return this.info;
	}

	public void setInfo(String info){
		this.info = info;
	}
}
