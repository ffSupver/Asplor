package com.ffsupver.asplor.screen.assembler;

import com.ffsupver.asplor.AllBlocks;
import com.ffsupver.asplor.item.ModItems;
import com.ffsupver.asplor.item.item.GuideBookItem;
import com.ffsupver.asplor.recipe.AssemblerRecipe;
import com.ffsupver.asplor.recipe.ModRecipes;
import com.ffsupver.asplor.screen.ModScreenHandlers;
import com.simibubi.create.AllItems;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.slot.ForgingSlotsManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.ffsupver.asplor.screen.backpack.BackpackBaseHandler.backpackDataKey;
import static com.ffsupver.asplor.screen.backpack.BackpackBaseHandler.backpackTypeKey;

public class AssemblerScreenHandler extends ForgingScreenHandler {
    private World world;
    private AssemblerRecipe currenRecipe;
    public AssemblerScreenHandler(int syncId, PlayerInventory playerInventory) {
        this(syncId,playerInventory, ScreenHandlerContext.EMPTY);
    }
    public AssemblerScreenHandler(int syncId, PlayerInventory playerInventory,ScreenHandlerContext context){
        super(ModScreenHandlers.ASSEMBLER_SCREEN_HANDLER,syncId,playerInventory,context);
        this.world=playerInventory.player.getWorld();

    }




    @Override
    protected boolean canTakeOutput(PlayerEntity player, boolean present) {
        return true;
    }

    @Override
    protected void onTakeOutput(PlayerEntity player, ItemStack stack) {

            decreaseItem(this.input, 0, 1);
            decreaseItem(this.input, 1, 1);
            damageSuperGlue(this.input.getStack(2),world);

    }

    private void decreaseItem(Inventory inventory, int slot,int count){
        ItemStack newStack = inventory.getStack(slot);
        newStack.decrement(count);
        inventory.setStack(slot,newStack);
    }

    @Override
    protected boolean canUse(BlockState state) {
        return true;
    }

    private Optional<AssemblerRecipe> getCurrentRecipe(Inventory input){
        SimpleInventory inputForTest = new SimpleInventory(2);
        for (int slot=0;slot<inputForTest.size();slot++){
            inputForTest.setStack(slot,input.getStack(slot));
        }
        return this.world.getRecipeManager().getFirstMatch(ModRecipes.ASSEMBLER_RECIPETYPE,inputForTest,this.world);
    }

    @Override
    public void updateResult() {
        ItemStack inputStack1 =this.input.getStack(0);
        ItemStack inputStack2 =this.input.getStack(1);
        ItemStack inputStack3 = this.input.getStack(2);
        boolean isAddingGoggles = inputStack1.getItem() instanceof ArmorItem &&((ArmorItem) inputStack1.getItem()).getType().equals(ArmorItem.Type.HELMET)&&inputStack2.isOf(AllItems.GOGGLES.asItem());
        boolean isAddingChest = inputStack1.getItem() instanceof ArmorItem && ((ArmorItem) inputStack1.getItem()).getType().equals(ArmorItem.Type.CHESTPLATE)&&(inputStack2.isOf(Items.CHEST)||inputStack2.isOf(AllBlocks.ALLOY_CHEST.asItem()));
        boolean isAddingMysteriousPages = inputStack1.isOf(ModItems.GUIDE_BOOK) && inputStack2.isOf(ModItems.MYSTERIOUS_PAPER) && GuideBookItem.canAdd(inputStack1,inputStack2);
        if(inputStack3.isOf(AllItems.SUPER_GLUE.asItem())){
            if (isAddingGoggles) {
                ItemStack outputStack = inputStack1.copy();

                NbtCompound outputStackNbt = outputStack.getOrCreateNbt();
                NbtCompound outputStackDisplayNBT = outputStackNbt.getCompound("display");
                outputStackNbt.putBoolean("goggle", true);

                List<Text> lore = new ArrayList<>();
                lore.add(Text.translatable("description.asplor.goggle").styled(style -> style.withColor(Formatting.GOLD)));

                NbtList loreList = outputStackDisplayNBT.contains("Lore", 9) ? outputStackDisplayNBT.getList("Lore", 8) : new NbtList();
                for (Text line : lore) {
                    loreList.add(NbtString.of(Text.Serializer.toJson(line)));
                }

                outputStackDisplayNBT.put("Lore", loreList);
                outputStackNbt.put("display", outputStackDisplayNBT);

                outputStack.setNbt(outputStackNbt);

                output.setStack(0, outputStack);
            }else if (isAddingChest){
                NbtCompound originBackpackData=new NbtCompound();
                String backpackType = "null";
                DefaultedList<ItemStack> originInventory=DefaultedList.ofSize(54,ItemStack.EMPTY);
                if (inputStack1.hasNbt()&&inputStack1.getNbt().contains(backpackDataKey,10)){
                    originBackpackData=inputStack1.getNbt().getCompound(backpackDataKey).copy();
                }
                if (!originBackpackData.isEmpty()&&originBackpackData.contains(backpackTypeKey,8)){
                    backpackType=originBackpackData.getString(backpackTypeKey);
                    Inventories.readNbt(originBackpackData,originInventory);
                }
                if (inputStack2.isOf(Items.CHEST)){
                    if (!backpackType.equals("null")&&(backpackType.equals("large")||backpackType.equals("small"))){
                        removeOutput();
                    }else {
                        Inventories.writeNbt(originBackpackData,originInventory);
                        originBackpackData.putString(backpackTypeKey,"small");
                        ItemStack outputStack = inputStack1.copy();
                        NbtCompound outputStackNbt = outputStack.getOrCreateNbt();
                        outputStackNbt.put(backpackDataKey,originBackpackData);
                        addDescription(outputStackNbt,
                                Text.translatable("description.asplor.chest").styled(style -> style.withColor(Formatting.GOLD)),
                                Text.empty()
                        );
                        outputStack.setNbt(outputStackNbt);
                        this.output.setStack(0,outputStack);
                    }
                }else if (inputStack2.isOf(AllBlocks.ALLOY_CHEST.asItem())){
                    if (backpackType.equals("large")){
                        removeOutput();
                    }else {
                        Inventories.writeNbt(originBackpackData,originInventory);
                        originBackpackData.putString(backpackTypeKey,"large");
                        ItemStack outputStack = inputStack1.copy();
                        NbtCompound outputStackNbt = outputStack.getOrCreateNbt();
                        outputStackNbt.put(backpackDataKey,originBackpackData);
                        addDescription(outputStackNbt,
                                Text.translatable("description.asplor.alloy_chest").styled(style -> style.withColor(Formatting.GOLD)),
                                Text.translatable("description.asplor.chest").styled(style -> style.withColor(Formatting.GOLD))
                        );
                        outputStack.setNbt(outputStackNbt);
                        this.output.setStack(0,outputStack);
                    }
                }

            }else if (isAddingMysteriousPages){
                ItemStack outputStack = inputStack1.copy();
                NbtCompound outputStackNbt = outputStack.getOrCreateNbt();
                NbtCompound pageNbt = inputStack2.getOrCreateNbt();
                NbtList outputChapterList;
                if (outputStackNbt.contains(GuideBookItem.GUIDE_BOOK_DATA_KEY,9)){
                     outputChapterList = outputStackNbt.getList(GuideBookItem.GUIDE_BOOK_DATA_KEY, 8);
                }else {
                    outputChapterList = new NbtList();
                }
                outputChapterList.add(NbtString.of(pageNbt.getString("chapter")));
                outputStackNbt.put(GuideBookItem.GUIDE_BOOK_DATA_KEY,outputChapterList);
                outputStack.setNbt(outputStackNbt);


                this.output.setStack(0,outputStack);
            }else if (getCurrentRecipe(input).isPresent()) {
                currenRecipe = getCurrentRecipe(input).get();
                ItemStack outputStack = currenRecipe.getOutput(null).copy();
                this.output.setStack(0, outputStack);
            }else {
                removeOutput();
            }
        }else {
           removeOutput();
        }

    }

    private NbtCompound addDescription(NbtCompound nbt,Text text,Text textToReplace){
        NbtCompound displayNBT = nbt.getCompound("display");
        NbtList loreList = displayNBT.contains("Lore", 9) ? displayNBT.getList("Lore", 8) : new NbtList();
        if (!textToReplace.equals(Text.empty())){
            for (int i = 0; i < loreList.size(); i++) {
                Text existingText = Text.Serializer.fromJson(loreList.getString(i));
                if (existingText != null && existingText.equals(textToReplace)) {
                    loreList.set(i, NbtString.of(Text.Serializer.toJson(text))); // 替换为新文本
                    return nbt; // 替换后直接返回
                }
            }
        }

        loreList.add(NbtString.of(Text.Serializer.toJson(text)));
        displayNBT.put("Lore", loreList);
        nbt.put("display", displayNBT);
        return nbt;
    }

    private void removeOutput(){
        currenRecipe = null;
        this.output.setStack(0, ItemStack.EMPTY);
    }

    private void damageSuperGlue(ItemStack itemStack3,World world){
        if (itemStack3.isDamageable()&&itemStack3.isOf(AllItems.SUPER_GLUE.asItem())&&(!world.isClient)){
            itemStack3.damage(1,world.getRandom(),(ServerPlayerEntity) player);
        }

    }

    @Override
    protected ForgingSlotsManager getForgingSlotsManager() {
         return ForgingSlotsManager.create().input(0, 26, 48, (stack)->{
                    return true;
                 })
                 .input(1, 44, 48, (stack)->{
                     return true;
                 })
                 .input(2, 76, 14, (stack)->{
                     return stack.isOf(AllItems.SUPER_GLUE.asItem());
                 })
                 .output(3, 132, 48).build();
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return true;
    }

    @Override
    public ItemStack quickMove(PlayerEntity player, int slot) {
        return super.quickMove(player, slot);
    }
}
