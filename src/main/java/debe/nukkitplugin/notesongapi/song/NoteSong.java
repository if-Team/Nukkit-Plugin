package debe.nukkitplugin.notesongapi.song;

import debe.nukkitplugin.notesongapi.sound.NoteSound;
import debe.nukkitplugin.notesongapi.sound.SoundTable;

public class NoteSong extends BaseSong<NoteSound>{
	protected short height = 0;
	protected String originalName = "";
	protected String originalAuthor = "";

	public NoteSong(String name){
		super(name, new SoundTable<NoteSound>());
	}

	public NoteSong(String name, SoundTable<NoteSound> soundTable){
		super(name, soundTable);
	}

	public NoteSong(String name, String author, String description, SoundTable<NoteSound> soundTable){
		super(name, author, description, soundTable);
	}

	public NoteSong(String name, String author, String description, short tempo, SoundTable<NoteSound> soundTable){
		super(name, author, description, tempo, soundTable);
	}

	public NoteSong(String name, String author, String description, short tempo, short height, String originalName, String originalAuthor, SoundTable<NoteSound> soundTable){
		super(name, author, description, tempo, soundTable);
		this.height = height;
		this.originalName = originalName;
		this.originalAuthor = originalAuthor;
	}

	public short getHeight(){
		return this.height;
	}

	public void setHeight(short height){
		this.height = height;
	}

	public String getOriginalName(){
		return this.originalName;
	}

	public void setOriginalName(String originalName){
		this.originalName = originalName;
	}

	public String getAuthor(){
		return this.author;
	}

	public void setAuthor(String author){
		this.author = author;
	}

	public String getOriginalAuthor(){
		return this.originalAuthor;
	}

	public void setOriginalAuthor(String originalAuthor){
		this.originalAuthor = originalAuthor;
	}

	public String getDescription(){
		return this.description;
	}

	public void setDescription(String description){
		this.description = description;
	}
}
