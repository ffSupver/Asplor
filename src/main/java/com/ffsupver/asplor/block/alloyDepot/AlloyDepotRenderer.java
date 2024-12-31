package com.ffsupver.asplor.block.alloyDepot;

import com.ffsupver.asplor.util.RenderUtil;
import com.jozufozu.flywheel.util.transform.TransformStack;
import com.simibubi.create.content.kinetics.belt.BeltHelper;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.*;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class AlloyDepotRenderer extends SafeBlockEntityRenderer<AlloyDepotEntity> {
    public AlloyDepotRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    protected void renderSafe(AlloyDepotEntity be, float partialTicks, MatrixStack ms, VertexConsumerProvider bufferSource, int light, int overlay) {
        renderItemsOf(be, partialTicks, ms, bufferSource, light, overlay, be.behaviour);
    }
    public static void renderItemsOf(SmartBlockEntity be, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer,
                                     int light, int overlay, AlloyDepotBehaviour depotBehaviour) {

        TransportedItemStack transported = depotBehaviour.heldItem;
        TransformStack msr = TransformStack.cast(ms);
        Vec3d itemPosition = VecHelper.getCenterOf(be.getPos());

        ms.push();
        ms.translate(.5f, 15 / 16f, .5f);

        if (transported != null)
            depotBehaviour.incoming.add(transported);

        // Render main items
        for (TransportedItemStack tis : depotBehaviour.incoming) {
            ms.push();
            msr.nudge(0);
            float offset = MathHelper.lerp(partialTicks, tis.prevBeltPosition, tis.beltPosition);
            float sideOffset = MathHelper.lerp(partialTicks, tis.prevSideOffset, tis.sideOffset);

            if (tis.insertedFrom.getAxis()
                    .isHorizontal()) {
                Vec3d offsetVec = Vec3d.of(tis.insertedFrom.getOpposite()
                        .getVector()).multiply(.5f - offset);
                ms.translate(offsetVec.x, offsetVec.y, offsetVec.z);
                boolean alongX = tis.insertedFrom.rotateYClockwise()
                        .getAxis() == Direction.Axis.X;
                if (!alongX)
                    sideOffset *= -1;
                ms.translate(alongX ? sideOffset : 0, 0, alongX ? 0 : sideOffset);
            }

            ItemStack itemStack = tis.stack;
            int angle = tis.angle;
            Random r = Random.create(0);
            renderItem(be.getWorld(), be.getPos(),ms, buffer, light, overlay, itemStack, angle, r, itemPosition);
            ms.pop();
        }

        if (transported != null)
            depotBehaviour.incoming.remove(transported);

        // Render output items
        for (int i = 0; i < depotBehaviour.processingOutputBuffer.getSlotCount(); i++) {
            ItemStack stack = depotBehaviour.processingOutputBuffer.getStackInSlot(i);
            if (stack.isEmpty())
                continue;
            ms.push();
            msr.nudge(i);

            boolean renderUpright = BeltHelper.isItemUpright(stack);
            msr.rotateY(360 / 8f * i);
            ms.translate(.35f, 0, 0);
            if (renderUpright)
                msr.rotateY(-(360 / 8f * i));
            Random r = Random.create(i + 1);
            int angle = (int) (360 * r.nextFloat());
            renderItem(be.getWorld(), be.getPos(), ms, buffer, light, overlay, stack, renderUpright ? angle + 90 : angle, r, itemPosition);
            ms.pop();
        }

        ms.pop();
    }

    public static void renderItem(World level,BlockPos pos, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay, ItemStack itemStack,
                                  int angle, Random r, Vec3d itemPosition) {
        ItemRenderer itemRenderer = MinecraftClient.getInstance()
                .getItemRenderer();
        TransformStack msr = TransformStack.cast(ms);
        int count = (int) (MathHelper.floorLog2((int) (itemStack.getCount()))) / 2;
        boolean renderUpright = BeltHelper.isItemUpright(itemStack);
        boolean blockItem = itemRenderer.getModel(itemStack, null, null, 0)
                .hasDepth();

        ms.push();
        msr.rotateY(angle);

        if (renderUpright) {
            Entity renderViewEntity = MinecraftClient.getInstance().cameraEntity;
            if (renderViewEntity != null) {
                Vec3d positionVec = renderViewEntity.getPos();
                Vec3d vectorForOffset = itemPosition;
                Vec3d diff = vectorForOffset.subtract(positionVec);
                float yRot = (float) (MathHelper.atan2(diff.x, diff.z) + Math.PI);
                ms.multiply(RotationAxis.POSITIVE_Y.rotation(yRot));
            }
            ms.translate(0, 3 / 32d, -1 / 16f);
        }

        for (int i = 0; i <= count; i++) {
            ms.push();
            if (blockItem)
                ms.translate(r.nextFloat() * .0625f * i, 0, r.nextFloat() * .0625f * i);
            ms.scale(.5f, .5f, .5f);
            if (!blockItem && !renderUpright) {
                ms.translate(0, -3 / 16f, 0);
                msr.rotateX(90);
            }
            itemRenderer.renderItem(itemStack, ModelTransformationMode.FIXED,RenderUtil.getTextureLight(RenderUtil.getBlockLight(level,pos)), overlay, ms, buffer, level, 0);
            ms.pop();

            if (!renderUpright) {
                if (!blockItem)
                    msr.rotateY(10);
                ms.translate(0, blockItem ? 1 / 64d : 1 / 16d, 0);
            } else
                ms.translate(0, 0, -1 / 16f);
        }

        ms.pop();
    }
}
