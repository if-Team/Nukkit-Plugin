package debe.nukkitplugin.autogarbagecollector.task;

import java.util.HashMap;

import cn.nukkit.Server;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.scheduler.PluginTask;
import debe.nukkitplugin.autogarbagecollector.AutoGarbageCollector;
import debe.nukkitplugin.autogarbagecollector.utils.Translation;

public class GarbageCollectorTask extends PluginTask<AutoGarbageCollector>{
	public GarbageCollectorTask(AutoGarbageCollector owner){
		super(owner);
	}

	@Override
	public void onRun(int currentTick){
		long memory = Runtime.getRuntime().freeMemory();
		int collectedChunks = 0;
		int collectedEntities = 0;
		int collectedBlockEntities = 0;
		for(Level level : Server.getInstance().getLevels().values()){
			int chunksCount = level.getChunks().size();
			int entitieschunkCount = level.getEntities().length;
			int blockEntitiesCount = level.getBlockEntities().size();
			level.doChunkGarbageCollection();
			try{
				level.unloadChunks(true);
			}catch(Exception e){}
			collectedChunks += chunksCount - level.getChunks().size();
			collectedEntities += entitieschunkCount - level.getEntities().length;
			collectedBlockEntities += blockEntitiesCount - level.getBlockEntities().size();
			level.clearCache(true);
		}
		System.gc();
		HashMap<String, Double> map = new HashMap<String, Double>();
		map.put("consloe.notice.garbageCollect.memory", NukkitMath.round(((Runtime.getRuntime().freeMemory() - memory) / 1048576), 2));
		map.put("consloe.notice.garbageCollect.chunks", (double) collectedChunks);
		map.put("consloe.notice.garbageCollect.entities", (double) collectedEntities);
		map.put("consloe.notice.garbageCollect.blockEntities", (double) collectedBlockEntities);
		map.entrySet().stream().filter(entry->!Translation.translate(entry.getKey()).trim().equals("") && entry.getValue() > 0).forEach(entry->{
			Server.getInstance().getLogger().notice(Translation.translate("colors.success") + Translation.translate("prefix") + " " + Translation.translate(entry.getKey(), String.valueOf(entry.getValue())));
		});
	}
}
