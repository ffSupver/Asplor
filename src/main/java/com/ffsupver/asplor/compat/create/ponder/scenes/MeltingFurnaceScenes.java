package com.ffsupver.asplor.compat.create.ponder.scenes;

import com.ffsupver.asplor.fluid.ModFluids;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import static com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HEAT_LEVEL;
import static com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel.KINDLED;
import static com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HeatLevel.SEETHING;

public class MeltingFurnaceScenes {
    public static void meltFurnace(SceneBuilder scene, SceneBuildingUtil util){
        scene.title("melting_furnace", "xxx");
        scene.configureBasePlate(0, 0, 6);
        scene.scaleSceneView(1.0f);
        scene.rotateCameraY(-45f);
        scene.world.showSection(util.select.layer(0), Direction.UP);

        float speed = -16;
        scene.world.setKineticSpeed(util.select.everywhere(),speed);
        scene.world.setKineticSpeed(util.select.position(5,0,4),-0.5f*speed);
        BlockPos meltingFurnace = util.grid.at(2,2,1);

        scene.idle(10);

        for (int i =0 ; i<3;i++){
            scene.world.showSection(util.select.position(5-i,1,3),Direction                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                     .DOWN);
            scene.idle(2);
        }

        scene.idle(5);

        scene.world.showSection(util.select.fromTo(2,2,0,2,1,3),Direction.DOWN);

        scene.idle(10);

        scene.overlay.showText(20)
                .attachKeyFrame()
                .text("can do")
                .pointAt(util.vector.blockSurface(meltingFurnace, Direction.UP));

        scene.idle(20);

        BlockPos burner = util.grid.at(2, 1, 1);

        scene.world.modifyBlock(burner, s -> s.with(HEAT_LEVEL, KINDLED), false);
        scene.overlay.showText(20)
                .attachKeyFrame()
                .text("need burn")
                .pointAt(util.vector.blockSurface(burner, Direction.UP));

        scene.idle(25);

        BlockPos beltStart = util.grid.at(2,1,3);
        ItemStack iron = Items.IRON_INGOT.getDefaultStack();
        scene.world.createItemOnBelt(beltStart,Direction.DOWN,iron);
        scene.overlay.showText(20)
                .attachKeyFrame()
                .text("add item")
                .pointAt(util.vector.blockSurface(beltStart, Direction.UP));

        scene.idle(30);

        BlockPos beltEnd = util.grid.at(2,1,2);
        scene.world.removeItemsFromBelt(beltEnd);

        scene.idle(20);

        scene.overlay.showText(20)
                .attachKeyFrame()
                .text("wait")
                .pointAt(util.vector.blockSurface(meltingFurnace, Direction.UP));

        scene.idle(20);

        BlockPos fluidTankPos = util.grid.at(2,1,0);
        scene.world.modifyBlockEntity(fluidTankPos, FluidTankBlockEntity.class,fluidTankBlockEntity ->
                ((FluidTank) fluidTankBlockEntity.getFluidStorage(null)).setFluid(new FluidStack(ModFluids.MOLTEN_IRON,9000))
        );
        scene.overlay.showText(20)
                .attachKeyFrame()
                .text("output")
                .pointAt(util.vector.blockSurface(fluidTankPos, Direction.UP));

        scene.idle(30);


        scene.markAsFinished();
    }

    public static void heatLevel(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("melting_furnace_heat", "xxx");
        scene.configureBasePlate(0, 0, 1);
        scene.scaleSceneView(1.0f);
        scene.world.showSection(util.select.fromTo(0,0,0,0,1,0), Direction.DOWN);

        scene.idle(5);

        BlockPos burner = util.grid.at(0, 0, 0);


        scene.world.modifyBlock(burner, s -> s.with(HEAT_LEVEL, KINDLED), false);
        scene.overlay.showText(30)
                .text("recipe need normal burn")
                .pointAt(util.vector.blockSurface(burner, Direction.UP));

        scene.idle(40);

        scene.world.modifyBlock(burner, s -> s.with(HEAT_LEVEL, SEETHING), false);
        scene.overlay.showText(30)
                .attachKeyFrame()
                .text("recipe need super burn")
                .pointAt(util.vector.blockSurface(burner, Direction.UP));

        scene.idle(40);

        scene.world.modifyBlock(burner, s -> s.with(HEAT_LEVEL, KINDLED), false);
        scene.overlay.showText(20)
                .attachKeyFrame()
                .text("for recipe need normal burn")
                .pointAt(util.vector.blockSurface(burner, Direction.UP));

        scene.idle(20);

        scene.world.modifyBlock(burner, s -> s.with(HEAT_LEVEL, SEETHING), false);
        scene.overlay.showText(30)
                .attachKeyFrame()
                .text("super burn doubles")
                .pointAt(util.vector.blockSurface(burner, Direction.UP));

        scene.idle(40);

        scene.markAsFinished();
    }
    public static void largeMeltingFurnace(SceneBuilder scene, SceneBuildingUtil util){
        scene.title("large_melting_furnace.base", "xxx");
        scene.configureBasePlate(0, 0, 9);
        scene.scaleSceneView(0.8f);
        scene.world.showSection(util.select.layer(0), Direction.UP);

        scene.world.showSection(util.select.fromTo(2,2,2,6,2,6),Direction.UP);
        scene.overlay.showText(20)
                .text("size");

        scene.idle(35);

        scene.world.showSection(util.select.fromTo(2,3,2,6,3,6),Direction.UP);
        scene.overlay.showText(20)
                .text("item")
                .pointAt(util.vector.blockSurface(util.grid.at(2,3,5),Direction.UP));

        scene.idle(35);

        scene.world.showSection(util.select.fromTo(2,4,2,6,4,6),Direction.UP);
        scene.overlay.showText(20)
                .text("fluid")
                .pointAt(util.vector.blockSurface(util.grid.at(3,4,2),Direction.UP));

        scene.idle(35);

        scene.overlay.showText(20)
                .text("controller")
                .pointAt(util.vector.blockSurface(util.grid.at(2,4,3),Direction.UP));

        scene.idle(35);

        scene.world.showSection(util.select.fromTo(2,5,2,6,5,6),Direction.UP);
        scene.overlay.showText(20)
                .text("air or ladder")
                .pointAt(util.vector.blockSurface(util.grid.at(4,5,5),Direction.NORTH));

        scene.idle(35);

        scene.addKeyframe();

        for (int i =0;i<5;i++){
            for (int j =0;j<5;j++){
                scene.world.modifyBlock(util.grid.at(2+i,1,2+j),s->s.with(HEAT_LEVEL,KINDLED),false);
            }
        }

        scene.idle(5);
        scene.world.hideSection(util.select.fromTo(2,2,2,6,5,6),Direction.UP);
        scene.world.showSection(util.select.fromTo(2,1,2,6,1,6),Direction.UP);
        scene.overlay.showText(20)
                .text("heat")
                .pointAt(util.vector.blockSurface(util.grid.at(4,1,4),Direction.UP));

        scene.idle(40);

        scene.world.showSection(util.select.fromTo(2,2,2,6,5,6),Direction.UP);

        scene.idle(5);

        scene.world.showSection(util.select.fromTo(1,2,3,1,3,5),Direction.UP);
        scene.world.setKineticSpeed(util.select.everywhere(),64);
        scene.world.createItemOnBeltLike(util.grid.at(1,2,3),Direction.DOWN, Blocks.IRON_BLOCK.asItem().getDefaultStack());
        scene.overlay.showText(20)
                .text("addItem")
                .pointAt(util.vector.blockSurface(util.grid.at(1,2,5),Direction.WEST));

        scene.idle(15);

        scene.world.removeItemsFromBelt(util.grid.at(1,2,5));

        scene.idle(20);

        scene.world.showSection(util.select.fromTo(3,1,0,3,4,1),Direction.UP);
        BlockPos fluidTankPos = util.grid.at(3,1,0);
        scene.world.modifyBlockEntity(fluidTankPos, FluidTankBlockEntity.class,fluidTankBlockEntity ->
                ((FluidTank) fluidTankBlockEntity.getFluidStorage(null)).setFluid(new FluidStack(ModFluids.MOLTEN_IRON,81000))
        );
        scene.overlay.showText(20)
                .text("outputFluid")
                .pointAt(util.vector.blockSurface(util.grid.at(3,4,1),Direction.WEST));

        scene.idle(35);


        scene.markAsFinished();
    }
}
