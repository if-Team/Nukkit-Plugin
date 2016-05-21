package debe.nukkitplugin.simpleserversong;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;

import cn.nukkit.plugin.PluginBase;
import debe.nukkitplugin.notesongapi.decoder.NBSDecoder;
import debe.nukkitplugin.notesongapi.song.NoteSong;
import debe.nukkitplugin.notesongapi.utils.BinaryStream;
import debe.nukkitplugin.simpleserversong.player.ServerNotePlayer;

public class SimpleServerSong extends PluginBase{
	@Override
	public void onEnable(){
		try{
			File dataFolder = this.getDataFolder();
			dataFolder.mkdirs();
			File[] nbsFiles = dataFolder.listFiles(new FilenameFilter(){
				public boolean accept(File directory, String fileName){
					return fileName.endsWith(".nbs");
				}
			});
			ArrayList<NoteSong> songs = new ArrayList<NoteSong>();
			Arrays.stream(nbsFiles).forEach(nbsFile->{
				try{
					BinaryStream stream = new BinaryStream(nbsFile);
					NBSDecoder decoder = new NBSDecoder(stream);
					NoteSong song = decoder.getSong();
					songs.add(song);
					this.getLogger().info("Loaded song : [" + song.getId() + "] " + song.getOriginalName());
				}catch(Exception e){
					this.getLogger().warning("Failed load song : " + nbsFile.getName());
				}
			});
			if(songs.size() == 0){
				this.getLogger().info("You not have song");
			}else{
				ServerNotePlayer player = new ServerNotePlayer(songs, this);
				player.play();
				this.getLogger().info("ServerSongPlayer is enabled.");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
