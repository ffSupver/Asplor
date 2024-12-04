package com.ffsupver.asplor.util;

import com.simibubi.create.AllFluids;
import com.simibubi.create.content.fluids.potion.PotionFluidHandler;
import com.simibubi.create.foundation.fluid.FluidIngredient;
import com.simibubi.create.foundation.utility.Lang;
import com.simibubi.create.infrastructure.config.AllConfigs;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import io.github.fabricators_of_create.porting_lib.util.FluidTextUtil;
import io.github.fabricators_of_create.porting_lib.util.FluidUnit;
import me.shedaniel.rei.api.client.gui.widgets.Slot;
import me.shedaniel.rei.api.client.gui.widgets.Tooltip;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;
import net.minecraft.fluid.Fluid;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public final class REIFluidDisplay {
    public static <T> EntryStack<T> setTooltipProcessor(EntryStack<? extends T> stack, BiFunction<EntryStack<T>, Tooltip, Tooltip> processor) {
        return stack.tooltipProcessor((BiFunction) processor);
    }

    public static void addFluidTooltip(List<Widget> fluidStacks, List<FluidIngredient> inputs,
                                       List<FluidStack> outputs) {
        addFluidTooltip(fluidStacks, inputs, outputs, -1);
    }


    public static void addFluidTooltip(List<Widget> fluidStacks, List<FluidIngredient> inputs,
                                       List<FluidStack> outputs, int index1) {
        List<Long> amounts = new ArrayList<>();
        Map<Fluid,Long> fluidAmounts=new HashMap<>();
        inputs.forEach(f -> {
            amounts.add(f.getRequiredAmount());
            fluidAmounts.put(f.getMatchingFluidStacks().get(0).getFluid(),f.getRequiredAmount());
        });
        outputs.forEach(f -> {
            amounts.add(f.getAmount());
            fluidAmounts.put(f.getFluid(),f.getAmount());
        });



        fluidStacks.stream().filter(widget -> widget instanceof Slot slot && slot.getCurrentEntry().getType() == VanillaEntryTypes.FLUID).forEach(widget -> {
            Slot slot = (Slot) widget;
            setTooltipProcessor(slot.getCurrentEntry(), (entryStack, tooltip) -> {
                dev.architectury.fluid.FluidStack fluidStack = entryStack.castValue();
                FluidStack fluid = new FluidStack(fluidStack.getFluid(), fluidStack.getAmount(), fluidStack.getTag());
                if (fluid.getFluid()
                        .equals(AllFluids.POTION.get())) {
                    Text name = fluid.getDisplayName();
                    if (tooltip.entries().isEmpty())
                        tooltip.entries().add(0, Tooltip.entry(name));
                    else
                        tooltip.entries().set(0, Tooltip.entry(name));

                    ArrayList<Text> potionTooltip = new ArrayList<>();
                    PotionFluidHandler.addPotionTooltip(fluid, potionTooltip, 1);
                    ArrayList<Tooltip.Entry> potionEntries = new ArrayList<>();
                    potionTooltip.forEach(component -> potionEntries.add(Tooltip.entry(component)));
                    // why 2 here??? it works though
                    tooltip.entries().addAll(2, potionEntries.stream().toList());
                }

                FluidUnit unit = AllConfigs.client().fluidUnitType.get();
                String amount = FluidTextUtil.getUnicodeMillibuckets(fluidAmounts.containsKey(fluid.getFluid())? fluidAmounts.get(fluid.getFluid()) : amounts.get(0), unit, AllConfigs.client().simplifyFluidUnit.get());
                Text text = Text.literal(String.valueOf(amount)).append(Lang.translateDirect(unit.getTranslationKey())).formatted(Formatting.GOLD);
                if (tooltip.entries().isEmpty())
                    tooltip.entries().add(0, Tooltip.entry(text));
                else {
                    List<Text> siblings = tooltip.entries().get(0).getAsText().getSiblings();
                    siblings.add(Text.literal(" "));
                    siblings.add(text);
                }
                tooltip.entries().remove(1); // Remove REI added amount
                return tooltip;
            });
        });
    }


}
