package debe.nukkitplugin.notesongapi.utils;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class BinaryStream extends DataInputStream{
	public BinaryStream(File file) throws IOException, FileNotFoundException{
		this(new FileInputStream(file));
	}

	public BinaryStream(InputStream in){
		super(in);
	}

	public final byte getByte() throws IOException{
		return this.readByte();
	}

	public final boolean getBoolean() throws IOException{
		return this.readBoolean();
	}

	public final short getShort() throws IOException{
		int ch1 = this.readUnsignedByte();
		int ch2 = this.readUnsignedByte();
		return (short) (ch1 + (ch2 << 8));
	}

	public final int getInt() throws IOException{
		int ch1 = this.readUnsignedByte();
		int ch2 = this.readUnsignedByte();
		int ch3 = this.readUnsignedByte();
		int ch4 = this.readUnsignedByte();
		return (ch1 + (ch2 << 8) + (ch3 << 16) + (ch4 << 24));
	}

	public final String getString() throws IOException{
		int length = this.getInt();
		StringBuilder sb = new StringBuilder(length);
		for(; length > 0; --length){
			char c = (char) this.getByte();
			if(c == (char) 0x0D){
				c = ' ';
			}
			sb.append(c);
		}
		return sb.toString();
	}
}
