package com.ffsupver.asplor.entity.custom;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.ModTags;
import com.ffsupver.asplor.entity.ModEntities;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.PiglinBrain;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.vehicle.VehicleInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public class AlloyChestEntity extends Entity implements VehicleInventory {

    private DefaultedList<ItemStack> inventory;
    @Nullable
    private Identifier lootTableId;
    private long lootSeed;

    public AlloyChestEntity(World world, Vec3d position, Vec3d velocity){
        this(ModEntities.ALLOY_CHEST,world);
        this.setPosition(position.x, position.y, position.z);
        this.setVelocity(velocity.x, velocity.y, velocity.z);
    }



    public AlloyChestEntity(EntityType<AlloyChestEntity> entityEntityType, World world) {
        super(entityEntityType, world);
        this.initBaseProperties();
        this.inventory = DefaultedList.ofSize(54, ItemStack.EMPTY);
    }


    private void initBaseProperties(){
        Vec3d dimensionCenter  =this.getDimensions(this.getPose()).getBoxAt(this.getPos()).getCenter();
        this.setBoundingBox(this.getDimensions(this.getPose()).getBoxAt(dimensionCenter));
        this.intersectionChecked = true;
        this.noClip=false;
    }

    @Override
    public void tick() {
        // 调用父类的 tick 方法
        super.tick();

        // 添加重力效果
        if (!this.hasNoGravity()) {
            this.setVelocity(this.getVelocity().add(0, -0.04, 0)); // 简单重力加速度
        }

        // 移动实体
        this.move(MovementType.SELF, this.getVelocity());

        // 减少速度（模拟摩擦）
        this.setVelocity(this.getVelocity().multiply(0.98));
        
        
        //实体化
        boolean needToBeBlock = this.getWorld().getBlockState(this.getBlockPos().down()).isIn(ModTags.Blocks.LET_STORAGE_ENTITY_BE_BLOCK);
        boolean noOccupied = this.getWorld().getBlockState(this.getBlockPos()).isAir();
        if (needToBeBlock && noOccupied){
            this.getWorld().setBlockState(this.getBlockPos(), AllBlocks.ALLOY_CHEST.getDefaultState());
            BlockEntity blockEntity = this.getWorld().getBlockEntity(this.getBlockPos());
            if (blockEntity instanceof com.ffsupver.asplor.block.alloyChest.AlloyChestEntity){
                for (int i=0;i<this.getInventory().size();i++){
                    ((com.ffsupver.asplor.block.alloyChest.AlloyChestEntity) blockEntity).setStack(i,this.getStack(i));
                }
                this.discard();
            }
        }
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        if (this.isInvulnerableTo(source)) {
            return false;
        } else if (!this.getWorld().isClient && !this.isRemoved()) {
            if (amount>4) {
                this.onBroken(source,getWorld(),this);
                this.dropStack(AllBlocks.ALLOY_CHEST.asItem().getDefaultStack());
                this.discard();
            }
            return true;
        } else {
            return true;
        }
    }



    @Override
    protected void initDataTracker() {
    }

    @Override
    protected void readCustomDataFromNbt(NbtCompound nbt) {
        this.resetInventory();
        if (nbt.contains("LootTable", 8)) {
            this.setLootTableId(new Identifier(nbt.getString("LootTable")));
            this.setLootTableSeed(nbt.getLong("LootTableSeed"));
        } else {
            Inventories.readNbt(nbt, this.getInventory());
        }
    }

    @Override
    protected void writeCustomDataToNbt(NbtCompound nbt) {
        if (this.getLootTableId() != null) {
            nbt.putString("LootTable", this.getLootTableId().toString());
            if (this.getLootTableSeed() != 0L) {
                nbt.putLong("LootTableSeed", this.getLootTableSeed());
            }
        } else {
            Inventories.writeNbt(nbt, this.getInventory());
        }
    }

    @Override
    public ActionResult interact(PlayerEntity player, Hand hand) {
        ActionResult actionResult = this.open(player);
        if (actionResult.isAccepted()) {
            this.emitGameEvent(GameEvent.CONTAINER_OPEN, player);
            PiglinBrain.onGuardedBlockInteracted(player, true);
        }

        return actionResult;
    }



    @Override
    public boolean isCollidable() {
        return true;
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        if (entity instanceof AlloyChestEntity) {
            if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
                super.pushAwayFrom(entity);
            }
        } else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
            super.pushAwayFrom(entity);
        }
    }

    @Override
    public boolean canHit() {return !this.isRemoved();}

    @Nullable
    @Override
    public Identifier getLootTableId() {
        return lootTableId;
    }

    @Override
    public void setLootTableId(@Nullable Identifier lootTableId) {
        this.lootTableId=lootTableId;
    }

    @Override
    public long getLootTableSeed() {
        return lootSeed;
    }

    @Override
    public void setLootTableSeed(long lootTableSeed) {
        this.lootSeed=lootTableSeed;
    }

    @Override
    public DefaultedList<ItemStack> getInventory() {
        return this.inventory;
    }

    @Override
    public void resetInventory() {
        this.inventory = DefaultedList.ofSize(this.size(), ItemStack.EMPTY);
    }


    @Override
    public int size() {
        return inventory.size();
    }

    @Override
    public ItemStack getStack(int slot) {
        return this.getInventoryStack(slot);
    }
    @Override

    public ItemStack removeStack(int slot, int amount) {
        return this.removeInventoryStack(slot, amount);
    }
    @Override
    public ItemStack removeStack(int slot) {
        return this.removeInventoryStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        this.setInventoryStack(slot, stack);
    }

    @Override
    public void markDirty() {}

    @Override
    public boolean canPlayerUse(PlayerEntity player) {
        return this.canPlayerAccess(player);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        if (this.lootTableId != null && player.isSpectator()) {
            return null;
        } else {
            this.generateInventoryLoot(playerInventory.player);
            return this.getScreenHandler(syncId, playerInventory);
        }
    }
    public ScreenHandler getScreenHandler(int syncId, PlayerInventory playerInventory) {
        return GenericContainerScreenHandler.createGeneric9x6(syncId, playerInventory, this);
    }

    @Override
    public void clear() {
        this.clearInventory();
    }
}
