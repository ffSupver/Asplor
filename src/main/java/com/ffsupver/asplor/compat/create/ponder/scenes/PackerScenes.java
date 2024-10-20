package com.ffsupver.asplor.compat.create.ponder.scenes;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.entity.custom.AlloyChestEntity;
import com.ffsupver.asplor.item.ModItems;
import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.EntityElement;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class PackerScenes {
    public static void Packer(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("packer", "xxx");
        scene.configureBasePlate(0, 0, 14);
        scene.scaleSceneView(0.5f);
        scene.world.showSection(util.select.layer(0), Direction.UP);




        scene.idle(15);


        scene.world.showSection(util.select.position(5,3,9),Direction.DOWN);
        scene.overlay.showText(30)
                .placeNearTarget()
                .text("pack")
                .pointAt(util.vector.of(5.5, 3.5, 9.5));

        scene.idle(20);

        scene.addKeyframe();


        BlockPos chestPos = util.grid.at(5,3,9);
        ItemStack packerItem = ModItems.PACKER.asStack();
        Box alloyChestBB = AllBlocks.ALLOY_CHEST.getDefaultState()
                .getOutlineShape(null, null)
                .getBoundingBox();


        scene.overlay.showControls(new InputWindowElement(util.vector.topOf(chestPos), Pointing.DOWN).rightClick()
                .withItem(packerItem), 20);
        scene.idle(5);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, chestPos, alloyChestBB.offset(chestPos), 15);

        scene.idle(15);

        scene.world.setBlocks(util.select.position(5,3,9), Blocks.AIR.getDefaultState(),false);
        ElementLink<EntityElement> chestEntity =
                scene.world.createEntity(world -> {
                    return new AlloyChestEntity(
                            world,
                            util.vector.centerOf(5,3,9),
                            util.vector.of(0, 0, 0)
                    );
                });

        scene.idle(10);

        scene.world.showSection(util.select.layer(1), Direction.DOWN);
        scene.world.showSection(util.select.position(5,2,5),Direction.DOWN);
        scene.world.showSection(util.select.position(5,3,4),Direction.DOWN);

        float speed = -32;
        scene.world.modifyKineticSpeed(util.select.everywhere(), f->speed);
        scene.world.setKineticSpeed(util.select.position(8,0,6),-speed/2);

        scene.idle(40);
        scene.addKeyframe();
        scene.world.showSection(util.select.position(5,2,3),Direction.DOWN);
        scene.idle(30);

        BlockPos newChestPos = util.grid.at(5,2,3);
        scene.world.modifyEntity(chestEntity, Entity::discard);
        scene.world.setBlock(newChestPos,AllBlocks.ALLOY_CHEST.getDefaultState(),false);

        scene.idle(2);
        scene.overlay.showText(30)
                .placeNearTarget()
                .text("unpack")
                .pointAt(util.vector.of(5.5, 2.5, 3.5));

        scene.idle(30);

        scene.markAsFinished();
    }
}
