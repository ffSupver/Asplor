package com.ffsupver.asplor.networking.packet.renderingWorld;

import com.ffsupver.asplor.world.WorldRenderingData;
import earth.terrarium.adastra.client.dimension.MovementType;
import earth.terrarium.adastra.client.dimension.PlanetRenderer;
import earth.terrarium.adastra.client.dimension.SkyRenderable;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ffsupver.asplor.world.WorldRenderingData.registerAll;

public class GetWorldRendererPacketClient {

    public static void receive(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender packetSender) {
        WorldRenderingData.PlanetRendererData planetRendererData = WorldRenderingData.PlanetRendererData.readFromPacket(buf);

        client.execute(() ->{
            System.out.println("client rec " + planetRendererData.planetRenderer().skyRenderables());

            WorldRenderingData.registerPlanetWithOrbit(planetRendererData);
            registerAll();
        });
    }



        public static PlanetRenderer fromPacketBuf(PacketByteBuf buf) {

        Identifier dimensionId = buf.readIdentifier();
        RegistryKey<World> dimension = RegistryKey.of(RegistryKeys.WORLD,dimensionId);


        boolean customClouds = buf.readBoolean();
        boolean customSky = buf.readBoolean();
        boolean customWeather = buf.readBoolean();
        boolean hasThickFog = buf.readBoolean();
        boolean hasFog = buf.readBoolean();
        int sunriseColor = buf.readInt();
        int stars = buf.readInt();


        Optional<Float> starBrightness = buf.readBoolean() ? Optional.of(buf.readFloat()) : Optional.empty();


        int sunriseAngle = buf.readInt();
        boolean renderInRain = buf.readBoolean();

        int starColorSize = buf.readInt();
        List<Weighted.Present<Integer>> starColorLists = new ArrayList<>();
        for (int i = 0; i < starColorSize; i++) {
            int color = buf.readInt();
            int weight = buf.readInt();
            Weighted.Present<Integer> weighted = Weighted.of(color,weight);
            starColorLists.add(weighted);
        }
        Pool<Weighted.Present<Integer>> starColors = Pool.of(starColorLists);

        int skySize = buf.readInt();
        List<SkyRenderable> skyRenderables = new ArrayList<>();
        for (int i = 0; i < skySize; i++) {
            Identifier texture = buf.readIdentifier();
            Vec3d globalRotation = new Vec3d(buf.readVector3f());
            Vec3d localRotation = new Vec3d(buf.readVector3f());
            float scale = buf.readFloat();
            float backLightScale = buf.readFloat();
            int backLightColor = buf.readInt();
            MovementType movementType = buf.readEnumConstant(MovementType.class);
            boolean blend = buf.readBoolean();
            SkyRenderable skyRenderable = new SkyRenderable(texture, scale, globalRotation, localRotation, movementType,blend,backLightColor,backLightScale);
            skyRenderables.add(skyRenderable);
        }


        // 返回新的 PlanetRenderer 实例
        return new PlanetRenderer(dimension, customClouds, customSky, customWeather, hasThickFog, hasFog, sunriseColor, stars, starBrightness, sunriseAngle, renderInRain, starColors, skyRenderables);
    }
}
