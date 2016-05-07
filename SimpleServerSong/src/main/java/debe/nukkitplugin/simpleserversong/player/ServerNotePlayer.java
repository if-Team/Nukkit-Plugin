package debe.nukkitplugin.simpleserversong.player;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import cn.nukkit.Player;
import debe.nukkitplugin.notesongapi.player.BaseBroadcastPlayer;
import debe.nukkitplugin.notesongapi.song.NoteSong;
import debe.nukkitplugin.notesongapi.sound.NoteSound;
import debe.nukkitplugin.simpleserversong.SimpleServerSong;

public class ServerNotePlayer extends BaseBroadcastPlayer<NoteSong, NoteSound>{
	protected SimpleServerSong plugin;

	public ServerNotePlayer(ArrayList<NoteSong> songs, SimpleServerSong plugin){
		super(songs, new Player[]{}, false, false, 0);
		this.plugin = plugin;
	}

	public ServerNotePlayer(ArrayList<NoteSong> songs, Player[] recipients, boolean loop, boolean shuffle, int delay){
		super(songs, recipients, loop, shuffle, delay);
	}

	public ServerNotePlayer(LinkedHashMap<Integer, NoteSong> songs, LinkedHashMap<String, Player> recipients, boolean loop, boolean shuffle, int delay){
		super(songs, recipients, loop, shuffle, delay);
	}

	@Override
	public NoteSong getNextSong(){
		NoteSong song = super.getNextSong();
		this.plugin.getLogger().info("Next song [" + song.getId() + "] " + song.getOriginalName());
		return song;
	}
}
