package com.ffsupver.asplor.compat.create.ponder.scenes;

import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.foundation.ponder.PonderPalette;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import earth.terrarium.adastra.common.registry.ModFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class AtmosphericRegulatorScenes {
    public static void use(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("atmospheric_regulator.use", "xxx");
        scene.configureBasePlate(0, 0, 18);
        scene.scaleSceneView(0.5f);

        scene.rotateCameraY(-45);
        scene.world.showSection(util.select.everywhere(), Direction.DOWN);
        scene.world.showSection(util.select.fromTo(6,2,1,8,4,16),Direction.DOWN);
        scene.idle(20);

        scene.world.hideSection(util.select.fromTo(6,2,1,8,4,16),Direction.DOWN);

        scene.idle(20);

        scene.world.setKineticSpeed(util.select.position(10,5,13),64);
        scene.overlay.showText(20)
                .text("use")
                .pointAt(util.vector.blockSurface(util.grid.at(10,2,3),Direction.WEST));

        scene.idle(30);

        scene.overlay.showText(20)
                .text("add oxygen")
                .pointAt(util.vector.blockSurface(util.grid.at(10,5,14),Direction.WEST))
                .attachKeyFrame();
        scene.world.modifyBlockEntity(util.grid.at(10,5,14), FluidTankBlockEntity.class,fluidTankBlockEntity -> {
            try(Transaction t = Transaction.openOuter()) {
                fluidTankBlockEntity.getFluidStorage(Direction.WEST).insert(FluidVariant.of(ModFluids.OXYGEN.get()),12 * 81000L,t);
                t.commit();
            }
        });

        scene.idle(40);

       scene.world.modifyBlockEntity(util.grid.at(10,5,14), FluidTankBlockEntity.class,fluidTankBlockEntity -> {
            try(Transaction t = Transaction.openOuter()) {
                fluidTankBlockEntity.getFluidStorage(Direction.WEST).extract(FluidVariant.of(ModFluids.OXYGEN.get()),12 * 81000L,t);
                t.commit();
            }
        });

       scene.idle(20);

        Vec3d boxCenter = util.grid.at(10,2,8).toCenterPos().add(0,0,-0.5);
        scene.overlay.chaseBoundingBoxOutline(PonderPalette.GREEN,util.grid.at(10,2,12),Box.of(boxCenter,1,1,8),20);
        scene.overlay.showText(20)
                .text("share")
                .pointAt(util.vector.blockSurface(util.grid.at(10,2,3),Direction.WEST));

        scene.idle(40);

        scene.markAsFinished();
    }
}
