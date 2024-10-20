package com.ffsupver.asplor.block.alloy_mechanical_press;

import com.simibubi.create.AllRecipeTypes;
import com.simibubi.create.AllTags;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.content.kinetics.crafter.MechanicalCraftingRecipe;
import com.simibubi.create.content.kinetics.press.PressingBehaviour;
import com.simibubi.create.content.kinetics.press.PressingRecipe;
import com.simibubi.create.content.processing.basin.BasinBlockEntity;
import com.simibubi.create.content.processing.basin.BasinOperatingBlockEntity;
import com.simibubi.create.content.processing.recipe.ProcessingRecipe;
import com.simibubi.create.content.processing.sequenced.SequencedAssemblyRecipe;
import com.simibubi.create.foundation.advancement.AdvancementBehaviour;
import com.simibubi.create.foundation.advancement.AllAdvancements;
import com.simibubi.create.foundation.advancement.CreateAdvancement;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.foundation.item.ItemHelper;
import com.simibubi.create.foundation.item.SmartInventory;
import com.simibubi.create.foundation.recipe.RecipeApplier;
import com.simibubi.create.foundation.utility.VecHelper;
import com.simibubi.create.infrastructure.config.AllConfigs;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemHandlerHelper;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandlerContainer;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.recipe.CraftingRecipe;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;

import java.util.List;
import java.util.Optional;

public class AlloyMechanicalPressEntity extends BasinOperatingBlockEntity implements PressingBehaviour.PressingBehaviourSpecifics {
    private static final Object compressingRecipesKey = new Object();

    public PressingBehaviour pressingBehaviour;
    private int tracksCreated;

    public AlloyMechanicalPressEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    protected Box createRenderBoundingBox() {
        return new Box(pos).stretch(0, -1.5, 0)
                .stretch(0, 1, 0);
    }

    @Override
    public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
        super.addBehaviours(behaviours);
        pressingBehaviour = new PressingBehaviour(this);
        behaviours.add(pressingBehaviour);

        registerAwardables(behaviours, AllAdvancements.PRESS, AllAdvancements.COMPACTING,
                AllAdvancements.TRACK_CRAFTING);
    }

    public void onItemPressed(ItemStack result) {
        award(AllAdvancements.PRESS);
        if (AllTags.AllBlockTags.TRACKS.matches(result))
            tracksCreated += result.getCount();
        if (tracksCreated >= 1000) {
            award(AllAdvancements.TRACK_CRAFTING);
            tracksCreated = 0;
        }
    }

    public PressingBehaviour getPressingBehaviour() {
        return pressingBehaviour;
    }

    @Override
    public boolean tryProcessInBasin(boolean simulate) {
        applyBasinRecipe();

        Optional<BasinBlockEntity> basin = getBasin();
        if (basin.isPresent()) {
            SmartInventory inputs = basin.get()
                    .getInputInventory();
            for (int slot = 0; slot < inputs.getSlotCount(); slot++) {
                ItemStack stackInSlot = inputs.getStack(slot);
                if (stackInSlot.isEmpty())
                    continue;
                pressingBehaviour.particleItems.add(stackInSlot);
            }
        }

        return true;
    }

    @Override
    protected void write(NbtCompound compound, boolean clientPacket) {
        super.write(compound, clientPacket);
        if (getBehaviour(AdvancementBehaviour.TYPE).isOwnerPresent())
            compound.putInt("TracksCreated", tracksCreated);
    }

    @Override
    protected void read(NbtCompound compound, boolean clientPacket) {
        super.read(compound, clientPacket);
        tracksCreated = compound.getInt("TracksCreated");
    }

    @Override
    public boolean tryProcessInWorld(ItemEntity itemEntity, boolean simulate) {
        ItemStack item = itemEntity.getStack();
        Optional<? extends ProcessingRecipe<Inventory>> recipe = getRecipe(item);
        if (!recipe.isPresent())
            return false;
        if (simulate)
            return true;

        ItemStack itemCreated = ItemStack.EMPTY;
        pressingBehaviour.particleItems.add(item);
        if (canProcessInBulk() || item.getCount() == 1) {
            RecipeApplier.applyRecipeOn(itemEntity, recipe.get());
            itemCreated = itemEntity.getStack()
                    .copy();
        } else {
            for (ItemStack result : RecipeApplier.applyRecipeOn(world, ItemHandlerHelper.copyStackWithSize(item, 1),
                    recipe.get())) {
                if (itemCreated.isEmpty())
                    itemCreated = result.copy();
                ItemEntity created =
                        new ItemEntity(world, itemEntity.getX(), itemEntity.getY(), itemEntity.getZ(), result);
                created.setToDefaultPickupDelay();
                created.setVelocity(VecHelper.offsetRandomly(Vec3d.ZERO, world.random, .05f));
                world.spawnEntity(created);
            }
            item.decrement(1);
        }

        if (!itemCreated.isEmpty())
            onItemPressed(itemCreated);
        return true;
    }

    @Override
    public boolean tryProcessOnBelt(TransportedItemStack input, List<ItemStack> outputList, boolean simulate) {
        Optional<? extends ProcessingRecipe<Inventory>> recipe = getRecipe(input.stack);
        if (!recipe.isPresent())
            return false;
        if (simulate)
            return true;
        pressingBehaviour.particleItems.add(input.stack);
        List<ItemStack> outputs = RecipeApplier.applyRecipeOn(world,
                canProcessInBulk() ?
                input.stack
                : ItemHandlerHelper.copyStackWithSize(input.stack, 1)
                , recipe.get());

        for (ItemStack created : outputs) {
            if (!created.isEmpty()) {
                onItemPressed(created);
                break;
            }
        }

        outputList.addAll(outputs);
        return true;
    }

    @Override
    public void onPressingCompleted() {
        if (pressingBehaviour.onBasin() && matchBasinRecipe(currentRecipe)
                && getBasin().filter(BasinBlockEntity::canContinueProcessing)
                .isPresent())
            startProcessingBasin();
        else
            basinChecker.scheduleUpdate();
    }

    private static final Inventory pressingInv = new ItemStackHandlerContainer(1);

    public Optional<?extends ProcessingRecipe<Inventory>> getRecipe(ItemStack item) {
        Optional<PressingRecipe> assemblyRecipe =
                SequencedAssemblyRecipe.getRecipe(world, item, AllRecipeTypes.PRESSING.getType(), PressingRecipe.class);
        Optional<AlloyPressingRecipe> assemblyRecipe1 =
                SequencedAssemblyRecipe.getRecipe(world, item, com.ffsupver.asplor.AllRecipeTypes.ALLOY_PRESSING.getType(), AlloyPressingRecipe.class);
        if (assemblyRecipe.isPresent())
            return assemblyRecipe;
        if (assemblyRecipe1.isPresent())
            return assemblyRecipe1;
        pressingInv.setStack(0, item);
        if (AllRecipeTypes.PRESSING.find(pressingInv, world).isPresent())
            return AllRecipeTypes.PRESSING.find(pressingInv, world);
        return com.ffsupver.asplor.AllRecipeTypes.ALLOY_PRESSING.find(pressingInv,world);
    }

    public static <C extends Inventory> boolean canCompress(Recipe<C> recipe) {
        if (!(recipe instanceof CraftingRecipe) || !AllConfigs.server().recipes.allowShapedSquareInPress.get())
            return false;
        DefaultedList<Ingredient> ingredients = recipe.getIngredients();
        return (ingredients.size() == 4 || ingredients.size() == 9) && ItemHelper.matchAllIngredients(ingredients);
    }

    @Override
    protected <C extends Inventory> boolean matchStaticFilters(Recipe<C> recipe) {
        return (recipe instanceof CraftingRecipe && !(recipe instanceof MechanicalCraftingRecipe) && canCompress(recipe)
                && !AllRecipeTypes.shouldIgnoreInAutomation(recipe))
                || recipe.getType() == AllRecipeTypes.COMPACTING.getType();
    }

    @Override
    public float getKineticSpeed() {
        return getSpeed();
    }

    @Override
    public boolean canProcessInBulk() {
        return
                true;
//                AllConfigs.server().recipes.bulkPressing.get();
    }

    @Override
    protected Object getRecipeCacheKey() {
        return compressingRecipesKey;
    }

    @Override
    public int getParticleAmount() {
        return 15;
    }

    @Override
    public void startProcessingBasin() {
        if (pressingBehaviour.running && pressingBehaviour.runningTicks <= PressingBehaviour.CYCLE / 2)
            return;
        super.startProcessingBasin();
        pressingBehaviour.start(PressingBehaviour.Mode.BASIN);
    }

    @Override
    protected void onBasinRemoved() {
        pressingBehaviour.particleItems.clear();
        pressingBehaviour.running = false;
        pressingBehaviour.runningTicks = 0;
        sendData();
    }

    @Override
    protected boolean isRunning() {
        return pressingBehaviour.running;
    }

    @Override
    protected Optional<CreateAdvancement> getProcessedRecipeTrigger() {
        return Optional.of(AllAdvancements.COMPACTING);
    }

}
