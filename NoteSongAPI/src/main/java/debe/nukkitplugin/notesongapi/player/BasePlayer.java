package debe.nukkitplugin.notesongapi.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.stream.Collectors;

import cn.nukkit.Player;
import cn.nukkit.plugin.Plugin;
import debe.nukkitplugin.notesongapi.NoteSongAPI;
import debe.nukkitplugin.notesongapi.song.BaseSong;
import debe.nukkitplugin.notesongapi.sound.BaseSound;
import debe.nukkitplugin.notesongapi.task.SongTimerTask;

abstract public class BasePlayer<T extends BaseSong<S>, S extends BaseSound>{
	private int id;
	protected LinkedHashMap<Integer, T> songs = new LinkedHashMap<Integer, T>();
	protected List<Integer> waitingList = new ArrayList<Integer>();
	protected Map<String, Player> recipients = new HashMap<String, Player>();
	protected int songId = -1;
	protected int delay = 0;
	protected int tick = 0;
	protected boolean loop = false;
	protected boolean shuffle = false;
	protected Timer timer = new Timer();
	protected SongTimerTask timerTask;

	public BasePlayer(ArrayList<T> songs){
		this(songs, new ArrayList<Player>(), false, false, 0);
	}

	public BasePlayer(ArrayList<T> songs, ArrayList<Player> recipients, boolean loop, boolean shuffle, int delay){
		songs.forEach(this::addSong);
		recipients.forEach(this::addRecipient);
		this.id = NoteSongAPI.getInstance().registerPlayer(this);
		this.loop = loop;
		this.shuffle = shuffle;
		this.delay = delay;
	}

	public BasePlayer(LinkedHashMap<Integer, T> songs, Map<String, Player> recipients, boolean loop, boolean shuffle, int delay){
		this.id = NoteSongAPI.getInstance().registerPlayer(this);
		this.setSongs(songs);
		this.setRecipients(recipients);
		this.loop = loop;
		this.shuffle = shuffle;
		this.delay = delay;
	}

	public Plugin getPlugin(){
		return NoteSongAPI.getInstance();
	}

	public void play(){
		this.play(this.getSpeed());
	}

	public void play(short period){
		if(!this.isPlaying()){
			if(this.timerTask == null){
				this.timerTask = new SongTimerTask(this.getPlugin(), this);
				this.timer.schedule(this.timerTask, period, period);
			}
		}
	}

	public void pause(){
		if(this.isPlaying()){
			this.timerTask.cancel();
			this.timerTask = null;
		}
	}

	public void stop(){
		this.pause();
		this.tick = 0;
		this.songId = -1;
		this.timerTask.cancel();
		this.timerTask = null;
	}

	public short getSpeed(){
		T playingSong = this.getPlayingSong();
		return playingSong != null ? (short) (100000 / playingSong.getTempo()) : 100;
	}

	public boolean isPlaying(){
		return this.timerTask != null;
	}

	public void onRun(){
		T song = this.getPlayingSong();
		if(song != null){
			if(this.tick > song.getLength() + this.getDelay()){
				this.tick = 0;
			}else{
				this.sendSound(this.tick++);
				return;
			}
		}
		song = this.getNextSong();
		if(song == null){
			if(!this.isLoop()){
				this.stop();
			}else{
				this.songId = -1;
			}
		}else{
			this.songId = song.getId();
			this.timerTask = new SongTimerTask(this.getPlugin(), this);
			this.play(this.getSpeed());;
			this.removeWaitingList(song);
		}
	}

	public void sendSound(int tick){
		T song = this.getPlayingSong();
		if(song != null && song.getSounds(tick) != null){
			this.sendSound(this.getRecipients(), song.getSounds(tick));
		}
	}

	public void sendSound(ArrayList<Player> players, ArrayList<S> sounds){
		players.forEach(player->this.sendSound(player, sounds));
	}

	public void sendSound(Player player, ArrayList<S> sounds){
		sounds.forEach(sound->this.sendSound(player, sound));
	}

	public void sendSound(Player player, S sound){
		sound.sendTo(player, player);
	}

	public int getId(){
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

	public float getTick(){
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

	public ArrayList<T> getSongs(){
		return new ArrayList<T>(this.songs.values());
	}

	public void setSongs(ArrayList<T> songs){
		this.songs.clear();
		songs.forEach(this::addSong);
		this.resetWaitingList();
	}

	public void setSongs(LinkedHashMap<Integer, T> songs){
		this.songs = songs;
		this.resetWaitingList();
	}

	public T getPlayingSong(){
		return this.songId != -1 && this.songs.containsKey(this.songId) ? this.songs.get(this.songId) : null;
	}

	public T getNextSong(){
		return this.getNextSong(this.isShuffle());
	}

	public T getNextSong(boolean isShuffle){
		if(this.waitingList.size() == 0){
			if(this.isLoop()){
				this.resetWaitingList();
			}else{
				return null;
			}
		}
		return this.songs.get(this.waitingList.get(isShuffle ? new Random().nextInt(this.waitingList.size()) : 0));
	}

	public void addSong(T song){
		if(!this.existsSong(song)){
			this.songs.put(song.getId(), song);
			this.waitingList.add(song.getId());
		}
	}

	public void removeSong(BaseSong<?> song){
		if(this.existsSong(song)){
			this.songs.remove(song.getId(), song);
			if(this.waitingList.contains(song.getId())){
				this.waitingList.remove(this.waitingList.indexOf(song.getId()));
			}
		}
	}

	public boolean existsSong(BaseSong<?> song){
		return this.existsSong(song.getId());
	}

	public boolean existsSong(int songId){
		return this.songs.containsKey(songId);
	}

	public List<Integer> getWaitingList(){
		return this.waitingList.stream().collect(Collectors.toList());
	}

	public void setWaitingList(ArrayList<Integer> waitingList){
		this.waitingList = waitingList.stream().filter(songId->this.existsSong(songId)).collect(Collectors.toList());
	}

	public void addWaitingList(BaseSong<?> song){
		this.addWaitingList(song.getId());
	}

	public void addWaitingList(int songId){
		if(!this.waitingList.contains(songId)){
			this.waitingList.add(songId);
		}
	}

	public void removeWaitingList(BaseSong<?> song){
		this.removeWaitingList(song.getId());
	}

	public void removeWaitingList(int songId){
		if(this.waitingList.contains(songId)){
			this.waitingList.remove(this.waitingList.indexOf(songId));
		}
	}

	public void resetWaitingList(){
		this.waitingList = new ArrayList<Integer>(this.songs.keySet());
	}

	public ArrayList<Player> getRecipients(){
		return new ArrayList<Player>(this.recipients.values());
	}

	public void setRecipients(ArrayList<Player> recipients){
		this.recipients.clear();
		recipients.forEach(this::addRecipient);
	}

	public void setRecipients(Map<String, Player> recipients){
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
}
