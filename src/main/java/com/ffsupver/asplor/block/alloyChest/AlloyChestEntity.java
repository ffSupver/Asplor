package com.ffsupver.asplor.block.alloyChest;

import com.ffsupver.asplor.AllBlockEntityTypes;
import com.ffsupver.asplor.entity.ModEntities;
import com.ffsupver.asplor.screen.alloyChest.AlloyChestScreenHandler;
import com.ffsupver.asplor.sound.ModSounds;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.ViewerCountManager;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class AlloyChestEntity extends BlockEntity implements ExtendedScreenHandlerFactory,ImplementedInventory {

    public static final int SIZE = 54;
    private Text displayName = Text.translatable("block.asplor.alloy_chest");
    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(SIZE,ItemStack.EMPTY);
    protected final PropertyDelegate propertyDelegate;
    private final ViewerCountManager stateManager;

    public AlloyChestEntity( BlockPos pos, BlockState state) {
        super(AllBlockEntityTypes.ALLOY_CHEST_BLOCK_ENTITY,pos,state);

        this.propertyDelegate = new PropertyDelegate() {
            @Override
            public int get(int index) {
                return 0;
            }

            @Override
            public void set(int index, int value) {

            }

            @Override
            public int size() {
                return 0;
            }
        };
        this.stateManager = new ViewerCountManager() {
            @Override
            protected void onContainerOpen(World world, BlockPos pos, BlockState state) {
              AlloyChestEntity.this.playSound(state, ModSounds.ALLOY_CHEST_OPEN);
                AlloyChestEntity.this.setOpen(state,true);
            }

            @Override
            protected void onContainerClose(World world, BlockPos pos, BlockState state) {
                AlloyChestEntity.this.playSound(state, ModSounds.ALLOY_CHEST_CLOSE);
                AlloyChestEntity.this.setOpen(state,false);
            }

            @Override
            protected void onViewerCountUpdate(World world, BlockPos pos, BlockState state, int oldViewerCount, int newViewerCount) {

            }

            @Override
            protected boolean isPlayerViewing(PlayerEntity player) {
                if (player.currentScreenHandler instanceof AlloyChestScreenHandler){
                    return true;
                }
                return false;
            }
        };
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
    buf.writeBlockPos(this.pos);
    }

    @Override
    public Text getDisplayName() {
        return displayName;
    }
    public void setCustomName(Text customName){
        displayName = customName;
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory playerInventory, PlayerEntity player) {
        return new AlloyChestScreenHandler(syncId,playerInventory,this,this.propertyDelegate);
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        Inventories.writeNbt(nbt,inventory);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        Inventories.readNbt(nbt,inventory);
    }

    public ActionResult turnIntoEntity(){
        if (!world.isClient()) {
            com.ffsupver.asplor.entity.custom.AlloyChestEntity alloyChestEntity = ModEntities.ALLOY_CHEST.spawn((ServerWorld) world, pos, SpawnReason.TRIGGERED);
            for (int i =0;i<this.inventory.size();i++){
                alloyChestEntity.setStack(i,this.inventory.get(i));
            }
        }
        this.inventory.clear();
        world.setBlockState(pos, Blocks.AIR.getDefaultState(),3);
        return ActionResult.SUCCESS;
    }

    @Override
    public void onClose(PlayerEntity player) {
        if(!this.removed&&!player.isSpectator()){
            this.stateManager.closeContainer(player,this.getWorld(),this.getPos(),this.getCachedState());
        }
    }

    @Override
    public void onOpen(PlayerEntity player) {
        if(!this.removed&&!player.isSpectator()){
            this.stateManager.openContainer(player,this.getWorld(),this.getPos(),this.getCachedState());
        }
    }

    public void tick(){
        if (!this.removed){
            this.stateManager.updateViewerCount(this.getWorld(),this.getPos(),this.getCachedState());
        }

    }

    void setOpen(BlockState state,boolean open){
        this.world.setBlockState(this.getPos(),(BlockState) state.with(AlloyChest.OPEN,open),3);
    }
    void playSound(BlockState state, SoundEvent soundEvent) {
        Vec3i vec3i = ((Direction)state.get(AlloyChest.FACING)).getVector();
        double d = (double)this.pos.getX() + 0.5 + (double)vec3i.getX() / 2.0;
        double e = (double)this.pos.getY() + 0.5 + (double)vec3i.getY() / 2.0;
        double f = (double)this.pos.getZ() + 0.5 + (double)vec3i.getZ() / 2.0;
        this.world.playSound((PlayerEntity)null, d, e, f, soundEvent, SoundCategory.BLOCKS, 0.5F, this.world.random.nextFloat() * 0.1F + 0.9F);
    }
}
