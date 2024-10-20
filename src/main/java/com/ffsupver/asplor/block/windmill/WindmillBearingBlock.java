package com.ffsupver.asplor.block.windmill;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.simibubi.create.content.contraptions.bearing.BearingBlock;
import com.simibubi.create.foundation.block.IBE;
import com.simibubi.create.foundation.utility.Couple;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class WindmillBearingBlock extends BearingBlock implements IBE<WindmillBearingBlockEntity> {

        public WindmillBearingBlock(Settings properties) {
            super(properties);
        }

        @Override
        public ActionResult onUse(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn,
                                  BlockHitResult hit) {
            if (!player.canModifyBlocks())
                return ActionResult.FAIL;
            if (player.isSneaking())
                return ActionResult.FAIL;
            if (player.getStackInHand(handIn)
                    .isEmpty()) {
                if (worldIn.isClient)
                    return ActionResult.SUCCESS;
                withBlockEntityDo(worldIn, pos, be -> {
                    if (be.running) {
                        be.disassemble();
                        return;
                    }
                    be.assembleNextTick = true;
                });
                return ActionResult.SUCCESS;
            }
            return ActionResult.PASS;
        }

        @Override
        public Class<WindmillBearingBlockEntity> getBlockEntityClass() {
            return WindmillBearingBlockEntity.class;
        }

        @Override
        public BlockEntityType<? extends WindmillBearingBlockEntity> getBlockEntityType() {
            return AllBlockEntityTypes.WINDMILL_BEARING.get();
        }

        public static Couple<Integer> getSpeedRange() {
            return Couple.create(1, 16);
        }


}
