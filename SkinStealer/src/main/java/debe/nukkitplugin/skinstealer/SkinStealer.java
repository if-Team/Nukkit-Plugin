package debe.nukkitplugin.skinstealer;

import cn.nukkit.plugin.PluginBase;
import debe.nukkitplugin.skinstealer.listener.PlayerJoinListener;

public class SkinStealer extends PluginBase{
	@Override
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(new PlayerJoinListener(this), this);
	}
}
