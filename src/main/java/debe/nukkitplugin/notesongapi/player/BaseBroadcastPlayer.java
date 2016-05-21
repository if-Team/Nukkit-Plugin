package debe.nukkitplugin.notesongapi.player;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import cn.nukkit.Player;
import cn.nukkit.Server;
import debe.nukkitplugin.notesongapi.song.BaseSong;
import debe.nukkitplugin.notesongapi.sound.BaseSound;

public class BaseBroadcastPlayer<T extends BaseSong<S>, S extends BaseSound>extends BasePlayer<T, S>{
	public BaseBroadcastPlayer(ArrayList<T> songs){
		super(songs, new ArrayList<Player>(), false, false, 0);
	}

	public BaseBroadcastPlayer(ArrayList<T> songs, ArrayList<Player> recipients, boolean loop, boolean shuffle, int delay){
		super(songs, recipients, loop, shuffle, delay);
	}

	public BaseBroadcastPlayer(LinkedHashMap<Integer, T> songs, Map<String, Player> recipients, boolean loop, boolean shuffle, int delay){
		super(songs, recipients, loop, shuffle, delay);
	}

	@Override
	public void sendSound(Player player, S sound){
		sound.sendTo(player, player.add(0.3 /* player.getWidth() / 2 */, 2.12 /* player.getEyeHeight() + 0.5 */, 0.3 /* player.getLength() / 2 */));
	}

	@Override
	public ArrayList<Player> getRecipients(){
		return new ArrayList<Player>(Server.getInstance().getOnlinePlayers().values());
	}
}
