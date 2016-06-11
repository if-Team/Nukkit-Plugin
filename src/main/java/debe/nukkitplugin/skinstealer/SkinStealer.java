package debe.nukkitplugin.skinstealer;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.scheduler.AsyncTask;

public class SkinStealer extends PluginBase{
	@Override
	public void onEnable(){
		this.getServer().getPluginManager().registerEvents(new Listener(){
			protected SkinStealer plugin;

			public Listener init(SkinStealer plugin){
				this.plugin = plugin;
				return this;
			}

			@EventHandler(priority = EventPriority.HIGHEST)
			public void onPlayerJoin(PlayerJoinEvent event){
				Server.getInstance().getScheduler().scheduleAsyncTask(new AsyncTask(){
					protected SkinStealer plugin;
					protected Player player;
					protected BufferedImage skinBuffer = null;

					public AsyncTask init(SkinStealer plugin, Player player){
						this.plugin = plugin;
						this.player = player;
						return this;
					}

					@Override
					public void onRun(){
						Skin skin = player.getSkin();
						String skinModel = skin.getModel();
						if(skinModel.toLowerCase().startsWith("standard_custom")){ // STANDARD_CUSTOM & STANDARD_CUSTOMSLIM
							byte[] skinData = skin.getData();
							BufferedImage skinBuffer = new BufferedImage(64, (skinData.length / 4) / 64, BufferedImage.TYPE_INT_ARGB);
							int index;
							for(int x = 0; x < skinBuffer.getWidth(); x++){
								for(int y = 0; y < skinBuffer.getHeight(); y++){
									index = (x + y * 64) * 4;
									skinBuffer.setRGB(x, y, new Color(skinData[index] & 0xFF, skinData[index + 1] & 0xFF, skinData[index + 2] & 0xFF, skinData[index + 3] & 0xFF).getRGB());
								}
							}
							try{
								File folder = new File(this.plugin.getDataFolder().toString() + "/" + player.getName().toLowerCase() + "/");
								if(folder.exists() && folder.isDirectory()){
									for(File pngFile : folder.listFiles(new FilenameFilter(){
										public boolean accept(File directory, String fileName){
											return fileName.endsWith(".png");
										}
									})){
										if(this.isEquals(skinBuffer, ImageIO.read(pngFile))){
											this.plugin.getLogger().debug(this.player.getName() + "'s skin is not saved. This skin is equal to " + pngFile.getName());
											return;
										}
									}
								}else{
									folder.mkdirs();
								}
								this.skinBuffer = skinBuffer;
							}catch(IOException e){
								e.printStackTrace();
							}
						}else{
							this.plugin.getLogger().debug(this.player.getName() + "'s skin is not saved. This skin is not costum skin.");
						}
					}

					@Override
					public void onCompletion(Server server){
						if(this.skinBuffer != null){
							try{
								long time = System.currentTimeMillis();
								ImageIO.write(this.skinBuffer, "png", new File(this.plugin.getDataFolder().toString() + "/" + player.getName().toLowerCase() + "/", time + ".png"));
								this.plugin.getLogger().debug(this.player.getName() + "'s skin was saved to \"" + time + ".png\"");
							}catch(IOException e){
								e.printStackTrace();
							}
						}
					}

					public boolean isEquals(BufferedImage bufferA, BufferedImage bufferB){
						if(bufferA.getHeight() == bufferB.getHeight() && bufferA.getWidth() == bufferB.getWidth()){
							for(int x = 0; x < bufferA.getWidth(); x++){
								for(int y = 0; y < bufferA.getHeight(); y++){
									if(bufferA.getRGB(x, y) != bufferB.getRGB(x, y)){
										return false;
									}
								}
							}
						}
						return true;
					}
				}.init(this.plugin, event.getPlayer()));
			}
		}.init(this), this);
	}
}
