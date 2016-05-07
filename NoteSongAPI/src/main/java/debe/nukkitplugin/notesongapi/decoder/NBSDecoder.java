package debe.nukkitplugin.notesongapi.decoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import debe.nukkitplugin.notesongapi.song.NoteSong;
import debe.nukkitplugin.notesongapi.sound.NoteSound;
import debe.nukkitplugin.notesongapi.sound.SoundTable;

public class NBSDecoder extends BaseDecoder<NoteSound, NoteSong>{
	public NBSDecoder(File file) throws IOException, FileNotFoundException{
		super(file);
	}

	public NBSDecoder(InputStream inputStream) throws IOException, FileNotFoundException{
		super(inputStream);
	}

	public NBSDecoder(BinaryStream binaryStream){
		super(binaryStream);
	}

	@Override
	public NoteSong getSong() throws IOException{
		BinaryStream bs = this.getBinaryStream();
		bs.getShort();// Short: Song length
		Short height = bs.getShort(); // Short: Song height
		String name = bs.getString(); // String: Song name
		String author = bs.getString(); // String: Song author
		String originalAuthor = bs.getString(); // String: Original song author
		String description = bs.getString(); // String: Song description
		Short tempo = bs.getShort(); // Short: Tempo
		bs.getBoolean(); // Byte: Auto-saving
		bs.getByte(); // Byte: Auto-saving duration
		bs.getByte(); // Byte: Time signature
		bs.getInt(); // Integer: Minutes spent
		bs.getInt(); // Integer: Left clicks
		bs.getInt(); // Integer: Right clicks
		bs.getInt(); // Integer: Blocks added
		bs.getInt(); // Integer: Blocks removed
		bs.getString(); // String: MIDI/Schematic file name
		/* Part #2: Note blocks */
		SoundTable<NoteSound> soundTable = new SoundTable<NoteSound>(){};
		Integer tick = -1, jumpLayers;
		Byte instrument, pitch;
		while(true){
			short jumpTicks = bs.getShort(); // jumps till next tick
			if(jumpTicks == 0){
				break;
			}
			tick += jumpTicks;
			while(true){
				jumpLayers = (int) bs.getShort(); // jumps till next layer
				if(jumpLayers == 0){
					break;
				}
				switch(bs.getByte()){ // Instrument code is not equal MCPC.
					case 1: // Double Bass (wood)
						instrument = NoteSound.BASS_GUITAR;
						break;
					case 2: // Bass Drum (stone)
						instrument = NoteSound.BASS_DRUM;
						break;
					case 3: // Snare Drum (sand)
						instrument = NoteSound.SNARE_DRUM;
						break;
					case 4: // Click (glass)
						instrument = NoteSound.CLICKS_AND_STICKS;
						break;
					default: // Piano (air)
						instrument = NoteSound.PIANO_OR_HARP;
						break;
				}
				pitch = (byte) (bs.getByte() - 0x21); // Pitch -= 33(0x21)
				soundTable.addSound(tick, new NoteSound(instrument, pitch));
			}
		}
		return new NoteSong(name, author, description, tempo, height, name, originalAuthor, soundTable);
	};
}
