package com.ffsupver.asplor.compat.create.ponder.scenes;

import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;

public class TimeInjectorScenes {
    public static void timeInjector(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("time_injector", "xxx");
        scene.configureBasePlate(0, 0, 11);
        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.world.showSection(util.select.position(5,3,5),Direction.DOWN);

        scene.overlay.showText(40)
                .placeNearTarget()
                .text("time_injector")
                .pointAt(util.vector.of(5.5, 3.5, 5.5));
        scene.idle(40);

        scene.addKeyframe();


        scene.world.showSection(util.select.fromTo(0,1,0,9,1,9), Direction.DOWN);
        scene.overlay.showText(40)
                .placeNearTarget()
                .text("add_base_block")
                .pointAt(util.vector.of(3.5, 1.5, 3.5));
        scene.idle(40);

        scene.addKeyframe();


        scene.world.showSection(util.select.fromTo(1,2,1,9,2,9), Direction.DOWN);
        scene.overlay.showText(40)
                .placeNearTarget()
                .text("add_liquid")
                .pointAt(util.vector.of(3.5, 2.5, 3.5));
        scene.idle(40);

        scene.addKeyframe();


        scene.world.showSection(util.select.position(5,4,5),Direction.DOWN);
        scene.overlay.showText(40)
                .placeNearTarget()
                .text("add_power")
                .pointAt(util.vector.of(5.5, 4.5, 5.5));
        scene.idle(40);

        scene.addKeyframe();


        scene.world.setBlocks(util.select.fromTo(1,2,1,9,2,9), Blocks.SLIME_BLOCK.getDefaultState(),false);
        scene.overlay.showText(1000)
                .placeNearTarget()
                .text("craft")
                .pointAt(util.vector.of(3.5, 2.5, 3.5));

        scene.idle(20);
        scene.markAsFinished();
    }
}
