package debe.nukkitplugin.notesongapi.decoder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import debe.nukkitplugin.notesongapi.song.BaseSong;
import debe.nukkitplugin.notesongapi.sound.BaseSound;
import debe.nukkitplugin.notesongapi.sound.SoundTable;
import debe.nukkitplugin.notesongapi.utils.BinaryStream;

abstract public class BaseDecoder<T extends BaseSound, S extends BaseSong<T>>{
	protected BinaryStream binaryStrem;

	public BaseDecoder(File file) throws IOException, FileNotFoundException{
		this(new FileInputStream(file));
	}

	public BaseDecoder(InputStream inputStream) throws IOException, FileNotFoundException{
		this(new BinaryStream(inputStream));
	}

	public BaseDecoder(BinaryStream binaryStream){
		this.binaryStrem = binaryStream;
	}

	public SoundTable<T> getSoundTable() throws IOException{
		return (SoundTable<T>) this.getSong().getSoundTable();
	}

	public abstract S getSong() throws IOException;

	public BinaryStream getBinaryStream(){
		return new BinaryStream(this.binaryStrem);
	}

	public void setBinaryStream(BinaryStream binaryStream){
		this.binaryStrem = binaryStream;
	}
}
