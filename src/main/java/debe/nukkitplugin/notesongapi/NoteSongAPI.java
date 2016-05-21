package debe.nukkitplugin.notesongapi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import debe.nukkitplugin.notesongapi.player.BasePlayer;
import debe.nukkitplugin.notesongapi.song.BaseSong;

public class NoteSongAPI extends PluginBase{
	private static NoteSongAPI instance;
	private int playerCount = 0;
	private int songCount = 0;
	private Map<Integer, BasePlayer<?, ?>> players = new HashMap<Integer, BasePlayer<?, ?>>();
	private Map<Integer, BaseSong<?>> songs = new HashMap<Integer, BaseSong<?>>();

	public final static NoteSongAPI getInstance(){
		return NoteSongAPI.instance;
	}

	@Override
	public void onLoad(){
		NoteSongAPI.instance = this;
	}

	public ArrayList<BasePlayer<?, ?>> getPlayers(){
		return new ArrayList<BasePlayer<?, ?>>(this.players.values());
	}

	public BasePlayer<?, ?> getPlayer(int playerId){
		return this.players.get(playerId);
	}

	public List<BasePlayer<?, ?>> getPlayers(Player recipient){
		return this.players.values().stream().filter(player->player.existsRecipient(recipient)).collect(Collectors.toList());
	}

	public List<BasePlayer<?, ?>> getPlayers(BaseSong<?> song){
		return this.players.values().stream().filter(player->player.existsSong(song)).collect(Collectors.toList());
	}

	public boolean playerExists(int playerId){
		return this.players.containsKey(playerId);
	}

	public int registerPlayer(BasePlayer<?, ?> player){
		int playerId = this.getNewPlayerId();
		this.players.put(playerId, player);
		return playerId;
	}

	public int getNewPlayerId(){
		return ++this.playerCount;
	}

	public ArrayList<BaseSong<?>> getSongs(){
		return new ArrayList<BaseSong<?>>(this.songs.values());
	}

	public BaseSong<?> getSong(int songId){
		return this.songs.get(songId);
	}

	public boolean songExists(int songId){
		return this.songs.containsKey(songId);
	}

	public int registerSong(BaseSong<?> song){
		int songId = this.getNewSongId();
		this.songs.put(songId, song);
		return songId;
	}

	public int getNewSongId(){
		return ++this.songCount;
	}
}
