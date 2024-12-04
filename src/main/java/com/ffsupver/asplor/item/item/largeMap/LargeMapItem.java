package com.ffsupver.asplor.item.item.largeMap;

import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.networking.packet.large_map.OpenLargeMapS2CPacket;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.trains.entity.CarriageContraptionEntity;
import com.simibubi.create.content.trains.station.StationBlockEntity;
import earth.terrarium.adastra.common.blocks.LaunchPadBlock;
import earth.terrarium.adastra.common.blocks.properties.LaunchPadPartProperty;
import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.MapColor;
import net.minecraft.block.entity.BannerBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.NetworkSyncedItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.*;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;
import net.minecraft.world.chunk.WorldChunk;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

import static com.ffsupver.asplor.ModTags.Blocks.LARGE_MAP_ITEM_INTERACT_BLOCK;
import static com.ffsupver.asplor.ModTags.Blocks.LARGE_MAP_MARK_BLOCK;
import static com.ffsupver.asplor.ModTags.EntityTypes.CAN_MARK_ON_MAP;

public class LargeMapItem extends NetworkSyncedItem {
    public LargeMapItem(Settings settings) {
        super(settings);
    }

    @Nullable
    public static LargeMapState getMapState(@Nullable Integer id, World world) {
        return id == null ? null :
                world.isClient() ? null : world.getServer().getOverworld().getPersistentStateManager().get(LargeMapState::fromNbt,getLargeMapName(id));
    }

    public static ItemStack createMap(World world){
        ItemStack itemStack = new ItemStack(ModItems.LARGE_MAP,1);
        createMapState(itemStack,world,world.getRegistryKey());
        return itemStack;
    }

    public static int allocateMapId(World world,RegistryKey<World> dimension){
        int id = world.getNextMapId();
        LargeMapState largeMapState = new LargeMapState(dimension,new HashMap<>(),id);
        if (!world.isClient()){
            world.getServer().getOverworld().getPersistentStateManager().set(getLargeMapName(id), largeMapState);
        }
        return id;
    }

    private static void setMapId(ItemStack stack, int id) {
        stack.getOrCreateNbt().putInt("large_map", id);
    }

    private static void createMapState(ItemStack stack, World world, RegistryKey<World> dimension){
       int id = allocateMapId(world,dimension);
       setMapId(stack,id);
    }

    @Nullable
    public static LargeMapState getMapState(ItemStack map, World world) {
        Integer integer = getMapId(map);
        return getMapState(integer, world);
    }

    @Nullable
    public static Integer getMapId(ItemStack stack) {
        NbtCompound nbtCompound = stack.getNbt();
        return nbtCompound != null && nbtCompound.contains("large_map", 99) ? nbtCompound.getInt("large_map") : null;
    }
    private static String getLargeMapName(Integer id) {
        return "large_map"+id;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if (!user.isSneaking()){

            LargeMapState largeMapState = getMapState(itemStack, world);
            world.playSound(user.getX(),user.getY(),user.getZ(), SoundEvents.ITEM_BOOK_PAGE_TURN, SoundCategory.PLAYERS,1,1,true);
            if (!user.getWorld().isClient() && largeMapState != null && getMapId(itemStack) != null) {
                int mapId = getMapId(itemStack);
                LargeMapState renderMapState = largeMapState.getRenderState((ServerPlayerEntity) user);
                OpenLargeMapS2CPacket.send((ServerPlayerEntity) user, hand, mapId, renderMapState);
            }
            return TypedActionResult.success(itemStack, world.isClient());
        }
        return TypedActionResult.pass(itemStack);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);
        BlockEntity blockEntity = world.getBlockEntity(pos);
        LargeMapState largeMapState = getMapState(context.getStack(),context.getWorld());
        if (largeMapState != null && largeMapState.dimension.equals(world.getRegistryKey())) {
            if (state.isIn(LARGE_MAP_MARK_BLOCK)) {
                largeMapState.addOrRemoveStaticIcon(pos, LargeMapState.MapIcon.LOCATION, null);
                return ActionResult.SUCCESS;
            } else if (blockEntity instanceof BannerBlockEntity bannerBlockEntity) {
                Text customName = bannerBlockEntity.getCustomName();
                LargeMapState.MapIcon bannerMapIcon = LargeMapState.MapIcon.getBannerIconByColor(bannerBlockEntity.getColorForState());
                largeMapState.addOrRemoveStaticIcon(pos, bannerMapIcon, customName);
                return ActionResult.SUCCESS;
            } else if (blockEntity instanceof StationBlockEntity stationBlockEntity) {
                Text stationName = stationBlockEntity.getStation() == null ? null : Text.literal(stationBlockEntity.getStation().name);
                largeMapState.addOrRemoveStaticIcon(pos, LargeMapState.MapIcon.STATION,stationName);
                return ActionResult.SUCCESS;
            } else if (state.isOf(AllBlocks.TRACK.get())) {
                List<Entity> entities = world.getOtherEntities(null, new Box(pos.getX() - 3, pos.getY(), pos.getZ() - 3, pos.getX() + 3, pos.getY() + 5, pos.getZ() + 3),
                        entity -> entity instanceof CarriageContraptionEntity);
                for (Entity entity : entities) {
                    if (entity instanceof CarriageContraptionEntity){
                        largeMapState.AddOrRemoveEntity(entity);
                    }
                }
                return ActionResult.SUCCESS;
            } else if (state.isOf(ModBlocks.LAUNCH_PAD.get()) && state.get(LaunchPadBlock.PART).equals(LaunchPadPartProperty.CENTER)) {
                largeMapState.addOrRemoveStaticIcon(pos, LargeMapState.MapIcon.LAUNCH_PAD,null);
                return ActionResult.SUCCESS;
            }
        }
        if (world.isClient() && state.isIn(LARGE_MAP_ITEM_INTERACT_BLOCK)){
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        World world = user.getWorld();
        if (entity.getType().isIn(CAN_MARK_ON_MAP)){
            LargeMapState largeMapState = getMapState(stack,world);
            if (largeMapState != null && largeMapState.dimension.equals(world.getRegistryKey())){
                largeMapState.AddOrRemoveEntity(entity);
            }
            return ActionResult.SUCCESS;
        }
        return super.useOnEntity(stack, user, entity, hand);
    }

    public static boolean hasMap(PlayerEntity player, int id){
        for (int i = 0;i < player.getInventory().size();i++){
            ItemStack itemStack = player.getInventory().getStack(i);
            if (itemStack.isOf(ModItems.LARGE_MAP)  && getMapId(itemStack) != null && getMapId(itemStack) == id){
                return true;
            }
        }
        return false;
    }

    private void updateColors(World world, Entity entity, LargeMapState largeMapState){
        if (world.getRegistryKey() == largeMapState.dimension && entity instanceof PlayerEntity player) {
            largeMapState.checkAndAddPlayer(player);
            largeMapState.updateByList(player);
            // 玩家当前坐标
            int playerChunkX = ChunkSectionPos.getSectionCoord(MathHelper.floor(entity.getX()));
            int playerChunkZ = ChunkSectionPos.getSectionCoord(MathHelper.floor(entity.getZ()));

            int updateRadius = 4;
            if (!world.isClient()){
               updateRadius = world.getServer().getPlayerManager().getSimulationDistance() - 2;
            }

            // 遍历玩家周围的区块
            for (int offsetZ = -updateRadius; offsetZ < updateRadius; offsetZ++) {
                int[] heights = new int[16];
                for (int offsetX = -updateRadius; offsetX < updateRadius; offsetX++) {
                    int chunkX = playerChunkX + offsetX;
                    int chunkZ = playerChunkZ + offsetZ;
                    long chunkPos = ChunkPos.toLong(chunkX, chunkZ); // 唯一标识区块

                    byte[] colors = new byte[256]; // 每个区块的颜色数组（16×16）


                    // 遍历区块中的 16×16 方块
                    for (int localZ = 0; localZ < 16; localZ++) {
                        int preHeight = heights[localZ];
                        for (int localX = 0; localX < 16; localX++) {
                            int worldX = chunkX * 16 + localX; // 绝对 X 坐标
                            int worldZ = chunkZ * 16 + localZ; // 绝对 Z 坐标

                            if (offsetX == -updateRadius && localX == 0) {
                                preHeight = world.getChunk(chunkX - 1, chunkZ).sampleHeightmap(Heightmap.Type.WORLD_SURFACE, worldX - 1, worldZ);
                            }


                            BlockPos.Mutable blockPos = new BlockPos.Mutable(worldX, 0, worldZ);
                            if (world.isChunkLoaded(chunkX, chunkZ)) {
                                WorldChunk chunk = world.getChunk(chunkX, chunkZ);

                                // 获取当前坐标最高的方块
                                int height = chunk.sampleHeightmap(Heightmap.Type.WORLD_SURFACE, worldX, worldZ);
                                blockPos.setY(height);

                                MapColor mapColor = MapColor.CLEAR;

                                while (height > world.getBottomY()) {
                                    // 获取方块的颜色
                                    BlockState blockState = chunk.getBlockState(blockPos);
                                    if (!blockState.isAir()) {
                                        mapColor = blockState.getMapColor(world, blockPos);

                                        // 如果存在液体，处理液体颜色
                                        if (blockState.getFluidState() != Fluids.EMPTY.getDefaultState()) {
                                            blockState = this.getFluidStateIfVisible(world, blockState, blockPos);
                                            mapColor = blockState.getMapColor(world, blockPos);
                                        }


                                        // 找到有效颜色后退出循环
                                        if (mapColor != MapColor.CLEAR) {
                                            break;
                                        }
                                    }

                                    // 向下移动
                                    height--;
                                    blockPos.setY(height);
                                }


                                // 计算亮度等级
                                MapColor.Brightness brightness = calculateBrightness(height, preHeight);


                                //存储本区块最后一列的高度
                                if (localX == 15) {
                                    heights[localZ] = height;
                                }
                                preHeight = height;

                                // 调整颜色亮度并存入数组
                                byte mapColorId = (byte) (mapColor != null ? mapColor.id : 0);
                                mapColorId = setBrightness(mapColorId, brightness);


                                // 将颜色存入数组
                                colors[localX + localZ * 16] = mapColorId;
                            }
                        }

                    }



                    // 将颜色数组存入 LargeMapState
                    largeMapState.setColor(chunkPos, colors);
                }
            }
        }
    }

    private MapColor.Brightness calculateBrightness(int currentHeight, int previousHeight) {
        double brightnessOffset = (currentHeight - previousHeight) ;

        if (brightnessOffset > 0.5) {
            return MapColor.Brightness.HIGH; // 更亮
        } else if (brightnessOffset < -0.5) {
            return MapColor.Brightness.LOW; // 更暗
        } else {
            return MapColor.Brightness.NORMAL; // 普通亮度
        }
    }

    private byte setBrightness(byte mapColorId,MapColor.Brightness brightness){
        byte brightnessId = (byte) brightness.id;
        return (byte) (mapColorId | (brightnessId << 6));
    }



    private BlockState getFluidStateIfVisible(World world, BlockState state, BlockPos pos) {
        FluidState fluidState = state.getFluidState();
        return !fluidState.isEmpty() && !state.isSideSolidFullSquare(world, pos, Direction.UP) ? fluidState.getBlockState() : state;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if (!world.isClient()){
            LargeMapState largeMapState = getMapState(stack,world);
            if (largeMapState != null && world.getTime() % 10 == 0){
                updateColors(world, entity, largeMapState);

            }
        }
    }
}
