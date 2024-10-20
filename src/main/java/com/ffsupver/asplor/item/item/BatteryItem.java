package com.ffsupver.asplor.item.item;

import appeng.api.config.Actionable;
import appeng.core.localization.Tooltips;
import appeng.items.tools.powered.powersink.AEBasePoweredItem;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.item.ModItems;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import team.reborn.energy.api.EnergyStorage;

import java.util.List;

import static com.ffsupver.asplor.util.MathUtil.fromAEtoE;
import static com.ffsupver.asplor.util.MathUtil.fromEtoAE;

public class BatteryItem extends AEBasePoweredItem {
    private static final Double CAPACITY = 2500.0;

    public BatteryItem(Settings settings) {
        super(()->CAPACITY,settings);
    }

    public static ItemStack getInctanceItemStack(){
        ItemStack INSTANCE = new ItemStack(ModItems.ZINC_COPPER_BATTERY,1);
        NbtCompound tag = new NbtCompound();
        tag.putDouble("internalCurrentPower",CAPACITY);
        INSTANCE.writeNbt(tag);
        return INSTANCE;
    }



    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (powerBlock(context)){
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }

    private boolean powerBlock(ItemUsageContext context){
        BlockEntity blockEntity = context.getWorld().getBlockEntity(context.getBlockPos());
        if (blockEntity != null){
            EnergyStorage blockEnergyStorage = EnergyStorage.SIDED.find(context.getWorld(),context.getBlockPos(),context.getSide());
            if (blockEnergyStorage != null){
                long energyNeedToTransfer = (long) Math.min(blockEnergyStorage.getCapacity()-blockEnergyStorage.getAmount(),
                        fromAEtoE(getAECurrentPower(context.getStack())));
                if (energyNeedToTransfer>0) {
                    try (Transaction t = Transaction.openOuter()) {
                        long inserted = blockEnergyStorage.insert(energyNeedToTransfer,t);
                        extractAEPower(context.getStack(),fromEtoAE(inserted), Actionable.MODULATE);
                        t.commit();
                    }
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public double getChargeRate(ItemStack itemStack) {
        return 250;
    }

}