package com.ffsupver.asplor.block.laserDrill;

import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.util.RenderUtil;
import com.simibubi.create.foundation.blockEntity.renderer.SmartBlockEntityRenderer;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RotationAxis;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import static com.ffsupver.asplor.util.RenderUtil.renderVertex;

public class LaserDrillLenRenderer extends SmartBlockEntityRenderer<LaserDrillLenEntity> {
    public LaserDrillLenRenderer(BlockEntityRendererFactory.Context context) {
        super(context);
    }

    @Override
    protected void renderSafe(LaserDrillLenEntity blockEntity, float partialTicks, MatrixStack ms, VertexConsumerProvider buffer, int light, int overlay) {
        super.renderSafe(blockEntity, partialTicks, ms, buffer, light, overlay);
        BlockPos digPos = blockEntity.getLastDigPos();
        int height = blockEntity.getPos().getY() - digPos.getY();

        long time = blockEntity.getWorld().getTime();
        float lDegree = (time % (360 / 2)) * 2;
        ms.translate(.5f,0,.5f);
        ms.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(lDegree));
        ms.translate(-.5f,0,-.5f);
        renderLaser(ms,buffer,height,time);
    }

    private void renderLaser(MatrixStack ms, VertexConsumerProvider bufferSource,int height,long time){
        Matrix3f normalMatrix = ms.peek().getNormalMatrix();
        Matrix4f positionMatrix = ms.peek().getPositionMatrix();

        int red = 20;
        int green = 150;
        int blue = 150;
        int redH = 10;
        int greenH = 140;
        int blueH = 255;


        int eachBlotHCount = 2;
        int baseHeight = 4;

        float eachYHeight = 1f / eachBlotHCount;
        float yDownOffset = -eachBlotHCount * eachYHeight / 20 * (time % 20);
        int iStart = (int) ((yDownOffset) / eachYHeight) - 1;


        for (int i = iStart - baseHeight; i < height * eachBlotHCount + iStart; i++) {
            boolean upsideDown = i % 2 == 0;
            float yOffset = (float) -i / eachBlotHCount + yDownOffset;

            BufferBuilder innerBuilder = (BufferBuilder) bufferSource.getBuffer(RenderLayer.getBeaconBeam(new Identifier(Asplor.MOD_ID,"textures/block/laser_drill/laser_drill_light.png"),false));
            renderBolt(innerBuilder, normalMatrix, positionMatrix,
                    red, green, blue, redH, greenH, blueH,
                    0.1f, 1,yOffset,eachYHeight,upsideDown
            );
        }

        int iStartOut = (int) ((yDownOffset * 2) / eachYHeight) - 1;
        for (int i = iStartOut - baseHeight; i < height * eachBlotHCount + iStartOut; i++) {
            boolean upsideDown = i % 2 == 0;

            RenderUtil.rotationBlockCenter(ms,RotationAxis.POSITIVE_Y,12.5f);

            float yOffset = (float) -i / eachBlotHCount + yDownOffset * 2;
            BufferBuilder outerBuilder = (BufferBuilder) bufferSource.getBuffer(RenderLayer.getBeaconBeam(new Identifier(Asplor.MOD_ID, "textures/block/laser_drill/laser_drill_light.png"), true));
            renderBolt(outerBuilder, normalMatrix, positionMatrix,
                    red, green, blue, redH, greenH, blueH,
                    0.2f, 0.1f, yOffset, 0.5f / eachBlotHCount, upsideDown
            );
        }
    }

    private void renderBolt(BufferBuilder builder,Matrix3f normalMatrix,Matrix4f positionMatrix,int red,int green ,int blue ,int redH,int greenH, int blueH,float r,float alpha,float yOffset,float yHeight,boolean upsideDown){
        float x1 = 0.5f - r;
        float x2 = 0.5f + r;
        float y1 = yHeight / 2 + yOffset;
        float y2 = -yHeight / 2 + yOffset;

        if (upsideDown){
            float tmpY = y1;
            y1 = y2;
            y2 = tmpY;
        }

        renderVertex(builder,positionMatrix,normalMatrix,
                x1, y1, x1,
                x1,  y2, x1,
                x1,  y2, x2,
                x1, y1, x2,
                red,green,blue,redH,greenH,blueH,alpha
        );
        renderVertex(builder,positionMatrix,normalMatrix,
                x1, y1, x2,
                x1,  y2, x2,
                x2,  y2, x2,
                x2, y1, x2,
                red,green,blue,redH,greenH,blueH,alpha
        );
        renderVertex(builder,positionMatrix,normalMatrix,
                x2, y1, x2,
                x2,  y2, x2,
                x2,  y2, x1,
                x2, y1, x1,
                red,green,blue,redH,greenH,blueH,alpha
        );
        renderVertex(builder,positionMatrix,normalMatrix,
                x2, y1, x1,
                x2,  y2, x1,
                x1,  y2, x1,
                x1, y1, x1,
                red,green,blue,redH,greenH,blueH,alpha
        );
    }
}
