package com.ffsupver.asplor.util;

import com.ffsupver.asplor.screen.worldAdder.WorldAdderScreen;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public final class ModUtil {
    @Environment(EnvType.CLIENT)
    public static void openWorldAdderScreen(PlayerEntity user){
            MinecraftClient.getInstance().setScreen(new WorldAdderScreen(Text.translatable("asplor.screen.world_adder")));
    }
}
