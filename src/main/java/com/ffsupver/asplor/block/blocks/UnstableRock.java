package com.ffsupver.asplor.block.blocks;

import com.ffsupver.asplor.ModTags;
import earth.terrarium.adastra.common.registry.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class UnstableRock extends Block {
    public static final IntProperty STAGE = IntProperty.of("stage",0,3);
    public static final EnumProperty<TurnEnvironmentType> TYPE = EnumProperty.of("type",TurnEnvironmentType.class);

    public UnstableRock(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState().with(STAGE,0).with(TYPE,TurnEnvironmentType.MOON));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(STAGE,TYPE);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (random.nextInt(2) == 0) {
            turn(world,pos,state);
        }
    }

    public boolean turn(World world, BlockPos pos, BlockState state){
        RegistryEntry<Biome> biome = world.getBiome(pos);
        int stage = state.get(STAGE);
        boolean updated = false;
        if (stage <= 0){
           updated = updateEnvironment(world,pos,state,biome);
        }

        TurnEnvironmentType type = state.get(TYPE);

        if (type.canTurn(biome)){
            BlockState newState;
            if (state.get(STAGE) != 3) {
                newState = state.with(STAGE, state.get(STAGE) + 1);
            } else {
                newState = type.targetState;
            }
            world.setBlockState(pos, newState, 3);
            return true;
        }
        return updated;
    }

    public boolean updateEnvironment(World world, BlockPos pos, BlockState state,RegistryEntry<Biome> biome){
        TurnEnvironmentType type = null;
        for (int i = 0;i < TurnEnvironmentType.values().length;i++){
            TurnEnvironmentType checkType = TurnEnvironmentType.values()[i];
            if (checkType.canTurn(biome)){
                type = checkType;
                break;
            }
        }
        if (type != null && !type.equals(state.get(TYPE))){
            world.setBlockState(pos, state.with(TYPE,type));
            return true;
        }
        return false;
    }



    @Override
    public boolean hasRandomTicks(BlockState state) {
        return true;
    }

    public enum TurnEnvironmentType implements StringIdentifiable {
        MOON("moon",ModTags.Biomes.MOON, ModBlocks.MOON_DESH_ORE.get().getDefaultState())
        ,MARS("mars",ModTags.Biomes.MARS, ModBlocks.MARS_OSTRUM_ORE.get().getDefaultState());
        public final String name;
        public final TagKey<Biome> matchBiomes;
        public final BlockState targetState;
        TurnEnvironmentType(String name,TagKey<Biome> matchBiomes, BlockState targetState){
            this.name = name;
            this.matchBiomes = matchBiomes;
            this.targetState = targetState;
        }

        public boolean canTurn(RegistryEntry<Biome> biome){
            return biome.isIn(matchBiomes);
        }


        @Override
        public String asString() {
            return name;
        }
    }
}
