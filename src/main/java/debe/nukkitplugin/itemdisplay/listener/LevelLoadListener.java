package debe.nukkitplugin.itemdisplay.listener;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.level.LevelLoadEvent;
import debe.nukkitplugin.itemdisplay.utils.FileUtils;

public class LevelLoadListener implements Listener{
	public LevelLoadListener(){}

	@EventHandler()
	public void onLevelLoad(LevelLoadEvent event){
		FileUtils.loadData(event.getLevel().getFolderName());
	}
}
