package debe.nukkitplugin.notesongapi.task;

import cn.nukkit.plugin.Plugin;
import cn.nukkit.scheduler.PluginTask;
import debe.nukkitplugin.notesongapi.NoteSongAPI;
import debe.nukkitplugin.notesongapi.player.BasePlayer;

public class SongPlayerTask<T extends BasePlayer<?, ?>>extends PluginTask<Plugin>{
	protected T player;

	public SongPlayerTask(T player){
		this(player, NoteSongAPI.getInstance());
	}

	public SongPlayerTask(T player, Plugin owner){
		super(owner);
		this.player = player;
	}

	@Override
	public void onRun(int currentTick){
		this.player.onRun(currentTick);
	}
}
