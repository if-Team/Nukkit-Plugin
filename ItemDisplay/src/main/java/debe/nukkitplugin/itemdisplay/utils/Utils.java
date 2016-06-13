package debe.nukkitplugin.itemdisplay.utils;

public class Utils{
	public static int toInt(String content){
		try{
			return (int) Double.parseDouble(content);
		}catch(Exception e){}
		return 0;
	}

	public static double toDouble(String content){
		try{
			return Double.parseDouble(content);
		}catch(Exception e){}
		return 0;
	}

	public static boolean toBoolean(String content){
		switch(content.toLowerCase().trim()){
			case "on":
			case "true":
			case "yes":
				return true;
			case "off":
			case "false":
			case "no":
				return false;
		}
		return false;
	}

	public static boolean isInt(String content){
		try{
			Integer.parseInt(content);
			return true;
		}catch(Exception e){}
		return false;
	}
}
