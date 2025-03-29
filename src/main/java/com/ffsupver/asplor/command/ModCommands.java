package com.ffsupver.asplor.command;

import com.ffsupver.asplor.Asplor;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.util.Identifier;

public class ModCommands {
    public static void register(){
        CommandRegistrationCallback.EVENT.register(new Identifier(Asplor.MOD_ID,"worldtp"), (dispatcher, registryAccess, environment) -> {
            WorldTeleportCommand.register(dispatcher);
        });
    }
}
