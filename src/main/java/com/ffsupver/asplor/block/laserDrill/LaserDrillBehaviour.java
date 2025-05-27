package com.ffsupver.asplor.block.laserDrill;

import com.ffsupver.asplor.ModTags;
import com.ffsupver.asplor.util.NbtUtil;
import com.simibubi.create.content.kinetics.base.BlockBreakingKineticBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BehaviourType;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.BlockHelper;
import com.simibubi.create.foundation.utility.VecHelper;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.List;

public class LaserDrillBehaviour extends BlockEntityBehaviour {
    private int tickToNextBreakByHardness = 10;
    private int digProcess = 0;
    private int bedrockMineProcess;
    private static final int BEDROCK_MINE_PROGRESS = 100;
    public static final int ENERGY_PER_BEDROCK_MINE = 20000;
    public static final int ENERGY_PER_TICK = 10000;

    private int tickToNextBreak;
    private final int breakerId = -BlockBreakingKineticBlockEntity.NEXT_BREAKER_ID.incrementAndGet();
    private BlockPos lastDigPos;
    private final LaserDrillLenEntity be;
    public static final BehaviourType<LaserDrillBehaviour> TYPE = new BehaviourType<>();
    public LaserDrillBehaviour(LaserDrillLenEntity be) {
        super(be);
        this.be = be;
    }

    @Override
    public void tick() {
        super.tick();
        BlockPos digPos = getLowestPos();
        BlockState digState = getWorld().getBlockState(digPos);

        float hardness = digState.getHardness(getWorld(),digPos);
        tickToNextBreakByHardness = (int) (5 * hardness);


        if (lastDigPos != null){
            if (!lastDigPos.equals(digPos)){
                resetDigProcess();
                blockEntity.notifyUpdate();
                tickToNextBreak = tickToNextBreakByHardness;
                lastDigPos = digPos;
            }
        }else {
            lastDigPos = digPos;
        }
        dig(digPos,digState);

    }

    private void dig(BlockPos digPos,BlockState digState){
        if (digState.isOf(Blocks.BEDROCK)){
            if (bedrockMineProcess >= BEDROCK_MINE_PROGRESS) {
                resetBedrockMineProcess();
                onBedrockMine(digPos);
            }else if (be.extractEnergy(ENERGY_PER_BEDROCK_MINE)){
                bedrockMineProcess++;
            }
        }else{
            resetBedrockMineProcess();

            if (tickToNextBreakByHardness > 0){
                getWorld().setBlockBreakingInfo(breakerId,digPos,digProcess);

                if (tickToNextBreak <= 0){
                    if (digProcess >= 9) {
                        resetDigProcess();
                        onBlockBreak(digPos);
                    } else {
                        digProcess++;
                    }

                    be.notifyUpdate();
                    tickToNextBreak = tickToNextBreakByHardness;
                }else {
                    if (be.extractEnergy(ENERGY_PER_TICK)) {
                        tickToNextBreak--;
                    }
                }
            }
        }
    }

    private void resetBedrockMineProcess(){
        bedrockMineProcess = 0;
    }

    private void resetDigProcess(){
        digProcess = -1;
    }

    private BlockPos getLowestPos(){
        BlockPos checkPos = getPos().down();
        while (getWorld().getBlockState(checkPos).isOf(Blocks.AIR) || getWorld().getBlockState(checkPos).isIn(ModTags.Blocks.CAN_LASER_PASS)){
            checkPos = checkPos.down();
        }
        return checkPos;
    }

    private void onBedrockMine(BlockPos bedrockPos){
        List<ItemStack> r = List.of(new ItemStack(Items.DIAMOND),new ItemStack(Items.IRON_INGOT,2));
        for (ItemStack itemStack : r){
            getNewStack(itemStack, bedrockPos.up());
        }
    }


    private void onBlockBreak(BlockPos breakPos){
        BlockHelper.destroyBlock(getWorld(), breakPos, 1f, (stack) -> getNewStack(stack,breakPos));
    }

    private void getNewStack(ItemStack stack,BlockPos breakPos){
        World world = getWorld();
        Vec3d vec = VecHelper.offsetRandomly(VecHelper.getCenterOf(breakPos), world.random, .125f);
        if (stack.isEmpty())
            return;
        if (!world.getGameRules()
                .getBoolean(GameRules.DO_TILE_DROPS))
            return;


        int left = be.insertItemToOutput(stack);
        if (left <= 0){
            return;
        }else {
            stack = stack.copyWithCount(left);
        }


        ItemEntity itementity = new ItemEntity(world, vec.x, vec.y, vec.z, stack);
        itementity.setToDefaultPickupDelay();
        itementity.setVelocity(Vec3d.ZERO);
        world.spawnEntity(itementity);
    }

    @Override
    public void write(NbtCompound nbt, boolean clientPacket) {
        super.write(nbt, clientPacket);
        nbt.putInt("dig_process",digProcess);
        nbt.putInt("tick_to_next_break",tickToNextBreak);
        nbt.putInt("ttnbbh",tickToNextBreakByHardness);
        nbt.putInt("b_mine_process", bedrockMineProcess);
        if (lastDigPos != null) {
            nbt.put("last_pos", NbtUtil.writeBlockPosToNbt(lastDigPos));
        }
    }

    @Override
    public void read(NbtCompound nbt, boolean clientPacket) {
        super.read(nbt, clientPacket);
        digProcess = nbt.getInt("dig_process");
        tickToNextBreak = nbt.getInt("tick_to_next_break");
        tickToNextBreakByHardness = nbt.getInt("ttnbbh");
        bedrockMineProcess = nbt.getInt("b_mine_process");
        if (nbt.contains("last_pos", NbtElement.COMPOUND_TYPE)){
           lastDigPos = NbtUtil.readBlockPosFromNbt(nbt.getCompound("last_pos"));
        }
    }

    public BlockPos getLastDigPos() {
        return lastDigPos;
    }

    @Override
    public BehaviourType<?> getType() {
        return TYPE;
    }

    public void resetLastDigPos() {
        this.lastDigPos = getPos();
    }
}
