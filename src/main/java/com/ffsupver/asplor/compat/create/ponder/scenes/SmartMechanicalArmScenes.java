package com.ffsupver.asplor.compat.create.ponder.scenes;

import com.ffsupver.asplor.AllBlocks;
import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;

public class SmartMechanicalArmScenes {
    public static void use(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("smart_mechanical_arm.use", "xxx");
        scene.configureBasePlate(0, 0, 11);
        scene.scaleSceneView(0.5f);
        scene.world.showSection(util.select.layer(0), Direction.UP);

        scene.world.showSection(util.select.position(3,1,3),Direction.DOWN);
        scene.world.showSection(util.select.position(4,1,7),Direction.DOWN);
        scene.world.showSection(util.select.position(6,1,4),Direction.DOWN);
        scene.world.showSection(util.select.position(8,1,2),Direction.DOWN);

        scene.idle(5);

        ItemStack smartMechanicalArmStack = new ItemStack(AllBlocks.SMART_MECHANICAL_ARM,1);

        Box targetBlockBB = com.simibubi.create.AllBlocks.DEPOT.getDefaultState()
                .getOutlineShape(null, null)
                .getBoundingBox();
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, util.grid.at(3,1,3), targetBlockBB.offset(util.grid.at(3,1,3)), 15);
        scene.overlay.showControls(new InputWindowElement(util.vector.topOf(util.grid.at(3,1,3)), Pointing.DOWN).rightClick()
                .withItem(smartMechanicalArmStack), 10);
        scene.overlay.showText(30)
                .text("select")
                .pointAt(util.vector.topOf(util.grid.at(3,1,3)));
        scene.idle(25);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, util.grid.at(4,1,7), targetBlockBB.offset(util.grid.at(4,1,7)), 15);
        scene.idle(25);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, util.grid.at(6,1,4), targetBlockBB.offset(util.grid.at(6,1,4)), 15);
        scene.idle(25);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN, util.grid.at(8,1,2), targetBlockBB.offset(util.grid.at(8,1,2)), 15);

        scene.idle(30);

        scene.world.showSection(util.select.position(5,1,5),Direction.DOWN);
        scene.idle(10);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.RED, util.grid.at(8,1,2), targetBlockBB.offset(util.grid.at(8,1,2)), 10);
        scene.overlay.showText(30)
                .text("out of range")
                .pointAt(util.vector.topOf(util.grid.at(8,1,2)));

        scene.idle(40);

        scene.world.hideSection(util.select.fromTo(3,1,3,6,1,7),Direction.UP);
        scene.world.hideSection(util.select.position(8,1,2),Direction.UP);

        scene.addKeyframe();

        for (int i = 0;i<4;i++){
            scene.world.showSection(util.select.position(i,1,2),Direction.UP);
            scene.idle(5);
        }
        scene.world.showSection(util.select.position(3,2,2),Direction.UP);
        scene.world.setKineticSpeed(util.select.position(0,1,2),64f);
        scene.world.setKineticSpeed(util.select.position(2,1,2),64f);
        scene.world.setKineticSpeed(util.select.position(1,1,2),-64f);
        scene.world.setKineticSpeed(util.select.position(3,1,2),-64f);

        scene.overlay.showText(30)
                .text("need speed")
                .pointAt(util.vector.topOf(util.grid.at(3,2,2)));

        scene.idle(40);

        scene.markAsFinished();
    }
}
