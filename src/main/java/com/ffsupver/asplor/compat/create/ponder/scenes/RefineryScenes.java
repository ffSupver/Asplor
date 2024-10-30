package com.ffsupver.asplor.compat.create.ponder.scenes;

import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import net.minecraft.util.math.Direction;

public class RefineryScenes {
    public static void build(SceneBuilder scene, SceneBuildingUtil util){
        scene.title("refinery.build", "xxx");
        scene.configureBasePlate(0, 0, 16);
        scene.scaleSceneView(0.5f);


        scene.world.showSection(util.select.layer(0), Direction.UP);

        scene.idle(10);
        for (int i=0;i<5;i++){
            scene.world.showSection(util.select.position(1,1,1+i*2),Direction.DOWN);
            scene.idle(5);
        }

        scene.overlay.showText(20)
                .attachKeyFrame()
                .text("blocks")
                .pointAt(util.vector.blockSurface(util.grid.at(1,1,5), Direction.UP));

        scene.idle(25);

        for (int i=0;i<5;i++){
            scene.world.hideSection(util.select.position(1,1,1+i*2),Direction.DOWN);
        }

        scene.idle(5);

        for (int i=0;i<5;i++){
            scene.world.showSection(util.select.fromTo(4,1+i,3,9,1+i,8),Direction.DOWN);
            scene.idle(10);
        }


        scene.overlay.showText(20)
                .attachKeyFrame()
                .text("empty inside")
                .pointAt(util.vector.blockSurface(util.grid.at(6,3,6), Direction.UP));

        scene.idle(25);

        scene.overlay.showText(20)
                .attachKeyFrame()
                .text("controller")
                .pointAt(util.vector.blockSurface(util.grid.at(4,3,5), Direction.UP));

        scene.idle(25);

        scene.overlay.showText(20)
                .text("input")
                .pointAt(util.vector.blockSurface(util.grid.at(6,2,4), Direction.DOWN));

        scene.idle(25);

        scene.overlay.showText(20)
                .text("output")
                .pointAt(util.vector.blockSurface(util.grid.at(6,4,4), Direction.DOWN));

        scene.idle(25);

        scene.overlay.showText(20)
                .attachKeyFrame()
                .text("output height")
                .pointAt(util.vector.blockSurface(util.grid.at(6,4,4), Direction.DOWN));

        scene.idle(40);


        scene.markAsFinished();
    }

    public static void outputCount(SceneBuilder scene, SceneBuildingUtil util){
        scene.title("refinery.output_count", "xxx");
        scene.configureBasePlate(0, 0, 9);
        scene.scaleSceneView(1.0f);

        scene.world.showSection(util.select.layer(0), Direction.UP);
        scene.world.showSection(util.select.fromTo(2,1,2,6,7,6),Direction.DOWN);

        scene.idle(5);

        scene.overlay.showText(40)
                .attachKeyFrame()
                .text("one output")
                .pointAt(util.vector.blockSurface(util.grid.at(4,6,2), Direction.DOWN));

        scene.idle(45);

        scene.overlay.showText(40)
                .text("same height")
                .pointAt(util.vector.blockSurface(util.grid.at(3,6,2), Direction.DOWN));

        scene.idle(50);

        scene.overlay.showText(30)
                .attachKeyFrame()
                .text("more than one output")
                .pointAt(util.vector.blockSurface(util.grid.at(4,6,2), Direction.DOWN));

        scene.idle(35);

        scene.overlay.showText(20)
                .text("output one")
                .pointAt(util.vector.blockSurface(util.grid.at(4,6,2), Direction.DOWN));

        scene.idle(25);

        scene.overlay.showText(20)
                .text("output two")
                .pointAt(util.vector.blockSurface(util.grid.at(4,5,2), Direction.DOWN));

        scene.idle(25);

        scene.overlay.showText(20)
                .text("output three")
                .pointAt(util.vector.blockSurface(util.grid.at(4,4,2), Direction.DOWN));

        scene.idle(25);

        scene.overlay.showText(20)
                .text("need enough output count")
                .pointAt(util.vector.blockSurface(util.grid.at(4,4,2), Direction.DOWN));

        scene.idle(25);

        scene.markAsFinished();
    }
}
