package com.ffsupver.asplor;

import com.ffsupver.asplor.entity.ModEntities;
import com.ffsupver.asplor.item.ModItemGroups;
import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.networking.ModPackets;
import com.ffsupver.asplor.recipe.ModRecipes;
import com.ffsupver.asplor.screen.ModScreenHandlers;
import com.ffsupver.asplor.sound.ModSounds;
import com.ffsupver.asplor.util.GoggleDisplays;
import com.ffsupver.asplor.villager.ModTraders;
import com.ffsupver.asplor.villager.ModVillagers;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.RegistryKeys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Asplor implements ModInitializer {
	public static final String MOD_ID = "asplor";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(Asplor.MOD_ID);



	//添加应力描述
	static {
		REGISTRATE.setTooltipModifierFactory(item -> {
			return new ItemDescription.Modifier(item, TooltipHelper.Palette.STANDARD_CREATE)
					.andThen(TooltipModifier.mapNull(KineticStats.create(item)));
		});
	}


	@Override
	public void onInitialize() {

		LOGGER.info("Hello Fabric world!");
		ModItems.registerModItems();
		ModItemGroups.registerModItemGroups();
		ModRecipes.registerRecipes();
		ModScreenHandlers.registerModScreenHandlers();
		ModSounds.registerModSounds();
		ModEntities.register();

		AllBlocks.register();
		AllBlockEntityTypes.register();
		AllRecipeTypes.register();
		REGISTRATE.register();

		AllEnergyStorages.registerEnergyStorages();
		AllBoilerHeaters.register();

		if (!Create.REGISTRATE.isRegistered(RegistryKeys.ITEM)) {
			Create.REGISTRATE.addRegisterCallback(RegistryKeys.ITEM, Asplor::registerAfterCreateItems);
		} else {
			registerAfterCreateItems();
		}


		GoggleDisplays.register();

		ModPackets.registerC2SPack();
	}

	private static void registerAfterCreateItems() {
		ModVillagers.registerVillagers();
		ModTraders.registerTraders();
		Asplor.LOGGER.info(" create items loaded :" + Create.REGISTRATE.isRegistered(RegistryKeys.ITEM));
	}

}