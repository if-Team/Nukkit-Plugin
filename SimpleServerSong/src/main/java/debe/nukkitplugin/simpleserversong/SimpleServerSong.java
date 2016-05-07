package debe.nukkitplugin.simpleserversong;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import cn.nukkit.plugin.PluginBase;
import debe.nukkitplugin.notesongapi.decoder.BinaryStream;
import debe.nukkitplugin.notesongapi.decoder.NBSDecoder;
import debe.nukkitplugin.notesongapi.song.NoteSong;
import debe.nukkitplugin.simpleserversong.player.ServerNotePlayer;

public class SimpleServerSong extends PluginBase{
	@Override
	public void onEnable(){
		try{
			File dataFolder = this.getDataFolder();
			if(!dataFolder.exists()){
				dataFolder.mkdirs();
				this.getLogger().info("You not have song");
			}else{
				File[] nbsFiles = dataFolder.listFiles(new FilenameFilter(){
					public boolean accept(File directory, String fileName){
						return fileName.endsWith(".nbs");
					}
				});
				ArrayList<NoteSong> songs = new ArrayList<NoteSong>();
				NBSDecoder decoder;
				BinaryStream stream;
				NoteSong song;
				for(File nbsFile : nbsFiles){
					stream = new BinaryStream(nbsFile);
					decoder = new NBSDecoder(stream);
					song = decoder.getSong();
					songs.add(song);
					this.getLogger().info("Loaded song : [" + song.getId() + "] " + song.getOriginalName());
				}
				if(songs.size() == 0){
					this.getLogger().info("You not have song");
				}else{
					ServerNotePlayer player = new ServerNotePlayer(songs, this);
					this.getLogger().info("ServerSongPlayer is enabled.");
					player.play();
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void onRun(int currentTick){}
}
