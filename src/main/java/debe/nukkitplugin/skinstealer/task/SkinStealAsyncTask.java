package debe.nukkitplugin.skinstealer.task;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import javax.imageio.ImageIO;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.entity.data.Skin;
import cn.nukkit.scheduler.AsyncTask;
import debe.nukkitplugin.skinstealer.SkinStealer;

public class SkinStealAsyncTask extends AsyncTask{
	protected SkinStealer plugin;
	protected Player player;
	protected File saveFile;
	protected BufferedImage skinBuffer = null;

	public SkinStealAsyncTask(SkinStealer plugin, Player player){
		this.plugin = plugin;
		this.player = player;
	}

	@Override
	public void onRun(){
		Skin skin = player.getSkin();
		String skinModel = skin.getModel();
		if(skinModel.equalsIgnoreCase("standard_custom") || skinModel.equalsIgnoreCase("standard_customslim")){
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
				folder.mkdirs();
				boolean equal;
				BufferedImage pngBuffer;
				for(File pngFile : folder.listFiles(new FilenameFilter(){
					public boolean accept(File directory, String fileName){
						return fileName.endsWith(".png");
					}
				})){
					equal = false;
					pngBuffer = ImageIO.read(pngFile);
					if(skinBuffer.getHeight() == pngBuffer.getHeight() && skinBuffer.getWidth() == pngBuffer.getWidth()){
						equal = true;
						for(int x = 0; x < skinBuffer.getWidth(); x++){
							for(int y = 0; y < skinBuffer.getHeight(); y++){
								if(skinBuffer.getRGB(x, y) != pngBuffer.getRGB(x, y)){
									equal = false;
									break;
								}
							}
						}
					}
					if(equal){
						this.plugin.getLogger().debug(this.player.getName() + "'s skin is not saved. This skin is equal to " + pngFile.getName());
						return;
					}
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
				ImageIO.write(this.skinBuffer, "png", new File(this.plugin.getDataFolder().toString() + "/" + player.getName().toLowerCase() + "/", System.currentTimeMillis() + ".png"));
				this.plugin.getLogger().debug(this.player.getName() + "'s skin was saved");
			}catch(IOException e){
				e.printStackTrace();
			}
		}
	}
}
