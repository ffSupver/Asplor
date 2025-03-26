package com.ffsupver.asplor;

import com.ffsupver.asplor.block.smartMechanicalArm.ToolTypes;
import com.ffsupver.asplor.enchantment.ModEnchantments;
import com.ffsupver.asplor.entity.ModEntities;
import com.ffsupver.asplor.item.ModItemGroups;
import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.item.item.largeMap.LargeMapState;
import com.ffsupver.asplor.item.item.singleItemCell.ICellHandlerRegister;
import com.ffsupver.asplor.networking.ModPackets;
import com.ffsupver.asplor.planet.PlanetData;
import com.ffsupver.asplor.recipe.ModRecipes;
import com.ffsupver.asplor.screen.ModScreenHandlers;
import com.ffsupver.asplor.sound.ModSounds;
import com.ffsupver.asplor.structure.ModStructureTypes;
import com.ffsupver.asplor.util.GoggleDisplays;
import com.ffsupver.asplor.villager.ModTraders;
import com.ffsupver.asplor.villager.ModVillagers;
import com.ffsupver.asplor.world.WorldRenderingData;
import com.simibubi.create.Create;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import com.simibubi.create.foundation.item.TooltipHelper;
import com.simibubi.create.foundation.item.TooltipModifier;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.entity.decoration.painting.PaintingVariant;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

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

		LOGGER.info("Loading Asplor! ");

		onAddReloadListener();

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

		ModLootTables.register();
		ModEnchantments.register();

		ModPointOfInterestTypes.register();

		if (!Create.REGISTRATE.isRegistered(RegistryKeys.ITEM)) {
			Create.REGISTRATE.addRegisterCallback(RegistryKeys.ITEM, Asplor::registerAfterCreateItems);
		} else {
			registerAfterCreateItems();
		}

		ModStructureTypes.register();

		GoggleDisplays.register();

		ModPackets.registerC2SPack();
		ToolTypes.register();
		ICellHandlerRegister.register();



		LargeMapState.loadMapIcon();

		Registry.register(Registries.PAINTING_VARIANT,new Identifier(MOD_ID,"icon"),new PaintingVariant(64,64));

		WorldRenderingData.registerListener();

	}

	private static void registerAfterCreateItems() {
		ModVillagers.registerVillagers();
		ModTraders.registerTraders();
		Asplor.LOGGER.info(" create items loaded :" + Create.REGISTRATE.isRegistered(RegistryKeys.ITEM));
	}

	public static void onAddReloadListener() {
		onAddReloadListener((id, listener) -> {
			ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new IdentifiableResourceReloadListener() {
				public Identifier getFabricId() {
					return id;
				}
				public @NotNull CompletableFuture<Void> reload(@NotNull ResourceReloader.@NotNull Synchronizer synchronizer, @NotNull ResourceManager manager, @NotNull Profiler prepareProfiler, @NotNull Profiler applyProfiler, @NotNull Executor prepareExecutor, @NotNull Executor applyExecutor) {
					return listener.reload(synchronizer, manager, prepareProfiler, applyProfiler, prepareExecutor, applyExecutor);
				}
			});
		});
	}
	public static void onAddReloadListener(BiConsumer<Identifier, ResourceReloader> registry) {
		registry.accept(new Identifier(MOD_ID, "planets"), new PlanetData());
	}




}