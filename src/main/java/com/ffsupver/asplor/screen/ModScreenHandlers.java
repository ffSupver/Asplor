package com.ffsupver.asplor.screen;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.screen.alloyChest.AlloyChestScreen;
import com.ffsupver.asplor.screen.alloyChest.AlloyChestScreenHandler;
import com.ffsupver.asplor.screen.assembler.AssemblerScreen;
import com.ffsupver.asplor.screen.assembler.AssemblerScreenHandler;
import com.ffsupver.asplor.screen.backpack.BackpackLargeHandler;
import com.ffsupver.asplor.screen.backpack.BackpackLargeScreen;
import com.ffsupver.asplor.screen.backpack.BackpackSmallHandler;
import com.ffsupver.asplor.screen.backpack.BackpackSmallScreen;
import com.ffsupver.asplor.screen.cargoRocket.CargoRocketScreen;
import com.ffsupver.asplor.screen.cargoRocket.CargoRocketScreenHandler;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.minecraft.client.gui.screen.ingame.HandledScreens;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlers {
    public static final ScreenHandlerType<AlloyChestScreenHandler> ALLOY_CHEST_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER,new Identifier(Asplor.MOD_ID,"alloy_chest"),
                    new ExtendedScreenHandlerType<>(AlloyChestScreenHandler::new));
    public static final ScreenHandlerType<AssemblerScreenHandler> ASSEMBLER_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER,new Identifier(Asplor.MOD_ID,"assembler"),
                 new ScreenHandlerType( AssemblerScreenHandler::new, FeatureFlags.VANILLA_FEATURES));

    public static final ScreenHandlerType<BackpackLargeHandler> BACKPACK_LARGE_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER,new Identifier(Asplor.MOD_ID,"backpack_large"),
                    new ExtendedScreenHandlerType<>(BackpackLargeHandler::new));
    public static final ScreenHandlerType<BackpackSmallHandler> BACKPACK_SMALL_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER,new Identifier(Asplor.MOD_ID,"backpack_small"),
                    new ExtendedScreenHandlerType<>(BackpackSmallHandler::new));
    public static final ScreenHandlerType<CargoRocketScreenHandler> CARGO_ROCKET_SCREEN_HANDLER =
            Registry.register(Registries.SCREEN_HANDLER,new Identifier(Asplor.MOD_ID,"cargo_rocket"),
                    new ExtendedScreenHandlerType<>(CargoRocketScreenHandler::new));
    public static void registerModScreenHandlers(){
    }

    public static void registerModScreens(){
        HandledScreens.register(ModScreenHandlers.ALLOY_CHEST_SCREEN_HANDLER, AlloyChestScreen::new);
        HandledScreens.register(ModScreenHandlers.ASSEMBLER_SCREEN_HANDLER, AssemblerScreen::new);

        HandledScreens.register(ModScreenHandlers.BACKPACK_LARGE_SCREEN_HANDLER, BackpackLargeScreen::new);
        HandledScreens.register(ModScreenHandlers.BACKPACK_SMALL_SCREEN_HANDLER, BackpackSmallScreen::new);
        HandledScreens.register(ModScreenHandlers.CARGO_ROCKET_SCREEN_HANDLER, CargoRocketScreen::new);
    }

}
