package debe.nukkitplugin.notesongapi.sound;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SoundTable<T extends BaseSound>{
	protected Map<Integer, ArrayList<T>> soundTable = new HashMap<Integer, ArrayList<T>>();
	protected int length = 0;

	public SoundTable(){}

	public SoundTable(SoundTable<T> soundTable){
		this.setSoundTable(soundTable);
	}

	public SoundTable(Map<Integer, ArrayList<T>> soundTable){
		this.setSoundTable(soundTable);
	}

	public Map<Integer, ArrayList<T>> getSoundTable(){
		return new HashMap<Integer, ArrayList<T>>(this.soundTable);
	}

	public void setSoundTable(SoundTable<T> soundTable){
		this.setSoundTable(soundTable.getSoundTable());
	}

	public void setSoundTable(Map<Integer, ArrayList<T>> soundTable){
		this.soundTable = soundTable;
		this.length = this.soundTable.keySet().stream().mapToInt(Integer::intValue).max().getAsInt();
	}

	public ArrayList<T> getSounds(int tick){
		return new ArrayList<T>(this.soundTable.get(tick));
	}

	public void setSounds(int tick, ArrayList<T> sounds){
		if(!sounds.isEmpty()){
			this.soundTable.put(tick, sounds);
			if(this.length < tick){
				this.length = tick;
			}
		}
	}

	public boolean existSounds(int tick){
		return this.soundTable.containsKey(tick) && !this.soundTable.get(tick).isEmpty();
	}

	public void addSound(int tick, T sound){
		ArrayList<T> sounds = this.soundTable.get(tick);
		if(sounds == null){
			this.soundTable.put(tick, new ArrayList<T>(){
				{
					add(sound);
				}
			});
			if(this.length < tick){
				this.length = tick;
			}
		}else if(sounds.stream().noneMatch(oldSound->oldSound.equals(sound))){
			sounds.add(sound);
			this.soundTable.put(tick, sounds);
		}
	}

	public int getLength(){
		return this.length;
	}
}
