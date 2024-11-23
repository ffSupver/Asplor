package com.ffsupver.asplor;

import com.ffsupver.asplor.networking.ModPackets;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class AllKeys {
    private static final KeyBinding OPEN_BACKPACK = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.asplor.open_backpack",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_B,
            "category.asplor.key"
    ));
    public static final KeyBinding LARGE_MAP_MOVE_UP = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.asplor.large_map.move_up",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_UP,
            "category.asplor.key"
    ));
    public static final KeyBinding LARGE_MAP_MOVE_DOWN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.asplor.large_map.move_down",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_DOWN,
            "category.asplor.key"
    ));

    public static final KeyBinding LARGE_MAP_MOVE_LEFT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.asplor.large_map.move_left",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_LEFT,
            "category.asplor.key"
    ));

    public static final KeyBinding LARGE_MAP_MOVE_RIGHT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.asplor.large_map.move_right",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT,
            "category.asplor.key"
    ));
    public static final KeyBinding LARGE_MAP_ZOOM_IN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.asplor.large_map.zoom_in",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_EQUAL,
            "category.asplor.key"
    ));
    public static final KeyBinding LARGE_MAP_ZOOM_OUT = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.asplor.large_map.zoom_out",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_MINUS,
            "category.asplor.key"
    ));
    public static void register(){
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_BACKPACK.wasPressed()) {
                ClientPlayNetworking.send(ModPackets.OPEN_BACKPACK, PacketByteBufs.create());
            }
        });

    }
}
