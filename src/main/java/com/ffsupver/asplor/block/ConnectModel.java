package com.ffsupver.asplor.block;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.model.SpriteFinder;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.texture.Sprite;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.BlockRenderView;
import net.minecraft.world.BlockView;
import org.joml.Vector2f;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class ConnectModel extends ForwardingBakedModel {
    public ConnectModel(BakedModel bakedModel) {
        this.wrapped = bakedModel;
    }

    @Override
    public void emitBlockQuads(BlockRenderView blockView, BlockState state, BlockPos pos, Supplier<Random> randomSupplier, RenderContext context) {
        MinecraftClient client =MinecraftClient.getInstance();
        // 获取原版的方块模型
        BakedModel originalModel = client.getBlockRenderManager().getModels().getModel(state);

        SpriteFinder spriteFinder = SpriteFinder.get(client.getBakedModelManager().getAtlas(PlayerScreenHandler.BLOCK_ATLAS_TEXTURE));
        List<BakedQuad> quads = originalModel.getQuads(state, null, randomSupplier.get());



        Identifier id = new Identifier( "block/iron_block");
        System.out.println(" emit  "+pos+"   "+state);
        context.pushTransform(quad -> {
            Direction direction = quad.nominalFace();

            Direction[] directionsToTest = getAdjacentDirections(direction);
            int connectState = getConnectivityBitmask(directionsToTest,blockView,state,pos);
            int[] offset = getOffset(connectState);


            Sprite sprite = spriteFinder.find(quad, 0);

            quad.spriteBake(sprite,0);

            float uMin = sprite.getMinU();
            float vMin = sprite.getMinV();
            float uMax = sprite.getMaxU();
            float vMax = sprite.getMaxV();

            float u = (uMax - uMin)/8;
            float v = (vMax - vMin)/8;

            uMin += u*offset[0];
            vMin += v*offset[1];
            uMax = uMin + u;
            vMax = vMin + v;

            quad.uv(0, new Vector2f(uMin, vMax)); // 左下
            quad.uv(1, new Vector2f(uMin, vMin)); // 左上
            quad.uv(2, new Vector2f(uMax, vMin)); // 右上
            quad.uv(3, new Vector2f(uMax, vMax)); // 右下
            System.out.println(direction+"  "+pos+"   "+state+" uv "+uMin+" "+uMax+" v "+vMin+" "+vMax+" c "+connectState+" "+ Arrays.toString(offset));


            return true;
        });

        super.emitBlockQuads(blockView, state, pos, randomSupplier, context);


        context.popTransform();

    }

    private boolean isConnected(BlockView blockView, BlockState blockState, BlockPos blockPos, Direction direction){
//        return blockView.getBlockState(blockPos.offset(direction)).getBlock().equals(blockState.getBlock());
        return isSameBlock(blockView,blockState,blockPos.offset(direction));
    }

    private boolean isSameBlock(BlockView blockView,BlockState blockState,BlockPos posToTest){
        return blockView.getBlockState(posToTest).getBlock().equals(blockState.getBlock());
    }

    private Direction[] getAdjacentDirections(Direction direction) {
        Direction[] result = new Direction[4];
        switch (direction){
            case UP -> {
                result[0] = Direction.NORTH;
                result[1] = Direction.SOUTH;
                result[2] = Direction.WEST;
                result[3] = Direction.EAST;
            }
            case DOWN -> {
                result[0] = Direction.SOUTH;
                result[1] = Direction.NORTH;
                result[2] = Direction.WEST;
                result[3] = Direction.EAST;
            }
            default -> {
                result[0] = Direction.UP;
                result[1] = Direction.DOWN;
            }
        }
        switch (direction){
            case NORTH -> {
                result[2] = Direction.EAST;
                result[3] = Direction.WEST;
            }
            case SOUTH -> {
                result[2] = Direction.WEST;
                result[3] = Direction.EAST;
            }
            case EAST -> {
                result[2] = Direction.SOUTH;
                result[3] = Direction.NORTH;
            }
            case WEST -> {
                result[2] = Direction.NORTH;
                result[3] = Direction.SOUTH;
            }
        }

        return result;
    }

    private int getConnectivityBitmask(Direction[] adjacentDirections, BlockView blockView, BlockState blockState, BlockPos blockPos) {
        // 初始化二进制数为0
        int connectivityMask = 0;

        // 检查每个方向的连接状态
        for (int i = 0; i < 4; i++) {
            boolean isConnected = isConnected(blockView, blockState, blockPos, adjacentDirections[i]);
            // 将每个方向的连接状态存入二进制掩码
            // 如果连接，则将当前位设为1，否则为0
            connectivityMask |= (isConnected ? 1 : 0) << (3 - i);  // 3-i 保证从高位到低位依次设置
        }

        // 获取四个角的 BlockPos
        BlockPos[] cornerPositions = new BlockPos[4];
        cornerPositions[0] = blockPos.offset(adjacentDirections[0]).offset(adjacentDirections[2]);  // 左上
        cornerPositions[1] = blockPos.offset(adjacentDirections[1]).offset(adjacentDirections[2]);  // 左下
        cornerPositions[2] = blockPos.offset(adjacentDirections[0]).offset(adjacentDirections[3]);  //右上
        cornerPositions[3] = blockPos.offset(adjacentDirections[1]).offset(adjacentDirections[3]);  //右下

        // 计算后四位：四个角的 BlockState 是否相同
        for (int i = 0; i < 4; i++) {
            boolean isNotSame = !isSameBlock(blockView, blockState, cornerPositions[i]);
            // 将相同的角存入二进制掩码
            // 如果缺角，则将当前位设为1，否则为0
            connectivityMask |= (isNotSame ? 1 : 0) << (7 - i);  // 7-i 保证从高位到低位依次设置
        }

        return connectivityMask;  // 返回八位二进制数，左上 左下 右上 右下 上 下 左 右
    }

    private int[] getOffset(int connectState){
        int[] offset = new int[2];

        int connectSide = connectState & 0b00001111;
        int connectConner = (connectState & 0b11110000) / 0b10000;

        switch (connectSide){
            case 0b0000 : {  //单独
                offset[0] = 0;
                offset[1] = 0;
                break;
            }
            case 0b1010 : { //左上连
                if ((connectConner & 0b1000) != 0b1000){
                    offset[0] = 1;
                    offset[1] = 1;
                }else{
                    offset[0] = 4;
                    offset[1] = 0;
                }
                break;
            }
            case 0b1001 : { //右上连
                if ((connectConner & 0b0010) != 0b0010) {
                    offset[0] = 0;
                    offset[1] = 1;
                }else {
                    offset[0] = 5;
                    offset[1] = 0;
                }
                break;
            }
            case 0b0101 : { //右下连
                if ((connectConner & 0b0001) != 0b0001) {
                    offset[0] = 2;
                    offset[1] = 0;
                }else {
                    offset[0] = 6;
                    offset[1] = 0;
                }
                break;
            }
            case 0b0110 : { //左下连
                if ((connectConner & 0b0100) != 0b0100) {
                    offset[0] = 3;
                    offset[1] = 0;
                }else {
                    offset[0] = 7;
                    offset[1] = 0;
                }
                break;
            }
            case 0b1100 : { //上下连
                offset[0] = 2;
                offset[1] = 1;
                break;
            }
            case 0b0011 : { //左右连
                offset[0] = 3;
                offset[1] = 1;
                break;
            }
            case 0b0010 : { //左连
                offset[0] = 1;
                offset[1] = 2;
                break;
            }
            case 0b0001 : { //右连
                offset[0] = 0;
                offset[1] = 2;
                break;
            }
            case 0b0100 : { //下连
                offset[0] = 3;
                offset[1] = 2;
                break;
            }
            case 0b1000 : { //上连
                offset[0] = 2;
                offset[1] = 2;
                break;
            }
            case 0b1101 : { //上下右连
                if ((connectConner & 0b0010) == 0b0010) {
                    if ((connectConner & 0b0001) == 0b0001) {
                        offset[0] = 5;
                        offset[1] = 1;
                    } else {
                        offset[0] = 6;
                        offset[1] = 1;
                    }
                }else if ((connectConner & 0b0001) == 0b0001){
                    offset[0] = 4;
                    offset[1] = 1;
                }else {
                    offset[0] = 1;
                    offset[1] = 3;
                }
                break;
            }
            case 0b1110 : { //上下左连
                if ((connectConner & 0b1000) == 0b1000){
                    if ((connectConner & 0b0100) == 0b0100){
                        offset[0] = 4;
                        offset[1] = 2;
                    }else {
                        offset[0] = 7;
                        offset[1] = 1;
                    }
                }else if((connectConner & 0b0100) == 0b0100){
                    offset[0] = 5;
                    offset[1] = 2;
                }else{
                    offset[0] = 0;
                    offset[1] = 3;
                }
                break;
            }
            case 0b1011 : { //左右上连
                if ((connectConner & 0b1000) == 0b1000){
                    if ((connectConner & 0b0010) == 0b0010){
                        offset[0] = 7;
                        offset[1] = 2;
                    }else {
                        offset[0] = 6;
                        offset[1] = 2;
                    }
                }else if ((connectConner & 0b0010) == 0b0010){
                    offset[0] = 4;
                    offset[1] = 3;
                }else {
                    offset[0] = 3;
                    offset[1] = 3;
                }
                break;
            }
            case 0b0111 : { //左右下连
                if ((connectConner & 0b0100) == 0b0100){
                    if ((connectConner & 0b0001) == 0b0001){
                        offset[0] = 6;
                        offset[1] = 3;
                    }else {
                        offset[0] = 5;
                        offset[1] = 3;
                    }
                }else if ((connectConner & 0b0001) == 0b0001){
                    offset[0] = 7;
                    offset[1] = 3;
                }else {
                    offset[0] = 2;
                    offset[1] = 3;
                }
                break;
            }
            case 0b1111 : { //全
                switch (connectConner){
                    case 0b0000 ->{
                        offset[0] = 1;
                        offset[1] = 0;
                    }
                    case 0b1000 ->{
                        offset[0] = 2;
                        offset[1] = 4;
                    }
                    case 0b0100 ->{
                        offset[0] = 0;
                        offset[1] = 4;
                    }
                    case 0b0010 ->{
                        offset[0] = 3;
                        offset[1] = 4;
                    }
                    case 0b0001 ->{
                        offset[0] = 1;
                        offset[1] = 4;
                    }
                    case 0b1100 ->{
                        offset[0] = 4;
                        offset[1] = 4;
                    }
                    case 0b0011 ->{
                        offset[0] = 5;
                        offset[1] = 4;
                    }
                    case 0b1010 ->{
                        offset[0] = 6;
                        offset[1] = 4;
                    }
                    case 0b0110 ->{
                        offset[0] = 7;
                        offset[1] = 4;
                    }
                    case 0b1001 ->{
                        offset[0] = 0;
                        offset[1] = 5;
                    }
                    case 0b0101 ->{
                        offset[0] = 1;
                        offset[1] = 5;
                    }
                    case 0b0111 ->{
                        offset[0] = 3;
                        offset[1] = 5;
                    }
                    case 0b1011 ->{
                        offset[0] = 2;
                        offset[1] = 5;
                    }
                    case 0b1101 ->{
                        offset[0] = 5;
                        offset[1] = 5;
                    }
                    case 0b1110 ->{
                        offset[0] = 4;
                        offset[1] = 5;
                    }
                    case 0b1111 ->{
                        offset[0] = 6;
                        offset[1] = 5;
                    }
                }

                break;
            }
        }



        return offset;
    }
}
