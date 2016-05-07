package debe.nukkitplugin.notesongapi.player;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import cn.nukkit.Player;
import cn.nukkit.Server;
import debe.nukkitplugin.notesongapi.song.BaseSong;
import debe.nukkitplugin.notesongapi.sound.BaseSound;

public class BaseBroadcastPlayer<T extends BaseSong<S>, S extends BaseSound>extends BasePlayer<T, S>{
	public BaseBroadcastPlayer(ArrayList<T> songs){
		super(songs, new Player[]{}, false, false, 0);
	}

	public BaseBroadcastPlayer(ArrayList<T> songs, Player[] recipients, boolean loop, boolean shuffle, int delay){
		super(songs, recipients, loop, shuffle, delay);
	}

	public BaseBroadcastPlayer(LinkedHashMap<Integer, T> songs, LinkedHashMap<String, Player> recipients, boolean loop, boolean shuffle, int delay){
		super(songs, recipients, loop, shuffle, delay);
	}

	@Override
	public Player[] getRecipients(){
		return Server.getInstance().getOnlinePlayers().values().stream().toArray(Player[]::new);
	}

	@Override
	public void sendSound(Player player, S sound){
		sound.sendTo(player, player.add(player.getWidth() / 2, player.getEyeHeight() + 0.5, player.getLength() / 2));
	}
}
