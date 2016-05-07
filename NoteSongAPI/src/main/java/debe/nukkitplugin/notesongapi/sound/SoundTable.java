package debe.nukkitplugin.notesongapi.sound;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SoundTable<T extends BaseSound>{
	protected LinkedHashMap<Integer, ArrayList<T>> soundTable = new LinkedHashMap<Integer, ArrayList<T>>(){};

	public SoundTable(){}

	public SoundTable(LinkedHashMap<Integer, ArrayList<T>> soundTable){
		this.soundTable = soundTable;
	}

	public SoundTable(SoundTable<T> soundTable){
		this.soundTable = soundTable.getSoundTable();
	}

	public LinkedHashMap<Integer, ArrayList<T>> getSoundTable(){
		return new LinkedHashMap<Integer, ArrayList<T>>(this.soundTable);
	}

	public void setSoundTable(LinkedHashMap<Integer, ArrayList<T>> soundTable){
		this.soundTable = soundTable;
	}

	public ArrayList<T> getSounds(int tick){
		return this.existSounds(tick) ? new ArrayList<T>(this.soundTable.get(tick)) : new ArrayList<T>(){};
	}

	public void setSounds(int tick, ArrayList<T> sounds){
		this.soundTable.put(tick, sounds);
	}

	public Boolean existSounds(int tick){
		return this.soundTable.containsKey(tick) && !this.soundTable.get(tick).isEmpty();
	}

	public void addSound(int tick, T sound){
		ArrayList<T> sounds = this.existSounds(tick) ? this.soundTable.get(tick) : new ArrayList<T>(){};
		Boolean isExists = false;
		for(T oldSound : sounds){
			if(oldSound.equals(sound)){
				isExists = true;
				break;
			}
		}
		if(!isExists){
			sounds.add(sound);
			this.soundTable.put(tick, sounds);
		}
	}

	public int getLength(){
		// return ((TreeSet<Integer>) this.getSoundTable().keySet()).last();
		int length = 0;
		for(int tick : this.soundTable.keySet()){
			if(this.existSounds(tick) && length < tick){
				length = tick;
			}
		}
		return Integer.valueOf(length);
	}
}
