package com.ffsupver.asplor.block.atmosphericRegulator;

import com.ffsupver.asplor.util.MathUtil;
import com.simibubi.create.content.equipment.wrench.IWrenchable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Property;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.util.math.Direction.*;


public class OxygenPipe extends Block implements IWrenchable {
    public static final Property<Shape> SHAPE = EnumProperty.of("shape",Shape.class);
    public OxygenPipe(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(SHAPE,Shape.UN));
    }

    public static BlockPos getConnectPos(BlockPos blockPos,BlockState state,BlockPos fromPos){
            return blockPos.offset(state.get(SHAPE).getOtherDirection(MathUtil.directionFromPos(blockPos,fromPos)));
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        World world = ctx.getWorld();
        BlockState state = super.getPlacementState(ctx);
        state = connectNeighbor(state,world,pos);
        return state;
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        if (canConnect(neighborState,MathUtil.directionFromPos(pos,neighborPos))){
            state = connectNeighbor(state,world,pos);
        }
        return state;
    }


    private BlockState connectNeighbor(BlockState state, WorldAccess world, BlockPos pos){
        Direction direction1 = null;
        Direction direction2 = null;
        for (Direction direction : DIRECTIONS){
            if (direction1 != null && direction2 != null){
                break;
            }
            BlockPos checkPos = pos.offset(direction);
            BlockState checkState = world.getBlockState(checkPos);
            if (canConnect(checkState,direction.getOpposite())){
                if (direction1 == null){
                    direction1 = direction;
                }else {
                    direction2 = direction;
                }
            }
        }

        boolean b1 = direction1 != null;
        boolean b2 = direction2 != null;


        if (b1 && !b2){
            direction2 = direction1.getOpposite();
        }

        if (direction1 != null && direction2 != null){
            state = state.with(SHAPE, Shape.getFromDirection(direction1, direction2));
        }

        return state;
    }

    private static boolean canConnect(BlockState state,Direction direction){
        return state.getBlock() instanceof OxygenPipe || (
                state.getBlock() instanceof AtmosphericRegulator && !state.get(AtmosphericRegulator.FACING).getAxis().equals(direction.getAxis())
        );
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder.add(SHAPE));
    }

    public enum Shape implements StringIdentifiable {
        UE,UW,UN,US,DW,DE,DN,DS,NE,NW,SE,SW,NS,UD,EW;

        public final static Map<Integer,Shape> CODE_SHAPE_MAP = getCodeShapeMap();
        public final static Map<Shape,Integer> SHAPE_CODE_MAP = getShapeCodeMap();


        public static Shape getFromDirection(Direction direction1,Direction direction2){
            int code = toDirectionCode(direction1) + toDirectionCode(direction2);
            return CODE_SHAPE_MAP.get(code);
        }

        public Direction getOtherDirection(Direction direction){
            int code = SHAPE_CODE_MAP.get(this);
            return codeToDirection(code - toDirectionCode(direction));
        }

        private static int toDirectionCode(Direction direction){
            return switch (direction){
                case UP -> 0b100000;
                case DOWN -> 0b010000;
                case NORTH -> 0b001000;
                case SOUTH -> 0b000100;
                case EAST -> 0b000010;
                case WEST -> 0b000001;
            };
        }

        private static Direction codeToDirection(int code){
            return switch (code){
                case 0b100000 -> UP;
                case 0b010000 -> DOWN;
                case 0b001000 -> NORTH;
                case 0b000100 -> SOUTH;
                case 0b000010 -> EAST;
                case 0b000001 -> WEST;
                default -> throw new IllegalStateException("Unexpected direction cod: " + code);
            };
        }


        @Override
        public String asString() {
            return name().toLowerCase();
        }

        private static Map<Integer,Shape> getCodeShapeMap() {
            Map<Integer, Shape> result = new HashMap<>();
            result.put(0b100010, UE);
            result.put(0b100001, UW);
            result.put(0b101000, UN);
            result.put(0b100100, US);
            result.put(0b010010, DE);
            result.put(0b010001, DW);
            result.put(0b011000, DN);
            result.put(0b010100, DS);
            result.put(0b001010, NE);
            result.put(0b001001, NW);
            result.put(0b000110, SE);
            result.put(0b000101, SW);
            result.put(0b001100, NS);
            result.put(0b110000, UD);
            result.put(0b000011, EW);
            return result;
        }

        private static Map<Shape,Integer> getShapeCodeMap() {
            Map<Shape,Integer> result = new HashMap<>();
            CODE_SHAPE_MAP.forEach((integer, shape1) -> result.put(shape1,integer));
            return result;
        }
    }
}
