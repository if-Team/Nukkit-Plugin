package debe.nukkitplugin.notesongapi;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import cn.nukkit.Player;
import cn.nukkit.plugin.PluginBase;
import debe.nukkitplugin.notesongapi.player.BasePlayer;
import debe.nukkitplugin.notesongapi.song.BaseSong;

public class NoteSongAPI extends PluginBase{
	private static NoteSongAPI instance;
	public static int playerCount = 0;
	private static LinkedHashMap<Integer, BasePlayer<?, ?>> players = new LinkedHashMap<Integer, BasePlayer<?, ?>>(){};
	public static int songCount = 0;
	private static LinkedHashMap<Integer, BaseSong<?>> songs = new LinkedHashMap<Integer, BaseSong<?>>(){};

	public final static NoteSongAPI getInstance(){
		return NoteSongAPI.instance;
	}

	public final static BasePlayer<?, ?>[] getPlayers(){
		return NoteSongAPI.players.values().stream().toArray(BasePlayer[]::new);
	}

	public final static BasePlayer<?, ?> getPlayer(int playerId){
		return NoteSongAPI.players.get(playerId);
	}

	public final static BasePlayer<?, ?>[] getPlayers(Player recipient){
		ArrayList<BasePlayer<?, ?>> players = new ArrayList<BasePlayer<?, ?>>();
		for(BasePlayer<?, ?> player : NoteSongAPI.getPlayers()){
			if(player.existsRecipient(recipient)){
				players.add(player);
			}
		}
		return players.stream().toArray(BasePlayer[]::new);
	}

	public final static BasePlayer<?, ?>[] getPlayers(BaseSong<?> song){
		ArrayList<BasePlayer<?, ?>> players = new ArrayList<BasePlayer<?, ?>>();
		for(BasePlayer<?, ?> player : NoteSongAPI.getPlayers()){
			if(player.existsSong(song)){
				players.add(player);
			}
		}
		return players.stream().toArray(BasePlayer[]::new);
	}

	public final static Boolean isPlayerId(int playerId){
		return NoteSongAPI.players.containsKey(playerId);
	}

	public final static int registerPlayer(BasePlayer<?, ?> player){
		NoteSongAPI.players.put(NoteSongAPI.playerCount, player);
		return NoteSongAPI.playerCount++;
	}

	public final static BaseSong<?>[] getSongs(){
		return NoteSongAPI.songs.values().stream().toArray(BaseSong[]::new);
	}

	public final static BaseSong<?> getSong(int songId){
		return NoteSongAPI.songs.get(songId);
	}

	public final static Boolean isSongId(int songId){
		return NoteSongAPI.songs.containsKey(songId);
	}

	public final static int registerSong(BaseSong<?> song){
		NoteSongAPI.songs.put(NoteSongAPI.songCount, song);
		return NoteSongAPI.songCount++;
	}

	@Override
	public void onLoad(){
		NoteSongAPI.instance = this;
	}
}
