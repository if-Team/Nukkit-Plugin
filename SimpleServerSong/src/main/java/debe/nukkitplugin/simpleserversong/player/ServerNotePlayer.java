package debe.nukkitplugin.simpleserversong.player;

import java.util.ArrayList;

import cn.nukkit.Player;
import cn.nukkit.plugin.Plugin;
import debe.nukkitplugin.notesongapi.player.BaseBroadcastPlayer;
import debe.nukkitplugin.notesongapi.song.NoteSong;
import debe.nukkitplugin.notesongapi.sound.NoteSound;
import debe.nukkitplugin.simpleserversong.SimpleServerSong;

public class ServerNotePlayer extends BaseBroadcastPlayer<NoteSong, NoteSound>{
	protected SimpleServerSong plugin;

	public ServerNotePlayer(ArrayList<NoteSong> songs, SimpleServerSong plugin){
		super(songs, new ArrayList<Player>(), true, false, 0);
		this.plugin = plugin;
	}

	@Override
	public Plugin getPlugin(){
		return this.plugin;
	}

	@Override
	public NoteSong getNextSong(){
		NoteSong song = super.getNextSong();
		this.plugin.getLogger().info("Next song [" + song.getId() + "] " + song.getOriginalName());
		return song;
	}
}
