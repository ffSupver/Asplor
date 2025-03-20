package com.ffsupver.asplor.item.item;

import com.ffsupver.asplor.Asplor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.StructurePlacementData;
import net.minecraft.structure.StructureTemplate;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.InvalidIdentifierException;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.Optional;

public class SpaceStationItem extends Item {
    public static final Identifier STRUCTURE_ID = new Identifier(Asplor.MOD_ID,"simple_space_station");
    public SpaceStationItem(Settings settings) {
        super(settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (world.isClient()){
            return TypedActionResult.success(itemStack);
        }else {
            ServerWorld serverWorld = (ServerWorld) world;
            loadStructure(serverWorld,STRUCTURE_ID,user.getBlockPos());
            return TypedActionResult.success(itemStack);
        }
    }

    public static void loadStructure(ServerWorld serverWorld, Identifier templateName, BlockPos blockPos){
        StructureTemplateManager structureTemplateManager = serverWorld.getStructureTemplateManager();

        Optional<StructureTemplate> optional;
        try {
            optional = structureTemplateManager.getTemplate(templateName);

            if (optional.isPresent()){
                StructureTemplate structureTemplate = optional.get();

                Vec3i size = structureTemplate.getSize();

                StructurePlacementData structurePlacementData = new StructurePlacementData();

                BlockPos placePos = blockPos.add(new Vec3i(-size.getX() / 2,0,-size.getZ() / 2));

                structureTemplate.place(serverWorld,placePos,placePos,structurePlacementData, Random.create(),2);
            }

        } catch (InvalidIdentifierException e) {
            throw new RuntimeException(e);
        }
    }
}
