package com.ffsupver.asplor.block.smartMechanicalArm;

import com.ffsupver.asplor.block.alloyDepot.AlloyDepotBehaviour;
import com.ffsupver.asplor.block.alloyDepot.AlloyDepotEntity;
import com.ffsupver.asplor.item.item.ToolItem;
import com.ffsupver.asplor.recipe.ModRecipes;
import com.ffsupver.asplor.recipe.SmartProcessingRecipe;
import com.ffsupver.asplor.util.NbtUtil;
import com.ffsupver.asplor.util.RenderUtil;
import com.jozufozu.flywheel.core.PartialModel;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.content.kinetics.base.KineticBlockEntity;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.logistics.depot.DepotBehaviour;
import com.simibubi.create.content.logistics.depot.DepotBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SmartMechanicalArmEntity extends KineticBlockEntity implements IHaveGoggleInformation , IWrenchable {
    private BlockPos targetPos;
    private ArrayList<BlockPos> toolPosList;
    private ToolType toolType;
    private int usage;
    private int process;
    private int getToolProcess;
    private final int GET_TOOL_TIME = 20;
    public final int PROCESS_TIME = 100;
    private ArmData armData;
    public SmartMechanicalArmEntity(BlockEntityType<?> typeIn, BlockPos pos, BlockState state) {
        super(typeIn, pos, state);
        this.usage = 0;
        this.process = PROCESS_TIME;
        this.getToolProcess = GET_TOOL_TIME;
        this.armData = new ArmData(new Vec3d(0,1,-2),0,65,-115);
        this.toolPosList = new ArrayList<>();
        this.toolType = ToolTypes.EMPTY;
    }


    @Override
    public void tickAudio() {
        super.tickAudio();
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getSpeed() == 0){
            return;
        }

        double armSpeed = 0.4*Math.abs(speed)/256;


        if (hasTarget() && hasRecipe() && canInsertToOutput()){
            ToolType recipeToolType = getRecipeToolType();
            if (this.toolType.equals(ToolTypes.EMPTY)){
                if (hasTool(recipeToolType)){
                    this.armData.moveTo(getToolPos(recipeToolType),armSpeed);
                    getTool(recipeToolType);
                }
            }else if (!this.toolType.equals(recipeToolType) || this.usage <= 0){
                if (getToolEntityPosToDrop() != null){
                    this.armData.moveTo(getToolEntityPosToDrop(),armSpeed);
                    dropTool();
                }
            }else{
                if (armData.isAt(targetPos) || process < PROCESS_TIME) {
                    this.armData.process();
                    if (process <= 0) {
                        if (craft()){
                            process = PROCESS_TIME;
                        }

                    } else {
                        process -= 1;
                    }
                } else {
                    armData.moveTo(targetPos, armSpeed);
                }
            }

        }else {
            process = PROCESS_TIME;
        }
    }

    private @Nullable BlockEntity getTargetEntity(){
        if (targetPos != null){
           BlockEntity blockEntity = world.getBlockEntity(targetPos);
           if (blockEntity instanceof DepotBlockEntity depotBlockEntity){
               return depotBlockEntity;
           }
           if (blockEntity instanceof AlloyDepotEntity alloyDepotEntity){
               return alloyDepotEntity;
           }
        }
        return null;
    }

    private boolean isAlloyDepot(){
        return getTargetEntity() instanceof AlloyDepotEntity;
    }

    private @Nullable BlockPos getToolEntityPosToDrop(){
        for (BlockPos toolPos : toolPosList){
            ToolGearEntity toolGearEntity = getToolGearEntity(toolPos);
            if (toolGearEntity != null && toolGearEntity.canDropTool()){
                return toolPos;
            }
        }
        return null;
    }

    private void dropTool(){
        if (this.armData.isAt(getToolEntityPosToDrop())){
            ToolGearEntity toolGearEntity = getToolGearEntity(getToolEntityPosToDrop());
            if (toolGearEntity.dropTool(toolType,usage)){
                this.toolType = ToolTypes.EMPTY;
                sendData();
            }
        }
    }

    private Optional<SmartProcessingRecipe> getCurrentRecipe(){
        Inventory test = new SimpleInventory(1);
        test.setStack(0,getTargetItemStack());
        return world.getRecipeManager().getFirstMatch(ModRecipes.SMART_PROCESSING_RECIPETYPE,test,world);
    }

    private ItemStack getTargetItemStack(){
        if (!isAlloyDepot()){
            DepotBlockEntity depotBlockEntity = (DepotBlockEntity) getTargetEntity();
            return depotBlockEntity.getHeldItem();
        }else {
            return ((AlloyDepotEntity)getTargetEntity()).getHeldItem();
        }
    }

    private ToolType getRecipeToolType(){
        SmartProcessingRecipe recipe = getCurrentRecipe().get();
        return recipe.getToolType(getTargetItemStack());
    }

    private boolean hasRecipe(){
        if (getCurrentRecipe().isPresent()){
            SmartProcessingRecipe recipe = getCurrentRecipe().get();
            if (recipe.requireSchematic()){
                if (isAlloyDepot()){
                    AlloyDepotEntity alloyDepotEntity = (AlloyDepotEntity) getTargetEntity();
                    String schematic = recipe.getSchematic();
                    if (alloyDepotEntity != null) {
                        return schematic.equals(alloyDepotEntity.getSchematic());
                    }
                }
                return false;
            }
            return true;
        }
       return false;
    }

    private boolean canInsertToOutput(){
        SmartProcessingRecipe recipe = getCurrentRecipe().get();
        if (isAlloyDepot()){
            AlloyDepotEntity alloyDepotEntity = (AlloyDepotEntity) getTargetEntity();
            ItemStack output = recipe.process(alloyDepotEntity.getHeldItem().copyWithCount(1));
            return output.getCount() <= alloyDepotEntity.insertToOutput(ItemVariant.of(output),output.getCount(),true);
        }else {
            DepotBlockEntity depotBlockEntity = (DepotBlockEntity) getTargetEntity();
            return depotBlockEntity.getHeldItem().getCount() == 1;
        }
    }

    private boolean hasTool(ToolType toolTypeNeed){
        return getToolPos(toolTypeNeed) !=null;
    }



    private void getTool(ToolType toolTypeNeed){
        if (this.armData.isAt(getToolPos(toolTypeNeed))){
            if (getToolProcess<0){
                ToolGearEntity toolGearEntity = getToolGearEntity(getToolPos(toolTypeNeed));
                if (toolGearEntity != null){
                    int usage =toolGearEntity.getTool(toolTypeNeed);
                    if (usage > 0){
                        this.usage = usage;
                        this.toolType = toolTypeNeed;
                        sendData();
                    }
                }
                getToolProcess = GET_TOOL_TIME;
            }else {
                getToolProcess -= 1;
            }
        }else {
            getToolProcess = GET_TOOL_TIME;
        }
    }

    private @Nullable ToolGearEntity getToolGearEntity(BlockPos blockPos){
       BlockEntity blockEntity = world.getBlockEntity(blockPos);
       if (blockEntity instanceof ToolGearEntity toolGearEntity){
          return toolGearEntity;
       }
       return null;
    }

    private @Nullable BlockPos getToolPos(ToolType toolTypeNeed){
        for (BlockPos toolPos : toolPosList){
            if (getToolGearEntity(toolPos) != null){
                ToolGearEntity toolEntity = getToolGearEntity(toolPos);
                for (ToolType toolType1 : toolEntity.getTools()){
                    if (toolType1 != null && toolType1.equals(toolTypeNeed)){
                        return toolPos;
                    }
                }
            }
        }
        return null;
    }

    private boolean craft(){
        SmartProcessingRecipe recipe = getCurrentRecipe().get();
        if (!isAlloyDepot()){
            DepotBlockEntity depotBlockEntity = (DepotBlockEntity) getTargetEntity();

            ItemStack originalItem = depotBlockEntity.getHeldItem();
            ItemStack output = recipe.process(getTargetItemStack()).copy();
            DepotBehaviour behaviour = depotBlockEntity.getBehaviour(DepotBehaviour.TYPE);
            try(Transaction t = Transaction.openOuter()){
                behaviour.setHeldItem(new TransportedItemStack(output));
                t.commit();
            }
            if (depotBlockEntity.getHeldItem().equals(originalItem)){
                return false;
            }else {
                this.usage -= 1;
                return true;
            }
        }else {
            AlloyDepotEntity alloyDepotBlockEntity = (AlloyDepotEntity) getTargetEntity();

            ItemStack originalItem = alloyDepotBlockEntity.getHeldItem();
            ItemStack output = recipe.process(getTargetItemStack().copyWithCount(1)).copy();
            AlloyDepotBehaviour behaviour = alloyDepotBlockEntity.getBehaviour(AlloyDepotBehaviour.TYPE);
            TransportedItemStack left = behaviour.getHeldItem().copy();
            left.stack.setCount(originalItem.getCount() - 1);
            behaviour.setHeldItem(left);
            alloyDepotBlockEntity.insertToOutput(ItemVariant.of(output),output.getCount(),false);


            if (alloyDepotBlockEntity.getHeldItem().equals(originalItem)){
                return false;
            }else {
                this.usage -= 1;
                return true;
            }
        }
    }

    private boolean hasTarget(){
        return getTargetEntity() != null && targetPos.getY() == pos.getY();
    }

    @Override
    protected void read(NbtCompound compound, boolean clientPacket) {
        targetPos = NbtUtil.readBlockPosFromNbt(compound.getCompound("target"));
        getToolProcess = compound.getInt("get_tool_process");
        process = compound.getInt("process");
        armData.readFromNbt(compound.getCompound("arm"));
        NbtList tools = compound.getList("tools",10);
        toolPosList.clear();
        for (NbtElement element:tools){
            BlockPos tool = NbtUtil.readBlockPosFromNbt((NbtCompound) element);
            toolPosList.add(tool);
        }
        this.toolType = ToolType.readFromNbt(compound.getCompound("tool_type"));
        this.usage = compound.getInt("usage");
        super.read(compound, clientPacket);
    }

    @Override
    protected void write(NbtCompound compound, boolean clientPacket) {
        if (targetPos != null) {
            compound.put("target", NbtUtil.writeBlockPosToNbt(targetPos));
        }
        compound.putInt("get_tool_process",getToolProcess);
        compound.putInt("process",process);
        compound.put("arm",armData.writeToNbt());
        NbtList tools = new NbtList();
        for (BlockPos tool:this.toolPosList){
            tools.add(NbtUtil.writeBlockPosToNbt(tool));
        }
        compound.put("tools",tools);
        compound.put("tool_type",toolType.writeToNbt());
        compound.putInt("usage",usage);
        super.write(compound, clientPacket);
    }

    @Override
    public void destroy() {
        super.destroy();
        if (toolType.getToolItem() instanceof ToolItem toolItem){
            ItemStack toolToDrop = toolType.getToolItem().getDefaultStack();
            toolToDrop = toolItem.setUse(toolToDrop,usage);
            Block.dropStack(world,pos,toolToDrop);
        }
    }

    public PartialModel getToolModel(boolean isWorking){
       return isWorking ? this.toolType.getToolWorkModel() : this.toolType.getToolModel();
    }

    @Override
    public Box getRenderBoundingBox() {
        return RenderUtil.createRenderBoundingBox(pos,5.0);
    }

    public ArmData getArmData() {
        return armData;
    }

    public int getProcess() {
        return process;
    }

    public void addToolPos(BlockPos pos){
        this.toolPosList.add(pos);
    }

    public void setTargetPos(BlockPos pos){
        this.targetPos = pos;
    }

    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        return super.addToGoggleTooltip(tooltip, isPlayerSneaking);
    }

    public class ArmData{
        public float baseRotationDegree;
        public float firstRotationDegree;
        public float secondRotationDegree;
        //相对位置
        public Vec3d headPos;
        public ArmData(Vec3d headPos,float baseRotationDegree,float firstRotationDegree,float secondRotationDegree){
            this.baseRotationDegree=baseRotationDegree;
            this.firstRotationDegree=firstRotationDegree;
            this.secondRotationDegree=secondRotationDegree;
            this.headPos = headPos;
            updateArm();
        }

        public void moveTo(BlockPos destinationPos,double distant){
            Vec3d nowPos = pos.toCenterPos().add(headPos);
            Vec3d targetPosVec3d = destinationPos.toCenterPos().offset(Direction.UP,1);
            Vec3d relative = new Vec3d(targetPosVec3d.x - nowPos.x,targetPosVec3d.y - nowPos.y,targetPosVec3d.z - nowPos.z);
            if (relative.length() > distant){
               relative = relative.multiply(distant/relative.length());
            }
            Vec3d newHeadPos = this.headPos.add(relative);
            Vec3d newHorizonHeadPos = newHeadPos.add(0, -newHeadPos.y, 0);
            double closestDistance = 1.07;
            if (newHorizonHeadPos.length() < closestDistance){
                newHorizonHeadPos = newHorizonHeadPos
                        .multiply(closestDistance/newHorizonHeadPos.length())
                        .rotateY((float) (distant/(Math.PI*closestDistance*closestDistance*2)));
                newHeadPos = newHorizonHeadPos.add(0,newHeadPos.y,0);
            }
            this.headPos = newHeadPos;
            updateArm();
        }

        public boolean isAt(BlockPos destinationPos){
            boolean sameY = destinationPos.getY() == pos.getY();
            int maxQBXFDistance = Math.max(
                    Math.abs(destinationPos.getX()-pos.getX()),
                    Math.abs(destinationPos.getZ()-pos.getZ()));
            boolean nextToArm = sameY && maxQBXFDistance == 1;
            Vec3d worldPos = pos.toCenterPos().add(headPos);
            Vec3d desPos = destinationPos.toCenterPos().offset(Direction.UP,1);
            return worldPos.isInRange(desPos,0.05) ||
                    (nextToArm && worldPos.isInRange(desPos,0.57));
        }

        public void process(){
            float processTime =1 - (float) process /PROCESS_TIME;
            this.headPos = this.headPos.add(0.5*Math.sin(processTime*4*Math.PI)/PROCESS_TIME,0,0.5*Math.sin(processTime*10*Math.PI)/PROCESS_TIME);
            updateArm();
        }

        private void updateArm(){
            //处理底座
            // 定义模型当前朝向向量，即 z 轴负方向
            Vec3d modelDirection = new Vec3d(0, 0, -1);

            // 计算单位化的目标向量
            Vec3d normalizedTarget = headPos.add(0,-headPos.y,0).normalize();

            // 计算两向量的夹角
            double dotProduct = modelDirection.dotProduct(normalizedTarget);
            dotProduct = Math.max(-1.0, Math.min(1.0, dotProduct));
            double angleRad = Math.acos(dotProduct);  // 返回弧度

            // 将弧度转为角度
            float angleDeg = (float) Math.toDegrees(angleRad);

            // 使用叉积判断方向
            double crossProductY = modelDirection.crossProduct(normalizedTarget).y;
            if (crossProductY < 0) {
                // 若叉积的 Y 分量小于 0，则向右旋转
                angleDeg = -angleDeg;
            }
            this.baseRotationDegree = angleDeg;

            //处理动力臂
            Vec3d headPosRelative = headPos.add(0,-1,0).multiply((headPos.add(0,-1,0).length() - 5/16f)/headPos.add(0,-1,0).length());
            double distant = headPosRelative.length();
            double firstArmLength = 32/16.0;
            float firstArmPitchAngleCos = (float) (distant/(2*firstArmLength));
            firstArmPitchAngleCos = (float) Math.max(-1.0, Math.min(1.0, firstArmPitchAngleCos));
            float basePitchAngleSin = (float) ((headPos.y - 1)/distant);
            basePitchAngleSin = (float) Math.max(-1.0, Math.min(1.0, basePitchAngleSin));

            float firstArmRotationRad = (float) (Math.acos(firstArmPitchAngleCos) + Math.asin(basePitchAngleSin));
            this.firstRotationDegree = (float) Math.toDegrees(firstArmRotationRad);

            this.secondRotationDegree = -180 + 2*(90f - (float) Math.toDegrees(Math.acos(firstArmPitchAngleCos)));


            sendData();
            markDirty();
        }



        public NbtCompound writeToNbt(){
            NbtCompound nbt = new NbtCompound();
            nbt.putFloat("base",baseRotationDegree);
            nbt.putFloat("first",firstRotationDegree);
            nbt.putFloat("second",secondRotationDegree);
            nbt.put("head",NbtUtil.writeVec3dToNbt(headPos));
            return nbt;
        }

        public void readFromNbt(NbtCompound nbt){
            this.baseRotationDegree = nbt.getFloat("base");
            this.firstRotationDegree = nbt.getFloat("first");
            this.secondRotationDegree = nbt.getFloat("second");
            this.headPos = NbtUtil.readVec3dFromNbt(nbt.getCompound("head"));
        }
    }
}
