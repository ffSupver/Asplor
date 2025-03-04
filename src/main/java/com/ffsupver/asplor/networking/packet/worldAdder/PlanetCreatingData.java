package com.ffsupver.asplor.networking.packet.worldAdder;

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

    public static PlanetCreatingData generateRandomPlanetData(Random random, int tier){
        PlanetCreatingData planetCreatingData = new PlanetCreatingData();
        planetCreatingData.temperature = (short) random.nextBetween(-400,400);
        planetCreatingData.tier = tier;
        planetCreatingData.oxygen = random.nextBoolean();
        planetCreatingData.gravity = random.nextFloat() * 20f;
        planetCreatingData.solarPower = random.nextInt(200);
        return planetCreatingData;
    }
}