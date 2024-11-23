package com.ffsupver.asplor.item.item.largeMap;

import com.ffsupver.asplor.util.NbtUtil;
import com.simibubi.create.Create;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.entity.Train;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LargeMapState extends PersistentState {


    private final Map<Long,byte[]> colorMap;
    public final RegistryKey<World> dimension;
    private final Map<PlayerEntity,MapIconData> playerIcons = new HashMap<>();
    private final Map<Entity,MapIconData> entityIcons = new HashMap<>();
    private final Map<UUID,MapIconData> entityIconNeedToGet = new HashMap<>();
    private final ArrayList<UUID> trainsNotOnMap = new ArrayList<>();
    private ArrayList<MapIconData> iconDataList;
    private final ArrayList<MapIconData> staticIconList = new ArrayList<>();

    private final ArrayList<PlayerEntity> updatePlayerList = new ArrayList<>();
    private final int id;


    public LargeMapState(RegistryKey<World> dimension, Map<Long,byte[]> colorMap, ArrayList<MapIconData> iconDataList) {
        this(dimension,colorMap,iconDataList,0);
    }
    public LargeMapState(RegistryKey<World> dimension, Map<Long,byte[]> colorMap, int id) {
        this(dimension,colorMap,new ArrayList<>(),0);
    }

    public LargeMapState(RegistryKey<World> dimension, Map<Long,byte[]> colorMap, ArrayList<MapIconData> iconDataList,int id) {
        this.dimension = dimension;
        this.colorMap = colorMap;
        this.iconDataList = iconDataList;
        this.id = id;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtList chunkList = new NbtList();
        for (long chunk : this.colorMap.keySet()){
            NbtCompound chunkNbt = new NbtCompound();
            chunkNbt.putLong("chunk",chunk);
            NbtList colors = new NbtList();
            for(Byte color : colorMap.get(chunk)){
                colors.add(NbtByte.of(color));
            }
            chunkNbt.put("colors",colors);
            chunkList.add(chunkNbt);
        }
        nbt.put("chunks",chunkList);
        nbt.putString("dimension",dimension.getValue().toString());
        nbt.putInt("id",id);
        NbtList staticIconListNbt = new NbtList();
        for (MapIconData mapIconData : this.staticIconList){
            staticIconListNbt.add(mapIconData.writeToNbt());
        }
        nbt.put("static_icons",staticIconListNbt);

        NbtList entityIconListNbt = new NbtList();
        for(Map.Entry<Entity,MapIconData> entry : entityIcons.entrySet()){
            Entity entity = entry.getKey();
            MapIconData mapIconData = entry.getValue();
            NbtCompound entityIconNbt = new NbtCompound();
            entityIconNbt.putString("UUID",entity.getUuid().toString());
            entityIconNbt.put("icon_data",mapIconData.writeToNbt());
            entityIconListNbt.add(entityIconNbt);
        }
        if (!entityIconNeedToGet.isEmpty()){
            for(Map.Entry<UUID,MapIconData> entry : entityIconNeedToGet.entrySet()){
                NbtCompound unloadEntityNbt = new NbtCompound();
                unloadEntityNbt.putString("UUID",entry.getKey().toString());
                unloadEntityNbt.put("icon_data",entry.getValue().writeToNbt());
                entityIconListNbt.add(unloadEntityNbt);
            }
        }
        nbt.put("entity_icons",entityIconListNbt);
        NbtList trainsNotOnMapListNbt = new NbtList();
        for (UUID trainId : trainsNotOnMap){
            trainsNotOnMapListNbt.add(NbtString.of(trainId.toString()));
        }
        nbt.put("trains",trainsNotOnMapListNbt);

        return nbt;
    }

    public static LargeMapState fromNbt(NbtCompound nbt) {
        Map<Long,byte[]> colorMap = new HashMap<>();
        NbtList chunkList = nbt.getList("chunks",10);
        for (NbtElement e : chunkList){
            long chunk = ((NbtCompound)e).getLong("chunk");

            byte[] colors = new byte[256];
            NbtList colorList = ((NbtCompound) e).getList("colors",1);
            for (int i=0;i<colorList.size();i++){
                colors[i] = ((NbtByte) colorList.get(i)).byteValue();
            }
            colorMap.put(chunk,colors);
        }
        RegistryKey<World> dimension = RegistryKey.of(RegistryKeys.WORLD,new Identifier(nbt.getString("dimension")));
        int id = nbt.getInt("id");

        LargeMapState fromNbt = new LargeMapState(dimension,colorMap,id);

        NbtList staticIconListNbt = nbt.getList("static_icons",10);
        for (NbtElement element : staticIconListNbt){
            NbtCompound mapIconDataNbt = (NbtCompound)element;
            MapIconData mapIconData = MapIconData.readFromNbt(mapIconDataNbt);
            fromNbt.addStaticIcon(mapIconData);
        }

        NbtList entityIconListNbt = nbt.getList("entity_icons",10);
        for (NbtElement e : entityIconListNbt){
            NbtCompound entityData = (NbtCompound) e;
            UUID entityId = UUID.fromString(entityData.getString("UUID"));
            MapIconData mapIconData = MapIconData.readFromNbt(entityData.getCompound("icon_data"));
            fromNbt.addEntityNeedToUpdate(entityId,mapIconData);
        }
        NbtList trainsNotOnMapListNbt = nbt.getList("trains",8);
        for (NbtElement e : trainsNotOnMapListNbt){
            String trainIdString = e.asString();
            fromNbt.trainsNotOnMap.add(UUID.fromString(trainIdString));
        }

        return fromNbt;
    }

    public static LargeMapState readFromBuf(PacketByteBuf buf) {
        // 读取维度 (RegistryKey<World>)
        Identifier dimensionId = buf.readIdentifier();
        RegistryKey<World> dimension = RegistryKey.of(RegistryKeys.WORLD, dimensionId);

        // 读取 colorMap 的大小
        int colorMapSize = buf.readInt();
        Map<Long, byte[]> colorMap = new HashMap<>();

        // 读取 colorMap 的内容
        for (int i = 0; i < colorMapSize; i++) {
            long chunkKey = buf.readLong(); // 读取区块标识符
            byte[] colors = buf.readByteArray(); // 读取颜色数据
            colorMap.put(chunkKey, colors);
        }

        //读取icon
        int iconCount = buf.readInt();
        ArrayList<MapIconData> iconDataList = new ArrayList<>();
        for (int i = 0;i<iconCount;i++){
            iconDataList.add(MapIconData.readFromBuf(buf));
        }



        // 创建并返回 LargeMapState 实例
        return new LargeMapState(dimension, colorMap,iconDataList);
    }

    public void writeToBuf(PacketByteBuf buf) {
        // 写入维度 (RegistryKey<World>)
        buf.writeIdentifier(this.dimension.getValue()); // 将维度的 ID 写入

        // 写入 colorMap 的大小
        buf.writeInt(this.colorMap.size());

        // 写入 colorMap 的内容
        this.colorMap.forEach((chunkKey, colors) -> {
            buf.writeLong(chunkKey); // 写入区块标识符
            buf.writeByteArray(colors); // 写入对应的颜色数据
        });

        //写入图标
        buf.writeInt(this.iconDataList.size());
        for (MapIconData mapIconData : this.iconDataList){
            mapIconData.writeToBuf(buf);
        }
    }

    public void setColor(long chunk,byte[] colors){
       if (this.colorMap.containsKey(chunk)){
           byte[] originColors = this.colorMap.get(chunk);
           if (originColors != colors){
               this.colorMap.remove(chunk);
           }else {
               return;
           }
       }
        this.colorMap.put(chunk,colors);
       this.markDirty();
    }

    public void setColors(Map<Long,byte[]> colors){
        this.colorMap.putAll(colors);
    }

    public byte[] getColor(long chunk){
        return this.colorMap.get(chunk);
    }

    public Map<Long,byte[]> getColors(long[] chunks){
        Map<Long,byte[]> result = new HashMap<>();
        for(long chunk : chunks){
            if (this.colorMap.containsKey(chunk)){
                result.put(chunk,this.colorMap.get(chunk));
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder chunks = new StringBuilder();
        for (long chunk : this.colorMap.keySet()){
            ChunkPos chunkPos = new ChunkPos(chunk);
            chunks.append(" chunk: " +chunkPos.x+ "," +chunkPos.z+" \n");

//            chunks.append(chunk+":\n");
//            int i = 0;
//            for (byte color : this.colorMap.get(chunk)){
//                chunks.append(color+" ");
//                i++;
//                if (i >= 16){
//                    chunks.append("\n");
//                    i=0;
//                }
//            }
        }
        return "Large Map State "+this.dimension+"\n\tcolors: "+chunks;
    }

    public LargeMapState getRenderState(ServerPlayerEntity player){
        ChunkPos chunkPos = player.getWorld().getRegistryKey().equals(this.dimension) ? new ChunkPos(player.getBlockPos()) :
                !this.colorMap.keySet().isEmpty() ? new ChunkPos(this.colorMap.keySet().stream().findFirst().get()) : new ChunkPos(0);
        LargeMapState renderState = new LargeMapState(this.dimension,new HashMap<>(),this.iconDataList);
        for (long chunk : this.colorMap.keySet()){
            ChunkPos testChunkPos = new ChunkPos(chunk);
            if (Math.abs(testChunkPos.x-chunkPos.x) <= 2 && Math.abs(testChunkPos.z-chunkPos.z) <= 2){
                renderState.setColor(chunk,this.colorMap.get(chunk));
            }
        }
        return renderState;
    }

    public Map<Long, byte[]> getColorMap() {
        return colorMap;
    }

    public void addStaticIcon(MapIconData mapIconData){
        if (!this.staticIconList.contains(mapIconData)){
            this.staticIconList.add(mapIconData);
        }
    }

    public void removeStaticIcon(MapIconData mapIconData){
        this.staticIconList.remove(mapIconData);
    }

    public void addOrRemoveStaticIcon(BlockPos pos,MapIcon mapIcon,@Nullable Text text){
         for(MapIconData mapIconData : this.iconDataList){
            if (mapIconData.blockPos.getX() == pos.getX() && mapIconData.blockPos.getZ() == pos.getZ() && mapIconData.mapIcon.equals(mapIcon)){
                this.staticIconList.remove(mapIconData);
                return;
            }
         }
         addStaticIcon(new MapIconData(pos,mapIcon,text));
    }

    private boolean isOnMap(Entity entity){
        return !entity.isRemoved() && entity.getWorld().getRegistryKey().equals(dimension);
    }

    public boolean checkAndAddPlayer(PlayerEntity player){
        if (isOnMap(player)){
            if (!this.playerIcons.containsKey(player)){
                this.playerIcons.put(player,new MapIconData(player.getBlockPos(),MapIcon.PLAYER,player.getYaw(),player.getName()));
                return true;
            }
        }
        return false;
    }

    private void updatePlayerIcons(){
            Iterator<Map.Entry<PlayerEntity, MapIconData>> iterator = this.playerIcons.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<PlayerEntity, MapIconData> entry = iterator.next();
                PlayerEntity player = entry.getKey();
                if (isOnMap(player) && LargeMapItem.hasMap(player, this.id)) {
                    MapIconData playIconData = entry.getValue();
                    playIconData.setBlockPos(player.getBlockPos());
                    playIconData.setYaw(player.getYaw());
                    entry.setValue(playIconData);
                } else {
                    iterator.remove(); // 使用 Iterator 的 remove 方法
                }
            }
    }

    public void AddOrRemoveEntity(Entity entity){
        if (isOnMap(entity)){
            if (!entityIcons.containsKey(entity)){
                boolean rotateIcon = true;
                if (entity.getType().equals(EntityType.WITHER)){
                    rotateIcon = false;
                }
                Text name = entity instanceof CarriageContraptionEntity carriageContraptionEntity?
                        Create.RAILWAYS.trains.get(carriageContraptionEntity.trainId).name : entity.getCustomName();
                MapIconData entityIconData = MapIconData.fromEntity(entity,name,rotateIcon);
                addEntityIcon(entity,entityIconData);
            }else {
                entityIcons.remove(entity);
            }
        }
    }

    public void addEntityNeedToUpdate(UUID uuid,MapIconData mapIconData){
        if (!this.entityIconNeedToGet.containsKey(uuid)){
            this.entityIconNeedToGet.put(uuid, mapIconData);
        }
    }

    private void addEntityIcon(Entity entity,MapIconData mapIconData){
        if (!this.entityIcons.containsKey(entity)){
            this.entityIcons.put(entity,mapIconData);
        }
    }

    private void updateEntityIcons(World world){
        if (!world.isClient()){
            ServerWorld serverWorld = ((ServerWorld)world).getServer().getWorld(this.dimension);
            Iterator<Map.Entry<UUID,MapIconData>> needUpdateEntityIterator = this.entityIconNeedToGet.entrySet().iterator();
            while (needUpdateEntityIterator.hasNext()){
                Map.Entry<UUID,MapIconData> entityByIdData = needUpdateEntityIterator.next();
                Entity entity = null;
                if (serverWorld != null) {
                    entity = serverWorld.getEntity(entityByIdData.getKey());
                }
                if (entity != null){
                    addEntityIcon(entity,entityByIdData.getValue());
                    needUpdateEntityIterator.remove();
                }
            }
            Iterator<UUID> trainsNotOnMapIterator = trainsNotOnMap.iterator();
            while (trainsNotOnMapIterator.hasNext()){
                UUID trainId = trainsNotOnMapIterator.next();
                Train train = Create.RAILWAYS.trains.get(trainId);
                if (train != null && !train.carriages.isEmpty()){
                    CarriageContraptionEntity carriageContraptionEntity = train.carriages.get(0).anyAvailableEntity();
                    if (carriageContraptionEntity != null && isOnMap(carriageContraptionEntity)){
                        addEntityIcon(carriageContraptionEntity,MapIconData.fromEntity(carriageContraptionEntity));
                        trainsNotOnMapIterator.remove();
                    }
                }else {
                    trainsNotOnMapIterator.remove();
                }
            }
        }
        Iterator<Map.Entry<Entity,MapIconData>> iterator = this.entityIcons.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<Entity,MapIconData> entry = iterator.next();
            Entity entity = entry.getKey();
            if (isOnMap(entity)){
                MapIconData entityIconData = entry.getValue();
                entityIconData.setBlockPos(entity.getBlockPos());
                if (entity instanceof CarriageContraptionEntity carriageContraptionEntity){
                    entityIconData.setYaw(carriageContraptionEntity.yaw + 90);
                }else {
                    entityIconData.setYaw(entity.getYaw());
                }
                entry.setValue(entityIconData);
            }else {
                List<Entity.RemovalReason> needToAddReason = List.of(Entity.RemovalReason.CHANGED_DIMENSION, Entity.RemovalReason.UNLOADED_TO_CHUNK);
                if (needToAddReason.contains(entity.getRemovalReason())){
                    addEntityNeedToUpdate(entity.getUuid(), entry.getValue());
                }else if (Entity.RemovalReason.DISCARDED.equals(entity.getRemovalReason()) && entity instanceof CarriageContraptionEntity carriageContraptionEntity){
                    Train train = Create.RAILWAYS.trains.get(carriageContraptionEntity.trainId);
                    if (train != null && !train.carriages.isEmpty()){
                        this.trainsNotOnMap.add(train.id);
                    }
                }
                iterator.remove();
            }
        }
    }

    public void updateByList(PlayerEntity playerEntity){
        if (this.updatePlayerList.contains(playerEntity)) {
            removePlayerWithoutMap();
            if (!this.updatePlayerList.isEmpty() && this.updatePlayerList.get(0).equals(playerEntity)){
                updatePlayerIcons();
                updateEntityIcons(playerEntity.getWorld());
            }
        }else {
            this.updatePlayerList.add(playerEntity);
        }
        updateIconList();
    }

    private void removePlayerWithoutMap(){
        this.updatePlayerList.removeIf(player -> !LargeMapItem.hasMap(player, this.id));
    }

    public void updateIconList(){
        this.iconDataList = new ArrayList<>();
        this.iconDataList.addAll(this.playerIcons.values());
        this.iconDataList.addAll(this.entityIcons.values());
        this.iconDataList.addAll(this.staticIconList);
    }



    public ArrayList<MapIconData> getIconDataList() {
        return iconDataList;
    }

    public void setIconDataList(ArrayList<MapIconData> iconDataList) {
        this.iconDataList = iconDataList;
    }






    public static class MapIconData {
        private MapIcon mapIcon;
        private BlockPos blockPos;
        private float yaw;
        private final Text text;
        private final boolean canRotate;
        public MapIconData(BlockPos blockPos, MapIcon mapIcon, float yaw, Text text,boolean canRotate){
            this.blockPos = blockPos;
            this.mapIcon = mapIcon;
            this.yaw = yaw;
            this.text = text;
            this.canRotate = canRotate;
        }

        public MapIconData(BlockPos blockPos,MapIcon mapIcon,Text text){
            this( blockPos, mapIcon,180,text,true);
        }
        public MapIconData(BlockPos blockPos,MapIcon mapIcon,float yaw,Text text){
            this( blockPos, mapIcon,yaw,null,true);
        }

        public static MapIconData fromEntity(Entity entity,Text name,boolean rotateIcon){
            return  new MapIconData(entity.getBlockPos(),MapIcon.getEntityIconByEntity(entity),entity.getYaw(),name,rotateIcon);
        }

        public static MapIconData fromEntity(Entity entity){
            Text name = null;
            if (entity instanceof CarriageContraptionEntity carriageContraptionEntity){
                Train train = Create.RAILWAYS.trains.get(carriageContraptionEntity.trainId);
                name = train == null ? null : train.name;
            }
            return  MapIconData.fromEntity(entity,name,true);
        }

        public void setBlockPos(BlockPos blockPos) {
            this.blockPos = blockPos;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }

        public float getYaw() {
            return canRotate ?  yaw : 180;
        }

        public void setYaw(float yaw) {
            this.yaw = yaw;
        }

        public MapIcon getMapIcon() {
            return mapIcon;
        }

        public Text getText() {
            return text;
        }

        @Override
        public String toString() {
            return "MapIconData: "+blockPos+" "+mapIcon;
        }

        public NbtCompound writeToNbt(){
            NbtCompound nbt = new NbtCompound();
            nbt.put("pos",NbtUtil.writeBlockPosToNbt(blockPos));
            nbt.putInt("icon",mapIcon.id);
            nbt.putFloat("yaw",yaw);
            nbt.putString("text", Text.Serializer.toJson(text));
            nbt.putBoolean("can_rotate",canRotate);
            return nbt;
        }

        public static MapIconData readFromNbt(NbtCompound nbt){
            boolean can_rotate = true;
           if (nbt.contains("can_rotate")){
              can_rotate = nbt.getBoolean("can_rotate");
           }
           return new MapIconData(NbtUtil.readBlockPosFromNbt(nbt.getCompound("pos")),
                   MapIcon.get(nbt.getInt("icon")),
                   nbt.getFloat("yaw"),
                   Text.Serializer.fromJson(nbt.getString("text")),
                   can_rotate);
        }

        public void writeToBuf(PacketByteBuf buf){
            buf.writeBlockPos(blockPos);
            buf.writeEnumConstant(mapIcon);
            buf.writeFloat(yaw);
            buf.writeBoolean(this.text != null);
            if (this.text != null){
                buf.writeText(text);
            }
            buf.writeBoolean(canRotate);
        }

        public static MapIconData readFromBuf(PacketByteBuf buf){
            BlockPos blockPos = buf.readBlockPos();
            MapIcon mapIcon = buf.readEnumConstant(MapIcon.class);
            float yaw = buf.readFloat();
            Text text1 = null;
            if (buf.readBoolean()){
               text1 = buf.readText();
            }
            boolean can_rotate = buf.readBoolean();
            return new MapIconData(blockPos,mapIcon,yaw,text1,can_rotate);
        }
    }

    public static enum MapIcon {
        LOCATION(-4,-11,0),
        PLAYER(-8,-9,1),
        WHITE_BANNER(-8,-8,2),
        ORANGE_BANNER(-8,-8,3),
        MAGENTA_BANNER(-8,-8,4),
        LIGHT_BLUE_BANNER(-8,-8,5),
        YELLOW_BANNER(-8,-8,6),
        LIME_BANNER(-8,-8,7),
        PINK_BANNER(-8,-8,8),
        GRAY_BANNER(-8,-8,9),
        LIGHT_GRAY_BANNER(-8,-8,10),
        CYAN_BANNER(-8,-8,11),
        PURPLE_BANNER(-8,-8,12),
        BLUE_BANNER(-8,-8,13),
        BROWN_BANNER(-8,-8,14),
        GREEN_BANNER(-8,-8,15),
        RED_BANNER(-8,-8,16),
        BLACK_BANNER(-8,-8,17),
        TRAIN(-8,-8,18,12),
        WITHER(-8,-8,19),
        STATION(-8,-12,20)
        ;
        public final int offsetX;
        public final int offsetY;
        public final int textOffset;
        public final int textureIndex;
        public final int id;
        private static MapIcon[] ICONS = new MapIcon[]{
                LOCATION,PLAYER,WHITE_BANNER,ORANGE_BANNER,MAGENTA_BANNER,LIGHT_BLUE_BANNER,
                YELLOW_BANNER,LIME_BANNER,PINK_BANNER,GRAY_BANNER,LIGHT_GRAY_BANNER,CYAN_BANNER,PURPLE_BANNER,
                BLUE_BANNER,BROWN_BANNER,GREEN_BANNER,RED_BANNER,BLACK_BANNER,TRAIN,WITHER,STATION};

        MapIcon(int offsetX, int offsetY, int textureIndex, int id,int textOffset) {
            this.offsetX = offsetX;
            this.offsetY = offsetY;
            this.textureIndex = textureIndex;
            this.id = id;
            this.textOffset = textOffset;
        }
        MapIcon(int offsetX, int offsetY, int textureIndex){
            this(offsetX,offsetY,textureIndex,textureIndex,6);
        }
        MapIcon(int offsetX, int offsetY, int textureIndex,int textOffset){
            this(offsetX,offsetY,textureIndex,textureIndex,textOffset);
        }

        public static MapIcon get(int id){
            id = Math.min(id,ICONS.length);
            return ICONS[id];
        }

        public static MapIcon getBannerIconByColor(DyeColor color){
           return switch (color){
               case WHITE -> WHITE_BANNER;
               case ORANGE -> ORANGE_BANNER;
               case MAGENTA -> MAGENTA_BANNER;
               case LIGHT_BLUE -> LIGHT_BLUE_BANNER;
               case YELLOW -> YELLOW_BANNER;
               case LIME -> LIME_BANNER;
               case PINK -> PINK_BANNER;
               case LIGHT_GRAY -> LIGHT_GRAY_BANNER;
               case GRAY -> GRAY_BANNER;
               case CYAN -> CYAN_BANNER;
               case PURPLE -> PURPLE_BANNER;
               case BLUE -> BLUE_BANNER;
               case BROWN -> BROWN_BANNER;
               case GREEN -> GREEN_BANNER;
               case RED -> RED_BANNER;
               case BLACK -> BLACK_BANNER;
           };
        }

        public static MapIcon getEntityIconByEntity(Entity entity){
            if (entity.getType().equals(EntityType.WITHER)){
                return WITHER;
            }
           return TRAIN;
        }

        @Override
        public String toString() {
            return "MapIcon "+this.name();
        }
    }
}
