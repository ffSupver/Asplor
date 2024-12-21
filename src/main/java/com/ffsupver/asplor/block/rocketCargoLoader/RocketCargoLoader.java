package com.ffsupver.asplor.block.rocketCargoLoader;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.foundation.block.IBE;
import earth.terrarium.adastra.common.blocks.LaunchPadBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.state.property.Property;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class RocketCargoLoader extends Block implements IBE<RocketCargoLoaderEntity> {
    public static final Property<Direction> FACING = Properties.HOPPER_FACING;
    public static final Property<Boolean> CONNECTED = BooleanProperty.of("connected");
    public RocketCargoLoader(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FACING,Direction.NORTH).with(CONNECTED,false));
    }

    @Override
    public Class<RocketCargoLoaderEntity> getBlockEntityClass() {
        return RocketCargoLoaderEntity.class;
    }

    @Override
    public BlockEntityType<? extends RocketCargoLoaderEntity> getBlockEntityType() {
        return AllBlockEntityTypes.ROCKET_CARGO_LOADER_ENTITY.get();
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        super.onPlaced(world, pos, state, placer, itemStack);
        for (Direction direction : DIRECTIONS){
            BlockPos checkPos = pos.offset(direction);
            BlockState checkState = world.getBlockState(checkPos);
            if (checkState.getBlock() instanceof LaunchPadBlock launchPadBlock){
                BlockPos rocketPos = launchPadBlock.getController(checkState,checkPos);
                withBlockEntityDo(world,pos,rocketCargoLoaderEntity -> rocketCargoLoaderEntity.setRocketPos(rocketPos,direction));
            }
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(FACING,CONNECTED));
    }
}
