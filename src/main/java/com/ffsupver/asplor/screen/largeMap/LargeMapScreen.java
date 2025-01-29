package com.ffsupver.asplor.screen.largeMap;

import com.ffsupver.asplor.AllKeys;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.item.item.largeMap.LargeMapState;
import com.ffsupver.asplor.networking.ModPackets;
import com.ffsupver.asplor.networking.packet.largeMap.RequestLargeMapDataC2SPacket;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.block.MapColor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.RotationAxis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LargeMapScreen extends Screen {
    private static final Identifier BACKGROUND_TEXTURE = new Identifier(Asplor.MOD_ID,"textures/large_map/background.png");
    private static final Identifier MAP_ICON = new Identifier(Asplor.MOD_ID,"textures/large_map/map_icon.png");
    private LargeMapState largeMapState;
    private int startX;
    private int startZ;
    private final int mapId;

    private int updateColorCooldown;
    private int updateCacheCooldown;
    private final int MAX_UPDATE_COLOR_COOLDOWN = 5;
    private final int MAX_UPDATE_CACHE_COOLDOWN = 40;

    private float scale = 1.0f;
    private final float SCALE_STEP = 0.1f;
    private final float MIN_SCALE = 1/16f;
    private final float MAX_SCALE = 8f;
    private final int backgroundWidth = 255,backgroundHeight = 255;
    private int screenWidth, screenHeight;

    private int backgroundX,backgroundY;

    private  int borderXMin,borderXMax ,borderYMin, borderYMax ;

    //鼠标手势
    private boolean dragging = false;
    private int dragStartX,dragStartY,dragStartOriginalX, dragStartOriginalZ;


    private final Map<Long, Identifier> chunkTextures = new HashMap<>();
    private final Map<Long, NativeImageBackedTexture> textureCache = new HashMap<>();

    public LargeMapScreen(Text title,int mapId, LargeMapState largeMapState, int startX, int startZ) {
        super(title);
        this.largeMapState = largeMapState;
        this.startX = startX;
        this.startZ = startZ;
        this.mapId = mapId;
        this.updateColorCooldown = MAX_UPDATE_COLOR_COOLDOWN;
        this.updateCacheCooldown = MAX_UPDATE_CACHE_COOLDOWN;



    }

    @Override
    protected void init() {
        screenWidth = this.width;
        screenHeight = this.height;
        backgroundX = (screenWidth - backgroundWidth) / 2;
        backgroundY = (screenHeight - backgroundHeight) / 2;
        borderXMin = backgroundX+4;
        borderXMax = backgroundX+backgroundWidth-4;
        borderYMin = backgroundY+4;
        borderYMax = backgroundY+backgroundHeight-4;
        super.init();
    }

    private void requestMapData(int mapId, List<Long> needUpdateChunks) {
        int chunkSize = Long.BYTES; // 每个 chunk 的字节大小
        int maxChunksPerPacket = (30000 - Integer.BYTES) / chunkSize; // 计算每个包最多能容纳的 chunks 数量

        // 将 missingChunks 拆分为多个子列表，每个子列表的大小不超过 maxChunksPerPacket
        for (int i = 0; i < needUpdateChunks.size(); i += maxChunksPerPacket) {
            int end = Math.min(i + maxChunksPerPacket, needUpdateChunks.size());
            List<Long> chunkBatch = needUpdateChunks.subList(i, end);
           long[] chunks = new long[chunkBatch.size()];
           for (int j =0;j<chunks.length;j++){
               chunks[j] = chunkBatch.get(j);
           }

            // 创建并发送包
            RequestLargeMapDataC2SPacket packet = new RequestLargeMapDataC2SPacket(mapId, chunks);
            ClientPlayNetworking.send(ModPackets.REQUEST_LARGE_MAP_DATA, packet.toPacketByteBuf());
        }

        //更新图标数据
        if (needUpdateChunks.isEmpty()){
            RequestLargeMapDataC2SPacket packet = new RequestLargeMapDataC2SPacket(mapId, new long[]{});
            ClientPlayNetworking.send(ModPackets.REQUEST_LARGE_MAP_DATA, packet.toPacketByteBuf());
        }


    }

    private void cleanCache(){
        this.chunkTextures.clear();
    }


    private int decodeColor(byte colorByte) {
        int mapColorId = colorByte & 0b00111111;
        int brightnessId = (colorByte & 0b11000000) >>> 6;
        int argb = MapColor.get(mapColorId).color;
        argb=adjustColorBrightness(argb,MapColor.Brightness.validateAndGet(brightnessId));
//        System.out.println(colorByte+" "+argb);
//        int argb = colorByte;
        // 提取颜色通道（默认是 ARGB）
        int alpha = (argb >> 24) & 0xFF;
        int red = (argb >> 16) & 0xFF;
        int green = (argb >> 8) & 0xFF;
        int blue = argb & 0xFF;

        // 转换为 BGRA 格式
        return (0xFF << 24) | (blue << 16) | (green << 8) | red;
    }

    private int adjustColorBrightness(int originalColor, MapColor.Brightness brightness) {
        float brightnessFactor = switch (brightness) {
            case HIGH -> 1.2f; // 更亮
            case LOW -> 0.8f; // 更暗
            default -> 1.0f; // 普通
        };

        // 分解 RGB 分量
        int r = (originalColor >> 16) & 0xFF;
        int g = (originalColor >> 8) & 0xFF;
        int b = originalColor & 0xFF;

        // 应用亮度因子
        r = Math.min(255, (int) (r * brightnessFactor));
        g = Math.min(255, (int) (g * brightnessFactor));
        b = Math.min(255, (int) (b * brightnessFactor));

        // 重新组合颜色
        return (r << 16) | (g << 8) | b;
    }

    private Identifier createChunkTexture(long chunkKey, byte[] colors) {
        // 生成 NativeImage
        NativeImage image = new NativeImage(16, 16, false);
        for (int localZ = 0; localZ < 16; localZ++) {
            for (int localX = 0; localX < 16; localX++) {
                int index = localZ * 16 + localX;
                int color = decodeColor(colors[index]);
                image.setColor(localX, localZ, color);
            }
        }

        // 创建 NativeImageBackedTexture
        NativeImageBackedTexture texture = new NativeImageBackedTexture(image);

        // 将纹理注册为 Identifier
        Identifier textureId = new Identifier("large_map", "chunk_" + chunkKey);
        MinecraftClient.getInstance().getTextureManager().registerTexture(textureId, texture);

        // 缓存纹理
        chunkTextures.put(chunkKey, textureId);
        textureCache.put(chunkKey, texture);

        return textureId;
    }

    private void cleanupTextures() {
        for (NativeImageBackedTexture texture : textureCache.values()) {
            texture.close(); // 释放纹理资源
        }
        textureCache.clear();
        chunkTextures.clear();
    }

    @Override
    public void removed() {
        // 清理所有加载的纹理
        cleanupTextures();
        super.removed();
    }

    private void renderChunk(DrawContext context, int startX, int startY, long chunkKey, byte[] colors,int borderXMin,int borderXMax,int borderYMin,int borderYMax) {
        int scaledSize = (int) (16 * scale); // 缩放后的区块大小
        if (startX+scaledSize >= borderXMin && startX <= borderXMax && startY+scaledSize >= borderYMin && startY <= borderYMax ){
            Identifier textureId = chunkTextures.get(chunkKey);
            if (textureId == null) {
                textureId = createChunkTexture(chunkKey, colors);
            }

            int u = 0;
            int v = 0;
            int width = scaledSize;
            int height = scaledSize;

            if (startX < borderXMin && startX + scaledSize >= borderXMin) {
                u = borderXMin - startX;
            } else if (startX + scaledSize > borderXMax) {
                width = borderXMax - startX;
            }
            if (startY < borderYMin && startX + scaledSize >= borderYMin) {
                v = borderYMin - startY;
            } else if (startY + scaledSize > borderYMax) {
                height = borderYMax - startY;
            }

            // 渲染纹理
            context.drawTexture(textureId, startX + u, startY + v,u, v, width, height, scaledSize, scaledSize);
        }
    }


    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (mouseX <= borderXMax && mouseX >= borderXMin && mouseY <= borderYMax && mouseY >= borderYMin){
            //左键
            if (button == 0) {
                dragging = true;
                dragStartX = (int) mouseX;
                dragStartY = (int) mouseY;
                dragStartOriginalX = startX;
                dragStartOriginalZ = startZ;
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0){
            dragging = false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (dragging && button == 0) { // 如果正在拖动
            // 计算新的屏幕坐标
            float dragSpeed = 1.0f;
            startX = dragStartOriginalX - (int) ((mouseX - dragStartX)/scale* dragSpeed);
            startZ = dragStartOriginalZ - (int) ((mouseY - dragStartY)/scale* dragSpeed);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        if (amount > 0){
            zoom(true);
        }else if (amount < 0){
            zoom(false);
        }
        return true;
    }

    private void zoom(boolean in){
        this.scale = in ? scale*2 : scale/2;
        this.scale = Math.min(Math.max(this.scale, MIN_SCALE),MAX_SCALE);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        int moveSpeed = (int) (16 / scale);
        if (AllKeys.LARGE_MAP_MOVE_UP.matchesKey(keyCode, scanCode)) {
            startZ -= moveSpeed;
            return true;
        }else if (AllKeys.LARGE_MAP_MOVE_DOWN.matchesKey(keyCode, scanCode)){
            startZ += moveSpeed;
            return true;
        }else if (AllKeys.LARGE_MAP_MOVE_LEFT.matchesKey(keyCode, scanCode)){
            startX -= moveSpeed;
            return true;
        }else if (AllKeys.LARGE_MAP_MOVE_RIGHT.matchesKey(keyCode, scanCode)){
            startX += moveSpeed;
            return true;
        }else if (AllKeys.LARGE_MAP_ZOOM_IN.matchesKey(keyCode, scanCode)){
            zoom(true);
            return true;
        }else if (AllKeys.LARGE_MAP_ZOOM_OUT.matchesKey(keyCode, scanCode)){
            zoom(false);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        context.drawTexture(BACKGROUND_TEXTURE, backgroundX, backgroundY, 0, 0, backgroundWidth, backgroundHeight);




        // 初始的绝对坐标
        int initialChunkX = Math.floorDiv(this.startX, 16);
        int initialChunkZ = Math.floorDiv(this.startZ, 16);

        int offsetX = this.startX - initialChunkX * 16;
        int offsetZ = this.startZ - initialChunkZ * 16;

        // 地图的中心像素坐标
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        // 渲染范围，决定当前屏幕显示半径的区块数量
        int renderRadius = (int) (9 / scale); // 缩放时调整渲染半径
        List<Long> needUpdateChunks = new ArrayList<>();
        int startChunkX = initialChunkX - renderRadius;
        int startChunkZ = initialChunkZ - renderRadius;
        int endChunkX = initialChunkX + renderRadius;
        int endChunkZ = initialChunkZ + renderRadius;

        // 遍历 colorMap 绘制每个区块
        for (int chunkX = startChunkX; chunkX <= endChunkX; chunkX++) {
            for (int chunkZ = startChunkZ; chunkZ <= endChunkZ; chunkZ++) {
                long chunkKey = ChunkPos.toLong(chunkX, chunkZ);

                if (this.largeMapState.getColorMap().containsKey(chunkKey)) {
                    byte[] colors = this.largeMapState.getColor(chunkKey);

                    // 计算区块的屏幕坐标
                    int relativeChunkX = chunkX - initialChunkX;
                    int relativeChunkZ = chunkZ - initialChunkZ;

                    int chunkScreenX = (int) (centerX + (relativeChunkX * 16 - offsetX) * scale);
                    int chunkScreenY = (int) (centerY + (relativeChunkZ * 16 - offsetZ) * scale);


                    renderChunk(context,chunkScreenX,chunkScreenY,chunkKey,colors,
                            borderXMin,borderXMax,borderYMin,borderYMax);
                }
                needUpdateChunks.add(chunkKey);
            }
        }


        for(LargeMapState.MapIconData mapIconData : largeMapState.getIconDataList()){
            BlockPos iconPos = mapIconData.getBlockPos();
            int iconOffsetX = (int) ((iconPos.getX() - this.startX)*scale);
            int iconOffsetY = (int) ((iconPos.getZ() - this.startZ)*scale);
            int iconX = screenWidth / 2 + iconOffsetX;
            int iconY = screenHeight / 2 + iconOffsetY;
            float iconYaw = mapIconData.getYaw();
            if (iconX >= borderXMin && iconY >= borderYMin && iconX+16 <= borderXMax && iconY + 16 <= borderYMax){
                renderIcon(context, iconX, iconY, mapIconData.getMapIcon(), iconYaw,mapIconData.getText());
            }
        }

        if (updateColorCooldown <= 0){
            requestMapData(mapId, needUpdateChunks);
            updateColorCooldown = MAX_UPDATE_COLOR_COOLDOWN;
            updateCacheCooldown -= 1;
        }else {
            updateColorCooldown--;
        }

        if (updateCacheCooldown <= 0){
            cleanCache();
            updateCacheCooldown = MAX_UPDATE_CACHE_COOLDOWN;
        }



        super.render(context, mouseX, mouseY, delta);
    }

    private void renderIcon(DrawContext context, int x, int y, LargeMapState.MapIcon icon,float iconYaw,Text text){
        int index = icon.textureIndex;
        int v = index / 16;
        int u = index - v * 16;
        int scaleOffset = (int)(0.5f*scale);
        int iconX = x+icon.offsetX + scaleOffset;
        int iconY = y+icon.offsetY + scaleOffset;
        MatrixStack ms = context.getMatrices();
        ms.push();
        if (text != null){
            int textWidth = textRenderer.getWidth(text);
            int textHeight = textRenderer.fontHeight;
            context.drawText(textRenderer, text, x - textWidth/2 + scaleOffset, y - textHeight/2 + scaleOffset + icon.textOffset, 0xFFFFFF, true);
        }
        ms.translate(iconX + 8,iconY + 8,0);
        ms.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(iconYaw+180));
        ms.translate(-iconX - 8,-iconY - 8,0);
        context.drawTexture(MAP_ICON, iconX, iconY, u*16, v*16, 16, 16);
        ms.pop();

    }

    public void updateMapColor(Map<Long,byte[]> colors) {
        this.largeMapState.setColors(colors);
    }

    public void updateIcon(ArrayList<LargeMapState.MapIconData> iconData){
        this.largeMapState.setIconDataList(iconData);
    }
}
