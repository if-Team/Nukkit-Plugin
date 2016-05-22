package debe.nukkitplugin.notesongapi.task;

import java.util.TimerTask;

import cn.nukkit.plugin.Plugin;
import debe.nukkitplugin.notesongapi.player.BasePlayer;

public class SongTimerTask extends TimerTask{
	protected Plugin owner;
	protected BasePlayer<?, ?> player;

	public SongTimerTask(Plugin owner){
		this.owner = owner;
	}

	public SongTimerTask(Plugin owner, BasePlayer<?, ?> player){
		this(owner);
		this.player = player;
	}

	@Override
	public void run(){
		if(this.owner.isDisabled()){
			this.player.stop();
		}else{
			this.player.onRun();
		}
	}
}
