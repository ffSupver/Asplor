package com.ffsupver.asplor.block.spaceTeleporter;

import com.ffsupver.asplor.ModTags;
import com.ffsupver.asplor.block.SmartEnergyStorage;
import com.ffsupver.asplor.util.GoggleDisplays;
import com.ffsupver.asplor.util.RenderUtil;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.utility.Lang;
import net.fabricmc.fabric.impl.dimension.FabricDimensionInternals;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;

import java.util.List;
import java.util.Objects;

public class SpaceTeleporterEntity extends SmartBlockEntity implements IWrenchable , IHaveGoggleInformation {


    public final SmartEnergyStorage energyStorage;
    private static final long CAPACITY=1000000;
    private static final long MAX_TRANSFER =CAPACITY;
    private static final long MIN_ENERGY = (long) (CAPACITY*0.5);
    private static final long ENERGY_PER_TELEPORT = (long) (CAPACITY*0.1);


    private boolean canTeleport;
    private boolean renderCanTeleport;
    private boolean hasTarget;
    private final int TELEPORT_HEIGHT = 800;
    private final int LIFT_HEIGHT=1000;
    //相对高度
    private int highestEntity ;

    private BlockPos targetPos;
    private String targetDimension;

    private final Double RENDER_DISTANCE = 1024.0;
    public SpaceTeleporterEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.canTeleport=false;
        this.renderCanTeleport=canTeleport;
        this.hasTarget=false;
        this.highestEntity=0;
        this.targetPos=pos;
        this.targetDimension=world != null?world.getDimension().toString():"none";
        this.energyStorage=new SmartEnergyStorage(CAPACITY, MAX_TRANSFER, MAX_TRANSFER,this::onEnergyLevelChanged);


    }

    protected void onEnergyLevelChanged(long newEnergyLevel) {
        markDirty();
        if (!world.isClient()) {
            sendData();
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (world == null) {
            return;
        }

        if (Objects.equals(this.targetDimension, "none")){
            targetDimension= world.getRegistryKey().getValue().toString();
        }

        //检测是否有目标
         hasTarget = !(targetPos.getX() == pos.getX() && targetPos.getZ() == pos.getZ() &&
                Objects.equals(targetDimension, world.getRegistryKey().getValue().toString()));

        //检测电量是否足够
        boolean hasEnergy = energyStorage.getAmount()> MIN_ENERGY;

        //检测高度
        int maxHeight = pos.getY() - 1;
        for (int i = pos.getY() + 1; i <= world.getTopY(); i++) {
            BlockState checkState = world.getBlockState(new BlockPos(pos.getX(), i, pos.getZ()));
            if (!(checkState.isAir())) {
                maxHeight = i;
                break;
            }
        }

        canTeleport = maxHeight < pos.getY();
        maxHeight = canTeleport ? LIFT_HEIGHT : maxHeight;


        renderCanTeleport=canTeleport;


        //检测生物
        List<Entity> entitiesAbove = world.getOtherEntities(null, new Box(pos.getX(), pos.getY(), pos.getZ(), pos.getX() + 1, maxHeight, pos.getZ() + 1), (entity -> entity.getType().isIn(ModTags.EntityTypes.CAN_TELEPORT))
        );


        //传送生物
        highestEntity=0;
        entitiesAbove.forEach(entity -> {
            if (!hasTarget||!hasEnergy){
                return;
            }
            if (entity instanceof PlayerEntity&& entity.isSpectator()){
                return;
            }
            highestEntity=Math.max(highestEntity,(int)entity.getY()-pos.getY());
            Vec3d motion = entity.getVelocity();
            if (entity.getY() < (pos.getY() + TELEPORT_HEIGHT)) {
                motion = motion.getY() < 0 ? new Vec3d(motion.x, 0, motion.z) : motion;
                motion = motion.add(0, 0.2, 0);
                entity.setVelocity(motion);
                //耗能
                energyStorage.extractEnergy(1);
            } else {
                Vec3d newMotion = new Vec3d(motion.x,-motion.y,motion.z);



                TeleportTarget teleportTarget = new TeleportTarget(
                        new Vec3d(targetPos.getX()+0.5,targetPos.getY()+LIFT_HEIGHT,targetPos.getZ()+0.5),
                        newMotion, entity.getYaw(), entity.getPitch());
                if (!world.isClient()) {
                    if (world.getRegistryKey().getValue().toString().equals(targetDimension)) {
                        entity.setVelocity(teleportTarget.velocity);
                        entity.teleport(teleportTarget.position.x,teleportTarget.position.y,teleportTarget.position.z);
                    } else {
                        RegistryKey<World> targetWorld = RegistryKey.of(RegistryKeys.WORLD, new Identifier(targetDimension));
                        FabricDimensionInternals.changeDimension(entity, world.getServer().getWorld(targetWorld), teleportTarget);
                    }
                }
                energyStorage.extractEnergy(ENERGY_PER_TELEPORT);
            }
        });


    }

    public int getHighestEntity() {
        return highestEntity;
    }

    public void setCanTeleport(boolean canTeleport) {
        this.canTeleport = canTeleport;
    }

    public void setTargetPos(BlockPos targetPos) {
        this.targetPos = targetPos;
    }

    public void setTargetDimension(String targetDimension) {
        this.targetDimension = targetDimension;
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);

        energyStorage.readFromNBT(tag.getCompound("Energy"));

        canTeleport = tag.getBoolean("can_teleport");
        NbtCompound targetData = tag.getCompound("target");
        String dimensionId = targetData.getString("dimension");
        targetDimension = dimensionId;
        targetPos=new BlockPos(
                targetData.getInt("x"),
                targetData.getInt("y"),
                targetData.getInt("z")
        );
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);

        tag.put("Energy",energyStorage.writeToNBT(new NbtCompound()));

        tag.getBoolean("can_teleport");
        NbtCompound targetData = new NbtCompound();
        targetData.putInt("x",targetPos.getX());
        targetData.putInt("y",targetPos.getY());
        targetData.putInt("z",targetPos.getZ());
        targetData.putString("dimension", targetDimension);
        tag.put("target",targetData);
    }

    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        if (renderCanTeleport&&hasTarget){
            Lang.translate("gui.goggles.asplor.space_teleporter.info").forGoggles(tooltip);
            Lang.translate("gui.goggles.asplor.space_teleporter.destination",
                            targetDimension,targetPos.getX(),targetPos.getY(),targetPos.getZ())
                    .style(Formatting.AQUA)
                    .forGoggles(tooltip,1);

        }else{
            Lang.translate("gui.goggles.asplor.space_teleporter.info")
                    .forGoggles(tooltip);
            Lang.translate("gui.goggles.asplor.space_teleporter.can_not_teleport")
                    .style(Formatting.AQUA)
                    .forGoggles(tooltip,1);
        }
        return GoggleDisplays.addEnergyDisplayToGoggles(tooltip,energyStorage);
    }

    public SmartEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }



    //视锥外不会剔除渲染
    @Override
    protected Box createRenderBoundingBox() {
        return RenderUtil.createRenderBoundingBox(pos,RENDER_DISTANCE);
    }
}
