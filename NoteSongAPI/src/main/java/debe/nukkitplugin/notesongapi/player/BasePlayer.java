package debe.nukkitplugin.notesongapi.player;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.scheduler.TaskHandler;
import debe.nukkitplugin.notesongapi.NoteSongAPI;
import debe.nukkitplugin.notesongapi.song.BaseSong;
import debe.nukkitplugin.notesongapi.sound.BaseSound;
import debe.nukkitplugin.notesongapi.task.SongPlayerTask;

abstract public class BasePlayer<T extends BaseSong<S>, S extends BaseSound>{
	private int id;
	protected LinkedHashMap<Integer, T> songs;
	protected LinkedHashMap<String, Player> recipients;
	protected int songId = -1;
	protected int delay = 0;
	protected int tick = 0;
	protected boolean loop = false;
	protected boolean shuffle = false;
	protected TaskHandler task;

	public BasePlayer(ArrayList<T> songs){
		this(songs, new Player[]{}, false, false, 0);
	}

	public BasePlayer(ArrayList<T> songs, Player[] recipients, boolean loop, boolean shuffle, int delay){
		this.recipients = new LinkedHashMap<String, Player>(){};
		for(Player recipient : recipients){
			this.addRecipient(recipient);
		}
		this.songs = new LinkedHashMap<Integer, T>(){};
		for(T song : songs){
			this.addSong(song);
		}
		this.id = NoteSongAPI.registerPlayer(this);
		this.loop = loop;
		this.shuffle = shuffle;
		this.delay = delay;
	}

	public BasePlayer(LinkedHashMap<Integer, T> songs, LinkedHashMap<String, Player> recipients, boolean loop, boolean shuffle, int delay){
		this.id = NoteSongAPI.registerPlayer(this);
		this.songs = songs;
		this.recipients = recipients;
		this.loop = loop;
		this.shuffle = shuffle;
		this.delay = delay;
	}

	public void play(){
		this.play(2);
	}

	public void play(int period){
		if(!this.isPlaying()){
			this.task = Server.getInstance().getScheduler().scheduleRepeatingTask(new SongPlayerTask<BasePlayer<T, S>>(this), period);
		}
	}

	public void pause(){
		if(this.isPlaying()){
			Server.getInstance().getScheduler().cancelTask(this.task.getTaskId());
		}
	}

	public void stop(){
		this.pause();
		this.tick = 0;
		this.songId = -1;
	}

	public boolean isPlaying(){
		return this.task != null && !this.task.isCancelled();
	}

	public void onRun(int currentTick){
		T song = this.getSong();
		if(song != null){
			if(this.tick > song.getLength().intValue() + this.getDelay()){
				this.tick = 0;
				song = this.isShuffle() ? this.getRandomSong() : this.getNextSong();
				this.songId = song == null ? -1 : song.getId();
			}else{
				this.sendSound(this.tick);
				this.tick++;
			}
		}else{
			this.setSongId(this.getNextSong().getId());
		}
	}

	public void sendSound(int tick){
		T song = this.getSong();
		if(song != null){
			this.sendSound(this.getRecipients(), song.getSounds(tick));
		}
	}

	public void sendSound(Player[] players, ArrayList<S> sounds){
		for(Player player : players){
			this.sendSound(player, sounds);
		}
	}

	public void sendSound(Player player, ArrayList<S> sounds){
		for(S sound : sounds){
			this.sendSound(player, sound);
		}
	}

	public void sendSound(Player player, S sound){
		sound.sendTo(player, player);
	}

	public Player[] getRecipients(){
		return this.recipients.values().stream().toArray(Player[]::new);
	}

	public void setRecipients(Player[] recipients){
		this.recipients = new LinkedHashMap<String, Player>(){};
		for(Player player : recipients){
			this.recipients.put(player.getName().toLowerCase(), player);
		};
	}

	public void setRecipients(LinkedHashMap<String, Player> recipients){
		this.recipients = recipients;
	}

	public void addRecipient(Player recipient){
		this.recipients.put(recipient.getName().toLowerCase(), recipient);
	}

	public void removeRecipient(Player recipient){
		if(this.existsRecipient(recipient)){
			this.recipients.remove(recipient.getName().toLowerCase());
		}
	}

	public boolean existsRecipient(Player recipient){
		return this.recipients.containsKey(recipient.getName().toLowerCase());
	}

	public LinkedHashMap<Integer, T> getSongs(){
		return new LinkedHashMap<Integer, T>(this.songs);
	}

	public void setSongs(ArrayList<T> songs){
		this.songs = new LinkedHashMap<Integer, T>(){};
		for(T song : songs){
			this.songs.put(song.getId(), song);
		};
	}

	public void setSongs(LinkedHashMap<Integer, T> songs){
		this.songs = songs;
	}

	public T getSong(){
		if(this.songId != -1 && this.songs.containsKey(this.songId)){
			return this.songs.get(this.songId);
		}
		return null;
	}

	public T getNextSong(){
		if(this.hasSong()){
			if(this.songId != -1 || !this.songs.containsKey(this.songId)){
				boolean checkedNow = false;
				for(int songId : this.songs.keySet()){
					if(checkedNow){
						return this.songs.get(songId);
					}else if(this.songId == songId){
						checkedNow = true;
					}
				}
			}
			return this.getFirstSong();
		}
		return null;
	}

	public T getRandomSong(){
		if(this.hasSong()){
			Integer[] songIds = this.songs.keySet().stream().toArray(Integer[]::new);
			return this.songs.get((int) Math.floor(Math.random() * songIds.length));
		}
		return null;
	}

	public T getFirstSong(){
		for(T song : this.songs.values()){
			return song;
		}
		return null;
	}

	public void addSong(T song){
		this.songs.put(song.getId(), song);
	}

	public void removeSong(BaseSong<?> song){
		if(this.existsSong(song)){
			this.songs.remove(song.getId(), song);
		}
	}

	public boolean hasSong(){
		return this.getSongs().size() > 0;
	}

	public boolean existsSong(BaseSong<?> song){
		return this.songs.containsKey(song.getId());
	}

	public final int getId(){
		return this.id;
	}

	public int getSongId(){
		return this.songId;
	}

	public void setSongId(int songId){
		this.songId = songId;
	}

	public int getDelay(){
		return this.delay;
	}

	public void setDelay(int delay){
		this.delay = delay;
	}

	public int getTick(){
		return this.tick;
	}

	public void setTick(int tick){
		this.tick = tick;
	}

	public boolean isLoop(){
		return this.loop;
	}

	public void setLoop(boolean loop){
		this.loop = loop;
	}

	public boolean isShuffle(){
		return this.shuffle;
	}

	public void setShuffle(boolean shuffle){
		this.shuffle = shuffle;
	}
}
