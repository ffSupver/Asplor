package com.ffsupver.asplor.block.chunkLoader;

import appeng.server.services.ChunkLoadingService;
import com.ffsupver.asplor.item.ModItems;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.List;

public class ChunkLoaderEntity extends SmartBlockEntity {

    private int ticksRemain;
    public static final int MAX_TICK = 72000;
    public ChunkLoaderEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.ticksRemain = 0;
    }

    @Override
    public void tick() {
        super.tick();
            if (ticksRemain > 0) {
                ticksRemain--;
                if (world instanceof ServerWorld serverWorld) {
                    if (!isChunkLoad(serverWorld)) {
                        addChunk(serverWorld);
                    }
                }
            } else {
                if (world instanceof ServerWorld serverWorld) {
                    removeChunk(serverWorld);
                }
            }

    }

    public int getTicksRemain() {
        return ticksRemain;
    }

    public void setTicksRemain(int ticksRemain){
        this.ticksRemain = ticksRemain;
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);

        this.ticksRemain = tag.getInt("tick_remain");
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);

        tag.putInt("tick_remain",ticksRemain);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    private void addTick(int tick){
        this.ticksRemain = Math.min(MAX_TICK,this.ticksRemain+tick);
        sendData();
    }

    public ActionResult onUse(PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.isSneaking()){
            ItemStack handItemStack = player.getStackInHand(hand);
            if (handItemStack.isOf(ModItems.INFUSION_CLOCK)){
                int damageRemain = handItemStack.getMaxDamage() - handItemStack.getDamage();
                addTick(4800*damageRemain / handItemStack.getMaxDamage());
                if (!player.isCreative()){
                    handItemStack.setCount(handItemStack.getCount() - 1);
                    player.setStackInHand(hand, handItemStack);
                }
                return ActionResult.SUCCESS;
            }
        }
        return ActionResult.PASS;
    }

    @Override
    public void destroy() {
        if (world instanceof ServerWorld serverWorld){
            removeChunk(serverWorld);
        }
        super.destroy();
    }

    private ChunkPos getChunkPos(){return getWorld().getChunk(getPos()).getPos();}

    private boolean removeChunk(ServerWorld serverWorld){
        return ChunkLoadingService.getInstance().releaseChunk(serverWorld,getPos(),getChunkPos());
    }

    private boolean addChunk(ServerWorld serverWorld){
        return ChunkLoadingService.getInstance().forceChunk(serverWorld,getPos(),getChunkPos());
    }

    private boolean isChunkLoad(ServerWorld serverWorld){
        return ChunkLoadingService.getInstance().isChunkForced(serverWorld,getChunkPos().x,getChunkPos().z);
    }
}
