package com.ffsupver.asplor.planet;

import com.ffsupver.asplor.Asplor;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.teamresourceful.resourcefullib.common.lib.Constants;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.world.World;
import org.slf4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PlanetData extends JsonDataLoader {
    private static final Map<RegistryKey<World>, PlanetEnvironment> PLANET_ENVIRONMENTS = new HashMap<>();
    public PlanetData() {
        super(Constants.GSON, "planets");
    }

    public static boolean isCharged(RegistryKey<World> worldKey){
        if (PLANET_ENVIRONMENTS.containsKey(worldKey)){
            return PLANET_ENVIRONMENTS.get(worldKey).charged();
        }
        return false;
    }


    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        PLANET_ENVIRONMENTS.clear();
        prepared.forEach((key,value)->{
            JsonObject json = JsonHelper.asObject(value, "planets");
            DataResult result = PlanetEnvironment.CODEC.parse(JsonOps.INSTANCE,json);
            Logger logger = Asplor.LOGGER;
            Objects.requireNonNull(logger);
            PlanetEnvironment planetEnvironment = (PlanetEnvironment) result.getOrThrow(false,(object)->logger.error(object.toString()));
            PLANET_ENVIRONMENTS.put(planetEnvironment.dimension(),planetEnvironment);
        });
    }

    public record PlanetEnvironment(RegistryKey<World> dimension,boolean charged) {
       public static Codec<PlanetEnvironment> CODEC = RecordCodecBuilder.create((instance) -> {
           return instance.group(RegistryKey.createCodec(RegistryKeys.WORLD).fieldOf("dimension").forGetter(PlanetEnvironment::dimension),
                           Codec.BOOL.optionalFieldOf("charged",false).forGetter(PlanetEnvironment::charged))
                   .apply(instance,PlanetEnvironment::new);
       });

        public RegistryKey<World> dimension() {
            return this.dimension;
        }

        @Override
        public boolean charged() {
            return charged;
        }
    }
}
