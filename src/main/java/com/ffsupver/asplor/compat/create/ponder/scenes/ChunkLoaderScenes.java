package com.ffsupver.asplor.compat.create.ponder.scenes;

import com.ffsupver.asplor.item.ModItems;
import com.simibubi.create.content.kinetics.deployer.DeployerBlockEntity;
import com.simibubi.create.foundation.ponder.SceneBuilder;
import com.simibubi.create.foundation.ponder.SceneBuildingUtil;
import com.simibubi.create.foundation.ponder.element.InputWindowElement;
import com.simibubi.create.foundation.utility.Pointing;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Direction;

public class ChunkLoaderScenes {
    public static void chunkLoader(SceneBuilder scene, SceneBuildingUtil util) {
        scene.title("chunk_loader", "xxx");
        scene.configureBasePlate(0, 0, 7);
        scene.scaleSceneView(1.0f);
        scene.world.showSection(util.select.layer(0), Direction.UP);

        scene.world.showSection(util.select.position(3,1,2),Direction.DOWN);
        scene.world.showSection(util.select.position(3,1,4),Direction.DOWN);

        scene.idle(5);

        ItemStack clockItem = ModItems.INFUSION_CLOCK.getDefaultStack();
        scene.overlay.showControls(new InputWindowElement(util.vector.topOf(util.grid.at(3,1,2)), Pointing.DOWN).rightClick()
                .withItem(clockItem), 20);


        scene.world.showSection(util.select.fromTo(2,1,6,3,1,6),Direction.DOWN);
        scene.world.setKineticSpeed(util.select.everywhere(),64.0F);
        scene.world.modifyBlockEntity(util.grid.at(3,1,6), DeployerBlockEntity.class,
                deployerBlockEntity -> {
                    try (Transaction t = Transaction.openOuter()) {
                        deployerBlockEntity.getItemStorage(null).insert(ItemVariant.of(clockItem), clockItem.getCount(), t);
                        t.commit();
                    }
                });

        scene.overlay.showText(30)
                .text("use")
                .pointAt(util.vector.of(3.5, 2, 3.5));

        scene.idle(40);

        scene.world.showSection(util.select.fromTo(1,1,4,1,2,4),Direction.DOWN);
        scene.world.hideSection(util.select.fromTo(2,1,6,3,1,6),Direction.DOWN);
        scene.world.hideSection(util.select.position(3,1,4),Direction.DOWN);

        scene.overlay.showText(30)
                .text("crop")
                    .pointAt(util.vector.of(1.5, 2, 4.5));

        scene.idle(40);

        scene.markAsFinished();
    }
}
