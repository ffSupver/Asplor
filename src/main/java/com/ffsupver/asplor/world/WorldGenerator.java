package com.ffsupver.asplor.world;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.Asplor;
import com.ffsupver.asplor.util.MathUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.densityfunction.DensityFunction;
import net.minecraft.world.gen.densityfunction.DensityFunctionTypes;
import net.minecraft.world.gen.noise.NoiseRouter;
import net.minecraft.world.gen.surfacebuilder.MaterialRules;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.ffsupver.asplor.world.WorldGenerator.DensityFunctionBuilder.of;

public class WorldGenerator {
    private static final Pattern IS_NUMBER = Pattern.compile("-?\\d+(\\.\\d+)?");
    private static final Pattern IS_NEGATIVE_NUMBER = Pattern.compile("-\\d+(\\.\\d+)?");
    private static final Pattern HAS_BLOCK_STATE = Pattern.compile("^(.*?)(?:\\[(.*?)])?$");

    private static final List<RandomDensityFunctionGenerator> DENSITY_FUNCTIONS = List.of(
            new RandomDensityFunctionGenerator(List.of("start", "add", "minecraft:overworld/depth", "add", "-0.5", "mul", "start", "shift_noise", "0.25","minecraft:cave_cheese", "stop", "add", "0.25", "min", "start", "add", "minecraft:overworld/depth", "mul", "0.5", "add", "0.25", "stop", "stop"),
                    List.of(RandomDensityFunctionGenerator.createProcessor(4,-0.3f,-0.7f),RandomDensityFunctionGenerator.createProcessor(8,0.1f,0.6f),RandomDensityFunctionGenerator.createProcessor(12,0.0f,0.25f),RandomDensityFunctionGenerator.createProcessor(20,0.0f,0.3f))
            ),
            new RandomDensityFunctionGenerator(List.of( "start", "add", "minecraft:overworld/depth", "add","-0.5", "mul", "start", "noise_in_range", "0.25", "1", "-1", "1", "minecraft:cave_cheese", "stop", "add", "0.25", "min", "start", "add", "minecraft:overworld/depth", "mul", "0.5", "add", "0.25", "stop", "stop"),
                    List.of(RandomDensityFunctionGenerator.createProcessor(8,0.1f,0.6f),RandomDensityFunctionGenerator.createProcessor(9,0.8f,1.2f),RandomDensityFunctionGenerator.createProcessor(15,0.0f,0.25f),RandomDensityFunctionGenerator.createProcessor(23,0.0f,0.4f))
            ),
            new RandomDensityFunctionGenerator(List.of( "start", "add", "minecraft:overworld/depth", "mul", "minecraft:overworld/factor", "add", "0.1", "max", "start", "add", "minecraft:overworld/depth", "mul", "start", "add", "0", "max", "start", "add", "minecraft:overworld/ridges_folded", "mul", "-1", "stop", "stop", "add", "-0.25", "stop", "min", "start", "noise", "minecraft:cave_cheese", "add", "0.5", "stop", "stop"),
                        List.of(RandomDensityFunctionGenerator.createProcessor(6,0.0f,0.2f),RandomDensityFunctionGenerator.createProcessor(14,0.0f,1.0f),RandomDensityFunctionGenerator.createProcessor(24,-0.5f,-0.0f),RandomDensityFunctionGenerator.createProcessor(31,0.0f,0.4f))
                    ),
            new RandomDensityFunctionGenerator(
                    List.of( "start", "noise_in_range", "0.25", "1", "1", "-1", "clay_bands_offset", "noise_in_range", "2", "1", "1", "-1", "clay_bands_offset", "min", "start", "add", "minecraft:overworld/depth", "stop", "max", "start", "add", "minecraft:overworld/depth", "add", "-0.5", "stop", "stop"),
                    List.of(RandomDensityFunctionGenerator.createProcessor(2,0.1f,0.6f),RandomDensityFunctionGenerator.createProcessor(3,0.8f,1.2f),RandomDensityFunctionGenerator.createProcessor(8,1f,3f),RandomDensityFunctionGenerator.createProcessor(9,0.8f,1.2f),RandomDensityFunctionGenerator.createProcessor(23,-1.0f,-0.5f))
            )
    );

//    public static ChunkGeneratorSettings getGeneratorSettings(MinecraftServer server){
//        return getGeneratorSettings(server,new ArrayList<>(),List.of());
//    }
    public static ChunkGeneratorSettings getGeneratorSettings(MinecraftServer server, ArrayList<String> functionList, List<String> blockList, List<RegistryKey<Biome>> biomes){
        DynamicRegistryManager.Immutable registryManager = server.getRegistryManager();
        ChunkGeneratorSettings overWorldSettings = registryManager.get(RegistryKeys.CHUNK_GENERATOR_SETTINGS).get(ChunkGeneratorSettings.OVERWORLD);


        DensityFunction zero = DensityFunctionTypes.zero();

        DensityFunctionBuilder finalDensityBuilder =
                of(server, functionList)
                        .max(
                                new DensityFunctionBuilder(server)
                                        .yClampedGradient(-63,-60,1,0)
                        );

        if (functionList.isEmpty()){
            Random random = server.getOverworld().getRandom();
            ArrayList<Integer> weight = new ArrayList<>();
            for (int i = 0; i < DENSITY_FUNCTIONS.size(); i++) {
                weight.add(random.nextInt(100));
            }
            int sum = 0;
            for (int w : weight) {
                sum += w;
            }
            functionList.add("interpolated");
            functionList.add("start");
            for (int i = 0; i < DENSITY_FUNCTIONS.size(); i++) {
                functionList.add("add");
                functionList.add("start");
                functionList.add("add");
                functionList.addAll(DENSITY_FUNCTIONS.get(i).applyProcessor(random));
                functionList.add("mul");
                functionList.add(String.valueOf((float)weight.get(i) / sum));

                functionList.add("stop");

            }
            functionList.add("stop");

            finalDensityBuilder = of(server,functionList)
                    .yClampedGradient(-63,-48,1,0);
        }


        DensityFunction finalDensity = finalDensityBuilder.build();

        ArrayList<BlockState> blockStates = new ArrayList<>(List.of(
                AllBlocks.FLINT_BLOCK.getDefaultState(), Blocks.WATER.getDefaultState(), Blocks.DIAMOND_BLOCK.getDefaultState())
        );
        for (int i = 0;i < blockList.size(); i++){
            String blockStateCode = blockList.get(i);
            Matcher hasBlockStateResult = HAS_BLOCK_STATE.matcher(blockStateCode);

            Block block = Registries.BLOCK.get(new Identifier(hasBlockStateResult.matches() ? hasBlockStateResult.group(1) : blockStateCode));
            if (block != Blocks.AIR){
                BlockState blockState = block.getDefaultState();
                if (i < blockStates.size()){
                    blockStates.set(i, blockState);
                }else {
                    blockStates.add(blockState);
                }
            }

        }

        MaterialRules.MaterialRule biomeRule = BiomesSupplier.generateMaterialRules(BiomesSupplier.BIOMES_LIST,blockStates,biomes);
        MaterialRules.MaterialRule baseRule =  MaterialRules.condition(
                MaterialRules.aboveYWithStoneDepth(YOffset.aboveBottom(128),-3),
                MaterialRules.condition(
                        MaterialRules.stoneDepth(0,true,0, VerticalSurfaceType.FLOOR),
                        MaterialRules.block(blockStates.get(2))
                )
        );

        ChunkGeneratorSettings newSettings =   new ChunkGeneratorSettings(
                overWorldSettings.generationShapeConfig(),
                blockStates.get(0),
                blockStates.get(1),
                new NoiseRouter(
                        overWorldSettings.noiseRouter().barrierNoise(),
                        zero,zero,zero,
                        new DensityFunctionBuilder(server).shiftNoise(0.25,"temperature").build(),
                        new DensityFunctionBuilder(server).shiftNoise(0.25,"vegetation").build(),
                        new DensityFunctionBuilder(server).add("minecraft:overworld/continents").build(),
                        new DensityFunctionBuilder(server).add("minecraft:overworld/erosion").build(),
                        new DensityFunctionBuilder(server).add("minecraft:overworld/depth").build(),
                        new DensityFunctionBuilder(server).add("minecraft:overworld/ridges").build(),
                        finalDensityBuilder.add(-0.5).build(),
                        finalDensity,
                        zero,zero,zero
                ),
//                overWorldSettings.noiseRouter(),
                MaterialRules.sequence(
                        MaterialRules.condition(
                                MaterialRules.verticalGradient("minecraft:bedrock_floor", YOffset.aboveBottom(0),YOffset.aboveBottom(5)),
                                MaterialRules.block(Blocks.BEDROCK.getDefaultState())
                        ),
                        biomeRule == null ?
                                MaterialRules.sequence(baseRule) :
                                MaterialRules.sequence(biomeRule, baseRule)
                ),
//                overWorldSettings.surfaceRule(),
                overWorldSettings.spawnTarget(),
                overWorldSettings.seaLevel(),
                overWorldSettings.mobGenerationDisabled(),
                overWorldSettings.aquifers(),
                overWorldSettings.oreVeins(),
                overWorldSettings.usesLegacyRandom()
        );
        return newSettings;
    }





    private static Pair<DensityFunctionBuilder, Integer> getNextFunction(MinecraftServer server, List<String> functionList, int index){
        String function = functionList.get(index);
        if (function.equals("start")) {
            ArrayList<String> minList = new ArrayList<>();
            int startCount = 0;
            for (int j = 1;; j++) {
                String nextFunction = functionList.get(index + j);

                if (startCount <= 0 && functionList.get(index + j).equals("stop") || functionList.size() - 1 < index + j){
                    break;
                }

                if (nextFunction.equals("start")){
                    startCount += 1;
                }
                if (nextFunction.equals("stop")){
                    startCount -= 1;
                }
                minList.add(nextFunction);
            }

            index += minList.size() +2;
            if (!minList.isEmpty()){
                return new  Pair<>(of(server, minList),index);
            }else {
                Asplor.LOGGER.error("No function inside start-stop "+index);
            }
        }


        index += 1;
        Optional<Double> functionNumberOptional = getNextNumber(server,function);
        if (functionNumberOptional.isPresent()) {
            double functionNumber = functionNumberOptional.get();
            return new Pair<>(new DensityFunctionBuilder(server).add(functionNumber),index);
        }else{
            return new  Pair<>(new DensityFunctionBuilder(server).add(function),index);
        }
    }

    private static Optional<Double> getNextNumber(MinecraftServer server,String function){
        Matcher matcher = IS_NUMBER.matcher(function);
        if (matcher.matches()){
            boolean negative = IS_NEGATIVE_NUMBER.matcher(function).matches();
            String functionNumberString = negative ? function.replace('-', '0') : function;
            double functionNumber = (negative ? -1 : 1) * (
                    functionNumberString.contains(".") ?
                            Double.parseDouble(functionNumberString) : Integer.parseInt(functionNumberString)
            );
            return Optional.of(functionNumber);
        }
        return Optional.empty();
    }

    public static class DensityFunctionBuilder{
        private static final List<String> FIRST_COMMAND = List.of("add","mul","noise","shift_noise","max","min","interpolated","y_clamped_gradient","noise_in_range");
        private final MinecraftServer server;
        private final DynamicRegistryManager.Immutable registryManager;
        private DensityFunction function;

        private DensityFunctionBuilder(MinecraftServer server) {
            this.server = server;
            this.function = DensityFunctionTypes.zero();
            this.registryManager = server.getRegistryManager();
        }



        public static DensityFunction getFunctionById(MinecraftServer server,String id){
            DynamicRegistryManager.Immutable registryManager = server.getRegistryManager();
            Registry<DensityFunction> densityFunctionRegistry = registryManager.get(RegistryKeys.DENSITY_FUNCTION);

            DensityFunction result = densityFunctionRegistry.get(RegistryKey.of(RegistryKeys.DENSITY_FUNCTION, new Identifier(id)));

            if (result == null){
                throw new RuntimeException("No Density Function found : " + id);
            }

            return  result;
        }

        public DensityFunctionBuilder add(DensityFunction addFunction){
            this.function = DensityFunctionTypes.add(this.function,addFunction);
            return this;
        }

        public DensityFunctionBuilder add(String id){
            return this.add(getFunctionById(server,id));
        }
        public DensityFunctionBuilder add(DensityFunctionBuilder builder){
            return this.add(builder.build());
        }

        public DensityFunctionBuilder add(double constant){
            return this.add(DensityFunctionTypes.constant(constant));
        }

        public DensityFunctionBuilder yClampedGradient(int fromY,int toY,double from,double to){
            return this.add(DensityFunctionTypes.yClampedGradient(fromY,toY,from,to));
        }
        public DensityFunctionBuilder yClampedGradient(int fromY,int toY,DensityFunctionBuilder from,DensityFunctionBuilder to){
            DensityFunction fromToFunction = DensityFunctionTypes.yClampedGradient(fromY,toY,1,0);
            DensityFunction toFromFunction = DensityFunctionTypes.yClampedGradient(fromY,toY,0,1);
            return this.add(from.mul(fromToFunction).add(to.mul(toFromFunction)));
        }


        public DensityFunctionBuilder mul(DensityFunction function){
            this.function = DensityFunctionTypes.mul(this.function,function);
            return this;
        }
        public DensityFunctionBuilder mul(DensityFunctionBuilder functionBuilder){
            return mul(functionBuilder.build());
        }
        public DensityFunctionBuilder mul(String id){
            return mul(getFunctionById(server,id));
        }
        public DensityFunctionBuilder mul(double constant){
            return mul(DensityFunctionTypes.constant(constant));
        }


        public RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> getNoiseParameters(String id){
            return registryManager.get(RegistryKeys.NOISE_PARAMETERS).entryOf(
                    RegistryKey.of(RegistryKeys.NOISE_PARAMETERS,new Identifier(id)));
        }

        public DensityFunctionBuilder noise(String id){
            return this.add(DensityFunctionTypes.noise(getNoiseParameters(id)));
        }

        public DensityFunctionBuilder shiftNoise(DensityFunction shiftX,DensityFunction shiftZ,double xzScale,String id){
            return this.add(DensityFunctionTypes.shiftedNoise(shiftX,shiftZ,xzScale,getNoiseParameters(id)));
        }

        public DensityFunctionBuilder shiftNoise(double xzScale,String id){
            DensityFunction shiftX = DensityFunctionBuilder.getFunctionById(server,"minecraft:shift_x");
            DensityFunction shiftZ = DensityFunctionBuilder.getFunctionById(server,"minecraft:shift_z");
            return this.shiftNoise(shiftX,shiftZ,xzScale,id);
        }


        public DensityFunctionBuilder noiseInRange(RegistryEntry<DoublePerlinNoiseSampler.NoiseParameters> noiseParameters, @Deprecated double scaleXz, double scaleY, double min, double max){
            return this.add(DensityFunctionTypes.noiseInRange(noiseParameters,scaleXz,scaleY,min,max));
        }

        public DensityFunctionBuilder noiseInRange(String noiseId, @Deprecated double scaleXz, double scaleY, double min, double max) {
            return this.noiseInRange(getNoiseParameters(noiseId),scaleXz,scaleY,min,max);
        }

            public DensityFunctionBuilder min(DensityFunctionBuilder functionBuilder){
            this.function = DensityFunctionTypes.min(this.function,functionBuilder.build());

            return this;
        }

        public DensityFunctionBuilder max(DensityFunctionBuilder functionBuilder){
            this.function = DensityFunctionTypes.max(this.function,functionBuilder.build());
            return this;
        }

        public DensityFunctionBuilder interpolated(DensityFunctionBuilder functionBuilder){
            return this.interpolated(functionBuilder.build());
        }
        public DensityFunctionBuilder interpolated(DensityFunction function){
           return this.add(DensityFunctionTypes.interpolated(function));
        }

        public DensityFunctionBuilder cache2d(){
            this.function = DensityFunctionTypes.cache2d(function);
            return this;
        }

        public DensityFunction build(){
            return this.function;
        }

        @NotNull
        public static DensityFunctionBuilder of(MinecraftServer server, List<String> functionList) {
            DensityFunctionBuilder densityFunctionBuilder = new DensityFunctionBuilder(server);

            ArrayList<String> newFunctionList = new ArrayList<>();
            if (!functionList.isEmpty() && !FIRST_COMMAND.contains(functionList.get(0))){
                newFunctionList.add("add");
            }
            newFunctionList.addAll(functionList);



            for (int i = 0;i < newFunctionList.size();) {
                String function = newFunctionList.get(i);

                switch (function){
                    case "add","mul","min","max","interpolated" -> {
                        Pair<DensityFunctionBuilder, Integer> nextFunctionData = getNextFunction(server, newFunctionList, i + 1);

                        switch (function) {
                            case "add" -> densityFunctionBuilder.add(nextFunctionData.getLeft().build());
                            case "mul" -> densityFunctionBuilder.mul(nextFunctionData.getLeft().build());
                            case "min" -> densityFunctionBuilder.min(nextFunctionData.getLeft());
                            case "max" -> densityFunctionBuilder.max(nextFunctionData.getLeft());
                            case "interpolated" -> densityFunctionBuilder.interpolated(nextFunctionData.getLeft());
                        }
                        i = nextFunctionData.getRight();
                    }
                    case "noise" -> {
                        densityFunctionBuilder.noise(functionList.get(i+1));
                        i += 2;
                    }
                    case "shift_noise" ->{
                        densityFunctionBuilder.shiftNoise(Double.parseDouble(functionList.get(i+1)),functionList.get(i+2));
                        i += 3;
                    }
                    case "y_clamped_gradient" ->{//noise, jagged, mul, overworld/depth, add, 0.15, yClampedGradient, 310, 300, 1, 0
                        Optional<Double> fromYOptional = getNextNumber(server,functionList.get(i+1));
                        Optional<Double> toYOptional = getNextNumber(server,functionList.get(i+2));
                        Pair<DensityFunctionBuilder, Integer> fromValueData = getNextFunction(server,functionList,i + 3);
                        DensityFunctionBuilder fromValue = fromValueData.getLeft();
                        Pair<DensityFunctionBuilder, Integer> toValueData = getNextFunction(server,functionList,  fromValueData.getRight());
                        if (fromYOptional.isPresent() && toYOptional.isPresent()){
                            double fromY = fromYOptional.get();
                            double toY = toYOptional.get();
                            densityFunctionBuilder.yClampedGradient(
                                    (int)fromY,(int)toY,
                                    fromValue,toValueData.getLeft());
                        }
                        i = toValueData.getRight();
                    }
                    case "noise_in_range" ->{//noise_in_range, 0.25, 1, -1, 1, minecraft:cave_cheese
                        Optional<Double> xzScaleOptional = getNextNumber(server,functionList.get(i+1));
                        Optional<Double> yScaleOptional = getNextNumber(server,functionList.get(i+2));
                        Optional<Double> minOptional = getNextNumber(server,functionList.get(i+3));
                        Optional<Double> maxOptional = getNextNumber(server,functionList.get(i+4));
                        if (xzScaleOptional.isPresent() && yScaleOptional.isPresent() && minOptional.isPresent() && maxOptional.isPresent()){
                            densityFunctionBuilder.noiseInRange(
                                    functionList.get(i+5),
                                    xzScaleOptional.get(),
                                    yScaleOptional.get(),
                                    minOptional.get(),
                                    maxOptional.get()
                                    );
                        }
                        i += 6;
                    }
                    default -> {
                        Asplor.LOGGER.error("World adder : wrong function "+function);
                        i += 1;
                    }
                }
            }
            return densityFunctionBuilder;
        }
    }
    public static class RandomDensityFunctionGenerator{
        private ArrayList<String> functionList;
        private ArrayList<Pair<Integer,Pair<Float,Float>>> processors;
        public RandomDensityFunctionGenerator(List<String> functionList,List<Pair<Integer,Pair<Float,Float>>> processors){
            this.functionList = new ArrayList<>(functionList);
            this.processors = new ArrayList<>(processors);
        }

        public List<String> applyProcessor(Random random){
            for (Pair<Integer,Pair<Float,Float>> pair : this.processors){
                int index = pair.getLeft();
                if (index < functionList.size()){
                    float min = pair.getRight().getLeft();
                    float max = pair.getRight().getRight();
                    functionList.set(index, String.valueOf(MathUtil.getRandomFloat(random, min, max)));
                }
            }
            return functionList;
        }

        public static Pair<Integer,Pair<Float,Float>> createProcessor(int index, float min,float max) {
            return new Pair<>(index,new Pair<>(min,max));
        }
    }
}
