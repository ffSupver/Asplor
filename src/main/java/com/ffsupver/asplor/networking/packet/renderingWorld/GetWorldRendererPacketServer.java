package com.ffsupver.asplor.networking.packet.renderingWorld;

import com.ffsupver.asplor.networking.ModPackets;
import com.ffsupver.asplor.world.WorldRenderingData;
import earth.terrarium.adastra.client.dimension.PlanetRenderer;
import earth.terrarium.adastra.client.dimension.SkyRenderable;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;

public class GetWorldRendererPacketServer {

    public static void send(WorldRenderingData.PlanetRendererData planetRenderer, ServerPlayerEntity serverPlayer){
        PacketByteBuf buf = PacketByteBufs.create();

        planetRenderer.writeToPacket(buf);

        ServerPlayNetworking.send(serverPlayer, ModPackets.GET_WORLD_RENDERER_SYNC,buf);
    }

    public static void toPacketBuf(PacketByteBuf buf,PlanetRenderer planetRenderer) {
        Identifier dimensionId = planetRenderer.dimension().getValue();
        buf.writeIdentifier(dimensionId);


        buf.writeBoolean(planetRenderer.customClouds());
        buf.writeBoolean(planetRenderer.customSky());
        buf.writeBoolean(planetRenderer.customWeather());
        buf.writeBoolean(planetRenderer.hasThickFog());
        buf.writeBoolean(planetRenderer.hasFog());
        buf.writeInt(planetRenderer.sunriseColor());
        buf.writeInt(planetRenderer.stars());


        if (planetRenderer.starBrightness().isPresent()) {
            buf.writeBoolean(true);
            buf.writeFloat(planetRenderer.starBrightness().get());
        } else {
            buf.writeBoolean(false);
        }


        buf.writeInt(planetRenderer.sunriseAngle());
        buf.writeBoolean(planetRenderer.renderInRain());

        Pool<Weighted.Present<Integer>> stars = planetRenderer.starColors();
        buf.writeInt(stars.getEntries().size());
        for (Weighted.Present<Integer> present :stars.getEntries()){
            buf.writeInt(present.getData());
            buf.writeInt(present.getWeight().getValue());
        }


        int skyIndex = planetRenderer.skyRenderables().size();
        buf.writeInt(skyIndex);
        for (int i = 0;i < skyIndex;i++){
            SkyRenderable skyRenderable = planetRenderer.skyRenderables().get(i);
            buf.writeIdentifier(skyRenderable.texture());
            buf.writeVector3f(skyRenderable.globalRotation().toVector3f());
            buf.writeVector3f(skyRenderable.localRotation().toVector3f());
            buf.writeFloat(skyRenderable.scale());
            buf.writeFloat(skyRenderable.backLightScale());
            buf.writeInt(skyRenderable.backLightColor());
            buf.writeEnumConstant(skyRenderable.movementType());
            buf.writeBoolean(skyRenderable.blend());
        }

    }

}