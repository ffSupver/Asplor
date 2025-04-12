package com.ffsupver.asplor.block.airlockSwitch;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.ffsupver.asplor.util.NbtUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import static com.ffsupver.asplor.block.airlockSwitch.AirlockSwitch.ON;

public class AirlockSwitchEntity extends BlockEntity {
    private boolean switched;
    private BlockPos pairPos;
    private Vec3i offset;
    public AirlockSwitchEntity(BlockPos pos, BlockState state) {
        super(AllBlockEntityTypes.AIRLOCK_SWITCH_ENTITY, pos, state);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        if (offset != null) {
            nbt.put("offset",NbtUtil.writeVec3iToNbt(offset));
        }
        super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if (nbt.contains("offset",10)){
            offset = NbtUtil.readBlockPosFromNbt(nbt.getCompound("offset"));
            pairPos = pos.add(offset);
        }
        super.readNbt(nbt);
    }



    public void setPairPos(BlockPos pairPos) {
        this.pairPos = pairPos;
        this.offset = new Vec3i(pairPos.getX() - pos.getX(),pairPos.getY() - pos.getY(),pairPos.getZ() - pos.getZ());
    }


    public void use(){
        boolean origin = getCachedState().get(ON);
        if (origin){
            switchTo(false);
            switched = true;
        }else {
            if (pairPos != null && world.getBlockEntity(pairPos) instanceof AirlockSwitchEntity airlockSwitchEntity){
                airlockSwitchEntity.switchTo(false);
            }
        }
    }

    public void schedule(){
        boolean originNew = getCachedState().get(ON);
        if (!originNew) {
            if (!switched) {
                switchTo(true);
            } else {
                if (pairPos != null && world.getBlockEntity(pairPos) instanceof AirlockSwitchEntity airlockSwitchEntity) {
                    airlockSwitchEntity.switchTo(true);
                }
            }
        }
        switched = false;
    }

    private void switchTo(boolean on){
        world.setBlockState(pos,getCachedState().with(ON, on), Block.NOTIFY_ALL);
    }
}
