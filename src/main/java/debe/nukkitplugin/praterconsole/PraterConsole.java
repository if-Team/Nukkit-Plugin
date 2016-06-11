package debe.nukkitplugin.praterconsole;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import cn.nukkit.plugin.PluginBase;
import debe.nukkitplugin.praterconsole.listener.ConsoleCommandListener;

public class PraterConsole extends PluginBase{
	private static String format = "";

	public static String getFormat(){
		return PraterConsole.format;
	}

	@Override
	public void onEnable(){
		this.saveResource("defaults/Format.txt", "Format.txt", false);
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File(this.getDataFolder() + "/Format.txt")), StandardCharsets.UTF_8));
			String temp;
			StringBuilder stringBuilder = new StringBuilder();
			while((temp = reader.readLine()) != null){
				stringBuilder.append(temp).append("\n");
			}
			reader.close();
			PraterConsole.format = stringBuilder.toString().trim();
		}catch(Exception e){
			PraterConsole.format = "";
		}
		this.getServer().getPluginManager().registerEvents(new ConsoleCommandListener(), this);
	}
}
