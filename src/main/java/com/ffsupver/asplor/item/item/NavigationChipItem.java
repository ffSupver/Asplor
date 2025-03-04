package com.ffsupver.asplor.item.item;

import com.ffsupver.asplor.world.WorldData;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class NavigationChipItem extends Item {
    public static final String NAVIGATION_DATA_KEY = "nav";
    private static final Text UNKNOWN_WORLD = Text.literal("???");
    public NavigationChipItem(Settings settings) {
        super(settings);
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        super.appendTooltip(stack, world, tooltip, context);
        Optional<RegistryKey<World>> first = getWorldKey(stack,true);
        Optional<RegistryKey<World>> second = getWorldKey(stack,false);
        if (first.isPresent() || second.isPresent()){
            tooltip.add((first.map(worldRegistryKey -> toPlanetName(worldRegistryKey.getValue())).orElse(UNKNOWN_WORLD))
                    .copy()
                    .append(Text.literal(" <--> "))
                    .append((second.map(
                            registryKey -> toPlanetName(registryKey.getValue())
                    ).orElse(UNKNOWN_WORLD)))
            );
        }

    }

    private static Text toPlanetName(Identifier id){
        if (id.getNamespace().equals(WorldData.NAMESPACE)){
            return Text.literal(id.getPath());
        }else {
            return Text.translatable("planet." + id.getNamespace() + "." + id.getPath());
        }
    }

    public static ItemStack putWorldKey(RegistryKey<World> worldKey, ItemStack itemStack, boolean first){
        NbtCompound nbt = itemStack.getOrCreateNbt();
        NbtCompound navNbt = new NbtCompound();
        if (nbt.contains(NAVIGATION_DATA_KEY, NbtElement.COMPOUND_TYPE)){
            navNbt = nbt.getCompound(NAVIGATION_DATA_KEY);
        }
        navNbt.putString(first ? "f" : "s",worldKey.getValue().toString());
        nbt.put(NAVIGATION_DATA_KEY,navNbt);
        itemStack.setNbt(nbt);
        return itemStack;
    }

    public static Optional<RegistryKey<World>> getWorldKey(ItemStack itemStack, boolean first){
        NbtCompound nbt = itemStack.getOrCreateNbt();
        if (nbt.contains(NAVIGATION_DATA_KEY,NbtElement.COMPOUND_TYPE)){
            NbtCompound navNbt = nbt.getCompound(NAVIGATION_DATA_KEY);
            String string = navNbt.getString(first ? "f" : "s");
            if (string.isEmpty()){
                return Optional.empty();
            }
            Identifier id = new Identifier(string);
            RegistryKey<World> worldKey = RegistryKey.of(RegistryKeys.WORLD,id);
            return Optional.of(worldKey);
        }
        return Optional.empty();
    }

    public static Optional<RegistryKey<World>> getWorldKey(ItemStack itemStack, RegistryKey<World> world) {
        Optional<RegistryKey<World>> first = getWorldKey(itemStack,true);
        Optional<RegistryKey<World>> second = getWorldKey(itemStack,false);
        if (first.isPresent()){
            if (!first.get().getValue().equals(world.getValue())){
                return first;
            }
        }
        if (second.isPresent()){
            if (!second.get().getValue().equals(world.getValue())){
                return second;
            }
        }
        return Optional.empty();
    }

}
