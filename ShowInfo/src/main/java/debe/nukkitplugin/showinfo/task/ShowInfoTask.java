package debe.nukkitplugin.showinfo.task;

import cn.nukkit.scheduler.PluginTask;
import debe.nukkitplugin.showinfo.ShowInfo;

public class ShowInfoTask extends PluginTask<ShowInfo>{
	public ShowInfoTask(ShowInfo owner){
		super(owner);
	}

	@Override
	public void onRun(int currentTick){
		this.getOwner().onTaskRun();
	}
}
