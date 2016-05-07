package debe.nukkitplugin.notesongapi.player;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import debe.nukkitplugin.notesongapi.song.BaseSong;
import debe.nukkitplugin.notesongapi.sound.BaseSound;

public class BasePositionPlayer<T extends BaseSong<S>, S extends BaseSound>extends BasePlayer<T, S>{
	protected Position position;

	public BasePositionPlayer(ArrayList<T> songs, Position position){
		super(songs, new Player[]{}, false, false, 0);
		this.position = position;
	}

	public BasePositionPlayer(ArrayList<T> songs, Player[] recipients, boolean loop, boolean shuffle, int delay, Position position){
		super(songs, recipients, loop, shuffle, delay);
		this.position = position;
	}

	public BasePositionPlayer(LinkedHashMap<Integer, T> songs, LinkedHashMap<String, Player> recipients, boolean loop, boolean shuffle, int delay, Position position){
		super(songs, recipients, loop, shuffle, delay);
		this.position = position;
	}

	@Override
	public Player[] getRecipients(){
		return this.position.level.getChunkPlayers((int) this.position.x >> 4, (int) this.position.z >> 4).values().stream().toArray(Player[]::new);
	}

	@Override
	public void sendSound(Player player, S sound){
		sound.sendTo(player, this.position);
	}
}
