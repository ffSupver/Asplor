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
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class SpaceTeleporterScenes {
    public static void teleport(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("teleport", "xxx");
        scene.configureBasePlate(0, 0, 10);

        ElementLink<WorldSectionElement> overWorld = scene.world.showIndependentSection(util.select.layers(0, 3), Direction.DOWN);
        ElementLink<WorldSectionElement> end = scene.world.showIndependentSection(util.select.layers(5,5),Direction.DOWN);

        scene.world.moveSection(overWorld, util.vector.of(0, 50, 0), 0);
        scene.world.moveSection(end,util.vector.of(0, -5, 0), 0);




        scene.idle(5);


        BlockPos targetPos = util.grid.at(4,1,4);
        ItemStack anchorItem = ModItems.SPACE_TELEPORTER_ANCHOR.getDefaultStack();
        Box targetBlockBB = Blocks.DIAMOND_BLOCK.getDefaultState()
                .getOutlineShape(null, null)
                .getBoundingBox();
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, targetPos, targetBlockBB.offset(targetPos), 10);
        scene.overlay.showControls(new InputWindowElement(util.vector.topOf(targetPos), Pointing.DOWN).whileSneaking().rightClick()
                .withItem(anchorItem), 10);
        scene.overlay.showText(30)
                .text("anchor")
                .pointAt(util.vector.topOf(targetPos));

        scene.idle(40);

        scene.addKeyframe();

        scene.world.moveSection(end,util.vector.of(0, 55, 0), 0);
        scene.world.moveSection(overWorld, util.vector.of(0, -50, 0), 0);

        BlockPos teleporterPos = util.grid.at(5,1,4);
        Box teleporterBlockBB = AllBlocks.SPACE_TELEPORTER.getDefaultState()
                .getOutlineShape(null, null)
                .getBoundingBox();
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, teleporterPos, teleporterBlockBB.offset(teleporterPos), 10);
        scene.overlay.showControls(new InputWindowElement(util.vector.topOf(teleporterPos),Pointing.DOWN).rightClick()
                .withItem(anchorItem),10);
        scene.overlay.showText(30)
                .text("set_target")
                .pointAt(util.vector.topOf(teleporterPos));
        scene.idle(40);

        scene.addKeyframe();

        float speed = -32;
        scene.world.setKineticSpeed(util.select.everywhere(), speed);
        scene.world.setKineticSpeed(util.select.position(9,0,6),-speed/2);
        ElementLink<EntityElement> chestEntity =
                scene.world.createEntity(world -> {
                    return new AlloyChestEntity(
                            world,
                            util.vector.centerOf(5,3,9),
                            util.vector.of(0, 0, 0)
                    );
                });

        scene.overlay.showText(30)
                .text("teleport")
                .pointAt(util.vector.topOf(teleporterPos));

        scene.idle(65);

        scene.world.modifyEntity(chestEntity,entity -> {
            entity.setNoGravity(true);
            entity.noClip=true;
            entity.setVelocity(-0.055,5,0.01);
        });

        scene.idle(20);

        scene.addKeyframe();
        scene.world.moveSection(end,util.vector.of(0, -55, 0), 0);
        scene.world.moveSection(overWorld, util.vector.of(0, 50, 0), 0);
        scene.idle(5);
        scene.world.modifyEntity(chestEntity, entity -> {
            entity.setVelocity(0,-5,0);
        });

        scene.idle(25);

        scene.overlay.showText(30)
                .text("teleported")
                .pointAt(util.vector.topOf(targetPos));
        scene.world.modifyEntity(chestEntity,entity -> {
            entity.noClip = false;
        });

        scene.idle(30);

        scene.markAsFinished();
    }
}
