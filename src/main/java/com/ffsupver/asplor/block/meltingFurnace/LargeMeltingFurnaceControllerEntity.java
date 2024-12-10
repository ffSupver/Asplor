package com.ffsupver.asplor.block.meltingFurnace;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.ModDamages;
import com.ffsupver.asplor.ModTags;
import com.ffsupver.asplor.recipe.FluidInventory;
import com.ffsupver.asplor.recipe.MeltRecipe;
import com.ffsupver.asplor.recipe.ModRecipes;
import com.ffsupver.asplor.util.RenderUtil;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import static com.ffsupver.asplor.util.NbtUtil.*;
import static com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HEAT_LEVEL;
import static net.minecraft.util.Formatting.AQUA;

public class LargeMeltingFurnaceControllerEntity extends SmartBlockEntity implements IHaveGoggleInformation {
    private boolean isComplete;
    protected LargeMeltingFurnaceData data;
    private int checkCoolDown;
    private int processCoolDown = 0;
    private int maxProcessCoolDown;
    private final int MAX_CHECK_COOL_DOWN = 50;
    private final int MAX_SIZE = 32;

    private final MultiFluidTank multiFluidTank;
    private ItemHandler handler;



    public LargeMeltingFurnaceControllerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        data = null;
        checkCoolDown = MAX_CHECK_COOL_DOWN;
        multiFluidTank = new MultiFluidTank(0);
        handler = new ItemHandler(0);

    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }


    @Override
    public void tick() {
        super.tick();
        boolean originalComplete = isComplete;
        if (checkCoolDown < 0){
            checkCoolDown = MAX_CHECK_COOL_DOWN;
            checkComplete();
            updateFluidTank();
            updateInventory();
        }else {
            checkCoolDown -= 1;
        }


        if (!isComplete){
            breakFurnace();
            return;
        }

        if (!originalComplete){
            setupInput();
        }

        if (processCoolDown <= 0){
            int heatCount = getHeatCount();
            int areaCount = calculateAreaCount();
            BlazeBurnerBlock.HeatLevel heatLevel = areaCount < heatCount ?
                    BlazeBurnerBlock.HeatLevel.SEETHING : heatCount > 0 ?
                        BlazeBurnerBlock.HeatLevel.KINDLED : BlazeBurnerBlock.HeatLevel.NONE;

            if (!world.isClient() &&  heatLevel.isAtLeast(BlazeBurnerBlock.HeatLevel.KINDLED)){
                damageEntity();
            }


            maxProcessCoolDown = (int) Math.pow(calculateBlockCount(),1.0/3);
            for(Slot slot :handler.slots){
                slot.tick(maxProcessCoolDown*heatCount/areaCount, heatLevel);
            }
            processCoolDown = maxProcessCoolDown;
        }else {
            processCoolDown -= 1;
        }

    }

    private void checkComplete(){
        BlockPos checkPos = pos;


        while (isAvailBlock(world.getBlockState(checkPos.down())) &&
        pos.getY() - checkPos.getY() <= MAX_SIZE){
            checkPos = checkPos.down();
        }
        while (isAvailBlock(world.getBlockState(checkPos.west())) &&
                pos.getX() - checkPos.getX() <= MAX_SIZE){
            checkPos = checkPos.west();
        }
        while (isAvailBlock(world.getBlockState(checkPos.north())) &&
                pos.getZ() - checkPos.getZ() <= MAX_SIZE){
            checkPos = checkPos.north();
        }

        BlockPos low = checkPos;

        while (isAvailBlock(world.getBlockState(checkPos.east())) &&
                checkPos.getX() - low.getX() <= MAX_SIZE){
            checkPos = checkPos.east();
        }
        while (isAvailBlock(world.getBlockState(checkPos.south())) &&
                checkPos.getZ() - low.getZ() <= MAX_SIZE){
            checkPos = checkPos.south();
        }
        while (isAvailBlock(world.getBlockState(checkPos.up())) &&
                checkPos.getY() - low.getY() <= MAX_SIZE){
            checkPos = checkPos.up();
        }

        BlockPos height = checkPos;

        if (height.getY()==pos.getY() || low.getY() == pos.getY() ||
                height.getZ()==pos.getZ() && height.getX() == pos.getX() && low.getZ()==pos.getZ() && low.getX() == pos.getX()||
                height.getX()-low.getX() <= 1 || height.getZ()-low.getZ() <= 1
        ){
            isComplete = false;
            return;
        }

        ArrayList<BlockPos> fluidPort = new ArrayList<>();
        ArrayList<BlockPos> itemPort = new ArrayList<>();




        for (int x = low.getX();x<=height.getX();x++){
            for (int y = low.getY();y<=height.getY();y++){
                for (int z = low.getZ();z<height.getZ();z++){
                    BlockPos posToCheck = new BlockPos(x,y,z);
                    BlockState stateToCheck = world.getBlockState(posToCheck);
                    boolean side = x==low.getX() || x==height.getX() || z==low.getZ() || z==height.getZ() || y == low.getY();
                    if (side){
                        if (!isAvailBlock(stateToCheck) ||
                                (posToCheck.getX() != pos.getX() || posToCheck.getY() != pos.getY() || posToCheck.getZ() != pos.getZ())
                        && stateToCheck.isOf(AllBlocks.LARGE_MELTING_FURNACE_CONTROLLER.get())){
                            isComplete = false;
                            return;
                        }
                        if (stateToCheck.isOf(AllBlocks.LARGE_MELTING_FURNACE_FLUID_PORT.get())){
                            BlockEntity blockEntity = world.getBlockEntity(posToCheck);
                            if (blockEntity instanceof LargeMeltingFurnaceFluidPortEntity){
                                fluidPort.add(posToCheck);
                            }
                        }
                        if (stateToCheck.isOf(AllBlocks.LARGE_MELTING_FURNACE_ITEM_PORT.get())){
                            BlockEntity blockEntity = world.getBlockEntity(posToCheck);
                            if (blockEntity instanceof LargeMeltingFurnaceItemPortEntity){
                                itemPort.add(posToCheck);
                            }
                        }
                    }else {
                        if (!stateToCheck.isAir() && !stateToCheck.isOf(com.simibubi.create.AllBlocks.ANDESITE_LADDER.get())){
                            isComplete = false;
                            return;
                        }
                    }
                }
            }
        }

        isComplete = true;
        data = new LargeMeltingFurnaceData(low,height,fluidPort,itemPort);
    }

    private int getHeatCount(){
        int heatCount = 0;
        for (int x = data.low.getX();x <= data.height.getX();x++){
            for (int z = data.low.getZ();z <= data.height.getZ();z++){
                BlockPos checkPos = new BlockPos(x,data.low.getY()-1,z);
                BlockState blockState = world.getBlockState(checkPos);
                if (blockState.contains(HEAT_LEVEL)){
                    heatCount += switch (blockState.get(HEAT_LEVEL)){
                        case SEETHING -> 2;
                        case KINDLED -> 1;
                        default -> 0;
                    };
                }
            }
        }
        return heatCount;
    }

    private void setupInput(){
        for (BlockPos fluidPortPos : data.fluidPort){
            BlockEntity blockEntity = world.getBlockEntity(fluidPortPos);
            if (blockEntity instanceof LargeMeltingFurnaceFluidPortEntity largeMeltingFurnaceInputEntity){
                largeMeltingFurnaceInputEntity.setControllerPos(pos);
            }
        }
        for (BlockPos itemPortPos : data.itemPort){
            BlockEntity blockEntity = world.getBlockEntity(itemPortPos);
            if (blockEntity instanceof LargeMeltingFurnaceItemPortEntity largeMeltingFurnaceItemPortEntity){
                largeMeltingFurnaceItemPortEntity.setControllerPos(pos);
            }
        }
    }

    private void breakFurnace(){
        if (data!=null){
            for (BlockPos inputPos : data.fluidPort) {
                BlockEntity blockEntity = world.getBlockEntity(inputPos);
                if (blockEntity instanceof LargeMeltingFurnaceFluidPortEntity largeMeltingFurnaceInputEntity) {
                    largeMeltingFurnaceInputEntity.removeController();
                }
            }
            for (BlockPos itemPortPos : data.itemPort){
                BlockEntity blockEntity = world.getBlockEntity(itemPortPos);
                if (blockEntity instanceof LargeMeltingFurnaceItemPortEntity largeMeltingFurnaceItemPortEntity){
                    largeMeltingFurnaceItemPortEntity.removeController();
                }
            }
        }
        data = null;
    }

    private int calculateBlockCount(){
        if (data==null){
            return 0;
        }
        return (data.height.getX()-data.low.getX())*(data.height.getY()-data.low.getY())*(data.height.getZ()-data.low.getZ());
    }

    private int calculateInnerAreaCount(){
        if (data == null){
            return 0;
        }
        return (data.height.getX()-data.low.getX()-1)*(data.height.getZ()-data.low.getZ()-1);
    }
    private int calculateAreaCount(){
        if (data == null){
            return 0;
        }
        return (data.height.getX()-data.low.getX()+1)*(data.height.getZ()-data.low.getZ()+1);
    }

    private void updateFluidTank(){
        multiFluidTank.setMaxCapacity(calculateBlockCount()* 4L*81000);
    }
    private void updateInventory(){
        handler.setSize(calculateInnerAreaCount()*4);
    }

    private void damageEntity(){
        List<Entity> entities = world.getOtherEntities(null,getFluidBox(), entity -> entity instanceof LivingEntity);
        for (Entity entity : entities){
            entity.damage(ModDamages.molten((ServerWorld) world),6.0f);
        }
    }

    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        //添加状态
        Lang.translate("gui.goggles.large_melting_furnace_stats")
                .forGoggles(tooltip);
        String refineryDescription = isComplete ? "tooltip.large_melting_furnace.ready" : "tooltip.large_melting_furnace.incomplete";
        Lang.translate(refineryDescription)
                .style(AQUA)
                .forGoggles(tooltip);
        return true;
    }



    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        if (tag.contains("data",10)){
           data = LargeMeltingFurnaceData.readFromNbt(tag.getCompound("data"));
        }
        multiFluidTank.readFromNbt(tag.getCompound("tank"));
        handler.readFromNbt(tag.getCompound("inventory"));
        super.read(tag, clientPacket);
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        if (data != null){
            tag.put("data",data.writeToNbt());
        }
        tag.put("tank",multiFluidTank.writeToNbt());
        tag.put("inventory",handler.writeToNbt());
        super.write(tag, clientPacket);
    }

    private boolean isComplete(){
        return data == null;
    }

    private boolean isAvailBlock(BlockState state){
        return state.isIn(ModTags.Blocks.LARGE_MELTING_FURNACE_BLOCK);
    }

    public long insert(FluidVariant resource, long amount, TransactionContext transaction){
           notifyUpdate();
           return multiFluidTank.insert(resource,amount,transaction);
    }
    public long extract(FluidVariant resource,long amount, TransactionContext transactionContext){
            notifyUpdate();
            return multiFluidTank.extract(resource,amount,transactionContext);
    }

    private float getLiquidHeight(){
        return (float) multiFluidTank.getAmount() /multiFluidTank.getMaxCapacity();
    }

    private BlockPos getHeight(){
        return data.getHeight();
    }

    private BlockPos getLow(){
        return data.getLow();
    }

    public Box getFluidBox(){
        float height = (getHeight().getY() - getLow().getY() - 1/8f) * getLiquidHeight();
        Vec3d minPos = getLow().toCenterPos().add(.5,.5,.5);
        Vec3d heightConner = getHeight().toCenterPos().add(-.5,0,-.5);
        Vec3d maxPos = new Vec3d(heightConner.x,minPos.y + height,heightConner.z);
        return new Box(minPos,maxPos);
    }

    public FluidStack getRenderFluid(){
        if (multiFluidTank.isEmpty()){
            return null;
        }
        return new FluidStack(multiFluidTank.getFirstFluid(), multiFluidTank.getAmount());
    }

    public List<Pair<ItemStack,Float>> getRenderItem(){
        List<Pair<ItemStack,Float>> renderItems = new ArrayList<>();
        for (Slot slot : handler.slots){
            if (!slot.isMarkRemove() && !slot.isEmpty()){
                renderItems.add(new Pair<>(slot.stack,Math.max(Math.min(1,1 - (float)slot.processTime/slot.maxProcessTime),0)));
            }
        }
        return renderItems;
    }

    public Iterator<StorageView<FluidVariant>> getIterator(){
        return multiFluidTank.iterator();
    }
    public ItemHandler getItemHandler(){return handler;}


    @Override
    public void destroy() {
        breakFurnace();
        super.destroy();
    }



    @Override
    public Box getRenderBoundingBox() {
        return RenderUtil.createRenderBoundingBox(pos,64.0);
    }

    private static class LargeMeltingFurnaceData {
        private final BlockPos low;
        private final BlockPos height;
        private final ArrayList<BlockPos> fluidPort;
        private final ArrayList<BlockPos> itemPort;

        public LargeMeltingFurnaceData(BlockPos low, BlockPos height, ArrayList<BlockPos> fluidPort, ArrayList<BlockPos> itemPort) {
            this.low = low;
            this.height = height;
            this.fluidPort = fluidPort;
            this.itemPort = itemPort;
        }

        public NbtCompound writeToNbt(){
            NbtCompound nbt = new NbtCompound();
            nbt.put("low",writeBlockPosToNbt(low));
            nbt.put("height", writeBlockPosToNbt(height));

            nbt.put("fluid_port",writeBlockPosListToNbt(this.fluidPort));
            nbt.put("item_port",writeBlockPosListToNbt(this.itemPort));

            return nbt;
        }

        private static LargeMeltingFurnaceData readFromNbt(NbtCompound nbt){
            BlockPos low = readBlockPosFromNbt(nbt.getCompound("low"));
            BlockPos height = readBlockPosFromNbt(nbt.getCompound("height"));

            ArrayList<BlockPos> fluidPort1 = readBlockPosListFromNbt(nbt.getList("fluid_port",10));
            ArrayList<BlockPos> itemPort1 = readBlockPosListFromNbt(nbt.getList("item_port",10));

            return new LargeMeltingFurnaceData(low,height, fluidPort1, itemPort1);
        }

        public BlockPos getHeight() {
            return height;
        }

        public BlockPos getLow() {
            return low;
        }
    }

    private class ItemHandler implements Storage<ItemVariant>{
        private final ArrayList<Slot> slots = new ArrayList<>();
        private int size;

        public ItemHandler(int size){this.size = size;}

        public void setSize(int size) {
            this.size = size;
        }

        private void updateSlots(){
            slots.removeIf(Slot::isMarkRemove);
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            if (slots.size()<size){
                slots.add(new Slot(resource.toStack(1)));
                notifyUpdate();
                return 1;
            }
            for (Slot slot : slots){
                if (slot.isMarkRemove()){
                    slot.setStack(resource.toStack(1));
                    notifyUpdate();
                    return 1;
                }
            }
            return 0;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            Iterator<Slot> iterator = slots.iterator();
            long extracted = 0;
            while (iterator.hasNext() && extracted < maxAmount){
                Slot slot = iterator.next();
                if (!slot.isMarkRemove() && resource.matches(slot.getStack())){
                    slot.markRemove();
                    notifyUpdate();
                    transaction.addCloseCallback((t,r)->{
                        if (!r.wasCommitted()){
                            slot.setMarkRemove(false);
                        }
                    });
                    extracted += 1;
                }
            }
            return extracted;
        }

        @Override
        public Iterator<StorageView<ItemVariant>> iterator() {
            ArrayList<StorageView<ItemVariant>> views = new ArrayList<>();
            for (Slot slot : slots){
                views.add(new StorageView<ItemVariant>() {
                    @Override
                    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                        return ItemHandler.this.extract(resource,maxAmount,transaction);
                    }

                    @Override
                    public boolean isResourceBlank() {
                        return slot.isEmpty() || slot.isMarkRemove();
                    }

                    @Override
                    public ItemVariant getResource() {
                        return ItemVariant.of(slot.getStack());
                    }

                    @Override
                    public long getAmount() {
                        return slot.getStack().getCount();
                    }

                    @Override
                    public long getCapacity() {
                        return 1;
                    }
                });
            }
            return views.iterator();
        }

        public NbtCompound writeToNbt(){
            NbtCompound result = new NbtCompound();
            NbtList itemList = new NbtList();
            for (Slot slot : slots){
                itemList.add(slot.writeToNbt());
            }
            result.put("items",itemList);
            result.putInt("size",size);
            return result;
        }

        public void readFromNbt(NbtCompound nbt){
            slots.clear();
            NbtList itemList = nbt.getList("items",10);
            for (NbtElement e : itemList){
                NbtCompound slotNbt = (NbtCompound) e;
                if (!slotNbt.getBoolean("remove")){
                    Slot slot = new Slot(ItemStack.fromNbt(slotNbt));
                    slot.readFromNbt(slotNbt);
                    slots.add(slot);
                }
            }
            size = nbt.getInt("size");
        }
    }

    private class Slot{
        private ItemStack stack;
        private boolean markRemove;
        private int maxProcessTime = 100;
        private int processTime = maxProcessTime;
        private boolean hasRecipe = false;
        public Slot(ItemStack stack,boolean markRemove){
            this.stack = stack;
            this.markRemove=markRemove;
        }

        public void tick(int processSpeed,BlazeBurnerBlock.HeatLevel heatType){
            boolean hasRecipeOriginal = hasRecipe;
            if (getCurrentRecipe().isPresent()){
                hasRecipe = true;
                MeltRecipe recipe = getCurrentRecipe().get();
                maxProcessTime = recipe.getProcessTime();

                if (!hasRecipeOriginal){
                    processTime = maxProcessTime;
                }
                if (heatType.isAtLeast(getHeatType())){
                    if (processTime <= 0 && craft(recipe)){
                        processTime=maxProcessTime;
                    }else {
                        processTime -= processSpeed;
                    }
                }else {
                    hasRecipe = false;
                    processTime = maxProcessTime;
                }
            }else {
                hasRecipe = false;
                processTime = maxProcessTime;
            }

        }

        private BlazeBurnerBlock.HeatLevel getHeatType(){
            return switch (getCurrentRecipe().get().getHeatType()) {
                case "normal" -> BlazeBurnerBlock.HeatLevel.KINDLED;
                case "super" -> BlazeBurnerBlock.HeatLevel.SEETHING;
                default -> BlazeBurnerBlock.HeatLevel.NONE;
            };
        }



        private boolean craft(MeltRecipe recipe){
            if (multiFluidTank.canInsert(recipe.getOutputAmount())){
                try (Transaction t = Transaction.openOuter()) {
                    insert(FluidVariant.of(recipe.getOutputFluid()), recipe.getOutputAmount(), t);
                    this.remove();
                    return true;
                }
            }
            return false;
        }

        private Optional<MeltRecipe> getCurrentRecipe(){
            FluidInventory test = new FluidInventory(1,0);
            test.setStack(0,stack);
            return world.getRecipeManager().getFirstMatch(ModRecipes.MELT_RECIPETYPE,test,world);
        }

        public NbtCompound writeToNbt(){
            NbtCompound slotNbt = getStack().writeNbt(new NbtCompound());
            slotNbt.putBoolean("remove",isMarkRemove());
            slotNbt.putInt("process",processTime);
            slotNbt.putInt("max_process",maxProcessTime);
            return slotNbt;
        }

        public void readFromNbt(NbtCompound nbt){
             processTime = nbt.getInt("process");
             maxProcessTime = nbt.getInt("max_process");
        }

        public Slot(ItemStack stack){this(stack,false);}
        public Slot(ItemStack stack,boolean markRemove,int processTime,int maxProcessTime){
            this(stack,markRemove);
            this.processTime = processTime;
            this.maxProcessTime = maxProcessTime;
        }

        public ItemStack getStack() {
            return stack;
        }


        public void setStack(ItemStack stack) {
            this.stack = stack;
            this.markRemove = false;
        }
        public void remove(){
            this.stack = ItemStack.EMPTY;
            this.markRemove = true;
        }

        public boolean isMarkRemove() {
            return markRemove;
        }
        public void markRemove(){markRemove=true;}

        public void setMarkRemove(boolean markRemove) {
            this.markRemove = markRemove;
        }

        public boolean isEmpty(){return stack.isEmpty();}

        @Override
        public String toString() {
            return stack.toString()+" Remove "+markRemove;
        }
    }

}
