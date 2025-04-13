package com.ffsupver.asplor.sound;

import com.ffsupver.asplor.Asplor;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public class ModSounds {
    public static final SoundEvent ALLOY_CHEST_OPEN=registerSoundEvent("alloy_chest_open");
    public static final SoundEvent ALLOY_CHEST_CLOSE=registerSoundEvent("alloy_chest_close");
    public static final SoundEvent ALLOY_BLOCK_BREAK=registerSoundEvent("alloy_block_break");
    public static final SoundEvent ALLOY_BLOCK_STEP=registerSoundEvent("alloy_block_step");
    public static final SoundEvent ALLOY_BLOCK_PLACE=registerSoundEvent("alloy_block_place");
    public static final SoundEvent ALLOY_BLOCK_HIT=registerSoundEvent("alloy_block_hit");
    public static final SoundEvent ALLOY_BLOCK_FALL=registerSoundEvent("alloy_block_fall");
    public static final SoundEvent DIVIDER_CUT = registerSoundEvent("divider_cut");
    public static final SoundEvent ASSEMBLER_WORK = registerSoundEvent("assembler_work");
    public static final SoundEvent ELECTRICITY_WORK = registerSoundEvent("electricity_work");
    public static final SoundEvent ZOMBIFIED_COSMONAUT_AMBIENT = registerSoundEvent("zombified_cosmonaut.ambient");
    public static final SoundEvent ZOMBIFIED_COSMONAUT_DEATH = registerSoundEvent("zombified_cosmonaut.death");
    public static final SoundEvent ZOMBIFIED_COSMONAUT_HURT = registerSoundEvent("zombified_cosmonaut.hurt");

    public static final BlockSoundGroup ALLOY_BLOCK_SOUND_GROUP=new BlockSoundGroup(1f,1f
            ,ModSounds.ALLOY_BLOCK_BREAK,ModSounds.ALLOY_BLOCK_STEP,ModSounds.ALLOY_BLOCK_PLACE,ModSounds.ALLOY_BLOCK_HIT,ModSounds.ALLOY_BLOCK_FALL);
    public static SoundEvent registerSoundEvent(String name){
        Identifier identifier=new Identifier(Asplor.MOD_ID,name);
        return Registry.register(Registries.SOUND_EVENT,identifier,SoundEvent.of(identifier));
    }
    public static void registerModSounds(){

    }
}
