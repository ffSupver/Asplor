package com.ffsupver.asplor.compat.create.ponder.scenes;

import com.ffsupver.asplor.entity.custom.AlloyChestEntity;
import com.ffsupver.asplor.entity.custom.cargoRocket.CargoRocketEntity;
import com.simibubi.create.content.fluids.tank.FluidTankBlockEntity;
import com.simibubi.create.content.logistics.funnel.BeltFunnelBlock;
import com.simibubi.create.foundation.ponder.ElementLink;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.EntityElement;
import earth.terrarium.adastra.common.registry.ModFluids;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

public class CargoRocketScenes {
    public static void use(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("cargo_rocket.use", "xxx");
        scene.configureBasePlate(0, 0, 9);
        scene.scaleSceneView(0.8f);
        scene.showBasePlate();

        scene.world.showSection(util.select.fromTo(3,1,3,5,1,5), Direction.DOWN);
        scene.world.showSection(util.select.position(3,1,2), Direction.DOWN);
        scene.world.showSection(util.select.position(3,1,6), Direction.DOWN);


        scene.world.setKineticSpeed(util.select.fromTo(2,0,0,2,0,2),64);

        scene.overlay.showText(15)
                .text("item bind")
                .pointAt(util.vector.topOf(3,1,2));
        scene.overlay.showText(15)
                .text("fuel bind")
                .pointAt(util.vector.topOf(3,1,6));

        scene.idle(30);

        scene.addKeyframe();

        ElementLink<EntityElement> cargoEntityElement = scene.world.createEntity(world -> new CargoRocketEntity(world,util.grid.at(4,0,4).toCenterPos().add(0,0.5,0),new Vec3d(0,0,0)));

        scene.idle(30);

        ItemStack diamond = Items.DIAMOND.getDefaultStack();
        scene.world.showSection(util.select.position(2,1,2),Direction.WEST);
        scene.world.createItemOnBelt(util.grid.at(2,0,0),Direction.UP, diamond);

        scene.idle(15);

        scene.world.removeItemsFromBelt(util.grid.at(2,0,2));

        scene.overlay.showText(15)
                .text("input item")
                .pointAt(util.vector.topOf(3,1,2));

        scene.idle(30);

        ElementLink<EntityElement> alloyChestLow = scene.world.createEntity(world ->
                new AlloyChestEntity(world,util.grid.at(3,2,2).toCenterPos().add(0,-0.5,0),new Vec3d(0,0,0)));

        scene.overlay.showText(15)
                .text("load chest")
                .pointAt(util.vector.topOf(3,2,2));

        scene.idle(15);

        scene.world.modifyEntity(alloyChestLow, Entity::discard);

        scene.idle(30);

        scene.world.modifyBlockEntity(util.grid.at(3,1,8), FluidTankBlockEntity.class,fluidTankBlockEntity ->{
            try(Transaction t = Transaction.openOuter()) {
                fluidTankBlockEntity.getTank(0).insert(FluidVariant.of(ModFluids.FUEL.get()),6*1000*81,t);
                t.commit();
            }
        });
        scene.world.showSection(util.select.fromTo(3,2,7,5,0,8),Direction.DOWN);
        scene.world.setKineticSpeed(util.select.position(3,1,7),64);
        scene.world.setKineticSpeed(util.select.fromTo(4,1,7,4,1,8),-64);
        scene.world.setKineticSpeed(util.select.position(5,0,8),32);

        scene.idle(5);

        scene.overlay.showText(15)
                .text("input fuel")
                .pointAt(util.vector.topOf(3,1,6));


        scene.idle(0);

        for (int i=0;i<5;i++){
            scene.idle(5);
            scene.world.modifyBlockEntity(util.grid.at(3,1,8), FluidTankBlockEntity.class,fluidTankBlockEntity ->{
                try(Transaction t = Transaction.openOuter()) {
                    fluidTankBlockEntity.getTank(0).extract(FluidVariant.of(ModFluids.FUEL.get()),6*1000*81/5,t);
                    t.commit();
                }
            });
        }

        scene.idle(30);

        scene.addKeyframe();

        scene.world.showSection(util.select.position(4,1,2),Direction.DOWN);
        scene.overlay.showText(15)
                .text("item single")
                .pointAt(util.vector.topOf(4,1,2));

        scene.idle(30);

        scene.world.showSection(util.select.position(2,1,6),Direction.DOWN);
        scene.overlay.showText(15)
                .text("fuel single")
                .pointAt(util.vector.topOf(2,1,6));

        scene.idle(30);

        scene.addKeyframe();
        scene.overlay.showText(15)
                .text("launch")
                .pointAt(util.vector.topOf(4,1,2));

        scene.world.modifyEntity(cargoEntityElement,cargoEntity->{
            if (cargoEntity instanceof CargoRocketEntity cargoRocketEntity){
                cargoRocketEntity.initiateLaunchSequence();
            }
        });

        scene.idle(200);

        for (int i =0;i<50;i++){
            int finalI = i;
            scene.world.modifyEntity(cargoEntityElement, cargoEntity -> {
                Vec3d pos = cargoEntity.getPos();
                cargoEntity.setPosition(pos.x,pos.y + (double) finalI /16,pos.z);
            });
            scene.idle(1);
        }

        scene.addKeyframe();

        for (int i =0;i<50;i++){
            int finalI = 49 - i;
            scene.world.modifyEntity(cargoEntityElement, cargoEntity -> {
                Vec3d pos = cargoEntity.getPos();
                cargoEntity.setPosition(pos.x,pos.y - (double) finalI /16,pos.z);
            });
            scene.idle(1);
        }
        scene.world.modifyEntity(cargoEntityElement, cargoEntity -> {
            if (cargoEntity instanceof CargoRocketEntity cargoRocketEntity){
                cargoRocketEntity.setHasLaunched(false);
            }
        });

        scene.idle(20);

        scene.world.createEntity(world ->
                new AlloyChestEntity(world,util.grid.at(3,2,2).toCenterPos().add(0,-0.5,0),new Vec3d(0,0,0)));
        scene.overlay.showText(15)
                .text("unload alloy chest")
                .pointAt(util.vector.topOf(3,2,2));

        scene.idle(20);

        scene.world.setKineticSpeed(util.select.fromTo(2,0,0,2,0,2),-64);
        scene.world.modifyBlock(util.grid.at(2,1,2),blockState -> blockState.with(BeltFunnelBlock.SHAPE,BeltFunnelBlock.Shape.PUSHING),false);
        scene.world.createItemOnBelt(util.grid.at(2,0,2),Direction.WEST,diamond);
        scene.overlay.showText(15)
                .text("unload item")
                .pointAt(util.vector.topOf(3,1,2));

        scene.idle(20);

        scene.markAsFinished();

    }
}
