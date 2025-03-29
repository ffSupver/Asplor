package com.ffsupver.asplor.command;

import com.ffsupver.asplor.util.RenderUtil;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.*;
import net.minecraft.entity.Entity;
import net.minecraft.network.packet.s2c.play.PositionFlag;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

public class WorldTeleportCommand {
    public static void register(CommandDispatcher<ServerCommandSource> dispatcher) {
//        LiteralCommandNode<ServerCommandSource> literalCommandNode = dispatcher.register(
//                CommandManager.literal("worldtp")
//                        .requires(source -> source.hasPermissionLevel(2))
//
//        );
        dispatcher.register(
                CommandManager.literal("worldtp")
                        .requires(source -> source.hasPermissionLevel(2))
                        .then(
                                CommandManager.argument("dimension", DimensionArgumentType.dimension())
                                        .then(
                                                CommandManager.argument("location", Vec3ArgumentType.vec3())
                                                        .executes(
                                                                context -> execute(
                                                                        context.getSource(),
                                                                        Collections.singleton(context.getSource().getEntityOrThrow()),
                                                                        DimensionArgumentType.getDimensionArgument(context, "dimension"),
                                                                        Vec3ArgumentType.getPosArgument(context, "location"),
                                                                        DefaultPosArgument.zero()
                                                                )
                                                        )
                                                        .then(
                                                                CommandManager.argument("targets", EntityArgumentType.entities())
                                                                        .executes(
                                                                                context -> execute(
                                                                                        context.getSource(),
                                                                                        EntityArgumentType.getEntities(context,"targets"),
                                                                                        DimensionArgumentType.getDimensionArgument(context, "dimension"),
                                                                                        Vec3ArgumentType.getPosArgument(context, "location"),
                                                                                        DefaultPosArgument.zero()
                                                                                )
                                                                        )
                                                        )
                                        )
                        )

        );
    }
    private static int execute(
            ServerCommandSource source,
            Collection<? extends Entity> targets,
            ServerWorld world,
            PosArgument location,
            @Nullable PosArgument rotation
    ) throws CommandSyntaxException {
        Vec3d destination = location.toAbsolutePos(source);
        Vec2f r = rotation == null ? null : rotation.toAbsoluteRotation(source);
        Set<PositionFlag> set = EnumSet.noneOf(PositionFlag.class);
        if (location.isXRelative()) {
            set.add(PositionFlag.X);
        }

        if (location.isYRelative()) {
            set.add(PositionFlag.Y);
        }

        if (location.isZRelative()) {
            set.add(PositionFlag.Z);
        }
        for (Entity entity : targets) {
            Vec2f rE = r == null ? new Vec2f(entity.getYaw(),entity.getPitch()) : r;
            entity.teleport(world,
                    destination.getX(),destination.getY(),destination.getZ(),
                    set,rE.x,rE.y
            );
        }
        if (targets.size() == 1){
            source.sendFeedback(()-> Text.translatable("command.worldtp.success",
                    targets.iterator().next().getDisplayName(),
                    RenderUtil.formatFloat(destination.x),
                    RenderUtil.formatFloat(destination.y),
                    RenderUtil.formatFloat(destination.z),
                    world.getRegistryKey().getValue().toString()
                    ),true);
        }else {
            source.sendFeedback(()-> Text.translatable("command.worldtp.success",
                    targets.size() + " entities",
                    RenderUtil.formatFloat(destination.x),
                    RenderUtil.formatFloat(destination.y),
                    RenderUtil.formatFloat(destination.z),
                    world.getRegistryKey().getValue().toString()
            ),true);
        }
        return targets.size();
    }

}
