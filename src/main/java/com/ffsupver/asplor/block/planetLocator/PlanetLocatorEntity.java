package com.ffsupver.asplor.block.planetLocator;

import com.ffsupver.asplor.block.SmartEnergyStorage;
import com.ffsupver.asplor.item.item.MeteoriteFragmentItem;
import com.ffsupver.asplor.item.item.NavigationChipItem;
import com.ffsupver.asplor.networking.packet.worldAdder.PlanetCreatingData;
import com.ffsupver.asplor.util.GoggleDisplays;
import com.ffsupver.asplor.util.RenderUtil;
import com.ffsupver.asplor.world.WorldData;
import com.simibubi.create.content.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.foundation.blockEntity.SmartBlockEntity;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import earth.terrarium.adastra.api.planets.Planet;
import earth.terrarium.adastra.common.planets.AdAstraData;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.List;

public class PlanetLocatorEntity extends SmartBlockEntity implements IHaveGoggleInformation  {
    private final PlanetLocatorItemStackHadler navInv;
    private final PlanetLocatorItemStackHadler meteoriteInv;

    private final PlanetLocatorItemStackHadler outputInv;
    private int process;
    private static final int MAX_PROCESS = 40;

    private PlanetCreatingData planet;
    private static final long ENERGY_RATE = 10;
    private static final long CAPACITY=  MAX_PROCESS * ENERGY_RATE * 10;
    private static final long MAX_TRANSFER =5*CAPACITY;
    private Identifier worldKey;
    protected SmartEnergyStorage energyStorage;

    public PlanetLocatorEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
        navInv = new PlanetLocatorItemStackHadler(1);
        meteoriteInv = new PlanetLocatorItemStackHadler(1);
        outputInv = new PlanetLocatorItemStackHadler(1);
        energyStorage = createInventory();
        process = MAX_PROCESS;
    }

    protected SmartEnergyStorage createInventory() {
        return new SmartEnergyStorage(CAPACITY, MAX_TRANSFER, MAX_TRANSFER,energy->{
            notifyUpdate();
        });
    }

    @Override
    protected void read(NbtCompound tag, boolean clientPacket) {
        super.read(tag, clientPacket);
        navInv.deserializeNBT(tag.getCompound("nav_inv"));
        meteoriteInv.deserializeNBT(tag.getCompound("meteorite_inv"));
        outputInv.deserializeNBT(tag.getCompound("output_inv"));
        process = tag.getInt("process");

        energyStorage.readFromNBT(tag.getCompound("energy"));

        if (tag.contains("planet", NbtElement.COMPOUND_TYPE)){
            planet = new PlanetCreatingData();
            planet.fromNbt(tag.getCompound("planet"));
        }
        if (tag.contains("planet_key", NbtElement.STRING_TYPE)){
            worldKey = new Identifier(tag.getString("planet_key"));
        }
    }

    @Override
    protected void write(NbtCompound tag, boolean clientPacket) {
        super.write(tag, clientPacket);
        tag.put("nav_inv", navInv.serializeNBT());
        tag.put("meteorite_inv", meteoriteInv.serializeNBT());
        tag.put("output_inv", outputInv.serializeNBT());
        tag.putInt("process",process);

        tag.put("energy",energyStorage.writeToNBT(new NbtCompound()));

        if (planet != null){
            tag.put("planet", planet.toNbt());
        }
        if (worldKey != null){
            tag.putString("planet_key",worldKey.toString());
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (canProcess() && !navInv.getStackInSlot(0).isEmpty()){

            if (process <= 0){
                processNavItem();
            }else {
                if (hasEnergy() && worldKey != null){
                    energyStorage.extractEnergy(ENERGY_RATE);
                    process--;
                }
            }
        }
        if (!meteoriteInv.getStackInSlot(0).isEmpty()){
            ItemStack itemStack = meteoriteInv.getStackInSlot(0).copyWithCount(1);
            if (itemStack.getItem() instanceof MeteoriteFragmentItem meteoriteFragmentItem){
                PlanetCreatingData newPlanet = meteoriteFragmentItem.getPlanetData(itemStack);
                if (!newPlanet.isEmpty()){
                    planet = newPlanet;
                    meteoriteInv.extract(itemStack);
                }
            }
        }
    }

    private void processNavItem() {
        ItemStack itemStack = navInv.getStackInSlot(0).copyWithCount(1);
        if (worldKey != null){
            RegistryKey<World> firstKey = getWorld().getRegistryKey();
            RegistryKey<World> second = RegistryKey.of(RegistryKeys.WORLD,worldKey);
            System.out.println(second);
            firstKey = AdAstraData.getPlanet(firstKey).orbit().orElse(firstKey);
            second = WorldData.createWorldKey(second);
            System.out.println(second);
            NavigationChipItem.putWorldKey(firstKey,itemStack,true);
            NavigationChipItem.putWorldKey(second,itemStack,false);
        }
        long navCount =  outputInv.insert(itemStack);
        if (navCount > 0){
            navInv.extract(navInv.getStackInSlot(0).copyWithCount(1));
            process = MAX_PROCESS;
        }
    }

    private boolean hasEnergy(){
        return energyStorage.getAmount() > ENERGY_RATE;
    }

    public PlanetCreatingData getPlanet() {
        notifyUpdate();
        return planet;
    }

    public void setWorldKey(Identifier worldKey){
        this.worldKey = worldKey;
    }

    public void updatePlanetData(){
        RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD,this.worldKey);
        Planet getPlanet = AdAstraData.getPlanet(worldKey);
        if (getPlanet.isSpace()){
           RegistryKey<World> g = getPlanet.getOrbitPlanet().orElse(null);
            if (g != null) {
                getPlanet = AdAstraData.getPlanet(g);
            }
        }
        if (getPlanet != null){
            if (this.planet == null){
                this.planet = new PlanetCreatingData();
            }
            this.planet.fromPlanet(getPlanet);
        }
        notifyUpdate();
    }




    @Override
    public boolean addToGoggleTooltip(List<Text> tooltip, boolean isPlayerSneaking) {
        return GoggleDisplays.addEnergyDisplayToGoggles(tooltip,energyStorage);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
    }

    public long insertNav(ItemStack itemStack){
       return navInv.insert(itemStack);
    }

    public long insertMeteorite(ItemStack itemStack){
        return meteoriteInv.insert(itemStack);
    }

    public ItemStack extractOutput(){
        ItemStack result = outputInv.getStackInSlot(0);
        if (!result.isEmpty()){
            outputInv.extract(outputInv.getStackInSlot(0));
        }
        return result;
    }

    public boolean hasItem(){
        return !navInv.getStackInSlot(0).isEmpty() && !meteoriteInv.getStackInSlot(0).isEmpty();
    }

    public ItemStack getItemInside(){
        if (!navInv.getStackInSlot(0).isEmpty()){
            ItemStack o = navInv.getStackInSlot(0);
            navInv.extract(o);
            return o;
        }else  if (!meteoriteInv.getStackInSlot(0).isEmpty()){
            ItemStack o = meteoriteInv.getStackInSlot(0);
            meteoriteInv.extract(o);
            return o;
        }else {
            return ItemStack.EMPTY;
        }
    }


    public ItemStack getNavItem(){
        return navInv.getStackInSlot(0);
    }
    public ItemStack getOutputItem(){
        return outputInv.getStackInSlot(0);
    }

    public float getPlanetRadius(){
        return planet == null ? 0 : planet.gravity == null ? 0 : planet.gravity * 0.01f;
    }

    public boolean canProcess(){
        return worldKey != null || planet != null;
    }

    public boolean hasPlanet(){
        return planet != null;
    }

    public float getProcess(){
        return (float) process / MAX_PROCESS;
    }

    public SmartEnergyStorage getEnergyStorage(Direction direction) {
        return direction.equals(Direction.DOWN) ? energyStorage : null;
    }

    @Override
    public Box getRenderBoundingBox() {
        return RenderUtil.createRenderBoundingBox(pos,16.0);
    }

    private class PlanetLocatorItemStackHadler extends ItemStackHandler{
        public PlanetLocatorItemStackHadler(int stacks){
            super(stacks);
        }

        public long insert(ItemStack stack){
            long result;
            try(Transaction t = Transaction.openOuter()){
                result = this.insert(ItemVariant.of(stack), stack.getCount(), t);
                t.commit();
            }
            return result;
        }

        public long extract(ItemStack stack){
            long result;
            try(Transaction t = Transaction.openOuter()){
                result = this.extract(ItemVariant.of(stack), stack.getCount(), t);
                t.commit();
            }
            return result;
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            long result = super.insert(resource, maxAmount, transaction);
            notifyUpdate();
            return result;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            long result = super.extract(resource, maxAmount, transaction);
            notifyUpdate();
            return result;
        }
    }

}
