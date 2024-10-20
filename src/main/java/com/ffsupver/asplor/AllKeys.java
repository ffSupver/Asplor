package com.ffsupver.asplor;

import com.ffsupver.asplor.networking.ModPackets;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class AllKeys {
    private static KeyBinding openBackpack = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.asplor.open_backpack",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.asplor.key"
    ));
    public static void register(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openBackpack.wasPressed()) {
//                client.player.sendMessage(Text.literal("zombie was spawned!"), false);
                ClientPlayNetworking.send(ModPackets.OPEN_BACKPACK, PacketByteBufs.create());
            }
        });
    }
}
