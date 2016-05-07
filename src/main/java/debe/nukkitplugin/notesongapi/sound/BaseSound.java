package debe.nukkitplugin.notesongapi.sound;

import cn.nukkit.Player;
import cn.nukkit.math.Vector3;

abstract public class BaseSound{
	public byte pitch = 0;;

	public BaseSound(){}

	public BaseSound(byte pitch){
		this.pitch = pitch;
	}

	public boolean equals(BaseSound sound){
		return this.pitch == sound.pitch;
	}

	public abstract void sendTo(Player player, Vector3 vec);
}
