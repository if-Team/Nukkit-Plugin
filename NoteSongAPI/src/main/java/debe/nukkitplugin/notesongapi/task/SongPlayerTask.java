package debe.nukkitplugin.notesongapi.task;

import cn.nukkit.scheduler.PluginTask;
import debe.nukkitplugin.notesongapi.NoteSongAPI;
import debe.nukkitplugin.notesongapi.player.BasePlayer;

public class SongPlayerTask<T extends BasePlayer<?, ?>>extends PluginTask<NoteSongAPI>{
	protected T player;

	public SongPlayerTask(T player){
		super(NoteSongAPI.getInstance());
		this.player = player;
	}

	@Override
	public void onRun(int currentTick){
		this.player.onRun(currentTick);
	}
}
