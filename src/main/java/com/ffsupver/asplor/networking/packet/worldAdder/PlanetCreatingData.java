package com.ffsupver.asplor.networking.packet.worldAdder;

import earth.terrarium.adastra.api.planets.Planet;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.math.random.Random;

public class PlanetCreatingData {
    public Boolean oxygen;
    public Short temperature;
    public Float gravity;
    public Integer tier;
    public Integer solarPower;

    // 写入数据到 PacketByteBuf
    public void writeToBuffer(PacketByteBuf buf) {
        // 处理 oxygen 字段，Boolean 可能为 null
        buf.writeBoolean(oxygen != null);  // 写入是否为 null
        if (oxygen != null) {
            buf.writeBoolean(oxygen);  // 如果不为 null，则写入值
        }

        // 处理 temperature 字段，Short 可能为 null
        buf.writeShort(temperature != null ? temperature : Short.MIN_VALUE); // 如果为 null，则使用 Short.MIN_VALUE

        // 处理 gravity 字段，Float 可能为 null
        boolean gravityNotNull = gravity != null;
        buf.writeBoolean(gravityNotNull);
        if (gravityNotNull){
            buf.writeFloat(gravity);
        }

        // 处理 tier 字段，Integer 可能为 null
        buf.writeInt(tier != null ? tier : Integer.MIN_VALUE); // 如果为 null，使用 Integer.MIN_VALUE

        // 处理 solarPower 字段，Integer 可能为 null
        buf.writeInt(solarPower != null ? solarPower : Integer.MIN_VALUE);  // 使用 Integer.MIN_VALUE 表示 null
    }
    public static PlanetCreatingData readFromBuffer(PacketByteBuf buf) {
        PlanetCreatingData planetData = new PlanetCreatingData();

        if (buf.readBoolean()) {
            planetData.oxygen = buf.readBoolean();
        }

        short temp = buf.readShort();
        planetData.temperature = (temp == Short.MIN_VALUE) ? null : temp;

        boolean gravityNotNull = buf.readBoolean();
        if (gravityNotNull){
            planetData.gravity = buf.readFloat();
        }

        int tier = buf.readInt();
        planetData.tier = (tier == Integer.MIN_VALUE) ? null : tier;

        int solarPower = buf.readInt();
        planetData.solarPower = (solarPower == Integer.MIN_VALUE) ? null : solarPower;

        return planetData;
    }

    public NbtCompound toNbt(){
        NbtCompound nbt = new NbtCompound();
        if (oxygen != null){
            nbt.putBoolean("oxygen",oxygen);
        }
        if (temperature != null){
            nbt.putShort("temperature",temperature);
        }
        if (tier != null){
            nbt.putInt("temperature",tier);
        }
        if (gravity != null){
            nbt.putFloat("gravity",gravity);
        }
        if (solarPower != null){
            nbt.putInt("solar_power",solarPower);
        }
        return nbt;
    }

    public void fromNbt(NbtCompound nbt){
        if (nbt.contains("oxygen", NbtElement.BYTE_TYPE)){
            oxygen = nbt.getBoolean("oxygen");
        }
        if (nbt.contains("temperature", NbtElement.SHORT_TYPE)){
            temperature = nbt.getShort("temperature");
        }
        if (nbt.contains("tier", NbtElement.INT_TYPE)){
            tier = nbt.getInt("tier");
        }
        if (nbt.contains("gravity", NbtElement.FLOAT_TYPE)){
            gravity = nbt.getFloat("gravity");
        }
        if (nbt.contains("solar_power", NbtElement.INT_TYPE)){
            solarPower = nbt.getInt("solar_power");
        }
    }

    public void fillNullValues(Boolean oxygen, Short temperature, Float gravity, Integer tier, Integer solarPower) {
        if (this.oxygen == null && oxygen != null) {
            this.oxygen = oxygen;
        }
        if (this.temperature == null && temperature != null) {
            this.temperature = temperature;
        }
        if (this.gravity == null && gravity != null) {
            this.gravity = gravity;
        }
        if (this.tier == null && tier != null) {
            this.tier = tier;
        }
        if (this.solarPower == null && solarPower != null) {
            this.solarPower = solarPower;
        }
    }

    public void fillNullValues(Random random, int tier) {
        if (this.oxygen == null) {
            this.oxygen = random.nextBoolean();
        }
        if (this.temperature == null) {
            this.temperature = (short) random.nextBetween(-400,400);
        }
        if (this.gravity == null) {
            this.gravity = random.nextFloat() * 20f;
        }
        if (this.tier == null) {
            this.tier = tier;
        }
        if (this.solarPower == null) {
            this.solarPower = random.nextInt(200);
        }
    }

    public void fromPlanet(Planet planet){
        this.oxygen = planet.oxygen();
        this.temperature = planet.temperature();
        this.gravity = planet.gravity();
        this.tier = planet.tier();
        this.solarPower = planet.solarPower();
    }

    public static PlanetCreatingData generateRandomPlanetData(Random random, int tier){
        PlanetCreatingData planetCreatingData = new PlanetCreatingData();
        planetCreatingData.temperature = (short) random.nextBetween(-400,400);
        planetCreatingData.tier = tier;
        planetCreatingData.oxygen = random.nextBoolean();
        planetCreatingData.gravity = random.nextFloat() * 20f;
        planetCreatingData.solarPower = random.nextInt(200);
        return planetCreatingData;
    }

    @Override
    public String toString() {
        return "PlanetCreatingData{" +
                "oxygen=" + oxygen +
                ", temperature=" + temperature +
                ", gravity=" + gravity +
                ", tier=" + tier +
                ", solarPower=" + solarPower +
                '}';
    }

    public boolean isEmpty(){
        return oxygen == null && temperature == null && gravity == null && tier == null && solarPower == null;
    }
}