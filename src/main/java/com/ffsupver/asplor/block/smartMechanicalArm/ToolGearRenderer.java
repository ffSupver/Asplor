package com.ffsupver.asplor.block.smartMechanicalArm;

import com.ffsupver.asplor.item.item.ToolItem;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.Item;

import java.util.ArrayList;

import static com.ffsupver.asplor.util.RenderUtil.renderModel;

public class ToolGearRenderer extends SafeBlockEntityRenderer<ToolGearEntity> {
    public ToolGearRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    protected void renderSafe(ToolGearEntity be, float partialTicks, MatrixStack ms, VertexConsumerProvider bufferSource, int light, int overlay) {
        ArrayList<PartialModel> models = new ArrayList<>();
        ArrayList<Item> items = new ArrayList<>();
        be.getItemStorage(null).iterator().forEachRemaining(itemVariantStorageView -> {
            items.add(itemVariantStorageView.getResource().getItem());
        });
        for (Item item : items){
            if (item instanceof ToolItem toolItem){
                models.add(toolItem.getToolModel());
            }
        }

        float offset = 6/16f;
        int offsetDirection = 0;
        ms.push();
        ms.translate(-3/16f,8/16f,-3/16f);
        for (PartialModel model : models){
            renderModel(be,ms,bufferSource,model,light);
            switch (offsetDirection){
                case 0 -> ms.translate(offset, 0, 0);
                case 1 -> ms.translate(0,0,offset);
                case 2 -> ms.translate(-offset,0,0);
            }
            offsetDirection += 1;
        }
        ms.pop();
    }
}
