package com.ffsupver.asplor.entity.custom.rocket;

import com.ffsupver.asplor.entity.ModEntities;
import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.item.item.SpaceStationItem;
import com.ffsupver.asplor.item.item.NavigationChipItem;
import com.ffsupver.asplor.screen.rocket.AdvanceRocketScreenHandler;
import earth.terrarium.adastra.api.planets.Planet;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AdvanceRocketEntity extends RoundTripRocketEntity{
    public AdvanceRocketEntity(World world, Vec3d pos, Vec3d velocity) {
        super(ModEntities.ADVANCE_ROCKET,world, pos, velocity);
    }

    public AdvanceRocketEntity(EntityType<?> type, World world) {
        super(type, world);
    }

    @Override
    protected int getInventorySize() {
        return 47;
    }

    @Override
    protected Optional<RegistryKey<World>> getTargetWorldKeyOptional(Planet planet) {
        ItemStack itemStack = inventory().getStack(2);
        if (!itemStack.isEmpty() && itemStack.getItem() instanceof NavigationChipItem){
            RegistryKey<World> planetKey = planet.dimension();
            return NavigationChipItem.getWorldKey(itemStack,planetKey);
        }
        return Optional.empty();
    }

    @Override
    protected @NotNull ItemStack asItemStack() {
        return ModItems.ADVANCE_ROCKET.getDefaultStack();
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AdvanceRocketScreenHandler(syncId,playerInventory,this);
    }

    @Override
    protected void landingTick() {
        super.landingTick();
        if (getWorld().getHeight() / 2 + getWorld().getBottomY() > getBlockY()){
            if (getWorld() instanceof ServerWorld serverWorld){
                boolean hasBlock = this.height == getBlockY() - getWorld().getBottomY() + 1;
                ItemStack spaceItemStack = inventory().getStack(46);
                if (hasBlock && spaceItemStack.getItem() instanceof SpaceStationItem){
                    SpaceStationItem.loadStructure(serverWorld,SpaceStationItem.STRUCTURE_ID,getBlockPos().down(15));
                    inventory().setStack(46,spaceItemStack.copyWithCount(spaceItemStack.getCount() - 1));
                }
            }
        }
    }
}
