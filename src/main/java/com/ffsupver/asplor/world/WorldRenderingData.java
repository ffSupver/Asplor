package com.ffsupver.asplor.world;

import com.ffsupver.asplor.networking.packet.renderingWorld.GetWorldRendererPacketClient;
import com.ffsupver.asplor.networking.packet.renderingWorld.GetWorldRendererPacketServer;
import com.ffsupver.asplor.util.NbtUtil;
import com.ffsupver.asplor.util.RenderUtil;
import dev.architectury.event.events.common.PlayerEvent;
import earth.terrarium.adastra.client.dimension.MovementType;
import earth.terrarium.adastra.client.dimension.PlanetRenderer;
import earth.terrarium.adastra.client.dimension.SkyRenderable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.regex.Pattern;

public class WorldRenderingData {
    private static final Pattern IS_ORBIT = Pattern.compile("\\S+:orbit_\\S*");

    private static final Map<RegistryKey<World>, PlanetRendererData> WORLD_RENDERERS = new HashMap<>();

    @Environment(EnvType.CLIENT)
    public static void registerAll(){
        for (Map.Entry<RegistryKey<World>, WorldRenderingData.PlanetRendererData> world : WORLD_RENDERERS.entrySet()){
            RegistryKey<World> worldKey = world.getKey();

            RenderUtil.checkAndRegisterDimensionEffects(worldKey, WorldRendererGenerator.getOuterSpacePlanetRender(world.getValue()));
        }
    }


    public static void registerOrbit(PlanetRendererData planetRendererData){
        PlanetRenderer planetRenderer = planetRendererData.planetRenderer();
        RegistryKey<World> worldKey = planetRenderer.dimension();
        RegistryKey<World> orbitKey = WorldData.createWorldKey(worldKey);
        if (!WORLD_RENDERERS.containsKey(orbitKey)){
            PlanetRenderer orbitPlanetRenderer = new PlanetRenderer(orbitKey,planetRenderer.customClouds(),planetRenderer.customSky(),planetRenderer.customWeather(),false,
                    false,0,planetRenderer.stars(),planetRenderer.starBrightness(),0,true,
                    planetRenderer.starColors(),planetRenderer.skyRenderables());
//            System.out.println("register orbit "+ (orbitKey.getValue().getNamespace().equals(WorldData.NAMESPACE))+ " \n "+planetRenderer);

            WORLD_RENDERERS.put(orbitKey,new PlanetRendererData(orbitPlanetRenderer,planetRendererData.planetTexture(),planetRendererData.ringTexture()));
        }
    }

    public static void registerPlanetWithOrbit(PlanetRendererData planetRendererData){
        RegistryKey<World> worldKey = planetRendererData.planetRenderer().dimension();

        if (WorldData.isOuterSpaceKey(worldKey)){
            if (isOrbit(worldKey)){
                RegistryKey<World> planetKey = WorldData.getOrbitPlanetWorldKey(worldKey);
                planetRendererData = changePlanetRendererDataWorldKey(planetRendererData,planetKey);
            }
            registerOrbit(planetRendererData);
            register(planetRendererData);
        }
    }

    @NotNull
    private static PlanetRendererData changePlanetRendererDataWorldKey(PlanetRendererData planetRendererData, RegistryKey<World> planetKey) {
        PlanetRenderer planetRenderer = planetRendererData.planetRenderer();
        planetRenderer = new PlanetRenderer(planetKey,planetRenderer.customClouds(),
                planetRenderer.customSky(),planetRenderer.customWeather(), planetRenderer.hasThickFog(),
                planetRenderer.hasFog(),planetRenderer.sunriseColor(),planetRenderer.stars(),
                planetRenderer.starBrightness(),planetRenderer.sunriseAngle(),
                planetRenderer.renderInRain(),planetRenderer.starColors(),planetRenderer.skyRenderables());
        return new PlanetRendererData(planetRenderer,planetRendererData.planetTexture(),planetRendererData.ringTexture());
    }

    public static void register(PlanetRendererData planetRendererData){
        RegistryKey<World> worldKey = planetRendererData.planetRenderer().dimension();
//        System.out.println("register "+ (worldKey.getValue().getNamespace().equals(WorldData.NAMESPACE))+ " \n "+planetRendererData);
        if (!WORLD_RENDERERS.containsKey(worldKey)){
//            System.out.println("put register");
            WORLD_RENDERERS.put(worldKey,planetRendererData);
        }
    }


    
    public static boolean isOrbit(RegistryKey<World> worldKey){
            return IS_ORBIT.matcher(worldKey.getValue().toString()).matches();
    }



    private static void sendOrCreateRenderer(RegistryKey<World> worldKey, ServerPlayerEntity player){
        if (WORLD_RENDERERS.containsKey(worldKey)){
            GetWorldRendererPacketServer.send(WORLD_RENDERERS.get(worldKey),player);
        }else if (WorldData.isOuterSpaceKey(worldKey)){
            Random random = player.getServer().getOverworld().getRandom();
            PlanetRendererData planetRendererData = WorldRendererGenerator.getPlanetRenderer(worldKey,random);
            registerPlanetWithOrbit(planetRendererData);
            GetWorldRendererPacketServer.send(planetRendererData,player);

        }
    }

    public static void registerListener(){
        PlayerEvent.CHANGE_DIMENSION.register((player, oldLevel, newLevel) -> {
//            System.out.println("change  server send "+WORLD_RENDERERS);
            sendOrCreateRenderer(newLevel,player);
        });
        PlayerEvent.PLAYER_JOIN.register(player -> {
            sendOrCreateRenderer(player.getWorld().getRegistryKey(),player);
        });
    }

    public static NbtList saveRenderers(){
//        System.out.println("save "+ WORLD_RENDERERS);
        NbtList result = new NbtList();
        for (Map.Entry<RegistryKey<World>,PlanetRendererData> e : WORLD_RENDERERS.entrySet()){
            NbtCompound planetRendererNbt = e.getValue().toNbt();
            result.add(planetRendererNbt);
        }
        WORLD_RENDERERS.clear();
        return result;
    }

    public static void loadRenderers(NbtList nbtList){
        WORLD_RENDERERS.clear();
        for (NbtElement e : nbtList){
            PlanetRendererData planetRendererData = PlanetRendererData.fromNbt((NbtCompound) e);
            WORLD_RENDERERS.put(planetRendererData.planetRenderer().dimension(),planetRendererData);
        }
    }

    private static NbtCompound planetRendererToNbt(PlanetRenderer planetRenderer){
        NbtCompound nbt = new NbtCompound();
        nbt.putString("id",planetRenderer.dimension().getValue().toString());

        nbt.putBoolean("could",planetRenderer.customClouds());
        nbt.putBoolean("sky",planetRenderer.customSky());
        nbt.putBoolean("weather",planetRenderer.customWeather());
        nbt.putBoolean("thick_fog",planetRenderer.hasThickFog());
        nbt.putBoolean("fog",planetRenderer.hasFog());
        nbt.putInt("sunrise_color",planetRenderer.sunriseColor());
        nbt.putInt("stars",planetRenderer.stars());

        planetRenderer.starBrightness().ifPresent(p->nbt.putFloat("star_brightness",p));
        nbt.putInt("sunrise_angle",planetRenderer.sunriseAngle());
        nbt.putBoolean("rain",planetRenderer.renderInRain());

        NbtList starColorNbtList = new NbtList();
        for (Weighted.Present<Integer> p : planetRenderer.starColors().getEntries()){
            NbtCompound starColor = new NbtCompound();
            starColor.putInt("color",p.getData());
            starColor.putInt("weight",p.getWeight().getValue());
            starColorNbtList.add(starColor);
        }
        nbt.put("star_color",starColorNbtList);


        NbtList skyRenderablesNbtList = new NbtList();
        for (SkyRenderable s : planetRenderer.skyRenderables()){
            NbtCompound sNbt = new NbtCompound();
            sNbt.putString("texture",s.texture().toString());
            sNbt.put("global",NbtUtil.writeVec3dToNbt(s.globalRotation()));
            sNbt.put("local",NbtUtil.writeVec3dToNbt(s.localRotation()));
            sNbt.putFloat("scale",s.scale());
            sNbt.putFloat("back_scale",s.backLightScale());
            sNbt.putInt("back_light",s.backLightColor());
            sNbt.putString("movement_type",s.movementType().name());
            sNbt.putBoolean("blend",s.blend());
            skyRenderablesNbtList.add(sNbt);
        }
        nbt.put("sky_renderable",skyRenderablesNbtList);

        return nbt;
    }

    private static PlanetRenderer planetRendererFromNbt(NbtCompound nbt){
        Identifier dimensionId = new Identifier(nbt.getString("id"));
        RegistryKey<World> dimension = RegistryKey.of(RegistryKeys.WORLD,dimensionId);

        // 反序列化基本类型
        boolean customClouds = nbt.getBoolean("could");
        boolean customSky = nbt.getBoolean("sky");
        boolean customWeather = nbt.getBoolean("weather");
        boolean hasThickFog = nbt.getBoolean("thick_fog");
        boolean hasFog = nbt.getBoolean("fog");
        int sunriseColor = nbt.getInt("sunrise_color");
        int stars = nbt.getInt("stars");

        // 反序列化 Optional<Float>
        Optional<Float> starBrightness =
                nbt.contains("star_brightness", NbtElement.FLOAT_TYPE) ?
                        Optional.of(nbt.getFloat("star_brightness")) : Optional.empty();

        // 反序列化基本类型
        int sunriseAngle = nbt.getInt("sunrise_angle");
        boolean renderInRain = nbt.getBoolean("rain");


        List<Weighted.Present<Integer>> starColorLists = new ArrayList<>();
        for (NbtElement e : nbt.getList("star_color",NbtElement.COMPOUND_TYPE)) {
            NbtCompound n = (NbtCompound) e;
            int color = n.getInt("color");
            int weight = n.getInt("weight");
            Weighted.Present<Integer> weighted = Weighted.of(color,weight);
            starColorLists.add(weighted);
        }
        Pool<Weighted.Present<Integer>> starColors = Pool.of(starColorLists);


        List<SkyRenderable> skyRenderables = new ArrayList<>();
        for (NbtElement e : nbt.getList("sky_renderable",NbtElement.COMPOUND_TYPE)) {
            NbtCompound n = (NbtCompound)e;

            Identifier texture = new Identifier(n.getString("texture"));
            Vec3d globalRotation = NbtUtil.readVec3dFromNbt(n.getCompound("global"));
            Vec3d localRotation = NbtUtil.readVec3dFromNbt(n.getCompound("local"));
            float scale = n.getFloat("scale");
            float backLightScale = n.getFloat("back_scale");
            int backLightColor = n.getInt("back_light");
            MovementType movementType = MovementType.valueOf(n.getString("movement_type"));
            boolean blend = n.getBoolean("blend");
            SkyRenderable skyRenderable = new SkyRenderable(texture, scale, globalRotation, localRotation, movementType,blend,backLightColor,backLightScale);
            skyRenderables.add(skyRenderable);
        }

        return new PlanetRenderer(
                dimension,customClouds,customSky,customWeather,hasThickFog,hasFog,sunriseColor,
                stars, starBrightness,sunriseAngle,renderInRain,starColors,skyRenderables
        );
    }

    public record PlanetRendererData(PlanetRenderer planetRenderer,Identifier planetTexture,@Nullable Identifier ringTexture){
        public PlanetRendererData(PlanetRenderer planetRenderer,Identifier planetTexture,Identifier ringTexture){
            this.ringTexture = ringTexture;
            this.planetRenderer = planetRenderer;
            this.planetTexture = planetTexture == null ? new Identifier("ad_astra:textures/environment/earth.png") : planetTexture;
        }

        public boolean isOrbit(){
            return WorldRenderingData.isOrbit(planetRenderer.dimension());
        }

        public NbtCompound toNbt(){
            NbtCompound nbt = planetRendererToNbt(planetRenderer);
            nbt.putString("planet_texture",planetTexture.toString());
            nbt.putString("ring_texture",ringTexture == null ? "NULL" : ringTexture.toString());
            return nbt;
        }

        public static PlanetRendererData fromNbt(NbtCompound nbt){
            PlanetRenderer planetRenderer = planetRendererFromNbt(nbt);
            Identifier planetTexture = new Identifier(nbt.getString("planet_texture"));
            String ringTextureId = nbt.getString("ring_texture");
            Identifier ringTexture = ringTextureId.equals("NULL")? null : new Identifier(ringTextureId);
            return new PlanetRendererData(planetRenderer,planetTexture,ringTexture);
        }

        public void writeToPacket(PacketByteBuf buf){
            GetWorldRendererPacketServer.toPacketBuf(buf,planetRenderer);
            buf.writeIdentifier(planetTexture);
            boolean hasRing = ringTexture != null;
            buf.writeBoolean(hasRing);
            if (hasRing){
                buf.writeIdentifier(ringTexture);
            }
        }

        public static PlanetRendererData readFromPacket(PacketByteBuf buf){
            PlanetRenderer planetRenderer = GetWorldRendererPacketClient.fromPacketBuf(buf);
            Identifier planetTexture = buf.readIdentifier();
            boolean hasRing = buf.readBoolean();
            Identifier ringTexture = null;
            if (hasRing){
                ringTexture = buf.readIdentifier();
            }
            return new PlanetRendererData(planetRenderer,planetTexture,ringTexture);
        }
    }

}
