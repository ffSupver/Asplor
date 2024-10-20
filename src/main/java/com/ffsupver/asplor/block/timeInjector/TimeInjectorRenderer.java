package com.ffsupver.asplor.block.timeInjector;

import com.ffsupver.asplor.AllPartialModels;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.foundation.blockEntity.renderer.SafeBlockEntityRenderer;
import com.simibubi.create.foundation.render.CachedBufferer;
import com.simibubi.create.foundation.render.SuperByteBuffer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;

import java.util.Map;
import java.util.Objects;

public class TimeInjectorRenderer extends SafeBlockEntityRenderer<TimeInjectorEntity> {
    public TimeInjectorRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    protected void renderSafe(TimeInjectorEntity be, float partialTicks, MatrixStack ms, VertexConsumerProvider bufferSource, int light, int overlay) {
        ItemStack clock = new ItemStack(Items.CLOCK,1);
        ItemStack enchantedClock = new ItemStack(Items.CLOCK,1);
        EnchantmentHelper.set(Map.of(Enchantments.UNBREAKING,3) ,enchantedClock);
        int clockCount = 4;
        if (be.getProcessTime()==be.getRenderProcess()){
            renderClock(clock,1,clockCount, be, ms, bufferSource, light);
        }else {
            for (int i = 0; i < clockCount; i++) {
                renderClock(enchantedClock,i+1,clockCount, be, ms, bufferSource, light);
            }
        }
        float innerRingScale = 1.0f;
        renderInnerRing(be,ms,bufferSource,innerRingScale,0,partialTicks);
        renderInnerRing(be,ms,bufferSource,innerRingScale*1.4f,1,partialTicks);

        Map<BlockPos,ItemStack> renderWorkArea = be.getRenderWorkArea();
        for (BlockPos workPos:be.getWorkArea()){
            ItemStack output = renderWorkArea.get(workPos);
            if (output != null)
                renderOutputItem(workPos,output,be,light,ms,bufferSource);
//            System.out.println(" render "+workPos+" "+output);
        }
    }

    private void renderOutputItem(BlockPos pos,ItemStack itemStack,TimeInjectorEntity be,int light,MatrixStack ms,VertexConsumerProvider bufferSource){
        float offSetX = pos.getX()-be.getPos().getX();
        float offSetY = -1.0f;
        float offSetZ = pos.getZ()-be.getPos().getZ();
        float minScale = 0.1f;
        float maxScale =2.0f;
        int process = be.getRenderProcess();
        int processTime =be.getProcessTime();

        //时间currentTime从0到1
        float currentTime=1-(float) process /processTime;
        float partOneTime = 0.75f;
        boolean isPartOne = currentTime<=partOneTime;

        ms.push();
        ms.translate(offSetX + 0.5f, 0.5f + offSetY, 0.5f + offSetZ);

        //放大动画
        float animation = getOutputScale(minScale,maxScale,currentTime,partOneTime,isPartOne);
        if (isPartOne) {
            ms.scale(minScale, animation, minScale);
        }else {
            ms.scale(animation, maxScale, animation);
        }

        renderItem(itemStack,light,be,ms,bufferSource);
        ms.pop();
    }

    private float getOutputScale(float minScale,float maxScale, float currentTime,float partOneTime,boolean isPartOne){
        if (isPartOne){
            return minScale+(maxScale-minScale)*currentTime/partOneTime;
        }
        return minScale+(maxScale-minScale)*(currentTime-partOneTime)/(1-partOneTime);
    }

    private float getClockRotationDegrees(TimeInjectorEntity be){
        float process = be.getRenderProcess();
        float processTime = be.getProcessTime();
        int clockTurnPerProcess =8;
        float degree = (float)((Math.sin(process/processTime*Math.PI-Math.PI/2)+1.0f)/2)*clockTurnPerProcess*360;
        return degree;
    }

    private float getInnerRingRotationDegrees(int process,int processTime,TimeInjectorEntity be,float partialTicks){
        int innerRingTurnPerProcess = 8;
        float innerRingTurnPerSecond = 0.3f;
        if (process==processTime){
            return Objects.requireNonNull(be.getWorld()).getTime() *innerRingTurnPerSecond/20;
        }else {
            return (float) process /processTime*2*(float) Math.PI *innerRingTurnPerProcess;
        }
    }
    private void renderInnerRing(TimeInjectorEntity be,MatrixStack ms,VertexConsumerProvider bufferSource,float innerRingScale,int innerRingCount,float partialTicks){
        VertexConsumer solid = bufferSource.getBuffer(RenderLayer.getSolid());
        PartialModel model = AllPartialModels.TIME_INJECTOR_INNER_RING;
        SuperByteBuffer modelBuffer = CachedBufferer.partial(model,be.getCachedState());
        float rotationDegrees=getInnerRingRotationDegrees(be.getRenderProcess(),be.getProcessTime(),be,partialTicks)*
                (1+innerRingCount);


        ms.push();

        modelBuffer.rotateCentered(Direction.UP, rotationDegrees)
            .rotateCentered(Direction.NORTH, rotationDegrees/4)
            .rotateCentered(Direction.EAST, rotationDegrees/2)
            .translate(.5f, .5f, .5f)
            .scale(innerRingScale)
            .translate(-.5f, -.475f, -.5f);

        draw(modelBuffer,ms,solid);

        ms.pop();
    }

    private void renderClock(ItemStack clock,int itemCount,int clockCount,TimeInjectorEntity be,MatrixStack ms,VertexConsumerProvider bufferSource,int light){
        ms.push();
        ms.translate(0.5, 0.525, 0.5);
        float clockScale = 0.45f;
        ms.scale(clockScale, clockScale, clockScale);
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(getClockRotationDegrees(be)*itemCount));
        ms.multiply(RotationAxis.POSITIVE_X.rotationDegrees(getClockRotationDegrees(be)*itemCount/4));
        renderItem(clock,light,be,ms,bufferSource);
        ms.pop();
    }

    private void renderItem(ItemStack itemStack,int light,TimeInjectorEntity be,MatrixStack ms,VertexConsumerProvider bufferSource){
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();
        itemRenderer.renderItem(itemStack, ModelTransformationMode.FIXED, light, OverlayTexture.DEFAULT_UV, ms, bufferSource, be.getWorld(), 0);
    }

    private static void draw(SuperByteBuffer buffer, MatrixStack ms, VertexConsumer vc) {
        buffer.light(LightmapTextureManager.MAX_LIGHT_COORDINATE)
                .renderInto(ms, vc);
    }

    @Override
    public boolean rendersOutsideBoundingBox(TimeInjectorEntity blockEntity) {
        return true;
    }
}
