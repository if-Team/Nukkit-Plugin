package debe.nukkitplugin.itemdisplay.command.subcommand;

import java.util.ArrayList;

public class SubCommandData{
	protected String command;
	protected ArrayList<String> aliases;

	public SubCommandData(String command, ArrayList<String> aliases){
		this.command = command;
		this.aliases = aliases;
	}

	public String getCommand(){
		return this.command;
	}

	public void setCommand(String name){
		this.command = name;
	}

	public ArrayList<String> getAliases(){
		return this.aliases;
	}

	public void setAliases(ArrayList<String> aliases){
		this.aliases = aliases;
	}

	public boolean equals(String string){
		return this.command.equalsIgnoreCase(string) || this.aliases.contains(string.toLowerCase());
	}
}
