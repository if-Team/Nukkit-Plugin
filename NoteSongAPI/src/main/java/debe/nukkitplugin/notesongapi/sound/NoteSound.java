package debe.nukkitplugin.notesongapi.sound;

import cn.nukkit.Player;
import cn.nukkit.block.Block;
import cn.nukkit.level.sound.NoteBoxSound;
import cn.nukkit.math.Vector3;

public class NoteSound extends BaseSound{
	public static byte PIANO_OR_HARP = 0; // Any other material
	public static byte BASS_DRUM = 1; // Stone, SandStone, Ores, Brick, NetherRack, Opsidian, Quartz
	public static byte SNARE_DRUM = 2; // Sand, Gravel, SoulSand
	public static byte CLICKS_AND_STICKS = 3; // Glass, GlowStone
	public static byte BASS_GUITAR = 4; // Wood, Mushroom, Daylight Sensor, Wooden plate
	public byte instrument = 0;

	public NoteSound(byte instrument, byte pitch){
		super(pitch);
		this.instrument = instrument;
	}

	@Override
	public boolean equals(BaseSound sound){
		if(sound instanceof NoteSound){
			NoteSound noteSound = (NoteSound) sound;
			return this.pitch == noteSound.pitch && this.instrument == noteSound.instrument;
		}else{
			return super.equals(sound);
		}
	}

	@Override
	public void sendTo(Player player, Vector3 vec){
		Block block = player.level.getBlock(vec);
		if(block.getId() == Block.NOTEBLOCK){
			player.level.addSound(new NoteBoxSound(block, this.instrument, this.pitch), player);
		}else{
			player.level.sendBlocks(new Player[]{player}, new Vector3[]{Block.get(Block.NOTEBLOCK, 0, block)});
			player.level.addSound(new NoteBoxSound(block, this.instrument, this.pitch), player);
			player.level.sendBlocks(new Player[]{player}, new Vector3[]{block});
		}
	}
}
