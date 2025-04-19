package com.ffsupver.asplor.compat.create.ponder.scenes;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.block.smartMechanicalArm.BeltSmartProcessor;
import com.ffsupver.asplor.block.smartMechanicalArm.BeltSmartProcessorEntity;
import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.item.item.SchematicItem;
import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.BeltItemElement;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.ponder.element.WorldSectionElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class BeltSmartProcessorScenes {
    public static void use(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("belt_smart_processor.use", "xxx");
        scene.configureBasePlate(0, 0, 5);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.world.showSection(util.select.position(2,3,2),Direction.UP);

        BlockPos beltProcessorPos = util.grid.at(2,3,2);

        scene.overlay.showText(30)
                .pointAt(util.vector.blockSurface(beltProcessorPos,Direction.WEST))
                .placeNearTarget()
                .attachKeyFrame()
                .text("des");

        scene.idle(40);

        scene.world.showSection(util.select.fromTo(2,4,2,4,4,2),Direction.UP);
        scene.overlay.showText(30)
                .pointAt(util.vector.blockSurface(util.grid.at(2, 4, 2), Direction.WEST))
                .placeNearTarget()
                .attachKeyFrame()
                .text("energy");

        scene.idle(40);

        ItemStack toolItem = new ItemStack(ModItems.DRILL_TOOL);
        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(util.grid.at(2, 3, 2), Direction.WEST), Pointing.RIGHT)
                        .withItem(toolItem)
                        .rightClick(),
                30
        );
        scene.overlay.showText(30)
                .pointAt(util.vector.blockSurface(util.grid.at(2, 3, 2), Direction.WEST))
                .placeNearTarget()
                .attachKeyFrame()
                .text("insert_tool");
        scene.world.modifyBlockEntity(beltProcessorPos, BeltSmartProcessorEntity.class,beltSmartProcessorEntity -> beltSmartProcessorEntity.insertTool(toolItem));

        scene.idle(40);


        ElementLink<WorldSectionElement> depot = scene.world.showIndependentSection(
                util.select.position(0,1,0),Direction.UP
        );
        scene.world.moveSection(depot,util.vector.of(2,0,2),0);

        scene.idle(10);

        ItemStack processorItem = new ItemStack(ModItems.CRUDE_PRINTED_LOGIC_PROCESSOR);
        scene.world.createItemOnBeltLike(util.grid.at(0,1,0),Direction.NORTH,processorItem);

        scene.idle(5);

        process(scene,util,2,3,2);

        scene.idle(5);

        scene.world.hideIndependentSection(depot,Direction.UP,1);
        scene.world.showSection(util.select.fromTo(2,1,0,2,1,4),Direction.DOWN);
        scene.world.setKineticSpeed(util.select.everywhere(),-64f);

        scene.overlay.showText(30)
                .pointAt(util.vector.blockSurface(util.grid.at(2, 4, 2), Direction.WEST))
                .placeNearTarget()
                .attachKeyFrame()
                .text("belt");

        scene.idle(40);

        ElementLink<BeltItemElement> processorItemElement = scene.world.createItemOnBelt(util.grid.at(2,1,4),Direction.WEST,processorItem);

        scene.idle(15);

        scene.world.stallBeltItem(processorItemElement,true);

        process(scene,util,2,3,2);

        scene.world.stallBeltItem(processorItemElement,false);

        scene.idle(20);

        ItemStack schematicItem = SchematicItem.getSchematicItem("instance");
        scene.overlay.showControls(new InputWindowElement(util.vector.blockSurface(util.grid.at(2, 3, 2), Direction.WEST), Pointing.RIGHT)
                        .withItem(schematicItem)
                        .rightClick(),
                30
        );
        scene.overlay.showText(30)
                .pointAt(util.vector.blockSurface(util.grid.at(2, 3, 2), Direction.WEST))
                .placeNearTarget()
                .attachKeyFrame()
                .text("insert_schematic");
        scene.world.modifyBlock(beltProcessorPos, b-> b.with(BeltSmartProcessor.SCHEMATIC,true),false);

        scene.idle(40);

        ElementLink<BeltItemElement> alloyBlockElement = scene.world.createItemOnBelt(util.grid.at(2,1,4),Direction.WEST,new ItemStack(AllBlocks.CHARGED_ALLOY_BLOCK));

        scene.idle(15);

        scene.world.stallBeltItem(alloyBlockElement,true);
        process(scene,util,2,3,2);
        scene.world.changeBeltItemTo(alloyBlockElement,new ItemStack(ModItems.SPACE_CORE));
        scene.world.stallBeltItem(alloyBlockElement,false);

        scene.idle(20);

        scene.world.showSection(util.select.position(1,3,2),Direction.WEST);

        scene.idle(10);

        scene.overlay.showText(30)
                .pointAt(util.vector.blockSurface(util.grid.at(1, 3, 2), Direction.WEST))
                .placeNearTarget()
                .attachKeyFrame()
                .text("extract tool");

        scene.idle(10);

        scene.world.createItemEntity(util.grid.at(1,3,2).toCenterPos().offset(Direction.DOWN,0.3), Vec3d.ZERO,toolItem);
        scene.world.modifyBlockEntity(beltProcessorPos, BeltSmartProcessorEntity.class, BeltSmartProcessorEntity::extractToolItem);

        scene.idle(40);

        scene.markAsFinished();
    }

    private static void process(SceneBuilder scene, SceneBuildingUtil util,int x,int y,int z){
        scene.world.modifyBlockEntity(util.grid.at(x,y,z), BeltSmartProcessorEntity.class, BeltSmartProcessorEntity::resetProcess);
        for (int i = 0; i < 40; i++) {
            scene.world.modifyBlockEntity(util.grid.at(x,y,z), BeltSmartProcessorEntity.class, BeltSmartProcessorEntity::declineProcess);
            scene.idle(1);
        }
    }
}


