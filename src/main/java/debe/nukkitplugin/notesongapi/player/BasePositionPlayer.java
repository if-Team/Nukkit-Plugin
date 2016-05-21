package debe.nukkitplugin.notesongapi.player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.level.Position;
import debe.nukkitplugin.notesongapi.song.BaseSong;
import debe.nukkitplugin.notesongapi.sound.BaseSound;

public class BasePositionPlayer<T extends BaseSong<S>, S extends BaseSound>extends BasePlayer<T, S>{
	protected Position position;

	public BasePositionPlayer(ArrayList<T> songs, Position position){
		super(songs, new ArrayList<Player>(), false, false, 0);
		this.position = position;
	}

	public BasePositionPlayer(ArrayList<T> songs, ArrayList<Player> recipients, boolean loop, boolean shuffle, int delay, Position position){
		super(songs, recipients, loop, shuffle, delay);
		this.position = position;
	}

	public BasePositionPlayer(LinkedHashMap<Integer, T> songs, Map<String, Player> recipients, boolean loop, boolean shuffle, int delay, Position position){
		super(songs, recipients, loop, shuffle, delay);
		this.position = position;
	}

	@Override
	public ArrayList<Player> getRecipients(){
		return new ArrayList<Player>(this.position.level.getChunkPlayers((int) this.position.x >> 4, (int) this.position.z >> 4).values());
	}

	public Position getPosition(){
		return this.position;
	}

	public void getPosition(Position position){
		this.position = position;
	}
}
