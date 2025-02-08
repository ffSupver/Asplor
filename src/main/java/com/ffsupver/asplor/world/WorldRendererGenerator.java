package com.ffsupver.asplor.world;

import com.ffsupver.asplor.Asplor;
import earth.terrarium.adastra.client.dimension.MovementType;
import earth.terrarium.adastra.client.dimension.PlanetRenderer;
import earth.terrarium.adastra.client.dimension.SkyRenderable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.Pool;
import net.minecraft.util.collection.Weighted;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class WorldRendererGenerator{
    private static final List<Star> STARS = List.of(
            adAstraStar("deimos",-0x172641),
            adAstraStar("earth",-12813094),
            adAstraStar("glacio",-3221524),
            adAstraStar("mars",-4827861),
            adAstraStar("mercury",-5543543),
            adAstraStar("moon",-5261108),
            adAstraStar("phobos",-0x1C456C),
            adAstraStar("venus",-801674),
            adAstraStar("vicinus",-6861640),
            newStar("the_nether",-4375778),
            newStar("earth_0",-12813094),
            newStar("earth_1",-12813094),
            newStar("earth_2",-12813094),
            newStar("earth_3",-12813094),
            newStar("jupiter",-0x185297),
            newStar("saturn",-0x4F5BAE)
    );
    private final static List<Star> SUNS = List.of(
            adAstraStar("sun",-0x0a0a0a),
            adAstraStar("blue_sun",-0x3E0004),
            adAstraStar("red_sun",-0x005252)
    );
    private final static List<Identifier> RINGS = List.of(
            getRingTexture("yellow"),
            getRingTexture("blue")
    );
    private static Identifier getRingTexture(String name){
        return new Identifier(Asplor.MOD_ID,"textures/environment/ring/"+name+".png");
    }

    public static WorldRenderingData.PlanetRendererData getPlanetRenderer(RegistryKey<World> worldKey, Random random){
        List<SkyRenderable> stars = new ArrayList<>();
        for (int i = 0; i < random.nextBetween(4,8); i++) {
            stars.add(generateStar(random));
        }

        stars.add(generateSun(random));
        PlanetRenderer planetRenderer = new PlanetRenderer(worldKey,true,true,true,false,true,
                0xED8132,0X008811, Optional.of(0.6f),60,true, generateStarColors(random), stars);
        return new WorldRenderingData.PlanetRendererData(planetRenderer,getRandomPlanet(random), generateRing(random));
    }

    private static Pool<Weighted.Present<Integer>> generateStarColors(Random random){
        List<Weighted.Present<Integer>> colors = new ArrayList<>();
        for (int i = 0; i < random.nextBetween(8,16); i++) {
            int r = random.nextInt(4) == 0 ? random.nextBetween(0,127) : random.nextBetween(0,64);
            int b = random.nextInt(4) == 0 ? random.nextBetween(0,127) : random.nextBetween(0,64);
            int g = random.nextInt(4) == 0 ? random.nextBetween(0,127) : random.nextBetween(0,64);
            int color = -(r * 65536 + b * 256 + g);
            int weight = random.nextInt(100) + 1;
            colors.add(Weighted.of(color,weight));

        }
        return Pool.of(colors);
    }

    public static SkyRenderable generateStar(Random random){
        float scale = random.nextFloat() * random.nextFloat() * random.nextFloat() * 30f;
        Vec3d globalRotation = generatorVec3d(random).multiply(180);
        Vec3d localRotation = new Vec3d(0,360 * random.nextFloat(),0);
        Star star = STARS.get(random.nextInt(STARS.size()));
        Identifier texture = star.texture();
        int backgroundColor = star.backgroundColor();
        MovementType movementType = MovementType.values()[random.nextInt(MovementType.values().length)];

        return new SkyRenderable(texture,scale,globalRotation,localRotation,movementType,backgroundColor);
    }

    public static SkyRenderable generateSun(Random random){
        float scale = random.nextFloat() * 30f + 10f;
        Vec3d globalRotation = new Vec3d(0,360 * random.nextFloat(),0);
        Vec3d localRotation = new Vec3d(0,360 * random.nextFloat(),0);
        Star star = SUNS.get(random.nextInt(SUNS.size()));
        Identifier texture = star.texture();
        int backgroundColor = star.backgroundColor();
        MovementType movementType = MovementType.TIME_OF_DAY;

        return new SkyRenderable(texture,scale,globalRotation,localRotation,movementType,backgroundColor);
    }
    private static Identifier generateRing(Random random){
        return random.nextInt(3) == 0 ? RINGS.get(random.nextInt(RINGS.size())) : null;
    }

    private static Identifier getRandomPlanet(Random random){
        return STARS.get(random.nextInt(STARS.size())).texture();
    }


    public static Vec3d generatorVec3d(Random random){
        return new Vec3d(random.nextFloat(),random.nextFloat(),random.nextFloat());
    }

    public static OuterSpacePlanetRender getOuterSpacePlanetRender(WorldRenderingData.PlanetRendererData planetRendererData){
        return new OuterSpacePlanetRender(planetRendererData);
    }

    private static Star adAstraStar(String name,int color){
        return new Star(adAstraTexture(name),color);
    }
    private static Star newStar(String name,int color){
        return new Star(environmentTexture(name),color);
    }

    private static Identifier adAstraTexture(String name){
        return new Identifier("ad_astra:textures/environment/"+name+".png");
    }
    private static Identifier environmentTexture(String name){
        return new Identifier(Asplor.MOD_ID,"textures/environment/"+name+".png");
    }

    private record Star(Identifier texture, int backgroundColor) {
    }
}
