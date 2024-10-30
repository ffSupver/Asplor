package com.ffsupver.asplor.block.refinery;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.ModTags;
import com.ffsupver.asplor.recipe.FluidInventory;
import com.ffsupver.asplor.recipe.ModRecipes;
import com.ffsupver.asplor.recipe.RefineryRecipe;
import com.ffsupver.asplor.util.NbtUtil;
import com.ffsupver.asplor.util.RenderUtil;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.fluid.SmartFluidTank;
import com.simibubi.create.foundation.utility.Lang;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.transfer.fluid.FluidTank;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SidedStorageBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.simibubi.create.content.processing.burner.BlazeBurnerBlock.HEAT_LEVEL;
import static net.minecraft.util.Formatting.AQUA;

public class RefineryControllerEntity extends SmartBlockEntity implements SidedStorageBlockEntity , IHaveGoggleInformation {
    private final RefineryData data;
    private int heatCount;
    private int outputLayerCount;
    private ArrayList<Integer> outputLayer;
    private boolean isComplete;
    private SmartFluidTank fluidTank;
    private int checkCooldown;
    private final int maxCheckCooldown = 20;
    private int process;
    private final int maxProcess = 500;
    private final int MAX_SIZE = 16;
    private final long CAPACITY_PER_BLOCK = 4000 * 81;
    public RefineryControllerEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        data = new RefineryData(pos,pos,new ArrayList<>(),new ArrayList<>());
        fluidTank = createTank();
        checkCooldown = maxCheckCooldown;
        this.isComplete = false;
        process = maxProcess;
        this.heatCount = 0;
        this.outputLayerCount = 0;
        this.outputLayer = new ArrayList<>();

    }

    private SmartFluidTank createTank(){
        checkRefinery();
        long capacity = 0;
        if (isComplete){
            capacity = calculateBlocksInside() * CAPACITY_PER_BLOCK;
        }
        return new SmartFluidTank(capacity, this::onFluidTankUpdate);
    }

    private int calculateBlocksInside(){
        return (data.connerHigh.getX() - data.connerLow.getX()-1) * (data.connerHigh.getY() - data.connerLow.getY() - 1) * (data.connerHigh.getZ() - data.connerLow.getZ() - 1);
    }

    private void onFluidTankUpdate(FluidStack fluidStack){
        if (!hasWorld()){
            return;
        }
        if (!world.isClient) {
            markDirty();
            sendData();
        }
    }


    @Override
    public void tick() {
        super.tick();
        if (checkCooldown > 0){
            checkCooldown--;
        }else {
            boolean perIsComplete = isComplete;
            checkRefinery();
            checkCooldown = maxCheckCooldown;
            if (isComplete){
                setInput();
                checkHeat();
                checkOutputLayer();
                if (!perIsComplete){
                    fluidTank.setCapacity(calculateBlocksInside()*CAPACITY_PER_BLOCK);
                }
            }
        }


        if (isComplete && hasRecipe() && hasHeat()){
            updateBlockState(true);
            if (world.isClient()){
                spawnParticle();
            }
            if (process<=0){
                craft();
                process = maxProcess;
            }else {
                process -= getProcessTimeDecreasePerTick();
            }
        }else {
            updateBlockState(false);
            process = maxProcess;
        }

    }

    private void updateBlockState(boolean active){
        world.setBlockState(pos,getCachedState().with(RefineryController.ACTIVE,active));
    }

    private int getProcessTimeDecreasePerTick(){
        return Math.max(1,calculateBlocksInside()/2);
    }

    private void craft(){
        RefineryRecipe recipe = getCurrentRecipe().get();
        try (Transaction t = Transaction.openOuter()){
            fluidTank.extract(FluidVariant.of(recipe.getInputFluid()),recipe.getInputFluidAmount(),t);
            ArrayList<Fluid> outputFluids = recipe.getOutputFluids();
            for (int i =0 ; i < outputFluids.size();i++){
                Fluid outputFluid = outputFluids.get(i);
                Long outputFluidAmount = recipe.getOutputFluidAmounts().get(outputFluid);
                getOutputFluidTank(i,outputFluid,outputFluidAmount).insert(FluidVariant.of(outputFluid),outputFluidAmount,t);
            }
            t.commit();
        }
    }

    private Optional<RefineryRecipe> getCurrentRecipe(){
        FluidInventory test = new FluidInventory(1,MAX_SIZE*MAX_SIZE*MAX_SIZE*CAPACITY_PER_BLOCK*2);
        test.insertFluid(0,FluidVariant.of(fluidTank.getFluid().getFluid()),fluidTank.getFluidAmount());
        return world.getRecipeManager().getFirstMatch(ModRecipes.REFINERY_RECIPETYPE,test,world);
    }

    private boolean hasRecipe(){
        if (getCurrentRecipe().isPresent()){
            RefineryRecipe recipe = getCurrentRecipe().get();
            ArrayList<Fluid> outputFluids = recipe.getOutputFluids();

            for (int i =0 ; i < outputFluids.size();i++){
                Fluid outputFluid = outputFluids.get(i);
                Long outputFluidAmount = recipe.getOutputFluidAmounts().get(outputFluid);
                if(getOutputFluidTank(i,outputFluid,outputFluidAmount) == null){
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean hasHeat(){
        return heatCount >= Math.max(1,calculateBlocksInside()*0.2);
    }

    private void checkOutputLayer(){
        outputLayerCount = 0;
        outputLayer.clear();
        if (!this.data.output.isEmpty()){
            ArrayList<Integer> checked = new ArrayList<>();
            for (BlockPos outputPos : this.data.output){
                if (!checked.contains(outputPos.getY())){
                    checked.add(outputPos.getY());
                    outputLayerCount++;
                }
            }
            checked.sort((x,y)->Integer.compare(y,x));
            outputLayer = checked;
        }

    }

    private void setInput(){
        for (BlockPos inputPos:this.data.input){
           BlockEntity blockEntity = world.getBlockEntity(inputPos);
           if (blockEntity instanceof RefineryInputEntity){
               RefineryInputEntity inputEntity = (RefineryInputEntity) blockEntity;
               inputEntity.setControllerPos(pos);
           }
        }
    }

    private FluidTank getOutputFluidTank(int index,Fluid outputFluid,long outputAmount){
        if (index < outputLayerCount){
            for (BlockPos outputPos : this.data.output) {
                if (outputPos.getY() == outputLayer.get(index)) {
                    BlockEntity blockEntity = world.getBlockEntity(outputPos);
                    if (blockEntity instanceof RefineryOutputEntity) {
                        RefineryOutputEntity outputEntity = (RefineryOutputEntity) blockEntity;
                        FluidTank outputFluidTank = outputEntity.getFluidTank();
                        Fluid originalFluid = outputFluidTank.getFluid().getFluid();
                        if ((originalFluid.equals(outputFluid) || originalFluid.equals(Fluids.EMPTY)) && outputFluidTank.getCapacity() - outputFluidTank.getFluidAmount() >= outputAmount) {
                            return outputFluidTank;
                        }
                    }
                }
            }
        }
        return null;
    }

    private void checkHeat(){
        int newHeatCount = 0;
        for (int i = data.connerLow.getX() + 1; i < data.connerHigh.getX();i++){
            for (int j = data.connerLow.getZ() + 1;j < data.connerHigh.getZ();j++){
                BlockPos checkPos = new BlockPos(i,data.connerLow.getY()-1,j);
                if(world.getBlockState(checkPos).contains(HEAT_LEVEL)){
                    newHeatCount += switch (world.getBlockState(checkPos).get(HEAT_LEVEL)){
                        case SEETHING -> 2;
                        case KINDLED, FADING -> 1;
                        default -> 0;
                    };
                }
            }
        }
        heatCount = newHeatCount;
    }


    private void checkRefinery(){
        //east x+    south z+
        BlockPos lower = pos;

        //找到最小坐标
        while (isBlockAvailable(lower.down()) && pos.getY() - lower.down().getY() <= MAX_SIZE){
            lower = lower.down();
        }
        while (isBlockAvailable(lower.west()) && pos.getX() - lower.down().getX() <= MAX_SIZE){
            lower = lower.west();
        }
        while (isBlockAvailable(lower.north()) && pos.getZ() - lower.down().getZ() <= MAX_SIZE){
            lower = lower.north();
        }

        //找到最大坐标
        BlockPos higher = lower;
        while (isBlockAvailable(higher.east()) && higher.getX() - lower.getX() <=MAX_SIZE){
           higher = higher.east();
        }
        while (isBlockAvailable(higher.south()) && higher.getZ() - lower.getZ() <=MAX_SIZE){
            higher = higher.south();
        }
        while (isBlockAvailable(higher.up()) && higher.getY() - lower.getY() <=MAX_SIZE){
            higher = higher.up();
        }



        //设置对角
        if (checkHollowAndAddOutput(lower,higher)){
            this.isComplete = true;
            data.connerLow = lower;
            data.connerHigh = higher;
        }else {
            this.isComplete = false;
            data.clear();
        }

    }



    private boolean isBlockAvailable(BlockPos pos){
        if (hasWorld()) {
            return world.getBlockState(pos).isIn(ModTags.Blocks.REFINERY_BLOCK);
        }
        return false;
    }

    private boolean checkHollowAndAddOutput(BlockPos minPos, BlockPos maxPos) {
        boolean hasInner = maxPos.getX() - minPos.getX() >=2 && maxPos.getY() - minPos.getY() >=2 && maxPos.getZ() - minPos.getZ() >=2;
        if (!hasInner){
            return false;
        }


        this.data.output.clear();
        this.data.input.clear();

        for (int x = minPos.getX(); x <= maxPos.getX(); x++) {
            for (int y = minPos.getY(); y <= maxPos.getY(); y++) {
                for (int z = minPos.getZ(); z <= maxPos.getZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);

                    boolean isOnBoundary = (
                            x == minPos.getX() || x == maxPos.getX() ||
                                    y == minPos.getY() || y == maxPos.getY() ||
                                    z == minPos.getZ() || z == maxPos.getZ()
                    );

                    // 边界必须是有效方块，内部必须是空气
                    if (isOnBoundary) {
                        if (!isBlockAvailable(pos)) return false;
                        if (!pos.equals(this.pos) && world.getBlockState(pos).isOf(AllBlocks.REFINERY_CONTROLLER.get())) return false;
                        if (pos.getY() != maxPos.getY() && pos.getY() != minPos.getY()){
                            boolean upperThanHalf = minPos.getY()+(maxPos.getY()-minPos.getY()-1)/2 < pos.getY();
                            if (upperThanHalf && !this.data.output.contains(pos) && world.getBlockState(pos).isOf(AllBlocks.REFINERY_OUTPUT.get())) {
                                this.data.output.add(pos);
                            }
                            if (!this.data.input.contains(pos) && world.getBlockState(pos).isOf(AllBlocks.REFINERY_INPUT.get())){
                                this.data.input.add(pos);
                            }
                        }
                    } else {
                        if (!world.getBlockState(pos).isAir()) return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        this.fluidTank.readFromNBT(tag.getCompound("fluid"));
        this.isComplete = tag.getBoolean("is_complete");
        if (isComplete){
            data.readFromNbt(tag.getCompound("refinery_data"));
            this.heatCount = tag.getInt("heat");
            this.process = tag.getInt("process");
        }
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("fluid", this.fluidTank.writeToNBT(new NbtCompound()));
        tag.putBoolean("is_complete",this.isComplete);
        if (isComplete){
            tag.put("refinery_data",data.writeToNbt());
            tag.putInt("heat",this.heatCount);
            tag.putInt("process",process);
        }
    }

    @Override
    public @Nullable Storage<FluidVariant> getFluidStorage(@Nullable Direction side) {
        return null;
    }

    @Override
    public Box getRenderBoundingBox() {
        return RenderUtil.createRenderBoundingBox(pos, (double) (MAX_SIZE*2));
    }

    public SmartFluidTank getFluidTank() {
        return fluidTank;
    }

    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        //添加精炼厂状态
        Lang.translate("gui.goggles.refinery_stats")
                .forGoggles(tooltip);
        String refineryDescription = isComplete ?
                this.data.input.isEmpty() ?
                        "tooltip.refinery.no_input" :
                        this.data.output.isEmpty() ?
                            "tooltip.refinery.no_output"
                            : "tooltip.refinery.ready"
                : "tooltip.refinery.incomplete";
        Lang.translate(refineryDescription)
                .style(AQUA)
                .forGoggles(tooltip);
        if (isComplete){
           return containedFluidTooltip(tooltip,isPlayerSneaking,fluidTank);
        }
        return IHaveGoggleInformation.super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    private void spawnParticle(){
        Vec3d particlePos = this.data.connerLow.toCenterPos().add(.5,.5 + getLiquidHeight(),.5);
        double offsetX = (this.data.connerHigh.getX()-this.data.connerLow.getX()-1)*world.getRandom().nextDouble();
        double offsetZ = (this.data.connerHigh.getZ()-this.data.connerLow.getZ()-1)*world.getRandom().nextDouble();
        world.addParticle(ParticleTypes.CAMPFIRE_COSY_SMOKE,particlePos.x+offsetX,particlePos.y,particlePos.z+offsetZ,0.0, 0.005, 0.0);
    }

    public double getLiquidHeight(){
        FluidStack fluidStack = fluidTank.getFluid();
        Long capacity = fluidTank.getCapacity();

        Vec3d minPosA = this.getLiquidBox().get(0);
        Vec3d maxPosA = this.getLiquidBox().get(1);
        double height = maxPosA.y-minPosA.y;
        return height*(fluidStack.getAmount())/capacity;
    }

    public ArrayList<Vec3d> getLiquidBox(){
        ArrayList<Vec3d> liquidBox = new ArrayList<>();
        int height = this.data.connerHigh.getY()-this.data.connerLow.getY()-1;
        liquidBox.add(this.data.connerLow.toCenterPos().add(.5,.5,.5));
        liquidBox.add(this.data.connerHigh.toCenterPos().add(-.5, (double) - height /2-.5,-.5));
        return liquidBox;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }
    private class RefineryData{
        private BlockPos connerLow;
        private BlockPos connerHigh;
        private ArrayList<BlockPos> input;
        private ArrayList<BlockPos> output;
        public RefineryData(BlockPos connerLow, BlockPos connerHigh, ArrayList<BlockPos> input, ArrayList<BlockPos> output){
            this.connerLow = connerLow;
            this.connerHigh = connerHigh;
            this.output = output;
            this.input = input;
        }


        public void clear(){
            this.connerLow = pos;
            this.connerHigh = pos;
            this.input = new ArrayList<>();
            this.output = new ArrayList<>();
        }

        public void readFromNbt(NbtCompound nbt){
            this.connerHigh = NbtUtil.readBlockPosFromNbt(nbt.getCompound("high"));
            this.connerLow = NbtUtil.readBlockPosFromNbt(nbt.getCompound("low"));
            NbtList inputList = nbt.getList("input",10);
            this.input = new ArrayList<>();
            for (NbtElement element : inputList){
                NbtCompound inputPosNbt = (NbtCompound) element;
                input.add(NbtUtil.readBlockPosFromNbt(inputPosNbt));
            }
            NbtList outputList = nbt.getList("output",10);
            this.output = new ArrayList<>();
            for (NbtElement element : outputList){
                NbtCompound outputPosNbt = (NbtCompound) element;
                output.add(NbtUtil.readBlockPosFromNbt(outputPosNbt));
            }
        }
        public NbtCompound writeToNbt(){
            NbtCompound result = new NbtCompound();
            result.put("high",NbtUtil.writeBlockPosToNbt(connerHigh));
            result.put("low",NbtUtil.writeBlockPosToNbt(connerLow));
            NbtList inputList = new NbtList();
            for (BlockPos inputPos : input){
                inputList.add(NbtUtil.writeBlockPosToNbt(inputPos));
            }
            result.put("input",inputList);
            NbtList outputList = new NbtList();
            for (BlockPos outputPos : output){
                outputList.add(NbtUtil.writeBlockPosToNbt(outputPos));
            }
            result.put("output",outputList);
            return result;
        }
    }
}
