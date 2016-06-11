package debe.nukkitplugin.showinfo.task;

import cn.nukkit.Server;
import cn.nukkit.scheduler.PluginTask;
import debe.nukkitplugin.showinfo.ShowInfo;
import debe.nukkitplugin.showinfo.event.SendInfoEvent;
import debe.nukkitplugin.showinfo.utils.Utils;

public class ShowInfoTask extends PluginTask<ShowInfo>{
	public ShowInfoTask(ShowInfo owner){
		super(owner);
	}

	@Override
	public void onRun(int currentTick){
		String format;
		int push = Utils.toInt(this.owner.getSetting().get("PushLevel").toString());
		if(push == 0){
			format = this.owner.getFormat();
		}else{
			StringBuilder pushBuilder = new StringBuilder();
			for(int i = 0; i < Math.abs(push); i++){
				pushBuilder.append(" ");
			}
			if(push > 0){
				format = pushBuilder.append(this.owner.getFormat().replace("\n", "\n" + pushBuilder.toString())).toString();
			}else{
				format = pushBuilder.insert(0, this.owner.getFormat().replace("\n", pushBuilder.toString() + "\n")).append(pushBuilder.toString()).toString();
			}
		}
		Server.getInstance().getOnlinePlayers().values().stream().filter(player->(!this.owner.getData().containsKey(player.getName().toLowerCase()) && player.isAlive() && player.spawned)).forEach(player->{
			SendInfoEvent event = new SendInfoEvent(player, format);
			Server.getInstance().getPluginManager().callEvent(event);
			if(!event.isCancelled()){
				player.sendPopup(event.getInfo());
			}
		});
	}
}
