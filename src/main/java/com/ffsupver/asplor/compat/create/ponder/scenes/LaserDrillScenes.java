package com.ffsupver.asplor.compat.create.ponder.scenes;

import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;

public class LaserDrillScenes {
    public static void use(SceneBuilder scene, SceneBuildingUtil util){
        scene.title("laser_drill.use", "xxx");
        scene.configureBasePlate(0, 0, 6);
        scene.scaleSceneView(1.0f);

        scene.markAsFinished();
    }
}
