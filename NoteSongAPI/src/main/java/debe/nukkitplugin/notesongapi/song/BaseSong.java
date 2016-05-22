package debe.nukkitplugin.notesongapi.song;

import java.util.ArrayList;

import debe.nukkitplugin.notesongapi.NoteSongAPI;
import debe.nukkitplugin.notesongapi.sound.BaseSound;
import debe.nukkitplugin.notesongapi.sound.SoundTable;

public class BaseSong<T extends BaseSound>{
	private int id;
	protected String name;
	protected String author = "";
	protected String description = "";
	protected short tempo = 1000; // Play 10 note in 1 second
	protected SoundTable<T> soundTable;

	public BaseSong(String name){
		this(name, new SoundTable<T>());
	}

	public BaseSong(String name, SoundTable<T> soundTable){
		this.id = NoteSongAPI.getInstance().registerSong(this);
		this.name = name;
		this.soundTable = soundTable;
	}

	public BaseSong(String name, String author, String description, SoundTable<T> soundTable){
		this(name, soundTable);
		this.author = author;
		this.description = description;
	}

	public BaseSong(String name, String author, String description, short tempo, SoundTable<T> soundTable){
		this(name, author, description, soundTable);
		this.tempo = tempo;
	}

	public String getName(){
		return this.name;
	}

	public void setName(String name){
		this.name = name;
	}

	public final int getId(){
		return this.id;
	}

	public SoundTable<T> getSoundTable(){
		return new SoundTable<T>(this.soundTable);
	}

	public void setSoundTable(SoundTable<T> soundTable){
		this.soundTable = soundTable;
	}

	public ArrayList<T> getSounds(int tick){
		return this.soundTable.getSoundTable().get(tick);
	}

	public boolean existSounds(int tick){
		return this.soundTable.getSoundTable().containsKey(tick) && !this.soundTable.getSoundTable().get(tick).isEmpty();
	}

	public float getTempo(){
		return this.tempo;
	}

	public void setTempo(short tempo){
		this.tempo = tempo;
	}

	public int getLength(){
		return this.soundTable.getLength();
	}
}
