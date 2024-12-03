package com.ffsupver.asplor.structure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.structure.PoolStructurePiece;
import net.minecraft.structure.StructureTemplateManager;
import net.minecraft.structure.pool.StructurePool;
import net.minecraft.structure.pool.StructurePoolElement;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.*;
import net.minecraft.world.gen.HeightContext;
import net.minecraft.world.gen.StructureAccessor;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.heightprovider.HeightProvider;
import net.minecraft.world.gen.structure.StructureType;

import java.util.ArrayList;
import java.util.Optional;

public class CrashedGenerator {

    public CrashedGenerator(){}
    public static void adjust(StructureWorldAccess world, BlockBox box, Random random, BlockBox structureBox){
        BlockPos centerPos = structureBox.getCenter();
        ArrayList<Block> blockSample = new ArrayList<>();

        int height = structureBox.getBlockCountY();
        centerPos = centerPos.up(height/2);
        int radius = Math.max(structureBox.getBlockCountX(),structureBox.getBlockCountY())+8;
        BlockPos testSamplePos = centerPos.down(height/2);

        int testTime = 0;
        int testLayer = 1;
        int maxTestTime = radius*radius*radius*10;
        while (blockSample.size() <= 10 && testTime <= maxTestTime){
            testTime++;
            BlockPos samplePos = new BlockPos(testSamplePos.getX()+random.nextInt(radius),testSamplePos.getY()+random.nextInt(radius),testSamplePos.getZ()+random.nextInt(radius));
            BlockState blockSampleState = world.getBlockState(samplePos);
            if(isNotBlockReplaceAble(blockSampleState)){
                blockSample.add(blockSampleState.getBlock());
            }
            if (testTime >= testLayer*radius*radius*radius && testSamplePos.getY() - height >= world.getBottomY()){
                testSamplePos = testSamplePos.down(height);
                testLayer++;
            }
        }
        for (int x = - radius;x <= radius;x++) {
            for (int y = -radius; y <radius*3; y++) {
                for (int z = -radius; z <= radius; z++) {
                    int yTest = y * radius / (height - 4);
                    BlockPos blockPos = centerPos.add(x, y, z);
                    if (box == null || box.contains(blockPos)) {
                        if (x * x + yTest * yTest + z * z <= radius * radius || (x * x + z * z <= radius * radius && y > 0)) {

                            if (!world.getBlockState(blockPos).isAir() && (box != null || !structureBox.contains(blockPos))){
                                world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), 3);
                            }

                        }
                        int spread = Math.max(3,random.nextInt(radius/2));
                        if (x * x + z * z >= radius * radius && x * x + z * z <= (radius + spread) * (radius +spread) && y <= height/2) {
                            BlockState testState = world.getBlockState(blockPos.down());
                            BlockState thisState = world.getBlockState(blockPos);
                            float randomFloat = random.nextFloat();
                            if (isNotBlockReplaceAble(testState) && !isNotBlockReplaceAble(thisState) && randomFloat < 0.55 && !blockSample.isEmpty()) {
                                Block block = blockSample.get(random.nextInt(blockSample.size()-1));
                                world.setBlockState(blockPos, block.getDefaultState(), 3);
                            }
                        }
                    }
                }
            }
        }

    }

    private static BlockBox getAvailableBox(BlockBox originalBox,BlockBox structureBox){
        BlockBox boxToAdjust = originalBox;
        if (originalBox.getMaxX() >= structureBox.getMaxX()){
            boxToAdjust = modifyBox(boxToAdjust,16,0,0);
        }
        if (originalBox.getMinX() <= structureBox.getMinX()){
            boxToAdjust = modifyBox(boxToAdjust,-16,0,0);
        }
        if (originalBox.getMaxZ() >= structureBox.getMaxZ()){
            boxToAdjust = modifyBox(boxToAdjust,0,0,16);
        }
        if (originalBox.getMinZ() <= structureBox.getMinZ()){
            boxToAdjust = modifyBox(boxToAdjust,0,0,-16);
        }
        return boxToAdjust;
    }

    private static BlockBox modifyBox(BlockBox box,int x,int y,int z){
        return new BlockBox(box.getMinX() + (Math.min(x, 0)),box.getMinY()+(Math.min(y, 0)),box.getMinZ()+(Math.min(z, 0)),
                box.getMaxX()+Math.max(0,x),box.getMaxY()+Math.max(0,y),box.getMaxZ()+Math.max(0,z));
    }

    private static boolean isNotBlockReplaceAble(BlockState blockState){
        return !blockState.isAir() && !blockState.isIn(BlockTags.LEAVES) && !blockState.isReplaceable() && !blockState.isIn(BlockTags.FLOWERS);
    }


    public static class Piece extends PoolStructurePiece{

        public Piece(StructureTemplateManager structureTemplateManager, StructurePoolElement poolElement, BlockPos pos, int groundLevelDelta, BlockRotation rotation, BlockBox boundingBox) {
            super(structureTemplateManager, poolElement, pos, groundLevelDelta, rotation, boundingBox);
        }


        @Override
        public void generate(StructureWorldAccess world, StructureAccessor structureAccessor, ChunkGenerator chunkGenerator, Random random, BlockBox boundingBox, BlockPos pivot, boolean keepJigsaws) {
            BlockBox box = getAvailableBox(boundingBox,this.boundingBox);
            adjust(world,box,random,this.boundingBox);
            super.generate(world, structureAccessor, chunkGenerator, random, boundingBox, pivot, keepJigsaws);
        }


    }






    public static class Structure extends net.minecraft.world.gen.structure.Structure{
    public static final StructureType<Structure> CODEC = () -> RecordCodecBuilder.create(instance ->
            instance.group(
                    configCodecBuilder(instance),
                    StructurePool.REGISTRY_CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    Identifier.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 7).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Codec.BOOL.fieldOf("use_expansion_hack").forGetter(structure -> structure.useExpansionHack),
                    Heightmap.Type.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter)
            ).apply(instance, Structure::new));


    private final RegistryEntry<StructurePool> startPool;
        private final Optional<Identifier> startJigsawName;
        private final int size;
        private final HeightProvider startHeight;
        private final boolean useExpansionHack;
        private final Optional<Heightmap.Type> projectStartToHeightmap;
        private final int maxDistanceFromCenter;

        protected Structure(Config config, RegistryEntry<StructurePool> startPool, Optional<Identifier> startJigsawName, int size, HeightProvider startHeight, boolean useExpansionHack, Optional<Heightmap.Type> projectStartToHeightmap, int maxDistanceFromCenter) {
        super(config);
        this.startPool = startPool;
            this.startJigsawName = startJigsawName;
            this.size = size;
            this.startHeight = startHeight;
            this.useExpansionHack = useExpansionHack;
            this.projectStartToHeightmap = projectStartToHeightmap;
            this.maxDistanceFromCenter = maxDistanceFromCenter;
        }


        @Override
        protected Optional<StructurePosition> getStructurePosition(Context context) {
            ChunkPos chunkPos = context.chunkPos();



            BlockRotation rotation = BlockRotation.random(context.random());
            StructurePoolElement poolElement = startPool.value().getRandomElement(context.random());
            int x = chunkPos.getCenterX();
            int z = chunkPos.getCenterZ();
            int y = context.chunkGenerator().getHeightInGround(x, z, Heightmap.Type.WORLD_SURFACE_WG, context.world(), context.noiseConfig())
                    +this.startHeight.get(context.random(), new HeightContext(context.chunkGenerator(),context.world()));
            BlockPos pos = new BlockPos(x,y,z);
            BlockBox structureBox = poolElement.getBoundingBox(context.structureTemplateManager(),pos,rotation);

            if (context.world() instanceof ServerWorld serverWorld){
                adjust(serverWorld,null,context.random(),structureBox);
            }


            return Optional.of(new StructurePosition(pos,structurePiecesCollector -> {
                PoolStructurePiece newPiece = new Piece(
                        context.structureTemplateManager(),
                        poolElement,
                        pos,
                        poolElement.getGroundLevelDelta(),
                        rotation,
                        poolElement.getBoundingBox(context.structureTemplateManager(), pos, rotation)
                );
                structurePiecesCollector.addPiece(newPiece);
            })) ;
        }






    @Override
    public StructureType<?> getType() {
        return ModStructureTypes.CRASHED_STRUCTURE_TYPE;
    }
}
}
